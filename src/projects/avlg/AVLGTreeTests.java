
package projects.avlg;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import projects.avlg.exceptions.EmptyTreeException;
import projects.avlg.exceptions.InvalidBalanceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/** A class containing jUnit tests to test the students' code with.
 *
 * @author <a href="https://github.com/jasonfil">Jason Filippou</a>
 */
public class AVLGTreeTests {

    // TODO: Use the jUnit4 solution of Exception.ExpectedException to simplify the code.

    private ArrayList<AVLGTree<Integer>> trees = new ArrayList<>(MAX_IMBALANCE);
    private static final Random RNG = new Random(47);

    private static final int MAX_IMBALANCE=10;
    private static final Integer ZERO = 0;
    private static final int NUMS = 150;

    private boolean ensureTreeEmpty(AVLGTree<?> tree){
        assert tree!= null : "ensureTreeEmpty() expects non-null trees";
        return tree.isEmpty() || tree.getCount() == 0;
    }

    private boolean ensureHeightCountAndRoot(AVLGTree<Integer> tree,
                                             int expectedHeight, int expectedCount,
                                             Object expectedRoot){
        assert tree!= null : "ensureHeightAndCount() expects non-null trees";
        try {
            assertEquals(" ", expectedRoot , tree.getRoot());
        } catch(EmptyTreeException exc ){
            return (expectedCount == 0);
        }
        return ( expectedHeight == tree.getHeight() ) &&
                (expectedCount == tree.getCount());

    }

    private boolean ensureAVLGBST(AVLGTree<?> tree){
        assert tree!= null : "ensureAVLGBST() expects non-null trees";
        return tree.isBST() && tree.isAVLGBalanced();
    }

    private void insertAndTest(IntStream stream,
                                  AVLGTree<Integer> tree, int expectedHeight,
                                  int expectedCount, Object expectedRoot) throws AssertionError{

        assert stream != null && tree != null: "insertAndTest() expects a valid IntStream and tree.";
        assert expectedCount >= 0 && expectedHeight >= -1 : "insertAndTest() expects " +
                "non-negative count parameter and a height parameter of at least -1";

        tree.clear();
        List<Integer> keys = stream.boxed().collect(Collectors.toList()); // Only for printing purposes
        keys.forEach(tree::insert);

        if(!ensureHeightCountAndRoot(tree, expectedHeight, expectedCount, expectedRoot))
            throw new AssertionError("After inserting the key sequence " + keys +
                    " in an initially empty AVL-" + tree.getMaxImbalance() + " tree, the height, count and/or new " +
                    "root were different from what was expected.");
        if(!ensureAVLGBST(tree))
            throw new AssertionError("After inserting the key sequence " + keys +
                    " in an initially empty AVL-" + tree.getMaxImbalance() + " tree, the height, count " +
                    "and/or new root were different from what was expected.");
    }

    /**
     * Set-up the trees that we will use for our tests.
     */
    @Before
    public void setUp() {
        IntStream.rangeClosed(1, MAX_IMBALANCE).forEach(imb ->
        {
            try {
                trees.add(new AVLGTree<>(imb));
            } catch (InvalidBalanceException i) {
                throw new RuntimeException(i.getMessage());
            }
        });
    }

    /**
     * Clear the contents of the trees used in our tests.
     */
    @After
    public void tearDown(){
        trees.forEach(AVLGTree::clear);
        trees.clear(); // The C++ in me has spoken.
    }

    @Test
    public void testInvalidImbalances(){
        IntStream.range(0, NUMS).forEach(imb->
        {
            InvalidBalanceException expected = null;
            try {
                new AVLGTree<Integer>(-imb); // Zero or negative imbalance
            } catch(InvalidBalanceException thrown){
                expected = thrown;
            } catch(Throwable thr) {
                fail("While initializing an AVL-" + imb + " tree we caught a "
                        + thr.getClass().getSimpleName() + " with message: " +
                        thr.getMessage() + " instead of an InvalidBalanceException.");
            }
            assertNotNull("Initializing an AVL-G tree with an imbalance parameter of " +
                    + imb + " should have thrown an InvalidBalanceException.", expected);
        });
    }

