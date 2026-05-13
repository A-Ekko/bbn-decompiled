/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class UUIDUtils
/*     */ {
/*  41 */   private static SecureRandom _rand = new SecureRandom();
/*     */ 
/*  43 */   private static Random _weakRand = new Random();
/*     */   private static final int MAX_IDS_PER_MILLI = 10000;
/*  59 */   private static long lastUsedTOD = 0L;
/*     */ 
/*  61 */   private static int numIdsThisMilli = 0;
/*     */   private static final String alphaNum = "0123456789ABCDEF";
/*     */   private static final int BITS_PER_DIGIT = 4;
/*     */   private static final int BITS_PER_INT = 32;
/*     */   private static final int BITS_PER_LONG = 64;
/*     */   private static final int DIGITS_PER_INT = 8;
/*     */   private static final int DIGITS_PER_LONG = 16;
/*  77 */   private static char[] UPPER_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */   public static String createUUID()
/*     */   {
/* 104 */     return createUUID(true);
/*     */   }
/*     */ 
/*     */   public static String createUUID(boolean secure)
/*     */   {
/* 113 */     Random rand = secure ? _rand : _weakRand;
/*     */ 
/* 115 */     StringBuffer s = new StringBuffer(36);
/*     */ 
/* 117 */     appendHexString(uniqueTOD(), false, 11, s);
/*     */ 
/* 121 */     s.append("0123456789ABCDEF".charAt(rand.nextInt(16) | 0x8));
/*     */ 
/* 124 */     appendRandomHexChars(32 - s.length(), rand, s);
/*     */ 
/* 127 */     s.insert(8, "-");
/* 128 */     s.insert(13, "-");
/* 129 */     s.insert(18, "-");
/* 130 */     s.insert(23, "-");
/*     */ 
/* 132 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public static String fromByteArray(byte[] ba)
/*     */   {
/* 147 */     if ((ba != null) && (ba.length == 16))
/*     */     {
/* 149 */       StringBuffer result = new StringBuffer(36);
/* 150 */       for (int i = 0; i < 16; i++)
/*     */       {
/* 152 */         if ((i == 4) || (i == 6) || (i == 8) || (i == 10)) {
/* 153 */           result.append("-");
/*     */         }
/* 155 */         result.append(UPPER_DIGITS[((ba[i] & 0xF0) >>> 4)]);
/* 156 */         result.append(UPPER_DIGITS[(ba[i] & 0xF)]);
/*     */       }
/* 158 */       return result.toString();
/*     */     }
/*     */ 
/* 161 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isUID(String uid)
/*     */   {
/* 177 */     if ((uid != null) && (uid.length() == 36))
/*     */     {
/* 179 */       char[] chars = uid.toCharArray();
/* 180 */       for (int i = 0; i < 36; i++)
/*     */       {
/* 182 */         char c = chars[i];
/*     */ 
/* 185 */         if ((i == 8) || (i == 13) || (i == 18) || (i == 23))
/*     */         {
/* 187 */           if (c != '-')
/*     */           {
/* 189 */             return false;
/*     */           }
/*     */ 
/*     */         }
/* 193 */         else if ((c < '0') || (c > 'F') || ((c > '9') && (c < 'A')))
/*     */         {
/* 195 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 199 */       return true;
/*     */     }
/*     */ 
/* 202 */     return false;
/*     */   }
/*     */ 
/*     */   public static byte[] toByteArray(String uid)
/*     */   {
/* 216 */     if (isUID(uid))
/*     */     {
/* 218 */       byte[] result = new byte[16];
/* 219 */       char[] chars = uid.toCharArray();
/* 220 */       int r = 0;
/*     */ 
/* 222 */       for (int i = 0; i < chars.length; i++)
/*     */       {
/* 224 */         if (chars[i] == '-')
/*     */           continue;
/* 226 */         int h1 = Character.digit(chars[i], 16);
/* 227 */         i++;
/* 228 */         int h2 = Character.digit(chars[i], 16);
/* 229 */         result[(r++)] = (byte)((h1 << 4 | h2) & 0xFF);
/*     */       }
/* 231 */       return result;
/*     */     }
/*     */ 
/* 234 */     return null;
/*     */   }
/*     */ 
/*     */   private static void appendRandomHexChars(int n, Random rand, StringBuffer result)
/*     */   {
/* 239 */     int digitsPerInt = 8;
/* 240 */     while (n > 0)
/*     */     {
/* 242 */       int digitsToUse = Math.min(n, digitsPerInt);
/* 243 */       n -= digitsToUse;
/* 244 */       appendHexString(rand.nextInt(), true, digitsToUse, result);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void appendHexString(long value, boolean prependZeroes, int nLeastSignificantDigits, StringBuffer result)
/*     */   {
/* 252 */     int bitsPerDigit = 4;
/*     */ 
/* 254 */     long mask = (1L << bitsPerDigit) - 1L;
/*     */ 
/* 256 */     if (nLeastSignificantDigits < 16)
/*     */     {
/* 259 */       value &= (1L << bitsPerDigit * nLeastSignificantDigits) - 1L;
/*     */     }
/*     */ 
/* 264 */     int i = 0;
/* 265 */     long reorderedValue = 0L;
/* 266 */     if (value == 0L)
/*     */     {
/* 269 */       i++;
/*     */     }
/*     */     else
/*     */     {
/*     */       do
/*     */       {
/* 275 */         reorderedValue = reorderedValue << bitsPerDigit | value & mask;
/* 276 */         value >>>= bitsPerDigit;
/* 277 */         i++;
/* 278 */       }while (value != 0L);
/*     */     }
/*     */ 
/* 281 */     if (prependZeroes)
/*     */     {
/* 283 */       for (int j = nLeastSignificantDigits - i; j > 0; j--)
/*     */       {
/* 285 */         result.append('0');
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 292 */     for (; i > 0; i--)
/*     */     {
/* 294 */       result.append("0123456789ABCDEF".charAt((int)(reorderedValue & mask)));
/* 295 */       reorderedValue >>>= bitsPerDigit;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized long uniqueTOD()
/*     */   {
/* 305 */     long currentTOD = System.currentTimeMillis();
/*     */ 
/* 309 */     if (currentTOD < lastUsedTOD) {
/* 310 */       lastUsedTOD = currentTOD;
/*     */     }
/* 312 */     if (currentTOD == lastUsedTOD)
/*     */     {
/* 314 */       numIdsThisMilli += 1;
/*     */ 
/* 319 */       if (numIdsThisMilli >= 10000)
/*     */       {
/* 321 */         while (currentTOD == lastUsedTOD) {
/*     */           try {
/* 323 */             Thread.sleep(1L); } catch (Exception interrupt) {
/* 324 */           }currentTOD = System.currentTimeMillis();
/*     */         }
/* 326 */         lastUsedTOD = currentTOD;
/* 327 */         numIdsThisMilli = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 333 */       lastUsedTOD = currentTOD;
/* 334 */       numIdsThisMilli = 0;
/*     */     }
/*     */ 
/* 337 */     return lastUsedTOD * 10000L + numIdsThisMilli;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.UUIDUtils
 * JD-Core Version:    0.6.0
 */