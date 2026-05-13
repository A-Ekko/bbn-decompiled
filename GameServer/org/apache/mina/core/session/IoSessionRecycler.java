/*    */ package org.apache.mina.core.session;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ 
/*    */ public abstract interface IoSessionRecycler
/*    */ {
/* 39 */   public static final IoSessionRecycler NOOP = new IoSessionRecycler()
/*    */   {
/*    */     public void put(IoSession session) {
/*    */     }
/*    */ 
/*    */     public IoSession recycle(SocketAddress localAddress, SocketAddress remoteAddress) {
/* 45 */       return null;
/*    */     }
/*    */ 
/*    */     public void remove(IoSession session)
/*    */     {
/*    */     }
/* 39 */   };
/*    */ 
/*    */   public abstract void put(IoSession paramIoSession);
/*    */ 
/*    */   public abstract IoSession recycle(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2);
/*    */ 
/*    */   public abstract void remove(IoSession paramIoSession);
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.session.IoSessionRecycler
 * JD-Core Version:    0.6.0
 */