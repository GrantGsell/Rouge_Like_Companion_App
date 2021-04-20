import com.opencsv.CSVWriter;
import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

public class ObtainData {

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void create_image_border_data(int total_num_borders, int border_width, int border_height, int num_classes){
        // Obtain borders, transform into useable data
        try {
            int num_borders = 0;
            int arr_index = 0;
            int num_cols = border_width * border_height * 2 * 3;
            int[][] border_data_array = new int[total_num_borders][num_cols];
            double[] output_classes = new double[total_num_borders];

            for(int k = 0; k < num_classes; k++) {
                String base_file_path = "";
                switch(k){
                    case 0:
                        base_file_path = "screenshots/negative_borders/negative_";
                        num_borders = 285;
                        break;
                    case 1:
                        base_file_path = "screenshots/class_1_border/class_1_";
                        num_borders = 285;
                        break;
                    case 2:
                        base_file_path = "screenshots/class_2_border/class_2_";
                        num_borders = 285;
                        break;
                    case 3:
                        base_file_path = "screenshots/class_3_border/class_3_";
                        num_borders = 134;
                        break;
                }

                for (int i = 0; i < num_borders; i++) {
                    String file_path = base_file_path + Integer.toString(i) + ".jpg";
                    BufferedImage image = ImageIO.read(new File(file_path));

                    // Obtain outline BufferedImage object for both top and bottom
                    BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), border_height);
                    BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), border_height);

                    // Transform BufferedImage object into array
                    int[] top_array = get_image_rgb_data(top_outline);
                    int[] bottom_array = get_image_rgb_data(bottom_outline);

