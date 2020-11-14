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
public class TransformNavigableMap<K,V,KW,VW> extends TransformSortedMap<K,V,KW,VW> implements NavigableMap<K,V> {

	/**
	 * Wraps a navigable map.
	 */
	public static <K,V,KW,VW> TransformNavigableMap<K,V,KW,VW> of(
		NavigableMap<KW,VW> map,
		Transformer<K,KW> keyTransformer,
		Transformer<V,VW> valueTransformer
	) {
		return (map == null) ? null : new TransformNavigableMap<>(map, keyTransformer, valueTransformer);
	}

	/**
	 * @see  #of(java.util.NavigableMap, com.aoindustries.collections.transformers.Transformer, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <K,V> TransformNavigableMap<K,V,K,V> of(NavigableMap<K,V> map) {
		return of(map, Transformer.identity(), Transformer.identity());
	}

	protected TransformNavigableMap(NavigableMap<KW,VW> wrapped, Transformer<K,KW> keyTransformer, Transformer<V,VW> valueTransformer) {
		super(wrapped, keyTransformer, valueTransformer);
	}

	@Override
	protected NavigableMap<KW,VW> getWrapped() {
		return (NavigableMap<KW,VW>)super.getWrapped();
	}

	@Override
	public TransformEntry<K,V,KW,VW> lowerEntry(K key) {
		return TransformEntry.of(getWrapped().lowerEntry(keyTransformer.toWrapped(key)),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public K lowerKey(K key) {
		return keyTransformer.fromWrapped(getWrapped().lowerKey(keyTransformer.toWrapped(key)));
	}

	@Override
	public TransformEntry<K,V,KW,VW> floorEntry(K key) {
		return TransformEntry.of(getWrapped().floorEntry(keyTransformer.toWrapped(key)),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public K floorKey(K key) {
		return keyTransformer.fromWrapped(getWrapped().floorKey(keyTransformer.toWrapped(key)));
	}

	@Override
	public TransformEntry<K,V,KW,VW> ceilingEntry(K key) {
		return TransformEntry.of(getWrapped().ceilingEntry(keyTransformer.toWrapped(key)),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public K ceilingKey(K key) {
		return keyTransformer.fromWrapped(getWrapped().ceilingKey(keyTransformer.toWrapped(key)));
	}

	@Override
	public TransformEntry<K,V,KW,VW> higherEntry(K key) {
		return TransformEntry.of(getWrapped().higherEntry(keyTransformer.toWrapped(key)),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public K higherKey(K key) {
		return keyTransformer.fromWrapped(getWrapped().higherKey(keyTransformer.toWrapped(key)));
	}

	@Override
	public TransformEntry<K,V,KW,VW> firstEntry() {
		return TransformEntry.of(getWrapped().firstEntry(),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformEntry<K,V,KW,VW> lastEntry() {
		return TransformEntry.of(getWrapped().lastEntry(),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformEntry<K,V,KW,VW> pollFirstEntry() {
		return TransformEntry.of(getWrapped().pollFirstEntry(),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformEntry<K,V,KW,VW> pollLastEntry() {
		return TransformEntry.of(getWrapped().pollLastEntry(),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformNavigableMap<K,V,KW,VW> descendingMap() {
		return of(getWrapped().descendingMap(),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformNavigableSet<K,KW> navigableKeySet() {
		return TransformNavigableSet.of(getWrapped().navigableKeySet(),
			keyTransformer
		);
	}

	@Override
	public TransformNavigableSet<K,KW> descendingKeySet() {
		return TransformNavigableSet.of(getWrapped().descendingKeySet(),
			keyTransformer
		);
	}

	@Override
	public TransformNavigableMap<K,V,KW,VW> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		return of(getWrapped().subMap(
				keyTransformer.toWrapped(fromKey),
				fromInclusive,
				keyTransformer.toWrapped(toKey),
				toInclusive
			),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformNavigableMap<K,V,KW,VW> headMap(K toKey, boolean inclusive) {
		return of(getWrapped().headMap(
				keyTransformer.toWrapped(toKey),
				inclusive
			),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformNavigableMap<K,V,KW,VW> tailMap(K fromKey, boolean inclusive) {
		return of(getWrapped().tailMap(
				keyTransformer.toWrapped(fromKey),
				inclusive
			),
			keyTransformer,
			valueTransformer
		);
	}
}
