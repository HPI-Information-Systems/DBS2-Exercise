package exercise2;

import com.google.common.base.Preconditions;
import de.hpi.dbs2.exercise2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is the B+-Tree implementation you will work on.
 * Your task is to implement the insert-operation.
 *
 */
public class BPlusTreeJava extends AbstractBPlusTree {
    public final int order;
    private BPlusTreeNode<?> rootNode;

    /**
     * An empty B+-Tree starts with a root node, which is a specialized LeafNode.
     * The InitialRootNode will be replaced with a InnerNode
     * once there are enough entries in the tree.
     */
    public BPlusTreeJava(int order) {
        this.order = order;
        rootNode = new InitialRootNode(order);
    }

    /**
     * Uses an existing tree structure which must be valid.
     * @param rootNode
     */
    public BPlusTreeJava(BPlusTreeNode<?> rootNode) {
        this.rootNode = rootNode;
        this.order = rootNode.order;
        // check that all nodes in the given tree have the same order
        Preconditions.checkState(isValid());
    }

    public BPlusTreeNode<?> getRootNode() {
        return rootNode;
    }

    @Nullable
    @Override
    public ValueReference getOrNull(@NotNull Integer searchKey) {
        return rootNode.getOrNull(searchKey);
    }

    @Nullable
    @Override
    public ValueReference insert(@NotNull Integer key, @NotNull ValueReference value) {
        // Find LeafNode in which the key has to be inserted.
        // Does the key already exist? Overwrite!
        //   leafNode.references[pos] = value;
        //   But remember return the old value!
        // New key - Is there still space?
        //   leafNode.keys[pos] = key;
        //   leafNode.references[pos] = value;
        //   Don't forget to update the parent keys and so on...
        // Otherwise
        //   Split the LeafNode in two!
        //   Is parent node root?
        //     update rootNode = ... // will have only one key
        //   Was node instanceof LeafNode?
        //     update parentNode.keys[?] = ...
        //   Don't forget to update the parent keys and so on...

        // If you feel stuck, try to draw what you want to do and
        // check out Ex2Main for playing around with the tree by e.g. printing or debugging it.
        // Also check out all the methods on BPlusTreeNode and how they are implemented or
        // the tests in BPlusTreeNodeTests and BPlusTreeTests!

        return null; // inserted new mapping, no old value has been overwritten
    }

    @Override
    public boolean isValid() {
        return rootNode.isValid() && rootNode.order == order;
    }

    @Override
    public String toString() {
        return rootNode.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BPlusTreeJava that)) return false;
        return this.rootNode.equals(that.rootNode);
    }
}
