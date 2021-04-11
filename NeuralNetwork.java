import org.ejml.simple.SimpleMatrix;

import java.beans.DesignMode;

public class NeuralNetwork {

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
                 parameters
                 input_data
                 output_data
                 input_layer_size
                 hidden_layer_size
                 num_labels
                 lambda
    Return     : cost a double denoting the cost the nn should pay for
                    obtaining incorrect values.
    Notes      :
     */
    public static double nn_cost_function(SimpleMatrix parameters,
                                        SimpleMatrix input_data,
                                        SimpleMatrix output_data,
                                        int input_layer_size,
                                        int hidden_layer_size,
                                        int num_labels, double lambda){

        // Num examples, num features
        int m = input_data.numRows();
        int n = input_data.numCols();

        // Reshape the nn parameters back into the weights for each layer.
        SimpleMatrix theta_1 = new SimpleMatrix(hidden_layer_size, input_layer_size+1);
        SimpleMatrix theta_2 = new SimpleMatrix(num_labels, hidden_layer_size + 1);
        theta_1 = copy_parameters(theta_1, parameters, 0);
        theta_2 = copy_parameters(theta_2, parameters, theta_1.getNumElements());

        /*
        Forward Propagation
         */
        // Expand the y output values into a maxtrix of single values
        SimpleMatrix eye_matrix = SimpleMatrix.identity(num_labels);
        SimpleMatrix y_matrix = new SimpleMatrix(m, num_labels);
        for(int i = 0; i < m; i++){
            int row_num = (int)output_data.get(i, 0);
            SimpleMatrix eye_row_data = eye_matrix.rows(row_num-1, row_num);
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
        SimpleMatrix a_3 = sigmoid(z_3);            // hypothesis == a_3 (for this specific netowrk)


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

    public static SimpleMatrix copy_parameters(SimpleMatrix inp_matrix, SimpleMatrix inp_vector, int vect_idx){
        for(int col = 0; col < inp_matrix.numCols(); col++){
            for(int row = 0; row < inp_matrix.numRows(); row++){
                inp_matrix.set(row, col, inp_vector.get(0, vect_idx));
                vect_idx += 1;
            }
        }

        return inp_matrix;
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static SimpleMatrix nn_gradient(SimpleMatrix parameters,
                                   SimpleMatrix input_data,
                                   SimpleMatrix output_data,
                                   int input_layer_size,
                                   int hidden_layer_size,
                                   int num_labels, double lambda){
        // Num examples, num features
        int m = input_data.numRows();
        int n = input_data.numCols();

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
            SimpleMatrix eye_row_data = eye_matrix.rows(row_num-1, row_num);
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

        // Gradient Regularization

        // Unroll gradients
        int total_theta_elements = theta_1_grad.getNumElements() + theta_2_grad.getNumElements();
        SimpleMatrix grad = new SimpleMatrix(total_theta_elements, 1);
        int unroll_index = 0;
        for(int row_1 = 0; row_1 < theta_1_grad.numRows(); row_1++){
            for(int col_1 = 0; col_1 < theta_1_grad.numCols(); col_1++){
                double temp_val = theta_1_grad.get(row_1, col_1);
                grad.set(unroll_index, 0, temp_val);
                unroll_index += 1;
            }
        }
        for(int row_2 = 0; row_2 < theta_2_grad.numRows(); row_2++){
            for(int col_2 = 0; col_2 < theta_2_grad.numCols(); col_2++){
                double temp_val = theta_2_grad.get(row_2, col_2);
                grad.set(unroll_index, 0, temp_val);
                unroll_index += 1;
            }
        }

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
        SimpleMatrix test_a = sigmoid(z);
        SimpleMatrix test_b = sigmoid(z).negative().plus(1.0);
        return sigmoid(z).elementMult(sigmoid(z).negative().plus(1.0));
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String[] agrs){
        double[][] nn_params =new double[][] {
                new double[]{1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d},
                new double[]{1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d},
                new double[]{1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d},
                new double[]{1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d},
                new double[]{1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d}
        };
        SimpleMatrix nn_param_mat = new SimpleMatrix(nn_params);
        NeuralNetwork nn = new NeuralNetwork();
        double[][] test = new double[][]{
                new double[]{0},
                new double[]{0},
                new double[]{2},
                new double[]{2},
                new double[]{4}
        };
        SimpleMatrix y = new SimpleMatrix(test);
        nn.nn_cost_function(nn_param_mat, nn_param_mat, y, 3, 2, 5, 0.1);
    }

}
