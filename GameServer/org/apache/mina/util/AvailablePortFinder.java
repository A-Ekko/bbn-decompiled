/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.ServerSocket;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class AvailablePortFinder
/*     */ {
/*     */   public static final int MIN_PORT_NUMBER = 1;
/*     */   public static final int MAX_PORT_NUMBER = 49151;
/*     */ 
/*     */   public static Set<Integer> getAvailablePorts()
/*     */   {
/*  61 */     return getAvailablePorts(1, 49151);
/*     */   }
/*     */ 
/*     */   public static int getNextAvailable()
/*     */   {
/*  70 */     return getNextAvailable(1);
/*     */   }
/*     */ 
/*     */   public static int getNextAvailable(int fromPort)
/*     */   {
/*  80 */     if ((fromPort < 1) || (fromPort > 49151)) {
/*  81 */       throw new IllegalArgumentException("Invalid start port: " + fromPort);
/*     */     }
/*     */ 
/*  85 */     for (int i = fromPort; i <= 49151; i++) {
/*  86 */       if (available(i)) {
/*  87 */         return i;
/*     */       }
/*     */     }
/*     */ 
/*  91 */     throw new NoSuchElementException("Could not find an available port above " + fromPort);
/*     */   }
/*     */ 
/*     */   public static boolean available(int port)
/*     */   {
/* 101 */     if ((port < 1) || (port > 49151)) {
/* 102 */       throw new IllegalArgumentException("Invalid start port: " + port);
/*     */     }
/*     */ 
/* 105 */     ServerSocket ss = null;
/* 106 */     DatagramSocket ds = null;
/*     */     try {
/* 108 */       ss = new ServerSocket(port);
/* 109 */       ss.setReuseAddress(true);
/* 110 */       ds = new DatagramSocket(port);
/* 111 */       ds.setReuseAddress(true);
/* 112 */       int i = 1;
/*     */       return i;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */     finally
/*     */     {
/* 115 */       if (ds != null) {
/* 116 */         ds.close();
/*     */       }
/*     */ 
/* 119 */       if (ss != null) {
/*     */         try {
/* 121 */           ss.close();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 128 */     return false;
/*     */   }
/*     */ 
/*     */   public static Set<Integer> getAvailablePorts(int fromPort, int toPort)
/*     */   {
/* 140 */     if ((fromPort < 1) || (toPort > 49151) || (fromPort > toPort))
/*     */     {
/* 142 */       throw new IllegalArgumentException("Invalid port range: " + fromPort + " ~ " + toPort);
/*     */     }
/*     */ 
/* 146 */     Set result = new TreeSet();
/*     */ 
/* 148 */     for (int i = fromPort; i <= toPort; i++) {
/* 149 */       ServerSocket s = null;
/*     */       try
/*     */       {
/* 152 */         s = new ServerSocket(i);
/* 153 */         result.add(new Integer(i));
/*     */       } catch (IOException e) {
/*     */       } finally {
/* 156 */         if (s != null) {
/*     */           try {
/* 158 */             s.close();
/*     */           }
/*     */           catch (IOException e)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 166 */     return result;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.AvailablePortFinder
 * JD-Core Version:    0.6.0
 */