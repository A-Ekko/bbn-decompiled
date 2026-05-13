/*    */ package flex.messaging.io.amfx;
/*    */ 
/*    */ import flex.messaging.io.PropertyProxyRegistry;
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import flex.messaging.io.amf.Amf3Output;
/*    */ import flex.messaging.io.amf.Java15Amf3Output;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class Java15AmfxOutput extends AmfxOutput
/*    */ {
/*    */   public Java15AmfxOutput(SerializationContext context)
/*    */   {
/* 14 */     super(context);
/*    */   }
/*    */ 
/*    */   public void writeObject(Object o) throws IOException
/*    */   {
/* 19 */     if ((o != null) && ((o instanceof Enum)) && (PropertyProxyRegistry.getRegistry().getProxy(o.getClass()) == null))
/*    */     {
/* 21 */       Enum enumValue = (Enum)o;
/* 22 */       writeString(enumValue.name());
/*    */     }
/*    */     else
/*    */     {
/* 26 */       super.writeObject(o);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected Amf3Output createAMF3Output()
/*    */   {
/* 32 */     return new Java15Amf3Output(this.context);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amfx.Java15AmfxOutput
 * JD-Core Version:    0.6.0
 */