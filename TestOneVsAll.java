import org.ejml.EjmlUnitTests;
import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestOneVsAll {
    @Test
    void test_sigmoid() {

        // Create arrays for input matrices
        double[][] test_arr_inp_0 = new double[][]{new double[]{-5d}};
        double[][] test_arr_inp_1 = new double[][]{new double[]{0d}};
        double[][] test_arr_inp_2 = new double[][]{new double[]{5d}};
        double[][] test_arr_inp_3 = new double[][]{new double[]{4d, 5d, 6d}};
        double[][] test_arr_inp_4 = new double[][]{
                new double[]{-1d},
                new double[]{0d},
                new double[]{1d}
        };
        double[][] test_arr_inp_5 = new double[4][5];
        double init_val = -1.0;
        for(int col = 0; col < test_arr_inp_5[0].length; col++){
            for(int row = 0; row < test_arr_inp_5.length; row++){
                test_arr_inp_5[row][col] = init_val;
                init_val += 0.1;
            }
        }

        // Create arrays for output matrices
        double[][] test_arr_out_0 = new double[][]{ new double[]{0.006692851}};
        double[][] test_arr_out_1 = new double[][]{ new double[]{0.500000000}};
        double[][] test_arr_out_2 = new double[][]{ new double[]{0.993307149}};
        double[][] test_arr_out_3 = new double[][]{ new double[]{0.9820137900, 0.9933071490, 0.997527377}};
        double[][] test_arr_out_4 = new double[][]{ new double[]{0.268941421},
                new double[]{0.500000000},
                new double[]{0.731058579}
        };
        double[][] test_arr_out_5 = new double[][]{
                new double[]{0.268941421, 0.354343694, 0.450166003, 0.549833997, 0.645656306},
                new double[]{0.289050497, 0.377540669, 0.475020813, 0.574442517, 0.668187772},
                new double[]{0.310025519, 0.401312340, 0.500000000, 0.598687660, 0.689974481},
                new double[]{0.331812228, 0.425557483, 0.524979187, 0.622459331, 0.710949503}
        };

        // Create input SimpleMatrices
        SimpleMatrix test_inp_0 = new SimpleMatrix(test_arr_inp_0);
        SimpleMatrix test_inp_1 = new SimpleMatrix(test_arr_inp_1);
        SimpleMatrix test_inp_2 = new SimpleMatrix(test_arr_inp_2);
        SimpleMatrix test_inp_3 = new SimpleMatrix(test_arr_inp_3);
        SimpleMatrix test_inp_4 = new SimpleMatrix(test_arr_inp_4);
        SimpleMatrix test_inp_5 = new SimpleMatrix(test_arr_inp_5);

        // Create output SimpleMatrices
        SimpleMatrix test_out_0 = new SimpleMatrix(test_arr_out_0);
        SimpleMatrix test_out_1 = new SimpleMatrix(test_arr_out_1);
        SimpleMatrix test_out_2 = new SimpleMatrix(test_arr_out_2);
        SimpleMatrix test_out_3 = new SimpleMatrix(test_arr_out_3);
        SimpleMatrix test_out_4 = new SimpleMatrix(test_arr_out_4);
        SimpleMatrix test_out_5 = new SimpleMatrix(test_arr_out_5);

        // Perform assertions
        double tolerance = 0.000000001;
        EjmlUnitTests.assertEquals(test_out_0.getDDRM(), NeuralNetwork.sigmoid(test_inp_0).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_1.getDDRM(), NeuralNetwork.sigmoid(test_inp_1).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_2.getDDRM(), NeuralNetwork.sigmoid(test_inp_2).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_3.getDDRM(), NeuralNetwork.sigmoid(test_inp_3).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_4.getDDRM(), NeuralNetwork.sigmoid(test_inp_4).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_5.getDDRM(), NeuralNetwork.sigmoid(test_inp_5).getDDRM(), tolerance);
    }

    @Test
    void test_lr_cost_function(){
        double tolerance = 1e-5;

        SimpleMatrix theta = new SimpleMatrix(new double[][]{
                new double[]{-2d},
                new double[]{-1d},
                new double[]{1d},
                new double[]{2d}
        });

        SimpleMatrix input = new SimpleMatrix(new double[][]{
                new double[]{1.0, 0.1, 0.60, 1.10},
                new double[]{1.0, 0.2, 0.70, 1.20},
                new double[]{1.0, 0.3, 0.80, 1.30},
                new double[]{1.0, 0.4, 0.90, 1.40},
                new double[]{1.0, 0.5, 1.00, 1.50},
        });

        SimpleMatrix output = new SimpleMatrix(new double[][]{
                new double[]{1d},
                new double[]{0d},
                new double[]{1d},
                new double[]{0d},
                new double[]{1d}
        });

        SimpleMatrix cost_un_regularized = new SimpleMatrix(new double[][]{
                new double[]{0.734819396}
        });

        SimpleMatrix cost_regularized = new SimpleMatrix(new double[][]{
                new double[]{2.534819396}
        });

        // Test Cost function
        Assertions.assertEquals(cost_un_regularized.getDDRM().getData()[0],
                OneVsAll.lr_cost_function(theta, input, output, 0),tolerance);
        Assertions.assertEquals(cost_regularized.getDDRM().getData()[0],
                OneVsAll.lr_cost_function(theta, input, output, 3),tolerance);
    }

    @Test
    void test_lr_gradient(){
        double tolerance = 1e-5;

        SimpleMatrix theta = new SimpleMatrix(new double[][]{
                new double[]{-2d},
                new double[]{-1d},
                new double[]{1d},
                new double[]{2d}
        });

        SimpleMatrix input = new SimpleMatrix(new double[][]{
                new double[]{1.0, 0.1, 0.60, 1.10},
                new double[]{1.0, 0.2, 0.70, 1.20},
                new double[]{1.0, 0.3, 0.80, 1.30},
                new double[]{1.0, 0.4, 0.90, 1.40},
                new double[]{1.0, 0.5, 1.00, 1.50},
        });

        SimpleMatrix output = new SimpleMatrix(new double[][]{
                new double[]{1d},
                new double[]{0d},
                new double[]{1d},
                new double[]{0d},
                new double[]{1d}
        });

        SimpleMatrix gradient_un_regularized = new SimpleMatrix(new double[][]{
                new double[]{0.146561368},
                new double[]{0.051441588},
                new double[]{0.124722272},
                new double[]{0.198002956}
        });

        SimpleMatrix gradient_regularized = new SimpleMatrix(new double[][]{
                new double[]{0.146561368},
                new double[]{-0.548558412},
                new double[]{0.724722272},
                new double[]{1.398002956}
        });

        // Test Cost function
        EjmlUnitTests.assertEquals(gradient_un_regularized.getDDRM(),
                OneVsAll.lr_gradient_regularized(theta, input, output, 0).getDDRM(),tolerance);
        EjmlUnitTests.assertEquals(gradient_regularized.getDDRM(),
                OneVsAll.lr_gradient_regularized(theta, input, output, 3).getDDRM(),tolerance);
    }

    @Test
    void test_logical_array(){
        // Set 'output' matrices
        SimpleMatrix output = new SimpleMatrix(new double[][]{
                new double[]{1},
                new double[]{2},
                new double[]{3},
                new double[]{3},
                new double[]{1}
        });

        // Set return matrix for class = 0
        SimpleMatrix return_0 = new SimpleMatrix(new double[][]{
                new double[]{0},
                new double[]{0},
                new double[]{0},
                new double[]{0},
                new double[]{0}
        });

        // Set return matrix for class = 1
        SimpleMatrix return_1 = new SimpleMatrix(new double[][]{
                new double[]{1},
                new double[]{0},
                new double[]{0},
                new double[]{0},
                new double[]{1}
        });

        // Set return matrix for class = 2
        SimpleMatrix return_2 = new SimpleMatrix(new double[][]{
                new double[]{0},
                new double[]{1},
                new double[]{0},
                new double[]{0},
                new double[]{0}
        });

        // Set return matrix for class = 3
        SimpleMatrix return_3 = new SimpleMatrix(new double[][]{
                new double[]{0},
                new double[]{0},
                new double[]{1},
                new double[]{1},
                new double[]{0}
        });


        // Run Tests
        //EjmlUnitTests.assertEquals(return_0.getDDRM(), OneVsAll.logical_array(output, 0).getDDRM());
        //EjmlUnitTests.assertEquals(return_1.getDDRM(), OneVsAll.logical_array(output, 1).getDDRM());
        //EjmlUnitTests.assertEquals(return_2.getDDRM(), OneVsAll.logical_array(output, 2).getDDRM());
        //EjmlUnitTests.assertEquals(return_3.getDDRM(), OneVsAll.logical_array(output, 3).getDDRM());
    }


    // Uses all training examples to determine algorithm accuracy. If under given threshold algorithm fails
    @Test
    void test_one_vs_all(){
        // Create a new object (needed to hold the std, mean values)
        OneVsAll obj = new OneVsAll();

        // Testing variables
        int number_examples = 989;
        int number_features = 7200;
        int num_classes = 4;

        // Read in all training examples
        SimpleMatrix input_data = new SimpleMatrix(number_examples, number_features);
        SimpleMatrix output_data = new SimpleMatrix(number_examples, 1);
        SimpleMatrix learned_parameters = new SimpleMatrix(num_classes, number_features + 1);
        SimpleMatrix mean_norm_arr = new SimpleMatrix(1, number_features);
        SimpleMatrix std_norm_arr = new SimpleMatrix(1, number_features);

        // Read input/output data
        String input_file_name = "src/lr_border_data.csv";
        String output_file_name = "src/lr_output_data.csv";
        //OneVsAll.read_data(number_examples, input_data, output_data, input_file_name, output_file_name);

        // Read in learned parameters
        OneVsAll.read_parameter_data(learned_parameters, "src/logistic_regression_learned_parameters.csv");

        // Read in normalization vectors
        OneVsAll.read_parameter_data(mean_norm_arr, "src/norm_mean_arr.csv");
        OneVsAll.read_parameter_data(std_norm_arr, "src/norm_std_arr.csv");

        // Normalize the input data
        OneVsAll.normalize_input_data(input_data, mean_norm_arr.getDDRM().data, std_norm_arr.getDDRM().data);

        // Make a prediction on each training example
        int num_correct_predictions = 0;
        for(int i = 0; i < number_examples; i++){
            SimpleMatrix example = input_data.rows(i, i + 1);
            double predict = OneVsAll.predict_one_vs_all(learned_parameters, example, num_classes);
            if(predict == output_data.get(i, 0)){
                num_correct_predictions += 1;
            }
            else{
                System.out.format("Actual: %.2f | %.2f : Predicted\n", output_data.get(i, 0), predict);
            }
        }

        // Determine percentage correct
        double percent = (double )num_correct_predictions / (double) number_examples * 100.0;

        // Perform training set test
        double threshold = 93;
        //Assertions.assertTrue(percent >= threshold);

        // Test new prediction
        double[][] border_data_array = new double[1][7200];
        String test_file_path = "screenshots/class_2_0.jpg";
        try {
            BufferedImage image = ImageIO.read(new File(test_file_path));
            // Obtain outline BufferedImage object for both top and bottom
            BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), 3);
            BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), 3);

            // Transform BufferedImage object into array
            double[] top_array = CharacterSegmentation.get_image_rgb_data_double(top_outline);
            double[] bottom_array = CharacterSegmentation.get_image_rgb_data_double(bottom_outline);

            // Add data to 2D array
            System.arraycopy(top_array, 0, border_data_array[0], 0, top_array.length);
            System.arraycopy(bottom_array, 0, border_data_array[0], top_array.length, bottom_array.length);

            // Make new simplematrix data object
            double[] double_data_conv = new double[border_data_array[0].length];
            for(int i = 0; i < border_data_array[0].length; i++){
                double_data_conv[i] = (double) border_data_array[0][i];
            }
            SimpleMatrix new_data_matrix = new SimpleMatrix(new double[][]{double_data_conv});

            // Normalize the new example data
            obj.normalize_input_data(new_data_matrix, mean_norm_arr.getDDRM().data, std_norm_arr.getDDRM().data);

            // Make new prediction
            double prediction = OneVsAll.predict_one_vs_all(learned_parameters, new_data_matrix, 4);
            int temp = 0;


        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    @Test
    void test_normalize_input_data(){
        SimpleMatrix test_0 = new SimpleMatrix(new double[][]{
                new double[]{1d, 2d, 3d, 4d},
                new double[]{5d, 6d, 7d, 8d},
                new double[]{9d, 10d, 11d, 12d}
        });

        SimpleMatrix return_0 = new SimpleMatrix(new double[][]{
                new double[]{-1d, -1d, -1d, -1d},
                new double[]{0d, 0d, 0d, 0d},
                new double[]{1d, 1d, 1d, 1d}
        });
        OneVsAll obj = new OneVsAll();
        obj.create_training_data_normalization_arrays(test_0);
        obj.normalize_input_data(test_0, obj.norm_mean, obj.norm_std);
        EjmlUnitTests.assertEquals(return_0.getDDRM(), test_0.getDDRM());
        int test = 1;

    }

    @Test
    void test_standard_deviation(){
        double[] test_arr_0 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        double return_0 = 3.0276503540975;
        double tolerance = 1e-5;
        Assertions.assertEquals(return_0, OneVsAll.standard_deviation(test_arr_0), tolerance);
    }

}