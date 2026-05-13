/*    */ package flex.messaging.io.amf.translator;
/*    */ 
/*    */ import flex.messaging.MessageException;
/*    */ 
/*    */ public class TranslationException extends MessageException
/*    */ {
/*    */   static final long serialVersionUID = 3312487017261810877L;
/*    */ 
/*    */   public TranslationException(String message)
/*    */   {
/* 29 */     super(message);
/*    */   }
/*    */ 
/*    */   public TranslationException(String message, Throwable rootCause)
/*    */   {
/* 34 */     super(message);
/* 35 */     setRootCause(rootCause);
/*    */   }
/*    */ 
/*    */   public TranslationException(String message, String details)
/*    */   {
/* 40 */     super(message);
/* 41 */     setDetails(details);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.TranslationException
 * JD-Core Version:    0.6.0
 */