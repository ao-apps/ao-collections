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

import java.util.NavigableMap;
import java.util.SortedMap;

/**
 * Wraps a {@link SortedMap}, with optional type conversion.
 *
 * @author  AO Industries, Inc.
 */
public class SortedMapWrapper<K,V,KW,VW> extends MapWrapper<K,V,KW,VW> implements SortedMap<K,V> {

	/**
	 * Wraps a sorted map.
	 * <ol>
	 * <li>If the given map is a {@link NavigableMap}, then will return a {@link NavigableMapWrapper}.</li>
	 * </ol>
	 *
	 * @see  NavigableMapWrapper#of(java.util.NavigableMap, com.aoindustries.collections.wrapper.Converter, com.aoindustries.collections.wrapper.Converter)
	 */
	public static <K,V,KW,VW> SortedMapWrapper<K,V,KW,VW> of(
		SortedMap<KW,VW> map,
		Converter<K,KW> keyConverter,
		Converter<V,VW> valueConverter
	) {
		if(map instanceof NavigableMap) {
			return NavigableMapWrapper.of((NavigableMap<KW,VW>)map, keyConverter, valueConverter);
		}
		return (map == null) ? null : new SortedMapWrapper<>(map, keyConverter, valueConverter);
	}

	/**
	 * @see  #of(java.util.SortedMap, com.aoindustries.collections.wrapper.Converter, com.aoindustries.collections.wrapper.Converter)
	 * @see  Converter#identity()
	 */
	public static <K,V> SortedMapWrapper<K,V,K,V> of(SortedMap<K,V> map) {
		return of(map, Converter.identity(), Converter.identity());
	}

	protected SortedMapWrapper(SortedMap<KW,VW> wrapped, Converter<K,KW> keyConverter, Converter<V,VW> valueConverter) {
		super(wrapped, keyConverter, valueConverter);
	}

	@Override
	protected SortedMap<KW,VW> getWrapped() {
		return (SortedMap<KW,VW>)super.getWrapped();
	}

	private ComparatorWrapper<K,KW> comparator;

	@Override
	public ComparatorWrapper<K,KW> comparator() {
		ComparatorWrapper<K,KW> c = comparator;
		if(c == null) {
			c = ComparatorWrapper.of(getWrapped().comparator(), keyConverter);
			comparator = c;
		}
		return c;
	}

	@Override
	public SortedMapWrapper<K,V,KW,VW> subMap(K fromKey, K toKey) {
		return of(
			getWrapped().subMap(
				keyConverter.toWrapped(fromKey),
				keyConverter.toWrapped(toKey)
			),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public SortedMapWrapper<K,V,KW,VW> headMap(K toKey) {
		return of(
			getWrapped().headMap(
				keyConverter.toWrapped(toKey)
			),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public SortedMapWrapper<K,V,KW,VW> tailMap(K fromKey) {
		return of(
			getWrapped().tailMap(
				keyConverter.toWrapped(fromKey)
			),
			keyConverter,
			valueConverter
		);
	}

	@Override
	public K firstKey() {
		return keyConverter.fromWrapped(getWrapped().firstKey());
	}

	@Override
	public K lastKey() {
		return keyConverter.fromWrapped(getWrapped().lastKey());
	}
}
