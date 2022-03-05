using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace NeuralNetworkVisualization
{
    public partial class individualCharacterForm : Form
    {
        PictureBox characterImage = new PictureBox();
        TextBox characterGuess = new TextBox();

        /*
         * Constructor that creates the PictureBox and teatbox controls
         * for this form.
         * 
         * @param img, the image of the extracted character
         * @param synergizesWith, the guess for the character represente by the
         *   bitmap image.
         */
        public individualCharacterForm(Bitmap img, string character)
        {
            InitializeComponent();

            // Set image properties
            int scale = 5;
            int imgWidth = 18 * scale;
            int imgHeight = 15 * scale;
            characterImage.Image = scaleImage(img, imgWidth, imgHeight);
            characterImage.Size  = new Size(this.characterImage.Image.Width, this.characterImage.Image.Height);

            // Set TextBox properties
            this.characterGuess.Text = character;
            this.characterGuess.Font = new Font(characterGuess.Font.FontFamily, 16);
            this.characterGuess.TextAlign = HorizontalAlignment.Center;
            this.characterGuess.Location = new Point(this.characterImage.Image.Width / 2 - this.characterGuess.Width / 2, 0);

            // Set form properties
            this.Size = new Size(this.characterImage.Image.Width, 200);
        }


        /*
         * Loads this form with the character bitmap image and the prediction
         *   for that character.
         */
        private void individualCharacterForm_Load(object sender, EventArgs e) {  }


        /*
         * Transforms an image from its original size into the maximum size
         * that allows the image to fit in a give rectangle of maxWidth x 
         * maxHeight.
         * 
         * @param bmp, the original image.
         * @param maxWidth, the maximum width of the rectangle to scale the
         *      image to fit into.
         * @param maxHeight, the maximum height of the rectangle to scale the
         *      image to fit into.
         * @return newImage, the original image scaled to fit into a rectangle
         *      given by the input parameters.
         */
        public static Bitmap scaleImage(Bitmap bmp, int maxWidth, int maxHeight)
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
