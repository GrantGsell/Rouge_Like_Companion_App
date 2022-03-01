using RoguelikeCompanion;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace NeuralNetworkVisualization
{
    public partial class NN_Visualization : Form
    {
        // Fields
        NeuralNetwork nn = new NeuralNetwork();
        string textBoxPathName = @"C:\Users\Grant\Desktop\Java_Rouge_Like_App\screenshots\temp_1.jpg";

        public NN_Visualization()
        {
            InitializeComponent();
            Bitmap newImage = convertToBitmap(textBoxPathName);
            showExtractedTextBox(newImage);
            showCurrentSlidingWindow(newImage);
        }


        public void showExtractedTextBox(Bitmap textBoxImage)
        {
            // Set image and fit to size
            extractedTextBox.Image = textBoxImage;
            extractedTextBox.SizeMode = PictureBoxSizeMode.StretchImage;
        }

        public void showCurrentSlidingWindow(Bitmap slidingWindow)
        {
            // Set image and fit to size
            currentSlidingWindowSlice.Image = slidingWindow;
            currentSlidingWindowSlice.SizeMode = PictureBoxSizeMode.StretchImage;
        }


        public Bitmap convertToBitmap(string fileName)
        {
            Bitmap bitmap;
            using (Stream bmpStream = System.IO.File.Open(fileName, System.IO.FileMode.Open))
            {
                Image image = Image.FromStream(bmpStream);

                bitmap = new Bitmap(image);

            }
            return bitmap;
        }

    }
}
