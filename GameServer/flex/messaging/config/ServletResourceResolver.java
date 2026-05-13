/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.Stack;
/*     */ import javax.servlet.ServletContext;
/*     */ 
/*     */ public class ServletResourceResolver
/*     */   implements ConfigurationFileResolver
/*     */ {
/*     */   private ServletContext context;
/*  28 */   private Stack configurationPathStack = new Stack();
/*     */ 
/*     */   public ServletResourceResolver(ServletContext context)
/*     */   {
/*  32 */     this.context = context;
/*     */   }
/*     */ 
/*     */   public boolean isAvailable(String path, boolean throwError) throws ConfigurationException
/*     */   {
/*  37 */     boolean available = false;
/*  38 */     InputStream is = this.context.getResourceAsStream(path);
/*  39 */     if (is != null)
/*     */     {
/*  41 */       pushConfigurationFile(path);
/*  42 */       available = true;
/*     */     }
/*  46 */     else if (throwError)
/*     */     {
/*  49 */       ConfigurationException e = new ConfigurationException();
/*  50 */       e.setMessage(11108, new Object[] { path });
/*  51 */       throw e;
/*     */     }
/*     */ 
/*  55 */     return available;
/*     */   }
/*     */ 
/*     */   public InputStream getConfigurationFile(String path)
/*     */   {
/*  60 */     InputStream is = this.context.getResourceAsStream(path);
/*  61 */     if (is != null)
/*     */     {
/*  63 */       pushConfigurationFile(path);
/*  64 */       return is;
/*     */     }
/*     */ 
/*  69 */     ConfigurationException e = new ConfigurationException();
/*  70 */     e.setMessage(11108, new Object[] { path });
/*  71 */     throw e;
/*     */   }
/*     */ 
/*     */   public InputStream getIncludedFile(String src)
/*     */   {
/*  77 */     String path = this.configurationPathStack.peek() + "/" + src;
/*  78 */     InputStream is = this.context.getResourceAsStream(path);
/*     */ 
/*  80 */     if (is != null)
/*     */     {
/*  82 */       pushConfigurationFile(path);
/*  83 */       return is;
/*     */     }
/*     */ 
/*  88 */     ConfigurationException e = new ConfigurationException();
/*  89 */     e.setMessage(11107, new Object[] { path });
/*  90 */     throw e;
/*     */   }
/*     */ 
/*     */   public void popIncludedFile()
/*     */   {
/*  96 */     this.configurationPathStack.pop();
/*     */   }
/*     */ 
/*     */   private void pushConfigurationFile(String path)
/*     */   {
/* 101 */     String topLevelPath = path.substring(0, path.lastIndexOf("/"));
/* 102 */     this.configurationPathStack.push(topLevelPath);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ServletResourceResolver
 * JD-Core Version:    0.6.0
 */