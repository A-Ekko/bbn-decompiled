/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeList;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.DynamicMBean;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanRegistration;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public abstract class AbstractDynamicMBean
/*     */   implements DynamicMBean, MBeanRegistration
/*     */ {
/*     */   String dClassName;
/*     */   MBeanServer server;
/*     */ 
/*     */   public AttributeList getAttributes(String[] attributeNames)
/*     */   {
/*  45 */     if (attributeNames == null) {
/*  46 */       throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"), "Cannot invoke a getter of " + this.dClassName);
/*     */     }
/*     */ 
/*  51 */     AttributeList resultList = new AttributeList();
/*     */ 
/*  54 */     if (attributeNames.length == 0) {
/*  55 */       return resultList;
/*     */     }
/*     */ 
/*  58 */     for (int i = 0; i < attributeNames.length; i++) {
/*     */       try {
/*  60 */         Object value = getAttribute(attributeNames[i]);
/*  61 */         resultList.add(new Attribute(attributeNames[i], value));
/*     */       } catch (Exception e) {
/*  63 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  66 */     return resultList;
/*     */   }
/*     */ 
/*     */   public AttributeList setAttributes(AttributeList attributes)
/*     */   {
/*  76 */     if (attributes == null) {
/*  77 */       throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList attributes cannot be null"), "Cannot invoke a setter of " + this.dClassName);
/*     */     }
/*     */ 
/*  81 */     AttributeList resultList = new AttributeList();
/*     */ 
/*  84 */     if (attributes.isEmpty()) {
/*  85 */       return resultList;
/*     */     }
/*     */ 
/*  88 */     for (Iterator i = attributes.iterator(); i.hasNext(); ) {
/*  89 */       Attribute attr = (Attribute)i.next();
/*     */       try {
/*  91 */         setAttribute(attr);
/*  92 */         String name = attr.getName();
/*  93 */         Object value = getAttribute(name);
/*  94 */         resultList.add(new Attribute(name, value));
/*     */       } catch (Exception e) {
/*  96 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  99 */     return resultList;
/*     */   }
/*     */ 
/*     */   protected abstract Logger getLogger();
/*     */ 
/*     */   public void postDeregister()
/*     */   {
/* 108 */     getLogger().debug("postDeregister is called.");
/*     */   }
/*     */ 
/*     */   public void postRegister(Boolean registrationDone)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void preDeregister()
/*     */   {
/* 119 */     getLogger().debug("preDeregister called.");
/*     */   }
/*     */ 
/*     */   public ObjectName preRegister(MBeanServer server, ObjectName name)
/*     */   {
/* 124 */     getLogger().debug("preRegister called. Server=" + server + ", name=" + name);
/* 125 */     this.server = server;
/* 126 */     return name;
/*     */   }
/*     */ 
/*     */   public abstract MBeanInfo getMBeanInfo();
/*     */ 
/*     */   public abstract Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
/*     */     throws MBeanException, ReflectionException;
/*     */ 
/*     */   public abstract void setAttribute(Attribute paramAttribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;
/*     */ 
/*     */   public abstract Object getAttribute(String paramString)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.jmx.AbstractDynamicMBean
 * JD-Core Version:    0.6.0
 */