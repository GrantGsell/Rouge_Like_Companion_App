using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;

namespace RoguelikeCompanion
{
    class ScreenImgCapture
    {

        /*
         * Obtain a full-screen image and resize it to 1536 x 864
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
                Bitmap resized = new Bitmap(bitMap, new Size(width, height));

                // Set the picture box image
                return resized;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
            return bitMap;
        }


        /*
         * Crop a bitmap
         */
        public static Bitmap cropBitMap(Bitmap fullScreenCapture, int widthOffset, int newWidth, int heightOffset, int newHeight)
        {
            // Set the crop location, and the crop bitmap
            Rectangle cropRectangle = new Rectangle(widthOffset, heightOffset, newWidth, newHeight);
            Bitmap target = new Bitmap(cropRectangle.Width, cropRectangle.Height);

            // Isolate and draw the croped portion of the image
            using (Graphics g = Graphics.FromImage(target))
            {
                g.DrawImage(fullScreenCapture, new Rectangle(0, 0, cropRectangle.Width, cropRectangle.Height), cropRectangle, GraphicsUnit.Pixel);
            }

            return target;
        }
    }
}
