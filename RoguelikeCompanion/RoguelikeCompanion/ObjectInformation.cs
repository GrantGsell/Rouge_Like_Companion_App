using System;
using System.Collections.Generic;
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
        public void obtainWeaponStats(string weaponName)
        {
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
                query = "SELECT object_id, object_name, dps, reload_time, sell_price, gun_type " +
                        "FROM objects " +
                        "LEFT JOIN gun_stats ON object_id = gun_id " +
                        "LEFT JOIN object_quality ON quality = quality_letter " +
                        "WHERE object_name = " + weaponName;
                MyCommand = new MySqlCommand(query, MyConnection);
                MyReader = MyCommand.ExecuteReader();
                MyReader.Read();

                //


            }
            catch (MySqlException e)
            {
                Console.WriteLine(e.Message);
            }
    }
}
