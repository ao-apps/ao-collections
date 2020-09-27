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

import java.util.Enumeration;

/**
 * Wraps an {@link Enumeration}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class EnumerationWrapper<E,W> implements Enumeration<E> {

	/**
	 * Wraps an enumeration.
	 */
	public static <E,W> EnumerationWrapper<E,W> of(Enumeration<W> enumeration, Converter<E,W> converter) {
		return (enumeration == null) ? null : new EnumerationWrapper<>(enumeration, converter);
	}

	/**
	 * @see  #of(java.util.Enumeration, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> EnumerationWrapper<E,E> of(Enumeration<E> enumeration) {
		return of(enumeration, Converter.identity());
	}

	private final Enumeration<W> wrapped;
	protected final Converter<E,W> converter;

	protected EnumerationWrapper(Enumeration<W> wrapped, Converter<E,W> converter) {
		this.wrapped = wrapped;
		this.converter = converter;
	}

	protected Enumeration<W> getWrapped() {
		return wrapped;
	}

	@Override
	public boolean hasMoreElements() {
		return getWrapped().hasMoreElements();
	}

	@Override
	public E nextElement() {
		return converter.fromWrapped(getWrapped().nextElement());
	}

	// Java 9: asIterator()
}
