package bolscript.scanner;

//import java_cup.runtime.*;
import static bolscript.sequences.Representable.*;

/**
 * This class is a simple example lexer.
 */
%%


%class SequenceScanner
%type SequenceToken
%function nextToken
%unicode
%char
%line

%{
  public SequenceScanner (String input) {
  	this(new java.io.StringReader(input));
  }
  
  private SequenceToken token(int type, String value) {
    return new SequenceToken(type, value, yychar, yyline);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Space = [\t\f\s]
WhiteSpace     = {LineTerminator} | [ \t\f\s]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*
PosInteger = [1-9][0-9]*

/* bolscript preUnits */
 

BolCandidate = [A-Za-z]+ {Numeration}? {WhiteSpace}* {Questioned}? {WhiteSpace}* {Emphasized}?
Numeration = [0-9]+ 
Questioned = "?"+
Emphasized = "!"+

Footnote = "\"" [^\"]* "\""

BracketOpen = "("

BracketClosed = ")"

Comma = "," ( {WhiteSpace} | "," )*

RationalSpeed =  {Numerator} ( {WhiteSpace}* "/" {WhiteSpace}* {Denominator} )?
Numerator = {PosInteger}*
Denominator = {PosInteger}*

KardinalityModifierEndOfLine = KardinalityModifier {Space}* {LineTerminator}

KardinalityModifier = {Multiplication} ( {WhiteSpace}* {Truncation} )* | {Truncation}
Multiplication = "x" {WhiteSpace}* {PosInteger}
Truncation = "<" {WhiteSpace}* {PosInteger}

%state STRING

%%

/* keywords */
<YYINITIAL> {

  {Footnote}					{return token(FOOTNOTE, yytext());}
  
  {KardinalityModifierEndOfLine} {return token(KARDINALITY_MODIFIER_END_OF_LINE, yytext());}
  
  {KardinalityModifier} 		{return token(KARDINALITY_MODIFIER, yytext());}
  
  {BolCandidate}                {return token(BOL_CANDIDATE, yytext());}
  
  {RationalSpeed}				{return token(SPEED,yytext());}
  
  {BracketOpen} 				{return token(BRACKET_OPEN,yytext());}
  
  {BracketClosed} 				{return token(BRACKET_CLOSED, yytext());}
  
  {Comma} 						{return token(COMMA, yytext());}
  
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}



/* error fallback */
.|\n                             { /* ignore */ }
