/*    */ package org.apache.log4j.varia;
/*    */ 
/*    */ import org.apache.log4j.FileAppender;
/*    */ import org.apache.log4j.RollingFileAppender;
/*    */ 
/*    */ public class ExternallyRolledFileAppender extends RollingFileAppender
/*    */ {
/*    */   public static final String ROLL_OVER = "RollOver";
/*    */   public static final String OK = "OK";
/* 62 */   int port = 0;
/*    */   HUP hup;
/*    */ 
/*    */   public void setPort(int port)
/*    */   {
/* 78 */     this.port = port;
/*    */   }
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 86 */     return this.port;
/*    */   }
/*    */ 
/*    */   public void activateOptions()
/*    */   {
/* 94 */     super.activateOptions();
/* 95 */     if (this.port != 0) {
/* 96 */       if (this.hup != null) {
/* 97 */         this.hup.interrupt();
/*    */       }
/* 99 */       this.hup = new HUP(this, this.port);
/* 100 */       this.hup.setDaemon(true);
/* 101 */       this.hup.start();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.varia.ExternallyRolledFileAppender
 * JD-Core Version:    0.6.0
 */