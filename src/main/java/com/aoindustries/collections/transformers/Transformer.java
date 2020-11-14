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
import java.util.Map;

/**
 * Performs type conversions.
 *
 * @param  <E>  The wrapper type
 * @param  <W>  The wrapped type
 *
 * @author  AO Industries, Inc.
 */
public interface Transformer<E,W> {

	W toWrapped(E e);

	E fromWrapped(W w);

	/**
	 * Gets a transformer that wraps and unwraps only when elements are of the wrapper or wrapped types, respectively.
	 * This is useful for legacy APIs that use {@link Object} or unbounded generics, such as:
	 * <ul>
	 * <li>{@link Collection#contains(java.lang.Object)}</li>
	 * <li>{@link Collection#containsAll(java.util.Collection)}</li>
	 * <li>{@link Map#get(java.lang.Object)}</li>
	 * </ul>
	 */
	Transformer<Object,Object> unbounded();

	Transformer<W,E> invert();

	@SuppressWarnings("unchecked")
	static <E> Transformer<E,E> identity() {
		return (Transformer<E,E>)IdentityTransformer.instance;
	}
}
