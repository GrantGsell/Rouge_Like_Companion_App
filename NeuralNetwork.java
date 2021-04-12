import org.ejml.simple.SimpleMatrix;
import java.util.Random;
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

        // Gradient Regularization Term calculation
        SimpleMatrix reg_term_theta_1 = theta_1.extractMatrix(0, theta_1.numRows(), 1, theta_1.numCols()).scale(lambda/m);
        SimpleMatrix reg_term_theta_2 = theta_2.extractMatrix(0, theta_2.numRows(), 1, theta_2.numCols()).scale(lambda/m);

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
    Purpose    : To randomly initialize the weights of a layer with l_in
                    incoming connections, and l_out outgoing connections.
    Parameters :
                 l_in, an integer denoting the number of incoming connections.
                 l_out, an integer denoting the number of outgoing connections.
    Return     : rand_weights a simple matrix of dimensions (l_out x l_in + 1)
    Notes      :
     */
    public static SimpleMatrix random_initialize_weights(int l_in, int l_out){
        SimpleMatrix rand_weights = SimpleMatrix.random_DDRM(l_out, l_in + 1, 0, 1,  new Random());
        double epsilon = Math.sqrt(6) / Math.sqrt(l_in + l_out);
        rand_weights.scale(2).scale(epsilon).minus(epsilon);
        return rand_weights;
    }


    /*
    Name       : debug_initialize_weights
    Purpose    : To initialize the weights of a layer with l_in incoming
                    connections, and l_out outgoing connections using a fixed
                    strategy (the same weight values each time).
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
    Purpose    : To perform numerical gradient checking
    Parameters :
                 parameters
                 input_data
                 output_data
                 input_layer_size
                 hidden_layer_size
                 num_labels
                 lambda
    Return     :
    Notes      :
     */
    public static SimpleMatrix compute_numerical_gradient(SimpleMatrix theta,
                                                          SimpleMatrix input_data,
                                                          SimpleMatrix output_data,
                                                          int input_layer_size,
                                                          int hidden_layer_size,
                                                          int num_labels,
                                                          double lambda){
        SimpleMatrix numerical_gradient = new SimpleMatrix(theta.numRows(), theta.numCols());
        SimpleMatrix perturb = new SimpleMatrix(theta.numRows(), theta.numCols());
        double epsilon = 1e-4;
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
    Purpose    : To create a small neural network to check the backpropagation
                    gradients. This occurs by obtaining the analytical
                    gradients produced by the backpropagation code, and
                    obtaining the numerical gradients from the numerical
                    gradient code. The values for both are compared
                    respectively to one another and if backpropagation was
                    implemented correctly the two gradients should be near
                    identical.
    Parameters :
    Return     :
    Notes      :
     */
    public static void check_nn_gradients(double lambda){
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
                        new double[]{2d},
                        new double[]{3d},
                        new double[]{1d},
                        new double[]{2d},
                        new double[]{3d}
                }
        );

        // Unroll parameters
        SimpleMatrix nn_parameters = unroll_matrices(theta_1, theta_2);

        // Compute analytical gradients
        SimpleMatrix analytical_grad = nn_gradient(nn_parameters, input_data,
                output_data, input_layer_size, hidden_layer_size, num_labels,
                lambda);

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
            System.out.format(format_data, analytical_grad.get(i, 0), numerical_grad.get(i, 0), difference);
        }
    }


    /*
    Name       : unroll_matrices
    Purpose    : To unroll two matrices into one matrix with n rows and one
                    column where n denotes the total number of elements in both
                    matrices.
    Parameters :
                 matrix_1, a simple matrix.
                 matrix_2, a simple matrix.
    Return     : unrolled_matrices, a simple matrix denoting the unrolled matrix
                    containing all elements of the two matrices.
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
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void learn_parameters(){

    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void new_prediction(){

    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String[] args){
        NeuralNetwork.check_nn_gradients(3);
    }

}
