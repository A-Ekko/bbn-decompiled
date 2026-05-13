/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.CharArrayWriter;
/*    */ 
/*    */ class WatchableWriter extends CharArrayWriter
/*    */ {
/*    */   private WriterWatcher watcher;
/*    */ 
/*    */   public void close()
/*    */   {
/* 47 */     super.close();
/*    */ 
/* 50 */     if (this.watcher != null)
/* 51 */       this.watcher.writerClosed(this);
/*    */   }
/*    */ 
/*    */   public void setWatcher(WriterWatcher watcher)
/*    */   {
/* 62 */     this.watcher = watcher;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.WatchableWriter
 * JD-Core Version:    0.6.0
 */