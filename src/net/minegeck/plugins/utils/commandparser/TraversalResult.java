package net.minegeck.plugins.utils.commandparser;

import net.minegeck.plugins.utils.Annotations;

import java.util.ArrayList;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class TraversalResult {
  private final ArrayList<FormatWalker> walkers = new ArrayList<>();

  public ArrayList<FormatWalker> getWalkers() {
    return walkers;
  }
}
