/*    */ package org.logicalcobwebs.concurrent;
/*    */ 
/*    */ public class DefaultChannelCapacity
/*    */ {
/*    */   public static final int INITIAL_DEFAULT_CAPACITY = 1024;
/* 30 */   private static final SynchronizedInt defaultCapacity_ = new SynchronizedInt(1024);
/*    */ 
/*    */   public static void set(int capacity)
/*    */   {
/* 40 */     if (capacity <= 0) throw new IllegalArgumentException();
/* 41 */     defaultCapacity_.set(capacity);
/*    */   }
/*    */ 
/*    */   public static int get()
/*    */   {
/* 52 */     return defaultCapacity_.get();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.DefaultChannelCapacity
 * JD-Core Version:    0.6.0
 */