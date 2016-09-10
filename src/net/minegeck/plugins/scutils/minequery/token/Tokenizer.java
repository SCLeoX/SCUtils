package net.minegeck.plugins.scutils.minequery.token;

import net.minegeck.plugins.scutils.minequery.CharReader;
import net.minegeck.plugins.scutils.minequery.TraceInfo;
import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class Tokenizer {
  private final CharReader input;
  public Tokenizer(CharReader input) {
    this.input = input;
  }
  public Tokenizer(String input) {
    this.input = new CharReader(input);
  }
  public static boolean isWhiteSpace(char ch) {
    return " \t".indexOf(ch) >= 0;
  }
  public void skipAllWhiteSpace() {
    while (!input.eof() && isWhiteSpace(input.peek())) {
      input.next();
    }
  }
  public static Pattern chinesePattern = Pattern.compile("[\\u4E00-\\u9FBF]");
  public static boolean isChinese(char ch) {
    return chinesePattern.matcher(String.valueOf(ch)).find();
  }
  public static Pattern letterPattern = Pattern.compile("[a-zA-Z]");
  public static boolean isLetter(char ch) {
    return letterPattern.matcher(String.valueOf(ch)).find();
  }
  public static boolean isNumber(char ch) {
    return "1234567890".indexOf(ch) >= 0;
  }
  public static boolean isPunctuation(char ch) {
    return "()[],".indexOf(ch) >= 0;
  }
  public static boolean isStartOfIDSelector(char ch) {
    return ch == '#';
  }
  public static boolean isStartOfClassSelector(char ch) {
    return ch == '.';
  }
  public static boolean isContentOfSelector(char ch) {
    return isLetter(ch) || isNumber(ch) || "_".indexOf(ch) >= 0 || isChinese(ch);
  }
  public static boolean isQuerySign(char ch) {
    return ch == '$';
  }
  public static boolean isContentOfQuerySelector(char ch) {
    return isLetter(ch) || isNumber(ch) || "_".indexOf(ch) >= 0 || isChinese(ch);
  }
  public static boolean isStartOfIndentifier(char ch) {
    return isLetter(ch) || isChinese(ch);
  }
  public static boolean isContentOfIndentifier(char ch) {
    return isLetter(ch) || isNumber(ch) || "_:".indexOf(ch) >= 0 || isChinese(ch);
  }
  public static boolean isStartOfOperator(char ch) {
    return ">=<~!&|+-*/%".indexOf(ch) >= 0;
  }
  public static boolean isContentOfOperator(char ch) {
    return ">=<~!&|+*/%".indexOf(ch) >= 0;
  }
  public static boolean isStartOfLimiter(char ch) {
    return ch == ':';
  }
  public static boolean isContentOfLimiter(char ch) {
    return isLetter(ch);
  }
  public static boolean isDot(char ch) {
    return '.' == ch;
  }
  public static boolean isStartOfString(char ch) {
    return "'\"".indexOf(ch) >= 0;
  }
  public static boolean isEscape(char ch) {
    return '\\' == ch;
  }
  public static char escape(char ch) {
    switch(ch) {
      case '\\': return '\\';
      case 'n': return '\n';
      case 't': return '\t';
      default: return ch;
    }
  }
  public Token readNext() {
    char ch = input.next();
    TraceInfo trace = new TraceInfo(input.getSource(), input.getPos());

    //标点符号
    if (isPunctuation(ch)) {
      return new Token(TokenType.PUNCTUATION, String.valueOf(ch), trace);
    }

    //ID选择器
    if (isStartOfIDSelector(ch)) {
      StringBuilder sb = new StringBuilder();
      sb.append(ch);
      while (isContentOfSelector(input.peek())) {
        sb.append(input.next());
      }
      if (sb.length() == 1) {
        input.exception("ID 选择器内容不能为空。");
      }
      return new Token(TokenType.ID_SELECTOR, sb.toString(), trace);
    }

    //类选择器
    if (isStartOfClassSelector(ch)) {
      StringBuilder sb = new StringBuilder();
      sb.append(ch);
      while (isContentOfSelector(input.peek())) {
        sb.append(input.next());
      }
      if (sb.length() == 1) {
        input.exception("类选择器内容不能为空。");
      }
      return new Token(TokenType.CLASS_SELECTOR, sb.toString(), trace);
    }

    //限制器
    if (isStartOfLimiter(ch)) {
      StringBuilder sb = new StringBuilder();
      sb.append(ch);
      while (isContentOfLimiter(input.peek())) {
        sb.append(input.next());
      }
      if (sb.length() == 1) {
        input.exception("限制器内容不能为空。");
      }
      return new Token(TokenType.LIMITER, sb.toString(), trace);
    }

    //查询选择器
    if (isQuerySign(ch)) {
      if (isContentOfQuerySelector(input.peek())) {
        //普通查询选择器
        StringBuilder sb = new StringBuilder();
        sb.append(ch);
        while (isContentOfQuerySelector(input.peek())) {
          sb.append(input.next());
        }
        return new Token(TokenType.QUERY_SELECTOR, sb.toString(), trace);
      } else {
        //匿名查询选择器
        return new Token(TokenType.ANONYMOUS_QUERY_SELECTOR, String.valueOf(ch), trace);
      }
    }

    //标识符
    if (isStartOfIndentifier(ch)) {
      StringBuilder sb = new StringBuilder();
      sb.append(ch);
      while (isContentOfIndentifier(input.peek())) {
        sb.append(input.next());
      }
      return new Token(TokenType.IDENTIFIER, sb.toString(), trace);
    }

    //操作符
    if (isStartOfOperator(ch)) {
      StringBuilder sb = new StringBuilder();
      sb.append(ch);
      while (isContentOfOperator(input.peek())) {
        sb.append(input.next());
      }
      return new Token(TokenType.OPERATOR, sb.toString(), trace);
    }

    //数字
    if (isNumber(ch)) {
      StringBuilder sb = new StringBuilder();
      sb.append(ch);
      while (isNumber(input.peek())) {
        sb.append(input.next());
      }
      if (isDot(input.peek())) {
        sb.append(input.next());
        while (isNumber(input.peek())) {
          sb.append(input.next());
        }
      }
      return new Token(TokenType.NUMBER, sb.toString(), trace);
    }

    //字符串
    if (isStartOfString(ch)) {
      StringBuilder sb = new StringBuilder();
      while (ch != input.peek()) {
        if (input.eof()) {
          input.exception(MessageFormat.format("期待 <{0}>，找到 <EOF>。", ch));
        }
        char next = input.next();
        if (isEscape(next)) {
          if (input.eof()) {
            input.exception("未结束的 Escape 符。");
          }
          sb.append(escape(input.next()));
        } else {
          sb.append(next);
        }
      }
      input.next();
      return new Token(TokenType.STRING, sb.toString(), trace);
    }

    input.exception(MessageFormat.format("不知道如何处理的字符 <{0}>。", ch));
    return null;
  }
  public TokenSequence tokenize() {
    List<Token> list = new ArrayList<>();
    skipAllWhiteSpace();
    while (!input.eof()) {
      list.add(readNext());
      skipAllWhiteSpace();
    }
    return new TokenSequence(input.getSource(), list);
  }
}
