/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.security.InvalidParameterException;
/*     */ 
/*     */ public class Base64
/*     */ {
/*     */   static final int CHUNK_SIZE = 76;
/*  55 */   static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();
/*     */   static final int BASELENGTH = 255;
/*     */   static final int LOOKUPLENGTH = 64;
/*     */   static final int EIGHTBIT = 8;
/*     */   static final int SIXTEENBIT = 16;
/*     */   static final int TWENTYFOURBITGROUP = 24;
/*     */   static final int FOURBYTE = 4;
/*     */   static final int SIGN = -128;
/*     */   static final byte PAD = 61;
/*  99 */   private static byte[] base64Alphabet = new byte['ÿ'];
/*     */ 
/* 101 */   private static byte[] lookUpBase64Alphabet = new byte[64];
/*     */ 
/*     */   private static boolean isBase64(byte octect)
/*     */   {
/* 138 */     if (octect == 61) {
/* 139 */       return true;
/*     */     }
/* 141 */     return base64Alphabet[octect] != -1;
/*     */   }
/*     */ 
/*     */   public static boolean isArrayByteBase64(byte[] arrayOctect)
/*     */   {
/* 157 */     arrayOctect = discardWhitespace(arrayOctect);
/*     */ 
/* 159 */     int length = arrayOctect.length;
/* 160 */     if (length == 0)
/*     */     {
/* 163 */       return true;
/*     */     }
/* 165 */     for (int i = 0; i < length; i++) {
/* 166 */       if (!isBase64(arrayOctect[i])) {
/* 167 */         return false;
/*     */       }
/*     */     }
/* 170 */     return true;
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64(byte[] binaryData)
/*     */   {
/* 181 */     return encodeBase64(binaryData, false);
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64Chunked(byte[] binaryData)
/*     */   {
/* 192 */     return encodeBase64(binaryData, true);
/*     */   }
/*     */ 
/*     */   public Object decode(Object pObject)
/*     */   {
/* 208 */     if (!(pObject instanceof byte[])) {
/* 209 */       throw new InvalidParameterException("Parameter supplied to Base64 decode is not a byte[]");
/*     */     }
/*     */ 
/* 212 */     return decode((byte[])(byte[])pObject);
/*     */   }
/*     */ 
/*     */   public byte[] decode(byte[] pArray)
/*     */   {
/* 223 */     return decodeBase64(pArray);
/*     */   }
/*     */ 
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked)
/*     */   {
/* 236 */     int lengthDataBits = binaryData.length * 8;
/* 237 */     int fewerThan24bits = lengthDataBits % 24;
/* 238 */     int numberTriplets = lengthDataBits / 24;
/* 239 */     byte[] encodedData = null;
/* 240 */     int encodedDataLength = 0;
/* 241 */     int nbrChunks = 0;
/*     */ 
/* 243 */     if (fewerThan24bits != 0)
/*     */     {
/* 245 */       encodedDataLength = (numberTriplets + 1) * 4;
/*     */     }
/*     */     else {
/* 248 */       encodedDataLength = numberTriplets * 4;
/*     */     }
/*     */ 
/* 254 */     if (isChunked)
/*     */     {
/* 256 */       nbrChunks = CHUNK_SEPARATOR.length == 0 ? 0 : (int)Math.ceil(encodedDataLength / 76.0F);
/*     */ 
/* 258 */       encodedDataLength += nbrChunks * CHUNK_SEPARATOR.length;
/*     */     }
/*     */ 
/* 261 */     encodedData = new byte[encodedDataLength];
/*     */ 
/* 263 */     byte k = 0; byte l = 0; byte b1 = 0; byte b2 = 0; byte b3 = 0;
/*     */ 
/* 265 */     int encodedIndex = 0;
/* 266 */     int dataIndex = 0;
/* 267 */     int i = 0;
/* 268 */     int nextSeparatorIndex = 76;
/* 269 */     int chunksSoFar = 0;
/*     */ 
/* 272 */     for (i = 0; i < numberTriplets; i++) {
/* 273 */       dataIndex = i * 3;
/* 274 */       b1 = binaryData[dataIndex];
/* 275 */       b2 = binaryData[(dataIndex + 1)];
/* 276 */       b3 = binaryData[(dataIndex + 2)];
/*     */ 
/* 280 */       l = (byte)(b2 & 0xF);
/* 281 */       k = (byte)(b1 & 0x3);
/*     */ 
/* 283 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/*     */ 
/* 285 */       byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/*     */ 
/* 287 */       byte val3 = (b3 & 0xFFFFFF80) == 0 ? (byte)(b3 >> 6) : (byte)(b3 >> 6 ^ 0xFC);
/*     */ 
/* 290 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/*     */ 
/* 294 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(val2 | k << 4)];
/*     */ 
/* 296 */       encodedData[(encodedIndex + 2)] = lookUpBase64Alphabet[(l << 2 | val3)];
/*     */ 
/* 298 */       encodedData[(encodedIndex + 3)] = lookUpBase64Alphabet[(b3 & 0x3F)];
/*     */ 
/* 300 */       encodedIndex += 4;
/*     */ 
/* 303 */       if (!isChunked)
/*     */         continue;
/* 305 */       if (encodedIndex == nextSeparatorIndex) {
/* 306 */         System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, encodedIndex, CHUNK_SEPARATOR.length);
/*     */ 
/* 308 */         chunksSoFar++;
/* 309 */         nextSeparatorIndex = 76 * (chunksSoFar + 1) + chunksSoFar * CHUNK_SEPARATOR.length;
/*     */ 
/* 311 */         encodedIndex += CHUNK_SEPARATOR.length;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 317 */     dataIndex = i * 3;
/*     */ 
/* 319 */     if (fewerThan24bits == 8) {
/* 320 */       b1 = binaryData[dataIndex];
/* 321 */       k = (byte)(b1 & 0x3);
/*     */ 
/* 324 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/*     */ 
/* 326 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 327 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(k << 4)];
/* 328 */       encodedData[(encodedIndex + 2)] = 61;
/* 329 */       encodedData[(encodedIndex + 3)] = 61;
/* 330 */     } else if (fewerThan24bits == 16)
/*     */     {
/* 332 */       b1 = binaryData[dataIndex];
/* 333 */       b2 = binaryData[(dataIndex + 1)];
/* 334 */       l = (byte)(b2 & 0xF);
/* 335 */       k = (byte)(b1 & 0x3);
/*     */ 
/* 337 */       byte val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);
/*     */ 
/* 339 */       byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);
/*     */ 
/* 342 */       encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
/* 343 */       encodedData[(encodedIndex + 1)] = lookUpBase64Alphabet[(val2 | k << 4)];
/*     */ 
/* 345 */       encodedData[(encodedIndex + 2)] = lookUpBase64Alphabet[(l << 2)];
/* 346 */       encodedData[(encodedIndex + 3)] = 61;
/*     */     }
/*     */ 
/* 349 */     if (isChunked)
/*     */     {
/* 351 */       if (chunksSoFar < nbrChunks) {
/* 352 */         System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, encodedDataLength - CHUNK_SEPARATOR.length, CHUNK_SEPARATOR.length);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 358 */     return encodedData;
/*     */   }
/*     */ 
/*     */   public static byte[] decodeBase64(byte[] base64Data)
/*     */   {
/* 369 */     base64Data = discardNonBase64(base64Data);
/*     */ 
/* 372 */     if (base64Data.length == 0) {
/* 373 */       return new byte[0];
/*     */     }
/*     */ 
/* 376 */     int numberQuadruple = base64Data.length / 4;
/* 377 */     byte[] decodedData = null;
/* 378 */     byte b1 = 0; byte b2 = 0; byte b3 = 0; byte b4 = 0; byte marker0 = 0; byte marker1 = 0;
/*     */ 
/* 382 */     int encodedIndex = 0;
/* 383 */     int dataIndex = 0;
/*     */ 
/* 386 */     int lastData = base64Data.length;
/*     */ 
/* 388 */     while (base64Data[(lastData - 1)] == 61) {
/* 389 */       lastData--; if (lastData == 0) {
/* 390 */         return new byte[0];
/*     */       }
/*     */     }
/* 393 */     decodedData = new byte[lastData - numberQuadruple];
/*     */ 
/* 396 */     for (int i = 0; i < numberQuadruple; i++) {
/* 397 */       dataIndex = i * 4;
/* 398 */       marker0 = base64Data[(dataIndex + 2)];
/* 399 */       marker1 = base64Data[(dataIndex + 3)];
/*     */ 
/* 401 */       b1 = base64Alphabet[base64Data[dataIndex]];
/* 402 */       b2 = base64Alphabet[base64Data[(dataIndex + 1)]];
/*     */ 
/* 404 */       if ((marker0 != 61) && (marker1 != 61))
/*     */       {
/* 406 */         b3 = base64Alphabet[marker0];
/* 407 */         b4 = base64Alphabet[marker1];
/*     */ 
/* 409 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 410 */         decodedData[(encodedIndex + 1)] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
/* 411 */         decodedData[(encodedIndex + 2)] = (byte)(b3 << 6 | b4);
/* 412 */       } else if (marker0 == 61)
/*     */       {
/* 414 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 415 */       } else if (marker1 == 61)
/*     */       {
/* 417 */         b3 = base64Alphabet[marker0];
/*     */ 
/* 419 */         decodedData[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
/* 420 */         decodedData[(encodedIndex + 1)] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
/*     */       }
/* 422 */       encodedIndex += 3;
/*     */     }
/* 424 */     return decodedData;
/*     */   }
/*     */ 
/*     */   static byte[] discardWhitespace(byte[] data)
/*     */   {
/* 435 */     byte[] groomedData = new byte[data.length];
/* 436 */     int bytesCopied = 0;
/*     */ 
/* 438 */     for (int i = 0; i < data.length; i++) {
/* 439 */       switch (data[i]) {
/*     */       case 9:
/*     */       case 10:
/*     */       case 13:
/*     */       case 32:
/* 444 */         break;
/*     */       default:
/* 446 */         groomedData[(bytesCopied++)] = data[i];
/*     */       }
/*     */     }
/*     */ 
/* 450 */     byte[] packedData = new byte[bytesCopied];
/*     */ 
/* 452 */     System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/*     */ 
/* 454 */     return packedData;
/*     */   }
/*     */ 
/*     */   static byte[] discardNonBase64(byte[] data)
/*     */   {
/* 467 */     byte[] groomedData = new byte[data.length];
/* 468 */     int bytesCopied = 0;
/*     */ 
/* 470 */     for (int i = 0; i < data.length; i++) {
/* 471 */       if (isBase64(data[i])) {
/* 472 */         groomedData[(bytesCopied++)] = data[i];
/*     */       }
/*     */     }
/*     */ 
/* 476 */     byte[] packedData = new byte[bytesCopied];
/*     */ 
/* 478 */     System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
/*     */ 
/* 480 */     return packedData;
/*     */   }
/*     */ 
/*     */   public Object encode(Object pObject)
/*     */   {
/* 498 */     if (!(pObject instanceof byte[])) {
/* 499 */       throw new InvalidParameterException("Parameter supplied to Base64 encode is not a byte[]");
/*     */     }
/*     */ 
/* 502 */     return encode((byte[])(byte[])pObject);
/*     */   }
/*     */ 
/*     */   public byte[] encode(byte[] pArray)
/*     */   {
/* 513 */     return encodeBase64(pArray, false);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 105 */     for (int i = 0; i < 255; i++) {
/* 106 */       base64Alphabet[i] = -1;
/*     */     }
/* 108 */     for (int i = 90; i >= 65; i--) {
/* 109 */       base64Alphabet[i] = (byte)(i - 65);
/*     */     }
/* 111 */     for (int i = 122; i >= 97; i--) {
/* 112 */       base64Alphabet[i] = (byte)(i - 97 + 26);
/*     */     }
/* 114 */     for (int i = 57; i >= 48; i--) {
/* 115 */       base64Alphabet[i] = (byte)(i - 48 + 52);
/*     */     }
/*     */ 
/* 118 */     base64Alphabet[43] = 62;
/* 119 */     base64Alphabet[47] = 63;
/*     */ 
/* 121 */     for (int i = 0; i <= 25; i++) {
/* 122 */       lookUpBase64Alphabet[i] = (byte)(65 + i);
/*     */     }
/*     */ 
/* 125 */     int i = 26; for (int j = 0; i <= 51; j++) {
/* 126 */       lookUpBase64Alphabet[i] = (byte)(97 + j);
/*     */ 
/* 125 */       i++;
/*     */     }
/*     */ 
/* 129 */     int i = 52; for (int j = 0; i <= 61; j++) {
/* 130 */       lookUpBase64Alphabet[i] = (byte)(48 + j);
/*     */ 
/* 129 */       i++;
/*     */     }
/*     */ 
/* 133 */     lookUpBase64Alphabet[62] = 43;
/* 134 */     lookUpBase64Alphabet[63] = 47;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.Base64
 * JD-Core Version:    0.6.0
 */