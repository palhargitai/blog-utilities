package eu.palhargitai.examples.utilities.adapter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Simple example of an adapter.
 * @author Pal Hargitai
 *
 * @param <Type> The collection type.
 */
public class AdaptableCollection<Type> extends LinkedList<Type> implements Adaptable {

	/** Serial id. */
	private static final long serialVersionUID = 8912408954782650938L;

	/** {@inheritDoc} */
	@Override
	public <AdapterType> AdapterType adapt(Class<AdapterType> adapterType) {
		AdapterType result;
		if (Set.class.equals(adapterType)) {
			result = adapterType.cast(new HashSet<Type>(this));
		} else {
			result = null;
		}
		return result;
	}
}
