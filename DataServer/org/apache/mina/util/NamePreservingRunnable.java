/*    */ package org.apache.mina.util;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class NamePreservingRunnable
/*    */   implements Runnable
/*    */ {
/* 32 */   private final Logger logger = LoggerFactory.getLogger(NamePreservingRunnable.class);
/*    */   private final String newName;
/*    */   private final Runnable runnable;
/*    */ 
/*    */   public NamePreservingRunnable(Runnable runnable, String newName)
/*    */   {
/* 47 */     this.runnable = runnable;
/* 48 */     this.newName = newName;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 57 */     Thread currentThread = Thread.currentThread();
/* 58 */     String oldName = currentThread.getName();
/*    */ 
/* 60 */     if (this.newName != null) {
/* 61 */       setName(currentThread, this.newName);
/*    */     }
/*    */     try
/*    */     {
/* 65 */       this.runnable.run();
/*    */     } finally {
/* 67 */       setName(currentThread, oldName);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void setName(Thread thread, String name)
/*    */   {
/*    */     try
/*    */     {
/* 77 */       thread.setName(name);
/*    */     } catch (SecurityException se) {
/* 79 */       if (this.logger.isWarnEnabled())
/* 80 */         this.logger.warn("Failed to set the thread name.", se);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.NamePreservingRunnable
 * JD-Core Version:    0.6.0
 */