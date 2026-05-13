/*    */ package flex.messaging.config;
/*    */ 
/*    */ import flex.messaging.FlexFactory;
/*    */ import flex.messaging.util.ClassUtil;
/*    */ 
/*    */ public class FactorySettings extends PropertiesSettings
/*    */ {
/*    */   protected String id;
/*    */   protected String className;
/*    */ 
/*    */   public FactorySettings(String id, String className)
/*    */   {
/* 37 */     this.id = id;
/* 38 */     this.className = className;
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 43 */     return this.id;
/*    */   }
/*    */ 
/*    */   public String getClassName()
/*    */   {
/* 48 */     return this.className;
/*    */   }
/*    */ 
/*    */   public FlexFactory createFactory() {
/*    */     ConfigurationException cx;
/*    */     try {
/* 55 */       Class c = ClassUtil.createClass(this.className);
/* 56 */       Object f = ClassUtil.createDefaultInstance(c, FlexFactory.class);
/* 57 */       if ((f instanceof FlexFactory))
/*    */       {
/* 59 */         FlexFactory ff = (FlexFactory)f;
/* 60 */         ff.initialize(getId(), getProperties());
/* 61 */         return ff;
/*    */       }
/*    */ 
/* 65 */       ConfigurationException cx = new ConfigurationException();
/* 66 */       cx.setMessage(11101, new Object[] { this.className });
/* 67 */       throw cx;
/*    */     }
/*    */     catch (Throwable th)
/*    */     {
/* 72 */       cx = new ConfigurationException();
/* 73 */       cx.setMessage(11102, new Object[] { this.className, th.toString() });
/* 74 */       cx.setRootCause(th);
/* 75 */     }throw cx;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.FactorySettings
 * JD-Core Version:    0.6.0
 */