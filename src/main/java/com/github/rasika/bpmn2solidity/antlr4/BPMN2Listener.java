// Generated from ../resources/BPMN2.g4 by ANTLR 4.7.2
package com.github.rasika.bpmn2solidity.antlr4;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BPMN2}.
 */
public interface BPMN2Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BPMN2#document}.
	 * @param ctx the parse tree
	 */
	void enterDocument(BPMN2.DocumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#document}.
	 * @param ctx the parse tree
	 */
	void exitDocument(BPMN2.DocumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#prolog}.
	 * @param ctx the parse tree
	 */
	void enterProlog(BPMN2.PrologContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#prolog}.
	 * @param ctx the parse tree
	 */
	void exitProlog(BPMN2.PrologContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#content}.
	 * @param ctx the parse tree
	 */
	void enterContent(BPMN2.ContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#content}.
	 * @param ctx the parse tree
	 */
	void exitContent(BPMN2.ContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#selfClosingElement}.
	 * @param ctx the parse tree
	 */
	void enterSelfClosingElement(BPMN2.SelfClosingElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#selfClosingElement}.
	 * @param ctx the parse tree
	 */
	void exitSelfClosingElement(BPMN2.SelfClosingElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#blockElement}.
	 * @param ctx the parse tree
	 */
	void enterBlockElement(BPMN2.BlockElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#blockElement}.
	 * @param ctx the parse tree
	 */
	void exitBlockElement(BPMN2.BlockElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(BPMN2.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(BPMN2.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#reference}.
	 * @param ctx the parse tree
	 */
	void enterReference(BPMN2.ReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#reference}.
	 * @param ctx the parse tree
	 */
	void exitReference(BPMN2.ReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(BPMN2.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(BPMN2.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#chardata}.
	 * @param ctx the parse tree
	 */
	void enterChardata(BPMN2.ChardataContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#chardata}.
	 * @param ctx the parse tree
	 */
	void exitChardata(BPMN2.ChardataContext ctx);
	/**
	 * Enter a parse tree produced by {@link BPMN2#misc}.
	 * @param ctx the parse tree
	 */
	void enterMisc(BPMN2.MiscContext ctx);
	/**
	 * Exit a parse tree produced by {@link BPMN2#misc}.
	 * @param ctx the parse tree
	 */
	void exitMisc(BPMN2.MiscContext ctx);
}
