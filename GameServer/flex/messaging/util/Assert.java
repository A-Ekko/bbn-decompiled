/*    */ package flex.messaging.util;
/*    */ 
/*    */ public class Assert
/*    */ {
/* 29 */   public static final boolean enableAssert = System.getProperty("assert") != null;
/*    */ 
/*    */   public static void testAssertion(boolean expr) {
/* 32 */     if ((enableAssert) && (!expr))
/* 33 */       throw new AssertionFailedError();
/*    */   }
/*    */ 
/*    */   public static void testAssertion(boolean expr, String message)
/*    */   {
/* 39 */     if ((enableAssert) && (!expr))
/* 40 */       throw new AssertionFailedError(message);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.Assert
 * JD-Core Version:    0.6.0
 */