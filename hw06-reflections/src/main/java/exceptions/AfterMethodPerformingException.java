package exceptions;

public class AfterMethodPerformingException extends MethodPerformingException{
    public AfterMethodPerformingException(String message, String methodName) {
        super(message, methodName);
    }
}
