package utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
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
public final class Block {

    private static final int BLOCK_LEFT = 1;
    private static final int BLOCK_CENTRE = 2;
    private static final int BLOCK_RIGHT = 3;
    private static final int DATA_TOP_LEFT = 4;
    private static final int DATA_TOP_MIDDLE = 5;
    private static final int DATA_TOP_RIGHT = 6;
    static final int DATA_MIDDLE_LEFT = 7;
    static final int DATA_CENTER = 8;
    static final int DATA_MIDDLE_RIGHT = 9;
    private static final int DATA_BOTTOM_LEFT = 10;
    private static final int DATA_BOTTOM_MIDDLE = 11;
    private static final int DATA_BOTTOM_RIGHT = 12;
    static int nextIndex;
    private final int index;
    private final Board board;
    private int width;
    private int height;
    private boolean allowGrid;
    private int blockAlign;
    private String data;
    private int dataAlign;
    private int x;

    private int y;

    private Block rightBlock;

    private Block belowBlock;

    private List<Charr> charrsList;

    private String preview;

    private Block(Board board, final int width, final int height) {
        this.board = board;
        if (width <= board.boardWidth) {
            this.width = width;
        } else {
            throw new RuntimeException("Block " + this.toString() + " exceeded the board width " + board.boardWidth);
        }
        this.height = height;
        allowGrid = true;
        blockAlign = Block.BLOCK_LEFT;
        data = null;
        dataAlign = Block.DATA_TOP_LEFT;
        x = 0;
        y = 0;
        rightBlock = null;
        belowBlock = null;
        charrsList = new ArrayList<>();
        preview = "";
        index = Block.nextIndex;
        nextIndex++;
    }

    Block(final Board board, final int width, final int height, final String data) {
        this(board, width, height);
        this.data = data;
    }

    public Block(final Board board, final int width, final int height, final String data, final Block rightBlock, final Block belowBlock) {
        this(board, width, height, data);
        if (rightBlock != null) {
            rightBlock.setX(this.getX() + this.getWidth() + (this.isGridAllowed() ? 1 : 0));
            rightBlock.setY(this.getY());
            this.rightBlock = rightBlock;
        }
        if (belowBlock != null) {
            belowBlock.setX(this.getX());
            belowBlock.setY(this.getY() + this.getHeight() + (this.isGridAllowed() ? 1 : 0));
            this.belowBlock = belowBlock;
        }
    }

    int getIndex() {
        return this.index;
    }

    private int getWidth() {
        return this.width;
    }

    public Block setWidth(final int width) {
        this.width = width;
        return this;
    }

    private int getHeight() {
        return this.height;
    }

    public Block setHeight(final int height) {
        this.height = height;
        return this;
    }

    private boolean isGridAllowed() {
        return this.allowGrid;
    }

    Block allowGrid(final boolean allowGrid) {
        this.allowGrid = allowGrid;
        return this;
    }

    private int getBlockAlign() {
        return this.blockAlign;
    }

    public Block setBlockAlign(final int blockAlign) {
        if (blockAlign == Block.BLOCK_LEFT || blockAlign == Block.BLOCK_CENTRE || blockAlign == Block.BLOCK_RIGHT) {
            this.blockAlign = blockAlign;
        } else {
            throw new RuntimeException("Invalid block align mode. " + this.dataAlign + " given.");
        }
        return this;
    }

    public String getData() {
        return this.data;
    }

    public Block setData(final String data) {
        this.data = data;
        return this;
    }

    private int getDataAlign() {
        return this.dataAlign;
    }

    Block setDataAlign(final int dataAlign) {
        if (dataAlign == Block.DATA_TOP_LEFT || dataAlign == Block.DATA_TOP_MIDDLE || dataAlign == Block.DATA_TOP_RIGHT
                || dataAlign == Block.DATA_MIDDLE_LEFT || dataAlign == Block.DATA_CENTER || dataAlign == Block.DATA_MIDDLE_RIGHT
                || dataAlign == Block.DATA_BOTTOM_LEFT || dataAlign == Block.DATA_BOTTOM_MIDDLE || dataAlign == Block.DATA_BOTTOM_RIGHT) {
            this.dataAlign = dataAlign;
        } else {
            throw new RuntimeException("Invalid data align mode. " + dataAlign + " given.");
        }
        return this;
    }

    private int getX() {
        return this.x;
    }

    private Block setX(final int x) {
        if (x + this.getWidth() + (this.isGridAllowed() ? 2 : 0) <= this.board.boardWidth) {
            this.x = x;
        } else {
            throw new RuntimeException("Block " + this + " exceeded the board width " + board.boardWidth);
        }
        return this;
    }

