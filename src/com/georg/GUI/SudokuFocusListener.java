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

package com.georg.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by Georg on 05/06/16.
 * <p>
 * Automatically selects the whole text
 * when the JFormattedTextField is focused.
 */
class SudokuFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
        dumpInfo(e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        dumpInfo(e);
    }

    private void dumpInfo(FocusEvent e) {
        final Component c = e.getComponent();
        // Selects the text if the focus is gained.
        if (c instanceof JFormattedTextField) {
            SwingUtilities.invokeLater(() -> {
                ((JFormattedTextField) c).setText(((JFormattedTextField) c).getText());
                ((JFormattedTextField) c).selectAll();
            });
        }
    }

}
