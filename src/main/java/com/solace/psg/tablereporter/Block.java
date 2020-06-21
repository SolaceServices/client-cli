/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Thahzan Mohomed
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.solace.psg.tablereporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to handle table block. This implementation is base on Wagu library.
 * 
 * @author Victor Tsonkov
 */
public final class Block
{

	protected static int nextIndex = 0;

	private Board board;

	private final int index;

	private int width;

	private int height;

	private boolean allowGrid;

	private int blockAlign;

	public static final int BLOCK_LEFT = 1;

	public static final int BLOCK_CENTRE = 2;

	public static final int BLOCK_RIGHT = 3;

	private String data;

	private int dataAlign;

	public static final int DATA_TOP_LEFT = 4;

	public static final int DATA_TOP_MIDDLE = 5;

	public static final int DATA_TOP_RIGHT = 6;

	public static final int DATA_MIDDLE_LEFT = 7;

	public static final int DATA_CENTER = 8;

	public static final int DATA_MIDDLE_RIGHT = 9;

	public static final int DATA_BOTTOM_LEFT = 10;

	public static final int DATA_BOTTOM_MIDDLE = 11;

	public static final int DATA_BOTTOM_RIGHT = 12;

	private int x;

	private int y;

	private Block rightBlock;

	private Block belowBlock;

	private List<CharMap> charrsList;

	private String preview;

	/**
	 * Initialises a new instance of the class.
	 * 
	 * @param board  The board
	 * @param width  The width
	 * @param height The height
	 */
	public Block(Board board, int width, int height)
	{
		this.board = board;
		if (width <= board.boardWidth)
		{
			this.width = width;
		}
		else
		{
			throw new RuntimeException("Block " + toString() + " exceeded the board width " + board.boardWidth);
		}
		this.height = height;
		this.allowGrid = true;
		this.blockAlign = BLOCK_LEFT;
		this.data = null;
		this.dataAlign = DATA_TOP_LEFT;
		this.x = 0;
		this.y = 0;
		this.rightBlock = null;
		this.belowBlock = null;
		this.charrsList = new ArrayList<>();
		this.preview = "";
		this.index = nextIndex;
		Block.nextIndex++;
	}

	/**
	 * Initialises a new instance of the class.
	 * 
	 * @param board  The board
	 * @param width  The width
	 * @param height The height
	 * @param data   Data in the block.
	 */
	public Block(Board board, int width, int height, String data)
	{
		this(board, width, height);
		this.data = data;
	}

	/**
	 * Initialises a new instance of the class.
	 * 
	 * @param board      The board
	 * @param width      The width
	 * @param height     The height
	 * @param data       Data in the block.
	 * @param rightBlock right block
	 * @param belowBlock left block
	 */
	public Block(Board board, int width, int height, String data, Block rightBlock, Block belowBlock)
	{
		this(board, width, height, data);
		if (rightBlock != null)
		{
			rightBlock.setX(getX() + getWidth() + (isGridAllowed() ? 1 : 0));
			rightBlock.setY(getY());
			this.rightBlock = rightBlock;
		}
		if (belowBlock != null)
		{
			belowBlock.setX(getX());
			belowBlock.setY(getY() + getHeight() + (isGridAllowed() ? 1 : 0));
			this.belowBlock = belowBlock;
		}
	}

	/**
	 * Gets the index.
	 * 
	 * @return The index.
	 */
	protected int getIndex()
	{
		return index;
	}

	/**
	 * Gets the width.
	 * 
	 * @return The width.
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width The width.
	 * @return the parent block.
	 */
	public Block setWidth(int width)
	{
		this.width = width;
		return this;
	}

	/**
	 * gets the height.
	 * 
	 * @return The height.
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Sets the heigth.
	 * 
	 * @param height the height
	 * @return The parent block.
	 */
	public Block setHeight(int height)
	{
		this.height = height;
		return this;
	}

	/**
	 * If grid is allowed.
	 * @return true or false.
	 */
	public boolean isGridAllowed()
	{
		return allowGrid;
	}

	/**
	 * Allow grid.
	 * @param allowGrid true or false.
	 * @return the parent block.
	 */
	public Block allowGrid(boolean allowGrid)
	{
		this.allowGrid = allowGrid;
		return this;
	}

