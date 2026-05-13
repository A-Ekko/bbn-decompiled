/*    */ package flex.messaging.services.messaging.selector;
/*    */ 
/*    */ public abstract interface SQLParserConstants
/*    */ {
/*    */   public static final int EOF = 0;
/*    */   public static final int AND = 6;
/*    */   public static final int BETWEEN = 7;
/*    */   public static final int IN = 8;
/*    */   public static final int IS = 9;
/*    */   public static final int LIKE = 10;
/*    */   public static final int NOT = 11;
/*    */   public static final int NULL = 12;
/*    */   public static final int OR = 13;
/*    */   public static final int ESCAPE = 14;
/*    */   public static final int INTEGER_LITERAL = 15;
/*    */   public static final int FLOATING_POINT_LITERAL = 16;
/*    */   public static final int EXPONENT = 17;
/*    */   public static final int STRING_LITERAL = 18;
/*    */   public static final int BOOLEAN_LITERAL = 19;
/*    */   public static final int ID = 20;
/*    */   public static final int LETTER = 21;
/*    */   public static final int DIGIT = 22;
/*    */   public static final int LESS = 23;
/*    */   public static final int LESSEQUAL = 24;
/*    */   public static final int GREATER = 25;
/*    */   public static final int GREATEREQUAL = 26;
/*    */   public static final int EQUAL = 27;
/*    */   public static final int NOTEQUAL = 28;
/*    */   public static final int OPENPAREN = 29;
/*    */   public static final int CLOSEPAREN = 30;
/*    */   public static final int ASTERISK = 31;
/*    */   public static final int SLASH = 32;
/*    */   public static final int PLUS = 33;
/*    */   public static final int MINUS = 34;
/*    */   public static final int QUESTIONMARK = 35;
/*    */   public static final int DEFAULT = 0;
/* 40 */   public static final String[] tokenImage = { "<EOF>", " ", "\\n", "\\r", "\\t", "\\f", "and", "between", "in", "is", "like", "not", "null", "or", "escape", "<INTEGER_LITERAL>", "<FLOATING_POINT_LITERAL>", "<EXPONENT>", "<STRING_LITERAL>", "<BOOLEAN_LITERAL>", "<ID>", "<LETTER>", "<DIGIT>", "<", "<=", ">", ">=", "=", "<>", "(", ")", "*", "/", "+", "-", "?", "," };
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.selector.SQLParserConstants
 * JD-Core Version:    0.6.0
 */