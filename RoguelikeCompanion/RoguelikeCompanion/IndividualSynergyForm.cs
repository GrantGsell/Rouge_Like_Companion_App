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
    public partial class IndividualSynergyForm : Form
    {
        PictureBox synergyImage;
        DataGridView synergyData;

        public IndividualSynergyForm(Image img, string synergizesWith)
        {
            InitializeComponent();
            this.synergyData = synergyDataGrid(synergizesWith);
            synergyImage = IndividualWeaponForm.createPictureBox(img, 10);
        }


        /*
         */
        public DataGridView synergyDataGrid(string synergizesWith)
        {
            // Create a new container to hold item data
            DataGridView synergyGrid = new DataGridView();

            // Set interactive options to off
            synergyGrid.ReadOnly = true;
            synergyGrid.AllowUserToOrderColumns = false;
            synergyGrid.AllowUserToResizeRows = false;
            synergyGrid.AllowUserToResizeColumns = false;
            synergyGrid.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.DisableResizing;

            // Turn 'off' cell highlighting
            synergyGrid.DefaultCellStyle.SelectionBackColor = synergyGrid.DefaultCellStyle.BackColor;
            synergyGrid.DefaultCellStyle.SelectionForeColor = synergyGrid.DefaultCellStyle.ForeColor;

            // Turn off scrolling and row headers
            synergyGrid.ScrollBars = ScrollBars.None;
            synergyGrid.RowHeadersVisible = false;

            // Set number of columns, names and center headers/column[0]
            synergyGrid.ColumnCount = 1;
            synergyGrid.Columns[0].Name = "Synergizes With:";
            synergyGrid.Columns[0].HeaderCell.Style.Alignment = DataGridViewContentAlignment.MiddleCenter;

            // Turn off sorting for columns
            synergyGrid.Columns[0].SortMode = DataGridViewColumnSortMode.NotSortable;

            // Populate 1 row
            synergyGrid.RowCount = 1;
            synergyGrid.Rows[0].SetValues(new string[] { synergizesWith });

            // Set DataGridView size
            synergyGrid.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.AllCells;


            return synergyGrid;
        }

        private void IndividualSynergyForm_Load(object sender, EventArgs e)
        {
            // Remove borders
            this.FormBorderStyle = FormBorderStyle.None;

            // Create an image object in the top left corner
            synergyImage.Location = new System.Drawing.Point(0, 0);

            // Place the synergy DataGridView object below the image
            int imageHeightOffset = synergyImage.Height;
            synergyData.Location = new Point(0, imageHeightOffset);

            // Add image and data grid to the form
            this.Controls.Add(synergyImage);
            this.Controls.Add(synergyData);

            // Set form size
            int height = synergyData.Height + synergyImage.Height;
            int width = (synergyData.Width > synergyImage.Width) ? synergyData.Width : synergyImage.Width;
            this.Size = new Size(width, height);
        }
    }
}
