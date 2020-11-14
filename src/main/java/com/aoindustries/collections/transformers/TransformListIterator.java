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

import java.util.ListIterator;

/**
 * Wraps a {@link ListIterator}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class TransformListIterator<E,W> extends TransformIterator<E,W> implements ListIterator<E> {

	/**
	 * Wraps a list iterator.
	 */
	public static <E,W> TransformListIterator<E,W> of(ListIterator<W> wrapped, Transformer<E,W> transformer) {
		return (wrapped == null) ? null : new TransformListIterator<>(wrapped, transformer);
	}

	/**
	 * @see  #of(java.util.ListIterator, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformListIterator<E,E> of(ListIterator<E> wrapped) {
		return of(wrapped, Transformer.identity());
	}

	protected TransformListIterator(ListIterator<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
	}

	@Override
	protected ListIterator<W> getWrapped() {
		return (ListIterator<W>)super.getWrapped();
	}

	@Override
	public boolean hasPrevious() {
		return getWrapped().hasPrevious();
	}

	@Override
	public E previous() {
		return transformer.fromWrapped(getWrapped().previous());
	}

	@Override
	public int nextIndex() {
		return getWrapped().nextIndex();
	}

	@Override
	public int previousIndex() {
		return getWrapped().previousIndex();
	}

	@Override
	public void set(E e) {
		getWrapped().set(transformer.toWrapped(e));
	}

	@Override
	public void add(E e) {
		getWrapped().add(transformer.toWrapped(e));
	}
}
