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
            int x_offset = 567;
            int width = 425;
            int y_offset = 767;
            int height = 77;
         */
        public static Bitmap cropBitMap(Bitmap fullScreenCapture, int widthOffset, int newWidth, int heightOffset, int newHeight)
        {
            Rectangle cropRectangle = new Rectangle(widthOffset, heightOffset, newWidth + widthOffset, newHeight + heightOffset);
            Bitmap target = new Bitmap(cropRectangle.Width, cropRectangle.Height);

            using (Graphics g = Graphics.FromImage(target))
            {
                g.DrawImage(fullScreenCapture, new Rectangle(widthOffset, heightOffset, cropRectangle.Width, cropRectangle.Height), cropRectangle, GraphicsUnit.Pixel);
            }

            return target;
        }

    }
}
