/*
 * ==========================================================================
 * Copyright (C) 2019-2022 HCL America, Inc. ( http://www.hcl.com/ )
 *                            All rights reserved.
 * ==========================================================================
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may
 * not use this file except in compliance with the License.  You may obtain a
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.
 *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the  specific language  governing permissions  and limitations
 * under the License.
 * ==========================================================================
 */
package com.hcl.domino.commons.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ListUtil {

  /**
   * Checks if a list contains a value ignoring the case
   * 
   * @param list  list of strings
   * @param value value to search for
   * @return true if list contains value
   */
  public static boolean containsIgnoreCase(final List<String> list, final String value) {
    if (list == null) {
      return false;
    }

    for (final String currStr : list) {
      if (currStr.equalsIgnoreCase(value)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes empty strings from the list and trims each list entry
   * 
   * @param list list
   * @return trimmed list
   */
  public static List<String> fullTrim(final List<String> list) {
    if (list == null) {
      return list;
    }

    boolean isRequired = false;
    for (final String currStr : list) {
      if ("".equals(currStr) || currStr.startsWith(" ") || currStr.endsWith(" ")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        isRequired = true;
        break;
      }
    }

    if (!isRequired) {
      return list;
    } else {
      final List<String> trimmedList = new ArrayList<>(list.size());
      for (String currStr : list) {
        currStr = currStr.trim();
        if (currStr.length() > 0) {
          trimmedList.add(currStr);
        }
      }
      return trimmedList;
    }
  }

  /**
   * Returns the element at list position <code>list</code> or <code>null</code>,
   * if the list is not big enough.
   * 
   * @param <T>   list element type
   * @param list  list
   * @param index element position
   * @return element or <code>null</code>
   */
  public static <T> T getNth(final List<T> list, final int index) {
    if (list == null) {
      return null;
    } else if (list.size() > index) {
      final T element = list.get(index);
      return element;
    } else {
      return null;
    }
  }

  /**
   * Method to return a sublist from a list based on the start index
   * <code>start</code> and
   * the number of entries to return <code>count</code>. In constrast to
   * {@link List#subList(int, int)},
   * this implementation is forgiving in case <code>start</code> or
   * <code>start+count</code> is higher than
   * the actual number of list entries.
   * 
   * @param <T>   list type
   * @param list  list
   * @param start start index
   * @param count number of entries to return
   * @return sublist, backed by the original list; see
   *         {@link List#subList(int, int)} for details
   */
  public static <T> List<T> subListChecked(final List<T> list, final int start, final int count) {
    if (start > list.size()) {
      return Collections.emptyList();
    } else {
      final long startLong = start;
      final long countLong = count;
      // make sure we do not exceed Integer.MAX_VALUE
      final long sum = Math.min(Integer.MAX_VALUE, startLong + countLong);

      return list.subList(start, Math.min(list.size(), (int) sum));
    }
  }

  /**
   * Converts a list with strings to lowercase format
   * 
   * @param values list with strings
   * @param loc    locale to use for conversion
   * @return list in lowercase
   */
  public static List<String> toLowerCase(final List<String> values, final Locale loc) {
    if (values == null) {
      return null;
    }
    final List<String> listInLowercase = new ArrayList<>(values.size());
    for (final String currValue : values) {
      listInLowercase.add(currValue.toLowerCase(loc));
    }
    return listInLowercase;
  }

  /**
   * Removes empty strings from the list
   * 
   * @param list list
   * @return trimmed list
   */
  public static List<String> trim(final List<String> list) {
    if (list == null) {
      return list;
    }

    boolean isRequired = false;
    for (final String currStr : list) {
      if ("".equals(currStr)) { //$NON-NLS-1$
        isRequired = true;
        break;
      }
    }

    if (!isRequired) {
      return list;
    } else {
      final List<String> trimmedList = new ArrayList<>(list.size() - 1);
      for (final String currStr : list) {
        if (currStr.length() > 0) {
          trimmedList.add(currStr);
        }
      }
      return trimmedList;
    }
  }
}
