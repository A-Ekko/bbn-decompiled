/*     */ package flex.messaging.log;
/*     */ 
/*     */ import flex.messaging.util.PrettyPrinter;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public class Logger
/*     */ {
/*     */   private volatile String category;
/*     */   private final ArrayList targets;
/*     */ 
/*     */   public Logger(String category)
/*     */   {
/*  52 */     this.category = category;
/*  53 */     this.targets = new ArrayList();
/*     */   }
/*     */ 
/*     */   public String getCategory()
/*     */   {
/*  63 */     return this.category;
/*     */   }
/*     */ 
/*     */   void addTarget(Target target)
/*     */   {
/*  74 */     synchronized (this.targets)
/*     */     {
/*  76 */       if (!this.targets.contains(target))
/*  77 */         this.targets.add(target);
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeTarget(Target target)
/*     */   {
/*  88 */     synchronized (this.targets)
/*     */     {
/*  90 */       this.targets.remove(target);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void debug(String message)
/*     */   {
/* 104 */     log(2, message, null, null);
/*     */   }
/*     */ 
/*     */   public void debug(String message, Throwable t)
/*     */   {
/* 115 */     log(2, message, null, t);
/*     */   }
/*     */ 
/*     */   public void debug(String message, Object[] parameters)
/*     */   {
/* 126 */     log(2, message, parameters, null);
/*     */   }
/*     */ 
/*     */   public void debug(String message, Object[] parameters, Throwable t)
/*     */   {
/* 139 */     log(2, message, parameters, t);
/*     */   }
/*     */ 
/*     */   public void info(String message)
/*     */   {
/* 152 */     log(4, message, null, null);
/*     */   }
/*     */ 
/*     */   public void info(String message, Throwable t)
/*     */   {
/* 163 */     log(4, message, null, t);
/*     */   }
/*     */ 
/*     */   public void info(String message, Object[] parameters)
/*     */   {
/* 174 */     log(4, message, parameters, null);
/*     */   }
/*     */ 
/*     */   public void info(String message, Object[] parameters, Throwable t)
/*     */   {
/* 187 */     log(4, message, parameters, t);
/*     */   }
/*     */ 
/*     */   public void warn(String message)
/*     */   {
/* 200 */     log(6, message, null, null);
/*     */   }
/*     */ 
/*     */   public void warn(String message, Throwable t)
/*     */   {
/* 211 */     log(6, message, null, t);
/*     */   }
/*     */ 
/*     */   public void warn(String message, Object[] parameters)
/*     */   {
/* 222 */     log(6, message, parameters, null);
/*     */   }
/*     */ 
/*     */   public void warn(String message, Object[] parameters, Throwable t)
/*     */   {
/* 235 */     log(6, message, parameters, t);
/*     */   }
/*     */ 
/*     */   public void error(String message)
/*     */   {
/* 248 */     log(8, message, null, null);
/*     */   }
/*     */ 
/*     */   public void error(String message, Throwable t)
/*     */   {
/* 259 */     log(8, message, null, t);
/*     */   }
/*     */ 
/*     */   public void error(String message, Object[] parameters)
/*     */   {
/* 270 */     log(8, message, parameters, null);
/*     */   }
/*     */ 
/*     */   public void error(String message, Object[] parameters, Throwable t)
/*     */   {
/* 283 */     log(8, message, parameters, t);
/*     */   }
/*     */ 
/*     */   public void fatal(String message)
/*     */   {
/* 296 */     log(1000, message, null, null);
/*     */   }
/*     */ 
/*     */   public void fatal(String message, Throwable t)
/*     */   {
/* 307 */     log(1000, message, null, t);
/*     */   }
/*     */ 
/*     */   public void fatal(String message, Object[] parameters)
/*     */   {
/* 318 */     log(1000, message, parameters, null);
/*     */   }
/*     */ 
/*     */   public void fatal(String message, Object[] parameters, Throwable t)
/*     */   {
/* 331 */     log(1000, message, parameters, t);
/*     */   }
/*     */ 
/*     */   public void log(short level, String message, Object[] parameters, Throwable t)
/*     */   {
/* 345 */     log(level, message, parameters, t, true);
/*     */   }
/*     */ 
/*     */   public void log(short level, String message, Object[] parameters, Throwable t, boolean verifyLevel)
/*     */   {
/* 360 */     if ((this.targets.size() > 0) && ((!verifyLevel) || (level >= Log.getTargetLevel())))
/*     */     {
/* 362 */       if (parameters != null)
/*     */       {
/* 364 */         PrettyPrinter prettyPrinter = Log.getPrettyPrinter();
/*     */ 
/* 367 */         for (int i = 0; i < parameters.length; i++)
/*     */         {
/* 369 */           String replacement = parameters[i] != null ? prettyPrinter.prettify(parameters[i]) : "null";
/*     */ 
/* 373 */           message = StringUtils.substitute(message, "{" + i + "}", replacement);
/*     */         }
/*     */       }
/* 376 */       LogEvent event = new LogEvent(this, message, level, t);
/*     */       Iterator iter;
/* 378 */       synchronized (this.targets)
/*     */       {
/* 380 */         for (iter = this.targets.iterator(); iter.hasNext(); )
/*     */         {
/* 382 */           Target tgt = (Target)iter.next();
/* 383 */           if ((!verifyLevel) || (level >= tgt.getLevel()))
/* 384 */             tgt.logEvent(event);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.Logger
 * JD-Core Version:    0.6.0
 */