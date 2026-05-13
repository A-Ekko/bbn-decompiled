/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Vector;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.ListenerNotFoundException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.Notification;
/*     */ import javax.management.NotificationBroadcaster;
/*     */ import javax.management.NotificationBroadcasterSupport;
/*     */ import javax.management.NotificationFilter;
/*     */ import javax.management.NotificationFilterSupport;
/*     */ import javax.management.NotificationListener;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.LogManager;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.HierarchyEventListener;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ 
/*     */ public class HierarchyDynamicMBean extends AbstractDynamicMBean
/*     */   implements HierarchyEventListener, NotificationBroadcaster
/*     */ {
/*     */   static final String ADD_APPENDER = "addAppender.";
/*     */   static final String THRESHOLD = "threshold";
/*  59 */   private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
/*  60 */   private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
/*     */ 
/*  62 */   private Vector vAttributes = new Vector();
/*  63 */   private String dClassName = getClass().getName();
/*  64 */   private String dDescription = "This MBean acts as a management facade for org.apache.log4j.Hierarchy.";
/*     */ 
/*  67 */   private NotificationBroadcasterSupport nbs = new NotificationBroadcasterSupport();
/*     */   private LoggerRepository hierarchy;
/*  72 */   private static Logger log = Logger.getLogger(HierarchyDynamicMBean.class);
/*     */ 
/*     */   public HierarchyDynamicMBean() {
/*  75 */     this.hierarchy = LogManager.getLoggerRepository();
/*  76 */     buildDynamicMBeanInfo();
/*     */   }
/*     */ 
/*     */   private void buildDynamicMBeanInfo()
/*     */   {
/*  81 */     Constructor[] constructors = getClass().getConstructors();
/*  82 */     this.dConstructors[0] = new MBeanConstructorInfo("HierarchyDynamicMBean(): Constructs a HierarchyDynamicMBean instance", constructors[0]);
/*     */ 
/*  86 */     this.vAttributes.add(new MBeanAttributeInfo("threshold", "java.lang.String", "The \"threshold\" state of the hiearchy.", true, true, false));
/*     */ 
/*  93 */     MBeanParameterInfo[] params = new MBeanParameterInfo[1];
/*  94 */     params[0] = new MBeanParameterInfo("name", "java.lang.String", "Create a logger MBean");
/*     */ 
/*  96 */     this.dOperations[0] = new MBeanOperationInfo("addLoggerMBean", "addLoggerMBean(): add a loggerMBean", params, "javax.management.ObjectName", 1);
/*     */   }
/*     */ 
/*     */   public ObjectName addLoggerMBean(String name)
/*     */   {
/* 106 */     Logger cat = LogManager.exists(name);
/*     */ 
/* 108 */     if (cat != null) {
/* 109 */       return addLoggerMBean(cat);
/*     */     }
/* 111 */     return null;
/*     */   }
/*     */ 
/*     */   ObjectName addLoggerMBean(Logger logger)
/*     */   {
/* 116 */     String name = logger.getName();
/* 117 */     ObjectName objectName = null;
/*     */     try {
/* 119 */       LoggerDynamicMBean loggerMBean = new LoggerDynamicMBean(logger);
/* 120 */       objectName = new ObjectName("log4j", "logger", name);
/*     */ 
/* 122 */       if (!this.server.isRegistered(objectName)) {
/* 123 */         this.server.registerMBean(loggerMBean, objectName);
/* 124 */         NotificationFilterSupport nfs = new NotificationFilterSupport();
/* 125 */         nfs.enableType("addAppender." + logger.getName());
/* 126 */         log.debug("---Adding logger [" + name + "] as listener.");
/* 127 */         this.nbs.addNotificationListener(loggerMBean, nfs, null);
/* 128 */         this.vAttributes.add(new MBeanAttributeInfo("logger=" + name, "javax.management.ObjectName", "The " + name + " logger.", true, true, false));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 136 */       log.error("Could not add loggerMBean for [" + name + "].", e);
/*     */     }
/* 138 */     return objectName;
/*     */   }
/*     */ 
/*     */   public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
/*     */   {
/* 145 */     this.nbs.addNotificationListener(listener, filter, handback);
/*     */   }
/*     */ 
/*     */   protected Logger getLogger()
/*     */   {
/* 150 */     return log;
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 157 */     MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.vAttributes.size()];
/* 158 */     this.vAttributes.toArray(attribs);
/*     */ 
/* 160 */     return new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
/*     */   }
/*     */ 
/*     */   public MBeanNotificationInfo[] getNotificationInfo()
/*     */   {
/* 170 */     return this.nbs.getNotificationInfo();
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 179 */     if (operationName == null) {
/* 180 */       throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
/*     */     }
/*     */ 
/* 186 */     if (operationName.equals("addLoggerMBean")) {
/* 187 */       return addLoggerMBean((String)params[0]);
/*     */     }
/* 189 */     throw new ReflectionException(new NoSuchMethodException(operationName), "Cannot find the operation " + operationName + " in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 203 */     if (attributeName == null) {
/* 204 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 209 */     log.debug("Called getAttribute with [" + attributeName + "].");
/*     */ 
/* 212 */     if (attributeName.equals("threshold"))
/* 213 */       return this.hierarchy.getThreshold();
/* 214 */     if (attributeName.startsWith("logger")) {
/* 215 */       int k = attributeName.indexOf("%3D");
/* 216 */       String val = attributeName;
/* 217 */       if (k > 0)
/* 218 */         val = attributeName.substring(0, k) + '=' + attributeName.substring(k + 3);
/*     */       try
/*     */       {
/* 221 */         return new ObjectName("log4j:" + val);
/*     */       } catch (Exception e) {
/* 223 */         log.error("Could not create ObjectName" + val);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 230 */     throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public void addAppenderEvent(Category logger, Appender appender)
/*     */   {
/* 238 */     log.debug("addAppenderEvent called: logger=" + logger.getName() + ", appender=" + appender.getName());
/*     */ 
/* 240 */     Notification n = new Notification("addAppender." + logger.getName(), this, 0L);
/* 241 */     n.setUserData(appender);
/* 242 */     log.debug("sending notification.");
/* 243 */     this.nbs.sendNotification(n);
/*     */   }
/*     */ 
/*     */   public void removeAppenderEvent(Category cat, Appender appender)
/*     */   {
/* 248 */     log.debug("removeAppenderCalled: logger=" + cat.getName() + ", appender=" + appender.getName());
/*     */   }
/*     */ 
/*     */   public void postRegister(Boolean registrationDone)
/*     */   {
/* 254 */     log.debug("postRegister is called.");
/* 255 */     this.hierarchy.addHierarchyEventListener(this);
/* 256 */     Logger root = this.hierarchy.getRootLogger();
/* 257 */     addLoggerMBean(root);
/*     */   }
/*     */ 
/*     */   public void removeNotificationListener(NotificationListener listener)
/*     */     throws ListenerNotFoundException
/*     */   {
/* 263 */     this.nbs.removeNotificationListener(listener);
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 273 */     if (attribute == null) {
/* 274 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
/*     */     }
/*     */ 
/* 278 */     String name = attribute.getName();
/* 279 */     Object value = attribute.getValue();
/*     */ 
/* 281 */     if (name == null) {
/* 282 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 288 */     if (name.equals("threshold")) {
/* 289 */       Level l = OptionConverter.toLevel((String)value, this.hierarchy.getThreshold());
/*     */ 
/* 291 */       this.hierarchy.setThreshold(l);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.jmx.HierarchyDynamicMBean
 * JD-Core Version:    0.6.0
 */