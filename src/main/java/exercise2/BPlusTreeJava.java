package exercise2;

import de.hpi.dbs2.ChosenImplementation;
import de.hpi.dbs2.exercise2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * This is the B+-Tree implementation you will work on.
 * Your task is to implement the insert-operation.
 *
 */
@ChosenImplementation(true)
public class BPlusTreeJava extends AbstractBPlusTree {
    public BPlusTreeJava(int order) {
        super(order);
    }

    public BPlusTreeJava(BPlusTreeNode<?> rootNode) {
        super(rootNode);
    }

    @Nullable
    @Override
    //The main insert function
    public ValueReference insert(@NotNull Integer key, @NotNull ValueReference value) {
        BPlusTreeNode<?> root = this.getRootNode();
        //Get the path to the leaf node and the leaf node#
        Stack<BPlusTreeNode<?>> path = findPath(root, key);
        LeafNode leafNode = (LeafNode) path.peek();
        ValueReference result = update_leaf_node(leafNode, key, value);
        //If the result node is not null, then the key already exists in the tree, and we have replaced it
        if (result != null) {
            return result;
        }
        //Otherwise we put the key in the leaf node
        else {
            insert_into_btree(path, key, value, root);
            return null;
        }
    }

    //This function takes the root and a key and returns a stack with the path to the leaf node that contains the key
    public Stack<BPlusTreeNode<?>> findPath(BPlusTreeNode<?> root, int key) {
        Stack<BPlusTreeNode<?>> path = new Stack<>();
        path.push(root);
        BPlusTreeNode<?> node = root;
        while (node.getHeight() > 0) {
            node = ((InnerNode) node).selectChild(key);
            path.push(node);
        }
        return path;
    }

    //This function takes a leaf node and a key, value pair and updates the leaf node, if the key already exists.
    //Otherwise, it returns null
    public ValueReference update_leaf_node(LeafNode leafNode, int key, ValueReference value) {
        ValueReference old_value = null;
        Integer[] keys = leafNode.keys;
        ValueReference[] values = leafNode.references;
        int size = leafNode.getNodeSize();
        //Go through all keys and find the right one to replace
        for (int i = 0; i < size; i++) {
            if (keys[i] == key) {
                old_value = values[i];
                values[i] = value;
            }
        }
        return old_value;
    }

    //This function takes a path, a key, value pair and the root node and inserts the key, value pair into the tree
    //It goes from the leaf to the root and splits nodes if necessary
    public void insert_into_btree(Stack<BPlusTreeNode<?>> path, int key, ValueReference value, BPlusTreeNode<?> root) {
        Object new_value = value;
        int new_key = key;
        Map<String, Object> return_result = null;
        //We go from the leaf to the root
        while(!path.isEmpty()){
            BPlusTreeNode<?> node = path.pop();
            //If the node is full we have to split it
            if(node.isFull()) {
                //We need to distinguish between inner and leaf nodes
                if (node instanceof LeafNode) {
                    return_result = split_node((LeafNode) node, new_key, (ValueReference) new_value);
                } else {
                    return_result = split_node((InnerNode) node, new_key, (BPlusTreeNode<?>) new_value);
                }
                new_value = (BPlusTreeNode<?>) return_result.get("new_node");
                new_key = (int) return_result.get("key");
                if(node == root){
                    //If we are at the root we have to create a new root
                    InnerNode new_root = new InnerNode(node.order);
                    new_root.keys[0] = new_key;
                    //If the node was a InitialRootNode we have to delete it and create a new leaf node for that nood
                    if(node instanceof InitialRootNode){
                        LeafNode new_leaf = new LeafNode(node.order);
                        for (int i = 0; i < node.getNodeSize(); i++) {
                            new_leaf.keys[i] = ((InitialRootNode) node).keys[i];
                            new_leaf.references[i] = ((InitialRootNode) node).references[i];
                            new_leaf.nextSibling = ((InitialRootNode) node).nextSibling;
                        }
                        new_root.references[0] = new_leaf;
                    }
                    else{
                        new_root.references[0] = node;
                    }

                    new_root.references[1] = (BPlusTreeNode<?>) new_value;
                    this.rootNode = new_root;
                }
            }
            //We know we have to split the leaf node, otherwise we could have inserted the key
            else {
                insert_with_space(node, new_key, new_value);
                return;
            }
        }
    }

    //This function takes a node that is not full and inserts a key, value pair into it
    public static void insert_with_space(BPlusTreeNode<?> node, int key, Object value) {
        Integer[] keys = node.keys;
        if(node instanceof LeafNode){
            ValueReference[] values = ((LeafNode) node).references;
            put_new_value_in_node(keys, values, node.getNodeSize(), key, (ValueReference) value);
        }
        else{
            BPlusTreeNode<?>[] children = ((InnerNode) node).references;
            put_new_value_in_node(keys, children, node.getNodeSize(), key, (BPlusTreeNode<?>) value);
        }
    }

