/*    */ package com.pst.core.shutdown;
/*    */ 
/*    */ import com.pst.config.SystemConfig;
/*    */ import com.pst.core.data.DataQueue;
/*    */ import java.util.Vector;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.mina.transport.socket.SocketAcceptor;
/*    */ 
/*    */ public class ShutdownHook
/*    */   implements Runnable
/*    */ {
/*    */   public void run()
/*    */   {
/* 12 */     Logger logger = Logger.getLogger(ShutdownHook.class);
/*    */ 
/* 14 */     logger.info("启动关闭进程....................");
/* 15 */     check(logger);
/* 16 */     unbind(logger);
/*    */   }
/*    */ 
/*    */   private void unbind(Logger logger) {
/*    */     try {
/* 21 */       SystemConfig.acceptor.unbind();
/* 22 */       logger.info("关闭端口数据接收监听器...........");
/*    */     } catch (Exception e) {
/* 24 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   private void check(Logger logger) {
/*    */     while (true) {
/* 30 */       int size = DataQueue.sqlQueue.size();
/* 31 */       if (size == 0) {
/* 32 */         logger.info("现在队列中没有数据要保存,关闭系统!");
/* 33 */         break;
/*    */       }
/* 35 */       logger.info("现在还有" + size + "条数据没有处理,等等数据处理完成");
/*    */       try {
/* 37 */         Thread.sleep(1000L); } catch (InterruptedException e) { e.printStackTrace();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.core.shutdown.ShutdownHook
 * JD-Core Version:    0.6.0
 */