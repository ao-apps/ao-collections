/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2013, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MinimalList provides a set of static methods to dynamically choose the most
 * efficient List implementation.  The implementation of List is changed as needed.
 * MinimalList is most suited for building list-based data structures that use less
 * heap space than a pure ArrayList-based solution.
 *
 * <p>size=0: {@link Collections#emptyList()}<br>
 * size=1: {@link Collections#singletonList(java.lang.Object)}<br>
 * size=2: {@link ArrayList}</p>
 *
 * @author  AO Industries, Inc.
 */
// TODO: Add assertions throughtout both this and MinimalMap
public final class MinimalList {

  /** Make no instances. */
  private MinimalList() {
    throw new AssertionError();
  }

  /**
   * Gets the empty list representation.
   */
  public static <E> List<E> emptyList() {
    return Collections.emptyList();
  }

  /**
   * Adds a new element to a list, returning the (possibly new) list.
   */
  public static <E> List<E> add(List<E> list, E elem) {
    // Still supporting null list for API compatibility
    if (list == null || list.isEmpty()) {
      // The first element is always a singletonList
      list = Collections.singletonList(elem);
    } else if (list.size() == 1) {
      // Is a singleton list
      List<E> newList = new ArrayList<>(8);
      newList.add(list.get(0));
      newList.add(elem);
      list = newList;
    } else {
      // Is an ArrayList
      list.add(elem);
    }
    return list;
  }

  /**
   * Gets an element from a list.
   *
   * @deprecated  Since empty lists are no longer represented by {@code null}, invoke
   *              {@link List#get(int)} directly now.
   */
  @Deprecated
  public static <E> E get(List<E> list, int index) throws IndexOutOfBoundsException {
    // Still supporting null list for API compatibility
    if (list == null) {
      throw new IndexOutOfBoundsException();
    }
    return list.get(index);
  }

  /**
   * Performs a shallow copy of a list.  The list is assumed to have been
   * created by MinimalList and to be used through MinimalList.
   */
  public static <E> List<E> copy(List<E> list) {
    // Still supporting null list for API compatibility
    if (list == null || list.isEmpty()) {
      // Empty
      return Collections.emptyList();
    }
    if (list.size() == 1) {
      // Is a singletonList (unmodifiable) - safe to share instance.
      return list;
    }
    // Create copy of list
    return new ArrayList<>(list);
  }

  /**
   * Gets an unmodifiable wrapper around this list.
   * May or may not wrap this list itself.
   */
  public static <E> List<E> unmodifiable(List<E> list) {
    // Still supporting null list for API compatibility
    if (list == null) {
      // Empty
      return Collections.emptyList();
    }
    return AoCollections.optimalUnmodifiableList(list);
  }
}
