/*     */ package org.apache.log4j.or;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ 
/*     */ public class RendererMap
/*     */ {
/*     */   Hashtable map;
/*  25 */   static ObjectRenderer defaultRenderer = new DefaultRenderer();
/*     */ 
/*     */   public RendererMap()
/*     */   {
/*  29 */     this.map = new Hashtable();
/*     */   }
/*     */ 
/*     */   public static void addRenderer(RendererSupport repository, String renderedClassName, String renderingClassName)
/*     */   {
/*  39 */     LogLog.debug("Rendering class: [" + renderingClassName + "], Rendered class: [" + renderedClassName + "].");
/*     */ 
/*  41 */     ObjectRenderer renderer = (ObjectRenderer)OptionConverter.instantiateByClassName(renderingClassName, ObjectRenderer.class, null);
/*     */ 
/*  45 */     if (renderer == null) {
/*  46 */       LogLog.error("Could not instantiate renderer [" + renderingClassName + "].");
/*  47 */       return;
/*     */     }
/*     */     try {
/*  50 */       Class renderedClass = Loader.loadClass(renderedClassName);
/*  51 */       repository.setRenderer(renderedClass, renderer);
/*     */     } catch (ClassNotFoundException e) {
/*  53 */       LogLog.error("Could not find class [" + renderedClassName + "].", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String findAndRender(Object o)
/*     */   {
/*  67 */     if (o == null) {
/*  68 */       return null;
/*     */     }
/*  70 */     return get(o.getClass()).doRender(o);
/*     */   }
/*     */ 
/*     */   public ObjectRenderer get(Object o)
/*     */   {
/*  79 */     if (o == null) {
/*  80 */       return null;
/*     */     }
/*  82 */     return get(o.getClass());
/*     */   }
/*     */ 
/*     */   public ObjectRenderer get(Class clazz)
/*     */   {
/* 138 */     ObjectRenderer r = null;
/* 139 */     for (Class c = clazz; c != null; c = c.getSuperclass())
/*     */     {
/* 141 */       r = (ObjectRenderer)this.map.get(c);
/* 142 */       if (r != null) {
/* 143 */         return r;
/*     */       }
/* 145 */       r = searchInterfaces(c);
/* 146 */       if (r != null)
/* 147 */         return r;
/*     */     }
/* 149 */     return defaultRenderer;
/*     */   }
/*     */ 
/*     */   ObjectRenderer searchInterfaces(Class c)
/*     */   {
/* 155 */     ObjectRenderer r = (ObjectRenderer)this.map.get(c);
/* 156 */     if (r != null) {
/* 157 */       return r;
/*     */     }
/* 159 */     Class[] ia = c.getInterfaces();
/* 160 */     for (int i = 0; i < ia.length; i++) {
/* 161 */       r = searchInterfaces(ia[i]);
/* 162 */       if (r != null) {
/* 163 */         return r;
/*     */       }
/*     */     }
/* 166 */     return null;
/*     */   }
/*     */ 
/*     */   public ObjectRenderer getDefaultRenderer()
/*     */   {
/* 172 */     return defaultRenderer;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 178 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   public void put(Class clazz, ObjectRenderer or)
/*     */   {
/* 186 */     this.map.put(clazz, or);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.or.RendererMap
 * JD-Core Version:    0.6.0
 */