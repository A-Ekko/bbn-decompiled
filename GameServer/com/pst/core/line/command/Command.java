/*     */ package com.pst.core.line.command;
/*     */ 
/*     */ public class Command
/*     */ {
/*     */   public static final String lineOpen = "3001";
/*     */   public static final String online = "3002";
/*     */   public static final String downline = "3003";
/*     */   public static final String friendOnOrDownline = "3004";
/*     */   public static final String changeMap = "3005";
/*     */   public static final String createTeam = "3006";
/*     */   public static final String joinTeam = "3007";
/*     */   public static final String leaveTeam = "3008";
/*     */   public static final String changeBugle = "3009";
/*     */   public static final String connectGame = "1000";
/*     */   public static final String lineChat = "1004";
/*     */   public static final String lineInfo = "1185";
/*     */   public static final String client_sysMessage = "1181";
/*  74 */   public static final byte[] decreaseBugle = { 48, 16 };
/*     */ 
/*  79 */   public static final byte[] informRole = { 35, 1 };
/*     */ 
/*  84 */   public static final byte[] chat = { 32, 4 };
/*     */ 
/*  89 */   public static final byte[] chatError = { 33, 120 };
/*     */ 
/*  94 */   public static final byte[] gameLineInfo = { 37, 4 };
/*     */ 
/*  99 */   public static final byte[] roleLineConnet = { 48, 17 };
/*     */ 
/* 104 */   public static final byte[] otherLineAccountConnect = { 48, 18 };
/*     */ 
/* 109 */   public static final byte[] heartBeat = { 37, 2 };
/*     */ 
/* 114 */   public static final byte[] systemResult = { 32, 7 };
/*     */ 
/* 116 */   public static final byte[] sysMessage = { 35, 17 };
/*     */ 
/* 126 */   public static final byte[] toGserver_createGuild = { 64 };
/*     */ 
/* 131 */   public static final byte[] toGserver_addApplyJionGuild = { 64, 1 };
/*     */ 
/* 136 */   public static final byte[] toGserver_addGuildRole = { 64, 2 };
/*     */ 
/* 141 */   public static final byte[] toGserver_addRoleGuild = { 64, 3 };
/*     */ 
/* 146 */   public static final byte[] toGserver_rejectApplyRole = { 64, 4 };
/*     */ 
/* 151 */   public static final byte[] toGserver_rejectRole = { 64, 5 };
/*     */ 
/* 156 */   public static final byte[] toGserver_dismissGuild = { 64, 6 };
/*     */ 
/* 161 */   public static final byte[] toGserver_changeGuildInfo = { 64, 7 };
/*     */ 
/* 167 */   public static final byte[] toGserver_careteTitle = { 64, 8 };
/*     */ 
/* 173 */   public static final byte[] toGserver_alertTitle = { 64, 9 };
/*     */ 
/* 178 */   public static final byte[] toGserver_delTitle = { 64, 16 };
/*     */ 
/* 183 */   public static final byte[] toGserver_checkRoleInfo = { 64, 17 };
/*     */ 
/* 188 */   public static final byte[] toGserver_radioKillMonster = { 64, 18 };
/*     */ 
/* 193 */   public static final byte[] toGserver_findRoleInfo = { 64, 19 };
/*     */ 
/* 198 */   public static final byte[] toGserver_warInfo = { 64, 20 };
/*     */   public static final String fromGserver_createGuild = "5000";
/*     */   public static final String fromGserver_addApplyJionGuild = "5001";
/*     */   public static final String fromGserver_addGuildRole = "5002";
/*     */   public static final String fromGserver_addRoleGuild = "5003";
/*     */   public static final String fromGserver_rejectApplyRole = "5004";
/*     */   public static final String fromGserver_rejectRole = "5005";
/*     */   public static final String fromGserver_dismissGuild = "5006";
/*     */   public static final String fromGserver_changeGuildInfo = "5007";
/*     */   public static final String fromGserver_careteTitle = "5008";
/*     */   public static final String fromGserver_alertTitle = "5009";
/*     */   public static final String fromGserver_delTitle = "5010";
/*     */   public static final String fromGserver_checkRoleInfo = "5011";
/*     */   public static final String fromGserver_radioKillMonster = "5012";
/*     */   public static final String fromGserver_findRoleInfo = "5013";
/*     */   public static final String fromGserver_warInfo = "5014";
/*     */   public static final String fromGserver_horn = "5016";
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.command.Command
 * JD-Core Version:    0.6.0
 */