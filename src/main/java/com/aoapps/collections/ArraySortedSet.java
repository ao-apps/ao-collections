/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2010, 2011, 2013, 2016, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;

/**
 * A compact <code>SortedSet</code> implementation that stores the elements in order.
 * The emphasis is to use as little heap space as possible - this is not a general-purpose
 * <code>SortedSet</code> implementation as it has specific constraints about the order elements
 * may be added or removed.  To avoid the possibility of O(n^2) behavior, the elements must
 * already be sorted and be added in ascending order.  Also, only the last element may be
 * removed.
 *
 * <p>This set does not support null values.</p>
 *
 * <p>Creation of a set for an already sorted set is O(n) compared to TreeSet's O(n log n).
 * Other operations perform at O(log n) with times very similar to TreeSet.</p>
 *
 * <p>This set is not thread safe.</p>
 *
 * @author  AO Industries, Inc.
 */
public class ArraySortedSet<E> extends AbstractSet<E> implements SortedSet<E>, Serializable {

  private static final long serialVersionUID = -8200779660844889853L;

  private final Comparator<? super E> comparator;
  private final ArrayList<E> elements;

  public ArraySortedSet() {
    this.comparator = null;
    this.elements = new ArrayList<>();
  }

  public ArraySortedSet(int initialCapacity) {
    this.comparator = null;
    this.elements = new ArrayList<>(initialCapacity);
  }

  public ArraySortedSet(Comparator<? super E> comparator) {
    this.comparator = comparator;
    this.elements = new ArrayList<>();
  }

  public ArraySortedSet(Comparator<? super E> comparator, int initialCapacity) {
    this.comparator = comparator;
    this.elements = new ArrayList<>(initialCapacity);
  }

  @SuppressWarnings("OverridableMethodCallInConstructor")
  public ArraySortedSet(Collection<? extends E> c) {
    this.comparator = null;
    this.elements = new ArrayList<>(c.size());
    addAll(c);
  }

  public ArraySortedSet(SortedSet<E> s) {
    this.comparator = s.comparator();
    this.elements = new ArrayList<>(s);
  }

  @SuppressWarnings("unchecked")
  private int binarySearch(E elem) {
    return
        comparator == null
            ? java.util.Collections.binarySearch((List) elements, elem)
            : java.util.Collections.binarySearch(elements, elem, comparator);
  }

  @SuppressWarnings("unchecked")
  private int compare(E elem1, E elem2) {
    return
        comparator == null
            ? ((Comparable) elem1).compareTo(elem2)
            : comparator.compare(elem1, elem2);
  }

  public void trimToSize() {
    elements.trimToSize();
  }

  @Override
  public Comparator<? super E> comparator() {
    return comparator;
  }

  @Override
  public SortedSet<E> subSet(E fromElement, E toElement) {
    throw new UnsupportedOperationException("TODO: Not supported yet.");
  }

  @Override
  public SortedSet<E> headSet(E toElement) {
    throw new UnsupportedOperationException("TODO: Not supported yet.");
  }

  @Override
  public SortedSet<E> tailSet(E fromElement) {
    throw new UnsupportedOperationException("TODO: Not supported yet.");
  }

  @Override
  public E first() {
    if (elements.isEmpty()) {
      throw new NoSuchElementException();
    }
    return elements.get(0);
  }

  @Override
  public E last() {
    int size = elements.size();
    if (size == 0) {
      throw new NoSuchElementException();
    }
    return elements.get(size - 1);
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean contains(Object o) {
    if (o == null) {
      return false;
    }
    // TODO: How can we check if the passed-in object is of an unrelated, unexpected class
    // TODO: without passing around Class objects?
    // TODO: with equals - just like ArraySet.
    return binarySearch((E) o) >= 0;
  }

  @Override
  public Iterator<E> iterator() {
    return elements.iterator();
  }

  @Override
  public Object[] toArray() {
    return elements.toArray();
  }

  @Override
  @SuppressWarnings("SuspiciousToArrayCall")
  public <T> T[] toArray(T[] a) {
    return elements.toArray(a);
  }

  @Override
  public boolean add(E e) {
    int size = elements.size();
    if (size == 0) {
      elements.add(e);
      return true;
    } else {
      // Shortcut for adding last element
      E last = elements.get(size - 1);
      int diff = compare(e, last);
      if (diff > 0) {
        elements.add(e);
        return true;
      } else if (diff == 0) {
        // Already in set
        return false;
      } else {
        int index = binarySearch(e);
        if (index >= 0) {
          // Already in set
          return false;
        } else {
          throw new UnsupportedOperationException("May only add the last element.");
        }
      }
    }
  }

  @Override
  @SuppressWarnings({"unchecked", "element-type-mismatch"})
  public boolean remove(Object o) {
    int size = elements.size();
    if (size == 0) {
      return false;
    }
    // Shortcut for removing last element
    E lastElem = elements.get(size - 1);
    // TODO: How can we check if the passed-in object is of an unrelated, unexpected class
    // TODO: without passing around Class objects?
    if (compare(lastElem, (E) o) == 0) {
      elements.remove(size - 1);
      return true;
    } else {
      if (contains(o)) {
        throw new UnsupportedOperationException("May only remove the last element.");
      }
      return false;
    }
  }

  @Override
  @SuppressWarnings("element-type-mismatch")
  public boolean containsAll(Collection<?> c) {
    for (Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    boolean modified = false;
    for (E elem : c) {
      if (add(elem)) {
        modified = true;
      }
    }
    return modified;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("element-type-mismatch")
  public boolean removeAll(Collection<?> c) {
    boolean modified = false;
    for (Object o : c) {
      if (remove(o)) {
        modified = true;
      }
    }
    return modified;
  }

  @Override
  public void clear() {
    elements.clear();
  }
}
