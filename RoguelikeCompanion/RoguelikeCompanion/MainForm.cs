using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class MainForm : Form
    {
        string[] objectNames;
        double[,] borderData = BorderClass.readBorderData();
        NeuralNetwork nn = new NeuralNetwork();
        FlowLayoutPanel dynamicFlowLayoutPanelWeapon;
        FlowLayoutPanel dynamicFlowLayoutPanelItem;
        FlowLayoutPanel dynamicFlowLayoutPanelSynergy;
        Dictionary<string, bool> currentRunObjects = new Dictionary<string, bool>();
        Dictionary<string, (string, bool)> objectNameDictionary = ObjectInformation.createObjectNameDictionary();

        // Dictionaries for background color change
        Dictionary<string, Point> objectFormPoint = new Dictionary<string, Point>();
        Dictionary<string, Point> synergyFormPoint = new Dictionary<string, Point>();

        // Array for Nonobjet Notification Box text
        string[] arrayNonObjectText = new string[]{"CELL_KEY", "CHALLENGE_COMPLETE", "CHALLENGE_FAILED",
                                                       "DEAL_WITH_THE_DEVIL", "SACRIFICE_ACCEPTED", "SACRIFICE",
                                                       "PURIFIED", "BRAVE_COMPANION", "ROLL_OF_THE_DICE",
                                                       "GLASS_ARMOR", "SER_JUNKAN'S_BOON", "AT_PEACCE", "POP",
                                                       "HUNT_COMPLETE"};

        // Muncher recipe form
        MuncherRecipes recipesForm;
        bool showRecipesFlag;

        public MainForm()
        {
            InitializeComponent();
            recipesForm = new MuncherRecipes();
            showRecipesFlag = false;
        }


        /*
         */
        private void closeButton_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }


        /*
         */
        private void Form1_Load(object sender, EventArgs e)
        {
            this.Size = Screen.PrimaryScreen.WorkingArea.Size;

            objectNames = objectNameDictionary.Keys.ToArray<string>();

            objectNames = objectNames.Concat(arrayNonObjectText).ToArray();

            this.IsMdiContainer = true;

            // Create container for all weapons
            dynamicFlowLayoutPanelWeapon = new CustomFlowLayoutPanel();
            dynamicFlowLayoutPanelWeapon.Name = "weaponFlowLayoutPanel";
            dynamicFlowLayoutPanelWeapon.Width = this.Width * 55 / 100;
            dynamicFlowLayoutPanelWeapon.Height = this.Height - 40;
            dynamicFlowLayoutPanelWeapon.Location = new Point(0, 40);
            dynamicFlowLayoutPanelWeapon.TabIndex = 0;
            dynamicFlowLayoutPanelWeapon.FlowDirection = FlowDirection.LeftToRight;
            dynamicFlowLayoutPanelWeapon.AutoScroll = true;
            dynamicFlowLayoutPanelWeapon.WrapContents = true;
            dynamicFlowLayoutPanelWeapon.SetAutoScrollMargin(10, 10);
            this.Controls.Add(dynamicFlowLayoutPanelWeapon);

            // Create container for all items
            dynamicFlowLayoutPanelItem = new CustomFlowLayoutPanel();
            dynamicFlowLayoutPanelItem.Name = "itemFlowLayoutPanel";
            dynamicFlowLayoutPanelItem.Width = this.Width - dynamicFlowLayoutPanelWeapon.Width;
            dynamicFlowLayoutPanelItem.Height = this.Height / 2 - (40 / 2);
            dynamicFlowLayoutPanelItem.TabIndex = 0;
            dynamicFlowLayoutPanelItem.FlowDirection = FlowDirection.TopDown;
            dynamicFlowLayoutPanelItem.Location = new Point(dynamicFlowLayoutPanelWeapon.Width, 40);
            dynamicFlowLayoutPanelItem.AutoScroll = true;
            dynamicFlowLayoutPanelItem.WrapContents = false;
            dynamicFlowLayoutPanelItem.SetAutoScrollMargin(10, 10);
            this.Controls.Add(dynamicFlowLayoutPanelItem);

            // Create container for all synergies
            dynamicFlowLayoutPanelSynergy = new CustomFlowLayoutPanel();
            dynamicFlowLayoutPanelSynergy.Name = "synergyFlowLayoutPanel";
            dynamicFlowLayoutPanelSynergy.Width = this.Width - dynamicFlowLayoutPanelWeapon.Width;
            dynamicFlowLayoutPanelSynergy.Height = this.Height / 2 - (40 / 2);
            dynamicFlowLayoutPanelSynergy.TabIndex = 0;
            dynamicFlowLayoutPanelSynergy.FlowDirection = FlowDirection.LeftToRight;
            dynamicFlowLayoutPanelSynergy.Location = new Point(dynamicFlowLayoutPanelWeapon.Width, dynamicFlowLayoutPanelItem.Height + 40);
            dynamicFlowLayoutPanelSynergy.AutoScroll = true;
            dynamicFlowLayoutPanelSynergy.WrapContents = true;
            dynamicFlowLayoutPanelSynergy.SetAutoScrollMargin(10, 10);
            this.Controls.Add(dynamicFlowLayoutPanelSynergy);

            // Set FLP background images
            addBackgroundImages();

            // Set Muncher Recipes button size and location
            btnMuncherRecipes.Height = newRunButton.Height;
            btnMuncherRecipes.Location = new Point(newRunButton.Width, 0);

            // Show then hide Muncher Recipes, this fixes initial form location
            recipesForm.Show();
            recipesForm.Hide();

            // Timer to run the active capture method
            Timer activeCapTimer = new Timer();
            activeCapTimer.Interval = (1250);
            activeCapTimer.Tick += new EventHandler(activeCapTimer_Tick);
            activeCapTimer.Start();
        }


        /*
         */
        public void activeCapTimer_Tick(object sender, EventArgs e)
        {
            activeCapture();
        }


        /*
         */
        public void activeCapture()
        {
            // Obtain bitmaps
            Bitmap initialImage = ScreenImgCapture.bitmapScreenCapture();
            Bitmap notificationBox = ScreenImgCapture.cropBitMap(initialImage, 567, 425, 767, 77);
            Bitmap borderNotificationBox = ScreenImgCapture.cropBitMap(notificationBox, 25, notificationBox.Width - 25, 0, notificationBox.Height);

            // Check for notification box
            int borderClass = BorderClass.predictIsBorder(borderNotificationBox, borderData);
            if (borderClass != 0 && borderClass != 4)
            {
                string guess = nn.newImagePrediction(notificationBox);

                // Return if null
                if (guess == null) return;
                                
                // Fix guess object if not found in dictionary
                if (!objectNameDictionary.ContainsKey(guess))
                {
                    guess = closestWord(guess, objectNames);
                }

                // Check to see if the text is a non object notification box
                if (arrayNonObjectText.Contains(guess))
                {
                    return;
                }

                // Check to see if the object was already found
                if (currentRunObjects.ContainsKey(guess))
                {
                    return;
                }
                currentRunObjects.Add(guess, true);

                // Obtain object information
                if (objectNameDictionary.GetValueOrDefault(guess).Item2)
                {
                    // Add weapon to the main form
                    var dataTuple = ObjectInformation.obtainWeaponStats(guess);
                    IndividualWeaponForm formChild = new IndividualWeaponForm(dataTuple.Item6, dataTuple.Item7, dataTuple.Item1, dataTuple.Item2, dataTuple.Item3, dataTuple.Item4, dataTuple.Item5);
                    formChild.MdiParent = this;
                    dynamicFlowLayoutPanelWeapon.Controls.Add(formChild);
                    formChild.Show();

                    // Add Form object name and Location to dictionary
                    if(!objectFormPoint.ContainsKey(dataTuple.Item1))
                    {
                        objectFormPoint.Add(dataTuple.Item1, formChild.Location);
                    }

                    // Set forms to focus on newly added images
                    dynamicFlowLayoutPanelWeapon.ScrollControlIntoView(formChild);
                }
                else
                {
                    // Add item to the main form
                    var itemDataTuple = ObjectInformation.obtainItemStats(guess);
                    IndividualItemForm formChild = new IndividualItemForm(itemDataTuple.Item4, itemDataTuple.Item2, itemDataTuple.Item3, dynamicFlowLayoutPanelItem.Width - (dynamicFlowLayoutPanelItem.AutoScrollMargin.Width * 3));
                    formChild.MdiParent = this;
                    dynamicFlowLayoutPanelItem.Controls.Add(formChild);
                    formChild.Show();
                    dynamicFlowLayoutPanelItem.ScrollControlIntoView(formChild);

                    // Add Form object name and Location to dictionary
                    if (!objectFormPoint.ContainsKey(itemDataTuple.Item1))
                    {
                        objectFormPoint.Add(itemDataTuple.Item1, formChild.Location);
                    }
                }


                // Add synergies to the main form
                var synergyTupleList = ObjectInformation.obtainSynergyStats(guess);
                foreach (var synergyTuple in synergyTupleList)
                {
                    // Check the object form dictionary for the synergy item
                    if (objectFormPoint.ContainsKey(synergyTuple.Item3))
                    {
                        // Change the background of synergized objects
                        changeChildFormColor(synergyTuple.Item3, synergyTuple.Item4);
                        changeChildFormColor(synergyTuple.Item2, objectNameDictionary.GetValueOrDefault(guess).Item2);

                        // Remove the synergy object that exists
                        removeFoundSynergy(synergyTuple.Item2);
                        continue;
                    }

                    
                    // Add synergy object if it is not in the synergyFormPoint dictionary
                    if (!synergyFormPoint.ContainsKey(synergyTuple.Item3))
                    {
                        IndividualSynergyForm formChild1 = new IndividualSynergyForm(synergyTuple.Item1, synergyTuple.Item2);
                        formChild1.MdiParent = this;
                        dynamicFlowLayoutPanelSynergy.Controls.Add(formChild1);
                        formChild1.Show();
                        dynamicFlowLayoutPanelSynergy.ScrollControlIntoView(formChild1);

                        // Add synergy to point dicitonary
                        synergyFormPoint.Add(synergyTuple.Item3, formChild1.Location);
                    }
                }

            }

        }


        /*
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


        /*
         */
        private void Form1_LocationChanged(object sender, EventArgs e)
        {
            this.Bounds = Screen.FromControl(this).Bounds;

            try
            {
                // Change size of the three children forms
                dynamicFlowLayoutPanelWeapon.Width = this.Width * 55 / 100;
                dynamicFlowLayoutPanelWeapon.Height = this.Height - 40;
                dynamicFlowLayoutPanelItem.Width = this.Width - dynamicFlowLayoutPanelWeapon.Width;
                dynamicFlowLayoutPanelItem.Height = (this.Height - 40) / 2;
                dynamicFlowLayoutPanelSynergy.Width = this.Width - dynamicFlowLayoutPanelWeapon.Width;
                dynamicFlowLayoutPanelSynergy.Height = (this.Height - 40) / 2;

                // Change the location of two children forms
                dynamicFlowLayoutPanelItem.Location = new Point(dynamicFlowLayoutPanelWeapon.Width, 40);
                dynamicFlowLayoutPanelSynergy.Location = new Point(dynamicFlowLayoutPanelWeapon.Width, dynamicFlowLayoutPanelItem.Height + 40);

                // Change the location of the Muncher Recipies form
                int newX = this.Location.X + this.Width / 2 - recipesForm.Width / 2;
                int newY = this.Location.Y + this.Height / 2 - recipesForm.Height / 2;
                recipesForm.Location = new Point(newX, newY);
            }
            catch (System.NullReferenceException)
            {
                return;
            }
        }


        /*
         */
        private void newRunButton_Click(object sender, EventArgs e)
        {
            // Clear the flow layout panels for a new run
            dynamicFlowLayoutPanelWeapon.Controls.Clear();
            dynamicFlowLayoutPanelItem.Controls.Clear();
            dynamicFlowLayoutPanelSynergy.Controls.Clear();

            // Clear dictionaries
            objectFormPoint.Clear();
            synergyFormPoint.Clear();
            currentRunObjects.Clear();
        }

        /*
         */
        public void changeChildFormColor(string objectName, bool isWeapon)
        {
            // Change the bacground of the object
            Point formPt;
            objectFormPoint.TryGetValue(objectName, out formPt);
            if(isWeapon)
            {
                var temp = dynamicFlowLayoutPanelWeapon.GetChildAtPoint(formPt, GetChildAtPointSkip.None);
                temp.BackColor = Color.LightGreen;
            }
            else
            {
                var temp = dynamicFlowLayoutPanelItem.GetChildAtPoint(formPt, GetChildAtPointSkip.None);
                temp.ForeColor = Color.DarkGreen;
                temp.BackColor = Color.LightGreen;
            }
        }


        /*
         */
        public void removeFoundSynergy(string objectName)
        {
            try
            {
                // Remove the synergy object form 
                Point formPt;
                synergyFormPoint.TryGetValue(objectName, out formPt);
                var synergyToRemove = dynamicFlowLayoutPanelSynergy.GetChildAtPoint(formPt, GetChildAtPointSkip.None);
                synergyToRemove.Dispose();

                // Remove the synergy object from the syenrgy dictionary
                synergyFormPoint.Remove(objectName);

            }
            catch
            {
                return;
            }
        }


        /*
         */
        private void btnMuncherRecipes_Click(object sender, EventArgs e)
        {
            if (!showRecipesFlag)
            {
                int newX = this.Location.X + this.Width / 2 - recipesForm.Width / 2;
                int newY = this.Location.Y + this.Height / 2 - recipesForm.Height / 2;
                recipesForm.Location = new Point(newX, newY);
                recipesForm.Show();
            }
            else
            {
                recipesForm.Hide();
            }
            showRecipesFlag = !showRecipesFlag;
        }


        public void addBackgroundImages()
        {
            // Obtain images 
            Image imageWeaponFLP = Image.FromFile("C:/Users/Grant/Desktop/Rouge_Like_Companion_App/Gungeon_Backgound_Art.png");
            Image imageItemFLP = Image.FromFile("C:/Users/Grant/Desktop/Rouge_Like_Companion_App/Gungeon_Backgound_Art_1.png");
            Image imageSynergyFLP = Image.FromFile("C:/Users/Grant/Desktop/Rouge_Like_Companion_App/Gungeon_Backgound_Art_2.png");

            // Set form backgrounds
            dynamicFlowLayoutPanelWeapon.BackgroundImage = imageWeaponFLP;
            dynamicFlowLayoutPanelItem.BackgroundImage = imageItemFLP;
            dynamicFlowLayoutPanelSynergy.BackgroundImage = imageSynergyFLP;

            // Fit the image to its container
            dynamicFlowLayoutPanelWeapon.BackgroundImageLayout = ImageLayout.Stretch;
            dynamicFlowLayoutPanelItem.BackgroundImageLayout = ImageLayout.Stretch;
            dynamicFlowLayoutPanelSynergy.BackgroundImageLayout = ImageLayout.Stretch;

        }
    }
}
