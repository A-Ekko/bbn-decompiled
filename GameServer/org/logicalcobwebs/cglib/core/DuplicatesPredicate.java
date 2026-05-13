/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class DuplicatesPredicate
/*    */   implements Predicate
/*    */ {
/* 22 */   private Set unique = new HashSet();
/*    */ 
/*    */   public boolean evaluate(Object arg) {
/* 25 */     return this.unique.add(MethodWrapper.create((Method)arg));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.DuplicatesPredicate
 * JD-Core Version:    0.6.0
 */