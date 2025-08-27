/*
 * HPPC
 *
 * Copyright (C) 2010-2024 Carrot Search s.c. and contributors
 * All rights reserved.
 *
 * Refer to the full license file "LICENSE.txt":
 * https://github.com/carrotsearch/hppc/blob/master/LICENSE.txt
 */
package com.carrotsearch.hppc;

import com.carrotsearch.hppc.internals.SuppressForbidden;

/**
 * Constants used as defaults in containers.
 *
 * @see HashContainers
 */
public final class Containers {
	/** The default number of expected elements for containers. */
	public static final int DEFAULT_EXPECTED_ELEMENTS = 4;

	/**
	 * External initial seed value. We do not care about multiple assignments so not
	 * volatile.
	 *
	 * @see #randomSeed64()
	 */
	private static String testsSeedProperty;

	/** Unique marker for {@link #testsSeedProperty}. */
	private static final String NOT_AVAILABLE = new String();

	private Containers() {
	}

	/**
	 * Provides a (possibly) random initial seed for randomized stuff.
	 *
	 * <p>
	 * If <code>tests.seed</code> property is available and accessible, the returned
	 * value will be derived from the value of that property and will be constant to
	 * ensure reproducibility in presence of the randomized testing package.
	 *
	 * @see "https://github.com/carrotsearch/randomizedtesting"
	 */
	@SuppressForbidden
	public static long randomSeed64() {
		if (testsSeedProperty == null) {
			testsSeedProperty = System.getProperty("tests.seed", NOT_AVAILABLE);
		}

		long initialSeed;
		if (testsSeedProperty != NOT_AVAILABLE) {
			initialSeed = testsSeedProperty.hashCode();
		} else {
			// Mix something that is changing over time (nanoTime)
			// ... with something that is thread-local and relatively unique
			// even for very short time-spans (new Object's address from a TLAB).
			initialSeed = System.nanoTime() ^ System.identityHashCode(new Object());
		}
		return BitMixer.mix64(initialSeed);
	}

	/** Reset state for tests. */
	static void test$reset() {
		testsSeedProperty = null;
	}
}
