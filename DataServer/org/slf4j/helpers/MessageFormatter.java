/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class MessageFormatter
/*     */ {
/*     */   static final char DELIM_START = '{';
/*     */   static final char DELIM_STOP = '}';
/*     */   static final String DELIM_STR = "{}";
/*     */   private static final char ESCAPE_CHAR = '\\';
/*     */ 
/*     */   public static final String format(String messagePattern, Object arg)
/*     */   {
/* 114 */     return arrayFormat(messagePattern, new Object[] { arg });
/*     */   }
/*     */ 
/*     */   public static final String format(String messagePattern, Object arg1, Object arg2)
/*     */   {
/* 142 */     return arrayFormat(messagePattern, new Object[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */   public static final String arrayFormat(String messagePattern, Object[] argArray)
/*     */   {
/* 159 */     if (messagePattern == null) {
/* 160 */       return null;
/*     */     }
/* 162 */     if (argArray == null) {
/* 163 */       return messagePattern;
/*     */     }
/* 165 */     int i = 0;
/*     */ 
/* 167 */     StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);
/*     */ 
/* 169 */     for (int L = 0; L < argArray.length; L++)
/*     */     {
/* 171 */       int j = messagePattern.indexOf("{}", i);
/*     */ 
/* 173 */       if (j == -1)
/*     */       {
/* 175 */         if (i == 0) {
/* 176 */           return messagePattern;
/*     */         }
/*     */ 
/* 179 */         sbuf.append(messagePattern.substring(i, messagePattern.length()));
/* 180 */         return sbuf.toString();
/*     */       }
/*     */ 
/* 183 */       if (isEscapedDelimeter(messagePattern, j)) {
/* 184 */         if (!isDoubleEscaped(messagePattern, j)) {
/* 185 */           L--;
/* 186 */           sbuf.append(messagePattern.substring(i, j - 1));
/* 187 */           sbuf.append('{');
/* 188 */           i = j + 1;
/*     */         }
/*     */         else
/*     */         {
/* 193 */           sbuf.append(messagePattern.substring(i, j - 1));
/* 194 */           deeplyAppendParameter(sbuf, argArray[L], new HashMap());
/* 195 */           i = j + 2;
/*     */         }
/*     */       }
/*     */       else {
/* 199 */         sbuf.append(messagePattern.substring(i, j));
/* 200 */         deeplyAppendParameter(sbuf, argArray[L], new HashMap());
/* 201 */         i = j + 2;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 206 */     sbuf.append(messagePattern.substring(i, messagePattern.length()));
/* 207 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex)
/*     */   {
/* 213 */     if (delimeterStartIndex == 0) {
/* 214 */       return false;
/*     */     }
/* 216 */     char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
/*     */ 
/* 218 */     return potentialEscape == '\\';
/*     */   }
/*     */ 
/*     */   static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex)
/*     */   {
/* 228 */     return (delimeterStartIndex >= 2) && (messagePattern.charAt(delimeterStartIndex - 2) == '\\');
/*     */   }
/*     */ 
/*     */   private static void deeplyAppendParameter(StringBuffer sbuf, Object o, Map seenMap)
/*     */   {
/* 237 */     if (o == null) {
/* 238 */       sbuf.append("null");
/* 239 */       return;
/*     */     }
/* 241 */     if (!o.getClass().isArray()) {
/* 242 */       safeObjectAppend(sbuf, o);
/*     */     }
/* 246 */     else if ((o instanceof boolean[]))
/* 247 */       booleanArrayAppend(sbuf, (boolean[])(boolean[])o);
/* 248 */     else if ((o instanceof byte[]))
/* 249 */       byteArrayAppend(sbuf, (byte[])(byte[])o);
/* 250 */     else if ((o instanceof char[]))
/* 251 */       charArrayAppend(sbuf, (char[])(char[])o);
/* 252 */     else if ((o instanceof short[]))
/* 253 */       shortArrayAppend(sbuf, (short[])(short[])o);
/* 254 */     else if ((o instanceof int[]))
/* 255 */       intArrayAppend(sbuf, (int[])(int[])o);
/* 256 */     else if ((o instanceof long[]))
/* 257 */       longArrayAppend(sbuf, (long[])(long[])o);
/* 258 */     else if ((o instanceof float[]))
/* 259 */       floatArrayAppend(sbuf, (float[])(float[])o);
/* 260 */     else if ((o instanceof double[]))
/* 261 */       doubleArrayAppend(sbuf, (double[])(double[])o);
/*     */     else
/* 263 */       objectArrayAppend(sbuf, (Object[])(Object[])o, seenMap);
/*     */   }
/*     */ 
/*     */   private static void safeObjectAppend(StringBuffer sbuf, Object o)
/*     */   {
/*     */     try
/*     */     {
/* 270 */       String oAsString = o.toString();
/* 271 */       sbuf.append(oAsString);
/*     */     } catch (Throwable t) {
/* 273 */       System.err.println("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]");
/* 274 */       t.printStackTrace();
/* 275 */       sbuf.append("[FAILED toString()]");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void objectArrayAppend(StringBuffer sbuf, Object[] a, Map seenMap)
/*     */   {
/* 282 */     sbuf.append('[');
/* 283 */     if (!seenMap.containsKey(a)) {
/* 284 */       seenMap.put(a, null);
/* 285 */       int len = a.length;
/* 286 */       for (int i = 0; i < len; i++) {
/* 287 */         deeplyAppendParameter(sbuf, a[i], seenMap);
/* 288 */         if (i != len - 1) {
/* 289 */           sbuf.append(", ");
/*     */         }
/*     */       }
/* 292 */       seenMap.remove(a);
/*     */     } else {
/* 294 */       sbuf.append("...");
/*     */     }
/* 296 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void booleanArrayAppend(StringBuffer sbuf, boolean[] a) {
/* 300 */     sbuf.append('[');
/* 301 */     int len = a.length;
/* 302 */     for (int i = 0; i < len; i++) {
/* 303 */       sbuf.append(a[i]);
/* 304 */       if (i != len - 1)
/* 305 */         sbuf.append(", ");
/*     */     }
/* 307 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void byteArrayAppend(StringBuffer sbuf, byte[] a) {
/* 311 */     sbuf.append('[');
/* 312 */     int len = a.length;
/* 313 */     for (int i = 0; i < len; i++) {
/* 314 */       sbuf.append(a[i]);
/* 315 */       if (i != len - 1)
/* 316 */         sbuf.append(", ");
/*     */     }
/* 318 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void charArrayAppend(StringBuffer sbuf, char[] a) {
/* 322 */     sbuf.append('[');
/* 323 */     int len = a.length;
/* 324 */     for (int i = 0; i < len; i++) {
/* 325 */       sbuf.append(a[i]);
/* 326 */       if (i != len - 1)
/* 327 */         sbuf.append(", ");
/*     */     }
/* 329 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void shortArrayAppend(StringBuffer sbuf, short[] a) {
/* 333 */     sbuf.append('[');
/* 334 */     int len = a.length;
/* 335 */     for (int i = 0; i < len; i++) {
/* 336 */       sbuf.append(a[i]);
/* 337 */       if (i != len - 1)
/* 338 */         sbuf.append(", ");
/*     */     }
/* 340 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void intArrayAppend(StringBuffer sbuf, int[] a) {
/* 344 */     sbuf.append('[');
/* 345 */     int len = a.length;
/* 346 */     for (int i = 0; i < len; i++) {
/* 347 */       sbuf.append(a[i]);
/* 348 */       if (i != len - 1)
/* 349 */         sbuf.append(", ");
/*     */     }
/* 351 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void longArrayAppend(StringBuffer sbuf, long[] a) {
/* 355 */     sbuf.append('[');
/* 356 */     int len = a.length;
/* 357 */     for (int i = 0; i < len; i++) {
/* 358 */       sbuf.append(a[i]);
/* 359 */       if (i != len - 1)
/* 360 */         sbuf.append(", ");
/*     */     }
/* 362 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void floatArrayAppend(StringBuffer sbuf, float[] a) {
/* 366 */     sbuf.append('[');
/* 367 */     int len = a.length;
/* 368 */     for (int i = 0; i < len; i++) {
/* 369 */       sbuf.append(a[i]);
/* 370 */       if (i != len - 1)
/* 371 */         sbuf.append(", ");
/*     */     }
/* 373 */     sbuf.append(']');
/*     */   }
/*     */ 
/*     */   private static void doubleArrayAppend(StringBuffer sbuf, double[] a) {
/* 377 */     sbuf.append('[');
/* 378 */     int len = a.length;
/* 379 */     for (int i = 0; i < len; i++) {
/* 380 */       sbuf.append(a[i]);
/* 381 */       if (i != len - 1)
/* 382 */         sbuf.append(", ");
/*     */     }
/* 384 */     sbuf.append(']');
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.helpers.MessageFormatter
 * JD-Core Version:    0.6.0
 */