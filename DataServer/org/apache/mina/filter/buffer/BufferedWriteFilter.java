/*     */ package org.apache.mina.filter.buffer;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.DefaultWriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.util.LazyInitializedCacheMap;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public final class BufferedWriteFilter extends IoFilterAdapter
/*     */ {
/*  51 */   private final Logger logger = LoggerFactory.getLogger(BufferedWriteFilter.class);
/*     */   public static final int DEFAULT_BUFFER_SIZE = 8192;
/*  62 */   private int bufferSize = 8192;
/*     */   private final LazyInitializedCacheMap<IoSession, IoBuffer> buffersMap;
/*     */ 
/*     */   public BufferedWriteFilter()
/*     */   {
/*  75 */     this(8192, null);
/*     */   }
/*     */ 
/*     */   public BufferedWriteFilter(int bufferSize)
/*     */   {
/*  85 */     this(bufferSize, null);
/*     */   }
/*     */ 
/*     */   public BufferedWriteFilter(int bufferSize, LazyInitializedCacheMap<IoSession, IoBuffer> buffersMap)
/*     */   {
/*  99 */     this.bufferSize = bufferSize;
/* 100 */     if (buffersMap == null)
/* 101 */       this.buffersMap = new LazyInitializedCacheMap();
/*     */     else
/* 103 */       this.buffersMap = buffersMap;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 111 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize)
/*     */   {
/* 120 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 133 */     Object data = writeRequest.getMessage();
/*     */ 
/* 135 */     if ((data instanceof IoBuffer))
/* 136 */       write(session, (IoBuffer)data);
/*     */     else
/* 138 */       throw new IllegalArgumentException("This filter should only buffer IoBuffer objects");
/*     */   }
/*     */ 
/*     */   private void write(IoSession session, IoBuffer data)
/*     */   {
/* 150 */     IoBuffer dest = (IoBuffer)this.buffersMap.putIfAbsent(session, new IoBufferLazyInitializer(this.bufferSize));
/*     */ 
/* 153 */     write(session, data, dest);
/*     */   }
/*     */ 
/*     */   private void write(IoSession session, IoBuffer data, IoBuffer buf)
/*     */   {
/*     */     try
/*     */     {
/* 168 */       int len = data.remaining();
/* 169 */       if (len >= buf.capacity())
/*     */       {
/* 174 */         IoFilter.NextFilter nextFilter = session.getFilterChain().getNextFilter(this);
/*     */ 
/* 176 */         internalFlush(nextFilter, session, buf);
/* 177 */         nextFilter.filterWrite(session, new DefaultWriteRequest(data));
/* 178 */         return;
/*     */       }
/* 180 */       if (len > buf.limit() - buf.position()) {
/* 181 */         internalFlush(session.getFilterChain().getNextFilter(this), session, buf);
/*     */       }
/*     */ 
/* 184 */       synchronized (buf) {
/* 185 */         buf.put(data);
/*     */       }
/*     */     } catch (Throwable e) {
/* 188 */       session.getFilterChain().fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void internalFlush(IoFilter.NextFilter nextFilter, IoSession session, IoBuffer buf)
/*     */     throws Exception
/*     */   {
/* 202 */     IoBuffer tmp = null;
/* 203 */     synchronized (buf) {
/* 204 */       buf.flip();
/* 205 */       tmp = buf.duplicate();
/* 206 */       buf.clear();
/*     */     }
/* 208 */     this.logger.debug("Flushing buffer: {}", tmp);
/* 209 */     nextFilter.filterWrite(session, new DefaultWriteRequest(tmp));
/*     */   }
/*     */ 
/*     */   public void flush(IoSession session)
/*     */   {
/*     */     try
/*     */     {
/* 219 */       internalFlush(session.getFilterChain().getNextFilter(this), session, (IoBuffer)this.buffersMap.get(session));
/*     */     }
/*     */     catch (Throwable e) {
/* 222 */       session.getFilterChain().fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void free(IoSession session)
/*     */   {
/* 233 */     IoBuffer buf = (IoBuffer)this.buffersMap.remove(session);
/* 234 */     if (buf != null)
/* 235 */       buf.free();
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/* 245 */     free(session);
/* 246 */     nextFilter.exceptionCaught(session, cause);
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 255 */     free(session);
/* 256 */     nextFilter.sessionClosed(session);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.buffer.BufferedWriteFilter
 * JD-Core Version:    0.6.0
 */