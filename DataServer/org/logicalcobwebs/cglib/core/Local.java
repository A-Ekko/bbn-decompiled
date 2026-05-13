/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ 
/*    */ public class Local
/*    */ {
/*    */   private Type type;
/*    */   private int index;
/*    */ 
/*    */   public Local(int index, Type type)
/*    */   {
/* 26 */     this.type = type;
/* 27 */     this.index = index;
/*    */   }
/*    */ 
/*    */   public int getIndex() {
/* 31 */     return this.index;
/*    */   }
/*    */ 
/*    */   public Type getType() {
/* 35 */     return this.type;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.Local
 * JD-Core Version:    0.6.0
 */