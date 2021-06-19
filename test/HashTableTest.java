package test;

import hashtable.HashTable;
import linkedhashtable.LinkedHashTableImpl;

public class HashTableTest {

    public static void main(String[] args) {
//        HashTable<Product, Integer> hashTable = new HashTableImpl<>(5);//5 * 2 = 10
//        HashTable<Product, Integer> hashTable = new DoubleHashTableImpl<>(5);//5 * 2 = 10
        HashTable<Product, Integer> hashTable = new LinkedHashTableImpl<>(5);//5 * 2 = 10

        System.out.println("Create list-----------------------");

        hashTable.put(new Product(1, "Orange"), 150);
        hashTable.put(new Product(77, "Banana"), 100);
        hashTable.put(new Product(67, "Carrot"), 228);
        hashTable.put(new Product(60, "Lemon"), 250);
        hashTable.put(new Product(51, "Milk"), 120);
        hashTable.put(new Product(21, "Potato"), 67);

        System.out.println("Size is " + hashTable.size());
        hashTable.display();


        System.out.println("Cost potato is " + hashTable.get(new Product(21, "Potato")));
        System.out.println("Cost banana is " + hashTable.get(new Product(77, "Banana")));
        System.out.println("Cost carrot is " + hashTable.get(new Product(67, "Carrot")));

        System.out.println("Remove items-----------------------");
        System.out.println("Remove Lemon - " + hashTable.remove(new Product(60, "Lemon")));
        System.out.println("Remove Potato - " + hashTable.remove(new Product(21, "Potato")));
        System.out.println("Remove Banana - " + hashTable.remove(new Product(77, "Banana")));
        hashTable.display();

        System.out.println("Cost potato is " + hashTable.get(new Product(21, "Potato")));
        System.out.println("Cost banana is " + hashTable.get(new Product(77, "Banana")));
        System.out.println("Cost carrot is " + hashTable.get(new Product(67, "Carrot")));


        System.out.println("Add items-----------------------");
        hashTable.put(new Product(47, "Pineapple"), 228);

        hashTable.display();
    }

}
