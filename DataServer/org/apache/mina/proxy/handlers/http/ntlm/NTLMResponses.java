/*     */ package org.apache.mina.proxy.handlers.http.ntlm;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.Key;
/*     */ import java.security.MessageDigest;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ 
/*     */ public class NTLMResponses
/*     */ {
/*  47 */   public static byte[] LM_HASH_MAGIC_CONSTANT = null;
/*     */ 
/*     */   public static byte[] getLMResponse(String password, byte[] challenge)
/*     */     throws Exception
/*     */   {
/*  68 */     byte[] lmHash = lmHash(password);
/*  69 */     return lmResponse(lmHash, challenge);
/*     */   }
/*     */ 
/*     */   public static byte[] getNTLMResponse(String password, byte[] challenge)
/*     */     throws Exception
/*     */   {
/*  83 */     byte[] ntlmHash = ntlmHash(password);
/*  84 */     return lmResponse(ntlmHash, challenge);
/*     */   }
/*     */ 
/*     */   public static byte[] getNTLMv2Response(String target, String user, String password, byte[] targetInformation, byte[] challenge, byte[] clientNonce)
/*     */     throws Exception
/*     */   {
/* 106 */     return getNTLMv2Response(target, user, password, targetInformation, challenge, clientNonce, System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   public static byte[] getNTLMv2Response(String target, String user, String password, byte[] targetInformation, byte[] challenge, byte[] clientNonce, long time)
/*     */     throws Exception
/*     */   {
/* 129 */     byte[] ntlmv2Hash = ntlmv2Hash(target, user, password);
/* 130 */     byte[] blob = createBlob(targetInformation, clientNonce, time);
/* 131 */     return lmv2Response(ntlmv2Hash, blob, challenge);
/*     */   }
/*     */ 
/*     */   public static byte[] getLMv2Response(String target, String user, String password, byte[] challenge, byte[] clientNonce)
/*     */     throws Exception
/*     */   {
/* 150 */     byte[] ntlmv2Hash = ntlmv2Hash(target, user, password);
/* 151 */     return lmv2Response(ntlmv2Hash, clientNonce, challenge);
/*     */   }
/*     */ 
/*     */   public static byte[] getNTLM2SessionResponse(String password, byte[] challenge, byte[] clientNonce)
/*     */     throws Exception
/*     */   {
/* 168 */     byte[] ntlmHash = ntlmHash(password);
/* 169 */     MessageDigest md5 = MessageDigest.getInstance("MD5");
/* 170 */     md5.update(challenge);
/* 171 */     md5.update(clientNonce);
/* 172 */     byte[] sessionHash = new byte[8];
/* 173 */     System.arraycopy(md5.digest(), 0, sessionHash, 0, 8);
/* 174 */     return lmResponse(ntlmHash, sessionHash);
/*     */   }
/*     */ 
/*     */   private static byte[] lmHash(String password)
/*     */     throws Exception
/*     */   {
/* 186 */     byte[] oemPassword = password.toUpperCase().getBytes("US-ASCII");
/* 187 */     int length = Math.min(oemPassword.length, 14);
/* 188 */     byte[] keyBytes = new byte[14];
/* 189 */     System.arraycopy(oemPassword, 0, keyBytes, 0, length);
/* 190 */     Key lowKey = createDESKey(keyBytes, 0);
/* 191 */     Key highKey = createDESKey(keyBytes, 7);
/* 192 */     Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
/* 193 */     des.init(1, lowKey);
/* 194 */     byte[] lowHash = des.doFinal(LM_HASH_MAGIC_CONSTANT);
/* 195 */     des.init(1, highKey);
/* 196 */     byte[] highHash = des.doFinal(LM_HASH_MAGIC_CONSTANT);
/* 197 */     byte[] lmHash = new byte[16];
/* 198 */     System.arraycopy(lowHash, 0, lmHash, 0, 8);
/* 199 */     System.arraycopy(highHash, 0, lmHash, 8, 8);
/* 200 */     return lmHash;
/*     */   }
/*     */ 
/*     */   private static byte[] ntlmHash(String password)
/*     */     throws Exception
/*     */   {
/* 212 */     byte[] unicodePassword = password.getBytes("UnicodeLittleUnmarked");
/* 213 */     MessageDigest md4 = MessageDigest.getInstance("MD4");
/* 214 */     return md4.digest(unicodePassword);
/*     */   }
/*     */ 
/*     */   private static byte[] ntlmv2Hash(String target, String user, String password)
/*     */     throws Exception
/*     */   {
/* 229 */     byte[] ntlmHash = ntlmHash(password);
/* 230 */     String identity = user.toUpperCase() + target;
/* 231 */     return hmacMD5(identity.getBytes("UnicodeLittleUnmarked"), ntlmHash);
/*     */   }
/*     */ 
/*     */   private static byte[] lmResponse(byte[] hash, byte[] challenge)
/*     */     throws Exception
/*     */   {
/* 245 */     byte[] keyBytes = new byte[21];
/* 246 */     System.arraycopy(hash, 0, keyBytes, 0, 16);
/* 247 */     Key lowKey = createDESKey(keyBytes, 0);
/* 248 */     Key middleKey = createDESKey(keyBytes, 7);
/* 249 */     Key highKey = createDESKey(keyBytes, 14);
/* 250 */     Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
/* 251 */     des.init(1, lowKey);
/* 252 */     byte[] lowResponse = des.doFinal(challenge);
/* 253 */     des.init(1, middleKey);
/* 254 */     byte[] middleResponse = des.doFinal(challenge);
/* 255 */     des.init(1, highKey);
/* 256 */     byte[] highResponse = des.doFinal(challenge);
/* 257 */     byte[] lmResponse = new byte[24];
/* 258 */     System.arraycopy(lowResponse, 0, lmResponse, 0, 8);
/* 259 */     System.arraycopy(middleResponse, 0, lmResponse, 8, 8);
/* 260 */     System.arraycopy(highResponse, 0, lmResponse, 16, 8);
/* 261 */     return lmResponse;
/*     */   }
/*     */ 
/*     */   private static byte[] lmv2Response(byte[] hash, byte[] clientData, byte[] challenge)
/*     */     throws Exception
/*     */   {
/* 277 */     byte[] data = new byte[challenge.length + clientData.length];
/* 278 */     System.arraycopy(challenge, 0, data, 0, challenge.length);
/* 279 */     System.arraycopy(clientData, 0, data, challenge.length, clientData.length);
/*     */ 
/* 281 */     byte[] mac = hmacMD5(data, hash);
/* 282 */     byte[] lmv2Response = new byte[mac.length + clientData.length];
/* 283 */     System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
/* 284 */     System.arraycopy(clientData, 0, lmv2Response, mac.length, clientData.length);
/*     */ 
/* 286 */     return lmv2Response;
/*     */   }
/*     */ 
/*     */   private static byte[] createBlob(byte[] targetInformation, byte[] clientNonce, long time)
/*     */   {
/* 302 */     byte[] blobSignature = { 1, 1, 0, 0 };
/*     */ 
/* 304 */     byte[] reserved = { 0, 0, 0, 0 };
/*     */ 
/* 306 */     byte[] unknown1 = { 0, 0, 0, 0 };
/*     */ 
/* 308 */     byte[] unknown2 = { 0, 0, 0, 0 };
/*     */ 
/* 310 */     time += 11644473600000L;
/* 311 */     time *= 10000L;
/*     */ 
/* 313 */     byte[] timestamp = new byte[8];
/* 314 */     for (int i = 0; i < 8; i++) {
/* 315 */       timestamp[i] = (byte)(int)time;
/* 316 */       time >>>= 8;
/*     */     }
/* 318 */     byte[] blob = new byte[blobSignature.length + reserved.length + timestamp.length + clientNonce.length + unknown1.length + targetInformation.length + unknown2.length];
/*     */ 
/* 321 */     int offset = 0;
/* 322 */     System.arraycopy(blobSignature, 0, blob, offset, blobSignature.length);
/* 323 */     offset += blobSignature.length;
/* 324 */     System.arraycopy(reserved, 0, blob, offset, reserved.length);
/* 325 */     offset += reserved.length;
/* 326 */     System.arraycopy(timestamp, 0, blob, offset, timestamp.length);
/* 327 */     offset += timestamp.length;
/* 328 */     System.arraycopy(clientNonce, 0, blob, offset, clientNonce.length);
/* 329 */     offset += clientNonce.length;
/* 330 */     System.arraycopy(unknown1, 0, blob, offset, unknown1.length);
/* 331 */     offset += unknown1.length;
/* 332 */     System.arraycopy(targetInformation, 0, blob, offset, targetInformation.length);
/*     */ 
/* 334 */     offset += targetInformation.length;
/* 335 */     System.arraycopy(unknown2, 0, blob, offset, unknown2.length);
/* 336 */     return blob;
/*     */   }
/*     */ 
/*     */   public static byte[] hmacMD5(byte[] data, byte[] key)
/*     */     throws Exception
/*     */   {
/* 349 */     byte[] ipad = new byte[64];
/* 350 */     byte[] opad = new byte[64];
/*     */ 
/* 353 */     for (int i = 0; i < 64; i++) {
/* 354 */       if (i < key.length) {
/* 355 */         ipad[i] = (byte)(key[i] ^ 0x36);
/* 356 */         opad[i] = (byte)(key[i] ^ 0x5C);
/*     */       } else {
/* 358 */         ipad[i] = 54;
/* 359 */         opad[i] = 92;
/*     */       }
/*     */     }
/*     */ 
/* 363 */     byte[] content = new byte[data.length + 64];
/* 364 */     System.arraycopy(ipad, 0, content, 0, 64);
/* 365 */     System.arraycopy(data, 0, content, 64, data.length);
/* 366 */     MessageDigest md5 = MessageDigest.getInstance("MD5");
/* 367 */     data = md5.digest(content);
/* 368 */     content = new byte[data.length + 64];
/* 369 */     System.arraycopy(opad, 0, content, 0, 64);
/* 370 */     System.arraycopy(data, 0, content, 64, data.length);
/* 371 */     return md5.digest(content);
/*     */   }
/*     */ 
/*     */   private static Key createDESKey(byte[] bytes, int offset)
/*     */   {
/* 385 */     byte[] keyBytes = new byte[7];
/* 386 */     System.arraycopy(bytes, offset, keyBytes, 0, 7);
/* 387 */     byte[] material = new byte[8];
/* 388 */     material[0] = keyBytes[0];
/* 389 */     material[1] = (byte)(keyBytes[0] << 7 | (keyBytes[1] & 0xFF) >>> 1);
/* 390 */     material[2] = (byte)(keyBytes[1] << 6 | (keyBytes[2] & 0xFF) >>> 2);
/* 391 */     material[3] = (byte)(keyBytes[2] << 5 | (keyBytes[3] & 0xFF) >>> 3);
/* 392 */     material[4] = (byte)(keyBytes[3] << 4 | (keyBytes[4] & 0xFF) >>> 4);
/* 393 */     material[5] = (byte)(keyBytes[4] << 3 | (keyBytes[5] & 0xFF) >>> 5);
/* 394 */     material[6] = (byte)(keyBytes[5] << 2 | (keyBytes[6] & 0xFF) >>> 6);
/* 395 */     material[7] = (byte)(keyBytes[6] << 1);
/* 396 */     oddParity(material);
/* 397 */     return new SecretKeySpec(material, "DES");
/*     */   }
/*     */ 
/*     */   private static void oddParity(byte[] bytes)
/*     */   {
/* 407 */     for (int i = 0; i < bytes.length; i++) {
/* 408 */       byte b = bytes[i];
/* 409 */       boolean needsParity = ((b >>> 7 ^ b >>> 6 ^ b >>> 5 ^ b >>> 4 ^ b >>> 3 ^ b >>> 2 ^ b >>> 1) & 0x1) == 0;
/*     */ 
/* 411 */       if (needsParity)
/*     */       {
/*     */         int tmp58_57 = i; bytes[tmp58_57] = (byte)(bytes[tmp58_57] | 0x1);
/*     */       }
/*     */       else
/*     */       {
/*     */         int tmp69_68 = i; bytes[tmp69_68] = (byte)(bytes[tmp69_68] & 0xFFFFFFFE);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  51 */       LM_HASH_MAGIC_CONSTANT = "KGS!@#$%".getBytes("US-ASCII");
/*     */     } catch (UnsupportedEncodingException e) {
/*  53 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.ntlm.NTLMResponses
 * JD-Core Version:    0.6.0
 */