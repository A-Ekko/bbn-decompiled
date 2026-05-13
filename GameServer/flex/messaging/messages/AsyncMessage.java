/*     */ package flex.messaging.messages;
/*     */ 
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.util.UUIDUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ 
/*     */ public class AsyncMessage extends AbstractMessage
/*     */   implements SmallMessage
/*     */ {
/*     */   private static final long serialVersionUID = -3549535089417916783L;
/*     */   public static final String SUBTOPIC_HEADER_NAME = "DSSubtopic";
/*  50 */   private static byte CORRELATION_ID_FLAG = 1;
/*  51 */   private static byte CORRELATION_ID_BYTES_FLAG = 2;
/*     */   protected String correlationId;
/*     */   protected byte[] correlationIdBytes;
/*     */ 
/*     */   public String getCorrelationId()
/*     */   {
/*  70 */     return this.correlationId;
/*     */   }
/*     */ 
/*     */   public void setCorrelationId(String correlationId)
/*     */   {
/*  80 */     this.correlationId = correlationId;
/*     */   }
/*     */ 
/*     */   public Message getSmallMessage()
/*     */   {
/*  88 */     if (getClass() == AsyncMessage.class)
/*  89 */       return new AsyncMessageExt(this);
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   public void readExternal(ObjectInput input)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  98 */     super.readExternal(input);
/*     */ 
/* 100 */     short[] flagsArray = readFlags(input);
/* 101 */     for (int i = 0; i < flagsArray.length; i++)
/*     */     {
/* 103 */       short flags = flagsArray[i];
/* 104 */       short reservedPosition = 0;
/*     */ 
/* 106 */       if (i == 0)
/*     */       {
/* 108 */         if ((flags & CORRELATION_ID_FLAG) != 0) {
/* 109 */           this.correlationId = ((String)input.readObject());
/*     */         }
/* 111 */         if ((flags & CORRELATION_ID_BYTES_FLAG) != 0)
/*     */         {
/* 113 */           this.correlationIdBytes = ((byte[])(byte[])input.readObject());
/* 114 */           this.correlationId = UUIDUtils.fromByteArray(this.correlationIdBytes);
/*     */         }
/*     */ 
/* 117 */         reservedPosition = 2;
/*     */       }
/*     */ 
/* 122 */       if (flags >> reservedPosition == 0)
/*     */         continue;
/* 124 */       for (short j = reservedPosition; j < 6; j = (short)(j + 1))
/*     */       {
/* 126 */         if ((flags >> j & 0x1) == 0)
/*     */           continue;
/* 128 */         input.readObject();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeExternal(ObjectOutput output)
/*     */     throws IOException
/*     */   {
/* 140 */     super.writeExternal(output);
/*     */ 
/* 142 */     if (this.correlationIdBytes == null) {
/* 143 */       this.correlationIdBytes = UUIDUtils.toByteArray(this.correlationId);
/*     */     }
/* 145 */     short flags = 0;
/*     */ 
/* 147 */     if ((this.correlationId != null) && (this.correlationIdBytes == null)) {
/* 148 */       flags = (short)(flags | CORRELATION_ID_FLAG);
/*     */     }
/* 150 */     if (this.correlationIdBytes != null) {
/* 151 */       flags = (short)(flags | CORRELATION_ID_BYTES_FLAG);
/*     */     }
/* 153 */     output.writeByte(flags);
/*     */ 
/* 155 */     if ((this.correlationId != null) && (this.correlationIdBytes == null)) {
/* 156 */       output.writeObject(this.correlationId);
/*     */     }
/* 158 */     if (this.correlationIdBytes != null)
/* 159 */       output.writeObject(this.correlationIdBytes);
/*     */   }
/*     */ 
/*     */   protected String toStringFields(int indentLevel)
/*     */   {
/* 164 */     String sep = getFieldSeparator(indentLevel);
/* 165 */     String s = sep + "clientId = " + (Log.isExcludedProperty("clientId") ? "** [Value Suppressed] **" : this.clientId);
/* 166 */     s = s + sep + "correlationId = " + (Log.isExcludedProperty("correlationId") ? "** [Value Suppressed] **" : this.correlationId);
/* 167 */     s = s + sep + "destination = " + (Log.isExcludedProperty("destination") ? "** [Value Suppressed] **" : this.destination);
/* 168 */     s = s + sep + "messageId = " + (Log.isExcludedProperty("messageId") ? "** [Value Suppressed] **" : this.messageId);
/* 169 */     s = s + sep + "timestamp = " + (Log.isExcludedProperty("timestamp") ? "** [Value Suppressed] **" : String.valueOf(this.timestamp));
/* 170 */     s = s + sep + "timeToLive = " + (Log.isExcludedProperty("timeToLive") ? "** [Value Suppressed] **" : String.valueOf(this.timeToLive));
/* 171 */     s = s + sep + "body = " + (Log.isExcludedProperty("body") ? "** [Value Suppressed] **" : bodyToString(this.body, indentLevel));
/* 172 */     s = s + super.toStringFields(indentLevel);
/* 173 */     return s;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.AsyncMessage
 * JD-Core Version:    0.6.0
 */