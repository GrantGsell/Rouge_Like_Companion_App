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
    public partial class Form2 : Form
    {
        // Class members;
        PictureBox imagePB;
        WeaponObjectForm obj;

        public Form2(Image img, string name, string dps, string reloadTime, string sellPrice, string gunType)
        {
            InitializeComponent();
            this.imagePB = createPictureBox(img);
            this.obj = new WeaponObjectForm(name, dps, reloadTime, sellPrice, gunType);
        }

        private void Form2_Load(object sender, EventArgs e)
        {
            // Remove borders
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;

            // Create an image object in the top left corner
            imagePB.Location = new System.Drawing.Point(0, 0);

            // Put DataGridView below image
            int imageOffset = imagePB.Height;
            DataGridView data = obj.weaponDataGrid();
            data.Location = new System.Drawing.Point(0, imageOffset);

            // Add image and grid to form
            this.Controls.Add(imagePB);
            this.Controls.Add(data);

            // Set form size
            int width = data.Width;
            int height = imageOffset + data.Height;
            this.Size = new Size(width, height);

        }

        /*
         */
        public PictureBox createPictureBox(Image img)
        {
            PictureBox imageBox = new PictureBox();
            Bitmap bm = new Bitmap(img);
            bm = ScaleImage(bm, 100, 200);
            imageBox.Height = 100;
            imageBox.Width = 200;
            imageBox.Image = bm;

            return imageBox;
        }

        /*
         */
        public static Bitmap ScaleImage(Bitmap img, int height, int width)
        {
            if (img == null || height <= 0 || width <= 0)
            {
                return null;
            }
            int newWidth = (img.Width * height) / (img.Height);
            int newHeight = (img.Height * width) / (img.Width);
            int x = 0;
            int y = 0;

            Bitmap bmp = new Bitmap(width, height);
            Graphics g = Graphics.FromImage(bmp);
            g.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.HighQualityBilinear;

            if (newWidth > width)
            {
                // New height
                x = (bmp.Width - width) / 2;
                y = (bmp.Height - newHeight) / 2;
                g.DrawImage(img, x, y, width, newHeight);
            }
            else
            {
                // New width
                x = (bmp.Width / 2) - (newWidth / 2);
                y = (bmp.Height / 2) - (height / 2);
                g.DrawImage(img, x, y, newWidth, height);
            }

            return bmp;
        }

    }
}
