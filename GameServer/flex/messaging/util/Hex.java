/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class Hex
/*     */ {
/*  30 */   private static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 142 */     boolean printData = false;
/* 143 */     int randomLimit = 500;
/*     */ 
/* 145 */     for (int myCount = 0; myCount < 10000; myCount++)
/*     */     {
/* 147 */       byte[] raw = new byte[(int)(Math.random() * randomLimit)];
/*     */ 
/* 149 */       for (int i = 0; i < raw.length; i++)
/*     */       {
/* 151 */         if (i % 1024 < 256)
/* 152 */           raw[i] = (byte)(i % 1024);
/*     */         else
/* 154 */           raw[i] = (byte)((int)(Math.random() * 255.0D) - 128);
/*     */       }
/* 156 */       Encoder encoder = new Encoder(100);
/* 157 */       encoder.encode(raw);
/*     */ 
/* 159 */       String encoded = encoder.drain();
/*     */ 
/* 161 */       Decoder decoder = new Decoder();
/* 162 */       decoder.decode(encoded);
/* 163 */       byte[] check = decoder.flush();
/*     */ 
/* 165 */       String mesg = "Success!";
/* 166 */       if (check.length != raw.length)
/*     */       {
/* 168 */         mesg = "***** length mismatch!";
/*     */       }
/*     */       else
/*     */       {
/* 172 */         for (int i = 0; i < check.length; i++)
/*     */         {
/* 174 */           if (check[i] == raw[i])
/*     */             continue;
/* 176 */           mesg = "***** data mismatch!";
/* 177 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 181 */       if (mesg.indexOf("Success") == -1)
/*     */       {
/* 183 */         System.out.println(mesg + myCount);
/* 184 */         break;
/*     */       }
/*     */ 
/* 187 */       if (!printData)
/*     */         continue;
/* 189 */       System.out.println("Decoded: " + new String(raw));
/* 190 */       System.out.println("Encoded: " + encoded);
/* 191 */       System.out.println("Decoded: " + new String(check));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Encoder
/*     */   {
/*     */     private StringBuffer output;
/*     */ 
/*     */     public Encoder(int size)
/*     */     {
/* 102 */       this.output = new StringBuffer(size * 2);
/*     */     }
/*     */ 
/*     */     private void encodeBlock(byte work)
/*     */     {
/* 107 */       this.output.append(Hex.digits[((work & 0xF0) >>> 4)]);
/* 108 */       this.output.append(Hex.digits[(work & 0xF)]);
/*     */     }
/*     */ 
/*     */     public void encode(byte[] data)
/*     */     {
/* 113 */       encode(data, 0, data.length);
/*     */     }
/*     */ 
/*     */     public void encode(byte[] data, int offset, int length)
/*     */     {
/* 118 */       int plainIndex = offset;
/*     */ 
/* 120 */       while (plainIndex < offset + length)
/*     */       {
/* 122 */         encodeBlock(data[plainIndex]);
/* 123 */         plainIndex++;
/*     */       }
/*     */     }
/*     */ 
/*     */     public String drain()
/*     */     {
/* 129 */       String r = this.output.toString();
/* 130 */       this.output.setLength(0);
/* 131 */       return r;
/*     */     }
/*     */ 
/*     */     public String flush()
/*     */     {
/* 136 */       return drain();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Decoder
/*     */   {
/*  38 */     private int filled = 0;
/*     */     private byte[] data;
/*  40 */     private int[] work = { 0, 0 };
/*     */ 
/*     */     public Decoder()
/*     */     {
/*  45 */       this.data = new byte[256];
/*     */     }
/*     */ 
/*     */     public void decode(String encoded)
/*     */     {
/*  51 */       int estimate = 1 + encoded.length() / 2;
/*     */ 
/*  53 */       if (this.filled + estimate > this.data.length)
/*     */       {
/*  55 */         int length = this.data.length * 2;
/*  56 */         while (length < this.filled + estimate)
/*     */         {
/*  58 */           length *= 2;
/*     */         }
/*  60 */         byte[] newdata = new byte[length];
/*     */ 
/*  62 */         System.arraycopy(this.data, 0, newdata, 0, this.filled);
/*  63 */         this.data = newdata;
/*     */       }
/*     */ 
/*  66 */       for (int i = 0; i < encoded.length(); i++)
/*     */       {
/*  68 */         this.work[0] = Character.digit(encoded.charAt(i), 16);
/*  69 */         i++;
/*  70 */         this.work[1] = Character.digit(encoded.charAt(i), 16);
/*  71 */         this.data[(this.filled++)] = (byte)((this.work[0] << 4 | this.work[1]) & 0xFF);
/*     */       }
/*     */     }
/*     */ 
/*     */     public byte[] drain()
/*     */     {
/*  77 */       byte[] r = new byte[this.filled];
/*  78 */       System.arraycopy(this.data, 0, r, 0, this.filled);
/*  79 */       this.filled = 0;
/*  80 */       return r;
/*     */     }
/*     */ 
/*     */     public byte[] flush() throws IllegalStateException
/*     */     {
/*  85 */       return drain();
/*     */     }
/*     */ 
/*     */     public void reset()
/*     */     {
/*  90 */       this.filled = 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.Hex
 * JD-Core Version:    0.6.0
 */