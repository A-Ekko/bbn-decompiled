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
/*  56 */     this.obj = obj;
/*     */   }
/*     */ 
/*     */   protected void introspect()
/*     */   {
/*     */     try
/*     */     {
/*  66 */       BeanInfo bi = Introspector.getBeanInfo(this.obj.getClass());
/*  67 */       this.props = bi.getPropertyDescriptors();
/*     */     } catch (IntrospectionException ex) {
/*  69 */       LogLog.error("Failed to introspect " + this.obj + ": " + ex.getMessage());
/*  70 */       this.props = new PropertyDescriptor[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setProperties(Object obj, Properties properties, String prefix)
/*     */   {
/*  87 */     new PropertySetter(obj).setProperties(properties, prefix);
/*     */   }
/*     */ 
/*     */   public void setProperties(Properties properties, String prefix)
/*     */   {
/*  99 */     int len = prefix.length();
/*     */ 
/* 101 */     for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
/* 102 */       String key = (String)e.nextElement();
/*     */ 
/* 105 */       if (!key.startsWith(prefix))
/*     */       {
/*     */         continue;
/*     */       }
/* 109 */       if (key.indexOf('.', len + 1) > 0)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 115 */       String value = OptionConverter.findAndSubst(key, properties);
/* 116 */       key = key.substring(len);
/* 117 */       if (("layout".equals(key)) && ((this.obj instanceof Appender))) {
/*     */         continue;
/*     */       }
/* 120 */       setProperty(key, value);
/*     */     }
/*     */ 
/* 123 */     activate();
/*     */   }
/*     */ 
/*     */   public void setProperty(String name, String value)
/*     */   {
/* 143 */     if (value == null) return;
/*     */ 
/* 145 */     name = Introspector.decapitalize(name);
/* 146 */     PropertyDescriptor prop = getPropertyDescriptor(name);
/*     */ 
/* 150 */     if (prop == null)
/* 151 */       LogLog.warn("No such property [" + name + "] in " + this.obj.getClass().getName() + ".");
/*     */     else
/*     */       try
/*     */       {
/* 155 */         setProperty(prop, name, value);
/*     */       } catch (PropertySetterException ex) {
/* 157 */         LogLog.warn("Failed to set property [" + name + "] to value \"" + value + "\". ", ex.rootCause);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setProperty(PropertyDescriptor prop, String name, String value)
/*     */     throws PropertySetterException
/*     */   {
/* 174 */     Method setter = prop.getWriteMethod();
/* 175 */     if (setter == null) {
/* 176 */       throw new PropertySetterException("No setter for property [" + name + "].");
/*     */     }
/* 178 */     Class[] paramTypes = setter.getParameterTypes();
/* 179 */     if (paramTypes.length != 1)
/* 180 */       throw new PropertySetterException("#params for setter != 1");
/*     */     Object arg;
/*     */     try
/*     */     {
/* 185 */       arg = convertArg(value, paramTypes[0]);
/*     */     } catch (Throwable t) {
/* 187 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. Reason: " + t);
/*     */     }
/*     */ 
/* 190 */     if (arg == null) {
/* 191 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
/*     */     }
/*     */ 
/* 194 */     LogLog.debug("Setting property [" + name + "] to [" + arg + "].");
/*     */     try {
/* 196 */       setter.invoke(this.obj, new Object[] { arg });
/*     */     } catch (Exception ex) {
/* 198 */       throw new PropertySetterException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object convertArg(String val, Class type)
/*     */   {
/* 209 */     if (val == null) {
/* 210 */       return null;
/*     */     }
/* 212 */     String v = val.trim();
/* 213 */     if (String.class.isAssignableFrom(type))
/* 214 */       return val;
/* 215 */     if (Integer.TYPE.isAssignableFrom(type))
/* 216 */       return new Integer(v);
/* 217 */     if (Long.TYPE.isAssignableFrom(type))
/* 218 */       return new Long(v);
/* 219 */     if (Boolean.TYPE.isAssignableFrom(type)) {
/* 220 */       if ("true".equalsIgnoreCase(v))
/* 221 */         return Boolean.TRUE;
/* 222 */       if ("false".equalsIgnoreCase(v))
/* 223 */         return Boolean.FALSE;
/*     */     }
/* 225 */     else if (Priority.class.isAssignableFrom(type)) {
/* 226 */       return OptionConverter.toLevel(v, Level.DEBUG);
/*     */     }
/* 228 */     return null;
/*     */   }
/*     */ 
/*     */   protected PropertyDescriptor getPropertyDescriptor(String name)
/*     */   {
/* 234 */     if (this.props == null) introspect();
/*     */ 
/* 236 */     for (int i = 0; i < this.props.length; i++) {
/* 237 */       if (name.equals(this.props[i].getName())) {
/* 238 */         return this.props[i];
/*     */       }
/*     */     }
/* 241 */     return null;
/*     */   }
/*     */ 
/*     */   public void activate()
/*     */   {
/* 246 */     if ((this.obj instanceof OptionHandler))
/* 247 */       ((OptionHandler)this.obj).activateOptions();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.config.PropertySetter
 * JD-Core Version:    0.6.0
 */