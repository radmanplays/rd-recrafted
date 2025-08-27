package com.carrotsearch.hppc.cursors;

/** A cursor over a collection of <code>chars</code>. */
@com.carrotsearch.hppc.Generated(date = "2024-06-04T15:20:17+0200", value = "KTypeCursor.java")
public final class CharCursor {
	/**
	 * The current value's index in the container this cursor belongs to. The
	 * meaning of this index is defined by the container (usually it will be an
	 * index in the underlying storage buffer).
	 */
	public int index;

	/** The current value. */
	public char value;

	@Override
	public String toString() {
		return "[cursor, index: " + index + ", value: " + value + "]";
	}
}
