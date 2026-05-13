/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import java.lang.reflect.Member;
/*    */ 
/*    */ public class RejectModifierPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private int rejectMask;
/*    */ 
/*    */   public RejectModifierPredicate(int rejectMask)
/*    */   {
/* 24 */     this.rejectMask = rejectMask;
/*    */   }
/*    */ 
/*    */   public boolean evaluate(Object arg) {
/* 28 */     return (((Member)arg).getModifiers() & this.rejectMask) == 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.RejectModifierPredicate
 * JD-Core Version:    0.6.0
 */