    @Test
    public void testEmptyTree(){
        trees.forEach(t->
        {
            assertTrue("Upon creation, an AVL-"+t.getMaxImbalance() +
                    " tree should be empty.", t.isEmpty());
            assertEquals("Upon creation, an AVL-"+t.getMaxImbalance() +
                    " tree should have a height of -1.", -1, t.getHeight());
            assertEquals("Upon creation, an AVL-"+t.getMaxImbalance() +
                    " tree should have a count of 0.", 0, t.getCount());
            EmptyTreeException expected = null;
            try {
                t.getRoot();
            } catch(EmptyTreeException thrown){
                expected = thrown;
            } catch(Throwable thr){
                fail("While calling getRoot() in an empty AVL-" + t.getMaxImbalance() +
                        " tree, instead of an EmptyTreeException, we caught a "
                        + thr.getClass().getSimpleName() + " with message: " +
                        thr.getMessage() + " instead of an EmptyTreeException.");
            }
            assertNotNull("Upon creation, retrieving the root of " +
                    "an AVL-" + t.getMaxImbalance() + " tree should throw an " +
                    "EmptyTreeException.", expected);
        });
    }

    @Test
    public void testTwoInsertionsAndDeletions(){
        trees.forEach(t->
        {
            // Make an insertion and test everything.

            Integer firstKey = RNG.nextInt();
            t.insert(firstKey);
            assertFalse("After inserting a key, the AVL-" +
                            t.getMaxImbalance()+ " tree should no longer be empty.",
                    t.isEmpty());
            assertEquals("After inserting a key, the AVL-" +
                            t.getMaxImbalance()+ " tree's new height should be 0.",
                    0, t.getHeight());
            assertEquals("After inserting a key, the AVL-" +
                            t.getMaxImbalance()+ " tree's new count should be 1.",
                    1, t.getCount());
            try {
                assertEquals("After inserting a key in an AVL-" +
                                t.getMaxImbalance()+ " tree, a search for it should be successful.",
                        firstKey, t.search(firstKey));
            }catch(EmptyTreeException ignored){
                fail("When searching for the only key in an AVL-" + t.getMaxImbalance() + "" +
                        " tree, an EmptyTreeException was thrown.");
            }catch(Throwable thr){
                fail("While searching for the only key of a non- empty AVL-" +
                        t.getMaxImbalance() + " tree,  we caught a " + thr.getClass().getSimpleName()
                        + " with message: " +  thr.getMessage() + ".");
            }
            try {
                assertEquals("After inserting a key in a previously empty AVL-" +
                                t.getMaxImbalance()+ " tree, it should be the tree's root.",
                        firstKey, t.getRoot());
            } catch(EmptyTreeException ignored){
                fail("getRoot() should *not* have thrown an EmptyTreeException at this point.");
            } catch(Throwable thr){
                fail("While retrieving the root of an AVL-" +  t.getMaxImbalance() +
                        " tree with a single key,  we caught a " + thr.getClass().
                        getSimpleName() + " with message: " +  thr.getMessage() + ".");
            }

            // Insert a second node and test everything

            Integer secondKey = RNG.nextInt();
            t.insert(secondKey); // Will either be left or right of root. No rotations irrespective of parameter G.
            assertFalse("After inserting a second key, the AVL-" +
                            t.getMaxImbalance()+ " tree should still *not* be empty.",
                    t.isEmpty());
            assertEquals("After inserting a second key, the AVL-" +
                            t.getMaxImbalance()+ " tree's new height should be 01",
                    1, t.getHeight());// Irrespective of G, this tree will have a height of 1.
            assertEquals("After inserting a key, the AVL-" +
                            t.getMaxImbalance()+ " tree's new count should be 2.",
                    2, t.getCount());
            try {
                assertEquals("After inserting a second key in an AVL-" +
                        t.getMaxImbalance()+ " tree, a search for the first one should" +
                        "*still* be successful.", firstKey, t.search(firstKey));
                assertEquals("After inserting a second key in an AVL-" +
                        t.getMaxImbalance()+ " tree, a search for the new key should" +
                        "be successful.", secondKey, t.search(secondKey));
            } catch(EmptyTreeException ignored){
                fail("search(Key) on an AVL-" + t.getMaxImbalance() + " tree should *not* have " +
                        "thrown an EmptyTreeException at this point.");
            } catch(Throwable thr) {
                fail("While retrieving the root of an AVL-" + t.getMaxImbalance() + " tree " +
                        " with two stored keys, we caught a " + thr.getClass().getSimpleName()
                        + " with message: " + thr.getMessage() + ".");
            }
            try {
                assertEquals("After the second key is inserted in an AVL-" + t.getMaxImbalance() +
                        "tree, the first key should still be at the root.", firstKey, t.getRoot()); // Irrespective of G, the root should *not* have changed. This is not a splay tree.
            } catch(EmptyTreeException ignored){
                fail("getRoot() on an AVL-" + t.getMaxImbalance() + " tree should *not* have " +
                        "thrown an EmptyTreeException at this point since we had another " +
                        "insertion and the tree should *still* be non-empty.");
            } catch(Throwable thr){
                fail("While retrieving the root of an AVL-" +  t.getMaxImbalance() + " tree " +
                        " with two stored keys, we caught a " + thr.getClass().getSimpleName()
                        + " with message: " +  thr.getMessage() + ".");
            }

            // Delete the root and see what happens.

            try {
                assertEquals("After deleting the key " + firstKey + " from an AVL-" + t.getMaxImbalance() +
                        " tree, we expected delete() to return the key itself.", firstKey, t.delete(firstKey));
            } catch(EmptyTreeException ignored){
                fail("delete(Key) threw an EmptyTreeException when deleting the root key " + firstKey +
                        " of an AVL-"+ t.getMaxImbalance() + " tree.");
            } catch(Throwable thr){
                fail("delete(Key) threw a " + thr.getClass().getSimpleName() +
                        " with message: " + thr.getMessage() + " when deleting" +
                        "the root key " + firstKey + " of an AVL-" + t.getMaxImbalance() + " tree.");
            }
            assertEquals("After deleting one of 2 keys available in an AVL-" +
                            t.getMaxImbalance() + " tree, we expected the new height to be 0.",
                    0, t.getHeight());
            assertEquals("After deleting one of 2 keys available in an AVL-" +
                            t.getMaxImbalance() + " tree, we expected the new count to be 1.",
                    1, t.getCount());
            assertFalse("After deleting one of 2 keys available in an AVL-" +
                    t.getMaxImbalance() + " tree, we *still* expect the tree " +
                    "to be considered empty.", t.isEmpty());

            try {
                assertEquals("After deleting the root of an AVL-" + t.getMaxImbalance() +
                                " tree, we expected the new root to be the other key.",
                        secondKey, t.getRoot());
            } catch(EmptyTreeException ignored){
                fail("getRoot() on an AVL-" + t.getMaxImbalance() + " tree should *not* have " +
                        "thrown an EmptyTreeException since the deletion of the first key" +
                        " did not leave the tree empty.");
            }catch(Throwable thr){
                fail("After we deleted the root key of an AVL-" + t.getMaxImbalance() + " tree "
                        + "with two keys in it, getRoot() threw a " + thr.getClass().getSimpleName() +
                        " with message: " + thr.getMessage() + ".");
            }

            try {
                assertEquals("", secondKey, t.search(secondKey));
            } catch(EmptyTreeException ignored){
                fail("search() on an AVL-" + t.getMaxImbalance() + " tree threw " +
                        "an EmptyTreeException when searching an AVL-" + t.getMaxImbalance() +
                        " tree for its single key.");
            } catch(Throwable thr){
                fail("search() threw a " + thr.getClass().getSimpleName() + " when searching" +
                        "the root key " + firstKey + " of an AVL-" + t.getMaxImbalance() +
                        " tree with another key in it.");
            }


            // Delete the second key, making the tree empty, and see what happens.

            try {
                assertEquals("When deleting the key " + secondKey + " from an AVL-" + t.getMaxImbalance() +
                        " tree, we expected delete() to return the key itself.", secondKey, t.delete(secondKey));
            } catch(EmptyTreeException ignored){
                fail("delete(Key) threw an EmptyTreeException when deleting key " + secondKey +
                        " from an AVL-"+ t.getMaxImbalance() + " tree.");
            } catch(Throwable thr){
                fail("delete(Key) threw a " + thr.getClass().getSimpleName() + " with message: "
                        + thr.getMessage() + " when deleting key " + secondKey +
                        " from an AVL-" + t.getMaxImbalance() + " tree.");
            }
            assertEquals("After deleting the last key of an AVL-" + t.getMaxImbalance() +
                    " tree, we expected its height to be -1.", -1, t.getHeight());
            assertEquals("After deleting the last key of an AVL-" + t.getMaxImbalance() +
                    " tree, we expected its count to be 0.", 0, t.getCount());
            assertTrue("After deleting the last key of an AVL-" + t.getMaxImbalance() +
                    " tree, we expected it to be empty.", t.isEmpty());

            // TODO: The following is a perfect example of code that could be
            // TODO: simplified using jUnit's Exception.ExpectedException rule.
            EmptyTreeException expected = null;
            try {
                t.getRoot();
            } catch(EmptyTreeException thrown){
                expected = thrown;
            } catch(Throwable thr){
                fail("After deleting the last key in an AVL-" +  t.getMaxImbalance()
                        + " tree, instead of an EmptyTreeException we caught a " +
                        thr.getClass().getSimpleName() + " with message: " +
                        thr.getMessage() + " while retrieving the root.");
            }
            assertNotNull("After deleting the last key of an AVL-" + t.getMaxImbalance() +
                    " tree, we expect getRoot() to throw an EmptyTreeException" , expected);

            expected = null;
            try {
                t.search(secondKey);
            } catch(EmptyTreeException thrown){
                expected = thrown;
            } catch(Throwable thr){
                fail("After deleting the last keys in an AVL-" +  t.getMaxImbalance()
                        + " tree  we caught a " + thr.getClass().getSimpleName()
                        + " with message: " +  thr.getMessage() + "" +
                        " instead of an EmptyTreeException");
            }
            assertNotNull("After deleting the last key of an AVL-" + t.getMaxImbalance() +
                    " tree, we expect search() to throw an EmptyTreeException" , expected);

            expected = null;
            try {
                t.delete(RNG.nextInt());
            } catch(EmptyTreeException thrown){
                expected = thrown;
            } catch(Throwable thr){
                fail("After deleting a randomly generated key from an *empty* AVL-" +  t.getMaxImbalance()
                        + " tree  we caught a " + thr.getClass().getSimpleName()
                        + " with message: " +  thr.getMessage() + "" +
                        " instead of an EmptyTreeException.");
            }
            assertNotNull("After deleting the last key of an AVL-" + t.getMaxImbalance() +
                    " tree, we expect delete() to throw an EmptyTreeException" , expected);
        });
    }

