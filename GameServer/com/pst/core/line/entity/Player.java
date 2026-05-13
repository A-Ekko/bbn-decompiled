/*     */ package com.pst.core.line.entity;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class Player
/*     */ {
/*     */   private int id;
/*     */   private String name;
/*     */   private int guild;
/*     */   private int camp;
/*     */   private int map;
/*     */   private int userId;
/*     */   private int line;
/*  71 */   private Map<Integer, Integer> bugles = new HashMap();
/*     */ 
/*  76 */   private List<Integer> friends = new ArrayList();
/*     */   private IoSession clientSession;
/*     */   private IoSession serviceSession;
/*     */ 
/*     */   public int getUserId()
/*     */   {
/*  43 */     return this.userId;
/*     */   }
/*     */ 
/*     */   public void setUserId(int userId) {
/*  47 */     this.userId = userId;
/*     */   }
/*     */ 
/*     */   public int getMap() {
/*  51 */     return this.map;
/*     */   }
/*     */ 
/*     */   public void setMap(int map) {
/*  55 */     this.map = map;
/*     */   }
/*     */ 
/*     */   public IoSession getServiceSession()
/*     */   {
/*  89 */     return this.serviceSession;
/*     */   }
/*     */ 
/*     */   public void setServiceSession(IoSession serviceSession) {
/*  93 */     this.serviceSession = serviceSession;
/*     */   }
/*     */ 
/*     */   public IoSession getClientSession() {
/*  97 */     return this.clientSession;
/*     */   }
/*     */ 
/*     */   public void setClientSession(IoSession session) {
/* 101 */     this.clientSession = session;
/*     */   }
/*     */ 
/*     */   public int getLine()
/*     */   {
/* 113 */     return this.line;
/*     */   }
/*     */ 
/*     */   public void setLine(int line) {
/* 117 */     this.line = line;
/*     */   }
/*     */ 
/*     */   public int getId() {
/* 121 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(int id) {
/* 125 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 129 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/* 133 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public int getGuild() {
/* 137 */     return this.guild;
/*     */   }
/*     */ 
/*     */   public void setGuild(int guild) {
/* 141 */     this.guild = guild;
/*     */   }
/*     */ 
/*     */   public int getCamp() {
/* 145 */     return this.camp;
/*     */   }
/*     */ 
/*     */   public void setCamp(int camp) {
/* 149 */     this.camp = camp;
/*     */   }
/*     */ 
/*     */   public Map<Integer, Integer> getBugles() {
/* 153 */     return this.bugles;
/*     */   }
/*     */ 
/*     */   public void setBugles(Map<Integer, Integer> bugles) {
/* 157 */     this.bugles = bugles;
/*     */   }
/*     */ 
/*     */   public List<Integer> getFriends() {
/* 161 */     return this.friends;
/*     */   }
/*     */ 
/*     */   public void setFriends(List<Integer> friends) {
/* 165 */     this.friends = friends;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.entity.Player
 * JD-Core Version:    0.6.0
 */