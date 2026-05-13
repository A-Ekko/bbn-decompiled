/*    */ package flex.messaging.messages;
/*    */ 
/*    */ import flex.messaging.MessageException;
/*    */ import flex.messaging.log.Log;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ErrorMessage extends AcknowledgeMessage
/*    */ {
/*    */   private static final long serialVersionUID = -9069412644250075809L;
/*    */   public String faultCode;
/*    */   public String faultString;
/*    */   public String faultDetail;
/*    */   public Object rootCause;
/*    */   public Map extendedData;
/*    */ 
/*    */   public ErrorMessage(MessageException mxe)
/*    */   {
/* 48 */     this.faultCode = mxe.getCode();
/* 49 */     this.faultString = mxe.getMessage();
/* 50 */     this.faultDetail = mxe.getDetails();
/* 51 */     if (mxe.getRootCause() != null)
/*    */     {
/* 53 */       this.rootCause = mxe.getRootCauseErrorMessage();
/*    */     }
/* 55 */     Map extendedData = mxe.getExtendedData();
/* 56 */     if (extendedData != null)
/*    */     {
/* 58 */       this.extendedData = extendedData;
/*    */     }
/*    */   }
/*    */ 
/*    */   public ErrorMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Message getSmallMessage()
/*    */   {
/* 71 */     return null;
/*    */   }
/*    */ 
/*    */   protected String toStringFields(int indentLevel)
/*    */   {
/* 76 */     String sep = getFieldSeparator(indentLevel);
/* 77 */     String s = super.toStringFields(indentLevel);
/* 78 */     s = s + sep + "code =  " + this.faultCode;
/* 79 */     s = s + sep + "message =  " + this.faultString;
/* 80 */     s = s + sep + "details =  " + this.faultDetail;
/* 81 */     s = s + sep + "rootCause =  ";
/* 82 */     if (this.rootCause == null) s = s + "null"; else
/* 83 */       s = s + this.rootCause.toString();
/* 84 */     if (Log.isExcludedProperty("body"))
/* 85 */       s = s + sep + "body = " + "** [Value Suppressed] **";
/*    */     else
/* 87 */       s = s + sep + "body =  " + bodyToString(this.body, indentLevel);
/* 88 */     s = s + sep + "extendedData =  " + bodyToString(this.extendedData, indentLevel);
/* 89 */     return s;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.ErrorMessage
 * JD-Core Version:    0.6.0
 */