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
        public static Bitmap bitmapScreenCapture()
        {
            int width = Screen.PrimaryScreen.Bounds.Width;
            int height = Screen.PrimaryScreen.Bounds.Height;
            width = 1536;
            height = 864;
            Bitmap bitMap = new Bitmap(Screen.PrimaryScreen.Bounds.Width, Screen.PrimaryScreen.Bounds.Height);
            try
            {
                // Obtain screen bitmap
                Graphics g = Graphics.FromImage(bitMap);
                g.CopyFromScreen(0, 0, 0, 0, bitMap.Size);

                // Resize the bitmap image
                bitMap = new Bitmap(bitMap, new Size(width, height));

                // Set the picture box image
                return bitMap;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
            return bitMap;
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
