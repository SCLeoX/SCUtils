package net.minegeck.plugins.scutils.minequery;

import net.minegeck.plugins.scutils.PlayerInterlayer;
import net.minegeck.plugins.utils.Annotations;

import java.util.*;

@Annotations.Info(作者 = "SCLeo", 许可 = "GPLv3")
public class PlayerCollection implements Set<PlayerInterlayer> {
  private final LinkedHashSet<PlayerInterlayer> collection;
  public PlayerCollection() {
    this.collection = new LinkedHashSet<>();
  }
  public PlayerCollection(LinkedHashSet<PlayerInterlayer> set) {
    this.collection = set;
  }
  public PlayerCollection(PlayerInterlayer player) {
    this.collection = new LinkedHashSet<>();
    this.collection.add(player);
  }
  public PlayerCollection(PlayerInterlayer[] array) {
    this.collection = new LinkedHashSet<>(Arrays.asList(array));
  }
  public PlayerCollection(Collection<PlayerInterlayer> set) {
    this(new LinkedHashSet<>(set));
  }

  public static PlayerCollection union(Collection<PlayerInterlayer> ...targets) {
    return new PlayerCollection().unionWith(targets);
  }

  public static PlayerCollection intersect(Collection<PlayerInterlayer>... targets) {
    return new PlayerCollection().intersectionWith(targets);
  }

  /**
   * 变成并获得与另一个或多个 Collection 的并集。{1, 2} + {2, 3} = {1, 2, 3}； {1, 2} + {2, 3} + {2, 3, 4} = {1, 2, 3, 4}
   *
   * @param targets
   * @return
   */
  public PlayerCollection unionWith(Collection<PlayerInterlayer> ...targets) {
    for (Collection<PlayerInterlayer> target : targets) {
      collection.addAll(target);
    }
    return this;
  }

  /**
   * 变成并获得与另一个或多个 Collection 的交集。{1, 2} * {2, 3} = {2}； {1, 2} * {2, 3} * {2, 3, 4} = {2}
   *
   * @param targets
   * @return
   */
  public PlayerCollection intersectionWith(Collection<PlayerInterlayer> ...targets) {
    Iterator<PlayerInterlayer> iter = collection.iterator();
    while (iter.hasNext()) {
      PlayerInterlayer pi = iter.next();
      for (Collection<PlayerInterlayer> target : targets) {
        if (!target.contains(pi)) {
          iter.remove();
          break;
        }
      }
    }
    return this;
  }

  /**
   * 变成并获得与另一个 Collection 的差集。{1, 2} - {2, 3} = {1}
   *
   * @param target
   * @return
   */
  public PlayerCollection diffWith(Collection<PlayerInterlayer> target) {
    collection.removeAll(target);
    return this;
  }

  /**
   * 变成并获得与另一个 Collection 的对等差集。{1, 2} / {2, 3} = {1, 3}
   *
   * @param target
   * @return
   */
  public PlayerCollection symmetricDiffWith(Collection<PlayerInterlayer> target) {
    for (PlayerInterlayer pi : target) {
      if (collection.contains(pi)) {
        collection.remove(pi);
      } else {
        collection.add(pi);
      }
    }
    return this;
  }

  @Override
  public int size() {
    return collection.size();
  }
  @Override
  public boolean isEmpty() {
    return collection.isEmpty();
  }
  @Override
  public boolean contains(Object o) {
    return collection.contains(o);
  }
  @Override
  public Iterator<PlayerInterlayer> iterator() {
    return collection.iterator();
  }
  @Override
  public Object[] toArray() {
    return collection.toArray();
  }
  @Override
  public <T> T[] toArray(T[] a) {
    return collection.toArray(a);
  }
  @Override
  public boolean add(PlayerInterlayer e) {
    return collection.add(e);
  }
  @Override
  public boolean remove(Object o) {
    return collection.remove(o);
  }
  @Override
  public boolean containsAll(Collection<?> c) {
    return collection.containsAll(c);
  }
  @Override
  public boolean addAll(Collection<? extends PlayerInterlayer> c) {
    return collection.addAll(c);
  }
  @Override
  public boolean removeAll(Collection<?> c) {
    return collection.removeAll(c);
  }
  @Override
  public boolean retainAll(Collection<?> c) {
    return collection.retainAll(c);
  }
  @Override
  public void clear() {
    collection.clear();
  }
}
