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
/*  31 */   static String DELIM_START = "${";
/*  32 */   static char DELIM_STOP = '}';
/*  33 */   static int DELIM_START_LEN = 2;
/*  34 */   static int DELIM_STOP_LEN = 1;
/*     */ 
/*     */   public static String[] concatanateArrays(String[] l, String[] r)
/*     */   {
/*  42 */     int len = l.length + r.length;
/*  43 */     String[] a = new String[len];
/*     */ 
/*  45 */     System.arraycopy(l, 0, a, 0, l.length);
/*  46 */     System.arraycopy(r, 0, a, l.length, r.length);
/*     */ 
/*  48 */     return a;
/*     */   }
/*     */ 
/*     */   public static String convertSpecialChars(String s)
/*     */   {
/*  55 */     int len = s.length();
/*  56 */     StringBuffer sbuf = new StringBuffer(len);
/*     */ 
/*  58 */     int i = 0;
/*  59 */     while (i < len) {
/*  60 */       char c = s.charAt(i++);
/*  61 */       if (c == '\\') {
/*  62 */         c = s.charAt(i++);
/*  63 */         if (c == 'n') c = '\n';
/*  64 */         else if (c == 'r') c = '\r';
/*  65 */         else if (c == 't') c = '\t';
/*  66 */         else if (c == 'f') c = '\f';
/*  67 */         else if (c == '\b') c = '\b';
/*  68 */         else if (c == '"') c = '"';
/*  69 */         else if (c == '\'') c = '\'';
/*  70 */         else if (c == '\\') c = '\\';
/*     */       }
/*  72 */       sbuf.append(c);
/*     */     }
/*  74 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public static String getSystemProperty(String key, String def)
/*     */   {
/*     */     try
/*     */     {
/*  92 */       return System.getProperty(key, def);
/*     */     } catch (Throwable e) {
/*  94 */       LogLog.debug("Was not allowed to read system property \"" + key + "\".");
/*  95 */     }return def;
/*     */   }
/*     */ 
/*     */   public static Object instantiateByKey(Properties props, String key, Class superClass, Object defaultValue)
/*     */   {
/* 106 */     String className = findAndSubst(key, props);
/* 107 */     if (className == null) {
/* 108 */       LogLog.error("Could not find value for key " + key);
/* 109 */       return defaultValue;
/*     */     }
/*     */ 
/* 112 */     return instantiateByClassName(className.trim(), superClass, defaultValue);
/*     */   }
/*     */ 
/*     */   public static boolean toBoolean(String value, boolean dEfault)
/*     */   {
/* 126 */     if (value == null)
/* 127 */       return dEfault;
/* 128 */     String trimmedVal = value.trim();
/* 129 */     if ("true".equalsIgnoreCase(trimmedVal))
/* 130 */       return true;
/* 131 */     if ("false".equalsIgnoreCase(trimmedVal))
/* 132 */       return false;
/* 133 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static int toInt(String value, int dEfault)
/*     */   {
/* 139 */     if (value != null) {
/* 140 */       String s = value.trim();
/*     */       try {
/* 142 */         return Integer.valueOf(s).intValue();
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 145 */         LogLog.error("[" + s + "] is not in proper int form.");
/* 146 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 149 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String value, Level defaultValue)
/*     */   {
/* 175 */     if (value == null) {
/* 176 */       return defaultValue;
/*     */     }
/* 178 */     int hashIndex = value.indexOf('#');
/* 179 */     if (hashIndex == -1) {
/* 180 */       if ("NULL".equalsIgnoreCase(value)) {
/* 181 */         return null;
/*     */       }
/*     */ 
/* 184 */       return Level.toLevel(value, defaultValue);
/*     */     }
/*     */ 
/* 188 */     Level result = defaultValue;
/*     */ 
/* 190 */     String clazz = value.substring(hashIndex + 1);
/* 191 */     String levelName = value.substring(0, hashIndex);
/*     */ 
/* 194 */     if ("NULL".equalsIgnoreCase(levelName)) {
/* 195 */       return null;
/*     */     }
/*     */ 
/* 198 */     LogLog.debug("toLevel:class=[" + clazz + "]" + ":pri=[" + levelName + "]");
/*     */     try
/*     */     {
/* 202 */       Class customLevel = Loader.loadClass(clazz);
/*     */ 
/* 206 */       Class[] paramTypes = { String.class, Level.class };
/*     */ 
/* 209 */       Method toLevelMethod = customLevel.getMethod("toLevel", paramTypes);
/*     */ 
/* 213 */       Object[] params = { levelName, defaultValue };
/* 214 */       Object o = toLevelMethod.invoke(null, params);
/*     */ 
/* 216 */       result = (Level)o;
/*     */     } catch (ClassNotFoundException e) {
/* 218 */       LogLog.warn("custom level class [" + clazz + "] not found.");
/*     */     } catch (NoSuchMethodException e) {
/* 220 */       LogLog.warn("custom level class [" + clazz + "]" + " does not have a constructor which takes one string parameter", e);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 223 */       LogLog.warn("custom level class [" + clazz + "]" + " could not be instantiated", e);
/*     */     }
/*     */     catch (ClassCastException e) {
/* 226 */       LogLog.warn("class [" + clazz + "] is not a subclass of org.apache.log4j.Level", e);
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 229 */       LogLog.warn("class [" + clazz + "] cannot be instantiated due to access restrictions", e);
/*     */     }
/*     */     catch (Exception e) {
/* 232 */       LogLog.warn("class [" + clazz + "], level [" + levelName + "] conversion failed.", e);
/*     */     }
/*     */ 
/* 235 */     return result;
/*     */   }
/*     */ 
/*     */   public static long toFileSize(String value, long dEfault)
/*     */   {
/* 241 */     if (value == null) {
/* 242 */       return dEfault;
/*     */     }
/* 244 */     String s = value.trim().toUpperCase();
/* 245 */     long multiplier = 1L;
/*     */     int index;
/* 248 */     if ((index = s.indexOf("KB")) != -1) {
/* 249 */       multiplier = 1024L;
/* 250 */       s = s.substring(0, index);
/*     */     }
/* 252 */     else if ((index = s.indexOf("MB")) != -1) {
/* 253 */       multiplier = 1048576L;
/* 254 */       s = s.substring(0, index);
/*     */     }
/* 256 */     else if ((index = s.indexOf("GB")) != -1) {
/* 257 */       multiplier = 1073741824L;
/* 258 */       s = s.substring(0, index);
/*     */     }
/* 260 */     if (s != null) {
/*     */       try {
/* 262 */         return Long.valueOf(s).longValue() * multiplier;
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 265 */         LogLog.error("[" + s + "] is not in proper int form.");
/* 266 */         LogLog.error("[" + value + "] not in expected format.", e);
/*     */       }
/*     */     }
/* 269 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static String findAndSubst(String key, Properties props)
/*     */   {
/* 281 */     String value = props.getProperty(key);
/* 282 */     if (value == null)
/* 283 */       return null;
/*     */     try
/*     */     {
/* 286 */       return substVars(value, props);
/*     */     } catch (IllegalArgumentException e) {
/* 288 */       LogLog.error("Bad option value [" + value + "].", e);
/* 289 */     }return value;
/*     */   }
/*     */ 
/*     */   public static Object instantiateByClassName(String className, Class superClass, Object defaultValue)
/*     */   {
/* 307 */     if (className != null) {
/*     */       try {
/* 309 */         Class classObj = Loader.loadClass(className);
/* 310 */         if (!superClass.isAssignableFrom(classObj)) {
/* 311 */           LogLog.error("A \"" + className + "\" object is not assignable to a \"" + superClass.getName() + "\" variable.");
/*     */ 
/* 313 */           LogLog.error("The class \"" + superClass.getName() + "\" was loaded by ");
/* 314 */           LogLog.error("[" + superClass.getClassLoader() + "] whereas object of type ");
/* 315 */           LogLog.error("\"" + classObj.getName() + "\" was loaded by [" + classObj.getClassLoader() + "].");
/*     */ 
/* 317 */           return defaultValue;
/*     */         }
/* 319 */         return classObj.newInstance();
/*     */       } catch (Exception e) {
/* 321 */         LogLog.error("Could not instantiate class [" + className + "].", e);
/*     */       }
/*     */     }
/* 324 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static String substVars(String val, Properties props)
/*     */     throws IllegalArgumentException
/*     */   {
/* 368 */     StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 370 */     int i = 0;
/*     */     while (true)
/*     */     {
/* 374 */       int j = val.indexOf(DELIM_START, i);
/* 375 */       if (j == -1)
/*     */       {
/* 377 */         if (i == 0) {
/* 378 */           return val;
/*     */         }
/* 380 */         sbuf.append(val.substring(i, val.length()));
/* 381 */         return sbuf.toString();
/*     */       }
/*     */ 
/* 384 */       sbuf.append(val.substring(i, j));
/* 385 */       int k = val.indexOf(DELIM_STOP, j);
/* 386 */       if (k == -1) {
/* 387 */         throw new IllegalArgumentException('"' + val + "\" has no closing brace. Opening brace at position " + j + '.');
/*     */       }
/*     */ 
/* 391 */       j += DELIM_START_LEN;
/* 392 */       String key = val.substring(j, k);
/*     */ 
/* 394 */       String replacement = getSystemProperty(key, null);
/*     */ 
/* 396 */       if ((replacement == null) && (props != null)) {
/* 397 */         replacement = props.getProperty(key);
/*     */       }
/*     */ 
/* 400 */       if (replacement != null)
/*     */       {
/* 406 */         String recursiveReplacement = substVars(replacement, props);
/* 407 */         sbuf.append(recursiveReplacement);
/*     */       }
/* 409 */       i = k + DELIM_STOP_LEN;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void selectAndConfigure(URL url, String clazz, LoggerRepository hierarchy)
/*     */   {
/* 440 */     Configurator configurator = null;
/* 441 */     String filename = url.getFile();
/*     */ 
/* 443 */     if ((clazz == null) && (filename != null) && (filename.endsWith(".xml"))) {
/* 444 */       clazz = "org.apache.log4j.xml.DOMConfigurator";
/*     */     }
/*     */ 
/* 447 */     if (clazz != null) {
/* 448 */       LogLog.debug("Preferred configurator class: " + clazz);
/* 449 */       configurator = (Configurator)instantiateByClassName(clazz, Configurator.class, null);
/*     */ 
/* 452 */       if (configurator == null) {
/* 453 */         LogLog.error("Could not instantiate configurator [" + clazz + "].");
/* 454 */         return;
/*     */       }
/*     */     } else {
/* 457 */       configurator = new PropertyConfigurator();
/*     */     }
/*     */ 
/* 460 */     configurator.doConfigure(url, hierarchy);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.OptionConverter
 * JD-Core Version:    0.6.0
 */