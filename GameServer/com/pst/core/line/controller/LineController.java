/*     */ package com.pst.core.line.controller;
/*     */ 
/*     */ import com.pst.core.config.SystemConfig;
/*     */ import com.pst.core.line.command.CommandWrapper;
/*     */ import com.pst.core.line.command.info.LineInfo;
/*     */ import com.pst.core.line.command.info.ServerInfo;
/*     */ import com.pst.core.line.entity.Line;
/*     */ import com.pst.core.line.entity.Player;
/*     */ import com.pst.core.line.store.SystemStore;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class LineController
/*     */ {
/*  19 */   private CommandWrapper commandWrapper = new CommandWrapper();
/*  20 */   private Logger logger = Logger.getLogger(LineController.class);
/*     */ 
/*     */   public void lineOpen(int id, String ip, int port, String name, IoSession session)
/*     */   {
/*  28 */     boolean has = false;
/*  29 */     synchronized (SystemStore.lines) {
/*  30 */       for (int i = 0; i < SystemStore.lines.size(); i++) {
/*  31 */         Line line = (Line)SystemStore.lines.get(i);
/*  32 */         if (line != null) {
/*  33 */           if (line.getId() == id) {
/*  34 */             if ((line.getSession() != null) && (line.getSession().isConnected()) && (!line.getSession().isClosing())) {
/*  35 */               line.getSession().close(false);
/*     */             }
/*  37 */             line.setSession(session);
/*  38 */             line.setIp(ip);
/*  39 */             line.setPort(port);
/*  40 */             line.setName(name);
/*  41 */             line.setStatus(1);
/*     */ 
/*  43 */             has = true;
/*  44 */             break;
/*     */           }
/*     */         }
/*  47 */         else SystemStore.lines.remove(i--);
/*     */       }
/*     */ 
/*  50 */       if (!has) {
/*  51 */         Line newLine = new Line();
/*     */ 
/*  53 */         newLine.setId(id);
/*  54 */         newLine.setSession(session);
/*  55 */         newLine.setIp(ip);
/*  56 */         newLine.setPort(port);
/*  57 */         newLine.setName(name);
/*  58 */         newLine.setStatus(1);
/*     */ 
/*  60 */         boolean add = false;
/*  61 */         for (int i = 0; i < SystemStore.lines.size(); i++) {
/*  62 */           Line line = (Line)SystemStore.lines.get(i);
/*  63 */           if (line.getId() > id) {
/*  64 */             SystemStore.lines.add(i, newLine);
/*  65 */             add = true;
/*  66 */             break;
/*     */           }
/*     */         }
/*  69 */         if (!add) {
/*  70 */           SystemStore.lines.add(newLine);
/*     */         }
/*     */       }
/*  73 */       this.logger.info("线[" + id + " : " + name + "]开通,IP[" + ip + "],端点[" + port + "]");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createGuild(IoSession session, byte[] data)
/*     */   {
/*  80 */     byte[] cmdBytes = this.commandWrapper.createGuild(data);
/*  81 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/*  82 */       Line line = (Line)SystemStore.lines.get(i);
/*  83 */       if (line != null) {
/*  84 */         IoSession temp = line.getSession();
/*  85 */         if ((temp != null) && (temp != session))
/*  86 */           temp.write(cmdBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addApplyJionGuild(IoSession session, byte[] data)
/*     */   {
/*  95 */     byte[] cmdBytes = this.commandWrapper.addApplyJionGuild(data);
/*  96 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/*  97 */       Line line = (Line)SystemStore.lines.get(i);
/*  98 */       if (line != null) {
/*  99 */         IoSession temp = line.getSession();
/* 100 */         if ((temp != null) && (temp != session)) {
/* 101 */           this.logger.info("通知线[" + line.getId() + "]");
/* 102 */           temp.write(cmdBytes);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkRoleInfo(IoSession session, byte[] data)
/*     */   {
/* 116 */     byte[] cmdBytes = this.commandWrapper.checkRoleInfo(data);
/* 117 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 118 */       Line line = (Line)SystemStore.lines.get(i);
/* 119 */       if (line != null) {
/* 120 */         IoSession temp = line.getSession();
/* 121 */         if ((temp == null) || (temp == session))
/*     */           continue;
/* 123 */         temp.write(cmdBytes);
/* 124 */         this.logger.info("通知线[" + line.getId() + "]跨线查询他线玩家信息");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void findRoleInfo(IoSession session, byte[] data)
/*     */   {
/* 138 */     byte[] cmdBytes = this.commandWrapper.findRoleInfo(data);
/* 139 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 140 */       Line line = (Line)SystemStore.lines.get(i);
/* 141 */       if (line != null) {
/* 142 */         IoSession temp = line.getSession();
/* 143 */         if ((temp == null) || (temp == session))
/*     */           continue;
/* 145 */         temp.write(cmdBytes);
/* 146 */         this.logger.info("通知线[" + line.getId() + "]跨线返回查询他线玩家信息");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addGuildRole(IoSession session, byte[] data)
/*     */   {
/* 156 */     byte[] cmdBytes = this.commandWrapper.addGuildRole(data);
/* 157 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 158 */       Line line = (Line)SystemStore.lines.get(i);
/* 159 */       if (line != null) {
/* 160 */         IoSession temp = line.getSession();
/* 161 */         if ((temp != null) && (temp != session)) {
/* 162 */           temp.write(cmdBytes);
/* 163 */           this.logger.info("通知线[" + line.getId() + "]添加玩家进入公会");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addRoleGuild(IoSession session, byte[] data)
/*     */   {
/* 172 */     byte[] cmdBytes = this.commandWrapper.addRoleGuild(data);
/* 173 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 174 */       Line line = (Line)SystemStore.lines.get(i);
/* 175 */       if (line != null) {
/* 176 */         IoSession temp = line.getSession();
/* 177 */         if ((temp != null) && (temp != session)) {
/* 178 */           temp.write(cmdBytes);
/* 179 */           this.logger.info("通知线[" + line.getId() + "]玩家进入公会xxxxxxxxxxxxxxx");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void rejectApplyRole(IoSession session, byte[] data)
/*     */   {
/* 188 */     byte[] cmdBytes = this.commandWrapper.rejectApplyRole(data);
/* 189 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 190 */       Line line = (Line)SystemStore.lines.get(i);
/* 191 */       if (line != null) {
/* 192 */         IoSession temp = line.getSession();
/* 193 */         if ((temp != null) && (temp != session))
/* 194 */           temp.write(cmdBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void rejectRole(IoSession session, byte[] data)
/*     */   {
/* 203 */     byte[] cmdBytes = this.commandWrapper.rejectRole(data);
/* 204 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 205 */       Line line = (Line)SystemStore.lines.get(i);
/* 206 */       if (line != null) {
/* 207 */         IoSession temp = line.getSession();
/* 208 */         if ((temp != null) && (temp != session))
/* 209 */           temp.write(cmdBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dismissGuild(IoSession session, byte[] data)
/*     */   {
/* 223 */     byte[] cmdBytes = this.commandWrapper.dismissGuild(data);
/* 224 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 225 */       Line line = (Line)SystemStore.lines.get(i);
/* 226 */       if (line != null) {
/* 227 */         IoSession temp = line.getSession();
/* 228 */         if ((temp != null) && (temp != session))
/* 229 */           temp.write(cmdBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void changeGuildInfo(IoSession session, byte[] data)
/*     */   {
/* 243 */     byte[] cmdBytes = this.commandWrapper.changeGuildInfo(data);
/* 244 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 245 */       Line line = (Line)SystemStore.lines.get(i);
/* 246 */       if (line != null) {
/* 247 */         IoSession temp = line.getSession();
/* 248 */         if ((temp != null) && (temp != session))
/* 249 */           temp.write(cmdBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void careteTitle(IoSession session, byte[] data)
/*     */   {
/* 268 */     byte[] cmdBytes = this.commandWrapper.careteTitle(data);
/* 269 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 270 */       Line line = (Line)SystemStore.lines.get(i);
/* 271 */       if (line != null) {
/* 272 */         IoSession temp = line.getSession();
/* 273 */         if ((temp != null) && (temp != session)) {
/* 274 */           this.logger.info("line[" + line.getId() + " : " + line.getIp() + "]");
/* 275 */           temp.write(cmdBytes);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void alertTitle(IoSession session, byte[] data)
/*     */   {
/* 289 */     byte[] cmdBytes = this.commandWrapper.alertTitle(data);
/* 290 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 291 */       Line line = (Line)SystemStore.lines.get(i);
/* 292 */       if (line != null) {
/* 293 */         IoSession temp = line.getSession();
/* 294 */         if ((temp != null) && (temp != session))
/* 295 */           temp.write(cmdBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void delTitle(IoSession session, byte[] data)
/*     */   {
/* 309 */     byte[] cmdBytes = this.commandWrapper.delTitle(data);
/* 310 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 311 */       Line line = (Line)SystemStore.lines.get(i);
/* 312 */       if (line != null) {
/* 313 */         IoSession temp = line.getSession();
/* 314 */         if ((temp != null) && (temp != session))
/* 315 */           temp.write(cmdBytes);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void lineClose(Line line)
/*     */   {
/* 336 */     line.setStatus(0);
/*     */   }
/*     */ 
/*     */   public void gameLineInfo(IoSession session, int account) {
/* 340 */     this.logger.info("玩家账号[" + account + "]获取线的信息");
/*     */ 
/* 343 */     otherLineAccountConnect(account);
/*     */ 
/* 346 */     ServerInfo si = new ServerInfo();
/* 347 */     si.n = SystemStore.server;
/*     */ 
/* 349 */     si.l = new ArrayList();
/*     */ 
/* 352 */     LineInfo minLineInfo = null;
/*     */ 
/* 354 */     int min = 99999999;
/* 355 */     int playerNum = 0;
/* 356 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 357 */       Line line = (Line)SystemStore.lines.get(i);
/* 358 */       if (line != null) {
/* 359 */         LineInfo info = new LineInfo();
/* 360 */         info.ip = line.getIp();
/* 361 */         info.name = line.getName();
/* 362 */         info.port = line.getPort();
/* 363 */         if (line.getStatus() != 0) {
/* 364 */           playerNum = countLineNum(line.getId());
/*     */ 
/* 366 */           if (playerNum < min) {
/* 367 */             min = playerNum;
/* 368 */             minLineInfo = info;
/*     */           }
/*     */ 
/* 371 */           if (playerNum < SystemConfig.good)
/* 372 */             info.status = 1;
/* 373 */           else if ((playerNum >= SystemConfig.good) && (playerNum < SystemConfig.bad))
/* 374 */             info.status = 2;
/* 375 */           else if (playerNum >= SystemConfig.bad)
/* 376 */             info.status = 3;
/*     */         }
/*     */         else {
/* 379 */           info.status = line.getStatus();
/*     */         }
/*     */ 
/* 382 */         si.l.add(info);
/*     */       }
/*     */ 
/* 385 */       if (minLineInfo != null) {
/* 386 */         boolean f = si.l.remove(minLineInfo);
/* 387 */         if (f) {
/* 388 */           si.l.add(0, minLineInfo);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 393 */     session.write(this.commandWrapper.gameLineInfo(si));
/*     */   }
/*     */ 
/*     */   private int countLineNum(int lineId)
/*     */   {
/* 402 */     int playerNum = 0;
/* 403 */     Iterator it = SystemStore.players.keySet().iterator();
/*     */ 
/* 406 */     while (it.hasNext()) {
/* 407 */       Integer id = (Integer)it.next();
/* 408 */       Player player = (Player)SystemStore.players.get(id);
/* 409 */       if ((player != null) && (lineId == player.getLine())) {
/* 410 */         playerNum++;
/*     */       }
/*     */     }
/* 413 */     return playerNum;
/*     */   }
/*     */ 
/*     */   private void otherLineAccountConnect(int account)
/*     */   {
/* 443 */     byte[] roleByte = this.commandWrapper.otherLineAccountConnect(account);
/* 444 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 445 */       Line line = (Line)SystemStore.lines.get(i);
/* 446 */       if (line.getSession() == null) continue;
/*     */       try {
/* 448 */         this.logger.info("通知线[" + line.getId() + "]玩家账号[" + account + "]上线");
/* 449 */         line.getSession().write(roleByte);
/*     */       } catch (Exception e) {
/* 451 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void warInfo(IoSession session, byte[] data)
/*     */   {
/* 465 */     byte[] cmdBytes = this.commandWrapper.warInfo(data);
/* 466 */     for (int i = 0; i < SystemStore.lines.size(); i++) {
/* 467 */       Line line = (Line)SystemStore.lines.get(i);
/* 468 */       if (line != null) {
/* 469 */         IoSession temp = line.getSession();
/* 470 */         if ((temp == null) || (temp == session))
/*     */           continue;
/* 472 */         temp.write(cmdBytes);
/* 473 */         this.logger.info("通知线[" + line.getId() + "]跨线改变战场信息");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void horn(IoSession session, byte[] data)
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.controller.LineController
 * JD-Core Version:    0.6.0
 */