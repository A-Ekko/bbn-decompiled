/*    */ package flex.messaging.messages;
/*    */ 
/*    */ import flex.messaging.log.Log;
/*    */ 
/*    */ public abstract class RPCMessage extends AbstractMessage
/*    */ {
/*    */   private static final long serialVersionUID = -1203255926746881424L;
/*    */   private String remoteUsername;
/*    */   private String remotePassword;
/*    */ 
/*    */   public String getRemoteUsername()
/*    */   {
/* 46 */     return this.remoteUsername;
/*    */   }
/*    */ 
/*    */   public void setRemoteUsername(String s)
/*    */   {
/* 51 */     this.remoteUsername = s;
/*    */   }
/*    */ 
/*    */   public String getRemotePassword()
/*    */   {
/* 56 */     return this.remotePassword;
/*    */   }
/*    */ 
/*    */   public void setRemotePassword(String s)
/*    */   {
/* 61 */     this.remotePassword = s;
/*    */   }
/*    */ 
/*    */   protected String toStringFields(int indentLevel)
/*    */   {
/* 66 */     String sp = super.toStringFields(indentLevel);
/* 67 */     String sep = getFieldSeparator(indentLevel);
/* 68 */     String s = sep + "clientId = " + (Log.isExcludedProperty("clientId") ? "** [Value Suppressed] **" : this.clientId);
/* 69 */     s = s + sep + "destination = " + (Log.isExcludedProperty("destination") ? "** [Value Suppressed] **" : this.destination);
/* 70 */     s = s + sep + "messageId = " + (Log.isExcludedProperty("messageId") ? "** [Value Suppressed] **" : this.messageId);
/* 71 */     s = s + sep + "timestamp = " + (Log.isExcludedProperty("timestamp") ? "** [Value Suppressed] **" : String.valueOf(this.timestamp));
/* 72 */     s = s + sep + "timeToLive = " + (Log.isExcludedProperty("timeToLive") ? "** [Value Suppressed] **" : String.valueOf(this.timeToLive));
/* 73 */     s = s + sep + "body = " + (Log.isExcludedProperty("clientId") ? "** [Value Suppressed] **" : new StringBuffer().append(bodyToString(this.body, indentLevel)).append(sp).toString());
/* 74 */     return s;
/*    */   }
/*    */ 
/*    */   public String logCategory()
/*    */   {
/* 79 */     return "Message.RPC";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.RPCMessage
 * JD-Core Version:    0.6.0
 */