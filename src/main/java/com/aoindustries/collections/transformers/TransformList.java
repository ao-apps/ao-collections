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
public class TransformList<E,W> extends TransformCollection<E,W> implements List<E> {

	/**
	 * Wraps a list.
	 * <ol>
	 * <li>If the given list implements {@link RandomAccess}, then the returned list will also implement {@link RandomAccess}.</li>
	 * </ol>
	 */
	public static <E,W> TransformList<E,W> of(List<W> list, Transformer<E,W> transformer) {
		if(list instanceof RandomAccess) {
			return new TransformList_RandomAccess<>(list, transformer);
		}
		return (list == null) ? null : new TransformList<>(list, transformer);
	}

	/**
	 * @see  #of(java.util.List, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformList<E,E> of(List<E> list) {
		return of(list, Transformer.identity());
	}

	protected TransformList(List<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
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
			of((Collection<E>)c, transformer.invert())
		);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		getWrapped().replaceAll(
			w -> transformer.toWrapped(operator.apply(transformer.fromWrapped(w)))
		);
	}

	@Override
	public void sort(Comparator<? super E> c) {
		getWrapped().sort(
			TransformComparator.of(c, transformer.invert())
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		return getWrapped().equals(
			(o instanceof List)
				? of((List<Object>)o, transformer.invert().unbounded())
				: o
		);
	}

	@Override
	public E get(int index) {
		return transformer.fromWrapped(getWrapped().get(index));
	}

	@Override
	public E set(int index, E element) {
		return transformer.fromWrapped(getWrapped().set(index, transformer.toWrapped(element)));
	}

	@Override
	public void add(int index, E element) {
		getWrapped().add(index, transformer.toWrapped(element));
	}

	@Override
	public E remove(int index) {
		return transformer.fromWrapped(getWrapped().remove(index));
	}

	@Override
	public int indexOf(Object o) {
		return getWrapped().indexOf(transformer.unbounded().toWrapped(o));
	}

	@Override
	public int lastIndexOf(Object o) {
		return getWrapped().lastIndexOf(transformer.unbounded().toWrapped(o));
	}

	@Override
	public TransformListIterator<E,W> listIterator() {
		return TransformListIterator.of(getWrapped().listIterator(), transformer);
	}

	@Override
	public TransformListIterator<E,W> listIterator(int index) {
		return TransformListIterator.of(getWrapped().listIterator(index), transformer);
	}

	@Override
	public TransformList<E,W> subList(int fromIndex, int toIndex) {
		return of(getWrapped().subList(fromIndex, toIndex), transformer);
	}

	// TODO: spliterator()?

	private static class TransformList_RandomAccess<E,W> extends TransformList<E,W> implements RandomAccess {
		private TransformList_RandomAccess(List<W> wrapped, Transformer<E,W> transformer) {
			super(wrapped, transformer);
		}
	}
}
