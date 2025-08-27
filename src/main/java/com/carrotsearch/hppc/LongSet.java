package com.carrotsearch.hppc;

/** A set of <code>long</code>s. */
@com.carrotsearch.hppc.Generated(date = "2024-06-04T15:20:17+0200", value = "KTypeSet.java")
public interface LongSet extends LongCollection {
	/**
	 * Adds <code>k</code> to the set.
	 *
	 * @return Returns <code>true</code> if this element was not part of the set
	 *         before. Returns <code>false</code> if an equal element is already
	 *         part of the set, <b>does not replace the existing element</b> with
	 *         the argument.
	 */
	public boolean add(long k);

	/**
	 * Visually depict the distribution of keys.
	 *
	 * @param characters The number of characters to "squeeze" the entire buffer
	 *                   into.
	 * @return Returns a sequence of characters where '.' depicts an empty fragment
	 *         of the internal buffer and 'X' depicts full or nearly full capacity
	 *         within the buffer's range and anything between 1 and 9 is between.
	 */
	public String visualizeKeyDistribution(int characters);

	/**
	 * Adds all elements from the given {@link LongContainer} to this set.
	 *
	 * @return Returns the number of elements actually added as a result of this
	 *         call (not previously present in the set).
	 * @since 0.9.1
	 */
	public int addAll(LongContainer container);
}
