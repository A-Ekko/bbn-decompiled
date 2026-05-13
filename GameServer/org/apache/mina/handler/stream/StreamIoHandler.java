/*     */ package org.apache.mina.handler.stream;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.SocketTimeoutException;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.service.IoHandlerAdapter;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class StreamIoHandler extends IoHandlerAdapter
/*     */ {
/*  48 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */ 
/*  50 */   private static final AttributeKey KEY_IN = new AttributeKey(StreamIoHandler.class, "in");
/*  51 */   private static final AttributeKey KEY_OUT = new AttributeKey(StreamIoHandler.class, "out");
/*     */   private int readTimeout;
/*     */   private int writeTimeout;
/*     */ 
/*     */   protected abstract void processStreamIo(IoSession paramIoSession, InputStream paramInputStream, OutputStream paramOutputStream);
/*     */ 
/*     */   public int getReadTimeout()
/*     */   {
/*  73 */     return this.readTimeout;
/*     */   }
/*     */ 
/*     */   public void setReadTimeout(int readTimeout)
/*     */   {
/*  81 */     this.readTimeout = readTimeout;
/*     */   }
/*     */ 
/*     */   public int getWriteTimeout()
/*     */   {
/*  89 */     return this.writeTimeout;
/*     */   }
/*     */ 
/*     */   public void setWriteTimeout(int writeTimeout)
/*     */   {
/*  97 */     this.writeTimeout = writeTimeout;
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoSession session)
/*     */   {
/* 106 */     session.getConfig().setWriteTimeout(this.writeTimeout);
/* 107 */     session.getConfig().setIdleTime(IdleStatus.READER_IDLE, this.readTimeout);
/*     */ 
/* 110 */     InputStream in = new IoSessionInputStream();
/* 111 */     OutputStream out = new IoSessionOutputStream(session);
/* 112 */     session.setAttribute(KEY_IN, in);
/* 113 */     session.setAttribute(KEY_OUT, out);
/* 114 */     processStreamIo(session, in, out);
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoSession session)
/*     */     throws Exception
/*     */   {
/* 122 */     InputStream in = (InputStream)session.getAttribute(KEY_IN);
/* 123 */     OutputStream out = (OutputStream)session.getAttribute(KEY_OUT);
/*     */     try {
/* 125 */       in.close();
/*     */     } finally {
/* 127 */       out.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoSession session, Object buf)
/*     */   {
/* 136 */     IoSessionInputStream in = (IoSessionInputStream)session.getAttribute(KEY_IN);
/*     */ 
/* 138 */     in.write((IoBuffer)buf);
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoSession session, Throwable cause)
/*     */   {
/* 146 */     IoSessionInputStream in = (IoSessionInputStream)session.getAttribute(KEY_IN);
/*     */ 
/* 149 */     IOException e = null;
/* 150 */     if ((cause instanceof StreamIoException))
/* 151 */       e = (IOException)cause.getCause();
/* 152 */     else if ((cause instanceof IOException)) {
/* 153 */       e = (IOException)cause;
/*     */     }
/*     */ 
/* 156 */     if ((e != null) && (in != null)) {
/* 157 */       in.throwException(e);
/*     */     } else {
/* 159 */       this.logger.warn("Unexpected exception.", cause);
/* 160 */       session.close(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoSession session, IdleStatus status)
/*     */   {
/* 169 */     if (status == IdleStatus.READER_IDLE)
/* 170 */       throw new StreamIoException(new SocketTimeoutException("Read timeout"));
/*     */   }
/*     */ 
/*     */   private static class StreamIoException extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 3976736960742503222L;
/*     */ 
/*     */     public StreamIoException(IOException cause) {
/* 179 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.handler.stream.StreamIoHandler
 * JD-Core Version:    0.6.0
 */