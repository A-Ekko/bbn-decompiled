/*     */ package org.slf4j;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import org.slf4j.helpers.SubstituteLoggerFactory;
/*     */ import org.slf4j.helpers.Util;
/*     */ import org.slf4j.impl.StaticLoggerBinder;
/*     */ 
/*     */ public final class LoggerFactory
/*     */ {
/*     */   static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
/*     */   static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
/*     */   static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
/*     */   static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
/*     */   static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
/*     */   static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final int UNINITIALIZED = 0;
/*     */   static final int ONGOING_INITILIZATION = 1;
/*     */   static final int FAILED_INITILIZATION = 2;
/*     */   static final int SUCCESSFUL_INITILIZATION = 3;
/*     */   static final int GET_SINGLETON_INEXISTENT = 1;
/*     */   static final int GET_SINGLETON_EXISTS = 2;
/*  76 */   static int INITIALIZATION_STATE = 0;
/*  77 */   static int GET_SINGLETON_METHOD = 0;
/*  78 */   static SubstituteLoggerFactory TEMP_FACTORY = new SubstituteLoggerFactory();
/*     */ 
/*  86 */   private static final String[] API_COMPATIBILITY_LIST = { "1.5.5", "1.5.6", "1.5.7", "1.5.8" };
/*     */ 
/* 188 */   private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";
/*     */ 
/*     */   static void reset()
/*     */   {
/* 105 */     INITIALIZATION_STATE = 0;
/* 106 */     GET_SINGLETON_METHOD = 0;
/* 107 */     TEMP_FACTORY = new SubstituteLoggerFactory();
/*     */   }
/*     */ 
/*     */   private static final void performInitialization() {
/* 111 */     bind();
/* 112 */     versionSanityCheck();
/* 113 */     singleImplementationSanityCheck();
/*     */   }
/*     */ 
/*     */   private static final void bind()
/*     */   {
/*     */     try
/*     */     {
/* 120 */       getSingleton();
/* 121 */       INITIALIZATION_STATE = 3;
/* 122 */       emitSubstitureLoggerWarning();
/*     */     } catch (NoClassDefFoundError ncde) {
/* 124 */       INITIALIZATION_STATE = 2;
/* 125 */       String msg = ncde.getMessage();
/* 126 */       if ((msg != null) && (msg.indexOf("org/slf4j/impl/StaticLoggerBinder") != -1)) {
/* 127 */         Util.reportFailure("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
/*     */ 
/* 129 */         Util.reportFailure("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
/*     */       }
/*     */ 
/* 133 */       throw ncde;
/*     */     } catch (Exception e) {
/* 135 */       INITIALIZATION_STATE = 2;
/*     */ 
/* 137 */       Util.reportFailure("Failed to instantiate logger [" + getSingleton().getLoggerFactoryClassStr() + "]", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final void emitSubstitureLoggerWarning()
/*     */   {
/* 143 */     List loggerNameList = TEMP_FACTORY.getLoggerNameList();
/* 144 */     if (loggerNameList.size() == 0) {
/* 145 */       return;
/*     */     }
/* 147 */     Util.reportFailure("The following loggers will not work becasue they were created");
/*     */ 
/* 149 */     Util.reportFailure("during the default configuration phase of the underlying logging system.");
/*     */ 
/* 151 */     Util.reportFailure("See also http://www.slf4j.org/codes.html#substituteLogger");
/* 152 */     for (int i = 0; i < loggerNameList.size(); i++) {
/* 153 */       String loggerName = (String)loggerNameList.get(i);
/* 154 */       Util.reportFailure(loggerName);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final void versionSanityCheck() {
/*     */     try {
/* 160 */       String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
/*     */ 
/* 162 */       boolean match = false;
/* 163 */       for (int i = 0; i < API_COMPATIBILITY_LIST.length; i++) {
/* 164 */         if (API_COMPATIBILITY_LIST[i].equals(requested)) {
/* 165 */           match = true;
/*     */         }
/*     */       }
/* 168 */       if (!match) {
/* 169 */         Util.reportFailure("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(API_COMPATIBILITY_LIST).toString());
/*     */ 
/* 172 */         Util.reportFailure("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (NoSuchFieldError nsfe)
/*     */     {
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 181 */       Util.reportFailure("Unexpected problem occured during version sanity check", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void singleImplementationSanityCheck()
/*     */   {
/*     */     try
/*     */     {
/* 192 */       Enumeration paths = LoggerFactory.class.getClassLoader().getResources(STATIC_LOGGER_BINDER_PATH);
/*     */ 
/* 194 */       List implementationList = new ArrayList();
/* 195 */       while (paths.hasMoreElements()) {
/* 196 */         URL path = (URL)paths.nextElement();
/* 197 */         implementationList.add(path);
/*     */       }
/* 199 */       if (implementationList.size() > 1) {
/* 200 */         Util.reportFailure("Class path contains multiple SLF4J bindings.");
/*     */ 
/* 202 */         for (int i = 0; i < implementationList.size(); i++) {
/* 203 */           Util.reportFailure("Found binding in [" + implementationList.get(i) + "]");
/*     */         }
/* 205 */         Util.reportFailure("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
/*     */       }
/*     */     }
/*     */     catch (IOException ioe) {
/* 209 */       Util.reportFailure("Error getting resources from path", ioe);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final StaticLoggerBinder getSingleton() {
/* 214 */     if (GET_SINGLETON_METHOD == 1) {
/* 215 */       return StaticLoggerBinder.SINGLETON;
/*     */     }
/*     */ 
/* 218 */     if (GET_SINGLETON_METHOD == 2) {
/* 219 */       return StaticLoggerBinder.getSingleton();
/*     */     }
/*     */     try
/*     */     {
/* 223 */       StaticLoggerBinder singleton = StaticLoggerBinder.getSingleton();
/* 224 */       GET_SINGLETON_METHOD = 2;
/* 225 */       return singleton;
/*     */     } catch (NoSuchMethodError nsme) {
/* 227 */       GET_SINGLETON_METHOD = 1;
/* 228 */     }return StaticLoggerBinder.SINGLETON;
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/* 242 */     ILoggerFactory iLoggerFactory = getILoggerFactory();
/* 243 */     return iLoggerFactory.getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/* 255 */     return getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static ILoggerFactory getILoggerFactory()
/*     */   {
/* 267 */     if (INITIALIZATION_STATE == 0) {
/* 268 */       INITIALIZATION_STATE = 1;
/* 269 */       performInitialization();
/*     */     }
/*     */ 
/* 272 */     switch (INITIALIZATION_STATE) {
/*     */     case 3:
/* 274 */       return getSingleton().getLoggerFactory();
/*     */     case 2:
/* 276 */       throw new IllegalStateException("org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
/*     */     case 1:
/* 280 */       return TEMP_FACTORY;
/*     */     }
/* 282 */     throw new IllegalStateException("Unreachable code");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.slf4j.LoggerFactory
 * JD-Core Version:    0.6.0
 */