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
        FlowLayoutPanel dynamicFlowLayoutPanel;

        public MainWindowForm()
        {
            InitializeComponent();
            
            this.IsMdiContainer = true;
            this.WindowState = FormWindowState.Maximized;

            // Create container for all weapons
            dynamicFlowLayoutPanel = new FlowLayoutPanel();
            dynamicFlowLayoutPanel.Name = "weaponFlowLayoutPanel";
            dynamicFlowLayoutPanel.Width = this.Width * 65 / 100;
            dynamicFlowLayoutPanel.Height = this.Height;
            dynamicFlowLayoutPanel.TabIndex = 0;
            dynamicFlowLayoutPanel.FlowDirection = FlowDirection.LeftToRight;
            this.Controls.Add(dynamicFlowLayoutPanel);
           
            // For testing
            //Image test = Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/temp_0.jpg");
            //IndividualWeaponForm formChild = new IndividualWeaponForm(test, "Crossbow", "33.2", "0.3 seconds", "21", "Automatic");
            //formChild.MdiParent = this;
            //dynamicFlowLayoutPanel.Controls.Add(formChild);
            //formChild.Show();


            // For testing
            //Image test = Image.FromFile("C:/Users/Grant/Desktop/Java_Rouge_Like_App/screenshots/temp_0.jpg");
            //IndividualWeaponForm formChild = new IndividualWeaponForm(test, "Crossbow", "33.2", "0.3 seconds", "21", "Automatic");
            //formChild.MdiParent = this;
            //formChild.Show();
            
        }

        private void capture_Click(object sender, EventArgs e)
        {
            WeaponObjectForm test = new WeaponObjectForm("Crossbow", "33.2", "0.3 seconds", "21", "Automatic");
            //test.weaponListBox();
            this.Controls.Add(test.weaponDataGrid());

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
                    //var dataTuple = ObjectInformation.obtainWeaponStats(guess);
                    //IndividualWeaponForm formChild = new IndividualWeaponForm(dataTuple.Item6, dataTuple.Item1, dataTuple.Item2, dataTuple.Item3, dataTuple.Item4, dataTuple.Item5);
                    //formChild.MdiParent = this;
                    //dynamicFlowLayoutPanel.Controls.Add(formChild);
                    //formChild.Show();
                }
                else
                {

                }
            }

            var dataTuple = ObjectInformation.obtainWeaponStats("Casey");
            IndividualWeaponForm formChild = new IndividualWeaponForm(dataTuple.Item6, dataTuple.Item7, dataTuple.Item1, dataTuple.Item2, dataTuple.Item3, dataTuple.Item4, dataTuple.Item5);
            formChild.MdiParent = this;
            dynamicFlowLayoutPanel.Controls.Add(formChild);
            formChild.Show();


        }


        /*
         * Dont Delete this Funciton
         */
        private void MainWindowForm_Load(object sender, EventArgs e)
        {

        }
    }
}
