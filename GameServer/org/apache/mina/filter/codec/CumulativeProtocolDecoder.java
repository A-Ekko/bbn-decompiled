/*     */ package org.apache.mina.filter.codec;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public abstract class CumulativeProtocolDecoder extends ProtocolDecoderAdapter
/*     */ {
/* 106 */   private final AttributeKey BUFFER = new AttributeKey(getClass(), "buffer");
/*     */ 
/*     */   public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/* 125 */     if (!session.getTransportMetadata().hasFragmentation()) {
/* 126 */       while (in.hasRemaining()) {
/* 127 */         if (!doDecode(session, in, out)) {
/* 128 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 132 */       return;
/*     */     }
/*     */ 
/* 135 */     boolean usingSessionBuffer = true;
/* 136 */     IoBuffer buf = (IoBuffer)session.getAttribute(this.BUFFER);
/*     */ 
/* 139 */     if (buf != null) {
/* 140 */       boolean appended = false;
/*     */ 
/* 142 */       if (buf.isAutoExpand()) {
/*     */         try {
/* 144 */           buf.put(in);
/* 145 */           appended = true;
/*     */         }
/*     */         catch (IllegalStateException e)
/*     */         {
/*     */         }
/*     */         catch (IndexOutOfBoundsException e)
/*     */         {
/*     */         }
/*     */       }
/* 154 */       if (appended) {
/* 155 */         buf.flip();
/*     */       }
/*     */       else
/*     */       {
/* 159 */         buf.flip();
/* 160 */         IoBuffer newBuf = IoBuffer.allocate(buf.remaining() + in.remaining()).setAutoExpand(true);
/*     */ 
/* 162 */         newBuf.order(buf.order());
/* 163 */         newBuf.put(buf);
/* 164 */         newBuf.put(in);
/* 165 */         newBuf.flip();
/* 166 */         buf = newBuf;
/*     */ 
/* 169 */         session.setAttribute(this.BUFFER, buf);
/*     */       }
/*     */     } else {
/* 172 */       buf = in;
/* 173 */       usingSessionBuffer = false;
/*     */     }
/*     */     while (true)
/*     */     {
/* 177 */       int oldPos = buf.position();
/* 178 */       boolean decoded = doDecode(session, buf, out);
/* 179 */       if (!decoded) break;
/* 180 */       if (buf.position() == oldPos) {
/* 181 */         throw new IllegalStateException("doDecode() can't return true when buffer is not consumed.");
/*     */       }
/*     */ 
/* 185 */       if (!buf.hasRemaining())
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 196 */     if (buf.hasRemaining()) {
/* 197 */       if ((usingSessionBuffer) && (buf.isAutoExpand()))
/* 198 */         buf.compact();
/*     */       else {
/* 200 */         storeRemainingInSession(buf, session);
/*     */       }
/*     */     }
/* 203 */     else if (usingSessionBuffer)
/* 204 */       removeSessionBuffer(session);
/*     */   }
/*     */ 
/*     */   protected abstract boolean doDecode(IoSession paramIoSession, IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*     */     throws Exception;
/*     */ 
/*     */   public void dispose(IoSession session)
/*     */     throws Exception
/*     */   {
/* 230 */     removeSessionBuffer(session);
/*     */   }
/*     */ 
/*     */   private void removeSessionBuffer(IoSession session) {
/* 234 */     session.removeAttribute(this.BUFFER);
/*     */   }
/*     */ 
/*     */   private void storeRemainingInSession(IoBuffer buf, IoSession session) {
/* 238 */     IoBuffer remainingBuf = IoBuffer.allocate(buf.capacity()).setAutoExpand(true);
/*     */ 
/* 240 */     remainingBuf.order(buf.order());
/* 241 */     remainingBuf.put(buf);
/*     */ 
/* 243 */     session.setAttribute(this.BUFFER, remainingBuf);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.CumulativeProtocolDecoder
 * JD-Core Version:    0.6.0
 */