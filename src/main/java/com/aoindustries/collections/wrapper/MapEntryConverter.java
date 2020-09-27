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

import java.util.Map;
import java.util.Objects;

/**
 * Converts map entries.
 *
 * @author  AO Industries, Inc.
 */
public class MapEntryConverter<K,V,KW,VW> implements Converter<Map.Entry<K,V>,Map.Entry<KW,VW>> {

	/**
	 * Gets a map entry converter.
	 */
	public static <K,V,KW,VW> Converter<Map.Entry<K,V>,Map.Entry<KW,VW>> of(
		Converter<K,KW> keyConverter,
		Converter<V,VW> valueConverter
	) {
		if(
			keyConverter == IdentityConverter.instance
			&& valueConverter == IdentityConverter.instance
		) {
			@SuppressWarnings("unchecked")
			Converter<Map.Entry<K,V>,Map.Entry<KW,VW>> identity = (Converter)IdentityConverter.instance;
			return identity;
		}
		return new MapEntryConverter<>(keyConverter, valueConverter);
	}

	protected final Converter<K,KW> keyConverter;
	protected final Converter<V,VW> valueConverter;

	protected volatile MapEntryConverter<KW,VW,K,V> inverted;

	protected MapEntryConverter(Converter<K,KW> keyConverter, Converter<V,VW> valueConverter) {
		this.keyConverter = keyConverter;
		this.valueConverter = valueConverter;
	}

	@Override
	public MapWrapper.EntryWrapper<KW,VW,K,V> toWrapped(Map.Entry<K, V> entry) {
		return MapWrapper.EntryWrapper.of(entry, keyConverter.invert(), valueConverter.invert());
	}

	@Override
	public MapWrapper.EntryWrapper<K,V,KW,VW> fromWrapped(Map.Entry<KW, VW> entry) {
		return MapWrapper.EntryWrapper.of(entry, keyConverter, valueConverter);
	}

	private final Converter<Object,Object> unbouned = new Converter<Object,Object>() {
		/**
		 * Unwraps the given object if is of our wrapper type.
		 *
		 * @return  The unwrapped object or {@code o} if not of our wrapper type.
		 */
		@Override
		public Object toWrapped(Object e) {
			if(e instanceof Map.Entry) {
				Map.Entry<?,?> entry = (Map.Entry<?,?>)e;
				Object k = entry.getKey();
				Object v = entry.getValue();
				Converter<Object,Object> unboundedKeyConverter = keyConverter.unbounded();
				Converter<Object,Object> unboundedValueConverter = valueConverter.unbounded();
				Object kw = unboundedKeyConverter.toWrapped(k);
				Object vw = unboundedValueConverter.toWrapped(v);
				if(kw != k || vw != v) {
					return new Map.Entry<Object,Object>() {
						@Override
						public Object getKey() {
							return kw;
						}

						@Override
						public Object getValue() {
							return vw;
						}

						@Override
						public Object setValue(Object value) {
							throw new UnsupportedOperationException();
						}

						@Override
						public boolean equals(Object o) {
							if(!(o instanceof Map.Entry)) return false;
							Map.Entry<?,?> other = (Map.Entry<?,?>)o;
							return
								Objects.equals(kw, unboundedKeyConverter.toWrapped(other.getKey()))
								&& Objects.equals(vw, unboundedValueConverter.toWrapped(other.getValue()));
						}

						@Override
						public int hashCode() {
							return Objects.hashCode(kw) ^ Objects.hashCode(vw);
						}
					};
				}
			}
			return e;
		}

		/**
		 * Wraps the given object if is of our wrapped type.
		 *
		 * @return  The wrapped object or {@code o} if not of our wrapped type.
		 */
		@Override
		public Object fromWrapped(Object w) {
			if(w instanceof Map.Entry) {
				Map.Entry<?,?> entry = (Map.Entry<?,?>)w;
				Object kw = entry.getKey();
				Object vw = entry.getValue();
				Converter<Object,Object> unboundedKeyConverter = keyConverter.unbounded();
				Converter<Object,Object> unboundedValueConverter = valueConverter.unbounded();
				Object k = unboundedKeyConverter.fromWrapped(kw);
				Object v = unboundedValueConverter.fromWrapped(vw);
				if(k != kw || v != vw) {
					return new Map.Entry<Object,Object>() {
						@Override
						public Object getKey() {
							return k;
						}

						@Override
						public Object getValue() {
							return v;
						}

						@Override
						public Object setValue(Object value) {
							throw new UnsupportedOperationException();
						}

						@Override
						public boolean equals(Object o) {
							if(!(o instanceof Map.Entry)) return false;
							Map.Entry<?,?> other = (Map.Entry<?,?>)o;
							return
								Objects.equals(k, unboundedKeyConverter.fromWrapped(other.getKey()))
								&& Objects.equals(v, unboundedValueConverter.fromWrapped(other.getValue()));
						}

						@Override
						public int hashCode() {
							return Objects.hashCode(k) ^ Objects.hashCode(v);
						}
					};
				}
			}
			return w;
		}

		@Override
		public Converter<Object,Object> unbounded() {
			return this;
		}

		@Override
		public Converter<Object,Object> invert() {
			return MapEntryConverter.this.invert().unbounded();
		}
	};

	@Override
	public Converter<Object,Object> unbounded() {
		return unbouned;
	}

	@Override
	public MapEntryConverter<KW,VW,K,V> invert() {
		MapEntryConverter<KW,VW,K,V> i = inverted;
		if(i == null) {
			i = new MapEntryConverter<>(keyConverter.invert(), valueConverter.invert());
			i.inverted = this;
			this.inverted = i;
		}
		return i;
	}
}
