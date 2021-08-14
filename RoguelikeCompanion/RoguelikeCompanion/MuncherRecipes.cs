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
        FlowLayoutPanel recipe1 = new FlowLayoutPanel();
        FlowLayoutPanel recipe2 = new FlowLayoutPanel();
        FlowLayoutPanel recipe3 = new FlowLayoutPanel();
        FlowLayoutPanel recipe4 = new FlowLayoutPanel();
        FlowLayoutPanel recipe5 = new FlowLayoutPanel();

        public MuncherRecipes()
        {
            InitializeComponent();
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
            string[] result5 = { "Mutation" };


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
        public void populateOneRecipeRow(List<Tuple<string[], string[], string[]>> allRecipesList)
        {
            

            // Obtain item stats from IndividualItemform class


            // Create a form for each item using IndividualSynergyForm
            

        }
    }
}
