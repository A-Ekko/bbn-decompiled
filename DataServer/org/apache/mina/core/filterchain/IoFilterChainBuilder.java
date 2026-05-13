/*    */ package org.apache.mina.core.filterchain;
/*    */ 
/*    */ public abstract interface IoFilterChainBuilder
/*    */ {
/* 44 */   public static final IoFilterChainBuilder NOOP = new IoFilterChainBuilder()
/*    */   {
/*    */     public void buildFilterChain(IoFilterChain chain) throws Exception {
/*    */     }
/*    */ 
/*    */     public String toString() {
/* 50 */       return "NOOP";
/*    */     }
/* 44 */   };
/*    */ 
/*    */   public abstract void buildFilterChain(IoFilterChain paramIoFilterChain)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.filterchain.IoFilterChainBuilder
 * JD-Core Version:    0.6.0
 */