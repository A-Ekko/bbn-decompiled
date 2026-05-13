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
/*  44 */   protected ErrorHandler errorHandler = new OnlyOnceErrorHandler();
/*     */   protected Filter headFilter;
/*     */   protected Filter tailFilter;
/*  55 */   protected boolean closed = false;
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addFilter(Filter newFilter)
/*     */   {
/*  73 */     if (this.headFilter == null) {
/*  74 */       this.headFilter = (this.tailFilter = newFilter);
/*     */     } else {
/*  76 */       this.tailFilter.next = newFilter;
/*  77 */       this.tailFilter = newFilter;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void append(LoggingEvent paramLoggingEvent);
/*     */ 
/*     */   public void clearFilters()
/*     */   {
/*  99 */     this.headFilter = (this.tailFilter = null);
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 111 */     if (this.closed) {
/* 112 */       return;
/*     */     }
/* 114 */     LogLog.debug("Finalizing appender named [" + this.name + "].");
/* 115 */     close();
/*     */   }
/*     */ 
/*     */   public ErrorHandler getErrorHandler()
/*     */   {
/* 126 */     return this.errorHandler;
/*     */   }
/*     */ 
/*     */   public Filter getFilter()
/*     */   {
/* 137 */     return this.headFilter;
/*     */   }
/*     */ 
/*     */   public final Filter getFirstFilter()
/*     */   {
/* 149 */     return this.headFilter;
/*     */   }
/*     */ 
/*     */   public Layout getLayout()
/*     */   {
/* 157 */     return this.layout;
/*     */   }
/*     */ 
/*     */   public final String getName()
/*     */   {
/* 167 */     return this.name;
/*     */   }
/*     */ 
/*     */   public Priority getThreshold()
/*     */   {
/* 177 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   public boolean isAsSevereAsThreshold(Priority priority)
/*     */   {
/* 189 */     return (this.threshold == null) || (priority.isGreaterOrEqual(this.threshold));
/*     */   }
/*     */ 
/*     */   public synchronized void doAppend(LoggingEvent event)
/*     */   {
/* 201 */     if (this.closed) {
/* 202 */       LogLog.error("Attempted to append to closed appender named [" + this.name + "].");
/* 203 */       return;
/*     */     }
/*     */ 
/* 206 */     if (!isAsSevereAsThreshold(event.getLevel())) {
/* 207 */       return;
/*     */     }
/*     */ 
/* 210 */     Filter f = this.headFilter;
/*     */ 
/* 213 */     while (f != null) {
/* 214 */       switch (f.decide(event)) { case -1:
/* 215 */         return;
/*     */       case 1:
/* 216 */         break;
/*     */       case 0:
/* 217 */         f = f.next;
/*     */       }
/*     */     }
/*     */ 
/* 221 */     append(event);
/*     */   }
/*     */ 
/*     */   public synchronized void setErrorHandler(ErrorHandler eh)
/*     */   {
/* 231 */     if (eh == null)
/*     */     {
/* 234 */       LogLog.warn("You have tried to set a null error-handler.");
/*     */     }
/* 236 */     else this.errorHandler = eh;
/*     */   }
/*     */ 
/*     */   public void setLayout(Layout layout)
/*     */   {
/* 248 */     this.layout = layout;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 257 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public void setThreshold(Priority threshold)
/*     */   {
/* 272 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */   public abstract boolean requiresLayout();
/*     */ 
/*     */   public abstract void close();
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.AppenderSkeleton
 * JD-Core Version:    0.6.0
 */