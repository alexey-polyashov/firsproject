package linkedhashtable;

import hashtable.HashTable;

public class LinkedHashTableImpl<K, V> implements HashTable<K, V> {

    private final Item<K, V> emptyItem = new Item<>(null, null);

    static class Item<K, V> implements Entry<K, V> {

        private final K key;
        private V value;
        private Item<K, V> next = null;
        private Item<K, V> prev = null;

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

        private Item<K, V> getNext(){
            return next;
        }

        private void setNext(Item<K,V> next){
            this.next = next;
        }

        private Item<K, V> getPrev(){
            return prev;
        }

        private void setPrev(Item<K,V> prev){
            this.prev = prev;
        }

    }

    private final Item<K, V>[] data;
    private int size;

    public LinkedHashTableImpl(int initialCapacity) {
        this.data = new Item[initialCapacity * 2];
    }

    private int hashFunc(K key) {
        return key.hashCode() % data.length;
    }

    @Override
    public boolean put(K key, V value) {

        int index = hashFunc(key);

        Item<K, V> currEl = data[index];
        Item<K, V> prevEl = null;

        while (currEl != null && currEl != emptyItem) {
            if (isKeysEqual(currEl, key)) {
                currEl.setValue(value);
                return true;
            }
            prevEl = currEl;
            currEl = currEl.getNext();
        }

        if(prevEl == null){
            data[index] = new Item<>(key, value);
        }else{
            currEl = new Item<>(key, value);
            prevEl.setNext(currEl);
            currEl.setPrev(prevEl);
        }

        size++;
        return true;

    }

    @Override
    public V get(K key) {
        Item<K, V> item = indexOf(key);
        return item != null ? item.getValue() : null;
    }

    private Item<K, V> indexOf(K key) {
        int index = hashFunc(key);

        Item<K, V> currEl = data[index];

        while (currEl != null) {
            if (isKeysEqual(currEl, key)) {
                return currEl;
            }

            currEl = currEl.getNext();
        }

        return currEl;
    }


    private boolean isKeysEqual(Item<K, V> item, K key) {
        if (item == emptyItem) {
            return false;
        }
        return item.getKey() == null ? key == null : item.getKey().equals(key);
    }

    @Override
    public V remove(K key) {

        Item<K, V> item = indexOf(key);
        if (item == null) {
            return null;
        }

        if(item.getPrev() != null){
            item.getPrev().setNext(item.getNext());
        }else if(item.getNext() != null){
            int index = hashFunc(key);
            data[index] = item.getNext();
        }else{
            int index = hashFunc(key);
            data[index] = null;
        }

        if(item.getNext() != null){
            item.getNext().setPrev(item.getPrev());
        }

        V val = item.getValue();

        item = null;

        size--;
        return val;
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
            Item<K, V> currEl = data[i];
            String pref = "";
            while(true) {
                if(currEl == null){
                    if (pref.equals("")){
                        System.out.printf("%d = [%s] - %s %n", i, "null", "null");
                    }
                    break;
                }
                System.out.printf("%s%d = [%s] - %s %n", pref, i, currEl.getKey(), currEl.getValue());
                currEl = currEl.getNext();
                pref = " ";
            }
        }
        System.out.println("----------");
    }
}