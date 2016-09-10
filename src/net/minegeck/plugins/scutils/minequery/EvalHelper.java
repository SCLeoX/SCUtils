package net.minegeck.plugins.scutils.minequery;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.scutils.minequery.ast.ASTExpression;
import net.minegeck.plugins.scutils.minequery.ast.AnyType;
import net.minegeck.plugins.scutils.minequery.ast.MineQueryRuntimeException;
import net.minegeck.plugins.scutils.minequery.ast.NumberCastable;
import net.minegeck.plugins.utils.Annotations;
import net.minegeck.plugins.utils.commandparser.ParseResult;

import java.text.MessageFormat;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class EvalHelper {

  public static double evalNextDouble(ParseResult result, PlayerInterlayer player) {
    AnyType a = ((ASTExpression) result.nextASTNode()).eval(player.getIdentifierProvider());
    if (!(a instanceof NumberCastable)) {
      throw new MineQueryRuntimeException(MessageFormat.format("无法将计算结果 {0} 转换为数值。", a.toString()));
    }
    return ((NumberCastable) a).castToNumber().get();
  }
}
