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

/**
 * Class to handle table board. This implementation is base on Wagu library.
 * 
 * @author Victor Tsonkov
 */
public class Board
{

	protected boolean showBlockIndex;

	protected int boardWidth;

	private Block initialBlock;

	private List<CharMap> charrs;

	private String preview;

	public static final int APPEND_RIGHT = 16;

	public static final int APPEND_BELOW = 17;

	/**
	 * Initialises a new instance of the class.
	 * @param boardWidth the width
	 */
	public Board(int boardWidth)
	{
		this.boardWidth = boardWidth;
		this.charrs = new ArrayList<>();
		this.preview = "";
		this.showBlockIndex = false;
		Block.nextIndex = 0;
	}

	/**
	 * Gets the list of columns recommended width to pass to Board as width and
	 * table. objects based on calculations.
	 * 
	 * @param columnList    list of columns
	 * @param isGridAllowed if grid is allowed
	 * @return the width
	 */
	public static int getRecommendedWidth(List<Integer> columnList, boolean isGridAllowed)
	{
		int result = columnList.size() + (isGridAllowed ? 2 : 0);

		for (Integer i : columnList)
		{
			result += i;
		}

		return result;
	}

	/**
	 * Sets the initial block.
	 * @param initialBlock the block
	 * @return the board.
	 */
	public Board setInitialBlock(Block initialBlock)
	{
		this.initialBlock = initialBlock;
		return this;
	}

	/**
	 * Is block showing.
	 * @return true or false
	 */
	public boolean isBlockIndexShowing()
	{
		return showBlockIndex;
	}

	/**
	 * Show block index.
	 * @param showBlockIndex true or false
	 */
	public void showBlockIndex(boolean showBlockIndex)
	{
		this.showBlockIndex = showBlockIndex;
	}

	/**
	 * Appends table to. 
	 * @param appendableBlockIndex table index
	 * @param appendableDirection the direction
	 * @param table the table
	 * @return the board
	 */
	public Board appendTableTo(int appendableBlockIndex, int appendableDirection, Table table)
	{
		Block tableBlock = table.tableToBlocks();
		Block block = getBlock(appendableBlockIndex);
		if (appendableDirection == APPEND_RIGHT)
		{
			block.setRightBlock(tableBlock);
			rearranegCoordinates(block);
		}
		else if (appendableDirection == APPEND_BELOW)
		{
			block.setBelowBlock(tableBlock);
			rearranegCoordinates(block);
		}
		else
		{
			throw new RuntimeException("Invalid block appending direction given");
		}
		return this;
	}

	/**
	 * Rearrange the coordinates.
	 * @param block
	 */
	private void rearranegCoordinates(Block block)
	{
		Block rightBlock = block.getRightBlock();
		Block belowBlock = block.getBelowBlock();
		if (rightBlock != null && belowBlock == null)
		{
			block.setRightBlock(rightBlock);
			rearranegCoordinates(rightBlock);
		}
		else if (rightBlock == null && belowBlock != null)
		{
			block.setBelowBlock(belowBlock);
			rearranegCoordinates(belowBlock);
		}
		else if (rightBlock != null && belowBlock != null)
		{
			int rightIndex = rightBlock.getIndex();
			int belowIndex = belowBlock.getIndex();
			int blockIdDiff = rightIndex - belowIndex;
			if (blockIdDiff > 0)
			{
				if (blockIdDiff == 1)
				{
					block.setRightBlock(rightBlock);
					block.setBelowBlock(belowBlock);
					rearranegCoordinates(rightBlock);
					rearranegCoordinates(belowBlock);
				}
				else
				{
					block.setRightBlock(rightBlock);
					rearranegCoordinates(rightBlock);
					block.setBelowBlock(belowBlock);
					rearranegCoordinates(belowBlock);
				}
			}
			else if (blockIdDiff < 0)
			{
				blockIdDiff *= -1;
				if (blockIdDiff == 1)
				{
					block.setBelowBlock(belowBlock);
					block.setRightBlock(rightBlock);
					rearranegCoordinates(belowBlock);
					rearranegCoordinates(rightBlock);
				}
				else
				{
					block.setBelowBlock(belowBlock);
					rearranegCoordinates(belowBlock);
					block.setRightBlock(rightBlock);
					rearranegCoordinates(rightBlock);
				}
			}
		}
	}

	/**
	 * Gets the block.
	 * @param blockIndex the index
	 * @return the block
	 */
	public Block getBlock(int blockIndex)
	{
		if (blockIndex >= 0)
		{
			return getBlock(blockIndex, initialBlock);
		}
		else
		{
			throw new RuntimeException("Block index cannot be negative. " + blockIndex + " given.");
		}
	}

	/**
	 * gets the block. 
	 * @param blockIndex index
	 * @param block block
	 * @return the block
	 */
	private Block getBlock(int blockIndex, Block block)
	{
		Block foundBlock = null;
		if (block.getIndex() == blockIndex)
		{
			return block;
		}
		else
		{
			if (block.getRightBlock() != null)
			{
				foundBlock = getBlock(blockIndex, block.getRightBlock());
			}
			if (foundBlock != null)
			{
				return foundBlock;
			}
			if (block.getBelowBlock() != null)
			{
				foundBlock = getBlock(blockIndex, block.getBelowBlock());
			}
			if (foundBlock != null)
			{
				return foundBlock;
			}
		}
		return foundBlock;
	}

	/**
	 * Builds the board.
	 * @return the board
	 */
	public Board build()
	{
		if (charrs.isEmpty())
		{
			// rearranegCoordinates(initialBlock);
			buildBlock(initialBlock);
			dumpCharrsFromBlock(initialBlock);

			int maxY = -1;
			int maxX = -1;
			for (CharMap charr : charrs)
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
			String[][] dataPoints = new String[maxY + 1][boardWidth];
			for (CharMap charr : charrs)
			{
				String currentValue = dataPoints[charr.getY()][charr.getX()];
				String newValue = String.valueOf(charr.getC());
				if (currentValue == null || !currentValue.equals("+"))
				{
					dataPoints[charr.getY()][charr.getX()] = newValue;
				}
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

		return this;
	}

	/**
	 * Gets a preview.
	 * @return the data
	 */
	public String getPreview()
	{
		build();
		return preview;
	}

	/**
	 * Invalidates. 
	 * @return the board
	 */
	public Board invalidate()
	{
		invalidateBlock(initialBlock);
		charrs = new ArrayList<>();
		preview = "";
		return this;
	}

	/**
	 * Builds the block.
	 * @param block the block./
	 */
	private void buildBlock(Block block)
	{
		if (block != null)
		{
			block.build();
			buildBlock(block.getRightBlock());
			buildBlock(block.getBelowBlock());
		}
	}

	/**
	 * Dump characters from the block.
	 * @param block
	 */
	private void dumpCharrsFromBlock(Block block)
	{
		if (block != null)
		{
			charrs.addAll(block.getChars());
			dumpCharrsFromBlock(block.getRightBlock());
			dumpCharrsFromBlock(block.getBelowBlock());
		}
	}

	/**
	 * Invalidate the block.
	 * @param block
	 */
	private void invalidateBlock(Block block)
	{
		if (block != null)
		{
			block.invalidate();
			invalidateBlock(block.getRightBlock());
			invalidateBlock(block.getBelowBlock());
		}
	}
}
