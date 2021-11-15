package helpers;

public class ConsoleHelper {

    public static void write(String message){
        System.out.println(message);
    }

    public static void writeTestClassHeader(String className) {

        write("---------------------------------------------------------------");
        write(String.format("Testing class - %s", className));

    }

    public static void writeMainHeader(long count) {
        write(String.format("Total testing classes - %d", count));
    }

    public static void writeTotalClassInfo(int countTotalTests) {
        write(String.format("Total tests - %d", countTotalTests));
    }

    public static void writeSuccesInfo(int count) {
        write(String.format("Success - %d", count));
    }

    public static void writeMethodName(String name) {
        write(String.format("     Method name: %s", name));
    }

    public static void writeStackTraceInfo(String stackTraceInfo) {

        write("          Stack trace: ");
        String[] stackTraceInfoInArray =  stackTraceInfo.split("\n");
        for (String s: stackTraceInfoInArray){
            write(String.format("                %s", s));
        }

    }
}


