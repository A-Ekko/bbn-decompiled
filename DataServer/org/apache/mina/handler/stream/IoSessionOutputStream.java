/*    */ package org.apache.mina.handler.stream;
/*    */ 
/*    */ import B;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.future.CloseFuture;
/*    */ import org.apache.mina.core.future.WriteFuture;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ class IoSessionOutputStream extends OutputStream
/*    */ {
/*    */   private final IoSession session;
/*    */   private WriteFuture lastWriteFuture;
/*    */ 
/*    */   public IoSessionOutputStream(IoSession session)
/*    */   {
/* 43 */     this.session = session;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/*    */     try {
/* 49 */       flush();
/*    */     } finally {
/* 51 */       this.session.close(true).awaitUninterruptibly();
/*    */     }
/*    */   }
/*    */ 
/*    */   private void checkClosed() throws IOException {
/* 56 */     if (!this.session.isConnected())
/* 57 */       throw new IOException("The session has been closed.");
/*    */   }
/*    */ 
/*    */   private synchronized void write(IoBuffer buf) throws IOException
/*    */   {
/* 62 */     checkClosed();
/* 63 */     WriteFuture future = this.session.write(buf);
/* 64 */     this.lastWriteFuture = future;
/*    */   }
/*    */ 
/*    */   public void write(byte[] b, int off, int len) throws IOException
/*    */   {
/* 69 */     write(IoBuffer.wrap((byte[])b.clone(), off, len));
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException
/*    */   {
/* 74 */     IoBuffer buf = IoBuffer.allocate(1);
/* 75 */     buf.put((byte)b);
/* 76 */     buf.flip();
/* 77 */     write(buf);
/*    */   }
/*    */ 
/*    */   public synchronized void flush() throws IOException
/*    */   {
/* 82 */     if (this.lastWriteFuture == null) {
/* 83 */       return;
/*    */     }
/*    */ 
/* 86 */     this.lastWriteFuture.awaitUninterruptibly();
/* 87 */     if (!this.lastWriteFuture.isWritten())
/* 88 */       throw new IOException("The bytes could not be written to the session");
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.handler.stream.IoSessionOutputStream
 * JD-Core Version:    0.6.0
 */