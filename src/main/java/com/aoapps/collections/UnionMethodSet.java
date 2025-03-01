/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2011, 2013, 2014, 2016, 2019, 2020, 2021, 2022, 2024, 2025  AO Industries, Inc.
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

import com.aoapps.lang.exception.WrappedException;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A union set that assumes objects of different classes are not equal.  It obtains
 * the objects by invoking the provided methods.  The provided methods will be invoked
 * on an as-needed basis.  It never does any combining.
 *
 * <p>The following assumptions are made:</p>
 *
 * <ol>
 *   <li>All elements of the returned sets are of the same exact class (within a single method)</li>
 *   <li>Objects of different classes are not equal.</li>
 *   <li>No set will contain <code>null</code></li>
 * </ol>
 *
 * @see  AoCollections#optimalUnmodifiableSet(java.util.Set) This class is unmodifiable.
 *
 * @author  AO Industries, Inc.
 */
public class UnionMethodSet<E> extends AbstractSet<E> {

  public static interface Method<E> {

    /**
     * Checks if the set returned by the method contains the non-null object element.
     */
    boolean contains(Object target, Object element);

    /**
     * Gets the single value that represents the set or <code>null</code> if cannot
     * be represented as a single value.
     */
    E getSingleton(Object target);

    /**
     * Gets the set view of the method result or <code>null</code> if set is
     * not applicable.
     */
    Set<? extends E> getSet(Object target);
  }

  public abstract static class ReflectionMethod<E> implements Method<E> {

    protected final java.lang.reflect.Method method;

    protected ReflectionMethod(Class<?> targetClass, String methodName) throws NoSuchMethodException {
      this(targetClass.getMethod(methodName));
    }

    protected ReflectionMethod(java.lang.reflect.Method method) {
      this.method = method;
    }

    public final java.lang.reflect.Method getMethod() {
      return method;
    }
  }

  /**
   * A single value will be obtained from the call to the method.
   */
  public static class SingletonMethod<E> extends ReflectionMethod<E> {

    public SingletonMethod(Class<?> targetClass, String methodName) throws NoSuchMethodException {
      super(targetClass, methodName);
    }

    public SingletonMethod(java.lang.reflect.Method method) {
      super(method);
    }

    @Override
    public boolean contains(Object target, Object element) {
      return element.equals(getSingleton(target));
    }

    @Override
    @SuppressWarnings({"unchecked", "UseSpecificCatch", "TooBroadCatch"})
    public E getSingleton(Object target) {
      try {
        try {
          return (E) method.invoke(target);
        } catch (InvocationTargetException e) {
          // Unwrap cause for more direct stack traces
          Throwable cause = e.getCause();
          throw (cause == null) ? e : cause;
        }
      } catch (ThreadDeath td) {
        throw td;
      } catch (Throwable t) {
        throw new WrappedException(target + "." + method + "()", t);
      }
    }

    @Override
    public Set<? extends E> getSet(Object target) {
      return null;
    }
  }

  /**
   * A set of values will be obtained from the call to the method.
   */
  public static class SetMethod<E> extends ReflectionMethod<E> {

    public SetMethod(Class<?> targetClass, String methodName) throws NoSuchMethodException {
      super(targetClass, methodName);
    }

    public SetMethod(java.lang.reflect.Method method) {
      super(method);
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean contains(Object target, Object element) {
      Set<? extends E> set = getSet(target);
      return set != null && set.contains(element);
    }

    @Override
    public E getSingleton(Object target) {
      return null;
    }

    @Override
    @SuppressWarnings({"unchecked", "UseSpecificCatch", "TooBroadCatch"})
    public Set<? extends E> getSet(Object target) {
      try {
        try {
          return (Set<E>) method.invoke(target);
        } catch (InvocationTargetException e) {
          // Unwrap cause for more direct stack traces
          Throwable cause = e.getCause();
          throw (cause == null) ? e : cause;
        }
      } catch (ThreadDeath td) {
        throw td;
      } catch (Throwable t) {
        throw new WrappedException(target + "." + method + "()", t);
      }
    }
  }

  private final Object target;
  private final Class<E> classE;
  private final Map<Class<? extends E>, ? extends List<? extends Method<? extends E>>> methodsByClass;

  public UnionMethodSet(Object target, Class<E> classE, Map<Class<? extends E>, ? extends List<? extends Method<? extends E>>> methodsByClass) {
    this.target = target;
    this.classE = classE;
    this.methodsByClass = methodsByClass;
    /*
    Map<Class<? extends E>, ArrayList<Method<? extends E>>> newAdded = new LinkedHashMap<>();
    // Build a temporary map by return type
    for (Method<? extends E> method : methods) {
      Class<? extends E> returnType = method.getReturnType();
      ArrayList<Method<? extends E>> list = newAdded.get(returnType);
      if (list == null) {
        newAdded.put(returnType, list = new ArrayList<>());
      }
      list.add(method);
    }
    // Build final map with trimmed array lists or singletonList
    added = AoCollections.<Class<? extends E>, List<Method<? extends E>>>newLinkedHashMap(newAdded.size());
    for (Map.Entry<Class<? extends E>, ArrayList<Method<? extends E>>> entry : newAdded.entrySet()) {
      Class<? extends E> returnType = entry.getKey();
      ArrayList<Method<? extends E>> list = entry.getValue();
      if (list.size() == 1) {
        // TODO: Why doesn't this work?  singletonList too restrictive?  added.put(returnType, Collections.singletonList(list.get(0)));
        list.trimToSize();
        added.put(returnType, list);
      } else {
        list.trimToSize();
        added.put(returnType, list);
      }
    }*/
  }

