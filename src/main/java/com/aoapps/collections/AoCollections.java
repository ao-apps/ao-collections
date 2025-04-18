/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2010, 2011, 2012, 2013, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025  AO Industries, Inc.
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

import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * General-purpose collection utilities and constants.
 *
 * @author  AO Industries, Inc.
 */
public final class AoCollections {

  /** Make no instances. */
  private AoCollections() {
    throw new AssertionError();
  }

  /**
   * {@link Collections#emptySortedSet()}.
   *
   * @deprecated  Please use {@link Collections#emptySortedSet()}.
   */
  @Deprecated(forRemoval = true)
  public static final SortedSet<?> EMPTY_SORTED_SET = new EmptySortedSet();

  /**
   * {@link Collections#emptySortedSet()}.
   *
   * @deprecated  Please use {@link Collections#emptySortedSet()}.
   */
  @Deprecated(forRemoval = true)
  @SuppressWarnings("unchecked")
  public static <T> SortedSet<T> emptySortedSet() {
    return (SortedSet<T>) EMPTY_SORTED_SET;
  }

  @Deprecated(forRemoval = true)
  private static final class EmptySortedSet extends AbstractSet<Object> implements SortedSet<Object>, Serializable {

    private static final long serialVersionUID = 5914343416838268017L;

    private EmptySortedSet() {
    }

    @Override
    public Iterator<Object> iterator() {
      return new Iterator<>() {
        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public Object next() throws NoSuchElementException {
          throw new NoSuchElementException();
        }
      };
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public boolean contains(Object obj) {
      return false;
    }

    private Object readResolve() {
      return EMPTY_SORTED_SET;
    }

    @Override
    public Comparator<? super Object> comparator() {
      return null;
    }

    @Override
    public SortedSet<Object> subSet(Object fromElement, Object toElement) {
      throw new IllegalArgumentException();
    }

    @Override
    public SortedSet<Object> headSet(Object toElement) {
      throw new IllegalArgumentException();
    }

    @Override
    public SortedSet<Object> tailSet(Object fromElement) {
      throw new IllegalArgumentException();
    }

    @Override
    public Object first() {
      throw new NoSuchElementException();
    }

    @Override
    public Object last() {
      throw new NoSuchElementException();
    }
  }

  public static <T> SortedSet<T> singletonSortedSet(T o) {
    return new SingletonSortedSet<>(o);
  }

  private static final class SingletonSortedSet<E> extends AbstractSet<E> implements SortedSet<E>, Serializable {

    private static final long serialVersionUID = -6732971044735913580L;

    private final E element;

    private SingletonSortedSet(E e) {
      element = e;
    }

