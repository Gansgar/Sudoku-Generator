/*
 * MIT License
 *
 * Copyright (c) 2016 Georg A. Friedrich
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

package com.georg.Generator;

import com.georg.Level;
import com.georg.Sudoku;
import com.georg.ValueFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Georg on 30/05/16.
 * <p>
 * A subclass of {@link Sudoku} specialised
 * for doing computations.
 */
public class CompSudoku extends Sudoku {
    /**
     * The current index variable. This method
     * of accessing the sudoku field is especially
     * useful in {@link SudokuGen} algorithms to
     * generate sudokus in with difficulty higher
     * than Easy but also for {@link SudokuSolver}.
     */
    private int index = 0;

    CompSudoku(Level l) {
        super(l);
    }

    private CompSudoku(Level l, byte[] field) throws ValueFormatException {
        super(l, field);
    }

    /**
     * Creates a Computational Sudoku from its
     * superclass. Automatically selects the next
     * available free field.
     *
     * @param sudoku The sudoku.
     */
    public CompSudoku(Sudoku sudoku) throws ValueFormatException {
        this(sudoku.getDifficulty(), sudoku.getField());
        moveNext();
    }

    /**
     * This constructor creates a copy of another
     * instance of a {@link CompSudoku}
     *
     * @param sudoku The sudoku.
     */
    @SuppressWarnings("unused")
    private CompSudoku(CompSudoku sudoku) throws ValueFormatException {
        this(sudoku.getDifficulty(), sudoku.getField().clone());
        index = sudoku.index;
    }

    /**
     * Creates an index from an x, y position.
     *
     * @param x The x position.
     * @param y The y position.
     * @return The calculated 1D index.
     */
    static int getStaticIndex(int x, int y) {
        return x + FIELD_SIZE * y;
    }

    /**
     * Returns the current value on the
     * current index.
     *
     * @return Returns the value on the
     * current index.
     */
    byte getAtIndex() {
        return field[index];
    }

    /**
     * Swaps the values from two indexes
     * on the field. Unsecured but fast.
     * Doesn't move index.
     *
     * @param i1 The first index.
     * @param i2 The second index.
     */
    void swapIndex(int i1, int i2) {
        byte tmp = field[i1];
        field[i1] = field[i2];
        field[i2] = tmp;
    }

    /**
     * Move the index to the next
     * free field.
     */
    private void moveNext() {
        while (field[index] != NAN && !isIndexLast())
            moveIndex();

    }

    /**
     * @return True of the current index is the
     * last free one, if else than false.
     */
    boolean isIndexLast() {
        for (int i = index; i < FIELD_COUNT; i++) {
            if (field[i] == NAN)
                return false;
        }
        return true;
    }

    /**
     * Move the index by one field.
     * Save.
     */
    private void moveIndex() {
        if (index < (FIELD_COUNT - 1))
            index++;
    }

    /**
     * Resets the index to the next
     * free field.
     */
    void resetIndex() {
        index = 0;
        moveNext();
    }

    /**
     * Sets the value of the field the
     * input index to the specified value.
     * This is independent from the local
     * index.
     *
     * @param index The index
     * @param value The new value for
     *              the field at index.
     */
    void setAtField(int index, byte value) {
        setIndex(index);
        setField(value);
    }

    /**
     * Lists all sudokus that have the field
     * at the current free index replaced with
     * all possible values.
     *
     * @return The list of possible sudokus that
     * could be childs from the local index.
     */
    List<CompSudoku> expand() throws ValueFormatException {
        /* All numbers. Create boolean vector,
         * where each index represents a
         * number that is equal to the index+1.
         */
        Boolean[] possible = new Boolean[MAX_NUM];
        Arrays.fill(possible, true);
        possible = rule_3(rule_2(rule_1(possible)));

        List<CompSudoku> ret = new ArrayList<>();
        for (byte i = 1; i <= MAX_NUM; i++) {
            if (possible[i - 1]) {
                CompSudoku c = new CompSudoku(this);
                c.setField(i);
                c.resetIndex();
                ret.add(c);
            }
        }
        return ret;
    }

