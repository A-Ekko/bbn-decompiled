/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ public class DefaultNamingPolicy
/*    */   implements NamingPolicy
/*    */ {
/* 31 */   public static final DefaultNamingPolicy INSTANCE = new DefaultNamingPolicy();
/*    */ 
/*    */   public String getClassName(String prefix, String source, Object key, Predicate names)
/*    */   {
/* 36 */     StringBuffer sb = new StringBuffer();
/* 37 */     sb.append(prefix != null ? prefix : prefix.startsWith("java") ? "$" + prefix : "org.logicalcobwebs.cglib.empty.Object");
/*    */ 
/* 45 */     sb.append("$$");
/* 46 */     sb.append(source.substring(source.lastIndexOf('.') + 1));
/* 47 */     sb.append("ByCGLIB$$");
/* 48 */     sb.append(Integer.toHexString(key.hashCode()));
/* 49 */     String base = sb.toString();
/* 50 */     String attempt = base;
/* 51 */     int index = 2;
/* 52 */     while (names.evaluate(attempt)) {
/* 53 */       attempt = base + "_" + index++;
/*    */     }
/*    */ 
/* 56 */     return attempt;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.DefaultNamingPolicy
 * JD-Core Version:    0.6.0
 */