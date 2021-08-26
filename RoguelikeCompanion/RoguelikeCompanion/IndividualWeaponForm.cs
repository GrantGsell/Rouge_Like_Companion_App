using System;
using System.Drawing;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class IndividualWeaponForm : Form
    {
        // Class members;
        PictureBox imagePB;
        PictureBox qualityPB;
        DataGridView weaponData;

        public IndividualWeaponForm(Image img, Image qualtiy, string name, string dps, string reloadTime, string sellPrice, string gunType)
        {
            InitializeComponent();
            this.imagePB = createPictureBox(img, 100, 100);
            this.qualityPB = createPictureBox(qualtiy, 50, 50);
            this.weaponData = weaponDataGrid(name, dps, reloadTime, sellPrice, gunType);
        }


        /*
         * Loads the foram to contian the weapon image, weapon statistics and 
         * weapon quality image.
         */
        private void weaponForm_Load(object sender, EventArgs e)
        {
            // Remove borders
            this.FormBorderStyle = FormBorderStyle.None;
            this.BackColor = Color.Aqua;

            // Create an image object in the top left corner
            imagePB.Location = new System.Drawing.Point(0, 0);

            // Put DataGridView below image
            int imageOffset = imagePB.Height;
            weaponData.Location = new System.Drawing.Point(0, imageOffset);

            // Add the quality image below the DataGridView
            int qualityOffset = imagePB.Height + weaponData.Height;
            qualityPB.Location = new System.Drawing.Point(0, qualityOffset);

            // Add image and grid to form
            this.Controls.Add(imagePB);
            this.Controls.Add(weaponData);
            this.Controls.Add(qualityPB);

            // Set form size
            int width = weaponData.Width;
            int height = 100 + 50 + weaponData.Height;
            this.Size = new Size(width, height);

            // Center Images
            int centerWidthImage = (width / 2) - (imagePB.Width / 2);
            int centerWidthQuality = (width / 2) - (qualityPB.Width / 2);
            imagePB.Location = new Point(centerWidthImage, 0);
            qualityPB.Location = new Point(centerWidthQuality, qualityOffset);
        }


        /*
         * Creates a new PictureBox control, scales the given image and then
         * places it into the new control. The control is sized to fit the
         * scaled image size and not the rectangle specified by newWidth,
         * newHeight.
         * 
         * @param img, the image of the object.
         * @param newWidth, the maximum width of the rectangle to scale the 
         *      image into.
         * @param newHeight, the maximum height of the rectangle to scale the
         *      image into.
         * @return imageBox, a PictureBox control containing the scaled image.
         */
        public static PictureBox createPictureBox(Image img, int newWidth, int newHeight)
        {
            PictureBox imageBox = new PictureBox();

            // Transform image into a bitmap and scale it
            Bitmap bm = new Bitmap(img);
            bm = ScaleImage(bm, newHeight, newWidth);

            // Set the picture box size to fit the scaled image
            imageBox.Height = bm.Height;
            imageBox.Width = bm.Width;
            imageBox.Image = bm;

            return imageBox;
        }


        /*
         * Transforms an image from its original size into the maximum size
         * that allows the image to fit in a give rectangle of maxWidth x 
         * maxHeight.
         * 
         * @param bmp, the original image.
         * @param maxWidth, the maximum width of the rectangle to scale the
         *      image to fit into.
         * @param maxHeight, the maximum height of the rectangle to scale the
         *      image to fit into.
         * @return newImage, the original image scaled to fit into a rectangle
         *      given by the input parameters.
         */
        public static Bitmap ScaleImage(Bitmap bmp, int maxWidth, int maxHeight)
        {
            var ratioX = (double)maxWidth / bmp.Width;
            var ratioY = (double)maxHeight / bmp.Height;
            var ratio = Math.Min(ratioX, ratioY);

            var newWidth = (int)(bmp.Width * ratio);
            var newHeight = (int)(bmp.Height * ratio);

            var newImage = new Bitmap(newWidth, newHeight);

            using (var graphics = Graphics.FromImage(newImage))
                graphics.DrawImage(bmp, 0, 0, newWidth, newHeight);

            return newImage;
        }


        /*
         * Creates a DataGridView object to display the weapons stats.
         * 
         * @param name, the weapon name.
         * @param dps, the damage per second of the weapon.
         * @param reloatTime, the amount of time in seconds for the weapon to 
         *      be reloaded.
         * @param sellPrice, the amount of shells the weapon is worth if sold.
         * @param gunType, the output type of the weapon, automatic, semiauto,
         *      beam, etc.
         * @return weaponGrid, a DGV control displaying all of the input 
         *      parameters in a 5x2 grid. Column 1 contains the stat name,
         *      column 2 contains the statistic.
         */
        public DataGridView weaponDataGrid(string name, string dps, string reloadTime, string sellPrice, string gunType)
        {
            // Remove \\ and _ from names
            name = name.Replace("\\", "").Replace("_", " ");

            // Create new container to hold data
            DataGridView weaponGrid = new DataGridView();

            // Set interactive option off
            weaponGrid.ReadOnly = true;
            weaponGrid.AllowUserToOrderColumns = false;
            weaponGrid.AllowUserToResizeRows = false;
            weaponGrid.AllowUserToResizeColumns = false;
            weaponGrid.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.DisableResizing;

            // Turn 'off' cell highlighting
            weaponGrid.DefaultCellStyle.SelectionBackColor = weaponGrid.DefaultCellStyle.BackColor;
            weaponGrid.DefaultCellStyle.SelectionForeColor = weaponGrid.DefaultCellStyle.ForeColor;

            // Turn off scrolling and row headers
            weaponGrid.ScrollBars = ScrollBars.None;
            weaponGrid.RowHeadersVisible = false;

            // Set number of columns, names and center headers/column[1] data
            weaponGrid.ColumnCount = 2;
            weaponGrid.Columns[0].Name = "Property";
            weaponGrid.Columns[1].Name = "Value";
            weaponGrid.Columns[0].HeaderCell.Style.Alignment = DataGridViewContentAlignment.MiddleCenter;
            weaponGrid.Columns[1].HeaderCell.Style.Alignment = DataGridViewContentAlignment.MiddleCenter;
            weaponGrid.Columns[1].DefaultCellStyle.Alignment = DataGridViewContentAlignment.MiddleCenter;

            // Turn off sorting for columns
            weaponGrid.Columns[0].SortMode = DataGridViewColumnSortMode.NotSortable;
            weaponGrid.Columns[1].SortMode = DataGridViewColumnSortMode.NotSortable;

            // Add, populate 5 rows of data
            weaponGrid.RowCount = 5;
            weaponGrid.Rows[0].SetValues(new string[] { "Name", name });
            weaponGrid.Rows[1].SetValues(new string[] { "DPS", dps });
            weaponGrid.Rows[2].SetValues(new string[] { "Reload Time", reloadTime });
            weaponGrid.Rows[3].SetValues(new string[] { "Sell Price", sellPrice });
            weaponGrid.Rows[4].SetValues(new string[] { "Gun Type", gunType });

            // Autosize rows and columns
            weaponGrid.AutoSizeRowsMode = DataGridViewAutoSizeRowsMode.AllCells;

            // Set the container size
            int totalHeight = weaponGrid.ColumnHeadersHeight + weaponGrid.Rows[0].Height * (weaponGrid.Rows.Count - 1) + 2;
            int totalWidth = weaponGrid.Columns[0].Width + weaponGrid.Columns[1].Width;
            weaponGrid.Size = new Size(totalWidth, totalHeight);

            return weaponGrid;
        }

    }
}
