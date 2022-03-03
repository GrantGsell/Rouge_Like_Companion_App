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
        // Fields
        NeuralNetwork nn = new NeuralNetwork();
        string textBoxPathName = @"C:\Users\Grant\Desktop\Java_Rouge_Like_App\screenshots\temp_1.jpg";
        List<Bitmap> slidingWindowImages = new List<Bitmap>();
        List<bool> isCharacterList = new List<bool>();
        int currSWIndex = 0;
        Bitmap isolatedTextImage;
        Timer slideShowTimer = new Timer();

        /*
         */
        public NN_Visualization()
        {
            InitializeComponent();
            Bitmap newImage = convertToBitmap(textBoxPathName);
            showExtractedTextBox(newImage);
            showCurrentSlidingWindow(newImage);
            string guess = nn.newImagePrediction(newImage);
            //imageIsolation(newImage);

            // Obtain all sliding window images
            (slidingWindowImages, isCharacterList) = imageIsolation(newImage);

            // Image Slideshow via timer
            slideShowTimer.Interval = (2000);
            slideShowTimer.Tick += new EventHandler(slideShow_Tick);
            slideShowTimer.Start();
        }


        /*
         */
        public void showExtractedTextBox(Bitmap textBoxImage)
        {
            // Set image and fit to size
            extractedTextBox.Image = textBoxImage;
            extractedTextBox.SizeMode = PictureBoxSizeMode.StretchImage;
        }


        /*
         */
        public void showCurrentSlidingWindow(Bitmap slidingWindow)
        {
            // Set image and fit to size
            currentSlidingWindowSlice.Image = slidingWindow;
            currentSlidingWindowSlice.SizeMode = PictureBoxSizeMode.StretchImage;
        }


        /*
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
         */
        public (List<Bitmap>, List<bool>) imageIsolation(Bitmap newImage)
        {
            // Timer
            System.Windows.Forms.Timer myTimer = new System.Windows.Forms.Timer();
            int delayTime = 2000;
            myTimer.Interval = delayTime;

            // Sliding window dimensions and step size
            int swHeight = 18, swWidth = 15, swDelta = 5;

            // Obtain indicies for character separation
            List<int> charSeparationIndicies;
            
            (charSeparationIndicies, isolatedTextImage) = CharacterSegmentation.characterSegmentation(newImage);

            // Create List of Sliding Window Images
            List<Bitmap> slidingWindowImages = new List<Bitmap>();
            List<bool> isCharacterList = new List<bool>();
            for(int i = 0; i < isolatedTextImage.Width - swWidth + swDelta; i += swDelta)
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
         */
        public void imageSlideShow(List<Bitmap> slidingWindowImages, List<bool> foundLetters, Bitmap image)
        {
            // Sliding window dimensions and step size
            int swHeight = 18, swWidth = 15, swDelta = 5;

            showExtractedTextBox(image);
            Bitmap test = ScreenImgCapture.cropBitMap(image, currSWIndex, swWidth, 0, swHeight);
            showCurrentSlidingWindow(test);
            currSWIndex++;
            if (currSWIndex > 8)
                slideShowTimer.Stop();
        }


        /*
         */
        public void slideShow_Tick(object sender, EventArgs e)
        {
            imageSlideShow(slidingWindowImages, isCharacterList, isolatedTextImage);
        }

    }
}
