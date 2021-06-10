import org.ejml.simple.SimpleMatrix;
import javax.imageio.ImageIO;
import javax.swing.border.Border;
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
    Notes      : This method is used to collect border data for when its known that an item will be picked up.
     */
    public static void screenshot(String file_name, int num_screenshots, int delay_time_ms) {
        // Obtain the cwd
        String cwd = System.getProperty("user.dir") + "\\screenshots\\" + file_name + "_";

        // Take infinite screenshots
        System.out.println("Imaging Started");

        // Take n screenshots
        for(int i = 0; i < num_screenshots; i++){
            if(i % 100 == 0 && i != 0){
                try {
                    Thread.sleep(300);
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

        // Obtain border class base matrix
        double[][] class_border_matrix = BorderData.average_border_values_per_class();

        // Set all possible characters string
        String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "4", "5", "7", "8", "'", "-", "SPACE", "ERRN", "ERRL",
                "ERRM", "ERRT", "ERRU", "ERRAP", "ERRAPT"};

        // Generate hashtable for incorrect word comparison
        Hashtable<Integer, ArrayList<String>> word_ht =  MySQLAccess.generate_word_hash_table();

        // Generate User Interface
        UserInterface custom_ui = new UserInterface();
        custom_ui.run_user_interface();

        while(true){
            // Screenshot
            try {
                // Obtain full screen image then cropped images
                BufferedImage image_crop = image_method();
                BufferedImage border_crop = image_crop.getSubimage( 25, 0, image_crop.getWidth() - 25, image_crop.getHeight());
                int border_class = BorderData.get_border_class(border_crop, class_border_matrix);

                // Set threshold to save if reached
                if(border_class != 0 && border_class != 4 && !timer_flag){
                    // Set timer flag high and start the timer
                    timer_flag = true;
                    event_timer.schedule(new FlagSetTask(), 2000);

                    // Make prediction on the new found notification box.
                    String guess = NeuralNetwork.test_new_image("", characters, image_crop);
                    if(guess != null) {
                        MySQLAccess.read_from_database(guess, word_ht, custom_ui);
                    }

                    // Generate iterative file path
                    String index = Integer.toString(itr);
                    String file_path = cwd + index + ".jpg";
                    itr += 1;

                    // Write image buffer to file
                    File outputfile = new File(file_path);
                    ImageIO.write(border_crop, "jpg", outputfile);

                }

                // Time delay between screenshots
                Thread.sleep(delay_time_ms);
            }
            catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        }


    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    static class FlagSetTask extends TimerTask{
        public void run(){
            System.out.println("Timer Stopped, Imaging Resuming!");
            timer_flag = false;
        }
    }

    
    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    private static BufferedImage image_method(){
        BufferedImage crop = null;

        // Crop rectangle variables
        int x_offset = 567;
        int width = 425;
        int y_offset = 767;
        int height = 77;
        int ideal_img_height = 864;
        int ideal_img_width = 1536;

        try {
            // Obtain image
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);

            // Resize image to fit crop offsets
            if(capture.getWidth() != ideal_img_width && capture.getHeight() != ideal_img_height) {
                capture = image_resize(capture, ideal_img_width, ideal_img_height);
            }

            // Crop original image to isolate notification box
            crop = capture.getSubimage(x_offset, y_offset, width, height);

        }catch(AWTException e){
            System.out.println(e);
        }
        return crop;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    private static BufferedImage image_resize(BufferedImage img, int width, int height){
        // Set new image containers
        Image temp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage img_resize = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Resize old image into new image
        Graphics2D g2d = img_resize.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();

        return img_resize;
    }


    /*
    Name       :
    Purpose    :
    Parameters :
    Return     :
    Notes      :
     */
    public static void main(String[] args) {
        // Normal Running code
        String file_name = "temp";
        boolean active_capture = true;

        if(active_capture) {
            screen_capture.active_capture(file_name, 50);
        }
        else{
            // Passive screen captures for data collection
            int num_pics = 100;
            int delay_time = 250;
            screen_capture obj = new screen_capture();
            obj.screenshot(file_name, num_pics, delay_time);
        }
    }
}
