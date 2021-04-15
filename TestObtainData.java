import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestObtainData {

    @Test
    void test_compare_image_to_border_data() {
        /*
        Test against 'database' to ensure 'database is working correctly.
         */
        // Set file_path for testing files
        String base_file_name = "screenshots/images_for_processing/temp_";//0.jpg"

        // Obtain border data
        int[][] border_data = new int[345][400*3*2];
        ObtainData.read_border_data(border_data);

        // Iterate over files and perform tests
        for(int i = 0; i < 17; i++){
            // Create iterative file name
            String file_path = base_file_name + Integer.toString(i) + ".jpg";
            try {
                // Obtain BufferedImage based on file name
                BufferedImage image = ImageIO.read(new File(file_path));
                // Compare image border data to 'database'
                Assertions.assertTrue(ObtainData.compare_image_to_border_data(image, border_data));
            }
            catch (IOException e){
                System.out.print(e);
            }
        }

        /*
        Real testing
         */
        // Set file_path for testing files
        String base_file_name_2 = "screenshots/test_";//0.jpg"

        // Iterate over files and perform tests
        for(int i = 11; i < 24; i++){
            // Create iterative file name
            String file_path = base_file_name_2 + Integer.toString(i) + ".jpg";
            try {
                // Obtain BufferedImageage based on file name
                BufferedImage image = ImageIO.read(new File(file_path));
                // Compare image border data to 'database'
                System.out.println(ObtainData.compare_image_to_border_data(image, border_data));
            }
            catch (IOException e){
                System.out.print(e);
            }
        }

    }
}