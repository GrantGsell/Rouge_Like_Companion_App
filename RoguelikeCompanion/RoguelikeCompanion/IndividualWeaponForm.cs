using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
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
            this.imagePB = createPictureBox(img, 15);
            this.qualityPB = createPictureBox(qualtiy, 3);
            this.weaponData = weaponDataGrid(name, dps, reloadTime, sellPrice, gunType);
        }


        private void Form2_Load(object sender, EventArgs e)
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
            int height = imageOffset + weaponData.Height + qualityPB.Height;
            this.Size = new Size(width, height);

            // Center Images
            int centerWidthImage = (width / 2) - (imagePB.Width / 2);
            int centerWidthQuality = (width / 2) - (qualityPB.Width / 2);
            imagePB.Location = new Point(centerWidthImage, 0);
            qualityPB.Location = new Point(centerWidthQuality, qualityOffset);
        }


        /*
         */
        public static PictureBox createPictureBox(Image img, int scaleFactor)
        {
            PictureBox imageBox = new PictureBox();
            Bitmap bm = new Bitmap(img);
            int newHeight = img.Height * scaleFactor;
            int newWidth = img.Width * scaleFactor;
            bm = ScaleImage(bm, newHeight, newWidth);
            imageBox.Height = bm.Height;
            imageBox.Width = bm.Width;
            imageBox.Image = bm;

            return imageBox;
        }


        /*
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
         */
        public DataGridView weaponDataGrid(string name, string dps, string reloadTime, string sellPrice, string gunType)
        {
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
            int totalHeight = weaponGrid.ColumnHeadersHeight + weaponGrid.Rows[0].Height * (weaponGrid.Rows.Count) + 2;
            int totalWidth = weaponGrid.Columns[0].Width + weaponGrid.Columns[1].Width;
            weaponGrid.Size = new Size(totalWidth, totalHeight);

            return weaponGrid;
        }

    }
}
