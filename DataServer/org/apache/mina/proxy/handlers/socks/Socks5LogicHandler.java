/*     */ package org.apache.mina.proxy.handlers.socks;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.apache.mina.proxy.utils.ByteUtilities;
/*     */ import org.ietf.jgss.GSSContext;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSManager;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Socks5LogicHandler extends AbstractSocksLogicHandler
/*     */ {
/*  48 */   private static final Logger logger = LoggerFactory.getLogger(Socks5LogicHandler.class);
/*     */ 
/*  54 */   private static final String SELECTED_AUTH_METHOD = Socks5LogicHandler.class.getName() + ".SelectedAuthMethod";
/*     */ 
/*  61 */   private static final String HANDSHAKE_STEP = Socks5LogicHandler.class.getName() + ".HandshakeStep";
/*     */ 
/*  68 */   private static final String GSS_CONTEXT = Socks5LogicHandler.class.getName() + ".GSSContext";
/*     */ 
/*  75 */   private static final String GSS_TOKEN = Socks5LogicHandler.class.getName() + ".GSSToken";
/*     */ 
/*     */   public Socks5LogicHandler(ProxyIoSession proxyIoSession)
/*     */   {
/*  82 */     super(proxyIoSession);
/*  83 */     getSession().setAttribute(HANDSHAKE_STEP, Integer.valueOf(0));
/*     */   }
/*     */ 
/*     */   public synchronized void doHandshake(IoFilter.NextFilter nextFilter)
/*     */   {
/*  91 */     logger.debug(" doHandshake()");
/*     */ 
/*  94 */     writeRequest(nextFilter, this.request, ((Integer)getSession().getAttribute(HANDSHAKE_STEP)).intValue());
/*     */   }
/*     */ 
/*     */   private IoBuffer encodeInitialGreetingPacket(SocksProxyRequest request)
/*     */   {
/* 105 */     byte nbMethods = (byte)SocksProxyConstants.SUPPORTED_AUTH_METHODS.length;
/* 106 */     IoBuffer buf = IoBuffer.allocate(2 + nbMethods);
/*     */ 
/* 108 */     buf.put(request.getProtocolVersion());
/* 109 */     buf.put(nbMethods);
/* 110 */     buf.put(SocksProxyConstants.SUPPORTED_AUTH_METHODS);
/*     */ 
/* 112 */     return buf;
/*     */   }
/*     */ 
/*     */   private IoBuffer encodeProxyRequestPacket(SocksProxyRequest request)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 125 */     int len = 6;
/* 126 */     byte[] host = request.getHost() != null ? request.getHost().getBytes("ASCII") : null;
/*     */ 
/* 129 */     InetSocketAddress adr = request.getEndpointAddress();
/* 130 */     byte addressType = 0;
/*     */ 
/* 132 */     if ((adr != null) && (!adr.isUnresolved())) {
/* 133 */       if ((adr.getAddress() instanceof Inet6Address)) {
/* 134 */         len += 16;
/* 135 */         addressType = 4;
/* 136 */       } else if ((adr.getAddress() instanceof Inet4Address)) {
/* 137 */         len += 4;
/* 138 */         addressType = 1;
/*     */       }
/*     */     } else {
/* 141 */       len += 1 + host.length;
/* 142 */       addressType = 3;
/*     */     }
/*     */ 
/* 145 */     IoBuffer buf = IoBuffer.allocate(len);
/*     */ 
/* 147 */     buf.put(request.getProtocolVersion());
/* 148 */     buf.put(request.getCommandCode());
/* 149 */     buf.put(0);
/* 150 */     buf.put(addressType);
/*     */ 
/* 152 */     if (addressType == 3) {
/* 153 */       buf.put((byte)host.length);
/* 154 */       buf.put(host);
/*     */     } else {
/* 156 */       buf.put(request.getIpAddress());
/*     */     }
/*     */ 
/* 159 */     buf.put(request.getPort());
/*     */ 
/* 161 */     return buf;
/*     */   }
/*     */ 
/*     */   private IoBuffer encodeAuthenticationPacket(SocksProxyRequest request)
/*     */     throws UnsupportedEncodingException, GSSException
/*     */   {
/* 176 */     byte method = ((Byte)getSession().getAttribute(SELECTED_AUTH_METHOD)).byteValue();
/*     */ 
/* 179 */     switch (method)
/*     */     {
/*     */     case 0:
/* 183 */       getSession().setAttribute(HANDSHAKE_STEP, Integer.valueOf(2));
/*     */ 
/* 185 */       break;
/*     */     case 1:
/* 188 */       return encodeGSSAPIAuthenticationPacket(request);
/*     */     case 2:
/* 192 */       byte[] user = request.getUserName().getBytes("ASCII");
/* 193 */       byte[] pwd = request.getPassword().getBytes("ASCII");
/* 194 */       IoBuffer buf = IoBuffer.allocate(3 + user.length + pwd.length);
/*     */ 
/* 196 */       buf.put(1);
/* 197 */       buf.put((byte)user.length);
/* 198 */       buf.put(user);
/* 199 */       buf.put((byte)pwd.length);
/* 200 */       buf.put(pwd);
/*     */ 
/* 202 */       return buf;
/*     */     }
/*     */ 
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */   private IoBuffer encodeGSSAPIAuthenticationPacket(SocksProxyRequest request)
/*     */     throws GSSException
/*     */   {
/* 217 */     GSSContext ctx = (GSSContext)getSession().getAttribute(GSS_CONTEXT);
/* 218 */     if (ctx == null) {
/* 219 */       GSSManager manager = GSSManager.getInstance();
/* 220 */       GSSName serverName = manager.createName(request.getServiceKerberosName(), null);
/*     */ 
/* 222 */       Oid krb5OID = new Oid("1.2.840.113554.1.2.2");
/*     */ 
/* 224 */       if (logger.isDebugEnabled()) {
/* 225 */         logger.debug("Available mechs:");
/* 226 */         for (Oid o : manager.getMechs()) {
/* 227 */           if (o.equals(krb5OID)) {
/* 228 */             logger.debug("Found Kerberos V OID available");
/*     */           }
/* 230 */           logger.debug("{} with oid = {}", manager.getNamesForMech(o), o);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 235 */       ctx = manager.createContext(serverName, krb5OID, null, 0);
/*     */ 
/* 238 */       ctx.requestMutualAuth(true);
/* 239 */       ctx.requestConf(false);
/* 240 */       ctx.requestInteg(false);
/*     */ 
/* 242 */       getSession().setAttribute(GSS_CONTEXT, ctx);
/*     */     }
/*     */ 
/* 245 */     byte[] token = (byte[])(byte[])getSession().getAttribute(GSS_TOKEN);
/* 246 */     if (token != null) {
/* 247 */       logger.debug("  Received Token[{}] = {}", Integer.valueOf(token.length), ByteUtilities.asHex(token));
/*     */     }
/*     */ 
/* 250 */     IoBuffer buf = null;
/*     */ 
/* 252 */     if (!ctx.isEstablished())
/*     */     {
/* 254 */       if (token == null) {
/* 255 */         token = new byte[32];
/*     */       }
/*     */ 
/* 258 */       token = ctx.initSecContext(token, 0, token.length);
/*     */ 
/* 262 */       if (token != null) {
/* 263 */         logger.debug("  Sending Token[{}] = {}", Integer.valueOf(token.length), ByteUtilities.asHex(token));
/*     */ 
/* 266 */         getSession().setAttribute(GSS_TOKEN, token);
/* 267 */         buf = IoBuffer.allocate(4 + token.length);
/* 268 */         buf.put(new byte[] { 1, 1 });
/*     */ 
/* 272 */         buf.put(ByteUtilities.intToNetworkByteOrder(token.length, new byte[2], 0, 2));
/*     */ 
/* 274 */         buf.put(token);
/*     */       }
/*     */     }
/*     */ 
/* 278 */     return buf;
/*     */   }
/*     */ 
/*     */   private void writeRequest(IoFilter.NextFilter nextFilter, SocksProxyRequest request, int step)
/*     */   {
/*     */     try
/*     */     {
/* 292 */       IoBuffer buf = null;
/*     */ 
/* 294 */       if (step == 0) {
/* 295 */         buf = encodeInitialGreetingPacket(request);
/* 296 */       } else if (step == 1)
/*     */       {
/* 298 */         buf = encodeAuthenticationPacket(request);
/*     */ 
/* 300 */         if (buf == null) {
/* 301 */           step = 2;
/*     */         }
/*     */       }
/*     */ 
/* 305 */       if (step == 2) {
/* 306 */         buf = encodeProxyRequestPacket(request);
/*     */       }
/*     */ 
/* 309 */       buf.flip();
/* 310 */       writeData(nextFilter, buf);
/*     */     }
/*     */     catch (Exception ex) {
/* 313 */       closeSession("Unable to send Socks request: ", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void messageReceived(IoFilter.NextFilter nextFilter, IoBuffer buf)
/*     */   {
/*     */     try
/*     */     {
/* 324 */       int step = ((Integer)getSession().getAttribute(HANDSHAKE_STEP)).intValue();
/*     */ 
/* 327 */       if ((step == 0) && (buf.get(0) != 5))
/*     */       {
/* 329 */         throw new IllegalStateException("Wrong socks version running on server");
/*     */       }
/*     */ 
/* 333 */       if (((step == 0) || (step == 1)) && (buf.remaining() >= 2))
/*     */       {
/* 335 */         handleResponse(nextFilter, buf, step);
/* 336 */       } else if ((step == 2) && (buf.remaining() >= 5))
/*     */       {
/* 338 */         handleResponse(nextFilter, buf, step);
/*     */       }
/*     */     } catch (Exception ex) {
/* 341 */       closeSession("Proxy handshake failed: ", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void handleResponse(IoFilter.NextFilter nextFilter, IoBuffer buf, int step)
/*     */     throws Exception
/*     */   {
/* 350 */     int len = 2;
/* 351 */     if (step == 0)
/*     */     {
/* 353 */       byte method = buf.get(1);
/*     */ 
/* 355 */       if (method == -1) {
/* 356 */         throw new IllegalStateException("No acceptable authentication method to use the socks proxy server");
/*     */       }
/*     */ 
/* 360 */       getSession().setAttribute(SELECTED_AUTH_METHOD, new Byte(method));
/*     */     }
/* 362 */     else if (step == 1)
/*     */     {
/* 364 */       byte method = ((Byte)getSession().getAttribute(SELECTED_AUTH_METHOD)).byteValue();
/*     */ 
/* 367 */       if (method == 1) {
/* 368 */         int oldPos = buf.position();
/*     */ 
/* 370 */         if (buf.get(0) != 1) {
/* 371 */           throw new IllegalStateException("Authentication failed");
/*     */         }
/* 373 */         if (buf.get(1) == 255) {
/* 374 */           throw new IllegalStateException("Authentication failed: GSS API Security Context Failure");
/*     */         }
/*     */ 
/* 378 */         if (buf.remaining() >= 2) {
/* 379 */           byte[] size = new byte[2];
/* 380 */           buf.get(size);
/* 381 */           int s = ByteUtilities.makeIntFromByte2(size);
/* 382 */           if (buf.remaining() >= s) {
/* 383 */             byte[] token = new byte[s];
/* 384 */             buf.get(token);
/* 385 */             getSession().setAttribute(GSS_TOKEN, token);
/* 386 */             len = 0;
/*     */           }
/*     */           else {
/* 389 */             return;
/*     */           }
/*     */         } else {
/* 392 */           buf.position(oldPos);
/* 393 */           return;
/*     */         }
/* 395 */       } else if (buf.get(1) != 0) {
/* 396 */         throw new IllegalStateException("Authentication failed");
/*     */       }
/*     */     }
/* 399 */     else if (step == 2)
/*     */     {
/* 401 */       byte addressType = buf.get(3);
/* 402 */       len = 6;
/* 403 */       if (addressType == 4)
/* 404 */         len += 16;
/* 405 */       else if (addressType == 1)
/* 406 */         len += 4;
/* 407 */       else if (addressType == 3)
/* 408 */         len += 1 + (short)buf.get(4);
/*     */       else {
/* 410 */         throw new IllegalStateException("Unknwon address type");
/*     */       }
/*     */ 
/* 413 */       if (buf.remaining() >= len)
/*     */       {
/* 415 */         byte status = buf.get(1);
/* 416 */         logger.debug("  response status: {}", SocksProxyConstants.getReplyCodeAsString(status));
/*     */ 
/* 419 */         if (status == 0) {
/* 420 */           buf.position(buf.position() + len);
/* 421 */           setHandshakeComplete();
/* 422 */           return;
/*     */         }
/* 424 */         throw new Exception("Proxy handshake failed - Code: 0x" + ByteUtilities.asHex(new byte[] { status }));
/*     */       }
/*     */ 
/* 427 */       return;
/*     */     }
/*     */ 
/* 430 */     if (len > 0) {
/* 431 */       buf.position(buf.position() + len);
/*     */     }
/*     */ 
/* 436 */     boolean isAuthenticating = false;
/* 437 */     if (step == 1) {
/* 438 */       byte method = ((Byte)getSession().getAttribute(SELECTED_AUTH_METHOD)).byteValue();
/*     */ 
/* 440 */       if (method == 1) {
/* 441 */         GSSContext ctx = (GSSContext)getSession().getAttribute(GSS_CONTEXT);
/*     */ 
/* 443 */         if ((ctx == null) || (!ctx.isEstablished())) {
/* 444 */           isAuthenticating = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 449 */     if (!isAuthenticating) {
/* 450 */       step++; getSession().setAttribute(HANDSHAKE_STEP, Integer.valueOf(step));
/*     */     }
/*     */ 
/* 453 */     doHandshake(nextFilter);
/*     */   }
/*     */ 
/*     */   protected void closeSession(String message)
/*     */   {
/* 461 */     GSSContext ctx = (GSSContext)getSession().getAttribute(GSS_CONTEXT);
/* 462 */     if (ctx != null) {
/*     */       try {
/* 464 */         ctx.dispose();
/*     */       } catch (GSSException e) {
/* 466 */         e.printStackTrace();
/* 467 */         super.closeSession(message, e);
/* 468 */         return;
/*     */       }
/*     */     }
/* 471 */     super.closeSession(message);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.socks.Socks5LogicHandler
 * JD-Core Version:    0.6.0
 */