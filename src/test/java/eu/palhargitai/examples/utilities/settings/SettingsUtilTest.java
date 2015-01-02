package eu.palhargitai.examples.utilities.settings;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Settings util test.
 *
 * @author Pal Hargitai
 */
public class SettingsUtilTest {

	/** Existing key 1. */
	private static final String EXISTING_KEY_1 = "eu.palhargitai.some.key";
	/** Existing value 1. */
	private static final String EXISTING_VALUE_1 = "Some value";
	/** Existing key 2. */
	private static final String EXISTING_KEY_2 = "eu.palhargitai.some.other.key";
	/** Existing value 2. */ 
	private static final String EXISTING_VALUE_2 = "Some other value.";
	/** Non-Existing key 1. */
	private static final String NON_EXISTING_KEY_1 = "some.key";
	/** Non-Existing value 1. */
	private static final String NON_EXISTING_VALUE_1 = "Some nonexisting value";
	/** Non-Existing key 2. */
	private static final String NON_EXISTING_KEY_2 = "some.other.key";
	/** Non-Existing value 2. */
	private static final String NON_EXISTING_VALUE_2 = "Some other nonexisting value";
	/** An existing location. */
	private static final String EXISTING_PROPERTIES_LOCATION = "/settings.properties";
	/** A non-existing location. */
	private static final String NON_EXISTING_PROPERTIES_LOCATION = "/non-existing-test-settings.properties";

	/** Tests getting a property for a non-existing key, thus returning the default value. */
	@Test
	public void testGetPropertyDefaultValue() {
		final String value1 = SettingsUtil.getValue(NON_EXISTING_KEY_1, NON_EXISTING_VALUE_1);
		Assert.assertEquals(NON_EXISTING_VALUE_1, value1);
		final String value2 = SettingsUtil.getValue(NON_EXISTING_KEY_2, NON_EXISTING_VALUE_2);
		Assert.assertEquals(NON_EXISTING_VALUE_2, value2);
	}

	/** Tests getting a property for an existing key, thus returning the real value. */
	@Test
	public void testGetProperty() {
		final String value1 = SettingsUtil.getValue(EXISTING_KEY_1, NON_EXISTING_VALUE_1);
		Assert.assertEquals(EXISTING_VALUE_1, value1);
		final String value2 = SettingsUtil.getValue(EXISTING_KEY_2, NON_EXISTING_VALUE_2);
		Assert.assertEquals(EXISTING_VALUE_2, value2);
	}

	/** Tests classloader cannot find resource. */
	@Test
	public void testClassloaderResourceNotFound() {
		// Reset value to something non-existing.
		SettingsUtil.INSTANCE.resetProperties();
		SettingsUtil.INSTANCE.updatePropertiesLocation(NON_EXISTING_PROPERTIES_LOCATION);

		final String value1 = SettingsUtil.getValue(EXISTING_KEY_1, NON_EXISTING_VALUE_1);
		Assert.assertEquals(NON_EXISTING_VALUE_1, value1);
		final String value2 = SettingsUtil.getValue(EXISTING_KEY_2, NON_EXISTING_VALUE_2);
		Assert.assertEquals(NON_EXISTING_VALUE_2, value2);
	}

	/** Tests stream could not be read. */
	@Test
	public void testClassloaderLoadException() {
		// Reset properties and replace classloader.
		SettingsUtil.INSTANCE.resetProperties();
		final ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader();
		final ClassLoader newClassloader = new InterceptingInputStreamClassLoader(new NonReadingInputStream());
		Thread.currentThread().setContextClassLoader(newClassloader);
		// We assume the reading has gone wrong.
		final String value1 = SettingsUtil.getValue(EXISTING_KEY_1, NON_EXISTING_VALUE_1);
		Assert.assertEquals(NON_EXISTING_VALUE_1, value1);
		final String value2 = SettingsUtil.getValue(EXISTING_KEY_2, NON_EXISTING_VALUE_2);
		Assert.assertEquals(NON_EXISTING_VALUE_2, value2);
		// Return original classloader.
		Thread.currentThread().setContextClassLoader(originalClassloader);
	}
	
	/** Tests stream could not be closed. */
	@Test
	public void testClassloaderCloseException() {
		// Reset properties and replace classloader.
		SettingsUtil.INSTANCE.resetProperties();
		final ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader();
		final InputStream resourceStream = getClass().getResourceAsStream(EXISTING_PROPERTIES_LOCATION);
		final ClassLoader newClassloader = new InterceptingInputStreamClassLoader(new NonClosingInputStream(resourceStream));
		Thread.currentThread().setContextClassLoader(newClassloader);
		// We assume the rest of the reading was done properly.
		final String value1 = SettingsUtil.getValue(EXISTING_KEY_1, NON_EXISTING_VALUE_1);
		Assert.assertEquals(EXISTING_VALUE_1, value1);
		final String value2 = SettingsUtil.getValue(EXISTING_KEY_2, NON_EXISTING_VALUE_2);
		Assert.assertEquals(EXISTING_VALUE_2, value2);
		// Return original classloader.
		Thread.currentThread().setContextClassLoader(originalClassloader);
	}

	/**
	 * A classloader that intercepts the {@link ClassLoader#getResourceAsStream(String)} method and gives an alternative input stream.
	 * @author Pal Hargitai
	 */
	public class InterceptingInputStreamClassLoader extends ClassLoader {
		/** The alternative input stream. */
		private InputStream inputStream;
		/**
		 * Constructs the classloader with an alternative input stream.
		 * @param inputStream The input stream to replace the original.
		 */
		public InterceptingInputStreamClassLoader(InputStream inputStream) {
			this.inputStream = inputStream;
		}
		/**
		 * Replaces the input stream with a different one.
		 * {@inheritDoc}
		 */
		@Override
		public InputStream getResourceAsStream(String name) {
			return inputStream;
		}
	}

	/**
	 * An input stream that throws an exception on reading.
	 * @author Pal Hargitai
	 */
	public class NonReadingInputStream extends InputStream {
		/**
		 * Always throws an exception.
		 * {@inheritDoc}
		 */
		@Override
		public int read() throws IOException {
			throw new IOException("Error reading!");
		}
	}

	/**
	 * An input stream that cannot close.
	 * @author Pal Hargitai
	 */
	public class NonClosingInputStream extends InputStream {
		/** The input stream delegate. */
		private InputStream delegate;
		/**
		 * Constructs the input stream.
		 * @param delegate The delegate to delegate the reading to.
		 */
		public NonClosingInputStream(InputStream delegate) {
			this.delegate = delegate;
		}

		/** {@inheritDoc} */
		@Override
		public int read() throws IOException {
			return delegate.read();
		}

		/**
		 * Always throws an exception, though closes the delegate.
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
			delegate.close();
			throw new IOException("Error reading!");
		}
	}
}
