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
public class DequeWrapper<E,W> extends QueueWrapper<E,W> implements Deque<E> {

	/**
	 * Wraps a deque.
	 */
	public static <E,W> DequeWrapper<E,W> of(Deque<W> deque, Converter<E,W> converter) {
		return (deque == null) ? null : new DequeWrapper<>(deque, converter);
	}

	/**
	 * @see  #of(java.util.Deque, com.aoindustries.collections.transformers.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> DequeWrapper<E,E> of(Deque<E> deque) {
		return of(deque, Converter.identity());
	}

	protected DequeWrapper(Deque<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
	}

	@Override
	protected Deque<W> getWrapped() {
		return (Deque<W>)super.getWrapped();
	}

	@Override
	public void addFirst(E w) {
		getWrapped().addFirst(converter.toWrapped(w));
	}

	@Override
	public void addLast(E e) {
		getWrapped().addLast(converter.toWrapped(e));
	}

	@Override
	public boolean offerFirst(E e) {
		return getWrapped().offerFirst(converter.toWrapped(e));
	}

	@Override
	public boolean offerLast(E e) {
		return getWrapped().offerLast(converter.toWrapped(e));
	}

	@Override
	public E removeFirst() {
		return converter.fromWrapped(getWrapped().removeFirst());
	}

	@Override
	public E removeLast() {
		return converter.fromWrapped(getWrapped().removeLast());
	}

	@Override
	public E pollFirst() {
		return converter.fromWrapped(getWrapped().pollFirst());
	}

	@Override
	public E pollLast() {
		return converter.fromWrapped(getWrapped().pollLast());
	}

	@Override
	public E getFirst() {
		return converter.fromWrapped(getWrapped().getFirst());
	}

	@Override
	public E getLast() {
		return converter.fromWrapped(getWrapped().getLast());
	}

	@Override
	public E peekFirst() {
		return converter.fromWrapped(getWrapped().peekFirst());
	}

	@Override
	public E peekLast() {
		return converter.fromWrapped(getWrapped().peekLast());
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return getWrapped().removeFirstOccurrence(converter.unbounded().toWrapped(o));
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return getWrapped().removeLastOccurrence(converter.unbounded().toWrapped(o));
	}

	@Override
	public void push(E e) {
		getWrapped().push(converter.toWrapped(e));
	}

	@Override
	public E pop() {
		return converter.fromWrapped(getWrapped().pop());
	}

	@Override
	public IteratorWrapper<E,W> descendingIterator() {
		return IteratorWrapper.of(getWrapped().descendingIterator(), converter);
	}
}
