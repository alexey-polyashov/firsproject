package hashtable;

public class HashTableImpl<K, V> implements HashTable<K, V> {

    private final Item<K, V> emptyItem = new Item<>(null, null);

    static class Item<K, V> implements Entry<K, V> {
        private final K key;
        private V value;

        public Item(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    private final Item<K, V>[] data;
    private int size;

    public HashTableImpl(int initialCapacity) {
        this.data = new Item[initialCapacity * 2];
    }

    private int hashFunc(K key) {
        return key.hashCode() % data.length;
    }

    @Override
    public boolean put(K key, V value) {
        int index = hashFunc(key);

        int count = 0;
        while (data[index] != null && data[index] != emptyItem) {
            if (isKeysEqual(data[index], key)) {
                data[index].setValue(value);
                return true;
            }

            if (count >= data.length) {
                return false;
            }

            count++;
            index += getStep(key);
            index %= data.length;
        }

        data[index] = new Item<>(key, value);
        size++;
        return true;
    }

    @Override
    public V get(K key) {
        int index = indexOf(key);
        return index != -1 ? data[index].getValue() : null;
    }

    private int indexOf(K key) {
        int index = hashFunc(key);

        int count = 0;
        while (count < data.length) {
            Item<K, V> item = data[index];
            if (item == null) {
                break;
            } else if (isKeysEqual(item, key)) {
                return index;
            }

            count++;
            index += getStep(key);
            index %= data.length;
        }

        return -1;
    }

    protected int getStep(K key) {
        return 1;
    }

    private boolean isKeysEqual(Item<K, V> item, K key) {
        if (item == emptyItem) {
            return false;
        }
        return item.getKey() == null ? key == null : item.getKey().equals(key);
    }

    @Override
    public V remove(K key) {
        int index = indexOf(key);
        if (index == -1) {
            return null;
        }

        Item<K, V> item = data[index];
        data[index] = emptyItem;

        size--;
        return item.getValue();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size != 0;
    }

    @Override
    public void display() {
        System.out.println("----------");
        for (int i = 0; i < data.length; i++) {
            System.out.printf("%d = [%s]%n", i, data[i]);
        }
        System.out.println("----------");
    }
}