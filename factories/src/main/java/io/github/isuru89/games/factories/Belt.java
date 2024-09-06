package io.github.isuru89.games.factories;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Belt<T> {

    private final List<BeltItem<T>> items = new LinkedList<>();
    private int length;

    public Belt() {

    }

    public Belt(int length) {
        this.length = length;
    }

    public Belt(int length, List<BeltItem<T>> items) {
        this.length = length;
        this.items.addAll(items);
    }

    public void extendBy(int diff) {
        int tmp = length + diff;
        if (tmp < 1) {
            length = 1;
            return;
        }

        length = tmp;
    }

    public List<Belt<T>> cutAt(int at) {
        if (items.isEmpty()) {
            return List.of(new Belt<>(length - at), new Belt<>(at));
        }

        int curr = 0;
        var it = items.iterator();
        List<BeltItem<T>> left = new LinkedList<>();
        List<BeltItem<T>> right = new LinkedList<>();
        while (it.hasNext()) {
            var bItem = it.next();
            int tmp = curr + bItem.distanceToNext + 1;

            if (tmp > at) {
                left.add(bItem);
            } else {
                right.add(bItem);
            }

            curr = tmp;
        }

        var leftBelt = new Belt<>(length - at, left);
        var rightBelt = new Belt<>(at, right);

        var rightCount = 0;
        for (var item : rightBelt.items) {
            rightCount += item.distanceToNext;
        }
        rightCount = rightCount > 0 ? at - rightCount : 0;

        if (!leftBelt.items.isEmpty()) {
            var first = leftBelt.items.getFirst();
            first.setDistanceToNext(first.distanceToNext - rightCount + 1);
        }

        return List.of(leftBelt, rightBelt);
    }

    public void addItem(BeltItem<T> beltItem) {
        items.add(beltItem);
    }

    public void addItem(T item, int atDistance) {
        ListIterator<BeltItem<T>> it = items.listIterator();
        int curr = 0;
        BeltItem<T> prevItem = null;

        if (items.isEmpty()) {
            it.add(new BeltItem<>(item, atDistance - 1));
            return;
        }

        while (it.hasNext()) {
            var bItem = it.next();
            var tmp = curr + bItem.distanceToNext + 1;

            if (tmp < atDistance) {
                if (!it.hasNext()) {
                    // nothing next
                    it.add(new BeltItem<>(item, atDistance - tmp - 1));
                }

                prevItem = bItem;
                curr = tmp;
                continue;
            }

            if (prevItem != null) {
                it.previous();
                var itemNew = new BeltItem<>(item, atDistance - curr - 1);
                it.add(itemNew);
                it.next();
                bItem.setDistanceToNext(bItem.distanceToNext - itemNew.distanceToNext - 1);
            }

            prevItem = bItem;
            curr = tmp;
        }
    }

    public void tick() {
        if (items.isEmpty()) {
            return;
        }

        BeltItem<T> toUpdate = null;
        for (var item : items) {
            if (item.getDistanceToNext() > 0) {
                toUpdate = item;
                break;
            }
        }

        if (toUpdate == null) {
            return;
        }

        toUpdate.setDistanceToNext(toUpdate.getDistanceToNext() - 1);
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        if (items.isEmpty()) {
            return StringUtils.repeat('_', length);
        }

        var sb = new StringBuilder();
        int curr = length;
        for (BeltItem<T> bItem : items) {
            String text = bItem.item + StringUtils.repeat("_", bItem.getDistanceToNext());
            sb.insert(0, text);
            curr -= bItem.distanceToNext + 1;
        }
        if (curr > 0) {
            sb.insert(0, StringUtils.repeat("_", curr));
        }
        return sb.toString();
    }


    public static class BeltItem<T> {
        private final T item;
        private int distanceToNext;

        private BeltItem(T item, int distanceToNext) {
            this.item = item;
            this.distanceToNext = distanceToNext;
        }

        public int getDistanceToNext() {
            return distanceToNext;
        }

        public void setDistanceToNext(int distanceToNext) {
            this.distanceToNext = distanceToNext;
        }

        @Override
        public String toString() {
            return "BeltItem{" +
                    ", distanceToNext=" + distanceToNext +
                    '}';
        }
    }
}
