package net.minegeck.plugins.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class Annotations {
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Info {
    public String 注意() default "";
    public String 作者() default "";
    public String 许可() default "";
  }
}