    @Test
    public void testManySuccessfulInsertions(){
        List<Integer> keys = IntStream.range(0, NUMS).boxed().collect(Collectors.toList());
        Collections.shuffle(keys, RNG);
        trees.forEach(t->keys.forEach(k-> {
            try {
                t.insert(k);
            } catch(Throwable thr){
                fail("Caught a " + thr.getClass().getSimpleName() + " when inserting the key " + k +
                        " into a tree with maxImbalance parameter " + t.getMaxImbalance() + ".");
            }
            assertTrue("After inserting the key " + k + ", which was the key #" +
                    keys.indexOf(k) + " in the insertion sequence, we determined that the" +
                    " tree did not globally satisfy the AVLG and/or BST properties",
                    ensureAVLGBST(t));
        }));
    }

    @Test
    public void testManySuccessfulDeletions(){
        List<Integer> keys = IntStream.range(0, NUMS).boxed().collect(Collectors.toList());
        Collections.shuffle(keys, RNG);
        trees.forEach(t->keys.forEach(t::insert)); // Since we've already tested those above...
        Collections.shuffle(keys, RNG); // Re-shuffle to ensure non-dependency on insertion order.
        trees.forEach(t->keys.forEach(k-> {
            try {
                assertEquals("When deleting the key " + k + " from an AVL-" + t.getMaxImbalance()+
                        " tree, we expected delete() to return the key itself.", k, t.delete(k));
            } catch(Throwable thr) {
                fail("Caught a " + thr.getClass().getSimpleName() + " when deleting the key " + k +
                        " from an AVL-" + t.getMaxImbalance() + " tree.");
            }
            assertTrue("After inserting the key " + k + ", which was the key #" +
                            keys.indexOf(k) + " in the insertion sequence, we determined that the" +
                            " tree did not globally satisfy the AVLG and/or BST properties",
                    ensureAVLGBST(t));

        }));
    }

