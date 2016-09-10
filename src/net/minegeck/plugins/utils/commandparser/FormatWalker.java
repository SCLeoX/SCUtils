package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.util.ArrayList;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class FormatWalker {
  private final ArrayList<String> fieldNames;
  private String function;

  public String getFunction() {
    return function;
  }

  public void setFunction(String function) {
    this.function = function;
  }

  public FormatWalker() {
    this(new ArrayList<String>(), "");
  }

  public FormatWalker copy() {
    return new FormatWalker(new ArrayList<>(this.fieldNames), function);
  }

  public FormatWalker(ArrayList<String> fieldNames, String function) {
    this.fieldNames = fieldNames;
    this.function = function;
  }

  public ArrayList<String> getFieldNames() {
    return fieldNames;
  }

}
