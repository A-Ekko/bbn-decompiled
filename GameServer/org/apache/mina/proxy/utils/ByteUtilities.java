/*     */ package org.apache.mina.proxy.utils;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class ByteUtilities
/*     */ {
/*     */   public static int networkByteOrderToInt(byte[] buf, int start, int count)
/*     */   {
/*  36 */     if (count > 4) {
/*  37 */       throw new IllegalArgumentException("Cannot handle more than 4 bytes");
/*     */     }
/*     */ 
/*  41 */     int result = 0;
/*     */ 
/*  43 */     for (int i = 0; i < count; i++) {
/*  44 */       result <<= 8;
/*  45 */       result |= buf[(start + i)] & 0xFF;
/*     */     }
/*     */ 
/*  48 */     return result;
/*     */   }
/*     */ 
/*     */   public static byte[] intToNetworkByteOrder(int num, byte[] buf, int start, int count)
/*     */   {
/*  57 */     if (count > 4) {
/*  58 */       throw new IllegalArgumentException("Cannot handle more than 4 bytes");
/*     */     }
/*     */ 
/*  62 */     for (int i = count - 1; i >= 0; i--) {
/*  63 */       buf[(start + i)] = (byte)(num & 0xFF);
/*  64 */       num >>>= 8;
/*     */     }
/*     */ 
/*  67 */     return buf;
/*     */   }
/*     */ 
/*     */   public static final byte[] writeShort(short v)
/*     */   {
/*  76 */     return writeShort(v, new byte[2], 0);
/*     */   }
/*     */ 
/*     */   public static final byte[] writeShort(short v, byte[] b, int offset)
/*     */   {
/*  86 */     b[offset] = (byte)v;
/*  87 */     b[(offset + 1)] = (byte)(v >> 8);
/*     */ 
/*  89 */     return b;
/*     */   }
/*     */ 
/*     */   public static final byte[] writeInt(int v)
/*     */   {
/*  98 */     return writeInt(v, new byte[4], 0);
/*     */   }
/*     */ 
/*     */   public static final byte[] writeInt(int v, byte[] b, int offset)
/*     */   {
/* 108 */     b[offset] = (byte)v;
/* 109 */     b[(offset + 1)] = (byte)(v >> 8);
/* 110 */     b[(offset + 2)] = (byte)(v >> 16);
/* 111 */     b[(offset + 3)] = (byte)(v >> 24);
/*     */ 
/* 113 */     return b;
/*     */   }
/*     */ 
/*     */   public static final void changeWordEndianess(byte[] b, int offset, int length)
/*     */   {
/* 120 */     for (int i = offset; i < offset + length; i += 4) {
/* 121 */       byte tmp = b[i];
/* 122 */       b[i] = b[(i + 3)];
/* 123 */       b[(i + 3)] = tmp;
/* 124 */       tmp = b[(i + 1)];
/* 125 */       b[(i + 1)] = b[(i + 2)];
/* 126 */       b[(i + 2)] = tmp;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final void changeByteEndianess(byte[] b, int offset, int length)
/*     */   {
/* 134 */     for (int i = offset; i < offset + length; i += 2) {
/* 135 */       byte tmp = b[i];
/* 136 */       b[i] = b[(i + 1)];
/* 137 */       b[(i + 1)] = tmp;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final byte[] getOEMStringAsByteArray(String s) throws UnsupportedEncodingException
/*     */   {
/* 143 */     return s.getBytes("ASCII");
/*     */   }
/*     */ 
/*     */   public static final byte[] getUTFStringAsByteArray(String s) throws UnsupportedEncodingException
/*     */   {
/* 148 */     return s.getBytes("UTF-16LE");
/*     */   }
/*     */ 
/*     */   public static final byte[] encodeString(String s, boolean useUnicode) throws UnsupportedEncodingException
/*     */   {
/* 153 */     if (useUnicode) {
/* 154 */       return getUTFStringAsByteArray(s);
/*     */     }
/* 156 */     return getOEMStringAsByteArray(s);
/*     */   }
/*     */ 
/*     */   public static String asHex(byte[] bytes)
/*     */   {
/* 161 */     return asHex(bytes, null);
/*     */   }
/*     */ 
/*     */   public static String asHex(byte[] bytes, String separator) {
/* 165 */     StringBuilder sb = new StringBuilder();
/* 166 */     for (int i = 0; i < bytes.length; i++) {
/* 167 */       String code = Integer.toHexString(bytes[i] & 0xFF);
/* 168 */       if ((bytes[i] & 0xFF) < 16) {
/* 169 */         sb.append('0');
/*     */       }
/*     */ 
/* 172 */       sb.append(code);
/*     */ 
/* 174 */       if ((separator != null) && (i < bytes.length - 1)) {
/* 175 */         sb.append(separator);
/*     */       }
/*     */     }
/*     */ 
/* 179 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static byte[] asByteArray(String hex) {
/* 183 */     byte[] bts = new byte[hex.length() / 2];
/* 184 */     for (int i = 0; i < bts.length; i++) {
/* 185 */       bts[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
/*     */     }
/*     */ 
/* 189 */     return bts;
/*     */   }
/*     */ 
/*     */   public static final int makeIntFromByte4(byte[] b) {
/* 193 */     return makeIntFromByte4(b, 0);
/*     */   }
/*     */ 
/*     */   public static final int makeIntFromByte4(byte[] b, int offset) {
/* 197 */     return b[offset] << 24 | (b[(offset + 1)] & 0xFF) << 16 | (b[(offset + 2)] & 0xFF) << 8 | b[(offset + 3)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public static final int makeIntFromByte2(byte[] b)
/*     */   {
/* 202 */     return makeIntFromByte2(b, 0);
/*     */   }
/*     */ 
/*     */   public static final int makeIntFromByte2(byte[] b, int offset) {
/* 206 */     return (b[offset] & 0xFF) << 8 | b[(offset + 1)] & 0xFF;
/*     */   }
/*     */ 
/*     */   public static final boolean isFlagSet(int flagSet, int testFlag)
/*     */   {
/* 218 */     return (flagSet & testFlag) > 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.utils.ByteUtilities
 * JD-Core Version:    0.6.0
 */