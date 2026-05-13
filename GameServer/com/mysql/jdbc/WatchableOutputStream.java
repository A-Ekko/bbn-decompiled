/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ class WatchableOutputStream extends ByteArrayOutputStream
/*    */ {
/*    */   private OutputStreamWatcher watcher;
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 48 */     super.close();
/*    */ 
/* 50 */     if (this.watcher != null)
/* 51 */       this.watcher.streamClosed(this);
/*    */   }
/*    */ 
/*    */   public void setWatcher(OutputStreamWatcher watcher)
/*    */   {
/* 62 */     this.watcher = watcher;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.WatchableOutputStream
 * JD-Core Version:    0.6.0
 */