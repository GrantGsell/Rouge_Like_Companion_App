public class TestMultiThread extends Thread{
    private Thread t;
    private String threadName;

    TestMultiThread( String name) {
        threadName = name;
        System.out.println("Creating " +  threadName );
    }

    public void run() {
        System.out.println("Running " +  threadName );
        try {
            for(int i = 4; i > 0; i--) {
                System.out.println("Thread: " + threadName + ", " + i);
                // Let the thread sleep for a while.
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " +  threadName + " interrupted.");
        }
        System.out.println("Thread " +  threadName + " exiting.");
    }

    public void start () {
        System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }
}

class Test2Thread {

    public static void main(String args[]) {
        TestMultiThread T1 = new TestMultiThread( "Thread-1");
        T1.start();
        System.out.println(T1.isAlive());
        //TestMultiThread T2 = new TestMultiThread( "Thread-2");
        //T2.start();
    }
}

