package net.minegeck.plugins.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class TypeUtils {
  public static Class<?> getClass(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) type).getRawType();
    } else {
      return null;
    }
  }
  public static Type getActualParameterizedType(Type type, int index) {
    if (type instanceof Class) {
      return null;
    } else if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments()[index];
    } else {
      return null;
    }
  }
}
