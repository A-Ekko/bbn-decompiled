/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.io.amf.ASObject;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class BeanProxy extends AbstractProxy
/*     */ {
/*     */   static final long serialVersionUID = 7365078101695257715L;
/*     */   protected static final String LOG_CATEGORY = "Endpoint.Type";
/*     */   private static final int FAILED_PROPERTY_READ_ERROR = 10021;
/*     */   private static final int FAILED_PROPERTY_WRITE_ERROR = 10022;
/*     */   private static final int NON_READABLE_PROPERTY_ERROR = 10023;
/*     */   private static final int NON_WRITABLE_PROPERTY_ERROR = 10024;
/*     */   private static final int UNKNOWN_PROPERTY_ERROR = 10025;
/*  63 */   protected static final Map propertyNamesCache = new IdentityHashMap();
/*  64 */   protected static final Map beanPropertyCache = new IdentityHashMap();
/*  65 */   protected static final Map propertyDescriptorCache = new IdentityHashMap();
/*     */ 
/*  67 */   protected boolean cacheProperties = true;
/*  68 */   protected boolean cachePropertiesDescriptors = true;
/*  69 */   protected Class stopClass = Object.class;
/*     */ 
/*  71 */   protected static final Map ignoreProperties = new HashMap();
/*     */ 
/*     */   public BeanProxy()
/*     */   {
/*  85 */     this(null);
/*     */   }
/*     */ 
/*     */   public BeanProxy(Object defaultInstance)
/*     */   {
/*  90 */     super(defaultInstance);
/*     */ 
/*  94 */     if (defaultInstance != null)
/*     */     {
/*  96 */       this.alias = getClassName(defaultInstance);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getAlias(Object instance)
/*     */   {
/* 102 */     return getClassName(instance);
/*     */   }
/*     */ 
/*     */   public List getPropertyNames(Object instance)
/*     */   {
/* 107 */     if (instance == null) {
/* 108 */       return null;
/*     */     }
/* 110 */     Class c = instance.getClass();
/* 111 */     List propertyNames = null;
/* 112 */     Map properties = null;
/*     */ 
/* 114 */     if (this.descriptor == null)
/*     */     {
/* 116 */       propertyNames = (List)propertyNamesCache.get(c);
/*     */     }
/*     */ 
/* 119 */     if (propertyNames != null)
/*     */     {
/* 121 */       return propertyNames;
/*     */     }
/*     */ 
/* 125 */     properties = getBeanProperties(instance);
/*     */ 
/* 129 */     propertyNames = new ArrayList(properties.size());
/* 130 */     Iterator it = properties.keySet().iterator();
/* 131 */     while (it.hasNext())
/*     */     {
/* 133 */       propertyNames.add(it.next().toString());
/*     */     }
/*     */ 
/* 136 */     if ((this.cacheProperties) && (this.descriptor == null))
/*     */     {
/* 138 */       synchronized (propertyNamesCache)
/*     */       {
/* 140 */         List propertyNames2 = (List)propertyNamesCache.get(c);
/* 141 */         if (propertyNames2 == null)
/* 142 */           propertyNamesCache.put(c, propertyNames);
/*     */         else {
/* 144 */           propertyNames = propertyNames2;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 149 */     return propertyNames;
/*     */   }
/*     */ 
/*     */   public Class getType(Object instance, String propertyName)
/*     */   {
/* 155 */     if ((instance == null) || (propertyName == null)) {
/* 156 */       return null;
/*     */     }
/* 158 */     BeanProperty bp = getBeanProperty(instance, propertyName);
/*     */ 
/* 160 */     if (bp != null)
/*     */     {
/* 162 */       return bp.getType();
/*     */     }
/*     */ 
/* 165 */     return null;
/*     */   }
/*     */ 
/*     */   public Object getValue(Object instance, String propertyName)
/*     */   {
/* 170 */     if ((instance == null) || (propertyName == null))
/* 171 */       return null;
/* 172 */     BeanProperty bp = getBeanProperty(instance, propertyName);
/*     */ 
/* 174 */     if (bp != null)
/*     */     {
/* 176 */       return getBeanValue(instance, bp);
/*     */     }
/*     */ 
/* 180 */     SerializationContext context = getSerializationContext();
/* 181 */     if (!ignorePropertyErrors(context))
/*     */     {
/* 184 */       MessageException ex = new MessageException();
/* 185 */       ex.setMessage(10025, new Object[] { propertyName, getAlias(instance) });
/* 186 */       throw ex;
/*     */     }
/*     */ 
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */   protected final Object getBeanValue(Object instance, BeanProperty bp)
/*     */   {
/* 194 */     String propertyName = bp.getName();
/* 195 */     if (bp.isRead())
/*     */     {
/*     */       try
/*     */       {
/* 199 */         Object value = bp.get(instance);
/* 200 */         if ((value != null) && (this.descriptor != null))
/*     */         {
/* 202 */           SerializationDescriptor subDescriptor = (SerializationDescriptor)this.descriptor.get(propertyName);
/* 203 */           if (subDescriptor != null)
/*     */           {
/* 205 */             PropertyProxy subProxy = PropertyProxyRegistry.getProxyAndRegister(value);
/* 206 */             subProxy = (PropertyProxy)subProxy.clone();
/* 207 */             subProxy.setDescriptor(subDescriptor);
/* 208 */             subProxy.setDefaultInstance(value);
/* 209 */             value = subProxy;
/*     */           }
/*     */         }
/*     */ 
/* 213 */         return value;
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 217 */         SerializationContext context = getSerializationContext();
/*     */ 
/* 220 */         if ((Log.isWarn()) && (logPropertyErrors(context)))
/*     */         {
/* 222 */           Logger log = Log.getLogger("Endpoint.Type");
/* 223 */           log.warn("Failed to get property {0} on type {1}.", new Object[] { propertyName, getAlias(instance) }, e);
/*     */         }
/*     */ 
/* 227 */         if (!ignorePropertyErrors(context))
/*     */         {
/* 230 */           MessageException ex = new MessageException();
/* 231 */           ex.setMessage(10021, new Object[] { propertyName, getAlias(instance) });
/* 232 */           ex.setRootCause(e);
/* 233 */           throw ex;
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 239 */       SerializationContext context = getSerializationContext();
/* 240 */       if (!ignorePropertyErrors(context))
/*     */       {
/* 243 */         MessageException ex = new MessageException();
/* 244 */         ex.setMessage(10023, new Object[] { propertyName, getAlias(instance) });
/* 245 */         throw ex;
/*     */       }
/*     */     }
/*     */ 
/* 249 */     return null;
/*     */   }
/*     */ 
/*     */   public void setValue(Object instance, String propertyName, Object value)
/*     */   {
/* 254 */     BeanProperty bp = getBeanProperty(instance, propertyName);
/*     */ 
/* 256 */     if (bp != null)
/*     */     {
/* 258 */       if (bp.isWrite())
/*     */       {
/*     */         try
/*     */         {
/* 262 */           Class desiredPropClass = bp.getType();
/* 263 */           TypeMarshaller marshaller = TypeMarshallingContext.getTypeMarshaller();
/* 264 */           value = marshaller.convert(value, desiredPropClass);
/* 265 */           bp.set(instance, value);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 269 */           SerializationContext context = getSerializationContext();
/*     */ 
/* 272 */           if ((Log.isWarn()) && (logPropertyErrors(context)))
/*     */           {
/* 274 */             Logger log = Log.getLogger("Endpoint.Type");
/* 275 */             log.warn("Failed to set property {0} on type {1}.", new Object[] { propertyName, getAlias(instance) }, e);
/*     */           }
/*     */ 
/* 279 */           if (!ignorePropertyErrors(context))
/*     */           {
/* 282 */             MessageException ex = new MessageException();
/* 283 */             ex.setMessage(10022, new Object[] { propertyName, getAlias(instance) });
/* 284 */             ex.setRootCause(e);
/* 285 */             throw ex;
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 291 */         SerializationContext context = getSerializationContext();
/*     */ 
/* 293 */         if ((Log.isWarn()) && (logPropertyErrors(context)))
/*     */         {
/* 295 */           Logger log = Log.getLogger("Endpoint.Type");
/* 296 */           log.warn("Property {0} not writable on class {1}", new Object[] { propertyName, getAlias(instance) });
/*     */         }
/*     */ 
/* 300 */         if (!ignorePropertyErrors(context))
/*     */         {
/* 303 */           MessageException ex = new MessageException();
/* 304 */           ex.setMessage(10024, new Object[] { propertyName, getAlias(instance) });
/* 305 */           throw ex;
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 311 */       SerializationContext context = getSerializationContext();
/*     */ 
/* 313 */       if ((Log.isWarn()) && (logPropertyErrors(context)))
/*     */       {
/* 315 */         Logger log = Log.getLogger("Endpoint.Type");
/* 316 */         log.warn("Ignoring set property {0} for type {1} as a setter could not be found.", new Object[] { propertyName, getAlias(instance) });
/*     */       }
/*     */ 
/* 320 */       if (!ignorePropertyErrors(context))
/*     */       {
/* 323 */         MessageException ex = new MessageException();
/* 324 */         ex.setMessage(10025, new Object[] { propertyName, getAlias(instance) });
/* 325 */         throw ex;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean ignorePropertyErrors(SerializationContext context)
/*     */   {
/* 332 */     return context.ignorePropertyErrors;
/*     */   }
/*     */ 
/*     */   protected boolean logPropertyErrors(SerializationContext context)
/*     */   {
/* 337 */     return context.logPropertyErrors;
/*     */   }
/*     */ 
/*     */   protected String getClassName(Object instance)
/*     */   {
/* 351 */     String className = null;
/*     */ 
/* 353 */     if ((instance instanceof ASObject))
/*     */     {
/* 355 */       className = ((ASObject)instance).getType();
/*     */     }
/* 357 */     else if ((instance instanceof ClassAlias))
/*     */     {
/* 359 */       className = ((ClassAlias)instance).getAlias();
/*     */     }
/*     */     else
/*     */     {
/* 363 */       className = instance.getClass().getName();
/*     */     }
/*     */ 
/* 366 */     return className;
/*     */   }
/*     */ 
/*     */   protected Map getBeanProperties(Object instance)
/*     */   {
/* 371 */     Class c = instance.getClass();
/*     */ 
/* 373 */     if (this.descriptor == null)
/*     */     {
/* 375 */       Map props = (Map)beanPropertyCache.get(c);
/* 376 */       if (props != null)
/*     */       {
/* 378 */         return props;
/*     */       }
/*     */     }
/*     */ 
/* 382 */     Map props = new HashMap();
/* 383 */     PropertyDescriptor[] pds = getPropertyDescriptors(c);
/* 384 */     if (pds == null) {
/* 385 */       return null;
/*     */     }
/* 387 */     List excludes = null;
/* 388 */     if (this.descriptor != null)
/*     */     {
/* 390 */       excludes = this.descriptor.getExcludesForInstance(instance);
/* 391 */       if (excludes == null) {
/* 392 */         excludes = this.descriptor.getExcludes();
/*     */       }
/*     */     }
/*     */ 
/* 396 */     for (int i = 0; i < pds.length; i++)
/*     */     {
/* 398 */       PropertyDescriptor pd = pds[i];
/* 399 */       String propertyName = pd.getName();
/* 400 */       Method readMethod = pd.getReadMethod();
/* 401 */       Method writeMethod = pd.getWriteMethod();
/*     */ 
/* 403 */       if ((readMethod == null) || (!isPublicAccessor(readMethod.getModifiers())))
/*     */         continue;
/* 405 */       if ((!this.includeReadOnly) && (writeMethod == null)) {
/*     */         continue;
/*     */       }
/* 408 */       if ((excludes != null) && (excludes.contains(propertyName))) {
/*     */         continue;
/*     */       }
/* 411 */       if (isPropertyIgnored(c, propertyName)) {
/*     */         continue;
/*     */       }
/* 414 */       props.put(propertyName, new BeanProperty(propertyName, pd.getPropertyType(), readMethod, writeMethod, null));
/*     */     }
/*     */ 
/* 420 */     Field[] fields = instance.getClass().getFields();
/* 421 */     for (int i = 0; i < fields.length; i++)
/*     */     {
/* 423 */       Field field = fields[i];
/* 424 */       String propertyName = field.getName();
/* 425 */       int modifiers = field.getModifiers();
/* 426 */       if ((!isPublicField(modifiers)) || (props.containsKey(propertyName)))
/*     */         continue;
/* 428 */       if ((excludes != null) && (excludes.contains(propertyName))) {
/*     */         continue;
/*     */       }
/* 431 */       if (isPropertyIgnored(c, propertyName)) {
/*     */         continue;
/*     */       }
/* 434 */       props.put(propertyName, new BeanProperty(propertyName, field.getType(), null, null, field));
/*     */     }
/*     */ 
/* 439 */     if ((this.descriptor == null) && (this.cacheProperties))
/*     */     {
/* 441 */       synchronized (beanPropertyCache)
/*     */       {
/* 443 */         Map props2 = (Map)beanPropertyCache.get(c);
/* 444 */         if (props2 == null)
/* 445 */           beanPropertyCache.put(c, props);
/*     */         else {
/* 447 */           props = props2;
/*     */         }
/*     */       }
/*     */     }
/* 451 */     return props;
/*     */   }
/*     */ 
/*     */   protected final BeanProperty getBeanProperty(Object instance, String propertyName)
/*     */   {
/* 456 */     Class c = instance.getClass();
/*     */ 
/* 460 */     if ((this.descriptor == null) && (this.cacheProperties))
/*     */     {
/* 462 */       Map props = getBeanProperties(instance);
/* 463 */       if (props != null) {
/* 464 */         return (BeanProperty)props.get(propertyName);
/*     */       }
/* 466 */       return null;
/*     */     }
/*     */ 
/* 470 */     PropertyDescriptorCacheEntry pce = getPropertyDescriptorCacheEntry(c);
/* 471 */     if (pce == null) {
/* 472 */       return null;
/*     */     }
/* 474 */     Object pType = pce.propertiesByName.get(propertyName);
/* 475 */     if (pType == null) {
/* 476 */       return null;
/*     */     }
/* 478 */     List excludes = null;
/* 479 */     if (this.descriptor != null)
/*     */     {
/* 481 */       excludes = this.descriptor.getExcludesForInstance(instance);
/* 482 */       if (excludes == null) {
/* 483 */         excludes = this.descriptor.getExcludes();
/*     */       }
/*     */     }
/* 486 */     if ((pType instanceof PropertyDescriptor))
/*     */     {
/* 488 */       PropertyDescriptor pd = (PropertyDescriptor)pType;
/*     */ 
/* 490 */       Method readMethod = pd.getReadMethod();
/* 491 */       Method writeMethod = pd.getWriteMethod();
/*     */ 
/* 493 */       if ((readMethod != null) && (isPublicAccessor(readMethod.getModifiers())))
/*     */       {
/* 495 */         if ((!this.includeReadOnly) && (writeMethod == null)) {
/* 496 */           return null;
/*     */         }
/* 498 */         if ((excludes != null) && (excludes.contains(propertyName))) {
/* 499 */           return null;
/*     */         }
/* 501 */         if (isPropertyIgnored(c, propertyName)) {
/* 502 */           return null;
/*     */         }
/* 504 */         return new BeanProperty(propertyName, pd.getPropertyType(), readMethod, writeMethod, null);
/*     */       }
/*     */     }
/* 507 */     else if ((pType instanceof Field))
/*     */     {
/* 509 */       Field field = (Field)pType;
/*     */ 
/* 511 */       String pName = field.getName();
/* 512 */       int modifiers = field.getModifiers();
/* 513 */       if ((isPublicField(modifiers)) && (pName.equals(propertyName)))
/*     */       {
/* 515 */         if ((excludes != null) && (excludes.contains(propertyName))) {
/* 516 */           return null;
/*     */         }
/* 518 */         if (isPropertyIgnored(c, propertyName)) {
/* 519 */           return null;
/*     */         }
/* 521 */         return new BeanProperty(propertyName, field.getType(), null, null, field);
/*     */       }
/*     */     }
/*     */ 
/* 525 */     return null;
/*     */   }
/*     */ 
/*     */   private PropertyDescriptor[] getPropertyDescriptors(Class c)
/*     */   {
/* 530 */     PropertyDescriptorCacheEntry pce = getPropertyDescriptorCacheEntry(c);
/* 531 */     if (pce == null)
/* 532 */       return null;
/* 533 */     return pce.propertyDescriptors;
/*     */   }
/*     */ 
/*     */   private PropertyDescriptorCacheEntry getPropertyDescriptorCacheEntry(Class c)
/*     */   {
/* 538 */     PropertyDescriptorCacheEntry pce = (PropertyDescriptorCacheEntry)propertyDescriptorCache.get(c);
/*     */     try
/*     */     {
/* 542 */       if (pce == null)
/*     */       {
/* 544 */         BeanInfo beanInfo = Introspector.getBeanInfo(c, this.stopClass);
/* 545 */         pce = new PropertyDescriptorCacheEntry();
/* 546 */         pce.propertyDescriptors = beanInfo.getPropertyDescriptors();
/* 547 */         pce.propertiesByName = createPropertiesByNameMap(pce.propertyDescriptors, c.getFields());
/* 548 */         if (this.cachePropertiesDescriptors)
/*     */         {
/* 550 */           synchronized (propertyDescriptorCache)
/*     */           {
/* 552 */             PropertyDescriptorCacheEntry pce2 = (PropertyDescriptorCacheEntry)propertyDescriptorCache.get(c);
/* 553 */             if (pce2 == null)
/* 554 */               propertyDescriptorCache.put(c, pce);
/*     */             else
/* 556 */               pce = pce2;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IntrospectionException ex)
/*     */     {
/* 563 */       return null;
/*     */     }
/* 565 */     return pce;
/*     */   }
/*     */ 
/*     */   private Map createPropertiesByNameMap(PropertyDescriptor[] pds, Field[] fields)
/*     */   {
/* 570 */     Map m = new HashMap(pds.length);
/* 571 */     for (int i = 0; i < pds.length; i++)
/*     */     {
/* 573 */       PropertyDescriptor pd = pds[i];
/* 574 */       Method readMethod = pd.getReadMethod();
/* 575 */       if ((readMethod == null) || (!isPublicAccessor(readMethod.getModifiers())) || ((!this.includeReadOnly) && (pd.getWriteMethod() == null)))
/*     */         continue;
/* 577 */       m.put(pd.getName(), pd);
/*     */     }
/* 579 */     for (int i = 0; i < fields.length; i++)
/*     */     {
/* 581 */       Field field = fields[i];
/*     */ 
/* 583 */       if ((isPublicField(field.getModifiers())) && (!m.containsKey(field.getName())))
/* 584 */         m.put(field.getName(), field);
/*     */     }
/* 586 */     return m;
/*     */   }
/*     */ 
/*     */   public static boolean isPropertyIgnored(Class c, String propertyName)
/*     */   {
/* 591 */     boolean result = false;
/* 592 */     Set propertyOwners = (Set)ignoreProperties.get(propertyName);
/* 593 */     if (propertyOwners != null)
/*     */     {
/* 595 */       while (c != null)
/*     */       {
/* 597 */         if (propertyOwners.contains(c))
/*     */         {
/* 599 */           result = true;
/* 600 */           break;
/*     */         }
/* 602 */         c = c.getSuperclass();
/*     */       }
/*     */     }
/* 605 */     return result;
/*     */   }
/*     */ 
/*     */   public static void addIgnoreProperty(Class c, String propertyName)
/*     */   {
/* 610 */     synchronized (ignoreProperties)
/*     */     {
/* 612 */       Set propertyOwners = (Set)ignoreProperties.get(propertyName);
/* 613 */       if (propertyOwners == null)
/*     */       {
/* 615 */         propertyOwners = new HashSet();
/* 616 */         ignoreProperties.put(propertyName, propertyOwners);
/*     */       }
/* 618 */       propertyOwners.add(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isPublicField(int modifiers)
/*     */   {
/* 627 */     return (Modifier.isPublic(modifiers)) && (!Modifier.isFinal(modifiers)) && (!Modifier.isStatic(modifiers)) && (!Modifier.isTransient(modifiers));
/*     */   }
/*     */ 
/*     */   public static boolean isPublicAccessor(int modifiers)
/*     */   {
/* 639 */     return (Modifier.isPublic(modifiers)) && (!Modifier.isStatic(modifiers));
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 750 */     BeanProxy proxy = new BeanProxy();
/* 751 */     proxy.setCloneFieldsFrom(this);
/* 752 */     return proxy;
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 760 */     synchronized (ignoreProperties)
/*     */     {
/* 762 */       ignoreProperties.clear();
/*     */     }
/* 764 */     synchronized (propertyNamesCache)
/*     */     {
/* 766 */       propertyNamesCache.clear();
/*     */     }
/* 768 */     synchronized (beanPropertyCache)
/*     */     {
/* 770 */       beanPropertyCache.clear();
/*     */     }
/* 772 */     synchronized (propertyDescriptorCache)
/*     */     {
/* 774 */       propertyDescriptorCache.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     addIgnoreProperty(AbstractMap.class, "empty");
/*  75 */     addIgnoreProperty(AbstractCollection.class, "empty");
/*  76 */     addIgnoreProperty(ASObject.class, "type");
/*  77 */     addIgnoreProperty(Throwable.class, "stackTrace");
/*  78 */     addIgnoreProperty(File.class, "parentFile");
/*  79 */     addIgnoreProperty(File.class, "canonicalFile");
/*  80 */     addIgnoreProperty(File.class, "absoluteFile");
/*     */   }
/*     */ 
/*     */   protected static class PropertyDescriptorCacheEntry
/*     */   {
/*     */     PropertyDescriptor[] propertyDescriptors;
/*     */     Map propertiesByName;
/*     */   }
/*     */ 
/*     */   protected static class BeanProperty
/*     */   {
/*     */     private String name;
/*     */     private Class type;
/*     */     private Method readMethod;
/*     */     private Method writeMethod;
/*     */     private Field field;
/*     */ 
/*     */     protected BeanProperty(String name, Class type, Method read, Method write, Field field)
/*     */     {
/* 659 */       this.name = name;
/* 660 */       this.type = type;
/* 661 */       this.writeMethod = write;
/* 662 */       this.readMethod = read;
/* 663 */       this.field = field;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 668 */       return this.name;
/*     */     }
/*     */ 
/*     */     public Class getType()
/*     */     {
/* 673 */       return this.type;
/*     */     }
/*     */ 
/*     */     public boolean isWrite()
/*     */     {
/* 678 */       return (this.writeMethod != null) || (this.field != null);
/*     */     }
/*     */ 
/*     */     public boolean isRead()
/*     */     {
/* 683 */       return (this.readMethod != null) || (this.field != null);
/*     */     }
/*     */ 
/*     */     public Class getReadDeclaringClass()
/*     */     {
/* 688 */       if (this.readMethod != null)
/* 689 */         return this.readMethod.getDeclaringClass();
/* 690 */       if (this.field != null) {
/* 691 */         return this.field.getDeclaringClass();
/*     */       }
/* 693 */       return null;
/*     */     }
/*     */ 
/*     */     public Class getReadType()
/*     */     {
/* 698 */       if (this.readMethod != null)
/* 699 */         return this.readMethod.getReturnType();
/* 700 */       if (this.field != null) {
/* 701 */         return this.field.getType();
/*     */       }
/* 703 */       return null;
/*     */     }
/*     */ 
/*     */     public String getWriteName()
/*     */     {
/* 708 */       if (this.writeMethod != null)
/* 709 */         return "method " + this.writeMethod.getName();
/* 710 */       if (this.field != null) {
/* 711 */         return "field " + this.field.getName();
/*     */       }
/* 713 */       return null;
/*     */     }
/*     */ 
/*     */     public void set(Object bean, Object value)
/*     */       throws IllegalAccessException, InvocationTargetException
/*     */     {
/* 719 */       if (this.writeMethod != null)
/*     */       {
/* 721 */         this.writeMethod.invoke(bean, new Object[] { value });
/*     */       }
/* 723 */       else if (this.field != null)
/*     */       {
/* 725 */         this.field.set(bean, value);
/*     */       }
/*     */       else
/*     */       {
/* 729 */         throw new MessageException("Setter not found for property " + this.name);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object get(Object bean) throws IllegalAccessException, InvocationTargetException
/*     */     {
/* 735 */       Object obj = null;
/* 736 */       if (this.readMethod != null)
/*     */       {
/* 738 */         obj = this.readMethod.invoke(bean, null);
/*     */       }
/* 740 */       else if (this.field != null)
/*     */       {
/* 742 */         obj = this.field.get(bean);
/*     */       }
/* 744 */       return obj;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.BeanProxy
 * JD-Core Version:    0.6.0
 */