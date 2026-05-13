/*      */ package flex.messaging.services.messaging.selector;
/*      */ 
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.messages.Message;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.Vector;
/*      */ 
/*      */ public class SQLParser
/*      */   implements SQLParserConstants
/*      */ {
/*   21 */   Message msg = null;
/*      */   JMSSelector selector;
/*   23 */   PropertyValueComparator comparator = PropertyValueComparator.getInstance();
/*      */   public SQLParserTokenManager token_source;
/*      */   SimpleCharStream jj_input_stream;
/*      */   public Token token;
/*      */   public Token jj_nt;
/*      */   private int jj_ntk;
/*      */   private Token jj_scanpos;
/*      */   private Token jj_lastpos;
/*      */   private int jj_la;
/* 1291 */   public boolean lookingAhead = false;
/*      */   private boolean jj_semLA;
/*      */   private int jj_gen;
/* 1294 */   private final int[] jj_la1 = new int[19];
/*      */   private static int[] jj_la1_0;
/*      */   private static int[] jj_la1_1;
/* 1307 */   private final JJCalls[] jj_2_rtns = new JJCalls[13];
/* 1308 */   private boolean jj_rescan = false;
/* 1309 */   private int jj_gc = 0;
/*      */ 
/* 1394 */   private final LookaheadSuccess jj_ls = new LookaheadSuccess(null);
/*      */ 
/* 1440 */   private Vector jj_expentries = new Vector();
/*      */   private int[] jj_expentry;
/* 1442 */   private int jj_kind = -1;
/* 1443 */   private int[] jj_lasttokens = new int[100];
/*      */   private int jj_endpos;
/*      */ 
/*      */   public SQLParser(JMSSelector selector, InputStream stream)
/*      */   {
/*   26 */     this(stream);
/*   27 */     this.selector = selector;
/*      */   }
/*      */ 
/*      */   public void setMessage(Message msg)
/*      */   {
/*   32 */     this.msg = msg;
/*      */   }
/*      */ 
/*      */   public final boolean match(Message msg)
/*      */     throws ParseException
/*      */   {
/*   39 */     boolean matchResult = false;
/*   40 */     Object res = null;
/*   41 */     if (msg == null) {
/*   42 */       throw new MessageException("Null Message for Selector");
/*      */     }
/*   44 */     this.msg = msg;
/*   45 */     res = SQLOrExpr();
/*   46 */     if (res != null) {
/*   47 */       if (!(res instanceof Boolean)) {
/*   48 */         throw new ParseException("Selector must evaluate to a java.lang.Boolean. Instead evaluated to a " + res.getClass().getName());
/*      */       }
/*      */ 
/*   51 */       matchResult = ((Boolean)res).booleanValue();
/*      */     }
/*   53 */     return matchResult;
/*      */   }
/*      */ 
/*      */   public final Object SQLOrExpr() throws ParseException
/*      */   {
/*   58 */     Object res1 = null;
/*   59 */     Object res2 = null;
/*   60 */     res1 = SQLAndExpr();
/*      */     while (true)
/*      */     {
/*   63 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*      */       {
/*      */       case 13:
/*   66 */         break;
/*      */       default:
/*   68 */         this.jj_la1[0] = this.jj_gen;
/*   69 */         break;
/*      */       }
/*   71 */       jj_consume_token(13);
/*   72 */       res2 = SQLAndExpr();
/*   73 */       if (((res1 != null) && (!(res1 instanceof Boolean))) || ((res2 != null) && (!(res2 instanceof Boolean))))
/*      */       {
/*   75 */         throw new ParseException("SQLOrExpr requires java.lang.Boolean for opearnds of OR operation");
/*      */       }
/*      */ 
/*   78 */       if ((res1 != null) && (res2 != null)) {
/*   79 */         res1 = new Boolean((((Boolean)res1).booleanValue()) || (((Boolean)res2).booleanValue())); continue;
/*   80 */       }if ((res1 == null) && (res2 == null))
/*      */       {
/*   83 */         res1 = null; continue;
/*      */       }
/*      */ 
/*   87 */       Boolean notUnknownValue = (Boolean)(res1 == null ? res2 : res1);
/*   88 */       if (notUnknownValue.booleanValue())
/*      */       {
/*   91 */         res1 = notUnknownValue;
/*      */       }
/*      */       else
/*      */       {
/*   95 */         res1 = null;
/*      */       }
/*      */     }
/*      */ 
/*   99 */     return res1;
/*      */   }
/*      */ 
/*      */   public final Object SQLAndExpr() throws ParseException
/*      */   {
/*  104 */     Object res1 = null;
/*  105 */     Object res2 = null;
/*  106 */     res1 = SQLNotExpr();
/*      */     while (true)
/*      */     {
/*  109 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*      */       {
/*      */       case 6:
/*  112 */         break;
/*      */       default:
/*  114 */         this.jj_la1[1] = this.jj_gen;
/*  115 */         break;
/*      */       }
/*  117 */       jj_consume_token(6);
/*  118 */       res2 = SQLNotExpr();
/*  119 */       if (((res1 != null) && (!(res1 instanceof Boolean))) || ((res2 != null) && (!(res2 instanceof Boolean))))
/*      */       {
/*  121 */         throw new ParseException("SQLAndExpr requires java.lang.Boolean for operands of AND operation");
/*      */       }
/*      */ 
/*  124 */       if ((res1 != null) && (res2 != null)) {
/*  125 */         res1 = new Boolean((((Boolean)res1).booleanValue()) && (((Boolean)res2).booleanValue())); continue;
/*      */       }
/*      */ 
/*  128 */       if ((res1 == null) && (res2 == null))
/*      */       {
/*  131 */         res1 = null; continue;
/*      */       }
/*  133 */       Boolean notUnknownValue = (Boolean)(res1 == null ? res2 : res1);
/*  134 */       if (notUnknownValue.booleanValue())
/*      */       {
/*  137 */         res1 = null;
/*      */       }
/*      */       else
/*      */       {
/*  141 */         res1 = notUnknownValue;
/*      */       }
/*      */     }
/*      */ 
/*  145 */     return res1;
/*      */   }
/*      */ 
/*      */   public final Object SQLNotExpr() throws ParseException
/*      */   {
/*  150 */     boolean isNot = false;
/*  151 */     Object res = null;
/*  152 */     Object obj = null;
/*  153 */     if (jj_2_1(2)) {
/*  154 */       jj_consume_token(11);
/*  155 */       isNot = true;
/*      */     }
/*      */ 
/*  159 */     res = SQLCompareExpr();
/*  160 */     if (isNot) {
/*  161 */       if (res == null)
/*      */       {
/*  164 */         return res;
/*  165 */       }if (!(res instanceof Boolean)) {
/*  166 */         throw new ParseException("The NOT operator requires a Boolean to be returned by SQLCompareExpr");
/*      */       }
/*      */ 
/*  169 */       res = new Boolean(!((Boolean)res).booleanValue());
/*      */     }
/*  171 */     return res;
/*      */   }
/*      */ 
/*      */   public final Object SQLCompareExpr() throws ParseException
/*      */   {
/*  176 */     Object res = null;
/*  177 */     if (jj_2_2(2))
/*  178 */       res = SQLIsClause();
/*      */     else {
/*  180 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 15:
/*      */       case 16:
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 29:
/*      */       case 33:
/*      */       case 34:
/*  189 */         res = SQLSumExpr();
/*  190 */         switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */         case 7:
/*      */         case 8:
/*      */         case 10:
/*      */         case 11:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*  201 */           res = SQLCompareExprRight(res);
/*  202 */           break;
/*      */         case 9:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 15:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         default:
/*  204 */           this.jj_la1[2] = this.jj_gen;
/*      */         }
/*      */ 
/*  207 */         break;
/*      */       case 17:
/*      */       case 21:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       default:
/*  209 */         this.jj_la1[3] = this.jj_gen;
/*  210 */         jj_consume_token(-1);
/*  211 */         throw new ParseException();
/*      */       }
/*      */     }
/*  214 */     return res;
/*      */   }
/*      */ 
/*      */   public final Boolean SQLCompareExprRight(Object obj1) throws ParseException
/*      */   {
/*  219 */     Boolean res = null;
/*  220 */     Object obj2 = null;
/*  221 */     if ((obj1 != null) || 
/*  224 */       (jj_2_3(2))) {
/*  225 */       res = SQLLikeClause(obj1);
/*  226 */     } else if (jj_2_4(4)) {
/*  227 */       res = SQLInClause(obj1);
/*  228 */     } else if (jj_2_5(2)) {
/*  229 */       res = SQLBetweenClause(obj1);
/*  230 */     } else if (jj_2_6(2)) {
/*  231 */       jj_consume_token(27);
/*  232 */       obj2 = SQLSumExpr();
/*  233 */       if (obj1 == null) {
/*  234 */         return null;
/*      */       }
/*  236 */       res = new Boolean(this.comparator.compare(obj1, obj2) == 0);
/*  237 */     } else if (jj_2_7(2)) {
/*  238 */       jj_consume_token(28);
/*  239 */       obj2 = SQLSumExpr();
/*  240 */       if (obj1 == null) {
/*  241 */         return null;
/*      */       }
/*  243 */       res = new Boolean(this.comparator.compare(obj1, obj2) != 0);
/*  244 */     } else if (jj_2_8(2)) {
/*  245 */       jj_consume_token(25);
/*  246 */       obj2 = SQLSumExpr();
/*  247 */       if (((obj1 instanceof String)) || ((obj2 instanceof String)) || ((obj1 instanceof Boolean)) || ((obj2 instanceof Boolean)))
/*      */       {
/*  249 */         throw new ParseException("Cannot use > with String or Boolean types");
/*      */       }
/*  251 */       if (obj1 == null) {
/*  252 */         return null;
/*      */       }
/*  254 */       res = new Boolean(this.comparator.compare(obj1, obj2) > 0);
/*  255 */     } else if (jj_2_9(2)) {
/*  256 */       jj_consume_token(26);
/*  257 */       obj2 = SQLSumExpr();
/*  258 */       if (((obj1 instanceof String)) || ((obj2 instanceof String)) || ((obj1 instanceof Boolean)) || ((obj2 instanceof Boolean)))
/*      */       {
/*  260 */         throw new ParseException("Cannot use >= with String or Boolean types");
/*      */       }
/*  262 */       if (obj1 == null) {
/*  263 */         return null;
/*      */       }
/*  265 */       res = new Boolean(this.comparator.compare(obj1, obj2) >= 0);
/*  266 */     } else if (jj_2_10(2)) {
/*  267 */       jj_consume_token(23);
/*  268 */       obj2 = SQLSumExpr();
/*  269 */       if (((obj1 instanceof String)) || ((obj2 instanceof String)) || ((obj1 instanceof Boolean)) || ((obj2 instanceof Boolean)))
/*      */       {
/*  271 */         throw new ParseException("Cannot use < with String or Boolean types");
/*      */       }
/*  273 */       if (obj1 == null) {
/*  274 */         return null;
/*      */       }
/*      */ 
/*  277 */       int i = this.comparator.compare(obj1, obj2);
/*  278 */       if (i != -100)
/*  279 */         res = new Boolean(i < 0);
/*      */     }
/*  281 */     else if (jj_2_11(2)) {
/*  282 */       jj_consume_token(24);
/*  283 */       obj2 = SQLSumExpr();
/*  284 */       if (((obj1 instanceof String)) || ((obj2 instanceof String)) || ((obj1 instanceof Boolean)) || ((obj2 instanceof Boolean)))
/*      */       {
/*  286 */         throw new ParseException("Cannot use <= with String or Boolean types");
/*      */       }
/*  288 */       if (obj1 == null) {
/*  289 */         return null;
/*      */       }
/*      */ 
/*  292 */       int i = this.comparator.compare(obj1, obj2);
/*  293 */       if (i != -100)
/*  294 */         res = new Boolean(i <= 0);
/*      */     }
/*      */     else {
/*  297 */       jj_consume_token(-1);
/*  298 */       throw new ParseException();
/*      */     }
/*  300 */     return res;
/*      */   }
/*      */ 
/*      */   public final Object SQLSumExpr() throws ParseException
/*      */   {
/*  305 */     Object res1 = null;
/*  306 */     Object res2 = null;
/*  307 */     NumericValue num1 = null;
/*  308 */     NumericValue num2 = null;
/*  309 */     boolean doAdd = true;
/*  310 */     res1 = SQLProductExpr();
/*      */     while (true)
/*      */     {
/*  313 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*      */       {
/*      */       case 33:
/*      */       case 34:
/*  317 */         break;
/*      */       default:
/*  319 */         this.jj_la1[4] = this.jj_gen;
/*  320 */         break;
/*      */       }
/*  322 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 33:
/*  324 */         jj_consume_token(33);
/*  325 */         doAdd = true;
/*  326 */         break;
/*      */       case 34:
/*  328 */         jj_consume_token(34);
/*  329 */         doAdd = false;
/*  330 */         break;
/*      */       default:
/*  332 */         this.jj_la1[5] = this.jj_gen;
/*  333 */         jj_consume_token(-1);
/*  334 */         throw new ParseException();
/*      */       }
/*  336 */       res2 = SQLProductExpr();
/*  337 */       num1 = new NumericValue(res1);
/*  338 */       num2 = new NumericValue(res2);
/*      */ 
/*  340 */       if (doAdd) {
/*  341 */         res1 = num1.add(num2); continue;
/*      */       }
/*  343 */       res1 = num1.subtract(num2);
/*      */     }
/*      */ 
/*  346 */     return res1;
/*      */   }
/*      */ 
/*      */   public final Object SQLProductExpr() throws ParseException
/*      */   {
/*  351 */     Object res1 = null;
/*  352 */     Object res2 = null;
/*  353 */     NumericValue num1 = null;
/*  354 */     NumericValue num2 = null;
/*  355 */     boolean doMultiply = true;
/*  356 */     res1 = SQLUnaryExpr();
/*      */     while (true)
/*      */     {
/*  359 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*      */       {
/*      */       case 31:
/*      */       case 32:
/*  363 */         break;
/*      */       default:
/*  365 */         this.jj_la1[6] = this.jj_gen;
/*  366 */         break;
/*      */       }
/*  368 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 31:
/*  370 */         jj_consume_token(31);
/*  371 */         doMultiply = true;
/*  372 */         break;
/*      */       case 32:
/*  374 */         jj_consume_token(32);
/*  375 */         doMultiply = false;
/*  376 */         break;
/*      */       default:
/*  378 */         this.jj_la1[7] = this.jj_gen;
/*  379 */         jj_consume_token(-1);
/*  380 */         throw new ParseException();
/*      */       }
/*  382 */       res2 = SQLUnaryExpr();
/*  383 */       num1 = new NumericValue(res1);
/*  384 */       num2 = new NumericValue(res2);
/*      */ 
/*  386 */       if (doMultiply) {
/*  387 */         res1 = num1.multiply(num2); continue;
/*      */       }
/*  389 */       res1 = num1.divide(num2);
/*      */     }
/*      */ 
/*  392 */     return res1;
/*      */   }
/*      */ 
/*      */   public final Object SQLUnaryExpr() throws ParseException
/*      */   {
/*  397 */     Object res1 = null;
/*  398 */     NumericValue num1 = null;
/*  399 */     boolean negate = false;
/*  400 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 33:
/*      */     case 34:
/*  403 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 33:
/*  405 */         jj_consume_token(33);
/*  406 */         break;
/*      */       case 34:
/*  408 */         jj_consume_token(34);
/*  409 */         negate = true;
/*  410 */         break;
/*      */       default:
/*  412 */         this.jj_la1[8] = this.jj_gen;
/*  413 */         jj_consume_token(-1);
/*  414 */         throw new ParseException();
/*      */       }
/*      */ 
/*      */     default:
/*  418 */       this.jj_la1[9] = this.jj_gen;
/*      */     }
/*      */ 
/*  421 */     res1 = SQLTerm();
/*  422 */     if (negate) {
/*  423 */       num1 = new NumericValue(res1);
/*  424 */       res1 = num1.negate();
/*      */     }
/*  426 */     return res1;
/*      */   }
/*      */ 
/*      */   public final String SQLColRef()
/*      */     throws ParseException
/*      */   {
/*  432 */     String colName = new String("");
/*  433 */     Token x = jj_consume_token(20);
/*  434 */     colName = x.image;
/*  435 */     return colName;
/*      */   }
/*      */ 
/*      */   public final Object SQLTerm()
/*      */     throws ParseException
/*      */   {
/*  441 */     Object res = null;
/*  442 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 29:
/*  444 */       jj_consume_token(29);
/*  445 */       res = SQLOrExpr();
/*  446 */       jj_consume_token(30);
/*  447 */       return res;
/*      */     case 15:
/*      */     case 16:
/*      */     case 18:
/*      */     case 19:
/*  453 */       res = SQLLiteral();
/*  454 */       return res;
/*      */     case 20:
/*  457 */       String colName = SQLColRef();
/*      */       try
/*      */       {
/*  460 */         res = this.msg.getHeader(colName);
/*      */ 
/*  463 */         if (((res instanceof Byte)) || ((res instanceof Short)) || ((res instanceof Integer)))
/*      */         {
/*  467 */           res = new Long(((Number)res).longValue());
/*      */         }
/*  469 */         else if ((res instanceof Float))
/*  470 */           res = new Double(((Number)res).doubleValue());
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  474 */         e.printStackTrace();
/*  475 */         throw new MessageException(e.getMessage());
/*      */       }
/*  477 */       return res;
/*      */     case 17:
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*  480 */     case 28: } this.jj_la1[10] = this.jj_gen;
/*  481 */     jj_consume_token(-1);
/*  482 */     throw new ParseException();
/*      */   }
/*      */ 
/*      */   public final Object SQLLiteral()
/*      */     throws ParseException
/*      */   {
/*  488 */     Token x = null;
/*  489 */     Object obj = null;
/*      */     try {
/*  491 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */       case 18:
/*  493 */         x = jj_consume_token(18);
/*  494 */         obj = this.selector.processStringLiteral(x.image);
/*  495 */         break;
/*      */       case 15:
/*  497 */         x = jj_consume_token(15);
/*  498 */         obj = new NumericValue(x.image, 4);
/*      */ 
/*  500 */         break;
/*      */       case 16:
/*  502 */         x = jj_consume_token(16);
/*  503 */         obj = new NumericValue(x.image, 5);
/*  504 */         break;
/*      */       case 19:
/*  506 */         x = jj_consume_token(19);
/*  507 */         obj = new Boolean(x.image.toLowerCase());
/*  508 */         break;
/*      */       case 17:
/*      */       default:
/*  510 */         this.jj_la1[11] = this.jj_gen;
/*  511 */         jj_consume_token(-1);
/*  512 */         throw new ParseException();
/*      */       }
/*  514 */       return obj;
/*      */     } catch (Exception e) {
/*      */     }
/*  517 */     throw generateParseException();
/*      */   }
/*      */ 
/*      */   public final Boolean SQLLikeClause(Object obj1)
/*      */     throws ParseException
/*      */   {
/*  523 */     Boolean res = null;
/*  524 */     boolean isLike = false;
/*  525 */     boolean isNot = false;
/*  526 */     String propVal = null;
/*  527 */     char escapeChar = '\000';
/*  528 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 11:
/*  530 */       jj_consume_token(11);
/*  531 */       isNot = true;
/*  532 */       break;
/*      */     default:
/*  534 */       this.jj_la1[12] = this.jj_gen;
/*      */     }
/*      */ 
/*  537 */     jj_consume_token(10);
/*  538 */     String pattern = SQLPattern();
/*  539 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 14:
/*  541 */       jj_consume_token(14);
/*  542 */       escapeChar = EscapeChar();
/*  543 */       break;
/*      */     default:
/*  545 */       this.jj_la1[13] = this.jj_gen;
/*      */     }
/*      */ 
/*  548 */     if ((pattern != null) && (!(pattern instanceof String))) {
/*  549 */       throw new ParseException("The LIKE target must be a string. Found " + pattern.getClass());
/*      */     }
/*      */ 
/*  553 */     if ((obj1 instanceof String)) {
/*  554 */       isLike = this.selector.matchPattern(pattern, (String)obj1, escapeChar);
/*      */ 
/*  556 */       if (isNot) {
/*  557 */         isLike = !isLike;
/*      */       }
/*  559 */       res = new Boolean(isLike);
/*      */     }
/*  561 */     return res;
/*      */   }
/*      */ 
/*      */   public final String SQLPattern()
/*      */     throws ParseException
/*      */   {
/*  568 */     Token x = jj_consume_token(18);
/*  569 */     String res = x.image;
/*  570 */     return this.selector.processStringLiteral(res);
/*      */   }
/*      */ 
/*      */   public final char EscapeChar()
/*      */     throws ParseException
/*      */   {
/*  576 */     String escapeCharStr = null;
/*      */ 
/*  578 */     Token x = jj_consume_token(18);
/*  579 */     escapeCharStr = x.image;
/*      */ 
/*  581 */     if (escapeCharStr.length() != 3) {
/*  582 */       throw new ParseException("Expected single escape character for SQL pattern. Found " + escapeCharStr);
/*      */     }
/*      */ 
/*  585 */     char escapeChar = escapeCharStr.charAt(1);
/*  586 */     return escapeChar;
/*      */   }
/*      */ 
/*      */   public final Boolean SQLIsClause()
/*      */     throws ParseException
/*      */   {
/*  592 */     boolean isNull = false;
/*  593 */     boolean notNull = false;
/*  594 */     Boolean res = null;
/*  595 */     String colName = SQLColRef();
/*  596 */     jj_consume_token(9);
/*  597 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 11:
/*  599 */       jj_consume_token(11);
/*  600 */       notNull = true;
/*  601 */       break;
/*      */     default:
/*  603 */       this.jj_la1[14] = this.jj_gen;
/*      */     }
/*      */ 
/*  606 */     jj_consume_token(12);
/*      */     try {
/*  608 */       isNull = !this.msg.headerExists(colName);
/*      */     }
/*      */     catch (Exception e) {
/*  611 */       throw generateParseException();
/*      */     }
/*  613 */     if (notNull) {
/*  614 */       isNull = !isNull;
/*      */     }
/*  616 */     res = new Boolean(isNull);
/*  617 */     return res;
/*      */   }
/*      */ 
/*      */   public final Boolean SQLInClause(Object obj1) throws ParseException
/*      */   {
/*  622 */     boolean found = false;
/*  623 */     boolean negate = false;
/*  624 */     Boolean res = null;
/*  625 */     ArrayList list = null;
/*  626 */     Object element = null;
/*  627 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 11:
/*  629 */       jj_consume_token(11);
/*  630 */       negate = true;
/*  631 */       break;
/*      */     default:
/*  633 */       this.jj_la1[15] = this.jj_gen;
/*      */     }
/*      */ 
/*  636 */     jj_consume_token(8);
/*  637 */     jj_consume_token(29);
/*  638 */     list = SQLLValueList();
/*  639 */     jj_consume_token(30);
/*  640 */     if (list != null) {
/*  641 */       Iterator iter = list.iterator();
/*      */       try
/*      */       {
/*      */         String str;
/*  643 */         while (iter.hasNext()) {
/*  644 */           element = iter.next();
/*  645 */           str = (String)element;
/*      */         }
/*      */       } catch (ClassCastException cce) {
/*  648 */         throw new ParseException("All TARGETS of a IN clause must be a String. Found a " + element.getClass());
/*      */       }
/*      */ 
/*  652 */       if (obj1 == null) {
/*  653 */         return null;
/*      */       }
/*  655 */       if (!(obj1 instanceof String)) {
/*  656 */         throw new ParseException("Source of IN clause must be a String. Found a " + obj1.getClass().getName());
/*      */       }
/*      */ 
/*  659 */       found = list.contains(obj1);
/*      */     }
/*      */ 
/*  662 */     if (negate) {
/*  663 */       found = !found;
/*      */     }
/*      */ 
/*  666 */     res = new Boolean(found);
/*  667 */     return res;
/*      */   }
/*      */ 
/*      */   public final ArrayList SQLLValueList() throws ParseException
/*      */   {
/*  672 */     Object elem = null;
/*  673 */     ArrayList list = new ArrayList();
/*  674 */     elem = SQLLValueElement();
/*  675 */     list.add(elem);
/*      */     while (true)
/*      */     {
/*  678 */       switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
/*      */       {
/*      */       case 36:
/*  681 */         break;
/*      */       default:
/*  683 */         this.jj_la1[16] = this.jj_gen;
/*  684 */         break;
/*      */       }
/*  686 */       jj_consume_token(36);
/*  687 */       elem = SQLLValueElement();
/*  688 */       list.add(elem);
/*      */     }
/*  690 */     return list;
/*      */   }
/*      */ 
/*      */   public final Object SQLLValueElement() throws ParseException
/*      */   {
/*  695 */     Object res = null;
/*  696 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 12:
/*  698 */       jj_consume_token(12);
/*  699 */       break;
/*      */     default:
/*  701 */       this.jj_la1[17] = this.jj_gen;
/*  702 */       if (jj_2_12(3)) {
/*  703 */         res = SQLSumExpr();
/*  704 */       } else if (jj_2_13(3)) {
/*  705 */         res = SQLOrExpr();
/*      */       } else {
/*  707 */         jj_consume_token(-1);
/*  708 */         throw new ParseException();
/*      */       }
/*      */     }
/*  711 */     if ((res instanceof NumericValue)) {
/*  712 */       res = ((NumericValue)res).getValue();
/*      */     }
/*  714 */     return res;
/*      */   }
/*      */ 
/*      */   public final Boolean SQLBetweenClause(Object obj1) throws ParseException
/*      */   {
/*  719 */     boolean between = false;
/*  720 */     boolean negate = false;
/*  721 */     Object res1 = null;
/*  722 */     Object res2 = null;
/*  723 */     ArrayList list = null;
/*  724 */     switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk) {
/*      */     case 11:
/*  726 */       jj_consume_token(11);
/*  727 */       negate = true;
/*  728 */       break;
/*      */     default:
/*  730 */       this.jj_la1[18] = this.jj_gen;
/*      */     }
/*      */ 
/*  733 */     jj_consume_token(7);
/*  734 */     res1 = SQLSumExpr();
/*  735 */     jj_consume_token(6);
/*  736 */     res2 = SQLSumExpr();
/*  737 */     if ((obj1 instanceof NumericValue)) {
/*  738 */       obj1 = ((NumericValue)obj1).getValue();
/*      */     }
/*  740 */     if ((obj1 != null) && (!(obj1 instanceof Comparable))) {
/*  741 */       throw new ParseException("The LValue for BETWEEN must be a java.lang.Comparable. Found " + obj1);
/*      */     }
/*      */ 
/*  744 */     if ((res1 != null) && (((res1 instanceof String)) || ((res1 instanceof Boolean))))
/*      */     {
/*  746 */       throw new ParseException("The START target for BETWEEN must be a numeric value. Found " + res1.getClass());
/*      */     }
/*      */ 
/*  750 */     if ((res2 != null) && (((res2 instanceof String)) || ((res2 instanceof Boolean))))
/*      */     {
/*  752 */       throw new ParseException("The END target for BETWEEN must be a numeric value. Found " + res2.getClass());
/*      */     }
/*      */ 
/*  756 */     if ((res1 instanceof NumericValue)) {
/*  757 */       res1 = ((NumericValue)res1).getValue();
/*      */     }
/*  759 */     if ((res2 instanceof NumericValue)) {
/*  760 */       res2 = ((NumericValue)res2).getValue();
/*      */     }
/*      */     try
/*      */     {
/*  764 */       if ((((Comparable)obj1).compareTo(res1) >= 0) && (((Comparable)obj1).compareTo(res2) <= 0))
/*  765 */         between = true;
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  769 */       return null;
/*      */     }
/*      */ 
/*  772 */     if (negate) {
/*  773 */       between = !between;
/*      */     }
/*      */ 
/*  776 */     res1 = new Boolean(between);
/*  777 */     return (Boolean)res1;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_1(int xla)
/*      */   {
/*  782 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_1() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  784 */       int j = 1;
/*      */       return j; } finally { jj_save(0, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_2(int xla) {
/*  789 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_2() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  791 */       int j = 1;
/*      */       return j; } finally { jj_save(1, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_3(int xla) {
/*  796 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_3() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  798 */       int j = 1;
/*      */       return j; } finally { jj_save(2, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_4(int xla) {
/*  803 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_4() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  805 */       int j = 1;
/*      */       return j; } finally { jj_save(3, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_5(int xla) {
/*  810 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_5() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  812 */       int j = 1;
/*      */       return j; } finally { jj_save(4, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_6(int xla) {
/*  817 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_6() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  819 */       int j = 1;
/*      */       return j; } finally { jj_save(5, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_7(int xla) {
/*  824 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_7() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  826 */       int j = 1;
/*      */       return j; } finally { jj_save(6, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_8(int xla) {
/*  831 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_8() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  833 */       int j = 1;
/*      */       return j; } finally { jj_save(7, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_9(int xla) {
/*  838 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_9() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  840 */       int j = 1;
/*      */       return j; } finally { jj_save(8, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_10(int xla) {
/*  845 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_10() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  847 */       int j = 1;
/*      */       return j; } finally { jj_save(9, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_11(int xla) {
/*  852 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_11() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  854 */       int j = 1;
/*      */       return j; } finally { jj_save(10, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_12(int xla) {
/*  859 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_12() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  861 */       int j = 1;
/*      */       return j; } finally { jj_save(11, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_2_13(int xla) {
/*  866 */     this.jj_la = xla; this.jj_lastpos = (this.jj_scanpos = this.token);
/*      */     try { int i = !jj_3_13() ? 1 : 0;
/*      */       return i;
/*      */     }
/*      */     catch (LookaheadSuccess ls)
/*      */     {
/*  868 */       int j = 1;
/*      */       return j; } finally { jj_save(12, xla); } throw localObject;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_35() {
/*  873 */     return jj_scan_token(34);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_13()
/*      */   {
/*  878 */     return jj_scan_token(11);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_7()
/*      */   {
/*  884 */     Token xsp = this.jj_scanpos;
/*  885 */     if (jj_3R_13()) this.jj_scanpos = xsp;
/*  886 */     if (jj_scan_token(10)) return true;
/*  887 */     return jj_3R_14();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_30()
/*      */   {
/*  893 */     Token xsp = this.jj_scanpos;
/*  894 */     if (jj_scan_token(33)) {
/*  895 */       this.jj_scanpos = xsp;
/*  896 */       if (jj_3R_35()) return true;
/*      */     }
/*  898 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_15() {
/*  902 */     return jj_scan_token(11);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_11()
/*      */   {
/*  907 */     if (jj_scan_token(24)) return true;
/*  908 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_11()
/*      */   {
/*  913 */     if (jj_3R_20()) return true; Token xsp;
/*      */     do
/*  916 */       xsp = this.jj_scanpos;
/*  917 */     while (!jj_3R_21()); this.jj_scanpos = xsp;
/*      */ 
/*  919 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_8()
/*      */   {
/*  924 */     Token xsp = this.jj_scanpos;
/*  925 */     if (jj_3R_15()) this.jj_scanpos = xsp;
/*  926 */     if (jj_scan_token(8)) return true;
/*  927 */     if (jj_scan_token(29)) return true;
/*  928 */     if (jj_3R_16()) return true;
/*  929 */     return jj_scan_token(30);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_24()
/*      */   {
/*  935 */     Token xsp = this.jj_scanpos;
/*  936 */     if (jj_3R_30()) this.jj_scanpos = xsp;
/*  937 */     return jj_3R_31();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_45()
/*      */   {
/*  942 */     return jj_scan_token(19);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_1()
/*      */   {
/*  947 */     return jj_scan_token(11);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_28()
/*      */   {
/*  953 */     Token xsp = this.jj_scanpos;
/*  954 */     if (jj_3_1()) this.jj_scanpos = xsp;
/*  955 */     return jj_3R_34();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_44()
/*      */   {
/*  960 */     return jj_scan_token(16);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_10()
/*      */   {
/*  965 */     if (jj_scan_token(23)) return true;
/*  966 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_43()
/*      */   {
/*  971 */     return jj_scan_token(15);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_33()
/*      */   {
/*  976 */     return jj_scan_token(32);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_17()
/*      */   {
/*  981 */     return jj_scan_token(11);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_32()
/*      */   {
/*  986 */     return jj_scan_token(31);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_42()
/*      */   {
/*  991 */     return jj_scan_token(18);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_9()
/*      */   {
/*  997 */     Token xsp = this.jj_scanpos;
/*  998 */     if (jj_3R_17()) this.jj_scanpos = xsp;
/*  999 */     if (jj_scan_token(7)) return true;
/* 1000 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_9()
/*      */   {
/* 1005 */     if (jj_scan_token(26)) return true;
/* 1006 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_25()
/*      */   {
/* 1012 */     Token xsp = this.jj_scanpos;
/* 1013 */     if (jj_3R_32()) {
/* 1014 */       this.jj_scanpos = xsp;
/* 1015 */       if (jj_3R_33()) return true;
/*      */     }
/* 1017 */     return jj_3R_24();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_46()
/*      */   {
/* 1022 */     return jj_scan_token(11);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_40()
/*      */   {
/* 1028 */     Token xsp = this.jj_scanpos;
/* 1029 */     if (jj_3R_42()) {
/* 1030 */       this.jj_scanpos = xsp;
/* 1031 */       if (jj_3R_43()) {
/* 1032 */         this.jj_scanpos = xsp;
/* 1033 */         if (jj_3R_44()) {
/* 1034 */           this.jj_scanpos = xsp;
/* 1035 */           if (jj_3R_45()) return true;
/*      */         }
/*      */       }
/*      */     }
/* 1039 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_6() {
/* 1043 */     if (jj_3R_12()) return true;
/* 1044 */     if (jj_scan_token(9)) return true;
/*      */ 
/* 1046 */     Token xsp = this.jj_scanpos;
/* 1047 */     if (jj_3R_46()) this.jj_scanpos = xsp;
/* 1048 */     return jj_scan_token(12);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_13()
/*      */   {
/* 1053 */     return jj_3R_11();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_8()
/*      */   {
/* 1058 */     if (jj_scan_token(25)) return true;
/* 1059 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_18()
/*      */   {
/* 1064 */     if (jj_3R_24()) return true; Token xsp;
/*      */     do
/* 1067 */       xsp = this.jj_scanpos;
/* 1068 */     while (!jj_3R_25()); this.jj_scanpos = xsp;
/*      */ 
/* 1070 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_12() {
/* 1074 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_7()
/*      */   {
/* 1079 */     if (jj_scan_token(28)) return true;
/* 1080 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_22()
/*      */   {
/* 1086 */     Token xsp = this.jj_scanpos;
/* 1087 */     if (jj_scan_token(12)) {
/* 1088 */       this.jj_scanpos = xsp;
/* 1089 */       if (jj_3_12()) {
/* 1090 */         this.jj_scanpos = xsp;
/* 1091 */         if (jj_3_13()) return true;
/*      */       }
/*      */     }
/* 1094 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_6() {
/* 1098 */     if (jj_scan_token(27)) return true;
/* 1099 */     return jj_3R_10();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_27()
/*      */   {
/* 1104 */     return jj_scan_token(34);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_29()
/*      */   {
/* 1109 */     if (jj_scan_token(6)) return true;
/* 1110 */     return jj_3R_28();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_5()
/*      */   {
/* 1115 */     return jj_3R_9();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_4()
/*      */   {
/* 1120 */     return jj_3R_8();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_23()
/*      */   {
/* 1125 */     return jj_scan_token(36);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_41()
/*      */   {
/* 1130 */     return jj_3R_47();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_26()
/*      */   {
/* 1135 */     return jj_scan_token(33);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_38()
/*      */   {
/* 1140 */     return jj_3R_12();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_20()
/*      */   {
/* 1145 */     if (jj_3R_28()) return true; Token xsp;
/*      */     do
/* 1148 */       xsp = this.jj_scanpos;
/* 1149 */     while (!jj_3R_29()); this.jj_scanpos = xsp;
/*      */ 
/* 1151 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_3() {
/* 1155 */     return jj_3R_7();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_16()
/*      */   {
/* 1160 */     if (jj_3R_22()) return true; Token xsp;
/*      */     do
/* 1163 */       xsp = this.jj_scanpos;
/* 1164 */     while (!jj_3R_23()); this.jj_scanpos = xsp;
/*      */ 
/* 1166 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_37() {
/* 1170 */     return jj_3R_40();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_19()
/*      */   {
/* 1176 */     Token xsp = this.jj_scanpos;
/* 1177 */     if (jj_3R_26()) {
/* 1178 */       this.jj_scanpos = xsp;
/* 1179 */       if (jj_3R_27()) return true;
/*      */     }
/* 1181 */     return jj_3R_18();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_36()
/*      */   {
/* 1186 */     if (jj_scan_token(29)) return true;
/* 1187 */     if (jj_3R_11()) return true;
/* 1188 */     return jj_scan_token(30);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_31()
/*      */   {
/* 1194 */     Token xsp = this.jj_scanpos;
/* 1195 */     if (jj_3R_36()) {
/* 1196 */       this.jj_scanpos = xsp;
/* 1197 */       if (jj_3R_37()) {
/* 1198 */         this.jj_scanpos = xsp;
/* 1199 */         if (jj_3R_38()) return true;
/*      */       }
/*      */     }
/* 1202 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_14() {
/* 1206 */     return jj_scan_token(18);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_47()
/*      */   {
/* 1212 */     Token xsp = this.jj_scanpos;
/* 1213 */     if (jj_3_3()) {
/* 1214 */       this.jj_scanpos = xsp;
/* 1215 */       if (jj_3_4()) {
/* 1216 */         this.jj_scanpos = xsp;
/* 1217 */         if (jj_3_5()) {
/* 1218 */           this.jj_scanpos = xsp;
/* 1219 */           if (jj_3_6()) {
/* 1220 */             this.jj_scanpos = xsp;
/* 1221 */             if (jj_3_7()) {
/* 1222 */               this.jj_scanpos = xsp;
/* 1223 */               if (jj_3_8()) {
/* 1224 */                 this.jj_scanpos = xsp;
/* 1225 */                 if (jj_3_9()) {
/* 1226 */                   this.jj_scanpos = xsp;
/* 1227 */                   if (jj_3_10()) {
/* 1228 */                     this.jj_scanpos = xsp;
/* 1229 */                     if (jj_3_11()) return true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1238 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_10() {
/* 1242 */     if (jj_3R_18()) return true; Token xsp;
/*      */     do
/* 1245 */       xsp = this.jj_scanpos;
/* 1246 */     while (!jj_3R_19()); this.jj_scanpos = xsp;
/*      */ 
/* 1248 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_21() {
/* 1252 */     if (jj_scan_token(13)) return true;
/* 1253 */     return jj_3R_20();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_39()
/*      */   {
/* 1258 */     if (jj_3R_10()) return true;
/*      */ 
/* 1260 */     Token xsp = this.jj_scanpos;
/* 1261 */     if (jj_3R_41()) this.jj_scanpos = xsp;
/* 1262 */     return false;
/*      */   }
/*      */ 
/*      */   private final boolean jj_3_2() {
/* 1266 */     return jj_3R_6();
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_12()
/*      */   {
/* 1271 */     return jj_scan_token(20);
/*      */   }
/*      */ 
/*      */   private final boolean jj_3R_34()
/*      */   {
/* 1277 */     Token xsp = this.jj_scanpos;
/* 1278 */     if (jj_3_2()) {
/* 1279 */       this.jj_scanpos = xsp;
/* 1280 */       if (jj_3R_39()) return true;
/*      */     }
/* 1282 */     return false;
/*      */   }
/*      */ 
/*      */   private static void jj_la1_0()
/*      */   {
/* 1302 */     jj_la1_0 = new int[] { 8192, 64, 528485760, 538804224, 0, 0, -2147483648, -2147483648, 0, 0, 538804224, 884736, 2048, 16384, 2048, 2048, 0, 4096, 2048 };
/*      */   }
/*      */   private static void jj_la1_1() {
/* 1305 */     jj_la1_1 = new int[] { 0, 0, 0, 6, 6, 6, 1, 1, 6, 6, 0, 0, 0, 0, 0, 0, 16, 0, 0 };
/*      */   }
/*      */ 
/*      */   public SQLParser(InputStream stream)
/*      */   {
/* 1312 */     this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
/* 1313 */     this.token_source = new SQLParserTokenManager(this.jj_input_stream);
/* 1314 */     this.token = new Token();
/* 1315 */     this.jj_ntk = -1;
/* 1316 */     this.jj_gen = 0;
/* 1317 */     for (int i = 0; i < 19; i++) this.jj_la1[i] = -1;
/* 1318 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls(); 
/*      */   }
/*      */ 
/*      */   public void ReInit(InputStream stream)
/*      */   {
/* 1322 */     this.jj_input_stream.ReInit(stream, 1, 1);
/* 1323 */     this.token_source.ReInit(this.jj_input_stream);
/* 1324 */     this.token = new Token();
/* 1325 */     this.jj_ntk = -1;
/* 1326 */     this.jj_gen = 0;
/* 1327 */     for (int i = 0; i < 19; i++) this.jj_la1[i] = -1;
/* 1328 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls(); 
/*      */   }
/*      */ 
/*      */   public SQLParser(Reader stream)
/*      */   {
/* 1332 */     this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
/* 1333 */     this.token_source = new SQLParserTokenManager(this.jj_input_stream);
/* 1334 */     this.token = new Token();
/* 1335 */     this.jj_ntk = -1;
/* 1336 */     this.jj_gen = 0;
/* 1337 */     for (int i = 0; i < 19; i++) this.jj_la1[i] = -1;
/* 1338 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls(); 
/*      */   }
/*      */ 
/*      */   public void ReInit(Reader stream)
/*      */   {
/* 1342 */     this.jj_input_stream.ReInit(stream, 1, 1);
/* 1343 */     this.token_source.ReInit(this.jj_input_stream);
/* 1344 */     this.token = new Token();
/* 1345 */     this.jj_ntk = -1;
/* 1346 */     this.jj_gen = 0;
/* 1347 */     for (int i = 0; i < 19; i++) this.jj_la1[i] = -1;
/* 1348 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls(); 
/*      */   }
/*      */ 
/*      */   public SQLParser(SQLParserTokenManager tm)
/*      */   {
/* 1352 */     this.token_source = tm;
/* 1353 */     this.token = new Token();
/* 1354 */     this.jj_ntk = -1;
/* 1355 */     this.jj_gen = 0;
/* 1356 */     for (int i = 0; i < 19; i++) this.jj_la1[i] = -1;
/* 1357 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls(); 
/*      */   }
/*      */ 
/*      */   public void ReInit(SQLParserTokenManager tm)
/*      */   {
/* 1361 */     this.token_source = tm;
/* 1362 */     this.token = new Token();
/* 1363 */     this.jj_ntk = -1;
/* 1364 */     this.jj_gen = 0;
/* 1365 */     for (int i = 0; i < 19; i++) this.jj_la1[i] = -1;
/* 1366 */     for (int i = 0; i < this.jj_2_rtns.length; i++) this.jj_2_rtns[i] = new JJCalls();
/*      */   }
/*      */ 
/*      */   private final Token jj_consume_token(int kind)
/*      */     throws ParseException
/*      */   {
/* 1371 */     Token oldToken;
/* 1371 */     if ((oldToken = this.token).next != null) this.token = this.token.next; else
/* 1372 */       this.token = (this.token.next = this.token_source.getNextToken());
/* 1373 */     this.jj_ntk = -1;
/* 1374 */     if (this.token.kind == kind) {
/* 1375 */       this.jj_gen += 1;
/* 1376 */       if (++this.jj_gc > 100) {
/* 1377 */         this.jj_gc = 0;
/* 1378 */         for (int i = 0; i < this.jj_2_rtns.length; i++) {
/* 1379 */           JJCalls c = this.jj_2_rtns[i];
/* 1380 */           while (c != null) {
/* 1381 */             if (c.gen < this.jj_gen) c.first = null;
/* 1382 */             c = c.next;
/*      */           }
/*      */         }
/*      */       }
/* 1386 */       return this.token;
/*      */     }
/* 1388 */     this.token = oldToken;
/* 1389 */     this.jj_kind = kind;
/* 1390 */     throw generateParseException();
/*      */   }
/*      */ 
/*      */   private final boolean jj_scan_token(int kind)
/*      */   {
/* 1396 */     if (this.jj_scanpos == this.jj_lastpos) {
/* 1397 */       this.jj_la -= 1;
/* 1398 */       if (this.jj_scanpos.next == null)
/* 1399 */         this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken());
/*      */       else
/* 1401 */         this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next);
/*      */     }
/*      */     else {
/* 1404 */       this.jj_scanpos = this.jj_scanpos.next;
/*      */     }
/* 1406 */     if (this.jj_rescan) {
/* 1407 */       int i = 0; Token tok = this.token;
/* 1408 */       for (; (tok != null) && (tok != this.jj_scanpos); tok = tok.next) i++;
/* 1409 */       if (tok != null) jj_add_error_token(kind, i);
/*      */     }
/* 1411 */     if (this.jj_scanpos.kind != kind) return true;
/* 1412 */     if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) throw this.jj_ls;
/* 1413 */     return false;
/*      */   }
/*      */ 
/*      */   public final Token getNextToken() {
/* 1417 */     if (this.token.next != null) this.token = this.token.next; else
/* 1418 */       this.token = (this.token.next = this.token_source.getNextToken());
/* 1419 */     this.jj_ntk = -1;
/* 1420 */     this.jj_gen += 1;
/* 1421 */     return this.token;
/*      */   }
/*      */ 
/*      */   public final Token getToken(int index) {
/* 1425 */     Token t = this.lookingAhead ? this.jj_scanpos : this.token;
/* 1426 */     for (int i = 0; i < index; i++) {
/* 1427 */       if (t.next != null) t = t.next; else
/* 1428 */         t = t.next = this.token_source.getNextToken();
/*      */     }
/* 1430 */     return t;
/*      */   }
/*      */ 
/*      */   private final int jj_ntk() {
/* 1434 */     if ((this.jj_nt = this.token.next) == null) {
/* 1435 */       return this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind;
/*      */     }
/* 1437 */     return this.jj_ntk = this.jj_nt.kind;
/*      */   }
/*      */ 
/*      */   private void jj_add_error_token(int kind, int pos)
/*      */   {
/* 1447 */     if (pos >= 100) return;
/* 1448 */     if (pos == this.jj_endpos + 1) {
/* 1449 */       this.jj_lasttokens[(this.jj_endpos++)] = kind;
/* 1450 */     } else if (this.jj_endpos != 0) {
/* 1451 */       this.jj_expentry = new int[this.jj_endpos];
/* 1452 */       for (int i = 0; i < this.jj_endpos; i++) {
/* 1453 */         this.jj_expentry[i] = this.jj_lasttokens[i];
/*      */       }
/* 1455 */       boolean exists = false;
/* 1456 */       for (Enumeration e = this.jj_expentries.elements(); e.hasMoreElements(); ) {
/* 1457 */         int[] oldentry = (int[])(int[])e.nextElement();
/* 1458 */         if (oldentry.length == this.jj_expentry.length) {
/* 1459 */           exists = true;
/* 1460 */           for (int i = 0; i < this.jj_expentry.length; i++) {
/* 1461 */             if (oldentry[i] != this.jj_expentry[i]) {
/* 1462 */               exists = false;
/* 1463 */               break;
/*      */             }
/*      */           }
/* 1466 */           if (exists) break;
/*      */         }
/*      */       }
/* 1469 */       if (!exists) this.jj_expentries.addElement(this.jj_expentry);
/* 1470 */       if (pos != 0)
/*      */       {
/*      */         int tmp205_204 = pos; this.jj_endpos = tmp205_204; this.jj_lasttokens[(tmp205_204 - 1)] = kind;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public ParseException generateParseException() {
/* 1475 */     this.jj_expentries.removeAllElements();
/* 1476 */     boolean[] la1tokens = new boolean[37];
/* 1477 */     for (int i = 0; i < 37; i++) {
/* 1478 */       la1tokens[i] = false;
/*      */     }
/* 1480 */     if (this.jj_kind >= 0) {
/* 1481 */       la1tokens[this.jj_kind] = true;
/* 1482 */       this.jj_kind = -1;
/*      */     }
/* 1484 */     for (int i = 0; i < 19; i++) {
/* 1485 */       if (this.jj_la1[i] == this.jj_gen) {
/* 1486 */         for (int j = 0; j < 32; j++) {
/* 1487 */           if ((jj_la1_0[i] & 1 << j) != 0) {
/* 1488 */             la1tokens[j] = true;
/*      */           }
/* 1490 */           if ((jj_la1_1[i] & 1 << j) != 0) {
/* 1491 */             la1tokens[(32 + j)] = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1496 */     for (int i = 0; i < 37; i++) {
/* 1497 */       if (la1tokens[i] != 0) {
/* 1498 */         this.jj_expentry = new int[1];
/* 1499 */         this.jj_expentry[0] = i;
/* 1500 */         this.jj_expentries.addElement(this.jj_expentry);
/*      */       }
/*      */     }
/* 1503 */     this.jj_endpos = 0;
/* 1504 */     jj_rescan_token();
/* 1505 */     jj_add_error_token(0, 0);
/* 1506 */     int[][] exptokseq = new int[this.jj_expentries.size()][];
/* 1507 */     for (int i = 0; i < this.jj_expentries.size(); i++) {
/* 1508 */       exptokseq[i] = ((int[])(int[])this.jj_expentries.elementAt(i));
/*      */     }
/* 1510 */     return new ParseException(this.token, exptokseq, tokenImage);
/*      */   }
/*      */ 
/*      */   public final void enable_tracing() {
/*      */   }
/*      */ 
/*      */   public final void disable_tracing() {
/*      */   }
/*      */ 
/*      */   private final void jj_rescan_token() {
/* 1520 */     this.jj_rescan = true;
/* 1521 */     for (int i = 0; i < 13; i++) {
/* 1522 */       JJCalls p = this.jj_2_rtns[i];
/*      */       do {
/* 1524 */         if (p.gen > this.jj_gen) {
/* 1525 */           this.jj_la = p.arg; this.jj_lastpos = (this.jj_scanpos = p.first);
/* 1526 */           switch (i) { case 0:
/* 1527 */             jj_3_1(); break;
/*      */           case 1:
/* 1528 */             jj_3_2(); break;
/*      */           case 2:
/* 1529 */             jj_3_3(); break;
/*      */           case 3:
/* 1530 */             jj_3_4(); break;
/*      */           case 4:
/* 1531 */             jj_3_5(); break;
/*      */           case 5:
/* 1532 */             jj_3_6(); break;
/*      */           case 6:
/* 1533 */             jj_3_7(); break;
/*      */           case 7:
/* 1534 */             jj_3_8(); break;
/*      */           case 8:
/* 1535 */             jj_3_9(); break;
/*      */           case 9:
/* 1536 */             jj_3_10(); break;
/*      */           case 10:
/* 1537 */             jj_3_11(); break;
/*      */           case 11:
/* 1538 */             jj_3_12(); break;
/*      */           case 12:
/* 1539 */             jj_3_13();
/*      */           }
/*      */         }
/* 1542 */         p = p.next;
/* 1543 */       }while (p != null);
/*      */     }
/* 1545 */     this.jj_rescan = false;
/*      */   }
/*      */ 
/*      */   private final void jj_save(int index, int xla) {
/* 1549 */     JJCalls p = this.jj_2_rtns[index];
/* 1550 */     while (p.gen > this.jj_gen) {
/* 1551 */       if (p.next == null) { p = p.next = new JJCalls(); break; }
/* 1552 */       p = p.next;
/*      */     }
/* 1554 */     p.gen = (this.jj_gen + xla - this.jj_la); p.first = this.token; p.arg = xla;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 1298 */     jj_la1_0();
/* 1299 */     jj_la1_1();
/*      */   }
/*      */ 
/*      */   static final class JJCalls
/*      */   {
/*      */     int gen;
/*      */     Token first;
/*      */     int arg;
/*      */     JJCalls next;
/*      */   }
/*      */ 
/*      */   private static final class LookaheadSuccess extends Error
/*      */   {
/*      */     private LookaheadSuccess()
/*      */     {
/*      */     }
/*      */ 
/*      */     LookaheadSuccess(SQLParser.1 x0)
/*      */     {
/* 1393 */       this();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.selector.SQLParser
 * JD-Core Version:    0.6.0
 */