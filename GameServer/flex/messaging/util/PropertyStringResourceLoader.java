/*     */ package flex.messaging.util;
/*     */ 
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class PropertyStringResourceLoader
/*     */   implements ResourceLoader
/*     */ {
/*     */   public static final String PROPERTY_BUNDLE = "flex.messaging.errors";
/*     */   private static final String LOG_CATEGORY = "Resource";
/*     */   private String propertyBundle;
/*     */   private Locale defaultLocale;
/*  63 */   private Set loadedLocales = new TreeSet();
/*     */ 
/*  66 */   private Map strings = new HashMap();
/*     */   private Logger logger;
/*     */ 
/*     */   public PropertyStringResourceLoader()
/*     */   {
/*  77 */     this("flex.messaging.errors");
/*     */   }
/*     */ 
/*     */   public PropertyStringResourceLoader(String propertyBundle)
/*     */   {
/*  88 */     this.propertyBundle = propertyBundle.replace('.', '/');
/*  89 */     this.logger = Log.getLogger("Resource");
/*     */   }
/*     */ 
/*     */   public void init(Map properties)
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getString(String key)
/*     */   {
/*  99 */     return getString(key, null, null);
/*     */   }
/*     */ 
/*     */   public String getString(String key, Object[] arguments)
/*     */   {
/* 105 */     return getString(key, null, arguments);
/*     */   }
/*     */ 
/*     */   public String getString(String key, Locale locale)
/*     */   {
/* 111 */     return getString(key, locale, null);
/*     */   }
/*     */ 
/*     */   public String getString(String key, Locale locale, Object[] arguments)
/*     */   {
/* 117 */     synchronized (this.strings)
/*     */     {
/* 119 */       if (this.defaultLocale == null)
/*     */       {
/* 121 */         this.defaultLocale = getDefaultLocale();
/*     */       }
/*     */     }
/* 124 */     String value = null;
/* 125 */     String stringKey = null;
/* 126 */     String localeKey = locale != null ? generateLocaleKey(locale) : generateLocaleKey(this.defaultLocale);
/*     */ 
/* 129 */     String originalStringKey = generateStringKey(key, localeKey);
/* 130 */     int trimIndex = 0;
/*     */     while (true)
/*     */     {
/* 138 */       loadStrings(localeKey);
/* 139 */       stringKey = generateStringKey(key, localeKey);
/* 140 */       synchronized (this.strings)
/*     */       {
/* 142 */         value = (String)this.strings.get(stringKey);
/* 143 */         if (value != null)
/*     */         {
/* 145 */           if (!stringKey.equals(originalStringKey))
/*     */           {
/* 147 */             this.strings.put(originalStringKey, value);
/*     */           }
/* 149 */           return substituteArguments(value, arguments);
/*     */         }
/*     */       }
/* 152 */       trimIndex = localeKey.lastIndexOf("_");
/* 153 */       if (trimIndex == -1)
/*     */         break;
/* 155 */       localeKey = localeKey.substring(0, trimIndex);
/*     */     }
/*     */ 
/* 167 */     if ((locale != null) && (!locale.equals(this.defaultLocale)))
/*     */     {
/* 169 */       localeKey = generateLocaleKey(this.defaultLocale);
/* 170 */       stringKey = generateStringKey(key, localeKey);
/* 171 */       synchronized (this.strings)
/*     */       {
/* 173 */         value = (String)this.strings.get(stringKey);
/* 174 */         if (value != null)
/*     */         {
/* 176 */           this.strings.put(originalStringKey, value);
/* 177 */           return substituteArguments(value, arguments);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 183 */     loadStrings("");
/* 184 */     stringKey = generateStringKey(key, "");
/* 185 */     synchronized (this.strings)
/*     */     {
/* 187 */       value = (String)this.strings.get(stringKey);
/* 188 */       if (value != null)
/*     */       {
/* 190 */         this.strings.put(originalStringKey, value);
/* 191 */         return substituteArguments(value, arguments);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 196 */     return "???" + key + "???";
/*     */   }
/*     */ 
/*     */   public void setDefaultLocale(String locale)
/*     */   {
/* 207 */     this.defaultLocale = LocaleUtils.buildLocale(locale);
/*     */   }
/*     */ 
/*     */   public void setDefaultLocale(Locale locale)
/*     */   {
/* 217 */     this.defaultLocale = locale;
/*     */   }
/*     */ 
/*     */   public Locale getDefaultLocale()
/*     */   {
/* 225 */     if (this.defaultLocale == null) {
/* 226 */       this.defaultLocale = Locale.getDefault();
/*     */     }
/* 228 */     return this.defaultLocale;
/*     */   }
/*     */ 
/*     */   private synchronized void loadStrings(String localeKey)
/*     */   {
/* 238 */     if (this.loadedLocales.contains(localeKey))
/*     */     {
/* 240 */       return;
/*     */     }
/*     */ 
/* 243 */     String filename = this.propertyBundle;
/* 244 */     if (localeKey.length() > 0)
/*     */     {
/* 246 */       filename = filename + "_" + localeKey;
/*     */     }
/* 248 */     filename = filename + ".properties";
/*     */ 
/* 250 */     ClassLoader loader = getClass().getClassLoader();
/* 251 */     InputStream stream = loader.getResourceAsStream(filename);
/* 252 */     Properties props = new Properties();
/* 253 */     if (stream != null)
/*     */     {
/*     */       try
/*     */       {
/* 257 */         props.load(stream);
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 261 */         this.logger.warn("There was a problem reading the string resource property file '" + filename + "' stream.", ioe);
/*     */       }
/*     */       catch (IllegalArgumentException ioe)
/*     */       {
/* 265 */         this.logger.warn("The string resource property file '" + filename + "' contains a malformed Unicode escape sequence.", iae);
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/* 271 */           stream.close();
/*     */         }
/*     */         catch (IOException ioe)
/*     */         {
/* 275 */           this.logger.warn("The string resource property file '" + filename + "' stream failed to close.", ioe);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 281 */       this.logger.warn("The class loader could not locate the string resource property file '" + filename + "'. This may not be an issue if a property file is available for a less specific locale or the default locale.");
/*     */     }
/*     */ 
/* 284 */     if (props.size() > 0)
/*     */     {
/* 286 */       synchronized (this.strings)
/*     */       {
/* 288 */         Iterator iter = props.keySet().iterator();
/* 289 */         while (iter.hasNext())
/*     */         {
/* 291 */           String key = (String)iter.next();
/* 292 */           this.strings.put(generateStringKey(key, localeKey), props.getProperty(key));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private String generateLocaleKey(Locale locale)
/*     */   {
/* 306 */     return locale == null ? "" : locale.toString();
/*     */   }
/*     */ 
/*     */   private String generateStringKey(String key, String locale)
/*     */   {
/* 318 */     return key + "-" + locale;
/*     */   }
/*     */ 
/*     */   private String substituteArguments(String parameterized, Object[] arguments)
/*     */   {
/* 330 */     return MessageFormat.format(parameterized, arguments).trim();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.PropertyStringResourceLoader
 * JD-Core Version:    0.6.0
 */