/*     */ package flex.messaging.messages;
/*     */ 
/*     */ public class MessagePerformanceInfo
/*     */ {
/*     */   public long messageSize;
/*     */   public long sendTime;
/*     */   public long receiveTime;
/*     */   public long overheadTime;
/*     */   public String infoType;
/*     */   public boolean pushedFlag;
/*     */   public boolean recordMessageSizes;
/*     */   public boolean recordMessageTimes;
/*     */   public long serverPrePushTime;
/*     */   public long serverPreAdapterTime;
/*     */   public long serverPostAdapterTime;
/*     */   public long serverPreAdapterExternalTime;
/*     */   public long serverPostAdapterExternalTime;
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 146 */     MessagePerformanceInfo mpii = new MessagePerformanceInfo();
/* 147 */     mpii.messageSize = this.messageSize;
/* 148 */     mpii.sendTime = this.sendTime;
/* 149 */     mpii.receiveTime = this.receiveTime;
/* 150 */     mpii.overheadTime = this.overheadTime;
/* 151 */     mpii.serverPrePushTime = this.serverPrePushTime;
/* 152 */     mpii.serverPreAdapterTime = this.serverPreAdapterTime;
/* 153 */     mpii.serverPostAdapterTime = this.serverPostAdapterTime;
/* 154 */     mpii.serverPreAdapterExternalTime = this.serverPreAdapterExternalTime;
/* 155 */     mpii.serverPostAdapterExternalTime = this.serverPostAdapterExternalTime;
/* 156 */     mpii.recordMessageSizes = this.recordMessageSizes;
/* 157 */     mpii.recordMessageTimes = this.recordMessageTimes;
/* 158 */     return mpii;
/*     */   }
/*     */ 
/*     */   public void addToOverhead(long overhead)
/*     */   {
/* 169 */     this.overheadTime += overhead;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.MessagePerformanceInfo
 * JD-Core Version:    0.6.0
 */