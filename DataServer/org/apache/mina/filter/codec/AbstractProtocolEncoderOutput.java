/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Queue;
/*    */ import java.util.concurrent.ConcurrentLinkedQueue;
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ 
/*    */ public abstract class AbstractProtocolEncoderOutput
/*    */   implements ProtocolEncoderOutput
/*    */ {
/* 35 */   private final Queue<Object> messageQueue = new ConcurrentLinkedQueue();
/*    */ 
/* 37 */   private boolean buffersOnly = true;
/*    */ 
/*    */   public Queue<Object> getMessageQueue()
/*    */   {
/* 43 */     return this.messageQueue;
/*    */   }
/*    */ 
/*    */   public void write(Object encodedMessage) {
/* 47 */     if ((encodedMessage instanceof IoBuffer)) {
/* 48 */       IoBuffer buf = (IoBuffer)encodedMessage;
/* 49 */       if (buf.hasRemaining())
/* 50 */         this.messageQueue.offer(buf);
/*    */       else
/* 52 */         throw new IllegalArgumentException("buf is empty. Forgot to call flip()?");
/*    */     }
/*    */     else
/*    */     {
/* 56 */       this.messageQueue.offer(encodedMessage);
/* 57 */       this.buffersOnly = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void mergeAll() {
/* 62 */     if (!this.buffersOnly) {
/* 63 */       throw new IllegalStateException("the encoded message list contains a non-buffer.");
/*    */     }
/*    */ 
/* 67 */     int size = this.messageQueue.size();
/*    */ 
/* 69 */     if (size < 2)
/*    */     {
/* 71 */       return;
/*    */     }
/*    */ 
/* 75 */     int sum = 0;
/* 76 */     for (Iterator i$ = this.messageQueue.iterator(); i$.hasNext(); ) { Object b = i$.next();
/* 77 */       sum += ((IoBuffer)b).remaining();
/*    */     }
/*    */ 
/* 81 */     IoBuffer newBuf = IoBuffer.allocate(sum);
/*    */     while (true)
/*    */     {
/* 85 */       IoBuffer buf = (IoBuffer)this.messageQueue.poll();
/* 86 */       if (buf == null)
/*    */       {
/*    */         break;
/*    */       }
/* 90 */       newBuf.put(buf);
/*    */     }
/*    */ 
/* 94 */     newBuf.flip();
/* 95 */     this.messageQueue.add(newBuf);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.AbstractProtocolEncoderOutput
 * JD-Core Version:    0.6.0
 */