using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using MySql.Data.MySqlClient;
using MathNet.Numerics.LinearAlgebra;
using System.IO;

namespace RoguelikeCompanion
{
    public class NeuralNetwork
    {
        // Fields
        static int numFeatures = 810;
        static double[] mean = new double[810];
        static double[] std = new double[810];
        static List<int> constantColumns = new List<int>();
        static string[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "4", "5", "7", "8", "'", "-", "SPACE",
            "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP", "ERRAPT"};

        // Set input, hidden and output layer sizes
        static int inputLayerSize = 810;
        static int hiddenLayerSize = 100;

        // Theta matrices from parameters matrix for forward propagation
        static Matrix<double> theta1 = Matrix<double>.Build.Dense(hiddenLayerSize, inputLayerSize + 1);
        static Matrix<double> theta2 = Matrix<double>.Build.Dense(characters.Length, hiddenLayerSize + 1);


        /*
         * Neural network constructor. Creates mean, standard deviation,
         * constant columns and parameter vectors/matrices.
         */
        public NeuralNetwork()
        {
            readMeanStdConstCols(numFeatures, mean, std, constantColumns);
            populateParameterMatricies();
        }


        /*
         * Constructs a string prediction for an objects name that is displayed
         * in newImage.
         * 
         * @param newImage, an image with an objects name as text.
         * @return string, a prediction for the text within the given image.
         */
        public static string newImagePrediction(Bitmap newImage)
        {
            string objectName = null;
            try
            {
                // Sliding window dimensions
                int swHeight = 18;
                int swWidth = 15;

                // Perform character segmentation
                List<int> charSeparationIndicies;
                Bitmap isolatedTextImage;
                (charSeparationIndicies, isolatedTextImage) = CharacterSegmentation.characterSegmentation(newImage);

                // Set string array for prediction results
                string[] stringPredictions = new string[charSeparationIndicies.Count];

                // Make a prediction for each character
                int strPredArrIdx = 0;
                for (int curr = 0; curr < charSeparationIndicies.Count; curr++)
                {
                    // Obtain a sliding window box for one character
                    Bitmap currChar = ScreenImgCapture.cropBitMap(isolatedTextImage, charSeparationIndicies.ElementAt(curr), swWidth, 0, swHeight);

                    // Obtain sub-box pixel data
                    double[] pixelData = Array.ConvertAll<int, double>(BorderClass.getRGBData(currChar), x => x);

                    // Transform the data into a Simple Matrix object
                    Matrix<double> newCharMatrix = Matrix<double>.Build.Dense(1, numFeatures, pixelData);

                    // Normalize the new example data
                    normalizeInputData(newCharMatrix, mean, std, constantColumns);

                    // Make new prediction
                    double prediction = makeNNPrediction(newCharMatrix, inputLayerSize, hiddenLayerSize, characters.Length);

                    // Translate prediction into associated character
                    int predictIdx = (int)prediction;
                    String charPrediction = characters[predictIdx];
                    stringPredictions[strPredArrIdx] = charPrediction;
                    strPredArrIdx += 1;
                }

                // Translate string character array into a string
                objectName = transformCharArrayToString(stringPredictions);
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }

            // Check for empty string or ammo string
            if (objectName == "Ammo" || objectName == null) return null;
            return objectName;
        }


