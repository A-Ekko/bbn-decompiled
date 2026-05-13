/*    */ package com.pst.core.line.entity;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class Line
/*    */ {
/*    */   private int id;
/*    */   private String name;
/*    */   private String ip;
/*    */   private int port;
/*    */   private int status;
/*    */   private IoSession session;
/*    */ 
/*    */   public IoSession getSession()
/*    */   {
/* 35 */     return this.session;
/*    */   }
/*    */ 
/*    */   public void setSession(IoSession session) {
/* 39 */     this.session = session;
/*    */   }
/*    */ 
/*    */   public int getId() {
/* 43 */     return this.id;
/*    */   }
/*    */ 
/*    */   public void setId(int id) {
/* 47 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 51 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 55 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String getIp() {
/* 59 */     return this.ip;
/*    */   }
/*    */ 
/*    */   public void setIp(String ip) {
/* 63 */     this.ip = ip;
/*    */   }
/*    */ 
/*    */   public int getPort() {
/* 67 */     return this.port;
/*    */   }
/*    */ 
/*    */   public void setPort(int port) {
/* 71 */     this.port = port;
/*    */   }
/*    */ 
/*    */   public int getStatus() {
/* 75 */     return this.status;
/*    */   }
/*    */ 
/*    */   public void setStatus(int status) {
/* 79 */     this.status = status;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.entity.Line
 * JD-Core Version:    0.6.0
 */