import edu.stanford.nlp.util.Pair;
import org.ejml.simple.SimpleMatrix;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


public class CharacterSegmentation {

    /*
    Name       : top_character_segmentation
    Purpose    : To isolate the objects title text via crop, and determine the column indices for character
                    segmentation.
    Parameters : image, a BufferedImage object denoting a valid notification box.
    Return     : A pair, whose key is an ArrayList of column indices, and whose value is the cropped image that isolated
                    the objects title text.
    Notes      : None.
     */
    public static Pair<ArrayList<Integer>, BufferedImage> top_character_segmentation(BufferedImage image){
        // Sliding window dimensions and step size
        int sw_height = 18, sw_width = 15, sw_delta = 5;

        // Isolate text line from notification box
        image = image.getSubimage(0,16, image.getWidth(), image.getHeight()-16);

        // Isolate the text-box
        image = isolate_text_box(image, sw_height, sw_width, sw_delta);

        // Perform image pre-processing
        background_processing(image);

        // Obtain character segmentation arraylist
        return new Pair<ArrayList<Integer>, BufferedImage> (character_segmentation(image, sw_height), image);
    }


    /*
    Name       : get_image_rgb_data_double
    Purpose    : To convert a BufferedImage object into an array consisting of its RGB values.
    Parameters : One buffered image object.
    Return     : One double array consisting of its RGB values.
    Notes      : None.
     */
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


    /*
    Name       : isolate_text_box
    Purpose    : To further cut down the notification box image into an image containing only the objects name text, as
                    well as a slight buffer before the first letter, and after the last letter.
    Parameters :
                 image, a BufferedImage object containing the object name text.
                 sw_height, an int denoting the sliding window box height.
                 sw_width, an int denoting the sliding window box width.
                 sw_delta, an int denoting the sliding window horizontal step size.
    Return     : A buffered image containing the object text name.
    Notes      : This is a 1D sliding window method, and isolates the object text by finding teh first and last boxes
                    that set the the num_bw_pixels true for the number of white pixels.
     */
    public static BufferedImage isolate_text_box(BufferedImage image, int sw_height, int sw_width, int sw_delta){
        // Text box column variables
        int num_boxes = Math.floorDiv(image.getWidth() - sw_width, sw_delta);
        int text_box_start = -1;
        int text_box_end = -1;
        int x_offset = 0;
        int y_offset = 0;

        // Slide the sliding window box along the image width
        for(int curr_idx = 0; curr_idx < num_boxes; curr_idx++){
            // Obtain the current sliding window box
            BufferedImage curr_box = image.getSubimage(x_offset, y_offset, sw_width, sw_height);

            // Determine if the sliding window contains a character
            int curr_box_class = 0;
            if(num_bw_pixels(curr_box, 180, 68, true)) {
                curr_box_class = 1;
            }

            // Set the text box end/start indices
            if(curr_box_class == 1 && text_box_start < 0){
                text_box_start = x_offset - 5;
            }
            else if(curr_box_class == 1 && text_box_start >0){
                text_box_end = x_offset + sw_width;
            }

            // Iterate x_offset to move the sliding window horizontally
            x_offset += sw_delta;
        }

        // Check to see if a text box was found, if so return the cropped text box image
        if(text_box_start != text_box_end){
            int text_box_size = text_box_end - text_box_start + 3;
            return image.getSubimage(text_box_start, y_offset, text_box_size, sw_height);
        }
        else{
            return null;
        }
    }


