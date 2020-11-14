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
package com.aoindustries.collections.transformers;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * Wraps an {@link Iterator}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class TransformIterator<E,W> implements Iterator<E> {

	/**
	 * Wraps an iterator.
	 * <ol>
	 * <li>If the given iterator is a {@link ListIterator}, then will return a {@link TransformListIterator}.</li>
	 * </ol>
	 *
	 * @see  TransformListIterator#of(java.util.ListIterator, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <E,W> TransformIterator<E,W> of(Iterator<W> iterator, Transformer<E,W> transformer) {
		if(iterator instanceof ListIterator) {
			return TransformListIterator.of((ListIterator<W>)iterator, transformer);
		}
		return (iterator == null) ? null : new TransformIterator<>(iterator, transformer);
	}

	/**
	 * @see  #of(java.util.Iterator, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformIterator<E,E> of(Iterator<E> iterator) {
		return of(iterator, Transformer.identity());
	}

	private final Iterator<W> wrapped;
	protected final Transformer<E,W> transformer;

	protected TransformIterator(Iterator<W> wrapped, Transformer<E,W> transformer) {
		this.wrapped = wrapped;
		this.transformer = transformer;
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
		return transformer.fromWrapped(getWrapped().next());
	}

	@Override
	public void remove() {
		getWrapped().remove();
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		getWrapped().forEachRemaining(w -> action.accept(transformer.fromWrapped(w)));
	}
}
