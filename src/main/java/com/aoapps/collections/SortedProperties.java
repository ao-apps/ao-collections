/*
 * ao-collections - Collections and related utilities for Java.
 * Copyright (C) 2012, 2013, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A Properties implementation that returns and writes its keys in alphabetical ({@code Locale.ROOT}) order.
 *
 * @deprecated  Please use {@link org.apache.commons.collections4.properties.SortedProperties} from
 *              <a href="https://commons.apache.org/proper/commons-collections/">Apache Commons Collections</a>.
 *              <p>
 *              As of Java 18, properties will be sorted by default, per
 *              <a href="https://bugs.openjdk.org/browse/JDK-8231640">[JDK-8231640] (prop) Canonical property storage - Java Bug System</a>.
 *              </p>
 *
 * @author  AO Industries, Inc.
 */
@Deprecated
@SuppressWarnings("CloneableImplementsClone")
// Java 18: Remove this class entirely
public class SortedProperties extends Properties {

  private static final long serialVersionUID = 1L;

  public SortedProperties() {
    super();
  }

  public SortedProperties(Properties defaults) {
    super(defaults);
  }

  /**
   * Gets the comparator used to sort the keys.
   * <p>
   * Defaults to {@link Collator#getInstance(java.util.Locale)}
   * in {@link Locale#ROOT}.
   * </p>
   */
  public Comparator<Object> getKeyComparator() {
    return Collator.getInstance(Locale.ROOT);
  }

  // Java <= 1.8: Properties.save uses keys()
  @Override
  public Enumeration<Object> keys() {
    SortedSet<Object> sortedSet = new TreeSet<>(getKeyComparator());
    Enumeration<Object> e = super.keys();
    while (e.hasMoreElements()) {
      sortedSet.add(e.nextElement());
    }
    return Collections.enumeration(sortedSet);
  }

  // Java >= 9: Properties.save uses entrySet()
  @Override
  public Set<Map.Entry<Object, Object>> entrySet() {
    Comparator<Object> keyComparator = getKeyComparator();
    SortedSet<Map.Entry<Object, Object>> sortedSet = new TreeSet<>(
        (Map.Entry<Object, Object> e1, Map.Entry<Object, Object> e2) -> keyComparator.compare(e1.getKey(), e2.getKey())
    );
    sortedSet.addAll(super.entrySet());
    return sortedSet;
  }
}
