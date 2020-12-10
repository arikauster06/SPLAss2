package bgu.spl.mics;

import java.util.Scanner;

//Coded By Ron Rachev
public class InvokeTest {

    public static void main(String [] args)
    {

        Scanner in = new Scanner(System.in);
        System.out.println("Type 3 to run logical tests");
        System.out.println("Type 2 to generate tests into the tests.json file");
        System.out.println("Type 1 to run the tests from the tests.json file");
        int choosenOption = in.nextInt();
        Tester myTester = new Tester();
//        myTester.runLogicalTests();
        if(choosenOption == 2)  myTester.generateTests();  else
            if(choosenOption == 1)
            myTester.runTestsFromFile();
            else if(choosenOption == 3)
            myTester.runLogicalTests();
    }
}
