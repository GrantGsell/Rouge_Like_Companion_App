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
        Dictionary<string, (string, bool)> objectNameDictionary = ObjectInformation.createObjectNameDictionary();
        double[,] borderData = BorderClass.readBorderData();
        NeuralNetwork nn = new NeuralNetwork();
        FlowLayoutPanel dynamicFlowLayoutPanel1;
        FlowLayoutPanel dynamicFlowLayoutPanel2;

        public MainWindowForm()
        {
            InitializeComponent();

            // Set app size
            this.Bounds = Screen.PrimaryScreen.Bounds;
            this.WindowState = FormWindowState.Maximized;

            this.IsMdiContainer = true;
           

            // Create container for all weapons
            dynamicFlowLayoutPanel1 = new FlowLayoutPanel();
            dynamicFlowLayoutPanel1.Name = "weaponFlowLayoutPanel";
            dynamicFlowLayoutPanel1.Width = this.Width * 65 / 100;
            dynamicFlowLayoutPanel1.Height = this.Height;
            dynamicFlowLayoutPanel1.TabIndex = 0;
            dynamicFlowLayoutPanel1.FlowDirection = FlowDirection.LeftToRight;
            this.Controls.Add(dynamicFlowLayoutPanel1);

            // Create container for all items
            dynamicFlowLayoutPanel2 = new FlowLayoutPanel();
            dynamicFlowLayoutPanel2.BackColor = Color.DarkCyan;
            dynamicFlowLayoutPanel2.Name = "itemFlowLayoutPanel";
            dynamicFlowLayoutPanel2.Width = this.Width - dynamicFlowLayoutPanel1.Width;
            dynamicFlowLayoutPanel2.Height = this.Height / 2;
            dynamicFlowLayoutPanel2.TabIndex = 0;
            dynamicFlowLayoutPanel2.FlowDirection = FlowDirection.TopDown;
            dynamicFlowLayoutPanel2.Location = new Point(dynamicFlowLayoutPanel1.Width, 0 );
            this.Controls.Add(dynamicFlowLayoutPanel2);

            // For testing
            /*
            Image test = Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/temp_0.jpg");
            IndividualWeaponForm formChild = new IndividualWeaponForm(test, "Crossbow", "33.2", "0.3 seconds", "21", "Automatic");
            formChild.MdiParent = this;
            dynamicFlowLayoutPanel.Controls.Add(formChild);
            formChild.Show();
            Image test = Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/temp_0.jpg");
            IndividualWeaponForm formChild = new IndividualWeaponForm(test, "Crossbow", "33.2", "0.3 seconds", "21", "Automatic");
            formChild.MdiParent = this;
            formChild.Show();
            */

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

                }

                // Obtain object information
                if (objectNameDictionary.GetValueOrDefault(guess).Item2)
                {
                    // Used for weapons
                    var dataTuple = ObjectInformation.obtainWeaponStats(guess);
                    IndividualWeaponForm formChild = new IndividualWeaponForm(dataTuple.Item6, dataTuple.Item7, dataTuple.Item1, dataTuple.Item2, dataTuple.Item3, dataTuple.Item4, dataTuple.Item5);
                    formChild.MdiParent = this;
                    dynamicFlowLayoutPanel1.Controls.Add(formChild);
                    formChild.Show();
                }
                else
                {
                    // Used for Items

                }
            }

            // Add Test weapon
            var dataTuple2 = ObjectInformation.obtainWeaponStats("Casey");
            IndividualWeaponForm formChild2 = new IndividualWeaponForm(dataTuple2.Item6, dataTuple2.Item7, dataTuple2.Item1, dataTuple2.Item2, dataTuple2.Item3, dataTuple2.Item4, dataTuple2.Item5);
            formChild2.MdiParent = this;
            dynamicFlowLayoutPanel1.Controls.Add(formChild2);
            formChild2.Show();

            // Add Test item
            var itemDataTuple = ObjectInformation.obtainItemStats("Orange");
            IndividualItemForm formChild3 = new IndividualItemForm(itemDataTuple.Item4, itemDataTuple.Item2, itemDataTuple.Item3, dynamicFlowLayoutPanel2.Width);
            formChild3.MdiParent = this;
            dynamicFlowLayoutPanel2.Controls.Add(formChild3);
            formChild3.Show();


        }


        /*
         * Dont Delete this Funciton
         */
        private void MainWindowForm_Load(object sender, EventArgs e)
        {

        }
    }
}
