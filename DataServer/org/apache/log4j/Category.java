/*      */ package org.apache.log4j;
/*      */ 
/*      */ import java.text.MessageFormat;
/*      */ import java.util.Enumeration;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import org.apache.log4j.helpers.AppenderAttachableImpl;
/*      */ import org.apache.log4j.helpers.NullEnumeration;
/*      */ import org.apache.log4j.spi.AppenderAttachable;
/*      */ import org.apache.log4j.spi.LoggerRepository;
/*      */ import org.apache.log4j.spi.LoggingEvent;
/*      */ 
/*      */ public class Category
/*      */   implements AppenderAttachable
/*      */ {
/*      */   protected String name;
/*      */   protected volatile Level level;
/*      */   protected volatile Category parent;
/*   99 */   private static final String FQCN = Category.class.getName();
/*      */   protected ResourceBundle resourceBundle;
/*      */   protected LoggerRepository repository;
/*      */   AppenderAttachableImpl aai;
/*  116 */   protected boolean additive = true;
/*      */ 
/*      */   protected Category(String name)
/*      */   {
/*  129 */     this.name = name;
/*      */   }
/*      */ 
/*      */   public synchronized void addAppender(Appender newAppender)
/*      */   {
/*  142 */     if (this.aai == null) {
/*  143 */       this.aai = new AppenderAttachableImpl();
/*      */     }
/*  145 */     this.aai.addAppender(newAppender);
/*  146 */     this.repository.fireAddAppenderEvent(this, newAppender);
/*      */   }
/*      */ 
/*      */   public void assertLog(boolean assertion, String msg)
/*      */   {
/*  164 */     if (!assertion)
/*  165 */       error(msg);
/*      */   }
/*      */ 
/*      */   public void callAppenders(LoggingEvent event)
/*      */   {
/*  181 */     int writes = 0;
/*      */ 
/*  183 */     for (Category c = this; c != null; c = c.parent)
/*      */     {
/*  185 */       synchronized (c) {
/*  186 */         if (c.aai != null) {
/*  187 */           writes += c.aai.appendLoopOnAppenders(event);
/*      */         }
/*  189 */         if (!c.additive) {
/*  190 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  195 */     if (writes == 0)
/*  196 */       this.repository.emitNoAppenderWarning(this);
/*      */   }
/*      */ 
/*      */   synchronized void closeNestedAppenders()
/*      */   {
/*  207 */     Enumeration enum = getAllAppenders();
/*  208 */     if (enum != null)
/*  209 */       while (enum.hasMoreElements()) {
/*  210 */         Appender a = (Appender)enum.nextElement();
/*  211 */         if ((a instanceof AppenderAttachable))
/*  212 */           a.close();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void debug(Object message)
/*      */   {
/*  238 */     if (this.repository.isDisabled(10000))
/*  239 */       return;
/*  240 */     if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel()))
/*  241 */       forcedLog(FQCN, Level.DEBUG, message, null);
/*      */   }
/*      */ 
/*      */   public void debug(Object message, Throwable t)
/*      */   {
/*  257 */     if (this.repository.isDisabled(10000))
/*  258 */       return;
/*  259 */     if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel()))
/*  260 */       forcedLog(FQCN, Level.DEBUG, message, t);
/*      */   }
/*      */ 
/*      */   public void error(Object message)
/*      */   {
/*  283 */     if (this.repository.isDisabled(40000))
/*  284 */       return;
/*  285 */     if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel()))
/*  286 */       forcedLog(FQCN, Level.ERROR, message, null);
/*      */   }
/*      */ 
/*      */   public void error(Object message, Throwable t)
/*      */   {
/*  300 */     if (this.repository.isDisabled(40000))
/*  301 */       return;
/*  302 */     if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel()))
/*  303 */       forcedLog(FQCN, Level.ERROR, message, t);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Logger exists(String name)
/*      */   {
/*  319 */     return LogManager.exists(name);
/*      */   }
/*      */ 
/*      */   public void fatal(Object message)
/*      */   {
/*  343 */     if (this.repository.isDisabled(50000))
/*  344 */       return;
/*  345 */     if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel()))
/*  346 */       forcedLog(FQCN, Level.FATAL, message, null);
/*      */   }
/*      */ 
/*      */   public void fatal(Object message, Throwable t)
/*      */   {
/*  360 */     if (this.repository.isDisabled(50000))
/*  361 */       return;
/*  362 */     if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel()))
/*  363 */       forcedLog(FQCN, Level.FATAL, message, t);
/*      */   }
/*      */ 
/*      */   protected void forcedLog(String fqcn, Priority level, Object message, Throwable t)
/*      */   {
/*  372 */     callAppenders(new LoggingEvent(fqcn, this, level, message, t));
/*      */   }
/*      */ 
/*      */   public boolean getAdditivity()
/*      */   {
/*  381 */     return this.additive;
/*      */   }
/*      */ 
/*      */   public synchronized Enumeration getAllAppenders()
/*      */   {
/*  393 */     if (this.aai == null) {
/*  394 */       return NullEnumeration.getInstance();
/*      */     }
/*  396 */     return this.aai.getAllAppenders();
/*      */   }
/*      */ 
/*      */   public synchronized Appender getAppender(String name)
/*      */   {
/*  407 */     if ((this.aai == null) || (name == null)) {
/*  408 */       return null;
/*      */     }
/*  410 */     return this.aai.getAppender(name);
/*      */   }
/*      */ 
/*      */   public Level getEffectiveLevel()
/*      */   {
/*  423 */     for (Category c = this; c != null; c = c.parent) {
/*  424 */       if (c.level != null)
/*  425 */         return c.level;
/*      */     }
/*  427 */     return null;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Priority getChainedPriority()
/*      */   {
/*  437 */     for (Category c = this; c != null; c = c.parent) {
/*  438 */       if (c.level != null)
/*  439 */         return c.level;
/*      */     }
/*  441 */     return null;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Enumeration getCurrentCategories()
/*      */   {
/*  457 */     return LogManager.getCurrentLoggers();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static LoggerRepository getDefaultHierarchy()
/*      */   {
/*  471 */     return LogManager.getLoggerRepository();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public LoggerRepository getHierarchy()
/*      */   {
/*  483 */     return this.repository;
/*      */   }
/*      */ 
/*      */   public LoggerRepository getLoggerRepository()
/*      */   {
/*  493 */     return this.repository;
/*      */   }
/*      */ 
/*      */   public static Category getInstance(String name)
/*      */   {
/*  514 */     return LogManager.getLogger(name);
/*      */   }
/*      */ 
/*      */   public static Category getInstance(Class clazz)
/*      */   {
/*  530 */     return LogManager.getLogger(clazz);
/*      */   }
/*      */ 
/*      */   public final String getName()
/*      */   {
/*  539 */     return this.name;
/*      */   }
/*      */ 
/*      */   public final Category getParent()
/*      */   {
/*  554 */     return this.parent;
/*      */   }
/*      */ 
/*      */   public final Level getLevel()
/*      */   {
/*  566 */     return this.level;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final Level getPriority()
/*      */   {
/*  575 */     return this.level;
/*      */   }
/*      */ 
/*      */   public static final Category getRoot()
/*      */   {
/*  595 */     return LogManager.getRootLogger();
/*      */   }
/*      */ 
/*      */   public ResourceBundle getResourceBundle()
/*      */   {
/*  611 */     for (Category c = this; c != null; c = c.parent) {
/*  612 */       if (c.resourceBundle != null) {
/*  613 */         return c.resourceBundle;
/*      */       }
/*      */     }
/*  616 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getResourceBundleString(String key)
/*      */   {
/*  629 */     ResourceBundle rb = getResourceBundle();
/*      */ 
/*  632 */     if (rb == null)
/*      */     {
/*  637 */       return null;
/*      */     }
/*      */     try
/*      */     {
/*  641 */       return rb.getString(key);
/*      */     }
/*      */     catch (MissingResourceException mre) {
/*  644 */       error("No resource is associated with key \"" + key + "\".");
/*  645 */     }return null;
/*      */   }
/*      */ 
/*      */   public void info(Object message)
/*      */   {
/*  671 */     if (this.repository.isDisabled(20000))
/*  672 */       return;
/*  673 */     if (Level.INFO.isGreaterOrEqual(getEffectiveLevel()))
/*  674 */       forcedLog(FQCN, Level.INFO, message, null);
/*      */   }
/*      */ 
/*      */   public void info(Object message, Throwable t)
/*      */   {
/*  688 */     if (this.repository.isDisabled(20000))
/*  689 */       return;
/*  690 */     if (Level.INFO.isGreaterOrEqual(getEffectiveLevel()))
/*  691 */       forcedLog(FQCN, Level.INFO, message, t);
/*      */   }
/*      */ 
/*      */   public boolean isAttached(Appender appender)
/*      */   {
/*  699 */     if ((appender == null) || (this.aai == null)) {
/*  700 */       return false;
/*      */     }
/*  702 */     return this.aai.isAttached(appender);
/*      */   }
/*      */ 
/*      */   public boolean isDebugEnabled()
/*      */   {
/*  742 */     if (this.repository.isDisabled(10000))
/*  743 */       return false;
/*  744 */     return Level.DEBUG.isGreaterOrEqual(getEffectiveLevel());
/*      */   }
/*      */ 
/*      */   public boolean isEnabledFor(Priority level)
/*      */   {
/*  757 */     if (this.repository.isDisabled(level.level))
/*  758 */       return false;
/*  759 */     return level.isGreaterOrEqual(getEffectiveLevel());
/*      */   }
/*      */ 
/*      */   public boolean isInfoEnabled()
/*      */   {
/*  771 */     if (this.repository.isDisabled(20000))
/*  772 */       return false;
/*  773 */     return Level.INFO.isGreaterOrEqual(getEffectiveLevel());
/*      */   }
/*      */ 
/*      */   public void l7dlog(Priority priority, String key, Throwable t)
/*      */   {
/*  787 */     if (this.repository.isDisabled(priority.level)) {
/*  788 */       return;
/*      */     }
/*  790 */     if (priority.isGreaterOrEqual(getEffectiveLevel())) {
/*  791 */       String msg = getResourceBundleString(key);
/*      */ 
/*  794 */       if (msg == null) {
/*  795 */         msg = key;
/*      */       }
/*  797 */       forcedLog(FQCN, priority, msg, t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void l7dlog(Priority priority, String key, Object[] params, Throwable t)
/*      */   {
/*  811 */     if (this.repository.isDisabled(priority.level)) {
/*  812 */       return;
/*      */     }
/*  814 */     if (priority.isGreaterOrEqual(getEffectiveLevel())) {
/*  815 */       String pattern = getResourceBundleString(key);
/*      */       String msg;
/*  817 */       if (pattern == null)
/*  818 */         msg = key;
/*      */       else
/*  820 */         msg = MessageFormat.format(pattern, params);
/*  821 */       forcedLog(FQCN, priority, msg, t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void log(Priority priority, Object message, Throwable t)
/*      */   {
/*  830 */     if (this.repository.isDisabled(priority.level)) {
/*  831 */       return;
/*      */     }
/*  833 */     if (priority.isGreaterOrEqual(getEffectiveLevel()))
/*  834 */       forcedLog(FQCN, priority, message, t);
/*      */   }
/*      */ 
/*      */   public void log(Priority priority, Object message)
/*      */   {
/*  842 */     if (this.repository.isDisabled(priority.level)) {
/*  843 */       return;
/*      */     }
/*  845 */     if (priority.isGreaterOrEqual(getEffectiveLevel()))
/*  846 */       forcedLog(FQCN, priority, message, null);
/*      */   }
/*      */ 
/*      */   public void log(String callerFQCN, Priority level, Object message, Throwable t)
/*      */   {
/*  860 */     if (this.repository.isDisabled(level.level)) {
/*  861 */       return;
/*      */     }
/*  863 */     if (level.isGreaterOrEqual(getEffectiveLevel()))
/*  864 */       forcedLog(callerFQCN, level, message, t);
/*      */   }
/*      */ 
/*      */   public synchronized void removeAllAppenders()
/*      */   {
/*  878 */     if (this.aai != null) {
/*  879 */       this.aai.removeAllAppenders();
/*  880 */       this.aai = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void removeAppender(Appender appender)
/*      */   {
/*  892 */     if ((appender == null) || (this.aai == null))
/*  893 */       return;
/*  894 */     this.aai.removeAppender(appender);
/*      */   }
/*      */ 
/*      */   public synchronized void removeAppender(String name)
/*      */   {
/*  905 */     if ((name == null) || (this.aai == null)) return;
/*  906 */     this.aai.removeAppender(name);
/*      */   }
/*      */ 
/*      */   public void setAdditivity(boolean additive)
/*      */   {
/*  915 */     this.additive = additive;
/*      */   }
/*      */ 
/*      */   final void setHierarchy(LoggerRepository repository)
/*      */   {
/*  923 */     this.repository = repository;
/*      */   }
/*      */ 
/*      */   public void setLevel(Level level)
/*      */   {
/*  939 */     this.level = level;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setPriority(Priority priority)
/*      */   {
/*  952 */     this.level = ((Level)priority);
/*      */   }
/*      */ 
/*      */   public void setResourceBundle(ResourceBundle bundle)
/*      */   {
/*  965 */     this.resourceBundle = bundle;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static void shutdown()
/*      */   {
/*  990 */     LogManager.shutdown();
/*      */   }
/*      */ 
/*      */   public void warn(Object message)
/*      */   {
/* 1015 */     if (this.repository.isDisabled(30000)) {
/* 1016 */       return;
/*      */     }
/* 1018 */     if (Level.WARN.isGreaterOrEqual(getEffectiveLevel()))
/* 1019 */       forcedLog(FQCN, Level.WARN, message, null);
/*      */   }
/*      */ 
/*      */   public void warn(Object message, Throwable t)
/*      */   {
/* 1033 */     if (this.repository.isDisabled(30000))
/* 1034 */       return;
/* 1035 */     if (Level.WARN.isGreaterOrEqual(getEffectiveLevel()))
/* 1036 */       forcedLog(FQCN, Level.WARN, message, t);
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.Category
 * JD-Core Version:    0.6.0
 */