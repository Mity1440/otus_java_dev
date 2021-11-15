package exceptions;

public class TestMethodPerformingException extends MethodPerformingException{
    public TestMethodPerformingException(String message, String methodName) {
        super(message,  methodName);
    }
}
