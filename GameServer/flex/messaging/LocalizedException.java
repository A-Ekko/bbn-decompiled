/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.messaging.util.PropertyStringResourceLoader;
/*     */ import flex.messaging.util.ResourceLoader;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class LocalizedException extends RuntimeException
/*     */ {
/*     */   protected transient ResourceLoader resourceLoader;
/*     */   protected int number;
/*     */   protected String message;
/*     */   protected String details;
/*     */   protected Throwable rootCause;
/*     */   private static final long serialVersionUID = 7980539484335065853L;
/*     */ 
/*     */   public LocalizedException()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LocalizedException(ResourceLoader resourceLoader)
/*     */   {
/*  82 */     this.resourceLoader = resourceLoader;
/*     */   }
/*     */ 
/*     */   public String getDetails()
/*     */   {
/*  92 */     return this.details;
/*     */   }
/*     */ 
/*     */   public void setDetails(String details)
/*     */   {
/* 102 */     this.details = details;
/*     */   }
/*     */ 
/*     */   public void setMessage(int number)
/*     */   {
/* 112 */     setMessage(number, null, null, null);
/*     */   }
/*     */ 
/*     */   public void setMessage(int number, Locale locale)
/*     */   {
/* 123 */     setMessage(number, null, locale, null);
/*     */   }
/*     */ 
/*     */   public void setMessage(int number, Object[] arguments)
/*     */   {
/* 135 */     setMessage(number, null, null, arguments);
/*     */   }
/*     */ 
/*     */   public void setMessage(int number, String variant)
/*     */   {
/* 146 */     setMessage(number, variant, null, null);
/*     */   }
/*     */ 
/*     */   public void setMessage(int number, String variant, Locale locale)
/*     */   {
/* 159 */     setMessage(number, variant, locale, null);
/*     */   }
/*     */ 
/*     */   public void setMessage(int number, String variant, Object[] arguments)
/*     */   {
/* 172 */     setMessage(number, variant, null, arguments);
/*     */   }
/*     */ 
/*     */   public void setMessage(int number, String variant, Locale locale, Object[] arguments)
/*     */   {
/* 187 */     setNumber(number);
/* 188 */     ResourceLoader resources = getResourceLoader();
/* 189 */     setMessage(resources.getString(generateFullKey(number, variant), locale, arguments));
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 199 */     return this.message;
/*     */   }
/*     */ 
/*     */   public void setMessage(String message)
/*     */   {
/* 209 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public void setNumber(int number)
/*     */   {
/* 219 */     this.number = number;
/*     */   }
/*     */ 
/*     */   public int getNumber()
/*     */   {
/* 229 */     return this.number;
/*     */   }
/*     */ 
/*     */   public void setDetails(int number)
/*     */   {
/* 239 */     setDetails(number, null, null, null);
/*     */   }
/*     */ 
/*     */   public void setDetails(int number, String variant)
/*     */   {
/* 250 */     setDetails(number, variant, null, null);
/*     */   }
/*     */ 
/*     */   public void setDetails(int number, String variant, Locale locale)
/*     */   {
/* 263 */     setDetails(number, variant, locale, null);
/*     */   }
/*     */ 
/*     */   public void setDetails(int number, String variant, Object[] arguments)
/*     */   {
/* 276 */     setDetails(number, variant, null, arguments);
/*     */   }
/*     */ 
/*     */   public void setDetails(int number, String variant, Locale locale, Object[] arguments)
/*     */   {
/* 292 */     setNumber(number);
/* 293 */     ResourceLoader resources = getResourceLoader();
/* 294 */     setDetails(resources.getString(generateDetailsKey(number, variant), locale, arguments));
/*     */   }
/*     */ 
/*     */   public Throwable getRootCause()
/*     */   {
/* 304 */     return this.rootCause;
/*     */   }
/*     */ 
/*     */   public void setRootCause(Throwable cause)
/*     */   {
/* 314 */     this.rootCause = cause;
/*     */   }
/*     */ 
/*     */   protected ResourceLoader getResourceLoader()
/*     */   {
/* 324 */     if (this.resourceLoader == null) {
/* 325 */       this.resourceLoader = new PropertyStringResourceLoader();
/*     */     }
/* 327 */     return this.resourceLoader;
/*     */   }
/*     */ 
/*     */   private String generateFullKey(int number, String variant)
/*     */   {
/* 341 */     return variant != null ? number + "-" + variant : String.valueOf(number);
/*     */   }
/*     */ 
/*     */   private String generateDetailsKey(int number, String variant)
/*     */   {
/* 355 */     return generateFullKey(number, variant) + "-details";
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 365 */     String result = super.toString();
/* 366 */     if (this.details != null)
/*     */     {
/* 368 */       StringBuffer buffer = new StringBuffer(result);
/* 369 */       if (!result.endsWith("."))
/*     */       {
/* 371 */         buffer.append(".");
/*     */       }
/* 373 */       buffer.append(' ').append(this.details);
/* 374 */       result = buffer.toString();
/*     */     }
/* 376 */     return result;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.LocalizedException
 * JD-Core Version:    0.6.0
 */