/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ class HUP extends Thread
/*     */ {
/*     */   int port;
/*     */   ExternallyRolledFileAppender er;
/*     */ 
/*     */   HUP(ExternallyRolledFileAppender er, int port)
/*     */   {
/* 102 */     this.er = er;
/* 103 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 108 */     while (!isInterrupted())
/*     */       try {
/* 110 */         ServerSocket serverSocket = new ServerSocket(this.port);
/*     */         while (true) {
/* 112 */           Socket socket = serverSocket.accept();
/* 113 */           LogLog.debug("Connected to client at " + socket.getInetAddress());
/* 114 */           new Thread(new HUPNode(socket, this.er)).start();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 118 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.HUP
 * JD-Core Version:    0.6.0
 */