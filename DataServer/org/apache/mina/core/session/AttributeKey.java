/*    */ package org.apache.mina.core.session;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public final class AttributeKey
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -583377473376683096L;
/*    */   private final String name;
/*    */ 
/*    */   public AttributeKey(Class<?> source, String name)
/*    */   {
/* 46 */     this.name = (source.getName() + '.' + name + '@' + Integer.toHexString(hashCode()));
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 54 */     return this.name;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.AttributeKey
 * JD-Core Version:    0.6.0
 */