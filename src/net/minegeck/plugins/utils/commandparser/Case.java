package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class Case {

  public class SCCommandFormater使用方式错误 extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private SCCommandFormater使用方式错误(String message) {
      super(message);
    }
  }

  private boolean closed = false;
  private final ArrayList<String> fieldNames = new ArrayList<>();
  private final ArrayList<FieldLoader> fieldLoaders = new ArrayList<>();
  private final ArrayList<Case> cases = new ArrayList<>();
  private String function;
  public Case optional(Case kase) {
    this.kase(new Case());
    this.kase(kase);
    return this;
  }
  public void traversal(FormatWalker walker, TraversalResult result) {
    if (this.function != null) {
      walker.setFunction(function);
    }
    walker.getFieldNames().addAll(fieldNames);
    if (cases.isEmpty()) {
      result.getWalkers().add(walker);
    } else {
      for (Case kase : cases) {
        kase.traversal(walker.copy(), result);
      }
    }
  }
  public Case func(String function) {
    this.function = function;
    return this;
  }
  public Case field(String fieldName, FieldLoader fieldLoader, boolean addBrackets) {
    if (closed) {
      throw new SCCommandFormater使用方式错误("此处不允许再添加新的 Field，请使用 merge 进行合并。");
    }
    this.fieldNames.add(addBrackets ? "<" + fieldName + ">" : fieldName);
    this.fieldLoaders.add(fieldLoader);
    return this;
  }
  public Case merge(Case kase) {
    if (cases.isEmpty()) {
      this.kase(kase);
    } else {
      for (Case subCase : cases) {
        subCase.merge(kase);
      }
    }
    return this;
  }
  public Case field(String fieldName, FieldLoader fieldLoader) {
    return field(fieldName, fieldLoader, true);
  }
  public Case field(FieldLoader fieldLoader) {
    return field(fieldLoader.getFieldName(), fieldLoader, false);
  }
  public Case kase(Case kase) {
    closed = true;
    this.cases.add(kase);
    return this;
  }

  private int unfitnessEachLeftOver = 4;
  private final HashSet<String> branchTags = new HashSet<>();
  private final ArrayList<CaseProcessor> caseProcessors = new ArrayList<>();

  public Case withUnfitnessEachLeftOverAt(int scale) {
    this.unfitnessEachLeftOver = scale;
    return this;
  }

  public Case tag(String tag) {
    this.branchTags.add(tag);
    return this;
  }

  public Case processor(CaseProcessor processor) {
    this.caseProcessors.add(processor);
    return this;
  }

  public ParseData load(ParseData data) {
    for (FieldLoader fieldLoader : this.fieldLoaders) {
      fieldLoader.load(data);
    }
    data.getFieldNames().addAll(fieldNames);
    data.getBranchTags().addAll(branchTags);
    data.getCaseProcessors().addAll(caseProcessors);
    if (this.cases.isEmpty()) {
      if (unfitnessEachLeftOver != 0) {
        int leftover = data.getLeftoverParametersCount();
        if (leftover != 0) {
          data.unfit(unfitnessEachLeftOver * leftover, MessageFormat.format("多提供了 {0} 个参数。", leftover));
        }
      }
      return data;
    } else {
      ParseData[] datas = new ParseData[this.cases.size()];
      int i = 0;
      for (Case kase : this.cases) {
        datas[i++] = kase.load(data.copy());
      }
      ParseData finalData = null;
      for (ParseData testingData : datas) {
        if (testingData.isFit()) {
          return testingData;
        }
        if (testingData.isFitterThan(finalData)) {
          finalData = testingData;
        }
      }
      return finalData;
    }
  }

}
