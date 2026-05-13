/*    */ package org.apache.mina.filter.stream;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.FileChannel;
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.file.FileRegion;
/*    */ 
/*    */ public class FileRegionWriteFilter extends AbstractStreamWriteFilter<FileRegion>
/*    */ {
/*    */   protected Class<FileRegion> getMessageClass()
/*    */   {
/* 64 */     return FileRegion.class;
/*    */   }
/*    */ 
/*    */   protected IoBuffer getNextBuffer(FileRegion fileRegion)
/*    */     throws IOException
/*    */   {
/* 70 */     if (fileRegion.getRemainingBytes() <= 0L) {
/* 71 */       return null;
/*    */     }
/*    */ 
/* 75 */     int bufferSize = (int)Math.min(getWriteBufferSize(), fileRegion.getRemainingBytes());
/* 76 */     IoBuffer buffer = IoBuffer.allocate(bufferSize);
/*    */ 
/* 79 */     int bytesRead = fileRegion.getFileChannel().read(buffer.buf(), fileRegion.getPosition());
/*    */ 
/* 81 */     fileRegion.update(bytesRead);
/*    */ 
/* 84 */     buffer.flip();
/* 85 */     return buffer;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.stream.FileRegionWriteFilter
 * JD-Core Version:    0.6.0
 */