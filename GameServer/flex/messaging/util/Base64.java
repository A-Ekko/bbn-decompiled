/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class Base64
/*     */ {
/*  30 */   private static final char[] alphabet = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
/*     */ 
/*  42 */   private static final int[] inverse = { 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64, 64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64 };
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 231 */     boolean printData = false;
/* 232 */     int randomLimit = 500;
/*     */ 
/* 234 */     byte[] raw = new byte[(int)(Math.random() * randomLimit)];
/*     */ 
/* 236 */     for (int i = 0; i < raw.length; i++)
/*     */     {
/* 238 */       if (i % 1024 < 256)
/* 239 */         raw[i] = (byte)(i % 1024);
/*     */       else
/* 241 */         raw[i] = (byte)((int)(Math.random() * 255.0D) - 128);
/*     */     }
/* 243 */     Encoder encoder = new Encoder(100);
/* 244 */     encoder.encode(raw);
/*     */ 
/* 246 */     String encoded = encoder.drain();
/*     */ 
/* 248 */     Decoder decoder = new Decoder();
/* 249 */     decoder.decode(encoded);
/* 250 */     byte[] check = decoder.flush();
/*     */ 
/* 252 */     String mesg = "Success!";
/* 253 */     if (check.length != raw.length)
/*     */     {
/* 255 */       mesg = "***** length mismatch!";
/*     */     }
/*     */     else
/*     */     {
/* 259 */       for (int i = 0; i < check.length; i++)
/*     */       {
/* 261 */         if (check[i] == raw[i])
/*     */           continue;
/* 263 */         mesg = "***** data mismatch!";
/* 264 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 268 */     System.out.println(mesg);
/*     */ 
/* 270 */     if (printData)
/*     */     {
/* 272 */       System.out.println("Decoded: " + new String(raw));
/* 273 */       System.out.println("Encoded: " + encoded);
/* 274 */       System.out.println("Decoded: " + new String(check));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Encoder
/*     */   {
/* 148 */     private int[] work = { 0, 0, 0 };
/* 149 */     private int count = 0;
/* 150 */     private int line = 0;
/*     */     private StringBuffer output;
/*     */ 
/*     */     public Encoder(int size)
/*     */     {
/* 155 */       this.output = new StringBuffer(size);
/*     */     }
/*     */ 
/*     */     private void encodeBlock()
/*     */     {
/* 160 */       this.output.append(Base64.alphabet[((this.work[0] & 0xFF) >> 2)]);
/* 161 */       this.output.append(Base64.alphabet[((this.work[0] & 0x3) << 4 | (this.work[1] & 0xF0) >> 4)]);
/* 162 */       if (this.count > 1)
/* 163 */         this.output.append(Base64.alphabet[((this.work[1] & 0xF) << 2 | (this.work[2] & 0xC0) >> 6)]);
/*     */       else {
/* 165 */         this.output.append('=');
/*     */       }
/* 167 */       if (this.count > 2)
/* 168 */         this.output.append(Base64.alphabet[(this.work[2] & 0x3F)]);
/*     */       else {
/* 170 */         this.output.append('=');
/*     */       }
/* 172 */       if (this.line += 4 == 76)
/*     */       {
/* 174 */         this.output.append('\n');
/* 175 */         this.line = 0;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void encode(byte[] data)
/*     */     {
/* 181 */       encode(data, 0, data.length);
/*     */     }
/*     */ 
/*     */     public void encode(byte[] data, int offset, int length)
/*     */     {
/* 186 */       int plainIndex = offset;
/*     */ 
/* 188 */       while (plainIndex < offset + length)
/*     */       {
/* 190 */         this.work[this.count] = data[plainIndex];
/* 191 */         this.count += 1;
/*     */ 
/* 193 */         if ((this.count == this.work.length) || (offset + length - plainIndex == 1))
/*     */         {
/* 195 */           encodeBlock();
/* 196 */           this.count = 0;
/* 197 */           this.work[0] = 0;
/* 198 */           this.work[1] = 0;
/* 199 */           this.work[2] = 0;
/*     */         }
/* 201 */         plainIndex++;
/*     */       }
/*     */     }
/*     */ 
/*     */     public String drain()
/*     */     {
/* 207 */       String r = this.output.toString();
/* 208 */       this.output.setLength(0);
/* 209 */       return r;
/*     */     }
/*     */ 
/*     */     public String flush()
/*     */     {
/* 214 */       if (this.count > 0) {
/* 215 */         encodeBlock();
/*     */       }
/* 217 */       String r = drain();
/* 218 */       this.count = 0;
/* 219 */       this.line = 0;
/* 220 */       this.work[0] = 0;
/* 221 */       this.work[1] = 0;
/* 222 */       this.work[2] = 0;
/* 223 */       return r;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Decoder
/*     */   {
/*  64 */     private int filled = 0;
/*     */     private byte[] data;
/*  66 */     private int count = 0;
/*  67 */     private int[] work = { 0, 0, 0, 0 };
/*     */ 
/*     */     public Decoder()
/*     */     {
/*  72 */       this.data = new byte[256];
/*     */     }
/*     */ 
/*     */     public void decode(String encoded)
/*     */     {
/*  77 */       int estimate = 1 + encoded.length() * 3 / 4;
/*     */ 
/*  79 */       if (this.filled + estimate > this.data.length)
/*     */       {
/*  81 */         int length = this.data.length * 2;
/*  82 */         while (length < this.filled + estimate)
/*     */         {
/*  84 */           length *= 2;
/*     */         }
/*  86 */         byte[] newdata = new byte[length];
/*     */ 
/*  88 */         System.arraycopy(this.data, 0, newdata, 0, this.filled);
/*  89 */         this.data = newdata;
/*     */       }
/*     */ 
/*  92 */       for (int i = 0; i < encoded.length(); i++)
/*     */       {
/*  94 */         char c = encoded.charAt(i);
/*     */ 
/*  96 */         if (c == '=') {
/*  97 */           this.work[(this.count++)] = -1; } else {
/*  98 */           if (Base64.inverse[c] == 64) continue;
/*  99 */           this.work[(this.count++)] = Base64.access$000()[c];
/*     */         }
/*     */ 
/* 103 */         if (this.count != 4)
/*     */           continue;
/* 105 */         this.count = 0;
/* 106 */         this.data[(this.filled++)] = (byte)(this.work[0] << 2 | (this.work[1] & 0xFF) >> 4);
/*     */ 
/* 108 */         if (this.work[2] == -1) {
/*     */           break;
/*     */         }
/* 111 */         this.data[(this.filled++)] = (byte)(this.work[1] << 4 | (this.work[2] & 0xFF) >> 2);
/*     */ 
/* 113 */         if (this.work[3] == -1) {
/*     */           break;
/*     */         }
/* 116 */         this.data[(this.filled++)] = (byte)(this.work[2] << 6 | this.work[3]);
/*     */       }
/*     */     }
/*     */ 
/*     */     public byte[] drain()
/*     */     {
/* 124 */       byte[] r = new byte[this.filled];
/* 125 */       System.arraycopy(this.data, 0, r, 0, this.filled);
/* 126 */       this.filled = 0;
/* 127 */       return r;
/*     */     }
/*     */ 
/*     */     public byte[] flush() throws IllegalStateException
/*     */     {
/* 132 */       if (this.count > 0)
/* 133 */         throw new IllegalStateException("a partial block (" + this.count + " of 4 bytes) was dropped, decoded data is probably truncated!");
/* 134 */       return drain();
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/* 139 */       this.count = 0;
/* 140 */       this.filled = 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.Base64
 * JD-Core Version:    0.6.0
 */