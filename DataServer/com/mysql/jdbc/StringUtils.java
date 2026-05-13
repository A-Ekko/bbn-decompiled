/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ public class StringUtils
/*      */ {
/*      */   private static final int BYTE_RANGE = 256;
/*   49 */   private static byte[] allBytes = new byte[256];
/*      */ 
/*   51 */   private static char[] byteToChars = new char[256];
/*      */   private static Method toPlainStringMethod;
/*      */   static final int WILD_COMPARE_MATCH_NO_WILD = 0;
/*      */   static final int WILD_COMPARE_MATCH_WITH_WILD = 1;
/*      */   static final int WILD_COMPARE_NO_MATCH = -1;
/*      */ 
/*      */   public static String consistentToString(BigDecimal decimal)
/*      */   {
/*   94 */     if (decimal == null) {
/*   95 */       return null;
/*      */     }
/*      */ 
/*   98 */     if (toPlainStringMethod != null)
/*      */       try {
/*  100 */         return (String)toPlainStringMethod.invoke(decimal, null);
/*      */       }
/*      */       catch (InvocationTargetException invokeEx)
/*      */       {
/*      */       }
/*      */       catch (IllegalAccessException accessEx)
/*      */       {
/*      */       }
/*  108 */     return decimal.toString();
/*      */   }
/*      */ 
/*      */   public static final String dumpAsHex(byte[] byteBuffer, int length)
/*      */   {
/*  122 */     StringBuffer outputBuf = new StringBuffer(length * 4);
/*      */ 
/*  124 */     int p = 0;
/*  125 */     int rows = length / 8;
/*      */ 
/*  127 */     for (int i = 0; (i < rows) && (p < length); i++) {
/*  128 */       int ptemp = p;
/*      */ 
/*  130 */       for (int j = 0; j < 8; j++) {
/*  131 */         String hexVal = Integer.toHexString(byteBuffer[ptemp] & 0xFF);
/*      */ 
/*  133 */         if (hexVal.length() == 1) {
/*  134 */           hexVal = "0" + hexVal;
/*      */         }
/*      */ 
/*  137 */         outputBuf.append(hexVal + " ");
/*  138 */         ptemp++;
/*      */       }
/*      */ 
/*  141 */       outputBuf.append("    ");
/*      */ 
/*  143 */       for (int j = 0; j < 8; j++) {
/*  144 */         if ((byteBuffer[p] > 32) && (byteBuffer[p] < 127))
/*  145 */           outputBuf.append((char)byteBuffer[p] + " ");
/*      */         else {
/*  147 */           outputBuf.append(". ");
/*      */         }
/*      */ 
/*  150 */         p++;
/*      */       }
/*      */ 
/*  153 */       outputBuf.append("\n");
/*      */     }
/*      */ 
/*  156 */     int n = 0;
/*      */ 
/*  158 */     for (int i = p; i < length; i++) {
/*  159 */       String hexVal = Integer.toHexString(byteBuffer[i] & 0xFF);
/*      */ 
/*  161 */       if (hexVal.length() == 1) {
/*  162 */         hexVal = "0" + hexVal;
/*      */       }
/*      */ 
/*  165 */       outputBuf.append(hexVal + " ");
/*  166 */       n++;
/*      */     }
/*      */ 
/*  169 */     for (int i = n; i < 8; i++) {
/*  170 */       outputBuf.append("   ");
/*      */     }
/*      */ 
/*  173 */     outputBuf.append("    ");
/*      */ 
/*  175 */     for (int i = p; i < length; i++) {
/*  176 */       if ((byteBuffer[i] > 32) && (byteBuffer[i] < 127))
/*  177 */         outputBuf.append((char)byteBuffer[i] + " ");
/*      */       else {
/*  179 */         outputBuf.append(". ");
/*      */       }
/*      */     }
/*      */ 
/*  183 */     outputBuf.append("\n");
/*      */ 
/*  185 */     return outputBuf.toString();
/*      */   }
/*      */ 
/*      */   private static boolean endsWith(byte[] dataFrom, String suffix) {
/*  189 */     for (int i = 1; i <= suffix.length(); i++) {
/*  190 */       int dfOffset = dataFrom.length - i;
/*  191 */       int suffixOffset = suffix.length() - i;
/*  192 */       if (dataFrom[dfOffset] != suffix.charAt(suffixOffset)) {
/*  193 */         return false;
/*      */       }
/*      */     }
/*  196 */     return true;
/*      */   }
/*      */ 
/*      */   public static byte[] escapeEasternUnicodeByteStream(byte[] origBytes, String origString, int offset, int length)
/*      */   {
/*  216 */     if ((origBytes == null) || (origBytes.length == 0)) {
/*  217 */       return origBytes;
/*      */     }
/*      */ 
/*  220 */     int bytesLen = origBytes.length;
/*  221 */     int bufIndex = 0;
/*  222 */     int strIndex = 0;
/*      */ 
/*  224 */     ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(bytesLen);
/*      */     while (true)
/*      */     {
/*  227 */       if (origString.charAt(strIndex) == '\\')
/*      */       {
/*  229 */         bytesOut.write(origBytes[(bufIndex++)]);
/*      */       }
/*      */       else
/*      */       {
/*  234 */         int loByte = origBytes[bufIndex];
/*      */ 
/*  236 */         if (loByte < 0) {
/*  237 */           loByte += 256;
/*      */         }
/*      */ 
/*  241 */         bytesOut.write(loByte);
/*      */ 
/*  259 */         if (loByte >= 128) {
/*  260 */           if (bufIndex < bytesLen - 1) {
/*  261 */             int hiByte = origBytes[(bufIndex + 1)];
/*      */ 
/*  263 */             if (hiByte < 0) {
/*  264 */               hiByte += 256;
/*      */             }
/*      */ 
/*  269 */             bytesOut.write(hiByte);
/*  270 */             bufIndex++;
/*      */ 
/*  273 */             if (hiByte == 92)
/*  274 */               bytesOut.write(hiByte);
/*      */           }
/*      */         }
/*  277 */         else if ((loByte == 92) && 
/*  278 */           (bufIndex < bytesLen - 1)) {
/*  279 */           int hiByte = origBytes[(bufIndex + 1)];
/*      */ 
/*  281 */           if (hiByte < 0) {
/*  282 */             hiByte += 256;
/*      */           }
/*      */ 
/*  285 */           if (hiByte == 98)
/*      */           {
/*  287 */             bytesOut.write(92);
/*  288 */             bytesOut.write(98);
/*  289 */             bufIndex++;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  294 */         bufIndex++;
/*      */       }
/*      */ 
/*  297 */       if (bufIndex >= bytesLen)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  302 */       strIndex++;
/*      */     }
/*      */ 
/*  305 */     return bytesOut.toByteArray();
/*      */   }
/*      */ 
/*      */   public static char firstNonWsCharUc(String searchIn)
/*      */   {
/*  317 */     if (searchIn == null) {
/*  318 */       return '\000';
/*      */     }
/*      */ 
/*  321 */     int length = searchIn.length();
/*      */ 
/*  323 */     for (int i = 0; i < length; i++) {
/*  324 */       char c = searchIn.charAt(i);
/*      */ 
/*  326 */       if (!Character.isWhitespace(c)) {
/*  327 */         return Character.toUpperCase(c);
/*      */       }
/*      */     }
/*      */ 
/*  331 */     return '\000';
/*      */   }
/*      */ 
/*      */   public static final String fixDecimalExponent(String dString)
/*      */   {
/*  344 */     int ePos = dString.indexOf("E");
/*      */ 
/*  346 */     if (ePos == -1) {
/*  347 */       ePos = dString.indexOf("e");
/*      */     }
/*      */ 
/*  350 */     if ((ePos != -1) && 
/*  351 */       (dString.length() > ePos + 1)) {
/*  352 */       char maybeMinusChar = dString.charAt(ePos + 1);
/*      */ 
/*  354 */       if ((maybeMinusChar != '-') && (maybeMinusChar != '+')) {
/*  355 */         StringBuffer buf = new StringBuffer(dString.length() + 1);
/*  356 */         buf.append(dString.substring(0, ePos + 1));
/*  357 */         buf.append('+');
/*  358 */         buf.append(dString.substring(ePos + 1, dString.length()));
/*  359 */         dString = buf.toString();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  364 */     return dString;
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  372 */       byte[] b = null;
/*      */ 
/*  374 */       if (converter != null) {
/*  375 */         b = converter.toBytes(c);
/*  376 */       } else if (encoding == null) {
/*  377 */         b = new String(c).getBytes();
/*      */       } else {
/*  379 */         String s = new String(c);
/*      */ 
/*  381 */         b = s.getBytes(encoding);
/*      */ 
/*  383 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  387 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  388 */             b = escapeEasternUnicodeByteStream(b, s, 0, s.length());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  393 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  395 */     throw new SQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009");
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(char[] c, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  406 */       byte[] b = null;
/*      */ 
/*  408 */       if (converter != null) {
/*  409 */         b = converter.toBytes(c, offset, length);
/*  410 */       } else if (encoding == null) {
/*  411 */         byte[] temp = new String(c, offset, length).getBytes();
/*      */ 
/*  413 */         length = temp.length;
/*      */ 
/*  415 */         b = new byte[length];
/*  416 */         System.arraycopy(temp, 0, b, 0, length);
/*      */       } else {
/*  418 */         String s = new String(c, offset, length);
/*      */ 
/*  420 */         byte[] temp = s.getBytes(encoding);
/*      */ 
/*  422 */         length = temp.length;
/*      */ 
/*  424 */         b = new byte[length];
/*  425 */         System.arraycopy(temp, 0, b, 0, length);
/*      */ 
/*  427 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  431 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  432 */             b = escapeEasternUnicodeByteStream(b, s, offset, length);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  437 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  439 */     throw new SQLException(Messages.getString("StringUtils.10") + encoding + Messages.getString("StringUtils.11"), "S1009");
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(char[] c, String encoding, String serverEncoding, boolean parserKnowsUnicode)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  449 */       SingleByteCharsetConverter converter = SingleByteCharsetConverter.getInstance(encoding, null);
/*      */ 
/*  452 */       return getBytes(c, converter, encoding, serverEncoding, parserKnowsUnicode);
/*      */     } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  455 */     throw new SQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), "S1009");
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, boolean parserKnowsUnicode)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  486 */       byte[] b = null;
/*      */ 
/*  488 */       if (converter != null) {
/*  489 */         b = converter.toBytes(s);
/*  490 */       } else if (encoding == null) {
/*  491 */         b = s.getBytes();
/*      */       } else {
/*  493 */         b = s.getBytes(encoding);
/*      */ 
/*  495 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  499 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  500 */             b = escapeEasternUnicodeByteStream(b, s, 0, s.length());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  505 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  507 */     throw new SQLException(Messages.getString("StringUtils.5") + encoding + Messages.getString("StringUtils.6"), "S1009");
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(String s, SingleByteCharsetConverter converter, String encoding, String serverEncoding, int offset, int length, boolean parserKnowsUnicode)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  541 */       byte[] b = null;
/*      */ 
/*  543 */       if (converter != null) {
/*  544 */         b = converter.toBytes(s, offset, length);
/*  545 */       } else if (encoding == null) {
/*  546 */         byte[] temp = s.substring(offset, offset + length).getBytes();
/*      */ 
/*  548 */         length = temp.length;
/*      */ 
/*  550 */         b = new byte[length];
/*  551 */         System.arraycopy(temp, 0, b, 0, length);
/*      */       }
/*      */       else {
/*  554 */         byte[] temp = s.substring(offset, offset + length).getBytes(encoding);
/*      */ 
/*  557 */         length = temp.length;
/*      */ 
/*  559 */         b = new byte[length];
/*  560 */         System.arraycopy(temp, 0, b, 0, length);
/*      */ 
/*  562 */         if ((!parserKnowsUnicode) && ((encoding.equalsIgnoreCase("SJIS")) || (encoding.equalsIgnoreCase("BIG5")) || (encoding.equalsIgnoreCase("GBK"))))
/*      */         {
/*  566 */           if (!encoding.equalsIgnoreCase(serverEncoding)) {
/*  567 */             b = escapeEasternUnicodeByteStream(b, s, offset, length);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  572 */       return b; } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  574 */     throw new SQLException(Messages.getString("StringUtils.10") + encoding + Messages.getString("StringUtils.11"), "S1009");
/*      */   }
/*      */ 
/*      */   public static final byte[] getBytes(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  600 */       SingleByteCharsetConverter converter = SingleByteCharsetConverter.getInstance(encoding, null);
/*      */ 
/*  603 */       return getBytes(s, converter, encoding, serverEncoding, parserKnowsUnicode);
/*      */     } catch (UnsupportedEncodingException uee) {
/*      */     }
/*  606 */     throw new SQLException(Messages.getString("StringUtils.0") + encoding + Messages.getString("StringUtils.1"), "S1009");
/*      */   }
/*      */ 
/*      */   public static int getInt(byte[] buf)
/*      */     throws NumberFormatException
/*      */   {
/*  613 */     int base = 10;
/*      */ 
/*  615 */     int s = 0;
/*      */ 
/*  618 */     while ((Character.isWhitespace((char)buf[s])) && (s < buf.length)) {
/*  619 */       s++;
/*      */     }
/*      */ 
/*  622 */     if (s == buf.length) {
/*  623 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  627 */     boolean negative = false;
/*      */ 
/*  629 */     if ((char)buf[s] == '-') {
/*  630 */       negative = true;
/*  631 */       s++;
/*  632 */     } else if ((char)buf[s] == '+') {
/*  633 */       s++;
/*      */     }
/*      */ 
/*  637 */     int save = s;
/*      */ 
/*  639 */     int cutoff = 2147483647 / base;
/*  640 */     int cutlim = 2147483647 % base;
/*      */ 
/*  642 */     if (negative) {
/*  643 */       cutlim++;
/*      */     }
/*      */ 
/*  646 */     boolean overflow = false;
/*      */ 
/*  648 */     int i = 0;
/*      */ 
/*  650 */     for (; s < buf.length; s++) {
/*  651 */       char c = (char)buf[s];
/*      */ 
/*  653 */       if (Character.isDigit(c)) {
/*  654 */         c = (char)(c - '0'); } else {
/*  655 */         if (!Character.isLetter(c)) break;
/*  656 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */ 
/*  661 */       if (c >= base)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  666 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  667 */         overflow = true;
/*      */       } else {
/*  669 */         i *= base;
/*  670 */         i += c;
/*      */       }
/*      */     }
/*      */ 
/*  674 */     if (s == save) {
/*  675 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  678 */     if (overflow) {
/*  679 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  683 */     return negative ? -i : i;
/*      */   }
/*      */ 
/*      */   public static long getLong(byte[] buf) throws NumberFormatException {
/*  687 */     int base = 10;
/*      */ 
/*  689 */     int s = 0;
/*      */ 
/*  692 */     while ((Character.isWhitespace((char)buf[s])) && (s < buf.length)) {
/*  693 */       s++;
/*      */     }
/*      */ 
/*  696 */     if (s == buf.length) {
/*  697 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  701 */     boolean negative = false;
/*      */ 
/*  703 */     if ((char)buf[s] == '-') {
/*  704 */       negative = true;
/*  705 */       s++;
/*  706 */     } else if ((char)buf[s] == '+') {
/*  707 */       s++;
/*      */     }
/*      */ 
/*  711 */     int save = s;
/*      */ 
/*  713 */     long cutoff = 9223372036854775807L / base;
/*  714 */     long cutlim = (int)(9223372036854775807L % base);
/*      */ 
/*  716 */     if (negative) {
/*  717 */       cutlim += 1L;
/*      */     }
/*      */ 
/*  720 */     boolean overflow = false;
/*  721 */     long i = 0L;
/*      */ 
/*  723 */     for (; s < buf.length; s++) {
/*  724 */       char c = (char)buf[s];
/*      */ 
/*  726 */       if (Character.isDigit(c)) {
/*  727 */         c = (char)(c - '0'); } else {
/*  728 */         if (!Character.isLetter(c)) break;
/*  729 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */ 
/*  734 */       if (c >= base)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  739 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  740 */         overflow = true;
/*      */       } else {
/*  742 */         i *= base;
/*  743 */         i += c;
/*      */       }
/*      */     }
/*      */ 
/*  747 */     if (s == save) {
/*  748 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  751 */     if (overflow) {
/*  752 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  756 */     return negative ? -i : i;
/*      */   }
/*      */ 
/*      */   public static short getShort(byte[] buf) throws NumberFormatException {
/*  760 */     short base = 10;
/*      */ 
/*  762 */     int s = 0;
/*      */ 
/*  765 */     while ((Character.isWhitespace((char)buf[s])) && (s < buf.length)) {
/*  766 */       s++;
/*      */     }
/*      */ 
/*  769 */     if (s == buf.length) {
/*  770 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  774 */     boolean negative = false;
/*      */ 
/*  776 */     if ((char)buf[s] == '-') {
/*  777 */       negative = true;
/*  778 */       s++;
/*  779 */     } else if ((char)buf[s] == '+') {
/*  780 */       s++;
/*      */     }
/*      */ 
/*  784 */     int save = s;
/*      */ 
/*  786 */     short cutoff = (short)(32767 / base);
/*  787 */     short cutlim = (short)(32767 % base);
/*      */ 
/*  789 */     if (negative) {
/*  790 */       cutlim = (short)(cutlim + 1);
/*      */     }
/*      */ 
/*  793 */     boolean overflow = false;
/*  794 */     short i = 0;
/*      */ 
/*  796 */     for (; s < buf.length; s++) {
/*  797 */       char c = (char)buf[s];
/*      */ 
/*  799 */       if (Character.isDigit(c)) {
/*  800 */         c = (char)(c - '0'); } else {
/*  801 */         if (!Character.isLetter(c)) break;
/*  802 */         c = (char)(Character.toUpperCase(c) - 'A' + 10);
/*      */       }
/*      */ 
/*  807 */       if (c >= base)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/*  812 */       if ((i > cutoff) || ((i == cutoff) && (c > cutlim))) {
/*  813 */         overflow = true;
/*      */       } else {
/*  815 */         i = (short)(i * base);
/*  816 */         i = (short)(i + c);
/*      */       }
/*      */     }
/*      */ 
/*  820 */     if (s == save) {
/*  821 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  824 */     if (overflow) {
/*  825 */       throw new NumberFormatException(new String(buf));
/*      */     }
/*      */ 
/*  829 */     return negative ? (short)(-i) : i;
/*      */   }
/*      */ 
/*      */   public static final int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor)
/*      */   {
/*  834 */     if ((searchIn == null) || (searchFor == null) || (startingPosition > searchIn.length()))
/*      */     {
/*  836 */       return -1;
/*      */     }
/*      */ 
/*  839 */     int patternLength = searchFor.length();
/*  840 */     int stringLength = searchIn.length();
/*  841 */     int stopSearchingAt = stringLength - patternLength;
/*      */ 
/*  843 */     int i = startingPosition;
/*      */ 
/*  845 */     if (patternLength == 0) {
/*  846 */       return -1;
/*      */     }
/*      */ 
/*  851 */     char firstCharOfPatternUc = Character.toUpperCase(searchFor.charAt(0));
/*  852 */     char firstCharOfPatternLc = Character.toLowerCase(searchFor.charAt(0));
/*      */ 
/*  857 */     while ((i < stopSearchingAt) && (Character.toUpperCase(searchIn.charAt(i)) != firstCharOfPatternUc) && (Character.toLowerCase(searchIn.charAt(i)) != firstCharOfPatternLc)) {
/*  858 */       i++;
/*      */     }
/*      */ 
/*  861 */     if (i > stopSearchingAt) {
/*  862 */       return -1;
/*      */     }
/*      */ 
/*  865 */     int j = i + 1;
/*  866 */     int end = j + patternLength - 1;
/*      */ 
/*  868 */     int k = 1;
/*      */     while (true) {
/*  870 */       if (j >= end) break label209; int searchInPos = j++;
/*  872 */       int searchForPos = k++;
/*      */ 
/*  874 */       if (Character.toUpperCase(searchIn.charAt(searchInPos)) != Character.toUpperCase(searchFor.charAt(searchForPos)))
/*      */       {
/*  876 */         i++;
/*      */ 
/*  879 */         break;
/*      */       }
/*      */ 
/*  885 */       if (Character.toLowerCase(searchIn.charAt(searchInPos)) != Character.toLowerCase(searchFor.charAt(searchForPos)))
/*      */       {
/*  887 */         i++;
/*      */ 
/*  890 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  894 */     label209: return i;
/*      */   }
/*      */ 
/*      */   public static final int indexOfIgnoreCase(String searchIn, String searchFor)
/*      */   {
/*  909 */     return indexOfIgnoreCase(0, searchIn, searchFor);
/*      */   }
/*      */ 
/*      */   public static int indexOfIgnoreCaseRespectMarker(int startAt, String src, String target, String marker, String markerCloses, boolean allowBackslashEscapes)
/*      */   {
/*  915 */     char contextMarker = '\000';
/*  916 */     boolean escaped = false;
/*  917 */     int markerTypeFound = 0;
/*  918 */     int srcLength = src.length();
/*  919 */     int ind = 0;
/*      */ 
/*  921 */     for (int i = startAt; i < srcLength; i++) {
/*  922 */       char c = src.charAt(i);
/*      */ 
/*  924 */       if ((allowBackslashEscapes) && (c == '\\')) {
/*  925 */         escaped = !escaped;
/*  926 */       } else if ((c == markerCloses.charAt(markerTypeFound)) && (!escaped)) {
/*  927 */         contextMarker = '\000';
/*  928 */       } else if (((ind = marker.indexOf(c)) != -1) && (!escaped) && (contextMarker == 0))
/*      */       {
/*  930 */         markerTypeFound = ind;
/*  931 */         contextMarker = c; } else {
/*  932 */         if ((c != target.charAt(0)) || (escaped) || (contextMarker != 0))
/*      */           continue;
/*  934 */         if (indexOfIgnoreCase(i, src, target) != -1) {
/*  935 */           return i;
/*      */         }
/*      */       }
/*      */     }
/*  939 */     return -1;
/*      */   }
/*      */ 
/*      */   public static int indexOfIgnoreCaseRespectQuotes(int startAt, String src, String target, char quoteChar, boolean allowBackslashEscapes)
/*      */   {
/*  945 */     char contextMarker = '\000';
/*  946 */     boolean escaped = false;
/*      */ 
/*  948 */     int srcLength = src.length();
/*      */ 
/*  950 */     for (int i = startAt; i < srcLength; i++) {
/*  951 */       char c = src.charAt(i);
/*      */ 
/*  953 */       if ((allowBackslashEscapes) && (c == '\\')) {
/*  954 */         escaped = !escaped;
/*  955 */       } else if ((c == contextMarker) && (!escaped)) {
/*  956 */         contextMarker = '\000';
/*  957 */       } else if ((c == quoteChar) && (!escaped) && (contextMarker == 0))
/*      */       {
/*  959 */         contextMarker = c; } else {
/*  960 */         if ((c != target.charAt(0)) || (escaped) || (contextMarker != 0))
/*      */           continue;
/*  962 */         if (startsWithIgnoreCase(src, i, target)) {
/*  963 */           return i;
/*      */         }
/*      */       }
/*      */     }
/*  967 */     return -1;
/*      */   }
/*      */ 
/*      */   public static final List split(String stringToSplit, String delimitter, boolean trim)
/*      */   {
/*  988 */     if (stringToSplit == null) {
/*  989 */       return new ArrayList();
/*      */     }
/*      */ 
/*  992 */     if (delimitter == null) {
/*  993 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*  996 */     StringTokenizer tokenizer = new StringTokenizer(stringToSplit, delimitter, false);
/*      */ 
/*  999 */     List splitTokens = new ArrayList(tokenizer.countTokens());
/*      */ 
/* 1001 */     while (tokenizer.hasMoreTokens()) {
/* 1002 */       String token = tokenizer.nextToken();
/*      */ 
/* 1004 */       if (trim) {
/* 1005 */         token = token.trim();
/*      */       }
/*      */ 
/* 1008 */       splitTokens.add(token);
/*      */     }
/*      */ 
/* 1011 */     return splitTokens;
/*      */   }
/*      */ 
/*      */   public static final List split(String stringToSplit, String delimiter, String markers, String markerCloses, boolean trim)
/*      */   {
/* 1031 */     if (stringToSplit == null) {
/* 1032 */       return new ArrayList();
/*      */     }
/*      */ 
/* 1035 */     if (delimiter == null) {
/* 1036 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/* 1039 */     int delimPos = 0;
/* 1040 */     int currentPos = 0;
/*      */ 
/* 1042 */     List splitTokens = new ArrayList();
/*      */ 
/* 1045 */     while ((delimPos = indexOfIgnoreCaseRespectMarker(currentPos, stringToSplit, delimiter, markers, markerCloses, false)) != -1) {
/* 1046 */       String token = stringToSplit.substring(currentPos, delimPos);
/*      */ 
/* 1048 */       if (trim) {
/* 1049 */         token = token.trim();
/*      */       }
/*      */ 
/* 1052 */       splitTokens.add(token);
/* 1053 */       currentPos = delimPos + 1;
/*      */     }
/*      */ 
/* 1056 */     if (currentPos < stringToSplit.length()) {
/* 1057 */       String token = stringToSplit.substring(currentPos);
/*      */ 
/* 1059 */       if (trim) {
/* 1060 */         token = token.trim();
/*      */       }
/*      */ 
/* 1063 */       splitTokens.add(token);
/*      */     }
/*      */ 
/* 1066 */     return splitTokens;
/*      */   }
/*      */ 
/*      */   private static boolean startsWith(byte[] dataFrom, String chars) {
/* 1070 */     for (int i = 0; i < chars.length(); i++) {
/* 1071 */       if (dataFrom[i] != chars.charAt(i)) {
/* 1072 */         return false;
/*      */       }
/*      */     }
/* 1075 */     return true;
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCase(String searchIn, int startAt, String searchFor)
/*      */   {
/* 1094 */     return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCase(String searchIn, String searchFor)
/*      */   {
/* 1110 */     return startsWithIgnoreCase(searchIn, 0, searchFor);
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor)
/*      */   {
/* 1127 */     if (searchIn == null) {
/* 1128 */       return searchFor == null;
/*      */     }
/*      */ 
/* 1131 */     int beginPos = 0;
/*      */ 
/* 1133 */     int inLength = searchIn.length();
/*      */ 
/* 1135 */     for (beginPos = 0; beginPos < inLength; beginPos++) {
/* 1136 */       char c = searchIn.charAt(beginPos);
/*      */ 
/* 1138 */       if (Character.isLetterOrDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 1143 */     return startsWithIgnoreCase(searchIn, beginPos, searchFor);
/*      */   }
/*      */ 
/*      */   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor)
/*      */   {
/* 1159 */     if (searchIn == null) {
/* 1160 */       return searchFor == null;
/*      */     }
/*      */ 
/* 1163 */     int beginPos = 0;
/*      */ 
/* 1165 */     int inLength = searchIn.length();
/*      */ 
/* 1167 */     for (beginPos = 0; (beginPos < inLength) && 
/* 1168 */       (Character.isWhitespace(searchIn.charAt(beginPos))); beginPos++);
/* 1173 */     return startsWithIgnoreCase(searchIn, beginPos, searchFor);
/*      */   }
/*      */ 
/*      */   public static byte[] stripEnclosure(byte[] source, String prefix, String suffix)
/*      */   {
/* 1184 */     if ((source.length >= prefix.length() + suffix.length()) && (startsWith(source, prefix)) && (endsWith(source, suffix)))
/*      */     {
/* 1187 */       int totalToStrip = prefix.length() + suffix.length();
/* 1188 */       int enclosedLength = source.length - totalToStrip;
/* 1189 */       byte[] enclosed = new byte[enclosedLength];
/*      */ 
/* 1191 */       int startPos = prefix.length();
/* 1192 */       int numToCopy = enclosed.length;
/* 1193 */       System.arraycopy(source, startPos, enclosed, 0, numToCopy);
/*      */ 
/* 1195 */       return enclosed;
/*      */     }
/* 1197 */     return source;
/*      */   }
/*      */ 
/*      */   public static final String toAsciiString(byte[] buffer)
/*      */   {
/* 1209 */     return toAsciiString(buffer, 0, buffer.length);
/*      */   }
/*      */ 
/*      */   public static final String toAsciiString(byte[] buffer, int startPos, int length)
/*      */   {
/* 1226 */     char[] charArray = new char[length];
/* 1227 */     int readpoint = startPos;
/*      */ 
/* 1229 */     for (int i = 0; i < length; i++) {
/* 1230 */       charArray[i] = (char)buffer[readpoint];
/* 1231 */       readpoint++;
/*      */     }
/*      */ 
/* 1234 */     return new String(charArray);
/*      */   }
/*      */ 
/*      */   public static int wildCompare(String searchIn, String searchForWildcard)
/*      */   {
/* 1252 */     if ((searchIn == null) || (searchForWildcard == null)) {
/* 1253 */       return -1;
/*      */     }
/*      */ 
/* 1256 */     if (searchForWildcard.equals("%"))
/*      */     {
/* 1258 */       return 1;
/*      */     }
/*      */ 
/* 1261 */     int result = -1;
/*      */ 
/* 1263 */     char wildcardMany = '%';
/* 1264 */     char wildcardOne = '_';
/* 1265 */     char wildcardEscape = '\\';
/*      */ 
/* 1267 */     int searchForPos = 0;
/* 1268 */     int searchForEnd = searchForWildcard.length();
/*      */ 
/* 1270 */     int searchInPos = 0;
/* 1271 */     int searchInEnd = searchIn.length();
/*      */ 
/* 1273 */     while (searchForPos != searchForEnd) {
/* 1274 */       char wildstrChar = searchForWildcard.charAt(searchForPos);
/*      */ 
/* 1277 */       while ((searchForWildcard.charAt(searchForPos) != wildcardMany) && (wildstrChar != wildcardOne)) {
/* 1278 */         if ((searchForWildcard.charAt(searchForPos) == wildcardEscape) && (searchForPos + 1 != searchForEnd))
/*      */         {
/* 1280 */           searchForPos++;
/*      */         }
/*      */ 
/* 1283 */         if ((searchInPos == searchInEnd) || (Character.toUpperCase(searchForWildcard.charAt(searchForPos++)) != Character.toUpperCase(searchIn.charAt(searchInPos++))))
/*      */         {
/* 1287 */           return 1;
/*      */         }
/*      */ 
/* 1290 */         if (searchForPos == searchForEnd) {
/* 1291 */           return searchInPos != searchInEnd ? 1 : 0;
/*      */         }
/*      */ 
/* 1298 */         result = 1;
/*      */       }
/*      */ 
/* 1301 */       if (searchForWildcard.charAt(searchForPos) == wildcardOne) {
/*      */         do {
/* 1303 */           if (searchInPos == searchInEnd)
/*      */           {
/* 1308 */             return result;
/*      */           }
/*      */ 
/* 1311 */           searchInPos++;
/*      */ 
/* 1313 */           searchForPos++; } while ((searchForPos < searchForEnd) && (searchForWildcard.charAt(searchForPos) == wildcardOne));
/*      */ 
/* 1315 */         if (searchForPos == searchForEnd)
/*      */         {
/*      */           break;
/*      */         }
/*      */       }
/* 1320 */       if (searchForWildcard.charAt(searchForPos) == wildcardMany)
/*      */       {
/* 1327 */         searchForPos++;
/*      */ 
/* 1330 */         for (; searchForPos != searchForEnd; searchForPos++) {
/* 1331 */           if (searchForWildcard.charAt(searchForPos) == wildcardMany)
/*      */           {
/*      */             continue;
/*      */           }
/* 1335 */           if (searchForWildcard.charAt(searchForPos) != wildcardOne) break;
/* 1336 */           if (searchInPos == searchInEnd) {
/* 1337 */             return -1;
/*      */           }
/*      */ 
/* 1340 */           searchInPos++;
/*      */         }
/*      */ 
/* 1348 */         if (searchForPos == searchForEnd) {
/* 1349 */           return 0;
/*      */         }
/*      */ 
/* 1352 */         if (searchInPos == searchInEnd)
/* 1353 */           return -1;
/*      */         char cmp;
/* 1356 */         if (((cmp = searchForWildcard.charAt(searchForPos)) == wildcardEscape) && (searchForPos + 1 != searchForEnd))
/*      */         {
/* 1358 */           searchForPos++; cmp = searchForWildcard.charAt(searchForPos);
/*      */         }
/*      */ 
/* 1361 */         searchForPos++;
/*      */         do
/*      */         {
/* 1365 */           while ((searchInPos != searchInEnd) && (Character.toUpperCase(searchIn.charAt(searchInPos)) != Character.toUpperCase(cmp)))
/*      */           {
/* 1368 */             searchInPos++;
/*      */           }
/* 1370 */           if (searchInPos++ == searchInEnd) {
/* 1371 */             return -1;
/*      */           }
/*      */ 
/* 1375 */           int tmp = wildCompare(searchIn, searchForWildcard);
/*      */ 
/* 1377 */           if (tmp <= 0) {
/* 1378 */             return tmp;
/*      */           }
/*      */         }
/*      */ 
/* 1382 */         while ((searchInPos != searchInEnd) && (searchForWildcard.charAt(0) != wildcardMany));
/*      */ 
/* 1384 */         return -1;
/*      */       }
/*      */     }
/*      */ 
/* 1388 */     return searchInPos != searchInEnd ? 1 : 0;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   62 */     for (int i = -128; i <= 127; i++) {
/*   63 */       allBytes[(i - -128)] = (byte)i;
/*      */     }
/*      */ 
/*   66 */     String allBytesString = new String(allBytes, 0, 255);
/*      */ 
/*   69 */     int allBytesStringLen = allBytesString.length();
/*      */ 
/*   71 */     int i = 0;
/*   72 */     for (; (i < 255) && (i < allBytesStringLen); i++) {
/*   73 */       byteToChars[i] = allBytesString.charAt(i);
/*      */     }
/*      */     try
/*      */     {
/*   77 */       toPlainStringMethod = BigDecimal.class.getMethod("toPlainString", new Class[0]);
/*      */     }
/*      */     catch (NoSuchMethodException nsme)
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.StringUtils
 * JD-Core Version:    0.6.0
 */