        /*
         * Read in the input array data for mean, standard deviation and
         * constant columns from the MySQL database. The mean and std arrays
         * have dimensions of 1 x numFeatures.
         * 
         * @param numFeatures, the number of features.
         * @param mean, the mean value for each feature.
         * @param std, the standard deviation for each feature.
         * @param constantColumns, the list of columns whose standard deviation
         *      is zero, to avoid a divide by zero error when performing 
         *      normalization.
         */
        public static void readMeanStdConstCols(int numFeatures, double[] mean, double[] std, List<int> constantColumns)
        {
            // Create connector and reader objects
            MySqlConnection MyConnection = null;
            MySqlDataReader MyReader = null;

            // Create the SQL connection
            MyConnection = new MySqlConnection(SQLInfo.getLogin());
            MyConnection.Open();

            // Create a query and command
            String query;
            MySqlCommand MyCommand;

            // Populate the mean array
            query = "SELECT * FROM mean";
            MyCommand = new MySqlCommand(query, MyConnection);
            MyReader = MyCommand.ExecuteReader();
            MyReader.Read();
            for (int i = 1; i < numFeatures + 1; i++)
            {
                mean[i - 1] = MyReader.GetDouble(i);
            }
            MyReader.Close();


            // Populate the standard deviation array
            query = "SELECT * FROM std";
            MyCommand = new MySqlCommand(query, MyConnection);
            MyReader = MyCommand.ExecuteReader();
            MyReader.Read();
            for (int i = 1; i < numFeatures + 1; i++)
            {
                std[i - 1] = MyReader.GetDouble(i);
            }
            MyReader.Close();


            // Obtain the number of constant column elements
            query = "SELECT COUNT(*) AS NUMBEROFCOLUMNS FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE table_schema = 'roguelike_companion' AND table_name = 'const_cols'";
            MyCommand = new MySqlCommand(query, MyConnection);
            MyReader = MyCommand.ExecuteReader();
            MyReader.Read();
            int numConstantCols = MyReader.GetInt32(0);
            MyReader.Close();


            // Populate the constant columns list
            query = "SELECT * FROM const_cols";
            MyCommand = new MySqlCommand(query, MyConnection);
            MyReader = MyCommand.ExecuteReader();
            MyReader.Read();
            for (int i = 1; i < numConstantCols; i++)
            {
                int temp = MyReader.GetInt32(i);
                constantColumns.Add(temp);
            }


            // Close connection and reader
            MyReader.Close();
            MyConnection.Close();
        }


        /*
         * Normalized the input matrix, column-wise with each column having a
         * zero mean unit variance.
         * 
         * @param inputMatrix, the input matrix to be normalized.
         * @param mean, an array containing the mean value for each column.
         * @param std, an array containing the standard deviation for each 
         *      column.
         * @param constantColumns, a list containing the columns whose values
         *      do not change from one example to the next.
         */
        public static void normalizeInputData(Matrix<double> inputMatrix, double[] mean, double[] std, List<int> constantColumns)
        {
            // Apply data normalization to input matrix
            for(int col = 0; col < inputMatrix.ColumnCount; col++)
            {
                if (!constantColumns.Contains(col))
                {
                    // Subtract mean
                    inputMatrix[0, col] = inputMatrix[0, col] - mean[col];

                    // Divide by standard deviation
                    inputMatrix[0, col] = inputMatrix[0, col] / std[col];
                }
            }

        }


        /*
         * Takes an array of strings whose elements are one of the strings
         * contained from the 'characters' array, and builds a string. Known 
         * errors add nothing to the string, SPACEs are turned into underscores
         * , the apostrophe error (ERRAP) and the appostrophe next to a 't' 
         * (ERRAPT) add an appostrophe.
         * 
         * @param namePrediction is an array of strings containing the
         *      character predictions.
         * @reutnr objectName the string created when combining the character
         *      predictions and removing/replacing errors.
         */
        public static string transformCharArrayToString(string[] namePrediction)
        {
            StringBuilder objectName = new StringBuilder();
            bool newWordFlag = false;

            for (int i = 0; i < namePrediction.Length; i++)
            {
                if (namePrediction[i].Equals("SPACE"))
                {
                    objectName.Append("_");
                    newWordFlag = true;
                }
                else if (namePrediction[i].Equals("ERRAP") || namePrediction[i].Equals("ERRAPT"))
                {
                    objectName.Append("'");
                }
                else if (namePrediction[i].Equals("ERRL") || namePrediction[i].Equals("ERRN") || namePrediction[i].Equals("ERRM") ||
                        namePrediction[i].Equals("ERRT") || namePrediction[i].Equals("ERRU"))
                {

                }
                else if (i == 0 || newWordFlag)
                {
                    objectName.Append(namePrediction[i].ToUpper());
                    newWordFlag = false;
                }
                else
                {
                    objectName.Append(namePrediction[i].ToLower());
                }
            }

            return objectName.ToString();
        }


