import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Hashtable;

public class MultiThreading extends Thread{
    private Thread t;
    private String thread_name;
    private SimpleMatrix initial_parameters;
    private SimpleMatrix input_data;
    private SimpleMatrix output_data;
    private double lambda;
    private double alpha;
    private String class_char;
    public boolean is_alive;
    public SimpleMatrix learned_parameters;

    MultiThreading(String name, SimpleMatrix init_params, SimpleMatrix inp_data, SimpleMatrix out_data,
                   double lam, double alp, String clss_char){
        thread_name = name;
        initial_parameters = init_params;
        input_data = inp_data;
        output_data = out_data;
        lambda = lam;
        alpha = alp;
        class_char = clss_char;
        is_alive = true;
        //System.out.format("Creating thread : %s\n", name);
    }

    public void run() {
        System.out.println("Running " +  thread_name );
        try {
            int max_iterations = 3000;
            learned_parameters = OneVsAllChar.gradient_descent(initial_parameters, input_data, output_data, lambda, alpha, class_char, max_iterations);
            is_alive = false;
            Thread.sleep(1);
        } catch (InterruptedException  e) {
           // System.out.println("Thread " +  thread_name + " interrupted.");
        }
        System.out.println("Thread " +  thread_name + " exiting.");
    }

    public void start () {
        //System.out.println("Starting " +  thread_name );
        if (t == null) {
            t = new Thread (this, thread_name);
            t.start();
        }
    }
}