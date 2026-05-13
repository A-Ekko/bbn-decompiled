/*    */ package org.apache.log4j;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class SimpleLayout extends Layout
/*    */ {
/* 29 */   StringBuffer sbuf = new StringBuffer(128);
/*    */ 
/*    */   public void activateOptions()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String format(LoggingEvent event)
/*    */   {
/* 51 */     this.sbuf.setLength(0);
/* 52 */     this.sbuf.append(event.getLevel().toString());
/* 53 */     this.sbuf.append(" - ");
/* 54 */     this.sbuf.append(event.getRenderedMessage());
/* 55 */     this.sbuf.append(Layout.LINE_SEP);
/* 56 */     return this.sbuf.toString();
/*    */   }
/*    */ 
/*    */   public boolean ignoresThrowable()
/*    */   {
/* 67 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.SimpleLayout
 * JD-Core Version:    0.6.0
 */