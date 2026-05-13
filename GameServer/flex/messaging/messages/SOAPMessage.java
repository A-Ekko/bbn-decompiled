/*    */ package flex.messaging.messages;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SOAPMessage extends HTTPMessage
/*    */ {
/*    */   private static final long serialVersionUID = 3706466843618325314L;
/*    */ 
/*    */   public SOAPMessage()
/*    */   {
/* 41 */     this.contentType = "text/xml; charset=utf-8";
/* 42 */     this.method = "POST";
/*    */   }
/*    */ 
/*    */   public String getAction()
/*    */   {
/* 47 */     Object action = this.httpHeaders.get("SOAPAction");
/* 48 */     return action == null ? null : action.toString();
/*    */   }
/*    */ 
/*    */   public void setAction(String action)
/*    */   {
/* 53 */     this.httpHeaders.put("SOAPAction", action);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.SOAPMessage
 * JD-Core Version:    0.6.0
 */