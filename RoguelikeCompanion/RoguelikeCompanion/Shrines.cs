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
            createButtons();
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
        public void createButtons()
        {
            // Obtain the size of each button
            int buttonWidth = this.Size.Width / 5;
            int buttonHeight = this.Size.Height / 3;

            // Create, add backgrond to buttons
            int row = 0;
            int col = 0;
            for(int i = 0; i < shrineData.Count; i++)
            {
                string newButtonName = "btn" + shrineData[i].Item1;
                Button newButton = new Button();
                newButton.Size = new Size(buttonWidth, buttonHeight);
                newButton.Name = newButtonName;
                newButton.BackgroundImage = shrineData[i].Item2;
                newButton.BackgroundImageLayout = ImageLayout.Stretch;
                newButton.Location = new Point(buttonWidth * col, buttonHeight * row);
                string effect = shrineData[i].Item3;
                newButton.MouseHover += (sender, EventArgs) => { btnShrine_MouseHover(sender, EventArgs, effect, newButton); };
                this.Controls.Add(newButton);
                col++;
                col = (col > 4) ? 0 : col;
                row = (col == 0) ? row + 1 : row;
            }
        }


        /*
         */
        void btnShrine_MouseHover(object sender, EventArgs e, string shrineEffect, Button newButton)
        {
            ToolTip ToolTip1 = new ToolTip();
            //ToolTip1.SetToolTip(newButton, "TEST");
            ToolTip1.Show(shrineEffect, newButton);
        }
    }
}
