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
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.UnaryOperator;

/**
 * Wraps a {@link List}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("EqualsAndHashcode")
public class ListWrapper<E,W> extends CollectionWrapper<E,W> implements List<E> {

	/**
	 * Wraps a list.
	 * <ol>
	 * <li>If the given list implements {@link RandomAccess}, then the returned list will also implement {@link RandomAccess}.</li>
	 * </ol>
	 */
	public static <E,W> ListWrapper<E,W> of(List<W> list, Converter<E,W> converter) {
		if(list instanceof RandomAccess) {
			return new RandomAccessListWrapper<>(list, converter);
		}
		return (list == null) ? null : new ListWrapper<>(list, converter);
	}

	/**
	 * @see  #of(java.util.List, com.aoindustries.collections.transformers.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> ListWrapper<E,E> of(List<E> list) {
		return of(list, Converter.identity());
	}

	protected ListWrapper(List<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
	}

	@Override
	protected List<W> getWrapped() {
		return (List<W>)super.getWrapped();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean addAll(int index, Collection<? extends E> c) {
		return getWrapped().addAll(
			index,
			of((Collection<E>)c, converter.invert())
		);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		getWrapped().replaceAll(
			w -> converter.toWrapped(operator.apply(converter.fromWrapped(w)))
		);
	}

	@Override
	public void sort(Comparator<? super E> c) {
		getWrapped().sort(
			ComparatorWrapper.of(c, converter.invert())
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		return getWrapped().equals(
			(o instanceof List)
				? of((List<Object>)o, converter.invert().unbounded())
				: o
		);
	}

	@Override
	public E get(int index) {
		return converter.fromWrapped(getWrapped().get(index));
	}

	@Override
	public E set(int index, E element) {
		return converter.fromWrapped(getWrapped().set(index, converter.toWrapped(element)));
	}

	@Override
	public void add(int index, E element) {
		getWrapped().add(index, converter.toWrapped(element));
	}

	@Override
	public E remove(int index) {
		return converter.fromWrapped(getWrapped().remove(index));
	}

	@Override
	public int indexOf(Object o) {
		return getWrapped().indexOf(converter.unbounded().toWrapped(o));
	}

	@Override
	public int lastIndexOf(Object o) {
		return getWrapped().lastIndexOf(converter.unbounded().toWrapped(o));
	}

	@Override
	public ListIteratorWrapper<E,W> listIterator() {
		return ListIteratorWrapper.of(getWrapped().listIterator(), converter);
	}

	@Override
	public ListIteratorWrapper<E,W> listIterator(int index) {
		return ListIteratorWrapper.of(getWrapped().listIterator(index), converter);
	}

	@Override
	public ListWrapper<E,W> subList(int fromIndex, int toIndex) {
		return of(getWrapped().subList(fromIndex, toIndex), converter);
	}

	// TODO: spliterator()?

	private static class RandomAccessListWrapper<E,W> extends ListWrapper<E,W> implements RandomAccess {
		private RandomAccessListWrapper(List<W> wrapped, Converter<E,W> converter) {
			super(wrapped, converter);
		}
	}
}
