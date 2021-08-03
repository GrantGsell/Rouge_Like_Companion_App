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



        


    }
}
