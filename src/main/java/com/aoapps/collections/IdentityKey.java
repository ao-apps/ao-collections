/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2012, 2016, 2017, 2020, 2021, 2022  AO Industries, Inc.
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

import java.util.IdentityHashMap;
import java.util.WeakHashMap;

/**
 * Allows any object to be used as a hash key, with identity used for {@link #hashCode()} and
 * {@link #equals(java.lang.Object)}.  They may be used, for example, to have {@link IdentityHashMap}
 * semantics with {@link WeakHashMap} references.
 * <p>
 * Supports {@code null} value, which may allow {@code null} keys in maps that otherwise do not support {@code null}
 * keys.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class IdentityKey<T> {

  public static final IdentityKey<?> NULL = new IdentityKey<>(null);

  /**
   * Gets the identity key that represents {@code null}.
   */
  @SuppressWarnings("unchecked")
  public static <T> IdentityKey<T> ofNull() {
    return (IdentityKey<T>)NULL;
  }

  /**
   * Gets the identity key for the given value or {@link #NULL} for a {@code null} value.
   */
  public static <T> IdentityKey<T> of(T value) {
    if (value == null) {
      return ofNull();
    } else {
      return new IdentityKey<>(value);
    }
  }

  private final T value;

  /**
   * @deprecated  Please use {@link #of(java.lang.Object)}, which may return {@link #NULL} for {@code null} values.
   */
  @Deprecated
  public IdentityKey(T value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return (value == null) ? "null" : value.toString();
  }

  /**
   * @see System#identityHashCode(java.lang.Object)
   */
  @Override
  public int hashCode() {
    return System.identityHashCode(value);
  }

  @Override
  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  public boolean equals(Object obj) {
    return this == obj;
  }

  public T getValue() {
    return value;
  }
}
