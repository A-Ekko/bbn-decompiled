/*    */ package org.apache.log4j.or.sax;
/*    */ 
/*    */ import org.apache.log4j.or.ObjectRenderer;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ public class AttributesRenderer
/*    */   implements ObjectRenderer
/*    */ {
/*    */   public String doRender(Object o)
/*    */   {
/* 31 */     if ((o instanceof Attributes)) {
/* 32 */       StringBuffer sbuf = new StringBuffer();
/* 33 */       Attributes a = (Attributes)o;
/* 34 */       int len = a.getLength();
/* 35 */       boolean first = true;
/* 36 */       for (int i = 0; i < len; i++) {
/* 37 */         if (first)
/* 38 */           first = false;
/*    */         else {
/* 40 */           sbuf.append(", ");
/*    */         }
/* 42 */         sbuf.append(a.getQName(i));
/* 43 */         sbuf.append('=');
/* 44 */         sbuf.append(a.getValue(i));
/*    */       }
/* 46 */       return sbuf.toString();
/*    */     }
/* 48 */     return o.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.or.sax.AttributesRenderer
 * JD-Core Version:    0.6.0
 */