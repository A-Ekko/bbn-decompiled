/*     */ package org.apache.mina.filter.stream;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Queue;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.DefaultWriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.util.CircularQueue;
/*     */ 
/*     */ public abstract class AbstractStreamWriteFilter<T> extends IoFilterAdapter
/*     */ {
/*     */   public static final int DEFAULT_STREAM_BUFFER_SIZE = 4096;
/*  49 */   protected final AttributeKey CURRENT_STREAM = new AttributeKey(getClass(), "stream");
/*     */ 
/*  51 */   protected final AttributeKey WRITE_REQUEST_QUEUE = new AttributeKey(getClass(), "queue");
/*  52 */   protected final AttributeKey CURRENT_WRITE_REQUEST = new AttributeKey(getClass(), "writeRequest");
/*     */ 
/*  54 */   private int writeBufferSize = 4096;
/*     */ 
/*     */   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/*  60 */     Class clazz = getClass();
/*  61 */     if (parent.contains(clazz))
/*  62 */       throw new IllegalStateException("Only one " + clazz.getName() + " is permitted.");
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/*  71 */     if (session.getAttribute(this.CURRENT_STREAM) != null) {
/*  72 */       Queue queue = getWriteRequestQueue(session);
/*  73 */       if (queue == null) {
/*  74 */         queue = new CircularQueue();
/*  75 */         session.setAttribute(this.WRITE_REQUEST_QUEUE, queue);
/*     */       }
/*  77 */       queue.add(writeRequest);
/*  78 */       return;
/*     */     }
/*     */ 
/*  81 */     Object message = writeRequest.getMessage();
/*     */ 
/*  83 */     if (getMessageClass().isInstance(message))
/*     */     {
/*  85 */       Object stream = getMessageClass().cast(message);
/*     */ 
/*  87 */       IoBuffer buffer = getNextBuffer(stream);
/*  88 */       if (buffer == null)
/*     */       {
/*  90 */         writeRequest.getFuture().setWritten();
/*  91 */         nextFilter.messageSent(session, writeRequest);
/*     */       } else {
/*  93 */         session.setAttribute(this.CURRENT_STREAM, message);
/*  94 */         session.setAttribute(this.CURRENT_WRITE_REQUEST, writeRequest);
/*     */ 
/*  96 */         nextFilter.filterWrite(session, new DefaultWriteRequest(buffer));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 101 */       nextFilter.filterWrite(session, writeRequest);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract Class<T> getMessageClass();
/*     */ 
/*     */   private Queue<WriteRequest> getWriteRequestQueue(IoSession session) {
/* 109 */     return (Queue)session.getAttribute(this.WRITE_REQUEST_QUEUE);
/*     */   }
/*     */ 
/*     */   private Queue<WriteRequest> removeWriteRequestQueue(IoSession session)
/*     */   {
/* 114 */     return (Queue)session.removeAttribute(this.WRITE_REQUEST_QUEUE);
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 120 */     Object stream = getMessageClass().cast(session.getAttribute(this.CURRENT_STREAM));
/*     */ 
/* 122 */     if (stream == null) {
/* 123 */       nextFilter.messageSent(session, writeRequest);
/*     */     } else {
/* 125 */       IoBuffer buffer = getNextBuffer(stream);
/*     */ 
/* 127 */       if (buffer == null)
/*     */       {
/* 129 */         session.removeAttribute(this.CURRENT_STREAM);
/* 130 */         WriteRequest currentWriteRequest = (WriteRequest)session.removeAttribute(this.CURRENT_WRITE_REQUEST);
/*     */ 
/* 134 */         Queue queue = removeWriteRequestQueue(session);
/* 135 */         if (queue != null) {
/* 136 */           WriteRequest wr = (WriteRequest)queue.poll();
/* 137 */           while (wr != null) {
/* 138 */             filterWrite(nextFilter, session, wr);
/* 139 */             wr = (WriteRequest)queue.poll();
/*     */           }
/*     */         }
/*     */ 
/* 143 */         currentWriteRequest.getFuture().setWritten();
/* 144 */         nextFilter.messageSent(session, currentWriteRequest);
/*     */       } else {
/* 146 */         nextFilter.filterWrite(session, new DefaultWriteRequest(buffer));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getWriteBufferSize()
/*     */   {
/* 159 */     return this.writeBufferSize;
/*     */   }
/*     */ 
/*     */   public void setWriteBufferSize(int writeBufferSize)
/*     */   {
/* 169 */     if (writeBufferSize < 1) {
/* 170 */       throw new IllegalArgumentException("writeBufferSize must be at least 1");
/*     */     }
/*     */ 
/* 173 */     this.writeBufferSize = writeBufferSize;
/*     */   }
/*     */ 
/*     */   protected abstract IoBuffer getNextBuffer(T paramT)
/*     */     throws IOException;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.stream.AbstractStreamWriteFilter
 * JD-Core Version:    0.6.0
 */