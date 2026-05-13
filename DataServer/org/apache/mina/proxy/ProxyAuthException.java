/*    */ package org.apache.mina.proxy;
/*    */ 
/*    */ import javax.security.sasl.SaslException;
/*    */ 
/*    */ public class ProxyAuthException extends SaslException
/*    */ {
/*    */   private static final long serialVersionUID = -6511596809517532988L;
/*    */ 
/*    */   public ProxyAuthException(String message)
/*    */   {
/* 39 */     super(message);
/*    */   }
/*    */ 
/*    */   public ProxyAuthException(String message, Throwable ex)
/*    */   {
/* 46 */     super(message, ex);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.ProxyAuthException
 * JD-Core Version:    0.6.0
 */