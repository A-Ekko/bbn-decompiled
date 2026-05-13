/*     */ package org.logicalcobwebs.proxool.admin.jmx;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeList;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.DynamicMBean;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.ListenerNotFoundException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.MBeanRegistration;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.Notification;
/*     */ import javax.management.NotificationBroadcaster;
/*     */ import javax.management.NotificationBroadcasterSupport;
/*     */ import javax.management.NotificationFilter;
/*     */ import javax.management.NotificationListener;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ConfigurationListenerIF;
/*     */ import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.logicalcobwebs.proxool.ProxoolFacade;
/*     */ import org.logicalcobwebs.proxool.ProxoolListenerIF;
/*     */ 
/*     */ public class ConnectionPoolMBean
/*     */   implements DynamicMBean, MBeanRegistration, NotificationBroadcaster, ProxoolListenerIF, ConfigurationListenerIF
/*     */ {
/*     */   public static final String NOTIFICATION_TYPE_DEFINITION_UPDATED = "proxool.definitionUpdated";
/*  99 */   private static final Log LOG = LogFactory.getLog(ConnectionPoolMBean.class);
/* 100 */   private static final String CLASS_NAME = ConnectionPoolMBean.class.getName();
/*     */   private static final String RECOURCE_NAME_MBEAN_POOL_DESCRIPTION = "mbean.pool.description";
/*     */   private static final String RECOURCE_NAME_MBEAN_NOTIFICATION_DESCRIPTION = "mbean.notification.description";
/*     */   private static final String RECOURCE_NAME_MBEAN_NOTIFICATION_DEF_UPDATED = "mbean.notification.defUpdated";
/*     */   private static final String OPERATION_NAME_SHUTDOWN = "shutdown";
/* 108 */   private static final ResourceBundle ATTRIBUTE_DESCRIPTIONS_RESOURCE = createAttributeDescriptionsResource();
/* 109 */   private static final ResourceBundle JMX_RESOURCE = createJMXResource();
/*     */ 
/* 111 */   private static final MBeanNotificationInfo[] NOTIFICATION_INFOS = getNotificationInfos();
/*     */   private MBeanInfo mBeanInfo;
/*     */   private ConnectionPoolDefinitionIF poolDefinition;
/*     */   private Properties poolProperties;
/*     */   private long definitionUpdatedSequence;
/* 117 */   private NotificationBroadcasterSupport notificationHelper = new NotificationBroadcasterSupport();
/*     */   private boolean active;
/*     */ 
/*     */   public ConnectionPoolMBean(String alias, Properties poolProperties)
/*     */     throws ProxoolException
/*     */   {
/* 122 */     this.poolDefinition = ProxoolFacade.getConnectionPoolDefinition(alias);
/*     */ 
/* 124 */     this.poolProperties = poolProperties;
/* 125 */     this.mBeanInfo = getDynamicMBeanInfo(this.poolDefinition.getAlias());
/* 126 */     ProxoolFacade.addProxoolListener(this);
/* 127 */     ProxoolFacade.addConfigurationListener(alias, this);
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 134 */     if (attributeName == null) {
/* 135 */       String message = "Cannot invoke a getter of " + CLASS_NAME + " with null attribute name";
/* 136 */       LOG.error(message);
/* 137 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), message);
/*     */     }
/*     */ 
/* 140 */     if (LOG.isDebugEnabled()) {
/* 141 */       LOG.debug("Getting attribute " + attributeName + ".");
/*     */     }
/* 143 */     return ((Attribute)getAttributes(new String[] { attributeName }).get(0)).getValue();
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 151 */     if (attribute == null) {
/* 152 */       String message = "Cannot invoke a setter of " + CLASS_NAME + " with null attribute";
/* 153 */       LOG.error(message);
/* 154 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), message);
/*     */     }
/*     */ 
/* 157 */     if (LOG.isDebugEnabled()) {
/* 158 */       LOG.debug("Setting attribute " + attribute.getName() + ".");
/*     */     }
/* 160 */     AttributeList attributeList = new AttributeList();
/* 161 */     attributeList.add(attribute);
/* 162 */     setAttributes(attributeList);
/*     */   }
/*     */ 
/*     */   public AttributeList getAttributes(String[] attributeNames)
/*     */   {
/* 169 */     if (attributeNames == null) {
/* 170 */       String message = "Cannot invoke a null getter of " + CLASS_NAME;
/* 171 */       LOG.error(message);
/* 172 */       throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"), message);
/*     */     }
/*     */ 
/* 175 */     AttributeList resultList = new AttributeList();
/*     */ 
/* 178 */     if (attributeNames.length == 0) {
/* 179 */       return resultList;
/*     */     }
/*     */ 
/* 183 */     for (int i = 0; i < attributeNames.length; i++) {
/*     */       try {
/* 185 */         if (equalsProperty(attributeNames[i], "alias")) {
/* 186 */           resultList.add(new Attribute(attributeNames[i], this.poolDefinition.getAlias()));
/*     */         }
/* 188 */         else if (equalsProperty(attributeNames[i], "driver-properties")) {
/* 189 */           resultList.add(new Attribute(attributeNames[i], getDelegatePropertiesAsString(this.poolProperties)));
/*     */         }
/* 191 */         else if (equalsProperty(attributeNames[i], "driver-url")) {
/* 192 */           resultList.add(new Attribute(attributeNames[i], this.poolDefinition.getUrl()));
/*     */         }
/* 194 */         else if (equalsProperty(attributeNames[i], "fatal-sql-exception")) {
/* 195 */           resultList.add(new Attribute(attributeNames[i], getValueOrEmpty(this.poolProperties.getProperty("proxool.fatal-sql-exception"))));
/*     */         }
/* 197 */         else if (equalsProperty(attributeNames[i], "house-keeping-sleep-time")) {
/* 198 */           resultList.add(new Attribute(attributeNames[i], new Long(this.poolDefinition.getHouseKeepingSleepTime())));
/*     */         }
/* 200 */         else if (equalsProperty(attributeNames[i], "house-keeping-test-sql")) {
/* 201 */           resultList.add(new Attribute(attributeNames[i], getValueOrEmpty(this.poolDefinition.getHouseKeepingTestSql())));
/*     */         }
/* 203 */         else if (equalsProperty(attributeNames[i], "test-before-use")) {
/* 204 */           resultList.add(new Attribute(attributeNames[i], new Boolean(this.poolDefinition.isTestBeforeUse())));
/*     */         }
/* 206 */         else if (equalsProperty(attributeNames[i], "test-after-use")) {
/* 207 */           resultList.add(new Attribute(attributeNames[i], new Boolean(this.poolDefinition.isTestAfterUse())));
/*     */         }
/* 209 */         else if (equalsProperty(attributeNames[i], "maximum-active-time")) {
/* 210 */           resultList.add(new Attribute(attributeNames[i], new Long(this.poolDefinition.getMaximumActiveTime())));
/*     */         }
/* 212 */         else if (equalsProperty(attributeNames[i], "maximum-connection-count")) {
/* 213 */           resultList.add(new Attribute(attributeNames[i], new Integer(this.poolDefinition.getMaximumConnectionCount())));
/*     */         }
/* 215 */         else if (equalsProperty(attributeNames[i], "maximum-connection-lifetime")) {
/* 216 */           resultList.add(new Attribute(attributeNames[i], new Long(this.poolDefinition.getMaximumConnectionLifetime())));
/*     */         }
/* 218 */         else if (equalsProperty(attributeNames[i], "maximum-new-connections")) {
/* 219 */           resultList.add(new Attribute(attributeNames[i], new Integer(this.poolDefinition.getMaximumNewConnections())));
/*     */         }
/* 221 */         else if (equalsProperty(attributeNames[i], "simultaneous-build-throttle")) {
/* 222 */           resultList.add(new Attribute(attributeNames[i], new Integer(this.poolDefinition.getSimultaneousBuildThrottle())));
/*     */         }
/* 224 */         else if (equalsProperty(attributeNames[i], "minimum-connection-count")) {
/* 225 */           resultList.add(new Attribute(attributeNames[i], new Integer(this.poolDefinition.getMinimumConnectionCount())));
/*     */         }
/* 227 */         else if (equalsProperty(attributeNames[i], "overload-without-refusal-lifetime")) {
/* 228 */           resultList.add(new Attribute(attributeNames[i], new Long(this.poolDefinition.getOverloadWithoutRefusalLifetime())));
/*     */         }
/* 230 */         else if (equalsProperty(attributeNames[i], "prototype-count")) {
/* 231 */           resultList.add(new Attribute(attributeNames[i], new Integer(this.poolDefinition.getPrototypeCount())));
/*     */         }
/* 233 */         else if (equalsProperty(attributeNames[i], "recently-started-threshold")) {
/* 234 */           resultList.add(new Attribute(attributeNames[i], new Long(this.poolDefinition.getRecentlyStartedThreshold())));
/*     */         }
/* 236 */         else if (equalsProperty(attributeNames[i], "statistics")) {
/* 237 */           resultList.add(new Attribute(attributeNames[i], getValueOrEmpty(this.poolDefinition.getStatistics())));
/*     */         }
/* 239 */         else if (equalsProperty(attributeNames[i], "statistics-log-level")) {
/* 240 */           resultList.add(new Attribute(attributeNames[i], getValueOrEmpty(this.poolDefinition.getStatisticsLogLevel())));
/*     */         }
/* 242 */         else if (equalsProperty(attributeNames[i], "trace")) {
/* 243 */           resultList.add(new Attribute(attributeNames[i], new Boolean(this.poolDefinition.isTrace())));
/*     */         }
/* 245 */         else if (equalsProperty(attributeNames[i], "verbose")) {
/* 246 */           resultList.add(new Attribute(attributeNames[i], new Boolean(this.poolDefinition.isVerbose())));
/*     */         }
/* 248 */         else if (equalsProperty(attributeNames[i], "fatal-sql-exception-wrapper-class")) {
/* 249 */           resultList.add(new Attribute(attributeNames[i], getValueOrEmpty(this.poolDefinition.getFatalSqlExceptionWrapper())));
/*     */         }
/*     */         else {
/* 252 */           String message = "Unknown attribute: " + attributeNames[i];
/* 253 */           LOG.error(message);
/* 254 */           throw new AttributeNotFoundException(message);
/*     */         }
/*     */       } catch (AttributeNotFoundException e) {
/* 257 */         throw new RuntimeOperationsException(new IllegalArgumentException(e.getMessage()));
/*     */       }
/*     */     }
/* 260 */     return resultList;
/*     */   }
/*     */ 
/*     */   public AttributeList setAttributes(AttributeList attributes)
/*     */   {
/* 268 */     if (attributes == null) {
/* 269 */       String message = "AttributeList attributes cannot be null";
/* 270 */       LOG.error("AttributeList attributes cannot be null");
/* 271 */       throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList attributes cannot be null"), "Cannot invoke a setter of " + CLASS_NAME);
/*     */     }
/*     */ 
/* 274 */     AttributeList resultList = new AttributeList();
/*     */ 
/* 276 */     if (attributes.isEmpty()) {
/* 277 */       return resultList;
/*     */     }
/*     */ 
/* 280 */     String name = null;
/* 281 */     Object value = null;
/* 282 */     Properties newProperties = new Properties();
/* 283 */     Attribute attribute = null;
/* 284 */     for (Iterator i = attributes.iterator(); i.hasNext(); ) {
/* 285 */       attribute = (Attribute)i.next();
/*     */       try {
/* 287 */         name = attribute.getName();
/* 288 */         value = attribute.getValue();
/*     */ 
/* 290 */         if (equalsProperty(name, "driver-properties")) {
/* 291 */           if (!isEqualProperties(value.toString(), getDelegatePropertiesAsString(this.poolProperties))) {
/* 292 */             checkAssignable(name, String.class, value);
/* 293 */             setDelegateProperties(newProperties, value.toString());
/* 294 */             resultList.add(new Attribute(name, value));
/*     */           }
/* 296 */         } else if (equalsProperty(name, "driver-url")) {
/* 297 */           checkAssignable(name, String.class, value);
/* 298 */           if (notEmpty(value))
/* 299 */             newProperties.setProperty("proxool.driver-url", value.toString());
/*     */           else {
/* 301 */             newProperties.setProperty("proxool.driver-url", "");
/*     */           }
/* 303 */           resultList.add(new Attribute(name, value));
/* 304 */         } else if (equalsProperty(name, "fatal-sql-exception")) {
/* 305 */           if (!isEqualProperties(value.toString(), this.poolProperties.getProperty("proxool.fatal-sql-exception")))
/*     */           {
/* 307 */             checkAssignable(name, String.class, value);
/* 308 */             if (notEmpty(value))
/* 309 */               newProperties.setProperty("proxool.fatal-sql-exception", value.toString());
/*     */             else {
/* 311 */               newProperties.setProperty("proxool.fatal-sql-exception", "");
/*     */             }
/* 313 */             resultList.add(new Attribute(name, value));
/*     */           }
/* 315 */         } else if (equalsProperty(name, "house-keeping-sleep-time")) {
/* 316 */           setIntegerAttribute(name, "proxool.house-keeping-sleep-time", value, 30000, newProperties, resultList);
/*     */         }
/* 318 */         else if (equalsProperty(name, "house-keeping-test-sql")) {
/* 319 */           checkAssignable(name, String.class, value);
/* 320 */           if (notEmpty(value))
/* 321 */             newProperties.setProperty("proxool.house-keeping-test-sql", value.toString());
/*     */           else {
/* 323 */             newProperties.setProperty("proxool.house-keeping-test-sql", "");
/*     */           }
/* 325 */           resultList.add(new Attribute(name, value));
/* 326 */         } else if (equalsProperty(name, "test-before-use")) {
/* 327 */           checkAssignable(name, Boolean.class, value);
/* 328 */           newProperties.setProperty("proxool.test-before-use", value.toString());
/* 329 */           resultList.add(new Attribute(name, value));
/* 330 */         } else if (equalsProperty(name, "test-after-use")) {
/* 331 */           checkAssignable(name, Boolean.class, value);
/* 332 */           newProperties.setProperty("proxool.test-after-use", value.toString());
/* 333 */           resultList.add(new Attribute(name, value));
/* 334 */         } else if (equalsProperty(name, "maximum-active-time")) {
/* 335 */           setIntegerAttribute(name, "proxool.maximum-active-time", value, 300000, newProperties, resultList);
/*     */         }
/* 337 */         else if (equalsProperty(name, "maximum-connection-count")) {
/* 338 */           setIntegerAttribute(name, "proxool.maximum-connection-count", value, 15, newProperties, resultList);
/*     */         }
/* 340 */         else if (equalsProperty(name, "maximum-connection-lifetime")) {
/* 341 */           setIntegerAttribute(name, "proxool.maximum-connection-lifetime", value, 14400000, newProperties, resultList);
/*     */         }
/* 343 */         else if (equalsProperty(name, "maximum-new-connections")) {
/* 344 */           setIntegerAttribute(name, "proxool.maximum-new-connections", value, 10, newProperties, resultList);
/*     */         }
/* 346 */         else if (equalsProperty(name, "simultaneous-build-throttle")) {
/* 347 */           setIntegerAttribute(name, "proxool.simultaneous-build-throttle", value, 10, newProperties, resultList);
/*     */         }
/* 349 */         else if (equalsProperty(name, "minimum-connection-count")) {
/* 350 */           checkAssignable(name, Integer.class, value);
/* 351 */           newProperties.setProperty("proxool.minimum-connection-count", value.toString());
/* 352 */           resultList.add(new Attribute(name, value));
/* 353 */         } else if (equalsProperty(name, "overload-without-refusal-lifetime")) {
/* 354 */           setIntegerAttribute(name, "proxool.overload-without-refusal-lifetime", value, 60000, newProperties, resultList);
/*     */         }
/* 356 */         else if (equalsProperty(name, "prototype-count")) {
/* 357 */           checkAssignable(name, Integer.class, value);
/* 358 */           newProperties.setProperty("proxool.prototype-count", value.toString());
/* 359 */           resultList.add(new Attribute(name, value));
/* 360 */         } else if (equalsProperty(name, "recently-started-threshold")) {
/* 361 */           setIntegerAttribute(name, "proxool.recently-started-threshold", value, 60000, newProperties, resultList);
/*     */         }
/* 363 */         else if (equalsProperty(name, "statistics")) {
/* 364 */           checkAssignable(name, String.class, value);
/* 365 */           if (notEmpty(value))
/* 366 */             newProperties.setProperty("proxool.statistics", value.toString());
/*     */           else {
/* 368 */             newProperties.setProperty("proxool.statistics", "");
/*     */           }
/* 370 */           resultList.add(new Attribute(name, value));
/* 371 */         } else if (equalsProperty(name, "statistics-log-level")) {
/* 372 */           checkAssignable(name, String.class, value);
/* 373 */           if (notEmpty(value))
/* 374 */             newProperties.setProperty("proxool.statistics-log-level", value.toString());
/*     */           else {
/* 376 */             newProperties.setProperty("proxool.statistics-log-level", "");
/*     */           }
/* 378 */           resultList.add(new Attribute(name, value));
/* 379 */         } else if (equalsProperty(name, "trace")) {
/* 380 */           checkAssignable(name, Boolean.class, value);
/* 381 */           newProperties.setProperty("proxool.trace", value.toString());
/* 382 */           resultList.add(new Attribute(name, value));
/* 383 */         } else if (equalsProperty(name, "verbose")) {
/* 384 */           checkAssignable(name, Boolean.class, value);
/* 385 */           newProperties.setProperty("proxool.verbose", value.toString());
/* 386 */           resultList.add(new Attribute(name, value));
/* 387 */         } else if (equalsProperty(name, "fatal-sql-exception-wrapper-class")) {
/* 388 */           checkAssignable(name, Boolean.class, value);
/* 389 */           newProperties.setProperty("proxool.fatal-sql-exception-wrapper-class", value.toString());
/* 390 */           resultList.add(new Attribute(name, value));
/*     */         } else {
/* 392 */           String message = "Unknown attribute: " + name;
/* 393 */           LOG.error(message);
/* 394 */           throw new AttributeNotFoundException(message);
/*     */         }
/*     */       } catch (InvalidAttributeValueException e) {
/* 397 */         String message = "Attribute value was illegal: " + e.getMessage();
/* 398 */         LOG.error(message);
/* 399 */         throw new RuntimeOperationsException(new RuntimeException(message));
/*     */       } catch (AttributeNotFoundException e) {
/* 401 */         throw new RuntimeOperationsException(new IllegalArgumentException(e.getMessage()));
/*     */       }
/*     */     }
/*     */     try {
/* 405 */       ProxoolFacade.updateConnectionPool("proxool." + this.poolDefinition.getAlias(), newProperties);
/*     */     } catch (ProxoolException e) {
/* 407 */       LOG.error("Update of Proxool pool failed: ", e);
/* 408 */       throw new RuntimeOperationsException(new RuntimeException(e.getMessage()));
/*     */     }
/* 410 */     return resultList;
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 417 */     if (operationName == null)
/* 418 */       throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + CLASS_NAME);
/* 419 */     if (operationName.equals("shutdown")) {
/*     */       try {
/* 421 */         ProxoolFacade.removeConnectionPool(this.poolDefinition.getAlias());
/*     */       } catch (ProxoolException e) {
/* 423 */         LOG.error("Shutdown of pool " + this.poolDefinition.getAlias() + " failed.", e);
/*     */       }
/* 425 */       return null;
/*     */     }
/* 427 */     throw new ReflectionException(new NoSuchMethodException(operationName), "Cannot find the operation " + operationName + ".");
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 436 */     return this.mBeanInfo;
/*     */   }
/*     */ 
/*     */   private MBeanInfo getDynamicMBeanInfo(String alias) {
/* 440 */     MBeanAttributeInfo[] attributeInfos = { createProxoolAttribute("alias", String.class, false), createProxoolAttribute("driver-properties", String.class), createProxoolAttribute("driver-url", String.class), createProxoolAttribute("fatal-sql-exception", String.class), createProxoolAttribute("house-keeping-sleep-time", Integer.class), createProxoolAttribute("house-keeping-test-sql", String.class), createProxoolAttribute("test-before-use", Boolean.class), createProxoolAttribute("test-after-use", Boolean.class), createProxoolAttribute("maximum-active-time", Integer.class), createProxoolAttribute("maximum-connection-count", Integer.class), createProxoolAttribute("maximum-connection-lifetime", Integer.class), createProxoolAttribute("simultaneous-build-throttle", Integer.class), createProxoolAttribute("minimum-connection-count", Integer.class), createProxoolAttribute("overload-without-refusal-lifetime", Integer.class), createProxoolAttribute("prototype-count", Integer.class), createProxoolAttribute("recently-started-threshold", Integer.class), createProxoolAttribute("statistics", String.class), createProxoolAttribute("statistics-log-level", String.class), createProxoolAttribute("trace", Boolean.class), createProxoolAttribute("verbose", Boolean.class), createProxoolAttribute("fatal-sql-exception-wrapper-class", String.class) };
/*     */ 
/* 464 */     MBeanConstructorInfo[] constructorInfos = { new MBeanConstructorInfo("ConnectionPoolMBean(): Construct a ConnectionPoolMBean object.", ConnectionPoolMBean.class.getConstructors()[0]) };
/*     */ 
/* 468 */     MBeanOperationInfo[] operationInfos = { new MBeanOperationInfo("shutdown", "Stop and dispose this connection pool.", new MBeanParameterInfo[0], "void", 1) };
/*     */ 
/* 473 */     return new MBeanInfo(CLASS_NAME, MessageFormat.format(getJMXText("mbean.pool.description"), new Object[] { alias }), attributeInfos, constructorInfos, operationInfos, new MBeanNotificationInfo[0]);
/*     */   }
/*     */ 
/*     */   private static String getAttributeDescription(String attributeName)
/*     */   {
/* 478 */     String description = "";
/* 479 */     if (ATTRIBUTE_DESCRIPTIONS_RESOURCE != null) {
/*     */       try {
/* 481 */         description = ATTRIBUTE_DESCRIPTIONS_RESOURCE.getString(attributeName);
/*     */       } catch (Exception e) {
/* 483 */         LOG.warn("Could not get description for attribute '" + attributeName + "' from resource " + "org.logicalcobwebs.proxool.resources.attributeDescriptions" + ".");
/*     */       }
/*     */     }
/* 486 */     return description;
/*     */   }
/*     */ 
/*     */   private static String getJMXText(String key) {
/* 490 */     String value = "";
/* 491 */     if (JMX_RESOURCE != null) {
/*     */       try {
/* 493 */         value = JMX_RESOURCE.getString(key);
/*     */       } catch (Exception e) {
/* 495 */         LOG.warn("Could not get value for attribute '" + key + "' from resource " + "org.logicalcobwebs.proxool.resources.jmx" + ".");
/*     */       }
/*     */     }
/* 498 */     return value;
/*     */   }
/*     */ 
/*     */   private static ResourceBundle createAttributeDescriptionsResource() {
/*     */     try {
/* 503 */       return ResourceBundle.getBundle("org.logicalcobwebs.proxool.resources.attributeDescriptions");
/*     */     } catch (Exception e) {
/* 505 */       LOG.error("Could not find resource org.logicalcobwebs.proxool.resources.attributeDescriptions", e);
/*     */     }
/* 507 */     return null;
/*     */   }
/*     */ 
/*     */   private static ResourceBundle createJMXResource() {
/*     */     try {
/* 512 */       return ResourceBundle.getBundle("org.logicalcobwebs.proxool.resources.jmx");
/*     */     } catch (Exception e) {
/* 514 */       LOG.error("Could not find resource org.logicalcobwebs.proxool.resources.jmx", e);
/*     */     }
/* 516 */     return null;
/*     */   }
/*     */ 
/*     */   private static MBeanAttributeInfo createProxoolAttribute(String attributeName, Class type) {
/* 520 */     return createProxoolAttribute(attributeName, type, true);
/*     */   }
/*     */ 
/*     */   private static MBeanAttributeInfo createProxoolAttribute(String attributeName, Class type, boolean writable) {
/* 524 */     return new MBeanAttributeInfo(ProxoolJMXHelper.getValidIdentifier(attributeName), type.getName(), getAttributeDescription(attributeName), true, writable, false);
/*     */   }
/*     */ 
/*     */   private void checkAssignable(String name, Class clazz, Object value) throws InvalidAttributeValueException
/*     */   {
/* 529 */     if (value == null) {
/* 530 */       if (!String.class.equals(clazz))
/* 531 */         throw new InvalidAttributeValueException("Cannot set attribute " + name + " to null " + " an instance of " + clazz.getName() + " expected.");
/*     */     }
/*     */     else
/*     */     {
/* 535 */       Class valueClass = value.getClass();
/* 536 */       if (!clazz.isAssignableFrom(valueClass))
/* 537 */         throw new InvalidAttributeValueException("Cannot set attribute " + name + " to a " + valueClass.getName() + " instance, " + clazz.getName() + " expected.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean equalsProperty(String beanAttribute, String proxoolProperty)
/*     */   {
/* 544 */     return beanAttribute.equals(ProxoolJMXHelper.getValidIdentifier(proxoolProperty));
/*     */   }
/*     */ 
/*     */   private void setDelegateProperties(Properties properties, String propertyString) throws InvalidAttributeValueException
/*     */   {
/* 549 */     if ((propertyString == null) || (propertyString.trim().length() == 0)) {
/* 550 */       return;
/*     */     }
/* 552 */     StringTokenizer tokenizer = new StringTokenizer(propertyString, ",");
/* 553 */     String keyValuePair = null;
/* 554 */     int equalsIndex = -1;
/* 555 */     while (tokenizer.hasMoreElements()) {
/* 556 */       keyValuePair = tokenizer.nextToken().trim();
/* 557 */       equalsIndex = keyValuePair.indexOf("=");
/* 558 */       if (equalsIndex != -1) {
/* 559 */         properties.put(keyValuePair.substring(0, equalsIndex).trim(), keyValuePair.substring(equalsIndex + 1).trim()); continue;
/*     */       }
/*     */ 
/* 562 */       throw new InvalidAttributeValueException("Could not find key/value delimiter '=' in property definition: '" + keyValuePair + "'.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getDelegatePropertiesAsString(Properties properties)
/*     */   {
/* 569 */     StringBuffer result = new StringBuffer();
/* 570 */     Iterator keyIterator = properties.keySet().iterator();
/* 571 */     String key = null;
/* 572 */     boolean first = true;
/* 573 */     while (keyIterator.hasNext()) {
/* 574 */       key = (String)keyIterator.next();
/* 575 */       if (!key.startsWith("proxool.")) {
/* 576 */         if (!first)
/* 577 */           result.append(", ");
/*     */         else {
/* 579 */           first = false;
/*     */         }
/* 581 */         result.append(key).append("=").append(properties.getProperty(key));
/*     */       }
/*     */     }
/* 584 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private boolean notEmpty(Object object) {
/* 588 */     return (object != null) && (object.toString().trim().length() > 0);
/*     */   }
/*     */ 
/*     */   private boolean notEmptyOrZero(Integer integer) {
/* 592 */     return (integer != null) && (integer.intValue() > 0);
/*     */   }
/*     */ 
/*     */   private String getValueOrEmpty(String property) {
/* 596 */     return property == null ? "" : property;
/*     */   }
/*     */ 
/*     */   private void setIntegerAttribute(String attributeName, String propertyName, Object value, int defaultValue, Properties properties, AttributeList resultList) throws InvalidAttributeValueException
/*     */   {
/* 601 */     checkAssignable(attributeName, Integer.class, value);
/* 602 */     if (notEmptyOrZero((Integer)value)) {
/* 603 */       properties.setProperty(propertyName, value.toString());
/* 604 */       resultList.add(new Attribute(attributeName, value));
/*     */     } else {
/* 606 */       resultList.add(new Attribute(attributeName, new Integer(defaultValue)));
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isEqualProperties(String property1, String property2)
/*     */   {
/* 612 */     if (property1 == null)
/* 613 */       return property2 == null;
/* 614 */     if (property2 == null) {
/* 615 */       return property1 == null;
/*     */     }
/* 617 */     return property1.equals(property2);
/*     */   }
/*     */ 
/*     */   private static MBeanNotificationInfo[] getNotificationInfos()
/*     */   {
/* 622 */     return new MBeanNotificationInfo[] { new MBeanNotificationInfo(new String[] { "proxool.definitionUpdated" }, Notification.class.getName(), getJMXText("mbean.notification.description")) };
/*     */   }
/*     */ 
/*     */   public void onRegistration(ConnectionPoolDefinitionIF connectionPoolDefinition, Properties completeInfo)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onShutdown(String alias)
/*     */   {
/* 643 */     if ((alias.equals(this.poolDefinition.getAlias())) && 
/* 644 */       (this.active)) {
/* 645 */       this.active = false;
/* 646 */       ProxoolJMXHelper.unregisterPool(this.poolDefinition.getAlias(), this.poolProperties);
/* 647 */       LOG.info(this.poolDefinition.getAlias() + " MBean unregistered.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void definitionUpdated(ConnectionPoolDefinitionIF connectionPoolDefinition, Properties completeInfo, Properties changedInfo)
/*     */   {
/* 658 */     this.poolDefinition = connectionPoolDefinition;
/* 659 */     this.poolProperties = completeInfo;
/* 660 */     this.notificationHelper.sendNotification(new Notification("proxool.definitionUpdated", this, this.definitionUpdatedSequence++, System.currentTimeMillis(), getJMXText("mbean.notification.defUpdated")));
/*     */   }
/*     */ 
/*     */   public void addNotificationListener(NotificationListener notificationListener, NotificationFilter notificationFilter, Object handBack)
/*     */     throws IllegalArgumentException
/*     */   {
/* 670 */     this.notificationHelper.addNotificationListener(notificationListener, notificationFilter, handBack);
/*     */   }
/*     */ 
/*     */   public void removeNotificationListener(NotificationListener notificationListener)
/*     */     throws ListenerNotFoundException
/*     */   {
/* 677 */     this.notificationHelper.removeNotificationListener(notificationListener);
/*     */   }
/*     */ 
/*     */   public MBeanNotificationInfo[] getNotificationInfo()
/*     */   {
/* 684 */     return NOTIFICATION_INFOS;
/*     */   }
/*     */ 
/*     */   public ObjectName preRegister(MBeanServer mBeanServer, ObjectName objectName)
/*     */     throws Exception
/*     */   {
/* 691 */     if (objectName == null) {
/* 692 */       throw new ProxoolException("objectName was null, but we can not construct an MBean instance without knowing the pool alias.");
/*     */     }
/*     */ 
/* 695 */     return objectName;
/*     */   }
/*     */ 
/*     */   public void postRegister(Boolean success)
/*     */   {
/* 702 */     if (success.booleanValue() == true)
/* 703 */       this.active = true;
/*     */   }
/*     */ 
/*     */   public void preDeregister()
/*     */     throws Exception
/*     */   {
/* 711 */     this.active = false;
/*     */   }
/*     */ 
/*     */   public void postDeregister()
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.jmx.ConnectionPoolMBean
 * JD-Core Version:    0.6.0
 */