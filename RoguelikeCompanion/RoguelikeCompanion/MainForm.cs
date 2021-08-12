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

        public MainForm()
        {
            InitializeComponent();
        }

        private void closeButton_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            this.Size = Screen.PrimaryScreen.WorkingArea.Size;

            objectNames = objectNameDictionary.Keys.ToArray<string>();

            this.IsMdiContainer = true;

            // Create container for all weapons
            dynamicFlowLayoutPanelWeapon = new FlowLayoutPanel();
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
            dynamicFlowLayoutPanelItem = new FlowLayoutPanel();
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
            dynamicFlowLayoutPanelSynergy = new FlowLayoutPanel();
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

                    // Add synergies to the main form
                    var synergyTupleList = ObjectInformation.obtainSynergyStats(guess);
                    foreach (var synergyTuple in synergyTupleList)
                    {
                        IndividualSynergyForm formChild1 = new IndividualSynergyForm(synergyTuple.Item1, synergyTuple.Item2);
                        formChild1.MdiParent = this;
                        dynamicFlowLayoutPanelSynergy.Controls.Add(formChild1);
                        formChild1.Show();
                        dynamicFlowLayoutPanelSynergy.ScrollControlIntoView(formChild1);
                    }

                    // Set forms to focus on newly added images
                    dynamicFlowLayoutPanelWeapon.ScrollControlIntoView(formChild);
                    //dynamicFlowLayoutPanelSynergy.ScrollControlIntoView(formChild1);
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
        }
    }
}
