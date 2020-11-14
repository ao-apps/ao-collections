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
public class MapWrapper<K,V,KW,VW> implements Map<K,V> {

	/**
	 * Wraps a map.
	 * <ol>
	 * <li>If the given map is a {@link SortedMap}, then will return a {@link SortedMapWrapper}.</li>
	 * </ol>
	 *
	 * @see  SortedMapWrapper#of(java.util.SortedMap, com.aoindustries.collections.transformers.Converter, com.aoindustries.collections.transformers.Converter)
	 */
	public static <K,V,KW,VW> MapWrapper<K,V,KW,VW> of(
		Map<KW,VW> map,
		Converter<K,KW> keyConverter,
		Converter<V,VW> valueConverter
	) {
		if(map instanceof SortedMap) {
			return SortedMapWrapper.of((SortedMap<KW,VW>)map, keyConverter, valueConverter);
		}
		return (map == null) ? null : new MapWrapper<>(map, keyConverter, valueConverter);
	}

	/**
	 * @see  #of(java.util.Map, com.aoindustries.collections.transformers.Converter, com.aoindustries.collections.transformers.Converter)
	 * @see  Converter#identity()
	 */
	public static <K,V> MapWrapper<K,V,K,V> of(Map<K,V> map) {
		return of(map, Converter.identity(), Converter.identity());
	}

	private final Map<KW,VW> wrapped;
	protected final Converter<K,KW> keyConverter;
	protected final Converter<V,VW> valueConverter;

