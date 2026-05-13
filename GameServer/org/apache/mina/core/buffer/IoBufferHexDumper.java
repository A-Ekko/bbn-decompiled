/*     */ package org.apache.mina.core.buffer;
/*     */ 
/*     */ class IoBufferHexDumper
/*     */ {
/*     */   private static final byte[] highDigits;
/*     */   private static final byte[] lowDigits;
/*     */ 
/*     */   public static String getHexdump(IoBuffer in, int lengthLimit)
/*     */   {
/*  68 */     if (lengthLimit == 0) {
/*  69 */       throw new IllegalArgumentException("lengthLimit: " + lengthLimit + " (expected: 1+)");
/*     */     }
/*     */ 
/*  73 */     boolean truncate = in.remaining() > lengthLimit;
/*     */     int size;
/*     */     int size;
/*  75 */     if (truncate)
/*  76 */       size = lengthLimit;
/*     */     else {
/*  78 */       size = in.remaining();
/*     */     }
/*     */ 
/*  81 */     if (size == 0) {
/*  82 */       return "empty";
/*     */     }
/*     */ 
/*  85 */     StringBuilder out = new StringBuilder(in.remaining() * 3 - 1);
/*     */ 
/*  87 */     int mark = in.position();
/*     */ 
/*  90 */     int byteValue = in.get() & 0xFF;
/*  91 */     out.append((char)highDigits[byteValue]);
/*  92 */     out.append((char)lowDigits[byteValue]);
/*  93 */     size--;
/*     */ 
/*  96 */     for (; size > 0; size--) {
/*  97 */       out.append(' ');
/*  98 */       byteValue = in.get() & 0xFF;
/*  99 */       out.append((char)highDigits[byteValue]);
/* 100 */       out.append((char)lowDigits[byteValue]);
/*     */     }
/*     */ 
/* 103 */     in.position(mark);
/*     */ 
/* 105 */     if (truncate) {
/* 106 */       out.append("...");
/*     */     }
/*     */ 
/* 109 */     return out.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  44 */     byte[] digits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*     */ 
/*  48 */     byte[] high = new byte[256];
/*  49 */     byte[] low = new byte[256];
/*     */ 
/*  51 */     for (int i = 0; i < 256; i++) {
/*  52 */       high[i] = digits[(i >>> 4)];
/*  53 */       low[i] = digits[(i & 0xF)];
/*     */     }
/*     */ 
/*  56 */     highDigits = high;
/*  57 */     lowDigits = low;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.buffer.IoBufferHexDumper
 * JD-Core Version:    0.6.0
 */