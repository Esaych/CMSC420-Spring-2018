package priorityqueues.test;

import fifoqueues.EmptyFIFOQueueException;
import org.junit.Test;
import priorityqueues.MinHeapPriorityQueue;
import priorityqueues.PriorityQueue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * <p>A testing framework for {@link MinHeapPriorityQueue}.</p>
 *
 * @author  <a href="mailto:jasonfil@cs.umd.edu">Jason Filippou</a>
 *
 * @see LinearPriorityQueueTest
 */
public class MinHeapPriorityQueueTest {

	/* We have copied the entire jUnit test case file from
	 * priorityqueues.LinearPriorityQueue verbatim, replacing the object
	 * being tested into a priorityqueues.MinHeapPriorityQueue. If everything's ok
	 * with the priorityqueues.MinHeapPriorityQueue class, then the same tests should
	 * be passing.
	 */
	private PriorityQueue<String> greekPublicSectorQueue =
			new MinHeapPriorityQueue<String>();
	
	@Test
	public void testMinHeapPQSimpleConstructorAndSize(){
		assertTrue(greekPublicSectorQueue.isEmpty());
		assertEquals(greekPublicSectorQueue.size(), 0);
		greekPublicSectorQueue.enqueue("ASD", 10);
		assertEquals(greekPublicSectorQueue.size(), 1);
		greekPublicSectorQueue.clear();
		assertTrue(greekPublicSectorQueue.isEmpty());
		assertEquals(greekPublicSectorQueue.size(), 0);
	}
	
	@Test
	public void testMinHeapPQOrderOfInsertedElements(){
		greekPublicSectorQueue.enqueue("Filippou", 2);
		greekPublicSectorQueue.enqueue("Vasilakopoulos", 2);
		assertEquals(greekPublicSectorQueue.size(), 2);
		try {
			assertEquals(greekPublicSectorQueue.getFirst(), "Filippou");
		} catch (EmptyFIFOQueueException e) {
			fail("EmptyFIFOQueueException should not've been thrown.");
		}
		assertEquals(greekPublicSectorQueue.size(), 2);
		try {
			assertEquals(greekPublicSectorQueue.dequeue(), "Filippou");
		} catch (EmptyFIFOQueueException e) {
			fail("EmptyFIFOQueueException should not've been thrown.");
		}
		assertEquals(greekPublicSectorQueue.size(), 1);
		try {
			assertEquals(greekPublicSectorQueue.getFirst(), "Vasilakopoulos");
		} catch (EmptyFIFOQueueException e) {
			fail("EmptyFIFOQueueException should not've been thrown.");
		}
		greekPublicSectorQueue.enqueue("Papandreou", 1);
		greekPublicSectorQueue.enqueue("Mitsotakis", 1);
		try {
			assertNotEquals(greekPublicSectorQueue.getFirst(), "Vasilakopoulos"); // No longer the getFirst
			assertEquals(greekPublicSectorQueue.dequeue(), "Papandreou");
			assertEquals(greekPublicSectorQueue.dequeue(), "Mitsotakis");
			assertEquals(greekPublicSectorQueue.dequeue(), "Vasilakopoulos");
		}catch(EmptyFIFOQueueException e){
			fail("EmptyFIFOQueueException should not've been thrown.");
		}
		assertTrue(greekPublicSectorQueue.isEmpty());
		greekPublicSectorQueue.clear();
	}
	
	@Test
	public void testMinHeapPQIterator(){
		String[] strings = {"Karathodori", "Stergiou", "Tasou", "Pipinis", "Papandreou", "Mitsotakis"};
		for(int i = 0; i < strings.length; i++)
			greekPublicSectorQueue.enqueue(strings[i], strings.length - 1 - i);
		Iterator<String> it = greekPublicSectorQueue.iterator();
		assertTrue(it.hasNext());
		/*assertEquals(it.next(), "Mitsotakis");
		assertEquals(it.next(), "Papandreou");
		assertEquals(it.next(), "Pipinis");
		*/try {
			for(int i = strings.length - 1; i > -1; i--)
				assertEquals(it.next(), strings[i]);
		}catch(NoSuchElementException e){
			fail("A NoSuchElementException should not've been thrown.");
		}
		assertFalse(it.hasNext());
		greekPublicSectorQueue.clear();
	}

}
