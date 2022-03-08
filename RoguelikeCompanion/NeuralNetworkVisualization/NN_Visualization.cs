using RoguelikeCompanion;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Windows.Forms;

namespace NeuralNetworkVisualization
{
    public partial class NN_Visualization : Form
    {
        // Class Fields
        NeuralNetwork nn = new NeuralNetwork();
        string textBoxPathName = @"C:\Users\Grant\Desktop\Java_Rouge_Like_App\screenshots\temp_1.jpg";
        List<Bitmap> slidingWindowImages = new List<Bitmap>();
        List<Bitmap> scaledSlidingWindowImages = new List<Bitmap>();
        List<bool> isCharacterList = new List<bool>();
        int currSWIndex = 0;
        Bitmap isolatedTextImage;
        Timer slideShowTimer = new Timer();

        // For guess correction
        Dictionary<string, (string, bool)> objectNameDictionary = ObjectInformation.createObjectNameDictionary();
        string[] objectNames;


        // Rectagle outline made out of four picture boxes
        PictureBox rectTop = new PictureBox();
        PictureBox rectRight = new PictureBox();
        PictureBox rectBottom = new PictureBox();
        PictureBox rectLeft = new PictureBox();

        /*
         * NN_Visualization constructor.
         */
        public NN_Visualization()
        {
            InitializeComponent();

            this.IsMdiContainer = true;

            // Read in new image 
            Bitmap newImage = convertToBitmap(textBoxPathName);

            // Obtain all sliding window images
            (slidingWindowImages, isCharacterList, scaledSlidingWindowImages) = imageIsolation(newImage);

            // Initialize outline rectangle
            initializeRectangleOutline();

            // Set object names dictionary
            objectNames = objectNameDictionary.Keys.ToArray<string>();

            // Initialize Textboxes
            initializeTextBoxes();
            
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
         * @return three lists, one containing the sliding window slices, one
         *   containing if the respective bitmap is a character to be processed,
         *   one containing the scaled bitmaps images.
         */
        public (List<Bitmap>, List<bool>, List<Bitmap>) imageIsolation(Bitmap newImage)
        {
            // Sliding window dimensions and step size
            int swHeight = 18, swWidth = 15, swDelta = 5;

            // Obtain indicies for character separation
            List<int> charSeparationIndicies;
            (charSeparationIndicies, isolatedTextImage) = CharacterSegmentation.characterSegmentation(newImage);
            //showExtractedTextBox(isolatedTextImage);
            Bitmap scaledIsolatedTextImage = individualCharacterForm.scaleImage(isolatedTextImage, isolatedTextImage.Width * 5, isolatedTextImage.Height * 5);
            showExtractedTextBox(scaledIsolatedTextImage);

            // Create List of Sliding Window Images
            List<Bitmap> slidingWindowImages = new List<Bitmap>();
            List<Bitmap> scaledSlidingWindowImages = new List<Bitmap>();
            List<bool> isCharacterList = new List<bool>();
            for(int i = 0; i < isolatedTextImage.Width - swWidth + swDelta; i++)
            {
                Bitmap newSlidingWindowBox = ScreenImgCapture.cropBitMap(isolatedTextImage, i, swWidth, 0, swHeight);
                Bitmap scaledNewSWB = individualCharacterForm.scaleImage(newSlidingWindowBox, newSlidingWindowBox.Width * 5, newSlidingWindowBox.Height * 5);
                slidingWindowImages.Add(newSlidingWindowBox);
                scaledSlidingWindowImages.Add(scaledNewSWB);
                if (charSeparationIndicies.Contains(i))
                    isCharacterList.Add(true);
                else
                    isCharacterList.Add(false);                             
            }

            return (slidingWindowImages, isCharacterList, scaledSlidingWindowImages);
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
            Bitmap scaledCurrSW = scaledSlidingWindowImages[currSWIndex];
            showCurrentSlidingWindow(scaledCurrSW);

            // Change sliding window background color
            if (foundLetters[currSWIndex])
            {
                slidingWindowBackground.BackColor = Color.Green;                
                string guess = NeuralNetwork.makeNNPrediction(currSW);
                addFoundCharacterToForm(currSW, guess);
            }
            else
            {
                slidingWindowBackground.BackColor = Color.Red;
            }

            currSWIndex++;
            moveRectangle();
            if (currSWIndex > image.Width - swWidth)
            {
                // Stop timer
                slideShowTimer.Stop();

                // Populate text boxes
                string guess = nn.newImagePrediction(convertToBitmap(textBoxPathName));
                initialGuessTB.Text = guess;

                // Correct guess and display, Fix guess object if not found in dictionary
                if (!objectNameDictionary.ContainsKey(guess))
                {
                    guess = closestWord(guess, objectNames);
                }
                correctedGuessTB.Text = guess;
            }
            
        }


        /*
         * Method to be run when the timer is raised. Calls the imageSlideShow method.
         */
        public void slideShow_Tick(object sender, EventArgs e)
        {
            imageSlideShow(slidingWindowImages, isCharacterList, isolatedTextImage);
        }


        /*
         * Creates a child form that contains a sliding window slice and the
         *   prediction for said slice.
         * @param image, the sliding window slice.
         * @param guess, the single character prediciton of the slice.
         */
        public void addFoundCharacterToForm(Bitmap image, string guess)
        {
            individualCharacterForm charChildForm = new individualCharacterForm(image, guess);
            charChildForm.MdiParent = this;
            extractedCharFLP.Controls.Add(charChildForm);
            charChildForm.Show();
        }


        /*
         * Creates a rectangle to mimic a sliding window, made out of four thin
         *   picture boxes.
         */
        public void initializeRectangleOutline()
        {
            // Get textBox width, height scalers
            int widthScalar = extractedTextBox.Width / 15;
            int heightScalar = extractedTextBox.Height / 18;

            // Set rectangle outline sizes
            rectTop.Size = new Size(15 * 4 + 5, 2);
            rectBottom.Size = new Size(15 * 4 + 5, 2);
            rectRight.Size = new Size(2, 18 * heightScalar);
            rectLeft.Size = new Size(2, 18 * heightScalar);

            // Set rectangle outline color (back color)
            rectTop.BackColor = Color.Aqua;
            rectBottom.BackColor = Color.Aqua;
            rectRight.BackColor = Color.Aqua;
            rectLeft.BackColor = Color.Aqua;

            // Set rectangle location
            rectTop.Location = new Point(extractedTextBox.Location.X, extractedTextBox.Location.Y);
            rectBottom.Location = new Point(extractedTextBox.Location.X, extractedTextBox.Location.Y + 18 * heightScalar);
            rectRight.Location = new Point(extractedTextBox.Location.X + 15 * 4 + 4, extractedTextBox.Location.Y);
            rectLeft.Location = new Point(extractedTextBox.Location.X, extractedTextBox.Location.Y);          

            // Add all rectangle outline constructs to form
            this.Controls.Add(rectTop);
            this.Controls.Add(rectBottom);
            this.Controls.Add(rectRight);
            this.Controls.Add(rectLeft);

            // Send all edges to front
            rectTop.BringToFront();
            rectBottom.BringToFront();
            rectRight.BringToFront();
            rectLeft.BringToFront();
        }


        /*
         * Moves each sliding window side by five in the x-direction
         */
        public void moveRectangle()
        {
            int delta = 5;
            rectTop.Location = new Point(rectTop.Location.X + delta, rectTop.Location.Y);
            rectBottom.Location = new Point(rectBottom.Location.X + delta, rectBottom.Location.Y);
            rectRight.Location = new Point(rectRight.Location.X + delta, rectRight.Location.Y);
            rectLeft.Location = new Point(rectLeft.Location.X + delta, rectLeft.Location.Y);
        }


        /*
         * Initialize Textbox Controls
         */
        public void initializeTextBoxes()
        {
            // Make the textbox un-editable
            initialGuessTB.Enabled = false;
            correctedGuessTB.Enabled = false;

            // Center all text for both boxes
            initialGuessTB.TextAlign = HorizontalAlignment.Center;
            correctedGuessTB.TextAlign = HorizontalAlignment.Center;
        }


        /*
         * Sets the text for the initialGuessTB.
         * @initialGuess, the string text to be displayed.
         */
        public void setInitialGuessTextBox(string initialGuess)
        {
            initialGuessTB.Text = initialGuess;
            initialGuessTB.TextAlign = HorizontalAlignment.Center;
        }


        /*
         * Sets the text for the correctedGuessTB.
         * @correctedGuess, the string text to be displayed.
         */
        public void setCorrectedGuessTextBox(string correctedGuess)
        {
            correctedGuessTB.Text = correctedGuess;
            correctedGuessTB.TextAlign = HorizontalAlignment.Center;
        }


        /*
         * Finds the closest word in the string array via levenshtein distance
         *   given the string guess.
         *   
         * @param guess, the string to be checked against.
         * @param objectNames, the array containing correct object names.
        */
        public string closestWord(string guess, string[] objectNames)
        {
            // Calculate the Levenshtein distance between incorrect word and all potential words
            int minLevDist = 50;
            String correctWord = "";
            foreach (string elem in objectNames)
            {
                int currDist = levenshteinDistance(guess, elem);
                if (minLevDist > currDist)
                {
                    minLevDist = currDist;
                    correctWord = elem;
                }
            }

            return correctWord;
        }


        /*
         * Edit Distance (Levenshtein Distance)
         */
        public int levenshteinDistance(string guess, string compareWord)
        {
            // Make both words lowercase
            string wordA = guess.ToLower();
            string wordB = compareWord.ToLower();

            // Declare Levenshtein distance matrix
            int[,] levMatrix = new int[wordB.Length + 1, wordA.Length + 1];

            // Initialize the first row, col of the matrix
            for (int row = 1; row < wordB.Length + 1; row++)
            {
                levMatrix[row, 0] = row;
            }
            for (int col = 1; col < wordA.Length + 1; col++)
            {
                levMatrix[0, col] = col;
            }

            // Use tabulation to populate the table
            for (int row = 1; row < wordB.Length + 1; row++)
            {
                for (int col = 1; col < wordA.Length + 1; col++)
                {
                    int sameCharFlag = 1;
                    if (wordA[col - 1] == wordB[row - 1])
                    {
                        sameCharFlag = 0;
                    }
                    levMatrix[row, col] = Math.Min(
                                Math.Min(levMatrix[row - 1, col] + 1, levMatrix[row - 1, col - 1] + sameCharFlag),
                                levMatrix[row, col - 1] + 1
                    );
                }
            }
            return levMatrix[wordB.Length, wordA.Length];
        }
    }
}
