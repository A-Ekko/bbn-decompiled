/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Enumeration;
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
/*     */ import javax.management.Notification;
/*     */ import javax.management.NotificationListener;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ 
/*     */ public class LoggerDynamicMBean extends AbstractDynamicMBean
/*     */   implements NotificationListener
/*     */ {
/*  49 */   private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
/*  50 */   private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
/*     */ 
/*  52 */   private Vector dAttributes = new Vector();
/*  53 */   private String dClassName = getClass().getName();
/*     */ 
/*  55 */   private String dDescription = "This MBean acts as a management facade for a org.apache.log4j.Logger instance.";
/*     */ 
/*  59 */   private static Logger cat = Logger.getLogger(LoggerDynamicMBean.class);
/*     */   private Logger logger;
/*     */ 
/*     */   public LoggerDynamicMBean(Logger logger)
/*     */   {
/*  65 */     this.logger = logger;
/*  66 */     buildDynamicMBeanInfo();
/*     */   }
/*     */ 
/*     */   public void handleNotification(Notification notification, Object handback)
/*     */   {
/*  71 */     cat.debug("Received notification: " + notification.getType());
/*  72 */     registerAppenderMBean((Appender)notification.getUserData());
/*     */   }
/*     */ 
/*     */   private void buildDynamicMBeanInfo()
/*     */   {
/*  79 */     Constructor[] constructors = getClass().getConstructors();
/*  80 */     this.dConstructors[0] = new MBeanConstructorInfo("HierarchyDynamicMBean(): Constructs a HierarchyDynamicMBean instance", constructors[0]);
/*     */ 
/*  84 */     this.dAttributes.add(new MBeanAttributeInfo("name", "java.lang.String", "The name of this Logger.", true, false, false));
/*     */ 
/*  91 */     this.dAttributes.add(new MBeanAttributeInfo("priority", "java.lang.String", "The priority of this logger.", true, true, false));
/*     */ 
/* 102 */     MBeanParameterInfo[] params = new MBeanParameterInfo[2];
/* 103 */     params[0] = new MBeanParameterInfo("class name", "java.lang.String", "add an appender to this logger");
/*     */ 
/* 105 */     params[1] = new MBeanParameterInfo("appender name", "java.lang.String", "name of the appender");
/*     */ 
/* 108 */     this.dOperations[0] = new MBeanOperationInfo("addAppender", "addAppender(): add an appender", params, "void", 1);
/*     */   }
/*     */ 
/*     */   protected Logger getLogger()
/*     */   {
/* 117 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 125 */     MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.dAttributes.size()];
/* 126 */     this.dAttributes.toArray(attribs);
/*     */ 
/* 128 */     MBeanInfo mb = new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
/*     */ 
/* 135 */     return mb;
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 143 */     if (operationName.equals("addAppender")) {
/* 144 */       addAppender((String)params[0], (String)params[1]);
/* 145 */       return "Hello world.";
/*     */     }
/*     */ 
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 158 */     if (attributeName == null) {
/* 159 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 165 */     if (attributeName.equals("name"))
/* 166 */       return this.logger.getName();
/* 167 */     if (attributeName.equals("priority")) {
/* 168 */       Level l = this.logger.getLevel();
/* 169 */       if (l == null) {
/* 170 */         return null;
/*     */       }
/* 172 */       return l.toString();
/*     */     }
/* 174 */     if (attributeName.startsWith("appender=")) {
/*     */       try {
/* 176 */         return new ObjectName("log4j:" + attributeName);
/*     */       } catch (Exception e) {
/* 178 */         cat.error("Could not create ObjectName" + attributeName);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 184 */     throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   void addAppender(String appenderClass, String appenderName)
/*     */   {
/* 191 */     cat.debug("addAppender called with " + appenderClass + ", " + appenderName);
/* 192 */     Appender appender = (Appender)OptionConverter.instantiateByClassName(appenderClass, Appender.class, null);
/*     */ 
/* 196 */     appender.setName(appenderName);
/* 197 */     this.logger.addAppender(appender);
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 211 */     if (attribute == null) {
/* 212 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
/*     */     }
/*     */ 
/* 217 */     String name = attribute.getName();
/* 218 */     Object value = attribute.getValue();
/*     */ 
/* 220 */     if (name == null) {
/* 221 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 228 */     if (name.equals("priority")) {
/* 229 */       if ((value instanceof String)) {
/* 230 */         String s = (String)value;
/* 231 */         Level p = this.logger.getLevel();
/* 232 */         if (s.equalsIgnoreCase("NULL"))
/* 233 */           p = null;
/*     */         else {
/* 235 */           p = OptionConverter.toLevel(s, p);
/*     */         }
/* 237 */         this.logger.setLevel(p);
/*     */       }
/*     */     }
/* 240 */     else throw new AttributeNotFoundException("Attribute " + name + " not found in " + getClass().getName());
/*     */   }
/*     */ 
/*     */   void appenderMBeanRegistration()
/*     */   {
/* 247 */     Enumeration enumeration = this.logger.getAllAppenders();
/* 248 */     while (enumeration.hasMoreElements()) {
/* 249 */       Appender appender = (Appender)enumeration.nextElement();
/* 250 */       registerAppenderMBean(appender);
/*     */     }
/*     */   }
/*     */ 
/*     */   void registerAppenderMBean(Appender appender) {
/* 255 */     String name = appender.getName();
/* 256 */     cat.debug("Adding AppenderMBean for appender named " + name);
/* 257 */     ObjectName objectName = null;
/*     */     try {
/* 259 */       AppenderDynamicMBean appenderMBean = new AppenderDynamicMBean(appender);
/* 260 */       objectName = new ObjectName("log4j", "appender", name);
/* 261 */       if (!this.server.isRegistered(objectName)) {
/* 262 */         this.server.registerMBean(appenderMBean, objectName);
/* 263 */         this.dAttributes.add(new MBeanAttributeInfo("appender=" + name, "javax.management.ObjectName", "The " + name + " appender.", true, true, false));
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 268 */       cat.error("Could not add appenderMBean for [" + name + "].", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void postRegister(Boolean registrationDone)
/*     */   {
/* 274 */     appenderMBeanRegistration();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.jmx.LoggerDynamicMBean
 * JD-Core Version:    0.6.0
 */