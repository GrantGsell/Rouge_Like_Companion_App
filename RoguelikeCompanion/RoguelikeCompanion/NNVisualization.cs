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
    public partial class NNVisualization : Form
    {
        // Fields
        NeuralNetwork nn = new NeuralNetwork();

        public NNVisualization()
        {
            InitializeComponent();
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

    }
}
