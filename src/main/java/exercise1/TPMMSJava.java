package exercise1;

import de.hpi.dbs2.ChosenImplementation;
import de.hpi.dbs2.dbms.*;

import de.hpi.dbs2.dbms.utils.BlockSorter;
import de.hpi.dbs2.exercise1.SortOperation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@ChosenImplementation(true)
public class TPMMSJava extends SortOperation {
    public TPMMSJava(@NotNull BlockManager manager, int sortColumnIndex) {
        super(manager, sortColumnIndex);
    }

    @Override
    public int estimatedIOCost(@NotNull Relation relation) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<List<Block>> phase1(Relation relation, BlockManager manager, Iterator<Block> blocks, Comparator<Tuple> comparator, int memoryBlocks) {

        List<List<Block>> sortedBlocks = new ArrayList<>();
        List<Block> loadedBlocks = new ArrayList<>();

        while(blocks.hasNext()) {
            for (int i = 0; i < memoryBlocks; i++) {
                loadedBlocks.add(manager.load(blocks.next()));
            }
            BlockSorter.INSTANCE.sort(relation, loadedBlocks, comparator);
            List<Block> sortBlock = new ArrayList<>();
            for (Block block : loadedBlocks) {
                sortBlock.add(block);
            }
            sortedBlocks.add(sortBlock);

            for (Block block : loadedBlocks) {
                manager.release(block, true);
            }
            loadedBlocks.clear();
        }
        System.out.println("Phase 1 finished");
        return sortedBlocks;
    }

    public Tuple getHead( List<List<Block>> pre_sorted_blocks, List<Integer> TuplePointer, List<Integer> BlockPointer, int i){
        return pre_sorted_blocks.get(i).get(BlockPointer.get(i)).get(TuplePointer.get(i));
    }

    public Block getBlock( List<List<Block>> pre_sorted_blocks,  List<Integer> BlockPointer, int i){
        return pre_sorted_blocks.get(i).get(BlockPointer.get(i));
    }


    public void phase2(BlockManager manager, List<List<Block>> pre_sorted_blocks, @NotNull BlockOutput output, Comparator<Tuple> comparator) {

        List<Integer> TuplePointer = new ArrayList<>();
        List<Integer> BlockPointer = new ArrayList<>();
        List<Integer> finished = new ArrayList<>();
        Block outputBlock = manager.allocate(true);

        for (List<Block> blocks : pre_sorted_blocks) {
            manager.load(blocks.get(0));
            TuplePointer.add(0);
            BlockPointer.add(0);
        }
        while(true){
            int minList = 0;
            for (int i = 0; i < pre_sorted_blocks.size(); i++) {
                Block block = getBlock(pre_sorted_blocks, BlockPointer, i);
                if (block.getCapacity() == TuplePointer.get(i)) {
                    if (BlockPointer.get(i) + 1 < pre_sorted_blocks.get(i).size()) {
                        manager.release(block, false);
                        block = manager.load(pre_sorted_blocks.get(i).get(BlockPointer.get(i) + 1));
                        BlockPointer.set(i, BlockPointer.get(i) + 1);
                        TuplePointer.set(i, 0);
                    }
                    else if(finished.contains(i)){
                        continue;
                    }
                    else {
                        manager.release(block, false);
                        finished.add(i);
                        continue;
                    }
                }

                Tuple head = block.get(TuplePointer.get(i));
                if(finished.contains(minList)){
                    minList = i;
                }
                else if (comparator.compare(head, getHead(pre_sorted_blocks, TuplePointer, BlockPointer, minList)) < 0) {
                    minList = i;
                }
            }
            if(finished.size() == pre_sorted_blocks.size()){
                if(outputBlock.isEmpty()){
                    manager.release(outputBlock, false);
                }
                else{
                    output.output(outputBlock);
                    manager.release(outputBlock, false);
                }
                break;
            }

            outputBlock.append(getHead(pre_sorted_blocks, TuplePointer, BlockPointer, minList));
            TuplePointer.set(minList, TuplePointer.get(minList) + 1);

            if (outputBlock.isFull()) {
                output.output(outputBlock);
                outputBlock.clear();
            }
    }
        System.out.println("Phase 2 finished");

    }
    @Override
    public void sort(@NotNull Relation relation, @NotNull BlockOutput output) {
        BlockManager manager = super.getBlockManager();
        Iterator<Block> blocks = relation.iterator();
        int columnIndex = super.getSortColumnIndex();
        Comparator<Tuple> comparator = relation.getColumns().getColumnComparator(columnIndex);

        int memoryBlocks = manager.getFreeBlocks();
        int relationSize = relation.getEstimatedSize();

        if (relationSize > memoryBlocks * memoryBlocks) {
            throw new RelationSizeExceedsCapacityException();
        }

        List<List<Block>> pre_sorted_blocks = phase1(relation, manager, blocks, comparator, memoryBlocks);
        phase2(manager, pre_sorted_blocks, output, comparator);
    }
}
