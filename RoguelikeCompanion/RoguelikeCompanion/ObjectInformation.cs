using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MySql.Data.MySqlClient;

namespace RoguelikeCompanion
{
    class ObjectInformation
    {
        /*
         */
        public static Dictionary<string, (string, bool)> createObjectNameDictionary()
        {
            Dictionary<string, (string, bool)> objectNames = null;
            try
            {
                // Create connector and reader objects
                MySqlConnection MyConnection = null;
                MySqlDataReader MyReader = null;

                // Create the SQL connection
                MyConnection = new MySqlConnection(SQLInfo.getLogin());
                MyConnection.Open();

                // Create a query string and command
                String query;
                MySqlCommand MyCommand;

                // Write and execute query
                query = "SELECT object_id, object_name, is_gun FROM objects";
                MyCommand = new MySqlCommand(query, MyConnection);
                MyReader = MyCommand.ExecuteReader();

                // Write object names into C# dictionary
                objectNames = new Dictionary<string, (string, bool)>();
                while (MyReader.Read())
                {
                    var id = MyReader.GetString(0);
                    var name = MyReader.GetString(1);
                    var isGun = MyReader.GetBoolean(2);
                    objectNames[name] = (id, isGun);
                }

                // Close connection and reader
                MyReader.Close();
                MyConnection.Close();
            }
            catch (MySqlException e)
            {
                Console.WriteLine(e.Message);
            }
            return objectNames;
        }


        /*
         */
        public static Tuple<string, string, string, string, string, Bitmap, Bitmap> obtainWeaponStats(string weaponName)
        {
            try
            {
                // Create connector and reader objects
                MySqlConnection MyConnection = null;

                // Create the SQL connection
                MyConnection = new MySqlConnection(SQLInfo.getLogin());
                MyConnection.Open();

                // Create a query string and command
                String query;
                MySqlCommand MyCommand;

                // Write and execute query
                query = "SELECT object_id, object_name, dps, reload_time, sell_price, gun_type, pic, quality_image " +
                        "FROM objects " +
                        "LEFT JOIN gun_stats ON object_id = gun_id " +
                        "LEFT JOIN object_quality ON quality = quality_letter " +
                        "WHERE object_name = \'" + weaponName + "\'";
                MyCommand = new MySqlCommand(query, MyConnection);

                // Obtain data
                MySqlDataAdapter da = new MySqlDataAdapter(MyCommand);
                DataTable table = new DataTable();
                da.Fill(table);
                string name = (string)table.Rows[0][1];
                string dps = (string)table.Rows[0][2];
                string reloadTime = (string)table.Rows[0][3];
                string sellPrice = (string)table.Rows[0][4];
                string gunType = (string)table.Rows[0][5];
                byte[] weaponRaw = (byte[])table.Rows[0][6];
                byte[] qualityRaw = (byte[])table.Rows[0][7];

                // Transform image byte data into bitmap
                MemoryStream ms = new MemoryStream(weaponRaw);
                Bitmap outImage = new Bitmap(ms);
                ms = new MemoryStream(qualityRaw);
                Bitmap outQuality = new Bitmap(ms);

                // Close connection and reader
                MyConnection.Close();

                // Put data into a tuple
                return Tuple.Create(name, dps, reloadTime, sellPrice, gunType, outImage, outQuality);
            }
            catch (MySqlException e)
            {
                Console.WriteLine(e.Message);
            }
            return null;
        }


        /*
         */
        public Tuple<string, string, string, Bitmap> obtainItemStats(string itemName)
        {
            try
            {
                // Create connector and reader objects
                MySqlConnection MyConnection = null;

                // Create the SQL connection
                MyConnection = new MySqlConnection(SQLInfo.getLogin());
                MyConnection.Open();

                // Create a query string and command
                String query;
                MySqlCommand MyCommand;

                // Write and execute query
                query = "SELECT object_id, object_name, item_type, effect, img" +
                        "FROM objects " +
                        "LEFT JOIN item_stats USING (object_id)  " +
                        "WHERE object_name = \'" + itemName + "\'";
                MyCommand = new MySqlCommand(query, MyConnection);

                // Obtain data
                MySqlDataAdapter da = new MySqlDataAdapter(MyCommand);
                DataTable table = new DataTable();
                da.Fill(table);
                string objectName = (string)table.Rows[0][1];
                string itemType = (string)table.Rows[0][2];
                string effect = (string)table.Rows[0][3];
                byte[] itemImageRaw = (byte[])table.Rows[0][3];

                // Transform image byte data into bitmap
                MemoryStream ms = new MemoryStream(itemImageRaw);
                Bitmap itemImage = new Bitmap(ms);

                // Close connection
                MyConnection.Close();

                // Put data into a tuple
                return Tuple.Create(objectName, itemType, effect, itemImage);
            }
            catch (MySqlException e)
            {
                Console.WriteLine(e.Message);
            }
            return null;
        }
    }
}
