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

import java.util.Map;
import java.util.Objects;

/**
 * Transforms map entries.
 *
 * @author  AO Industries, Inc.
 */
public class MapEntryTransformer<K,V,KW,VW> implements Transformer<Map.Entry<K,V>,Map.Entry<KW,VW>> {

	/**
	 * Gets a map entry transformer.
	 */
	public static <K,V,KW,VW> Transformer<Map.Entry<K,V>,Map.Entry<KW,VW>> of(
		Transformer<K,KW> keyTransformer,
		Transformer<V,VW> valueTransformer
	) {
		if(
			keyTransformer == IdentityTransformer.instance
			&& valueTransformer == IdentityTransformer.instance
		) {
			@SuppressWarnings("unchecked")
			Transformer<Map.Entry<K,V>,Map.Entry<KW,VW>> identity = (Transformer)IdentityTransformer.instance;
			return identity;
		}
		return new MapEntryTransformer<>(keyTransformer, valueTransformer);
	}

	protected final Transformer<K,KW> keyTransformer;
	protected final Transformer<V,VW> valueTransformer;

	protected volatile MapEntryTransformer<KW,VW,K,V> inverted;

	protected MapEntryTransformer(Transformer<K,KW> keyTransformer, Transformer<V,VW> valueTransformer) {
		this.keyTransformer = keyTransformer;
		this.valueTransformer = valueTransformer;
	}

	@Override
	public TransformMap.TransformEntry<KW,VW,K,V> toWrapped(Map.Entry<K, V> entry) {
		return TransformMap.TransformEntry.of(entry, keyTransformer.invert(), valueTransformer.invert());
	}

	@Override
	public TransformMap.TransformEntry<K,V,KW,VW> fromWrapped(Map.Entry<KW, VW> entry) {
		return TransformMap.TransformEntry.of(entry, keyTransformer, valueTransformer);
	}

	private final Transformer<Object,Object> unbouned = new Transformer<Object,Object>() {
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
				Transformer<Object,Object> unboundedKeyTransformer = keyTransformer.unbounded();
				Transformer<Object,Object> unboundedValueTransformer = valueTransformer.unbounded();
				Object kw = unboundedKeyTransformer.toWrapped(k);
				Object vw = unboundedValueTransformer.toWrapped(v);
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
								Objects.equals(kw, unboundedKeyTransformer.toWrapped(other.getKey()))
								&& Objects.equals(vw, unboundedValueTransformer.toWrapped(other.getValue()));
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
				Transformer<Object,Object> unboundedKeyTransformer = keyTransformer.unbounded();
				Transformer<Object,Object> unboundedValueTransformer = valueTransformer.unbounded();
				Object k = unboundedKeyTransformer.fromWrapped(kw);
				Object v = unboundedValueTransformer.fromWrapped(vw);
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
								Objects.equals(k, unboundedKeyTransformer.fromWrapped(other.getKey()))
								&& Objects.equals(v, unboundedValueTransformer.fromWrapped(other.getValue()));
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
		public Transformer<Object,Object> unbounded() {
			return this;
		}

		@Override
		public Transformer<Object,Object> invert() {
			return MapEntryTransformer.this.invert().unbounded();
		}
	};

	@Override
	public Transformer<Object,Object> unbounded() {
		return unbouned;
	}

	@Override
	public MapEntryTransformer<KW,VW,K,V> invert() {
		MapEntryTransformer<KW,VW,K,V> i = inverted;
		if(i == null) {
			i = new MapEntryTransformer<>(keyTransformer.invert(), valueTransformer.invert());
			i.inverted = this;
			this.inverted = i;
		}
		return i;
	}
}
