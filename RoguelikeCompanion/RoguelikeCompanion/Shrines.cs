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
    public partial class Shrines : Form
    {
        List<Tuple<string, Bitmap, string>> shrineData;
        List<Tuple<string, string>> goodDiceEffects;
        List<Tuple<string, string>> badDiceEffects;
        TextBox effectText = new TextBox();
        public Shrines(int width, int height)
        {
            InitializeComponent();
            this.Size = new Size(width, height);
        }

        private void Shrines_Load(object sender, EventArgs e)
        {
            getShrineData();
            createPictures();
            this.BackColor = Color.DeepSkyBlue;
            this.Controls.Add(effectText);
        }


        /*
         */
        public void getShrineData()
        {
            shrineData = ObjectInformation.getShrineData();
            goodDiceEffects = ObjectInformation.getDiceEffects(true);
            badDiceEffects = ObjectInformation.getDiceEffects(false);
        }


        /*
         */
        public void createPictures()
        {
            // Obtain the size of each button
            int buttonWidth = this.Size.Width / 5;
            int buttonHeight = (this.Size.Height - 40) / 3;

            // Create, add backgrond to buttons
            int row = 0;
            int col = 0;
            for(int i = 0; i < shrineData.Count; i++)
            {
                string newPictureName = "pic" + shrineData[i].Item1;
                PictureBox newPicture = new PictureBox();
                newPicture.Size = new Size(buttonWidth, buttonHeight);
                newPicture.Name = newPictureName;
                newPicture.BackgroundImage = shrineData[i].Item2;
                newPicture.BackgroundImageLayout = ImageLayout.Stretch;
                newPicture.Location = new Point(buttonWidth * col, buttonHeight * row + 40);
                string effect = shrineData[i].Item3;
                newPicture.MouseHover += (sender, EventArgs) => { picShrine_MouseHover(sender, EventArgs, effect, newPicture); };
                this.Controls.Add(newPicture);
                col++;
                col = (col > 4) ? 0 : col;
                row = (col == 0) ? row + 1 : row;
            }
        }


        /*
         */
        void picShrine_MouseHover(object sender, EventArgs e, string shrineEffect, PictureBox newPicture)
        {
            ToolTip ToolTip1 = new ToolTip();
            ToolTip1.Show(shrineEffect, newPicture);
        }
    }
}
