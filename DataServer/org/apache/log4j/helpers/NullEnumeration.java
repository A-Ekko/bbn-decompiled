/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ public class NullEnumeration
/*    */   implements Enumeration
/*    */ {
/* 22 */   private static final NullEnumeration instance = new NullEnumeration();
/*    */ 
/*    */   public static NullEnumeration getInstance()
/*    */   {
/* 29 */     return instance;
/*    */   }
/*    */ 
/*    */   public boolean hasMoreElements()
/*    */   {
/* 34 */     return false;
/*    */   }
/*    */ 
/*    */   public Object nextElement()
/*    */   {
/* 39 */     throw new NoSuchElementException();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.NullEnumeration
 * JD-Core Version:    0.6.0
 */