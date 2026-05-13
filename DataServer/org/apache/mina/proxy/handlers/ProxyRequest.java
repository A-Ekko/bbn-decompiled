/*    */ package org.apache.mina.proxy.handlers;
/*    */ 
/*    */ import java.net.InetSocketAddress;
/*    */ 
/*    */ public abstract class ProxyRequest
/*    */ {
/* 36 */   private InetSocketAddress endpointAddress = null;
/*    */ 
/*    */   public ProxyRequest()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ProxyRequest(InetSocketAddress endpointAddress)
/*    */   {
/* 51 */     this.endpointAddress = endpointAddress;
/*    */   }
/*    */ 
/*    */   public InetSocketAddress getEndpointAddress()
/*    */   {
/* 60 */     return this.endpointAddress;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.ProxyRequest
 * JD-Core Version:    0.6.0
 */