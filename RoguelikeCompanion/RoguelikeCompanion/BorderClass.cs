using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Drawing;

namespace RoguelikeCompanion
{
    class BorderClass
    {

        /*
         */
        public static void readBorderData()
        {

        }


        /*
         */
        public static int predictIsBorder(Bitmap notificationBox, double[][] borderData)
        {
            // Number of border features and classes
            int numClasses = borderData.Length;
            int numFeatures = borderData[0].Length;
            int borderPixelHeight = 3;

            // Create an array to hold top, bottom border data
            int[] combinedBorderData = new int[numFeatures];

            // Crop out the top and bottom borders
            Bitmap topBorder = ScreenImgCapture.cropBitMap(notificationBox, 0, notificationBox.Width, 0, borderPixelHeight);
            Bitmap bottomBorder = ScreenImgCapture.cropBitMap(notificationBox, 0, notificationBox.Width, 74, borderPixelHeight);

            // Transform bitmap data into array data
            int[] topBorderArr = getRGBData(topBorder);
            int[] bottomBorderArr = getRGBData(bottomBorder);

            // Copy RGB data into the combined array
            Array.Copy(topBorderArr, 0, combinedBorderData, 0, topBorderArr.Length);
            Array.Copy(bottomBorderArr, 0, combinedBorderData, topBorderArr.Length + 1, bottomBorderArr.Length);

            // Find the difference between each of the classes
            double[] numDiffPixels = new double[numClasses];
            for (int col = 0; col < numFeatures; col++)
            {
                if (Math.Abs((double)combinedBorderData[col] - borderData[0][col]) > 20)
                {
                    numDiffPixels[0] += 1.0;
                }
                if (Math.Abs((double)combinedBorderData[col] - borderData[1][col]) > 50)
                {
                    numDiffPixels[1] += 1.0;
                }
                if (Math.Abs((double)combinedBorderData[col] - borderData[2][col]) > 35)
                {
                    numDiffPixels[2] += 1.0;
                }
                if (Math.Abs((double)combinedBorderData[col] - borderData[3][col]) > 35)
                {
                    numDiffPixels[3] += 1.0;
                }
            }

            // Set smallest variables
            double smallestValue = 2e9;
            int smallestValIdx = -1;

            // Average each of the class differences
            for (int i = 0; i < numClasses; i++)
            {
                if (numDiffPixels[i] < smallestValue)
                {
                    smallestValue = numDiffPixels[i];
                    smallestValIdx = i + 1;
                }
            }
            if (smallestValue > 100)
            {
                return 0;
            }
            else
            {
                return smallestValIdx;
            }
        }


        /*
         */
        public static int[] getRGBData(Bitmap image)
        {
            // Obtain width and height
            int width = image.Width;
            int height = image.Height;

            // Initialize pixel array
            int[] imageRGBArray = new int[width * height * 3];
            int arrIndex = 0;

            // Iterate through image pixels and extract RGB data
            for(int row = 0; row < width; row++)
            {
                for(int col = 0; col < height; col++)
                {
                    Color c = image.GetPixel(row, col);
                    imageRGBArray[arrIndex++] = c.R;
                    imageRGBArray[arrIndex++] = c.G;
                    imageRGBArray[arrIndex++] = c.B;
                    //int r = c.R;
                    //int g = c.G;
                    //int b = c.B;
                }
            }

            return imageRGBArray;
        }

    }
}