    @Test
    public void testManySuccessfulSearches(){
        List<Integer> keys = IntStream.range(0, NUMS).boxed().collect(Collectors.toList());
        Collections.shuffle(keys, RNG);
        trees.forEach(t->keys.forEach(t::insert)); // Since we've already tested those above...
        Collections.shuffle(keys, RNG); // Re-shuffle just to avoid dependence on insertion order.
        trees.forEach(t->{
            keys.forEach(k->{
                try {
                    assertEquals("Key " + k + " should have been found in the AVL-"
                            + t.getMaxImbalance() + " tree.",  k, t.search(k));
                } catch(EmptyTreeException ignored){
                    fail("search(Key) threw an EmptyTreeException when searching for key " + k +
                            ", which was the key #"+keys.indexOf(k)+ " inside a non-empty AVL-"
                            + t.getMaxImbalance() + " tree, with " + k + " guaranteed to be in the tree.");
                } catch(Throwable thr){
                    fail("search(Key) threw a " + thr.getClass().getSimpleName() + " when searching " +
                            "for key " + k + ", which was the key #" +keys.indexOf(k)+ " in a non-empty AVL-"
                            + t.getMaxImbalance() +" tree, with " + k + " guaranteed to be in the tree.");
                }
            });
        });
    }

    @Test
    public void testManyFailedSearches(){
        List<Integer> keys = IntStream.range(0, NUMS).boxed().collect(Collectors.toList());
        Collections.shuffle(keys, RNG);
        trees.forEach(t->keys.forEach(t::insert)); // Since we've already tested those above...
        Collections.shuffle(keys, RNG); // Re-shuffle just to avoid dependence on insertion order.
        trees.forEach(t->{
            keys.stream().filter(x->(x>0)).forEach(k->{
                try {
                    assertEquals("", null, t.search(-k));
                    assertNotEquals("", k, t.search(-k));
                    assertNotEquals("", Integer.valueOf(-k), t.search(k));
                    try {
                        t.delete(k);
                    } catch(EmptyTreeException ignored){
                        fail("delete(Key) threw an EmptyTreeException when deleting key " + k +
                                " in an AVL-"+ t.getMaxImbalance() + " tree.");
                    }
                    assertEquals("", null, t.search(k));
                    assertEquals("", null, t.search(-k));
                } catch(EmptyTreeException ignored){
                    fail("search(Key) threw an EmptyTreeException when searching for key " + k +
                            ", which was the key #" + keys.indexOf(k)+ " in the sequence, inside " +
                            "an AVL-"+ t.getMaxImbalance() + " tree.");
                } catch(Throwable thr){
                    fail("search(Key) threw a " + thr.getClass().getSimpleName() + " with message " +
                            thr.getMessage() + " while performing searches which should fail " +
                            "in an AVL-" + t.getMaxImbalance() + " tree.");

                }
            });
        });
    }

