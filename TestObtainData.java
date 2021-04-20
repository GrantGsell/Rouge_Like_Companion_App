import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestObtainData {

    @Test
    void test_average_percent_difference(){
        // Class variables
        int num_classes = 4;
        int border_height = 3;
        int num_features = 7200;
        double[][] class_border_matrix;
        int num_errors = 0;

        // Generate the border data matrix
        class_border_matrix = ObtainData.average_border_color_per_class(num_classes, border_height, num_features);

        /*
         Perform test for class 1
         */
        String class_1_base_file_path = "screenshots/class_1_border/class_1_";
        int num_borders_1 = 285;
        try {
            for (int i = 0; i < num_borders_1; i++) {
                String file_path = class_1_base_file_path + Integer.toString(i) + ".jpg";
                BufferedImage image = ImageIO.read(new File(file_path));
                int prediction = ObtainData.average_percent_difference(image, class_border_matrix, num_classes, border_height, num_features);
                if(prediction != 1){
                    num_errors += 1;
                    System.out.println("Error");
                }
            }
            Assertions.assertTrue(num_errors < 5);
        }
        catch (IOException e){
            System.out.println(e);
        }


        /*
         Perform test for class 2
         */
        String class_2_base_file_path = "screenshots/class_2_border/class_2_";
        int num_borders_2 = 285;
        num_errors = 0;
        try {
            for (int i = 0; i < num_borders_2; i++) {
                String file_path = class_2_base_file_path + Integer.toString(i) + ".jpg";
                BufferedImage image = ImageIO.read(new File(file_path));
                int prediction = ObtainData.average_percent_difference(image, class_border_matrix, num_classes, border_height, num_features);
                if(prediction != 2){
                    num_errors += 1;
                    System.out.println("Error");
                }
            }
            Assertions.assertTrue(num_errors < 5);
        }
        catch (IOException e){
            System.out.println(e);
        }


        /*
         Perform test for class 3
         */
        String class_3_base_file_path = "screenshots/class_3_border/class_3_";
        int num_borders_3 = 112;
        num_errors = 0;
        try {
            for (int i = 0; i < num_borders_3; i++) {
                String file_path = class_3_base_file_path + Integer.toString(i) + ".jpg";
                BufferedImage image = ImageIO.read(new File(file_path));
                int prediction = ObtainData.average_percent_difference(image, class_border_matrix, num_classes, border_height, num_features);
                if(prediction != 3){
                    num_errors += 1;
                    System.out.println("Error");
                }
            }
            Assertions.assertTrue(num_errors < 5);
        }
        catch (IOException e){
            System.out.println(e);
        }


        /*
         Perform test for class 4
         */
        String class_4_base_file_path = "screenshots/class_4_border/class_4_";
        int num_borders_4 = 22;
        num_errors = 0;
        try {
            for (int i = 0; i < num_borders_4; i++) {
                String file_path = class_4_base_file_path + Integer.toString(i) + ".jpg";
                BufferedImage image = ImageIO.read(new File(file_path));
                int prediction = ObtainData.average_percent_difference(image, class_border_matrix, num_classes, border_height, num_features);
                if(prediction != 4){
                    num_errors += 1;
                    System.out.println("Error");
                }
            }
            Assertions.assertTrue(num_errors < 5);
        }
        catch (IOException e){
            System.out.println(e);
        }

        /*
         Perform test for the negative classes
         */
        String class_0_base_file_path = "screenshots/negative_borders/negative_";
        int num_borders_0 = 471;
        num_errors = 0;
        try {
            for (int i = 0; i < num_borders_0; i++) {
                String file_path = class_0_base_file_path + Integer.toString(i) + ".jpg";
                BufferedImage image = ImageIO.read(new File(file_path));
                int prediction = ObtainData.average_percent_difference(image, class_border_matrix, num_classes, border_height, num_features);
                if(prediction != 0){
                    num_errors += 1;
                    System.out.println("Error");
                }
            }
            Assertions.assertTrue(num_errors < 5);
        }
        catch (IOException e){
            System.out.println(e);
        }

    }
}