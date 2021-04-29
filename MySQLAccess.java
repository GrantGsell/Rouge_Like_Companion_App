import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class MySQLAccess {

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void database_connection_test() {

        try {
            Connection conn = MySQLJDBCUtil.getConnection();
            System.out.format("Connected to database %s successfully.", conn.getCatalog());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void read_from_database(String object_name){
        try {
            // Step 0. Add quotes to object name
            object_name = "\'" + object_name + "\'";

            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 3. Create the query string
            String query = "SELECT " +
                            "object_id, object_name, is_gun " +
                            "FROM objects " +
                            "WHERE object_name = " + object_name;

            // Step 4. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 5. Call the next method, bc the ResultSet is initially located before the first row
            rs.next();

            // Step 6. Obtain data from the result set
            boolean is_gun = rs.getBoolean("is_gun");

            // Obtain object data
            if(is_gun){
                obtain_gun_stats(object_name, conn, stmt);
            }
            else{
                obtain_item_stats(object_name);
            }

            // Step 7. Close the Result Set and Statement objects
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void obtain_gun_stats(String object_name, Connection conn, Statement stmt){
        try {
            // Step 1. Create the query string
            String query = "SELECT " +
                    "object_id, object_name, " +
                    "dps, reload_time, sell_price, gun_type " +
                    "FROM objects " +
                    "LEFT JOIN gun_stats ON object_id = gun_id " +
                    "WHERE object_name = " + object_name;

            // Step 2. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 3. Call the next method, bc the ResultSet is initially located before the first row
            rs.next();

            // Step 4. Obtain data from the result set
            int object_id = rs.getInt("object_id");
            String name = rs.getString("object_name");
            String dps = rs.getString("dps");
            String reload_time = rs.getString("reload_time");
            String sell_price =rs.getString("sell_price");
            String gun_type = rs.getString("gun_type");

            // Step 5. Output the found data
            System.out.format("\nName: %s\nDPS: %s\nReload Time: %s\nSell Price: %s\nGunType: %s\n",
                    name, dps, reload_time, sell_price, gun_type);

            // Obtain synergy information for the gun
            obtain_synergy_data(object_name, conn, stmt);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void obtain_item_stats(String object_name){

    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void obtain_synergy_data(String object_name, Connection conn, Statement stmt){
        try {
            // Step 1. Create the query string
            String query = "SELECT " +
                            "object_id, object_name," +
                            "synergy_object_name, synergy_object_text " +
                            "FROM objects " +
                            "INNER JOIN synergies USING (object_id)" +
                            "WHERE object_name = " + object_name;

            // Step 2. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 3. Obtain data from the result set
            System.out.println("Synergies: ");
            while(rs.next()){
                String synergy_object_name = rs.getString("synergy_object_name");
                String synergy_object_text = rs.getString("synergy_object_text");
                System.out.format("\t%s :: %s\n", synergy_object_name, synergy_object_text);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void create_parameters_mean_std_table_and_columns(int num_features){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();


            for(int i = 0; i < num_features; i++) {
                // Add columns based on the number of features
                String query = "ALTER TABLE parameters " +
                        "ADD " + "feature_" + Integer.toString(i) + " double";
                stmt.execute(query);

                // Add initial column and number of columns based on the number of features
                query = "ALTER TABLE mean " +
                        "ADD " + "feature_" + Integer.toString(i) + " double";
                stmt.execute(query);

                // Add initial column and number of columns based on the number of features

                query = "ALTER TABLE std " +
                        "ADD " + "feature_" + Integer.toString(i) + " double";
                stmt.execute(query);
            }
            // Step 5. Close the Result Set and Statement objects
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void create_constant_column_columns(ArrayList<Integer> data){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();


            for(int i = 0; i < data.size(); i++) {
                // Add columns based on the number of features
                String query = "ALTER TABLE const_cols " +
                                "ADD " + "column_" + Integer.toString(i) + " int";
                stmt.execute(query);

            }
            // Step 5. Close the Result Set and Statement objects
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void insert_const_cols_data(ArrayList<Integer> data){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            for(int i = 0; i < data.size(); i++) {
                // Step 3 Create the query data
                String query = "UPDATE const_cols" +
                        " SET column_" + Integer.toString(i) + " =" + Integer.toString(data.get(i)) +
                        " WHERE row_num = 0";

                // Step 4. Execute the query
                stmt.execute(query);
            }

            // Step 5. Close the Result Set and Statement objects
            conn.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void insert_mean_std_column_data(String table_name, int num_features, double[] data){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            for(int i = 0; i < num_features; i++) {
                // Step 3 Create the query data
                String query = "UPDATE " + table_name +
                                " SET feature_" + Integer.toString(i) + " =" + Double.toString(data[i]) +
                                " WHERE row_num = 0";

                // Step 4. Execute the query
                stmt.execute(query);
            }

            // Step 5. Close the Result Set and Statement objects
            conn.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void insert_parameter_column_data(String table_name, int num_features, double[] data, String classifier_id){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // If table_name is parameters insert classifier_id
            if(table_name.equals("parameters")){
                // Step 3 Create the query data
                String query = "INSERT INTO parameters(classifier_id) " +
                                "VALUES(\"" + classifier_id + "\")";

                // Step 4. Execute the query
                stmt.execute(query);
            }

            for(int i = 0; i < num_features; i++) {
                // Step 3 Create the query data
                String query = "UPDATE " + table_name +
                                " SET feature_" + Integer.toString(i) + " =" + Double.toString(data[i]) +
                                " WHERE classifier_id = \"" + classifier_id + "\"";

                // Step 4. Execute the query
                stmt.execute(query);
            }

            // Step 5. Close the Result Set and Statement objects
            conn.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void clear_and_initialize(){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 4. Create and Execute Queries
            /*
            Create clear queries and execute them
             */
            String query = "DROP TABLE IF EXISTS parameters";
            stmt.execute(query);
            query = "DROP TABLE IF EXISTS mean";
            stmt.execute(query);
            query = "DROP TABLE IF EXISTS std";
            stmt.execute(query);
            query = "DROP TABLE IF EXISTS const_cols";
            stmt.execute(query);

            /*
            Create queries to create the four tables
             */
            query = "CREATE TABLE IF NOT EXISTS parameters (classifier_id varchar(6) primary key)";
            stmt.execute(query);
            query = "CREATE TABLE IF NOT EXISTS mean (row_num tinyint primary key)";
            stmt.execute(query);
            query = "CREATE TABLE IF NOT EXISTS std (row_num tinyint primary key)";
            stmt.execute(query);
            query = "CREATE TABLE IF NOT EXISTS const_cols (row_num tinyint primary key)";
            stmt.execute(query);

            /*
            Initialize tables
             */
            query = "INSERT INTO mean (row_num) VALUES(0)";
            stmt.execute(query);
            query = "INSERT INTO std (row_num) VALUES(0)";
            stmt.execute(query);
            query = "INSERT INTO const_cols (row_num) VALUES(0)";
            stmt.execute(query);

            // Step 5. Close the Result Set and Statement objects
            conn.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void read_mean_std_const_cols(int num_features, double[] mean, double[] std, ArrayList<Integer> const_cols){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 3/4. Create queries to obtain data and execute them
            /*
            Obtain mean data
             */
            String query = "Select * FROM mean";
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            for(int i = 2; i < num_features + 2; i++) {
                mean[i-2] = rs.getDouble(i);
            }

            /*
            Obtain std data
             */
            query = "Select * FROM std";
            rs = stmt.executeQuery(query);
            rs.next();
            for(int i = 2; i < num_features + 2; i++) {
                std[i-2] = rs.getDouble(i);
            }

            /*
            Obtain const_cols data
             */
            // Obtain the number of columns in the const_cols table
            query = "SELECT COUNT(*) AS NUMBEROFCOLUMNS FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE table_schema = 'roguelike_companion' AND table_name = 'const_cols'";
            rs = stmt.executeQuery(query);
            rs.next();
            int num_cols = rs.getInt(1);

            // Obtain the data from the cost_col table
            query = "Select * FROM const_cols";
            rs = stmt.executeQuery(query);
            rs.next();
            for(int i = 2; i < num_cols + 2; i++) {
                int temp = rs.getInt(i);
                const_cols.add(temp);
            }

            // Step 5. Close the Result Set and Statement objects
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void read_parameter_cols(){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 3/4. Create queries to obtain data and execute them


            // Step 5. Close the Result Set and Statement objects
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String []args){
        //MySQLAccess.database_connection_test();
        //String test = "Rusty_Sidearm";
        //MySQLAccess.read_from_database(test);
        clear_and_initialize();
        create_parameters_mean_std_table_and_columns(810);
        double[] data = {0.1, 0.2, 0.3};
        ArrayList<Integer> data_2 = new ArrayList<>();
        data_2.add(1);
        data_2.add(2);
        data_2.add(3);
        insert_mean_std_column_data("mean", 3, data);
        insert_mean_std_column_data("std", 3, data);
        insert_parameter_column_data("parameters", 3, data, "A");
        create_constant_column_columns(data_2);
        insert_const_cols_data(data_2);
    }
}
