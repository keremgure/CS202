package utilities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thedath Oudarya
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Thahzan Mohomed
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * https://github.com/thedathoudarya/WAGU-data-in-table-view
 */
public final class Table {
    
    private Board board;

    private final int tableWidth;

    private List<String> headersList;

    private List<List<String>> rowsList;

    private List<Integer> colWidthsList;

    private List<Integer> colAlignsList;

    private int headerHeight;

    private int rowHeight;

    private int gridMode;

    private Block initialTableBlock;

    private final static int GRID_NON = 13;

    private final static int GRID_FULL = 14;

    private final static int GRID_COLUMN = 15;

    Table(Board board, int tableWidth, List<String> headersList, List<List<String>> rowsList) {
        this.board = board;
        if (tableWidth <= 0) {
            throw new RuntimeException("Board width must be large than zero. " + tableWidth + " given.");
        } else {
            this.tableWidth = tableWidth;
        }
        if (headersList.size() <= 0) {
            throw new RuntimeException("Header size must be large than zero. " + headersList.size() + " found.");
        } else {
            this.headersList = headersList;
        }
        for (int i = 0; i < rowsList.size(); i++) {
            List<String> row = rowsList.get(i);
            if (row.size() != headersList.size()) {
                throw new RuntimeException("Size(" + row.size() + ") of the row(" + i + ") and header size(" + headersList.size() + ") are not equal");
            }
        }
        this.rowsList = rowsList;
        this.colWidthsList = new ArrayList<>();
        int avgWidthOfCol = (tableWidth - (gridMode == GRID_NON ? 0 : headersList.size() + 1)) / headersList.size();
        int availableForExtend = (tableWidth - (gridMode == GRID_NON ? 0 : headersList.size() + 1)) % headersList.size();
        for (int i = 0; i < headersList.size(); i++, availableForExtend--) {
            int finalWidth = avgWidthOfCol + (availableForExtend > 0 ? 1 : 0);
            this.colWidthsList.add(finalWidth);
        }
        this.colAlignsList = new ArrayList<>();
        headerHeight = 1;
        gridMode = GRID_COLUMN;

        if (rowsList.size() != 0) {
            List<String> firstRow = rowsList.get(0);
            for (String cell : firstRow) {
                int alignMode;
                try {
                    Long.parseLong(cell);
                    alignMode = Block.DATA_MIDDLE_RIGHT;
                } catch (NumberFormatException e0) {
                    try {
                        Integer.parseInt(cell);
                        alignMode = Block.DATA_MIDDLE_RIGHT;
                    } catch (NumberFormatException e1) {
                        try {
                            Double.parseDouble(cell);
                            alignMode = Block.DATA_MIDDLE_RIGHT;
                        } catch (NumberFormatException e2) {
                            alignMode = Block.DATA_MIDDLE_LEFT;
                        }
                    }
                }
                this.colAlignsList.add(alignMode);
            }
            rowHeight = 1;
        }
    }

    private Table(Board board, int tableWidth, List<String> headersList, List<List<String>> rowsList, List<Integer> colWidthsList) {
        this(board, tableWidth, headersList, rowsList);
        if (colWidthsList.size() != headersList.size()) {
            throw new RuntimeException("Column width count(" + colWidthsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colWidthsList = colWidthsList;
        }
    }

    public Table(Board board, int tableWidth, List<String> headersList, List<List<String>> rowsList, List<Integer> colWidthsList, List<Integer> colAlignsList) {
        this(board, tableWidth, headersList, rowsList, colWidthsList);
        if (colAlignsList.size() != headersList.size()) {
            throw new RuntimeException("Column align count(" + colAlignsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colAlignsList = colAlignsList;
        }
    }

    public List<String> getHeadersList() {
        return headersList;
    }

    public Table setHeadersList(List<String> headersList) {
        this.headersList = headersList;
        return this;
    }

    public List<List<String>> getRowsList() {
        return rowsList;
    }

    public Table setRowsList(List<List<String>> rowsList) {
        this.rowsList = rowsList;
        return this;
    }

    public List<Integer> getColWidthsList() {
        return colWidthsList;
    }

    public Table setColWidthsList(List<Integer> colWidthsList) {
        if (colWidthsList.size() != headersList.size()) {
            throw new RuntimeException("Column width count(" + colWidthsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colWidthsList = colWidthsList;
        }
        return this;
    }

    public List<Integer> getColAlignsList() {
        return colAlignsList;
    }

    Table setColAlignsList(List<Integer> colAlignsList) {
        if (colAlignsList.size() != headersList.size()) {
            throw new RuntimeException("Column align count(" + colAlignsList.size() + ") and header size(" + headersList.size() + ") are not equal");
        } else {
            this.colAlignsList = colAlignsList;
        }
        return this;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public Table setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
        return this;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public Table setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        return this;
    }

    private int getGridMode() {
        return gridMode;
    }

    public Table setGridMode(int gridMode) {
        if (gridMode == GRID_NON || gridMode == GRID_FULL || gridMode == GRID_COLUMN) {
            this.gridMode = gridMode;
        } else {
            throw new RuntimeException("Invalid grid mode. " + gridMode + " given.");
        }
        return this;
    }

    Block tableToBlocks() {
        for (int i = 0; i < headersList.size(); i++) {
            String headerValue = headersList.get(i);
            int columnWidth = colWidthsList.get(i);
            Block block = new Block(board, columnWidth, headerHeight, headerValue);
            if (getGridMode() == GRID_NON) {
                block.allowGrid(false);
            } else {
                block.allowGrid(true);
            }
            int alignIndex = colAlignsList.get(i);
            block.setDataAlign(alignIndex);
            if (initialTableBlock == null) {
                initialTableBlock = block;
            } else {
                initialTableBlock.getMostRightBlock().setRightBlock(block);
            }
        }
        if (getGridMode() != GRID_COLUMN) {
            for (int i = 0; i < rowsList.size(); i++) {
                List<String> row = rowsList.get(i);
                Block rowStartingBlock = initialTableBlock.getMostBelowBlock();
                for (int j = 0; j < row.size(); j++) {
                    String rowValue = row.get(j);
                    int columnWidth = colWidthsList.get(j);
                    Block block = new Block(board, columnWidth, rowHeight, rowValue);
                    if (getGridMode() == GRID_NON) {
                        block.allowGrid(false);
                    } else {
                        block.allowGrid(true);
                    }
                    int alignIndex = colAlignsList.get(j);
                    block.setDataAlign(alignIndex);

                    if (rowStartingBlock.getBelowBlock() == null) {
                        rowStartingBlock.setBelowBlock(block);
                    } else {
                        rowStartingBlock.getBelowBlock().getMostRightBlock().setRightBlock(block);
                    }
                }
            }
        } else {            
            for (int i = 0; i < headersList.size(); i++) {
                String columnData = "";
                for (int j = 0; j < rowsList.size(); j++) {
                    String rowData = rowsList.get(j).get(i);
                    columnData = columnData.concat(rowData).concat("\n");
                }
                Block block = new Block(board, colWidthsList.get(i), rowsList.size(),columnData);
                int alignIndex = colAlignsList.get(i);
                    block.setDataAlign(alignIndex);
                if (initialTableBlock.getBelowBlock() == null) {
                    initialTableBlock.setBelowBlock(block);
                } else {
                    initialTableBlock.getBelowBlock().getMostRightBlock().setRightBlock(block);
                }
            }
        }
        return initialTableBlock;
    }
    
    public Table invalidate(){
        initialTableBlock = null;
        return this;
    }
}
