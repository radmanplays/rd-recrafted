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

import java.util.IllegalFormatException;
import java.util.Locale;

public class BufferAllocationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BufferAllocationException(String message) {
		super(message);
	}

	public BufferAllocationException(String message, Object... args) {
		this(message, null, args);
	}

	public BufferAllocationException(String message, Throwable t, Object... args) {
		super(formatMessage(message, t, args), t);
	}

	private static String formatMessage(String message, Throwable t, Object... args) {
		try {
			return String.format(Locale.ROOT, message, args);
		} catch (IllegalFormatException e) {
			BufferAllocationException substitute = new BufferAllocationException(
					message + " [ILLEGAL FORMAT, ARGS SUPPRESSED]");
			if (t != null) {
				substitute.addSuppressed(t);
			}
			substitute.addSuppressed(e);
			throw substitute;
		}
	}
}
