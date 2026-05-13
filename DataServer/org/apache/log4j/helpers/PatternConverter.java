/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public abstract class PatternConverter
/*    */ {
/*    */   public PatternConverter next;
/* 29 */   int min = -1;
/* 30 */   int max = 2147483647;
/* 31 */   boolean leftAlign = false;
/*    */ 
/* 82 */   static String[] SPACES = { " ", "  ", "    ", "        ", "                ", "                                " };
/*    */ 
/*    */   protected PatternConverter()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected PatternConverter(FormattingInfo fi)
/*    */   {
/* 38 */     this.min = fi.min;
/* 39 */     this.max = fi.max;
/* 40 */     this.leftAlign = fi.leftAlign;
/*    */   }
/*    */ 
/*    */   protected abstract String convert(LoggingEvent paramLoggingEvent);
/*    */ 
/*    */   public void format(StringBuffer sbuf, LoggingEvent e)
/*    */   {
/* 56 */     String s = convert(e);
/*    */ 
/* 58 */     if (s == null) {
/* 59 */       if (0 < this.min)
/* 60 */         spacePad(sbuf, this.min);
/* 61 */       return;
/*    */     }
/*    */ 
/* 64 */     int len = s.length();
/*    */ 
/* 66 */     if (len > this.max)
/* 67 */       sbuf.append(s.substring(len - this.max));
/* 68 */     else if (len < this.min) {
/* 69 */       if (this.leftAlign) {
/* 70 */         sbuf.append(s);
/* 71 */         spacePad(sbuf, this.min - len);
/*    */       }
/*    */       else {
/* 74 */         spacePad(sbuf, this.min - len);
/* 75 */         sbuf.append(s);
/*    */       }
/*    */     }
/*    */     else
/* 79 */       sbuf.append(s);
/*    */   }
/*    */ 
/*    */   public void spacePad(StringBuffer sbuf, int length)
/*    */   {
/* 91 */     while (length >= 32) {
/* 92 */       sbuf.append(SPACES[5]);
/* 93 */       length -= 32;
/*    */     }
/*    */ 
/* 96 */     for (int i = 4; i >= 0; i--)
/* 97 */       if ((length & 1 << i) != 0)
/* 98 */         sbuf.append(SPACES[i]);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.PatternConverter
 * JD-Core Version:    0.6.0
 */