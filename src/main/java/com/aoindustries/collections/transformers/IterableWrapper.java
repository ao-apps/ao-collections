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

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Wraps an {@link Iterable}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class IterableWrapper<E,W> implements Iterable<E> {

	/**
	 * Wraps an iterable.
	 * <ol>
	 * <li>If the given iterable is a {@link Collection}, then will return a {@link CollectionWrapper}.</li>
	 * </ol>
	 *
	 * @see  CollectionWrapper#of(java.util.Collection, com.aoindustries.collections.transformers.Converter)
	 */
	public static <E,W> IterableWrapper<E,W> of(Iterable<W> iterable, Converter<E,W> converter) {
		if(iterable instanceof Collection) {
			return CollectionWrapper.of((Collection<W>)iterable, converter);
		}
		return (iterable == null) ? null : new IterableWrapper<>(iterable, converter);
	}

	/**
	 * @see  #of(java.lang.Iterable, com.aoindustries.collections.transformers.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> IterableWrapper<E,E> of(Iterable<E> iterable) {
		return of(iterable, Converter.identity());
	}

	private final Iterable<W> wrapped;
	protected final Converter<E,W> converter;

	protected IterableWrapper(Iterable<W> wrapped, Converter<E,W> converter) {
		this.wrapped = wrapped;
		this.converter = converter;
	}

	protected Iterable<W> getWrapped() {
		return wrapped;
	}

	@Override
	public IteratorWrapper<E,W> iterator() {
		return IteratorWrapper.of(wrapped.iterator(), converter);
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		getWrapped().forEach(w -> action.accept(converter.fromWrapped(w)));
	}

	// TODO: spliterator()?
}
