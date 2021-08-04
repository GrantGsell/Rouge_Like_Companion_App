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
        public ListView weaponListView()
        {
            // Create new ListView object
            ListView weaponList = new ListView();

            // Add grid lines, remove clickable headers, remove scroll
            weaponList.GridLines = true;
            weaponList.HeaderStyle = ColumnHeaderStyle.Nonclickable;
            weaponList.Scrollable = false;

            // Set Name, size and location
            weaponList.Name = this.name + "ListView";
            weaponList.Location = new System.Drawing.Point(890, 73);

            // Populate ListView with weapon data
            weaponData(weaponList);

            // Autosize ListView columns
            weaponList.AutoResizeColumns(ColumnHeaderAutoResizeStyle.ColumnContent);
            weaponList.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);

            // Generate optimal width/height
            int totalWidth = weaponList.Columns[0].Width + weaponList.Columns[1].Width;

            int itemHeight = weaponList.Items.OfType<ListViewItem>().First().Bounds.Height;
            int itemCount = weaponList.Items.Count;
            int headerOffset = weaponList.TopItem.Bounds.Top;
            int totalHeight = itemCount * itemHeight + headerOffset;

            // Autosize Listview Height
            weaponList.Size = new Size(totalWidth, totalHeight);

            return weaponList;
        }


        /*
         */
        public void weaponData(ListView newBox)
        {
            // Set ListView into Details mode
            newBox.View = View.Details;

            // Declare, Constrct ColumnHeader objects
            ColumnHeader header1, header2;
            header1 = new ColumnHeader();
            header2 = new ColumnHeader();

            // Set header text, font and alignment
            header1.Text = "Property";
            header1.TextAlign = HorizontalAlignment.Center;

            header2.Text = "Values";
            header2.TextAlign = HorizontalAlignment.Center;

            // Set header font
            newBox.Font = new Font("Consolas", 12);

            // Create a two column list
            newBox.Columns.Add(header1);
            newBox.Columns.Add(header2);

            // Add, populate 5 rows of data
            newBox.Items.Add(new ListViewItem(new string[] { "Name", this.name }));
            newBox.Items.Add(new ListViewItem(new string[] { "DPS", this.dps }));
            newBox.Items.Add(new ListViewItem(new string[] { "Reload Time", this.reloadTime }));
            newBox.Items.Add(new ListViewItem(new string[] { "Sell Price", this.sellPrice }));
            newBox.Items.Add(new ListViewItem(new string[] { "Gun Type", this.gunType }));

            // Set font size for the row data
            foreach (ListViewItem lvi in newBox.Items)
            {
                lvi.Font = new Font("Consolas", 11);
            }
        }


        /*
         */
        public DataGridView weaponDataGrid()
        {
            // Create new container to hold data
            DataGridView weaponGrid = new DataGridView();

            // Set grid location
            weaponGrid.Location = new System.Drawing.Point(890, 400);

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

            // Add, populate 5 rows of data
            weaponGrid.RowCount = 5;
            weaponGrid.Rows[0].SetValues(new string[] { "Name", this.name });
            weaponGrid.Rows[1].SetValues(new string[] { "DPS", this.dps });
            weaponGrid.Rows[2].SetValues(new string[] { "Reload Time", this.reloadTime });
            weaponGrid.Rows[3].SetValues(new string[] { "Sell Price", this.sellPrice });
            weaponGrid.Rows[4].SetValues(new string[] { "Gun Type", this.gunType });

            // Autosize rows and columns
            //weaponGrid.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.AllCells;
            weaponGrid.AutoSizeRowsMode = DataGridViewAutoSizeRowsMode.AllCells;

            // Set the container size
            int totalHeight = weaponGrid.ColumnHeadersHeight + weaponGrid.Rows[0].Height * (weaponGrid.Rows.Count - 1) + 5;
            int totalWidth = weaponGrid.Columns[0].Width + weaponGrid.Columns[1].Width;
            weaponGrid.Size = new Size(totalWidth, totalHeight);

            return weaponGrid;
        }

    }
}
