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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * Wraps an {@link Iterator}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class IteratorWrapper<E,W> implements Iterator<E> {

	/**
	 * Wraps an iterator.
	 * <ol>
	 * <li>If the given iterator is a {@link ListIterator}, then will return a {@link ListIteratorWrapper}.</li>
	 * </ol>
	 *
	 * @see  ListIteratorWrapper#of(java.util.ListIterator, com.aoindustries.collections.wrapper.Converter)
	 */
	public static <E,W> IteratorWrapper<E,W> of(Iterator<W> iterator, Converter<E,W> converter) {
		if(iterator instanceof ListIterator) {
			return ListIteratorWrapper.of((ListIterator<W>)iterator, converter);
		}
		return (iterator == null) ? null : new IteratorWrapper<>(iterator, converter);
	}

	/**
	 * @see  #of(java.util.Iterator, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> IteratorWrapper<E,E> of(Iterator<E> iterator) {
		return of(iterator, Converter.identity());
	}

	private final Iterator<W> wrapped;
	protected final Converter<E,W> converter;

	protected IteratorWrapper(Iterator<W> wrapped, Converter<E,W> converter) {
		this.wrapped = wrapped;
		this.converter = converter;
	}

	protected Iterator<W> getWrapped() {
		return wrapped;
	}

	@Override
	public boolean hasNext() {
		return getWrapped().hasNext();
	}

	@Override
	public E next() {
		return converter.fromWrapped(getWrapped().next());
	}

	@Override
	public void remove() {
		getWrapped().remove();
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		getWrapped().forEachRemaining(w -> action.accept(converter.fromWrapped(w)));
	}
}
