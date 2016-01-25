package textgen;

import java.util.AbstractList;


/** A class that implements a doubly linked list
 * 
 * @author UC San Diego Intermediate Programming MOOC team & Karen Blakemore
 *
 * @param <E> The type of the elements stored in the list
 */
public class MyLinkedList<E> extends AbstractList<E> {
	LLNode<E> head;
	LLNode<E> tail;
	int size;

	/** Create a new empty LinkedList */
	public MyLinkedList() {
		this.head = new LLNode<E>(null);	// Head sentinel node
		this.tail = new LLNode<E>(null);	// Tail sentinel node
		this.head.next = tail;
		this.tail.prev = head;
		this.size = 0;
	}

	/**
	 * Appends an element to the end of the list
	 * @param element The element to add
	 */
	public boolean add(E element) 
	{
		new LLNode<E>(element, tail.prev, tail);	
		size++;
		return true;
	}

	/** Get the element at position index 
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 */
	public E get(int index) 
	{
		if(index < 0 || index > size-1) throw new IndexOutOfBoundsException();
		
		LLNode<E> node = this.head;
		
		for(int i=0; i<=index; i++) {
			node = node.next;
		}
		
		return node.data;
	}

	/**
	 * Add an element to the list at the specified index
	 * @param The index where the element should be added
	 * @param element The element to add
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 * @throws NullPointerException for null elements.
	 */
	public void add(int index, E element)
	{
		if(index < 0 || index > size) throw new IndexOutOfBoundsException();
		
		if(element==null) throw new NullPointerException();
		
		// get node currently at index; or tail, for empty list and index 0.
		LLNode<E> node = this.head;
		for(int i=0; i<=index; i++) {
			node = node.next;
		}
		
		// insert new node prior to node.
		new LLNode<E>(element, node.prev, node);
		size++;
	}


	/** Return the size of the list */
	public int size() 
	{
		return size;
	}

	/** Remove a node at the specified index and return its data element.
	 * @param index The index of the element to remove
	 * @return The data element removed
	 * @throws IndexOutOfBoundsException If index is outside the bounds of the list
	 * 
	 */
	public E remove(int index) 
	{
		if(index < 0 || index > size-1) throw new IndexOutOfBoundsException();
		
		// get node prior to node to be removed
		LLNode<E> priorNode = this.head;
		for(int i=0; i<index; i++) {
			priorNode = priorNode.next;
		}
		
		LLNode<E> node = priorNode.next;
		
		node.next.prev = priorNode;
		priorNode.next = node.next;
		size--;
		
		return node.data;
	}

	/**
	 * Set an index position in the list to a new element
	 * @param index The index of the element to change
	 * @param element The new element
	 * @return The element that was replaced
	 * @throws IndexOutOfBoundsException if the index is out of bounds.
	 * @throws NullPointer Exception for null elements.
	 */
	public E set(int index, E element)
	{
		if(index < 0 || index > size-1) throw new IndexOutOfBoundsException();
		
		if(element==null) throw new NullPointerException();
		
		// get node at specified index.
		LLNode<E> node = this.head;
		for(int i=0; i<=index; i++) {
			node = node.next;
		}
		
		E prevElement = node.data;
		node.data = element;
		
		return prevElement;
	}   
}

class LLNode<E> 
{
	LLNode<E> prev;
	LLNode<E> next;
	E data;

	public LLNode(E e) 
	{
		this.data = e;
		this.prev = null;
		this.next = null;
	}
	
	public LLNode(E e, LLNode<E> prevNode, LLNode<E> nextNode) 
	{
		this.data = e;
		this.next = nextNode;
		this.prev = nextNode.prev;
		prevNode.next = this;
		nextNode.prev = this;
	}

}