    /**
     * Returns a list of all possible values
     * at the position of the local index.
     *
     * @return List of all possible values
     * at the current index.
     */
    public List<Byte> getAvailable() {
        /* All numbers. Create boolean vector,
         * where each index represents a
         * number that is equal to the index+1.
         */
        Boolean[] possible = new Boolean[MAX_NUM];
        Arrays.fill(possible, true);
        possible = rule_3(rule_2(rule_1(possible)));

        List<Byte> ret = new ArrayList<>();
        for (byte i = 1; i <= MAX_NUM; i++) {
            // Only if True at pos (i-1), this number
            // is available for the position.
            if (possible[i - 1])
                ret.add(i);
        }
        return ret;
    }

    /**
     * Rule one is the first rule for solving sudokus named in
     * the paper. In this case it is that there cannot be
     * any double numbers in a row.
     *
     * @param possible the number variable. From 1 to 9, where false
     *                 marks not available anymore and true marks the
     *                 opposite.
     * @return returns the edited, now valid number variable. See input.
     */
    private Boolean[] rule_1(Boolean[] possible) {
        for (int i = 0; i < FIELD_SIZE; i++) {
            byte f = field[i + index / FIELD_SIZE * FIELD_SIZE];
            if (f != NAN)
                possible[f - 1] = false;
        }
        return possible;
    }

    /**
     * Rule one is the second rule for solving sudokus named in
     * the paper. In this case there cannot be a double number
     * in a column.
     *
     * @param possible the number variable. From 1 to 9, where false
     *                 marks not available anymore and true marks the
     *                 opposite.
     * @return returns the edited, now valid number variable. See input.
     */
    private Boolean[] rule_2(Boolean[] possible) {
        for (int i = 0; i < FIELD_SIZE; i++) {
            byte f = field[i * FIELD_SIZE + index % FIELD_SIZE];
            if (f != NAN)
                possible[f - 1] = false;
        }
        return possible;
    }

    /**
     * Rule one is the third rule for solving sudokus named in
     * the paper. In this case there cannot be a double number
     * in a block.
     *
     * @param possible the number variable. From 1 to 9, where false
     *                 marks not available anymore and true marks the
     *                 opposite.
     * @return returns the edited, now valid number variable. See input.
     */
    private Boolean[] rule_3(Boolean[] possible) {
        for (int i = 0; i < FIELD_SIZE; i++) {
            byte f = field[i % BLOCK_SIZE + (i / BLOCK_SIZE) * FIELD_SIZE
                    + (index % FIELD_SIZE) / BLOCK_SIZE * BLOCK_SIZE + index / (FIELD_SIZE * BLOCK_SIZE) * (FIELD_SIZE * BLOCK_SIZE)];
            if (f != NAN)
                possible[f - 1] = false;
        }
        return possible;
    }

    /**
     * @return the total number of non-free
     * fields. Also called "givens".
     */
    int getNumTotalFields() {
        int count = 0;
        for (byte f : field)
            if (f != NAN)
                count++;
        return count;
    }

    /**
     * @return The lower bound of givens for all
     * rows and columns in the sudoku.
     */
    int getLowerBoundRC() {
        int lowerBound = FIELD_SIZE;
        for (int i = 0; i < FIELD_SIZE; i++) {
            int countR = 0, countC = 0;
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (field[i * FIELD_SIZE + j] != NAN)
                    countR++;
                if (field[j * FIELD_SIZE + i] != NAN)
                    countC++;
            }
            lowerBound = Math.min(Math.min(countC, countR), lowerBound);
            if (lowerBound == 0)
                break;
        }
        return lowerBound;
    }

    /**
     * Returns a clone of the current object,
     * where the field at the specified index
     * is empty.
     *
     * @param index The index.
     * @return The cloned, edited sudoku.
     */
    public CompSudoku digClone(int index) throws ValueFormatException {
        CompSudoku ret = new CompSudoku(this);
        ret.field[index] = NAN;
        ret.setIndex(index);
        return ret;
    }

    /**
     * Editable.
     *
     * @return The editable field.
     */
    @Override
    public byte[] getField() {
        return field;
    }

    /**
     * Sets the field at the local index.
     *
     * @param value The value.
     */
    void setField(byte value) {
        field[index] = value;
    }

    /**
     * @return Current index.
     */
    int getIndex() {
        return index;
    }

    /**
     * Sets the internal index to the
     * specified one.
     *
     * @param index The index you
     *              talk'n about.
     */
    void setIndex(int index) {
        this.index = index;
    }
}
