package net.minegeck.plugins.utils;

import java.util.Collection;
import java.util.Iterator;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class StringJoiner {
  public static String join(Collection<?> col, String delim) {
    StringBuilder sb = new StringBuilder();
    Iterator<?> iter = col.iterator();
    if (iter.hasNext()) {
      sb.append(iter.next().toString());
    }
    while (iter.hasNext()) {
      sb.append(delim);
      sb.append(iter.next().toString());
    }
    return sb.toString();
  }
}
