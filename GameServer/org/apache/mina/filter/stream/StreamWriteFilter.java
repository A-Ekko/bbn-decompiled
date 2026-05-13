/*    */ package org.apache.mina.filter.stream;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ 
/*    */ public class StreamWriteFilter extends AbstractStreamWriteFilter<InputStream>
/*    */ {
/*    */   protected IoBuffer getNextBuffer(InputStream is)
/*    */     throws IOException
/*    */   {
/* 60 */     byte[] bytes = new byte[getWriteBufferSize()];
/*    */ 
/* 62 */     int off = 0;
/* 63 */     int n = 0;
/*    */ 
/* 65 */     while ((off < bytes.length) && ((n = is.read(bytes, off, bytes.length - off)) != -1)) {
/* 66 */       off += n;
/*    */     }
/*    */ 
/* 69 */     if ((n == -1) && (off == 0)) {
/* 70 */       return null;
/*    */     }
/*    */ 
/* 73 */     IoBuffer buffer = IoBuffer.wrap(bytes, 0, off);
/*    */ 
/* 75 */     return buffer;
/*    */   }
/*    */ 
/*    */   protected Class<InputStream> getMessageClass()
/*    */   {
/* 80 */     return InputStream.class;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.stream.StreamWriteFilter
 * JD-Core Version:    0.6.0
 */