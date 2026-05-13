/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class StandardSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*  49 */   protected String host = null;
/*     */ 
/*  52 */   protected int port = 3306;
/*     */ 
/*  55 */   protected Socket rawSocket = null;
/*     */ 
/*     */   public Socket afterHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/*  72 */     return this.rawSocket;
/*     */   }
/*     */ 
/*     */   public Socket beforeHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/*  87 */     return this.rawSocket;
/*     */   }
/*     */ 
/*     */   public Socket connect(String hostname, int portNumber, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/*  96 */     if (props != null) {
/*  97 */       this.host = hostname;
/*     */ 
/*  99 */       this.port = portNumber;
/*     */ 
/* 101 */       boolean hasConnectTimeoutMethod = false;
/*     */ 
/* 103 */       Method connectWithTimeoutMethod = null;
/*     */       try
/*     */       {
/* 107 */         Class socketAddressClass = Class.forName("java.net.SocketAddress");
/*     */ 
/* 110 */         connectWithTimeoutMethod = class$java$net$Socket.getMethod("connect", new Class[] { socketAddressClass, Integer.TYPE });
/*     */ 
/* 113 */         hasConnectTimeoutMethod = true;
/*     */       } catch (NoClassDefFoundError noClassDefFound) {
/* 115 */         hasConnectTimeoutMethod = false;
/*     */       } catch (NoSuchMethodException noSuchMethodEx) {
/* 117 */         hasConnectTimeoutMethod = false;
/*     */       } catch (Throwable catchAll) {
/* 119 */         hasConnectTimeoutMethod = false;
/*     */       }
/*     */ 
/* 122 */       int connectTimeout = 0;
/*     */ 
/* 124 */       String connectTimeoutStr = props.getProperty("connectTimeout");
/*     */ 
/* 126 */       if (connectTimeoutStr != null) {
/*     */         try {
/* 128 */           connectTimeout = Integer.parseInt(connectTimeoutStr);
/*     */         } catch (NumberFormatException nfe) {
/* 130 */           throw new SocketException("Illegal value '" + connectTimeoutStr + "' for connectTimeout");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 135 */       if (this.host != null) {
/* 136 */         if ((!hasConnectTimeoutMethod) || (connectTimeout == 0)) {
/* 137 */           InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/* 140 */           Exception caughtWhileConnecting = null;
/*     */ 
/* 145 */           for (int i = 0; i < possibleAddresses.length; i++) {
/*     */             try {
/* 147 */               this.rawSocket = new Socket(possibleAddresses[i], this.port);
/*     */             }
/*     */             catch (Exception ex)
/*     */             {
/* 151 */               caughtWhileConnecting = ex;
/*     */             }
/*     */           }
/*     */ 
/* 155 */           if (this.rawSocket == null) {
/* 156 */             throw new SocketException(caughtWhileConnecting.toString());
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           try
/*     */           {
/* 163 */             Class inetSocketAddressClass = Class.forName("java.net.InetSocketAddress");
/*     */ 
/* 165 */             Constructor addrConstructor = inetSocketAddressClass.getConstructor(new Class[] { String.class, Integer.TYPE });
/*     */ 
/* 169 */             InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */ 
/* 172 */             Exception caughtWhileConnecting = null;
/*     */ 
/* 177 */             for (int i = 0; i < possibleAddresses.length; i++) {
/*     */               try
/*     */               {
/* 180 */                 Object sockAddr = addrConstructor.newInstance(new Object[] { this.host, new Integer(this.port) });
/*     */ 
/* 184 */                 this.rawSocket = new Socket();
/* 185 */                 connectWithTimeoutMethod.invoke(this.rawSocket, new Object[] { sockAddr, new Integer(connectTimeout) });
/*     */               }
/*     */               catch (Exception ex)
/*     */               {
/* 191 */                 this.rawSocket = null;
/*     */ 
/* 193 */                 caughtWhileConnecting = ex;
/*     */               }
/*     */             }
/*     */ 
/* 197 */             if (this.rawSocket == null) {
/* 198 */               throw new SocketException(caughtWhileConnecting.toString());
/*     */             }
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 203 */             if (!(t instanceof SocketException)) {
/* 204 */               throw new SocketException(t.toString());
/*     */             }
/*     */ 
/* 207 */             throw ((SocketException)t);
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 212 */           this.rawSocket.setTcpNoDelay(true);
/*     */         }
/*     */         catch (Exception ex)
/*     */         {
/*     */         }
/*     */ 
/* 218 */         return this.rawSocket;
/*     */       }
/*     */     }
/*     */ 
/* 222 */     throw new SocketException("Unable to create socket");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.StandardSocketFactory
 * JD-Core Version:    0.6.0
 */