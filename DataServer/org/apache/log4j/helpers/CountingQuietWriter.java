/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.io.FilterWriter;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import org.apache.log4j.spi.ErrorHandler;
/*    */ 
/*    */ public class CountingQuietWriter extends QuietWriter
/*    */ {
/*    */   protected long count;
/*    */ 
/*    */   public CountingQuietWriter(Writer writer, ErrorHandler eh)
/*    */   {
/* 30 */     super(writer, eh);
/*    */   }
/*    */ 
/*    */   public void write(String string)
/*    */   {
/*    */     try {
/* 36 */       this.out.write(string);
/* 37 */       this.count += string.length();
/*    */     }
/*    */     catch (IOException e) {
/* 40 */       this.errorHandler.error("Write failure.", e, 1);
/*    */     }
/*    */   }
/*    */ 
/*    */   public long getCount()
/*    */   {
/* 46 */     return this.count;
/*    */   }
/*    */ 
/*    */   public void setCount(long count)
/*    */   {
/* 51 */     this.count = count;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.CountingQuietWriter
 * JD-Core Version:    0.6.0
 */