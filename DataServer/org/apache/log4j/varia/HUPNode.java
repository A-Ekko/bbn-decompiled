/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.RollingFileAppender;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ class HUPNode
/*     */   implements Runnable
/*     */ {
/*     */   Socket socket;
/*     */   DataInputStream dis;
/*     */   DataOutputStream dos;
/*     */   ExternallyRolledFileAppender er;
/*     */ 
/*     */   public HUPNode(Socket socket, ExternallyRolledFileAppender er)
/*     */   {
/* 133 */     this.socket = socket;
/* 134 */     this.er = er;
/*     */     try {
/* 136 */       this.dis = new DataInputStream(socket.getInputStream());
/* 137 */       this.dos = new DataOutputStream(socket.getOutputStream());
/*     */     }
/*     */     catch (Exception e) {
/* 140 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/* 146 */       String line = this.dis.readUTF();
/* 147 */       LogLog.debug("Got external roll over signal.");
/* 148 */       if ("RollOver".equals(line)) {
/* 149 */         synchronized (this.er) {
/* 150 */           this.er.rollOver();
/*     */         }
/* 152 */         this.dos.writeUTF("OK");
/*     */       }
/*     */       else {
/* 155 */         this.dos.writeUTF("Expecting [RollOver] string.");
/*     */       }
/* 157 */       this.dos.close();
/*     */     }
/*     */     catch (Exception e) {
/* 160 */       LogLog.error("Unexpected exception. Exiting HUPNode.", e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.HUPNode
 * JD-Core Version:    0.6.0
 */