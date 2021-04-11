import org.junit.jupiter.api.Assertions;
import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Test;
import org.ejml.EjmlUnitTests;

class TestNeuralNetwork {

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

        // Create NeuralNetwork object
        NeuralNetwork nn = new NeuralNetwork();

        // Perform assertions
        double tolerance = 0.000000001;
        EjmlUnitTests.assertEquals(test_out_0.getDDRM(), nn.sigmoid(test_inp_0).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_1.getDDRM(), nn.sigmoid(test_inp_1).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_2.getDDRM(), nn.sigmoid(test_inp_2).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_3.getDDRM(), nn.sigmoid(test_inp_3).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_4.getDDRM(), nn.sigmoid(test_inp_4).getDDRM(), tolerance);
        EjmlUnitTests.assertEquals(test_out_5.getDDRM(), nn.sigmoid(test_inp_5).getDDRM(), tolerance);
    }

    @Test
    void test_nn_cost_function(){
        // Input Parameter Instantiation
        int num_inp_layers = 2;
        int num_hidden_layers = 2;
        int num_labels = 4;
        SimpleMatrix parameters  = new SimpleMatrix(new double[][]{
                new double[]{0.100, 0.200, 0.300, 0.400, 0.500, 0.600, 0.700,
                             0.800, 0.900, 1.000, 1.100, 1.200, 1.300, 1.400,
                             1.500, 1.600, 1.700, 1.800}
        });
        SimpleMatrix input_data = new SimpleMatrix(new double[][]{
                new double[]{0.5403023, -0.4161468},
                new double[]{-0.9899925, -0.6536436},
                new double[]{0.2836622, 0.9601703}
        });
        SimpleMatrix output_data = new SimpleMatrix(new double[][]{
                new double[]{4d},
                new double[]{2d},
                new double[]{3d}
        });
        int lambda = 4;

        // Expected Results
        double un_regularized_cost_output = 7.4069698;
        double regularized_cost_output = 19.4736365;

        // Create NeuralNetwork object
        NeuralNetwork nn = new NeuralNetwork();

        // Tolerance Variable
        double tolerance = 0.0000001;

        // Unregularized Cost Function Test
        Assertions.assertEquals(un_regularized_cost_output, nn.nn_cost_function(
                parameters, input_data, output_data, num_inp_layers,
                num_hidden_layers, num_labels, 0 ), tolerance);

        // Regularized Cost Function Test
        Assertions.assertEquals(regularized_cost_output, nn.nn_cost_function(
                parameters, input_data, output_data, num_inp_layers,
                num_hidden_layers, num_labels, lambda ), tolerance);

        return;
    }

    @Test
    void test_sigmoid_gradient(){
        // Input Data
        SimpleMatrix input_data = new SimpleMatrix(
                new double[][]{
                        new double[]{-1d, -2d, -3d},
                        new double[]{8d, 1d, 6d},
                        new double[]{3d, 5d, 7d},
                        new double[]{4d, 9d, 2d}
                }
        );

        // Output Data
        SimpleMatrix output_data = new SimpleMatrix(
                new double[][]{
                        new double[]{0.196611933, 0.104993585, 0.045176660},
                        new double[]{0.000335238, 0.196611933, 0.002466509},
                        new double[]{0.045176660, 0.006648057, 0.000910221},
                        new double[]{0.017662706, 0.000123379, 0.104993585}
                }
        );

        // Create NeuralNetwork object
        NeuralNetwork nn = new NeuralNetwork();

        // Tolerance Variable
        double tolerance = 0.0000001;

        // Testing
        EjmlUnitTests.assertEquals(output_data.getDDRM(),
                nn.sigmoid_gradient(input_data).getDDRM(), tolerance);

        return;
    }

    @Test
    void test_nn_gradient(){
        // Input Data
        // Input Parameter Instantiation
        int num_inp_layers = 2;
        int num_hidden_layers = 2;
        int num_labels = 4;
        SimpleMatrix parameters  = new SimpleMatrix(new double[][]{
                new double[]{0.100, 0.200, 0.300, 0.400, 0.500, 0.600, 0.700,
                        0.800, 0.900, 1.000, 1.100, 1.200, 1.300, 1.400,
                        1.500, 1.600, 1.700, 1.800}
        });
        SimpleMatrix input_data = new SimpleMatrix(new double[][]{
                new double[]{0.5403023, -0.4161468},
                new double[]{-0.9899925, -0.6536436},
                new double[]{0.2836622, 0.9601703}
        });
        SimpleMatrix output_data = new SimpleMatrix(new double[][]{
                new double[]{4d},
                new double[]{2d},
                new double[]{3d}
        });
        int lambda = 4;

        // Create NeuralNetwork object
        NeuralNetwork nn = new NeuralNetwork();

        // Tolerance Variable
        double tolerance = 0.0000001;

        // Result Data
        SimpleMatrix expected_data_un_regularized = new SimpleMatrix(
                new double[][]{
                        new double[]{0.766138370},
                        new double[]{0.979896866},
                        new double[]{-0.027539616},
                        new double[]{-0.035844209},
                        new double[]{-0.024928783},
                        new double[]{-0.053861694},
                        new double[]{0.883417208},
                        new double[]{0.568762345},
                        new double[]{0.584667662},
                        new double[]{0.598139237},
                        new double[]{0.459313549},
                        new double[]{0.344618183},
                        new double[]{0.256313331},
                        new double[]{0.311885063},
                        new double[]{0.478336623},
                        new double[]{0.368920407},
                        new double[]{0.259770622},
                        new double[]{0.322330889}
                }
        );

        SimpleMatrix expected_data_regularized = new SimpleMatrix(
                new double[][]{
                        new double[]{0.766138370},
                        new double[]{0.979896866},
                        new double[]{0.372460384},
                        new double[]{0.497489124},
                        new double[]{0.641737884},
                        new double[]{0.746138306},
                        new double[]{0.883417208},
                        new double[]{0.568762345},
                        new double[]{0.584667662},
                        new double[]{0.598139237},
                        new double[]{1.925980216},
                        new double[]{1.944618183},
                        new double[]{1.989646665},
                        new double[]{2.178551730},
                        new double[]{2.478336623},
                        new double[]{2.502253740},
                        new double[]{2.526437289},
                        new double[]{2.722330889}
                }
        );

        // Testing without regularization
        EjmlUnitTests.assertEquals(expected_data_un_regularized.getDDRM(), nn.nn_gradient(
                parameters, input_data, output_data, num_inp_layers,
                num_hidden_layers, num_labels, 0).getDDRM(), tolerance);


        // Testing with regularization
        EjmlUnitTests.assertEquals(expected_data_regularized.getDDRM(), nn.nn_gradient(
                parameters, input_data, output_data, num_inp_layers,
                num_hidden_layers, num_labels, lambda).getDDRM(), tolerance);

        return;

    }
}