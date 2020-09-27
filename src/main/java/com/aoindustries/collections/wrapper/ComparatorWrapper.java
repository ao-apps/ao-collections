/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-collections.
 *
 * ao-collections is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-collections is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-collections.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.collections.wrapper;

import java.util.Comparator;

/**
 * Wraps a {@link Comparator}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class ComparatorWrapper<T,W> implements Comparator<T> {

	/**
	 * Wraps a comparator.
	 */
	public static <T,W> ComparatorWrapper<T,W> of(Comparator<? super W> comparator, Converter<T,W> converter) {
		return (comparator == null) ? null : new ComparatorWrapper<>(comparator, converter);
	}

	/**
	 * @see  #of(java.util.Comparator, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <T> ComparatorWrapper<T,T> of(Comparator<? super T> comparator) {
		return of(comparator, Converter.identity());
	}

	private final Comparator<? super W> wrapped;
	protected final Converter<T,W> converter;

	protected ComparatorWrapper(Comparator<? super W> wrapped, Converter<T,W> converter) {
		this.wrapped = wrapped;
		this.converter = converter;
	}

	protected Comparator<? super W> getWrapped() {
		return wrapped;
	}

	@Override
	public int compare(T t1, T t2) {
		return getWrapped().compare(
			converter.toWrapped(t1),
			converter.toWrapped(t2)
		);
	}
}
