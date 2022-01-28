import java.util.ArrayList;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;



public class hashTable {
	
	class Value {
		Queue.Node queueNode;
		tData transaction;

		public Value(Queue.Node queueNode, tData transaction) {
			this.queueNode = queueNode;
			this.transaction = transaction;
		}
	}

	private Integer size;
	private MultiValuedMap<String, Value> hashTable;
	private Queue queue;

	public hashTable(Integer size) {
		this.size = size;
		this.hashTable = new ArrayListValuedHashMap<String, Value>(this.size);
		this.queue = new Queue();
	}

	public void addTransactions(ArrayList<tData> transactions) {
		for (tData transaction : transactions) {
			this.hashTable.put(transaction.PRODUCT_ID,
					new Value(this.queue.getNode(transaction.PRODUCT_ID), transaction));
		}
	}

	public String getOldestEntry() {
		return this.queue.getHeadData();
	}

	public int getCapacity() {
		return 100 - this.hashTable.size();
	}

	public ArrayList<Value> join(String PRODUCT_ID) {
		return new ArrayList<Value>(hashTable.get(PRODUCT_ID));
	}

	public void discard(String PRODUCT_ID) {
		if (hashTable.containsKey(PRODUCT_ID)) {
			this.queue.deleteNode(new ArrayList<Value>(hashTable.get(PRODUCT_ID)).get(0).queueNode);
			this.hashTable.remove(PRODUCT_ID);
		}
	}

	public boolean isEmpty() {
		return this.hashTable.size() == 0;
	}
	
	

}
