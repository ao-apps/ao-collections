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

import java.util.ListIterator;

/**
 * Wraps a {@link ListIterator}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class ListIteratorWrapper<E,W> extends IteratorWrapper<E,W> implements ListIterator<E> {

	/**
	 * Wraps a list iterator.
	 */
	public static <E,W> ListIteratorWrapper<E,W> of(ListIterator<W> wrapped, Converter<E,W> converter) {
		return (wrapped == null) ? null : new ListIteratorWrapper<>(wrapped, converter);
	}

	/**
	 * @see  #of(java.util.ListIterator, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> ListIteratorWrapper<E,E> of(ListIterator<E> wrapped) {
		return of(wrapped, Converter.identity());
	}

	protected ListIteratorWrapper(ListIterator<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
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
		return converter.fromWrapped(getWrapped().previous());
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
		getWrapped().set(converter.toWrapped(e));
	}

	@Override
	public void add(E e) {
		getWrapped().add(converter.toWrapped(e));
	}
}
