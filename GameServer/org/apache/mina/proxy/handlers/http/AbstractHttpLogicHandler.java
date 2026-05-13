/*     */ package org.apache.mina.proxy.handlers.http;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionInitializer;
/*     */ import org.apache.mina.proxy.AbstractProxyLogicHandler;
/*     */ import org.apache.mina.proxy.ProxyAuthException;
/*     */ import org.apache.mina.proxy.ProxyConnector;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.apache.mina.proxy.utils.IoBufferDecoder;
/*     */ import org.apache.mina.proxy.utils.StringUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractHttpLogicHandler extends AbstractProxyLogicHandler
/*     */ {
/*  52 */   private static final Logger logger = LoggerFactory.getLogger(AbstractHttpLogicHandler.class);
/*     */ 
/*  55 */   private static final String DECODER = AbstractHttpLogicHandler.class.getName() + ".Decoder";
/*     */ 
/*  59 */   private static final byte[] HTTP_DELIMITER = { 13, 10, 13, 10 };
/*     */ 
/*  62 */   private static final byte[] CRLF_DELIMITER = { 13, 10 };
/*     */ 
/*  69 */   private IoBuffer responseData = null;
/*     */ 
/*  74 */   private HttpProxyResponse parsedResponse = null;
/*     */ 
/*  79 */   private int contentLength = -1;
/*     */   private boolean hasChunkedData;
/*     */   private boolean waitingChunkedData;
/*     */   private boolean waitingFooters;
/*     */   private int entityBodyStartPosition;
/*     */   private int entityBodyLimitPosition;
/*     */ 
/*     */   public AbstractHttpLogicHandler(ProxyIoSession proxyIoSession)
/*     */   {
/* 115 */     super(proxyIoSession);
/*     */   }
/*     */ 
/*     */   public synchronized void messageReceived(IoFilter.NextFilter nextFilter, IoBuffer buf)
/*     */     throws ProxyAuthException
/*     */   {
/* 124 */     logger.debug(" messageReceived()");
/*     */ 
/* 126 */     IoBufferDecoder decoder = (IoBufferDecoder)getSession().getAttribute(DECODER);
/*     */ 
/* 128 */     if (decoder == null) {
/* 129 */       decoder = new IoBufferDecoder(HTTP_DELIMITER);
/* 130 */       getSession().setAttribute(DECODER, decoder);
/*     */     }
/*     */     try
/*     */     {
/* 134 */       if (this.parsedResponse == null)
/*     */       {
/* 136 */         this.responseData = decoder.decodeFully(buf);
/* 137 */         if (this.responseData == null) {
/* 138 */           return;
/*     */         }
/*     */ 
/* 142 */         String responseHeader = this.responseData.getString(getProxyIoSession().getCharset().newDecoder());
/*     */ 
/* 145 */         this.entityBodyStartPosition = this.responseData.position();
/*     */ 
/* 147 */         logger.debug("  response header received:\n{}", responseHeader.replace("\r", "\\r").replace("\n", "\\n\n"));
/*     */ 
/* 151 */         this.parsedResponse = decodeResponse(responseHeader);
/*     */ 
/* 154 */         if ((this.parsedResponse.getStatusCode() == 200) || ((this.parsedResponse.getStatusCode() >= 300) && (this.parsedResponse.getStatusCode() <= 307)))
/*     */         {
/* 157 */           buf.position(0);
/* 158 */           setHandshakeComplete();
/* 159 */           return;
/*     */         }
/*     */ 
/* 162 */         String contentLengthHeader = StringUtilities.getSingleValuedHeader(this.parsedResponse.getHeaders(), "Content-Length");
/*     */ 
/* 166 */         if (contentLengthHeader == null) {
/* 167 */           this.contentLength = 0;
/*     */         } else {
/* 169 */           this.contentLength = Integer.parseInt(contentLengthHeader.trim());
/*     */ 
/* 171 */           decoder.setContentLength(this.contentLength, true);
/*     */         }
/*     */       }
/*     */ 
/* 175 */       if (!this.hasChunkedData) {
/* 176 */         if (this.contentLength > 0) {
/* 177 */           IoBuffer tmp = decoder.decodeFully(buf);
/* 178 */           if (tmp == null) {
/* 179 */             return;
/*     */           }
/* 181 */           this.responseData.setAutoExpand(true);
/* 182 */           this.responseData.put(tmp);
/* 183 */           this.contentLength = 0;
/*     */         }
/*     */ 
/* 186 */         if ("chunked".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(this.parsedResponse.getHeaders(), "Transfer-Encoding")))
/*     */         {
/* 190 */           logger.debug("Retrieving additional http response chunks");
/* 191 */           this.hasChunkedData = true;
/* 192 */           this.waitingChunkedData = true;
/*     */         }
/*     */       }
/*     */ 
/* 196 */       if (this.hasChunkedData)
/*     */       {
/* 198 */         while (this.waitingChunkedData) {
/* 199 */           if (this.contentLength == 0) {
/* 200 */             decoder.setDelimiter(CRLF_DELIMITER, false);
/* 201 */             IoBuffer tmp = decoder.decodeFully(buf);
/* 202 */             if (tmp == null) {
/* 203 */               return;
/*     */             }
/*     */ 
/* 206 */             String chunkSize = tmp.getString(getProxyIoSession().getCharset().newDecoder());
/*     */ 
/* 208 */             int pos = chunkSize.indexOf(';');
/* 209 */             if (pos >= 0)
/* 210 */               chunkSize = chunkSize.substring(0, pos);
/*     */             else {
/* 212 */               chunkSize = chunkSize.substring(0, chunkSize.length() - 2);
/*     */             }
/*     */ 
/* 215 */             this.contentLength = Integer.decode("0x" + chunkSize).intValue();
/* 216 */             if (this.contentLength > 0) {
/* 217 */               this.contentLength += 2;
/* 218 */               decoder.setContentLength(this.contentLength, true);
/*     */             }
/*     */           }
/*     */ 
/* 222 */           if (this.contentLength == 0) {
/* 223 */             this.waitingChunkedData = false;
/* 224 */             this.waitingFooters = true;
/* 225 */             this.entityBodyLimitPosition = this.responseData.position();
/* 226 */             break;
/*     */           }
/*     */ 
/* 229 */           IoBuffer tmp = decoder.decodeFully(buf);
/* 230 */           if (tmp == null) {
/* 231 */             return;
/*     */           }
/* 233 */           this.contentLength = 0;
/* 234 */           this.responseData.put(tmp);
/* 235 */           buf.position(buf.position());
/*     */         }
/*     */ 
/* 239 */         while (this.waitingFooters) {
/* 240 */           decoder.setDelimiter(CRLF_DELIMITER, false);
/* 241 */           IoBuffer tmp = decoder.decodeFully(buf);
/* 242 */           if (tmp == null) {
/* 243 */             return;
/*     */           }
/*     */ 
/* 246 */           if (tmp.remaining() == 2) {
/* 247 */             this.waitingFooters = false;
/* 248 */             break;
/*     */           }
/*     */ 
/* 252 */           String footer = tmp.getString(getProxyIoSession().getCharset().newDecoder());
/*     */ 
/* 254 */           String[] f = footer.split(":\\s?", 2);
/* 255 */           StringUtilities.addValueToHeader(this.parsedResponse.getHeaders(), f[0], f[1], false);
/*     */ 
/* 257 */           this.responseData.put(tmp);
/* 258 */           this.responseData.put(CRLF_DELIMITER);
/*     */         }
/*     */       }
/*     */ 
/* 262 */       this.responseData.flip();
/*     */ 
/* 264 */       logger.debug("  end of response received:\n{}", this.responseData.getString(getProxyIoSession().getCharset().newDecoder()));
/*     */ 
/* 269 */       this.responseData.position(this.entityBodyStartPosition);
/* 270 */       this.responseData.limit(this.entityBodyLimitPosition);
/* 271 */       this.parsedResponse.setBody(this.responseData.getString(getProxyIoSession().getCharset().newDecoder()));
/*     */ 
/* 275 */       this.responseData.free();
/* 276 */       this.responseData = null;
/*     */ 
/* 278 */       handleResponse(this.parsedResponse);
/*     */ 
/* 280 */       this.parsedResponse = null;
/* 281 */       this.hasChunkedData = false;
/* 282 */       this.contentLength = -1;
/* 283 */       decoder.setDelimiter(HTTP_DELIMITER, true);
/*     */ 
/* 285 */       if (!isHandshakeComplete())
/* 286 */         doHandshake(nextFilter);
/*     */     }
/*     */     catch (Exception ex) {
/* 289 */       if ((ex instanceof ProxyAuthException)) {
/* 290 */         throw ((ProxyAuthException)ex);
/*     */       }
/* 292 */       throw new ProxyAuthException("Handshake failed", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void handleResponse(HttpProxyResponse paramHttpProxyResponse)
/*     */     throws ProxyAuthException;
/*     */ 
/*     */   public void writeRequest(IoFilter.NextFilter nextFilter, HttpProxyRequest request)
/*     */     throws ProxyAuthException
/*     */   {
/* 311 */     ProxyIoSession proxyIoSession = getProxyIoSession();
/*     */ 
/* 313 */     if (proxyIoSession.isReconnectionNeeded())
/* 314 */       reconnect(nextFilter, request);
/*     */     else
/* 316 */       writeRequest0(nextFilter, request);
/*     */   }
/*     */ 
/*     */   private void writeRequest0(IoFilter.NextFilter nextFilter, HttpProxyRequest request)
/*     */   {
/*     */     try
/*     */     {
/* 326 */       String data = request.toHttpString();
/* 327 */       IoBuffer buf = IoBuffer.wrap(data.getBytes(getProxyIoSession().getCharsetName()));
/*     */ 
/* 330 */       logger.debug("   write:\n{}", data.replace("\r", "\\r").replace("\n", "\\n\n"));
/*     */ 
/* 333 */       writeData(nextFilter, buf);
/*     */     }
/*     */     catch (UnsupportedEncodingException ex) {
/* 336 */       closeSession("Unable to send HTTP request: ", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void reconnect(IoFilter.NextFilter nextFilter, HttpProxyRequest request)
/*     */     throws ProxyAuthException
/*     */   {
/* 346 */     logger.debug("Reconnecting to proxy ...");
/*     */ 
/* 348 */     ProxyIoSession proxyIoSession = getProxyIoSession();
/* 349 */     ProxyConnector connector = proxyIoSession.getConnector();
/*     */ 
/* 351 */     connector.connect(new IoSessionInitializer(proxyIoSession, nextFilter, request)
/*     */     {
/*     */       public void initializeSession(IoSession session, ConnectFuture future) {
/* 354 */         AbstractHttpLogicHandler.logger.debug("Initializing new session: " + session);
/* 355 */         session.setAttribute(ProxyIoSession.PROXY_SESSION, this.val$proxyIoSession);
/*     */ 
/* 357 */         this.val$proxyIoSession.setSession(session);
/* 358 */         AbstractHttpLogicHandler.logger.debug("  setting proxyIoSession: " + this.val$proxyIoSession);
/* 359 */         future.addListener(new IoFutureListener() {
/*     */           public void operationComplete(ConnectFuture future) {
/* 361 */             AbstractHttpLogicHandler.1.this.val$proxyIoSession.setReconnectionNeeded(false);
/* 362 */             AbstractHttpLogicHandler.this.writeRequest0(AbstractHttpLogicHandler.1.this.val$nextFilter, AbstractHttpLogicHandler.1.this.val$request);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected HttpProxyResponse decodeResponse(String response)
/*     */     throws Exception
/*     */   {
/* 376 */     logger.debug("  parseResponse()");
/*     */ 
/* 379 */     String[] responseLines = response.split("\r\n");
/*     */ 
/* 384 */     String[] statusLine = responseLines[0].trim().split(" ", 2);
/*     */ 
/* 386 */     if (statusLine.length < 2) {
/* 387 */       throw new Exception("Invalid response status line (" + statusLine + "). Response: " + response);
/*     */     }
/*     */ 
/* 392 */     if (statusLine[1].matches("^\\d\\d\\d")) {
/* 393 */       throw new Exception("Invalid response code (" + statusLine[1] + "). Response: " + response);
/*     */     }
/*     */ 
/* 397 */     Map headers = new HashMap();
/*     */ 
/* 399 */     for (int i = 1; i < responseLines.length; i++) {
/* 400 */       String[] args = responseLines[i].split(":\\s?", 2);
/* 401 */       StringUtilities.addValueToHeader(headers, args[0], args[1], false);
/*     */     }
/*     */ 
/* 404 */     return new HttpProxyResponse(statusLine[0], statusLine[1], headers);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.AbstractHttpLogicHandler
 * JD-Core Version:    0.6.0
 */