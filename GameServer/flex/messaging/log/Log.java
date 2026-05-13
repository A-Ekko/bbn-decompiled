/*     */ package flex.messaging.log;
/*     */ 
/*     */ import flex.messaging.LocalizedException;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.util.PrettyPrinter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Log
/*     */ {
/*     */   public static final String INVALID_CHARS = "[]~$^&\\/(){}<>+=`!#%?,:;'\"@";
/*     */   private static final int INVALID_TARGET = 10013;
/*     */   private static final int INVALID_CATEGORY = 10014;
/*     */   private static final int INVALID_CATEGORY_CHARS = 10015;
/*     */   private static Log log;
/*     */   private static PrettyPrinter prettyPrinter;
/*  47 */   private static String prettyPrinterClass = "BasicPrettyPrinter";
/*     */ 
/*  49 */   private static final HashSet excludedProperties = new HashSet();
/*     */   public static final String VALUE_SUPRESSED = "** [Value Suppressed] **";
/*     */   private volatile short targetLevel;
/*     */   private final Map loggers;
/*     */   private final List targets;
/*     */   private final Map targetMap;
/*  57 */   private static final Object staticLock = new Object();
/*     */ 
/*     */   private Log()
/*     */   {
/*  71 */     this.targetLevel = 2000;
/*  72 */     this.loggers = new HashMap();
/*  73 */     this.targets = new ArrayList();
/*  74 */     this.targetMap = new LinkedHashMap();
/*     */   }
/*     */ 
/*     */   public static Log createLog()
/*     */   {
/*  86 */     synchronized (staticLock)
/*     */     {
/*  88 */       if (log == null) {
/*  89 */         log = new Log();
/*     */       }
/*  91 */       return log;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized void initialize(String id, ConfigMap properties)
/*     */   {
/* 109 */     String value = properties.getPropertyAsString("pretty-printer", null);
/*     */ 
/* 111 */     if (value != null)
/*     */     {
/* 113 */       prettyPrinterClass = value;
/*     */     }
/*     */ 
/* 118 */     ConfigMap excludeMap = properties.getPropertyAsMap("exclude-properties", null);
/*     */ 
/* 120 */     if (excludeMap != null)
/*     */     {
/* 122 */       if (excludeMap.getPropertyAsList("property", null) != null)
/* 123 */         excludedProperties.addAll(excludeMap.getPropertyAsList("property", null));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isFatal()
/*     */   {
/* 138 */     return log != null;
/*     */   }
/*     */ 
/*     */   public static boolean isError()
/*     */   {
/* 146 */     return log != null;
/*     */   }
/*     */ 
/*     */   public static boolean isWarn()
/*     */   {
/* 154 */     return log != null;
/*     */   }
/*     */ 
/*     */   public static boolean isInfo()
/*     */   {
/* 162 */     return log != null;
/*     */   }
/*     */ 
/*     */   public static boolean isDebug()
/*     */   {
/* 170 */     return log != null;
/*     */   }
/*     */ 
/*     */   public static boolean isExcludedProperty(String property)
/*     */   {
/* 178 */     return (!excludedProperties.isEmpty()) && (excludedProperties.contains(property));
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String category)
/*     */   {
/* 190 */     if (log != null)
/*     */     {
/* 192 */       return getLogger(log, category);
/*     */     }
/*     */ 
/* 197 */     return new Logger(category);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Log log, String category)
/*     */   {
/* 206 */     checkCategory(category);
/*     */ 
/* 208 */     synchronized (staticLock)
/*     */     {
/* 210 */       Logger result = (Logger)log.loggers.get(category);
/* 211 */       if (result == null)
/*     */       {
/* 213 */         result = new Logger(category);
/*     */ 
/* 216 */         for (Iterator iter = log.targets.iterator(); iter.hasNext(); )
/*     */         {
/* 218 */           Target target = (Target)iter.next();
/* 219 */           if (categoryMatchInFilterList(category, target.getFilters())) {
/* 220 */             target.addLogger(result);
/*     */           }
/*     */         }
/* 223 */         log.loggers.put(category, result);
/*     */       }
/* 225 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static List getTargets()
/*     */   {
/* 235 */     if (log != null)
/*     */     {
/*     */       List currentTargets;
/* 239 */       synchronized (staticLock)
/*     */       {
/* 241 */         currentTargets = Collections.unmodifiableList(new ArrayList(log.targets));
/*     */       }
/* 243 */       return currentTargets;
/*     */     }
/* 245 */     return null;
/*     */   }
/*     */ 
/*     */   public static Map getTargetMap()
/*     */   {
/* 253 */     if (log != null)
/*     */     {
/*     */       Map currentTargets;
/* 256 */       synchronized (staticLock)
/*     */       {
/* 258 */         currentTargets = new LinkedHashMap(log.targetMap);
/*     */       }
/* 260 */       return currentTargets;
/*     */     }
/* 262 */     return null;
/*     */   }
/*     */ 
/*     */   public static Target getTarget(String searchId)
/*     */   {
/* 271 */     if (log != null)
/*     */     {
/* 273 */       synchronized (staticLock)
/*     */       {
/* 275 */         return (Target)log.targetMap.get(searchId);
/*     */       }
/*     */     }
/*     */ 
/* 279 */     return null;
/*     */   }
/*     */ 
/*     */   public String[] getLoggers()
/*     */   {
/*     */     String[] currentCategories;
/* 288 */     if (log != null)
/*     */     {
/* 291 */       synchronized (staticLock)
/*     */       {
/* 293 */         Object[] currentCategoryObjects = this.loggers.keySet().toArray();
/* 294 */         String[] currentCategories = new String[currentCategoryObjects.length];
/* 295 */         for (int i = 0; i < currentCategoryObjects.length; i++)
/*     */         {
/* 297 */           currentCategories[i] = ((String)(String)currentCategoryObjects[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 303 */       currentCategories = new String[0];
/*     */     }
/*     */ 
/* 306 */     return currentCategories;
/*     */   }
/*     */ 
/*     */   public static void addTarget(Target target)
/*     */   {
/* 316 */     if (log != null)
/*     */     {
/* 318 */       if (target != null)
/*     */       {
/* 320 */         synchronized (staticLock)
/*     */         {
/* 322 */           List filters = target.getFilters();
/*     */ 
/* 326 */           Iterator it = log.loggers.keySet().iterator();
/* 327 */           while (it.hasNext())
/*     */           {
/* 329 */             String key = (String)it.next();
/*     */ 
/* 331 */             if (categoryMatchInFilterList(key, filters)) {
/* 332 */               target.addLogger((Logger)log.loggers.get(key));
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 337 */           if (!log.targets.contains(target)) {
/* 338 */             log.targets.add(target);
/*     */           }
/* 340 */           if (!log.targetMap.containsValue(target))
/*     */           {
/* 342 */             String name = target.getClass().getName();
/*     */ 
/* 344 */             if (name.indexOf(".") > -1)
/*     */             {
/* 346 */               String[] classes = name.split("\\.");
/* 347 */               name = classes[(classes.length - 1)];
/*     */             }
/*     */ 
/* 350 */             log.targetMap.put(new String(name + log.targetMap.size()), target);
/*     */           }
/*     */ 
/* 354 */           short targetLevel = target.getLevel();
/* 355 */           if (log.targetLevel == 2000)
/* 356 */             log.targetLevel = targetLevel;
/* 357 */           else if (targetLevel < log.targetLevel)
/*     */           {
/* 359 */             log.targetLevel = targetLevel;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 366 */         LocalizedException ex = new LocalizedException();
/* 367 */         ex.setMessage(10013);
/* 368 */         throw ex;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void removeTarget(Target target)
/*     */   {
/* 380 */     if (log != null)
/*     */     {
/* 382 */       if (target != null)
/*     */       {
/* 384 */         synchronized (staticLock)
/*     */         {
/* 387 */           List filters = target.getFilters();
/* 388 */           Iterator it = log.loggers.keySet().iterator();
/* 389 */           while (it.hasNext())
/*     */           {
/* 391 */             String key = (String)it.next();
/*     */ 
/* 393 */             if (categoryMatchInFilterList(key, filters)) {
/* 394 */               target.removeLogger((Logger)log.loggers.get(key));
/*     */             }
/*     */           }
/* 397 */           log.targets.remove(target);
/* 398 */           resetTargetLevel();
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 404 */         LocalizedException ex = new LocalizedException();
/* 405 */         ex.setMessage(10013);
/* 406 */         throw ex;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static synchronized void reset()
/*     */   {
/* 423 */     flush();
/*     */   }
/*     */ 
/*     */   public static void flush()
/*     */   {
/* 431 */     if (log != null)
/*     */     {
/* 433 */       log.loggers.clear();
/* 434 */       log.targets.clear();
/* 435 */       log.targetLevel = 2000;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static short readLevel(String l)
/*     */   {
/* 444 */     short lvl = 8;
/* 445 */     if ((l != null) && (l.length() > 0))
/*     */     {
/* 447 */       l = l.trim().toLowerCase();
/* 448 */       char c = l.charAt(0);
/* 449 */       switch (c)
/*     */       {
/*     */       case 'n':
/* 452 */         lvl = 2000;
/* 453 */         break;
/*     */       case 'e':
/* 455 */         lvl = 8;
/* 456 */         break;
/*     */       case 'w':
/* 458 */         lvl = 6;
/* 459 */         break;
/*     */       case 'i':
/* 461 */         lvl = 4;
/* 462 */         break;
/*     */       case 'd':
/* 464 */         lvl = 2;
/* 465 */         break;
/*     */       case 'a':
/* 467 */         lvl = 0;
/* 468 */         break;
/*     */       default:
/* 470 */         lvl = 8;
/*     */       }
/*     */     }
/*     */ 
/* 474 */     return lvl;
/*     */   }
/*     */ 
/*     */   public static boolean hasIllegalCharacters(String value)
/*     */   {
/* 489 */     char[] chars = value.toCharArray();
/* 490 */     for (int i = 0; i < chars.length; i++)
/*     */     {
/* 492 */       char c = chars[i];
/* 493 */       if ("[]~$^&\\/(){}<>+=`!#%?,:;'\"@".indexOf(c) != -1)
/*     */       {
/* 495 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 499 */     return false;
/*     */   }
/*     */ 
/*     */   public static PrettyPrinter getPrettyPrinter()
/*     */   {
/* 508 */     if ((prettyPrinter == null) || (!prettyPrinter.getClass().getName().equals(prettyPrinterClass)))
/*     */     {
/*     */       try
/*     */       {
/* 513 */         Class c = Class.forName(prettyPrinterClass);
/* 514 */         prettyPrinter = (PrettyPrinter)c.newInstance();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */     }
/* 520 */     return (PrettyPrinter)prettyPrinter.clone();
/*     */   }
/*     */ 
/*     */   public static void setPrettyPrinterClass(String value)
/*     */   {
/* 531 */     prettyPrinterClass = value;
/*     */   }
/*     */ 
/*     */   static short getTargetLevel()
/*     */   {
/* 542 */     return log == null ? 2000 : log.targetLevel;
/*     */   }
/*     */ 
/*     */   static void resetTargetLevel()
/*     */   {
/* 547 */     if (log != null)
/*     */     {
/* 549 */       synchronized (staticLock)
/*     */       {
/* 551 */         short maxTargetLevel = 2000;
/* 552 */         for (Iterator iter = log.targets.iterator(); iter.hasNext(); )
/*     */         {
/* 554 */           short targetLevel = ((Target)iter.next()).getLevel();
/* 555 */           if ((maxTargetLevel == 2000) || (targetLevel < maxTargetLevel))
/*     */           {
/* 557 */             maxTargetLevel = targetLevel;
/*     */           }
/*     */         }
/* 560 */         log.targetLevel = maxTargetLevel;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void processTargetFilterAdd(Target target, String filter)
/*     */   {
/* 567 */     if (log != null)
/*     */     {
/* 569 */       synchronized (staticLock)
/*     */       {
/* 571 */         List filters = new ArrayList();
/* 572 */         filters.add(filter);
/*     */ 
/* 576 */         Iterator it = log.loggers.keySet().iterator();
/* 577 */         while (it.hasNext())
/*     */         {
/* 579 */           String key = (String)it.next();
/*     */ 
/* 581 */           if (categoryMatchInFilterList(key, filters))
/* 582 */             target.addLogger((Logger)log.loggers.get(key));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static void processTargetFilterRemove(Target target, String filter)
/*     */   {
/* 590 */     if (log != null)
/*     */     {
/* 592 */       synchronized (staticLock)
/*     */       {
/* 595 */         List filters = new ArrayList();
/* 596 */         filters.add(filter);
/* 597 */         Iterator it = log.loggers.keySet().iterator();
/* 598 */         while (it.hasNext())
/*     */         {
/* 600 */           String key = (String)it.next();
/*     */ 
/* 602 */           if (categoryMatchInFilterList(key, filters))
/* 603 */             target.removeLogger((Logger)log.loggers.get(key));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean categoryMatchInFilterList(String category, List filters)
/*     */   {
/* 621 */     if (filters == null) {
/* 622 */       return false;
/*     */     }
/*     */ 
/* 625 */     for (int i = 0; i < filters.size(); i++)
/*     */     {
/* 627 */       String filter = (String)filters.get(i);
/*     */ 
/* 630 */       if (checkFilterToCategory(filter, category))
/* 631 */         return true;
/*     */     }
/* 633 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean checkFilterToCategory(String filter, String category)
/*     */   {
/* 643 */     int index = -1;
/* 644 */     index = filter.indexOf("*");
/*     */ 
/* 646 */     if (index == 0)
/*     */     {
/* 648 */       return true;
/*     */     }
/* 650 */     if (index < 0)
/*     */     {
/* 652 */       if (category.equals(filter))
/*     */       {
/* 654 */         return true;
/*     */       }
/*     */ 
/*     */     }
/* 659 */     else if ((category.length() >= index) && (category.substring(0, index).equals(filter.substring(0, index))))
/*     */     {
/* 661 */       return true;
/*     */     }
/*     */ 
/* 665 */     return false;
/*     */   }
/*     */ 
/*     */   private static void checkCategory(String category)
/*     */   {
/* 677 */     if ((category == null) || (category.length() == 0))
/*     */     {
/* 680 */       LocalizedException ex = new LocalizedException();
/* 681 */       ex.setMessage(10014);
/* 682 */       throw ex;
/*     */     }
/*     */ 
/* 685 */     if ((hasIllegalCharacters(category)) || (category.indexOf("*") != -1))
/*     */     {
/* 688 */       LocalizedException ex = new LocalizedException();
/* 689 */       ex.setMessage(10015, new Object[] { "[]~$^&\\/(){}<>+=`!#%?,:;'\"@" });
/* 690 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 699 */     log = null;
/* 700 */     prettyPrinter = null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.Log
 * JD-Core Version:    0.6.0
 */