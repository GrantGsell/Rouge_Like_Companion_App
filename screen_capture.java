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
    private static final String[] characters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "4", "5", "7", "8", "'", "-", "SPACE",
            "ERRN", "ERRL", "ERRM", "ERRT", "ERRU", "ERRAP", "ERRAPT"};

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
    public static void screenshot(int num_screenshots, int delay_time_ms) {
        // Take infinite screenshots
        System.out.println("Imaging Started");

        // Take n screenshots
        for(int i = 0; i < num_screenshots; i++){
            if(i % 100 == 0 && i != 0){
                try {
                    Thread.sleep(300);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            // Screenshot
            try {
                // Obtain image
                BufferedImage image_crop = image_method();
                image_crop = image_crop.getSubimage( 25, 0, image_crop.getWidth() - 25, image_crop.getHeight());

                // Write image buffer to file
                image_data_collection(i, image_crop);

                // Time delay between screenshots
                Thread.sleep(delay_time_ms);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /*
    Name       : active_capture
    Purpose    : To actively take screenshots and look for notification boxes.
    Parameters :
                 delay_time_ms, an int denoting the time between each screenshot being taken.
                 collect_data, a boolean denoting if the image should be saved to a file location so it can be used as
                    a training example.
    Return     : None.
    Notes      : This imaging function will continue as long as the application is active. Additionally, images taken
                    outside of the game have not affected the application.
     */
    public static void active_capture(int delay_time_ms, boolean collect_data){
        // Take infinite screenshots
        System.out.println("Imaging Started");

        // Iterative file index
        int itr = 0;

        // Obtain border class base matrix
        double[][] class_border_matrix = BorderData.average_border_values_per_class();

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

                // Determine if the current image contains a notification box
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

                    // Collect notification box examples
                    if(collect_data) {
                        image_data_collection(itr, image_crop);
                        itr += 1;
                    }
                }

                // Time delay between screenshots
                Thread.sleep(delay_time_ms);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    /*
    Name       : FlagSetTask (class), run (method)
    Purpose    : To set a timer task that will delay another image from being processed for 2 second intervals.
    Parameters : None
    Return     : None
    Notes      : The 2 second length is determined in the class call. Additionally, once completed the timer flag will
                    be set low, thus allowing another image to be processed.
     */
    static class FlagSetTask extends TimerTask{
        public void run(){
            System.out.println("Timer Stopped, Imaging Resuming!");
            timer_flag = false;
        }
    }

    /*
    Name       : image_data_collection
    Purpose    : To take the notification box images and write them to a file location to be used to collect examples.
    Parameters :
                 file_number, an int that is used to generate an iterative file path name.
                 notification_box, the BufferedImage object that is written to the file location.
    Return     : None.
    Notes      : None.
     */
    private static void image_data_collection(int file_number, BufferedImage notification_box) {
        try {
            // Obtain the cwd and add file name
            String cwd = System.getProperty("user.dir") + "\\screenshots\\temp_";

            // Generate iterative file path
            String index = Integer.toString(file_number);
            String file_path = cwd + index + ".jpg";

            // Write image buffer to file
            File outputfile = new File(file_path);
            ImageIO.write(notification_box, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    
    /*
    Name       :
    Purpose    : To take a screenshot of the users game and crop the image to obtain only the area in which a
                    notification box would appear.
    Parameters : None
    Return     : A BufferedImage object containing only the portions of the original image that correspond to where a
                    notification box would appear.
    Notes      : If the screen capture does not have the dimensions of 1536 x 864, the image is resized to fit those
                    dimensions.
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
            e.printStackTrace();
        }
        return crop;
    }


    /*
    Name       : image_resize
    Purpose    : To resize a screen capture to have the dimensions width x height, which is necessary based on the
                    platform the training data was derived from.
    Parameters :
                 img, a BufferedImage object denoting the original screen capture that needs to be resized.
                 width, an int denoting the new width the image will be scaled to.
                 height, an int denoting the new height the image will be scaled to.
    Return     : A BufferedImage object denoting the resized old image.
    Notes      : None.
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
    Name       : main
    Purpose    : Client testing code.
    Parameters : Standard main arguments.
    Return     : None
    Notes      : Set active capture to true to perform continuous imaging, set to false to perform border data
                    collection.
     */
    public static void main(String[] args) {
        // Normal Running code
        boolean active_capture = true;

        if(active_capture) {
            screen_capture.active_capture(50, false);
        }
        else{
            // Passive screen captures for data collection
            int num_pics = 100;
            int delay_time = 250;
            screen_capture.screenshot(num_pics, delay_time);
        }
    }
}
