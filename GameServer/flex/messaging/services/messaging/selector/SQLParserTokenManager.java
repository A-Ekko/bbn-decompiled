/*     */ package flex.messaging.services.messaging.selector;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class SQLParserTokenManager
/*     */   implements SQLParserConstants
/*     */ {
/*   9 */   public PrintStream debugStream = System.out;
/*     */ 
/* 357 */   static final long[] jjbitVec0 = { 0L, 0L, -1L, -1L };
/*     */ 
/* 694 */   static final int[] jjnextStates = { 25, 26, 3, 27, 28, 33, 34, 9, 11, 13, 11, 12, 13, 6, 7, 31, 32, 35, 36 };
/*     */ 
/* 698 */   public static final String[] jjstrLiteralImages = { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "<", "<=", ">", ">=", "=", "<>", "(", ")", "*", "/", "+", "-", "?", "," };
/*     */ 
/* 702 */   public static final String[] lexStateNames = { "DEFAULT" };
/*     */ 
/* 705 */   static final long[] jjtoToken = { 137432530881L };
/*     */ 
/* 708 */   static final long[] jjtoSkip = { 62L };
/*     */   protected SimpleCharStream input_stream;
/* 712 */   private final int[] jjrounds = new int[37];
/* 713 */   private final int[] jjstateSet = new int[74];
/*     */   protected char curChar;
/* 766 */   int curLexState = 0;
/* 767 */   int defaultLexState = 0;
/*     */   int jjnewStateCnt;
/*     */   int jjround;
/*     */   int jjmatchedPos;
/*     */   int jjmatchedKind;
/*     */ 
/*     */   public void setDebugStream(PrintStream ds)
/*     */   {
/*  10 */     this.debugStream = ds;
/*     */   }
/*     */   private final int jjStopStringLiteralDfa_0(int pos, long active0) {
/*  13 */     switch (pos)
/*     */     {
/*     */     case 0:
/*  16 */       if ((active0 & 0x7FC0) != 0L)
/*     */       {
/*  18 */         this.jjmatchedKind = 20;
/*  19 */         return 37;
/*     */       }
/*  21 */       return -1;
/*     */     case 1:
/*  23 */       if ((active0 & 0x5CC0) != 0L)
/*     */       {
/*  25 */         this.jjmatchedKind = 20;
/*  26 */         this.jjmatchedPos = 1;
/*  27 */         return 37;
/*     */       }
/*  29 */       if ((active0 & 0x2300) != 0L)
/*  30 */         return 37;
/*  31 */       return -1;
/*     */     case 2:
/*  33 */       if ((active0 & 0x840) != 0L)
/*  34 */         return 37;
/*  35 */       if ((active0 & 0x5480) != 0L)
/*     */       {
/*  37 */         this.jjmatchedKind = 20;
/*  38 */         this.jjmatchedPos = 2;
/*  39 */         return 37;
/*     */       }
/*  41 */       return -1;
/*     */     case 3:
/*  43 */       if ((active0 & 0x1400) != 0L)
/*  44 */         return 37;
/*  45 */       if ((active0 & 0x4080) != 0L)
/*     */       {
/*  47 */         this.jjmatchedKind = 20;
/*  48 */         this.jjmatchedPos = 3;
/*  49 */         return 37;
/*     */       }
/*  51 */       return -1;
/*     */     case 4:
/*  53 */       if ((active0 & 0x4080) != 0L)
/*     */       {
/*  55 */         this.jjmatchedKind = 20;
/*  56 */         this.jjmatchedPos = 4;
/*  57 */         return 37;
/*     */       }
/*  59 */       return -1;
/*     */     case 5:
/*  61 */       if ((active0 & 0x4000) != 0L)
/*  62 */         return 37;
/*  63 */       if ((active0 & 0x80) != 0L)
/*     */       {
/*  65 */         this.jjmatchedKind = 20;
/*  66 */         this.jjmatchedPos = 5;
/*  67 */         return 37;
/*     */       }
/*  69 */       return -1;
/*     */     }
/*  71 */     return -1;
/*     */   }
/*     */ 
/*     */   private final int jjStartNfa_0(int pos, long active0)
/*     */   {
/*  76 */     return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
/*     */   }
/*     */ 
/*     */   private final int jjStopAtPos(int pos, int kind) {
/*  80 */     this.jjmatchedKind = kind;
/*  81 */     this.jjmatchedPos = pos;
/*  82 */     return pos + 1;
/*     */   }
/*     */ 
/*     */   private final int jjStartNfaWithStates_0(int pos, int kind, int state) {
/*  86 */     this.jjmatchedKind = kind;
/*  87 */     this.jjmatchedPos = pos;
/*     */     try { this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*  89 */       return pos + 1;
/*  90 */     }return jjMoveNfa_0(state, pos + 1);
/*     */   }
/*     */ 
/*     */   private final int jjMoveStringLiteralDfa0_0() {
/*  94 */     switch (this.curChar)
/*     */     {
/*     */     case '(':
/*  97 */       return jjStopAtPos(0, 29);
/*     */     case ')':
/*  99 */       return jjStopAtPos(0, 30);
/*     */     case '*':
/* 101 */       return jjStopAtPos(0, 31);
/*     */     case '+':
/* 103 */       return jjStopAtPos(0, 33);
/*     */     case ',':
/* 105 */       return jjStopAtPos(0, 36);
/*     */     case '-':
/* 107 */       return jjStopAtPos(0, 34);
/*     */     case '/':
/* 109 */       return jjStopAtPos(0, 32);
/*     */     case '<':
/* 111 */       this.jjmatchedKind = 23;
/* 112 */       return jjMoveStringLiteralDfa1_0(285212672L);
/*     */     case '=':
/* 114 */       return jjStopAtPos(0, 27);
/*     */     case '>':
/* 116 */       this.jjmatchedKind = 25;
/* 117 */       return jjMoveStringLiteralDfa1_0(67108864L);
/*     */     case '?':
/* 119 */       return jjStopAtPos(0, 35);
/*     */     case 'A':
/*     */     case 'a':
/* 122 */       return jjMoveStringLiteralDfa1_0(64L);
/*     */     case 'B':
/*     */     case 'b':
/* 125 */       return jjMoveStringLiteralDfa1_0(128L);
/*     */     case 'E':
/*     */     case 'e':
/* 128 */       return jjMoveStringLiteralDfa1_0(16384L);
/*     */     case 'I':
/*     */     case 'i':
/* 131 */       return jjMoveStringLiteralDfa1_0(768L);
/*     */     case 'L':
/*     */     case 'l':
/* 134 */       return jjMoveStringLiteralDfa1_0(1024L);
/*     */     case 'N':
/*     */     case 'n':
/* 137 */       return jjMoveStringLiteralDfa1_0(6144L);
/*     */     case 'O':
/*     */     case 'o':
/* 140 */       return jjMoveStringLiteralDfa1_0(8192L);
/*     */     case '.':
/*     */     case '0':
/*     */     case '1':
/*     */     case '2':
/*     */     case '3':
/*     */     case '4':
/*     */     case '5':
/*     */     case '6':
/*     */     case '7':
/*     */     case '8':
/*     */     case '9':
/*     */     case ':':
/*     */     case ';':
/*     */     case '@':
/*     */     case 'C':
/*     */     case 'D':
/*     */     case 'F':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'J':
/*     */     case 'K':
/*     */     case 'M':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'S':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'V':
/*     */     case 'W':
/*     */     case 'X':
/*     */     case 'Y':
/*     */     case 'Z':
/*     */     case '[':
/*     */     case '\\':
/*     */     case ']':
/*     */     case '^':
/*     */     case '_':
/*     */     case '`':
/*     */     case 'c':
/*     */     case 'd':
/*     */     case 'f':
/*     */     case 'g':
/*     */     case 'h':
/*     */     case 'j':
/*     */     case 'k':
/* 142 */     case 'm': } return jjMoveNfa_0(0, 0);
/*     */   }
/*     */ 
/*     */   private final int jjMoveStringLiteralDfa1_0(long active0) {
/*     */     try {
/* 147 */       this.curChar = this.input_stream.readChar();
/*     */     } catch (IOException e) {
/* 149 */       jjStopStringLiteralDfa_0(0, active0);
/* 150 */       return 1;
/*     */     }
/* 152 */     switch (this.curChar)
/*     */     {
/*     */     case '=':
/* 155 */       if ((active0 & 0x1000000) != 0L)
/* 156 */         return jjStopAtPos(1, 24);
/* 157 */       if ((active0 & 0x4000000) == 0L) break;
/* 158 */       return jjStopAtPos(1, 26);
/*     */     case '>':
/* 161 */       if ((active0 & 0x10000000) == 0L) break;
/* 162 */       return jjStopAtPos(1, 28);
/*     */     case 'E':
/*     */     case 'e':
/* 166 */       return jjMoveStringLiteralDfa2_0(active0, 128L);
/*     */     case 'I':
/*     */     case 'i':
/* 169 */       return jjMoveStringLiteralDfa2_0(active0, 1024L);
/*     */     case 'N':
/*     */     case 'n':
/* 172 */       if ((active0 & 0x100) != 0L)
/* 173 */         return jjStartNfaWithStates_0(1, 8, 37);
/* 174 */       return jjMoveStringLiteralDfa2_0(active0, 64L);
/*     */     case 'O':
/*     */     case 'o':
/* 177 */       return jjMoveStringLiteralDfa2_0(active0, 2048L);
/*     */     case 'R':
/*     */     case 'r':
/* 180 */       if ((active0 & 0x2000) == 0L) break;
/* 181 */       return jjStartNfaWithStates_0(1, 13, 37);
/*     */     case 'S':
/*     */     case 's':
/* 185 */       if ((active0 & 0x200) != 0L)
/* 186 */         return jjStartNfaWithStates_0(1, 9, 37);
/* 187 */       return jjMoveStringLiteralDfa2_0(active0, 16384L);
/*     */     case 'U':
/*     */     case 'u':
/* 190 */       return jjMoveStringLiteralDfa2_0(active0, 4096L);
/*     */     case '?':
/*     */     case '@':
/*     */     case 'A':
/*     */     case 'B':
/*     */     case 'C':
/*     */     case 'D':
/*     */     case 'F':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'J':
/*     */     case 'K':
/*     */     case 'L':
/*     */     case 'M':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'T':
/*     */     case 'V':
/*     */     case 'W':
/*     */     case 'X':
/*     */     case 'Y':
/*     */     case 'Z':
/*     */     case '[':
/*     */     case '\\':
/*     */     case ']':
/*     */     case '^':
/*     */     case '_':
/*     */     case '`':
/*     */     case 'a':
/*     */     case 'b':
/*     */     case 'c':
/*     */     case 'd':
/*     */     case 'f':
/*     */     case 'g':
/*     */     case 'h':
/*     */     case 'j':
/*     */     case 'k':
/*     */     case 'l':
/*     */     case 'm':
/*     */     case 'p':
/*     */     case 'q':
/* 194 */     case 't': } return jjStartNfa_0(0, active0);
/*     */   }
/*     */ 
/*     */   private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
/* 198 */     if ((active0 &= old0) == 0L)
/* 199 */       return jjStartNfa_0(0, old0); try {
/* 200 */       this.curChar = this.input_stream.readChar();
/*     */     } catch (IOException e) {
/* 202 */       jjStopStringLiteralDfa_0(1, active0);
/* 203 */       return 2;
/*     */     }
/* 205 */     switch (this.curChar)
/*     */     {
/*     */     case 'C':
/*     */     case 'c':
/* 209 */       return jjMoveStringLiteralDfa3_0(active0, 16384L);
/*     */     case 'D':
/*     */     case 'd':
/* 212 */       if ((active0 & 0x40) == 0L) break;
/* 213 */       return jjStartNfaWithStates_0(2, 6, 37);
/*     */     case 'K':
/*     */     case 'k':
/* 217 */       return jjMoveStringLiteralDfa3_0(active0, 1024L);
/*     */     case 'L':
/*     */     case 'l':
/* 220 */       return jjMoveStringLiteralDfa3_0(active0, 4096L);
/*     */     case 'T':
/*     */     case 't':
/* 223 */       if ((active0 & 0x800) != 0L)
/* 224 */         return jjStartNfaWithStates_0(2, 11, 37);
/* 225 */       return jjMoveStringLiteralDfa3_0(active0, 128L);
/*     */     }
/*     */ 
/* 229 */     return jjStartNfa_0(1, active0);
/*     */   }
/*     */ 
/*     */   private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
/* 233 */     if ((active0 &= old0) == 0L)
/* 234 */       return jjStartNfa_0(1, old0); try {
/* 235 */       this.curChar = this.input_stream.readChar();
/*     */     } catch (IOException e) {
/* 237 */       jjStopStringLiteralDfa_0(2, active0);
/* 238 */       return 3;
/*     */     }
/* 240 */     switch (this.curChar)
/*     */     {
/*     */     case 'A':
/*     */     case 'a':
/* 244 */       return jjMoveStringLiteralDfa4_0(active0, 16384L);
/*     */     case 'E':
/*     */     case 'e':
/* 247 */       if ((active0 & 0x400) == 0L) break;
/* 248 */       return jjStartNfaWithStates_0(3, 10, 37);
/*     */     case 'L':
/*     */     case 'l':
/* 252 */       if ((active0 & 0x1000) == 0L) break;
/* 253 */       return jjStartNfaWithStates_0(3, 12, 37);
/*     */     case 'W':
/*     */     case 'w':
/* 257 */       return jjMoveStringLiteralDfa4_0(active0, 128L);
/*     */     }
/*     */ 
/* 261 */     return jjStartNfa_0(2, active0);
/*     */   }
/*     */ 
/*     */   private final int jjMoveStringLiteralDfa4_0(long old0, long active0) {
/* 265 */     if ((active0 &= old0) == 0L)
/* 266 */       return jjStartNfa_0(2, old0); try {
/* 267 */       this.curChar = this.input_stream.readChar();
/*     */     } catch (IOException e) {
/* 269 */       jjStopStringLiteralDfa_0(3, active0);
/* 270 */       return 4;
/*     */     }
/* 272 */     switch (this.curChar)
/*     */     {
/*     */     case 'E':
/*     */     case 'e':
/* 276 */       return jjMoveStringLiteralDfa5_0(active0, 128L);
/*     */     case 'P':
/*     */     case 'p':
/* 279 */       return jjMoveStringLiteralDfa5_0(active0, 16384L);
/*     */     }
/*     */ 
/* 283 */     return jjStartNfa_0(3, active0);
/*     */   }
/*     */ 
/*     */   private final int jjMoveStringLiteralDfa5_0(long old0, long active0) {
/* 287 */     if ((active0 &= old0) == 0L)
/* 288 */       return jjStartNfa_0(3, old0); try {
/* 289 */       this.curChar = this.input_stream.readChar();
/*     */     } catch (IOException e) {
/* 291 */       jjStopStringLiteralDfa_0(4, active0);
/* 292 */       return 5;
/*     */     }
/* 294 */     switch (this.curChar)
/*     */     {
/*     */     case 'E':
/*     */     case 'e':
/* 298 */       if ((active0 & 0x4000) != 0L)
/* 299 */         return jjStartNfaWithStates_0(5, 14, 37);
/* 300 */       return jjMoveStringLiteralDfa6_0(active0, 128L);
/*     */     }
/*     */ 
/* 304 */     return jjStartNfa_0(4, active0);
/*     */   }
/*     */ 
/*     */   private final int jjMoveStringLiteralDfa6_0(long old0, long active0) {
/* 308 */     if ((active0 &= old0) == 0L)
/* 309 */       return jjStartNfa_0(4, old0); try {
/* 310 */       this.curChar = this.input_stream.readChar();
/*     */     } catch (IOException e) {
/* 312 */       jjStopStringLiteralDfa_0(5, active0);
/* 313 */       return 6;
/*     */     }
/* 315 */     switch (this.curChar)
/*     */     {
/*     */     case 'N':
/*     */     case 'n':
/* 319 */       if ((active0 & 0x80) == 0L) break;
/* 320 */       return jjStartNfaWithStates_0(6, 7, 37);
/*     */     }
/*     */ 
/* 325 */     return jjStartNfa_0(5, active0);
/*     */   }
/*     */ 
/*     */   private final void jjCheckNAdd(int state) {
/* 329 */     if (this.jjrounds[state] != this.jjround)
/*     */     {
/* 331 */       this.jjstateSet[(this.jjnewStateCnt++)] = state;
/* 332 */       this.jjrounds[state] = this.jjround;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void jjAddStates(int start, int end) {
/*     */     do
/* 338 */       this.jjstateSet[(this.jjnewStateCnt++)] = jjnextStates[start];
/* 339 */     while (start++ != end);
/*     */   }
/*     */ 
/*     */   private final void jjCheckNAddTwoStates(int state1, int state2) {
/* 343 */     jjCheckNAdd(state1);
/* 344 */     jjCheckNAdd(state2);
/*     */   }
/*     */ 
/*     */   private final void jjCheckNAddStates(int start, int end) {
/*     */     do
/* 349 */       jjCheckNAdd(jjnextStates[start]);
/* 350 */     while (start++ != end);
/*     */   }
/*     */ 
/*     */   private final void jjCheckNAddStates(int start) {
/* 354 */     jjCheckNAdd(jjnextStates[start]);
/* 355 */     jjCheckNAdd(jjnextStates[(start + 1)]);
/*     */   }
/*     */ 
/*     */   private final int jjMoveNfa_0(int startState, int curPos)
/*     */   {
/* 363 */     int startsAt = 0;
/* 364 */     this.jjnewStateCnt = 37;
/* 365 */     int i = 1;
/* 366 */     this.jjstateSet[0] = startState;
/* 367 */     int kind = 2147483647;
/*     */     while (true)
/*     */     {
/* 370 */       if (++this.jjround == 2147483647)
/* 371 */         ReInitRounds();
/* 372 */       if (this.curChar < '@')
/*     */       {
/* 374 */         long l = 1L << this.curChar;
/*     */         do
/*     */         {
/* 377 */           i--; switch (this.jjstateSet[i])
/*     */           {
/*     */           case 37:
/* 380 */             if ((0x0 & l) != 0L)
/*     */             {
/* 382 */               if (kind > 20)
/* 383 */                 kind = 20;
/* 384 */               jjCheckNAdd(23);
/*     */             }
/* 386 */             if (this.curChar != '$')
/*     */               continue;
/* 388 */             if (kind > 20)
/* 389 */               kind = 20;
/* 390 */             jjCheckNAddTwoStates(22, 23); break;
/*     */           case 0:
/* 394 */             if ((0x0 & l) != 0L)
/*     */             {
/* 396 */               if (kind > 15)
/* 397 */                 kind = 15;
/* 398 */               jjCheckNAddStates(0, 6);
/*     */             }
/* 400 */             else if (this.curChar == '$')
/*     */             {
/* 402 */               if (kind > 20)
/* 403 */                 kind = 20;
/* 404 */               jjCheckNAddTwoStates(22, 23);
/*     */             }
/* 406 */             else if (this.curChar == '\'') {
/* 407 */               jjCheckNAddStates(7, 9);
/* 408 */             } else if (this.curChar == '.') {
/* 409 */               jjCheckNAdd(4);
/* 410 */             }if (this.curChar != '0') continue;
/* 411 */             this.jjstateSet[(this.jjnewStateCnt++)] = 1; break;
/*     */           case 2:
/* 414 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 416 */             if (kind > 15)
/* 417 */               kind = 15;
/* 418 */             this.jjstateSet[(this.jjnewStateCnt++)] = 2;
/* 419 */             break;
/*     */           case 3:
/* 421 */             if (this.curChar != '.') continue;
/* 422 */             jjCheckNAdd(4); break;
/*     */           case 4:
/* 425 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 427 */             if (kind > 16)
/* 428 */               kind = 16;
/* 429 */             jjCheckNAddTwoStates(4, 5);
/* 430 */             break;
/*     */           case 6:
/* 432 */             if ((0x0 & l) == 0L) continue;
/* 433 */             jjCheckNAdd(7); break;
/*     */           case 7:
/* 436 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 438 */             if (kind > 16)
/* 439 */               kind = 16;
/* 440 */             jjCheckNAdd(7);
/* 441 */             break;
/*     */           case 8:
/* 443 */             if (this.curChar != '\'') continue;
/* 444 */             jjCheckNAddStates(7, 9); break;
/*     */           case 9:
/* 447 */             if ((0xFFFFFFFF & l) == 0L) continue;
/* 448 */             jjCheckNAddStates(7, 9); break;
/*     */           case 10:
/* 451 */             if (this.curChar != '\'') continue;
/* 452 */             jjCheckNAddStates(10, 12); break;
/*     */           case 11:
/* 455 */             if (this.curChar != '\'') continue;
/* 456 */             this.jjstateSet[(this.jjnewStateCnt++)] = 10; break;
/*     */           case 12:
/* 459 */             if ((0xFFFFFFFF & l) == 0L) continue;
/* 460 */             jjCheckNAddStates(10, 12); break;
/*     */           case 13:
/* 463 */             if ((this.curChar != '\'') || (kind <= 18)) continue;
/* 464 */             kind = 18; break;
/*     */           case 22:
/* 467 */             if (this.curChar != '$')
/*     */               continue;
/* 469 */             if (kind > 20)
/* 470 */               kind = 20;
/* 471 */             jjCheckNAddTwoStates(22, 23);
/* 472 */             break;
/*     */           case 23:
/* 474 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 476 */             if (kind > 20)
/* 477 */               kind = 20;
/* 478 */             jjCheckNAdd(23);
/* 479 */             break;
/*     */           case 24:
/* 481 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 483 */             if (kind > 15)
/* 484 */               kind = 15;
/* 485 */             jjCheckNAddStates(0, 6);
/* 486 */             break;
/*     */           case 25:
/* 488 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 490 */             if (kind > 15)
/* 491 */               kind = 15;
/* 492 */             jjCheckNAdd(25);
/* 493 */             break;
/*     */           case 26:
/* 495 */             if ((0x0 & l) == 0L) continue;
/* 496 */             jjCheckNAddTwoStates(26, 3); break;
/*     */           case 27:
/* 499 */             if ((0x0 & l) == 0L) continue;
/* 500 */             jjCheckNAddTwoStates(27, 28); break;
/*     */           case 28:
/* 503 */             if (this.curChar != '.')
/*     */               continue;
/* 505 */             if (kind > 16)
/* 506 */               kind = 16;
/* 507 */             jjCheckNAddTwoStates(29, 30);
/* 508 */             break;
/*     */           case 29:
/* 510 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 512 */             if (kind > 16)
/* 513 */               kind = 16;
/* 514 */             jjCheckNAddTwoStates(29, 30);
/* 515 */             break;
/*     */           case 31:
/* 517 */             if ((0x0 & l) == 0L) continue;
/* 518 */             jjCheckNAdd(32); break;
/*     */           case 32:
/* 521 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 523 */             if (kind > 16)
/* 524 */               kind = 16;
/* 525 */             jjCheckNAdd(32);
/* 526 */             break;
/*     */           case 33:
/* 528 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 530 */             if (kind > 16)
/* 531 */               kind = 16;
/* 532 */             jjCheckNAddTwoStates(33, 34);
/* 533 */             break;
/*     */           case 35:
/* 535 */             if ((0x0 & l) == 0L) continue;
/* 536 */             jjCheckNAdd(36); break;
/*     */           case 36:
/* 539 */             if ((0x0 & l) == 0L)
/*     */               continue;
/* 541 */             if (kind > 16)
/* 542 */               kind = 16;
/* 543 */             jjCheckNAdd(36);
/*     */           case 1:
/*     */           case 5:
/*     */           case 14:
/*     */           case 15:
/*     */           case 16:
/*     */           case 17:
/*     */           case 18:
/*     */           case 19:
/*     */           case 20:
/*     */           case 21:
/*     */           case 30:
/* 547 */           case 34: }  } while (i != startsAt);
/*     */       }
/* 549 */       else if (this.curChar < '')
/*     */       {
/* 551 */         long l = 1L << (this.curChar & 0x3F);
/*     */         do
/*     */         {
/* 554 */           i--; switch (this.jjstateSet[i])
/*     */           {
/*     */           case 37:
/* 557 */             if ((0x87FFFFFE & l) != 0L)
/*     */             {
/* 559 */               if (kind > 20)
/* 560 */                 kind = 20;
/* 561 */               jjCheckNAdd(23);
/*     */             }
/* 563 */             if ((0x87FFFFFE & l) == 0L)
/*     */               continue;
/* 565 */             if (kind > 20)
/* 566 */               kind = 20;
/* 567 */             jjCheckNAddTwoStates(22, 23); break;
/*     */           case 0:
/* 571 */             if ((0x87FFFFFE & l) != 0L)
/*     */             {
/* 573 */               if (kind > 20)
/* 574 */                 kind = 20;
/* 575 */               jjCheckNAddTwoStates(22, 23);
/*     */             }
/* 577 */             if ((0x40 & l) != 0L) {
/* 578 */               this.jjstateSet[(this.jjnewStateCnt++)] = 20; } else {
/* 579 */               if ((0x100000 & l) == 0L) continue;
/* 580 */               this.jjstateSet[(this.jjnewStateCnt++)] = 16; } break;
/*     */           case 1:
/* 583 */             if ((0x1000000 & l) == 0L) continue;
/* 584 */             jjCheckNAdd(2); break;
/*     */           case 2:
/* 587 */             if ((0x3E & l) == 0L)
/*     */               continue;
/* 589 */             if (kind > 15)
/* 590 */               kind = 15;
/* 591 */             jjCheckNAdd(2);
/* 592 */             break;
/*     */           case 5:
/* 594 */             if ((0x20 & l) == 0L) continue;
/* 595 */             jjAddStates(13, 14); break;
/*     */           case 9:
/* 598 */             jjCheckNAddStates(7, 9);
/* 599 */             break;
/*     */           case 12:
/* 601 */             jjCheckNAddStates(10, 12);
/* 602 */             break;
/*     */           case 14:
/* 604 */             if (((0x20 & l) == 0L) || (kind <= 19)) continue;
/* 605 */             kind = 19; break;
/*     */           case 15:
/* 608 */             if ((0x200000 & l) == 0L) continue;
/* 609 */             jjCheckNAdd(14); break;
/*     */           case 16:
/* 612 */             if ((0x40000 & l) == 0L) continue;
/* 613 */             this.jjstateSet[(this.jjnewStateCnt++)] = 15; break;
/*     */           case 17:
/* 616 */             if ((0x100000 & l) == 0L) continue;
/* 617 */             this.jjstateSet[(this.jjnewStateCnt++)] = 16; break;
/*     */           case 18:
/* 620 */             if ((0x80000 & l) == 0L) continue;
/* 621 */             jjCheckNAdd(14); break;
/*     */           case 19:
/* 624 */             if ((0x1000 & l) == 0L) continue;
/* 625 */             this.jjstateSet[(this.jjnewStateCnt++)] = 18; break;
/*     */           case 20:
/* 628 */             if ((0x2 & l) == 0L) continue;
/* 629 */             this.jjstateSet[(this.jjnewStateCnt++)] = 19; break;
/*     */           case 21:
/* 632 */             if ((0x40 & l) == 0L) continue;
/* 633 */             this.jjstateSet[(this.jjnewStateCnt++)] = 20; break;
/*     */           case 22:
/* 636 */             if ((0x87FFFFFE & l) == 0L)
/*     */               continue;
/* 638 */             if (kind > 20)
/* 639 */               kind = 20;
/* 640 */             jjCheckNAddTwoStates(22, 23);
/* 641 */             break;
/*     */           case 23:
/* 643 */             if ((0x87FFFFFE & l) == 0L)
/*     */               continue;
/* 645 */             if (kind > 20)
/* 646 */               kind = 20;
/* 647 */             jjCheckNAdd(23);
/* 648 */             break;
/*     */           case 30:
/* 650 */             if ((0x20 & l) == 0L) continue;
/* 651 */             jjAddStates(15, 16); break;
/*     */           case 34:
/* 654 */             if ((0x20 & l) == 0L) continue;
/* 655 */             jjAddStates(17, 18);
/*     */           case 3:
/*     */           case 4:
/*     */           case 6:
/*     */           case 7:
/*     */           case 8:
/*     */           case 10:
/*     */           case 11:
/*     */           case 13:
/*     */           case 24:
/*     */           case 25:
/*     */           case 26:
/*     */           case 27:
/*     */           case 28:
/*     */           case 29:
/*     */           case 31:
/*     */           case 32:
/*     */           case 33:
/*     */           case 35:
/* 659 */           case 36: }  } while (i != startsAt);
/*     */       }
/*     */       else
/*     */       {
/* 663 */         int i2 = (this.curChar & 0xFF) >> '\006';
/* 664 */         long l2 = 1L << (this.curChar & 0x3F);
/*     */         do
/*     */         {
/* 667 */           i--; switch (this.jjstateSet[i])
/*     */           {
/*     */           case 9:
/* 670 */             if ((jjbitVec0[i2] & l2) == 0L) continue;
/* 671 */             jjCheckNAddStates(7, 9); break;
/*     */           case 12:
/* 674 */             if ((jjbitVec0[i2] & l2) == 0L) continue;
/* 675 */             jjCheckNAddStates(10, 12);
/*     */           }
/*     */         }
/*     */ 
/* 679 */         while (i != startsAt);
/*     */       }
/* 681 */       if (kind != 2147483647)
/*     */       {
/* 683 */         this.jjmatchedKind = kind;
/* 684 */         this.jjmatchedPos = curPos;
/* 685 */         kind = 2147483647;
/*     */       }
/* 687 */       curPos++;
/* 688 */       if ((i = this.jjnewStateCnt) == (startsAt = 37 - (this.jjnewStateCnt = startsAt)))
/* 689 */         return curPos; try {
/* 690 */         this.curChar = this.input_stream.readChar(); } catch (IOException e) {
/*     */       }
/* 691 */     }return curPos;
/*     */   }
/*     */ 
/*     */   public SQLParserTokenManager(SimpleCharStream stream)
/*     */   {
/* 719 */     this.input_stream = stream;
/*     */   }
/*     */ 
/*     */   public SQLParserTokenManager(SimpleCharStream stream, int lexState) {
/* 723 */     this(stream);
/* 724 */     SwitchTo(lexState);
/*     */   }
/*     */ 
/*     */   public void ReInit(SimpleCharStream stream) {
/* 728 */     this.jjmatchedPos = (this.jjnewStateCnt = 0);
/* 729 */     this.curLexState = this.defaultLexState;
/* 730 */     this.input_stream = stream;
/* 731 */     ReInitRounds();
/*     */   }
/*     */ 
/*     */   private final void ReInitRounds()
/*     */   {
/* 736 */     this.jjround = -2147483647;
/* 737 */     for (int i = 37; i-- > 0; )
/* 738 */       this.jjrounds[i] = -2147483648;
/*     */   }
/*     */ 
/*     */   public void ReInit(SimpleCharStream stream, int lexState) {
/* 742 */     ReInit(stream);
/* 743 */     SwitchTo(lexState);
/*     */   }
/*     */ 
/*     */   public void SwitchTo(int lexState) {
/* 747 */     if ((lexState >= 1) || (lexState < 0)) {
/* 748 */       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
/*     */     }
/* 750 */     this.curLexState = lexState;
/*     */   }
/*     */ 
/*     */   protected Token jjFillToken()
/*     */   {
/* 755 */     Token t = Token.newToken(this.jjmatchedKind);
/* 756 */     t.kind = this.jjmatchedKind;
/* 757 */     String im = jjstrLiteralImages[this.jjmatchedKind];
/* 758 */     t.image = (im == null ? this.input_stream.GetImage() : im);
/* 759 */     t.beginLine = this.input_stream.getBeginLine();
/* 760 */     t.beginColumn = this.input_stream.getBeginColumn();
/* 761 */     t.endLine = this.input_stream.getEndLine();
/* 762 */     t.endColumn = this.input_stream.getEndColumn();
/* 763 */     return t;
/*     */   }
/*     */ 
/*     */   public Token getNextToken()
/*     */   {
/* 776 */     Token specialToken = null;
/*     */ 
/* 778 */     int curPos = 0;
/*     */     while (true)
/*     */     {
/*     */       try
/*     */       {
/* 785 */         this.curChar = this.input_stream.BeginToken();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 789 */         this.jjmatchedKind = 0;
/* 790 */         Token matchedToken = jjFillToken();
/* 791 */         return matchedToken;
/*     */       }
/*     */       try {
/* 794 */         this.input_stream.backup(0);
/* 795 */         while ((this.curChar <= ' ') && ((0x3600 & 1L << this.curChar) != 0L))
/* 796 */           this.curChar = this.input_stream.BeginToken(); 
/*     */       } catch (IOException e1) {
/*     */       }
/* 798 */       continue;
/* 799 */       this.jjmatchedKind = 2147483647;
/* 800 */       this.jjmatchedPos = 0;
/* 801 */       curPos = jjMoveStringLiteralDfa0_0();
/* 802 */       if (this.jjmatchedKind == 2147483647)
/*     */         break;
/* 804 */       if (this.jjmatchedPos + 1 < curPos)
/* 805 */         this.input_stream.backup(curPos - this.jjmatchedPos - 1);
/* 806 */       if ((jjtoToken[(this.jjmatchedKind >> 6)] & 1L << (this.jjmatchedKind & 0x3F)) == 0L)
/*     */         continue;
/* 808 */       Token matchedToken = jjFillToken();
/* 809 */       return matchedToken;
/*     */     }
/*     */ 
/* 816 */     int error_line = this.input_stream.getEndLine();
/* 817 */     int error_column = this.input_stream.getEndColumn();
/* 818 */     String error_after = null;
/* 819 */     boolean EOFSeen = false;
/*     */     try { this.input_stream.readChar(); this.input_stream.backup(1);
/*     */     } catch (IOException e1) {
/* 822 */       EOFSeen = true;
/* 823 */       error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
/* 824 */       if ((this.curChar == '\n') || (this.curChar == '\r')) {
/* 825 */         error_line++;
/* 826 */         error_column = 0;
/*     */       }
/*     */       else {
/* 829 */         error_column++;
/*     */       }
/*     */     }
/* 831 */     if (!EOFSeen) {
/* 832 */       this.input_stream.backup(1);
/* 833 */       error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
/*     */     }
/* 835 */     throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.selector.SQLParserTokenManager
 * JD-Core Version:    0.6.0
 */