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
import java.util.Queue;

/**
 * Wraps a {@link Queue}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class TransformQueue<E,W> extends TransformCollection<E,W> implements Queue<E> {

	/**
	 * Wraps a queue.
	 * <ol>
	 * <li>If the given queue is a {@link Deque}, then will return a {@link TransformDeque}.</li>
	 * </ol>
	 *
	 * @see  TransformDeque#of(java.util.Deque, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <E,W> TransformQueue<E,W> of(Queue<W> queue, Transformer<E,W> transformer) {
		if(queue instanceof Deque) {
			return TransformDeque.of((Deque<W>)queue, transformer);
		}
		return (queue == null) ? null : new TransformQueue<>(queue, transformer);
	}

	/**
	 * @see  #of(java.util.Queue, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <E> TransformQueue<E,E> of(Queue<E> queue) {
		return of(queue, Transformer.identity());
	}

	protected TransformQueue(Queue<W> wrapped, Transformer<E,W> transformer) {
		super(wrapped, transformer);
	}

	@Override
	protected Queue<W> getWrapped() {
		return (Queue<W>)super.getWrapped();
	}

	@Override
	public boolean offer(E e) {
		return getWrapped().offer(transformer.toWrapped(e));
	}

	@Override
	public E remove() {
		return transformer.fromWrapped(getWrapped().remove());
	}

	@Override
	public E poll() {
		return transformer.fromWrapped(getWrapped().poll());
	}

	@Override
	public E element() {
		return transformer.fromWrapped(getWrapped().element());
	}

	@Override
	public E peek() {
		return transformer.fromWrapped(getWrapped().peek());
	}
}
