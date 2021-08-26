using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

namespace RoguelikeCompanion
{
    public partial class MuncherRecipes : Form
    {
        // Initialize the five MuncherRecipes
        FlowLayoutPanel[,] recipeFLPArray;

        /*
         * Constructor that creates the array containing the muncher recipe 
         * data.
         */
        public MuncherRecipes()
        {
            InitializeComponent();
            recipeFLPArray = createArrayOfFlowLayoutPanels();
        }


        /*
         * Manually creates a list of Tuples containing the muncher recipes.
         *      item1: Array of weapon 1's
         *      item2: Array of weapon 2's
         *      item3: One element array of the result(weapon 1's + weapon 2's)
         * 
         * @return allRecipes, each tuple contains one of the five muncher 
         *      recipes.
         */
        public List<Tuple<string[], string[], string[]>> allRecipesList()
        {
            List<Tuple<string[], string[], string[]>> allRecipes = new List<Tuple<string[], string[], string[]>>();

            // Muncher Recipe 1
            string[] recipe1Weapons1 = { "38_Special", "Colt_1851", "Devolver", "Magnum", "SAA", "Smiley's_Revolver", "Shades's_Revolver" };
            string[] recipe1Weapons2 = { "Crestfaller", "Freeze_Ray", "Frost_Giant", "Glacier", "Ice_Breaker" };
            string[] result1 = { "Cold_45" };

            // Muncher Recipe 2
            string[] recipe2Weapons1 = { "Crossbow", "Shotbow", "Triple_Crossbow" };
            string[] recipe2Weapons2 = { "Hexagun", "Staff_of_Firepower", "Unicorn_Horn", "Witch_Pistol" };
            string[] result2 = { "Crescent_Crossbow" };

            // Muncher Recipe 3
            string[] recipe3Weapons1 = { "Flame_Hand", "Megahand" };
            string[] recipe3Weapons2 = { "AK-47", "M16", "MAC10", "Machine_Pistol" };
            string[] result3 = { "Machine_Fist" };

            // Muncher Recipe 4
            string[] recipe4Weapons1 = { "Flame_Hand", "Megahand", "Machine_Fist" };
            string[] recipe4Weapons2 = { "Gamma_Ray", "Plague_Pistol", "Plunger", "Rattler", "Shotgrub"};
            string[] result4 = { "Mutation" };

            // Muncher Recipe 5
            string[] recipe5Weapons1 = { "Bee_Hive"};
            string[] recipe5Weapons2 = { "RPG"};
            string[] result5 = { "Stinger" };

            // Add each recipe to a tuple and then to the allRecipes list
            allRecipes.Add(Tuple.Create(recipe1Weapons1, recipe1Weapons2, result1));
            allRecipes.Add(Tuple.Create(recipe2Weapons1, recipe2Weapons2, result2));
            allRecipes.Add(Tuple.Create(recipe3Weapons1, recipe3Weapons2, result3));
            allRecipes.Add(Tuple.Create(recipe4Weapons1, recipe4Weapons2, result4));
            allRecipes.Add(Tuple.Create(recipe5Weapons1, recipe5Weapons2, result5));

            return allRecipes;
        }


        /*
         * Adds each weapon from one recipe into one the three respective 
         * FlowLayoutPanels.
         * 
         * @param singleRecipe, a Tuple with an array of weapon names for each
         *      of the three columns in a single row.
         * @param rowNum, the number denoting the row of FLPs that these
         *      weapons should be added to.
         */
        public void populateOneRecipeRow(Tuple<string[], string[], string[]> singleRecipe, int rowNum)
        {
            // Add first column objects
            foreach (string name in singleRecipe.Item1) 
            {
                // Obtain weapon image
                var weaponTuple = ObjectInformation.obtainWeaponStats(name);

                // Create a form for each item using IndividualSynergyForm
                IndividualMuncherForm newForm = new IndividualMuncherForm(weaponTuple.Item6, weaponTuple.Item1);
                newForm.MdiParent = this;

                // Add the form to the correct FLP
                recipeFLPArray[rowNum, 0].Controls.Add(newForm);
                newForm.Show();
            }

            // Add second column objects
            foreach (string name in singleRecipe.Item2)
            {
                // Obtain item stats from IndividualWeaponform class
                var weaponTuple = ObjectInformation.obtainWeaponStats(name);

                // Create a form for each item using IndividualSynergyForm
                IndividualMuncherForm newForm = new IndividualMuncherForm(weaponTuple.Item6, weaponTuple.Item1);
                newForm.MdiParent = this;
                

                // Add the form to the correct FLP
                recipeFLPArray[rowNum, 1].Controls.Add(newForm);
                newForm.Show();
            }

            // Add result column object
            string resultName = singleRecipe.Item3[0];

            // Obtain item stats from IndividualWeaponform class
            var weaponResultTuple = ObjectInformation.obtainWeaponStats(resultName);

            // Create a form for each item using IndividualSynergyForm
            IndividualMuncherForm newFormResult = new IndividualMuncherForm(weaponResultTuple.Item6, weaponResultTuple.Item1);
            newFormResult.MdiParent = this;

            // Add the form to the correct FLP
            recipeFLPArray[rowNum, 2].Width = 180;
            recipeFLPArray[rowNum, 2].Controls.Add(newFormResult);
            newFormResult.Show();
        }


