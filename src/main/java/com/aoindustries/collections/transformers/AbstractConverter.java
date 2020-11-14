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

/**
 * Performs type conversions between two classes.
 *
 * @param  <E>  The wrapper type
 * @param  <W>  The wrapped type
 *
 * @author  AO Industries, Inc.
 */
public abstract class AbstractConverter<E,W> implements Converter<E,W> {

	protected final Class<E> eClass;
	protected final Class<W> wClass;
	protected final AbstractConverter<W,E> inverted;

	/**
	 * @param eClass The wrapper type
	 * @param wClass The wrapped type
	 */
	public AbstractConverter(
		Class<E> eClass,
		Class<W> wClass
	) {
		this.eClass = eClass;
		this.wClass = wClass;
		this.inverted = new FunctionalConverter<>(wClass, eClass, e -> fromWrapped(e), w -> toWrapped(w), this);
	}

	/**
	 * @param eClass The wrapper type
	 * @param wClass The wrapped type
	 */
	AbstractConverter(
		Class<E> eClass,
		Class<W> wClass,
		AbstractConverter<W,E> inverted
	) {
		this.eClass = eClass;
		this.wClass = wClass;
		this.inverted = inverted;
	}

	@Override
	public abstract W toWrapped(E e);

	@Override
	public abstract E fromWrapped(W w);

	private final Converter<Object,Object> unbouned = new Converter<Object,Object>() {
		/**
		 * Unwraps the given object if is of our wrapper type.
		 *
		 * @return  The unwrapped object or {@code o} if not of our wrapper type.
		 */
		@Override
		public Object toWrapped(Object e) {
			return eClass.isInstance(e) ? AbstractConverter.this.toWrapped(eClass.cast(e)) : e;
		}

		/**
		 * Wraps the given object if is of our wrapped type.
		 *
		 * @return  The wrapped object or {@code o} if not of our wrapped type.
		 */
		@Override
		public Object fromWrapped(Object w) {
			return wClass.isInstance(w) ? AbstractConverter.this.fromWrapped(wClass.cast(w)) : w;
		}

		@Override
		public Converter<Object,Object> unbounded() {
			return this;
		}

		@Override
		public Converter<Object,Object> invert() {
			return AbstractConverter.this.invert().unbounded();
		}
	};

	@Override
	public Converter<Object,Object> unbounded() {
		return unbouned;
	}

	@Override
	public AbstractConverter<W,E> invert() {
		return inverted;
	}
}
