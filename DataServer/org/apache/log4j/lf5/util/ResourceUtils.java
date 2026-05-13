/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class ResourceUtils
/*     */ {
/*     */   public static InputStream getResourceAsStream(Object object, Resource resource)
/*     */   {
/*  64 */     ClassLoader loader = object.getClass().getClassLoader();
/*     */ 
/*  66 */     InputStream in = null;
/*     */ 
/*  68 */     if (loader != null)
/*  69 */       in = loader.getResourceAsStream(resource.getName());
/*     */     else {
/*  71 */       in = ClassLoader.getSystemResourceAsStream(resource.getName());
/*     */     }
/*     */ 
/*  74 */     return in;
/*     */   }
/*     */ 
/*     */   public static URL getResourceAsURL(Object object, Resource resource)
/*     */   {
/*  93 */     ClassLoader loader = object.getClass().getClassLoader();
/*     */ 
/*  95 */     URL url = null;
/*     */ 
/*  97 */     if (loader != null)
/*  98 */       url = loader.getResource(resource.getName());
/*     */     else {
/* 100 */       url = ClassLoader.getSystemResource(resource.getName());
/*     */     }
/*     */ 
/* 103 */     return url;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.util.ResourceUtils
 * JD-Core Version:    0.6.0
 */