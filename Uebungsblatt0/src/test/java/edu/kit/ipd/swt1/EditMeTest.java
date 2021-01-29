package edu.kit.ipd.swt1;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * An exapmle class to help practice test capabilities.
 * @author tarik
 *
 */
public class EditMeTest {

	private EditMe editMe;

	/**
	 * Set up method before each test.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		editMe = new EditMe();
	}

	/**
	 * Clean up method after each test.
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		editMe = null;
	}

	/**
	 * Method to test matriculation number.
	 */
	@Test
	public void testMatNum() {
		assertEquals(2315017, editMe.getMatNum());
	}

	/**
	 * Method to test getFoo().
	 */
	@Test
	public void testFoo() {
		assertEquals("bar", editMe.getFoo());
	}
}