	/**
	 * Gets the block align.
	 * @return the align index.
	 */
	public int getBlockAlign()
	{
		return blockAlign;
	}

	/**
	 * Sets the block align,
	 * @param blockAlign The index
	 * @return the parent block
	 */
	public Block setBlockAlign(int blockAlign)
	{
		if (blockAlign == BLOCK_LEFT || blockAlign == BLOCK_CENTRE || blockAlign == BLOCK_RIGHT)
		{
			this.blockAlign = blockAlign;
		}
		else
		{
			throw new RuntimeException("Invalid block align mode. " + dataAlign + " given.");
		}
		return this;
	}

	/**
	 * Gets the inner data.
	 * @return The inner data.
	 */
	public String getData()
	{
		return data;
	}

	/**
	 * Sets the inner data
	 * @param data the data
	 * @return the parent block
	 */
	public Block setData(String data)
	{
		this.data = data;
		return this;
	}

	/**
	 * Gets the data align
	 * @return the index
	 */
	public int getDataAlign()
	{
		return dataAlign;
	}

	/**
	 * Sets the data align. 
	 * @param dataAlign the index
	 * @return the parent block
	 */
	public Block setDataAlign(int dataAlign)
	{
		if (dataAlign == DATA_TOP_LEFT || dataAlign == DATA_TOP_MIDDLE || dataAlign == DATA_TOP_RIGHT
				|| dataAlign == DATA_MIDDLE_LEFT || dataAlign == DATA_CENTER || dataAlign == DATA_MIDDLE_RIGHT
				|| dataAlign == DATA_BOTTOM_LEFT || dataAlign == DATA_BOTTOM_MIDDLE || dataAlign == DATA_BOTTOM_RIGHT)
		{
			this.dataAlign = dataAlign;
		}
		else
		{
			throw new RuntimeException("Invalid data align mode. " + dataAlign + " given.");
		}
		return this;
	}

	/**
	 * Gets X.
	 * @return the x value
	 */
	protected int getX()
	{
		return x;
	}

	/**
	 * Sets X. 
	 * @param x the X value
	 * @return the parent block.
	 */
	protected Block setX(int x)
	{
		if (x + getWidth() + (isGridAllowed() ? 2 : 0) <= board.boardWidth)
		{
			this.x = x;
		}
		else
		{
			throw new RuntimeException("Block " + toString() + " exceeded the board width " + board.boardWidth);
		}
		return this;
	}

	/**
	 * Gets Y
	 * @return the Y value
	 */
	protected int getY()
	{
		return y;
	}

	/**
	 * Sets Y
	 * @param y The Y value
	 * @return The parent block
	 */
	protected Block setY(int y)
	{
		this.y = y;
		return this;
	}

	/**
	 * Gets the right block.
	 * @return the block object
	 */
	public Block getRightBlock()
	{
		return rightBlock;
	}

	/**
	 * Sets the right block
	 * @param rightBlock 
	 * @return the block object.
	 */
	public Block setRightBlock(Block rightBlock)
	{
		if (rightBlock != null)
		{
			rightBlock.setX(getX() + getWidth() + (isGridAllowed() ? 1 : 0));
			rightBlock.setY(getY());
			this.rightBlock = rightBlock;
		}
		return this;
	}

	/**
	 * Gets the below block
	 * @return the object
	 */
	public Block getBelowBlock()
	{
		return belowBlock;
	}

	/**
	 * Sets the below blocki
	 * @param belowBlock the object
	 * @return the parent block
	 */
	public Block setBelowBlock(Block belowBlock)
	{
		if (belowBlock != null)
		{
			belowBlock.setX(getX());
			belowBlock.setY(getY() + getHeight() + (isGridAllowed() ? 1 : 0));
			this.belowBlock = belowBlock;
		}
		return this;
	}

	/**
	 * Invalidate.
	 * @return the parent block
	 */
	protected Block invalidate()
	{
		charrsList = new ArrayList<>();
		preview = "";
		return this;
	}

