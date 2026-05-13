/*     */ package org.apache.mina.core.session;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.mina.util.ExpirationListener;
/*     */ import org.apache.mina.util.ExpiringMap;
/*     */ import org.apache.mina.util.ExpiringMap.Expirer;
/*     */ 
/*     */ public class ExpiringSessionRecycler
/*     */   implements IoSessionRecycler
/*     */ {
/*     */   private ExpiringMap<Object, IoSession> sessionMap;
/*     */   private ExpiringMap<Object, IoSession>.Expirer mapExpirer;
/*     */ 
/*     */   public ExpiringSessionRecycler()
/*     */   {
/*  44 */     this(60);
/*     */   }
/*     */ 
/*     */   public ExpiringSessionRecycler(int timeToLive) {
/*  48 */     this(timeToLive, 1);
/*     */   }
/*     */ 
/*     */   public ExpiringSessionRecycler(int timeToLive, int expirationInterval) {
/*  52 */     this.sessionMap = new ExpiringMap(timeToLive, expirationInterval);
/*     */ 
/*  54 */     this.mapExpirer = this.sessionMap.getExpirer();
/*  55 */     this.sessionMap.addExpirationListener(new DefaultExpirationListener(null));
/*     */   }
/*     */ 
/*     */   public void put(IoSession session) {
/*  59 */     this.mapExpirer.startExpiringIfNotStarted();
/*     */ 
/*  61 */     Object key = generateKey(session);
/*     */ 
/*  63 */     if (!this.sessionMap.containsKey(key))
/*  64 */       this.sessionMap.put(key, session);
/*     */   }
/*     */ 
/*     */   public IoSession recycle(SocketAddress localAddress, SocketAddress remoteAddress)
/*     */   {
/*  70 */     return (IoSession)this.sessionMap.get(generateKey(localAddress, remoteAddress));
/*     */   }
/*     */ 
/*     */   public void remove(IoSession session) {
/*  74 */     this.sessionMap.remove(generateKey(session));
/*     */   }
/*     */ 
/*     */   public void stopExpiring() {
/*  78 */     this.mapExpirer.stopExpiring();
/*     */   }
/*     */ 
/*     */   public int getExpirationInterval() {
/*  82 */     return this.sessionMap.getExpirationInterval();
/*     */   }
/*     */ 
/*     */   public int getTimeToLive() {
/*  86 */     return this.sessionMap.getTimeToLive();
/*     */   }
/*     */ 
/*     */   public void setExpirationInterval(int expirationInterval) {
/*  90 */     this.sessionMap.setExpirationInterval(expirationInterval);
/*     */   }
/*     */ 
/*     */   public void setTimeToLive(int timeToLive) {
/*  94 */     this.sessionMap.setTimeToLive(timeToLive);
/*     */   }
/*     */ 
/*     */   private Object generateKey(IoSession session) {
/*  98 */     return generateKey(session.getLocalAddress(), session.getRemoteAddress());
/*     */   }
/*     */ 
/*     */   private Object generateKey(SocketAddress localAddress, SocketAddress remoteAddress)
/*     */   {
/* 104 */     List key = new ArrayList(2);
/* 105 */     key.add(remoteAddress);
/* 106 */     key.add(localAddress);
/* 107 */     return key;
/*     */   }
/*     */   private class DefaultExpirationListener implements ExpirationListener<IoSession> {
/*     */     private DefaultExpirationListener() {
/*     */     }
/*     */     public void expired(IoSession expiredSession) {
/* 113 */       expiredSession.close(true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.ExpiringSessionRecycler
 * JD-Core Version:    0.6.0
 */