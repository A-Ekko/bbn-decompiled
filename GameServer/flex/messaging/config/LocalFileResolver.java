/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class LocalFileResolver
/*     */   implements ConfigurationFileResolver
/*     */ {
/*  29 */   private Stack configurationPathStack = new Stack();
/*  30 */   int version = CLIENT;
/*     */ 
/*  32 */   public static int CLIENT = 0;
/*  33 */   public static int SERVER = 1;
/*  34 */   public static int LIVECYCLE = 2;
/*     */ 
/*     */   public LocalFileResolver()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LocalFileResolver(int version)
/*     */   {
/*  42 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public void setErrorMessage(ConfigurationException e, String path)
/*     */   {
/*  47 */     if (this.version == LIVECYCLE)
/*     */     {
/*  49 */       e.setMessage(11122, new Object[] { path });
/*     */     }
/*  51 */     else if (this.version == SERVER)
/*     */     {
/*  54 */       e.setMessage(11108);
/*     */     }
/*     */     else
/*     */     {
/*  59 */       e.setMessage(11106);
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream getConfigurationFile(String path) {
/*  65 */     File f = new File(path);
/*     */     ConfigurationException e;
/*     */     try {
/*  68 */       if ((f != null) && (f.exists()) && (f.isAbsolute()))
/*     */       {
/*  70 */         FileInputStream fin = new FileInputStream(f);
/*  71 */         pushConfigurationFile(f.getParent());
/*  72 */         return fin;
/*     */       }
/*     */ 
/*  76 */       ConfigurationException e = new ConfigurationException();
/*  77 */       setErrorMessage(e, path);
/*  78 */       throw e;
/*     */     }
/*     */     catch (FileNotFoundException ex)
/*     */     {
/*  83 */       ConfigurationException e = new ConfigurationException();
/*  84 */       setErrorMessage(e, path);
/*  85 */       e.setRootCause(ex);
/*  86 */       throw e;
/*     */     }
/*     */     catch (SecurityException se)
/*     */     {
/*  90 */       e = new ConfigurationException();
/*  91 */       setErrorMessage(e, path);
/*  92 */       e.setRootCause(se);
/*  93 */     }throw e;
/*     */   }
/*     */   public InputStream getIncludedFile(String src) {
/*  99 */     String path = this.configurationPathStack.peek() + File.separator + src;
/* 100 */     File f = new File(path);
/*     */     ConfigurationException e;
/*     */     try { if ((f != null) && (f.exists()) && (f.isAbsolute()))
/*     */       {
/* 105 */         FileInputStream fin = new FileInputStream(f);
/* 106 */         pushConfigurationFile(f.getParent());
/* 107 */         return fin;
/*     */       }
/*     */ 
/* 112 */       ConfigurationException e = new ConfigurationException();
/* 113 */       e.setMessage(11107, new Object[] { path });
/* 114 */       throw e;
/*     */     }
/*     */     catch (FileNotFoundException ex)
/*     */     {
/* 120 */       ConfigurationException e = new ConfigurationException();
/* 121 */       e.setMessage(11107, new Object[] { path });
/* 122 */       e.setRootCause(ex);
/* 123 */       throw e;
/*     */     }
/*     */     catch (SecurityException se)
/*     */     {
/* 128 */       e = new ConfigurationException();
/* 129 */       e.setMessage(11107, new Object[] { path });
/* 130 */       e.setRootCause(se);
/* 131 */     }throw e;
/*     */   }
/*     */ 
/*     */   public void popIncludedFile()
/*     */   {
/* 137 */     this.configurationPathStack.pop();
/*     */   }
/*     */ 
/*     */   private void pushConfigurationFile(String topLevelPath)
/*     */   {
/* 142 */     this.configurationPathStack.push(topLevelPath);
/*     */   }
/*     */ 
/*     */   public String getIncludedPath(String src)
/*     */   {
/* 147 */     return this.configurationPathStack.peek() + File.separator + src;
/*     */   }
/*     */ 
/*     */   public long getIncludedLastModified(String src)
/*     */   {
/* 152 */     String path = this.configurationPathStack.peek() + File.separator + src;
/* 153 */     File f = new File(path);
/* 154 */     return f.lastModified();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.LocalFileResolver
 * JD-Core Version:    0.6.0
 */