/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.sql.ResultSet;
/*     */ 
/*     */ public class Util
/*     */ {
/*  53 */   private static Util enclosingInstance = new Util();
/*     */ 
/*     */   static String newCrypt(String password, String seed)
/*     */   {
/*  60 */     if ((password == null) || (password.length() == 0)) {
/*  61 */       return password;
/*     */     }
/*     */ 
/*  64 */     long[] pw = newHash(seed);
/*  65 */     long[] msg = newHash(password);
/*  66 */     long max = 1073741823L;
/*  67 */     long seed1 = (pw[0] ^ msg[0]) % max;
/*  68 */     long seed2 = (pw[1] ^ msg[1]) % max;
/*  69 */     char[] chars = new char[seed.length()];
/*     */ 
/*  71 */     for (int i = 0; i < seed.length(); i++) {
/*  72 */       seed1 = (seed1 * 3L + seed2) % max;
/*  73 */       seed2 = (seed1 + seed2 + 33L) % max;
/*  74 */       double d = seed1 / max;
/*  75 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/*  76 */       chars[i] = (char)b;
/*     */     }
/*     */ 
/*  79 */     seed1 = (seed1 * 3L + seed2) % max;
/*  80 */     seed2 = (seed1 + seed2 + 33L) % max;
/*  81 */     double d = seed1 / max;
/*  82 */     byte b = (byte)(int)Math.floor(d * 31.0D);
/*     */ 
/*  84 */     for (int i = 0; i < seed.length(); tmp205_203++)
/*     */     {
/*     */       int tmp205_203 = i;
/*     */       char[] tmp205_201 = chars; tmp205_201[tmp205_203] = (char)(tmp205_201[tmp205_203] ^ (char)b);
/*     */     }
/*     */ 
/*  88 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   static long[] newHash(String password) {
/*  92 */     long nr = 1345345333L;
/*  93 */     long add = 7L;
/*  94 */     long nr2 = 305419889L;
/*     */ 
/*  97 */     for (int i = 0; i < password.length(); i++) {
/*  98 */       if ((password.charAt(i) == ' ') || (password.charAt(i) == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 102 */       long tmp = 0xFF & password.charAt(i);
/* 103 */       nr ^= ((nr & 0x3F) + add) * tmp + (nr << 8);
/* 104 */       nr2 += (nr2 << 8 ^ nr);
/* 105 */       add += tmp;
/*     */     }
/*     */ 
/* 108 */     long[] result = new long[2];
/* 109 */     result[0] = (nr & 0x7FFFFFFF);
/* 110 */     result[1] = (nr2 & 0x7FFFFFFF);
/*     */ 
/* 112 */     return result;
/*     */   }
/*     */ 
/*     */   static String oldCrypt(String password, String seed)
/*     */   {
/* 120 */     long max = 33554431L;
/*     */ 
/* 124 */     if ((password == null) || (password.length() == 0)) {
/* 125 */       return password;
/*     */     }
/*     */ 
/* 128 */     long hp = oldHash(seed);
/* 129 */     long hm = oldHash(password);
/*     */ 
/* 131 */     long nr = hp ^ hm;
/* 132 */     nr %= max;
/* 133 */     long s1 = nr;
/* 134 */     long s2 = nr / 2L;
/*     */ 
/* 136 */     char[] chars = new char[seed.length()];
/*     */ 
/* 138 */     for (int i = 0; i < seed.length(); i++) {
/* 139 */       s1 = (s1 * 3L + s2) % max;
/* 140 */       s2 = (s1 + s2 + 33L) % max;
/* 141 */       double d = s1 / max;
/* 142 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 143 */       chars[i] = (char)b;
/*     */     }
/*     */ 
/* 146 */     return new String(chars);
/*     */   }
/*     */ 
/*     */   static long oldHash(String password) {
/* 150 */     long nr = 1345345333L;
/* 151 */     long nr2 = 7L;
/*     */ 
/* 154 */     for (int i = 0; i < password.length(); i++) {
/* 155 */       if ((password.charAt(i) == ' ') || (password.charAt(i) == '\t'))
/*     */       {
/*     */         continue;
/*     */       }
/* 159 */       long tmp = password.charAt(i);
/* 160 */       nr ^= ((nr & 0x3F) + nr2) * tmp + (nr << 8);
/* 161 */       nr2 += tmp;
/*     */     }
/*     */ 
/* 164 */     return nr & 0x7FFFFFFF;
/*     */   }
/*     */ 
/*     */   private static RandStructcture randomInit(long seed1, long seed2)
/*     */   {
/*     */     Util tmp7_4 = enclosingInstance; tmp7_4.getClass(); RandStructcture randStruct = new RandStructcture();
/*     */ 
/* 170 */     randStruct.maxValue = 1073741823L;
/* 171 */     randStruct.maxValueDbl = randStruct.maxValue;
/* 172 */     randStruct.seed1 = (seed1 % randStruct.maxValue);
/* 173 */     randStruct.seed2 = (seed2 % randStruct.maxValue);
/*     */ 
/* 175 */     return randStruct;
/*     */   }
/*     */ 
/*     */   public static Object readObject(ResultSet resultSet, int index)
/*     */     throws Exception
/*     */   {
/* 193 */     ObjectInputStream objIn = new ObjectInputStream(resultSet.getBinaryStream(index));
/*     */ 
/* 195 */     Object obj = objIn.readObject();
/* 196 */     objIn.close();
/*     */ 
/* 198 */     return obj;
/*     */   }
/*     */ 
/*     */   private static double rnd(RandStructcture randStruct) {
/* 202 */     randStruct.seed1 = ((randStruct.seed1 * 3L + randStruct.seed2) % randStruct.maxValue);
/*     */ 
/* 204 */     randStruct.seed2 = ((randStruct.seed1 + randStruct.seed2 + 33L) % randStruct.maxValue);
/*     */ 
/* 207 */     return randStruct.seed1 / randStruct.maxValueDbl;
/*     */   }
/*     */ 
/*     */   public static String scramble(String message, String password)
/*     */   {
/* 223 */     byte[] to = new byte[8];
/* 224 */     String val = "";
/*     */ 
/* 226 */     message = message.substring(0, 8);
/*     */ 
/* 228 */     if ((password != null) && (password.length() > 0)) {
/* 229 */       long[] hashPass = newHash(password);
/* 230 */       long[] hashMessage = newHash(message);
/*     */ 
/* 232 */       RandStructcture randStruct = randomInit(hashPass[0] ^ hashMessage[0], hashPass[1] ^ hashMessage[1]);
/*     */ 
/* 235 */       int msgPos = 0;
/* 236 */       int msgLength = message.length();
/* 237 */       int toPos = 0;
/*     */ 
/* 239 */       while (msgPos++ < msgLength) {
/* 240 */         to[(toPos++)] = (byte)(int)(Math.floor(rnd(randStruct) * 31.0D) + 64.0D);
/*     */       }
/*     */ 
/* 244 */       byte extra = (byte)(int)Math.floor(rnd(randStruct) * 31.0D);
/*     */ 
/* 246 */       for (int i = 0; i < to.length; i++)
/*     */       {
/*     */         int tmp140_138 = i;
/*     */         byte[] tmp140_136 = to; tmp140_136[tmp140_138] = (byte)(tmp140_136[tmp140_138] ^ extra);
/*     */       }
/*     */ 
/* 250 */       val = new String(to);
/*     */     }
/*     */ 
/* 253 */     return val;
/*     */   }
/*     */ 
/*     */   public static String stackTraceToString(Throwable ex)
/*     */   {
/* 269 */     StringBuffer traceBuf = new StringBuffer();
/* 270 */     traceBuf.append(Messages.getString("Util.1"));
/*     */ 
/* 272 */     if (ex != null) {
/* 273 */       traceBuf.append(ex.getClass().getName());
/*     */ 
/* 275 */       String message = ex.getMessage();
/*     */ 
/* 277 */       if (message != null) {
/* 278 */         traceBuf.append(Messages.getString("Util.2"));
/* 279 */         traceBuf.append(message);
/*     */       }
/*     */ 
/* 282 */       StringWriter out = new StringWriter();
/*     */ 
/* 284 */       PrintWriter printOut = new PrintWriter(out);
/*     */ 
/* 286 */       ex.printStackTrace(printOut);
/*     */ 
/* 288 */       traceBuf.append(Messages.getString("Util.3"));
/* 289 */       traceBuf.append(out.toString());
/*     */     }
/*     */ 
/* 292 */     traceBuf.append(Messages.getString("Util.4"));
/*     */ 
/* 294 */     return traceBuf.toString();
/*     */   }
/*     */ 
/*     */   class RandStructcture
/*     */   {
/*     */     long maxValue;
/*     */     double maxValueDbl;
/*     */     long seed1;
/*     */     long seed2;
/*     */ 
/*     */     RandStructcture()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.Util
 * JD-Core Version:    0.6.0
 */