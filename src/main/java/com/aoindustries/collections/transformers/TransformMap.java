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
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Wraps a {@link Map}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class TransformMap<K,V,KW,VW> implements Map<K,V> {

	/**
	 * Wraps a map.
	 * <ol>
	 * <li>If the given map is a {@link SortedMap}, then will return a {@link TransformSortedMap}.</li>
	 * </ol>
	 *
	 * @see  TransformSortedMap#of(java.util.SortedMap, com.aoindustries.collections.transformers.Transformer, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <K,V,KW,VW> TransformMap<K,V,KW,VW> of(
		Map<KW,VW> map,
		Transformer<K,KW> keyTransformer,
		Transformer<V,VW> valueTransformer
	) {
		if(map instanceof SortedMap) {
			return TransformSortedMap.of((SortedMap<KW,VW>)map, keyTransformer, valueTransformer);
		}
		return (map == null) ? null : new TransformMap<>(map, keyTransformer, valueTransformer);
	}

	/**
	 * @see  #of(java.util.Map, com.aoindustries.collections.transformers.Transformer, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <K,V> TransformMap<K,V,K,V> of(Map<K,V> map) {
		return of(map, Transformer.identity(), Transformer.identity());
	}

	private final Map<KW,VW> wrapped;
	protected final Transformer<K,KW> keyTransformer;
	protected final Transformer<V,VW> valueTransformer;

	protected TransformMap(Map<KW,VW> wrapped, Transformer<K,KW> keyTransformer, Transformer<V,VW> valueTransformer) {
		this.wrapped = wrapped;
		this.keyTransformer = keyTransformer;
		this.valueTransformer = valueTransformer;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected Map<KW,VW> getWrapped() {
		return wrapped;
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
	public boolean containsKey(Object key) {
		return getWrapped().containsKey(keyTransformer.unbounded().toWrapped(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return getWrapped().containsValue(valueTransformer.unbounded().toWrapped(value));
	}

	@Override
	public V get(Object key) {
		return valueTransformer.fromWrapped(getWrapped().get(keyTransformer.unbounded().toWrapped(key))
		);
	}

	@Override
	public V put(K key, V value) {
		return valueTransformer.fromWrapped(getWrapped().put(keyTransformer.toWrapped(key),
				valueTransformer.toWrapped(value)
			)
		);
	}

	@Override
	public V remove(Object key) {
		return valueTransformer.fromWrapped(getWrapped().remove(keyTransformer.unbounded().toWrapped(key))
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void putAll(Map<? extends K,? extends V> m) {
		getWrapped().putAll(of((Map<K,V>)m, keyTransformer.invert(), valueTransformer.invert())
		);
	}

	@Override
	public void clear() {
		getWrapped().clear();
	}

	private TransformSet<K,KW> keySet;

	@Override
	public TransformSet<K,KW> keySet() {
		TransformSet<K,KW> ks = keySet;
		if(ks == null) {
			ks = TransformSet.of(getWrapped().keySet(), keyTransformer);
			keySet = ks;
		}
		return ks;
	}

	private TransformCollection<V,VW> values;

	@Override
	public TransformCollection<V,VW> values() {
		TransformCollection<V,VW> v = values;
		if(v == null) {
			v = TransformCollection.of(getWrapped().values(), valueTransformer);
			values = v;
		}
		return v;
	}

	private TransformSet<Entry<K,V>,Entry<KW,VW>> entrySet;

	@Override
	public TransformSet<Entry<K,V>,Entry<KW,VW>> entrySet() {
		TransformSet<Entry<K,V>,Entry<KW,VW>> es = entrySet;
		if(es == null) {
			es = TransformSet.of(getWrapped().entrySet(), new MapEntryTransformer<>(keyTransformer, valueTransformer));
			entrySet = es;
		}
		return es;
	}

	/**
	 * Wraps an entry, with optional type conversion.
	 */
	public static class TransformEntry<K,V,KW,VW> implements Entry<K,V> {

		/**
		 * Wraps a map entry.
		 */
		public static <K,V,KW,VW> TransformEntry<K,V,KW,VW> of(
			Entry<KW,VW> entry,
			Transformer<K,KW> keyTransformer,
			Transformer<V,VW> valueTransformer
		) {
			return (entry == null) ? null : new TransformEntry<>(entry, keyTransformer, valueTransformer);
		}

		/**
		 * @see  #of(java.util.Map.Entry, com.aoindustries.collections.transformers.Transformer, com.aoindustries.collections.transformers.Transformer)
		 * @see  Transformer#identity()
		 */
		public static <K,V> TransformEntry<K,V,K,V> of(Entry<K,V> entry) {
			return of(entry, Transformer.identity(), Transformer.identity());
		}

		private final Entry<KW,VW> wrapped;
		protected final Transformer<K,KW> keyTransformer;
		protected final Transformer<V,VW> valueTransformer;

		protected TransformEntry(Entry<KW,VW> wrapped, Transformer<K,KW> keyTransformer, Transformer<V,VW> valueTransformer) {
			this.wrapped = wrapped;
			this.keyTransformer = keyTransformer;
			this.valueTransformer = valueTransformer;
		}

		protected Entry<KW,VW> getWrapped() {
			return wrapped;
		}

		@Override
		public K getKey() {
			return keyTransformer.fromWrapped(getWrapped().getKey());
		}

		@Override
		public V getValue() {
			return valueTransformer.fromWrapped(getWrapped().getValue());
		}

		@Override
		public V setValue(V value) {
			return valueTransformer.fromWrapped(getWrapped().setValue(valueTransformer.toWrapped(value)));
		}

		@Override
		public boolean equals(Object o) {
			if(!(o instanceof Entry)) return false;
			Entry<?,?> other = (Entry<?,?>)o;
			return
				Objects.equals(getKey(), other.getKey())
				&& Objects.equals(getValue(), other.getValue());
		}

		@Override
		public int hashCode() {
			return getWrapped().hashCode();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		return getWrapped().equals((o instanceof Map)
				? of((Map<Object,Object>)o, keyTransformer.invert().unbounded(), valueTransformer.invert().unbounded())
				: o
		);
	}

	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return valueTransformer.fromWrapped(getWrapped().getOrDefault(keyTransformer.unbounded().toWrapped(key),
				valueTransformer.toWrapped(defaultValue)
			)
		);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		getWrapped().forEach((kw, vw) ->
			action.accept(keyTransformer.fromWrapped(kw),
				valueTransformer.fromWrapped(vw)
			)
		);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		getWrapped().replaceAll((kw, vw) ->
			valueTransformer.toWrapped(function.apply(keyTransformer.fromWrapped(kw),
					valueTransformer.fromWrapped(vw)
				)
			)
		);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return valueTransformer.fromWrapped(getWrapped().putIfAbsent(keyTransformer.toWrapped(key),
				valueTransformer.toWrapped(value)
			)
		);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return getWrapped().remove(keyTransformer.unbounded().toWrapped(key),
			valueTransformer.unbounded().toWrapped(value)
		);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return getWrapped().replace(keyTransformer.toWrapped(key),
			valueTransformer.toWrapped(oldValue),
			valueTransformer.toWrapped(newValue)
		);
	}

	@Override
	public V replace(K key, V value) {
		return valueTransformer.fromWrapped(getWrapped().replace(keyTransformer.toWrapped(key),
				valueTransformer.toWrapped(value)
			)
		);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return valueTransformer.fromWrapped(getWrapped().computeIfAbsent(keyTransformer.toWrapped(key),
				kw -> valueTransformer.toWrapped(mappingFunction.apply(keyTransformer.fromWrapped(kw)
					)
				)
			)
		);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return valueTransformer.fromWrapped(getWrapped().computeIfPresent(keyTransformer.toWrapped(key),
				(kw, vw) -> valueTransformer.toWrapped(remappingFunction.apply(keyTransformer.fromWrapped(kw),
						valueTransformer.fromWrapped(vw)
					)
				)
			)
		);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return valueTransformer.fromWrapped(getWrapped().compute(keyTransformer.toWrapped(key),
				(kw, vw) -> valueTransformer.toWrapped(remappingFunction.apply(keyTransformer.fromWrapped(kw),
						valueTransformer.fromWrapped(vw)
					)
				)
			)
		);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return valueTransformer.fromWrapped(getWrapped().merge(keyTransformer.toWrapped(key),
				valueTransformer.toWrapped(value),
				(oldVW, vw) -> valueTransformer.toWrapped(remappingFunction.apply(valueTransformer.fromWrapped(oldVW),
						valueTransformer.fromWrapped(vw)
					)
				)
			)
		);
	}
}