                    // Add data to 2D array
                    System.arraycopy(top_array, 0, border_data_array[arr_index], 0, top_array.length);
                    System.arraycopy(bottom_array, 0, border_data_array[arr_index], top_array.length, bottom_array.length);
                    arr_index += 1;

                }
            }
            // Copy array data to string array
            String[][] str_border_data = new String[total_num_borders][num_cols];
            for(int row = 0; row < total_num_borders; row++){
                for(int col = 0; col < num_cols; col++){
                    str_border_data[row][col] = Integer.toString(border_data_array[row][col]);
                }
            }

            // Write data into a csv file
            String filename = "src/lr_border_data.csv";
            CSVWriter writer = new CSVWriter(new FileWriter(filename), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            for(String[] data : str_border_data) {
                writer.writeNext(data);
                writer.flush();
            }
            System.out.println("Border Data Written\n");
        }
        catch (IOException e){
            System.out.println(e);
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
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

    public static double[] get_image_rgb_data_double(BufferedImage image){
        int height = image.getHeight();
        int width = image.getWidth();
        double[] rgb_data = new double[height * width * 3];
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


    public static int average_percent_difference(BufferedImage image, double[][] border_data_per_class, int num_classes, int border_height, int num_features){
        // Transform the new image into usable data
        int[] temp = new int[num_features];

        // Obtain outline BufferedImage object for both top and bottom
        BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), border_height);
        BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), border_height);

        // Transform BufferedImage object into array
        int[] top_array = get_image_rgb_data(top_outline);
        int[] bottom_array = get_image_rgb_data(bottom_outline);

        // Add data to temp array
        System.arraycopy(top_array, 0, temp, 0, top_array.length);
        System.arraycopy(bottom_array, 0, temp, top_array.length, bottom_array.length);

        // Find the difference between each of the classes
        double[] ave_class_diff = new double[num_classes];
        for(int col = 0; col < num_features; col++){
            if(Math.abs((double) temp[col] - border_data_per_class[0][col]) > 20){
                ave_class_diff[0] += 1.0;
            }
            if(Math.abs((double) temp[col] - border_data_per_class[1][col]) > 50){
                ave_class_diff[1] += 1.0;
            }
            if(Math.abs((double) temp[col] - border_data_per_class[2][col]) > 23){
                ave_class_diff[2] += 1.0;
            }
            if(Math.abs((double) temp[col] - border_data_per_class[3][col]) > 23){
                ave_class_diff[3] += 1.0;
            }
        }

        double smallest_value = 2e9;
        int smallest_val_idx = -1;

        // Average each of the class differences
        for(int i = 0; i < num_classes; i++){
            if(ave_class_diff[i] < smallest_value){
                smallest_value = ave_class_diff[i];
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
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static double[][] average_border_color_per_class(int num_classes,  int border_height, int num_features){
        //String base_file_path = "screenshots/class_1_border/class_1_";
        double[][] border_data_array = {};
        try {
            int num_borders = 0;
            //int[][] border_data_array = new int[num_classes][7200];
            double[] class_1_border = new double[num_features];
            double[] class_2_border = new double[num_features];
            double[] class_3_border = new double[num_features];
            double[] class_4_border = new double[num_features];

            for (int k = 0; k < num_classes; k++) {
                String base_file_path = "";
                switch (k) {
                    case 0:
                        base_file_path = "screenshots/class_1_border/class_1_";
                        num_borders = 285;
                        break;
                    case 1:
                        base_file_path = "screenshots/class_2_border/class_2_";
                        num_borders = 285;
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
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static BufferedImage text_box_recognition(BufferedImage image, int sw_height, int sw_width, int sw_delta){
        // Text box variables
        int num_boxes = Math.floorDiv(image.getWidth() - sw_width, sw_delta);
        int text_box_start = -1;
        int text_box_end = -1;
        int x_offset = 0;
        int y_offset = 0;

        // Obtain sliding window crop from the original given image (1D)
        for(int curr_idx = 0; curr_idx < num_boxes; curr_idx++){
            // Obtain a sliding window box
            BufferedImage curr_box = image.getSubimage(x_offset, y_offset, sw_width, sw_height);

            // Run the current box image through ML algorithm and return class number (0/1)
            int curr_box_class = 0;
            if(num_white_pixels(curr_box)){
                curr_box_class = 1;
            }

            // Set the text box end/start indices
            if(curr_box_class == 1 && text_box_start < 0){
                text_box_start = x_offset;
            }
            else if(curr_box_class == 1 && text_box_start >0){
                text_box_end = x_offset + sw_width;
            }

            // Iterate x_offset to move the sliding window
            x_offset += sw_delta;
        }

        // Check to see if a text box was found, if so return the text box image
        if(text_box_start != text_box_end){
            int text_box_size = text_box_end - text_box_start;
            BufferedImage text_box = image.getSubimage(text_box_start, y_offset, text_box_size, sw_height);
            return text_box;
        }
        else{
            return null;
        }
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void obtain_sliding_window_data(int sw_height,
                                                  int sw_width,
                                                  int sw_delta,
                                                  int start_class,
                                                  int start_ex_idx,
                                                  int num_examples,
                                                  int example_width,
                                                  String input_data_file,
                                                  String output_data_file)
    {
        // Set input/output data arrays
        int input_data_id = 0;
        int pixel_depth = 3;
        int num_boxes = Math.floorDiv(example_width - sw_width, sw_delta);
        double[][] input_data = new double[num_boxes * num_examples][sw_height * sw_width * pixel_depth];
        String[] output_data = new String[num_boxes * num_examples];

        // Determine which class, and set the max number of borders
        String base_file_path = "";
        int max_num_borders = -1;
        switch (start_class) {
            case 1:
                base_file_path = "screenshots/class_1_border/class_1_";
                max_num_borders = 285;
                break;
            case 2:
                base_file_path = "screenshots/class_2_border/class_2_";
                max_num_borders = 285;
                break;
            case 3:
                base_file_path = "screenshots/class_3_border/class_3_";
                max_num_borders = 112;
                break;
            case 4:
                base_file_path = "screenshots/class_4_border/class_4_";
                max_num_borders = 22;
                break;
        }

        /*
         Iterate over images, obtain pixel sub-boxes, prompt for user input
         */
        try {
            // Select the example image
            for(int ex_num = start_ex_idx; ex_num < max_num_borders; ex_num++) {
                // Read in the example image
                String file_path = base_file_path + Integer.toString(ex_num) + ".jpg";
                BufferedImage curr_ex_image = ImageIO.read(new File(file_path));

                /*
                 Use sliding window to obtain sub image
                 */
                int x_offset = 0;
                int y_offset = 16;
                for(int curr_idx = 0; curr_idx < num_boxes; curr_idx++) {
                    // Obtain a sliding window box
                    BufferedImage curr_box = curr_ex_image.getSubimage(x_offset, y_offset, sw_width, sw_height);

                    // Prompt for use input on the image
                    Scanner myObj = new Scanner(System.in);  // Create a Scanner object
                    System.out.println("Enter Character classification: 0 is not text, 1 is text");
                    String user_input = myObj.nextLine();  // Read user input

                    // Obtain sub-box pixel data
                    double[] pixel_data = get_image_rgb_data_double(curr_box);

                    // Set array data
                    input_data[input_data_id] = pixel_data;
                    output_data[input_data_id] = user_input;
                    input_data_id += 1;
                }
            }

            // Convert input/output data to SimpleMatrix
            SimpleMatrix input_data_matrix = new SimpleMatrix(input_data);
            //SimpleMatrix output_data_matrix = new SimpleMatrix(new double[][]{output_data});

            // Write Simple Matrix data to csv
            input_data_matrix.loadCSV(input_data_file);
            //output_data_matrix.loadCSV(output_data_file);
        }
        catch (IOException e){
            System.out.println("IO ERROR");
            System.out.println(e);
        }

    }

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static boolean num_white_pixels(BufferedImage image){
        // Pixel value threshold
        int threshold = 180;

        // White pixel count data
        int white_pixel_count = 0;

        // Obtain rgb pixel data
        int[] pixel_arr = get_image_rgb_data(image);

        // Iterate over pixel_array, looking at 3 pixel for white data.
        for(int idx = 0; idx < pixel_arr.length; idx += 3){
            if(pixel_arr[idx] > threshold && pixel_arr[idx + 1] > threshold && pixel_arr[idx + 2] > threshold ){
                white_pixel_count += 1;
            }
        }

        // Return boolean, 1 for valid box, 0 for invalid
        return white_pixel_count >= 68;

    }



    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String[] args) {

        int sliding_window_height = 18;
        int sliding_window_width =  15;
        int sliding_window_delta = 5;
        /*
        int class_num = 1;
        int ex_idx = 0;
        int number_examples = 15;
        int example_width = 400;
        String input_file_name = "text_box_recog/input_text_box_recog_data.csv";
        String output_file_name = "text_box_recog/output_text_box_recog_data.csv";

        obtain_sliding_window_data(
                sliding_window_height, sliding_window_width, sliding_window_delta,
                class_num, ex_idx, number_examples, example_width,
                input_file_name, output_file_name);
        */

        String class_2_base_file_path = "screenshots/class_2_border/class_2_";
        int num_borders_2 = 285;
        try {
            for (int i = 0; i < num_borders_2; i++) {
                String file_path = class_2_base_file_path + Integer.toString(i) + ".jpg";
                BufferedImage test_image = ImageIO.read(new File(file_path));
                test_image = test_image.getSubimage(0,16, test_image.getWidth(), test_image.getHeight()-16);
                text_box_recognition(test_image, sliding_window_height, sliding_window_width, sliding_window_delta);
            }
        }
        catch (IOException e){
            System.out.println(e);
        }

    }
}
