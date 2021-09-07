using System;
using Xunit;
using RoguelikeCompanion;
using MathNet.Numerics.LinearAlgebra;
using System.Drawing;

namespace TestRogulikeCompanion
{
    public class TestNeuralNetwork
    {
        [Fact]
        public void TestSigmoid()
        {
            // Create arrays for input matrices
            var testInp0 = Matrix<double>.Build.DenseOfArray( new double[,] { { -5D } });
            var testInp1 = Matrix<double>.Build.DenseOfArray( new double[,] { { 0D } });
            var testInp2 = Matrix<double>.Build.DenseOfArray( new double[,] { { 5D } });
            var testInp3 = Matrix<double>.Build.DenseOfArray( new double[,] { { 4D, 5D, 6D } });
            var testInp4 = Matrix<double>.Build.DenseOfArray( new double[,] { { -1D }, { 0D }, { 1D } });
            var testArr5 = new double[4,5];
            double val = -1.0;
            for (int col = 0; col < testArr5.GetLength(1); col++)
            {
                for (int row = 0; row < testArr5.GetLength(0); row++)
                {
                    testArr5[row, col] = val;
                    val += 0.1;
                }
            }
            var testInp5 = Matrix<double>.Build.DenseOfArray(testArr5);

            // Create arrays for output matrices
            var testOut0 = Matrix<double>.Build.DenseOfArray( new double[,] { { 0.006692851 } });
            var testOut1 = Matrix<double>.Build.DenseOfArray( new double[,] { { 0.500000000 } });
            var testOut2 = Matrix<double>.Build.DenseOfArray( new double[,] { { 0.993307149 } });
            var testOut3 = Matrix<double>.Build.DenseOfArray( new double[,] { { 0.9820137900, 0.9933071490, 0.997527377 } });
            var testOut4 = Matrix<double>.Build.DenseOfArray( new double[,] { {0.268941421}, {0.500000000}, {0.731058579}});
            var testOut5 = Matrix<double>.Build.DenseOfArray(new double[,]{
                                                        {0.268941421, 0.354343694, 0.450166003, 0.549833997, 0.645656306},
                                                        {0.289050497, 0.377540669, 0.475020813, 0.574442517, 0.668187772},
                                                        {0.310025519, 0.401312340, 0.500000000, 0.598687660, 0.689974481},
                                                        {0.331812228, 0.425557483, 0.524979187, 0.622459331, 0.710949503} });

            // Create testing tuple array
            Tuple<Matrix<double>, Matrix<double>>[] testingTuples =
            {
                Tuple.Create(testInp0, testOut0),
                Tuple.Create(testInp1, testOut1),
                Tuple.Create(testInp2, testOut2),
                Tuple.Create(testInp3, testOut3),
                Tuple.Create(testInp4, testOut4),
                Tuple.Create(testInp5, testOut5),
            };

            // Perform assertions, element wise with precision
            foreach(Tuple<Matrix<double>, Matrix<double>> newTestingPair in testingTuples)
            {
                double[] experimental = NeuralNetwork.sigmoid(newTestingPair.Item1).ToRowMajorArray();
                double[] actual = newTestingPair.Item2.ToRowMajorArray();
                for (int idx = 0; idx < experimental.Length; idx++)
                {
                    Assert.Equal(experimental[idx], actual[idx], 9);
                }
            }
            
        }


        [Fact]
        public void TestTransformCharArrayToString()
        {
            // Testing inputs
            string[] inp0 = { "" };
            string[] inp1 = { "T", "e", "s", "t" };
            string[] inp2 = { "S", "h", "a", "d", "e", "s", "ERRAP", "s", "SPACE", "r", "e", "v", "o", "l", "v", "e", "r" };
            string[] inp3 = { "D", "a", "r", "t", "SPACE", "g", "ERRU", "ERRN" };
            string[] inp4 = { "H", "u", "ERRN", "ERRT", "s", "m", "a", "n" };

            // Testing outputs
            string out0 = "";
            string out1 = "Test";
            string out2 = "Shades's_Revolver";
            string out3 = "Dart_G";
            string out4 = "Husman";

            // Assertions
            Assert.Equal(NeuralNetwork.transformCharArrayToString(inp0), out0);
            Assert.Equal(NeuralNetwork.transformCharArrayToString(inp1), out1);
            Assert.Equal(NeuralNetwork.transformCharArrayToString(inp2), out2);
            Assert.Equal(NeuralNetwork.transformCharArrayToString(inp3), out3);
            Assert.Equal(NeuralNetwork.transformCharArrayToString(inp4), out4);
        }

        [Fact]
        public static void TestNewImagePrediction()
        {
            // Testing inputs
            Bitmap inp0 = (Bitmap)Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/class_2_border/class_2_0.jpg");
            Bitmap inp1 = (Bitmap)Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/class_2_border/class_2_2.jpg");
            Bitmap inp2 = (Bitmap)Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/class_2_border/class_2_3.jpg");
            Bitmap inp3 = (Bitmap)Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/class_2_border/class_2_5.jpg");
            Bitmap inp4 = (Bitmap)Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/class_2_border/class_2_9.jpg");
            Bitmap inp5 = (Bitmap)Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/class_2_border/class_2_7.jpg");


            // Testing outputs
            string out0 = "Bullet_Time";
            string out1 = "Backpack";
            string out2 = "Ice_Cube";
            string out3 = "Orange";
            string out4 = "Trank_Gun";           
            string out5 = "Cloranthy_Ring";

            // Create Testing Tuples
            Tuple<Bitmap, string>[] testingTuple =
            {
                Tuple.Create(inp0, out0),
                Tuple.Create(inp1, out1),
                Tuple.Create(inp2, out2),
                Tuple.Create(inp3, out3),
                Tuple.Create(inp4, out4),
                Tuple.Create(inp5, out5)
            };

            // Perform testing
            NeuralNetwork nn = new NeuralNetwork();
            foreach(var pair in testingTuple)
            {
                Assert.Equal(nn.newImagePrediction(pair.Item1), pair.Item2);
            }
        }
    }
}
