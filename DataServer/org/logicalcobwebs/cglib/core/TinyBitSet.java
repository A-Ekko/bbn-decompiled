/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ public class TinyBitSet
/*    */ {
/* 19 */   private static int[] T = new int[256];
/* 20 */   private int value = 0;
/*    */ 
/*    */   private static int gcount(int x) {
/* 23 */     int c = 0;
/* 24 */     while (x != 0) {
/* 25 */       c++;
/* 26 */       x &= x - 1;
/*    */     }
/* 28 */     return c;
/*    */   }
/*    */ 
/*    */   private static int topbit(int i)
/*    */   {
/* 39 */     for (int j = 0; i != 0; i ^= j) {
/* 40 */       j = i & -i;
/*    */     }
/* 42 */     return j;
/*    */   }
/*    */ 
/*    */   private static int log2(int i) {
/* 46 */     int j = 0;
/* 47 */     for (j = 0; i != 0; i >>= 1) {
/* 48 */       j++;
/*    */     }
/* 50 */     return j;
/*    */   }
/*    */ 
/*    */   public int length() {
/* 54 */     return log2(topbit(this.value));
/*    */   }
/*    */ 
/*    */   public int cardinality() {
/* 58 */     int w = this.value;
/* 59 */     int c = 0;
/* 60 */     while (w != 0) {
/* 61 */       c += T[(w & 0xFF)];
/* 62 */       w >>= 8;
/*    */     }
/* 64 */     return c;
/*    */   }
/*    */ 
/*    */   public boolean get(int index) {
/* 68 */     return (this.value & 1 << index) != 0;
/*    */   }
/*    */ 
/*    */   public void set(int index) {
/* 72 */     this.value |= 1 << index;
/*    */   }
/*    */ 
/*    */   public void clear(int index) {
/* 76 */     this.value &= (1 << index ^ 0xFFFFFFFF);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 32 */     for (int j = 0; j < 256; j++)
/* 33 */       T[j] = gcount(j);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.TinyBitSet
 * JD-Core Version:    0.6.0
 */