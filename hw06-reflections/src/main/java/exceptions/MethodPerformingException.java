package exceptions;

public abstract class MethodPerformingException extends Exception{

    private String methodName;

    public MethodPerformingException(String message, String methodName) {
        super(message);
        this.methodName = methodName;
    }
}
