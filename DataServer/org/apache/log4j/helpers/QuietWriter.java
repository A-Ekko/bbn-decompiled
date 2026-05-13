/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.io.FilterWriter;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import org.apache.log4j.spi.ErrorHandler;
/*    */ 
/*    */ public class QuietWriter extends FilterWriter
/*    */ {
/*    */   protected ErrorHandler errorHandler;
/*    */ 
/*    */   public QuietWriter(Writer writer, ErrorHandler errorHandler)
/*    */   {
/* 32 */     super(writer);
/* 33 */     setErrorHandler(errorHandler);
/*    */   }
/*    */ 
/*    */   public void write(String string)
/*    */   {
/*    */     try {
/* 39 */       this.out.write(string);
/*    */     } catch (IOException e) {
/* 41 */       this.errorHandler.error("Failed to write [" + string + "].", e, 1);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */   {
/*    */     try
/*    */     {
/* 49 */       this.out.flush();
/*    */     } catch (IOException e) {
/* 51 */       this.errorHandler.error("Failed to flush writer,", e, 2);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setErrorHandler(ErrorHandler eh)
/*    */   {
/* 59 */     if (eh == null)
/*    */     {
/* 61 */       throw new IllegalArgumentException("Attempted to set null ErrorHandler.");
/*    */     }
/* 63 */     this.errorHandler = eh;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.QuietWriter
 * JD-Core Version:    0.6.0
 */