    @Test
    public void testCountDeleteAndClear(){
        List<Integer> keys = IntStream.range(0, NUMS).boxed().collect(Collectors.toList());
        Collections.shuffle(keys, RNG);
        trees.forEach(t->{
            keys.forEach(t::insert);
            Collections.shuffle(keys, RNG); // Re-shuffle just to avoid dependence on insertion order.
            assertEquals("When inserting a collection of keys in an AVL-" + t.getMaxImbalance()
                    + " tree, we expect the tree's count to be the same as the collection's "
                    + "size.", keys.size(), t.getCount());
            t.clear();
            assertTrue("After clearing an AVL-" + t.getMaxImbalance() +
                    " tree, either the tree wasn't empty or its count was 0.", ensureTreeEmpty(t));

            // Re-insert and delete() everything explicitly, instead of using clear()
            keys.forEach(t::insert);
            Collections.shuffle(keys, RNG); // Re-shuffle just to avoid dependence on insertion order.
            keys.forEach(k->{
                try {
                    t.delete(k);
                } catch(EmptyTreeException ignored){
                    fail("When deleting key " + k + " from a non-empty AVL-" + t.getMaxImbalance() +
                            " tree, we caught an EmptyTreeException.");
                } catch(Throwable thr) {
                    fail("When deleting key " + k + " from a non-empty AVL- " + t.getMaxImbalance() +
                            " tree, we caught a " + thr.getClass().getSimpleName() + " with message " +
                            thr.getMessage() + ".");
                }
                assertTrue("After deleting key " + k + ", which was key #" +
                        keys.indexOf(k) + " in the list, the AVL-" + t.getMaxImbalance() + "" +
                        " tree was found to not obey either the BST and/or the AVL-G properties.",
                        ensureAVLGBST(t));

            });
            assertTrue("After explicitly deleting all keys from an AVL-" + t.getMaxImbalance() +
                    " tree, either the tree wasn't empty or its count was 0.", ensureTreeEmpty(t));
        });
    }

