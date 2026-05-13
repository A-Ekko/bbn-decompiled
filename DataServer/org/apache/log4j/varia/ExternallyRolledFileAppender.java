/*    */ package org.apache.log4j.varia;
/*    */ 
/*    */ import org.apache.log4j.FileAppender;
/*    */ import org.apache.log4j.RollingFileAppender;
/*    */ 
/*    */ public class ExternallyRolledFileAppender extends RollingFileAppender
/*    */ {
/*    */   public static final String ROLL_OVER = "RollOver";
/*    */   public static final String OK = "OK";
/* 51 */   int port = 0;
/*    */   HUP hup;
/*    */ 
/*    */   public void setPort(int port)
/*    */   {
/* 67 */     this.port = port;
/*    */   }
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 75 */     return this.port;
/*    */   }
/*    */ 
/*    */   public void activateOptions()
/*    */   {
/* 83 */     super.activateOptions();
/* 84 */     if (this.port != 0) {
/* 85 */       if (this.hup != null) {
/* 86 */         this.hup.interrupt();
/*    */       }
/* 88 */       this.hup = new HUP(this, this.port);
/* 89 */       this.hup.setDaemon(true);
/* 90 */       this.hup.start();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.ExternallyRolledFileAppender
 * JD-Core Version:    0.6.0
 */