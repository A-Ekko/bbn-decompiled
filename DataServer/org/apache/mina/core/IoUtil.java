/*     */ package org.apache.mina.core;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class IoUtil
/*     */ {
/*  42 */   private static final IoSession[] EMPTY_SESSIONS = new IoSession[0];
/*     */ 
/*     */   public static List<WriteFuture> broadcast(Object message, Collection<IoSession> sessions)
/*     */   {
/*  50 */     List answer = new ArrayList(sessions.size());
/*  51 */     broadcast(message, sessions.iterator(), answer);
/*  52 */     return answer;
/*     */   }
/*     */ 
/*     */   public static List<WriteFuture> broadcast(Object message, Iterable<IoSession> sessions)
/*     */   {
/*  61 */     List answer = new ArrayList();
/*  62 */     broadcast(message, sessions.iterator(), answer);
/*  63 */     return answer;
/*     */   }
/*     */ 
/*     */   public static List<WriteFuture> broadcast(Object message, Iterator<IoSession> sessions)
/*     */   {
/*  72 */     List answer = new ArrayList();
/*  73 */     broadcast(message, sessions, answer);
/*  74 */     return answer;
/*     */   }
/*     */ 
/*     */   public static List<WriteFuture> broadcast(Object message, IoSession[] sessions)
/*     */   {
/*  83 */     if (sessions == null) {
/*  84 */       sessions = EMPTY_SESSIONS;
/*     */     }
/*     */ 
/*  87 */     List answer = new ArrayList(sessions.length);
/*  88 */     if ((message instanceof IoBuffer)) {
/*  89 */       for (IoSession s : sessions)
/*  90 */         answer.add(s.write(((IoBuffer)message).duplicate()));
/*     */     }
/*     */     else {
/*  93 */       for (IoSession s : sessions) {
/*  94 */         answer.add(s.write(message));
/*     */       }
/*     */     }
/*  97 */     return answer;
/*     */   }
/*     */ 
/*     */   private static void broadcast(Object message, Iterator<IoSession> sessions, Collection<WriteFuture> answer) {
/* 101 */     if ((message instanceof IoBuffer)) {
/* 102 */       while (sessions.hasNext()) {
/* 103 */         IoSession s = (IoSession)sessions.next();
/* 104 */         answer.add(s.write(((IoBuffer)message).duplicate()));
/*     */       }
/*     */     }
/* 107 */     while (sessions.hasNext()) {
/* 108 */       IoSession s = (IoSession)sessions.next();
/* 109 */       answer.add(s.write(message));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void await(Iterable<? extends IoFuture> futures) throws InterruptedException
/*     */   {
/* 115 */     for (IoFuture f : futures)
/* 116 */       f.await();
/*     */   }
/*     */ 
/*     */   public static void awaitUninterruptably(Iterable<? extends IoFuture> futures)
/*     */   {
/* 121 */     for (IoFuture f : futures)
/* 122 */       f.awaitUninterruptibly();
/*     */   }
/*     */ 
/*     */   public static boolean await(Iterable<? extends IoFuture> futures, long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/* 127 */     return await(futures, unit.toMillis(timeout));
/*     */   }
/*     */ 
/*     */   public static boolean await(Iterable<? extends IoFuture> futures, long timeoutMillis) throws InterruptedException {
/* 131 */     return await0(futures, timeoutMillis, true);
/*     */   }
/*     */ 
/*     */   public static boolean awaitUninterruptibly(Iterable<? extends IoFuture> futures, long timeout, TimeUnit unit) {
/* 135 */     return awaitUninterruptibly(futures, unit.toMillis(timeout));
/*     */   }
/*     */ 
/*     */   public static boolean awaitUninterruptibly(Iterable<? extends IoFuture> futures, long timeoutMillis) {
/*     */     try {
/* 140 */       return await0(futures, timeoutMillis, false); } catch (InterruptedException e) {
/*     */     }
/* 142 */     throw new InternalError();
/*     */   }
/*     */ 
/*     */   private static boolean await0(Iterable<? extends IoFuture> futures, long timeoutMillis, boolean interruptable) throws InterruptedException
/*     */   {
/* 147 */     long startTime = timeoutMillis <= 0L ? 0L : System.currentTimeMillis();
/* 148 */     long waitTime = timeoutMillis;
/*     */ 
/* 150 */     boolean lastComplete = true;
/* 151 */     Iterator i = futures.iterator();
/* 152 */     while (i.hasNext()) {
/* 153 */       IoFuture f = (IoFuture)i.next();
/*     */       do {
/* 155 */         if (interruptable)
/* 156 */           lastComplete = f.await(waitTime);
/*     */         else {
/* 158 */           lastComplete = f.awaitUninterruptibly(waitTime);
/*     */         }
/*     */ 
/* 161 */         waitTime = timeoutMillis - (System.currentTimeMillis() - startTime);
/*     */       }
/* 163 */       while ((!lastComplete) && (waitTime > 0L) && 
/* 166 */         (!lastComplete));
/*     */ 
/* 168 */       if (waitTime <= 0L)
/*     */       {
/*     */         break;
/*     */       }
/*     */     }
/* 173 */     return (lastComplete) && (!i.hasNext());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.IoUtil
 * JD-Core Version:    0.6.0
 */