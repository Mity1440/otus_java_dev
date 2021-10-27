package examples;

import annotations.After;
import annotations.Before;
import annotations.Test;
import helpers.ConsoleHelper;

public class SecondExample {

    @After
    public void IamAnnotatedAfter(){
        ConsoleHelper.write("I am from annotated After");
    }

    @After
    public void IamAnnotatedAfterOne(){
        ConsoleHelper.write("I am from annotated After one");
    }

    @After
    public void IamAnnotatedAfterTwo(){
        ConsoleHelper.write("I am from annotated After two");
    }

    @Before
    public void IamAnnotatedBefore(){
        ConsoleHelper.write("I am from annotated Before");
    }

    @Before
    public void IamAnnotatedBeforeOne(){
        ConsoleHelper.write("I am from annotated Before one");
    }

    @Test
    public void IamAnnotatedTest(){
        ConsoleHelper.write("I am from annotated Test");
    }

    @Test
    public void IamAnnotatedTestOne(){
        ConsoleHelper.write("I am from annotated Test one");
    }

}
