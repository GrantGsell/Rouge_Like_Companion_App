import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.sql.*;

public class BorderData {

    /*
    Name       : average_border_values_per_class
    Purpose    : To parse through each border image example and determine the average value for each corresponding
                    pixel.
    Parameters : None
    Return     : One 2D array containing the average RGB value for each pixel in each border class.
    Notes      : None
    */
    public static double[][] average_border_values_per_class(){
        // Border variables
        int num_classes = 4;
        int border_height = 3;
        int num_features = 7200;
        double[][] border_data_array = {};

        try {
            int num_borders = 0;
            double[] class_1_border = new double[num_features];
            double[] class_2_border = new double[num_features];
            double[] class_3_border = new double[num_features];
            double[] class_4_border = new double[num_features];

            for (int k = 0; k < num_classes; k++) {
                String base_file_path = "";
                switch (k) {
                    case 0:
                        base_file_path = "screenshots/class_1_border/class_1_";
                        num_borders = 415;
                        break;
                    case 1:
                        base_file_path = "screenshots/class_2_border/class_2_";
                        num_borders = 297;
                        break;
                    case 2:
                        base_file_path = "screenshots/class_3_border/class_3_";
                        num_borders = 112;
                        break;
                    case 3:
                        base_file_path = "screenshots/class_4_border/class_4_";
                        num_borders = 22;
                        break;
                }
                for (int i = 0; i < num_borders; i++) {
                    int[] temp = new int[7200];
                    String file_path = base_file_path + Integer.toString(i) + ".jpg";
                    BufferedImage image = ImageIO.read(new File(file_path));

                    // Obtain outline BufferedImage object for both top and bottom
                    BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), border_height);
                    BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), border_height);

                    // Transform BufferedImage object into array
                    int[] top_array = get_image_rgb_data(top_outline);
                    int[] bottom_array = get_image_rgb_data(bottom_outline);

                    // Add data to temp array
                    System.arraycopy(top_array, 0, temp, 0, top_array.length);
                    System.arraycopy(bottom_array, 0, temp, top_array.length, bottom_array.length);

                    // Add temp array to specified row in border data array
                    if (k == 0) {
                        Arrays.setAll(class_1_border, j -> class_1_border[j] + temp[j]);
                    } else if (k == 1) {
                        Arrays.setAll(class_2_border, j -> class_2_border[j] + temp[j]);
                    } else if (k == 2) {
                        Arrays.setAll(class_3_border, j -> class_3_border[j] + temp[j]);
                    }else if (k == 3) {
                        Arrays.setAll(class_4_border, j -> class_4_border[j] + temp[j]);
                    }
                }
                // Divide the current sum array by the total number of elements summed
                for(int p = 0; p < num_features; p++){
                    if (k == 0) {
                        class_1_border[p] = class_1_border[p] / (double) num_borders;
                    } else if (k == 1) {
                        class_2_border[p] = class_2_border[p] / (double) num_borders;
                    } else if (k == 2) {
                        class_3_border[p] = class_3_border[p] / (double) num_borders;
                    } else if (k == 3) {
                        class_4_border[p] = class_4_border[p] / (double) num_borders;
                    }
                }
            }

            border_data_array = new double[][]{
                    class_1_border,
                    class_2_border,
                    class_3_border,
                    class_4_border
            };
        }
        catch (IOException e){
            System.out.println(e);
        }
        return border_data_array;
    }


    /*
    Name       : border_class
    Purpose    : To determine if the input image contains pixel data in its top three and bottom three rows that
                    correspond to one of the four notification box borders.
    Parameters :
                 image, a BufferedImage object that contains the image data of where a notificaiton box would occur on
                    screen.
                 border_data_per_class, a 2D array of double values denoting the average value for each pixel in each of
                    the four border classes.
    Return     : One int value denoting the border class of th notification box, or lack thereof, in the given image.
    Notes      : If the image provided does not contain a notification box, the the method will return 0.
    */
    public static int get_border_class(BufferedImage image, double[][] border_data_per_class){
        int num_classes = border_data_per_class.length;
        int num_features = border_data_per_class[0].length;
        int border_height = 3;

        // An array to hold the border data for both the top and bottom borders
        int[] combined_border_data = new int[num_features];

        // Obtain outline BufferedImage object for both top and bottom
        BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), border_height);
        BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), border_height);

        // Transform BufferedImage object into array and add to the combined data array
        System.arraycopy(get_image_rgb_data(top_outline), 0, combined_border_data, 0, num_features/2);
        System.arraycopy(get_image_rgb_data(bottom_outline), 0, combined_border_data, num_features/2, num_features/2);

        // Find the difference between each of the classes
        double[] num_diff_pixels = new double[num_classes];
        for(int col = 0; col < num_features; col++){
            if(Math.abs((double) combined_border_data[col] - border_data_per_class[0][col]) > 20){
                num_diff_pixels[0] += 1.0;
            }
            if(Math.abs((double) combined_border_data[col] - border_data_per_class[1][col]) > 50){
                num_diff_pixels[1] += 1.0;
            }
            if(Math.abs((double) combined_border_data[col] - border_data_per_class[2][col]) > 35){
                num_diff_pixels[2] += 1.0;
            }
            if(Math.abs((double) combined_border_data[col] - border_data_per_class[3][col]) > 35){
                num_diff_pixels[3] += 1.0;
            }
        }

        double smallest_value = 2e9;
        int smallest_val_idx = -1;

        // Average each of the class differences
        for(int i = 0; i < num_classes; i++){
            if(num_diff_pixels[i] < smallest_value){
                smallest_value = num_diff_pixels[i];
                smallest_val_idx = i + 1;
            }
        }
        if(smallest_value > 100){
            return 0;
        }
        else{
            return smallest_val_idx;
        }
    }


    /*
    Name       : get_image_rgb_data
    Purpose    : To transform the data contained in the input image from a BufferedImage into an array of RGB values
    Parameters : One BufferedImage image
    Return     : An array containing the RGB values for the input image
    Notes      : None
     */
    public static int[] get_image_rgb_data(BufferedImage image){
        int height = image.getHeight();
        int width = image.getWidth();
        int[] rgb_data = new int[height * width * 3];
        int arr_index = 0;

        // Store image data into array
        for(int row = 0; row < width; row++){
            for(int col = 0; col < height; col++){
                Color c = new Color(image.getRGB(row, col));
                rgb_data[arr_index] = c.getRed();
                rgb_data[arr_index + 1] = c.getGreen();
                rgb_data[arr_index + 2] = c.getBlue();
                arr_index += 3;
            }
        }
        return  rgb_data;
    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void writeBorderDataToMySQL(){

    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void createBorderDataTable(){
        //

        // Clear and initialize table
        try {
            // Open a new connection to the database
            Connection conn = MySQLJDBCUtil.getConnection();

            // Create a Statement object
            Statement stmt = conn.createStatement();

            // Create and execute queries
            String query = "DROP TABLE IF EXISTS border_data";
            stmt.execute(query);
            query = "CREATE TABLE IF NOT EXISTS border_data (row_num tinyint primary key)";
            stmt.execute(query);

            // Add the columns for each row
            for(int i = 0; i < 900; i++){
                query = "ALTER TABLE border_data " +
                        "ADD " + "column_" + Integer.toString(i) + " int";
                stmt.execute(query);
            }

            // Close the Result Set and Statement objects
            stmt.close();
            conn.close();

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args){
        // Create and initialize the border_data table
        createBorderDataTable();
    }
}
