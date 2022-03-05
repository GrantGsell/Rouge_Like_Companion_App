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
        PictureBox characterImage;
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
            this.characterGuess.Text = character;
            this.characterImage = createPictureBox(img, 75, 75);

            // Remove borders, set double buffer
            this.FormBorderStyle = FormBorderStyle.None;
        }


        /*
         * Loads this form with the character bitmap image and the prediction
         *   for that character.
         */
        private void individualCharacterForm_Load(object sender, EventArgs e)
        {
            // Create an image object in the top left corner
            characterImage.Location = new Point(0, 75 - characterImage.Height);

            // Place thee TextBox below the image
            int imageHeightOffset = characterImage.Height;
            characterGuess.Location = new Point(0, (75 - characterImage.Height) + imageHeightOffset);

            // Set form size
            int height = characterGuess.Height + 75;
            int width = (characterGuess.Width > characterImage.Width) ? characterGuess.Width : characterImage.Width;
            this.Size = new Size(width, height);

            // Center image
            int updatedImageWidth = Math.Abs(characterGuess.Width / 2 - characterImage.Width / 2);
            characterImage.Location = new Point(updatedImageWidth, 75 - characterImage.Height);

            // Add image and TextBox
            this.Controls.Add(characterImage);
            this.Controls.Add(characterGuess);
        }


        /*
         * Creates a new PictureBox control, scales the given image and then
         * places it into the new control. The control is sized to fit the
         * scaled image size and not the rectangle specified by newWidth,
         * newHeight.
         * 
         * @param img, the image of the object.
         * @param newWidth, the maximum width of the rectangle to scale the 
         *      image into.
         * @param newHeight, the maximum height of the rectangle to scale the
         *      image into.
         * @return imageBox, a PictureBox control containing the scaled image.
         */
        public static PictureBox createPictureBox(Bitmap bm, int newWidth, int newHeight)
        {
            PictureBox imageBox = new PictureBox();

            // Scale bit map
            bm = ScaleImage(bm, newHeight, newWidth);

            // Set the picture box size to fit the scaled image
            imageBox.Height = bm.Height;
            imageBox.Width = bm.Width;
            imageBox.Image = bm;

            return imageBox;
        }


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
