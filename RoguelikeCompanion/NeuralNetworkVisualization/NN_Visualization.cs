using RoguelikeCompanion;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Windows.Forms;

namespace NeuralNetworkVisualization
{
    public partial class NN_Visualization : Form
    {
        // Class Fields
        NeuralNetwork nn = new NeuralNetwork();
        string textBoxPathName = @"C:\Users\Grant\Desktop\Java_Rouge_Like_App\screenshots\temp_1.jpg";
        List<Bitmap> slidingWindowImages = new List<Bitmap>();
        List<bool> isCharacterList = new List<bool>();
        int currSWIndex = 0;
        Bitmap isolatedTextImage;
        Timer slideShowTimer = new Timer();

        /*
         * NN_Visualization constructor.
         */
        public NN_Visualization()
        {
            InitializeComponent();

            // Read in new image 
            Bitmap newImage = convertToBitmap(textBoxPathName);

            string guess = nn.newImagePrediction(newImage);

            // Obtain all sliding window images
            (slidingWindowImages, isCharacterList) = imageIsolation(newImage);

            // Image Slideshow via timer
            slideShowTimer.Interval = (100);
            slideShowTimer.Tick += new EventHandler(slideShow_Tick);
            slideShowTimer.Start();
        }


        /*
         * Popuplates the 'extractedTextBox' image property.
         * 
         * @param textBoxImage, the image used to populate the image property.
         * @return none.
         */
        public void showExtractedTextBox(Bitmap textBoxImage)
        {
            // Set image and fit to size
            extractedTextBox.Image = textBoxImage;
            extractedTextBox.SizeMode = PictureBoxSizeMode.StretchImage;
        }


        /*
         * Popuplates the 'currentSlidingWindowSlice' image property.
         * 
         * @param textBoxImage, the image used to populate the image property.
         * @return none.
         */
        public void showCurrentSlidingWindow(Bitmap slidingWindow)
        {
            // Set image and fit to size
            currentSlidingWindowSlice.Image = slidingWindow;
            currentSlidingWindowSlice.SizeMode = PictureBoxSizeMode.StretchImage;
        }


        /*
         * Converts a png, jpg image to a bitmap.
         * 
         * @param fileName, the absolute file path of the picture location.
         * @return a Bitmap, representing the converted image.
         */
        public Bitmap convertToBitmap(string fileName)
        {
            Bitmap bitmap;
            using (Stream bmpStream = File.Open(fileName, FileMode.Open))
            {
                Image image = Image.FromStream(bmpStream);
                bitmap = new Bitmap(image);
            }
            return bitmap;
        }


        /*
         * Obtains the sliding window bitmap slices and marks them as either a box to be
         *   process or not.
         *
         * @params newImage, a bitmap denoting the image to be processed.
         * @return two lists, one containing the sliding window slices, and one
         *   containing if the respective bitmap is a character to be processed.
         */
        public (List<Bitmap>, List<bool>) imageIsolation(Bitmap newImage)
        {
            // Sliding window dimensions and step size
            int swHeight = 18, swWidth = 15, swDelta = 5;

            // Obtain indicies for character separation
            List<int> charSeparationIndicies;
            (charSeparationIndicies, isolatedTextImage) = CharacterSegmentation.characterSegmentation(newImage);
            showExtractedTextBox(isolatedTextImage);

            // Create List of Sliding Window Images
            List<Bitmap> slidingWindowImages = new List<Bitmap>();
            List<bool> isCharacterList = new List<bool>();
            for(int i = 0; i < isolatedTextImage.Width - swWidth + swDelta; i++)
            {
                Bitmap newSlidingWindowBox = ScreenImgCapture.cropBitMap(isolatedTextImage, i, swWidth, 0, swHeight);
                slidingWindowImages.Add(newSlidingWindowBox);
                if (charSeparationIndicies.Contains(i))
                    isCharacterList.Add(true);
                else
                    isCharacterList.Add(false);                             
            }

            return (slidingWindowImages, isCharacterList);
        }


        /*
         * Iterates, and shows the next sliding window slice in the slidingWindowImage
         *   list.
         * @param slidingWindowImages, a list of all of the sliding window slices.
         * @param foundLetters, a list denoting if the respective sliding window slice is
         *   a character that should be processed.
         * @param image, the original image from which all of the sliding window slices 
         *   were obtained.
         * @return none.
         */
        public void imageSlideShow(List<Bitmap> slidingWindowImages, List<bool> foundLetters, Bitmap image)
        {
            // Sliding window dimensions and step size
            int swHeight = 18, swWidth = 15, swDelta = 5;

            // Extract the next sliding window slice and display it
            Bitmap currSW = slidingWindowImages[currSWIndex];
            showCurrentSlidingWindow(currSW);

            // Change sliding window background color
            if (foundLetters[currSWIndex])
            {
                slidingWindowBackground.BackColor = Color.Green;                
                string guess = NeuralNetwork.makeNNPrediction(currSW);
                addFoundCharacterToForm(currSW, guess);
                int a = 5;
            }
            else
            {
                slidingWindowBackground.BackColor = Color.Red;
            }

            currSWIndex++;
            if (currSWIndex > image.Width - swWidth)
                slideShowTimer.Stop();
            
        }


        /*
         * Method to be run when the timer is raised. Calls the imageSlideShow method.
         */
        public void slideShow_Tick(object sender, EventArgs e)
        {
            imageSlideShow(slidingWindowImages, isCharacterList, isolatedTextImage);
        }


        /*
         */
        public void addFoundCharacterToForm(Bitmap image, string guess)
        {
            individualCharacterForm newChar = new individualCharacterForm(image, guess);
            extractedCharFLP.Controls.Add(newChar);
        }

    }
}
