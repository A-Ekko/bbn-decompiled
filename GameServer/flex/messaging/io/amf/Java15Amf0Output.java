/*    */ package flex.messaging.io.amf;
/*    */ 
/*    */ import flex.messaging.io.PropertyProxyRegistry;
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class Java15Amf0Output extends Amf0Output
/*    */ {
/*    */   public Java15Amf0Output(SerializationContext context)
/*    */   {
/* 16 */     super(context);
/*    */   }
/*    */ 
/*    */   protected void createAMF3Output()
/*    */   {
/* 27 */     this.avmPlusOutput = new Java15Amf3Output(this.context);
/* 28 */     this.avmPlusOutput.setOutputStream(this.out);
/* 29 */     this.avmPlusOutput.setDebugTrace(this.trace);
/*    */   }
/*    */ 
/*    */   public void writeObject(Object o) throws IOException
/*    */   {
/* 34 */     if ((o != null) && ((o instanceof Enum)) && (PropertyProxyRegistry.getRegistry().getProxy(o.getClass()) == null))
/*    */     {
/* 36 */       Enum enumValue = (Enum)o;
/* 37 */       writeAMFString(enumValue.name());
/*    */     }
/*    */     else
/*    */     {
/* 41 */       super.writeObject(o);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.Java15Amf0Output
 * JD-Core Version:    0.6.0
 */