/*     */ package org.apache.mina.proxy.handlers.socks;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.apache.mina.proxy.handlers.ProxyRequest;
/*     */ 
/*     */ public class SocksProxyRequest extends ProxyRequest
/*     */ {
/*     */   private byte protocolVersion;
/*     */   private byte commandCode;
/*     */   private String userName;
/*     */   private String password;
/*     */   private String host;
/*     */   private int port;
/*     */   private String serviceKerberosName;
/*     */ 
/*     */   public SocksProxyRequest(byte protocolVersion, byte commandCode, InetSocketAddress endpointAddress, String userName)
/*     */   {
/*  80 */     super(endpointAddress);
/*  81 */     this.protocolVersion = protocolVersion;
/*  82 */     this.commandCode = commandCode;
/*  83 */     this.userName = userName;
/*     */   }
/*     */ 
/*     */   public SocksProxyRequest(byte commandCode, String host, int port, String userName)
/*     */   {
/*  96 */     this.protocolVersion = 4;
/*  97 */     this.commandCode = commandCode;
/*  98 */     this.userName = userName;
/*  99 */     this.host = host;
/* 100 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public byte[] getIpAddress()
/*     */   {
/* 111 */     if (getEndpointAddress() == null) {
/* 112 */       return SocksProxyConstants.FAKE_IP;
/*     */     }
/* 114 */     return getEndpointAddress().getAddress().getAddress();
/*     */   }
/*     */ 
/*     */   public byte[] getPort()
/*     */   {
/* 123 */     byte[] port = new byte[2];
/* 124 */     int p = getEndpointAddress() == null ? this.port : getEndpointAddress().getPort();
/*     */ 
/* 126 */     port[1] = (byte)p;
/* 127 */     port[0] = (byte)(p >> 8);
/* 128 */     return port;
/*     */   }
/*     */ 
/*     */   public byte getCommandCode()
/*     */   {
/* 137 */     return this.commandCode;
/*     */   }
/*     */ 
/*     */   public byte getProtocolVersion()
/*     */   {
/* 146 */     return this.protocolVersion;
/*     */   }
/*     */ 
/*     */   public String getUserName()
/*     */   {
/* 155 */     return this.userName;
/*     */   }
/*     */ 
/*     */   public final synchronized String getHost()
/*     */   {
/* 164 */     if ((this.host == null) && 
/* 165 */       (getEndpointAddress() != null)) {
/* 166 */       this.host = getEndpointAddress().getHostName();
/*     */     }
/*     */ 
/* 170 */     return this.host;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 179 */     return this.password;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 188 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public String getServiceKerberosName()
/*     */   {
/* 197 */     return this.serviceKerberosName;
/*     */   }
/*     */ 
/*     */   public void setServiceKerberosName(String serviceKerberosName)
/*     */   {
/* 206 */     this.serviceKerberosName = serviceKerberosName;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.socks.SocksProxyRequest
 * JD-Core Version:    0.6.0
 */