using System;
using Xunit;
using RoguelikeCompanion;
using MathNet.Numerics.LinearAlgebra;

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
    }
}
