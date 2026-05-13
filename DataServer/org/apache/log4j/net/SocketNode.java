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
/* 40 */   static Logger logger = Logger.getLogger(SocketNode.class);
/*    */ 
/*    */   public SocketNode(Socket socket, LoggerRepository hierarchy) {
/* 43 */     this.socket = socket;
/* 44 */     this.hierarchy = hierarchy;
/*    */     try {
/* 46 */       this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 50 */       logger.error("Could not open ObjectInputStream to " + socket, e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/*    */       while (true)
/*    */       {
/* 67 */         LoggingEvent event = (LoggingEvent)this.ois.readObject();
/*    */ 
/* 69 */         Logger remoteLogger = this.hierarchy.getLogger(event.getLoggerName());
/*    */ 
/* 72 */         if (!event.getLevel().isGreaterOrEqual(remoteLogger.getEffectiveLevel()))
/*    */           continue;
/* 74 */         remoteLogger.callAppenders(event);
/*    */       }
/*    */     }
/*    */     catch (EOFException e) {
/* 78 */       logger.info("Caught java.io.EOFException closing conneciton.");
/*    */     } catch (SocketException e) {
/* 80 */       logger.info("Caught java.net.SocketException closing conneciton.");
/*    */     } catch (IOException e) {
/* 82 */       logger.info("Caught java.io.IOException: " + e);
/* 83 */       logger.info("Closing connection.");
/*    */     } catch (Exception e) {
/* 85 */       logger.error("Unexpected exception. Closing conneciton.", e);
/*    */     }
/*    */     try
/*    */     {
/* 89 */       this.ois.close();
/*    */     } catch (Exception e) {
/* 91 */       logger.info("Could not close connection.", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.SocketNode
 * JD-Core Version:    0.6.0
 */