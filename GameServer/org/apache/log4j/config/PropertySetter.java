/*     */ package org.apache.log4j.config;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.FeatureDescriptor;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ 
/*     */ public class PropertySetter
/*     */ {
/*     */   protected Object obj;
/*     */   protected PropertyDescriptor[] props;
/*     */ 
/*     */   public PropertySetter(Object obj)
/*     */   {
/*  66 */     this.obj = obj;
/*     */   }
/*     */ 
/*     */   protected void introspect()
/*     */   {
/*     */     try
/*     */     {
/*  76 */       BeanInfo bi = Introspector.getBeanInfo(this.obj.getClass());
/*  77 */       this.props = bi.getPropertyDescriptors();
/*     */     } catch (IntrospectionException ex) {
/*  79 */       LogLog.error("Failed to introspect " + this.obj + ": " + ex.getMessage());
/*  80 */       this.props = new PropertyDescriptor[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setProperties(Object obj, Properties properties, String prefix)
/*     */   {
/*  97 */     new PropertySetter(obj).setProperties(properties, prefix);
/*     */   }
/*     */ 
/*     */   public void setProperties(Properties properties, String prefix)
/*     */   {
/* 109 */     int len = prefix.length();
/*     */ 
/* 111 */     for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
/* 112 */       String key = (String)e.nextElement();
/*     */ 
/* 115 */       if (key.startsWith(prefix))
/*     */       {
/* 119 */         if (key.indexOf('.', len + 1) > 0)
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 125 */         String value = OptionConverter.findAndSubst(key, properties);
/* 126 */         key = key.substring(len);
/* 127 */         if (("layout".equals(key)) && ((this.obj instanceof Appender))) {
/*     */           continue;
/*     */         }
/* 130 */         setProperty(key, value);
/*     */       }
/*     */     }
/* 133 */     activate();
/*     */   }
/*     */ 
/*     */   public void setProperty(String name, String value)
/*     */   {
/* 153 */     if (value == null) return;
/*     */ 
/* 155 */     name = Introspector.decapitalize(name);
/* 156 */     PropertyDescriptor prop = getPropertyDescriptor(name);
/*     */ 
/* 160 */     if (prop == null)
/* 161 */       LogLog.warn("No such property [" + name + "] in " + this.obj.getClass().getName() + ".");
/*     */     else
/*     */       try
/*     */       {
/* 165 */         setProperty(prop, name, value);
/*     */       } catch (PropertySetterException ex) {
/* 167 */         LogLog.warn("Failed to set property [" + name + "] to value \"" + value + "\". ", ex.rootCause);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setProperty(PropertyDescriptor prop, String name, String value)
/*     */     throws PropertySetterException
/*     */   {
/* 184 */     Method setter = prop.getWriteMethod();
/* 185 */     if (setter == null) {
/* 186 */       throw new PropertySetterException("No setter for property [" + name + "].");
/*     */     }
/* 188 */     Class[] paramTypes = setter.getParameterTypes();
/* 189 */     if (paramTypes.length != 1)
/* 190 */       throw new PropertySetterException("#params for setter != 1");
/*     */     Object arg;
/*     */     try
/*     */     {
/* 195 */       arg = convertArg(value, paramTypes[0]);
/*     */     } catch (Throwable t) {
/* 197 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. Reason: " + t);
/*     */     }
/*     */ 
/* 200 */     if (arg == null) {
/* 201 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
/*     */     }
/*     */ 
/* 204 */     LogLog.debug("Setting property [" + name + "] to [" + arg + "].");
/*     */     try {
/* 206 */       setter.invoke(this.obj, new Object[] { arg });
/*     */     } catch (Exception ex) {
/* 208 */       throw new PropertySetterException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object convertArg(String val, Class type)
/*     */   {
/* 219 */     if (val == null) {
/* 220 */       return null;
/*     */     }
/* 222 */     String v = val.trim();
/* 223 */     if (String.class.isAssignableFrom(type))
/* 224 */       return val;
/* 225 */     if (Integer.TYPE.isAssignableFrom(type))
/* 226 */       return new Integer(v);
/* 227 */     if (Long.TYPE.isAssignableFrom(type))
/* 228 */       return new Long(v);
/* 229 */     if (Boolean.TYPE.isAssignableFrom(type)) {
/* 230 */       if ("true".equalsIgnoreCase(v))
/* 231 */         return Boolean.TRUE;
/* 232 */       if ("false".equalsIgnoreCase(v))
/* 233 */         return Boolean.FALSE;
/*     */     }
/* 235 */     else if (Priority.class.isAssignableFrom(type)) {
/* 236 */       return OptionConverter.toLevel(v, Level.DEBUG);
/*     */     }
/* 238 */     return null;
/*     */   }
/*     */ 
/*     */   protected PropertyDescriptor getPropertyDescriptor(String name)
/*     */   {
/* 244 */     if (this.props == null) introspect();
/*     */ 
/* 246 */     for (int i = 0; i < this.props.length; i++) {
/* 247 */       if (name.equals(this.props[i].getName())) {
/* 248 */         return this.props[i];
/*     */       }
/*     */     }
/* 251 */     return null;
/*     */   }
/*     */ 
/*     */   public void activate()
/*     */   {
/* 256 */     if ((this.obj instanceof OptionHandler))
/* 257 */       ((OptionHandler)this.obj).activateOptions();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.config.PropertySetter
 * JD-Core Version:    0.6.0
 */