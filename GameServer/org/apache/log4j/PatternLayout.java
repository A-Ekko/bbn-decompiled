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
/* 409 */   protected final int BUF_SIZE = 256;
/* 410 */   protected final int MAX_CAPACITY = 1024;
/*     */ 
/* 414 */   private StringBuffer sbuf = new StringBuffer(256);
/*     */   private String pattern;
/*     */   private PatternConverter head;
/*     */ 
/*     */   public PatternLayout()
/*     */   {
/* 426 */     this("%m%n");
/*     */   }
/*     */ 
/*     */   public PatternLayout(String pattern)
/*     */   {
/* 433 */     this.pattern = pattern;
/* 434 */     this.head = createPatternParser(pattern == null ? "%m%n" : pattern).parse();
/*     */   }
/*     */ 
/*     */   public void setConversionPattern(String conversionPattern)
/*     */   {
/* 445 */     this.pattern = conversionPattern;
/* 446 */     this.head = createPatternParser(conversionPattern).parse();
/*     */   }
/*     */ 
/*     */   public String getConversionPattern()
/*     */   {
/* 454 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 473 */     return true;
/*     */   }
/*     */ 
/*     */   protected PatternParser createPatternParser(String pattern)
/*     */   {
/* 484 */     return new PatternParser(pattern);
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 493 */     if (this.sbuf.capacity() > 1024)
/* 494 */       this.sbuf = new StringBuffer(256);
/*     */     else {
/* 496 */       this.sbuf.setLength(0);
/*     */     }
/*     */ 
/* 499 */     PatternConverter c = this.head;
/*     */ 
/* 501 */     while (c != null) {
/* 502 */       c.format(this.sbuf, event);
/* 503 */       c = c.next;
/*     */     }
/* 505 */     return this.sbuf.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.PatternLayout
 * JD-Core Version:    0.6.0
 */