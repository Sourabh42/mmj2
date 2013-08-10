//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SearchOptionsPosIntField.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

public abstract class SearchOptionsPosIntField extends SearchOptionsJTextField
    implements SearchOptionsScrnMapField
{

    public SearchOptionsPosIntField(final int i, final String s, final int j) {
        super(i, s, j);
    }

    public SearchOptionsPosIntField(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText,
            SearchOptionsConstants.FIELD_ATTR[i].columns);
    }
}