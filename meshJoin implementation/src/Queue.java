

public class Queue {
	
	class Node {
		String data;
		Node prev;
		Node next;

		public Node(String data) {
			this.data = data;
		}
	}

	private Node head;

	// returns node but if it already exists else creates a new one and returns that
	public Node getNode(String PRODUCT_ID) {
		if (this.head == null) {
			this.head = new Node(PRODUCT_ID);
			this.head.next = null;
			this.head.prev = null;
			return head;
		}
		Node iteratorNode = head;
		while (true) {
			if (iteratorNode.data.equals(PRODUCT_ID))
				return iteratorNode;
			if (iteratorNode.next != null)
				iteratorNode = iteratorNode.next;
			else
				break;
		}
		Node newNode = new Node(PRODUCT_ID);
		newNode.next = null;
		newNode.prev = iteratorNode;
		iteratorNode.next = newNode;
		return newNode;
	}

	public int totalSize() {
		int res = 0;
		Node node = this.head;
		while (node != null) {
			res++;
			node = node.next;
		}
		return res;
	}
	
	public String getHeadData() {
		if (this.head != null)
			return head.data;
		return null;
	}

	public void deleteNode(Node toDelete) {
		if (this.head == null || toDelete == null)
			return;
		if (toDelete == this.head)
			this.head = toDelete.next;
		if (toDelete.next != null)
			toDelete.next.prev = toDelete.prev;
		if (toDelete.prev != null)
			toDelete.prev.next = toDelete.next;
		toDelete = null;
		return;
	}

}

