/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.PatternConverter;
/*     */ import org.apache.log4j.helpers.PatternParser;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class PatternLayout extends Layout
/*     */ {
/*     */   public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
/*     */   public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";
/* 400 */   protected final int BUF_SIZE = 256;
/* 401 */   protected final int MAX_CAPACITY = 1024;
/*     */ 
/* 405 */   private StringBuffer sbuf = new StringBuffer(256);
/*     */   private String pattern;
/*     */   private PatternConverter head;
/*     */   private String timezone;
/*     */ 
/*     */   public PatternLayout()
/*     */   {
/* 419 */     this("%m%n");
/*     */   }
/*     */ 
/*     */   public PatternLayout(String pattern)
/*     */   {
/* 426 */     this.pattern = pattern;
/* 427 */     this.head = createPatternParser(pattern == null ? "%m%n" : pattern).parse();
/*     */   }
/*     */ 
/*     */   public void setConversionPattern(String conversionPattern)
/*     */   {
/* 438 */     this.pattern = conversionPattern;
/* 439 */     this.head = createPatternParser(conversionPattern).parse();
/*     */   }
/*     */ 
/*     */   public String getConversionPattern()
/*     */   {
/* 447 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 466 */     return true;
/*     */   }
/*     */ 
/*     */   protected PatternParser createPatternParser(String pattern)
/*     */   {
/* 477 */     return new PatternParser(pattern);
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 486 */     if (this.sbuf.capacity() > 1024)
/* 487 */       this.sbuf = new StringBuffer(256);
/*     */     else {
/* 489 */       this.sbuf.setLength(0);
/*     */     }
/*     */ 
/* 492 */     PatternConverter c = this.head;
/*     */ 
/* 494 */     while (c != null) {
/* 495 */       c.format(this.sbuf, event);
/* 496 */       c = c.next;
/*     */     }
/* 498 */     return this.sbuf.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.PatternLayout
 * JD-Core Version:    0.6.0
 */