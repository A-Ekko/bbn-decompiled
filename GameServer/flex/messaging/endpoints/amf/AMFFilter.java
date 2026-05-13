/*    */ package flex.messaging.endpoints.amf;
/*    */ 
/*    */ import flex.messaging.io.amf.ActionContext;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class AMFFilter
/*    */ {
/*    */   protected AMFFilter next;
/*    */ 
/*    */   public void setNext(AMFFilter next)
/*    */   {
/* 42 */     this.next = next;
/*    */   }
/*    */ 
/*    */   public AMFFilter getNext()
/*    */   {
/* 47 */     return this.next;
/*    */   }
/*    */ 
/*    */   public abstract void invoke(ActionContext paramActionContext)
/*    */     throws IOException;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.amf.AMFFilter
 * JD-Core Version:    0.6.0
 */