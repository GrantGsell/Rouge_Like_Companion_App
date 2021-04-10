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
}