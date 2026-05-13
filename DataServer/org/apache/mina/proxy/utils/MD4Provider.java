/*    */ package org.apache.mina.proxy.utils;
/*    */ 
/*    */ import java.security.Provider;
/*    */ 
/*    */ public class MD4Provider extends Provider
/*    */ {
/*    */   private static final long serialVersionUID = -1616816866935565456L;
/*    */   public static final String PROVIDER_NAME = "MINA";
/*    */   public static final double VERSION = 1.0D;
/*    */   public static final String INFO = "MINA MD4 Provider v1.0";
/*    */ 
/*    */   public MD4Provider()
/*    */   {
/* 58 */     super("MINA", 1.0D, "MINA MD4 Provider v1.0");
/* 59 */     put("MessageDigest.MD4", MD4.class.getName());
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.utils.MD4Provider
 * JD-Core Version:    0.6.0
 */