    //This function takes a node, a key and a value and inserts the key, value pair into right place for the node
    //It returns the new node and the new key
    public static Map<String, Object> put_new_value_in_node(Integer[] keys, BPlusTreeNode<?>[] values, int size, int key, BPlusTreeNode<?> value) {
        BPlusTreeNode<?> old_value = value;
        int old_key = key;
        //We go through all keys and find the right place to insert the new key, this we know by comparing the new key with the current key and checking if there are keys left
        //The difference is since we are not in a LeafNode, we have an extra pointer
        for (int i = 0; i <= size; i++) {
            if (keys[i] == null) {
                keys[i] = old_key;
                values[i+1] = old_value;
                break;
            }
            else if (keys[i] > old_key) {
                int temp_key = keys[i];
                BPlusTreeNode<?> temp_value = values[i+1];
                keys[i] = old_key;
                values[i+1] = old_value;
                old_key = temp_key;
                old_value = temp_value;
            }
        }
        //We return the new key and the new node, so we can insert it into the parent node
        Map<String, Object> result = new HashMap<>();
        result.put("keys", keys);
        result.put("values", values);
        return result;
    }

    //This function takes a node, a key and a value and inserts the key, value pair into right place for the node
    //It returns the new node and the new key
    public static Map<String, Object> put_new_value_in_node(Integer[] keys, ValueReference[] values, int size, int key, ValueReference value) {
        ValueReference old_value = value;
        int old_key = key;
        //We go through all keys and find the right place to insert the new key, this we know by comparing the new key with the current key and checking if there are keys left
        for (int i = 0; i <= size; i++) {
            if (keys[i] == null) {
                keys[i] = old_key;
                values[i] = old_value;
                break;
            }
            else if (keys[i] > old_key) {
                int temp_key = keys[i];
                ValueReference temp_value = values[i];
                keys[i] = old_key;
                values[i] = old_value;
                old_key = temp_key;
                old_value = temp_value;
            }
        }
        //We return the new key and the new node, so we can insert it into the parent node
        Map<String, Object> result = new HashMap<>();
        result.put("keys", keys);
        result.put("values", values);
        return result;
    }

    //This function splits a node and returns the new node and the new key
    public static Map<String, Object>  split_node(LeafNode node, int key, ValueReference value){
        //We create a new LeafNode and calculate, where we have to split the node
        int size = node.getNodeSize();
        int middle = (int) ((size +1) / 2);
        LeafNode new_node = new LeafNode(node.order);
        Map<String, Object> return_result = new HashMap<>();

        //TODO: ROOT NODE REPLACING

        Integer[] keys = node.keys;
        ValueReference[] values = node.references;

        //We put the new key and value in our Arrays
        Map<String, Object> result = put_new_value_in_node(Arrays.copyOf(keys, keys.length+1), Arrays.copyOf(values, values.length+1), size, key, value);
        Integer[] new_keys = (Integer[]) result.get("keys");
        ValueReference[] new_values = (ValueReference[]) result.get("values");

        //First we copy the higher keys into the new nodes, then the lower in the old one
        for (int i = middle; i <= size; i++) {
            new_node.keys[i - middle] = new_keys[i];
            new_node.references[i - middle] = new_values[i];
            new_keys[i] = null;
            new_values[i] = null;
        }
        for (int i = 0; i < size; i++){
            keys[i] = new_keys[i];
            values[i] = new_values[i];
        }

        //We set the last Pointer of the nodes, so they are linked
        new_node.nextSibling = node.nextSibling;
        node.nextSibling = new_node;
        return_result.put("new_node", new_node);
        return_result.put("key", new_node.getSmallestKey());

        return return_result;
    }

//This function splits a node and returns the new node and the new key, since we are splitting an InnerNode, we have to exclude one key from the new node and return it
    public static Map<String, Object> split_node(InnerNode node, int key, BPlusTreeNode<?> new_child) {
        //We create a new InnerNode and calculate, where we have to split the node
        int size = node.getNodeSize();
        int middle = (int) ((size +1) / 2);
        InnerNode new_node = new InnerNode(node.order);
        Map<String, Object> return_result = new HashMap<>();

        Integer[] keys = node.keys;
        BPlusTreeNode<?>[] values = node.references;

        //We put the new key and value in our Arrays
        Map<String, Object> result = put_new_value_in_node(Arrays.copyOf(keys, keys.length+1), Arrays.copyOf(values, values.length+1), size, key, new_child);
        Integer[] new_keys = (Integer[]) result.get("keys");
        BPlusTreeNode<?>[] new_values = (BPlusTreeNode<?>[]) result.get("values");

        //First we copy the higher keys into the new nodes, then the lower in the old one
        //Here we exclude the first key of the second node, since it will go in the parent node
        for (int i = middle; i < size; i++) {
            if (i != middle){
                new_node.keys[i - middle -1] = new_keys[i];
            }
            else{
                return_result.put("key", new_keys[i]);
            }
            new_node.references[i - middle] = new_values[i+1];
            new_keys[i] = null;
            new_values[i+1] = null;
        }
        //Since we are in a InnerNode, we have one Pointer more than keys
        for (int i = 0; i < size; i++){
            if (i != size-1){
                keys[i] = new_keys[i];
            }
            values[i] = new_values[i];
        }

        //We return the new_node and key, so we can insert it into the parent node
        return_result.put("new_node", new_node);
        return return_result;
    }


        // Find LeafNode in which the key has to be inserted.
        //   It is a good idea to track the "path" to the LeafNode in a Stack or something alike.
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

        // Check out the exercise slides for a flow chart of this logic.
        // If you feel stuck, try to draw what you want to do and
        // check out Ex2Main for playing around with the tree by e.g. printing or debugging it.
        // Also check out all the methods on BPlusTreeNode and how they are implemented or
        // the tests in BPlusTreeNodeTests and BPlusTreeTests!
}
