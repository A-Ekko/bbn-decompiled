/*    */ package org.logicalcobwebs.concurrent;
/*    */ 
/*    */ public class LinkedNode
/*    */ {
/*    */   public Object value;
/*    */   public LinkedNode next;
/*    */ 
/*    */   public LinkedNode()
/*    */   {
/*    */   }
/*    */ 
/*    */   public LinkedNode(Object x)
/*    */   {
/* 27 */     this.value = x;
/*    */   }
/*    */ 
/*    */   public LinkedNode(Object x, LinkedNode n) {
/* 31 */     this.value = x;
/* 32 */     this.next = n;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.LinkedNode
 * JD-Core Version:    0.6.0
 */