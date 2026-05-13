/*    */ package flex.messaging.io;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ClassAliasRegistry
/*    */ {
/* 33 */   private Map aliasRegistry = new HashMap();
/* 34 */   private static final ClassAliasRegistry registry = new ClassAliasRegistry();
/*    */ 
/*    */   public static ClassAliasRegistry getRegistry()
/*    */   {
/* 48 */     return registry;
/*    */   }
/*    */ 
/*    */   public String getClassName(String alias)
/*    */   {
/* 60 */     return (String)this.aliasRegistry.get(alias);
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 68 */     synchronized (this.aliasRegistry)
/*    */     {
/* 70 */       this.aliasRegistry.clear();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void registerAlias(String alias, String className)
/*    */   {
/* 82 */     synchronized (this.aliasRegistry)
/*    */     {
/* 84 */       this.aliasRegistry.put(alias, className);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void unregisterAlias(String alias)
/*    */   {
/* 95 */     synchronized (this.aliasRegistry)
/*    */     {
/* 97 */       this.aliasRegistry.remove(alias);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.ClassAliasRegistry
 * JD-Core Version:    0.6.0
 */