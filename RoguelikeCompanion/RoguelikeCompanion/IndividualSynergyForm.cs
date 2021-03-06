using System;
using System.Drawing;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class IndividualSynergyForm : Form
    {
        PictureBox synergyImage;
        DataGridView synergyData;

        /*
         * Constructor that creates the PictureBox and DataGridView controls
         * for this form.
         * 
         * @param img, the image of the synergy object.
         * @param synergizesWith, the name of the object held by the user, that
         *      the synergy object synergizes with.
         */
        public IndividualSynergyForm(Image img, string synergizesWith)
        {
            InitializeComponent();
            this.synergyData = synergyDataGrid(synergizesWith);
            this.synergyImage = IndividualWeaponForm.createPictureBox(img, 75, 75);

            // Remove borders, set double buffer
            this.FormBorderStyle = FormBorderStyle.None;
        }


        /*
         * Loads this form with the synergy objects image and a DGV specifying 
         * what object it synergizes with.
         */
        private void IndividualSynergyForm_Load(object sender, EventArgs e)
        {
            // Create an image object in the top left corner
            synergyImage.Location = new Point(0, 75 - synergyImage.Height);

            // Place the synergy DataGridView object below the image
            int imageHeightOffset = synergyImage.Height;
            synergyData.Location = new Point(0, (75 - synergyImage.Height) + imageHeightOffset);

            // Set form size
            int height = synergyData.Height + 75;
            int width = (synergyData.Width > synergyImage.Width) ? synergyData.Width : synergyImage.Width;
            this.Size = new Size(width, height);

            // Center image
            int updatedImageWidth = Math.Abs(synergyData.Width / 2 - synergyImage.Width / 2);
            synergyImage.Location = new Point(updatedImageWidth, 75 - synergyImage.Height);

            // Add image and data grid to the form
            this.Controls.Add(synergyImage);
            this.Controls.Add(synergyData);
        }


        /*
         * Creates a DataGridView control to contain the name of a single
         * object that this synergy object synergizes with.
         * 
         * @param synergizesWith, the name of the object that this synergy
         *      object synergizes with. This object is held by the user.
         * @return synergyGrid, a 2x1 DGV, Row 1 contains the text "Synergizes
         *      With", Row 2 contains the name specified by synergizesWith.
         */
        public DataGridView synergyDataGrid(string synergizesWith)
        {
            // Remove \\ and _ from synergizesWith objet name
            synergizesWith = synergizesWith.Replace("\\", "").Replace("_", " ");

            // Create a new container to hold item data
            DataGridView synergyGrid = new DataGridView();

            // Set interactive options to off
            synergyGrid.ReadOnly = true;
            synergyGrid.AllowUserToOrderColumns = false;
            synergyGrid.AllowUserToResizeRows = false;
            synergyGrid.AllowUserToResizeColumns = false;
            synergyGrid.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.DisableResizing;
            synergyGrid.ColumnHeadersDefaultCellStyle.WrapMode = DataGridViewTriState.False;

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
            synergyGrid.Columns[0].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            synergyGrid.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;

            // Turn off sorting for columns
            synergyGrid.Columns[0].SortMode = DataGridViewColumnSortMode.NotSortable;

            // Populate 1 row
            synergyGrid.RowCount = 1;
            synergyGrid.Rows[0].SetValues(new string[] { synergizesWith });

            // Set DataGridView size
            int height = synergyGrid.ColumnHeadersHeight + synergyGrid.Rows[0].Height;
            synergyGrid.Size = new Size( synergyGrid.Width, height);

            return synergyGrid;
        }
    }
}
