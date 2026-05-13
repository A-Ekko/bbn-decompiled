/*    */ package com.pst.core.shutdown;
/*    */ 
/*    */ import com.pst.core.config.SystemConfig;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.mina.transport.socket.SocketAcceptor;
/*    */ 
/*    */ public class ShutdownHook
/*    */   implements Runnable
/*    */ {
/*    */   public void run()
/*    */   {
/* 11 */     Logger logger = Logger.getLogger(ShutdownHook.class);
/*    */ 
/* 13 */     logger.info("启动关闭进程....................");
/* 14 */     unbind(logger);
/*    */   }
/*    */ 
/*    */   private void unbind(Logger logger) {
/*    */     try {
/* 19 */       SystemConfig.acceptor.unbind();
/* 20 */       logger.info("关闭端口数据接收监听器...........");
/*    */     } catch (Exception e) {
/* 22 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.shutdown.ShutdownHook
 * JD-Core Version:    0.6.0
 */