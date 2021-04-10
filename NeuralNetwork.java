import org.ejml.simple.SimpleMatrix;


public class NeuralNetwork {

    /*
    Name       : sigmoid
    Purpose    : Computes the sigmoid of z.
    Parameters : z, a SimpleMatrix.
    Return     : g, a SimpleMatrix denoting the computed sigmoid of z.
    Notes      : None.
     */
    public static SimpleMatrix sigmoid(SimpleMatrix z){
        SimpleMatrix g = z.scale(-1.0).elementExp().plus(1).elementPower(-1.0);
        return g;
    }


    public static void main(String[] agrs){
        double[][] firstMatrix = {
                new double[]{1d, 5d},
                new double[]{2d, 3d},
                new double[]{1d, 7d}
        };
        double [][] secondMatrix = {
            new double[]{-5d}
        };
        SimpleMatrix sim_firstMatrix = new SimpleMatrix(firstMatrix);
        SimpleMatrix sim_secondMatrix = new SimpleMatrix(secondMatrix);
        NeuralNetwork nn = new NeuralNetwork();
        SimpleMatrix test = nn.sigmoid(sim_secondMatrix);
        System.out.print(test);
    }

}
