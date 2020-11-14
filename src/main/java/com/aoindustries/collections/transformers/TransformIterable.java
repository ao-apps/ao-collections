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
public class TransformIterable<E,W> implements Iterable<E> {

	/**
	 * Wraps an iterable.
	 * <ol>
	 * <li>If the given iterable is a {@link Collection}, then will return a {@link TransformCollection}.</li>
	 * </ol>
	 *
	 * @see  TransformCollection#of(java.util.Collection, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <E,W> TransformIterable<E,W> of(Iterable<W> iterable, Transformer<E,W> transformer) {
		if(iterable instanceof Collection) {
			return TransformCollection.of((Collection<W>)iterable, transformer);
		}
		return (iterable == null) ? null : new TransformIterable<>(iterable, transformer);
	}

	/**
	 * @see  #of(java.lang.Iterable, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformIterable<E,E> of(Iterable<E> iterable) {
		return of(iterable, Transformer.identity());
	}

	private final Iterable<W> wrapped;
	protected final Transformer<E,W> transformer;

	protected TransformIterable(Iterable<W> wrapped, Transformer<E,W> transformer) {
		this.wrapped = wrapped;
		this.transformer = transformer;
	}

	protected Iterable<W> getWrapped() {
		return wrapped;
	}

	@Override
	public TransformIterator<E,W> iterator() {
		return TransformIterator.of(wrapped.iterator(), transformer);
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		getWrapped().forEach(w -> action.accept(transformer.fromWrapped(w)));
	}

	// TODO: spliterator()?
}
