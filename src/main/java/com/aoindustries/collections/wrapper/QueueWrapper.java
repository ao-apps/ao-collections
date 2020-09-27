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

import java.util.Deque;
import java.util.Queue;

/**
 * Wraps a {@link Queue}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class QueueWrapper<E,W> extends CollectionWrapper<E,W> implements Queue<E> {

	/**
	 * Wraps a queue.
	 * <ol>
	 * <li>If the given queue is a {@link Deque}, then will return a {@link DequeWrapper}.</li>
	 * </ol>
	 *
	 * @see  DequeWrapper#of(java.util.Deque, com.aoindustries.collections.wrapper.Converter)
	 */
	public static <E,W> QueueWrapper<E,W> of(Queue<W> queue, Converter<E,W> converter) {
		if(queue instanceof Deque) {
			return DequeWrapper.of((Deque<W>)queue, converter);
		}
		return (queue == null) ? null : new QueueWrapper<>(queue, converter);
	}

	/**
	 * @see  #of(java.util.Queue, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <E> QueueWrapper<E,E> of(Queue<E> queue) {
		return of(queue, Converter.identity());
	}

	protected QueueWrapper(Queue<W> wrapped, Converter<E,W> converter) {
		super(wrapped, converter);
	}

	@Override
	protected Queue<W> getWrapped() {
		return (Queue<W>)super.getWrapped();
	}

	@Override
	public boolean offer(E e) {
		return getWrapped().offer(converter.toWrapped(e));
	}

	@Override
	public E remove() {
		return converter.fromWrapped(getWrapped().remove());
	}

	@Override
	public E poll() {
		return converter.fromWrapped(getWrapped().poll());
	}

	@Override
	public E element() {
		return converter.fromWrapped(getWrapped().element());
	}

	@Override
	public E peek() {
		return converter.fromWrapped(getWrapped().peek());
	}
}
