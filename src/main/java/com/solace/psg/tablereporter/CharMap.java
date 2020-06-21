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

/**
 * Class to handle table character drawings. This implementation is base on Wagu
 * library.
 * 
 * @author Victor Tsonkov
 */
class CharMap
{

	protected static final char S = ' ';

	protected static final char NL = '\n';

	protected static final char P = '+';

	protected static final char D = '-';

	protected static final char VL = '|';

	private final int x;

	private final int y;

	private final char c;

	/**
	 * Initialises a new instance of the class.
	 * @param x X 
	 * @param y Y
	 * @param c Char map
	 */
	protected CharMap(int x, int y, char c)
	{
		this.x = x;
		this.y = y;
		this.c = c;
	}

	/**
	 * Gets X
	 * @return X
	 */
	protected int getX()
	{
		return x;
	}

	/**
	 * Gets Y
	 * @return y
	 */
	protected int getY()
	{
		return y;
	}

	/**
	 * Gets C
	 * @return char
	 */
	protected char getC()
	{
		return c;
	}
}
