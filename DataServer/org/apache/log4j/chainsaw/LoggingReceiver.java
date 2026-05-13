/*    */ package org.apache.log4j.chainsaw;
/*    */ 
/*    */ import java.io.EOFException;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.net.InetAddress;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketException;
/*    */ import org.apache.log4j.Category;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ class LoggingReceiver extends Thread
/*    */ {
/* 26 */   private static final Logger LOG = Logger.getLogger(LoggingReceiver.class);
/*    */   private final MyTableModel mModel;
/*    */   private final ServerSocket mSvrSock;
/*    */ 
/*    */   LoggingReceiver(MyTableModel aModel, int aPort)
/*    */     throws IOException
/*    */   {
/* 89 */     setDaemon(true);
/* 90 */     this.mModel = aModel;
/* 91 */     this.mSvrSock = new ServerSocket(aPort);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 96 */     LOG.info("Thread started");
/*    */     try {
/*    */       while (true) {
/* 99 */         LOG.debug("Waiting for a connection");
/* 100 */         Socket client = this.mSvrSock.accept();
/* 101 */         LOG.debug("Got a connection from " + client.getInetAddress().getHostName());
/*    */ 
/* 103 */         Thread t = new Thread(new Slurper(client));
/* 104 */         t.setDaemon(true);
/* 105 */         t.start();
/*    */       }
/*    */     } catch (IOException e) {
/* 108 */       LOG.error("Error in accepting connections, stopping.", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   private class Slurper
/*    */     implements Runnable
/*    */   {
/*    */     private final Socket mClient;
/*    */ 
/*    */     Slurper(Socket aClient)
/*    */     {
/* 44 */       this.mClient = aClient;
/*    */     }
/*    */ 
/*    */     public void run()
/*    */     {
/* 49 */       LoggingReceiver.LOG.debug("Starting to get data");
/*    */       try {
/* 51 */         ObjectInputStream ois = new ObjectInputStream(this.mClient.getInputStream());
/*    */         while (true)
/*    */         {
/* 54 */           LoggingEvent event = (LoggingEvent)ois.readObject();
/* 55 */           LoggingReceiver.this.mModel.addEvent(new EventDetails(event));
/*    */         }
/*    */       } catch (EOFException e) {
/* 58 */         LoggingReceiver.LOG.info("Reached EOF, closing connection");
/*    */       } catch (SocketException e) {
/* 60 */         LoggingReceiver.LOG.info("Caught SocketException, closing connection");
/*    */       } catch (IOException e) {
/* 62 */         LoggingReceiver.LOG.warn("Got IOException, closing connection", e);
/*    */       } catch (ClassNotFoundException e) {
/* 64 */         LoggingReceiver.LOG.warn("Got ClassNotFoundException, closing connection", e);
/*    */       }
/*    */       try
/*    */       {
/* 68 */         this.mClient.close();
/*    */       } catch (IOException e) {
/* 70 */         LoggingReceiver.LOG.warn("Error closing connection", e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.LoggingReceiver
 * JD-Core Version:    0.6.0
 */