/*     */ package org.apache.mina.filter.errorgenerating;
/*     */ 
/*     */ import java.util.Random;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.DefaultWriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ErrorGeneratingFilter extends IoFilterAdapter
/*     */ {
/*  57 */   private int removeByteProbability = 0;
/*     */ 
/*  59 */   private int insertByteProbability = 0;
/*     */ 
/*  61 */   private int changeByteProbability = 0;
/*     */ 
/*  63 */   private int removePduProbability = 0;
/*     */ 
/*  65 */   private int duplicatePduProbability = 0;
/*     */ 
/*  67 */   private int resendPduLasterProbability = 0;
/*     */ 
/*  69 */   private int maxInsertByte = 10;
/*     */ 
/*  71 */   private boolean manipulateWrites = false;
/*     */ 
/*  73 */   private boolean manipulateReads = false;
/*     */ 
/*  75 */   private Random rng = new Random();
/*     */ 
/*  77 */   private final Logger logger = LoggerFactory.getLogger(ErrorGeneratingFilter.class);
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/*  83 */     if (this.manipulateWrites)
/*     */     {
/*  85 */       if ((writeRequest.getMessage() instanceof IoBuffer)) {
/*  86 */         manipulateIoBuffer(session, (IoBuffer)writeRequest.getMessage());
/*     */ 
/*  88 */         IoBuffer buffer = insertBytesToNewIoBuffer(session, (IoBuffer)writeRequest.getMessage());
/*     */ 
/*  90 */         if (buffer != null) {
/*  91 */           writeRequest = new DefaultWriteRequest(buffer, writeRequest.getFuture(), writeRequest.getDestination());
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  96 */         if (this.duplicatePduProbability > this.rng.nextInt()) {
/*  97 */           nextFilter.filterWrite(session, writeRequest);
/*     */         }
/*  99 */         if ((this.resendPduLasterProbability <= this.rng.nextInt()) || 
/* 104 */           (this.removePduProbability > this.rng.nextInt())) {
/* 105 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 109 */     nextFilter.filterWrite(session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 115 */     if ((this.manipulateReads) && 
/* 116 */       ((message instanceof IoBuffer)))
/*     */     {
/* 118 */       manipulateIoBuffer(session, (IoBuffer)message);
/* 119 */       IoBuffer buffer = insertBytesToNewIoBuffer(session, (IoBuffer)message);
/*     */ 
/* 121 */       if (buffer != null) {
/* 122 */         message = buffer;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 128 */     nextFilter.messageReceived(session, message);
/*     */   }
/*     */ 
/*     */   private IoBuffer insertBytesToNewIoBuffer(IoSession session, IoBuffer buffer) {
/* 132 */     if (this.insertByteProbability > this.rng.nextInt(1000)) {
/* 133 */       this.logger.info(buffer.getHexDump());
/*     */ 
/* 135 */       int pos = this.rng.nextInt(buffer.remaining()) - 1;
/*     */ 
/* 138 */       int count = this.rng.nextInt(this.maxInsertByte - 1) + 1;
/*     */ 
/* 140 */       IoBuffer newBuff = IoBuffer.allocate(buffer.remaining() + count);
/* 141 */       for (int i = 0; i < pos; i++)
/* 142 */         newBuff.put(buffer.get());
/* 143 */       for (int i = 0; i < count; i++) {
/* 144 */         newBuff.put((byte)this.rng.nextInt(256));
/*     */       }
/* 146 */       while (buffer.remaining() > 0) {
/* 147 */         newBuff.put(buffer.get());
/*     */       }
/* 149 */       newBuff.flip();
/*     */ 
/* 151 */       this.logger.info("Inserted " + count + " bytes.");
/* 152 */       this.logger.info(newBuff.getHexDump());
/* 153 */       return newBuff;
/*     */     }
/* 155 */     return null;
/*     */   }
/*     */ 
/*     */   private void manipulateIoBuffer(IoSession session, IoBuffer buffer) {
/* 159 */     if (this.removeByteProbability > this.rng.nextInt(1000)) {
/* 160 */       this.logger.info(buffer.getHexDump());
/*     */ 
/* 162 */       int pos = this.rng.nextInt(buffer.remaining());
/*     */ 
/* 164 */       int count = this.rng.nextInt(buffer.remaining() - pos) + 1;
/* 165 */       if (count == buffer.remaining()) {
/* 166 */         count = buffer.remaining() - 1;
/*     */       }
/* 168 */       IoBuffer newBuff = IoBuffer.allocate(buffer.remaining() - count);
/* 169 */       for (int i = 0; i < pos; i++) {
/* 170 */         newBuff.put(buffer.get());
/*     */       }
/* 172 */       buffer.skip(count);
/* 173 */       while (newBuff.remaining() > 0)
/* 174 */         newBuff.put(buffer.get());
/* 175 */       newBuff.flip();
/*     */ 
/* 177 */       buffer.rewind();
/* 178 */       buffer.put(newBuff);
/* 179 */       buffer.flip();
/* 180 */       this.logger.info("Removed " + count + " bytes at position " + pos + ".");
/* 181 */       this.logger.info(buffer.getHexDump());
/*     */     }
/* 183 */     if (this.changeByteProbability > this.rng.nextInt(1000)) {
/* 184 */       this.logger.info(buffer.getHexDump());
/*     */ 
/* 186 */       int count = this.rng.nextInt(buffer.remaining() - 1) + 1;
/*     */ 
/* 188 */       byte[] values = new byte[count];
/* 189 */       this.rng.nextBytes(values);
/* 190 */       for (int i = 0; i < values.length; i++) {
/* 191 */         int pos = this.rng.nextInt(buffer.remaining());
/* 192 */         buffer.put(pos, values[i]);
/*     */       }
/* 194 */       this.logger.info("Modified " + count + " bytes.");
/* 195 */       this.logger.info(buffer.getHexDump());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getChangeByteProbability() {
/* 200 */     return this.changeByteProbability;
/*     */   }
/*     */ 
/*     */   public void setChangeByteProbability(int changeByteProbability)
/*     */   {
/* 210 */     this.changeByteProbability = changeByteProbability;
/*     */   }
/*     */ 
/*     */   public int getDuplicatePduProbability() {
/* 214 */     return this.duplicatePduProbability;
/*     */   }
/*     */ 
/*     */   public void setDuplicatePduProbability(int duplicatePduProbability)
/*     */   {
/* 222 */     this.duplicatePduProbability = duplicatePduProbability;
/*     */   }
/*     */ 
/*     */   public int getInsertByteProbability() {
/* 226 */     return this.insertByteProbability;
/*     */   }
/*     */ 
/*     */   public void setInsertByteProbability(int insertByteProbability)
/*     */   {
/* 236 */     this.insertByteProbability = insertByteProbability;
/*     */   }
/*     */ 
/*     */   public boolean isManipulateReads() {
/* 240 */     return this.manipulateReads;
/*     */   }
/*     */ 
/*     */   public void setManipulateReads(boolean manipulateReads)
/*     */   {
/* 248 */     this.manipulateReads = manipulateReads;
/*     */   }
/*     */ 
/*     */   public boolean isManipulateWrites() {
/* 252 */     return this.manipulateWrites;
/*     */   }
/*     */ 
/*     */   public void setManipulateWrites(boolean manipulateWrites)
/*     */   {
/* 260 */     this.manipulateWrites = manipulateWrites;
/*     */   }
/*     */ 
/*     */   public int getRemoveByteProbability() {
/* 264 */     return this.removeByteProbability;
/*     */   }
/*     */ 
/*     */   public void setRemoveByteProbability(int removeByteProbability)
/*     */   {
/* 274 */     this.removeByteProbability = removeByteProbability;
/*     */   }
/*     */ 
/*     */   public int getRemovePduProbability() {
/* 278 */     return this.removePduProbability;
/*     */   }
/*     */ 
/*     */   public void setRemovePduProbability(int removePduProbability)
/*     */   {
/* 286 */     this.removePduProbability = removePduProbability;
/*     */   }
/*     */ 
/*     */   public int getResendPduLasterProbability() {
/* 290 */     return this.resendPduLasterProbability;
/*     */   }
/*     */ 
/*     */   public void setResendPduLasterProbability(int resendPduLasterProbability)
/*     */   {
/* 297 */     this.resendPduLasterProbability = resendPduLasterProbability;
/*     */   }
/*     */ 
/*     */   public int getMaxInsertByte() {
/* 301 */     return this.maxInsertByte;
/*     */   }
/*     */ 
/*     */   public void setMaxInsertByte(int maxInsertByte)
/*     */   {
/* 310 */     this.maxInsertByte = maxInsertByte;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.errorgenerating.ErrorGeneratingFilter
 * JD-Core Version:    0.6.0
 */