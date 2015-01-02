package eu.palhargitai.examples.utilities.adapter;

/**
 * An interface for adapable classes.
 *
 * @author Pal Hargitai
 */
public interface Adaptable {

	/**
	 * Adapts a class to another class.
	 * @param <AdapterType> The type to adapt to.
	 * @param adapterType The type to adapt to.
	 * @return An instance of the adapt type.
	 */
	<AdapterType> AdapterType adapt(Class<AdapterType> adapterType);
}
