/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class ProxoolException extends Exception
/*     */ {
/*  35 */   private Throwable cause = this;
/*     */ 
/*     */   public ProxoolException()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ProxoolException(String message)
/*     */   {
/*  55 */     super(message);
/*     */   }
/*     */ 
/*     */   public ProxoolException(String message, Throwable cause)
/*     */   {
/*  73 */     this(message);
/*  74 */     this.cause = cause;
/*     */   }
/*     */ 
/*     */   public ProxoolException(Throwable cause)
/*     */   {
/*  90 */     this(cause == null ? null : cause.toString());
/*  91 */     this.cause = cause;
/*     */   }
/*     */ 
/*     */   public Throwable getCause()
/*     */   {
/* 107 */     return this.cause == this ? null : this.cause;
/*     */   }
/*     */ 
/*     */   public synchronized Throwable initCause(Throwable cause)
/*     */   {
/* 128 */     if (this.cause != this) {
/* 129 */       throw new IllegalStateException("Can't overwrite cause");
/*     */     }
/* 131 */     if (cause == this) {
/* 132 */       throw new IllegalArgumentException("Self-causation not permitted");
/*     */     }
/* 134 */     this.cause = cause;
/* 135 */     return this;
/*     */   }
/*     */ 
/*     */   public void printStackTrace()
/*     */   {
/* 146 */     printStackTrace(System.err);
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintStream stream)
/*     */   {
/* 155 */     synchronized (stream) {
/* 156 */       super.printStackTrace(stream);
/* 157 */       Throwable ourCause = getCause();
/* 158 */       if (ourCause != null) {
/* 159 */         stream.println();
/* 160 */         stream.println("Caused by:");
/* 161 */         ourCause.printStackTrace(stream);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintWriter writer)
/*     */   {
/* 173 */     synchronized (writer) {
/* 174 */       super.printStackTrace(writer);
/* 175 */       Throwable ourCause = getCause();
/* 176 */       if (ourCause != null) {
/* 177 */         writer.println();
/* 178 */         writer.println("Caused by:");
/* 179 */         ourCause.printStackTrace(writer);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxoolException
 * JD-Core Version:    0.6.0
 */