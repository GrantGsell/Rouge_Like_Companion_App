import com.opencsv.CSVWriter;
import org.ejml.simple.SimpleMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class OneVsAllChar {
    // Data Normalization variables
    public static double[] norm_mean;
    public static double[] norm_std;
    public static ArrayList<Integer> norm_constant_columns;
    public static SimpleMatrix test_learned_parameters;
    public static String[] characters_list;

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void top_one_vs_all_training(int num_examples, int num_features){
        /*
         Letter specific parameters
         */
        num_examples = 7979;
        num_features = 810;
        int sliding_window_height = 18;
        int sliding_window_width =  15;
        int sliding_window_delta = 5;
        int class_num = 1;
        int ex_idx = 0;
        int number_examples = 15;
        int example_width = 400;
        String input_file_name = "text_box_recog/input_text_box_recog_data.csv";

        // Basic parameters
        // String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "R",
        //       "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "0", "4", "7", "'", "SPACE", "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP",
        //     "ERRAPT"};
        String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "4", "5", "7", "8", "'", "-", "SPACE", "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP",
                "ERRAPT"};
        characters_list = characters;
        int num_classes = characters.length;
        double learning_constant = 0.15;
        double alpha = 250;

        // Initialize input/output matrices
        SimpleMatrix input_data = ObtainData.obtain_sliding_window_data(sliding_window_height, sliding_window_width, sliding_window_delta,
                class_num, ex_idx, number_examples, example_width,
                input_file_name, "");
        String[][] output_data = new String[num_examples][1];
        SimpleMatrix learned_parameters = new SimpleMatrix(num_classes, num_features + 1);

        // Read input/output data
        SimpleMatrix useless_data = new SimpleMatrix(num_examples, 1);
        String input_file_name_useless = "";
        String output_file_name = "text_box_recog/output_text_box_recog_data.csv";
        read_data(num_examples, useless_data, output_data, input_file_name_useless, output_file_name);

        // Clear and initialize the MySQL tables before obtaining the necessary data
        MySQLAccess.clear_and_initialize();

        // Normalize the input data
        create_training_data_normalization_arrays(input_data);

        // Obtain the normalized data
        double[] mean = new double[num_features];
        double[] std = new double[num_features];
        ArrayList<Integer> const_cols = new ArrayList<Integer>();
        MySQLAccess.read_mean_std_const_cols(num_features, mean, std, const_cols);

        // Normalize the training_data
        normalize_input_data(input_data, mean, std, const_cols);

        // Split the input/output data into 3 data sets
        ArrayList<Object> split_data = new ArrayList<>();
        split_original_training_set(input_data, output_data, split_data);

        // Obtain the data from the split data arraylist
        SimpleMatrix train_set_input = (SimpleMatrix) split_data.get(0);
        SimpleMatrix cv_set_input = (SimpleMatrix) split_data.get(1);
        SimpleMatrix test_set_input = (SimpleMatrix) split_data.get(2);
        String[][] train_set_output = (String[][]) split_data.get(3);
        String[][] cv_set_output = (String[][]) split_data.get(4);
        String[][] test_set_output = (String[][]) split_data.get(5);

        // Constant value optimal selection
        //int max_iterations = 100;
        //double[] optimized_constants = constant_selection(train_set_input, train_set_output, cv_set_input, cv_set_output, num_classes, characters, max_iterations);
        //learning_constant = optimized_constants[0];
        //alpha = optimized_constants[1];

        // Train classifiers for each character
        learned_parameters = one_vs_all(input_data, output_data, learning_constant, alpha, characters.length, characters, 3000, true);

        // Test prediction and run metrics
        int[] prediction = test_parameters_vs_cv_set(input_data, learned_parameters);
        List<double[]> metric_arrays = metrics(prediction, output_data, characters, num_classes);// Transform the cv_set_output data from strings into integers
        Hashtable<String, Integer> char_to_int_map = new Hashtable<>();
        for(int i = 0; i < characters.length; i++){
            char_to_int_map.put(characters[i], i);
        }
        int[] output_nums = new int[cv_set_input.numRows()];
        for(int i = 0; i < output_nums.length; i++){
            output_nums[i] = char_to_int_map.get(cv_set_output[i][0]);
        }
        double[] metrics = multi_f_score(metric_arrays.get(3), output_nums, num_classes);

        // Test parameters before writing to file
        test_parameters_vs_training_set(num_examples, num_features, num_classes, input_data, output_data, learned_parameters, characters);
    }


    /*
    Name       : sigmoid
    Purpose    : Computes the sigmoid of z.
    Parameters : z, a SimpleMatrix.
    Return     : g, a SimpleMatrix denoting the computed sigmoid of z.
    Notes      : None.
     */
    public static SimpleMatrix sigmoid(SimpleMatrix z){
        return z.scale(-1.0).elementExp().plus(1).elementPower(-1.0);
    }


    /*
    Name       : lr_cost_function_regularized
    Purpose    : Computes the cost for regularized logistic regression.
    Parameters : parameters which are the weights, input_data which is the input values, output_data which
                    is the output values, lambdaConst which is the regularization
                    constant.
    Return     : One double value denoting the cost.
    Notes      :
                 parameters has dimensions : (number of features x 1)
                 input_data has dimensions : (number of training examples x number of features)
                 output_data has dimensions: (number of examples x 1)
                 hyp has dimensions        : (number of training examples x 1)
     */
    public static double lr_cost_function(SimpleMatrix parameters,
                                          SimpleMatrix input_data,
                                          SimpleMatrix output_data,
                                          double lambda_const){
        // Create copy of parameters
        SimpleMatrix parameters_copy = parameters.copy();

        // Number of training examples
        double m = input_data.numRows();

        // Hypothesis calculation
        SimpleMatrix z = input_data.mult(parameters_copy);
        SimpleMatrix hyp = sigmoid(z);

        // Regularized Cost Function Calculation
        SimpleMatrix test = hyp.elementLog();
        SimpleMatrix term_0 = output_data.scale(-1).transpose().mult(hyp.elementLog());
        SimpleMatrix term_1 = output_data.scale(-1).plus(1).transpose().mult(hyp.scale(-1).plus(1).elementLog());
        parameters_copy.set(0,0, 0);
        SimpleMatrix term_2 = parameters_copy.transpose().mult(parameters_copy).scale(lambda_const / (2 * m));

        // Cost calculation
        double[] cost = (term_0.minus(term_1).scale((1.0/ m)).plus(term_2)).getDDRM().getData();

        return cost[0];
    }


    /*
    Name       : lr_gradient_regularized
    Purpose    : Computes the gradient for a logistic regression cost function
    Parameters : parameters which are the weights, input_data which is the input values, output_data which
                    is the output values, lambdaConst which is the regularization constant.
    Return     : grad which is a matrix denoting the gradient values
    Notes      :
                 parameters has dimensions : (number of features x 1)
                 input_data has dimensions : (number of training examples x number of features)
                 output_data has dimensions: (number of examples x 1)
                 hyp has dimensions        : (number of training examples x 1)
                 grad has dimensions       : (number of features x 1)
    */
    public static SimpleMatrix lr_gradient_regularized(SimpleMatrix parameters,
                                                       SimpleMatrix input_data,
                                                       SimpleMatrix output_data,
                                                       double lambda_const){
        // Create copy of parameters
        SimpleMatrix parameters_copy = parameters.copy();

        // Number of training examples
        double m = input_data.numRows();

        // Hypothesis calculation
        SimpleMatrix z = input_data.mult(parameters);
        SimpleMatrix hyp = sigmoid(z);

        // Gradient calculation
        parameters_copy.set(0, 0, 0);
        SimpleMatrix grad = (input_data.transpose().mult(hyp.minus(output_data))).scale(1.0/m);

        // Regularization term calculation
        SimpleMatrix reg_term = parameters_copy.scale(lambda_const / m);

        // Regularize the gradient
        grad = grad.plus(reg_term);

        return grad;
    }


    /*
    Name       : one_vs_all
    Purpose    : trains multiple logistic regression classifiers and returns all the classifiers in a matrix all_theta,
                    where the i-th row of all_theta corresponds to the classifier for label i.
    Parameters : X which is the input values, y which is the output values, num_labels which is the number of classes
                    we have, and lambdaConst which is the regularization constant.
    Return     :
    Notes      :
                 input_data has dimensions : (number of training examples x number of features)
                 output_dat has dimensions : (number of examples x 1)
                 parameters had dimensions : (number of classes x  number of features)
                 bias_unit has dimensions  : (number of training examples x 1)
    */
    public static SimpleMatrix one_vs_all(SimpleMatrix input_data,
                                          String[][] output_data,
                                          double lambda_const,
                                          double alpha,
                                          int num_classes, String[] class_char_arr, int max_iterations, boolean write_data){
        // m: number of examples, n: number of features
        int m = input_data.numRows();
        int n = input_data.numCols();

        // Initialize classifier matrix
        SimpleMatrix classifiers = new SimpleMatrix(num_classes, n + 1);

        // Create bias unit matrix
        SimpleMatrix bias_units = new SimpleMatrix(m, 1);
        bias_units = bias_units.plus(1.0);

        // Add bias unit to input_data matrix
        SimpleMatrix input_with_bias = bias_units.concatColumns(input_data);

        // Generate classifiers for each class
        for(int class_idx = 0; class_idx < num_classes; class_idx++){
            // Initialize initial parameter values
            SimpleMatrix initial_theta = new SimpleMatrix(n + 1, 1);

            // Set temporary output matrix based on class
            String class_char = class_char_arr[class_idx];
            SimpleMatrix temp_output = logical_array(output_data, class_char);

            // Run gradient descent or other optimization function
            SimpleMatrix learned_parameters = gradient_descent(initial_theta, input_with_bias, temp_output, lambda_const, alpha, class_char, max_iterations);

            // Assign learned parameters to respective row in  classifiers matrix
            classifiers.insertIntoThis(class_idx, 0, learned_parameters.transpose());

            // Transform learned_parameter data into a double array and add to MySQL database
            if(write_data == true) {
                double[] data = learned_parameters.getDDRM().getData();
                MySQLAccess.insert_parameter_column_data("parameters", n + 1, data, class_char);
            }
        }

        // Generate classifiers for each class using multithreading
        /*
        try {
            learn_classifiers_using_multithreading(input_with_bias, output_data, class_char_arr, lambda_const, alpha, n);
        }
        catch (InterruptedException e){
            System.out.println(e);
        }
        */
        return classifiers;
    }


    /*
    Name       : logical_array
    Purpose    : To generate a logical array of 1 or 0 values based on if the current element in the given array is
                    equal to the current class number.
    Parameters : output_data which is an array denoting the respective class the ith element of the given array belongs
                    to, current_class_number which is the current class number.
    Return     : A logical array of one and zero values, denoting if the respective element in output_data has the same
                    class number as the class that is currently having its classifier trained for.
    Notes      : This function allows for one-versus-all multi-class classification problems.
     */
    public static SimpleMatrix logical_array(String[][] output_data, String curr_class_value){
        // Set return logical matrix
        SimpleMatrix logical = new SimpleMatrix(output_data.length, output_data[0].length);

        // Set high if current row value is the same class
        for(int row = 0; row < logical.numRows(); row++){
            if(output_data[row][0].equals(curr_class_value)){
                logical.set(row, 0, 1);
            }
        }
        return logical;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static SimpleMatrix gradient_descent(
            SimpleMatrix initial_parameters,
            SimpleMatrix input_data,
            SimpleMatrix output_data,
            double lambda,
            double alpha,
            String class_char, int max_iterations){
        // Console parameters
        int iteration = 0;
        double prev_cost = 3;

        // Learn parameters
        while(true){
            // Calculate Gradient
            SimpleMatrix temp_grad = lr_gradient_regularized(initial_parameters, input_data, output_data, lambda);

            // Calculate Gradient using Multithreading
            //SimpleMatrix temp_grad = lr_gradient_regularized_multithreading(initial_parameters, input_data, output_data, lambda);

            // Sum new gradient values along rows (sum along m)
            double scale_factor = alpha/input_data.numRows();
            SimpleMatrix temp_grad_scale = temp_grad.extractMatrix(1, temp_grad.numRows(), 0, 1).scale(scale_factor);
            temp_grad.insertIntoThis(1, 0, temp_grad_scale);

            // Update theta values
            initial_parameters = initial_parameters.minus(temp_grad);

            // Perform Cost function
            double cost = lr_cost_function(initial_parameters, input_data, output_data, lambda);
            double cost_diff = prev_cost - cost;
            if(iteration % (max_iterations / 2) == 0) {
                System.out.format("Class: %s |Iteration: %d | Cost: %.7e | Prev Cost Diff: %.9f\n", class_char, iteration, cost, cost_diff);
            }
            prev_cost = cost;
            iteration += 1;
            if(cost <= 1e-5 || iteration > max_iterations){  //changed from 3000 for actual running
                break;
            }

        }
        return initial_parameters;
    }


    /*
    Name       : predict_one_vs_all
    Purpose    : To make a prediction on each example m, on which class the example most likely belongs to.
    Parameters : all_theta, which is a matrix denoting the learned parameters, X which is a matrix denoting the input training data.
    Return     : predict which is a row vector denoting the class each example is
                    is most likely to belong to.
    Notes      :
                 all_theta has dimensions: (number of features + 1 x 1)
                 X has dimensions: (number of examples x number of features)
                 predict had dimensions: (number of examples x 1)
    */
    public static double predict_one_vs_all(SimpleMatrix learned_parameters, SimpleMatrix new_image){
        // Create bias unit matrix
        SimpleMatrix bias_units = new SimpleMatrix(1, 1);
        bias_units = bias_units.plus(1.0);

        // Add bias unit to new image data
        SimpleMatrix input_with_bias = bias_units.concatColumns(new_image);

        // Run new data through sigmoid function
        SimpleMatrix hypothesis = sigmoid(input_with_bias.mult(learned_parameters.transpose()));

        // Find the class with the highest prediction value
        double[] prediction_arr = hypothesis.getDDRM().getData();
        double max_val = 0;
        int prediction_class_idx = 0;
        for(int i = 0; i < prediction_arr.length; i++){
            if(prediction_arr[i] > max_val){
                max_val = prediction_arr[i];
                prediction_class_idx = i;
            }
        }
        return prediction_class_idx;
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
    */
    public static void read_data(int num_examples, SimpleMatrix input_data, String[][] output_data, String input_file_name, String output_file_name){
        try{
            String line = "";
            if (input_file_name != "") {
                // Read in Input data
                BufferedReader br = new BufferedReader(new FileReader(input_file_name)); //"src/input_data_x.csv"));
                int row = 0;
                while (row < num_examples && (line = br.readLine()) != null) {
                    //line = br.readLine();
                    String[] data = line.split(",");
                    for (int col = 0; col < data.length; col++) {
                        double new_data = Double.parseDouble(data[col]);
                        input_data.set(row, col, new_data);
                    }
                    row += 1;

                }
            }

            // Read Output data
            BufferedReader br_y = new BufferedReader(new FileReader(output_file_name));
            int row_y = 0;
            while(row_y < num_examples && (line = br_y.readLine()) != null){
                String[] data = line.split(",");
                output_data[row_y][0] = data[0];
                row_y += 1;
            }
        }
        catch(IOException e){
            System.out.println(e);
        }

    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static SimpleMatrix turn_image_into_data(BufferedImage image){
        // Transform image into data
        int[] border_data_array = new int[10800];//int[7200];

        // Obtain outline BufferedImage object for both top and bottom
        BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), 3);
        BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), 3);

        // Transform BufferedImage object into array
        int[] top_array = ObtainData.get_image_rgb_data(top_outline);
        int[] bottom_array = ObtainData.get_image_rgb_data(bottom_outline);

        // Add data to 2D array
        System.arraycopy(top_array, 0, border_data_array, 0, top_array.length);
        System.arraycopy(bottom_array, 0, border_data_array, top_array.length, bottom_array.length);

        // Convert data array to simple matrix
        double[] double_data_conv = new double[border_data_array.length];
        for(int i = 0; i < border_data_array.length; i++){
            double_data_conv[i] = (double) border_data_array[i];
        }
        SimpleMatrix new_data_matrix = new SimpleMatrix(new double[][]{double_data_conv});

        // Return new image data
        return new_data_matrix;
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void normalize_input_data(SimpleMatrix input_data, double[] mean_arr, double[] std_arr, ArrayList<Integer> constant_columns){
        // Relevant variables
        int m = input_data.numRows();
        int n = input_data.numCols();

        // Apply data normalization to the input data matrix
        for(int col = 0; col < n; col++){
            if(!constant_columns.contains(col)) {
                // Obtain column matrix
                SimpleMatrix curr_column = input_data.extractMatrix(0, m, col, col + 1);

                // Subtract the mean from each element
                curr_column = curr_column.minus(mean_arr[col]);

                // Divide each element by the standard deviation of the column
                curr_column = curr_column.divide(std_arr[col]);

                // Replace the normalized data back into the input data matrix
                input_data.insertIntoThis(0, col, curr_column);
            }
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void create_training_data_normalization_arrays(SimpleMatrix input_data){
        // Relevant variables
        int m = input_data.numRows();
        int n = input_data.numCols();

        // Mean and standard deviation arrays
        double[] mean = new double[n];
        double[] std = new double[n];
        ArrayList<Integer> constant_columns = new ArrayList<Integer>();

        // Find the mean value for each feature
        for(int col = 0; col < n; col++){
            // Obtain column matrix
            SimpleMatrix curr_column = input_data.extractMatrix(0, m, col, col + 1);

            // Calculate total sum of feature column
            double curr_column_sum =  curr_column.elementSum();
            if(curr_column_sum == 0.0) {
                constant_columns.add(col);
            }

            // Calculate mean of feature column
            mean[col] = curr_column_sum / (double) m;

            // Obtain column data in array form
            double[] curr_column_arr =  curr_column.getDDRM().data;

            // Calculate the standard deviation of the current column
            std[col] = standard_deviation(curr_column_arr);
        }

        // Store the mean,std in the object fields
        norm_constant_columns = constant_columns;
        norm_mean = mean;
        norm_std = std;

        // Create the columns for the MySQL table
        MySQLAccess.create_constant_column_columns(constant_columns);
        MySQLAccess.create_parameters_mean_std_table_and_columns(n);

        // Store the mean, std in the MySQL database
        MySQLAccess.insert_mean_std_column_data("mean", n, mean);
        MySQLAccess.insert_mean_std_column_data("std", n, std);
        MySQLAccess.insert_const_cols_data(constant_columns);
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static double standard_deviation(double[] array) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = array.length;

        for(double num : array) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: array) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/ (length-1));
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void test_parameters_vs_training_set(int num_examples, int num_features, int num_classes,
                                                       SimpleMatrix input_data, String[][] output_data,
                                                       SimpleMatrix learned_parameters, String[] class_char_arr){

        // Make a prediction on each training example
        int num_correct_predictions = 0;
        for(int i = 0; i < num_examples; i++){
            SimpleMatrix example = input_data.rows(i, i + 1);
            double predict = predict_one_vs_all(learned_parameters, example);
            int predict_idx = (int) predict;
            if(class_char_arr[predict_idx].equals(output_data[i][0])){
                num_correct_predictions += 1;
            }
            else{
                System.out.format("Actual: %s | %s : Predicted\n", output_data[i][0], class_char_arr[predict_idx]);
            }
        }

        // Determine percentage correct
        double percent = (double )num_correct_predictions / (double) num_examples * 100.0;

        // Perform training set test
        double threshold = 93;
        System.out.format("Number of Incorrect Training Examples: %d / %d\n", (num_examples - num_correct_predictions), num_examples);
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static int[] test_parameters_vs_cv_set(SimpleMatrix input_data, SimpleMatrix learned_parameters){

        // Make a prediction on each training example
        int num_examples = input_data.numRows();
        int[] prediction_arr = new int[num_examples];
        for(int i = 0; i < num_examples; i++){
            SimpleMatrix example = input_data.rows(i, i + 1);
            double predict = predict_one_vs_all(learned_parameters, example);
            prediction_arr[i] = (int) predict;
        }

        // Return the prediction array
        return prediction_arr;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static String test_new_image(int num_classes, SimpleMatrix learned_parameters, String test_file_path, String[] class_char_arr, BufferedImage new_image){
        try {
            int sw_height = 18;
            int sw_width =  15;
            int sw_delta = 5;

            // Obtain new image
            BufferedImage curr_ex_image;
            if(test_file_path.equals("")){
                curr_ex_image = new_image;
            }else{
                curr_ex_image = ImageIO.read(new File(test_file_path));
            }

            curr_ex_image = curr_ex_image.getSubimage(0,16, curr_ex_image.getWidth(), curr_ex_image.getHeight()-16);

            // Isolate the text-box
            curr_ex_image = ObtainData.text_box_recognition(curr_ex_image, sw_height, sw_width, sw_delta);

            // Perform image pre-processing
            curr_ex_image = ObtainData.background_processing(curr_ex_image);

            // Obtain character segmentation arraylist
            ArrayList<Integer> char_seg = ObtainData.character_segmentation(curr_ex_image, sw_height, sw_width, sw_delta - 3);

            // Set String array for prediction results
            String[] str_pred = new String[char_seg.size()];
            int str_arr_idx = 0;
            for(int curr_idx = 0; curr_idx < char_seg.size(); curr_idx++) {
                // Obtain a sliding window box
                BufferedImage curr_char = curr_ex_image.getSubimage(char_seg.get(curr_idx), 0, sw_width, sw_height);

                // Obtain sub-box pixel data
                double[] pixel_data = ObtainData.get_image_rgb_data_double(curr_char);

                // Transform the data into a Simple Matrix object
                SimpleMatrix new_data_matrix = new SimpleMatrix(new double[][]{pixel_data});

                // Normalize the new example data
                double[] mean = new double[810];
                double[] std = new double[810];
                ArrayList<Integer> const_cols = new ArrayList<Integer>();
                MySQLAccess.read_mean_std_const_cols(810, mean, std, const_cols);
                normalize_input_data(new_data_matrix, mean, std, const_cols);

                // Make new prediction
                //double prediction = predict_one_vs_all(learned_parameters, new_data_matrix);
                double prediction = NeuralNetwork.test_new_char(new_data_matrix);

                // Translate prediction into associated character
                int predict_idx = (int) prediction;
                String char_prediction = class_char_arr[predict_idx];
                str_pred[str_arr_idx] = char_prediction;
                str_arr_idx += 1;
            }

            // Transform character array into string
            StringBuilder object_name = new StringBuilder();
            boolean new_word_flag = false;
            for(int i = 0; i < str_pred.length; i++){
                if(str_pred[i].equals("SPACE")){
                    object_name.append("_");
                    new_word_flag = true;
                }
                else if(str_pred[i].equals("ERRAP") || str_pred[i].equals("ERRAPT")){
                    object_name.append("'");
                }
                else if(str_pred[i].equals("ERRL") || str_pred[i].equals("ERRN") || str_pred[i].equals("ERRM") ||
                        str_pred[i].equals("ERRT") || str_pred[i].equals("ERRU")){

                }
                else if(i == 0 || new_word_flag){
                    object_name.append(str_pred[i]);
                    new_word_flag = false;
                }
                else{
                    object_name.append(str_pred[i].toLowerCase());
                }
            }
            System.out.format("Object Found: %s\n", object_name.toString());
            if(object_name.toString().equals("Ammo") || object_name.toString().equals("Cell_Key")) return null;
            return object_name.toString();
        }
        catch (IOException e){
            System.out.println(e);
        }
        return "";
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void split_original_training_set(SimpleMatrix input_data, String[][] output_data,
                                                   ArrayList<Object> split_data){
        // Relevant variables
        int num_examples = input_data.numRows();

        // Index variables for training set (train), cross validation set(cv) and test set (test)
        int train_lower = 0;
        int train_upper = (int) Math.floor(num_examples * 0.6);
        int cv_lower = train_upper;
        int cv_upper = cv_lower + (int) Math.floor(num_examples * 0.2);
        int test_lower = cv_upper;
        int test_upper = cv_upper + (int) Math.floor(num_examples * 0.2);

        // Determine if any examples were missed
        if(num_examples - test_upper > 0){
            int delta = num_examples - test_upper;
            train_upper += delta;
            cv_lower += delta;
            cv_upper += delta;
            test_lower += delta;
            test_upper += delta;
        }

        // Split the original input matrix
        SimpleMatrix train_set_input = input_data.extractMatrix(train_lower, train_upper, 0, input_data.numCols());
        SimpleMatrix cv_set_input = input_data.extractMatrix(cv_lower, cv_upper, 0, input_data.numCols());
        SimpleMatrix test_set_input = input_data.extractMatrix(test_lower, test_upper, 0, input_data.numCols());

        // Split the original output matrix
        String[][] train_set_output = new String[train_upper - train_lower][1];
        String[][] cv_set_output = new String[cv_upper - cv_lower][1];
        String[][] test_set_output = new String[test_upper - test_lower][1];
        for(int row = 0; row < num_examples; row++){
            if(row < train_upper){
                train_set_output[row][0] = output_data[row][0];
            }else if(row >= cv_lower && row < cv_upper){
                cv_set_output[row - cv_lower][0] = output_data[row][0];
            }else if(row >= test_lower && row < test_upper){
                test_set_output[row - test_lower][0] = output_data[row][0];
            }
        }

        // Add the split data to an object list
        split_data.add(train_set_input);
        split_data.add(cv_set_input);
        split_data.add(test_set_input);
        split_data.add(train_set_output);
        split_data.add(cv_set_output);
        split_data.add(test_set_output);
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static double[] constant_selection(SimpleMatrix train_set_input, String[][] train_set_output,
                                          SimpleMatrix cv_set_input, String[][] cv_set_output, int num_classes,
                                          String[] class_char_arr, int max_iterations){
        // Create Alpha and Lambda constant arrays
        double[] lambda_vals = new double[10];
        double[] alpha_vals = new double[10];
        double start = 0.01;
        lambda_vals[0] = alpha_vals[0] = start;
        for(int i = 1; i < lambda_vals.length; i++){
            lambda_vals[i] = lambda_vals[i - 1] * 3;
            alpha_vals[i] = alpha_vals[i - 1] * 3;
        }

        // Transform the cv_set_output data from strings into integers
        Hashtable<String, Integer> char_to_int_map = new Hashtable<>();
        for(int i = 0; i < class_char_arr.length; i++){
            char_to_int_map.put(class_char_arr[i], i);
        }
        int[] cv_set_output_nums = new int[cv_set_input.numRows()];
        for(int i = 0; i < cv_set_output_nums.length; i++){
            cv_set_output_nums[i] = char_to_int_map.get(cv_set_output[i][0]);
        }

        // F score array initialization
        HashMap<double[], double[]> multi_cv_f_scores = new HashMap<>();

        // Generate error values, and run metrics for all combinations of lambda and alpha
        for(int l_idx = 0; l_idx < lambda_vals.length; l_idx++){
            for(int a_idx = 0; a_idx < alpha_vals.length; a_idx++){
                // Obtain the optimized parameters using the current alpha/lambda pair and the training set data
                SimpleMatrix optimized_thetas = one_vs_all(train_set_input, train_set_output, lambda_vals[l_idx],
                        alpha_vals[a_idx], num_classes, class_char_arr, max_iterations, false);

                // Make predictions for the cross validation set
                int[] prediction_cv = test_parameters_vs_cv_set(cv_set_input, optimized_thetas);

                // Run the metrics for the cross validation set
                List<double[]> metric_arrays = metrics(prediction_cv, cv_set_output, class_char_arr, num_classes);

                // Calculate the Macro F1 score and the weighted f1 score
                double[] new_scores = multi_f_score(metric_arrays.get(3), cv_set_output_nums, num_classes);
                double[] curr_keys = {lambda_vals[l_idx], alpha_vals[a_idx]};
                multi_cv_f_scores.put(curr_keys, new_scores);
            }
        }

        // Find the lambda/alpha pair associated with the highest weighted F1 score
        double max_f1_score = 0;
        double[] best_const_pair = new double[2];
        for(double[] key : multi_cv_f_scores.keySet()){
            //if(multi_cv_f_scores.get(key) > max_f1_score){
           //     max_f1_score = multi_cv_f_scores.get(key);
           //     best_const_pair = key;
           //     System.out.format("Lambda: %0.4f, Alpha:  %0.4f, F Score: %0.4f", key[0], key[1], multi_cv_f_scores.get(key));
            //}
        }
        return best_const_pair;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static List<double[]> metrics(int[] predicted_output, String[][] actual_output, String[] class_char_arr, int num_classes){
        // Precision, recall, accuracy, f score array initializations
        double[] precision_arr = new double[num_classes];
        double[] recall_arr = new double[num_classes];
        double[] accuracy_arr = new double[num_classes];
        double[] f_score_arr = new double[num_classes];

        // Find the precision and recall for each class
        for(int i = 0; i < num_classes; i++){
            double tp, fp, fn, tn;
            tp = fp = fn = tn = 0;

            // Tabulate tp, fp, fn, and tn for each class i
            for(int j = 0; j < predicted_output.length; j++){
                if(predicted_output[j] == i && actual_output[j][0].equals(class_char_arr[i])){
                    tp += 1;
                }
                else if(predicted_output[j] == i && !actual_output[j][0].equals(class_char_arr[i])){
                    fp += 1;
                }
                else if(predicted_output[j] != i && actual_output[j][0].equals(class_char_arr[i])){
                    fn += 1;
                }
                else if(predicted_output[j] != i && !actual_output[j][0].equals(class_char_arr[i])){
                    tn += 1;
                }
            }

            // Calculate the precision and recall for class i
            if(tp + tp != 0){
                precision_arr[i] = tp / (tp + fp);
            } else{
                precision_arr[i] = 0;
            }
            if(tp + fn != 0){
                recall_arr[i] = tp / (tp + fn);
            } else{
                recall_arr[i] = 0;
            }

            // Calculate accuracy for the given class
            accuracy_arr[i] = (tp + tn) / (tp + fp + fn + tn);

            // Calculate F_score for the given class
            if(precision_arr[i] + recall_arr[i] != 0) {
                f_score_arr[i] = (2 * precision_arr[i] * recall_arr[i]) / (precision_arr[i] + recall_arr[i]);
            }else{
                f_score_arr[i] = 0;
            }
        }

        // Add all lists to the return array list
        List<double[]> metric_arrays = new ArrayList<>();
        metric_arrays.add(precision_arr);
        metric_arrays.add(recall_arr);
        metric_arrays.add(accuracy_arr);
        metric_arrays.add(f_score_arr);
        return metric_arrays;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static double[] multi_f_score(double[] f_score_arr, int[] output_data, int num_classes){
        // Count the number for examples in each class
        double[] class_counts = new double[num_classes];
        for(int i = 0; i < output_data.length; i++){
            class_counts[output_data[i]] += 1;
        }

        // Calculate the Macro F1 Score
        double macro_f1_score = Arrays.stream(f_score_arr).sum() / (double) num_classes;

        // Calculate the weighted F1 Score
        double total_num_examples = output_data.length;
        double weighted_f1_score = 0;
        for(int j = 0; j < num_classes; j++){
            weighted_f1_score += (class_counts[j] * f_score_arr[j]);
        }
        weighted_f1_score /= total_num_examples;
        return new double[] {macro_f1_score, weighted_f1_score};
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String[] args){
        int number_examples = 989;
        int number_features = 7200;
        top_one_vs_all_training(number_examples, number_features);
    }

}

