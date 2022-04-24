/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2010, 2011, 2014, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.EmptyArrays;
import com.aoapps.lang.io.FastObjectInput;
import com.aoapps.lang.io.FastObjectOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * An unmodifiable compact <code>Set</code> implementation that stores the elements in hashCode order.
 * The emphasis is to use as little heap space as possible - this is not a general-purpose
 * <code>Set</code> implementation.
 * </p>
 * <p>
 * This set does not support null values.
 * </p>
 * <p>
 * This set will generally operate at O(log n) due to binary search.  In general, it will
 * not be as fast as the O(1) behavior of HashSet.  Here we give up speed to save space.
 * </p>
 * <p>
 * This set is not thread safe.
 * </p>
 *
 * @see  HashCodeComparator to properly sort objects before adding to the set
 *
 * @author  AO Industries, Inc.
 */
public class UnmodifiableArraySet<E> extends AbstractSet<E> implements Externalizable {

  /**
   * May more forcefully disable asserts for benchmarking.
   */
  private static final boolean ASSERTIONS_ENABLED = true;

  /**
   * The number of elements at which a linear search switches to a binary search.
   */
  private static final int BINARY_SEARCH_THRESHOLD = 22; // The point where the O(n) line and O(log n) curves intersect.

  private E[] elements;

  private static boolean inOrderAndUnique(Object[] elements) {
    // Make sure all elements are in hashCode order and unique
    int size = elements.length;
    if (size > 1) {
      Object prev = elements[0];
      int prevHash = prev.hashCode();
      for (int index = 1; index < size; index++) {
        Object elem = elements[index];
        int elemHash = elem.hashCode();
        if (elemHash < prevHash) {
          return false; //throw new AssertionError("elements not sorted by hashCode: "+elemHash+"<"+prevHash+": "+elem+"<"+prev);
        }
        if (elemHash == prevHash) {
          // Make sure not equal to prev
          if (elem.equals(prev)) {
            return false; //throw new AssertionError("Element not unique: "+elem);
          }
          // Look backward until different hashCode
          for (int i = index - 2; i >= 0; i--) {
            Object morePrev = elements[i];
            if (morePrev.hashCode() != elemHash) {
              break;
            }
            if (elem.equals(morePrev)) {
              return false; // throw new AssertionError("Element not unique: "+elem);
            }
          }
        }
        prev = elem;
        prevHash = elemHash;
      }
    }
    return true;
  }

  /**
   * Uses the provided elements, which must already be sorted in hashCode order and unique.
   *
   * The sort order and uniqueness is only checked with assertions enabled.
   *
   * @see  HashCodeComparator to properly sort objects before adding to the set
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public UnmodifiableArraySet(E... elements) {
    if (ASSERTIONS_ENABLED) {
      assert inOrderAndUnique(elements);
    }
    this.elements = elements;
  }

  /**
   * Uses the provided elements collection, which must already be sorted in hashCode order and unique.
   * A defensive copy is made.
   *
   * The sort order and uniqueness is only checked with assertions enabled.
   *
   * @see  HashCodeComparator to properly sort objects before adding to the set
   */
  @SuppressWarnings("unchecked")
  public UnmodifiableArraySet(Collection<E> elements) {
    this((E[]) elements.toArray());
  }

  private static int binarySearch(Object[] elements, int oHash) {
    return binarySearch0(elements, 0, elements.length, oHash);
  }

  private static int binarySearch0(Object[] elements, int fromIndex, int toIndex, int oHash) {
    int low = fromIndex;
    int high = toIndex - 1;
    while (low <= high) {
      int mid = (low + high) >>> 1;
      int midHash = elements[mid].hashCode();
      if (midHash < oHash) {
        low = mid + 1;
      } else if (midHash > oHash) {
        high = mid - 1;
      } else {
        return mid;
      }
    }
    return -(low + 1);
  }

  @Override
  public int size() {
    return elements.length;
  }

  @Override
  public boolean isEmpty() {
    return elements.length == 0;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean contains(Object o) {
    E[] elems = this.elements; // Local fast reference
    int size = elems.length;
    if (size == 0 || o == null) {
      return false;
    }
    if (size < BINARY_SEARCH_THRESHOLD) {
      // Simple search
      for (int i = 0; i < size; i++) {
        if (elems[i].equals(o)) {
          return true;
        }
      }
    } else {
      int index = binarySearch(elems, o.hashCode());
      if (index < 0) {
        return false;
      }
      // Matches at index?
      E elem = elems[index];
      if (elem.equals(o)) {
        return true;
      }
      // Look forward until different hashCode
      int oHash = o.hashCode();
      for (int i = index + 1; i < size; i++) {
        elem = elems[i];
        if (elem.hashCode() != oHash) {
          break;
        }
        if (elem.equals(o)) {
          return true;
        }
      }
      // Look backward until different hashCode
      for (int i = index - 1; i >= 0; i--) {
        elem = elems[i];
        if (elem.hashCode() != oHash) {
          break;
        }
        if (elem.equals(o)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Iterator<E> iterator() {
    // Java 9: new Iterator<>
    return new Iterator<E>() {
      private int index = 0;
      final E[] elems = UnmodifiableArraySet.this.elements; // Local fast reference

      @Override
      public boolean hasNext() {
        return index < elems.length;
      }

      @Override
      public E next() throws NoSuchElementException {
        if (index >= elems.length) {
          throw new NoSuchElementException();
        }
        return elems[index++];
      }
    };
  }

  @Override
  public Object[] toArray() {
    return Arrays.copyOf(elements, elements.length);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    int size = elements.length;
    if (a.length < size) {
      return (T[]) Arrays.copyOf(elements, size, a.getClass());
    }
    System.arraycopy(elements, 0, a, 0, size);
    if (a.length > size) {
      a[size] = null;
    }
    return a;
  }

  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("element-type-mismatch")
  public boolean containsAll(Collection<?> c) {
    // Could do a nifty single-pass merge if the other collection is also an UnmodifiableArraySet and contains a large number of elements
    for (Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  // <editor-fold defaultstate="collapsed" desc="Externalizable">
  private static final long serialVersionUID = 5725680713634634667L;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  Externalizable
   */
  @Deprecated // Java 9: (forRemoval = false)
  public UnmodifiableArraySet() {
    // Do nothing
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    FastObjectOutput fastOut = FastObjectOutput.wrap(out);
    try {
      int len = elements.length;
      fastOut.writeInt(len);
      if (len > 0) {
        E[] elems = UnmodifiableArraySet.this.elements; // Local fast reference
        for (int i = 0; i < len; i++) {
          fastOut.writeObject(elems[i]);
        }
      }
    } finally {
      fastOut.unwrap();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    if (elements != null) {
      throw new IllegalStateException();
    }
    FastObjectInput fastIn = FastObjectInput.wrap(in);
    try {
      final int len = fastIn.readInt();
      if (len == 0) {
        elements = (E[]) EmptyArrays.EMPTY_OBJECT_ARRAY;
      } else {
        E[] newElements = (E[]) new Object[len];
        for (int i = 0; i < len; i++) {
          newElements[i] = (E) fastIn.readObject();
        }
        if (ASSERTIONS_ENABLED) {
          assert inOrderAndUnique(newElements);
        }
        UnmodifiableArraySet.this.elements = newElements;
      }
    } finally {
      fastIn.unwrap();
    }
  }
  // </editor-fold>
}
