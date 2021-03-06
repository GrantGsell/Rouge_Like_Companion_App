import edu.stanford.nlp.util.Pair;
import org.ejml.simple.SimpleMatrix;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.io.FileWriter;
import com.opencsv.CSVWriter;
import com.google.common.primitives.Doubles;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.imageio.ImageIO;


public class NeuralNetwork {
    // Basic parameters
    private static final String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "4", "5", "7", "8", "'", "-", "SPACE",
            "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP", "ERRAPT"};
    private static final int num_examples = 7979;
    private static final int num_features = 810;
    private static final int sliding_window_height = 18;
    private static final int sliding_window_width =  15;
    private static final int sliding_window_delta = 5;
    private static final int class_num = 1;
    private static final int ex_idx = 0;
    private static final int example_width = 400;
    private static final int input_layer_size = num_features;
    private static final int hidden_layer_size = 100;
    private static final int num_labels = characters.length;
    private static final double lambda = 0.05;
    private static final double alpha = 10000;
    public static double[] norm_mean;
    public static double[] norm_std;
    public static ArrayList<Integer> norm_constant_columns;


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void top_neural_network(){

    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void top_learn_parameters(){
        // Initialize input/output matrices
        SimpleMatrix input_data = CharacterSegmentation.obtain_sliding_window_data(sliding_window_height, sliding_window_width, sliding_window_delta,
                class_num, ex_idx);
        String[][] output_data = new String[num_examples][1];

        // Read input/output data
        SimpleMatrix useless_data = new SimpleMatrix(num_examples, 1);
        String input_file_name_useless = "";
        String output_file_name = "text_box_recog/output_text_box_recog_data.csv";
        read_data(useless_data, output_data, input_file_name_useless, output_file_name);

        // Transform the output data from strings into integers
        Hashtable<String, Integer> char_to_int_map = new Hashtable<>();
        for(int i = 0; i < characters.length; i++){
            char_to_int_map.put(characters[i], i);
        }
        double[] output_nums = new double[input_data.numRows()];
        int[] output_nums_int = new int[input_data.numRows()];
        for(int i = 0; i < output_nums.length; i++){
            output_nums[i] = (double) char_to_int_map.get(output_data[i][0]);
            output_nums_int[i] = char_to_int_map.get(output_data[i][0]);
        }

        // Transform output data into a simple matrix
        SimpleMatrix output_mat = new SimpleMatrix(new double[][]{output_nums});
        output_mat = output_mat.transpose();

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

        // Learn the parameters
        String parameter_file_path = "src/parameters.csv";
        NeuralNetwork.learn_parameters_via_gd(input_data, output_mat, input_layer_size, hidden_layer_size, num_labels, lambda, alpha, parameter_file_path);
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
    Name       : nn_cost_function
    Purpose    : To calculate the regularized cost of the neural network based
                    on the given parameters.
    Parameters :
                 parameters, a SimpleMatrix object containing the parameter constants.
                 input_data, a SimpleMatrix object containing the input data.
                 output_data, a SimpleMatrix object containing the output data.
                 input_layer_size, an int denoting the number of nodes in the input layer.
                 hidden_layer_size, an int denoting the number of nodes in the hidden layer.
                 num_labels, an int denoting the number of output nodes.
                 lambda, an int denoting the regularization parameter.
    Return     : cost a double denoting the cost the nn should pay for obtaining incorrect values.
    Notes      :
                 parameters has dimensions of
                 input_data has dimensions of (number of examples x number of features).
                 output data has dimensions of (number of examples x 1).
     */
    public static double nn_cost_function(SimpleMatrix parameters,
                                          SimpleMatrix input_data,
                                          SimpleMatrix output_data,
                                          int input_layer_size,
                                          int hidden_layer_size,
                                          int num_labels, double lambda){

        // Num examples
        int m = input_data.numRows();

        // Reshape the nn parameters back into the weights for each layer.
        SimpleMatrix theta_1 = new SimpleMatrix(hidden_layer_size, input_layer_size+1);
        SimpleMatrix theta_2 = new SimpleMatrix(num_labels, hidden_layer_size + 1);
        copy_parameters(theta_1, parameters, 0);
        copy_parameters(theta_2, parameters, theta_1.getNumElements());

        /*
        Forward Propagation
         */
        // Expand the y output values into a maxtrix of single values
        SimpleMatrix eye_matrix = SimpleMatrix.identity(num_labels);
        SimpleMatrix y_matrix = new SimpleMatrix(m, num_labels);
        for(int i = 0; i < m; i++){
            int row_num = (int)output_data.get(i, 0);
            SimpleMatrix eye_row_data = eye_matrix.rows(row_num, row_num + 1);
            y_matrix.insertIntoThis(i, 0, eye_row_data);
        }

        // Bias unit matrix
        SimpleMatrix bias_units = new SimpleMatrix(m, 1);
        bias_units = bias_units.plus(1.0);

        // Activation units for layer 1, add bias unit to the input layer
        SimpleMatrix a_1 = bias_units.concatColumns(input_data);

        // Activation units for layer 2
        SimpleMatrix z_2 = a_1.mult(theta_1.transpose());
        SimpleMatrix a_2 = sigmoid(z_2);
        a_2 = bias_units.concatColumns(a_2);

        // Activation units for layer 3 (output layer)
        SimpleMatrix z_3 = a_2.mult(theta_2.transpose());
        SimpleMatrix a_3 = sigmoid(z_3);                        // hypothesis == a_3 (for this specific network)

        // Inner Cost Function Calculation
        SimpleMatrix term_1 = y_matrix.negative().elementMult(a_3.elementLog());
        SimpleMatrix term_2 = y_matrix.negative().plus(1).elementMult(a_3.negative().plus(1).elementLog());
        SimpleMatrix inner_term = term_1.minus(term_2);
        double inner_sum = inner_term.elementSum();
        double cost = inner_sum / m;

        // Regularization Term Calculation
        SimpleMatrix temp_theta_1 =  theta_1.extractMatrix(0, theta_1.numRows(), 1, theta_1.numCols());
        SimpleMatrix temp_theta_2 =  theta_2.extractMatrix(0, theta_2.numRows(), 1, theta_2.numCols());
        double theta_1_const = temp_theta_1.elementPower(2.0).elementSum();
        double theta_2_const = temp_theta_2.elementPower(2.0).elementSum();
        double reg_term = (lambda / (2 * m)) * (theta_1_const + theta_2_const);

        // Cost Function Recalculation with Regularization
        cost += reg_term;

        return cost;
    }


    /*
    Name       : copy_parameters
    Purpose    : To turn the parameter vector into parameter matrices.
    Parameters :
                 inp_matrix, a SimpleMatrix object that is being populated with the vector parameter data.
                 inp_vector, a SimpleMatrix object containing the parameter data in a vector format.
                 vect_idx, an int denoting the starting point for the data being read from the inp_vector into the
                    inp_matrix.
    Return     : None.
    Notes      : None.
     */
    public static void copy_parameters(SimpleMatrix inp_matrix, SimpleMatrix inp_vector, int vect_idx){
        // Check to see if the input_vector is a column vector or row vector
        if(inp_vector.numCols() == 1){
            inp_vector = inp_vector.transpose();
        }

        // Copy vector values into matrix
        for(int col = 0; col < inp_matrix.numCols(); col++){
            for(int row = 0; row < inp_matrix.numRows(); row++){
                inp_matrix.set(row, col, inp_vector.get(0, vect_idx));
                vect_idx += 1;
            }
        }
    }


    /*
    Name       : nn_gradient
    Purpose    : To calculate the gradient of the neural network.
    Parameters :
                 parameters, a SimpleMatrix object containing the parameter constants.
                 input_data, a SimpleMatrix object containing the input data.
                 output_data, a SimpleMatrix object containing the output data.
                 input_layer_size, an int denoting the number of nodes in the input layer.
                 hidden_layer_size, an int denoting the number of nodes in the hidden layer.
                 num_labels, an int denoting the number of output nodes.
                 lambda, an int denoting the regularization parameter.
    Return     : A SimpleMatrix object denoting the gradient values.
    Notes      :
                 parameters has dimensions of
                 input_data has dimensions of (number of examples x number of features).
                 output data has dimensions of (number of examples x 1).
     */
    public static SimpleMatrix nn_gradient(SimpleMatrix parameters,
                                           SimpleMatrix input_data,
                                           SimpleMatrix output_data,
                                           int input_layer_size,
                                           int hidden_layer_size,
                                           int num_labels, double lambda){
        // Num examples, num features
        int m = input_data.numRows();

        // Reshape the nn parameters back into the weights for each layer.
        SimpleMatrix theta_1 = new SimpleMatrix(hidden_layer_size, input_layer_size+1);
        SimpleMatrix theta_2 = new SimpleMatrix(num_labels, hidden_layer_size + 1);
        copy_parameters(theta_1, parameters, 0);
        copy_parameters(theta_2, parameters, theta_1.getNumElements());

        // Return matrices
        SimpleMatrix theta_1_grad = new SimpleMatrix(theta_1.numRows(), theta_1.numCols());
        SimpleMatrix theta_2_grad = new SimpleMatrix(theta_2.numRows(), theta_2.numCols());

        // Expand the y output values into a maxtrix of single values
        SimpleMatrix eye_matrix = SimpleMatrix.identity(num_labels);
        SimpleMatrix y_matrix = new SimpleMatrix(m, num_labels);
        for(int i = 0; i < m; i++){
            int row_num = (int)output_data.get(i, 0);
            SimpleMatrix eye_row_data = eye_matrix.rows(row_num, row_num + 1);
            y_matrix.insertIntoThis(i, 0, eye_row_data);
        }

        /*
        Backpropagation
         */
        SimpleMatrix bias_unit = new SimpleMatrix(1, 1).plus(1.0);
        for(int t = 0; t < m; t++){
            // Forward Feed
            SimpleMatrix a_1 = input_data.extractMatrix(t, t + 1, 0, input_data.numCols());
            a_1 = bias_unit.concatColumns(a_1);
            SimpleMatrix z_2 = a_1.mult(theta_1.transpose());
            SimpleMatrix a_2 = sigmoid(z_2);
            a_2 = bias_unit.concatColumns(a_2);
            SimpleMatrix z_3 = a_2.mult(theta_2.transpose());
            SimpleMatrix a_3 = sigmoid(z_3);

            // Output Layer Delta
            SimpleMatrix delta_3 = (a_3.minus(y_matrix.extractMatrix(t, t + 1, 0, y_matrix.numCols())));

            // Hidden Layer Delta
            SimpleMatrix delta_2 = delta_3.mult(theta_2);
            delta_2 = delta_2.extractMatrix(0, delta_2.numRows(), 1, delta_2.numCols()).elementMult(sigmoid_gradient(z_2));

            // Accumulation for Theta gradients
            theta_1_grad = theta_1_grad.plus(delta_2.transpose().mult(a_1));
            theta_2_grad = theta_2_grad.plus(delta_3.transpose().mult(a_2));
        }

        // Obtain the unregularized gradient
        theta_1_grad = theta_1_grad.divide(m);
        theta_2_grad = theta_2_grad.divide(m);

        // Gradient Regularization Term calculation
        double scale_term = lambda / m;
        SimpleMatrix reg_term_theta_1 = theta_1.extractMatrix(0, theta_1.numRows(), 1, theta_1.numCols()).scale(scale_term);
        SimpleMatrix reg_term_theta_2 = theta_2.extractMatrix(0, theta_2.numRows(), 1, theta_2.numCols()).scale(scale_term);

        // Gradient Regularization
        theta_1_grad.insertIntoThis(0, 1, theta_1_grad.extractMatrix(0, theta_1_grad.numRows(), 1, theta_1_grad.numCols()).plus(reg_term_theta_1));
        theta_2_grad.insertIntoThis(0, 1, theta_2_grad.extractMatrix(0, theta_2_grad.numRows(), 1, theta_2_grad.numCols()).plus(reg_term_theta_2));

        // Unroll gradients (column wise)
        SimpleMatrix grad = unroll_matrices(theta_1_grad, theta_2_grad);
        return grad;
    }


    /*
    Name       : sigmoid_gradient
    Purpose    : To compute the gradient of the sigmoid function evaluated for
                    each element z.
    Parameters : z a simple matrix
    Return     : Returns teh gradient of the sigmoid function evaluated at z.
    Notes      : None.
     */
    public static SimpleMatrix sigmoid_gradient(SimpleMatrix z){
        return sigmoid(z).elementMult(sigmoid(z).negative().plus(1.0));
    }


    /*
    Name       : random_initialize_weights
    Purpose    : To randomly initialize the weights of a layer with l_in incoming connections, and l_out outgoing
                    connections.
    Parameters :
                 l_in, an integer denoting the number of incoming connections.
                 l_out, an integer denoting the number of outgoing connections.
    Return     : rand_weights a simple matrix of dimensions (l_out x l_in + 1)
    Notes      : None.
     */
    public static SimpleMatrix random_initialize_weights(int l_in, int l_out){
        SimpleMatrix rand_weights = SimpleMatrix.random_DDRM(l_out, l_in + 1, 0, 1,  new Random());
        double epsilon = 0.12;
        rand_weights = rand_weights.scale(2);
        rand_weights = rand_weights.scale(epsilon);
        rand_weights = rand_weights.minus(epsilon);
        return rand_weights;
    }


    /*
    Name       : debug_initialize_weights
    Purpose    : To initialize the weights of a layer with l_in incoming connections, and l_out outgoing connections
                    using a fixed strategy (the same weight values each time).
    Parameters :
                 l_in, an integer denoting the number of incoming connections.
                 l_out, an integer denoting the number of outgoing connections.
    Return     : fixed_weights, a simple matrix denoting a matrix of weights
                    that will always have the same values.
    Notes      : None.
     */
    public static SimpleMatrix debug_initialize_weights(int l_out, int l_in){
        SimpleMatrix fixed_weights = new SimpleMatrix(l_out, l_in + 1);
        int sin_index = 1;
        for(int col = 0; col < fixed_weights.numCols(); col++){
            for(int row = 0; row < fixed_weights.numRows(); row++){
                fixed_weights.set(row, col, Math.sin(sin_index));
                sin_index += 1;
            }
        }
        return fixed_weights.divide(10);
    }


    /*
    Name       : compute_numerical_gradient
    Purpose    : To perform a numerical gradient check.
    Parameters :
                 parameters, a SimpleMatrix object containing the parameter constants.
                 input_data, a SimpleMatrix object containing the input data.
                 output_data, a SimpleMatrix object containing the output data.
                 input_layer_size, an int denoting the number of nodes in the input layer.
                 hidden_layer_size, an int denoting the number of nodes in the hidden layer.
                 num_labels, an int denoting the number of output nodes.
                 lambda, an int denoting the regularization parameter.
    Return     : A SimpleMatrix denoting the numerical gradient that has been calculated.
    Notes      : None.
    */
    public static SimpleMatrix compute_numerical_gradient(SimpleMatrix theta,
                                                          SimpleMatrix input_data,
                                                          SimpleMatrix output_data,
                                                          int input_layer_size,
                                                          int hidden_layer_size,
                                                          int num_labels,
                                                          double lambda){
        // Declare matrices
        SimpleMatrix numerical_gradient = new SimpleMatrix(theta.numRows(), theta.numCols());
        SimpleMatrix perturb = new SimpleMatrix(theta.numRows(), theta.numCols());

        // Declare epsilon value
        double epsilon = 1e-4;

        // Calculate numerical gradients
        for(int i = 0; i < theta.getNumElements(); i++){
            // Set perturbation vector
            perturb.set(i, 0, epsilon);
            double loss_1 = nn_cost_function((theta.minus(perturb)),
                    input_data, output_data, input_layer_size,
                    hidden_layer_size, num_labels, lambda);
            double loss_2 = nn_cost_function((theta.plus(perturb)),
                    input_data, output_data, input_layer_size,
                    hidden_layer_size, num_labels, lambda);

            // Compute Numerical Gradient
            double num_grad_val = (loss_2 - loss_1) / (2*epsilon);
            numerical_gradient.set(i, 0, num_grad_val);
            perturb.set(i, 0, 0);
        }
        return numerical_gradient;
    }


    /*
    Name       : check_nn_gradients
    Purpose    : To create a small neural network to check the backpropagation gradients. This occurs by obtaining the
                    analytical gradients produced by the backpropagation code, and obtaining the numerical gradients
                    from the numerical gradient code. The values for both are compared respectively to one another and
                    if backpropagation was implemented correctly the two gradients should be near identical.
    Parameters : None.
    Return     : A boolean value denoting if the numerical gradients and analytical gradients have a relative difference
                    of less than 1e-9.
    Notes      : A small neural network with a relatively small number of input units and hidden units is used, thus
                    resulting in a relative small number of parameters. This is done because each dimension of the
                    parameters requires two evaluations of the cost function and can become very costly.
     */
    public static boolean check_nn_gradients(){
        // Neural Network variables
        int input_layer_size = 3;
        int hidden_layer_size = 5;
        int num_labels = 3;
        int num_examples = 5;

        // Generate pseudo-random weights
        SimpleMatrix theta_1 = debug_initialize_weights(hidden_layer_size, input_layer_size);
        SimpleMatrix theta_2 = debug_initialize_weights(num_labels, hidden_layer_size);

        // Generate input, output data
        SimpleMatrix input_data = debug_initialize_weights(num_examples, input_layer_size -1);
        SimpleMatrix output_data = new SimpleMatrix(
                new double[][]{
                        new double[]{1d},
                        new double[]{2d},
                        new double[]{0d},
                        new double[]{1d},
                        new double[]{2d}
                }
        );

        // Unroll parameters
        SimpleMatrix nn_parameters = unroll_matrices(theta_1, theta_2);

        // Compute analytical gradients
        SimpleMatrix analytical_grad = nn_gradient(nn_parameters, input_data,
                output_data, input_layer_size, hidden_layer_size, num_labels, lambda);

        // Compute numerical gradients
        SimpleMatrix numerical_grad = compute_numerical_gradient(nn_parameters,
                input_data, output_data, input_layer_size, hidden_layer_size,
                num_labels, lambda);

        // Display the two gradients and their difference
        String format_header = "| %12s | %12s | %12s |\n";
        String format_data = "| %12.9f | %12.9f | %12.5e |\n";
        String line = new String(new char[46]).replace('\0', '-');
        System.out.format(format_header, "Analytical", "Numerical", "Difference");
        System.out.println(line);
        for(int i = 0; i < analytical_grad.getNumElements(); i++) {
            double difference = Math.abs(analytical_grad.get(i, 0) - numerical_grad.get(i, 0));
            if(difference > 1e-9){
                System.out.println("Algorithmic Error");
                return false;
            }
            System.out.format(format_data, analytical_grad.get(i, 0), numerical_grad.get(i, 0), difference);
        }
        return true;
    }


    /*
    Name       : unroll_matrices
    Purpose    : To unroll two matrices into one matrix with n rows and one column where n denotes the total number of
                    elements in both matrices.
    Parameters :
                 matrix_1, a simple matrix.
                 matrix_2, a simple matrix.
    Return     : unrolled_matrices, a simple matrix denoting the unrolled matrix containing all elements of the two
                    matrices.
    Notes      : unrolled column first
     */
    public static SimpleMatrix unroll_matrices(SimpleMatrix matrix_1, SimpleMatrix matrix_2){
        int total_number_elements = matrix_1.getNumElements() + matrix_2.getNumElements();
        SimpleMatrix unrolled_matrix = new SimpleMatrix(total_number_elements, 1);
        int unroll_index = 0;
        for(int col_1 = 0; col_1 < matrix_1.numCols(); col_1++){
            for(int row_1 = 0; row_1 < matrix_1.numRows(); row_1++){
                double temp_val = matrix_1.get(row_1, col_1);
                unrolled_matrix.set(unroll_index, 0, temp_val);
                unroll_index += 1;
            }
        }
        for(int col_2 = 0; col_2 < matrix_2.numCols(); col_2++){
            for(int row_2 = 0; row_2 < matrix_2.numRows(); row_2++){
                double temp_val = matrix_2.get(row_2, col_2);
                unrolled_matrix.set(unroll_index, 0, temp_val);
                unroll_index += 1;
            }
        }
        return unrolled_matrix;
    }


    /*
    Name       : learn_parameters_via_gd
    Purpose    : To optimize the theta parameters using gradient descent.
    Parameters :
                 input_data, a SimpleMatrix object containing the input data.
                 output_data, a SimpleMatrix object containing the output data.
                 input_layer_size, an int denoting the number of nodes in the input layer.
                 hidden_layer_size, an int denoting the number of nodes in the hidden layer.
                 num_labels, an int denoting the number of output nodes.
                 lambda, an int denoting the regularization parameter.
                 file_name, a string denoting the csv file to write the parameters to.
    Return     : None.
    Notes      :
                 input_data has dimensions of (number of examples x number of features).
                 output data has dimensions of (number of examples x 1).
     */
    public static void learn_parameters_via_gd(
            SimpleMatrix input_data,
            SimpleMatrix output_data,
            int input_layer_size,
            int hidden_layer_size,
            int num_labels,
            double lambda,
            double alpha,
            String file_name){
        // Set initial theta values
        SimpleMatrix initial_theta_1 = random_initialize_weights(input_layer_size, hidden_layer_size);
        SimpleMatrix initial_theta_2 = random_initialize_weights(hidden_layer_size, num_labels);
        SimpleMatrix initial_theta_comb = unroll_matrices(initial_theta_1, initial_theta_2);

        // Set variables for console printing
        int iteration = 0;
        double prev_cost = 30;

        // Learn parameters
        while(true){
            // Calculate Gradient
            SimpleMatrix temp_grad = nn_gradient(initial_theta_comb, input_data, output_data, input_layer_size, hidden_layer_size, num_labels, lambda);

            // Sum new gradient values along rows (sum along m)
            double scale_factor = alpha/input_data.numRows();
            SimpleMatrix temp_grad_scale = temp_grad.extractMatrix(1, temp_grad.numRows(), 0, 1).scale(scale_factor);
            temp_grad.insertIntoThis(1, 0, temp_grad_scale);

            // Update theta values
            initial_theta_comb = initial_theta_comb.minus(temp_grad);

            // Perform Cost function
            double cost = nn_cost_function(initial_theta_comb, input_data, output_data, input_layer_size, hidden_layer_size, num_labels, lambda);
            double cost_diff = prev_cost - cost;
            System.out.format("Iteration: %d | Cost: %.7e | Prev Cost Diff: %.9f\n", iteration, cost, cost_diff);
            prev_cost = cost;
            iteration += 1;
            if(cost <= 1e-1 || iteration > 150){
                break;
            }

        }
        double[] learned_parameters = initial_theta_comb.getDDRM().data;
        write_parameters_to_csv(learned_parameters, file_name);
    }


    /*
    Name       : learn_parameters_via_gd
    Purpose    : To optimize the theta parameters using gradient descent.
    Parameters :
                 input_data, a SimpleMatrix object containing the input data.
                 output_data, a SimpleMatrix object containing the output data.
                 input_layer_size, an int denoting the number of nodes in the input layer.
                 hidden_layer_size, an int denoting the number of nodes in the hidden layer.
                 num_labels, an int denoting the number of output nodes.
                 lambda, an int denoting the regularization parameter.
                 file_name, a string denoting the csv file to write the parameters to.
    Return     : None.
    Notes      :
                 input_data has dimensions of (number of examples x number of features).
                 output data has dimensions of (number of examples x 1).
     */
    public static SimpleMatrix learn_parameters_via_gd(
                                                SimpleMatrix input_data,
                                                SimpleMatrix output_data,
                                                int input_layer_size,
                                                int hidden_layer_size,
                                                int num_labels,
                                                double lambda,
                                                double alpha){
        // Set initial theta values
        SimpleMatrix initial_theta_1 = random_initialize_weights(input_layer_size, hidden_layer_size);
        SimpleMatrix initial_theta_2 = random_initialize_weights(hidden_layer_size, num_labels);
        SimpleMatrix initial_theta_comb = unroll_matrices(initial_theta_1, initial_theta_2);

        // Set variables for console printing
        int iteration = 0;
        double prev_cost = 30;

        // Learn parameters
        while(true){
            // Calculate Gradient
            SimpleMatrix temp_grad = nn_gradient(initial_theta_comb, input_data, output_data, input_layer_size, hidden_layer_size, num_labels, lambda);

            // Sum new gradient values along rows (sum along m)
            double scale_factor = alpha/input_data.numRows();
            SimpleMatrix temp_grad_scale = temp_grad.extractMatrix(1, temp_grad.numRows(), 0, 1).scale(scale_factor);
            temp_grad.insertIntoThis(1, 0, temp_grad_scale);

            // Update theta values
            initial_theta_comb = initial_theta_comb.minus(temp_grad);

            // Perform Cost function
            double cost = nn_cost_function(initial_theta_comb, input_data, output_data, input_layer_size, hidden_layer_size, num_labels, lambda);
            double cost_diff = prev_cost - cost;
            System.out.format("Iteration: %d | Cost: %.7e | Prev Cost Diff: %.9f\n", iteration, cost, cost_diff);
            prev_cost = cost;
            iteration += 1;
            if(cost <= 1e-1 || iteration > 150){
                break;
            }

        }
        return initial_theta_comb;
    }


    /*
    Name       : new_predictions
    Purpose    : To predict which character each example in the input_data matrix represents.
    Parameters :
                 parameters, a SimpleMatrix denoting the learned theta parameters.
                 input_data, a SimpleMatrix denoting the character image data for each example.
                 input_layer_size, an int denoting the number of nodes in the first layer.
                 hidden_layer_size, an int denoting the number of nodes in the hidden layer.
                 num_labels, an int denoting the number of nodes in the output layer (number of classes).
    Return     : A SimpleMatrix denoting the character prediction for each example in the the input_data matrix.
    Notes      :
                 This is for testing multiple examples, not just one.
                 input_data has dimensions (number of examples x number of features)
     */
    public static SimpleMatrix new_predictions(SimpleMatrix parameters, SimpleMatrix input_data,
                                               int input_layer_size, int hidden_layer_size, int num_labels){
        // Number of examples
        int m = input_data.numRows();

        // Obtain theta values from parameters
        SimpleMatrix theta_1 = new SimpleMatrix(hidden_layer_size, input_layer_size+1);
        SimpleMatrix theta_2 = new SimpleMatrix(num_labels, hidden_layer_size + 1);
        copy_parameters(theta_1, parameters, 0);
        copy_parameters(theta_2, parameters, theta_1.getNumElements());

        // Predictions matrix
        SimpleMatrix predictions = new SimpleMatrix(input_data.numRows(), 1);

        // Bias unit matrix
        SimpleMatrix bias_units = new SimpleMatrix(m, 1);
        bias_units = bias_units.plus(1.0);

        // Calculate predictions
        SimpleMatrix input_1 = bias_units.concatColumns(input_data).mult(theta_1.transpose());
        SimpleMatrix h1 = sigmoid(input_1);
        SimpleMatrix input_2 = bias_units.concatColumns(h1).mult(theta_2.transpose());
        SimpleMatrix h2 = sigmoid(input_2);

        // Find the max value for each prediction
        for(int i = 0; i < m; i++){
            // Obtain row data in array form
            double[] row_data = h2.rows(i, i + 1).getDDRM().getData();

            // Find the max value in the row and its corresponding index
            double max_val = Doubles.max(row_data);
            double max_val_index = Doubles.indexOf(row_data, max_val);

            // Set the prediction value
            predictions.set(i, 0, max_val_index);

        }
        return predictions;
    }


    /*
    Name       : new_prediction
    Purpose    : To predict which character the input_data matrix represents.
    Parameters :
                 parameters, a SimpleMatrix denoting the learned theta parameters.
                 input_data, a SimpleMatrix denoting the character image in a matrix data format.
                 input_layer_size, an int denoting the number of nodes in the first layer.
                 hidden_layer_size, an int denoting the number of nodes in the hidden layer.
                 num_labels, an int denoting the number of nodes in the output layer (number of classes).
    Return     : A double denoting the character prediction index for the input_data image.
    Notes      : This if for predicting one example.
     */
    public static double new_prediction(SimpleMatrix parameters, SimpleMatrix input_data,
                                        int input_layer_size, int hidden_layer_size, int num_labels){
        // Number of examples
        int m = input_data.numRows();

        // Obtain theta values from parameters
        SimpleMatrix theta_1 = new SimpleMatrix(hidden_layer_size, input_layer_size+1);
        SimpleMatrix theta_2 = new SimpleMatrix(num_labels, hidden_layer_size + 1);
        copy_parameters(theta_1, parameters, 0);
        copy_parameters(theta_2, parameters, theta_1.getNumElements());

        // Bias unit matrix
        SimpleMatrix bias_units = new SimpleMatrix(m, 1);
        bias_units = bias_units.plus(1.0);

        // Calculate predictions
        SimpleMatrix input_1 = bias_units.concatColumns(input_data).mult(theta_1.transpose());
        SimpleMatrix h1 = sigmoid(input_1);
        SimpleMatrix input_2 = bias_units.concatColumns(h1).mult(theta_2.transpose());
        SimpleMatrix h2 = sigmoid(input_2);

        // Obtain row data in array form
        double[] row_data = h2.rows(0, 1).getDDRM().getData();

        // Find the max value in the row and its corresponding index
        double max_val = Doubles.max(row_data);

        return Doubles.indexOf(row_data, max_val);
    }


    /*
    Name       : write_parameters_to_csv
    Purpose    : To write the learned parameters to a cvs file.
    Parameters :
                 parameters, a double array denoting the learned parameters theta.
                 file_name, a String denoting the csv file name.
    Return     : None.
    Notes      : None.
     */
    public static void write_parameters_to_csv(double[] parameters, String file_name){
        // convert double array to string array
        String[] str = new String[parameters.length];
        for(int i = 0; i < parameters.length; i++){
            str[i] = String.valueOf(parameters[i]);
        }

        try {
            // Write data to file
            String filename = file_name; // "src/parameters.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(filename), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            writer.writeNext(str);
            writer.flush();
            System.out.println("Parameter Data Written\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    /*
    Name       : read_parameters
    Purpose    : To read in the parameter values from the csv file into a matrix format.
    Parameters :
                 parameter_vals, a SimpleMatrix object where the read in parameter will be stored.
                 parameter_file_name, a String denoting the name of the csv file containing the parameter values.
    Return     : None.
    Notes      : None.
     */
    public static void read_parameters(SimpleMatrix parameter_vals, String parameter_file_name){
        try{
            // Read in Input data
            BufferedReader br = new BufferedReader(new FileReader(parameter_file_name));// "src/parameters.csv"));
            String line = "";
            int row = 0;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");
                for(int col = 0; col < parameter_vals.numCols(); col++){
                    String dub_string = data[col];
                    dub_string = dub_string.replaceAll("\"","");
                    double new_data = Double.valueOf(dub_string);
                    parameter_vals.set(row, col, new_data);
                }
                row += 1;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    /*
    Name       : test_new_char
    Purpose    : To determine which character the input_data matrix represents.
    Parameters : input_data, a SimpleMatrix denoting a character image that has been transformed into a matrix data
                    format.
    Return     : A double denoting the class number prediction for the input_data, that corresponds to a specific
                    character in the characters field.
    Notes      : None.
     */
    public static double test_new_char(SimpleMatrix input_data){
        /*
        Testing purposes only
         */
        int input_layer_size = 810;
        int hidden_layer_size = 100;
        int num_labels = characters.length;
        String parameter_file_path = "src/parameters.csv";
        int theta_size = (hidden_layer_size * (input_layer_size + 1)) + (num_labels * (hidden_layer_size + 1));
        SimpleMatrix parameter_mat = new SimpleMatrix(1, theta_size);
        NeuralNetwork.read_parameters(parameter_mat, parameter_file_path);
        return NeuralNetwork.new_prediction(parameter_mat, input_data, input_layer_size, hidden_layer_size, num_labels);
    }

    /*
    Name       : metrics
    Purpose    : To calculate the precision, recall, accuracy and f_score of each class.
    Parameters :
                 predicted_output, an int array denoting the prediction for each example.
                 actual_output, a 2D string array denoting the correct values for each example.
    Return     : A list of double arrays denoting the metric values for each class.
    Notes      : None.
     */
    public static List<double[]> metrics(int[] predicted_output, String[][] actual_output){
        // Precision, recall, accuracy, f score array initializations
        double[] precision_arr = new double[characters.length];
        double[] recall_arr = new double[characters.length];
        double[] accuracy_arr = new double[characters.length];
        double[] f_score_arr = new double[characters.length];

        // Find the precision and recall for each class
        for(int i = 0; i < characters.length; i++){
            double tp, fp, fn, tn;
            tp = fp = fn = tn = 0;

            // Tabulate tp, fp, fn, and tn for each class i
            for(int j = 0; j < predicted_output.length; j++){
                if(predicted_output[j] == i && actual_output[j][0].equals(characters[i])){
                    tp += 1;
                }
                else if(predicted_output[j] == i && !actual_output[j][0].equals(characters[i])){
                    fp += 1;
                }
                else if(predicted_output[j] != i && actual_output[j][0].equals(characters[i])){
                    fn += 1;
                }
                else if(predicted_output[j] != i && !actual_output[j][0].equals(characters[i])){
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
    Name       : multi_f_score
    Purpose    : To calculate the weighted F-score and macro F-score given the F-score for each class.
    Parameters :
                 f_score_arr, a double array denoting the f score values for each class.
                 output_data, an int array denoting the numerical representation for the correct output values.
    Return     : A double array, with the first element containing the macro F-score and the second element containing
                    the weighted F-score.
    Notes      : None.
     */
    public static double[] multi_f_score(double[] f_score_arr, int[] output_data){
        // Count the number for examples in each class
        double[] class_counts = new double[characters.length];
        for(int i = 0; i < output_data.length; i++){
            class_counts[output_data[i]] += 1;
        }

        // Calculate the Macro F1 Score
        double macro_f1_score = Arrays.stream(f_score_arr).sum() / (double) characters.length;

        // Calculate the weighted F1 Score
        double total_num_examples = output_data.length;
        double weighted_f1_score = 0;
        for(int j = 0; j < characters.length; j++){
            weighted_f1_score += (class_counts[j] * f_score_arr[j]);
        }
        weighted_f1_score /= total_num_examples;
        return new double[] {macro_f1_score, weighted_f1_score};
    }


    /*
    Name       : test_new_image
    Purpose    : To construct a String word prediction for an objects name text within an image.
    Parameters :
                 test_file_path, a String denoting the file path location for a test image. Only used for testing.
                 new_image, a BufferedImage object denoting an image containing an objects name text.
    Return     : A String denoting a prediction for the name of the name text within the image.
    Notes      : None.
     */
    public static String test_new_image(String test_file_path, BufferedImage new_image){
        try {
            int sw_height = 18;
            int sw_width =  15;

            // Obtain new image
            BufferedImage curr_ex_image;
            if(test_file_path.equals("")){
                curr_ex_image = new_image;
            }else{
                curr_ex_image = ImageIO.read(new File(test_file_path));
            }

            // Perform Character Segmentation
            Pair<ArrayList<Integer>, BufferedImage> pair =  CharacterSegmentation.top_character_segmentation(curr_ex_image);
            ArrayList<Integer> char_seg = pair.first();
            curr_ex_image = pair.second();

            // Set String array for prediction results
            String[] str_pred = new String[char_seg.size()];
            int str_arr_idx = 0;
            for(int curr_idx = 0; curr_idx < char_seg.size(); curr_idx++) {
                // Obtain a sliding window box
                BufferedImage curr_char = curr_ex_image.getSubimage(char_seg.get(curr_idx), 0, sw_width, sw_height);

                // Obtain sub-box pixel data
                double[] pixel_data = CharacterSegmentation.get_image_rgb_data_double(curr_char);

                // Transform the data into a Simple Matrix object
                SimpleMatrix new_data_matrix = new SimpleMatrix(new double[][]{pixel_data});

                // Normalize the new example data
                double[] mean = new double[810];
                double[] std = new double[810];
                ArrayList<Integer> const_cols = new ArrayList<>();
                MySQLAccess.read_mean_std_const_cols(810, mean, std, const_cols);
                normalize_input_data(new_data_matrix, mean, std, const_cols);

                // Make new prediction
                double prediction = test_new_char(new_data_matrix);

                // Translate prediction into associated character
                int predict_idx = (int) prediction;
                String char_prediction = characters[predict_idx];
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
            e.printStackTrace();
        }
        return "";
    }


    /*
    Name       : read_data
    Purpose    : Read the input data from a csv file and transform it into a matrix data format.
    Parameters :
                 input_data, a SimpleMatrix denoting the matrix for the input data to be read into.
                 output_data, a 2D string array denoting the array for the output data to be read into.
                 input_file_name, a String denoting the csv file name containing the input data.
                 output_file_name, a String denoting the csv file containing the output data.
    Return     : None.
    Notes      : None.
    */
    public static void read_data(SimpleMatrix input_data, String[][] output_data, String input_file_name, String output_file_name){
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
            e.printStackTrace();
        }

    }

    /*
    Name       : create_training_data_normalization_arrays
    Purpose    : To create the mean and standard deviation arrays for each feature (column) in the input data matrix.
    Parameters : input_data, a SimpleMatrix object denoting the input data matrix.
    Return     : None.
    Notes      : None.
     */
    public static void create_training_data_normalization_arrays(SimpleMatrix input_data){
        // The number of examples and features respectively
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
    Name       : standard_deviation
    Purpose    : To calculate the standard deviation for an array.
    Parameters : array, a double array denoting the array which we want to calculate the standard deviation for.
    Return     : None.
    Notes      : None.
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
    Name       : normalize_input_data
    Purpose    : To normalize the entire input_data set column-wise with each column having a zero mean unit variance.
    Parameters :
                 input_data, a SimpleMatrix denoting the input data to be normalized.
                 mean_arr, a double array denoting the mean value for each column.
                 std_arr, a double array denoting the standard deviation for each column.
                 constant_columns, an ArrayList denoting the columns in which all elements are the same.
    Return     : None.
    Notes      : If all elements within a column are the same value, the column is not normalized.
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
        Name       : learning_curve
        Purpose    :
        Parameters :
        Return     :
        Notes      :
         */
    private static void learning_curve(SimpleMatrix input_train, SimpleMatrix output_train, SimpleMatrix input_cv,
                                       SimpleMatrix output_cv, double lambda){
        // Number of training examples
        int m = input_train.numRows();

        // Error value arrays
        double[] error_train = new double[m];
        double[] error_cv = new double[m];
        double[] num_examples = new double[m];
        for(int i = 1; i < m; ++i){
            num_examples[i] = i;
        }

        // Evaluate the training, cross validation error for the first i training examples
        for(int i = 1; i < m; ++i){
            // Learn the parameters theta for each new set of input examples
            SimpleMatrix partial_input_train = input_train.extractMatrix(0, i, 0 , input_train.numCols());
            SimpleMatrix partial_output_train = output_train.extractMatrix(0, i, 0 , output_train.numCols());
            SimpleMatrix optim_theta = learn_parameters_via_gd(partial_input_train, partial_output_train, input_layer_size, hidden_layer_size, num_labels, lambda, alpha);

            // Obtain the training error, without regularization
            error_train[i] = nn_cost_function(optim_theta, partial_input_train, partial_output_train,input_layer_size, hidden_layer_size, num_labels, 0);

            // Obtain the cross validation error over the entire set of cv input/output, not regularized
            error_cv[i] = nn_cost_function(optim_theta, input_cv, output_cv, input_layer_size, hidden_layer_size, num_labels, 0);
        }

        // Plot the cv/training set error vs the number of training examples
        XYChart chart = QuickChart.getChart("Learning Curves", "Number of Training Examples", "Error", "Train",
                error_train, num_examples);

        // Display the chart
        new SwingWrapper(chart).displayChart();
    }


    /*
    Name       : main
    Purpose    : Client testing code.
    Parameters : Standard main arguments.
    Return     : None.
    Notes      : None.
     */
    public static void main(String[] args){
        // Run gradient check before learning
        if(!check_nn_gradients()){
            return;
        }

        // Initialize input/output matrices
        SimpleMatrix input_data = CharacterSegmentation.obtain_sliding_window_data(sliding_window_height, sliding_window_width, sliding_window_delta,
                class_num, ex_idx);
        String[][] output_data = new String[num_examples][1];

        // Read input/output data
        SimpleMatrix useless_data = new SimpleMatrix(num_examples, 1);
        String input_file_name_useless = "";
        String output_file_name = "text_box_recog/output_text_box_recog_data.csv";
        read_data(useless_data, output_data, input_file_name_useless, output_file_name);

        // Transform the output data from strings into integers
        Hashtable<String, Integer> char_to_int_map = new Hashtable<>();
        for(int i = 0; i < characters.length; i++){
            char_to_int_map.put(characters[i], i);
        }
        double[] output_nums = new double[input_data.numRows()];
        int[] output_nums_int = new int[input_data.numRows()];
        for(int i = 0; i < output_nums.length; i++){
            output_nums[i] = (double) char_to_int_map.get(output_data[i][0]);
            output_nums_int[i] = char_to_int_map.get(output_data[i][0]);
        }

        // Transform output data into a simple matrix
        SimpleMatrix output_mat = new SimpleMatrix(new double[][]{output_nums});
        output_mat = output_mat.transpose();

        // Clear and initialize the MySQL tables before obtaining the necessary data
        MySQLAccess.clear_and_initialize();

        // Normalize the input data
        create_training_data_normalization_arrays(input_data);

        // Obtain the normalized data
        double[] mean = new double[num_features];
        double[] std = new double[num_features];
        ArrayList<Integer> const_cols = new ArrayList<>();
        MySQLAccess.read_mean_std_const_cols(num_features, mean, std, const_cols);

        // Normalize the training_data
        normalize_input_data(input_data, mean, std, const_cols);

        // Learn the parameters
        String parameter_file_path = "src/parameters.csv";
        NeuralNetwork.learn_parameters_via_gd(input_data, output_mat, input_layer_size, hidden_layer_size, num_labels, lambda, alpha, parameter_file_path);

        // Read in parameter data
        int theta_size = (hidden_layer_size * (input_layer_size + 1)) + (num_labels * (hidden_layer_size + 1));
        SimpleMatrix parameter_mat = new SimpleMatrix(1, theta_size);
        read_parameters(parameter_mat, parameter_file_path);

        SimpleMatrix prediction_mat = NeuralNetwork.new_predictions(parameter_mat, input_data, input_layer_size, hidden_layer_size, num_labels);

        // Display predictions/actual values
        double[] prediction_data = prediction_mat.getDDRM().getData();
        double[] actual_data = output_mat.getDDRM().getData();
        int total_correct = 0;
        for(int i = 0; i < prediction_data.length; i++){
            if(actual_data[i] == prediction_data[i]){
                total_correct += 1;
            }
            else{
                System.out.print("Wrong");
                System.out.format("Actual: %s | %s : Predicted\n", characters[(int)actual_data[i]], characters[(int)prediction_data[i]]);
            }
        }
        double percent_correct = ((double)total_correct/ (double)num_examples) * 100;
        System.out.format("Percentage Correct: %.2f%%\n", percent_correct);

        // Perform metrics
        int[] prediction_data_int = new int[prediction_data.length];
        for(int i = 0; i < prediction_data_int.length; i++){
            prediction_data_int[i] = (int) prediction_data[i];
        }

        List<double[]> metric_arrays = metrics(prediction_data_int, output_data);
        double[] new_scores = multi_f_score(metric_arrays.get(3), output_nums_int);
    }
}