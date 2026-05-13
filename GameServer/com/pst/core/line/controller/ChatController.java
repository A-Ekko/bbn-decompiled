/*     */ package com.pst.core.line.controller;
/*     */ 
/*     */ import com.pst.core.line.command.CommandWrapper;
/*     */ import com.pst.core.line.entity.Player;
/*     */ import com.pst.core.line.entity.Team;
/*     */ import com.pst.core.line.store.SystemStore;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class ChatController
/*     */ {
/*  16 */   private CommandWrapper commandWrapper = new CommandWrapper();
/*  17 */   private Logger logger = Logger.getLogger(ChatController.class);
/*     */ 
/*     */   public void chat(int playerId, int type, String target, byte[] content, String dataStr)
/*     */   {
/*  22 */     Player player = (Player)SystemStore.players.get(Integer.valueOf(playerId));
/*  23 */     if (player != null)
/*     */     {
/*  25 */       if (type == 0)
/*     */       {
/*  27 */         Player targetPlayer = (Player)SystemStore.nplayers.get(target);
/*  28 */         if (targetPlayer != null) {
/*  29 */           if (targetPlayer.getClientSession() != null) targetPlayer.getClientSession().write(this.commandWrapper.chat(playerId, player.getName(), type, content, dataStr));
/*     */         }
/*  31 */         else if (player.getClientSession() != null) {
/*  32 */           player.getClientSession().write(this.commandWrapper.charError("玩家[" + target + "]不在线"));
/*     */         }
/*     */ 
/*     */       }
/*  37 */       else if (type == 1)
/*     */       {
/*  39 */         if (player.getBugles().size() > 0)
/*     */         {
/*  41 */           if (player.getClientSession() != null)
/*     */           {
/*  43 */             int bugleId = Integer.parseInt(target);
/*  44 */             Integer num = (Integer)player.getBugles().get(Integer.valueOf(bugleId));
/*  45 */             if ((num != null) && (num.intValue() > 0))
/*     */             {
/*  47 */               player.getServiceSession().write(this.commandWrapper.decreaseBugle(playerId, bugleId));
/*     */ 
/*  49 */               byte[] data = this.commandWrapper.chat(playerId, player.getName(), type, content, dataStr);
/*     */ 
/*  51 */               Iterator it = SystemStore.players.keySet().iterator();
/*     */ 
/*  54 */               while (it.hasNext())
/*     */               {
/*  56 */                 Integer id = (Integer)it.next();
/*  57 */                 Player temp = (Player)SystemStore.players.get(id);
/*  58 */                 if ((temp == null) || (temp.getId() == playerId)) {
/*     */                   continue;
/*     */                 }
/*  61 */                 if (temp.getClientSession() == null)
/*     */                   continue;
/*  63 */                 temp.getClientSession().write(data);
/*     */               }
/*     */ 
/*  75 */               player.getBugles().put(Integer.valueOf(Integer.parseInt(target)), Integer.valueOf(num.intValue() - 1));
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*  89 */       else if (type == 2) {
/*  90 */         List list = (List)SystemStore.linemaps.get(player.getLine() + "-" + player.getMap());
/*     */ 
/*  92 */         if (list != null) {
/*  93 */           byte[] data = this.commandWrapper.chat(playerId, player.getName(), type, content, dataStr);
/*  94 */           for (int i = 0; i < list.size(); i++) {
/*  95 */             Player temp = (Player)SystemStore.players.get(list.get(i));
/*  96 */             if ((temp != null) && (temp.getId() != playerId) && (temp.getCamp() == player.getCamp()))
/*     */             {
/*  98 */               if (temp.getClientSession() != null)
/*  99 */                 temp.getClientSession().write(data);
/*     */             }
/* 101 */             else if (temp == null)
/* 102 */               this.logger.info("玩家[" + player.getId() + " : " + player.getName() + "]发送场景聊天对象不存在");
/*     */           }
/*     */         }
/*     */       }
/* 106 */       else if (type == 3) {
/* 107 */         Team team = (Team)SystemStore.pteams.get(Integer.valueOf(playerId));
/* 108 */         if (team != null)
/*     */         {
/* 110 */           byte[] data = this.commandWrapper.chat(playerId, player.getName(), type, content, dataStr);
/* 111 */           for (int i = 0; i < team.getMembers().size(); i++) {
/* 112 */             Player temp = (Player)SystemStore.players.get(team.getMembers().get(i));
/* 113 */             if ((temp == null) || (temp.getId() == playerId))
/*     */               continue;
/* 115 */             if (temp.getClientSession() != null) {
/* 116 */               temp.getClientSession().write(data);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/* 126 */       else if (type != 4)
/*     */       {
/* 128 */         if (type == 5) {
/* 129 */           Iterator it = SystemStore.players.keySet().iterator();
/*     */ 
/* 132 */           byte[] data = this.commandWrapper.chat(playerId, player.getName(), type, content, dataStr);
/* 133 */           while (it.hasNext()) {
/* 134 */             Integer id = (Integer)it.next();
/* 135 */             Player temp = (Player)SystemStore.players.get(id);
/* 136 */             if ((temp == null) || (temp.getId() == playerId) || (temp.getCamp() != player.getCamp()))
/*     */               continue;
/* 138 */             if (temp.getClientSession() != null)
/* 139 */               temp.getClientSession().write(data);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void horn(int playerId, int type, String target, byte[] content, String dataStr)
/*     */   {
/* 163 */     byte[] data = this.commandWrapper.chat(playerId, target, type, content, dataStr);
/*     */ 
/* 165 */     for (Map.Entry entry : SystemStore.players.entrySet())
/*     */     {
/* 167 */       if (((Player)entry.getValue()).getId() == playerId)
/*     */         continue;
/* 169 */       if (((Player)entry.getValue()).getClientSession() == null)
/*     */       {
/* 171 */         this.logger.info("[Chat][horn][Info:palyerID:'" + ((Player)entry.getValue()).getId() + "'][Fail:客户端Session为null]");
/*     */       }
/*     */       else
/*     */       {
/* 175 */         ((Player)entry.getValue()).getClientSession().write(data);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.controller.ChatController
 * JD-Core Version:    0.6.0
 */