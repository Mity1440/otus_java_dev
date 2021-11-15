package examples;

import annotations.Test;

public class NullPointerExceptionExample {

    @Test
    public void testException(){
        String s = null;
        testExceptionThrowException(s);
    }

    public void testExceptionThrowException(String s){
       s.split(" ");
    }

}
