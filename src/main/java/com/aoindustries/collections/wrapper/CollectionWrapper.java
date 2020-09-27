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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Wraps a {@link Collection}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class CollectionWrapper<E,W> extends IterableWrapper<E,W> implements Collection<E> {

	/**
	 * Wraps a collection.
	 * <ol>
	 * <li>If the given collection is a {@link List}, then will return a {@link ListWrapper}.</li>
	 * <li>If the given collection is a {@link Queue}, then will return a {@link QueueWrapper}.</li>
	 * <li>If the given collection is a {@link Set}, then will return a {@link SetWrapper}.</li>
	 * </ol>
	 *
	 * @see  ListWrapper#of(java.util.List, com.aoindustries.collections.wrapper.Converter)
	 * @see  QueueWrapper#of(java.util.Queue, com.aoindustries.collections.wrapper.Converter)
	 * @see  SetWrapper#of(java.util.Set, com.aoindustries.collections.wrapper.Converter)
	 */
	public static <E,W> CollectionWrapper<E,W> of(Collection<W> collection, Converter<E,W> converter) {
		if(collection instanceof List) {
			return ListWrapper.of((List<W>)collection, converter);
		}
		if(collection instanceof Queue) {
			return QueueWrapper.of((Queue<W>)collection, converter);
		}
		if(collection instanceof Set) {
			return SetWrapper.of((Set<W>)collection, converter);
		}
		return (collection == null) ? null : new CollectionWrapper<>(collection, converter);
	}

	/**
	 * @see  #of(java.util.Collection, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> CollectionWrapper<E,E> of(Collection<E> collection) {
		return of(collection, Converter.identity());
	}

	protected CollectionWrapper(Collection<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
	}

	@Override
	protected Collection<W> getWrapped() {
		return (Collection<W>)super.getWrapped();
	}

	@Override
	public int size() {
		return getWrapped().size();
	}

	@Override
	public boolean isEmpty() {
		return getWrapped().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return getWrapped().contains(converter.unbounded().toWrapped(o));
	}

	@Override
	public Object[] toArray() {
		return getWrapped().toArray();
	}

	@Override
	@SuppressWarnings("SuspiciousToArrayCall")
	public <T> T[] toArray(T[] a) {
		List<E> list = new ArrayList<>(size());
		for(W w : getWrapped()) {
			list.add(converter.fromWrapped(w));
		}
		return list.toArray(a);
	}

	// Java 11: toArray(IntFunction<T[]> generator)

	@Override
	public boolean add(E e) {
		return getWrapped().add(converter.toWrapped(e));
	}

	@Override
	public boolean remove(Object o) {
		return getWrapped().remove(converter.unbounded().toWrapped(o));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsAll(Collection<?> c) {
		return getWrapped().containsAll(
			of((Collection<Object>)c, converter.invert().unbounded())
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean addAll(Collection<? extends E> c) {
		return getWrapped().addAll(
			of((Collection<E>)c, converter.invert())
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<?> c) {
		return getWrapped().removeAll(
			of((Collection<Object>)c, converter.invert().unbounded())
		);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return getWrapped().removeIf(w -> filter.test(converter.fromWrapped(w)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection<?> c) {
		return getWrapped().retainAll(
			of((Collection<Object>)c, converter.invert().unbounded())
		);
	}

	@Override
	public void clear() {
		getWrapped().clear();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		return getWrapped().equals(
			(o instanceof Collection)
				? of((Collection<Object>)o, converter.invert().unbounded())
				: o
		);
	}

	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}

	// TODO: spliterator()?
	// TODO: stream()?
	// TODO: parallelStream()?
}
