package hashtable;

import hashtable.HashTableImpl;

public class DoubleHashTableImpl<K, V> extends HashTableImpl<K, V> {

    private static final int DOUBLE_HASH_CONST=5;

    public DoubleHashTableImpl(int initialCapacity){
            super(initialCapacity);
            }

    @Override
    protected int getStep(K key){
            return hashDoubleFunc(key);
            }

    private int hashDoubleFunc(K key){
            return 5-(key.hashCode()%DOUBLE_HASH_CONST);
            }

}
