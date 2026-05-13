/*    */ package com.pst.core.line.controller;
/*    */ 
/*    */ import com.pst.core.line.command.CommandWrapper;
/*    */ import com.pst.core.line.entity.Line;
/*    */ import com.pst.core.line.entity.Player;
/*    */ import com.pst.core.line.store.SystemStore;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class SystemMsgController
/*    */ {
/* 16 */   private CommandWrapper commandWrapper = new CommandWrapper();
/* 17 */   private Logger logger = Logger.getLogger(SystemMsgController.class);
/*    */ 
/*    */   public void sysMessage(byte[] message, int senderId, boolean flag, IoSession session)
/*    */   {
/* 26 */     byte[] cmdBytes = this.commandWrapper.sysMessage(message);
/* 27 */     String result = "发送系统消息成功";
/* 28 */     List receivers = new ArrayList();
/* 29 */     receivers.add(Integer.valueOf(senderId));
/* 30 */     if (flag)
/*    */     {
/* 34 */       Iterator it = SystemStore.players.keySet().iterator();
/* 35 */       this.logger.info("玩家数量:" + SystemStore.players.size());
/*    */ 
/* 37 */       while (it.hasNext()) {
/* 38 */         Integer id = (Integer)it.next();
/* 39 */         Player player = (Player)SystemStore.players.get(id);
/* 40 */         if ((player == null) || 
/* 41 */           (player.getClientSession() == null)) continue;
/* 42 */         this.logger.info("向玩家[" + player.getId() + " : " + player.getName() + "]发送系统信息");
/* 43 */         player.getClientSession().write(cmdBytes);
/*    */       }
/*    */     }
/*    */     else
/*    */     {
/* 48 */       result = "发送系统消息失败";
/*    */     }
/* 50 */     byte[] data = this.commandWrapper.systemResult(result);
/* 51 */     session.write(data);
/*    */   }
/*    */ 
/*    */   public void radioMonster(IoSession session, byte[] data)
/*    */   {
/* 63 */     byte[] cmdBytes = this.commandWrapper.radioMonster(data);
/* 64 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 65 */       Line line = (Line)SystemStore.lines.get(i);
/* 66 */       if (line != null) {
/* 67 */         IoSession temp = line.getSession();
/* 68 */         if ((temp == null) || (temp == session))
/*    */           continue;
/* 70 */         temp.write(cmdBytes);
/* 71 */         this.logger.info("通知线[" + line.getId() + "]跨线播报信息");
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.controller.SystemMsgController
 * JD-Core Version:    0.6.0
 */