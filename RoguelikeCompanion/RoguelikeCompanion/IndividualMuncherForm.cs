using System;
using System.Drawing;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class IndividualMuncherForm : Form
    {
        PictureBox objectImage;
        Label objectLabel;
        public IndividualMuncherForm(Image img, string objectName)
        {
            InitializeComponent();
            this.objectImage = IndividualWeaponForm.createPictureBox(img, 75, 75);
            this.objectLabel = createObjectLabel(objectName);
        }


        public Label createObjectLabel(string objectName)
        {
            // Replace objectName underscores and backslashes
            objectName = objectName.Replace("_", " ").Replace("\\", "");

            // Create a textbox for the object name
            Label displayName = new Label();
            displayName.Text = objectName;

            // Set propterties for the label
            displayName.BorderStyle = BorderStyle.None;
            displayName.Margin = new Padding(0);
            displayName.Font = new Font("Modern No. 20", 12, FontStyle.Bold);
            displayName.AutoSize = true;

            return displayName;
        }


        private void IndividualMuncherForm_Load(object sender, EventArgs e)
        {
            // Remove borders
            this.FormBorderStyle = FormBorderStyle.None;

            // Place the image object in the top left corner
            objectImage.Location = new Point(0, 75 - objectImage.Height);

            // Place the label below the image
            objectLabel.Location = new Point(0, 75);

            // Add image and textbox to form
            this.Controls.Add(objectImage);
            this.Controls.Add(objectLabel);

            // Set the form size
            int height = 75 + objectLabel.Height;
            int width = (objectLabel.Width > objectImage.Width) ? objectLabel.Width : objectImage.Width;
            this.Size = new Size(width, height);

            // Center the image
            objectImage.Location = new Point(this.Width / 2 - objectImage.Width / 2, 75 - objectImage.Height);

            // Set forground(text) and background color
            this.ForeColor = ColorTranslator.FromHtml("#000000");
            this.BackColor = Color.DeepSkyBlue;
        }
    }
}
