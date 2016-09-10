package net.minegeck.plugins.scutils.minequery.ast;

import net.minegeck.plugins.utils.Annotations;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public enum ASTNodeType {

  ANONYMOUS_QUERY_SELECTOR("匿名查询选择器", ASTAnonymousQuerySelector.class),

  ATTRIBUTE_SELECTOR("属性选择器", ASTAttributeSelector.class),

  BINARY_BOOLEAN_EXPRESSION("二元布尔表达式", ASTBinaryBooleanExpression.class),

  BINARY_NUMBER_EXPRESSION("二元数字表达式", ASTBinaryNumberExpression.class),

  BINARY_SELECTOR("二元选择器", ASTBinarySelector.class),

  BINARY_STRING_EXPRESSION("二元字符串表达式", ASTBinaryStringExpression.class),

  CLASS_SELECTOR("类选择器", ASTClassSelector.class),

  CONST_NUMBER_EXPRESSION("常量数字表达式", ASTConstNumberExpression.class),

  CONST_STRING_EXPRESSION("常量字符串表达式", ASTConstStringExpression.class),

  IDENTIFIER_EXPRESSION("标识符表达式", ASTIdentifierExpression.class),

  ID_SELECTOR("ID 选择器", ASTIDSelector.class),

  LIMITER("限制器", ASTLimiter.class),

  MONADIC_BOOLEAN_EXPRESSION("一元布尔表达式", ASTMonadicBooleanExpression.class),

  MONADIC_NUMBER_EXPRESSION("一元数字表达式", ASTMonadicNumberExpression.class),

  NAME_SELECTOR("名字选择器", ASTNameSelector.class),

  QUERY_SELECTOR("查询选择器", ASTQuerySelector.class),

  SELECTOR_UNIT("查询选择器单元", ASTSelectorUnit.class),

  ;

  private final String name;
  private final Class<? extends ASTNode> klass;
  private ASTNodeType(String name, Class<? extends ASTNode> klass) {
    this.name = name;
    this.klass = klass;
  }
  public String getName() {
    return this.name;
  }
  public Class<? extends ASTNode> getNodeClass() {
    return this.klass;
  }
}
