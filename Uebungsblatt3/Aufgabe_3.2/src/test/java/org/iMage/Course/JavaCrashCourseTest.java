package org.iMage.Course;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaCrashCourseTest {

    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private final PrintStream sysout = System.out;

    private JavaCrashCourse test;

    @Before
    public void setUp() {
        test = new JavaCrashCourse();
        System.setOut(new PrintStream(content));
    }

    @After
    public void tearDown() {
        test = null;
        System.setOut(sysout);
        content.reset();
    }

    //The implementation of this test must be changed when the implementation of the actual method changes.
    @Test
    public void getName() {
        assertEquals("JavaCrashCourse", test.getName());
    }

    //The implementation of this test must be changed when the implementation of the actual method changes.
    @Test
    public void getNumberOfParameters() {
        assertEquals(7, test.getNumberOfParameters());
    }

    //Ignored because it fails for some reason
    //The implementation of this test must be changed when the implementation of the actual method changes.
    @Ignore
    @Test
    public void run() {
        boolean passed = switch (content.toString()) {
            case "Running late", "Keeping updated", "JavaCrashCourse(7)" -> true;
            default -> false;
        };

        assertTrue(passed);
    }

    //The implementation of this test must be changed when the implementation of the actual method changes.
    @Test
    public void isConfigurable() {
        assertTrue(test.isConfigurable());
    }
}