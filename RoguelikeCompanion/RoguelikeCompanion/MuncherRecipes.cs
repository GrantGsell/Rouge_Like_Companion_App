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
    public partial class MuncherRecipes : Form
    {
        // Initialize the five MuncherRecipes
        FlowLayoutPanel[,] recipeFLPArray;
        FlowLayoutPanel test = new FlowLayoutPanel();

        public MuncherRecipes()
        {
            InitializeComponent();
            recipeFLPArray = createArrayOfFlowLayoutPanels();
            this.BackColor = ColorTranslator.FromHtml("#f47a60");
        }


        /*
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
         */
        public void populateOneRecipeRow(Tuple<string[], string[], string[]> singleRecipe, int rowNum)
        {
            // Add first column objects
            foreach (string name in singleRecipe.Item1) 
            {
                // Obtain item stats from IndividualWeaponform class
                string updatedName = name.Replace("'", "\\'");
                var weaponTuple = ObjectInformation.obtainWeaponStats(updatedName);

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
                string updatedName = name.Replace("'", "\\'");
                var weaponTuple = ObjectInformation.obtainWeaponStats(updatedName);

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
         */
        private void MuncherRecipes_Load(object sender, EventArgs e)
        {            
            this.IsMdiContainer = true;

            createArrayOfFlowLayoutPanels();

            var testTuple = allRecipesList();
            populateOneRecipeRow(testTuple[0], 0);
            this.Controls.Add(recipeFLPArray[0, 0]);
            this.Controls.Add(recipeFLPArray[0, 1]);
            this.Controls.Add(recipeFLPArray[0, 2]);

            populateOneRecipeRow(testTuple[1], 1);
            this.Controls.Add(recipeFLPArray[1, 0]);
            this.Controls.Add(recipeFLPArray[1, 1]);
            this.Controls.Add(recipeFLPArray[1, 2]);

            populateOneRecipeRow(testTuple[2], 2);
            this.Controls.Add(recipeFLPArray[2, 0]);
            this.Controls.Add(recipeFLPArray[2, 1]);
            this.Controls.Add(recipeFLPArray[2, 2]);

            populateOneRecipeRow(testTuple[3], 3);
            this.Controls.Add(recipeFLPArray[3, 0]);
            this.Controls.Add(recipeFLPArray[3, 1]);
            this.Controls.Add(recipeFLPArray[3, 2]);

            populateOneRecipeRow(testTuple[4], 4);
            this.Controls.Add(recipeFLPArray[4, 0]);
            this.Controls.Add(recipeFLPArray[4, 1]);
            this.Controls.Add(recipeFLPArray[4, 2]);

            // Obtain height, width values
            int width = recipeFLPArray[4, 0].Width + recipeFLPArray[4, 1].Width + recipeFLPArray[4, 2].Width + 50;
            int height = recipeFLPArray[4, 0].Height + recipeFLPArray[4, 0].Location.Y;
            this.Size = new Size(width, height);
        }

        
        /*
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
                        arrayFLP[row, col].Location = new Point(width * col + col * 10, 0);
                    }
                    else
                    {
                        arrayFLP[row, col].Location = new Point(width * col + col * 10, arrayFLP[row - 1, col].Height + arrayFLP[row - 1, col].Location.Y + 10);
                    }
                    arrayFLP[row, col].BackColor = ColorTranslator.FromHtml("#79CBB8");
                }
            }
            return arrayFLP;
        }


        /*
         */

    }
}
