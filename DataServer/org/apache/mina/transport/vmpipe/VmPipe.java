/*    */ package org.apache.mina.transport.vmpipe;
/*    */ 
/*    */ import org.apache.mina.core.service.IoHandler;
/*    */ import org.apache.mina.core.service.IoServiceListenerSupport;
/*    */ 
/*    */ class VmPipe
/*    */ {
/*    */   private final VmPipeAcceptor acceptor;
/*    */   private final VmPipeAddress address;
/*    */   private final IoHandler handler;
/*    */   private final IoServiceListenerSupport listeners;
/*    */ 
/*    */   VmPipe(VmPipeAcceptor acceptor, VmPipeAddress address, IoHandler handler, IoServiceListenerSupport listeners)
/*    */   {
/* 42 */     this.acceptor = acceptor;
/* 43 */     this.address = address;
/* 44 */     this.handler = handler;
/* 45 */     this.listeners = listeners;
/*    */   }
/*    */ 
/*    */   public VmPipeAcceptor getAcceptor() {
/* 49 */     return this.acceptor;
/*    */   }
/*    */ 
/*    */   public VmPipeAddress getAddress() {
/* 53 */     return this.address;
/*    */   }
/*    */ 
/*    */   public IoHandler getHandler() {
/* 57 */     return this.handler;
/*    */   }
/*    */ 
/*    */   public IoServiceListenerSupport getListeners() {
/* 61 */     return this.listeners;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.vmpipe.VmPipe
 * JD-Core Version:    0.6.0
 */