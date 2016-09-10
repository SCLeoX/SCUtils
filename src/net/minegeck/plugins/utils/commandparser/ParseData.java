package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.util.ArrayList;
import java.util.HashSet;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class ParseData {
  private int concat = 0;
  private final String[] args;
  private int pointer;
  private int unfitnessEachLeftover;
  private ArrayList<Object> results;
  private final ArrayList<String> unfitnessMessages;
  private final ArrayList<String> fieldNames;
  private final HashSet<String> branchTags;
  private final ArrayList<CaseProcessor> caseProcessors;

  public HashSet<String> getBranchTags() {
    return branchTags;
  }

  public ArrayList<CaseProcessor> getCaseProcessors() {
    return caseProcessors;
  }
  private int unfitness;
  private ParseData(
    String[] args,
    int pointer,
    int unfitnessEachLeftover,
    ArrayList<Object> results,
    ArrayList<String> unfitnessMessages,
    ArrayList<String> fieldNames,
    HashSet<String> branchTags,
    ArrayList<CaseProcessor> caseProcessors,
    int unfitness)
  {
    this.args = args;
    this.pointer = pointer;
    this.unfitnessEachLeftover = unfitnessEachLeftover;
    this.results = results;
    this.unfitnessMessages = unfitnessMessages;
    this.fieldNames = fieldNames;
    this.branchTags = branchTags;
    this.caseProcessors = caseProcessors;
    this.unfitness = unfitness;
  }

  public void setUnfitnessEachLeftover(int unfitnessEachLeftover) {
    this.unfitnessEachLeftover = unfitnessEachLeftover;
  }
  public ParseData(String[] args, int pointer) {
    this(args, pointer, 20, new ArrayList<>(), new ArrayList<String>(), new ArrayList<String>(), new HashSet<String>(), new ArrayList<CaseProcessor>(), 0);
  }
  public ParseData(String[] args) {
    this(args, 0);
  }
  public ArrayList<String> getFieldNames() {
    return this.fieldNames;
  }
  public String peek() {
    if (this.args.length <= this.pointer) {
      return null;
    } else {
      return this.args[this.pointer];
    }
  }
  public String next() {
    String result = this.peek();
    this.pointer++;
    return result;
  }
  public int getPointer() {
    return this.pointer;
  }
  public void incPointer() {
    this.pointer++;
  }
  public ParseData copy() {
    if (this.results == null) {
      return new ParseData(
        this.args,
        this.pointer,
        unfitnessEachLeftover,
        null,
        new ArrayList<>(this.unfitnessMessages),
        new ArrayList<>(this.fieldNames),
        new HashSet<>(this.branchTags),
        new ArrayList<>(this.caseProcessors),
        this.unfitness
      );
    } else {
      return new ParseData(
        this.args,
        this.pointer,
        unfitnessEachLeftover,
        new ArrayList<>(this.results),
        new ArrayList<>(this.unfitnessMessages),
        new ArrayList<>(this.fieldNames),
        new HashSet<>(this.branchTags),
        new ArrayList<>(this.caseProcessors),
        this.unfitness
      );
    }
  }
  public void unfit(int unfitness, String message) {
    this.unfitness += unfitness;
    this.unfitnessMessages.add(message);
    this.results = null;
  }
  public void putResult(Object value) {
    if (this.results != null) {
      this.results.add(value);
    }
  }
  public boolean isFit() {
    return this.unfitness == 0;
  }
  public ArrayList<String> getUnfitnessMessages() {
    return this.unfitnessMessages;
  }
  public boolean isFitterThan(ParseData target) {
    if (target == null) {
      return true;
    }
    return this.unfitness < target.unfitness;
  }
  public int getLeftoverParametersCount() {
    return Math.max(this.args.length - this.pointer, 0);
  }
  public ArrayList<Object> getResults() {
    return this.results;
  }
  public void incConcat() {
    concat++;
  }
  public int getLastParamPos() {
    return this.getPointer() - concat;
  }
}
