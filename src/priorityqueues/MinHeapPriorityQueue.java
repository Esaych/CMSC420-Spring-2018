package priorityqueues;

import exceptions.InvalidPriorityException;
import heaps.EmptyHeapException;
import heaps.LinkedMinHeap;
import heaps.MinHeap;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p><tt>MinHeapPriorityQueue</tt> is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * <p>You  <b>must</b> implement the methods in this file! To receive <b>any credit</b> for the unit tests related to this class, your implementation <b>must</b>
 * use <b>whichever</b> {@link MinHeap} implementation among the two that you should have implemented you choose!</p>
 *
 * @author  ---- YOUR NAME HERE ----
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T> {

	private int orderCounter;
	private MinHeap<PriorityQueueNode> data;
	private boolean modificationFlag;

	/**
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue() {
		orderCounter = 0;
		data = new LinkedMinHeap<>(); // Any MinHeap can be used.
		modificationFlag = false;
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException {
		if (priority < 1)
			throw new InvalidPriorityException("Invalid priority " + priority + " provided.");
		data.insert(new PriorityQueueNode(element, priority));
		modificationFlag = true;
	}


	@Override
	public T dequeue() throws EmptyPriorityQueueException {
		T retVal;
		try {
			retVal = data.deleteMin().data;
		} catch (EmptyHeapException e) {
			throw new EmptyPriorityQueueException("dequeue(): FIFOQueue is empty!");
		}
		modificationFlag = true;
		return retVal;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {
		// TODO Auto-generated method stub
		try {
			return data.getMin().data;
		} catch (EmptyHeapException e) {
			throw new EmptyPriorityQueueException("getFirst(): FIFOQueue is empty!");
		}
	}

	public Iterator<T> iterator() {
		return new MinHeapPQIterator();
	}

	private class MinHeapPQIterator implements Iterator<T> {


		private Iterator<PriorityQueueNode> currentIt;

		public MinHeapPQIterator() {
			currentIt = data.iterator(); // falling back to MinHeap's iterator().
			modificationFlag = false; // Necessary otherwise next() will never work.
		}

		@Override
		public boolean hasNext() {
			return currentIt.hasNext();
		}

		@Override
		public T next() throws ConcurrentModificationException, NoSuchElementException {
			if(!currentIt.hasNext())
				throw new NoSuchElementException("next(): No further elements!");
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): modification of MinHeap detected while " +
				" iterating through it.");
			return currentIt.next().data;
		}


	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return data.size() == 0;
	}

	@Override
	public void clear() {
		data.clear();
		orderCounter = 0;
	}

	/**
	 * A PriorityQueueNode is a Comparable type which is used to wrap around
	 * the (data, priority) pairs. Its overriding of the compareTo() method
	 * allows the contained MinHeap in the priority queue to disambiguate between
	 * the same priority elements, thus establishing a strict ordering in the heap,
	 * such that the root is always uniquely defined.
	 *
	 * @author <a href="mailto:jasonfil@cs.umd.edu">Jason Filippou</a>
	 */
	private class PriorityQueueNode implements Comparable<PriorityQueueNode> {

		private T data;
		private int priority;
		private int orderInserted;

		public PriorityQueueNode(T data, int priority) {
			this.data = data;
			this.priority = priority;
			orderInserted = orderCounter++;
		}

		public PriorityQueueNode(T data) {
			this(data, -1);
		}

		public PriorityQueueNode() {
			this(null);
		}

		@Override
		public int compareTo(PriorityQueueNode o) {
			// Remember that a numerically smaller priority
			// is actually considered larger in priority
			// queue terms. Also recall that we are using a
			// MinHeap, so the smallest elements ascend to the top,
			// not the largest.
			if (priority < o.priority)
				return -1;
			else if (priority > o.priority)
				return 1;
			else {
				if (orderInserted < o.orderInserted)
					return -1;
				else
					return 1;
			}
		}

	}

}

