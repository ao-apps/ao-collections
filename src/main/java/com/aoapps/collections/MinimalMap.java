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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * MinimalMap provides a set of static methods to dynamically choose the most
 * efficient Map implementation.  The implementation of Map is changed as needed.
 * MinimalMap is most suited for building map-based data structures that use less
 * heap space than a pure HashMap-based solution.
 *
 * <p>Insertion order is maintained.</p>
 *
 * <p>size=0: {@link Collections#emptyMap()}<br>
 * size=1: {@link Collections#singletonMap(java.lang.Object, java.lang.Object)}<br>
 * size=2: {@link LinkedHashMap}</p>
 *
 * @author  AO Industries, Inc.
 */
public final class MinimalMap {

  /** Make no instances. */
  private MinimalMap() {
    throw new AssertionError();
  }

  /**
   * Gets the empty map representation.
   */
  public static <K, V> Map<K, V> emptyMap() {
    return Collections.emptyMap();
  }

  /**
   * Puts a new element in a map, returning the (possibly new) map.
   */
  public static <K, V> Map<K, V> put(Map<K, V> map, K key, V value) {
    // Still supporting null map for API compatibility
    if (map == null || map.isEmpty()) {
      // The first entry is always a singletonMap
      map = Collections.singletonMap(key, value);
    } else if (map.size() == 1) {
      // Is a singleton map
      Map.Entry<K, V> entry = map.entrySet().iterator().next();
      K entryKey = entry.getKey();
      if (Objects.equals(key, entryKey)) {
        // If have the same key, replace entry
        map = Collections.singletonMap(entryKey, value);
      } else {
        // Is a second property
        map = new LinkedHashMap<>(8);
        map.put(entryKey, entry.getValue());
        map.put(key, value);
      }
    } else {
      // Is a LinkedHashMap
      map.put(key, value);
    }
    return map;
  }

  /**
   * Removes an element from a map, returning the (possibly new) map.
   */
  public static <K, V> Map<K, V> remove(final Map<K, V> map, final K key) {
    // Still supporting null map for API compatibility
    if (map == null || map.isEmpty()) {
      // Empty map, nothing to remove
      return Collections.emptyMap();
    } else if (map.size() == 1) {
      // Is a singleton map
      if (map.containsKey(key)) {
        // Map is now empty
        return Collections.emptyMap();
      } else {
        // Map unchanged
        return map;
      }
    } else {
      // Is a LinkedHashMap
      map.remove(key);
      if (map.size() == 1) {
        // Convert to singletonMap
        Map.Entry<K, V> entry = map.entrySet().iterator().next();
        return Collections.singletonMap(entry.getKey(), entry.getValue());
      } else {
        // Still more than one item, use same LinkedHashMap instance
        assert map.size() > 1;
        return map;
      }
    }
  }

  /**
   * Gets an element from a map.
   *
   * @deprecated  Since empty maps are no longer represented by {@code null}, invoke
   *              {@link Map#get(java.lang.Object)} directly now.
   */
  @Deprecated
  public static <K, V> V get(Map<K, V> map, K key) {
    // Still supporting null map for API compatibility
    return map == null ? null : map.get(key);
  }

  /**
   * Checks if a key is contained in the map.
   *
   * @deprecated  Since empty maps are no longer represented by {@code null}, invoke
   *              {@link Map#containsKey(java.lang.Object)} directly now.
   */
  @Deprecated
  public static <K, V> boolean containsKey(Map<K, V> map, K key) {
    // Still supporting null map for API compatibility
    return map != null && map.containsKey(key);
  }

  /**
   * Gets the value collection.
   *
   * @deprecated  Since empty maps are no longer represented by {@code null}, invoke
   *              {@link Map#values()} directly now.
   */
  @Deprecated
  public static <K, V> Collection<V> values(Map<K, V> map) {
    // Still supporting null map for API compatibility
    if (map == null) {
      return Collections.emptyList();
    } else {
      return map.values();
    }
  }

  /**
   * Performs a shallow copy of the value collection.
   */
  public static <K, V> Collection<V> valuesCopy(Map<K, V> map) {
    // Still supporting null map for API compatibility
    if (map == null || map.isEmpty()) {
      return Collections.emptyList();
    } else if (map.size() == 1) {
      // singletonMap is unmodifiable: no wrapping required
      return map.values();
    } else {
      // Wrap in an ArrayList
      return new ArrayList<>(map.values());
    }
  }

  /**
   * Performs a shallow copy of a map.  The map is assumed to have been
   * created by MinimalMap and to be used through MinimalMap.
   */
  public static <K, V> Map<K, V> copy(Map<K, V> map) {
    // Still supporting null map for API compatibility
    if (map == null || map.isEmpty()) {
      // Empty
      return Collections.emptyMap();
    }
    if (map.size() == 1) {
      // Is a singletonMap (unmodifiable) - safe to share instance.
      return map;
    }
    // Create copy of map
    return new LinkedHashMap<>(map);
  }

  /**
   * Gets an unmodifiable wrapper around this map.
   * May or may not wrap this map itself.
   */
  public static <K, V> Map<K, V> unmodifiable(Map<K, V> map) {
    // Still supporting null map for API compatibility
    if (map == null) {
      // Empty
      return Collections.emptyMap();
    }
    return AoCollections.optimalUnmodifiableMap(map);
  }
}
