/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OnlyOnceErrorHandler;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.Filter;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ 
/*     */ public abstract class AppenderSkeleton
/*     */   implements Appender, OptionHandler
/*     */ {
/*     */   protected Layout layout;
/*     */   protected String name;
/*     */   protected Priority threshold;
/*  54 */   protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();
/*     */   protected Filter headFilter;
/*     */   protected Filter tailFilter;
/*  65 */   protected boolean closed = false;
/*     */ 
/*     */   public AppenderSkeleton()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected AppenderSkeleton(boolean isActive)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addFilter(Filter newFilter)
/*     */   {
/* 103 */     if (this.headFilter == null) {
/* 104 */       this.headFilter = (this.tailFilter = newFilter);
/*     */     } else {
/* 106 */       this.tailFilter.setNext(newFilter);
/* 107 */       this.tailFilter = newFilter;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void append(LoggingEvent paramLoggingEvent);
/*     */ 
/*     */   public void clearFilters()
/*     */   {
/* 129 */     this.headFilter = (this.tailFilter = null);
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 141 */     if (this.closed) {
/* 142 */       return;
/*     */     }
/* 144 */     LogLog.debug("Finalizing appender named [" + this.name + "].");
/* 145 */     close();
/*     */   }
/*     */ 
/*     */   public ErrorHandler getErrorHandler()
/*     */   {
/* 156 */     return this.errorHandler;
/*     */   }
/*     */ 
/*     */   public Filter getFilter()
/*     */   {
/* 167 */     return this.headFilter;
/*     */   }
/*     */ 
/*     */   public final Filter getFirstFilter()
/*     */   {
/* 179 */     return this.headFilter;
/*     */   }
/*     */ 
/*     */   public Layout getLayout()
/*     */   {
/* 187 */     return this.layout;
/*     */   }
/*     */ 
/*     */   public final String getName()
/*     */   {
/* 197 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Priority getThreshold()
/*     */   {
/* 207 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   public boolean isAsSevereAsThreshold(Priority priority)
/*     */   {
/* 219 */     return (this.threshold == null) || (priority.isGreaterOrEqual(this.threshold));
/*     */   }
/*     */ 
/*     */   public synchronized void doAppend(LoggingEvent event)
/*     */   {
/* 231 */     if (this.closed) {
/* 232 */       LogLog.error("Attempted to append to closed appender named [" + this.name + "].");
/* 233 */       return;
/*     */     }
/*     */ 
/* 236 */     if (!isAsSevereAsThreshold(event.getLevel())) {
/* 237 */       return;
/*     */     }
/*     */ 
/* 240 */     Filter f = this.headFilter;
/*     */ 
/* 243 */     while (f != null) {
/* 244 */       switch (f.decide(event)) { case -1:
/* 245 */         return;
/*     */       case 1:
/* 246 */         break;
/*     */       case 0:
/* 247 */         f = f.getNext();
/*     */       }
/*     */     }
/*     */ 
/* 251 */     append(event);
/*     */   }
/*     */ 
/*     */   public synchronized void setErrorHandler(ErrorHandler eh)
/*     */   {
/* 261 */     if (eh == null)
/*     */     {
/* 264 */       LogLog.warn("You have tried to set a null error-handler.");
/*     */     }
/* 266 */     else this.errorHandler = eh;
/*     */   }
/*     */ 
/*     */   public void setLayout(Layout layout)
/*     */   {
/* 278 */     this.layout = layout;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 287 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public void setThreshold(Priority threshold)
/*     */   {
/* 302 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */   public abstract boolean requiresLayout();
/*     */ 
/*     */   public abstract void close();
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.AppenderSkeleton
 * JD-Core Version:    0.6.0
 */