using System;
using System.Drawing;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class IndividualItemForm : Form
    {
        PictureBox itemImage;
        DataGridView itemData;
        int formSize;


        /*
         * Item form constructor.
         * 
         * @param img, image of the item.
         * @param itemType, type of the item either passive or active.
         * @param itemEffect, a description of what the item does
         * @param maxFormWidth, the maximum width of the form so that none of
         *      the text is cut off.
         */
        public IndividualItemForm(Image img, string itemType, string itemEffect, int maxFormWidth)
        {
            InitializeComponent();
            formSize = maxFormWidth;
            itemImage = IndividualWeaponForm.createPictureBox(img, 75, 75);
            itemData = itemDataGrid(itemType, itemEffect);
        }


        /*
         * Load the form, add item image and item DataGridView to the form. The
         * image and DGV need to be added to the form first before the forms 
         * size is set, in order to remove any grey spaces.
         */
        private void itemForm_Load(object sender, EventArgs e)
        {
            // Remove borders
            this.FormBorderStyle = FormBorderStyle.None;

            // Create an image object in the top left corner
            itemImage.Location = new Point(0, 0);

            // Place the item DataGridView to the right of the image
            int imageWidthOffset = itemImage.Width;
            itemData.Location = new Point(imageWidthOffset, 0);

            // Add image and data grid to form
            this.Controls.Add(itemImage);
            this.Controls.Add(itemData);

            // Set the form size
            int width = formSize;
            int height = (itemImage.Height > (itemData.Rows[0].Height + itemData.ColumnHeadersHeight)) ? itemImage.Height : itemData.Rows[0].Height + itemData.ColumnHeadersHeight;
            this.Size = new Size(width, height);

            // Set the datagridviev header size if needed
            if (height > itemData.Rows[0].Height + itemData.ColumnHeadersHeight)
            {
                itemData.ColumnHeadersHeight = height - itemData.Rows[0].Height;
            }          
        }


        /*
         * Creates a DataGridView control to contain a single items type and
         * effect
         * 
         * @param itemType, a string denoting if an item is passive or active.
         * @param itemEffect, a string denothing what the item does.
         * @return itemGrid, a DataGridView control dispalying the item data.
         */
        public DataGridView itemDataGrid(string itemType, string itemEffect)
        {
            // Create a new container to hold item data
            DataGridView itemGrid = new DataGridView();
            itemGrid.Width = formSize - itemImage.Width;

            // Set number of columns, names and center headers/column[0]
            itemGrid.ColumnCount = 2;
            itemGrid.Columns[0].Name = "Item Type";
            itemGrid.Columns[1].Name = "Effect";
            itemGrid.Columns[0].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;
            itemGrid.Columns[0].HeaderCell.Style.Alignment = DataGridViewContentAlignment.MiddleCenter;
            itemGrid.Columns[1].HeaderCell.Style.Alignment = DataGridViewContentAlignment.MiddleCenter;

            // Turn off sorting for columns
            itemGrid.Columns[0].SortMode = DataGridViewColumnSortMode.NotSortable;
            itemGrid.Columns[1].SortMode = DataGridViewColumnSortMode.NotSortable;

            // Populate 1 row
            itemGrid.RowCount = 1;
            itemGrid.Rows[0].SetValues(new string[] { itemType, itemEffect });

            // Add text wraping
            itemGrid.DefaultCellStyle.WrapMode = DataGridViewTriState.True;
            itemGrid.Columns[1].DefaultCellStyle.WrapMode = DataGridViewTriState.True;
            itemGrid.AutoSizeRowsMode = DataGridViewAutoSizeRowsMode.AllCellsExceptHeaders;           

            // Set the second column size
            int idealEffectColumnWidth = formSize - itemImage.Width - itemGrid.Columns[0].Width - 5;
            itemGrid.Columns[1].Width = idealEffectColumnWidth;

            // Set interactive options to off
            itemGrid.ReadOnly = true;
            itemGrid.AllowUserToOrderColumns = false;
            itemGrid.AllowUserToResizeRows = false;
            itemGrid.AllowUserToResizeColumns = false;
            itemGrid.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.DisableResizing;

            // Turn 'off' cell highlighting
            itemGrid.DefaultCellStyle.SelectionBackColor = itemGrid.DefaultCellStyle.BackColor;
            itemGrid.DefaultCellStyle.SelectionForeColor = itemGrid.DefaultCellStyle.ForeColor;

            // Turn off scrolling and row headers
            itemGrid.ScrollBars = ScrollBars.None;
            itemGrid.RowHeadersVisible = false;

            return itemGrid;
        }
    }
}