    @Test
    public void testBalancedInsertionsAVL1(){

        AVLGTree<Integer> tree = trees.get(0);

        // (1) A single left rotation about the root.
        insertAndTest(IntStream.rangeClosed(0, 2), tree,
                1, 3, 1);

        // (2) A single right rotation about the root.
        insertAndTest(IntStream.of(0, -1, -2), tree,
                1, 3, -1);

        // (3) A R-L rotation about the root.
        insertAndTest(IntStream.of(1, 3, 2), tree,
                1, 3, 2);

        // (4) A L-R rotation about the root.
        insertAndTest(IntStream.of(3, 1, 2), tree,
                1, 3, 2);

        // (5) A L-R rotation about the root's left child.
        insertAndTest(IntStream.of(5, -5, 10, -10, -8), tree,
                2, 5, 5);

        // (6) A R-L rotation about the root's left child.
        insertAndTest(IntStream.of(5, -5, 10, 0, -2), tree,
                2, 5, 5);

        // (7) A L-R rotation about the root's right child.
        insertAndTest(IntStream.of(5, 10, 0, 7, 8), tree,
                2, 5, 5);

        // (8) A R-L rotation about the root's right child
        insertAndTest(IntStream.of(5, 10, 0, 13, 12), tree,
                2, 5, 5);
    }

    @Test
    public void testBalancedInsertionsAVL2(){


    }

    @Test
    public void testBalancedInsertionsAVL3(){


    }


    @Test
    public void testBalancedDeletionsAVL1(){




    }

    @Test
    public void testBalancedDeletionsAVL2(){

    }

    @Test
    public void testBalancedDeletionsAVL3() {

    }
}
