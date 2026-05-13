/*    */ package flex.messaging.messages;
/*    */ 
/*    */ import flex.messaging.io.ClassAlias;
/*    */ import java.io.Externalizable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectOutput;
/*    */ 
/*    */ public class AsyncMessageExt extends AsyncMessage
/*    */   implements Externalizable, ClassAlias
/*    */ {
/*    */   private static final long serialVersionUID = -5371460213241777011L;
/*    */   public static final String CLASS_ALIAS = "DSA";
/*    */   private AsyncMessage _message;
/*    */ 
/*    */   public AsyncMessageExt()
/*    */   {
/*    */   }
/*    */ 
/*    */   public AsyncMessageExt(AsyncMessage message)
/*    */   {
/* 43 */     this._message = message;
/*    */   }
/*    */ 
/*    */   public String getAlias()
/*    */   {
/* 48 */     return "DSA";
/*    */   }
/*    */ 
/*    */   public void writeExternal(ObjectOutput output) throws IOException
/*    */   {
/* 53 */     if (this._message != null)
/* 54 */       this._message.writeExternal(output);
/*    */     else
/* 56 */       super.writeExternal(output);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.AsyncMessageExt
 * JD-Core Version:    0.6.0
 */