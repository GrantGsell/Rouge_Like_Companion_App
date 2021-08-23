using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.IO;
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
                        "WHERE object_name = \'" + weaponName.Replace("'", "''") + "\'";
                MyCommand = new MySqlCommand(query, MyConnection);

                // Obtain data
                MySqlDataAdapter da = new MySqlDataAdapter(MyCommand);
                DataTable table = new DataTable();
                da.Fill(table);
                string name = (string)table.Rows[0][1];
                string dps = (string)table.Rows[0][2];
                string reloadTime = (string)table.Rows[0][3];
                string sellPrice = (string)table.Rows[0][4];
                string gunType;
                gunType = (string)table.Rows[0][5];
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
        public static Tuple<string, string, string, Bitmap> obtainItemStats(string itemName)
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
                query = "SELECT object_id, object_name, item_type, effect, img " +
                        "FROM objects " +
                        "LEFT JOIN item_stats USING (object_id)  " +
                        "WHERE object_name = \'" + itemName.Replace("'", "''") + "\'";
                MyCommand = new MySqlCommand(query, MyConnection);

                // Obtain data
                MySqlDataAdapter da = new MySqlDataAdapter(MyCommand);
                DataTable table = new DataTable();
                da.Fill(table);
                string objectName = (string)table.Rows[0][1];
                string itemType = (string)table.Rows[0][2];
                string effect = (string)table.Rows[0][3];
                byte[] itemImageRaw = (byte[])table.Rows[0][4];

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


        /*
         */
        public static List<Tuple<Bitmap, string, string, bool>> obtainSynergyStats(string objectName)
        {
            List<Tuple<Bitmap, string, string, bool>> results = new List<Tuple<Bitmap, string, string, bool>>();
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
                query = "SELECT " +
                        "object_id, object_name, is_gun, " +
                        "synergy_object_name, synergy_object_text, synergy_is_gun " +
                        "FROM objects " +
                        "INNER JOIN synergies USING (object_id) " +
                        "WHERE object_name = \'" + objectName.Replace("'", "''") + "\'";
                MyCommand = new MySqlCommand(query, MyConnection);

                // Obtain all synergy data
                MyReader = MyCommand.ExecuteReader();

                // Obtain data for each synergy object
                while (MyReader.Read())
                {
                    // Obtain synergy object name and is_gun
                    string synergyObjectName = MyReader.GetString(3);
                    bool synergy_is_gun = MyReader.GetBoolean(5);

                    // Create a second connector and reader objects
                    MySqlConnection MyConnection2 = null;
                    MySqlCommand MyCommand2;

                    // Create the SQL connection
                    MyConnection2 = new MySqlConnection(SQLInfo.getLogin());
                    MyConnection2.Open();

                    // Obtain synergy object image
                    byte[] itemImageRaw;
                    if (synergy_is_gun)
                    {
                        query = "SELECT " +
                                "object_id, object_name, pic " +
                                "FROM objects " +
                                "LEFT JOIN gun_stats ON object_id = gun_id " +
                                "WHERE object_name = \'" + synergyObjectName.Replace("'", "''") + "\'";
                        MyCommand2 = new MySqlCommand(query, MyConnection2);
                        MySqlDataAdapter da = new MySqlDataAdapter(MyCommand2);
                        DataTable table = new DataTable();
                        da.Fill(table);
                        itemImageRaw = (byte[])table.Rows[0][2];
                    }
                    else
                    {
                        query = "SELECT " +
                                "object_id, object_name, img " +
                                "FROM objects " +
                                "LEFT JOIN item_stats USING (object_id) " +
                                "WHERE object_name = \'" + synergyObjectName.Replace("'", "''") + "\'";
                        MyCommand2 = new MySqlCommand(query, MyConnection2);
                        MySqlDataAdapter da = new MySqlDataAdapter(MyCommand2);
                        DataTable table = new DataTable();
                        da.Fill(table);
                        itemImageRaw = (byte[])table.Rows[0][2];
                    }

                    // Transform image byte data into bitmap
                    MemoryStream ms = new MemoryStream(itemImageRaw);
                    Bitmap itemImage = new Bitmap(ms);

                    // Close the second connection
                    MyConnection2.Close();

                    // Add data to results list
                    results.Add(Tuple.Create(itemImage, objectName, synergyObjectName, synergy_is_gun));
                }

                // Close connection
                MyConnection.Close();
            }
            catch (MySqlException e)
            {
                Console.WriteLine(e.Message);
            }

            return results;
        }


        /*
         */
        public static List<Tuple<string, Bitmap, string>> getShrineData()
        {
            List<Tuple<string, Bitmap, string>> shrineData = new List<Tuple<string, Bitmap, string>>();
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
                query = "SELECT * FROM shrines";
                MyCommand = new MySqlCommand(query, MyConnection);

                // Obtain all shrine data
                MySqlDataAdapter da = new MySqlDataAdapter(MyCommand);
                DataTable table = new DataTable();
                da.Fill(table);
                
                for(int i = 0; i < 14; i++)
                {
                    string shrineName = (string)table.Rows[i][0];
                    byte[]  itemImageRaw = (byte[])table.Rows[i][1];
                    string shrineEffect = (string)table.Rows[i][2];

                    // Convert raw image data to image
                    MemoryStream ms = new MemoryStream(itemImageRaw);
                    Bitmap shrineImage = new Bitmap(ms);

                    // Add shrine data to list
                    shrineData.Add(Tuple.Create(shrineName, shrineImage, shrineEffect));
                }
            }
            catch (MySqlException e)
            {
                Console.WriteLine(e.Message);
            }

            return shrineData;
        }


        /*
         */
        public static List<Tuple<string, string>> getDiceEffects(Boolean goodEffects)
        {
            List<Tuple<string, string>> diceEffects = new List<Tuple<string, string>>();
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
                if(goodEffects) query = "SELECT * FROM good_dice_shrine_effects";
                else query = "SELECT * FROM bad_dice_shrine_effects";
                MyCommand = new MySqlCommand(query, MyConnection);

                // Obtain all shrine data
                MySqlDataAdapter da = new MySqlDataAdapter(MyCommand);
                DataTable table = new DataTable();
                da.Fill(table);

                for (int i = 0; i < 14; i++)
                {
                    string effectName = (string)table.Rows[i][0];
                    string effectText = (string)table.Rows[i][1];

                    // Add shrine data to list
                    diceEffects.Add(Tuple.Create(effectName, effectText));
                }
            }
            catch (MySqlException e)
            {
                Console.WriteLine(e.Message);
            }

            return diceEffects;
        }

    }
}
