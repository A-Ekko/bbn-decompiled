/*    */ package org.apache.mina.core.file;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.FileChannel;
/*    */ 
/*    */ public class DefaultFileRegion
/*    */   implements FileRegion
/*    */ {
/*    */   private final FileChannel channel;
/*    */   private final long originalPosition;
/*    */   private long position;
/*    */   private long remainingBytes;
/*    */ 
/*    */   public DefaultFileRegion(FileChannel channel)
/*    */     throws IOException
/*    */   {
/* 42 */     this(channel, 0L, channel.size());
/*    */   }
/*    */ 
/*    */   public DefaultFileRegion(FileChannel channel, long position, long remainingBytes) {
/* 46 */     if (channel == null) {
/* 47 */       throw new IllegalArgumentException("channel can not be null");
/*    */     }
/* 49 */     if (position < 0L) {
/* 50 */       throw new IllegalArgumentException("position may not be less than 0");
/*    */     }
/* 52 */     if (remainingBytes < 0L) {
/* 53 */       throw new IllegalArgumentException("remainingBytes may not be less than 0");
/*    */     }
/* 55 */     this.channel = channel;
/* 56 */     this.originalPosition = position;
/* 57 */     this.position = position;
/* 58 */     this.remainingBytes = remainingBytes;
/*    */   }
/*    */ 
/*    */   public long getWrittenBytes() {
/* 62 */     return this.position - this.originalPosition;
/*    */   }
/*    */ 
/*    */   public long getRemainingBytes() {
/* 66 */     return this.remainingBytes;
/*    */   }
/*    */ 
/*    */   public FileChannel getFileChannel() {
/* 70 */     return this.channel;
/*    */   }
/*    */ 
/*    */   public long getPosition() {
/* 74 */     return this.position;
/*    */   }
/*    */ 
/*    */   public void update(long value) {
/* 78 */     this.position += value;
/* 79 */     this.remainingBytes -= value;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.file.DefaultFileRegion
 * JD-Core Version:    0.6.0
 */