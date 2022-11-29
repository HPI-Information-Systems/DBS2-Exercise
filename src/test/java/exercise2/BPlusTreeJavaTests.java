package exercise2;

import de.hpi.dbs2.exercise2.AbstractBPlusTree;
import de.hpi.dbs2.exercise2.BPlusTreeNode;
import de.hpi.dbs2.exercise2.ValueReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class BPlusTreeJavaTests {
    @Test
    public void testTreeInsertRandomSet() {
        BPlusTreeJava tree = new BPlusTreeJava(4);

        Random random = new Random(1);

        int entryCount = 15;
        List<Integer> entries = new ArrayList<>(
            IntStream.range(0, entryCount).boxed().toList()
        );
        Collections.shuffle(entries, random);

        for (int i = 0; i < entries.size(); i++) {
            tree.insert(entries.get(i), new ValueReference(i));
            Assertions.assertTrue(tree.isValid());
        }

        Assertions.assertEquals(entryCount, tree.getRootNode().getEntries().count());
    }

    @Test
    public void testTreeInsert() {
        AbstractBPlusTree expectedTree = new BPlusTreeJava(BPlusTreeNode.buildTree(4,
            (Object[]) new AbstractBPlusTree.Entry[][]{
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(1, new ValueReference(6)),
                    new AbstractBPlusTree.Entry(2, new ValueReference(1)),
                    new AbstractBPlusTree.Entry(3, new ValueReference(4))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(4, new ValueReference(3)),
                    new AbstractBPlusTree.Entry(7, new ValueReference(2))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(8, new ValueReference(5)),
                    new AbstractBPlusTree.Entry(9, new ValueReference(7))
                }
            }
        ));

        AbstractBPlusTree tree = new BPlusTreeJava(4);

        tree.insert(2, new ValueReference(1));
        Assertions.assertTrue(tree.isValid());

        tree.insert(7, new ValueReference(2));
        Assertions.assertTrue(tree.isValid());

        tree.insert(4, new ValueReference(3));
        Assertions.assertTrue(tree.isValid());

        tree.insert(3, new ValueReference(4));
        Assertions.assertTrue(tree.isValid());

        tree.insert(8, new ValueReference(5));
        Assertions.assertTrue(tree.isValid());

        tree.insert(1, new ValueReference(6));
        Assertions.assertTrue(tree.isValid());

        tree.insert(9, new ValueReference(7));
        Assertions.assertTrue(tree.isValid());

        Assertions.assertEquals(expectedTree, tree);
    }
}
