package eu.palhargitai.examples.utilities.generics;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import eu.palhargitai.examples.utilities.adapter.AdaptableCollection;

/**
 * Provides 
 * @author Pal Hargitai
 */
public class GenericsRevisitedTest {

	/** The collection for the tests. */
	private AdaptableCollection<String> collection;

	/** Sets up the test. Creates a collection and adds some values. */
	@Before
	public void setup() {
		collection = new AdaptableCollection<String>();
		collection.add("Test Value");
		collection.add("Other Value");
	}

	/**
	 * Test the use that is questionable but correct.
	 */
	@Test
	public void correctAdapter() {
		@SuppressWarnings("unchecked")
		// The first hint this is not always the right thing to do.
		Set<String> set = collection.adapt(Set.class);
		Assert.assertTrue(set.contains("Test Value"));
		Assert.assertTrue(set.contains("Other Value"));
	}

	/**
	 * Test the use that is not just questionable but incorrect.
	 */
	@Test
	public void incorrectAdapter() {
		@SuppressWarnings("unchecked")
		// The first hint this is not always the right thing to do.
		Set<Integer> set = collection.adapt(Set.class);
		Assert.assertTrue(set.add(42));
		Assert.assertTrue(set.contains("Other Value"));
	}
}
