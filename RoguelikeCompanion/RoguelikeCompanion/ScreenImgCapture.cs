using System;
using System.Windows.Forms;
using System.Drawing;

namespace RoguelikeCompanion
{
    class ScreenImgCapture
    {

        /*
         * Obtain a full-screen image and resize it to 1536 x 864. The resize
         * is due to cropping of the area where the text box would appear.
         * 
         * @return bitMap, the resized screencapture.
         */
        public static Bitmap bitmapScreenCapture(int width, int height)
        {           
            Bitmap bitMap = new Bitmap(width, height);
            if(bitMap.Width == 1536 && bitMap.Height == 864)
            {
                return bitMap;
            }
            try
            {
                // Obtain screen bitmap
                Graphics g = Graphics.FromImage(bitMap);
                g.CopyFromScreen(0, 0, 0, 0, bitMap.Size);

                // Resize the bitmap image
                int resizeWidth = 1536;
                int resizeHeight = 864;
                Bitmap resize = new Bitmap(bitMap, new Size(resizeWidth, resizeHeight));

                // Set the picture box image
                return resize;
            }
            catch (ArgumentException ex)
            {
                Console.WriteLine(ex.ToString());
                return bitMap;
            }
            catch (System.ComponentModel.Win32Exception w)
            {
                return bitMap;
            }
        }


        /*
         * Crops a bitmap to isolate a certain arrea
         * 
         * @param fullScreenCapture, the imaged to be cropped.
         * @param widthOffset, starting X point on the original image.
         * @param heightOffset, starting Y point on the original image.
         * @param newWidth, the width of the crop box.
         * @param newHeight, the height of the crop box.
         * @return croppedBox, the cropped image from the specified location of
         * the original image.
         */
        public static Bitmap cropBitMap(Bitmap fullScreenCapture, int widthOffset, int newWidth, int heightOffset, int newHeight)
        {
            // Set the crop location, and the crop bitmap
            Rectangle cropRectangle = new Rectangle(widthOffset, heightOffset, newWidth, newHeight);
            Bitmap croppedBox = new Bitmap(cropRectangle.Width, cropRectangle.Height);

            // Isolate and draw the croped portion of the image
            using (Graphics g = Graphics.FromImage(croppedBox))
            {
                g.DrawImage(fullScreenCapture, new Rectangle(0, 0, cropRectangle.Width, cropRectangle.Height), cropRectangle, GraphicsUnit.Pixel);
            }

            return croppedBox;
        }
    }
}
