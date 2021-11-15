package exceptions;

public class BeforeMethodPerformingException extends MethodPerformingException{

    public BeforeMethodPerformingException(String message, String methodName) {
        super(message, methodName);
    }
}
