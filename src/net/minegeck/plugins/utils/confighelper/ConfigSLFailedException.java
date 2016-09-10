package net.minegeck.plugins.utils.confighelper;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ConfigSLFailedException extends RuntimeException {
  private static final long serialVersionUID = 4350406856615824061L;
  public ConfigSLFailedException(String message) {
    super(message);
  }
  public ConfigSLFailedException() {
    super();
  }
  public ConfigSLFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}