        /*
         * Creates the array of FLPs, populates each FLP with the correct data,
         * sets the title bar with labels, and sizes the form to show all FLPs
         * without cutting off any of the data.
         */
        private void MuncherRecipes_Load(object sender, EventArgs e)
        {            
            this.IsMdiContainer = true;

            // Create an array of FLPS
            createArrayOfFlowLayoutPanels();

            // Add all recipes to their respective FLPs
            populateAllFLPs();

            // Set form properties
            this.FormBorderStyle = FormBorderStyle.None;

            // Set titlebar size and location
            setTitleBarsAndLabels();

            // Obtain height, width values
            int width = recipeFLPArray[4, 0].Width + recipeFLPArray[4, 1].Width + recipeFLPArray[4, 2].Width + 30;
            int height = recipeFLPArray[4, 0].Height + recipeFLPArray[4, 0].Location.Y + 10;
            this.Size = new Size(width, height);
        }

        
        /*
         * Creates a 2D array of FLPs and sizes each FLP so that all of the 
         * FLPs are placed correctly within this form.
         * 
         * @return arrayFLP, containing the correctly placed 15 FLPs in a 
         *      5 x 3 grid.
         */
        public FlowLayoutPanel[,] createArrayOfFlowLayoutPanels()
        {
            FlowLayoutPanel[,] arrayFLP = new FlowLayoutPanel[5, 3];
            for(int row = 0; row < 5; row++)
            {
                for(int col = 0; col < 3; col++)
                {
                    // Create new FLP
                    arrayFLP[row, col] = new FlowLayoutPanel();

                    // Set Size for each FLP
                    int width = 600;
                    int height = (row > 0) ? 105 : 210;
                    arrayFLP[row, col].Size = new Size(width, height);

                    // Set location for each FLP
                    if (row == 0)
                    {
                        arrayFLP[row, col].Location = new Point(width * col + col * 10, titleBar0.Height);
                    }
                    else
                    {
                        arrayFLP[row, col].Location = new Point(width * col + col * 10, arrayFLP[row - 1, col].Height + arrayFLP[row - 1, col].Location.Y + 10);
                    }
                    arrayFLP[row, col].BackColor = Color.DeepSkyBlue;
                }
            }
            return arrayFLP;
        }


        /*
         * Adds the Muncher Recipes to each of their respective 
         * FlowLayoutPanels.
         */
        public void populateAllFLPs()
        {
            var recipeTuples = allRecipesList();

            for(int row = 0; row < 5; row++)
            {
                populateOneRecipeRow(recipeTuples[row], row);
                for(int col = 0; col < 3; col++)
                {
                    this.Controls.Add(recipeFLPArray[row, col]);
                }
            }
        }


        /*
         * Changes the properties of the title bars panels placed in the 
         * designer, adds labels to each of the panels and sets the properties
         * for both sets of controls.
         */
        public void setTitleBarsAndLabels()
        {
            // Set titlebar locations
            titleBar0.Location = new Point(0, 0);
            titleBar1.Location = new Point(recipeFLPArray[0, 1].Location.X, 0);
            titleBar2.Location = new Point(recipeFLPArray[0, 2].Location.X, 0);

            // Set titlebar colors
            titleBar0.BackColor = Color.Gold;
            titleBar1.BackColor = Color.Gold;
            titleBar2.BackColor = Color.Gold;

            // Set title bar widths
            titleBar0.Width = recipeFLPArray[0, 0].Width;
            titleBar1.Width = recipeFLPArray[0, 1].Width;
            titleBar2.Width = recipeFLPArray[0, 2].Width;

            // Create labels for the titlebars
            Label titleBar0Label = new Label();
            Label titleBar1Label = new Label();
            Label titleBar2Label = new Label();

            // Add text to the labels
            titleBar0Label.Text = "Weapon 1";
            titleBar1Label.Text = "Weapon 2";
            titleBar2Label.Text = "Result";

            // Set text label fonts
            titleBar0Label.Font = new Font("Modern No. 20", 16, FontStyle.Bold);
            titleBar1Label.Font = new Font("Modern No. 20", 16, FontStyle.Bold);
            titleBar2Label.Font = new Font("Modern No. 20", 16, FontStyle.Bold);

            // Set label sizes
            titleBar0Label.AutoSize = true;
            titleBar1Label.AutoSize = true;
            titleBar2Label.AutoSize = true;

            // Set label locations
            titleBar0Label.Location = new Point(titleBar0.Location.X + titleBar0.Width / 2 - titleBar0Label.Width / 2, 0);
            titleBar1Label.Location = new Point(titleBar1.Location.X + titleBar1.Width / 2 - titleBar1Label.Width / 2, 0);
            titleBar2Label.Location = new Point(titleBar2.Location.X + titleBar2.Width / 2 - titleBar2Label.Width / 2, 0);

            // Set text color
            titleBar0Label.ForeColor = Color.Black;
            titleBar1Label.ForeColor = Color.Black;
            titleBar2Label.ForeColor = Color.Black;

            // Set label background colors
            titleBar0Label.BackColor = titleBar0.BackColor;
            titleBar1Label.BackColor = titleBar1.BackColor;
            titleBar2Label.BackColor = titleBar2.BackColor;

            // Add labels to the form
            this.Controls.Add(titleBar0Label);
            this.Controls.Add(titleBar1Label);
            this.Controls.Add(titleBar2Label);
                    
            // Bring all text labels to the front
            titleBar0Label.BringToFront();
            titleBar1Label.BringToFront();
            titleBar2Label.BringToFront();
        }

    }
}