    /*
    Name       : obtain_sliding_window_data
    Purpose    : To transform the images collected for each border, isolate the characters within each example and
                    write the data to a file location.
    Parameters :
                 sw_height, an int denoting the sliding window box height.
                 sw_width, an int denoting the sliding window box width.
                 sw_delta, an int denoting the sliding window horizontal step size.
                 start_class, an int denoting which border class to start taking data from.
                 start_ex_idx, and int denoting the starting index for an iterative file name.
    Return     : A simple matrix where each row has the pixel data for one character example.
    Notes      : The parameters start_class, and start_ex_idx allow the user wants to pause and take a break from
                    collecting data. This break is necessary the user has to enter the respective character letters into
                    the database for each letter saved.
     */
    public static SimpleMatrix obtain_sliding_window_data(int sw_height, int sw_width, int sw_delta, int start_class,
                                                          int start_ex_idx) {
        // Set input/output data arrays
        int input_data_id = 0;
        int pixel_depth = 3;

        // Determine which class, and set the max number of borders
        String base_file_path = "";
        int num_class_examples = -1;

        // Instantiate input data
        double[][] input_data = new double[7979][sw_height * sw_width * pixel_depth];
        for(int i = start_class; i < 3; i++) {
            switch (i) {
                case 1:
                    base_file_path = "screenshots/class_1_border/class_1_";
                    num_class_examples = 416;
                    break;
                case 2:
                    base_file_path = "screenshots/class_2_border/class_2_";
                    num_class_examples = 297;
                    break;
                case 3:
                    base_file_path = "screenshots/class_3_border/class_3_";
                    num_class_examples = 112;
                    break;
                case 4:
                    base_file_path = "screenshots/class_4_border/class_4_";
                    num_class_examples = 22;
                    break;
            }

            //Iterate over images, obtain pixel sub-boxes, prompt for user input
            try {
                int char_index = 0;

                // Select the example image
                for (int ex_num = start_ex_idx; ex_num < num_class_examples; ex_num++) {
                    // Read in the example image
                    String file_path = base_file_path + Integer.toString(ex_num) + ".jpg";
                    BufferedImage curr_ex_image = ImageIO.read(new File(file_path));
                    curr_ex_image = curr_ex_image.getSubimage(0, 16, curr_ex_image.getWidth(), curr_ex_image.getHeight() - 16);

                    // Isolate the text-box
                    curr_ex_image = isolate_text_box(curr_ex_image, sw_height, sw_width, sw_delta);

                    // Perform image background pre-processing
                    //curr_ex_image = background_processing(curr_ex_image);
                    background_processing(curr_ex_image);

                    // Obtain character segmentation arraylist
                    ArrayList<Integer> char_seg = character_segmentation(curr_ex_image, sw_height);

                    //Use sliding window to obtain sub image for each character
                    for (int curr_idx = 0; curr_idx < char_seg.size(); curr_idx++) {
                        // Obtain a sliding window box
                        BufferedImage curr_char = curr_ex_image.getSubimage(char_seg.get(curr_idx), 0, sw_width, sw_height);

                        // Write character data to folder
                        collect_char_image_for_testing(char_index, curr_char);
                        char_index++;

                        // Obtain sub-box pixel data
                        double[] pixel_data = get_image_rgb_data_double(curr_char);

                        // Set array data
                        input_data[input_data_id] = pixel_data;
                        input_data_id += 1;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Convert input data into a SimpleMatrix object
        return new SimpleMatrix(input_data);
    }


    /*
    Name       : num_bw_pixels
    Purpose    : To determine if a given image contains a sufficient number of black or white pixels to be considered
                    to contain either an empty space or a letter.
    Parameters :
                 image, a BufferedImage object.
                 threshold, an int denoting the value for which each RGB element within a pixel needs to be greater
                    than, or less than to be considered a white pixel or black pixel respectively.
                 num_bw_pixels, an int denoting the number of black/white pixels that need to be present within the
                    given image to be considered either a blank space or to contain a letter.
                 count_white, a boolean denoting if the image in question should be compared to the black/white image
                    values.
    Return     : A boolean denoting if the image contains enough black/white pixels to be considered a blank space or
                    containing a letter.
    Notes      : None.
     */
    public static boolean num_bw_pixels(BufferedImage image, int threshold, int num_bw_pixels, boolean count_white){
        // White pixel count data
        int bw_pixel_count = 0;

        // Obtain rgb pixel data
        double[] pixel_arr = get_image_rgb_data_double(image);

        // Iterate over pixel_array, looking at 3 pixel for black/white data.
        for(int idx = 0; idx < pixel_arr.length; idx += 3){
            if(count_white && pixel_arr[idx] > threshold && pixel_arr[idx + 1] > threshold && pixel_arr[idx + 2] > threshold ){
                bw_pixel_count += 1;
            }
            else if(!count_white && pixel_arr[idx] < threshold && pixel_arr[idx + 1] < threshold && pixel_arr[idx + 2] < threshold){
                bw_pixel_count += 1;
            }
        }
        // Return boolean, 1 for valid box, 0 for invalid
        return bw_pixel_count >= num_bw_pixels;
    }


    /*
    Name       : character_segmentation
    Purpose    : To isolate each character within the text box image.
    Parameters :
                 image, a BufferedImage containing just the object text with a slight buffer on both ends.
                 sw_height, an int denoting the height of the sliding window.
    Return     : An arraylist containing the starting column indices for each character in the object text.
    Notes      : The characters are separated by looking at the first and last column in teh sliding window. If the two
                    columns both have a sufficient number of black pixels then the starting column is added to the
                    spacing indices array. Because the characters are separated in this manner some duplicates of the
                    same characters are added to the array and are only separated by 1-5 columns. Therefore the spacing
                    indices array had to be processed to see if two adjacent elements fall within the 1-5 duplicate
                    column spacing. Duplicates are not added to the final arraylist.
     */
    public static ArrayList<Integer> character_segmentation(BufferedImage image, int sw_height){
        // Set indices array, max value is image width / (sw_width + char_space_width)
        int[] spacing_indices = null;
        try {
            spacing_indices = new int[image.getWidth()];
        }catch(Exception e){
            e.printStackTrace();
        }
        int arr_idx = 0;

        // Search image for black columns, if found search for black column 14 spaces away
        int x_offset = 0;

        // Use the sliding window to assign potential column indices to the spacing indices array
        while(x_offset + 14 < image.getWidth()){
            // Obtain sliding window edge slices
            BufferedImage left_column = image.getSubimage(x_offset, 0, 1, sw_height);
            BufferedImage right_column = image.getSubimage(x_offset + 14, 0, 1, sw_height);

            if(num_bw_pixels(left_column, 100,(sw_height - 3), false)
                    && num_bw_pixels(right_column, 100,(sw_height - 2), false)){
                spacing_indices[arr_idx] = x_offset;
                arr_idx += 1;
            }
            x_offset += 1;
        }

        // Process array into new usable array list and remove duplicates
        ArrayList<Integer> char_splits = new ArrayList<Integer>();
        int idx = 0;
        while(spacing_indices[idx] != 0 || spacing_indices[idx + 1] !=0){
            if(spacing_indices[idx + 5] - spacing_indices[idx] == 5){
                char_splits.add(spacing_indices[idx + 3]);
                idx += 6;
            }
            else if(spacing_indices[idx + 4] - spacing_indices[idx] == 4){
                char_splits.add(spacing_indices[idx + 3]);
                idx += 5;
            }
            else if(spacing_indices[idx + 3] - spacing_indices[idx] == 3){
                char_splits.add(spacing_indices[idx + 2]);
                idx += 4;
            }
            else if(spacing_indices[idx + 2] - spacing_indices[idx] == 2){
                char_splits.add(spacing_indices[idx + 1]);
                idx += 3;
            }
            else if(spacing_indices[idx + 1] - spacing_indices[idx] == 1){
                char_splits.add(spacing_indices[idx]);
                idx += 2;
            }
            else{
                char_splits.add(spacing_indices[idx]);
                idx += 1;
            }
        }
        // Return the array list
        return char_splits;
    }


    /*
    Name       : background_processing
    Purpose    : To change any pixels in the given image to black if all three of its RGB values are below the given
                    threshold.
    Parameters : One BufferedImage object denoting an image that contains a object text.
    Return     : None.
    Notes      : None.
     */
    public static void background_processing(BufferedImage image){
        try {
            int threshold = 140;
            for (int row = 0; row < image.getWidth(); row++) {
                for (int col = 0; col < image.getHeight(); col++) {
                    Color c = new Color(image.getRGB(row, col));
                    int red = c.getRed();
                    int green = c.getGreen();
                    int blue = c.getBlue();
                    if (red <= threshold && green <= threshold && blue <= threshold) {
                        image.setRGB(row, col, 0);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
    Name       : collect_char_image_for_testing
    Purpose    : Write current char to folder, used for collection examples for learning.
    Parameters :
                 char_index, an int denoting the index to be used as an iterative file naming scheme.
                 curr_char, a BufferedImage object containing the pixel data for a letter within an objects name.
    Return     : None.
    Notes      : None.
     */
    private static void collect_char_image_for_testing(int char_index, BufferedImage curr_char) {
        try {
            // Generate iterative file path
            String index = Integer.toString(char_index);
            String cwd = "screenshots/Char_Test_folder/test_";
            String test_file_path = cwd + index + ".jpg";

            // Write image buffer to file
            File output_file = new File(test_file_path);
            ImageIO.write(curr_char, "jpg", output_file);
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    /*
    Name       : main
    Purpose    : Client testing code.
    Parameters : Standard main arguments.
    Return     : None.
    Notes      : None.
     */
    public static void main(String[] args) {

        int sliding_window_height = 18;
        int sliding_window_width =  15;
        int sliding_window_delta = 5;
        int class_num = 1;
        int ex_idx = 0;

        obtain_sliding_window_data(sliding_window_height, sliding_window_width, sliding_window_delta, class_num,
                ex_idx);

    }
}
