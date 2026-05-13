/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ByteChannel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.file.FileRegion;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoProcessor;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoProcessor.SessionState;
/*     */ 
/*     */ public final class NioProcessor extends AbstractPollingIoProcessor<NioSession>
/*     */ {
/*     */   private final Selector selector;
/*     */ 
/*     */   public NioProcessor(Executor executor)
/*     */   {
/*  53 */     super(executor);
/*     */     try
/*     */     {
/*  56 */       this.selector = Selector.open();
/*     */     } catch (IOException e) {
/*  58 */       throw new RuntimeIoException("Failed to open a selector.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void dispose0() throws Exception
/*     */   {
/*  64 */     this.selector.close();
/*     */   }
/*     */ 
/*     */   protected int select(long timeout) throws Exception
/*     */   {
/*  69 */     return this.selector.select(timeout);
/*     */   }
/*     */ 
/*     */   protected int select() throws Exception
/*     */   {
/*  74 */     return this.selector.select();
/*     */   }
/*     */ 
/*     */   protected boolean isSelectorEmpty()
/*     */   {
/*  79 */     return this.selector.keys().isEmpty();
/*     */   }
/*     */ 
/*     */   protected void wakeup()
/*     */   {
/*  84 */     this.selector.wakeup();
/*     */   }
/*     */ 
/*     */   protected Iterator<NioSession> allSessions()
/*     */   {
/*  89 */     return new IoSessionIterator(this.selector.keys(), null);
/*     */   }
/*     */ 
/*     */   protected Iterator<NioSession> selectedSessions()
/*     */   {
/*  94 */     return new IoSessionIterator(this.selector.selectedKeys(), null);
/*     */   }
/*     */ 
/*     */   protected void init(NioSession session) throws Exception
/*     */   {
/*  99 */     SelectableChannel ch = (SelectableChannel)session.getChannel();
/* 100 */     ch.configureBlocking(false);
/* 101 */     session.setSelectionKey(ch.register(this.selector, 1, session));
/*     */   }
/*     */ 
/*     */   protected void destroy(NioSession session) throws Exception
/*     */   {
/* 106 */     ByteChannel ch = session.getChannel();
/* 107 */     SelectionKey key = session.getSelectionKey();
/* 108 */     if (key != null) {
/* 109 */       key.cancel();
/*     */     }
/* 111 */     ch.close();
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoProcessor.SessionState state(NioSession session)
/*     */   {
/* 116 */     SelectionKey key = session.getSelectionKey();
/* 117 */     if (key == null) {
/* 118 */       return AbstractPollingIoProcessor.SessionState.PREPARING;
/*     */     }
/*     */ 
/* 121 */     return key.isValid() ? AbstractPollingIoProcessor.SessionState.OPEN : AbstractPollingIoProcessor.SessionState.CLOSED;
/*     */   }
/*     */ 
/*     */   protected boolean isReadable(NioSession session)
/*     */   {
/* 126 */     SelectionKey key = session.getSelectionKey();
/* 127 */     return (key.isValid()) && (key.isReadable());
/*     */   }
/*     */ 
/*     */   protected boolean isWritable(NioSession session)
/*     */   {
/* 132 */     SelectionKey key = session.getSelectionKey();
/* 133 */     return (key.isValid()) && (key.isWritable());
/*     */   }
/*     */ 
/*     */   protected boolean isInterestedInRead(NioSession session)
/*     */   {
/* 138 */     SelectionKey key = session.getSelectionKey();
/* 139 */     return (key.isValid()) && ((key.interestOps() & 0x1) != 0);
/*     */   }
/*     */ 
/*     */   protected boolean isInterestedInWrite(NioSession session)
/*     */   {
/* 144 */     SelectionKey key = session.getSelectionKey();
/* 145 */     return (key.isValid()) && ((key.interestOps() & 0x4) != 0);
/*     */   }
/*     */ 
/*     */   protected void setInterestedInRead(NioSession session, boolean value) throws Exception
/*     */   {
/* 150 */     SelectionKey key = session.getSelectionKey();
/* 151 */     int oldInterestOps = key.interestOps();
/*     */     int newInterestOps;
/*     */     int newInterestOps;
/* 153 */     if (value)
/* 154 */       newInterestOps = oldInterestOps | 0x1;
/*     */     else {
/* 156 */       newInterestOps = oldInterestOps & 0xFFFFFFFE;
/*     */     }
/* 158 */     if (oldInterestOps != newInterestOps)
/* 159 */       key.interestOps(newInterestOps);
/*     */   }
/*     */ 
/*     */   protected void setInterestedInWrite(NioSession session, boolean value)
/*     */     throws Exception
/*     */   {
/* 165 */     SelectionKey key = session.getSelectionKey();
/* 166 */     int oldInterestOps = key.interestOps();
/*     */     int newInterestOps;
/*     */     int newInterestOps;
/* 168 */     if (value)
/* 169 */       newInterestOps = oldInterestOps | 0x4;
/*     */     else {
/* 171 */       newInterestOps = oldInterestOps & 0xFFFFFFFB;
/*     */     }
/* 173 */     if (oldInterestOps != newInterestOps)
/* 174 */       key.interestOps(newInterestOps);
/*     */   }
/*     */ 
/*     */   protected int read(NioSession session, IoBuffer buf)
/*     */     throws Exception
/*     */   {
/* 180 */     return session.getChannel().read(buf.buf());
/*     */   }
/*     */ 
/*     */   protected int write(NioSession session, IoBuffer buf, int length) throws Exception
/*     */   {
/* 185 */     if (buf.remaining() <= length) {
/* 186 */       return session.getChannel().write(buf.buf());
/*     */     }
/* 188 */     int oldLimit = buf.limit();
/* 189 */     buf.limit(buf.position() + length);
/*     */     try {
/* 191 */       int i = session.getChannel().write(buf.buf());
/*     */       return i; } finally { buf.limit(oldLimit); } throw localObject;
/*     */   }
/*     */ 
/*     */   protected int transferFile(NioSession session, FileRegion region, int length)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 201 */       return (int)region.getFileChannel().transferTo(region.getPosition(), length, session.getChannel());
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 205 */       String message = e.getMessage();
/* 206 */       if ((message != null) && (message.contains("temporarily unavailable")))
/* 207 */         return 0;
/*     */     }
/* 209 */     throw e;
/*     */   }
/*     */ 
/*     */   protected static class IoSessionIterator
/*     */     implements Iterator<NioSession>
/*     */   {
/*     */     private final Iterator<SelectionKey> iterator;
/*     */ 
/*     */     private IoSessionIterator(Set<SelectionKey> keys)
/*     */     {
/* 227 */       this.iterator = keys.iterator();
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 234 */       return this.iterator.hasNext();
/*     */     }
/*     */ 
/*     */     public NioSession next()
/*     */     {
/* 241 */       SelectionKey key = (SelectionKey)this.iterator.next();
/* 242 */       NioSession nioSession = (NioSession)key.attachment();
/* 243 */       return nioSession;
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 250 */       this.iterator.remove();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioProcessor
 * JD-Core Version:    0.6.0
 */