package de.hpi.dbs2.exercise2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class lays the foundation for your BPlusTree implementation.
 * For this exercise, we use [Integer] as keys and [ValueReference] as value.
 *
 * <p>We have already implemented some features for you.
 */
public abstract class AbstractBPlusTree implements Index<Integer, ValueReference> {
    /**
     * Find the value mapped to the given key or null, if the key is not in the index.
     * @return associated value for the given key, if existing
     */
    @Nullable
    @Override
    public abstract ValueReference getOrNull(@NotNull Integer searchKey);

    /**
     * Find the value mapped to the given key.
     * @throws NoSuchElementException If the key is not in the index.
     * @return associated value for the given key
     */
    @NotNull
    @Override
    public ValueReference get(@NotNull Integer searchKey) {
        ValueReference value = getOrNull(searchKey);
        if (value == null) {
            throw new NoSuchElementException("Key not found in index: $searchKey");
        }
        return value;
    }

    /**
     * Insert a new key-value mapping into the index, replacing the old value for existing keys.
     * @return previously associated value for the given key, if existing
     */
    @Nullable
    @Override
    public abstract ValueReference insert(@NotNull Integer key, @NotNull ValueReference value);

    /**
     * Remove a key-value mapping from the index.
     * @return previously associated value for the given key, if existing
     */
    @Override
    public ValueReference remove(@NotNull Integer key) {
        throw new UnsupportedOperationException("You don't have to implement this :)");
    }

    /**
     * Finds all values for the keys between the given bounds.
     * @return an iterator over all found values
     *         will be empty if lowerBound > upperBound
     *         will contain only a single value if lowerBound = upperBound and the key maps to a value
     */
    @NotNull
    @Override
    public Iterator<ValueReference> getRange(@NotNull Integer lowerBound, @NotNull Integer upperBound) {
        throw new UnsupportedOperationException("You don't have to implement this :)");
    }

    public static class Entry extends IndexEntry<Integer, ValueReference> {
        public Entry(@NotNull Integer key, @NotNull ValueReference value) {
            super(key, value);
        }
    }

    public abstract boolean isValid();
}
