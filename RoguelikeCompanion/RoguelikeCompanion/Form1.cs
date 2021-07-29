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
            pictureBox.Image = ScreenImgCapture.CaptureScreen();
            //Bitmap initialImage = ScreenImgCapture.CaptureScreen();
            //pictureBox.Image = ScreenImgCapture.IsolateNotificationBoxArea(initialImage, 567, 425, 767, 77);
        }
    }
}
