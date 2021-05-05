import org.ejml.simple.SimpleMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class screen_capture {
    public static boolean timer_flag = false;
    public static Timer event_timer = new Timer();

    /*
    Name       : screenshot
    Purpose    : To take a specified number of screenshots of the users primary
                    screen with a given interval of time between screenshots.
    Parameters :
                 file_name, a string denoting the base name of the iterative
                    storage image.
                 num_screenshots, an integer denoting the number of screenshots
                    to be taken.
                 delay_time_ms, a integer denoting the time interval between
                    screen captures in milliseconds.
    Return     : None
    Notes      : None
     */
    public static void screenshot(String file_name, int num_screenshots, int delay_time_ms) {
        // Obtain the cwd
        String cwd = System.getProperty("user.dir") + "\\screenshots\\" + file_name + "_";

        // Take infinite screenshots
        System.out.println("Imaging Started");

        // Take n screenshots
        for(int i = 0; i < num_screenshots; i++){
            if(i % 100 == 0 && i != 0){
                int temp_break_point = 5;
                try {
                    Thread.sleep(5000);
                }
                catch (Exception e){
                    System.out.println(e);
                }
            }
            // Generate iterative file path
            String index = Integer.toString(i);
            String file_path = cwd + index + ".jpg";

            // Screenshot
            try {
                // Obtain image
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage capture = new Robot().createScreenCapture(screenRect);
                int x_offset = 592;
                int width = 400;
                int y_offset = 767;
                int height = 77;
                BufferedImage crop = capture.getSubimage(x_offset, y_offset, width, height);

                // Write image buffer to file
                File outputfile = new File(file_path);
                ImageIO.write(crop, "jpg", outputfile);

                // Time delay between screenshots
                Thread.sleep(delay_time_ms);
            }
            catch (AWTException | IOException | InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    /*
    Name       : active_capture
    Purpose    : To actively take screenshots and look for notification boxes.
    Parameters :
    Return     :
    Notes      :
     */
    public static void active_capture(String file_name, int delay_time_ms){
        // Obtain the cwd
        String cwd = System.getProperty("user.dir") + "\\screenshots\\" + file_name + "_";

        // Take infinite screenshots
        System.out.println("Imaging Started");

        // Iterative file index
        int itr = 0;

        // Testing variables
        int border_height = 3;
        int num_border_features = 7200;
        int num_border_classes = 4;

        // Obtain border class base matrix
        double[][] class_border_matrix = ObtainData.average_border_color_per_class(num_border_classes, border_height, num_border_features);

        // Create OneVsAll Object and train it
        //OneVsAllChar obj = new OneVsAllChar();
        //obj.top_one_vs_all_training(7979, 810);
        //SimpleMatrix test = obj.test_learned_parameters;
        //double[] test_2 = obj.norm_mean;
        String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "0", "4", "7", "'", "SPACE", "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP",
                "ERRAPT"};
        // Read in learned parameters
        SimpleMatrix learned_parameters = MySQLAccess.read_parameter_cols(characters.length, 811, characters);

        // Generate hashtable for incorrect word comparison
        Hashtable<Integer, ArrayList<String>> word_ht =  MySQLAccess.generate_word_hash_table();

        while(true){
            // Screenshot
            try {
                // Obtain image
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage capture = new Robot().createScreenCapture(screenRect);
                int x_offset = 592;
                int width = 400;
                int y_offset = 767;
                int height = 77;

                // Crop image to new image
                BufferedImage crop = capture.getSubimage(x_offset, y_offset, width, height);

                /*
                Class 3 Test
                 */
                //BufferedImage crop = ImageIO.read(new File("screenshots/class_3_border/class_3_0.jpg"));

                int prediction = ObtainData.average_percent_difference(crop, class_border_matrix, num_border_classes, border_height, num_border_features);

                // Set threshold to save if reached
                if(prediction != 0 && !timer_flag){
                    // Set timer flag high and start the timer
                    timer_flag = true;
                    event_timer.schedule(new FlagSetTask(), 2000);

                    // Print out message
                    System.out.println("Notification box found!");

                    // Make prediction on the new found notification box.
                    //String guess = obj.test_new_image(obj.characters_list.length, obj.test_learned_parameters, "", obj.characters_list, crop);
                    String guess = OneVsAllChar.test_new_image(characters.length, learned_parameters, "", characters, crop);
                    if(guess != null) {
                        MySQLAccess.read_from_database(guess, word_ht);
                    }

                    // Generate iterative file path
                    String index = Integer.toString(itr);
                    String file_path = cwd + index + ".jpg";
                    itr += 1;

                    // Write image buffer to file
                    File outputfile = new File(file_path);
                    ImageIO.write(crop, "jpg", outputfile);

                }
                //

                // Time delay between screenshots
                Thread.sleep(delay_time_ms);
            }
            catch (AWTException | IOException | InterruptedException e) {
                System.out.println(e);
            }
        }


    }


    static class FlagSetTask extends TimerTask{
        public void run(){
            System.out.println("Timer Stopped, Imaging Resuming!");
            timer_flag = false;
            //event_timer.cancel();
        }
    }



    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String[] args) {
        String file_name = "temp";
        boolean active_capture = true;

        if(active_capture) {
            screen_capture.active_capture(file_name, 50);
        }
        else{
            int num_pics = 50;
            int delay_time = 250;
            double run_time = (delay_time / 1000.0) * num_pics;
            System.out.printf("Ideal program runtime: %.3f seconds\n", run_time);
            screen_capture obj = new screen_capture();

            // Time and execute the function
            double start = System.currentTimeMillis();
            obj.screenshot(file_name, num_pics, delay_time);
            double end = System.currentTimeMillis();
            double time_delta = ((end - start) / 1000.0);
            System.out.printf("Actual program runtime: %.3f seconds\n", time_delta);

            // Determine time taken for one screenshot
            double screenshot_overhead = (time_delta - run_time) / num_pics;
            System.out.printf("Time for 1 screenshot: %.3f seconds\n", screenshot_overhead);
        }
    }
}
