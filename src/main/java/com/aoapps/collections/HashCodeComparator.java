/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2010, 2011, 2016, 2017, 2020, 2021, 2022  AO Industries, Inc.
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
import java.util.Comparator;

/**
 * Orders objects by hashCode.
 *
 * @author  AO Industries, Inc.
 */
public final class HashCodeComparator implements Comparator<Object>, Serializable {

  private static final long serialVersionUID = 5468576960399075645L;

  private static final HashCodeComparator singleton = new HashCodeComparator();

  public static HashCodeComparator getInstance() {
    return singleton;
  }

  private HashCodeComparator() {
    // Do nothing
  }

  private Object readResolve() {
    return singleton;
  }

  @Override
  public int compare(Object o1, Object o2) {
    int hash1 = o1.hashCode();
    int hash2 = o2.hashCode();
    if (hash1 < hash2) {
      return -1;
    }
    if (hash1 > hash2) {
      return 1;
    }
    return 0;
  }
}
