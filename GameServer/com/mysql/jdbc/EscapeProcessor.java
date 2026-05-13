/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ class EscapeProcessor
/*     */ {
/*     */   private static Map JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
/*     */   private static Map JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP;
/*     */ 
/*     */   public static final Object escapeSQL(String sql, boolean serverSupportsConvertFn)
/*     */     throws SQLException
/*     */   {
/*  99 */     boolean replaceEscapeSequence = false;
/* 100 */     String escapeSequence = null;
/*     */ 
/* 102 */     if (sql == null) {
/* 103 */       return null;
/*     */     }
/*     */ 
/* 110 */     int beginBrace = sql.indexOf('{');
/* 111 */     int nextEndBrace = beginBrace == -1 ? -1 : sql.indexOf('}', beginBrace);
/*     */ 
/* 114 */     if (nextEndBrace == -1) {
/* 115 */       return sql;
/*     */     }
/*     */ 
/* 118 */     StringBuffer newSql = new StringBuffer();
/*     */ 
/* 120 */     EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
/*     */ 
/* 122 */     byte usesVariables = 0;
/* 123 */     boolean callingStoredFunction = false;
/*     */ 
/* 125 */     while (escapeTokenizer.hasMoreTokens()) {
/* 126 */       String token = escapeTokenizer.nextToken();
/*     */ 
/* 128 */       if (token.length() != 0) {
/* 129 */         if (token.charAt(0) == '{')
/*     */         {
/* 131 */           if (!token.endsWith("}")) {
/* 132 */             throw new SQLException("Not a valid escape sequence: " + token);
/*     */           }
/*     */ 
/* 136 */           if (token.length() > 2) {
/* 137 */             int nestedBrace = token.indexOf('{', 2);
/*     */ 
/* 139 */             if (nestedBrace != -1) {
/* 140 */               StringBuffer buf = new StringBuffer(token.substring(0, 1));
/*     */ 
/* 143 */               Object remainingResults = escapeSQL(token.substring(1, token.length() - 1), serverSupportsConvertFn);
/*     */ 
/* 147 */               String remaining = null;
/*     */ 
/* 149 */               if ((remainingResults instanceof String)) {
/* 150 */                 remaining = (String)remainingResults;
/*     */               } else {
/* 152 */                 remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
/*     */ 
/* 154 */                 if (usesVariables != 1) {
/* 155 */                   usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
/*     */                 }
/*     */               }
/*     */ 
/* 159 */               buf.append(remaining);
/*     */ 
/* 161 */               buf.append('}');
/*     */ 
/* 163 */               token = buf.toString();
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 169 */           String collapsedToken = removeWhitespace(token);
/*     */ 
/* 174 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{escape"))
/*     */           {
/*     */             try {
/* 177 */               StringTokenizer st = new StringTokenizer(token, " '");
/*     */ 
/* 179 */               st.nextToken();
/* 180 */               escapeSequence = st.nextToken();
/*     */ 
/* 182 */               if (escapeSequence.length() < 3) {
/* 183 */                 throw new SQLException("Syntax error for escape sequence '" + token + "'", "42000");
/*     */               }
/*     */ 
/* 188 */               escapeSequence = escapeSequence.substring(1, escapeSequence.length() - 1);
/*     */ 
/* 190 */               replaceEscapeSequence = true;
/*     */             } catch (NoSuchElementException e) {
/* 192 */               throw new SQLException("Syntax error for escape sequence '" + token + "'", "42000");
/*     */             }
/*     */ 
/*     */           }
/* 196 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{fn"))
/*     */           {
/* 198 */             int startPos = token.toLowerCase().indexOf("fn ") + 3;
/* 199 */             int endPos = token.length() - 1;
/*     */ 
/* 201 */             String fnToken = token.substring(startPos, endPos);
/*     */ 
/* 205 */             if (StringUtils.startsWithIgnoreCaseAndWs(fnToken, "convert"))
/*     */             {
/* 207 */               newSql.append(processConvertToken(fnToken, serverSupportsConvertFn));
/*     */             }
/*     */             else
/*     */             {
/* 211 */               newSql.append(fnToken);
/*     */             }
/* 213 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{d"))
/*     */           {
/* 215 */             int startPos = token.indexOf('\'') + 1;
/* 216 */             int endPos = token.lastIndexOf('\'');
/*     */ 
/* 218 */             if ((startPos == -1) || (endPos == -1)) {
/* 219 */               throw new SQLException("Syntax error for DATE escape sequence '" + token + "'", "42000");
/*     */             }
/*     */ 
/* 224 */             String argument = token.substring(startPos, endPos);
/*     */             try
/*     */             {
/* 227 */               StringTokenizer st = new StringTokenizer(argument, " -");
/*     */ 
/* 229 */               String year4 = st.nextToken();
/* 230 */               String month2 = st.nextToken();
/* 231 */               String day2 = st.nextToken();
/* 232 */               String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
/*     */ 
/* 234 */               newSql.append(dateString);
/*     */             } catch (NoSuchElementException e) {
/* 236 */               throw new SQLException("Syntax error for DATE escape sequence '" + argument + "'", "42000");
/*     */             }
/*     */ 
/*     */           }
/* 240 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{ts"))
/*     */           {
/* 242 */             int startPos = token.indexOf('\'') + 1;
/* 243 */             int endPos = token.lastIndexOf('\'');
/*     */ 
/* 245 */             if ((startPos == -1) || (endPos == -1)) {
/* 246 */               throw new SQLException("Syntax error for TIMESTAMP escape sequence '" + token + "'", "42000");
/*     */             }
/*     */ 
/* 251 */             String argument = token.substring(startPos, endPos);
/*     */             try
/*     */             {
/* 254 */               StringTokenizer st = new StringTokenizer(argument, " .-:");
/*     */ 
/* 256 */               String year4 = st.nextToken();
/* 257 */               String month2 = st.nextToken();
/* 258 */               String day2 = st.nextToken();
/* 259 */               String hour = st.nextToken();
/* 260 */               String minute = st.nextToken();
/* 261 */               String second = st.nextToken();
/*     */ 
/* 287 */               newSql.append("'").append(year4).append("-").append(month2).append("-").append(day2).append(" ").append(hour).append(":").append(minute).append(":").append(second).append("'");
/*     */             }
/*     */             catch (NoSuchElementException e)
/*     */             {
/* 293 */               throw new SQLException("Syntax error for TIMESTAMP escape sequence '" + argument + "'", "42000");
/*     */             }
/*     */ 
/*     */           }
/* 297 */           else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{t"))
/*     */           {
/* 299 */             int startPos = token.indexOf('\'') + 1;
/* 300 */             int endPos = token.lastIndexOf('\'');
/*     */ 
/* 302 */             if ((startPos == -1) || (endPos == -1)) {
/* 303 */               throw new SQLException("Syntax error for TIME escape sequence '" + token + "'", "42000");
/*     */             }
/*     */ 
/* 308 */             String argument = token.substring(startPos, endPos);
/*     */             try
/*     */             {
/* 311 */               StringTokenizer st = new StringTokenizer(argument, " :");
/*     */ 
/* 313 */               String hour = st.nextToken();
/* 314 */               String minute = st.nextToken();
/* 315 */               String second = st.nextToken();
/* 316 */               String timeString = "'" + hour + ":" + minute + ":" + second + "'";
/*     */ 
/* 318 */               newSql.append(timeString);
/*     */             } catch (NoSuchElementException e) {
/* 320 */               throw new SQLException("Syntax error for escape sequence '" + argument + "'", "42000");
/*     */             }
/*     */ 
/*     */           }
/* 324 */           else if ((StringUtils.startsWithIgnoreCase(collapsedToken, "{call")) || (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")))
/*     */           {
/* 329 */             int startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
/*     */ 
/* 331 */             int endPos = token.length() - 1;
/*     */ 
/* 333 */             if (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call"))
/*     */             {
/* 335 */               callingStoredFunction = true;
/* 336 */               newSql.append("SELECT ");
/* 337 */               newSql.append(token.substring(startPos, endPos));
/*     */             } else {
/* 339 */               callingStoredFunction = false;
/* 340 */               newSql.append("CALL ");
/* 341 */               newSql.append(token.substring(startPos, endPos));
/*     */             }
/* 343 */           } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{oj"))
/*     */           {
/* 347 */             newSql.append(token);
/*     */           }
/*     */         } else {
/* 350 */           newSql.append(token);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 355 */     String escapedSql = newSql.toString();
/*     */ 
/* 361 */     if (replaceEscapeSequence) {
/* 362 */       String currentSql = escapedSql;
/*     */ 
/* 364 */       while (currentSql.indexOf(escapeSequence) != -1) {
/* 365 */         int escapePos = currentSql.indexOf(escapeSequence);
/* 366 */         String lhs = currentSql.substring(0, escapePos);
/* 367 */         String rhs = currentSql.substring(escapePos + 1, currentSql.length());
/*     */ 
/* 369 */         currentSql = lhs + "\\" + rhs;
/*     */       }
/*     */ 
/* 372 */       escapedSql = currentSql;
/*     */     }
/*     */ 
/* 375 */     EscapeProcessorResult epr = new EscapeProcessorResult();
/* 376 */     epr.escapedSql = escapedSql;
/* 377 */     epr.callingStoredFunction = callingStoredFunction;
/*     */ 
/* 379 */     if (usesVariables != 1) {
/* 380 */       if (escapeTokenizer.sawVariableUse())
/* 381 */         epr.usesVariables = 1;
/*     */       else {
/* 383 */         epr.usesVariables = 0;
/*     */       }
/*     */     }
/*     */ 
/* 387 */     return epr;
/*     */   }
/*     */ 
/*     */   private static String processConvertToken(String functionToken, boolean serverSupportsConvertFn)
/*     */     throws SQLException
/*     */   {
/* 430 */     int firstIndexOfParen = functionToken.indexOf("(");
/*     */ 
/* 432 */     if (firstIndexOfParen == -1) {
/* 433 */       throw new SQLException("Syntax error while processing {fn convert (... , ...)} token, missing opening parenthesis in token '" + functionToken + "'.", "42000");
/*     */     }
/*     */ 
/* 439 */     int tokenLength = functionToken.length();
/*     */ 
/* 441 */     int indexOfComma = functionToken.lastIndexOf(",");
/*     */ 
/* 443 */     if (indexOfComma == -1) {
/* 444 */       throw new SQLException("Syntax error while processing {fn convert (... , ...)} token, missing comma in token '" + functionToken + "'.", "42000");
/*     */     }
/*     */ 
/* 450 */     int indexOfCloseParen = functionToken.indexOf(')', indexOfComma);
/*     */ 
/* 452 */     if (indexOfCloseParen == -1) {
/* 453 */       throw new SQLException("Syntax error while processing {fn convert (... , ...)} token, missing closing parenthesis in token '" + functionToken + "'.", "42000");
/*     */     }
/*     */ 
/* 460 */     String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
/*     */ 
/* 462 */     String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
/*     */ 
/* 465 */     String newType = null;
/*     */ 
/* 467 */     String trimmedType = type.trim();
/*     */ 
/* 469 */     if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
/* 470 */       trimmedType = trimmedType.substring(4, trimmedType.length());
/*     */     }
/*     */ 
/* 473 */     if (serverSupportsConvertFn) {
/* 474 */       newType = (String)JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */     }
/*     */     else {
/* 477 */       newType = (String)JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */ 
/* 487 */       if (newType == null) {
/* 488 */         throw new SQLException("Can't find conversion re-write for type '" + type + "' that is applicable for this server version while processing escape tokens.", "S1000");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 496 */     if (newType == null) {
/* 497 */       throw new SQLException("Unsupported conversion type '" + type.trim() + "' found while processing escape token.", "S1000");
/*     */     }
/*     */ 
/* 502 */     int replaceIndex = newType.indexOf("?");
/*     */ 
/* 504 */     if (replaceIndex != -1) {
/* 505 */       StringBuffer convertRewrite = new StringBuffer(newType.substring(0, replaceIndex));
/*     */ 
/* 507 */       convertRewrite.append(expression);
/* 508 */       convertRewrite.append(newType.substring(replaceIndex + 1, newType.length()));
/*     */ 
/* 511 */       return convertRewrite.toString();
/*     */     }
/*     */ 
/* 514 */     StringBuffer castRewrite = new StringBuffer("CAST(");
/* 515 */     castRewrite.append(expression);
/* 516 */     castRewrite.append(" AS ");
/* 517 */     castRewrite.append(newType);
/* 518 */     castRewrite.append(")");
/*     */ 
/* 520 */     return castRewrite.toString();
/*     */   }
/*     */ 
/*     */   private static String removeWhitespace(String toCollapse)
/*     */   {
/* 534 */     if (toCollapse == null) {
/* 535 */       return null;
/*     */     }
/*     */ 
/* 538 */     int length = toCollapse.length();
/*     */ 
/* 540 */     StringBuffer collapsed = new StringBuffer(length);
/*     */ 
/* 542 */     for (int i = 0; i < length; i++) {
/* 543 */       char c = toCollapse.charAt(i);
/*     */ 
/* 545 */       if (!Character.isWhitespace(c)) {
/* 546 */         collapsed.append(c);
/*     */       }
/*     */     }
/*     */ 
/* 550 */     return collapsed.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     Map tempMap = new HashMap();
/*     */ 
/*  48 */     tempMap.put("BIGINT", "0 + ?");
/*  49 */     tempMap.put("BINARY", "BINARY");
/*  50 */     tempMap.put("BIT", "0 + ?");
/*  51 */     tempMap.put("CHAR", "CHAR");
/*  52 */     tempMap.put("DATE", "DATE");
/*  53 */     tempMap.put("DECIMAL", "0.0 + ?");
/*  54 */     tempMap.put("DOUBLE", "0.0 + ?");
/*  55 */     tempMap.put("FLOAT", "0.0 + ?");
/*  56 */     tempMap.put("INTEGER", "0 + ?");
/*  57 */     tempMap.put("LONGVARBINARY", "BINARY");
/*  58 */     tempMap.put("LONGVARCHAR", "CONCAT(?)");
/*  59 */     tempMap.put("REAL", "0.0 + ?");
/*  60 */     tempMap.put("SMALLINT", "CONCAT(?)");
/*  61 */     tempMap.put("TIME", "TIME");
/*  62 */     tempMap.put("TIMESTAMP", "DATETIME");
/*  63 */     tempMap.put("TINYINT", "CONCAT(?)");
/*  64 */     tempMap.put("VARBINARY", "BINARY");
/*  65 */     tempMap.put("VARCHAR", "CONCAT(?)");
/*     */ 
/*  67 */     JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
/*     */ 
/*  69 */     tempMap = new HashMap(JDBC_CONVERT_TO_MYSQL_TYPE_MAP);
/*     */ 
/*  71 */     tempMap.put("BINARY", "CONCAT(?)");
/*  72 */     tempMap.put("CHAR", "CONCAT(?)");
/*  73 */     tempMap.remove("DATE");
/*  74 */     tempMap.put("LONGVARBINARY", "CONCAT(?)");
/*  75 */     tempMap.remove("TIME");
/*  76 */     tempMap.remove("TIMESTAMP");
/*  77 */     tempMap.put("VARBINARY", "CONCAT(?)");
/*     */ 
/*  79 */     JDBC_NO_CONVERT_TO_MYSQL_EXPRESSION_MAP = Collections.unmodifiableMap(tempMap);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.EscapeProcessor
 * JD-Core Version:    0.6.0
 */