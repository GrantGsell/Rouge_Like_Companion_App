using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;
using MySql.Data.MySqlClient;
using MathNet.Numerics.LinearAlgebra;
using System.IO;

namespace RoguelikeCompanion
{
    class NeuralNetwork
    {
        // Fields
        int numFeatures = 810;
        double[] mean = new double[810];
        double[] std = new double[810];
        List<int> constantColumns = new List<int>();
        string[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "4", "5", "7", "8", "'", "-", "SPACE",
            "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP", "ERRAPT"};

        /*
         */
        public NeuralNetwork()
        {
            readMeanStdConstCols(numFeatures, mean, std, constantColumns);
        }


        /*
         */
        public void newImagePrediction(Bitmap newImage)
        {
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
                    Matrix<double> newCharMatrix = Matrix<double>.Build.Dense(1, this.numFeatures, pixelData);

                    // Normalize the new example data
                    this.normalizeInputData(newCharMatrix, this.mean, this.std, this.constantColumns);

                    // Make new prediction
                    double prediction = 2;
                    //double prediction = test_new_char(newCharMatrix);

                    // Translate prediction into associated character
                    int predict_idx = (int)prediction;
                    String char_prediction = characters[predict_idx];
                    stringPredictions[strPredArrIdx] = char_prediction;
                    strPredArrIdx += 1;
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }



        /*
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
            for (int i = 2; i < numFeatures + 2; i++)
            {
                mean[i - 2] = MyReader.GetDouble(i);
            }


            // Populate the standard deviation array
            query = "SELECT * FROM std";
            MyCommand = new MySqlCommand(query, MyConnection);
            MyReader = MyCommand.ExecuteReader();
            MyReader.Read();
            for (int i = 2; i < numFeatures + 2; i++)
            {
                std[i - 2] = MyReader.GetDouble(i);
            }


            // Obtain the number of constant column elements
            query = "SELECT COUNT(*) AS NUMBEROFCOLUMNS FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE table_schema = 'roguelike_companion' AND table_name = 'const_cols'";
            MyCommand = new MySqlCommand(query, MyConnection);
            MyReader = MyCommand.ExecuteReader();
            MyReader.Read();
            int numConstantCols = MyReader.GetInt32(0);


            // Populate the constant columns list
            query = "SELECT * FROM const_cols";
            MyCommand = new MySqlCommand(query, MyConnection);
            MyReader = MyCommand.ExecuteReader();
            MyReader.Read();
            for (int i = 2; i < numConstantCols + 1; i++)
            {
                int temp = MyReader.GetInt32(i);
                constantColumns.Add(temp);
            }


            // Close connection and reader
            MyReader.Close();
            MyConnection.Close();
        }


        /*
         * 
         */
        public void normalizeInputData(Matrix<double> inputMatrix, double[] mean, double[] std, List<int> constantColumns)
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
                    objectName.Append(namePrediction[i]);
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
         */
        public void determineCharacter(Matrix<double> newCharMatrix)
        {
            // Set input, hidden and output layer sizes
            int inputLayerSize = 810;
            int hiddenLayerSize = 100;
            int numLabels = this.characters.Length;

            // Set parameter matrix values and path
            string parameterFilePath = "C:/Users/Grant/Desktop/Java_Rouge_Like_App/src/parameters.csv";
            int theta_size = (hiddenLayerSize * (inputLayerSize + 1)) + (numLabels * (hiddenLayerSize + 1));
            Matrix<double> parameterMatrix = Matrix<double>.Build.Dense(1, theta_size);

            // Read in parameter values
            readParametersFromCSV(parameterMatrix, parameterFilePath);


            //return NeuralNetwork.new_prediction(parameterMatrix, newCharMatrix, inputLayerSize, hiddenLayerSize, numLabels);
        }


        /*
         */
        public static void readParametersFromCSV(Matrix<double> parameterMatrix, string parameterCSVFilePath)
        {
            using(var reader = new StreamReader(parameterCSVFilePath))
            {
                while(!reader.EndOfStream)
                {
                    var line = reader.ReadLine();
                    var values = line.Split(",");
                    for(int col = 0; col < parameterMatrix.ColumnCount; col++)
                    {
                        string strData = values[col];
                        double dblData = Convert.ToDouble(strData);
                        parameterMatrix.Add(dblData);
                    }
                }
            }
        }
    }
}
