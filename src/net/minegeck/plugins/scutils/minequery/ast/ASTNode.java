package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public abstract class ASTNode {
  public abstract ASTNodeType getType();
  public boolean is(ASTNodeType target) {
    return this.getType().equals(target);
  }
  public void getString(StringBuilder sb, int indent, int indentEachLevel) {
    sb.append(new String(new char[indent * indentEachLevel]).replace("\0", " ")).append(this.getType().getName()).append('\n').toString();
  }
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    getString(sb, 0, 2);
    return sb.toString();
  }
}
