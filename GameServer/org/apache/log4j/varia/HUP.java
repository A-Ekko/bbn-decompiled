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
/* 113 */     this.er = er;
/* 114 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 119 */     while (!isInterrupted())
/*     */       try {
/* 121 */         ServerSocket serverSocket = new ServerSocket(this.port);
/*     */         while (true) {
/* 123 */           Socket socket = serverSocket.accept();
/* 124 */           LogLog.debug("Connected to client at " + socket.getInetAddress());
/* 125 */           new Thread(new HUPNode(socket, this.er)).start();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 129 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.varia.HUP
 * JD-Core Version:    0.6.0
 */