/*    */ package org.apache.mina.util;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.IdentityHashMap;
/*    */ 
/*    */ public class IdentityHashSet<E> extends MapBackedSet<E>
/*    */ {
/*    */   private static final long serialVersionUID = 6948202189467167147L;
/*    */ 
/*    */   public IdentityHashSet()
/*    */   {
/* 37 */     super(new IdentityHashMap());
/*    */   }
/*    */ 
/*    */   public IdentityHashSet(int expectedMaxSize) {
/* 41 */     super(new IdentityHashMap(expectedMaxSize));
/*    */   }
/*    */ 
/*    */   public IdentityHashSet(Collection<E> c) {
/* 45 */     super(new IdentityHashMap(), c);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.IdentityHashSet
 * JD-Core Version:    0.6.0
 */