    private int getY() {
        return y;
    }

    private Block setY(int y) {
        this.y = y;
        return this;
    }

    Block getRightBlock() {
        return rightBlock;
    }

    Block setRightBlock(Block rightBlock) {
        if (rightBlock != null) {
            rightBlock.setX(getX() + getWidth() + (isGridAllowed() ? 1 : 0));
            rightBlock.setY(getY());
            this.rightBlock = rightBlock;
        }
        return this;
    }

    Block getBelowBlock() {
        return belowBlock;
    }

    Block setBelowBlock(Block belowBlock) {
        if (belowBlock != null) {
            belowBlock.setX(getX());
            belowBlock.setY(getY() + getHeight() + (isGridAllowed() ? 1 : 0));
            this.belowBlock = belowBlock;
        }
        return this;
    }

    Block invalidate() {
        charrsList = new ArrayList<>();
        preview = "";
        return this;
    }

    Block build() {
        if (charrsList.isEmpty()) {
            int ix = x;
            int iy = y;
            int blockLeftSideSpaces = -1;
            int additionalWidth = (isGridAllowed() ? 2 : 0);
            switch (getBlockAlign()) {
                case BLOCK_LEFT: {
                    blockLeftSideSpaces = 0;
                    break;
                }
                case BLOCK_CENTRE: {
                    blockLeftSideSpaces = (board.boardWidth - (ix + getWidth() + additionalWidth)) / 2 + (board.boardWidth - (ix + getWidth() + additionalWidth)) % 2;
                    break;
                }
                case BLOCK_RIGHT: {
                    blockLeftSideSpaces = board.boardWidth - (ix + getWidth() + additionalWidth);
                    break;
                }
            }
            ix += blockLeftSideSpaces;
            if (data == null) {
                data = toString();
            }
            String[] lines = data.split("\n");
            List<String> dataInLines = new ArrayList<>();
            if (board.showBlockIndex) {
                dataInLines.add("i = " + index);
            }
            for (String line : lines) {
                if (getHeight() > dataInLines.size()) {
                    dataInLines.add(line);
                } else {
                    break;
                }
            }
            for (int i = dataInLines.size(); i < getHeight(); i++) {
                dataInLines.add("");
            }
            for (int i = 0; i < dataInLines.size(); i++) {
                String dataLine = dataInLines.get(i);
                if (dataLine.length() > getWidth()) {
                    dataInLines.set(i, dataLine.substring(0, getWidth()));
                    if (i + 1 != dataInLines.size()) {
                        String prifix = dataLine.substring(getWidth());
                        String suffix = dataInLines.get(i + 1);
                        String combinedValue = prifix.concat((suffix.length() > 0 ? String.valueOf(Charr.S) : "")).concat(suffix);
                        dataInLines.set(i + 1, combinedValue);
                    }
                }
            }

            for (int i = 0; i < dataInLines.size(); i++) {
                if (dataInLines.remove("")) {
                    i--;
                }
            }

            int givenAlign = getDataAlign();
            int dataStartingLineIndex = -1;
            int additionalHeight = (isGridAllowed() ? 1 : 0);
            if (givenAlign == DATA_TOP_LEFT || givenAlign == Block.DATA_TOP_MIDDLE || givenAlign == DATA_TOP_RIGHT) {
                dataStartingLineIndex = iy + additionalHeight;
            } else if (givenAlign == DATA_MIDDLE_LEFT || givenAlign == DATA_CENTER || givenAlign == DATA_MIDDLE_RIGHT) {
                dataStartingLineIndex = iy + additionalHeight + ((getHeight() - dataInLines.size()) / 2 + (getHeight() - dataInLines.size()) % 2);
            } else if (givenAlign == DATA_BOTTOM_LEFT || givenAlign == DATA_BOTTOM_MIDDLE || givenAlign == DATA_BOTTOM_RIGHT) {
                dataStartingLineIndex = iy + additionalHeight + (getHeight() - dataInLines.size());
            }
            int dataEndingLineIndex = dataStartingLineIndex + dataInLines.size();

            int extendedIX = ix + getWidth() + (isGridAllowed() ? 2 : 0);
            int extendedIY = iy + getHeight() + (isGridAllowed() ? 2 : 0);
            int startingIX = ix;
            int startingIY = iy;
            for (; iy < extendedIY; iy++) {
                for (; ix < extendedIX; ix++) {
                    boolean writeData;
                    if (isGridAllowed()) {
                        if ((iy == startingIY) || (iy == extendedIY - 1)) {
                            if ((ix == startingIX) || (ix == extendedIX - 1)) {
                                charrsList.add(new Charr(ix, iy, Charr.P));
                                writeData = false;
                            } else {
                                charrsList.add(new Charr(ix, iy, Charr.D));
                                writeData = false;
                            }
                        } else {
                            if ((ix == startingIX) || (ix == extendedIX - 1)) {
                                charrsList.add(new Charr(ix, iy, Charr.VL));
                                writeData = false;
                            } else {
                                writeData = true;
                            }
                        }
                    } else {
                        writeData = true;
                    }
                    if (writeData && (iy >= dataStartingLineIndex && iy < dataEndingLineIndex)) {
                        int dataLineIndex = iy - dataStartingLineIndex;
                        String lineData = dataInLines.get(dataLineIndex);
                        if (!lineData.isEmpty()) {
                            int dataLeftSideSpaces = -1;
                            if (givenAlign == DATA_TOP_LEFT || givenAlign == DATA_MIDDLE_LEFT || givenAlign == DATA_BOTTOM_LEFT) {
                                dataLeftSideSpaces = 0;
                            } else if (givenAlign == DATA_TOP_MIDDLE || givenAlign == DATA_CENTER || givenAlign == DATA_BOTTOM_MIDDLE) {
                                dataLeftSideSpaces = (getWidth() - lineData.length()) / 2 + (getWidth() - lineData.length()) % 2;
                            } else if (givenAlign == DATA_TOP_RIGHT || givenAlign == DATA_MIDDLE_RIGHT || givenAlign == DATA_BOTTOM_RIGHT) {
                                dataLeftSideSpaces = getWidth() - lineData.length();
                            }
                            int dataStartingIndex = (startingIX + dataLeftSideSpaces + (isGridAllowed() ? 1 : 0));
                            int dataEndingIndex = (startingIX + dataLeftSideSpaces + lineData.length() - (isGridAllowed() ? 0 : 1));
                            if (ix >= dataStartingIndex && ix <= dataEndingIndex) {
                                char charData = lineData.charAt(ix - dataStartingIndex);
                                charrsList.add(new Charr(ix, iy, charData));
                            }
                        }
                    }
                }
                ix = startingIX;
            }
        }
        return this;
    }

