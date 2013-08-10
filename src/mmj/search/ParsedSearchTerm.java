//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ParsedSearchTerm.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import mmj.lang.*;
import mmj.mmio.Tokenizer;

public class ParsedSearchTerm {

    public static ParsedSearchTerm parseStmtUFOText(final String s,
        final int i, final SearchMgr searchMgr)
    {
        final ParsedSearchTerm parsedSearchTerm = new ParsedSearchTerm(s, i,
            searchMgr.getProvableLogicStmtTyp());
        parsedSearchTerm.parseStmt(searchMgr);
        return parsedSearchTerm;
    }

    public static ParsedSearchTerm parseExprUFOText(final String s,
        final int i, final SearchMgr searchMgr)
    {
        final ParsedSearchTerm parsedSearchTerm = new ParsedSearchTerm(s, i,
            null);
        parsedSearchTerm.parseExpr(searchMgr);
        return parsedSearchTerm;
    }

    private ParsedSearchTerm(final String s, final int i, final Cnst cnst) {
        errorMessage = null;
        isParseExpr = false;
        parseTree = null;
        formula = null;
        varHypArray = null;
        ufoText = s;
        maxSeq = i;
        typCd = cnst;
    }

    private void parseExpr(final SearchMgr searchMgr) {
        isParseExpr = true;
        loadSymbols(searchMgr);
        if (errorMessage == null) {
            loadParseTree(searchMgr);
            if (parseTree == null) {
                final Set<?> set = searchMgr.getGrammar()
                    .getSyntaxAxiomTypSet();
                final Iterator<?> iterator = set.iterator();
                boolean flag = false;
                do {
                    if (!iterator.hasNext())
                        break;
                    formula.setTyp((Cnst)iterator.next());
                    loadParseTree(searchMgr);
                    if (parseTree == null)
                        continue;
                    flag = true;
                    break;
                } while (true);
                if (!flag)
                    errorMessage = "Error parsing expression. Could not find matching syntax Type Code.";
            }
        }
    }

    private void parseStmt(final SearchMgr searchMgr) {
        loadSymbols(searchMgr);
        if (errorMessage == null) {
            loadParseTree(searchMgr);
            if (parseTree == null)
                errorMessage = "Error(?) in parse of search term expression.";
        }
    }

    private void loadParseTree(final SearchMgr searchMgr) {
        parseTree = searchMgr.getGrammar().parseFormulaWithoutSafetyNet(
            formula, varHypArray, maxSeq);
    }

    private void loadSymbols(final SearchMgr searchMgr) {
        final LogicalSystem logicalSystem = searchMgr.getLogicalSystem();
        searchMgr.getWorkVarManager();
        final MandFrame mandFrame = searchMgr.getComboFrame();
        final Map<String, Var> varMap = mandFrame.getVarMap();
        Tokenizer tokenizer;
        try {
            tokenizer = new Tokenizer(new StringReader(ufoText), "");
        } catch (final IOException e) {
            errorMessage = "Unable to parse expression. Detailed error = "
                + e.getMessage();
            return;
        }
        final List<VarHyp> varHyps = new ArrayList<VarHyp>();
        final List<Sym> symList = new ArrayList<Sym>();
        symList.add(searchMgr.getProvableLogicStmtTyp());
        while (true) {
            final StringBuilder sb = new StringBuilder();
            int len;
            try {
                len = tokenizer.getToken(sb, 0);
            } catch (final IOException e) {
                errorMessage = "Unable to parse expression. Detailed error = "
                    + e.getMessage();
                return;
            }
            if (len <= 0)
                break;
            final String token = sb.toString();
            Sym sym = varMap.get(token);
            if (sym == null) {
                sym = logicalSystem.getSymTbl().get(token);
                if (sym == null) {
                    errorMessage = "Invalid symbol in expression. Input token = "
                        + token
                        + " not found in Logical System Symbol Table."
                        + " Note: Work variables not allowed in search terms.";
                    return;
                }
                if (sym.getSeq() >= maxSeq) {
                    errorMessage = "Invalid symbol in expression. Input token = "
                        + token + " has sequence number >= Search maxSeq";
                    return;
                }
            }
            if (sym.isVar()) {
                final Var var = (Var)sym;
                if (var.getIsWorkVar()) {
                    errorMessage = "Work variables not allowed in search terms.";
                    return;
                }
                if (var.isActive() && var.getActiveVarHyp() != null)
                    Assrt.accumHypInList(varHyps, var.getActiveVarHyp());
                else {
                    errorMessage = "Invalid symbol in expression. Input token = "
                        + token
                        + " has does not have an active Var and VarHyp"
                        + " at the search's scope level.";
                    return;
                }
            }
            symList.add(sym);
        }
        formula = new Formula(symList);
        varHypArray = varHyps.toArray(new VarHyp[varHyps.size()]);
    }

    public String ufoText;
    public int maxSeq;
    public Cnst typCd;
    public String errorMessage;
    public boolean isParseExpr;
    public ParseTree parseTree;
    public Formula formula;
    public VarHyp[] varHypArray;
}
