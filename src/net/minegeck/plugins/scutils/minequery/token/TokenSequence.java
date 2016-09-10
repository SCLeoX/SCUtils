package net.minegeck.plugins.scutils.minequery.token;

import net.minegeck.plugins.scutils.minequery.MineQuerySyntaxExceptionBuilder;
import net.minegeck.plugins.scutils.minequery.TraceInfo;
import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class TokenSequence {
  private final ArrayList<Token> list;

  public ArrayList<Token> getList() {
    return list;
  }
  private final String source;
  public TokenSequence(String source) {
    list = new ArrayList<>();
    this.source = source;
  }
  public TokenSequence(String source, List<Token> tokens) {
    list = new ArrayList<>(tokens);
    this.source = source;
  }
  private int pos = 0;
  public Token next() {
    if (eof()) {
      return null;
    }
    return list.get(pos++);
  }
  public Token next(TokenType predictType) {
    Token token = next();
    if (token == null) {
      lastException(MessageFormat.format("期待类型为 \"{0}\" 的 Token，找到 <EOF>。", predictType.getName()));
    }
    if (!token.is(predictType)) {
      lastException(MessageFormat.format("期待类型为 \"{0}\" 的 Token，找到类型为 \"{1}\" 的 \"{2}\"。", predictType.getName(), token.getType().getName(), token.getContent()));
    }
    return token;
  }
  public Token next(TokenType predictType, String predictContent) {
    Token token = next(predictType);
    if (!token.is(predictContent)) {
      lastException(MessageFormat.format("期待内容为 \"{0}\" 的 Token，找到类型为 \"{1}\" 的 \"{2}\"。", predictContent, token.getType().getName(), token.getContent()));
    }
    return token;
  }
  public Token peek() {
    if (eof()) {
      return null;
    }
    return list.get(pos);
  }
  public boolean isNext(TokenType type) {
    Token token = peek();
    return !(token == null) && token.is(type);
  }
  public boolean isNext(TokenType type, String content) {
    Token token = peek();
    return !(token == null) && token.is(type) && token.is(content);
  }
  public boolean isNext(String content) {
    Token token = peek();
    return !(token == null) && token.is(content);
  }
  public boolean eof() {
    return list.size() <= pos;
  }
  public void lastException(String msg) {
    TraceInfo trace;
    if (pos == 0) {
      trace = new TraceInfo(this.source, 0);
    } else {
      trace = list.get(pos - 1).getTrace();
    }
    throw MineQuerySyntaxExceptionBuilder
      .trace(trace)
      .causedBy(msg)
      .done();
  }
  public void exception(String msg) {
    throw MineQuerySyntaxExceptionBuilder
      .trace(peek().getTrace())
      .causedBy(msg)
      .done();
  }
  @Override
  public String toString() {
    return list.toString();
  }
}
