import org.ejml.simple.SimpleMatrix;

public class GDMultiThreading extends OneVsAllChar implements Runnable {

    private Thread t;
    private String thread_name;
    private SimpleMatrix initial_parameters;
    private SimpleMatrix input_data;
    private SimpleMatrix output_data;
    private double lambda;
    public SimpleMatrix temp_grad;

    GDMultiThreading(String name, SimpleMatrix init_params, SimpleMatrix inp_data, SimpleMatrix out_data,
                   double lam){
        thread_name = name;
        initial_parameters = init_params;
        input_data = inp_data;
        output_data = out_data;
        lambda = lam;
        //System.out.format("Creating thread : %s\n", name);
    }

    public void run() {
        //System.out.println("Running " +  thread_name );
        try {
            temp_grad = lr_gradient_regularized(initial_parameters, input_data, output_data, lambda);
        } catch (Exception  e) {
            System.out.println("Thread " +  thread_name + " interrupted.");
        }
        //System.out.println("Thread " +  thread_name + " exiting.");
    }

    public void start () {
        //System.out.println("Starting " +  thread_name );
        if (t == null) {
            t = new Thread (this, thread_name);
            t.start();
            //System.out.println(t.isAlive());
        }
    }
}
