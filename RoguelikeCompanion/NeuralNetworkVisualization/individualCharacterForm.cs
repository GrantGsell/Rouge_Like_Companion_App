using System;
using System.Drawing;
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

            // Resize the image
            img = resize(img);

            // Initialize text box
            initializeTextBox(img, character);

            // Initialize image
            initializeImage(img);

            // Set form properties
            this.Size = new Size(this.characterImage.Image.Width, 200);
        }


        /*
         * Initialize image control and its background.
         * 
         * @param img, is the image to be displayed.
         */
        public void initializeImage(Bitmap img)
        {
            // Set image properties
            characterImage.Image = img;
            characterImage.Size = new Size(characterImage.Image.Width, characterImage.Image.Height);
            characterImage.Location = new Point(0, characterGuess.Height + 15);

            // Set image background
            guessBackground.Location = new Point(0, characterGuess.Height + 5);
        }


        /*
         * Initialize the text box containing the guess for the given image.
         * 
         * @param guess, the string guess associated with the image.
         */
        public void initializeTextBox(Bitmap img, string guess)
        {
            // Set TextBox properties
            characterGuess.Text = guess;
            if (guess.Equals("SPACE"))
            {
                characterGuess.Width = img.Width;
                characterGuess.Text = "Space";
            }
            else if(guess.Length > 1 && guess.Substring(0, 2).Equals("ER"))
            {
                characterGuess.Width = img.Width;
                characterGuess.Text = "Error";
            }
            characterGuess.Font = new Font(characterGuess.Font.FontFamily, 16);
            characterGuess.TextAlign = HorizontalAlignment.Center;
            characterGuess.Location = new Point(img.Width / 2 - characterGuess.Width / 2, 0);
        }


        /*
         * Resizes an image by a factor of 5.
         * 
         * @param image, the image to be resized.
         * @return bitmap, the resized image.
         */
        public Bitmap resize(Bitmap image)
        {
            int scale = 5;
            int imgWidth = 18 * scale;
            int imgHeight = 15 * scale;
            return scaleImage(image, imgWidth, imgHeight);
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