  /**
   * This is an expensive operation, since every single method is called, and
   * when there are multiple methods for one class, the sets must be merged to
   * avoid redundant values.
   */
  @Override
  public int size() {
    throw new RuntimeException("TODO: Implement method with similar results as iterator()");
  }

  /**
   * Checks if this set is empty.  This can be an expensive method since it can potentially call all methods.
   */
  @Override
  public boolean isEmpty() {
    for (List<? extends Method<? extends E>> methods : methodsByClass.values()) {
      for (Method<? extends E> method : methods) {
        E singleton = method.getSingleton(target);
        if (singleton != null) {
          return false;
        }
        Set<? extends E> set = method.getSet(target);
        if (set != null && !set.isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public boolean contains(Object element) {
    if (element == null) {
      return false;
    }
    Class<?> clazz = element.getClass();
    do {
      @SuppressWarnings("element-type-mismatch")
      List<? extends Method<? extends E>> methods = methodsByClass.get(clazz);
      if (methods != null) {
        for (int i = 0, size = methods.size(); i < size; i++) {
          if (methods.get(i).contains(target, element)) {
            return true;
          }
        }
      }
      clazz = clazz.getSuperclass();
    } while (clazz != classE && clazz != null);
    return false;
  }

  @Override
  public Iterator<E> iterator() {
    // Iterating through all lists of methods per class
    final Iterator<? extends List<? extends Method<? extends E>>> classIter = methodsByClass.values().iterator();
    return new Iterator<>() {
      // Iterating through methods
      private List<? extends Method<? extends E>> methods;
      private int methodsSize = 0;
      private int methodIndex = Integer.MAX_VALUE;
      // The sets previously returned for the current class, used to avoid returning duplicate objects
      private Set<? extends E> currentSet;
      private List<Set<? extends E>> previousSets;
      private E currentSingleton;
      private List<E> previousSingletons;
      // Iterating result of one method call
      private Iterator<? extends E> valIter;
      private E nextElement;

      /**
       * Prepares the next value for consumption.
       * The next value is consumed by setting nextValue to null.
       */
      private void prepareNextValue() {
        while (nextElement == null && classIter.hasNext()) {
          if (valIter != null) {
            nextElement = valIter.next();
            if (!valIter.hasNext()) {
              valIter = null;
            }
          } else {
            // Add currentValue / currentSet to previous
            if (currentSingleton != null) {
              if (previousSingletons == null) {
                previousSingletons = new ArrayList<>();
              }
              previousSingletons.add(currentSingleton);
              currentSingleton = null;
            }
            if (currentSet != null) {
              if (previousSets == null) {
                previousSets = new ArrayList<>();
              }
              previousSets.add(currentSet);
              currentSet = null;
            }

            // Switch to next non-empty method list as needed
            while (methodIndex >= methodsSize) {
              if (!classIter.hasNext()) {
                return;
              }
              methods = classIter.next();
              methodsSize = methods.size();
              methodIndex = 0;
              if (previousSets != null) {
                previousSets.clear();
              }
              if (previousSingletons != null) {
                previousSingletons.clear();
              }
            }

            // Get either singleton or iterator for method result
            Method<? extends E> method = methods.get(methodIndex++);
            nextElement = method.getSingleton(target);
            if (nextElement != null) {
              currentSingleton = nextElement;
              valIter = null;
            } else {
              Set<? extends E> set = method.getSet(target);
              if (set != null) {
                valIter = set.iterator();
                if (valIter.hasNext()) {
                  currentSet = set;
                  nextElement = valIter.next();
                  if (!valIter.hasNext()) {
                    valIter = null;
                  }
                } else {
                  nextElement = null;
                  valIter = null;
                }
              } else {
                valIter = null;
              }
            }
          }
          // Check if duplicate values
          if (nextElement != null) {
            if (previousSingletons != null && previousSingletons.contains(nextElement)) {
              nextElement = null;
            } else if (previousSets != null) {
              for (int i = 0, size = previousSets.size(); i < size; i++) {
                if (previousSets.get(i).contains(nextElement)) {
                  nextElement = null;
                  break;
                }
              }
            }
          }
        }
      }

      @Override
      public boolean hasNext() {
        prepareNextValue();
        return nextElement != null;
      }

      @Override
      public E next() throws NoSuchElementException {
        prepareNextValue();
        E element = nextElement;
        if (element == null) {
          throw new NoSuchElementException();
        }
        nextElement = null; // Consume value
        return element;
      }
    };
  }
}
