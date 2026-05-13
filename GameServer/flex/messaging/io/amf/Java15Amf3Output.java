/*    */ package flex.messaging.io.amf;
/*    */ 
/*    */ import flex.messaging.io.PropertyProxyRegistry;
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class Java15Amf3Output extends Amf3Output
/*    */ {
/*    */   public Java15Amf3Output(SerializationContext context)
/*    */   {
/* 12 */     super(context);
/*    */   }
/*    */ 
/*    */   public void writeObject(Object o)
/*    */     throws IOException
/*    */   {
/* 19 */     if ((o != null) && ((o instanceof Enum)) && (PropertyProxyRegistry.getRegistry().getProxy(o.getClass()) == null))
/*    */     {
/* 21 */       Enum enumValue = (Enum)o;
/* 22 */       writeAMFString(enumValue.name());
/*    */     }
/*    */     else
/*    */     {
/* 26 */       super.writeObject(o);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.Java15Amf3Output
 * JD-Core Version:    0.6.0
 */