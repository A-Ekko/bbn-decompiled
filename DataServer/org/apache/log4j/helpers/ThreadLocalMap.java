/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ 
/*    */ public final class ThreadLocalMap extends InheritableThreadLocal
/*    */ {
/*    */   public final Object childValue(Object parentValue)
/*    */   {
/* 26 */     Hashtable ht = (Hashtable)parentValue;
/* 27 */     if (ht != null) {
/* 28 */       return ht.clone();
/*    */     }
/* 30 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.ThreadLocalMap
 * JD-Core Version:    0.6.0
 */