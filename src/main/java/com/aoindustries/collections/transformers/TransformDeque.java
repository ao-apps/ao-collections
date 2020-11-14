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

import java.util.Deque;

/**
 * Wraps a {@link Deque}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class TransformDeque<E,W> extends TransformQueue<E,W> implements Deque<E> {

	/**
	 * Wraps a deque.
	 */
	public static <E,W> TransformDeque<E,W> of(Deque<W> deque, Transformer<E,W> transformer) {
		return (deque == null) ? null : new TransformDeque<>(deque, transformer);
	}

	/**
	 * @see  #of(java.util.Deque, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformDeque<E,E> of(Deque<E> deque) {
		return of(deque, Transformer.identity());
	}

	protected TransformDeque(Deque<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
	}

	@Override
	protected Deque<W> getWrapped() {
		return (Deque<W>)super.getWrapped();
	}

	@Override
	public void addFirst(E w) {
		getWrapped().addFirst(transformer.toWrapped(w));
	}

	@Override
	public void addLast(E e) {
		getWrapped().addLast(transformer.toWrapped(e));
	}

	@Override
	public boolean offerFirst(E e) {
		return getWrapped().offerFirst(transformer.toWrapped(e));
	}

	@Override
	public boolean offerLast(E e) {
		return getWrapped().offerLast(transformer.toWrapped(e));
	}

	@Override
	public E removeFirst() {
		return transformer.fromWrapped(getWrapped().removeFirst());
	}

	@Override
	public E removeLast() {
		return transformer.fromWrapped(getWrapped().removeLast());
	}

	@Override
	public E pollFirst() {
		return transformer.fromWrapped(getWrapped().pollFirst());
	}

	@Override
	public E pollLast() {
		return transformer.fromWrapped(getWrapped().pollLast());
	}

	@Override
	public E getFirst() {
		return transformer.fromWrapped(getWrapped().getFirst());
	}

	@Override
	public E getLast() {
		return transformer.fromWrapped(getWrapped().getLast());
	}

	@Override
	public E peekFirst() {
		return transformer.fromWrapped(getWrapped().peekFirst());
	}

	@Override
	public E peekLast() {
		return transformer.fromWrapped(getWrapped().peekLast());
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return getWrapped().removeFirstOccurrence(transformer.unbounded().toWrapped(o));
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return getWrapped().removeLastOccurrence(transformer.unbounded().toWrapped(o));
	}

	@Override
	public void push(E e) {
		getWrapped().push(transformer.toWrapped(e));
	}

	@Override
	public E pop() {
		return transformer.fromWrapped(getWrapped().pop());
	}

	@Override
	public TransformIterator<E,W> descendingIterator() {
		return TransformIterator.of(getWrapped().descendingIterator(), transformer);
	}
}
