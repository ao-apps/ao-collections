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
import java.util.SortedMap;

/**
 * Wraps a {@link SortedMap}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class TransformSortedMap<K,V,KW,VW> extends TransformMap<K,V,KW,VW> implements SortedMap<K,V> {

	/**
	 * Wraps a sorted map.
	 * <ol>
	 * <li>If the given map is a {@link NavigableMap}, then will return a {@link TransformNavigableMap}.</li>
	 * </ol>
	 *
	 * @see  TransformNavigableMap#of(java.util.NavigableMap, com.aoindustries.collections.transformers.Transformer, com.aoindustries.collections.transformers.Transformer)
	 */
	public static <K,V,KW,VW> TransformSortedMap<K,V,KW,VW> of(
		SortedMap<KW,VW> map,
		Transformer<K,KW> keyTransformer,
		Transformer<V,VW> valueTransformer
	) {
		if(map instanceof NavigableMap) {
			return TransformNavigableMap.of((NavigableMap<KW,VW>)map, keyTransformer, valueTransformer);
		}
		return (map == null) ? null : new TransformSortedMap<>(map, keyTransformer, valueTransformer);
	}

	/**
	 * @see  #of(java.util.SortedMap, com.aoindustries.collections.transformers.Transformer, com.aoindustries.collections.transformers.Transformer)
	 * @see  Transformer#identity()
	 */
	public static <K,V> TransformSortedMap<K,V,K,V> of(SortedMap<K,V> map) {
		return of(map, Transformer.identity(), Transformer.identity());
	}

	protected TransformSortedMap(SortedMap<KW,VW> wrapped, Transformer<K,KW> keyTransformer, Transformer<V,VW> valueTransformer) {
		super(wrapped, keyTransformer, valueTransformer);
	}

	@Override
	protected SortedMap<KW,VW> getWrapped() {
		return (SortedMap<KW,VW>)super.getWrapped();
	}

	private TransformComparator<K,KW> comparator;

	@Override
	public TransformComparator<K,KW> comparator() {
		TransformComparator<K,KW> c = comparator;
		if(c == null) {
			c = TransformComparator.of(getWrapped().comparator(), keyTransformer);
			comparator = c;
		}
		return c;
	}

	@Override
	public TransformSortedMap<K,V,KW,VW> subMap(K fromKey, K toKey) {
		return of(getWrapped().subMap(
				keyTransformer.toWrapped(fromKey),
				keyTransformer.toWrapped(toKey)
			),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformSortedMap<K,V,KW,VW> headMap(K toKey) {
		return of(getWrapped().headMap(
				keyTransformer.toWrapped(toKey)
			),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public TransformSortedMap<K,V,KW,VW> tailMap(K fromKey) {
		return of(getWrapped().tailMap(
				keyTransformer.toWrapped(fromKey)
			),
			keyTransformer,
			valueTransformer
		);
	}

	@Override
	public K firstKey() {
		return keyTransformer.fromWrapped(getWrapped().firstKey());
	}

	@Override
	public K lastKey() {
		return keyTransformer.fromWrapped(getWrapped().lastKey());
	}
}
