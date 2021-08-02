using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;
using MySql.Data.MySqlClient;

namespace RoguelikeCompanion
{
    class NeuralNetwork
    {
        /*
         */
        public static void newImagePrediction(Bitmap newImage)
        {

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
         */
        public static void normalizeInputData()
        {

        }
    }
}
