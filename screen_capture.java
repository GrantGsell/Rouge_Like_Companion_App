import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class screen_capture {

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
                    screen captures in miliseconds.
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
    Name       :
    Purpose    :
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

        // Read in the border image 'database'
        int[][] border_data = new int[345][400*3*2];
        ObtainData.read_border_data(border_data);

        // Threshold number of similarities
        int threshold = 150;

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
                BufferedImage crop = capture.getSubimage(x_offset, y_offset, width, height);

                // Obtain number of similarities
                int num_same_elem = ObtainData.compare_image_to_border_data_int_ret(crop,border_data);

                // Set threshold to save if reached
                if(num_same_elem > threshold){
                    // Print out message
                    System.out.println("Notification box found!");

                    // Generate iterative file path
                    String index = Integer.toString(itr);
                    String file_path = cwd + index + ".jpg";
                    itr += 1;

                    // Write image buffer to file
                    File outputfile = new File(file_path);
                    ImageIO.write(crop, "jpg", outputfile);

                }
                // Time delay between screenshots
                Thread.sleep(delay_time_ms);
            }
            catch (AWTException | IOException | InterruptedException e) {
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
    public static void main(String[] args) {
        String file_name = "test";
        boolean active_capture = false;

        if(!active_capture) {
            int num_pics = 25;
            int delay_time = 250;
            double run_time = (delay_time / 1000.0) * num_pics;
            System.out.printf("Ideal program runtime: %.3f seconds\n", run_time);
            screen_capture obj = new screen_capture();

            // Time and execute the funciton
            double start = System.currentTimeMillis();
            obj.screenshot(file_name, num_pics, delay_time);
            double end = System.currentTimeMillis();
            double time_delta = ((end - start) / 1000.0);
            System.out.printf("Actual program runtime: %.3f seconds\n", time_delta);

            // Determine time taken for one screenshot
            double screenshot_overhead = (time_delta - run_time) / num_pics;
            System.out.printf("Time for 1 screenshot: %.3f seconds\n", screenshot_overhead);
        }
        else{
            screen_capture.active_capture(file_name, 250);
        }


    }

}
