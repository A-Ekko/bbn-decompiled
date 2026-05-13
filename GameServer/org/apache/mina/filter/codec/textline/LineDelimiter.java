/*     */ package org.apache.mina.filter.codec.textline;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class LineDelimiter
/*     */ {
/*     */   public static final LineDelimiter DEFAULT;
/*     */   public static final LineDelimiter AUTO;
/*     */   public static final LineDelimiter CRLF;
/*     */   public static final LineDelimiter UNIX;
/*     */   public static final LineDelimiter WINDOWS;
/*     */   public static final LineDelimiter MAC;
/*     */   public static final LineDelimiter NUL;
/*     */   private final String value;
/*     */ 
/*     */   public LineDelimiter(String value)
/*     */   {
/*  85 */     if (value == null) {
/*  86 */       throw new NullPointerException("delimiter");
/*     */     }
/*  88 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  95 */     return this.value;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 100 */     return this.value.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 105 */     if (!(o instanceof LineDelimiter)) {
/* 106 */       return false;
/*     */     }
/*     */ 
/* 109 */     LineDelimiter that = (LineDelimiter)o;
/* 110 */     return this.value.equals(that.value);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 115 */     StringBuffer buf = new StringBuffer();
/* 116 */     buf.append("delimiter:");
/* 117 */     if (this.value.length() == 0)
/* 118 */       buf.append(" auto");
/*     */     else {
/* 120 */       for (int i = 0; i < this.value.length(); i++) {
/* 121 */         buf.append(" 0x");
/* 122 */         buf.append(Integer.toHexString(this.value.charAt(i)));
/*     */       }
/*     */     }
/* 125 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  39 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*  40 */     PrintWriter out = new PrintWriter(bout);
/*  41 */     out.println();
/*  42 */     DEFAULT = new LineDelimiter(new String(bout.toByteArray()));
/*     */ 
/*  51 */     AUTO = new LineDelimiter("");
/*     */ 
/*  56 */     CRLF = new LineDelimiter("\r\n");
/*     */ 
/*  61 */     UNIX = new LineDelimiter("\n");
/*     */ 
/*  66 */     WINDOWS = CRLF;
/*     */ 
/*  71 */     MAC = new LineDelimiter("\r");
/*     */ 
/*  77 */     NUL = new LineDelimiter("");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.textline.LineDelimiter
 * JD-Core Version:    0.6.0
 */