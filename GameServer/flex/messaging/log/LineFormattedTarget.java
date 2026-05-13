/*     */ package flex.messaging.log;
/*     */ 
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.util.ExceptionUtil;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class LineFormattedTarget extends AbstractTarget
/*     */ {
/*     */   protected boolean includeDate;
/*     */   protected boolean includeTime;
/*     */   protected boolean includeLevel;
/*     */   protected boolean includeCategory;
/*  54 */   protected String prefix = null;
/*     */ 
/*  60 */   protected DateFormat dateFormater = new SimpleDateFormat("MM/dd/yyyy");
/*     */ 
/*  66 */   protected DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/*  96 */     super.initialize(id, properties);
/*     */ 
/*  98 */     this.includeTime = properties.getPropertyAsBoolean("includeTime", false);
/*  99 */     this.includeDate = properties.getPropertyAsBoolean("includeDate", false);
/* 100 */     this.includeCategory = properties.getPropertyAsBoolean("includeCategory", false);
/* 101 */     this.includeLevel = properties.getPropertyAsBoolean("includeLevel", false);
/* 102 */     this.prefix = properties.getPropertyAsString("prefix", null);
/*     */   }
/*     */ 
/*     */   public boolean isIncludeCategory()
/*     */   {
/* 118 */     return this.includeCategory;
/*     */   }
/*     */ 
/*     */   public void setIncludeCategory(boolean includeCategory)
/*     */   {
/* 128 */     this.includeCategory = includeCategory;
/*     */   }
/*     */ 
/*     */   public boolean isIncludeDate()
/*     */   {
/* 138 */     return this.includeDate;
/*     */   }
/*     */ 
/*     */   public void setIncludeDate(boolean includeDate)
/*     */   {
/* 148 */     this.includeDate = includeDate;
/*     */   }
/*     */ 
/*     */   public boolean isIncludeLevel()
/*     */   {
/* 158 */     return this.includeLevel;
/*     */   }
/*     */ 
/*     */   public void setIncludeLevel(boolean includeLevel)
/*     */   {
/* 168 */     this.includeLevel = includeLevel;
/*     */   }
/*     */ 
/*     */   public boolean isIncludeTime()
/*     */   {
/* 178 */     return this.includeTime;
/*     */   }
/*     */ 
/*     */   public void setIncludeTime(boolean includeTime)
/*     */   {
/* 188 */     this.includeTime = includeTime;
/*     */   }
/*     */ 
/*     */   public String getPrefix()
/*     */   {
/* 198 */     return this.prefix;
/*     */   }
/*     */ 
/*     */   public void setPrefix(String prefix)
/*     */   {
/* 208 */     this.prefix = prefix;
/*     */   }
/*     */ 
/*     */   public void logEvent(LogEvent event)
/*     */   {
/* 220 */     String pre = "";
/* 221 */     if (this.prefix != null)
/*     */     {
/* 223 */       pre = this.prefix;
/*     */     }
/*     */ 
/* 226 */     String date = "";
/* 227 */     if ((this.includeDate) || (this.includeTime))
/*     */     {
/* 229 */       StringBuffer buffer = new StringBuffer();
/* 230 */       Date d = new Date();
/* 231 */       if (this.includeDate)
/*     */       {
/* 233 */         buffer.append(this.dateFormater.format(d));
/* 234 */         buffer.append(" ");
/*     */       }
/* 236 */       if (this.includeTime)
/*     */       {
/* 238 */         buffer.append(this.timeFormatter.format(d));
/* 239 */         buffer.append(" ");
/*     */       }
/* 241 */       date = buffer.toString();
/*     */     }
/*     */ 
/* 244 */     String cat = this.includeCategory ? "[" + event.logger.getCategory() + "] " : "";
/*     */ 
/* 246 */     String level = "";
/* 247 */     if (this.includeLevel)
/*     */     {
/* 249 */       StringBuffer buffer = new StringBuffer();
/* 250 */       buffer.append("[");
/* 251 */       buffer.append(LogEvent.getLevelString(event.level));
/* 252 */       buffer.append("]");
/* 253 */       buffer.append(" ");
/* 254 */       level = buffer.toString();
/*     */     }
/* 256 */     StringBuffer result = new StringBuffer(pre);
/* 257 */     result.append(date).append(level).append(cat).append(event.message);
/*     */ 
/* 259 */     if (event.throwable != null) {
/* 260 */       result.append(StringUtils.NEWLINE).append(ExceptionUtil.toString(event.throwable));
/*     */     }
/* 262 */     internalLog(result.toString());
/*     */   }
/*     */ 
/*     */   protected void internalLog(String message)
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.LineFormattedTarget
 * JD-Core Version:    0.6.0
 */