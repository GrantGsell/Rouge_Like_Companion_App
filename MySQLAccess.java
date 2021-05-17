import org.ejml.simple.SimpleMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

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
    public static void read_from_database(String object_name, Hashtable<Integer, ArrayList<String>> words_ht, UserInterface ui){
        try {
            // Step 0. Add quotes to object name
            String object_name_sql = "\'" + object_name + "\'";

            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Check to see if the word is not in the objects table
            if(!word_exists(object_name, stmt)){
                System.out.println("\nWord Not Found: Performing Correction...\n");
                object_name = find_closest_word(object_name, words_ht);
                object_name_sql = "\'" + object_name + "\'";
            }

            // Check to see if the object has already been processed, if true return else add to arraylist
            if(ui.processed_objects.contains(object_name_sql)){
                conn.close();
                stmt.close();
                return;
            }else{
                ui.processed_objects.add(object_name_sql);
            }

            // Step 3. Create the query string
            String query = "SELECT " +
                            "object_id, object_name, is_gun " +
                            "FROM objects " +
                            "WHERE object_name = " + object_name_sql;

            // Step 4. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 5. Call the next method, bc the ResultSet is initially located before the first row
            rs.next();

            // Step 6. Obtain data from the result set
            boolean is_gun = rs.getBoolean("is_gun");

            // Obtain object data
            if(is_gun){
                ui.add_weapon(object_name_sql, ui.weapon_list);
                ui.add_synergy(object_name_sql, ui.synergy_list);
                //obtain_gun_stats(object_name_sql);
            }
            else{
                ui.add_item(object_name_sql, ui.item_list);
                //obtain_item_stats(object_name_sql);
            }
            ui.update_frame();

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
    public static List<Object> obtain_gun_stats(String object_name){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 3. Create the query string
            String query = "SELECT " +
                    "object_id, object_name, " +
                    "dps, reload_time, sell_price, gun_type, pic, quality, " +
                    "quality_letter, quality_image " +
                    "FROM objects " +
                    "LEFT JOIN gun_stats ON object_id = gun_id " +
                    "LEFT JOIN object_quality ON quality = quality_letter " +
                    "WHERE object_name = " + object_name;

            // Step 4. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 5. Call the next method, bc the ResultSet is initially located before the first row
            rs.next();

            // Step 6. Obtain data from the result set
            int object_id = rs.getInt("object_id");
            String name = rs.getString("object_name");
            String dps = rs.getString("dps");
            String reload_time = rs.getString("reload_time");
            String sell_price =rs.getString("sell_price");
            String gun_type = rs.getString("gun_type");
            Blob gun_img_blob = rs.getBlob("pic");
            Blob quality = rs.getBlob("quality_image");

            // Transform Blob data into BufferedFrames
            InputStream in = gun_img_blob.getBinaryStream();
            BufferedImage gun_img = ImageIO.read(in);
            BufferedImage quality_img = null;
            if(quality != null) {
                in = quality.getBinaryStream();
                quality_img = ImageIO.read(in);
            }

            // Step 7. Output the found data
            //System.out.format("\nName: %s\nDPS: %s\nReload Time: %s\nSell Price: %s\nGunType: %s\n",
                    //name, dps, reload_time, sell_price, gun_type);

            // Obtain synergy information for the gun
            //List<Object> synergy_data = obtain_synergy_data(object_name);

            // Step 8. Close the Result Set and Statement objects
            rs.close();
            stmt.close();

            // Step 9. Return object data
            return Arrays.asList(name, dps, reload_time, sell_price, gun_type, gun_img, quality_img);

        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static List<Object> obtain_item_stats(String object_name){
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 1. Create the query string
            String query = "SELECT " +
                    "object_id, object_name, " +
                    "item_type, quality, effect, img, " +
                    "quality_letter, quality_image " +
                    "FROM objects " +
                    "LEFT JOIN item_stats USING (object_id) " +
                    "LEFT JOIN object_quality ON quality = quality_letter " +
                    "WHERE object_name = " + object_name;

            // Step 2. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 3. Call the next method, bc the ResultSet is initially located before the first row
            rs.next();

            // Step 4. Obtain data from the result set
            String item_name = rs.getString("object_name");
            String item_type = rs.getString("item_type");
            String effect = rs.getString("effect");
            String quality = rs.getString("quality_letter");
            Blob item_img_blob = rs.getBlob("img");
            Blob quality_img_blob = rs.getBlob("quality_image");

            // Transform blob data into images
            InputStream in = item_img_blob.getBinaryStream();
            BufferedImage item_img = ImageIO.read(in);
            BufferedImage quality_img = null;
            if(quality != null) {
                in = quality_img_blob.getBinaryStream();
                quality_img = ImageIO.read(in);
            }

            // Step 5. Output the found data
            //System.out.format("\tItem Name: %s\n\tItem Type: %s\n\tEffect: %s\n\tQuality: %s\n",
                                //item_name, item_type, effect, quality);

            return Arrays.asList(item_img, effect, item_type);
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static List<Object> obtain_synergy_data(String object_name){
        List<Object> ret = new ArrayList<Object>();
        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 3. Create the query string
            String query = "SELECT " +
                            "object_id, object_name, is_gun, " +
                            "synergy_object_name, synergy_object_text " +
                            "FROM objects " +
                            "INNER JOIN synergies USING (object_id)" +
                            "WHERE object_name = " + object_name;

            // Step 4. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 5. Obtain data from the result set
            //System.out.println("Synergies: ");
            while(rs.next()){
                String synergy_object_name = rs.getString("synergy_object_name");
                String synergy_object_text = rs.getString("synergy_object_text");
                //System.out.format("\t%s :: %s\n", synergy_object_name, synergy_object_text);

                // Create new connection and statement for is_gun query on each synergy object
                Connection conn_3 = MySQLJDBCUtil.getConnection();
                Statement stmt_3 = conn_3.createStatement();

                // Find out if the synergy_onject is a gun or not
                String query_3 = "SELECT " +
                                    "object_name, is_gun " +
                                    "FROM objects " +
                                    "WHERE object_name = \'" + synergy_object_name + "\'";
                ResultSet rs_3 = stmt_3.executeQuery(query_3);
                rs_3.next();
                boolean is_gun = rs_3.getBoolean("is_gun");
                rs_3.close();

                // Obtain synergy object blob image
                Blob object_img_blob = null;
                if(is_gun){
                    // Step 1. Open a new connection to the database
                    Connection conn_2 = MySQLJDBCUtil.getConnection();

                    // Step 2. Create a Statement object
                    Statement stmt_2 = conn_2.createStatement();

                    String query_2 = "SELECT " +
                                        "object_id, object_name," +
                                        "pic " +
                                        "FROM objects " +
                                        "LEFT JOIN  gun_stats ON object_id = gun_id " +
                                        "WHERE object_name = \'" + synergy_object_name + "\'";
                    ResultSet rs_2 = stmt_2.executeQuery(query_2);
                    rs_2.next();
                    object_img_blob = rs_2.getBlob("pic");
                    rs_2.close();
                    stmt_2.close();
                    conn_2.close();
                }
                else {
                    // Step 1. Open a new connection to the database
                    Connection conn_2 = MySQLJDBCUtil.getConnection();

                    // Step 2. Create a Statement object
                    Statement stmt_2 = conn_2.createStatement();
                    String query_2 = "SELECT " +
                                        "object_id, object_name," +
                                        "img " +
                                        "FROM objects " +
                                        "LEFT JOIN  item_stats USING (object_id) " +
                                        "WHERE object_name = \'" + synergy_object_name + "\'";
                    ResultSet rs_2 = stmt_2.executeQuery(query_2);
                    rs_2.next();
                    object_img_blob = rs_2.getBlob("img");
                    rs_2.close();
                    stmt_2.close();
                    conn_2.close();
                }

                // Transform Blob image into BufferedImage
                InputStream in = object_img_blob.getBinaryStream();
                BufferedImage syn_object_img = ImageIO.read(in);
                List<Object> temp = new ArrayList<>();

                // Add the three objects to a temp list
                temp.add(synergy_object_name);
                temp.add(synergy_object_text);
                temp.add(syn_object_img);
                temp.add(object_name);

                // Add the temp list to the return list
                ret.add(temp);
            }
            stmt.close();
            conn.close();
            return ret;
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
        return ret;
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

            for(int i = 0; i < num_features + 1; i++) {
                // Add columns based on the number of features
                String query = "ALTER TABLE parameters " +
                        "ADD " + "feature_" + Integer.toString(i) + " double";
                stmt.execute(query);
            }

            for(int i = 0; i < num_features; i++) {
                // Add initial column and number of columns based on the number of features
                String query = "ALTER TABLE mean " +
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
            for(int i = 2; i < num_cols + 1; i++) {
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
    public static SimpleMatrix read_parameter_cols(int num_classes, int num_features, String[] characters){
        try {
            // Step 0. Instantiate temporary 2D array
            double[][] temp_classifier_matrix = new double[num_classes][num_features];

            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            int row = 0;
            for(String curr_char : characters) {
                // Step 3. Create query to obtain all parameter data
                String query = "SELECT * FROM parameters WHERE classifier_id = \"" + curr_char + "\"";

                // Step 4. Execute the query
                ResultSet rs = stmt.executeQuery(query);
                rs.next();

                // Step 5. Obtain data from query
                //System.out.println(rs.getString(1));
                for (int col = 2; col < num_features + 2; col++) {
                    temp_classifier_matrix[row][col - 2] = rs.getDouble(col);
                }
                row += 1;
            }

            // Step 6. Close the Result Set and Statement objects
            stmt.close();
            conn.close();

            // Convert the 2D array into a simple matrix and return
            return new SimpleMatrix(temp_classifier_matrix);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    /*
    Name       :
    Purpose    : Check to see if the word exists within the
    Parameters :
    Return     :
    Notes      :
     */
    public static boolean word_exists(String object_name, Statement stmt) {
        try {
            // Step 1. Create the query string
            String query = "SELECT " +
                    "object_id, object_name " +
                    "FROM objects " +
                    "WHERE object_name = \'" + object_name + "\';";

            // Step 2. Execute the query
            ResultSet rs = stmt.executeQuery(query);
            rs.next();

            // Check to see if the word returned matches the input name
            String ret_name = rs.getString("object_name");
            return ret_name.equals(object_name);

        } catch (SQLException e) {
            return false;
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static Hashtable<Integer, ArrayList<String>> generate_word_hash_table() {
        Hashtable<Integer, ArrayList<String>> words_ht = new Hashtable<>();

        try {
            // Step 1. Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Step 2. Create a Statement object
            Statement stmt = conn.createStatement();

            // Step 3. Create the query string
            String query = "SELECT object_name FROM objects";

            // Step 4. Execute the query
            ResultSet rs = stmt.executeQuery(query);

            // Step 5. Obtain the data and sort into the hashtable
            while(rs.next()){
                // Obtain the current word
                String curr_word = rs.getString(1);

                // Find the length of the word, add word to ht
                int word_len = curr_word.length();
                if(words_ht.containsKey(word_len)){
                    ArrayList<String> temp_words = words_ht.get(word_len);
                    temp_words.add(curr_word);
                }
                else{
                    ArrayList<String> temp_words = new ArrayList<String>();
                    temp_words.add(curr_word);
                    words_ht.put(word_len, temp_words);
                }
            }

            // Step 6. Close the statement, connection objects
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
        return words_ht;
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static String find_closest_word(String incorrect_object_name, Hashtable<Integer, ArrayList<String>> word_ht){
        // Obtain the length of the incorrect word
        int word_len = incorrect_object_name.length();

        // Obtain all words of similar length, and +- 1 character

        List<String> check_words = new ArrayList<String>();
        for(ArrayList<String> elem : word_ht.values()){
            check_words.addAll(elem);
        }

        // Calculate the Levenshtein distance between incorrect word and all potential words
        int min_lev_dist = 50;
        String correct_word = "";
        for(String elem : check_words){
            int curr_dist = levenshtein_dist(incorrect_object_name, elem);
            if(min_lev_dist > curr_dist){
                min_lev_dist = curr_dist;
                correct_word = elem;
            }
        }

        return correct_word;
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static int levenshtein_dist(String word_a, String word_b){
        // Declare Levenshtein distance matrix
        int[][] lev_matrix = new int[word_b.length() + 1][word_a.length() + 1];

        // Initialize the first row, col of the matrix
        for(int row = 1; row < word_b.length() + 1; row++){
            lev_matrix[row][0] = row;
        }
        for(int col = 1; col < word_a.length() + 1; col++){
            lev_matrix[0][col] = col;
        }

        // Use tabulation to populate the table
        for(int row = 1; row < word_b.length() + 1; row++){
            for(int col = 1; col < word_a.length() + 1; col++){
                int same_char_flag = 1;
                if(Character.toLowerCase(word_a.charAt(col -1)) == Character.toLowerCase(word_b.charAt(row - 1))) {
                    same_char_flag = 0;
                }
                lev_matrix[row][col] = Math.min(
                            Math.min(lev_matrix[row - 1][col] + 1, lev_matrix[row - 1][col -1] + same_char_flag),
                            lev_matrix[row][col - 1] + 1
                );
            }
        }
        return lev_matrix[word_b.length()][word_a.length()];
    }



    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String []args){
        Hashtable<Integer, ArrayList<String>> words_ht = generate_word_hash_table();
        //String closest_word = find_closest_word("Cell_Key", words_ht);
        UserInterface test = new UserInterface();
        read_from_database("Mr_Accretion_Jr", words_ht, test);
        String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "0", "4", "7", "'", "SPACE", "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP",
                "ERRAPT"};

        read_parameter_cols(characters.length, 810, characters);
        return;
    }
}
