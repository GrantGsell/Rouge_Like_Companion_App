using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Drawing;

namespace RoguelikeCompanion
{
    class CharacterSegmentation
    {


        /*
         */
        public static void characterSegmentation(Bitmap image)
        {
            // Sliding window dimensions and step size
            int swHeight = 18, swWidth = 15, swDelta = 5;

            // Isolate text row from notificaiton box
            image = ScreenImgCapture.cropBitMap(image, 0, image.Width, 16, image.Height);

            // Isolate the text
            image = isolateText(image, swHeight, swWidth, swDelta);

            // Perform image pre-processing
            preprocessBackground(image);

            // Obtain character segmentation indices list
            getCharacterSeparationIndices(image, swHeight);

        }


        /*
         */
        public static Bitmap isolateText(Bitmap image, int swHeight, int swWidth, int swDelta)
        {
            // Text box column variables
            int num_boxes = (image.Width - swWidth) / swDelta;      // Potential for error
            int textBoxStart = -1;
            int textBoxEnd = -1;
            int widthOffset = 0;
            int heightOffset = 0;

            // Slide the sliding window box along the image width
            for (int curr_idx = 0; curr_idx < num_boxes; curr_idx++)
            {
                // Obtain the current sliding window box
                Bitmap curr_box = ScreenImgCapture.cropBitMap(image, widthOffset, swWidth, heightOffset, swHeight);

                // Determine if the sliding window contains a character
                int currBoxClass = 0;
                if (numBWPixels(curr_box, 180, 68, true))
                {
                    currBoxClass = 1;
                }

                // Set the text box end/start indices
                if (currBoxClass == 1 && textBoxStart < 0)
                {
                    textBoxStart = widthOffset - 5;
                }
                else if (currBoxClass == 1 && textBoxStart > 0)
                {
                    textBoxEnd = widthOffset + swWidth;
                }

                // Iterate x_offset to move the sliding window horizontally
                widthOffset += swDelta;
            }

            // Check to see if a text box was found, if so return the cropped text box image
            if (textBoxStart != textBoxEnd)
            {
                int textBoxSize = textBoxEnd - textBoxStart + 3;
                return ScreenImgCapture.cropBitMap(image, textBoxStart, textBoxSize, heightOffset, swHeight);
            }
            else
            {
                return null;
            }

        }


        /*
         */
        public static bool numBWPixels(Bitmap image, int threshold, int numBWPixels, bool countWhite)
        {
            // White pixel count data
            int bwPixelCount = 0;

            // Obtain rgb pixel data
            int[] pixelArr = BorderClass.getRGBData(image);

            // Iterate over pixel_array, looking at 3 pixel for black/white data.
            for (int idx = 0; idx < pixelArr.Length; idx += 3)
            {
                if (countWhite && pixelArr[idx] > threshold && pixelArr[idx + 1] > threshold && pixelArr[idx + 2] > threshold)
                {
                    bwPixelCount += 1;
                }
                else if (!countWhite && pixelArr[idx] < threshold && pixelArr[idx + 1] < threshold && pixelArr[idx + 2] < threshold)
                {
                    bwPixelCount += 1;
                }
            }
            // Return boolean, 1 for valid box, 0 for invalid
            return bwPixelCount >= numBWPixels;
        }



        /*
         */
        public static void preprocessBackground(Bitmap image)
        {
            try
            {
                int threshold = 140;
                for (int row = 0; row < image.Width; row++)
                {
                    for (int col = 0; col < image.Height; col++)
                    {
                        Color c = image.GetPixel(row, col);
                        int red = c.R;
                        int green = c.G;
                        int blue = c.B;
                        if (red <= threshold && green <= threshold && blue <= threshold)
                        {
                            image.SetPixel(row, col, Color.Black);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }



        /*
         * 
         */
        public static List<int> getCharacterSeparationIndices(Bitmap image, int swHeight)
        {
            int[] spacingIndices = new int[image.Width];
            int arrIdx = 0;

            // Search image for black columns, if found search for black column 14 spaces away
            int xOffset = 0;

            // Use the sliding window to assign potential column indices to the spacing indices array
            while (xOffset + 14 < image.Width)
            {
                // Obtain sliding window edge slices
                Bitmap left_column = ScreenImgCapture.cropBitMap(image, xOffset, 1, 0, swHeight);
                Bitmap right_column = ScreenImgCapture.cropBitMap(image, xOffset + 14, 1, 0, swHeight);

                if (numBWPixels(left_column, 100, (swHeight - 3), false)
                        && numBWPixels(right_column, 100, (swHeight - 2), false))
                {
                    spacingIndices[arrIdx] = xOffset;
                    arrIdx += 1;
                }
                xOffset += 1;
            }

            // Process array into new usable array list and remove duplicates
            List<int> charSplits = new List<int>();
            int idx = 0;
            while (spacingIndices[idx] != 0 || spacingIndices[idx + 1] != 0)
            {
                if (spacingIndices[idx + 5] - spacingIndices[idx] == 5)
                {
                    charSplits.Add(spacingIndices[idx + 3]);
                    idx += 6;
                }
                else if (spacingIndices[idx + 4] - spacingIndices[idx] == 4)
                {
                    charSplits.Add(spacingIndices[idx + 3]);
                    idx += 5;
                }
                else if (spacingIndices[idx + 3] - spacingIndices[idx] == 3)
                {
                    charSplits.Add(spacingIndices[idx + 2]);
                    idx += 4;
                }
                else if (spacingIndices[idx + 2] - spacingIndices[idx] == 2)
                {
                    charSplits.Add(spacingIndices[idx + 1]);
                    idx += 3;
                }
                else if (spacingIndices[idx + 1] - spacingIndices[idx] == 1)
                {
                    charSplits.Add(spacingIndices[idx]);
                    idx += 2;
                }
                else
                {
                    charSplits.Add(spacingIndices[idx]);
                    idx += 1;
                }
            }
            return charSplits;

        }


    }
}
