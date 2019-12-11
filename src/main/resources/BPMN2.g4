parser grammar BPMN2;

options { tokenVocab=BPMN2Lexer; }

document            :   prolog? misc* element misc*;

prolog              :   XMLDeclOpen attribute* SPECIAL_CLOSE ;

content             :   chardata?
                        ((element | reference | CDATA | PI | COMMENT) chardata?)* ;

selfClosingElement  : '<' Name attribute* '/>';
blockElement        : '<' Name attribute* '>' content '<' '/' Name '>';

element             :   selfClosingElement
                    |   blockElement
                    ;

reference           :   EntityRef | CharRef ;

attribute           :   Name '=' STRING ; // Our STRING is AttValue in spec

/** ``All text that is not markup constitutes the character data of
 *  the document.''
 */
chardata            :   TEXT | SEA_WS ;

misc                :   COMMENT | PI | SEA_WS ;
