/*    */ package com.pst.core.line;
/*    */ 
/*    */ import com.pst.core.line.entity.Line;
/*    */ import com.pst.core.line.store.SystemStore;
/*    */ import java.util.List;
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.mina.core.service.IoHandlerAdapter;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class ServerHandle extends IoHandlerAdapter
/*    */ {
/* 13 */   private Logger logger = Logger.getLogger(ServerHandle.class);
/* 14 */   private Console console = new Console();
/*    */ 
/*    */   public ServerHandle() {
/* 17 */     this.logger.setLevel(Level.DEBUG);
/*    */   }
/*    */ 
/*    */   public void sessionCreated(IoSession session) {
/* 21 */     this.logger.info("创建角色Session[" + session.getId() + "] session=" + session);
/*    */   }
/*    */ 
/*    */   public void sessionOpened(IoSession session)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void sessionClosed(IoSession session)
/*    */   {
/* 30 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 31 */       Line line = (Line)SystemStore.lines.get(i);
/* 32 */       if (session.equals(line.getSession())) {
/* 33 */         this.console.lineClose(line);
/* 34 */         this.logger.info("线[" + line.getId() + "]关闭");
/* 35 */         break;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void sessionIdle(IoSession session)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void messageReceived(IoSession session, Object data)
/*    */   {
/* 49 */     this.console.action(session, (byte[])data);
/*    */   }
/*    */ 
/*    */   public void messageSent(IoSession session, Object data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void exceptionCaught(IoSession session, Throwable throwables)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.ServerHandle
 * JD-Core Version:    0.6.0
 */