/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.Filter;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class StringMatchFilter extends Filter
/*     */ {
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String STRING_TO_MATCH_OPTION = "StringToMatch";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";
/*  55 */   boolean acceptOnMatch = true;
/*     */   String stringToMatch;
/*     */ 
/*     */   /** @deprecated */
/*     */   public String[] getOptionStrings()
/*     */   {
/*  64 */     return new String[] { "StringToMatch", "AcceptOnMatch" };
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setOption(String key, String value)
/*     */   {
/*  74 */     if (key.equalsIgnoreCase("StringToMatch"))
/*  75 */       this.stringToMatch = value;
/*  76 */     else if (key.equalsIgnoreCase("AcceptOnMatch"))
/*  77 */       this.acceptOnMatch = OptionConverter.toBoolean(value, this.acceptOnMatch);
/*     */   }
/*     */ 
/*     */   public void setStringToMatch(String s)
/*     */   {
/*  83 */     this.stringToMatch = s;
/*     */   }
/*     */ 
/*     */   public String getStringToMatch()
/*     */   {
/*  88 */     return this.stringToMatch;
/*     */   }
/*     */ 
/*     */   public void setAcceptOnMatch(boolean acceptOnMatch)
/*     */   {
/*  93 */     this.acceptOnMatch = acceptOnMatch;
/*     */   }
/*     */ 
/*     */   public boolean getAcceptOnMatch()
/*     */   {
/*  98 */     return this.acceptOnMatch;
/*     */   }
/*     */ 
/*     */   public int decide(LoggingEvent event)
/*     */   {
/* 106 */     String msg = event.getRenderedMessage();
/*     */ 
/* 108 */     if ((msg == null) || (this.stringToMatch == null)) {
/* 109 */       return 0;
/*     */     }
/*     */ 
/* 112 */     if (msg.indexOf(this.stringToMatch) == -1) {
/* 113 */       return 0;
/*     */     }
/* 115 */     if (this.acceptOnMatch) {
/* 116 */       return 1;
/*     */     }
/* 118 */     return -1;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.StringMatchFilter
 * JD-Core Version:    0.6.0
 */