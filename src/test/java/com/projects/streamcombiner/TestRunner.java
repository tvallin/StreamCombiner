package com.projects.streamcombiner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

@RunWith(Suite.class)
@SuiteClasses({StreamCombinerTest.class, ClientProcessorTest.class})

public class TestRunner
{
    public static void main()
    {
    	Result result = JUnitCore.runClasses(StreamCombinerTest.class);
        for (Failure failure : result.getFailures()) 
        {
          System.out.println(failure.toString());
        }
    }
}
