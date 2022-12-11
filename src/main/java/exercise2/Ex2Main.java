package exercise2;

import de.hpi.dbs2.exercise2.*;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class Ex2Main {
    public static void main(String[] args) {
        int order = 4;

        BPlusTreeNode<?> root = BPlusTreeNode.buildTree(order,
                new AbstractBPlusTree.Entry[][]{
                        new AbstractBPlusTree.Entry[]{
                                new AbstractBPlusTree.Entry(2, new ValueReference(0)),
                                new AbstractBPlusTree.Entry(3, new ValueReference(1)),
                                new AbstractBPlusTree.Entry(5, new ValueReference(2))
                        },
                        new AbstractBPlusTree.Entry[]{
                                new AbstractBPlusTree.Entry(7, new ValueReference(3)),
                                new AbstractBPlusTree.Entry(11, new ValueReference(4))
                        }
                },
                new AbstractBPlusTree.Entry[][]{
                        new AbstractBPlusTree.Entry[]{
                                new AbstractBPlusTree.Entry(13, new ValueReference(5)),
                                new AbstractBPlusTree.Entry(17, new ValueReference(6)),
                                new AbstractBPlusTree.Entry(19, new ValueReference(7))
                        },
                        new AbstractBPlusTree.Entry[]{
                                new AbstractBPlusTree.Entry(23, new ValueReference(8)),
                                new AbstractBPlusTree.Entry(29, new ValueReference(9))
                        },
                        new AbstractBPlusTree.Entry[]{
                                new AbstractBPlusTree.Entry(31, new ValueReference(10)),
                                new AbstractBPlusTree.Entry(37, new ValueReference(11)),
                                new AbstractBPlusTree.Entry(41, new ValueReference(12))
                        },
                        new AbstractBPlusTree.Entry[]{
                                new AbstractBPlusTree.Entry(43, new ValueReference(13)),
                                new AbstractBPlusTree.Entry(47, new ValueReference(14))
                        }
                }
        );
        //System.out.println("root: " + root);

        AbstractBPlusTree tree = new BPlusTreeJava(root);
        //System.out.println("tree: " + tree);

        LeafNode leafNode = new LeafNode(4);
        //System.out.println("leaf_node: " + leafNode);
        InnerNode innerNode = new InnerNode(4);
        //System.out.println("inner_node: " + innerNode);

        ValueReference value = new ValueReference(15);
        int key = 40;
        insert(key, value, root);
        System.out.println("root: " + root);
        insert(1, new ValueReference(16), root);

        insert(4, new ValueReference(17), root);

        insert(6, new ValueReference(18), root);

        insert(8, new ValueReference(19), root);

        insert(9, new ValueReference(20), root);

        insert(10, new ValueReference(18), root);

        insert(12, new ValueReference(18), root);

        insert(13, new ValueReference(18), root);

        insert(14, new ValueReference(18), root);

        insert(15, new ValueReference(18), root);
        System.out.println("15: " + root);
        insert(16, new ValueReference(18), root);
        System.out.println("16: " + root);
        System.out.println(root.isValid());
        root.isValid();


    }

    public static ValueReference insert(@NotNull Integer key, @NotNull ValueReference value, @NotNull BPlusTreeNode<?> root) {
        //Get the path to the leaf node and the leaf node#
        Stack<BPlusTreeNode<?>> path = findPath(root, key);
        Stack<BPlusTreeNode<?>> path_clone = (Stack<BPlusTreeNode<?>>) path.clone();
        LeafNode leafNode = (LeafNode) path_clone.pop();
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

    public static Stack<BPlusTreeNode<?>> findPath(BPlusTreeNode<?> root, int key) {
        Stack<BPlusTreeNode<?>> path = new Stack<>();
        path.push(root);
        BPlusTreeNode<?> node = root;
        while (node.getHeight() > 0) {
            node = ((InnerNode) node).selectChild(key);
            path.push(node);
        }
        return path;
    }

    public static ValueReference update_leaf_node(LeafNode leafNode, int key, ValueReference value) {
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

    public static void insert_into_btree(Stack<BPlusTreeNode<?>> path, int key, ValueReference value, BPlusTreeNode<?> root) {
        BPlusTreeNode<?> new_node = null;
        int new_key = key;
        Map<String, Object> return_result = null;
        //We go from the leaf to the root
        while(!path.isEmpty()){
            BPlusTreeNode<?> node = path.pop();
            //If the node is full we have to split it
            if(node.isFull()) {
                //We need to distinguish between inner and leaf nodes
                if (node instanceof LeafNode) {
                    return_result = split_node((LeafNode) node, key, value);
                } else {
                    return_result = split_node((InnerNode) node, new_node, new_key);
                }
                new_node = (BPlusTreeNode<?>) return_result.get("new_node");
                new_key = (int) return_result.get("key");
                if(node == root){
                    //If we are at the root we have to create a new root
                    InnerNode new_root = new InnerNode(node.order);
                    new_root.keys[0] = new_key;
                    new_root.references[0] = node;
                    new_root.references[1] = new_node;
                    root = new_root;
                    System.out.println("new root: " + root);
                }
            }
            //We know we have to split the leaf node, otherwise we could have inserted the key
            else {
                if (node instanceof LeafNode){
                    insert_with_space((LeafNode) node, key, value);
                    return;
                }
                else {
                    insert_with_space((InnerNode) node, new_node);
                    return;
                }
            }
        }
    }

    public static void insert_with_space(InnerNode node, BPlusTreeNode<?> new_node) {
        Integer[] keys = node.keys;
        BPlusTreeNode<?>[] children = node.references;
        int new_key = new_node.getSmallestKey();

        put_new_value_in_node(keys, children, node.getNodeSize(), new_key, new_node);
    }

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

    public static void insert_with_space(LeafNode leafNode, int key, ValueReference value) {
        Integer[] keys = leafNode.keys;
        ValueReference[] values = leafNode.references;

        put_new_value_in_node(keys, values, leafNode.getNodeSize(), key, value);
    }

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

    public static Map<String, Object>  split_node(LeafNode node, int key, ValueReference value){
        //We create a new LeafNode and calculate, where we have to split the node
        int size = node.getNodeSize();
        int middle = (int) ((size +1) / 2);
        LeafNode new_node = new LeafNode(node.order);
        Map<String, Object> return_result = new HashMap<>();

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

    public static Map<String, Object> split_node(InnerNode node, BPlusTreeNode<?> new_child, int key) {
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
}

