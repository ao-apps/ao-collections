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
public class TransformCollection<E,W> extends TransformIterable<E,W> implements Collection<E> {

	/**
	 * Wraps a collection.
	 * <ol>
	 * <li>If the given collection is a {@link List}, then will return a {@link TransformList}.</li>
	 * <li>If the given collection is a {@link Queue}, then will return a {@link TransformQueue}.</li>
	 * <li>If the given collection is a {@link Set}, then will return a {@link TransformSet}.</li>
	 * </ol>
	 *
	 * @see  TransformList#of(java.util.List, com.aoindustries.collections.transformers.Transformer)
	 * @see  TransformQueue#of(java.util.Queue, com.aoindustries.collections.transformers.Transformer)
	 * @see  TransformSet#of(java.util.Set, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <E,W> TransformCollection<E,W> of(Collection<W> collection, Transformer<E,W> transformer) {
		if(collection instanceof List) {
			return TransformList.of((List<W>)collection, transformer);
		}
		if(collection instanceof Queue) {
			return TransformQueue.of((Queue<W>)collection, transformer);
		}
		if(collection instanceof Set) {
			return TransformSet.of((Set<W>)collection, transformer);
		}
		return (collection == null) ? null : new TransformCollection<>(collection, transformer);
	}

	/**
	 * @see  #of(java.util.Collection, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformCollection<E,E> of(Collection<E> collection) {
		return of(collection, Transformer.identity());
	}

	protected TransformCollection(Collection<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
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
		return getWrapped().contains(transformer.unbounded().toWrapped(o));
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
			list.add(transformer.fromWrapped(w));
		}
		return list.toArray(a);
	}

	// Java 11: toArray(IntFunction<T[]> generator)

	@Override
	public boolean add(E e) {
		return getWrapped().add(transformer.toWrapped(e));
	}

	@Override
	public boolean remove(Object o) {
		return getWrapped().remove(transformer.unbounded().toWrapped(o));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsAll(Collection<?> c) {
		return getWrapped().containsAll(
			of((Collection<Object>)c, transformer.invert().unbounded())
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean addAll(Collection<? extends E> c) {
		return getWrapped().addAll(
			of((Collection<E>)c, transformer.invert())
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<?> c) {
		return getWrapped().removeAll(
			of((Collection<Object>)c, transformer.invert().unbounded())
		);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return getWrapped().removeIf(w -> filter.test(transformer.fromWrapped(w)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection<?> c) {
		return getWrapped().retainAll(
			of((Collection<Object>)c, transformer.invert().unbounded())
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
				? of((Collection<Object>)o, transformer.invert().unbounded())
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
