package ru.otus;

public class Demo {

    private final Object monitor = new Object();
    private String currentThreadWaiterName;

    public void run(){

        var t1 = new Thread(this::outNumbers);
        var t2 = new Thread(this::outNumbers);

        t1.setName("TheFirst");
        t2.setName("TheSecond");

        currentThreadWaiterName = "TheSecond";

        t1.start();
        t2.start();

    }

    private void outNumbers(){

        for (int i = 1; i < 10; i++){
            outNumber(Thread.currentThread().getName(), i);
        }

        for (int i = 10; i > 0; i--){
            outNumber(Thread.currentThread().getName(), i);
        }

    }

    private void outNumber(String threadName, int number){

        synchronized (monitor){

            try {
                while (notTheTurnCame(threadName)){
                    monitor.wait();
                }

                System.out.println(String.format("%s : %d", threadName, number));
                changeCurrentThreadWaiterName(threadName);
                sleep();

                monitor.notifyAll();
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }

        }

    }

    private void changeCurrentThreadWaiterName(String threadName) {
        currentThreadWaiterName = threadName;
    }

    private boolean notTheTurnCame(String threadName) {
        return threadName.equals(currentThreadWaiterName);
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
