/*    */ package org.apache.mina.util;
/*    */ 
/*    */ public abstract class LazyInitializer<V>
/*    */ {
/*    */   private V value;
/*    */ 
/*    */   public abstract V init();
/*    */ 
/*    */   public V get()
/*    */   {
/* 51 */     if (this.value == null) {
/* 52 */       this.value = init();
/*    */     }
/*    */ 
/* 55 */     return this.value;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.LazyInitializer
 * JD-Core Version:    0.6.0
 */