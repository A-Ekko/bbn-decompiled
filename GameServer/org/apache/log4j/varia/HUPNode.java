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
/* 144 */     this.socket = socket;
/* 145 */     this.er = er;
/*     */     try {
/* 147 */       this.dis = new DataInputStream(socket.getInputStream());
/* 148 */       this.dos = new DataOutputStream(socket.getOutputStream());
/*     */     }
/*     */     catch (Exception e) {
/* 151 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/* 157 */       String line = this.dis.readUTF();
/* 158 */       LogLog.debug("Got external roll over signal.");
/* 159 */       if ("RollOver".equals(line)) {
/* 160 */         synchronized (this.er) {
/* 161 */           this.er.rollOver();
/*     */         }
/* 163 */         this.dos.writeUTF("OK");
/*     */       }
/*     */       else {
/* 166 */         this.dos.writeUTF("Expecting [RollOver] string.");
/*     */       }
/* 168 */       this.dos.close();
/*     */     }
/*     */     catch (Exception e) {
/* 171 */       LogLog.error("Unexpected exception. Exiting HUPNode.", e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.varia.HUPNode
 * JD-Core Version:    0.6.0
 */