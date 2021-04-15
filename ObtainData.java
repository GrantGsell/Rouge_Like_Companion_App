import com.opencsv.CSVWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class ObtainData {

    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void create_image_border_data(int num_borders, int border_width, int border_height){
        // Obtain borders, transform into useable data
        try {
            int num_cols = border_width * border_height * 2;
            int[][] border_data_array = new int[num_borders][num_cols];

            String base_file_path = "screenshots/image_borders/image_border_"; //0.jpg"
            for(int i = 0; i < num_borders; i++) {
                String file_path = base_file_path + Integer.toString(i) + ".jpg";
                BufferedImage image = ImageIO.read(new File(file_path));

                // Obtain outline BufferedImage object for both top and bottom
                BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), 3);
                BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), 3);

                // Transform BufferedImage object into array
                int[] top_array = get_image_rgb_data(top_outline);
                int[] bottom_array = get_image_rgb_data(bottom_outline);

                // Add data to 2D array
                System.arraycopy(top_array, 0, border_data_array[i], 0, top_array.length);
                System.arraycopy(bottom_array, 0, border_data_array[i], top_array.length, bottom_array.length);
            }

            // Copy array data to string array
            String[][] str_border_data = new String[num_borders][num_cols];
            for(int row = 0; row < num_borders; row++){
                for(int col = 0; col < num_cols; col++){
                    str_border_data[row][col] = Integer.toString(border_data_array[row][col]);
                }
            }

            // Write data into a csv file
            String filename = "src/border_data.csv";
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
    public static void read_border_data(int[][] border_data_arr){
        String file_path = "src/border_data.csv";
        try{
            // Read in Input data
            BufferedReader br = new BufferedReader(new FileReader(file_path));
            String line = "";
            int row = 0;
            while((line = br.readLine()) != null){
                String[] data = line.split(",");
                for(int col = 0; col < border_data_arr[0].length; col++){
                    String inp_string = data[col];
                    inp_string = inp_string.replaceAll("\"","");
                    int new_data = Integer.valueOf(inp_string);
                    border_data_arr[row][col] = new_data;
                }
                row += 1;
            }
        }
        catch(IOException e){
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
    public static boolean compare_image_to_border_data(BufferedImage image, int[][] border_data){
        try {
            int[] border_data_array = new int[400*3*2];

            // Obtain outline BufferedImage object for both top and bottom
            BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), 3);
            BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), 3);

            // Transform BufferedImage object into array
            int[] top_array = get_image_rgb_data(top_outline);
            int[] bottom_array = get_image_rgb_data(bottom_outline);

            // Add data to 2D array
            System.arraycopy(top_array, 0, border_data_array, 0, top_array.length);
            System.arraycopy(bottom_array, 0, border_data_array, top_array.length, bottom_array.length);

            // Compare to border data
            for(int i = 0; i < border_data.length; i++){
                if(Arrays.equals(border_data_array,border_data[i])){
                    return true;
                }
            }
            int max_correct = 0;
            int arr_num = 0;
            // See if any images are even close in value
            for(int i = 0; i < border_data.length; i++){
                int temp = compare_array_elements(border_data_array,border_data[i]);
                if(temp > max_correct){
                    arr_num = i;
                    max_correct = temp;
                }
            }
            if(max_correct > 0) {
                System.out.format("Num Correct: %d\n" , max_correct);
                diff_between_elements(border_data_array, border_data[arr_num]);
            }


        }
        catch (Exception e){
            System.out.println(e);
        }


        return false;
    }



    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static int compare_image_to_border_data_int_ret(BufferedImage image, int[][] border_data){
        try {
            int[] border_data_array = new int[400*3*2];

            // Obtain outline BufferedImage object for both top and bottom
            BufferedImage top_outline = image.getSubimage(0, 0, image.getWidth(), 3);
            BufferedImage bottom_outline = image.getSubimage(0, 74, image.getWidth(), 3);

            // Transform BufferedImage object into array
            int[] top_array = get_image_rgb_data(top_outline);
            int[] bottom_array = get_image_rgb_data(bottom_outline);

            // Add data to 2D array
            System.arraycopy(top_array, 0, border_data_array, 0, top_array.length);
            System.arraycopy(bottom_array, 0, border_data_array, top_array.length, bottom_array.length);

            // Compare to border data
            for(int i = 0; i < border_data.length; i++){
                if(Arrays.equals(border_data_array,border_data[i])){
                    return 400*3*2;
                }
            }
            int max_correct = 0;
            // See if any images are even close in value
            for(int i = 0; i < border_data.length; i++){
                int temp = compare_array_elements(border_data_array,border_data[i]);
                if(temp > max_correct){
                    max_correct = temp;
                }
            }
            if(max_correct > 0) {
                System.out.format("Num Correct: %d\n" , max_correct);
                return max_correct;
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return 0;
    }


    public static void diff_between_elements(int[] arr_1, int[] arr_2){
        int sum = 0;
        for(int i = 0; i < arr_1.length; i++){
            sum += Math.abs(arr_1[i] - arr_2[i]);
        }
        double ave_diff = (double) sum / (double) arr_1.length;
        System.out.format("Average Difference: %.3f\n\n", ave_diff);
    }

    public static int compare_array_elements(int[] arr_1, int[] arr_2){
        int num_elements_correct = 0;
        for(int i = 0; i < arr_1.length; i++){
            if(arr_1[i] == arr_2[i]) num_elements_correct += 1;
        }
        return num_elements_correct;
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
        int[] rgb_data = new int[height * width];
        int arr_index = 0;

        // Store image data into array
        for(int row = 0; row < width; row++){
            for(int col = 0; col < height; col++){
                rgb_data[arr_index] = image.getRGB(row, col);
                arr_index += 1;
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
    public static void main(String[] args){
        int num_borders = 497;
        int border_width = 400;
        int border_height = 3;
        // Create border image 'database'
        ObtainData.create_image_border_data(num_borders, border_width, border_height);

        // Read in the border image 'database'
        int[][] border_data = new int[num_borders][400*3*2];
        ObtainData.read_border_data(border_data);

        // Compare a new image to the border image 'database'
        int temp = 0;
        try {
            String file_path = "screenshots/image_borders/image_border_0.jpg";
            BufferedImage image = ImageIO.read(new File(file_path));
            ObtainData.compare_image_to_border_data(image, border_data);
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

}