        /*
         * Reads in the NN parameters from the given CSV file.
         * 
         * @param parameterMatrix, the matrix that will contain the parameters.
         * @param parameterCSVFilePath, the file path for the parameter CSV 
         *      file.
         */
        public static void readParametersFromCSV(Matrix<double> parameterMatrix, string parameterCSVFilePath)
        {
            using(var reader = new StreamReader(parameterCSVFilePath))
            {
                int matrixArr = 0;
                while(!reader.EndOfStream)
                {
                    var line = reader.ReadLine();
                    var values = line.Split(",");
                    for(int col = 0; col < parameterMatrix.ColumnCount; col++)
                    {
                        string strData = values[col];
                        double dblData = Convert.ToDouble(strData);
                        parameterMatrix[0, matrixArr++] = dblData;
                    }
                }
            }
        }


        /*
         * Runs the NN and makes a prediction for a single character.
         * 
         * @param newCharData, new character data that has been transformed
         *      from a bitmap image into a matrix.
         * @param inputLayersSize, the number of input layer nodes.
         * @param hiddenLayerSize, the number of hidden nodes.
         * @param numLabels, the number of output nodes (classes).
         * @return maxValIdx, an index value corresponding to the string in the
         *      'characters' array, that the newCharData most likely 
         *      represents.
         */
        public static double makeNNPrediction(Matrix<double> newCharData, int inputLayerSize, int hiddenLayerSize, int numLabels)
        {
            // Create bias unit matrix
            Matrix<double> biasUnits = Matrix<double>.Build.Dense(1, 1);
            biasUnits.Add(1);

            // Run NN forward propagation
            Matrix<double> input1 = (biasUnits.Append(newCharData)).Multiply(theta1.Transpose());
            Matrix<double> h1 = sigmoid(input1);
            Matrix<double> input2 = (biasUnits.Append(h1)).Multiply(theta2.Transpose());
            Matrix<double> h2 = sigmoid(input2);

            // Obtain output layer data in vector form
            double[] outputArr = h2.Row(0).AsArray();

            // Find the max value in the row and its corresponding index
            double maxVal = outputArr.Max();
            double maxValIdx = Array.IndexOf(outputArr, maxVal);

            return maxValIdx;
            
        }


        /*
         * Transforms input parameters from a vector format into a matrix
         * format.
         * 
         * @param inputMatrix where the parameter data will be written into.
         * @param inputVector a vector contianing all of the paramter data.
         * @param vectorIdx the starting point for reading the parameter data
         *      from the parameter vector.
         */
        public static void copyParameters(Matrix<double> inputMatrix, Matrix<double> inputVector, int vectorIdx)
        {
            // Copy vector values into matrix
            for(int col = 0; col < inputMatrix.ColumnCount; col++)
            {
                for(int row = 0; row < inputMatrix.RowCount; row++)
                {
                    inputMatrix[row, col] = inputVector[0, vectorIdx++];
                }
            }
        }


        /*
         * Takes the parameter data from a CSV file, given in a vector format,
         * and transforms it into two matrices.
         */
        public static void populateParameterMatricies()
        {
            // Set parameter matrix values and path
            string parameterFilePath = "C:/Users/Grant/Desktop/Java_Rouge_Like_App/src/parameters.csv";
            int theta_size = (hiddenLayerSize * (inputLayerSize + 1)) + (characters.Length * (hiddenLayerSize + 1));
            Matrix<double> parameterMatrix = Matrix<double>.Build.Dense(1, theta_size);

            // Read and transform parameter data
            readParametersFromCSV(parameterMatrix, parameterFilePath);
            copyParameters(theta1, parameterMatrix, 0);
            copyParameters(theta2, parameterMatrix, theta1.RowCount * theta1.ColumnCount);
        }

        /*
         * Computes the sigmoid of the given matrix
         * 
         * @param matrix
         * @return matrix, the computed sigmoid of the input parameter.
         */
        public static Matrix<double> sigmoid(Matrix<double> matrix)
        {
            matrix = matrix.Multiply(-1);
            matrix = (Matrix<double>.Exp(matrix)).Add(1).PointwisePower(-1.0);
            return matrix;
        }
    }
}
