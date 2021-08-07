using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    class WeaponObjectForm
    {
        // Class members
        string name;
        string dps;
        string reloadTime;
        string sellPrice;
        string gunType;
        

        /*
         */
        public WeaponObjectForm(string name, string dps, string reloadTime, string sellPrice, string gunType)
        {
            this.name = name;
            this.dps = dps;
            this.reloadTime = reloadTime;
            this.sellPrice = sellPrice;
            this.gunType = gunType;  
        }


        /*
         */
        public DataGridView weaponDataGrid()
        {
            // Create new container to hold data
            DataGridView weaponGrid = new DataGridView();

            // Set interactive option off
            weaponGrid.ReadOnly = true;
            weaponGrid.AllowUserToOrderColumns = false;
            weaponGrid.AllowUserToResizeRows = false;
            weaponGrid.AllowUserToResizeColumns = false;

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
            weaponGrid.Rows[0].SetValues(new string[] { "Name", this.name });
            weaponGrid.Rows[1].SetValues(new string[] { "DPS", this.dps });
            weaponGrid.Rows[2].SetValues(new string[] { "Reload Time", this.reloadTime });
            weaponGrid.Rows[3].SetValues(new string[] { "Sell Price", this.sellPrice });
            weaponGrid.Rows[4].SetValues(new string[] { "Gun Type", this.gunType });


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
