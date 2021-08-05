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
    public partial class IndividualItemForm : Form
    {
        PictureBox itemImage;


        public IndividualItemForm()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }


        /*
         */
        public DataGridView itemDataGrid(string itemType, string itemEffect)
        {
            // Create a new container to hold item data
            DataGridView itemGrid = new DataGridView();

            // Turn off scrolling and row headers
            itemGrid.ScrollBars = ScrollBars.None;
            itemGrid.RowHeadersVisible = false;

            // Set number of columns, names and center headers/column[0]
            itemGrid.ColumnCount = 2;
            itemGrid.Columns[0].Name = "Item Type";
            itemGrid.Columns[1].Name = "Effect";
            itemGrid.Columns[0].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            itemGrid.Columns[0].HeaderCell.Style.Alignment = DataGridViewContentAlignment.MiddleCenter;
            itemGrid.Columns[1].HeaderCell.Style.Alignment = DataGridViewContentAlignment.MiddleCenter;

            // Populate 1 row
            itemGrid.RowCount = 1;
            itemGrid.Rows[0].SetValues(new string[] { itemType, itemEffect });

            // Autosize rows and columns
            itemGrid.AutoSizeRowsMode = DataGridViewAutoSizeRowsMode.AllCells;

            // Set the container size
            int totalHeight = itemGrid.ColumnHeadersHeight + itemGrid.Rows[0].Height + 5;
            int totalWidth = itemGrid.Columns[0].Width + itemGrid.Columns[1].Width;
            itemGrid.Size = new Size(totalWidth, totalHeight);

            return itemGrid;
        }


    }
}
