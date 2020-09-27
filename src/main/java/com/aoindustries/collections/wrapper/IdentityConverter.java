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

/**
 * Performs no type conversion.
 *
 * @author  AO Industries, Inc.
 *
 * @see  Converter#identity()
 */
class IdentityConverter<E> implements Converter<E,E> {

	static final IdentityConverter<Object> instance = new IdentityConverter<>();

	private IdentityConverter() {}

	@Override
	public E toWrapped(E e) {
		return e;
	}

	@Override
	public E fromWrapped(E w) {
		return w;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Converter<Object,Object> unbounded() {
		return (Converter<Object,Object>)this;
	}

	@Override
	public Converter<E,E> invert() {
		return this;
	}
}
