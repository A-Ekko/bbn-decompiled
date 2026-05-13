/*    */ package flex.messaging.messages;
/*    */ 
/*    */ import flex.messaging.io.ClassAlias;
/*    */ import java.io.Externalizable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectOutput;
/*    */ 
/*    */ public class AcknowledgeMessageExt extends AcknowledgeMessage
/*    */   implements Externalizable, ClassAlias
/*    */ {
/*    */   private static final long serialVersionUID = -8764729006642310394L;
/*    */   public static final String CLASS_ALIAS = "DSK";
/*    */   private AcknowledgeMessage _message;
/*    */ 
/*    */   public AcknowledgeMessageExt()
/*    */   {
/* 38 */     this(null);
/*    */   }
/*    */ 
/*    */   public AcknowledgeMessageExt(AcknowledgeMessage message)
/*    */   {
/* 44 */     this._message = message;
/*    */   }
/*    */ 
/*    */   public String getAlias()
/*    */   {
/* 49 */     return "DSK";
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput output) throws IOException
/*    */   {
/* 54 */     if (this._message != null)
/* 55 */       this._message.writeExternal(output);
/*    */     else
/* 57 */       super.writeExternal(output);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.AcknowledgeMessageExt
 * JD-Core Version:    0.6.0
 */