package eu.palhargitai.examples.utilities.generics;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Show some aspects of generics in Java.
 *
 * @author Pal Hargitai
 */
public class GenericsTest {

	/** A string list. */
	private List<String> stringList = new LinkedList<String>();

	/** Show that equals of Class does not use the generic type information of the object. */
	@Test
	public void testErasure() {
		List<Integer> integerList = new LinkedList<Integer>();
		Assert.assertEquals(integerList.getClass(), stringList.getClass());
	}

	/** Show that type erasure allows very unclean code to be written. */
	@Test
	public void testErasureCast() {
		@SuppressWarnings("rawtypes")
		List rawList = stringList;
		@SuppressWarnings("unchecked")
		List<Integer> integerList = rawList;
		Assert.assertNotNull(integerList);
	}

	/** Show that types should not be confused. */
	@Test
	public void testBadCasting() {
		String someString = "";
		Object someObject = someString;
		try {
			Integer someInteger = (Integer) someObject;
			Assert.assertNotNull(someInteger);
			Assert.fail("No compiler should accept this!");
		} catch (ClassCastException e) {
			// Very obvious!
		}
	}

	/** Show that types should not be confused. */
	@Test
	public void testWorseCasting() {
		String someString = null;
		Object someObject = someString;
		Integer someInteger = (Integer) someObject;
		Assert.assertNull(someInteger);
	}

	/** Show that the type information of declared fields is most certainly not lost, just well hidden. */
	@Test
	public void testParameterizedType() {
		try {
			// Get the field.
			Field stringListField = GenericsTest.class.getDeclaredField("stringList");
			Assert.assertTrue(stringListField.getGenericType() instanceof ParameterizedType);
			ParameterizedType stringListGenericType = (ParameterizedType) stringListField.getGenericType();
			// Make assertions about the field type.
			Assert.assertEquals(List.class, stringListGenericType.getRawType());
			Assert.assertEquals(1, stringListGenericType.getActualTypeArguments().length);
			Assert.assertEquals(String.class, stringListGenericType.getActualTypeArguments()[0]);
		} catch (SecurityException e) {
			Assert.fail(e.getMessage());
		} catch (NoSuchFieldException e) {
			Assert.fail(e.getMessage());
		}
	}
}
