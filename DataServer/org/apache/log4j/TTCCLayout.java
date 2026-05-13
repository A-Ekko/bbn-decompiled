/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.DateLayout;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class TTCCLayout extends DateLayout
/*     */ {
/*  68 */   private boolean threadPrinting = true;
/*  69 */   private boolean categoryPrefixing = true;
/*  70 */   private boolean contextPrinting = true;
/*     */ 
/*  73 */   protected final StringBuffer buf = new StringBuffer(256);
/*     */ 
/*     */   public TTCCLayout()
/*     */   {
/*  83 */     setDateFormat("RELATIVE", null);
/*     */   }
/*     */ 
/*     */   public TTCCLayout(String dateFormatType)
/*     */   {
/*  96 */     setDateFormat(dateFormatType);
/*     */   }
/*     */ 
/*     */   public void setThreadPrinting(boolean threadPrinting)
/*     */   {
/* 106 */     this.threadPrinting = threadPrinting;
/*     */   }
/*     */ 
/*     */   public boolean getThreadPrinting()
/*     */   {
/* 114 */     return this.threadPrinting;
/*     */   }
/*     */ 
/*     */   public void setCategoryPrefixing(boolean categoryPrefixing)
/*     */   {
/* 123 */     this.categoryPrefixing = categoryPrefixing;
/*     */   }
/*     */ 
/*     */   public boolean getCategoryPrefixing()
/*     */   {
/* 131 */     return this.categoryPrefixing;
/*     */   }
/*     */ 
/*     */   public void setContextPrinting(boolean contextPrinting)
/*     */   {
/* 141 */     this.contextPrinting = contextPrinting;
/*     */   }
/*     */ 
/*     */   public boolean getContextPrinting()
/*     */   {
/* 149 */     return this.contextPrinting;
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 169 */     this.buf.setLength(0);
/*     */ 
/* 171 */     dateFormat(this.buf, event);
/*     */ 
/* 173 */     if (this.threadPrinting) {
/* 174 */       this.buf.append('[');
/* 175 */       this.buf.append(event.getThreadName());
/* 176 */       this.buf.append("] ");
/*     */     }
/* 178 */     this.buf.append(event.getLevel().toString());
/* 179 */     this.buf.append(' ');
/*     */ 
/* 181 */     if (this.categoryPrefixing) {
/* 182 */       this.buf.append(event.getLoggerName());
/* 183 */       this.buf.append(' ');
/*     */     }
/*     */ 
/* 186 */     if (this.contextPrinting) {
/* 187 */       String ndc = event.getNDC();
/*     */ 
/* 189 */       if (ndc != null) {
/* 190 */         this.buf.append(ndc);
/* 191 */         this.buf.append(' ');
/*     */       }
/*     */     }
/* 194 */     this.buf.append("- ");
/* 195 */     this.buf.append(event.getRenderedMessage());
/* 196 */     this.buf.append(Layout.LINE_SEP);
/* 197 */     return this.buf.toString();
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 208 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.TTCCLayout
 * JD-Core Version:    0.6.0
 */