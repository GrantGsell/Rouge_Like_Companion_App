import org.ejml.simple.SimpleMatrix;

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

                // Write image buffer to file
                File outputfile = new File(file_path);
                ImageIO.write(capture, "jpg", outputfile);

                // Time delay between screenshots
                Thread.sleep(delay_time_ms);
            }
            catch (AWTException | IOException | InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    public static void matrix_test(){
        double[][] firstMatrix = {
                new double[]{1d, 5d},
                new double[]{2d, 3d},
                new double[]{1d, 7d}
        };
        SimpleMatrix firstMatrix_Test = new SimpleMatrix(firstMatrix);

        return;
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
        int num_pics = 25;
        int delay_time = 250;
        double run_time = (delay_time / 1000.0) * num_pics;
        System.out.printf("Ideal program runtime: %.3f seconds\n", run_time);
        screen_capture obj = new screen_capture();

        // Matrix test
        obj.matrix_test();

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

}
