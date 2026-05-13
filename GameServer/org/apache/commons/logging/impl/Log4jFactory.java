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
/*  92 */   private Hashtable attributes = new Hashtable();
/*     */ 
/*  95 */   private Hashtable instances = new Hashtable();
/*     */ 
/*     */   public Object getAttribute(String name)
/*     */   {
/* 106 */     return this.attributes.get(name);
/*     */   }
/*     */ 
/*     */   public String[] getAttributeNames()
/*     */   {
/* 116 */     Vector names = new Vector();
/* 117 */     Enumeration keys = this.attributes.keys();
/* 118 */     while (keys.hasMoreElements()) {
/* 119 */       names.addElement((String)keys.nextElement());
/*     */     }
/* 121 */     String[] results = new String[names.size()];
/* 122 */     for (int i = 0; i < results.length; i++) {
/* 123 */       results[i] = ((String)names.elementAt(i));
/*     */     }
/* 125 */     return results;
/*     */   }
/*     */ 
/*     */   public Log getInstance(Class clazz)
/*     */     throws LogConfigurationException
/*     */   {
/* 141 */     Log instance = (Log)this.instances.get(clazz);
/* 142 */     if (instance != null) {
/* 143 */       return instance;
/*     */     }
/* 145 */     instance = new Log4JLogger(Logger.getLogger(clazz));
/* 146 */     this.instances.put(clazz, instance);
/* 147 */     return instance;
/*     */   }
/*     */ 
/*     */   public Log getInstance(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 154 */     Log instance = (Log)this.instances.get(name);
/* 155 */     if (instance != null) {
/* 156 */       return instance;
/*     */     }
/* 158 */     instance = new Log4JLogger(Logger.getLogger(name));
/* 159 */     this.instances.put(name, instance);
/* 160 */     return instance;
/*     */   }
/*     */ 
/*     */   public void release()
/*     */   {
/* 173 */     this.instances.clear();
/*     */   }
/*     */ 
/*     */   public void removeAttribute(String name)
/*     */   {
/* 186 */     this.attributes.remove(name);
/*     */   }
/*     */ 
/*     */   public void setAttribute(String name, Object value)
/*     */   {
/* 200 */     if (value == null)
/* 201 */       this.attributes.remove(name);
/*     */     else
/* 203 */       this.attributes.put(name, value);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.Log4jFactory
 * JD-Core Version:    0.6.0
 */