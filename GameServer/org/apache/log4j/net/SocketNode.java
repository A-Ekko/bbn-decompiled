/*    */ package org.apache.log4j.net;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.EOFException;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketException;
/*    */ import org.apache.log4j.Category;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.Priority;
/*    */ import org.apache.log4j.spi.LoggerRepository;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class SocketNode
/*    */   implements Runnable
/*    */ {
/*    */   Socket socket;
/*    */   LoggerRepository hierarchy;
/*    */   ObjectInputStream ois;
/* 49 */   static Logger logger = Logger.getLogger(SocketNode.class);
/*    */ 
/*    */   public SocketNode(Socket socket, LoggerRepository hierarchy) {
/* 52 */     this.socket = socket;
/* 53 */     this.hierarchy = hierarchy;
/*    */     try {
/* 55 */       this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 59 */       logger.error("Could not open ObjectInputStream to " + socket, e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/* 74 */       if (this.ois != null)
/*    */         while (true)
/*    */         {
/* 77 */           LoggingEvent event = (LoggingEvent)this.ois.readObject();
/*    */ 
/* 79 */           Logger remoteLogger = this.hierarchy.getLogger(event.getLoggerName());
/*    */ 
/* 82 */           if (!event.getLevel().isGreaterOrEqual(remoteLogger.getEffectiveLevel()))
/*    */             continue;
/* 84 */           remoteLogger.callAppenders(event);
/*    */         }
/*    */     }
/*    */     catch (EOFException ex)
/*    */     {
/* 89 */       logger.info("Caught java.io.EOFException closing conneciton.");
/*    */     } catch (SocketException ex) {
/* 91 */       logger.info("Caught java.net.SocketException closing conneciton.");
/*    */     } catch (IOException ex) {
/* 93 */       logger.info("Caught java.io.IOException: " + e);
/* 94 */       logger.info("Closing connection.");
/*    */     } catch (Exception ex) {
/* 96 */       logger.error("Unexpected exception. Closing conneciton.", e);
/*    */     } finally {
/* 98 */       if (this.ois != null) {
/*    */         try {
/* 100 */           this.ois.close();
/*    */         } catch (Exception e) {
/* 102 */           logger.info("Could not close connection.", e);
/*    */         }
/*    */       }
/* 105 */       if (this.socket != null)
/*    */         try {
/* 107 */           this.socket.close();
/*    */         }
/*    */         catch (IOException ex)
/*    */         {
/*    */         }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.SocketNode
 * JD-Core Version:    0.6.0
 */