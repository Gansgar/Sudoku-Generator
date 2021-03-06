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

import com.georg.Sudoku;
import com.georg.ValueFormatException;

import java.util.List;

/**
 * Created by Georg on 30/05/16.
 * <p>
 * A class to solve sudokus in various ways.
 * By use of Depth First Search.
 */
public class SudokuSolver {
    private static Sudoku lastField = null;
    private static int cSol = 0;

    /**
     * Find number of solutions for given sudoku.
     *
     * @param sudoku The sudoku to solve.
     * @return Returns number of solutions.
     */
    @SuppressWarnings("WeakerAccess")
    public static int solutions(Sudoku sudoku) throws ValueFormatException {
        return DFS(new CompSudoku(sudoku), true);
    }

    /**
     * Gives an answer to the question:
     * "Is the sudoku solvable."
     * This is a faster variant of {@link #solutions(Sudoku)}
     * if the sudoku has a solution.
     *
     * @param sudoku The sudoku to evaluate.
     * @return True if the sudoku is solvable.
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isSolvable(Sudoku sudoku) throws ValueFormatException {
        return DFS(new CompSudoku(sudoku), false) > 0;
    }

    /**
     * Depth first search algorithm to find all
     * possible solutions for the sudoku using
     * a brut force like algorithm that tries
     * out every valid combination of on the field.
     *
     * @param sudoku The sudoku to solve.
     * @param end    False if just one solution should
     *               be found, true if the total number
     *               of all possible solution should returned.
     * @return Returns the number of solutions found.
     */
    private static int DFS(CompSudoku sudoku, boolean end) throws ValueFormatException {
        if (sudoku.isIndexLast())
            return 1;

        List<CompSudoku> compSudokus = sudoku.expand();
        if (compSudokus.size() == 0)
            return 0;

        int sum = 0;
        for (CompSudoku comp : compSudokus) {
            sum += DFS(comp, end);
            if (!end && sum != 0)
                break;
        }

        return sum;
    }


    /**
     * Securly returns the found
     * result from the
     * {@link #DFSLV(CompSudoku, long, int, int) Depth first seach for Las Vegas}
     * method.
     * <p>
     * It also will reset the internal used {@link #cSol counter}
     * and the {@link #lastField buffer field}.
     *
     * @return Returns the found field.
     */
    public static Sudoku getLastField() {
        cSol = 0;
        Sudoku su = lastField;
        lastField = null;
        return su;
    }

    /**
     * Tries to find a solution for a given field
     * and returns the solution.
     * The actual result of this method
     * can be accessed through {@link #getLastField()}.
     *
     * @param sudoku  The field to solve
     * @param start   The time of start
     * @param maxTime The maximum time the algorithm is
     *                allowed to calculate
     * @param maxSol  The maximum number of solutions,
     *                till the function returns.
     *                This allows for more diverse fields.
     * @return The sudoku field.
     */
    public static Sudoku DFSLV(CompSudoku sudoku, final long start, final int maxTime, final int maxSol) throws ValueFormatException {
        if (sudoku.isIndexLast())
            return sudoku;

        //if (System.currentTimeMillis() - start > maxTime)
        //    return null;

        List<CompSudoku> compSudokus = sudoku.expand();
        if (compSudokus.size() == 0)
            return null;

        for (CompSudoku comp : compSudokus) {
            Sudoku ret = DFSLV(comp, start, maxTime, maxSol);
            if (ret != null) {
                if (cSol <= maxSol)
                    lastField = ret;
                cSol++;
            }
            if (cSol == maxSol)
                return null;
        }
        return null;
    }
}
