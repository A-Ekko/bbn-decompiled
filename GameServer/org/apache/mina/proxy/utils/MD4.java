/*     */ package org.apache.mina.proxy.utils;
/*     */ 
/*     */ import java.security.DigestException;
/*     */ import java.security.MessageDigestSpi;
/*     */ 
/*     */ public class MD4 extends MessageDigestSpi
/*     */ {
/*     */   public static final int BYTE_DIGEST_LENGTH = 16;
/*     */   public static final int BYTE_BLOCK_LENGTH = 64;
/*     */   private static final int A = 1732584193;
/*     */   private static final int B = -271733879;
/*     */   private static final int C = -1732584194;
/*     */   private static final int D = 271733878;
/*  46 */   private int a = 1732584193;
/*     */ 
/*  48 */   private int b = -271733879;
/*     */ 
/*  50 */   private int c = -1732584194;
/*     */ 
/*  52 */   private int d = 271733878;
/*     */   private long msgLength;
/*  62 */   private final byte[] buffer = new byte[64];
/*     */ 
/*     */   protected int engineGetDigestLength()
/*     */   {
/*  76 */     return 16;
/*     */   }
/*     */ 
/*     */   protected void engineUpdate(byte b)
/*     */   {
/*  83 */     int pos = (int)(this.msgLength % 64L);
/*  84 */     this.buffer[pos] = b;
/*  85 */     this.msgLength += 1L;
/*     */ 
/*  88 */     if (pos == 63)
/*  89 */       process(this.buffer, 0);
/*     */   }
/*     */ 
/*     */   protected void engineUpdate(byte[] b, int offset, int len)
/*     */   {
/*  97 */     int pos = (int)(this.msgLength % 64L);
/*  98 */     int nbOfCharsToFillBuf = 64 - pos;
/*  99 */     int blkStart = 0;
/*     */ 
/* 101 */     this.msgLength += len;
/*     */ 
/* 104 */     if (len >= nbOfCharsToFillBuf) {
/* 105 */       System.arraycopy(b, offset, this.buffer, pos, nbOfCharsToFillBuf);
/* 106 */       process(this.buffer, 0);
/* 107 */       blkStart = nbOfCharsToFillBuf;
/* 108 */       for (; blkStart + 64 - 1 < len; blkStart += 64) {
/* 109 */         process(b, offset + blkStart);
/*     */       }
/* 111 */       pos = 0;
/*     */     }
/*     */ 
/* 115 */     if (blkStart < len)
/* 116 */       System.arraycopy(b, offset + blkStart, this.buffer, pos, len - blkStart);
/*     */   }
/*     */ 
/*     */   protected byte[] engineDigest()
/*     */   {
/* 124 */     byte[] p = pad();
/* 125 */     engineUpdate(p, 0, p.length);
/* 126 */     byte[] digest = { (byte)this.a, (byte)(this.a >>> 8), (byte)(this.a >>> 16), (byte)(this.a >>> 24), (byte)this.b, (byte)(this.b >>> 8), (byte)(this.b >>> 16), (byte)(this.b >>> 24), (byte)this.c, (byte)(this.c >>> 8), (byte)(this.c >>> 16), (byte)(this.c >>> 24), (byte)this.d, (byte)(this.d >>> 8), (byte)(this.d >>> 16), (byte)(this.d >>> 24) };
/*     */ 
/* 133 */     engineReset();
/*     */ 
/* 135 */     return digest;
/*     */   }
/*     */ 
/*     */   protected int engineDigest(byte[] buf, int offset, int len)
/*     */     throws DigestException
/*     */   {
/* 143 */     if ((offset < 0) || (offset + len >= buf.length)) {
/* 144 */       throw new DigestException("Wrong offset or not enough space to store the digest");
/*     */     }
/*     */ 
/* 147 */     int destLength = Math.min(len, 16);
/* 148 */     System.arraycopy(engineDigest(), 0, buf, offset, destLength);
/* 149 */     return destLength;
/*     */   }
/*     */ 
/*     */   protected void engineReset()
/*     */   {
/* 156 */     this.a = 1732584193;
/* 157 */     this.b = -271733879;
/* 158 */     this.c = -1732584194;
/* 159 */     this.d = 271733878;
/* 160 */     this.msgLength = 0L;
/*     */   }
/*     */ 
/*     */   private byte[] pad()
/*     */   {
/* 175 */     int pos = (int)(this.msgLength % 64L);
/* 176 */     int padLength = pos < 56 ? 64 - pos : 128 - pos;
/* 177 */     byte[] pad = new byte[padLength];
/*     */ 
/* 180 */     pad[0] = -128;
/*     */ 
/* 182 */     long bits = this.msgLength << 3;
/* 183 */     int index = padLength - 8;
/* 184 */     for (int i = 0; i < 8; i++) {
/* 185 */       pad[(index++)] = (byte)(int)(bits >>> (i << 3));
/*     */     }
/*     */ 
/* 188 */     return pad;
/*     */   }
/*     */ 
/*     */   private void process(byte[] in, int offset)
/*     */   {
/* 200 */     int aa = this.a;
/* 201 */     int bb = this.b;
/* 202 */     int cc = this.c;
/* 203 */     int dd = this.d;
/*     */ 
/* 206 */     int[] X = new int[16];
/* 207 */     for (int i = 0; i < 16; i++) {
/* 208 */       X[i] = (in[(offset++)] & 0xFF | (in[(offset++)] & 0xFF) << 8 | (in[(offset++)] & 0xFF) << 16 | (in[(offset++)] & 0xFF) << 24);
/*     */     }
/*     */ 
/* 213 */     this.a += (this.b & this.c | (this.b ^ 0xFFFFFFFF) & this.d) + X[0];
/* 214 */     this.a = (this.a << 3 | this.a >>> 29);
/* 215 */     this.d += (this.a & this.b | (this.a ^ 0xFFFFFFFF) & this.c) + X[1];
/* 216 */     this.d = (this.d << 7 | this.d >>> 25);
/* 217 */     this.c += (this.d & this.a | (this.d ^ 0xFFFFFFFF) & this.b) + X[2];
/* 218 */     this.c = (this.c << 11 | this.c >>> 21);
/* 219 */     this.b += (this.c & this.d | (this.c ^ 0xFFFFFFFF) & this.a) + X[3];
/* 220 */     this.b = (this.b << 19 | this.b >>> 13);
/* 221 */     this.a += (this.b & this.c | (this.b ^ 0xFFFFFFFF) & this.d) + X[4];
/* 222 */     this.a = (this.a << 3 | this.a >>> 29);
/* 223 */     this.d += (this.a & this.b | (this.a ^ 0xFFFFFFFF) & this.c) + X[5];
/* 224 */     this.d = (this.d << 7 | this.d >>> 25);
/* 225 */     this.c += (this.d & this.a | (this.d ^ 0xFFFFFFFF) & this.b) + X[6];
/* 226 */     this.c = (this.c << 11 | this.c >>> 21);
/* 227 */     this.b += (this.c & this.d | (this.c ^ 0xFFFFFFFF) & this.a) + X[7];
/* 228 */     this.b = (this.b << 19 | this.b >>> 13);
/* 229 */     this.a += (this.b & this.c | (this.b ^ 0xFFFFFFFF) & this.d) + X[8];
/* 230 */     this.a = (this.a << 3 | this.a >>> 29);
/* 231 */     this.d += (this.a & this.b | (this.a ^ 0xFFFFFFFF) & this.c) + X[9];
/* 232 */     this.d = (this.d << 7 | this.d >>> 25);
/* 233 */     this.c += (this.d & this.a | (this.d ^ 0xFFFFFFFF) & this.b) + X[10];
/* 234 */     this.c = (this.c << 11 | this.c >>> 21);
/* 235 */     this.b += (this.c & this.d | (this.c ^ 0xFFFFFFFF) & this.a) + X[11];
/* 236 */     this.b = (this.b << 19 | this.b >>> 13);
/* 237 */     this.a += (this.b & this.c | (this.b ^ 0xFFFFFFFF) & this.d) + X[12];
/* 238 */     this.a = (this.a << 3 | this.a >>> 29);
/* 239 */     this.d += (this.a & this.b | (this.a ^ 0xFFFFFFFF) & this.c) + X[13];
/* 240 */     this.d = (this.d << 7 | this.d >>> 25);
/* 241 */     this.c += (this.d & this.a | (this.d ^ 0xFFFFFFFF) & this.b) + X[14];
/* 242 */     this.c = (this.c << 11 | this.c >>> 21);
/* 243 */     this.b += (this.c & this.d | (this.c ^ 0xFFFFFFFF) & this.a) + X[15];
/* 244 */     this.b = (this.b << 19 | this.b >>> 13);
/*     */ 
/* 247 */     this.a += (this.b & (this.c | this.d) | this.c & this.d) + X[0] + 1518500249;
/* 248 */     this.a = (this.a << 3 | this.a >>> 29);
/* 249 */     this.d += (this.a & (this.b | this.c) | this.b & this.c) + X[4] + 1518500249;
/* 250 */     this.d = (this.d << 5 | this.d >>> 27);
/* 251 */     this.c += (this.d & (this.a | this.b) | this.a & this.b) + X[8] + 1518500249;
/* 252 */     this.c = (this.c << 9 | this.c >>> 23);
/* 253 */     this.b += (this.c & (this.d | this.a) | this.d & this.a) + X[12] + 1518500249;
/* 254 */     this.b = (this.b << 13 | this.b >>> 19);
/* 255 */     this.a += (this.b & (this.c | this.d) | this.c & this.d) + X[1] + 1518500249;
/* 256 */     this.a = (this.a << 3 | this.a >>> 29);
/* 257 */     this.d += (this.a & (this.b | this.c) | this.b & this.c) + X[5] + 1518500249;
/* 258 */     this.d = (this.d << 5 | this.d >>> 27);
/* 259 */     this.c += (this.d & (this.a | this.b) | this.a & this.b) + X[9] + 1518500249;
/* 260 */     this.c = (this.c << 9 | this.c >>> 23);
/* 261 */     this.b += (this.c & (this.d | this.a) | this.d & this.a) + X[13] + 1518500249;
/* 262 */     this.b = (this.b << 13 | this.b >>> 19);
/* 263 */     this.a += (this.b & (this.c | this.d) | this.c & this.d) + X[2] + 1518500249;
/* 264 */     this.a = (this.a << 3 | this.a >>> 29);
/* 265 */     this.d += (this.a & (this.b | this.c) | this.b & this.c) + X[6] + 1518500249;
/* 266 */     this.d = (this.d << 5 | this.d >>> 27);
/* 267 */     this.c += (this.d & (this.a | this.b) | this.a & this.b) + X[10] + 1518500249;
/* 268 */     this.c = (this.c << 9 | this.c >>> 23);
/* 269 */     this.b += (this.c & (this.d | this.a) | this.d & this.a) + X[14] + 1518500249;
/* 270 */     this.b = (this.b << 13 | this.b >>> 19);
/* 271 */     this.a += (this.b & (this.c | this.d) | this.c & this.d) + X[3] + 1518500249;
/* 272 */     this.a = (this.a << 3 | this.a >>> 29);
/* 273 */     this.d += (this.a & (this.b | this.c) | this.b & this.c) + X[7] + 1518500249;
/* 274 */     this.d = (this.d << 5 | this.d >>> 27);
/* 275 */     this.c += (this.d & (this.a | this.b) | this.a & this.b) + X[11] + 1518500249;
/* 276 */     this.c = (this.c << 9 | this.c >>> 23);
/* 277 */     this.b += (this.c & (this.d | this.a) | this.d & this.a) + X[15] + 1518500249;
/* 278 */     this.b = (this.b << 13 | this.b >>> 19);
/*     */ 
/* 281 */     this.a += (this.b ^ this.c ^ this.d) + X[0] + 1859775393;
/* 282 */     this.a = (this.a << 3 | this.a >>> 29);
/* 283 */     this.d += (this.a ^ this.b ^ this.c) + X[8] + 1859775393;
/* 284 */     this.d = (this.d << 9 | this.d >>> 23);
/* 285 */     this.c += (this.d ^ this.a ^ this.b) + X[4] + 1859775393;
/* 286 */     this.c = (this.c << 11 | this.c >>> 21);
/* 287 */     this.b += (this.c ^ this.d ^ this.a) + X[12] + 1859775393;
/* 288 */     this.b = (this.b << 15 | this.b >>> 17);
/* 289 */     this.a += (this.b ^ this.c ^ this.d) + X[2] + 1859775393;
/* 290 */     this.a = (this.a << 3 | this.a >>> 29);
/* 291 */     this.d += (this.a ^ this.b ^ this.c) + X[10] + 1859775393;
/* 292 */     this.d = (this.d << 9 | this.d >>> 23);
/* 293 */     this.c += (this.d ^ this.a ^ this.b) + X[6] + 1859775393;
/* 294 */     this.c = (this.c << 11 | this.c >>> 21);
/* 295 */     this.b += (this.c ^ this.d ^ this.a) + X[14] + 1859775393;
/* 296 */     this.b = (this.b << 15 | this.b >>> 17);
/* 297 */     this.a += (this.b ^ this.c ^ this.d) + X[1] + 1859775393;
/* 298 */     this.a = (this.a << 3 | this.a >>> 29);
/* 299 */     this.d += (this.a ^ this.b ^ this.c) + X[9] + 1859775393;
/* 300 */     this.d = (this.d << 9 | this.d >>> 23);
/* 301 */     this.c += (this.d ^ this.a ^ this.b) + X[5] + 1859775393;
/* 302 */     this.c = (this.c << 11 | this.c >>> 21);
/* 303 */     this.b += (this.c ^ this.d ^ this.a) + X[13] + 1859775393;
/* 304 */     this.b = (this.b << 15 | this.b >>> 17);
/* 305 */     this.a += (this.b ^ this.c ^ this.d) + X[3] + 1859775393;
/* 306 */     this.a = (this.a << 3 | this.a >>> 29);
/* 307 */     this.d += (this.a ^ this.b ^ this.c) + X[11] + 1859775393;
/* 308 */     this.d = (this.d << 9 | this.d >>> 23);
/* 309 */     this.c += (this.d ^ this.a ^ this.b) + X[7] + 1859775393;
/* 310 */     this.c = (this.c << 11 | this.c >>> 21);
/* 311 */     this.b += (this.c ^ this.d ^ this.a) + X[15] + 1859775393;
/* 312 */     this.b = (this.b << 15 | this.b >>> 17);
/*     */ 
/* 315 */     this.a += aa;
/* 316 */     this.b += bb;
/* 317 */     this.c += cc;
/* 318 */     this.d += dd;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.utils.MD4
 * JD-Core Version:    0.6.0
 */