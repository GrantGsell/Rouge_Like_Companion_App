using RoguelikeCompanion;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace NeuralNetworkVisualization
{
    public partial class NN_Visualization : Form
    {
        // Fields
        NeuralNetwork nn = new NeuralNetwork();
        string textBoxPathName = @"C:\Users\Grant\Desktop\Java_Rouge_Like_App\screenshots\temp_1.jpg";

        /*
         */
        public NN_Visualization()
        {
            InitializeComponent();
            Bitmap newImage = convertToBitmap(textBoxPathName);
            showExtractedTextBox(newImage);
            showCurrentSlidingWindow(newImage);
            string guess = nn.newImagePrediction(newImage);
            imageIsolation(newImage);
            int test = 5;
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


        public void imageIsolation(Bitmap newImage)
        {
            // Timer
            System.Windows.Forms.Timer myTimer = new System.Windows.Forms.Timer();
            int delayTime = 2000;
            myTimer.Interval = delayTime;

            // Sliding window dimensions and step size
            int swHeight = 18, swWidth = 15, swDelta = 5;

            // Obtain indicies for character separation
            List<int> charSeparationIndicies;
            Bitmap isolatedTextImage;
            (charSeparationIndicies, isolatedTextImage) = CharacterSegmentation.characterSegmentation(newImage);

            showExtractedTextBox(isolatedTextImage);
            for(int i = 0; i < isolatedTextImage.Width - swWidth + swDelta; i += swDelta)
            {
                //Bitmap test = ScreenImgCapture.cropBitMap(isolatedTextImage, charSeparationIndicies[0], swWidth, 0, swHeight);
                Bitmap test = ScreenImgCapture.cropBitMap(isolatedTextImage, i, swWidth, 0, swHeight);
                showCurrentSlidingWindow(test);
                if (charSeparationIndicies.Contains(i))
                {
                    Thread.Sleep(1000);
                }
            }
           
        }
                                     
    }
}
