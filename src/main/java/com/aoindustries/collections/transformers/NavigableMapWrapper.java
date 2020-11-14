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

import java.util.NavigableMap;

/**
 * Wraps a {@link NavigableMap}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class NavigableMapWrapper<K,V,KW,VW> extends SortedMapWrapper<K,V,KW,VW> implements NavigableMap<K,V> {

	/**
	 * Wraps a navigable map.
	 */
	public static <K,V,KW,VW> NavigableMapWrapper<K,V,KW,VW> of(
		NavigableMap<KW,VW> map,
		Converter<K,KW> keyConverter,
		Converter<V,VW> valueConverter
	) {
		return (map == null) ? null : new NavigableMapWrapper<>(map, keyConverter, valueConverter);
	}

	/**
	 * @see  #of(java.util.NavigableMap, com.aoindustries.collections.transformers.Converter, com.aoindustries.collections.transformers.Converter)
	 * @see  Converter#identity()
	 */
	public static <K,V> NavigableMapWrapper<K,V,K,V> of(NavigableMap<K,V> map) {
		return of(map, Converter.identity(), Converter.identity());
	}

	protected NavigableMapWrapper(NavigableMap<KW,VW> wrapped, Converter<K,KW> keyConverter, Converter<V,VW> valueConverter) {
		super(wrapped, keyConverter, valueConverter);
	}

	@Override
	protected NavigableMap<KW,VW> getWrapped() {
		return (NavigableMap<KW,VW>)super.getWrapped();
	}

	@Override
	public EntryWrapper<K,V,KW,VW> lowerEntry(K key) {
		return EntryWrapper.of(
			getWrapped().lowerEntry(keyConverter.toWrapped(key)),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public K lowerKey(K key) {
		return keyConverter.fromWrapped(getWrapped().lowerKey(keyConverter.toWrapped(key)));
	}

	@Override
	public EntryWrapper<K,V,KW,VW> floorEntry(K key) {
		return EntryWrapper.of(
			getWrapped().floorEntry(keyConverter.toWrapped(key)),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public K floorKey(K key) {
		return keyConverter.fromWrapped(getWrapped().floorKey(keyConverter.toWrapped(key)));
	}

	@Override
	public EntryWrapper<K,V,KW,VW> ceilingEntry(K key) {
		return EntryWrapper.of(
			getWrapped().ceilingEntry(keyConverter.toWrapped(key)),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public K ceilingKey(K key) {
		return keyConverter.fromWrapped(getWrapped().ceilingKey(keyConverter.toWrapped(key)));
	}

	@Override
	public EntryWrapper<K,V,KW,VW> higherEntry(K key) {
		return EntryWrapper.of(
			getWrapped().higherEntry(keyConverter.toWrapped(key)),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public K higherKey(K key) {
		return keyConverter.fromWrapped(getWrapped().higherKey(keyConverter.toWrapped(key)));
	}

	@Override
	public EntryWrapper<K,V,KW,VW> firstEntry() {
		return EntryWrapper.of(
			getWrapped().firstEntry(),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public EntryWrapper<K,V,KW,VW> lastEntry() {
		return EntryWrapper.of(
			getWrapped().lastEntry(),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public EntryWrapper<K,V,KW,VW> pollFirstEntry() {
		return EntryWrapper.of(
			getWrapped().pollFirstEntry(),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public EntryWrapper<K,V,KW,VW> pollLastEntry() {
		return EntryWrapper.of(
			getWrapped().pollLastEntry(),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public NavigableMapWrapper<K,V,KW,VW> descendingMap() {
		return of(
			getWrapped().descendingMap(),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public NavigableSetWrapper<K,KW> navigableKeySet() {
		return NavigableSetWrapper.of(
			getWrapped().navigableKeySet(),
			keyConverter
		);
	}

	@Override
	public NavigableSetWrapper<K,KW> descendingKeySet() {
		return NavigableSetWrapper.of(
			getWrapped().descendingKeySet(),
			keyConverter
		);
	}

	@Override
	public NavigableMapWrapper<K,V,KW,VW> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		return of(
			getWrapped().subMap(
				keyConverter.toWrapped(fromKey),
				fromInclusive,
				keyConverter.toWrapped(toKey),
				toInclusive
			),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public NavigableMapWrapper<K,V,KW,VW> headMap(K toKey, boolean inclusive) {
		return of(
			getWrapped().headMap(
				keyConverter.toWrapped(toKey),
				inclusive
			),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public NavigableMapWrapper<K,V,KW,VW> tailMap(K fromKey, boolean inclusive) {
		return of(
			getWrapped().tailMap(
				keyConverter.toWrapped(fromKey),
				inclusive
			),
			keyConverter,
			valueConverter
		);
	}
}
