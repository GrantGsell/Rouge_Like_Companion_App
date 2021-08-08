using MathNet.Numerics.LinearAlgebra;
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
    public partial class MainWindowForm : Form
    {
        string[] objectNames;
        double[,] borderData = BorderClass.readBorderData();
        NeuralNetwork nn = new NeuralNetwork();
        FlowLayoutPanel dynamicFlowLayoutPanelWeapon;
        FlowLayoutPanel dynamicFlowLayoutPanelItem;
        FlowLayoutPanel dynamicFlowLayoutPanelSynergy;
        Dictionary<string, bool> currentRunObjects = new Dictionary<string, bool>();
        Dictionary<string, (string, bool)> objectNameDictionary = ObjectInformation.createObjectNameDictionary();

        public MainWindowForm()
        {
            InitializeComponent();

            // Obtain all object names
            objectNames = objectNameDictionary.Keys.ToArray<string>();

            // Set app size
            this.Bounds = Screen.PrimaryScreen.Bounds;
            this.WindowState = FormWindowState.Maximized;
            this.IsMdiContainer = true;

            // Set app background image
            //string mainBackground = "C:/Users/Grant/Desktop/Rouge_Like_Companion_App/Gungeon_Backgound_Art.png";
            //Image mainBackgroundImage = Image.FromFile(mainBackground);
            //this.BackgroundImage = mainBackgroundImage;

            // Create container for all weapons
            dynamicFlowLayoutPanelWeapon = new FlowLayoutPanel();
            dynamicFlowLayoutPanelWeapon.BackColor = Color.Transparent;//Color.DeepSkyBlue;
            dynamicFlowLayoutPanelWeapon.Name = "weaponFlowLayoutPanel";
            dynamicFlowLayoutPanelWeapon.Width = this.Width * 65 / 100;
            dynamicFlowLayoutPanelWeapon.Height = this.Height;
            dynamicFlowLayoutPanelWeapon.TabIndex = 0;
            dynamicFlowLayoutPanelWeapon.FlowDirection = FlowDirection.LeftToRight;
            this.Controls.Add(dynamicFlowLayoutPanelWeapon);

            // Create container for all items
            dynamicFlowLayoutPanelItem = new FlowLayoutPanel();
            dynamicFlowLayoutPanelItem.BackColor = Color.Transparent;//Color.DarkCyan;
            dynamicFlowLayoutPanelItem.Name = "itemFlowLayoutPanel";
            dynamicFlowLayoutPanelItem.Width = this.Width - dynamicFlowLayoutPanelWeapon.Width;
            dynamicFlowLayoutPanelItem.Height = this.Height / 2;
            dynamicFlowLayoutPanelItem.TabIndex = 0;
            dynamicFlowLayoutPanelItem.FlowDirection = FlowDirection.TopDown;
            dynamicFlowLayoutPanelItem.Location = new Point(dynamicFlowLayoutPanelWeapon.Width, 0 );
            this.Controls.Add(dynamicFlowLayoutPanelItem);

            // Create container for all synergies
            dynamicFlowLayoutPanelSynergy = new FlowLayoutPanel();
            dynamicFlowLayoutPanelSynergy.BackColor = Color.Transparent; //Color.DarkGreen;
            dynamicFlowLayoutPanelSynergy.Name = "synergyFlowLayoutPanel";
            dynamicFlowLayoutPanelSynergy.Width = this.Width - dynamicFlowLayoutPanelWeapon.Width;
            dynamicFlowLayoutPanelSynergy.Height = this.Height / 2;
            dynamicFlowLayoutPanelSynergy.TabIndex = 0;
            dynamicFlowLayoutPanelSynergy.FlowDirection = FlowDirection.TopDown;
            dynamicFlowLayoutPanelSynergy.Location = new Point(dynamicFlowLayoutPanelWeapon.Width, this.Height / 2);
            this.Controls.Add(dynamicFlowLayoutPanelSynergy);
        }


        private void capture_Click(object sender, EventArgs e)
        {
            /*
            */
            //NeuralNetwork nn = new NeuralNetwork();
            System.Threading.Thread.Sleep(4000);
            
            // Obtain bitmaps
            Bitmap initialImage = ScreenImgCapture.bitmapScreenCapture();
            Bitmap notificationBox = ScreenImgCapture.cropBitMap(initialImage, 567, 425, 767, 77);
            Bitmap borderNotificationBox = ScreenImgCapture.cropBitMap(notificationBox, 25, notificationBox.Width - 25, 0, notificationBox.Height);

            // Check for notification box
            int borderClass = BorderClass.predictIsBorder(borderNotificationBox, borderData);
            if(borderClass != 0 && borderClass != 4)
            {
                string guess = nn.newImagePrediction(notificationBox);

                // Fix guess object if not found in dictionary
                if (!objectNameDictionary.ContainsKey(guess))
                {
                    guess = closestWord(guess, objectNames);
                }

                // Check to see if the object was already found
                if (currentRunObjects.ContainsKey(guess))
                {
                    //continue;
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
                    }
                }
                else
                {
                    // Add item to the main form
                    var itemDataTuple = ObjectInformation.obtainItemStats(guess);
                    IndividualItemForm formChild = new IndividualItemForm(itemDataTuple.Item4, itemDataTuple.Item2, itemDataTuple.Item3, dynamicFlowLayoutPanelItem.Width);
                    formChild.MdiParent = this;
                    dynamicFlowLayoutPanelItem.Controls.Add(formChild);
                    formChild.Show();

                }
            }

            // For Testing purposes only
            /*
            // Add Test weapon
            var dataTuple2 = ObjectInformation.obtainWeaponStats("Casey");
            IndividualWeaponForm formChild5 = new IndividualWeaponForm(dataTuple2.Item6, dataTuple2.Item7, dataTuple2.Item1, dataTuple2.Item2, dataTuple2.Item3, dataTuple2.Item4, dataTuple2.Item5);
            formChild5.MdiParent = this;
            dynamicFlowLayoutPanelWeapon.Controls.Add(formChild5);
            formChild5.Show();

            // Add Test item
            var itemDataTuple1 = ObjectInformation.obtainItemStats("Orange");
            IndividualItemForm formChild3 = new IndividualItemForm(itemDataTuple1.Item4, itemDataTuple1.Item2, itemDataTuple1.Item3, dynamicFlowLayoutPanelItem.Width);
            formChild3.MdiParent = this;
            dynamicFlowLayoutPanelItem.Controls.Add(formChild3);
            formChild3.Show();

            // Add Test synergy
            IndividualSynergyForm formChild4 = new IndividualSynergyForm(dataTuple2.Item6, "TEST TEST TEST");
            formChild4.MdiParent = this;
            dynamicFlowLayoutPanelSynergy.Controls.Add(formChild4);
            formChild4.Show();
            */
        }


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
                    }
                }
                else
                {
                    // Add item to the main form
                    var itemDataTuple = ObjectInformation.obtainItemStats(guess);
                    IndividualItemForm formChild = new IndividualItemForm(itemDataTuple.Item4, itemDataTuple.Item2, itemDataTuple.Item3, dynamicFlowLayoutPanelItem.Width);
                    formChild.MdiParent = this;
                    dynamicFlowLayoutPanelItem.Controls.Add(formChild);
                    formChild.Show();

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
         * Dont Delete this Funciton
         */
        private void MainWindowForm_Load(object sender, EventArgs e)
        {
            // Timer to run the active capture method
            Timer activeCapTimer = new Timer();
            activeCapTimer.Interval = (1000);
            activeCapTimer.Tick += new EventHandler(activeCapTimer_Tick);
            activeCapTimer.Start();
        }
    }
}