    List<Charr> getChars() {
        return this.charrsList;
    }

    public String getPreview() {
        build();
        if (preview.isEmpty()) {
            int maxY = -1;
            int maxX = -1;
            for (Charr charr : charrsList) {
                int testY = charr.getY();
                int testX = charr.getX();
                if (maxY < testY) {
                    maxY = testY;
                }
                if (maxX < testX) {
                    maxX = testX;
                }
            }
            String[][] dataPoints = new String[maxY + 1][board.boardWidth];
            for (Charr charr : charrsList) {
                dataPoints[charr.getY()][charr.getX()] = String.valueOf(charr.getC());
            }

            for (String[] dataPoint : dataPoints) {
                for (String point : dataPoint) {
                    if (point == null) {
                        point = String.valueOf(Charr.S);
                    }
                    preview = preview.concat(point);
                }
                preview = preview.concat(String.valueOf(Charr.NL));
            }
        }
        return preview;
    }

    Block getMostRightBlock() {
        return getMostRightBlock(this);
    }

    private Block getMostRightBlock(Block block) {
        if (block.getRightBlock() == null) {
            return block;
        } else {
            return getMostRightBlock(block.getRightBlock());
        }
    }

    Block getMostBelowBlock() {
        return getMostBelowBlock(this);
    }

    private Block getMostBelowBlock(Block block) {
        if (block.getBelowBlock() == null) {
            return block;
        } else {
            return getMostBelowBlock(block.getBelowBlock());
        }
    }

    @Override
    public String toString() {
        return index + " = [" + x + "," + y + "," + width + "," + height + "]";
    }

    @Override
    public boolean equals(Object block) {
        if (block == null) {
            return false;
        }
        if (!(block instanceof Block)) {
            return false;
        }
        Block b = (Block) block;
        return b.getIndex() == getIndex() && b.getX() == getX() && b.getY() == getY();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + this.index;
        hash = 43 * hash + this.width;
        hash = 43 * hash + this.height;
        hash = 43 * hash + (this.allowGrid ? 1 : 0);
        hash = 43 * hash + this.blockAlign;
        hash = 43 * hash + Objects.hashCode(this.data);
        hash = 43 * hash + this.dataAlign;
        hash = 43 * hash + this.x;
        hash = 43 * hash + this.y;
        hash = 43 * hash + Objects.hashCode(this.rightBlock);
        hash = 43 * hash + Objects.hashCode(this.belowBlock);
        return hash;
    }
}
