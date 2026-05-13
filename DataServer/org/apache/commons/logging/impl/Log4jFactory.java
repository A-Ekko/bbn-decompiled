/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogConfigurationException;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ /** @deprecated */
/*     */ public final class Log4jFactory extends LogFactory
/*     */ {
/*  48 */   private Hashtable attributes = new Hashtable();
/*     */ 
/*  51 */   private Hashtable instances = new Hashtable();
/*     */ 
/*     */   public Object getAttribute(String name)
/*     */   {
/*  62 */     return this.attributes.get(name);
/*     */   }
/*     */ 
/*     */   public String[] getAttributeNames()
/*     */   {
/*  72 */     Vector names = new Vector();
/*  73 */     Enumeration keys = this.attributes.keys();
/*  74 */     while (keys.hasMoreElements()) {
/*  75 */       names.addElement((String)keys.nextElement());
/*     */     }
/*  77 */     String[] results = new String[names.size()];
/*  78 */     for (int i = 0; i < results.length; i++) {
/*  79 */       results[i] = ((String)names.elementAt(i));
/*     */     }
/*  81 */     return results;
/*     */   }
/*     */ 
/*     */   public Log getInstance(Class clazz)
/*     */     throws LogConfigurationException
/*     */   {
/*  97 */     Log instance = (Log)this.instances.get(clazz);
/*  98 */     if (instance != null) {
/*  99 */       return instance;
/*     */     }
/* 101 */     instance = new Log4JLogger(Logger.getLogger(clazz));
/* 102 */     this.instances.put(clazz, instance);
/* 103 */     return instance;
/*     */   }
/*     */ 
/*     */   public Log getInstance(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 110 */     Log instance = (Log)this.instances.get(name);
/* 111 */     if (instance != null) {
/* 112 */       return instance;
/*     */     }
/* 114 */     instance = new Log4JLogger(Logger.getLogger(name));
/* 115 */     this.instances.put(name, instance);
/* 116 */     return instance;
/*     */   }
/*     */ 
/*     */   public void release()
/*     */   {
/* 129 */     this.instances.clear();
/*     */   }
/*     */ 
/*     */   public void removeAttribute(String name)
/*     */   {
/* 142 */     this.attributes.remove(name);
/*     */   }
/*     */ 
/*     */   public void setAttribute(String name, Object value)
/*     */   {
/* 156 */     if (value == null)
/* 157 */       this.attributes.remove(name);
/*     */     else
/* 159 */       this.attributes.put(name, value);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.Log4jFactory
 * JD-Core Version:    0.6.0
 */