    @Override
    public Iterator<E> iterator() {
      return new Iterator<>() {
        private boolean hasNext = true;

        @Override
        public boolean hasNext() {
          return hasNext;
        }

        @Override
        public E next() throws NoSuchElementException {
          if (!hasNext) {
            throw new NoSuchElementException();
          }
          hasNext = false;
          return element;
        }
      };
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public boolean contains(Object o) {
      return Objects.equals(o, element);
    }

    @Override
    public Comparator<? super E> comparator() {
      return null;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
      if (Objects.equals(element, fromElement) && Objects.equals(element, toElement)) {
        return Collections.emptySortedSet();
      }
      throw new IllegalArgumentException();
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
      if (Objects.equals(element, toElement)) {
        return Collections.emptySortedSet();
      }
      throw new IllegalArgumentException();
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public SortedSet<E> tailSet(E fromElement) {
      if (Objects.equals(element, fromElement)) {
        return this;
      }
      throw new IllegalArgumentException();
    }

    @Override
    public E first() {
      return element;
    }

    @Override
    public E last() {
      return element;
    }
  }

  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  private static final Class<?>[] unmodifiableCollectionClasses = {
      // Collection
      Collections.unmodifiableCollection(Collections.emptyList()).getClass(),

      // List
      Collections.singletonList(null).getClass(),
      Collections.unmodifiableList(new ArrayList<>(0)).getClass(), // RandomAccess
      Collections.unmodifiableList(new LinkedList<>()).getClass(), // Sequential

      // Set
      Collections.singleton(null).getClass(),
      Collections.unmodifiableSet(Collections.emptySet()).getClass(),
      //UnionMethodSet.class, // Is now read-through
      AoArrays.UnmodifiableArraySet.class,

      // SortedSet
      SingletonSortedSet.class,
      Collections.unmodifiableSortedSet(Collections.emptySortedSet()).getClass(),
  };

  /**
   * Gets the optimal implementation for unmodifiable collection.
   * If collection is empty, uses <code>Collections.emptyList</code>.
   * If the collection is already unmodifiable, returns the same collection.
   * If collection has one element, uses <code>Collections.singletonList</code>.
   * Otherwise, wraps the collection with <code>Collections.unmodifiableCollection</code>.
   *
   * @param  collection  may be {@code null}, which will return {@code null}
   */
  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  public static <T> Collection<T> optimalUnmodifiableCollection(Collection<? extends T> collection) {
    if (collection == null) {
      return null;
    }
    int size = collection.size();
    if (size == 0) {
      return Collections.emptyList();
    }
    Class<?> clazz = collection.getClass();
    for (int i = 0, len = unmodifiableCollectionClasses.length; i < len; i++) {
      if (unmodifiableCollectionClasses[i] == clazz) {
        // Safe change of generic bounds only because returned collection is unmodifiable
        @SuppressWarnings("unchecked")
        Collection<T> unmodifiable = (Collection<T>) collection;
        return unmodifiable;
      }
    }
    if (size == 1) {
      return Collections.singletonList(collection.iterator().next());
    }
    return Collections.unmodifiableCollection(collection);
  }

  /**
   * Gets a collection from an iterable.
   * Casts the iterable to collection, if possible.
   * Otherwise builds a new list from the iterable, maintaining iteration order.
   *
   * @param  iterable  may be {@code null}, which will return {@code null}
   */
  public static <E> Collection<E> asCollection(Iterable<E> iterable) {
    if (iterable == null) {
      return null;
    }
    if (iterable instanceof Collection) {
      return (Collection<E>) iterable;
    }
    List<E> list = new ArrayList<>();
    for (E elem : iterable) {
      list.add(elem);
    }
    return list;
  }

  private static <T> Collection<T> unmodifiableCopyCollection(Collection<? extends T> collection, boolean copyNeeded) {
    if (collection == null) {
      return null;
    }
    int size = collection.size();
    if (size == 0) {
      return Collections.emptyList();
    }
    // TODO: Create an unmodifiable collection that can only be populated here, and reused.
    // TODO: Goal is to protect from changes to original collection, while also not having
    // TODO: to copy repeatedly when different components use this same method for protection.
    // TODO: Also allow standard Collections singleton
    //Class<?> clazz = collection.getClass();
    //for (int i=0, len=unmodifiableCollectionClasses.length; i<len; i++) {
    //  if (unmodifiableCollectionClasses[i] == clazz) {
    //    return collection;
    //  }
    //}
    if (size == 1) {
      return Collections.singletonList(collection.iterator().next());
    }
    return Collections.unmodifiableCollection(copyNeeded ? new ArrayList<>(collection) : collection);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable collection.
   *
   * @param  collection  may be {@code null}, which will return {@code null}
   */
  public static <T> Collection<T> unmodifiableCopyCollection(Collection<? extends T> collection) {
    return unmodifiableCopyCollection(collection, true);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable collection.
   *
   * @param  iter  may be {@code null}, which will return {@code null}
   */
  public static <T> Collection<T> unmodifiableCopyCollection(Iterable<? extends T> iter) {
    if (iter == null) {
      return null;
    }
    if (iter instanceof Collection) {
      return unmodifiableCopyCollection((Collection<? extends T>) iter, true);
    }
    return unmodifiableCopyCollection(asCollection(iter), false);
  }

  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  private static final Class<?>[] unmodifiableListClasses = {
      Collections.singletonList(null).getClass(),
      Collections.unmodifiableList(new ArrayList<>(0)).getClass(), // RandomAccess
      Collections.unmodifiableList(new LinkedList<>()).getClass() // Sequential
  };

  /**
   * Gets the optimal implementation for unmodifiable list.
   * If list is empty, uses <code>Collections.emptyList</code>.
   * If the list is already unmodifiable, returns the same list.
   * If list has one element, uses <code>Collections.singletonList</code>.
   * Otherwise, wraps the list with <code>Collections.unmodifiableList</code>,
   * and will also call "trimToSize" if the list is an ArrayList.
   *
   * @param  list  may be {@code null}, which will return {@code null}
   *
   * @see Collections#emptyList()
   * @see Collections#singletonList(java.lang.Object)
   * @see ArrayList#trimToSize()
   * @see Collections#unmodifiableList(java.util.List)
   */
  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  public static <T> List<T> optimalUnmodifiableList(List<? extends T> list) {
    if (list == null) {
      return null;
    }
    int size = list.size();
    if (size == 0) {
      return Collections.emptyList();
    }
    Class<?> clazz = list.getClass();
    for (int i = 0, len = unmodifiableListClasses.length; i < len; i++) {
      if (unmodifiableListClasses[i] == clazz) {
        // Safe change of generic bounds only because returned list is unmodifiable
        @SuppressWarnings("unchecked")
        List<T> unmodifiable = (List<T>) list;
        return unmodifiable;
      }
    }
    if (size == 1) {
      return Collections.singletonList(list.get(0));
    }
    if (list instanceof ArrayList) {
      ((ArrayList) list).trimToSize();
    }
    return Collections.unmodifiableList(list);
  }

  /**
   * Gets a list from an iterable.
   * Casts the iterable to list, if possible.
   * Otherwise builds a new list from the iterable, maintaining iteration order.
   *
   * @param  iterable  may be {@code null}, which will return {@code null}
   */
  public static <E> List<E> asList(Iterable<E> iterable) {
    if (iterable == null) {
      return null;
    }
    if (iterable instanceof List) {
      return (List<E>) iterable;
    }
    List<E> list = new ArrayList<>();
    for (E elem : iterable) {
      list.add(elem);
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  private static <T> List<T> unmodifiableCopyList(Collection<? extends T> collection, boolean copyNeeded) {
    if (collection == null) {
      return null;
    }
    int size = collection.size();
    if (size == 0) {
      return Collections.emptyList();
    }
    // TODO: Create an unmodifiable collection that can only be populated here, and reused.
    // TODO: Goal is to protect from changes to original collection, while also not having
    // TODO: to copy repeatedly when different components use this same method for protection.
    // TODO: Also allow standard Collections singleton
    //Class<?> clazz = collection.getClass();
    //for (int i=0, len=unmodifiableListClasses.length; i<len; i++) {
    //  if (unmodifiableListClasses[i] == clazz) {
    //    return (List<T>)collection;
    //  }
    //}
    if (size == 1) {
      return Collections.singletonList(collection.iterator().next());
    }
    if (!copyNeeded && collection instanceof List) {
      return Collections.unmodifiableList((List<T>) collection);
    }
    return Collections.unmodifiableList(new ArrayList<>(collection));
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable list.
   *
   * @param  collection  may be {@code null}, which will return {@code null}
   */
  public static <T> List<T> unmodifiableCopyList(Collection<? extends T> collection) {
    return unmodifiableCopyList(collection, true);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable list.
   *
   * @param  iter  may be {@code null}, which will return {@code null}
   */
  public static <T> List<T> unmodifiableCopyList(Iterable<? extends T> iter) {
    if (iter == null) {
      return null;
    }
    if (iter instanceof Collection) {
      return unmodifiableCopyList((Collection<? extends T>) iter, true);
    }
    return unmodifiableCopyList(asList(iter), false);
  }

  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  private static final Class<?>[] unmodifiableSetClasses = {
      // Set
      Collections.singleton(null).getClass(),
      Collections.unmodifiableSet(Collections.emptySet()).getClass(),
      Collections.unmodifiableMap(Collections.emptyMap()).entrySet().getClass(),
      //UnionMethodSet.class, // Is now read-through
      AoArrays.UnmodifiableArraySet.class,

      // SortedSet
      SingletonSortedSet.class,
      Collections.unmodifiableSortedSet(Collections.emptySortedSet()).getClass()
  };

  /**
   * Gets the optimal implementation for unmodifiable set.
   * If set is empty, uses <code>Collections.emptySet</code>.
   * If the set is already unmodifiable, returns the same set.
   * If set has one element, uses <code>Collections.singleton</code>.
   * Otherwise, wraps the set with <code>Collections.unmodifiableSet</code>.
   *
   * @param  set  may be {@code null}, which will return {@code null}
   */
  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  public static <T> Set<T> optimalUnmodifiableSet(Set<? extends T> set) {
    if (set == null) {
      return null;
    }
    int size = set.size();
    if (size == 0) {
      return Collections.emptySet();
    }
    Class<?> clazz = set.getClass();
    for (int i = 0, len = unmodifiableSetClasses.length; i < len; i++) {
      if (unmodifiableSetClasses[i] == clazz) {
        // Safe change of generic bounds only because returned set is unmodifiable
        @SuppressWarnings("unchecked")
        Set<T> unmodifiable = (Set<T>) set;
        return unmodifiable;
      }
    }
    if (size == 1) {
      return Collections.singleton(set.iterator().next());
    }
    return Collections.unmodifiableSet(set);
  }

  /**
   * Gets a set from an iterable.
   * Casts the iterable to set, if possible.
   * Otherwise builds a new set from the iterable, maintaining iteration order.
   *
   * @param  iterable   may be {@code null}, which will return {@code null}
   */
  public static <E> Set<E> asSet(Iterable<E> iterable) {
    if (iterable == null) {
      return null;
    }
    if (iterable instanceof Set) {
      return (Set<E>) iterable;
    }
    Set<E> set = new LinkedHashSet<>();
    for (E elem : iterable) {
      set.add(elem);
    }
    return set;
  }

  @SuppressWarnings("unchecked")
  private static <T> Set<T> unmodifiableCopySet(Collection<? extends T> collection, boolean copyNeeded) {
    if (collection == null) {
      return null;
    }
    int size = collection.size();
    if (size == 0) {
      return Collections.emptySet();
    }
    // TODO: Create an unmodifiable collection that can only be populated here, and reused.
    // TODO: Goal is to protect from changes to original collection, while also not having
    // TODO: to copy repeatedly when different components use this same method for protection.
    // TODO: Also allow standard Collections singleton
    //Class<?> clazz = collection.getClass();
    //for (int i=0, len=unmodifiableSetClasses.length; i<len; i++) {
    //  if (unmodifiableSetClasses[i] == clazz) {
    //    return (Set<T>)collection;
    //  }
    //}
    if (size == 1) {
      return Collections.singleton(collection.iterator().next());
    }
    if (!copyNeeded && collection instanceof Set) {
      return Collections.unmodifiableSet((Set<T>) collection);
    }
    Set<T> set = new LinkedHashSet<>(collection);
    if (set.size() == 1) {
      return Collections.singleton(set.iterator().next());
    }
    return Collections.unmodifiableSet(set);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable set.
   * The iteration order of the original set is maintained.
   *
   * @param  collection  may be {@code null}, which will return {@code null}
   */
  public static <T> Set<T> unmodifiableCopySet(Collection<? extends T> collection) {
    return unmodifiableCopySet(collection, true);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable set.
   * The iteration order of the original set is maintained.
   *
   * @param  iter  may be {@code null}, which will return {@code null}
   */
  public static <T> Set<T> unmodifiableCopySet(Iterable<? extends T> iter) {
    if (iter == null) {
      return null;
    }
    if (iter instanceof Collection) {
      return unmodifiableCopySet((Collection<? extends T>) iter, true);
    }
    return unmodifiableCopySet(asSet(iter), false);
  }

  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  private static final Class<?>[] unmodifiableSortedSetClasses = {
      // SortedSet
      SingletonSortedSet.class,
      Collections.unmodifiableSortedSet(Collections.emptySortedSet()).getClass()
  };

  /**
   * Gets the optimal implementation for unmodifiable sorted set.
   * If sorted set is empty, uses {@link Collections#emptySortedSet()}.
   * If the sorted set is already unmodifiable, returns the same sorted set.
   * If sorted set has one element, uses <code>singletonSortedSet</code>.
   * Otherwise, wraps the sorted set with <code>Collections.unmodifiableSortedSet</code>.
   *
   * @param  sortedSet  may be {@code null}, which will return {@code null}
   */
  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  public static <T> SortedSet<T> optimalUnmodifiableSortedSet(SortedSet<T> sortedSet) {
    if (sortedSet == null) {
      return null;
    }
    int size = sortedSet.size();
    if (size == 0) {
      return Collections.emptySortedSet();
    }
    Class<?> clazz = sortedSet.getClass();
    for (int i = 0, len = unmodifiableSortedSetClasses.length; i < len; i++) {
      if (unmodifiableSortedSetClasses[i] == clazz) {
        return sortedSet;
      }
    }
    if (size == 1) {
      return singletonSortedSet(sortedSet.first());
    }
    return Collections.unmodifiableSortedSet(sortedSet);
  }

  /**
   * Gets a sorted set from an iterable.
   * Casts the iterable to sorted set, if possible.
   * Otherwise builds a new sorted set from the iterable, in natural ordering.
   *
   * @param  iterable  may be {@code null}, which will return {@code null}
   */
  public static <E> SortedSet<E> asSortedSet(Iterable<E> iterable) {
    if (iterable == null) {
      return null;
    }
    if (iterable instanceof SortedSet) {
      return (SortedSet<E>) iterable;
    }
    SortedSet<E> sortedSet = new TreeSet<>();
    for (E elem : iterable) {
      sortedSet.add(elem);
    }
    return sortedSet;
  }

  @SuppressWarnings("unchecked")
  private static <T> SortedSet<T> unmodifiableCopySortedSet(Collection<? extends T> collection, boolean copyNeeded) {
    if (collection == null) {
      return null;
    }
    int size = collection.size();
    if (size == 0) {
      return Collections.emptySortedSet();
    }
    // TODO: Create an unmodifiable collection that can only be populated here, and reused.
    // TODO: Goal is to protect from changes to original collection, while also not having
    // TODO: to copy repeatedly when different components use this same method for protection.
    // TODO: Also allow standard Collections singleton
    //Class<?> clazz = collection.getClass();
    //for (int i=0, len=unmodifiableSortedSetClasses.length; i<len; i++) {
    //  if (unmodifiableSortedSetClasses[i] == clazz) {
    //    return (SortedSet<T>)collection;
    //  }
    //}
    if (size == 1) {
      return singletonSortedSet(collection.iterator().next());
    }
    if (!copyNeeded && collection instanceof SortedSet) {
      return Collections.unmodifiableSortedSet((SortedSet<T>) collection);
    }
    SortedSet<T> copy;
    if (collection instanceof SortedSet) {
      copy = new TreeSet<>((SortedSet) collection);
    } else {
      copy = new TreeSet<>(collection);
    }
    if (copy.size() == 1) {
      return singletonSortedSet(copy.iterator().next());
    }
    return Collections.unmodifiableSortedSet(copy);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable sorted set.
   *
   * @param  collection  may be {@code null}, which will return {@code null}
   */
  public static <T> SortedSet<T> unmodifiableCopySortedSet(Collection<? extends T> collection) {
    return unmodifiableCopySortedSet(collection, true);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable sorted set.
   *
   * @param  iter  may be {@code null}, which will return {@code null}
   */
  public static <T> SortedSet<T> unmodifiableCopySortedSet(Iterable<? extends T> iter) {
    if (iter == null) {
      return null;
    }
    if (iter instanceof Collection) {
      return unmodifiableCopySortedSet((Collection<? extends T>) iter, true);
    }
    return unmodifiableCopySortedSet(asSortedSet(iter), false);
  }

  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  private static final Class<?>[] unmodifiableMapClasses = {
      // Map
      Collections.emptyMap().getClass(),
      Collections.singletonMap(null, null).getClass(),
      Collections.unmodifiableMap(Collections.emptyMap()).getClass(),

      // SortedMap
      Collections.unmodifiableSortedMap(new TreeMap<>()).getClass()
  };

  /**
   * Gets the optimal implementation for unmodifiable map.
   * If map is empty, uses <code>Collections.emptyMap</code>.
   * If the map is already unmodifiable, returns the same map.
   * If map has one element, uses <code>Collections.singletonMap</code>.
   * Otherwise, wraps the map with <code>Collections.unmodifiableMap</code>.
   *
   * @param  map  may be {@code null}, which will return {@code null}
   */
  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  public static <K, V> Map<K, V> optimalUnmodifiableMap(Map<? extends K, ? extends V> map) {
    if (map == null) {
      return null;
    }
    int size = map.size();
    if (size == 0) {
      return Collections.emptyMap();
    }
    Class<?> clazz = map.getClass();
    for (int i = 0, len = unmodifiableMapClasses.length; i < len; i++) {
      if (unmodifiableMapClasses[i] == clazz) {
        // Safe change of generic bounds only because returned map is unmodifiable
        @SuppressWarnings("unchecked")
        Map<K, V> unmodifiable = (Map<K, V>) map;
        return unmodifiable;
      }
    }
    if (size == 1) {
      Map.Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
      return Collections.singletonMap(entry.getKey(), entry.getValue());
    }
    return Collections.unmodifiableMap(map);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable map.
   * The iteration order of the original set is maintained.
   *
   * @param  map  may be {@code null}, which will return {@code null}
   */
  public static <K, V> Map<K, V> unmodifiableCopyMap(Map<? extends K, ? extends V> map) {
    if (map == null) {
      return null;
    }
    int size = map.size();
    if (size == 0) {
      return Collections.emptyMap();
    }
    // TODO: Create an unmodifiable collection that can only be populated here, and reused.
    // TODO: Goal is to protect from changes to original collection, while also not having
    // TODO: to copy repeatedly when different components use this same method for protection.
    // TODO: Also allow standard Collections singleton
    //Class<?> clazz = map.getClass();
    //for (int i=0, len=unmodifiableMapClasses.length; i<len; i++) {
    //  if (unmodifiableMapClasses[i] == clazz) {
    //    return map;
    //  }
    //}
    if (size == 1) {
      Map.Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
      return Collections.singletonMap(entry.getKey(), entry.getValue());
    }
    return Collections.unmodifiableMap(new LinkedHashMap<>(map));
  }

  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  private static final Class<?>[] unmodifiableSortedMapClasses = {
      Collections.unmodifiableSortedMap(new TreeMap<>()).getClass()
  };

  /**
   * Gets the optimal implementation for unmodifiable sorted map.
   * If sorted map is empty, uses <code>emptySortedMap</code>.
   * If the sorted map is already unmodifiable, returns the same sorted map.
   * If sorted map has one element, uses <code>singletonSortedMap</code>.
   * Otherwise, wraps the sorted map with <code>Collections.unmodifiableSortedMap</code>.
   *
   * @param  sortedMap  may be {@code null}, which will return {@code null}
   */
  // Java 17: Deprecate: https://bugs.openjdk.org/browse/JDK-6323374
  public static <K, V> SortedMap<K, V> optimalUnmodifiableSortedMap(SortedMap<K, ? extends V> sortedMap) {
    if (sortedMap == null) {
      return null;
    }
    // TODO: int size = sortedMap.size();
    // TODO: if (size == 0) {
    // TODO:   return emptySortedMap();
    // TODO: }
    Class<?> clazz = sortedMap.getClass();
    for (int i = 0, len = unmodifiableSortedMapClasses.length; i < len; i++) {
      if (unmodifiableSortedMapClasses[i] == clazz) {
        // Safe change of generic bounds only because returned map is unmodifiable
        @SuppressWarnings("unchecked")
        SortedMap<K, V> unmodifiable = (SortedMap<K, V>) sortedMap;
        return unmodifiable;
      }
    }
    // TODO: if (size == 1) {
    // TODO:     K key = sortedMap.firstKey();
    // TODO:     return singletonSortedMap(key, sortedMap.get(key));
    // TODO: }
    return Collections.unmodifiableSortedMap(sortedMap);
  }

  /**
   * Performs defensive shallow copy and returns unmodifiable sorted map.
   *
   * @param  map  may be {@code null}, which will return {@code null}
   */
  public static <K, V> SortedMap<K, V> unmodifiableCopySortedMap(Map<K, ? extends V> map) {
    if (map == null) {
      return null;
    }
    // TODO: int size = sortedMap.size();
    // TODO: if (size == 0) {
    // TODO:   return emptySortedMap();
    // TODO: }
    // TODO: Create an unmodifiable collection that can only be populated here, and reused.
    // TODO: Goal is to protect from changes to original collection, while also not having
    // TODO: to copy repeatedly when different components use this same method for protection.
    // TODO: Also allow standard Collections singleton
    //Class<?> clazz = map.getClass();
    //for (int i=0, len=unmodifiableSortedMapClasses.length; i<len; i++) {
    //  if (unmodifiableSortedMapClasses[i] == clazz) {
    //    return (SortedMap<K, V>)map;
    //  }
    //}
    // TODO: if (size == 1) {
    // TODO:     K key = sortedMap.firstKey();
    // TODO:     return singletonSortedMap(key, sortedMap.get(key));
    // TODO: }
    SortedMap<K, V> copy;
    if (map instanceof SortedMap) {
      copy = new TreeMap<>((SortedMap<K, ? extends V>) map);
    } else {
      copy = new TreeMap<>(map);
    }
    return Collections.unmodifiableSortedMap(copy);
  }

  /**
   * Gets the empty iterator.
   *
   * @deprecated  Use {@link Collections#emptyIterator()} as of Java 1.7.
   */
  @Deprecated
  @SuppressWarnings("unchecked")
  public static <E> Iterator<E> emptyIterator() {
    return Collections.emptyIterator();
  }

  /**
   * @deprecated  Please use {@link org.apache.commons.collections4.iterators.SingletonIterator} from
   *              <a href="https://commons.apache.org/proper/commons-collections/">Apache Commons Collections</a>.
   */
  @Deprecated
  static class SingletonIterator<E> implements Iterator<E> {

    private final E value;
    private boolean hasNext = true;

    private SingletonIterator(E value) {
      this.value = value;
    }

    @Override
    public boolean hasNext() {
      return hasNext;
    }

    @Override
    public E next() throws NoSuchElementException {
      if (!hasNext) {
        throw new NoSuchElementException();
      }
      hasNext = false;
      return value;
    }
  }

  /**
   * Gets an unmodifiable iterator for a single object.
   *
   * @deprecated  Please use {@link org.apache.commons.collections4.iterators.SingletonIterator} from
   *              <a href="https://commons.apache.org/proper/commons-collections/">Apache Commons Collections</a>.
   */
  @Deprecated
  public static <E> Iterator<E> singletonIterator(E value) {
    return new SingletonIterator<>(value);
  }

  static class UnmodifiableIterator<E> implements Iterator<E> {

    private final Iterator<? extends E> iter;

    private UnmodifiableIterator(Iterator<? extends E> iter) {
      this.iter = iter;
    }

    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public E next() throws NoSuchElementException {
      return iter.next();
    }
  }

  /**
   * Wraps an iterator to make it unmodifiable.
   * If the iterator is already unmodifiable, returns the same iterator.
   *
   * @param  iter  may be {@code null}, which will return {@code null}
   */
  public static <E> Iterator<E> unmodifiableIterator(Iterator<? extends E> iter) {
    if (iter == null) {
      return null;
    }
    // Don't wrap already unmodifiable iterator types.
    if (
        (iter instanceof UnmodifiableIterator)
            || (iter instanceof EnumerationIterator)
            || (iter instanceof SingletonIterator)
            || (iter == Collections.emptyIterator())
            || (iter == Collections.emptyListIterator())
    ) {
      // Safe change of generic bounds only because returned iterator is unmodifiable
      @SuppressWarnings("unchecked")
      Iterator<E> unmodifiable = (Iterator<E>) iter;
      return unmodifiable;
    }
    return new UnmodifiableIterator<>(iter);
  }

  /*
  private static void test() {
    List<Object> list = new ArrayList<>();
    list.add("One");
    list.add("Two");
    list = optimalUnmodifiableList(list);
    // Collection
    long startTime = System.currentTimeMillis();
    for (int c=0;c<100000000;c++) {
      optimalUnmodifiableList(list);
    }
    long endTime = System.currentTimeMillis() - startTime;
    System.out.println("    Finished optimalUnmodifiableCollection in "+BigDecimal.valueOf(endTime, 3)+" sec");
  }

  public static void main(String[] args) {
    for (int c=0;c<30;c++) test();
  }*/

  /**
   * Allows peeking the first element of iteration.  Does not support remove.
   * Does not support null elements.
   */
  // TODO: Move to own top-level class
  public static class PeekIterator<E> implements Iterator<E> {
    private final Iterator<? extends E> iter;
    private E next;

    private PeekIterator(Iterator<? extends E> iter) {
      this.iter = iter;
      next = iter.hasNext() ? iter.next() : null;
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public E next() throws NoSuchElementException {
      E value = next;
      if (value == null) {
        throw new NoSuchElementException();
      }
      next = iter.hasNext() ? iter.next() : null;
      return value;
    }

    /**
     * Gets the next value without removing it.
     *
     * @throws NoSuchElementException if no next value
     */
    public E peek() {
      E value = next;
      if (value == null) {
        throw new NoSuchElementException();
      }
      return value;
    }
  }

  /**
   * Wraps the provided iterator, allowing peek of first element.
   * Does not support null elements.
   */
  public static <E> PeekIterator<E> peekIterator(Iterator<? extends E> iter) {
    return new PeekIterator<>(iter);
  }

  /**
   * Two collections are considered equal when they are the same size and have the same
   * elements in the same iteration order.
   *
   * <p>If both collections are null they are also considered equal.</p>
   */
  public static boolean equals(Collection<?> collection1, Collection<?> collection2) {
    if (collection1 == null) {
      return collection2 == null;
    } else {
      if (collection2 == null) {
        return false;
      } else {
        int size = collection1.size();
        if (size != collection2.size()) {
          return false;
        } else {
          Iterator<?> iter1 = collection1.iterator();
          Iterator<?> iter2 = collection2.iterator();
          int count = 0;
          while (iter1.hasNext() && iter2.hasNext()) {
            if (
                !Objects.equals(
                    iter1.next(),
                    iter2.next()
                )
            ) {
              return false;
            }
            count++;
          }
          if (
              size != count
                  || iter1.hasNext()
                  || iter2.hasNext()
          ) {
            throw new ConcurrentModificationException();
          }
          return true;
        }
      }
    }
  }

  /**
   * Computes the hashCode of a collection in a manner consistent with
   * AbstractList.
   *
   * @param  iterable  may be {@code null}, which will return {@code 0}
   *
   * @see  AbstractList#hashCode()
   */
  public static int hashCode(Iterable<?> iterable) {
    if (iterable == null) {
      return 0;
    }
    int hashCode = 1;
    Iterator<?> iter = iterable.iterator();
    while (iter.hasNext()) {
      Object e = iter.next();
      hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
    }
    return hashCode;
  }

  /**
   * Filters a list for all elements of a given class.
   *
   * @param  list  may be {@code null}, which will return {@code null}
   */
  public static <E, R extends E> List<R> filter(List<? extends E> list, Class<? extends R> clazz) {
    if (list == null) {
      return null;
    }
    /* Imperative version: */
    List<R> results = MinimalList.emptyList();
    for (E element : list) {
      if (clazz.isInstance(element)) {
        results = MinimalList.add(results, clazz.cast(element));
      }
    }
    return MinimalList.unmodifiable(results);
    /* Functional version:
    return Collections.unmodifiableList(
      list
        .stream()
        .filter(e -> clazz.isInstance(e))
        .map(e -> clazz.cast(e))
        .collect(Collectors.toList())
    );
     */
  }

  //  private static <K, S, V extends S> Map<K, V> filter(Map<K, S> map, Class<V> clazz) {
  //    return filter(map, clazz, LinkedHashMap::new);
  //  }
  //  private static <K, V, R extends V> Map<K, R> filter(Map<K, V> map, Class<R> clazz, Supplier<Map<K, R>> mapSupplier) {
  //    if (map == null) {
  //      return Collections.emptyMap();
  //    } else {
  //      // Imperative version:
  ////      Map<K, R> results = mapSupplier.get();
  ////      for (Map.Entry<K, V> entry : map.entrySet()) {
  ////        V value = entry.getValue();
  ////        if (clazz.isInstance(value)) {
  ////          results.put(entry.getKey(), clazz.cast(value));
  ////        }
  ////      }
  ////      return Collections.unmodifiableMap(results);
  //      // Mixed functional/imperative:
  //      Map<K, R> results = mapSupplier.get();
  //      map.forEach(
  //        (key, value) -> {
  //          if (clazz.isInstance(value)) {
  //            results.put(key, clazz.cast(value));
  //          }
  //        }
  //      );
  //      return Collections.unmodifiableMap(results);
  //      // Functional version:
  ////      return Collections.unmodifiableMap(
  ////        map
  ////          .entrySet()
  ////          .stream()
  ////          .filter(e -> e.getValue() instanceof Contact)
  ////          .collect(
  ////            Collectors.toMap(
  ////              Map.Entry::getKey,
  ////              e -> clazz.cast(e.getValue()),
  ////              (u, v) -> {
  ////                throw new AssertionError("Duplicate keys should not happen since we use key from original map");
  ////              },
  ////              mapSupplier
  ////            )
  ////          )
  ////      );
  //    }
  //  }

  /**
   * Returns a modifiable set of all the keys in a map that match the given value.
   * This is a copy of the keys and will not write-through or be altered by the original map.
   * The set will have the same iteration order as the original map.
   *
   * @param  map  may be {@code null}, which will return {@code null}
   */
  public static <K, V> Set<K> filterByValue(Map<? extends K, ? extends V> map, V value) {
    if (map == null) {
      return null;
    }
    Set<K> filtered = new LinkedHashSet<>();
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      if (Objects.equals(entry.getValue(), value)) {
        K key = entry.getKey();
        if (!filtered.add(key)) {
          throw new AssertionError("Duplicate key: " + key);
        }
      }
    }
    return filtered;
  }

  /**
   * Returns a modifiable sorted set of all the keys in a sorted map that match the given value.
   * This is a copy of the keys and will not write-through or be altered by the original map.
   * The set uses the same comparator as the original map.
   *
   * @param  map  may be {@code null}, which will return {@code null}
   */
  public static <K, V> SortedSet<K> filterByValue(SortedMap<K, ? extends V> map, V value) {
    if (map == null) {
      return null;
    }
    TreeSet<K> filtered = new TreeSet<>(map.comparator());
    for (Map.Entry<K, ? extends V> entry : map.entrySet()) {
      if (Objects.equals(entry.getValue(), value)) {
        K key = entry.getKey();
        if (!filtered.add(key)) {
          throw new AssertionError("Duplicate key: " + key);
        }
      }
    }
    return filtered;
  }

  /**
   * Gets the initial capacity to use for hash-based collections that will contain the given number of elements,
   * assuming the default load factor of {@code 0.75f}.
   *
   * @see  HashMap
   * @see  HashSet
   * @see  IdentityHashMap
   */
  public static int getHashInitialCapacity(int elements) {
    return (int) (elements / 0.75f) + 1;
    //long initialCapacity = (long)elements * 4/3 + 1;
    //return (initialCapacity > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)initialCapacity;
  }

  /**
   * Creates a new {@link ArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  public static <E> ArrayList<E> newArrayList(int elements) {
    return (elements == -1) ? new ArrayList<>() : new ArrayList<>(elements);
  }

  /**
   * Creates a new {@link ArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  public static <E> ArrayList<E> newArrayList(Integer elements) {
    return newArrayList(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link HashMap} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  // Java 19: Deprecate in favor of HashMap.newHashMap, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <K, V> HashMap<K, V> newHashMap(int elements) {
    return (elements == -1) ? new HashMap<>() : new HashMap<>(getHashInitialCapacity(elements));
  }

  /**
   * Creates a new {@link HashMap} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  // Java 19: Deprecate in favor of HashMap.newHashMap, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <K, V> HashMap<K, V> newHashMap(Integer elements) {
    return newHashMap(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link HashSet} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  // Java 19: Deprecate in favor of HashSet.newHashSet, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <E> HashSet<E> newHashSet(int elements) {
    return (elements == -1) ? new HashSet<>() : new HashSet<>(getHashInitialCapacity(elements));
  }

  /**
   * Creates a new {@link HashSet} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  // Java 19: Deprecate in favor of HashSet.newHashSet, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <E> HashSet<E> newHashSet(Integer elements) {
    return newHashSet(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link IdentityHashMap} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  public static <K, V> IdentityHashMap<K, V> newIdentityHashMap(int elements) {
    return (elements == -1) ? new IdentityHashMap<>() : new IdentityHashMap<>(elements);
  }

  /**
   * Creates a new {@link IdentityHashMap} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  public static <K, V> IdentityHashMap<K, V> newIdentityHashMap(Integer elements) {
    return newIdentityHashMap(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link IntArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  public static IntArrayList newIntArrayList(int elements) {
    return (elements == -1) ? new IntArrayList() : new IntArrayList(elements);
  }

  /**
   * Creates a new {@link IntArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  public static IntArrayList newIntArrayList(Integer elements) {
    return newIntArrayList(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link LinkedHashMap} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  // Java 19: Deprecate in favor of LinkedHashMap.newLinkedHashMap, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int elements) {
    return (elements == -1) ? new LinkedHashMap<>() : new LinkedHashMap<>(getHashInitialCapacity(elements));
  }

  /**
   * Creates a new {@link LinkedHashMap} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  // Java 19: Deprecate in favor of LinkedHashMap.newLinkedHashMap, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Integer elements) {
    return newLinkedHashMap(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link LinkedHashSet} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  // Java 19: Deprecate in favor of LinkedHashSet.newLinkedHashSet, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <E> LinkedHashSet<E> newLinkedHashSet(int elements) {
    return (elements == -1) ? new LinkedHashSet<>() : new LinkedHashSet<>(getHashInitialCapacity(elements));
  }

  /**
   * Creates a new {@link LinkedHashSet} with a capacity large enough to contain the given number of elements without rehashing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  // Java 19: Deprecate in favor of LinkedHashSet.newLinkedHashSet, see https://bugs.openjdk.org/browse/JDK-8186958
  public static <E> LinkedHashSet<E> newLinkedHashSet(Integer elements) {
    return newLinkedHashSet(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link LongArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  public static LongArrayList newLongArrayList(int elements) {
    return (elements == -1) ? new LongArrayList() : new LongArrayList(elements);
  }

  /**
   * Creates a new {@link LongArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  public static LongArrayList newLongArrayList(Integer elements) {
    return newLongArrayList(elements == null ? -1 : elements);
  }

  /**
   * Creates a new {@link SortedArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code -1} when unknown
   */
  public static <E> SortedArrayList<E> newSortedArrayList(int elements) {
    return (elements == -1) ? new SortedArrayList<>() : new SortedArrayList<>(elements);
  }

  /**
   * Creates a new {@link SortedArrayList} with a capacity large enough to contain the given number of elements without growing.
   *
   * @param  elements  The number of elements or {@code null} when unknown
   */
  public static <E> SortedArrayList<E> newSortedArrayList(Integer elements) {
    return newSortedArrayList(elements == null ? -1 : elements);
  }
}
