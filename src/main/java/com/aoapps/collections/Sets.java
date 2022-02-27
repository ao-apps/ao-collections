/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-collections.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.collections;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * General-purpose utilities for working with {@link Set}.
 *
 * @author  AO Industries, Inc.
 */
public final class Sets {

	/** Make no instances. */
	private Sets() {throw new AssertionError();}

	/**
	 * Combines two sets, maintaining order.
	 *
	 * @param  set1  may be {@code null}, which will be treated same as an empty set
	 * @param  set2  may be {@code null}, which will be treated same as an empty set
	 *
	 * @see  LinkedHashSet
	 */
	// TODO:? https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/SetUtils.html#union-java.util.Set-java.util.Set-
	public static <E> Set<E> union(Set<? extends E> set1, Set<? extends E> set2) {
		Set<E> union = AoCollections.newLinkedHashSet(
			(set1 == null ? 0 : set1.size())
			+ (set2 == null ? 0 : set2.size())
		);
		if(set1 != null) union.addAll(set1);
		if(set2 != null) union.addAll(set2);
		return union;
	}

	/**
	 * Combines multiple sets, maintaining order.
	 *
	 * @param  sets  may be {@code null}, which will be treated same as an empty array.
	 *               Elements may be {@code null}, which will be treated same as an empty set.
	 *
	 * @see  LinkedHashSet
	 */
	@SafeVarargs
	public static <E> Set<E> union(Set<? extends E> ... sets) {
		int size = 0;
		if(sets != null) {
			for(Set<? extends E> set : sets) {
				if(set != null) size += set.size();
			}
		}
		Set<E> union = AoCollections.newLinkedHashSet(size);
		if(sets != null) {
			for(Set<? extends E> set : sets) {
				if(set != null) union.addAll(set);
			}
		}
		return union;
	}

	/**
	 * Combines a set with some new elements, maintaining order.
	 *
	 * @param  set  may be {@code null}, which will be treated same as an empty set
	 * @param  elements  may be {@code null}, which will be treated same as an empty array
	 *
	 * @see  LinkedHashSet
	 */
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <E> Set<E> union(Set<? extends E> set, E ... elements) {
		int size =
			(set == null ? 0 : set.size())
			+ (elements == null ? 0 : elements.length);
		Set<E> union = AoCollections.newLinkedHashSet(size);
		if(set != null) union.addAll(set);
		if(elements != null) union.addAll(Arrays.asList(elements));
		return union;
	}
}
