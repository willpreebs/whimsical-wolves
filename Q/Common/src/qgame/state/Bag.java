package qgame.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static qgame.util.ValidationUtil.validateArg;

/**
 * Represents a collection of T. While it is similar to a
 * Collections object, what makes it unique is that instead of getting specific
 * items T, a user of this class can request a number of Ts to retrieve from
 * the bag, and these items once requested are removed from the bag.
 * @param <T>
 */
public class Bag<T>{
  private final List<T> items;

  public Bag() {
    this.items = new ArrayList<>();
  }

  public Bag(Bag<T> items) {
    this.items = new ArrayList<>(items.getItems());
  }

  public Bag(Collection<T> items) {
    this.items = new ArrayList<>(items);
  }

  public void add(T item) throws IllegalArgumentException {
    this.items.add(item);
  }

  public void addAll(Collection<T> itemsToAdd) throws IllegalArgumentException {
    this.items.addAll(itemsToAdd);
  }

  public void addAll(Bag<T> itemsToAdd) throws IllegalArgumentException {
    this.items.addAll(itemsToAdd.getItems());
  }

  private Map<T, Integer> itemCounts(Collection<T> itemsToCount) {
    Map<T, Integer> counts = new HashMap<>();
    itemsToCount.forEach(item -> counts.put(item, 1 +counts.getOrDefault(item, 0)));
    return counts;
  }

  public boolean contains(T item) {
    return items.contains(item);
  }

  /**
   * Returns whether all the items in the collection of itemsToRemove are
   * in this bag.
   * @param itemsToRemove list of items to check for
   * @return true if all items are present in the bag, false if not.
   * @throws IllegalArgumentException if the collection is invalid (null)
   */
  public boolean contains(Collection<T> itemsToRemove) throws IllegalArgumentException {
    Map<T, Integer> bagCounts = itemCounts(this.items);
    Map<T, Integer> otherCounts = itemCounts(itemsToRemove);
    for (T item : otherCounts.keySet()) {
      if (!bagCounts.containsKey(item) ||
      bagCounts.get(item) < otherCounts.get(item)) {
        return false;
      }
    }
    return true;
  }

  public void remove(T item) {
    validateArg(this::contains, item, "Cannot remove an item this bag doesn't contain");
    this.items.remove(item);
  }

  public void removeAll(Collection<T> itemsToRemove) throws IllegalArgumentException {
    // validateArg(this::contains, itemsToRemove, "Cannot remove tiles this bag does not have");
    itemsToRemove.forEach(this::remove);
  }

  public Collection<T> getItems() {
    return new ArrayList<>(this.items);
  }

  public Collection<T> getItems(int count) {
    validateArg(size -> size >= count, this.items.size(), "Cannot request more items "
            + "than bag capacity.");
    List<T> newItems = this.items.subList(0, count);
    return new ArrayList<>(newItems);
  }

  public int size() {
    return this.items.size();
  }

  // removes a random item from this bag and returns the item
  public T removeRandomItem() {
    Random r = new Random();
    int randIndex = r.nextInt(items.size());
    return items.remove(randIndex);
  }
}
