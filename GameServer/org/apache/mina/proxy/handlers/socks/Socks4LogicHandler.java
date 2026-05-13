/*     */ package org.apache.mina.proxy.handlers.socks;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.apache.mina.proxy.utils.ByteUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Socks4LogicHandler extends AbstractSocksLogicHandler
/*     */ {
/*  38 */   private static final Logger logger = LoggerFactory.getLogger(Socks4LogicHandler.class);
/*     */ 
/*     */   public Socks4LogicHandler(ProxyIoSession proxyIoSession)
/*     */   {
/*  45 */     super(proxyIoSession);
/*     */   }
/*     */ 
/*     */   public void doHandshake(IoFilter.NextFilter nextFilter)
/*     */   {
/*  54 */     logger.debug(" doHandshake()");
/*     */ 
/*  57 */     writeRequest(nextFilter, this.request);
/*     */   }
/*     */ 
/*     */   protected void writeRequest(IoFilter.NextFilter nextFilter, SocksProxyRequest request)
/*     */   {
/*     */     try
/*     */     {
/*  70 */       boolean isV4ARequest = request.getHost() != null;
/*  71 */       byte[] userID = request.getUserName().getBytes("ASCII");
/*  72 */       byte[] host = isV4ARequest ? request.getHost().getBytes("ASCII") : null;
/*     */ 
/*  75 */       int len = 9 + userID.length;
/*     */ 
/*  77 */       if (isV4ARequest) {
/*  78 */         len += host.length + 1;
/*     */       }
/*     */ 
/*  81 */       IoBuffer buf = IoBuffer.allocate(len);
/*     */ 
/*  83 */       buf.put(request.getProtocolVersion());
/*  84 */       buf.put(request.getCommandCode());
/*  85 */       buf.put(request.getPort());
/*  86 */       buf.put(request.getIpAddress());
/*  87 */       buf.put(userID);
/*  88 */       buf.put(0);
/*     */ 
/*  90 */       if (isV4ARequest) {
/*  91 */         buf.put(host);
/*  92 */         buf.put(0);
/*     */       }
/*     */ 
/*  95 */       if (isV4ARequest)
/*  96 */         logger.debug("  sending SOCKS4a request");
/*     */       else {
/*  98 */         logger.debug("  sending SOCKS4 request");
/*     */       }
/*     */ 
/* 101 */       buf.flip();
/* 102 */       writeData(nextFilter, buf);
/*     */     } catch (Exception ex) {
/* 104 */       closeSession("Unable to send Socks request: ", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoBuffer buf)
/*     */   {
/*     */     try
/*     */     {
/* 118 */       if (buf.remaining() >= 8)
/* 119 */         handleResponse(buf);
/*     */     }
/*     */     catch (Exception ex) {
/* 122 */       closeSession("Proxy handshake failed: ", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void handleResponse(IoBuffer buf)
/*     */     throws Exception
/*     */   {
/* 136 */     byte first = buf.get(0);
/*     */ 
/* 138 */     if (first != 0) {
/* 139 */       throw new Exception("Socks response seems to be malformed");
/*     */     }
/*     */ 
/* 142 */     byte status = buf.get(1);
/*     */ 
/* 145 */     buf.position(buf.position() + 8);
/*     */ 
/* 147 */     if (status == 90)
/* 148 */       setHandshakeComplete();
/*     */     else
/* 150 */       throw new Exception("Proxy handshake failed - Code: 0x" + ByteUtilities.asHex(new byte[] { status }) + " (" + SocksProxyConstants.getReplyCodeAsString(status) + ")");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.socks.Socks4LogicHandler
 * JD-Core Version:    0.6.0
 */