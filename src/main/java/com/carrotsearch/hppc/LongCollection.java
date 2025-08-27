package com.carrotsearch.hppc;

import com.carrotsearch.hppc.predicates.LongPredicate;

/**
 * A collection allows basic, efficient operations on sets of elements
 * (difference and intersection).
 */
@com.carrotsearch.hppc.Generated(date = "2024-06-04T15:20:17+0200", value = "KTypeCollection.java")
public interface LongCollection extends LongContainer {
	/**
	 * Removes all occurrences of <code>e</code> from this collection.
	 *
	 * @param e Element to be removed from this collection, if present.
	 * @return The number of removed elements as a result of this call.
	 */
	public int removeAll(long e);

	/**
	 * Removes all elements in this collection that are present in <code>c</code>.
	 *
	 * @return Returns the number of removed elements.
	 */
	public int removeAll(LongLookupContainer c);

	/**
	 * Removes all elements in this collection for which the given predicate returns
	 * <code>true</code> .
	 *
	 * @return Returns the number of removed elements.
	 */
	public int removeAll(LongPredicate predicate);

	/**
	 * Keeps all elements in this collection that are present in <code>c</code>.
	 * Runs in time proportional to the number of elements in this collection.
	 * Equivalent of sets intersection.
	 *
	 * @return Returns the number of removed elements.
	 */
	public int retainAll(LongLookupContainer c);

	/**
	 * Keeps all elements in this collection for which the given predicate returns
	 * <code>true</code>.
	 *
	 * @return Returns the number of removed elements.
	 */
	public int retainAll(LongPredicate predicate);

	/**
	 * Removes all elements from this collection.
	 *
	 * @see #release()
	 */
	public void clear();

	/**
	 * Removes all elements from the collection and additionally releases any
	 * internal buffers. Typically, if the object is to be reused, a simple
	 * {@link #clear()} should be a better alternative since it'll avoid
	 * reallocation.
	 *
	 * @see #clear()
	 */
	public void release();
}
