package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class MineQueryRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MineQueryRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public MineQueryRuntimeException(String message) {
    super(message);
  }

  public MineQueryRuntimeException() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
