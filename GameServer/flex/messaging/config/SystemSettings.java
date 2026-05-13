/*     */ package flex.messaging.config;
/*     */ 
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.util.PropertyStringResourceLoader;
/*     */ import flex.messaging.util.ResourceLoader;
/*     */ import flex.messaging.util.WatchedObject;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import javax.servlet.ServletContext;
/*     */ 
/*     */ public class SystemSettings
/*     */ {
/*     */   private ResourceLoader resourceLoader;
/*     */   private Locale defaultLocale;
/*     */   private boolean manageable;
/*     */   private boolean redeployEnabled;
/*     */   private int watchInterval;
/*     */   private List watches;
/*     */   private List touches;
/*     */ 
/*     */   public SystemSettings()
/*     */   {
/*  47 */     this.manageable = true;
/*  48 */     this.redeployEnabled = false;
/*  49 */     this.resourceLoader = new PropertyStringResourceLoader();
/*  50 */     this.touches = new ArrayList();
/*  51 */     this.watches = new ArrayList();
/*  52 */     this.watchInterval = 20;
/*     */   }
/*     */ 
/*     */   public void setDefaultLocale(Locale locale)
/*     */   {
/*  57 */     this.defaultLocale = locale;
/*  58 */     this.resourceLoader.setDefaultLocale(this.defaultLocale);
/*     */   }
/*     */ 
/*     */   public Locale getDefaultLocale()
/*     */   {
/*  63 */     return this.defaultLocale;
/*     */   }
/*     */ 
/*     */   public boolean isManageable()
/*     */   {
/*  68 */     return this.manageable;
/*     */   }
/*     */ 
/*     */   public void setManageable(String manageable)
/*     */   {
/*  73 */     manageable = manageable.toLowerCase();
/*  74 */     if (manageable.startsWith("f"))
/*  75 */       this.manageable = false;
/*     */   }
/*     */ 
/*     */   public ResourceLoader getResourceLoader()
/*     */   {
/*  80 */     return this.resourceLoader;
/*     */   }
/*     */ 
/*     */   public void setResourceLoader(ResourceLoader resourceLoader)
/*     */   {
/*  85 */     this.resourceLoader = resourceLoader;
/*     */   }
/*     */ 
/*     */   public void setRedeployEnabled(String enabled)
/*     */   {
/*  90 */     enabled = enabled.toLowerCase();
/*  91 */     if (enabled.startsWith("t"))
/*  92 */       this.redeployEnabled = true;
/*     */   }
/*     */ 
/*     */   public boolean getRedeployEnabled()
/*     */   {
/*  97 */     return this.redeployEnabled;
/*     */   }
/*     */ 
/*     */   public void setWatchInterval(String interval)
/*     */   {
/* 102 */     this.watchInterval = Integer.parseInt(interval);
/*     */   }
/*     */ 
/*     */   public int getWatchInterval()
/*     */   {
/* 107 */     return this.watchInterval;
/*     */   }
/*     */ 
/*     */   public void addWatchFile(String watch)
/*     */   {
/* 112 */     this.watches.add(watch);
/*     */   }
/*     */ 
/*     */   public List getWatchFiles()
/*     */   {
/* 117 */     return this.watches;
/*     */   }
/*     */ 
/*     */   public void addTouchFile(String touch)
/*     */   {
/* 122 */     this.touches.add(touch);
/*     */   }
/*     */ 
/*     */   public List getTouchFiles()
/*     */   {
/* 127 */     return this.touches;
/*     */   }
/*     */ 
/*     */   public void setPaths(ServletContext context)
/*     */   {
/* 132 */     if (this.redeployEnabled)
/*     */     {
/* 134 */       List resolvedWatches = new ArrayList();
/* 135 */       for (int i = 0; i < this.watches.size(); i++)
/*     */       {
/* 137 */         String path = (String)this.watches.get(i);
/* 138 */         String resolvedPath = null;
/* 139 */         if ((path.startsWith("{context.root}")) || (path.startsWith("{context-root}")))
/*     */         {
/* 141 */           path = path.substring(14);
/* 142 */           resolvedPath = context.getRealPath(path);
/*     */ 
/* 144 */           if (resolvedPath != null)
/*     */           {
/*     */             try
/*     */             {
/* 148 */               resolvedWatches.add(new WatchedObject(resolvedPath));
/*     */             }
/*     */             catch (FileNotFoundException fnfe)
/*     */             {
/* 152 */               Logger logger = Log.getLogger("Configuration");
/* 153 */               if (logger != null)
/*     */               {
/* 155 */                 logger.warn("The watch-file, " + path + ", could not be found and will be ignored.");
/*     */               }
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 161 */             Logger logger = Log.getLogger("Configuration");
/* 162 */             logger.warn("The watch-file, " + path + ", could not be resolved to a path and will be ignored.");
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           try
/*     */           {
/* 169 */             resolvedWatches.add(new WatchedObject(path));
/*     */           }
/*     */           catch (FileNotFoundException fnfe)
/*     */           {
/* 173 */             Logger logger = Log.getLogger("Configuration");
/* 174 */             if (logger == null)
/*     */               continue;
/* 176 */             logger.warn("The watch-file, " + path + ", could not be found and will be ignored.");
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 181 */       this.watches = resolvedWatches;
/*     */ 
/* 183 */       List resolvedTouches = new ArrayList();
/* 184 */       for (int i = 0; i < this.touches.size(); i++)
/*     */       {
/* 186 */         String path = (String)this.touches.get(i);
/* 187 */         String resolvedPath = null;
/* 188 */         if ((path.startsWith("{context.root}")) || (path.startsWith("{context-root}")))
/*     */         {
/* 190 */           path = path.substring(14);
/* 191 */           resolvedPath = context.getRealPath(path);
/*     */ 
/* 193 */           if (resolvedPath != null)
/*     */           {
/* 195 */             File file = new File(resolvedPath);
/* 196 */             if ((!file.exists()) || ((!file.isFile()) && (!file.isDirectory())) || (!file.isAbsolute()))
/*     */             {
/* 198 */               Logger logger = Log.getLogger("Configuration");
/* 199 */               logger.warn("The touch-file, " + path + ", could not be found and will be ignored.");
/*     */             }
/*     */             else
/*     */             {
/* 203 */               resolvedTouches.add(resolvedPath);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 208 */             Logger logger = Log.getLogger("Configuration");
/* 209 */             logger.warn("The touch-file, " + path + ", could not be resolved to a path and will be ignored.");
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           try
/*     */           {
/* 216 */             resolvedTouches.add(new WatchedObject(path));
/*     */           }
/*     */           catch (FileNotFoundException fnfe)
/*     */           {
/* 220 */             Logger logger = Log.getLogger("Configuration");
/* 221 */             if (logger == null)
/*     */               continue;
/* 223 */             logger.warn("The touch-file, " + path + ", could not be found and will be ignored.");
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 228 */       this.touches = resolvedTouches;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 237 */     this.resourceLoader = null;
/* 238 */     this.defaultLocale = null;
/* 239 */     this.watches = null;
/* 240 */     this.touches = null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.SystemSettings
 * JD-Core Version:    0.6.0
 */