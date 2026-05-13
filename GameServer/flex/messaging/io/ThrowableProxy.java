/*    */ package flex.messaging.io;
/*    */ 
/*    */ public class ThrowableProxy extends BeanProxy
/*    */ {
/*    */   static final long serialVersionUID = 6363249716988887262L;
/*    */ 
/*    */   public ThrowableProxy()
/*    */   {
/* 34 */     this.includeReadOnly = true;
/*    */   }
/*    */ 
/*    */   public ThrowableProxy(Throwable defaultInstance)
/*    */   {
/* 39 */     super(defaultInstance);
/* 40 */     this.includeReadOnly = true;
/*    */   }
/*    */ 
/*    */   public Object clone()
/*    */   {
/* 45 */     ThrowableProxy proxy = new ThrowableProxy();
/* 46 */     proxy.setCloneFieldsFrom(this);
/* 47 */     return proxy;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.ThrowableProxy
 * JD-Core Version:    0.6.0
 */