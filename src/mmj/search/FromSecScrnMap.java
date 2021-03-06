//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * FromSecScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

public class FromSecScrnMap extends SearchOptionsJComboBox
    implements ActionListener
{

    public FromSecScrnMap(
        final DefaultComboBoxModel<String> defaultcomboboxmodel,
        final String[][] as)
    {
        super(44, defaultcomboboxmodel);
        fromChapScrnMap = null;
        secValues = as;
        addActionListener(this);
    }

    public void setFromChapScrnMap(final FromChapScrnMap fromChapScrnMap) {
        this.fromChapScrnMap = fromChapScrnMap;
    }

    public void chapIdUpdated(final int i) {
        updateComboBoxList(secValues[i]);
    }

    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        final String s = (String)getSelectedItem();
        if (s != null)
            fixThruSecIfNecessary(s);
    }

    private void updateComboBoxList(final String[] as) {
        removeAllItems();
        for (final String element : as)
            addItem(element);

        setSelectedItem(as[0]);
    }

    private void fixThruSecIfNecessary(final String s) {
        if (s.equals(secValues[0][0]) || fromChapScrnMap == null
            || fromChapScrnMap.chap.equals(secValues[0][0]))
            return;
        final ThruChapScrnMap thruChapScrnMap = fromChapScrnMap.thruChapScrnMap;
        if (thruChapScrnMap == null
            || thruChapScrnMap.chap.equals(secValues[0][0])
            || !fromChapScrnMap.chap.equals(thruChapScrnMap.chap))
            return;
        final int i = getSelectedIndex();
        final int j = thruChapScrnMap.thruSecScrnMap.getSelectedIndex();
        if (j < i && j != -1)
            thruChapScrnMap.thruSecScrnMap.setSelectedItem(secValues[0][0]);
    }

    FromChapScrnMap fromChapScrnMap;
    private final String[][] secValues;
}