	protected MapWrapper(Map<KW,VW> wrapped, Converter<K,KW> keyConverter, Converter<V,VW> valueConverter) {
		this.wrapped = wrapped;
		this.keyConverter = keyConverter;
		this.valueConverter = valueConverter;
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
		return getWrapped().containsKey(keyConverter.unbounded().toWrapped(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return getWrapped().containsValue(valueConverter.unbounded().toWrapped(value));
	}

	@Override
	public V get(Object key) {
		return valueConverter.fromWrapped(
			getWrapped().get(keyConverter.unbounded().toWrapped(key))
		);
	}

	@Override
	public V put(K key, V value) {
		return valueConverter.fromWrapped(
			getWrapped().put(
				keyConverter.toWrapped(key),
				valueConverter.toWrapped(value)
			)
		);
	}

	@Override
	public V remove(Object key) {
		return valueConverter.fromWrapped(
			getWrapped().remove(keyConverter.unbounded().toWrapped(key))
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void putAll(Map<? extends K,? extends V> m) {
		getWrapped().putAll(
			of((Map<K,V>)m, keyConverter.invert(), valueConverter.invert())
		);
	}

	@Override
	public void clear() {
		getWrapped().clear();
	}

	private SetWrapper<K,KW> keySet;

	@Override
	public SetWrapper<K,KW> keySet() {
		SetWrapper<K,KW> ks = keySet;
		if(ks == null) {
			ks = SetWrapper.of(getWrapped().keySet(), keyConverter);
			keySet = ks;
		}
		return ks;
	}

	private CollectionWrapper<V,VW> values;

	@Override
	public CollectionWrapper<V,VW> values() {
		CollectionWrapper<V,VW> v = values;
		if(v == null) {
			v = CollectionWrapper.of(getWrapped().values(), valueConverter);
			values = v;
		}
		return v;
	}

	private SetWrapper<Entry<K,V>,Entry<KW,VW>> entrySet;

	@Override
	public SetWrapper<Entry<K,V>,Entry<KW,VW>> entrySet() {
		SetWrapper<Entry<K,V>,Entry<KW,VW>> es = entrySet;
		if(es == null) {
			es = SetWrapper.of(getWrapped().entrySet(), new MapEntryConverter<>(keyConverter, valueConverter));
			entrySet = es;
		}
		return es;
	}

	/**
	 * Wraps an entry, with optional type conversion.
	 */
	public static class EntryWrapper<K,V,KW,VW> implements Entry<K,V> {

		/**
		 * Wraps a map entry.
		 */
		public static <K,V,KW,VW> EntryWrapper<K,V,KW,VW> of(
			Entry<KW,VW> entry,
			Converter<K,KW> keyConverter,
			Converter<V,VW> valueConverter
		) {
			return (entry == null) ? null : new EntryWrapper<>(entry, keyConverter, valueConverter);
		}

		/**
		 * @see  #of(java.util.Map.Entry, com.aoindustries.collections.transformers.Converter, com.aoindustries.collections.transformers.Converter)
		 * @see  Converter#identity()
		 */
		public static <K,V> EntryWrapper<K,V,K,V> of(Entry<K,V> entry) {
			return of(entry, Converter.identity(), Converter.identity());
		}

		private final Entry<KW,VW> wrapped;
		protected final Converter<K,KW> keyConverter;
		protected final Converter<V,VW> valueConverter;

		protected EntryWrapper(Entry<KW,VW> wrapped, Converter<K,KW> keyConverter, Converter<V,VW> valueConverter) {
			this.wrapped = wrapped;
			this.keyConverter = keyConverter;
			this.valueConverter = valueConverter;
		}

		protected Entry<KW,VW> getWrapped() {
			return wrapped;
		}

		@Override
		public K getKey() {
			return keyConverter.fromWrapped(getWrapped().getKey());
		}

		@Override
		public V getValue() {
			return valueConverter.fromWrapped(getWrapped().getValue());
		}

		@Override
		public V setValue(V value) {
			return valueConverter.fromWrapped(getWrapped().setValue(valueConverter.toWrapped(value)));
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
		return getWrapped().equals(
			(o instanceof Map)
				? of((Map<Object,Object>)o, keyConverter.invert().unbounded(), valueConverter.invert().unbounded())
				: o
		);
	}

	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return valueConverter.fromWrapped(
			getWrapped().getOrDefault(
				keyConverter.unbounded().toWrapped(key),
				valueConverter.toWrapped(defaultValue)
			)
		);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		getWrapped().forEach((kw, vw) ->
			action.accept(
				keyConverter.fromWrapped(kw),
				valueConverter.fromWrapped(vw)
			)
		);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		getWrapped().replaceAll((kw, vw) ->
			valueConverter.toWrapped(
				function.apply(
					keyConverter.fromWrapped(kw),
					valueConverter.fromWrapped(vw)
				)
			)
		);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return valueConverter.fromWrapped(
			getWrapped().putIfAbsent(
				keyConverter.toWrapped(key),
				valueConverter.toWrapped(value)
			)
		);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return getWrapped().remove(
			keyConverter.unbounded().toWrapped(key),
			valueConverter.unbounded().toWrapped(value)
		);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return getWrapped().replace(
			keyConverter.toWrapped(key),
			valueConverter.toWrapped(oldValue),
			valueConverter.toWrapped(newValue)
		);
	}

	@Override
	public V replace(K key, V value) {
		return valueConverter.fromWrapped(
			getWrapped().replace(
				keyConverter.toWrapped(key),
				valueConverter.toWrapped(value)
			)
		);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return valueConverter.fromWrapped(getWrapped().computeIfAbsent(keyConverter.toWrapped(key),
				kw -> valueConverter.toWrapped(mappingFunction.apply(keyConverter.fromWrapped(kw)
					)
				)
			)
		);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return valueConverter.fromWrapped(getWrapped().computeIfPresent(keyConverter.toWrapped(key),
				(kw, vw) -> valueConverter.toWrapped(remappingFunction.apply(keyConverter.fromWrapped(kw),
						valueConverter.fromWrapped(vw)
					)
				)
			)
		);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return valueConverter.fromWrapped(getWrapped().compute(keyConverter.toWrapped(key),
				(kw, vw) -> valueConverter.toWrapped(remappingFunction.apply(keyConverter.fromWrapped(kw),
						valueConverter.fromWrapped(vw)
					)
				)
			)
		);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return valueConverter.fromWrapped(getWrapped().merge(keyConverter.toWrapped(key),
				valueConverter.toWrapped(value),
				(oldVW, vw) -> valueConverter.toWrapped(remappingFunction.apply(valueConverter.fromWrapped(oldVW),
						valueConverter.fromWrapped(vw)
					)
				)
			)
		);
	}
}
