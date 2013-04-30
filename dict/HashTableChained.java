
/* HashTableChained.java */

package dict;


import list.ListNode;

/**
 *  HashTableChained implements a Dictionary as a hash table with chaining.
 *  All objects used as keys must have a valid hashCode() method, which is
 *  used to determine which bucket of the hash table an entry is stored in.
 *  Each object's hashCode() is presumed to return an int between
 *  Integer.MIN_VALUE and Integer.MAX_VALUE.  The HashTableChained class
 *  implements only the compression function, which maps the hash code to
 *  a bucket in the table's range.
 *
 *  DO NOT CHANGE ANY PROTOTYPES IN THIS FILE.
 **/

public class HashTableChained implements Dictionary {

	/**
	 *  Place any data fields here.
	 **/
	private int size = 0;
	private int bucketSize;

	private list.DList[] table;

	/** 
	 *  Construct a new empty hash table intended to hold roughly sizeEstimate
	 *  entries.  (The precise number of buckets is up to you, but we recommend
	 *  you use a prime number, and shoot for a load factor between 0.5 and 1.)
	 **/
	public HashTableChained(int sizeEstimate) {
		// Your solution here.
		double loadFactor = (7.0D + Math.random())/10;
		int buckets = (int) ((double)sizeEstimate/loadFactor);
		
		while(!isPrime(buckets)){
			buckets++;
		}
		bucketSize = buckets;
		
		table = new list.DList[bucketSize];
		for (int i = 0; i < bucketSize; i++){
			table[i] = new list.DList();
		}
	}
	
	public boolean isPrime(int n) {
	    if(n < 2) return false;
	    if(n == 2 || n == 3) return true;
	    if(n%2 == 0 || n%3 == 0) return false;
	    int sqrtN = (int)Math.sqrt(n)+1;
	    for(int i = 6; i <= sqrtN; i += 6) {
	        if(n%(i-1) == 0 || n%(i+1) == 0) return false;
	    }
	    return true;
	}

	/** 
	 *  Construct a new empty hash table with a default size.  Say, a prime in
	 *  the neighborhood of 100.
	 **/

	public HashTableChained() {
		// Your solution here.
		int defaultSize = 101;
		
		bucketSize = defaultSize;
		table = new list.DList[bucketSize];
		for (int i = 0; i < bucketSize; i++){
			table[i] = new list.DList();
		}
		
	}

	/**
	 *  Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
	 *  to a value in the range 0...(size of hash table) - 1.
	 *
	 *  This function should have package protection (so we can test it), and
	 *  should be used by insert, find, and remove.
	 **/

	int compFunction(int code) {
		// Replace the following line with your solution.
		int hashCode = ((13*code + 37) % 77691689) % bucketSize;
		return hashCode;
	}

	/** 
	 *  Returns the number of entries stored in the dictionary.  Entries with
	 *  the same key (or even the same key and value) each still count as
	 *  a separate entry.
	 *  @return number of entries in the dictionary.
	 **/

	public int size() {
		// Replace the following line with your solution.
		return size;
	}

	/** 
	 *  Tests if the dictionary is empty.
	 *
	 *  @return true if the dictionary has no entries; false otherwise.
	 **/

	public boolean isEmpty() {
		// Replace the following line with your solution.
		return (size == 0);
	}

	/**
	 *  Create a new Entry object referencing the input key and associated value,
	 *  and insert the entry into the dictionary.  Return a reference to the new
	 *  entry.  Multiple entries with the same key (or even the same key and
	 *  value) can coexist in the dictionary.
	 *
	 *  This method should run in O(1) time if the number of collisions is small.
	 *
	 *  @param key the key by which the entry can be retrieved.
	 *  @param value an arbitrary object.
	 *  @return an entry containing the key and value.
	 **/

	public Entry insert(Object key, Object value) {
		// Replace the following line with your solution.
		Entry newEntry = new Entry();
		newEntry.key = key;
		newEntry.value = value;
		
		int index = compFunction(key.hashCode());
		table[index].insertFront(newEntry);
		size++;
		
		//resize
		if (size > bucketSize) {
			  int s = bucketSize*2;
			  while (!isPrime(s)) {
				  s++;
			  }
			  list.DList[] oldTable = table;
			  bucketSize = s;
			  table = new list.DList[bucketSize];
			  for (int i = 0; i < bucketSize; i++) {
				  table[i] = new list.DList();
			  }
			  for (list.DList b : oldTable) {
				  list.DListNode cur = (list.DListNode) b.front();
				  while (cur.isValidNode()) {
					  try {
						  insert(((Entry) cur.item()).key, ((Entry) cur.item()).value);
						  cur = (list.DListNode) cur.next();
					  } catch (list.InvalidNodeException e) {
					  }
				  }
			  }
		  }
		//end resize
		
		return newEntry;
	}

	/** 
	 *  Search for an entry with the specified key.  If such an entry is found,
	 *  return it; otherwise return null.  If several entries have the specified
	 *  key, choose one arbitrarily and return it.
	 *
	 *  This method should run in O(1) time if the number of collisions is small.
	 *
	 *  @param key the search key.
	 *  @return an entry containing the key and an associated value, or null if
	 *          no entry contains the specified key.
	 **/

	public Entry find(Object key) {
		// Replace the following line with your solution.
		int index = compFunction(key.hashCode());
		
		if (table[index] != null){
			ListNode node = table[index].front();
			try{
				while(!((Entry)node.item()).key().equals(key)){
					node = node.next();
				}
				return (Entry)node.item();
			}catch(list.InvalidNodeException e){
				return null;
			}
		}else{
			return null;
		}
	}

	/** 
	 *  Remove an entry with the specified key.  If such an entry is found,
	 *  remove it from the table and return it; otherwise return null.
	 *  If several entries have the specified key, choose one arbitrarily, then
	 *  remove and return it.
	 *
	 *  This method should run in O(1) time if the number of collisions is small.
	 *
	 *  @param key the search key.
	 *  @return an entry containing the key and an associated value, or null if
	 *          no entry contains the specified key.
	 */

	public Entry remove(Object key) {
		// Replace the following line with your solution.
		int index = compFunction(key.hashCode());

		if (table[index] != null){
			ListNode node = table[index].front();
			try{
				while(!((Entry)node.item()).key().equals(key)){
					node = node.next();
				}
				Entry toReturn = (Entry) node.item();
				node.remove();
				size--;
				return toReturn;
			}catch(list.InvalidNodeException e){
				return null;
			}
		}else{
			return null;
		}
	}

	/**
	 *  Remove all entries from the dictionary.
	 */
	public void makeEmpty() {
		// Your solution here.
		size = 0;
		for (int i = 0; i < bucketSize; i++){
			table[i] = new list.DList();
		}
	}

}