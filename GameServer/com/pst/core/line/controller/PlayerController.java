/*     */ package com.pst.core.line.controller;
/*     */ 
/*     */ import com.pst.core.line.command.CommandWrapper;
/*     */ import com.pst.core.line.entity.Line;
/*     */ import com.pst.core.line.entity.Player;
/*     */ import com.pst.core.line.entity.Team;
/*     */ import com.pst.core.line.store.SystemStore;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class PlayerController
/*     */ {
/*  17 */   private Logger logger = Logger.getLogger(PlayerController.class);
/*  18 */   private CommandWrapper commandWrapper = new CommandWrapper();
/*     */ 
/*     */   public void online(IoSession session, int playerId, String name, int guild, int camp, int map, int line, Map<Integer, Integer> bugleNumber, List<Integer> friends, int userId)
/*     */   {
/*  34 */     Player player = (Player)SystemStore.players.get(Integer.valueOf(playerId));
/*  35 */     synchronized (SystemStore.players) {
/*  36 */       if (player == null) {
/*  37 */         player = new Player();
/*  38 */         SystemStore.players.put(Integer.valueOf(playerId), player);
/*     */       } else {
/*  40 */         synchronized (SystemStore.linemaps) {
/*  41 */           List linemap = (List)SystemStore.linemaps.get(player.getLine() + "-" + player.getMap());
/*  42 */           if (linemap != null) {
/*  43 */             synchronized (linemap) {
/*  44 */               linemap.remove(Integer.valueOf(playerId));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  50 */       synchronized (SystemStore.nplayers) {
/*  51 */         SystemStore.nplayers.put(name, player);
/*     */       }
/*     */ 
/*  54 */       synchronized (SystemStore.linemaps) {
/*  55 */         List linemap = (List)SystemStore.linemaps.get(line + "-" + map);
/*  56 */         if (linemap != null) {
/*  57 */           synchronized (linemap) {
/*  58 */             if (!linemap.contains(Integer.valueOf(playerId))) {
/*  59 */               linemap.add(Integer.valueOf(playerId));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  65 */       player.setBugles(bugleNumber);
/*  66 */       player.setCamp(camp);
/*  67 */       player.setGuild(guild);
/*  68 */       player.setId(playerId);
/*  69 */       player.setLine(line);
/*  70 */       player.setMap(map);
/*  71 */       player.setName(name);
/*  72 */       player.setServiceSession(session);
/*  73 */       player.setFriends(friends);
/*  74 */       player.setUserId(userId);
/*     */ 
/*  76 */       roleLineConnet(playerId, line);
/*     */ 
/*  78 */       this.logger.info("玩家[" + player.getId() + " ," + player.getName() + "]上线");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void roleLineConnet(int playerId, int lineId)
/*     */   {
/*  88 */     byte[] roleByte = this.commandWrapper.roleLineConnet(playerId);
/*  89 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/*  90 */       Line line = (Line)SystemStore.lines.get(i);
/*  91 */       if ((line.getSession() == null) || (line.getId() == lineId)) continue;
/*     */       try {
/*  93 */         this.logger.info("通知线[" + line.getId() + "]玩家[" + playerId + "]在线[" + lineId + "]上线");
/*  94 */         line.getSession().write(roleByte);
/*     */       } catch (Exception e) {
/*  96 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void otherLineAccountConnect(int account, int lineId)
/*     */   {
/* 108 */     byte[] roleByte = this.commandWrapper.otherLineAccountConnect(account);
/* 109 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 110 */       Line line = (Line)SystemStore.lines.get(i);
/* 111 */       if ((line.getSession() == null) || (line.getId() == lineId)) continue;
/*     */       try {
/* 113 */         this.logger.info("通知线[" + line.getId() + "]玩家账号[" + account + "]上线");
/* 114 */         line.getSession().write(roleByte);
/*     */       } catch (Exception e) {
/* 116 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connectGame(int playerId, IoSession session)
/*     */   {
/* 129 */     if (playerId > 0)
/*     */     {
/* 131 */       Player player = (Player)SystemStore.players.get(Integer.valueOf(playerId));
/*     */ 
/* 133 */       if (player != null)
/*     */       {
/* 135 */         player.setClientSession(session);
/*     */ 
/* 137 */         sendMsgToALLFriend(player, 1);
/*     */ 
/* 139 */         acceptOnlineFriendMsg(player, 1);
/*     */       }
/*     */       else
/*     */       {
/* 143 */         synchronized (SystemStore.players)
/*     */         {
/* 145 */           player = new Player();
/* 146 */           player.setClientSession(session);
/* 147 */           SystemStore.players.put(Integer.valueOf(playerId), player);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void downline(Player player)
/*     */   {
/* 158 */     if (player != null)
/* 159 */       synchronized (SystemStore.players)
/*     */       {
/* 161 */         sendMsgToALLFriend(player, 0);
/*     */ 
/* 163 */         synchronized (SystemStore.linemaps) {
/* 164 */           List linemap = (List)SystemStore.linemaps.get(player.getLine() + "-" + player.getMap());
/* 165 */           if (linemap != null) {
/* 166 */             synchronized (linemap) {
/* 167 */               linemap.remove(Integer.valueOf(player.getId()));
/*     */             }
/*     */           }
/*     */         }
/* 171 */         synchronized (SystemStore.nplayers) {
/* 172 */           SystemStore.nplayers.remove(player.getName());
/*     */         }
/*     */ 
/* 178 */         if (player.getClientSession() != null) player.getClientSession().close(false);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void friendOnOrDownline(int playerId, Integer[] receiver, int type)
/*     */   {
/* 191 */     byte[] data = this.commandWrapper.informRole(playerId, type);
/*     */ 
/* 193 */     for (int i = 0; i < receiver.length; i++) {
/* 194 */       Player player = (Player)SystemStore.players.get(receiver[i]);
/* 195 */       if ((player != null) && (player.getClientSession() != null))
/* 196 */         player.getClientSession().write(data);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createTeam(int masterId, int partnerId, String teamId)
/*     */   {
/* 208 */     Player player = (Player)SystemStore.players.get(Integer.valueOf(masterId));
/* 209 */     if (player != null) {
/* 210 */       Team team = new Team();
/* 211 */       team.getMembers().add(Integer.valueOf(masterId));
/* 212 */       team.getMembers().add(Integer.valueOf(partnerId));
/* 213 */       synchronized (SystemStore.teams) {
/* 214 */         synchronized (SystemStore.pteams) {
/* 215 */           SystemStore.teams.put(player.getLine() + "-" + teamId, team);
/* 216 */           SystemStore.pteams.put(Integer.valueOf(masterId), team);
/* 217 */           SystemStore.pteams.put(Integer.valueOf(partnerId), team);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void joinTeam(int playerId, String teamId)
/*     */   {
/* 229 */     Player player = (Player)SystemStore.players.get(Integer.valueOf(playerId));
/* 230 */     if (player != null) {
/* 231 */       Team team = (Team)SystemStore.teams.get(player.getLine() + "-" + teamId);
/* 232 */       if (team != null)
/* 233 */         synchronized (team.getMembers()) {
/* 234 */           if (!team.getMembers().contains(Integer.valueOf(playerId))) {
/* 235 */             team.getMembers().add(Integer.valueOf(playerId));
/*     */           }
/* 237 */           synchronized (SystemStore.pteams) {
/* 238 */             SystemStore.pteams.put(Integer.valueOf(playerId), team);
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void leaveTeam(int playerId, String teamId)
/*     */   {
/* 252 */     Player player = (Player)SystemStore.players.get(Integer.valueOf(playerId));
/* 253 */     if (player != null) {
/* 254 */       Team team = (Team)SystemStore.teams.get(player.getLine() + "-" + teamId);
/* 255 */       if (team != null)
/* 256 */         synchronized (team.getMembers()) {
/* 257 */           team.getMembers().remove(Integer.valueOf(playerId));
/* 258 */           synchronized (SystemStore.pteams) {
/* 259 */             SystemStore.pteams.remove(Integer.valueOf(playerId));
/*     */           }
/* 261 */           if (team.getMembers().size() == 0) {
/* 262 */             SystemStore.teams.remove(player.getLine() + "-" + teamId);
/* 263 */             for (int i = 0; i < team.getMembers().size(); i++)
/* 264 */               SystemStore.pteams.remove(team.getMembers().get(i));
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void changeMap(IoSession session, int playerId, int map)
/*     */   {
/* 281 */     Player player = (Player)SystemStore.players.get(Integer.valueOf(playerId));
/* 282 */     if (player != null) {
/* 283 */       synchronized (SystemStore.linemaps) {
/* 284 */         List linemap = (List)SystemStore.linemaps.get(player.getLine() + "-" + player.getMap());
/* 285 */         if (linemap != null) {
/* 286 */           synchronized (linemap) {
/* 287 */             linemap.remove(Integer.valueOf(playerId));
/*     */           }
/*     */         }
/* 290 */         linemap = (List)SystemStore.linemaps.get(player.getLine() + "-" + map);
/* 291 */         if (linemap != null) {
/* 292 */           synchronized (linemap) {
/* 293 */             if (!linemap.contains(Integer.valueOf(playerId))) {
/* 294 */               linemap.add(Integer.valueOf(playerId));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 299 */       player.setMap(map);
/* 300 */       player.setServiceSession(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void bugleNumber(int playerId, Map<Integer, Integer> bugleNumber)
/*     */   {
/* 311 */     Player player = (Player)SystemStore.players.get(Integer.valueOf(playerId));
/* 312 */     if (player != null) {
/* 313 */       Iterator it = bugleNumber.keySet().iterator();
/*     */ 
/* 316 */       while (it.hasNext()) {
/* 317 */         Integer id = (Integer)it.next();
/* 318 */         Integer newNum = (Integer)bugleNumber.get(id);
/* 319 */         Integer oldNum = (Integer)player.getBugles().get(id);
/* 320 */         if (oldNum != null) {
/* 321 */           Integer totalNum = Integer.valueOf(newNum.intValue() + oldNum.intValue());
/* 322 */           player.getBugles().put(id, totalNum);
/*     */         } else {
/* 324 */           player.getBugles().put(id, newNum);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sendMsgToALLFriend(Player player, int type)
/*     */   {
/* 336 */     List friends = player.getFriends();
/* 337 */     if ((friends != null) && (friends.size() > 0)) {
/* 338 */       byte[] ownBytes = this.commandWrapper.informRole(player.getId(), type);
/*     */ 
/* 340 */       for (int i = 0; i < friends.size(); i++) {
/* 341 */         Player friendEntity = (Player)SystemStore.players.get(friends.get(i));
/* 342 */         if ((friendEntity == null) || (friendEntity.getClientSession() == null))
/*     */           continue;
/* 344 */         friendEntity.getClientSession().write(ownBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void acceptOnlineFriendMsg(Player player, int type)
/*     */   {
/* 356 */     List friends = player.getFriends();
/* 357 */     if ((friends != null) && (friends.size() > 0))
/*     */     {
/* 359 */       for (int i = 0; i < friends.size(); i++) {
/* 360 */         Player friendEntity = (Player)SystemStore.players.get(friends.get(i));
/* 361 */         if (friendEntity == null)
/*     */           continue;
/* 363 */         byte[] friendBytes = this.commandWrapper.informRole(friendEntity.getId(), type);
/* 364 */         player.getClientSession().write(friendBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.controller.PlayerController
 * JD-Core Version:    0.6.0
 */