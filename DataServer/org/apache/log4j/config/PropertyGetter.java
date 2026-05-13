/*    */ package org.apache.log4j.config;
/*    */ 
/*    */ import java.beans.BeanInfo;
/*    */ import java.beans.FeatureDescriptor;
/*    */ import java.beans.IntrospectionException;
/*    */ import java.beans.Introspector;
/*    */ import java.beans.PropertyDescriptor;
/*    */ import java.lang.reflect.Method;
/*    */ import org.apache.log4j.Priority;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ 
/*    */ public class PropertyGetter
/*    */ {
/* 23 */   protected static final Object[] NULL_ARG = new Object[0];
/*    */   protected Object obj;
/*    */   protected PropertyDescriptor[] props;
/*    */ 
/*    */   public PropertyGetter(Object obj)
/*    */     throws IntrospectionException
/*    */   {
/* 40 */     BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
/* 41 */     this.props = bi.getPropertyDescriptors();
/* 42 */     this.obj = obj;
/*    */   }
/*    */ 
/*    */   public static void getProperties(Object obj, PropertyCallback callback, String prefix)
/*    */   {
/*    */     try
/*    */     {
/* 49 */       new PropertyGetter(obj).getProperties(callback, prefix);
/*    */     } catch (IntrospectionException ex) {
/* 51 */       LogLog.error("Failed to introspect object " + obj, ex);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void getProperties(PropertyCallback callback, String prefix)
/*    */   {
/* 57 */     for (int i = 0; i < this.props.length; i++) {
/* 58 */       Method getter = this.props[i].getReadMethod();
/* 59 */       if ((getter == null) || 
/* 60 */         (!isHandledType(getter.getReturnType())))
/*    */       {
/*    */         continue;
/*    */       }
/* 64 */       String name = this.props[i].getName();
/*    */       try {
/* 66 */         Object result = getter.invoke(this.obj, NULL_ARG);
/*    */ 
/* 68 */         if (result != null)
/* 69 */           callback.foundProperty(this.obj, prefix, name, result);
/*    */       }
/*    */       catch (Exception ex) {
/* 72 */         LogLog.warn("Failed to get value of property " + name);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   protected boolean isHandledType(Class type)
/*    */   {
/* 79 */     return (String.class.isAssignableFrom(type)) || (Integer.TYPE.isAssignableFrom(type)) || (Long.TYPE.isAssignableFrom(type)) || (Boolean.TYPE.isAssignableFrom(type)) || (Priority.class.isAssignableFrom(type));
/*    */   }
/*    */ 
/*    */   public static abstract interface PropertyCallback
/*    */   {
/*    */     public abstract void foundProperty(Object paramObject1, String paramString1, String paramString2, Object paramObject2);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.config.PropertyGetter
 * JD-Core Version:    0.6.0
 */