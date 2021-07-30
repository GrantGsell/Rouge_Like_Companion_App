using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void capture_Click(object sender, EventArgs e)
        {
            /*
            int x_offset = 567;
            int width = 425;
            int y_offset = 767;
            int height = 77;
            */
            System.Threading.Thread.Sleep(4000);
            
            // Obtain bitmaps
            Bitmap initialImage = ScreenImgCapture.bitmapScreenCapture();
            Bitmap notificationBox = ScreenImgCapture.cropBitMap(initialImage, 567, 425, 767, 77);
            Bitmap borderNotificationBox = ScreenImgCapture.cropBitMap(notificationBox, 25, notificationBox.Width - 25, 0, notificationBox.Height);

            // Display images for simple "testing"
            pictureBoxFullImage.Image = initialImage;
            pictureBoxCrop.Image = notificationBox;
            pictureBoxBorders.Image = borderNotificationBox;
        }
    }
}
