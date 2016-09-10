package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.minequery.PlayerCollection;
import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public abstract class ASTSelector extends ASTNode {
  public abstract PlayerCollection select(PlayerCollection previous);

  public PlayerCollection select() {
    return select(null);
  }

}
