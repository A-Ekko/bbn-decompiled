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
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ 
/*     */ public class AppenderDynamicMBean extends AbstractDynamicMBean
/*     */ {
/*  51 */   private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
/*  52 */   private Vector dAttributes = new Vector();
/*  53 */   private String dClassName = getClass().getName();
/*     */ 
/*  55 */   private Hashtable dynamicProps = new Hashtable(5);
/*  56 */   private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[2];
/*  57 */   private String dDescription = "This MBean acts as a management facade for log4j appenders.";
/*     */ 
/*  61 */   private static Logger cat = Logger.getLogger(AppenderDynamicMBean.class);
/*     */   private Appender appender;
/*     */ 
/*     */   public AppenderDynamicMBean(Appender appender)
/*     */     throws IntrospectionException
/*     */   {
/*  67 */     this.appender = appender;
/*  68 */     buildDynamicMBeanInfo();
/*     */   }
/*     */ 
/*     */   private void buildDynamicMBeanInfo() throws IntrospectionException
/*     */   {
/*  73 */     Constructor[] constructors = getClass().getConstructors();
/*  74 */     this.dConstructors[0] = new MBeanConstructorInfo("AppenderDynamicMBean(): Constructs a AppenderDynamicMBean instance", constructors[0]);
/*     */ 
/*  79 */     BeanInfo bi = Introspector.getBeanInfo(this.appender.getClass());
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
/*  92 */         if (returnClass.isAssignableFrom(Priority.class))
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
/* 111 */     this.dOperations[0] = new MBeanOperationInfo("activateOptions", "activateOptions(): add an appender", params, "void", 1);
/*     */ 
/* 117 */     params = new MBeanParameterInfo[1];
/* 118 */     params[0] = new MBeanParameterInfo("layout class", "java.lang.String", "layout class");
/*     */ 
/* 121 */     this.dOperations[1] = new MBeanOperationInfo("setLayout", "setLayout(): add a layout", params, "void", 1);
/*     */   }
/*     */ 
/*     */   private boolean isSupportedType(Class clazz)
/*     */   {
/* 130 */     if (clazz.isPrimitive()) {
/* 131 */       return true;
/*     */     }
/*     */ 
/* 134 */     if (clazz == String.class) {
/* 135 */       return true;
/*     */     }
/*     */ 
/* 140 */     return clazz.isAssignableFrom(Priority.class);
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 152 */     cat.debug("getMBeanInfo called.");
/*     */ 
/* 154 */     MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.dAttributes.size()];
/* 155 */     this.dAttributes.toArray(attribs);
/*     */ 
/* 157 */     return new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 170 */     if ((operationName.equals("activateOptions")) && ((this.appender instanceof OptionHandler)))
/*     */     {
/* 172 */       OptionHandler oh = (OptionHandler)this.appender;
/* 173 */       oh.activateOptions();
/* 174 */       return "Options activated.";
/* 175 */     }if (operationName.equals("setLayout")) {
/* 176 */       Layout layout = (Layout)OptionConverter.instantiateByClassName((String)params[0], Layout.class, null);
/*     */ 
/* 180 */       this.appender.setLayout(layout);
/* 181 */       registerLayoutMBean(layout);
/*     */     }
/* 183 */     return null;
/*     */   }
/*     */ 
/*     */   void registerLayoutMBean(Layout layout) {
/* 187 */     if (layout == null) {
/* 188 */       return;
/*     */     }
/* 190 */     String name = this.appender.getName() + ",layout=" + layout.getClass().getName();
/* 191 */     cat.debug("Adding LayoutMBean:" + name);
/* 192 */     ObjectName objectName = null;
/*     */     try {
/* 194 */       LayoutDynamicMBean appenderMBean = new LayoutDynamicMBean(layout);
/* 195 */       objectName = new ObjectName("log4j:appender=" + name);
/* 196 */       if (!this.server.isRegistered(objectName)) {
/* 197 */         this.server.registerMBean(appenderMBean, objectName);
/* 198 */         this.dAttributes.add(new MBeanAttributeInfo("appender=" + name, "javax.management.ObjectName", "The " + name + " layout.", true, true, false));
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 203 */       cat.error("Could not add DynamicLayoutMBean for [" + name + "].", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Logger getLogger()
/*     */   {
/* 209 */     return cat;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 219 */     if (attributeName == null) {
/* 220 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 225 */     cat.debug("getAttribute called with [" + attributeName + "].");
/* 226 */     if (attributeName.startsWith("appender=" + this.appender.getName() + ",layout")) {
/*     */       try {
/* 228 */         return new ObjectName("log4j:" + attributeName);
/*     */       } catch (Exception e) {
/* 230 */         cat.error("attributeName", e);
/*     */       }
/*     */     }
/*     */ 
/* 234 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(attributeName);
/*     */ 
/* 238 */     if ((mu != null) && (mu.readMethod != null)) {
/*     */       try {
/* 240 */         return mu.readMethod.invoke(this.appender, null);
/*     */       } catch (Exception e) {
/* 242 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 249 */     throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 262 */     if (attribute == null) {
/* 263 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
/*     */     }
/*     */ 
/* 268 */     String name = attribute.getName();
/* 269 */     Object value = attribute.getValue();
/*     */ 
/* 271 */     if (name == null) {
/* 272 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 280 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(name);
/*     */ 
/* 282 */     if ((mu != null) && (mu.writeMethod != null)) {
/* 283 */       Object[] o = new Object[1];
/*     */ 
/* 285 */       Class[] params = mu.writeMethod.getParameterTypes();
/* 286 */       if (params[0] == Priority.class) {
/* 287 */         value = OptionConverter.toLevel((String)value, (Level)getAttribute(name));
/*     */       }
/*     */ 
/* 290 */       o[0] = value;
/*     */       try
/*     */       {
/* 293 */         mu.writeMethod.invoke(this.appender, o);
/*     */       }
/*     */       catch (Exception e) {
/* 296 */         cat.error("FIXME", e);
/*     */       }
/* 298 */     } else if (!name.endsWith(".layout"))
/*     */     {
/* 301 */       throw new AttributeNotFoundException("Attribute " + name + " not found in " + getClass().getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public ObjectName preRegister(MBeanServer server, ObjectName name)
/*     */   {
/* 309 */     cat.debug("preRegister called. Server=" + server + ", name=" + name);
/* 310 */     this.server = server;
/* 311 */     registerLayoutMBean(this.appender.getLayout());
/*     */ 
/* 313 */     return name;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.jmx.AppenderDynamicMBean
 * JD-Core Version:    0.6.0
 */