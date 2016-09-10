package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.scutils.minequery.ast.ASTBinarySelector.ASTBinarySelectorType;
import net.minegeck.plugins.scutils.minequery.ast.ASTLimiter.ASTLimiterType;
import net.minegeck.plugins.scutils.minequery.token.Token;
import net.minegeck.plugins.scutils.minequery.token.TokenSequence;
import net.minegeck.plugins.scutils.minequery.token.TokenType;
import net.minegeck.plugins.scutils.minequery.token.Tokenizer;
import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;
import java.util.ArrayList;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ASTParser {
  private final TokenSequence sq;
  public ASTParser(TokenSequence sequence) {
    this.sq = sequence;
  }
  public ASTParser(String input) {
    this.sq = new Tokenizer(input).tokenize();
  }
  private ASTExpression parseExpression() {
    if (sq.isNext(TokenType.PUNCTUATION, "(")) {
      sq.next();
      ASTExpression expression = parsePossibleExpression();
      sq.next(TokenType.PUNCTUATION, ")");
      return expression;
    }
    if (sq.isNext(TokenType.IDENTIFIER)) {
      return new ASTIdentifierExpression(sq.next().getContent());
    }
    if (sq.isNext(TokenType.NUMBER)) {
      return new ASTConstNumberExpression(Double.valueOf(sq.next().getContent()));
    }
    if (sq.isNext(TokenType.STRING)) {
      return new ASTConstStringExpression(sq.next().getContent());
    }
    if (sq.isNext(TokenType.OPERATOR, "-")) {
      sq.next();
      ASTExpression content = parseExpression();
      if (content == null) {
        sq.lastException("取负操作符后必须是一个表达式。");
      }
      return new ASTMonadicNumberExpression(ASTMonadicNumberExpression.ASTMonadicNumberExpressionType.NEGATIVE, content);
    }
    if (sq.isNext(TokenType.OPERATOR, "!")) {
      sq.next();
      ASTExpression content = parseExpression();
      if (content == null) {
        sq.lastException("非操作符后必须是一个表达式。");
      }
      return new ASTMonadicBooleanExpression(ASTMonadicBooleanExpression.ASTMonadicBooleanExpressionType.NOT, content);
    }
    Token nextToken = sq.peek();
    sq.lastException(MessageFormat.format("此处不应有 <{0}>。", nextToken == null ? "EOF" : nextToken.getContent()));
    return null;
  }
  private ASTExpression parsePossibleExpression(ASTExpression left, int precedence) {
    if (sq.isNext(TokenType.OPERATOR)) {
      String operationRaw = sq.peek().getContent();
      BinaryExpressionType operation;
      switch (operationRaw) {
        case "&&":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.AND;
          break;
        case "||":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.OR;
          break;
        case ">":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.LARGER;
          break;
        case ">=":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.LARGER_OR_EQUAL;
          break;
        case "==":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.EQUAL;
          break;
        case "<":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.LESS;
          break;
        case "<=":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.LESS_OR_EQUAL;
          break;
        case "!=":
          operation = ASTBinaryBooleanExpression.ASTBinaryBooleanExpressionType.UNEQUAL;
          break;
        case "+":
          operation = ASTBinaryNumberExpression.ASTBinaryNumberExpressionType.PLUS;
          break;
        case "-":
          operation = ASTBinaryNumberExpression.ASTBinaryNumberExpressionType.MINUS;
          break;
        case "*":
          operation = ASTBinaryNumberExpression.ASTBinaryNumberExpressionType.MULTIPLY;
          break;
        case "/":
          operation = ASTBinaryNumberExpression.ASTBinaryNumberExpressionType.DIVID;
          break;
        case "%":
          operation = ASTBinaryNumberExpression.ASTBinaryNumberExpressionType.MOD;
          break;
        case "//":
          operation = ASTBinaryNumberExpression.ASTBinaryNumberExpressionType.INT_DIVID;
          break;
        case "&":
          operation = ASTBinaryStringExpression.ASTBinaryStringExpressionType.CONCAT;
          break;
        default:
          sq.exception(MessageFormat.format("不知道的操作符: {0}。", operationRaw));
          return null; //永远不会运行到这里
      }
      if (operation.getPrecedence() > precedence) {
        sq.next();
        return parsePossibleExpression(
          operation.createExpression(left, parsePossibleExpression(
            parseExpression(),
            operation.getPrecedence()
          )),
          precedence
        );
      }
    }
    return left;
  }
  private ASTExpression parsePossibleExpression() {
    return parsePossibleExpression(parseExpression(), 0);
  }
  private ASTClassSelector parseClassSelector() {
    Token token = sq.next(TokenType.CLASS_SELECTOR);
    return new ASTClassSelector(token.getContent().substring(1));
  }
  private ASTIDSelector parseIDSelector() {
    Token token = sq.next(TokenType.ID_SELECTOR);
    return new ASTIDSelector(token.getContent().substring(1));
  }
  private ASTQuerySelector parseQuerySelector() {
    Token token = sq.next(TokenType.QUERY_SELECTOR);
    return new ASTQuerySelector(token.getContent().substring(1));
  }
  private ASTNameSelector parseNameSelector() {
    Token token = sq.next(TokenType.IDENTIFIER);
    return new ASTNameSelector(token.getContent());
  }
  private ASTSelector parseAttributeSelector() {
    sq.next(TokenType.PUNCTUATION, "[");
    ASTAttributeSelector selector = new ASTAttributeSelector(parsePossibleExpression());
    if (!sq.isNext(TokenType.PUNCTUATION, ",")) {
      sq.next(TokenType.PUNCTUATION, "]");
      return selector;
    } else {
      ArrayList<ASTSelector> selectors = new ArrayList<>();
      selectors.add(selector);
      while (sq.isNext(TokenType.PUNCTUATION, ",")) {
        sq.next();
        selectors.add(new ASTAttributeSelector(parsePossibleExpression()));
      }
      sq.next(TokenType.PUNCTUATION, "]");
      return new ASTSelectorUnit(selectors, new ArrayList<ASTLimiter>());
    }
  }
  private ASTSelector parsePossibleSelector() {
    if (sq.eof()) {
      return null;
    }
    if (sq.isNext(TokenType.PUNCTUATION,  "[")) {
      return parseAttributeSelector();
    }
    switch (sq.peek().getType()) {
      case ANONYMOUS_QUERY_SELECTOR:
        return parseAnonymousQuerySelector();
      case QUERY_SELECTOR:
        return parseQuerySelector();
      case ID_SELECTOR:
        return parseIDSelector();
      case CLASS_SELECTOR:
        return parseClassSelector();
      case IDENTIFIER:
        return parseNameSelector();
      default:
        return null;
    }
  }
  private ASTLimiter parsePossibleLimiter() {
    if (sq.eof()) {
      return null;
    }
    if (!sq.isNext(TokenType.LIMITER)) {
      return null;
    }
    String typeName = sq.next(TokenType.LIMITER).getContent().substring(1);
    ASTLimiterType type = ASTLimiter.getLimiterType(typeName);
    if (type == null) {
      sq.lastException(MessageFormat.format("不知道名为 {0} 的限制器。", typeName));
    }
    int num = 1;
    if (sq.isNext(TokenType.PUNCTUATION, "(")) {
      sq.next(TokenType.PUNCTUATION, "(");
      String numberStr = sq.next(TokenType.NUMBER).getContent();
      try {
        num = Integer.valueOf(numberStr);
      } catch(NumberFormatException ex) {
        sq.lastException(MessageFormat.format("限制器的参数必须是整数, 而 {0} 无法被转换为整数。", numberStr));
      }
      sq.next(TokenType.PUNCTUATION, ")");
    }
    return new ASTLimiter(type, num);
  }
  private ASTSelectorUnit parseSelectorUnit() {
    ArrayList<ASTSelector> selectors = new ArrayList<>();
    ASTSelector selector;
    while ((selector = parsePossibleSelector()) != null) {
      selectors.add(selector);
    }
    if (selectors.isEmpty()) {
      sq.lastException("空的查询选择器单元。");
    }
    ArrayList<ASTLimiter> limiters = new ArrayList<>();
    ASTLimiter limiter;
    while ((limiter = parsePossibleLimiter()) != null) {
      limiters.add(limiter);
    }
    return new ASTSelectorUnit(selectors, limiters);
  }
  private ASTSelector parsePossibleBinarySelector(ASTSelector left, int precedence) {
    if (sq.isNext(TokenType.OPERATOR)) {
      String operationRaw = sq.peek().getContent();
      ASTBinarySelectorType operation = ASTBinarySelector.getASTBinarySelectorType(operationRaw);
      if (operation == null) {
        sq.exception(MessageFormat.format("不知道的集合操作符: {0}。", operationRaw));
      }
      if (operation.getPrecedence() > precedence) {
        sq.next();
        return parsePossibleBinarySelector(
          new ASTBinarySelector(operation, left, parsePossibleBinarySelector(
            parseSelectorUnit(),
            operation.getPrecedence()
          )),
          precedence
        );
      }
    }
    return left;
  }
  private ASTSelector parsePossibleBinarySelector() {
    return parsePossibleBinarySelector(parseSelectorUnit(), 0);
  }
  private ASTAnonymousQuerySelector parseAnonymousQuerySelector() {
    sq.next(TokenType.ANONYMOUS_QUERY_SELECTOR);
    if (!sq.isNext(TokenType.PUNCTUATION, "(")) {
      return new ASTAnonymousQuerySelector(new ArrayList<ASTSelector>());
    }
    sq.next(TokenType.PUNCTUATION, "(");
    ArrayList<ASTSelector> units = new ArrayList<>();
    if (!sq.isNext(TokenType.PUNCTUATION, ")")) {
      while(true) {
        units.add(parsePossibleBinarySelector());
        if (!sq.isNext(TokenType.PUNCTUATION, ",")) {
          break;
        }
        sq.next();
      }
    }
    sq.next(TokenType.PUNCTUATION, ")");
    return new ASTAnonymousQuerySelector(units);
  }
  public ASTSelector parseToSelector() {
    ASTSelector result = parsePossibleBinarySelector();
    if (!sq.eof()) {
      sq.exception(MessageFormat.format("期待 <EOF>, 找到 {0}。", sq.peek().toString()));
    }
    return result;
  }
  public ASTExpression parseToExpression() {
    ASTExpression result = parsePossibleExpression();
    if (!sq.eof()) {
      sq.exception(MessageFormat.format("期待 <EOF>, 找到 {0}。", sq.peek().toString()));
    }
    return result;
  }
}
