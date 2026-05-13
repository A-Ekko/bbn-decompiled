/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.FeatureDescriptor;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ 
/*     */ public class LayoutDynamicMBean extends AbstractDynamicMBean
/*     */ {
/*  51 */   private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
/*  52 */   private Vector dAttributes = new Vector();
/*  53 */   private String dClassName = getClass().getName();
/*     */ 
/*  55 */   private Hashtable dynamicProps = new Hashtable(5);
/*  56 */   private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
/*  57 */   private String dDescription = "This MBean acts as a management facade for log4j layouts.";
/*     */ 
/*  61 */   private static Logger cat = Logger.getLogger(LayoutDynamicMBean.class);
/*     */   private Layout layout;
/*     */ 
/*     */   public LayoutDynamicMBean(Layout layout)
/*     */     throws IntrospectionException
/*     */   {
/*  67 */     this.layout = layout;
/*  68 */     buildDynamicMBeanInfo();
/*     */   }
/*     */ 
/*     */   private void buildDynamicMBeanInfo() throws IntrospectionException
/*     */   {
/*  73 */     Constructor[] constructors = getClass().getConstructors();
/*  74 */     this.dConstructors[0] = new MBeanConstructorInfo("LayoutDynamicMBean(): Constructs a LayoutDynamicMBean instance", constructors[0]);
/*     */ 
/*  79 */     BeanInfo bi = Introspector.getBeanInfo(this.layout.getClass());
/*  80 */     PropertyDescriptor[] pd = bi.getPropertyDescriptors();
/*     */ 
/*  82 */     int size = pd.length;
/*     */ 
/*  84 */     for (int i = 0; i < size; i++) {
/*  85 */       String name = pd[i].getName();
/*  86 */       Method readMethod = pd[i].getReadMethod();
/*  87 */       Method writeMethod = pd[i].getWriteMethod();
/*  88 */       if (readMethod != null) {
/*  89 */         Class returnClass = readMethod.getReturnType();
/*  90 */         if (!isSupportedType(returnClass))
/*     */           continue;
/*     */         String returnClassName;
/*     */         String returnClassName;
/*  92 */         if (returnClass.isAssignableFrom(Level.class))
/*  93 */           returnClassName = "java.lang.String";
/*     */         else {
/*  95 */           returnClassName = returnClass.getName();
/*     */         }
/*     */ 
/*  98 */         this.dAttributes.add(new MBeanAttributeInfo(name, returnClassName, "Dynamic", true, writeMethod != null, false));
/*     */ 
/* 104 */         this.dynamicProps.put(name, new MethodUnion(readMethod, writeMethod));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 109 */     MBeanParameterInfo[] params = new MBeanParameterInfo[0];
/*     */ 
/* 111 */     this.dOperations[0] = new MBeanOperationInfo("activateOptions", "activateOptions(): add an layout", params, "void", 1);
/*     */   }
/*     */ 
/*     */   private boolean isSupportedType(Class clazz)
/*     */   {
/* 120 */     if (clazz.isPrimitive()) {
/* 121 */       return true;
/*     */     }
/*     */ 
/* 124 */     if (clazz == String.class) {
/* 125 */       return true;
/*     */     }
/*     */ 
/* 128 */     return clazz.isAssignableFrom(Level.class);
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 138 */     cat.debug("getMBeanInfo called.");
/*     */ 
/* 140 */     MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.dAttributes.size()];
/* 141 */     this.dAttributes.toArray(attribs);
/*     */ 
/* 143 */     return new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 156 */     if ((operationName.equals("activateOptions")) && ((this.layout instanceof OptionHandler)))
/*     */     {
/* 158 */       OptionHandler oh = this.layout;
/* 159 */       oh.activateOptions();
/* 160 */       return "Options activated.";
/*     */     }
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   protected Logger getLogger()
/*     */   {
/* 167 */     return cat;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 177 */     if (attributeName == null) {
/* 178 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 184 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(attributeName);
/*     */ 
/* 186 */     cat.debug("----name=" + attributeName + ", mu=" + mu);
/*     */ 
/* 188 */     if ((mu != null) && (mu.readMethod != null)) {
/*     */       try {
/* 190 */         return mu.readMethod.invoke(this.layout, null);
/*     */       } catch (Exception e) {
/* 192 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 199 */     throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 212 */     if (attribute == null) {
/* 213 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
/*     */     }
/*     */ 
/* 218 */     String name = attribute.getName();
/* 219 */     Object value = attribute.getValue();
/*     */ 
/* 221 */     if (name == null) {
/* 222 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 230 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(name);
/*     */ 
/* 232 */     if ((mu != null) && (mu.writeMethod != null)) {
/* 233 */       Object[] o = new Object[1];
/*     */ 
/* 235 */       Class[] params = mu.writeMethod.getParameterTypes();
/* 236 */       if (params[0] == Priority.class) {
/* 237 */         value = OptionConverter.toLevel((String)value, (Level)getAttribute(name));
/*     */       }
/*     */ 
/* 240 */       o[0] = value;
/*     */       try
/*     */       {
/* 243 */         mu.writeMethod.invoke(this.layout, o);
/*     */       }
/*     */       catch (Exception e) {
/* 246 */         cat.error("FIXME", e);
/*     */       }
/*     */     } else {
/* 249 */       throw new AttributeNotFoundException("Attribute " + name + " not found in " + getClass().getName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.jmx.LayoutDynamicMBean
 * JD-Core Version:    0.6.0
 */