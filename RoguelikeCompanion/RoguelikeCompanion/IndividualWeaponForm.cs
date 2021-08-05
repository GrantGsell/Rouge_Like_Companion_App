using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class IndividualWeaponForm : Form
    {
        // Class members;
        PictureBox imagePB;
        WeaponObjectForm weaponObj;
        PictureBox qualityPB;

        public IndividualWeaponForm(Image img, Image qualtiy, string name, string dps, string reloadTime, string sellPrice, string gunType)
        {
            InitializeComponent();
            this.imagePB = createPictureBox(img, 15);
            this.qualityPB = createPictureBox(qualtiy, 3);
            this.weaponObj = new WeaponObjectForm(name, dps, reloadTime, sellPrice, gunType);
        }


        private void Form2_Load(object sender, EventArgs e)
        {
            // Remove borders
            this.FormBorderStyle = FormBorderStyle.None;
            this.BackColor = Color.Aqua;

            // Create an image object in the top left corner
            imagePB.Location = new System.Drawing.Point(0, 0);

            // Put DataGridView below image
            int imageOffset = imagePB.Height;
            DataGridView data = weaponObj.weaponDataGrid();
            data.Location = new System.Drawing.Point(0, imageOffset);

            // Add the quality image below the DataGridView
            int qualityOffset = imagePB.Height + data.Height;
            qualityPB.Location = new System.Drawing.Point(0, qualityOffset);

            // Add image and grid to form
            this.Controls.Add(imagePB);
            this.Controls.Add(data);
            this.Controls.Add(qualityPB);

            // Set form size
            int width = data.Width;
            int height = imageOffset + data.Height + qualityPB.Height;
            this.Size = new Size(width, height);

            // Center Images
            int centerWidthImage = (width / 2) - (imagePB.Width / 2);
            int centerWidthQuality = (width / 2) - (qualityPB.Width / 2);
            imagePB.Location = new System.Drawing.Point(centerWidthImage, 0);
            qualityPB.Location = new System.Drawing.Point(centerWidthQuality, qualityOffset);
        }


        /*
         */
        public static PictureBox createPictureBox(Image img, int scaleFactor)
        {
            PictureBox imageBox = new PictureBox();
            Bitmap bm = new Bitmap(img);
            int newHeight = img.Height * scaleFactor;
            int newWidth = img.Width * scaleFactor;
            bm = ScaleImage(bm, newHeight, newWidth);
            imageBox.Height = bm.Height;
            imageBox.Width = bm.Width;
            imageBox.Image = bm;

            return imageBox;
        }


        /*
         */
        public static Bitmap ScaleImage(Bitmap bmp, int maxWidth, int maxHeight)
        {
            var ratioX = (double)maxWidth / bmp.Width;
            var ratioY = (double)maxHeight / bmp.Height;
            var ratio = Math.Min(ratioX, ratioY);

            var newWidth = (int)(bmp.Width * ratio);
            var newHeight = (int)(bmp.Height * ratio);

            var newImage = new Bitmap(newWidth, newHeight);

            using (var graphics = Graphics.FromImage(newImage))
                graphics.DrawImage(bmp, 0, 0, newWidth, newHeight);

            return newImage;
        }

    }
}
