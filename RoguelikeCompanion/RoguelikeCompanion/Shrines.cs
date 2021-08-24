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
        DataGridView diceNameEffects;
        Boolean showDiceEffects = false;

        /*
         */
        public Shrines(int width, int height)
        {
            InitializeComponent();
            this.Size = new Size(width, height);
        }


        /*
         */
        private void Shrines_Load(object sender, EventArgs e)
        {
            getShrineData();
            createPictures();
            this.BackColor = Color.DeepSkyBlue;
            this.Controls.Add(effectText);
            diceNameEffects.Show();
            diceNameEffects.Hide();
        }


        /*
         */
        public void getShrineData()
        {
            shrineData = ObjectInformation.getShrineData();
            goodDiceEffects = ObjectInformation.getDiceEffects(true);
            badDiceEffects = ObjectInformation.getDiceEffects(false);
            combineDiceShrineEffects(goodDiceEffects, badDiceEffects);
            btnDiceEffects.Location = new Point(this.Width - btnDiceEffects.Width, 0);
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
        private void picShrine_MouseHover(object sender, EventArgs e, string shrineEffect, PictureBox newPicture)
        {
            ToolTip toolTip = new ToolTip();
            toolTip.IsBalloon = true;
            toolTip.Show(shrineEffect, newPicture);
        }


        /*
         */
        public void combineDiceShrineEffects(List<Tuple<string, string>> goodEffects, List<Tuple<string, string>> badEffects)
        {
            // Create new DGV
            diceNameEffects = new DataGridView();

            // Set number of columns and header text
            diceNameEffects.ColumnCount = 4;
            diceNameEffects.Columns[0].HeaderText = "Good Name";
            diceNameEffects.Columns[1].HeaderText = "Good Effect";
            diceNameEffects.Columns[2].HeaderText = "Bad Name";
            diceNameEffects.Columns[3].HeaderText = "Bad Effect";

            // Set number of rows and add row data
            diceNameEffects.RowCount = goodEffects.Count;
            for(int row = 0; row < diceNameEffects.RowCount; row++)
            {
                // Obtain the current row good and bad informaiton
                string goodName = goodEffects[row].Item1;
                string goodEffect = goodEffects[row].Item2;
                string badName = badEffects[row].Item1;
                string badEffect = badEffects[row].Item2;

                // Add the data to a new row
                diceNameEffects.Rows[row].SetValues(new string[] {goodName, goodEffect, badName, badEffect});
            }

            // Remove row headers
            diceNameEffects.RowHeadersVisible = false;

            // Disable scroll bars
            diceNameEffects.ScrollBars = ScrollBars.None;

            // Add this control to the form
            this.Controls.Add(diceNameEffects);

            // Set the control to autosize
            diceNameEffects.AutoSizeRowsMode = DataGridViewAutoSizeRowsMode.AllCells;
            diceNameEffects.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.AllCells;

            // Set datagridview size
            int dgvWidth = diceNameEffects.Columns[0].Width + diceNameEffects.Columns[1].Width + diceNameEffects.Columns[2].Width + diceNameEffects.Columns[3].Width;
            int dgvHeight = diceNameEffects.Rows[0].Height * diceNameEffects.RowCount + diceNameEffects.ColumnHeadersHeight;
            diceNameEffects.Size = new Size(dgvWidth, dgvHeight);


            // Set control location
            diceNameEffects.Location = new Point(this.Width / 2 - diceNameEffects.Width / 2, this.Height / 2 - diceNameEffects.Height / 2);
        }


        /*
         */
        private void btnDiceEffects_Click(object sender, EventArgs e)
        {
            
            if (!showDiceEffects)
            {
                int newX = this.Width / 2 - diceNameEffects.Width / 2;
                int newY = this.Height / 2 - diceNameEffects.Height / 2;
                diceNameEffects.Location = new Point(newX, newY);
                diceNameEffects.Show();
            }
            else
            {
                diceNameEffects.Hide();
            }
            showDiceEffects = !showDiceEffects;
        }
    }
}
