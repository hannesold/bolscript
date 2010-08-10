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

LineTerminator = \r|\n|\r\n|\u2028|\u2029|\u000B|\u000C|\u0085
InputCharacter = [^\r\n]
Space = [ \t\f]
WhiteSpace     = {LineTerminator} | [ \t\f]

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
 
BolCandidate = {Pause}|([A-Za-z]*[A-WY-Za-wy-z]({OtherVariants}|{Numeration})*({WhiteSpace}*{Questioned})?({WhiteSpace}*{Emphasized})?)
Numeration = [0-9]+ 
OtherVariants = [.']
Questioned = "?"+
Emphasized = "!"+

Footnote = "\"" [^\"]* "\""

BracketOpen = "("

BracketClosed = ")"

Comma = "," ( {WhiteSpace} | "," )*

Pause = "-"

RationalSpeed = {Numerator}({WhiteSpace}*"/"{WhiteSpace}*{Denominator})?({WhiteSpace}*"!")?
Numerator = {PosInteger}*
Denominator = {PosInteger}*

KardinalityModifier = {Multiplication} ( {WhiteSpace}* {Truncation} )* | {Truncation}
Multiplication = "x" {WhiteSpace}* {PosInteger}
Truncation = "<" {WhiteSpace}* {PosInteger}
Spaces = {Space}+

LineBreak = {LineTerminator} ({WhiteSpace} | {LineTerminator})*
%state STRING

%%

/* keywords */
<YYINITIAL> {

  {Footnote}					{return token(FOOTNOTE, yytext());}
    
  {KardinalityModifier} 		{return token(KARDINALITY_MODIFIER, yytext());}
  
  {BolCandidate}                {return token(BOL_CANDIDATE, yytext());}
  
  {RationalSpeed}				{return token(SPEED,yytext());}
  
  {BracketOpen} 				{return token(BRACKET_OPEN,yytext());}
  
  {BracketClosed} 				{return token(BRACKET_CLOSED, yytext());}
  
  {Comma} 						{return token(COMMA, yytext());}
  
  {LineBreak}					{return token(LINE_BREAK, yytext());}
  
  {Spaces}                   	{return token(WHITESPACES, yytext());}

}



/* error fallback */
.|\n                             { /* ignore */ }