	/**
	 * Builds the block.
	 * 
	 * @return the block.
	 */
	protected Block build()
	{
		if (charrsList.isEmpty())
		{
			int ix = x;
			int iy = y;
			int blockLeftSideSpaces = -1;
			int additionalWidth = (isGridAllowed() ? 2 : 0);
			switch (getBlockAlign())
			{
			case BLOCK_LEFT:
			{
				blockLeftSideSpaces = 0;
				break;
			}
			case BLOCK_CENTRE:
			{
				blockLeftSideSpaces = (board.boardWidth - (ix + getWidth() + additionalWidth)) / 2
						+ (board.boardWidth - (ix + getWidth() + additionalWidth)) % 2;
				break;
			}
			case BLOCK_RIGHT:
			{
				blockLeftSideSpaces = board.boardWidth - (ix + getWidth() + additionalWidth);
				break;
			}
			}
			ix += blockLeftSideSpaces;
			if (data == null)
			{
				data = toString();
			}
			String[] lines = data.split("\n");
			List<String> dataInLines = new ArrayList<>();
			if (board.showBlockIndex)
			{
				dataInLines.add("i = " + index);
			}
			for (String line : lines)
			{
				if (getHeight() > dataInLines.size())
				{
					dataInLines.add(line);
				}
				else
				{
					break;
				}
			}
			for (int i = dataInLines.size(); i < getHeight(); i++)
			{
				dataInLines.add("");
			}
			for (int i = 0; i < dataInLines.size(); i++)
			{
				String dataLine = dataInLines.get(i);
				if (dataLine.length() > getWidth())
				{
					dataInLines.set(i, dataLine.substring(0, getWidth()));
					if (i + 1 != dataInLines.size())
					{
						String prifix = dataLine.substring(getWidth(), dataLine.length());
						String suffix = dataInLines.get(i + 1);
						String combinedValue = prifix.concat((suffix.length() > 0 ? String.valueOf(CharMap.S) : ""))
								.concat(suffix);
						dataInLines.set(i + 1, combinedValue);
					}
				}
			}

			for (int i = 0; i < dataInLines.size(); i++)
			{
				if (dataInLines.remove(""))
				{
					i--;
				}
			}

			int givenAlign = getDataAlign();
			int dataStartingLineIndex = -1;
			int additionalHeight = (isGridAllowed() ? 1 : 0);
			if (givenAlign == DATA_TOP_LEFT || givenAlign == DATA_TOP_MIDDLE || givenAlign == DATA_TOP_RIGHT)
			{
				dataStartingLineIndex = iy + additionalHeight;
			}
			else if (givenAlign == DATA_MIDDLE_LEFT || givenAlign == DATA_CENTER || givenAlign == DATA_MIDDLE_RIGHT)
			{
				dataStartingLineIndex = iy + additionalHeight
						+ ((getHeight() - dataInLines.size()) / 2 + (getHeight() - dataInLines.size()) % 2);
			}
			else if (givenAlign == DATA_BOTTOM_LEFT || givenAlign == DATA_BOTTOM_MIDDLE
					|| givenAlign == DATA_BOTTOM_RIGHT)
			{
				dataStartingLineIndex = iy + additionalHeight + (getHeight() - dataInLines.size());
			}
			int dataEndingLineIndex = dataStartingLineIndex + dataInLines.size();

			int extendedIX = ix + getWidth() + (isGridAllowed() ? 2 : 0);
			int extendedIY = iy + getHeight() + (isGridAllowed() ? 2 : 0);
			int startingIX = ix;
			int startingIY = iy;
			for (; iy < extendedIY; iy++)
			{
				for (; ix < extendedIX; ix++)
				{
					boolean writeData;
					if (isGridAllowed())
					{
						if ((iy == startingIY) || (iy == extendedIY - 1))
						{
							if ((ix == startingIX) || (ix == extendedIX - 1))
							{
								charrsList.add(new CharMap(ix, iy, CharMap.P));
								writeData = false;
							}
							else
							{
								charrsList.add(new CharMap(ix, iy, CharMap.D));
								writeData = false;
							}
						}
						else
						{
							if ((ix == startingIX) || (ix == extendedIX - 1))
							{
								charrsList.add(new CharMap(ix, iy, CharMap.VL));
								writeData = false;
							}
							else
							{
								writeData = true;
							}
						}
					}
					else
					{
						writeData = true;
					}
					if (writeData && (iy >= dataStartingLineIndex && iy < dataEndingLineIndex))
					{
						int dataLineIndex = iy - dataStartingLineIndex;
						String lineData = dataInLines.get(dataLineIndex);
						if (!lineData.isEmpty())
						{
							int dataLeftSideSpaces = -1;
							if (givenAlign == DATA_TOP_LEFT || givenAlign == DATA_MIDDLE_LEFT
									|| givenAlign == DATA_BOTTOM_LEFT)
							{
								dataLeftSideSpaces = 0;
							}
							else if (givenAlign == DATA_TOP_MIDDLE || givenAlign == DATA_CENTER
									|| givenAlign == DATA_BOTTOM_MIDDLE)
							{
								dataLeftSideSpaces = (getWidth() - lineData.length()) / 2
										+ (getWidth() - lineData.length()) % 2;
							}
							else if (givenAlign == DATA_TOP_RIGHT || givenAlign == DATA_MIDDLE_RIGHT
									|| givenAlign == DATA_BOTTOM_RIGHT)
							{
								dataLeftSideSpaces = getWidth() - lineData.length();
							}
							int dataStartingIndex = (startingIX + dataLeftSideSpaces + (isGridAllowed() ? 1 : 0));
							int dataEndingIndex = (startingIX + dataLeftSideSpaces + lineData.length()
									- (isGridAllowed() ? 0 : 1));
							if (ix >= dataStartingIndex && ix <= dataEndingIndex)
							{
								char charData = lineData.charAt(ix - dataStartingIndex);
								charrsList.add(new CharMap(ix, iy, charData));
							}
						}
					}
				}
				ix = startingIX;
			}
		}
		return this;
	}

