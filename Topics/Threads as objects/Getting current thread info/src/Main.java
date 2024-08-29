class Info {

    public static void printCurrentThreadInfo() {
        // get the thread and print its info

        Thread currentThread = Thread.currentThread();

        // Retrieve the thread's name
        String threadName = currentThread.getName();

        // Retrieve the thread's priority
        int threadPriority = currentThread.getPriority();

        // Print the thread's information
        System.out.println("name: " + threadName);
        System.out.println("priority: " + threadPriority);
    }
}