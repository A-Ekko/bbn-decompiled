/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Vector;
/*     */ 
/*     */ class VectorWriter extends PrintWriter
/*     */ {
/*     */   private Vector v;
/*     */ 
/*     */   VectorWriter()
/*     */   {
/*  71 */     super(new NullWriter());
/*  72 */     this.v = new Vector();
/*     */   }
/*     */ 
/*     */   public void print(Object o) {
/*  76 */     this.v.addElement(o.toString());
/*     */   }
/*     */ 
/*     */   public void print(char[] chars) {
/*  80 */     this.v.addElement(new String(chars));
/*     */   }
/*     */ 
/*     */   public void print(String s) {
/*  84 */     this.v.addElement(s);
/*     */   }
/*     */ 
/*     */   public void println(Object o) {
/*  88 */     this.v.addElement(o.toString());
/*     */   }
/*     */ 
/*     */   public void println(char[] chars)
/*     */   {
/*  95 */     this.v.addElement(new String(chars));
/*     */   }
/*     */ 
/*     */   public void println(String s)
/*     */   {
/* 100 */     this.v.addElement(s);
/*     */   }
/*     */ 
/*     */   public void write(char[] chars) {
/* 104 */     this.v.addElement(new String(chars));
/*     */   }
/*     */ 
/*     */   public void write(char[] chars, int off, int len) {
/* 108 */     this.v.addElement(new String(chars, off, len));
/*     */   }
/*     */ 
/*     */   public void write(String s, int off, int len) {
/* 112 */     this.v.addElement(s.substring(off, off + len));
/*     */   }
/*     */ 
/*     */   public void write(String s) {
/* 116 */     this.v.addElement(s);
/*     */   }
/*     */ 
/*     */   public String[] toStringArray() {
/* 120 */     int len = this.v.size();
/* 121 */     String[] sa = new String[len];
/* 122 */     for (int i = 0; i < len; i++) {
/* 123 */       sa[i] = ((String)this.v.elementAt(i));
/*     */     }
/* 125 */     return sa;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.spi.VectorWriter
 * JD-Core Version:    0.6.0
 */