	/**
	 * Gets the chars.
	 * @return list of chars
	 */
	protected List<CharMap> getChars()
	{
		return this.charrsList;
	}

	/**
	 * Gets preview.
	 * @return the inner data.
	 */
	public String getPreview()
	{
		build();
		if (preview.isEmpty())
		{
			int maxY = -1;
			int maxX = -1;
			for (CharMap charr : charrsList)
			{
				int testY = charr.getY();
				int testX = charr.getX();
				if (maxY < testY)
				{
					maxY = testY;
				}
				if (maxX < testX)
				{
					maxX = testX;
				}
			}
			String[][] dataPoints = new String[maxY + 1][board.boardWidth];
			for (CharMap charr : charrsList)
			{
				dataPoints[charr.getY()][charr.getX()] = String.valueOf(charr.getC());
			}

			for (String[] dataPoint : dataPoints)
			{
				for (String point : dataPoint)
				{
					if (point == null)
					{
						point = String.valueOf(CharMap.S);
					}
					preview = preview.concat(point);
				}
				preview = preview.concat(String.valueOf(CharMap.NL));
			}
		}
		return preview;
	}

	/**
	 * Gets the most right block
	 * @return the block
	 */
	public Block getMostRightBlock()
	{
		return getMostRightBlock(this);
	}

	/**
	 * Gets the most right block.
	 * @param block the block
	 * @return the block
	 */
	private Block getMostRightBlock(Block block)
	{
		if (block.getRightBlock() == null)
		{
			return block;
		}
		else
		{
			return getMostRightBlock(block.getRightBlock());
		}
	}

	/**
	 * Gets the most below block.
	 * @return the block
	 */
	public Block getMostBelowBlock()
	{
		return getMostBelowBlock(this);
	}

	/**
	 * Gets the most below block.
	 * @param block
	 * @return the block
	 */
	private Block getMostBelowBlock(Block block)
	{
		if (block.getBelowBlock() == null)
		{
			return block;
		}
		else
		{
			return getMostBelowBlock(block.getBelowBlock());
		}
	}

	/**
	 * This object to string.
	 */
	@Override
	public String toString()
	{
		return index + " = [" + x + "," + y + "," + width + "," + height + "]";
	}

	/**
	 * Equals.
	 */
	@Override
	public boolean equals(Object block)
	{
		if (block == null)
		{
			return false;
		}
		if (!(block instanceof Block))
		{
			return false;
		}
		Block b = (Block) block;
		return b.getIndex() == getIndex() && b.getX() == getX() && b.getY() == getY();
	}

	/**
	 * Hashcode.
	 */
	@Override
	public int hashCode()
	{
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
