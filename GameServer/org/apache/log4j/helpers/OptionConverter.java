/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ import org.apache.log4j.spi.Configurator;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ 
/*     */ public class OptionConverter
/*     */ {
/*  40 */   static String DELIM_START = "${";
/*  41 */   static char DELIM_STOP = '}';
/*  42 */   static int DELIM_START_LEN = 2;
/*  43 */   static int DELIM_STOP_LEN = 1;
/*     */ 
/*     */   public static String[] concatanateArrays(String[] l, String[] r)
/*     */   {
/*  51 */     int len = l.length + r.length;
/*  52 */     String[] a = new String[len];
/*     */ 
/*  54 */     System.arraycopy(l, 0, a, 0, l.length);
/*  55 */     System.arraycopy(r, 0, a, l.length, r.length);
/*     */ 
/*  57 */     return a;
/*     */   }
/*     */ 
/*     */   public static String convertSpecialChars(String s)
/*     */   {
/*  64 */     int len = s.length();
/*  65 */     StringBuffer sbuf = new StringBuffer(len);
/*     */ 
/*  67 */     int i = 0;
/*  68 */     while (i < len) {
/*  69 */       char c = s.charAt(i++);
/*  70 */       if (c == '\\') {
/*  71 */         c = s.charAt(i++);
/*  72 */         if (c == 'n') c = '\n';
/*  73 */         else if (c == 'r') c = '\r';
/*  74 */         else if (c == 't') c = '\t';
/*  75 */         else if (c == 'f') c = '\f';
/*  76 */         else if (c == '\b') c = '\b';
/*  77 */         else if (c == '"') c = '"';
/*  78 */         else if (c == '\'') c = '\'';
/*  79 */         else if (c == '\\') c = '\\';
/*     */       }
/*  81 */       sbuf.append(c);
/*     */     }
/*  83 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public static String getSystemProperty(String key, String def)
/*     */   {
/*     */     try
/*     */     {
/* 101 */       return System.getProperty(key, def);
/*     */     } catch (Throwable e) {
/* 103 */       LogLog.debug("Was not allowed to read system property \"" + key + "\".");
/* 104 */     }return def;
/*     */   }
/*     */ 
/*     */   public static Object instantiateByKey(Properties props, String key, Class superClass, Object defaultValue)
/*     */   {
/* 115 */     String className = findAndSubst(key, props);
/* 116 */     if (className == null) {
/* 117 */       LogLog.error("Could not find value for key " + key);
/* 118 */       return defaultValue;
/*     */     }
/*     */ 
/* 121 */     return instantiateByClassName(className.trim(), superClass, defaultValue);
/*     */   }
/*     */ 
/*     */   public static boolean toBoolean(String value, boolean dEfault)
/*     */   {
/* 135 */     if (value == null)
/* 136 */       return dEfault;
/* 137 */     String trimmedVal = value.trim();
/* 138 */     if ("true".equalsIgnoreCase(trimmedVal))
/* 139 */       return true;
/* 140 */     if ("false".equalsIgnoreCase(trimmedVal))
/* 141 */       return false;
/* 142 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static int toInt(String value, int dEfault)
/*     */   {
/* 148 */     if (value != null) {
/* 149 */       String s = value.trim();
/*     */       try {
/* 151 */         return Integer.valueOf(s).intValue();
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 154 */         LogLog.error("[" + s + "] is not in proper int form.");
/* 155 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 158 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String value, Level defaultValue)
/*     */   {
/* 184 */     if (value == null) {
/* 185 */       return defaultValue;
/*     */     }
/* 187 */     value = value.trim();
/*     */ 
/* 189 */     int hashIndex = value.indexOf('#');
/* 190 */     if (hashIndex == -1) {
/* 191 */       if ("NULL".equalsIgnoreCase(value)) {
/* 192 */         return null;
/*     */       }
/*     */ 
/* 195 */       return Level.toLevel(value, defaultValue);
/*     */     }
/*     */ 
/* 199 */     Level result = defaultValue;
/*     */ 
/* 201 */     String clazz = value.substring(hashIndex + 1);
/* 202 */     String levelName = value.substring(0, hashIndex);
/*     */ 
/* 205 */     if ("NULL".equalsIgnoreCase(levelName)) {
/* 206 */       return null;
/*     */     }
/*     */ 
/* 209 */     LogLog.debug("toLevel:class=[" + clazz + "]" + ":pri=[" + levelName + "]");
/*     */     try
/*     */     {
/* 213 */       Class customLevel = Loader.loadClass(clazz);
/*     */ 
/* 217 */       Class[] paramTypes = { String.class, Level.class };
/*     */ 
/* 220 */       Method toLevelMethod = customLevel.getMethod("toLevel", paramTypes);
/*     */ 
/* 224 */       Object[] params = { levelName, defaultValue };
/* 225 */       Object o = toLevelMethod.invoke(null, params);
/*     */ 
/* 227 */       result = (Level)o;
/*     */     } catch (ClassNotFoundException e) {
/* 229 */       LogLog.warn("custom level class [" + clazz + "] not found.");
/*     */     } catch (NoSuchMethodException e) {
/* 231 */       LogLog.warn("custom level class [" + clazz + "]" + " does not have a class function toLevel(String, Level)", e);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 234 */       LogLog.warn("custom level class [" + clazz + "]" + " could not be instantiated", e);
/*     */     }
/*     */     catch (ClassCastException e) {
/* 237 */       LogLog.warn("class [" + clazz + "] is not a subclass of org.apache.log4j.Level", e);
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 240 */       LogLog.warn("class [" + clazz + "] cannot be instantiated due to access restrictions", e);
/*     */     }
/*     */     catch (Exception e) {
/* 243 */       LogLog.warn("class [" + clazz + "], level [" + levelName + "] conversion failed.", e);
/*     */     }
/*     */ 
/* 246 */     return result;
/*     */   }
/*     */ 
/*     */   public static long toFileSize(String value, long dEfault)
/*     */   {
/* 252 */     if (value == null) {
/* 253 */       return dEfault;
/*     */     }
/* 255 */     String s = value.trim().toUpperCase();
/* 256 */     long multiplier = 1L;
/*     */     int index;
/* 259 */     if ((index = s.indexOf("KB")) != -1) {
/* 260 */       multiplier = 1024L;
/* 261 */       s = s.substring(0, index);
/*     */     }
/* 263 */     else if ((index = s.indexOf("MB")) != -1) {
/* 264 */       multiplier = 1048576L;
/* 265 */       s = s.substring(0, index);
/*     */     }
/* 267 */     else if ((index = s.indexOf("GB")) != -1) {
/* 268 */       multiplier = 1073741824L;
/* 269 */       s = s.substring(0, index);
/*     */     }
/* 271 */     if (s != null) {
/*     */       try {
/* 273 */         return Long.valueOf(s).longValue() * multiplier;
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 276 */         LogLog.error("[" + s + "] is not in proper int form.");
/* 277 */         LogLog.error("[" + value + "] not in expected format.", e);
/*     */       }
/*     */     }
/* 280 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static String findAndSubst(String key, Properties props)
/*     */   {
/* 292 */     String value = props.getProperty(key);
/* 293 */     if (value == null)
/* 294 */       return null;
/*     */     try
/*     */     {
/* 297 */       return substVars(value, props);
/*     */     } catch (IllegalArgumentException e) {
/* 299 */       LogLog.error("Bad option value [" + value + "].", e);
/* 300 */     }return value;
/*     */   }
/*     */ 
/*     */   public static Object instantiateByClassName(String className, Class superClass, Object defaultValue)
/*     */   {
/* 318 */     if (className != null) {
/*     */       try {
/* 320 */         Class classObj = Loader.loadClass(className);
/* 321 */         if (!superClass.isAssignableFrom(classObj)) {
/* 322 */           LogLog.error("A \"" + className + "\" object is not assignable to a \"" + superClass.getName() + "\" variable.");
/*     */ 
/* 324 */           LogLog.error("The class \"" + superClass.getName() + "\" was loaded by ");
/* 325 */           LogLog.error("[" + superClass.getClassLoader() + "] whereas object of type ");
/* 326 */           LogLog.error("\"" + classObj.getName() + "\" was loaded by [" + classObj.getClassLoader() + "].");
/*     */ 
/* 328 */           return defaultValue;
/*     */         }
/* 330 */         return classObj.newInstance();
/*     */       } catch (Exception e) {
/* 332 */         LogLog.error("Could not instantiate class [" + className + "].", e);
/*     */       }
/*     */     }
/* 335 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static String substVars(String val, Properties props)
/*     */     throws IllegalArgumentException
/*     */   {
/* 379 */     StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 381 */     int i = 0;
/*     */     while (true)
/*     */     {
/* 385 */       int j = val.indexOf(DELIM_START, i);
/* 386 */       if (j == -1)
/*     */       {
/* 388 */         if (i == 0) {
/* 389 */           return val;
/*     */         }
/* 391 */         sbuf.append(val.substring(i, val.length()));
/* 392 */         return sbuf.toString();
/*     */       }
/*     */ 
/* 395 */       sbuf.append(val.substring(i, j));
/* 396 */       int k = val.indexOf(DELIM_STOP, j);
/* 397 */       if (k == -1) {
/* 398 */         throw new IllegalArgumentException('"' + val + "\" has no closing brace. Opening brace at position " + j + '.');
/*     */       }
/*     */ 
/* 402 */       j += DELIM_START_LEN;
/* 403 */       String key = val.substring(j, k);
/*     */ 
/* 405 */       String replacement = getSystemProperty(key, null);
/*     */ 
/* 407 */       if ((replacement == null) && (props != null)) {
/* 408 */         replacement = props.getProperty(key);
/*     */       }
/*     */ 
/* 411 */       if (replacement != null)
/*     */       {
/* 417 */         String recursiveReplacement = substVars(replacement, props);
/* 418 */         sbuf.append(recursiveReplacement);
/*     */       }
/* 420 */       i = k + DELIM_STOP_LEN;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void selectAndConfigure(URL url, String clazz, LoggerRepository hierarchy)
/*     */   {
/* 451 */     Configurator configurator = null;
/* 452 */     String filename = url.getFile();
/*     */ 
/* 454 */     if ((clazz == null) && (filename != null) && (filename.endsWith(".xml"))) {
/* 455 */       clazz = "org.apache.log4j.xml.DOMConfigurator";
/*     */     }
/*     */ 
/* 458 */     if (clazz != null) {
/* 459 */       LogLog.debug("Preferred configurator class: " + clazz);
/* 460 */       configurator = (Configurator)instantiateByClassName(clazz, Configurator.class, null);
/*     */ 
/* 463 */       if (configurator == null) {
/* 464 */         LogLog.error("Could not instantiate configurator [" + clazz + "].");
/* 465 */         return;
/*     */       }
/*     */     } else {
/* 468 */       configurator = new PropertyConfigurator();
/*     */     }
/*     */ 
/* 471 */     configurator.doConfigure(url, hierarchy);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.helpers.OptionConverter
 * JD-Core Version:    0.6.0
 */