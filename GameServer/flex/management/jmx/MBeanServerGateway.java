/*     */ package flex.management.jmx;
/*     */ 
/*     */ import flex.management.MBeanServerLocator;
/*     */ import flex.management.MBeanServerLocatorFactory;
/*     */ import flex.management.ManagementException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.management.AttributeList;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.InstanceAlreadyExistsException;
/*     */ import javax.management.InstanceNotFoundException;
/*     */ import javax.management.IntrospectionException;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanRegistrationException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.MalformedObjectNameException;
/*     */ import javax.management.NotCompliantMBeanException;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ 
/*     */ public class MBeanServerGateway
/*     */ {
/*     */   private static final int MALFORMED_OBJECTNAME = 10400;
/*     */   private static final int GETINFO_INTROSPECTION_ERR = 10406;
/*     */   private static final int MBEAN_NOTFOUND = 10407;
/*     */   private static final int GETINFO_REFLECT_ERR = 10408;
/*     */   private static final int ATTRIB_NOTFOUND = 10409;
/*     */   private static final int GETATTRIB_EXCEPTION = 10410;
/*     */   private static final int GETATTRIB_REFLECT_ERR = 10411;
/*     */   private static final int GETATTRIB_NULL_ARGUMENT = 10412;
/*     */   private static final int GETATTRIBS_REFLECT_ERR = 10413;
/*     */   private static final int GETATTRIBS_NULL_ARGUMENT = 10414;
/*     */   private static final int INVOKE_REFLECT_ERR = 10415;
/*     */   private static final int INVOKE_ERR = 10416;
/*     */   private static final int CREATE_ERR = 10417;
/*     */   private static final int INSTANCE_EXISTS = 10418;
/*     */   private static final int NOT_COMPLIANT = 10419;
/*     */   private static final int MBEAN_PREREG_ERR = 10420;
/*     */   private static final int MBEAN_PREDEREG_ERR = 10421;
/*     */   private static final int SETATTRIB_REFLECT_ERR = 10422;
/*     */   private static final int SETATTRIB_EXCEPTION = 10423;
/*     */   private static final int INVALID_ATTRIB_VALUE = 10424;
/*     */   private static final int SETATTRIBS_REFLECT_ERR = 10425;
/*     */   private MBeanServer server;
/*     */ 
/*     */   public MBeanServerGateway()
/*     */   {
/* 120 */     this.server = MBeanServerLocatorFactory.getMBeanServerLocator().getMBeanServer();
/*     */   }
/*     */ 
/*     */   public ObjectInstance createMBean(String className, String objectName)
/*     */   {
/* 138 */     javax.management.ObjectName name = null;
/* 139 */     if (objectName != null)
/* 140 */       name = validateObjectName(objectName); ManagementException me;
/*     */     try {
/* 143 */       return new ObjectInstance(this.server.createMBean(className, name));
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 147 */       ManagementException me = new ManagementException();
/* 148 */       me.setMessage(10417, new Object[] { name });
/* 149 */       me.setRootCause(re);
/* 150 */       throw me;
/*     */     }
/*     */     catch (InstanceAlreadyExistsException iaee)
/*     */     {
/* 154 */       ManagementException me = new ManagementException();
/* 155 */       me.setMessage(10418, new Object[] { name });
/* 156 */       me.setRootCause(iaee);
/* 157 */       throw me;
/*     */     }
/*     */     catch (MBeanException mbe)
/*     */     {
/* 161 */       ManagementException me = new ManagementException();
/* 162 */       me.setMessage(10417, new Object[] { name });
/* 163 */       me.setRootCause(mbe);
/* 164 */       throw me;
/*     */     }
/*     */     catch (NotCompliantMBeanException ncmbe)
/*     */     {
/* 168 */       me = new ManagementException();
/* 169 */       me.setMessage(10419, new Object[] { className });
/* 170 */       me.setRootCause(ncmbe);
/* 171 */     }throw me;
/*     */   }
/*     */ 
/*     */   public ObjectInstance createMBean(String className, String objectName, String loaderName)
/*     */   {
/* 186 */     javax.management.ObjectName name = null;
/* 187 */     javax.management.ObjectName loader = null;
/* 188 */     if (objectName != null)
/* 189 */       name = validateObjectName(objectName);
/* 190 */     if (loaderName != null)
/* 191 */       loader = validateObjectName(loaderName); ManagementException me;
/*     */     try {
/* 194 */       return new ObjectInstance(this.server.createMBean(className, name, loader));
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 198 */       ManagementException me = new ManagementException();
/* 199 */       me.setMessage(10417, new Object[] { name });
/* 200 */       me.setRootCause(re);
/* 201 */       throw me;
/*     */     }
/*     */     catch (InstanceAlreadyExistsException iaee)
/*     */     {
/* 205 */       ManagementException me = new ManagementException();
/* 206 */       me.setMessage(10418, new Object[] { name });
/* 207 */       me.setRootCause(iaee);
/* 208 */       throw me;
/*     */     }
/*     */     catch (MBeanException mbe)
/*     */     {
/* 212 */       ManagementException me = new ManagementException();
/* 213 */       me.setMessage(10417, new Object[] { name });
/* 214 */       me.setRootCause(mbe);
/* 215 */       throw me;
/*     */     }
/*     */     catch (NotCompliantMBeanException ncmbe)
/*     */     {
/* 219 */       ManagementException me = new ManagementException();
/* 220 */       me.setMessage(10419, new Object[] { className });
/* 221 */       me.setRootCause(ncmbe);
/* 222 */       throw me;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 226 */       me = new ManagementException();
/* 227 */       me.setMessage(10407, new Object[] { name });
/* 228 */       me.setRootCause(infe);
/* 229 */     }throw me;
/*     */   }
/*     */ 
/*     */   public ObjectInstance createMBean(String className, String objectName, Object[] params, String[] signature)
/*     */   {
/* 244 */     javax.management.ObjectName name = null;
/* 245 */     if (objectName != null)
/* 246 */       name = validateObjectName(objectName); ManagementException me;
/*     */     try {
/* 249 */       return new ObjectInstance(this.server.createMBean(className, name, params, signature));
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 253 */       ManagementException me = new ManagementException();
/* 254 */       me.setMessage(10417, new Object[] { name });
/* 255 */       me.setRootCause(re);
/* 256 */       throw me;
/*     */     }
/*     */     catch (InstanceAlreadyExistsException iaee)
/*     */     {
/* 260 */       ManagementException me = new ManagementException();
/* 261 */       me.setMessage(10418, new Object[] { name });
/* 262 */       me.setRootCause(iaee);
/* 263 */       throw me;
/*     */     }
/*     */     catch (MBeanException mbe)
/*     */     {
/* 267 */       ManagementException me = new ManagementException();
/* 268 */       me.setMessage(10417, new Object[] { name });
/* 269 */       me.setRootCause(mbe);
/* 270 */       throw me;
/*     */     }
/*     */     catch (NotCompliantMBeanException ncmbe)
/*     */     {
/* 274 */       me = new ManagementException();
/* 275 */       me.setMessage(10419, new Object[] { className });
/* 276 */       me.setRootCause(ncmbe);
/* 277 */     }throw me;
/*     */   }
/*     */ 
/*     */   public ObjectInstance createMBean(String className, String objectName, String loaderName, Object[] params, String[] signature)
/*     */   {
/* 294 */     javax.management.ObjectName name = null;
/* 295 */     javax.management.ObjectName loader = null;
/* 296 */     if (objectName != null)
/* 297 */       name = validateObjectName(objectName);
/* 298 */     if (loaderName != null)
/* 299 */       loader = validateObjectName(loaderName); ManagementException me;
/*     */     try {
/* 302 */       return new ObjectInstance(this.server.createMBean(className, name, loader, params, signature));
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 306 */       ManagementException me = new ManagementException();
/* 307 */       me.setMessage(10417, new Object[] { name });
/* 308 */       me.setRootCause(re);
/* 309 */       throw me;
/*     */     }
/*     */     catch (InstanceAlreadyExistsException iaee)
/*     */     {
/* 313 */       ManagementException me = new ManagementException();
/* 314 */       me.setMessage(10418, new Object[] { name });
/* 315 */       me.setRootCause(iaee);
/* 316 */       throw me;
/*     */     }
/*     */     catch (MBeanException mbe)
/*     */     {
/* 320 */       ManagementException me = new ManagementException();
/* 321 */       me.setMessage(10417, new Object[] { name });
/* 322 */       me.setRootCause(mbe);
/* 323 */       throw me;
/*     */     }
/*     */     catch (NotCompliantMBeanException ncmbe)
/*     */     {
/* 327 */       ManagementException me = new ManagementException();
/* 328 */       me.setMessage(10419, new Object[] { className });
/* 329 */       me.setRootCause(ncmbe);
/* 330 */       throw me;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 334 */       me = new ManagementException();
/* 335 */       me.setMessage(10407, new Object[] { name });
/* 336 */       me.setRootCause(infe);
/* 337 */     }throw me;
/*     */   }
/*     */ 
/*     */   public ObjectInstance registerMBean(Object object, String objectName)
/*     */   {
/* 350 */     javax.management.ObjectName name = null;
/* 351 */     if (objectName != null)
/* 352 */       name = validateObjectName(objectName); ManagementException me;
/*     */     try {
/* 355 */       return new ObjectInstance(this.server.registerMBean(object, name));
/*     */     }
/*     */     catch (InstanceAlreadyExistsException iaee)
/*     */     {
/* 359 */       ManagementException me = new ManagementException();
/* 360 */       me.setMessage(10418, new Object[] { name });
/* 361 */       me.setRootCause(iaee);
/* 362 */       throw me;
/*     */     }
/*     */     catch (NotCompliantMBeanException ncmbe)
/*     */     {
/* 366 */       ManagementException me = new ManagementException();
/* 367 */       me.setMessage(10419, new Object[] { object.getClass().getName() });
/* 368 */       me.setRootCause(ncmbe);
/* 369 */       throw me;
/*     */     }
/*     */     catch (MBeanRegistrationException mbre)
/*     */     {
/* 373 */       me = new ManagementException();
/* 374 */       me.setMessage(10420, new Object[] { name });
/* 375 */       me.setRootCause(mbre);
/* 376 */     }throw me;
/*     */   }
/*     */ 
/*     */   public void unregisterMBean(String objectName)
/*     */   {
/* 387 */     javax.management.ObjectName name = validateObjectName(objectName);
/*     */     try
/*     */     {
/* 390 */       this.server.unregisterMBean(name);
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 394 */       ManagementException me = new ManagementException();
/* 395 */       me.setMessage(10407, new Object[] { name });
/* 396 */       me.setRootCause(infe);
/* 397 */       throw me;
/*     */     }
/*     */     catch (MBeanRegistrationException mbre)
/*     */     {
/* 401 */       ManagementException me = new ManagementException();
/* 402 */       me.setMessage(10421, new Object[] { name });
/* 403 */       me.setRootCause(mbre);
/* 404 */       throw me;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ObjectInstance getObjectInstance(String objectName) {
/* 417 */     javax.management.ObjectName name = validateObjectName(objectName);
/*     */     ManagementException me;
/*     */     try {
/* 420 */       return new ObjectInstance(this.server.getObjectInstance(name));
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 424 */       me = new ManagementException();
/* 425 */       me.setMessage(10407, new Object[] { name });
/* 426 */       me.setRootCause(infe);
/* 427 */     }throw me;
/*     */   }
/*     */ 
/*     */   public ObjectInstance[] queryMBeans(String objectName)
/*     */   {
/* 444 */     javax.management.ObjectName name = validateObjectName(objectName);
/* 445 */     Set result = this.server.queryMBeans(name, null);
/* 446 */     return (ObjectInstance[])(ObjectInstance[])result.toArray(new ObjectInstance[result.size()]);
/*     */   }
/*     */ 
/*     */   public ObjectName[] queryNames(String objectName)
/*     */   {
/* 462 */     javax.management.ObjectName name = validateObjectName(objectName);
/* 463 */     Set result = this.server.queryNames(name, null);
/* 464 */     return (ObjectName[])(ObjectName[])result.toArray(new ObjectName[result.size()]);
/*     */   }
/*     */ 
/*     */   public boolean isRegistered(String objectName)
/*     */   {
/* 475 */     javax.management.ObjectName name = validateObjectName(objectName);
/* 476 */     return this.server.isRegistered(name);
/*     */   }
/*     */ 
/*     */   public Integer getMBeanCount()
/*     */   {
/* 486 */     return this.server.getMBeanCount();
/*     */   }
/*     */   public Object getAttribute(String objectName, String attribute) {
/* 498 */     javax.management.ObjectName name = validateObjectName(objectName);
/*     */     ManagementException me;
/*     */     try {
/* 501 */       return this.server.getAttribute(name, attribute);
/*     */     }
/*     */     catch (AttributeNotFoundException anfe)
/*     */     {
/* 505 */       ManagementException me = new ManagementException();
/* 506 */       me.setMessage(10409, new Object[] { attribute, name });
/* 507 */       me.setRootCause(anfe);
/* 508 */       throw me;
/*     */     }
/*     */     catch (MBeanException mbe)
/*     */     {
/* 512 */       ManagementException me = new ManagementException();
/* 513 */       me.setMessage(10410, new Object[] { attribute, name });
/* 514 */       me.setRootCause(mbe);
/* 515 */       throw me;
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 519 */       ManagementException me = new ManagementException();
/* 520 */       me.setMessage(10411, new Object[] { attribute, name });
/* 521 */       me.setRootCause(re);
/* 522 */       throw me;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 526 */       ManagementException me = new ManagementException();
/* 527 */       me.setMessage(10407, new Object[] { name });
/* 528 */       me.setRootCause(infe);
/* 529 */       throw me;
/*     */     }
/*     */     catch (RuntimeOperationsException roe)
/*     */     {
/* 533 */       me = new ManagementException();
/* 534 */       me.setMessage(10412);
/* 535 */       me.setRootCause(roe);
/* 536 */     }throw me;
/*     */   }
/*     */   public Attribute[] getAttributes(String objectName, String[] attributes) {
/* 549 */     javax.management.ObjectName name = validateObjectName(objectName);
/*     */     ManagementException me;
/*     */     try {
/* 552 */       AttributeList result = this.server.getAttributes(name, attributes);
/* 553 */       Attribute[] values = new Attribute[result.size()];
/* 554 */       for (int i = 0; i < result.size(); i++)
/*     */       {
/* 556 */         values[i] = new Attribute((javax.management.Attribute)result.get(i));
/*     */       }
/* 558 */       return values;
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 562 */       ManagementException me = new ManagementException();
/* 563 */       me.setMessage(10413, new Object[] { name });
/* 564 */       me.setRootCause(re);
/* 565 */       throw me;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 569 */       ManagementException me = new ManagementException();
/* 570 */       me.setMessage(10407, new Object[] { name });
/* 571 */       me.setRootCause(infe);
/* 572 */       throw me;
/*     */     }
/*     */     catch (RuntimeOperationsException roe)
/*     */     {
/* 576 */       me = new ManagementException();
/* 577 */       me.setMessage(10414);
/* 578 */       me.setRootCause(roe);
/* 579 */     }throw me;
/*     */   }
/*     */ 
/*     */   public void setAttribute(String objectName, Attribute attribute)
/*     */   {
/* 591 */     javax.management.ObjectName name = validateObjectName(objectName);
/* 592 */     javax.management.Attribute attrib = validateAttribute(attribute);
/*     */     try
/*     */     {
/* 595 */       this.server.setAttribute(name, attrib);
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 599 */       ManagementException me = new ManagementException();
/* 600 */       me.setMessage(10422, new Object[] { attrib.getName(), name });
/* 601 */       me.setRootCause(re);
/* 602 */       throw me;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 606 */       ManagementException me = new ManagementException();
/* 607 */       me.setMessage(10407, new Object[] { name });
/* 608 */       me.setRootCause(infe);
/* 609 */       throw me;
/*     */     }
/*     */     catch (AttributeNotFoundException anfe)
/*     */     {
/* 613 */       ManagementException me = new ManagementException();
/* 614 */       me.setMessage(10409, new Object[] { attrib.getName(), name });
/* 615 */       me.setRootCause(anfe);
/* 616 */       throw me;
/*     */     }
/*     */     catch (MBeanException mbe)
/*     */     {
/* 620 */       ManagementException me = new ManagementException();
/* 621 */       me.setMessage(10423, new Object[] { attrib.getName(), name });
/* 622 */       me.setRootCause(mbe);
/* 623 */       throw me;
/*     */     }
/*     */     catch (InvalidAttributeValueException iave)
/*     */     {
/* 627 */       ManagementException me = new ManagementException();
/* 628 */       me.setMessage(10424, new Object[] { attrib.getValue(), attrib.getName(), name });
/* 629 */       me.setRootCause(iave);
/* 630 */       throw me;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Attribute[] setAttributes(String objectName, Attribute[] attributes)
/*     */   {
/* 643 */     javax.management.ObjectName name = validateObjectName(objectName);
/* 644 */     AttributeList attribList = new AttributeList();
/* 645 */     for (int i = 0; i < attributes.length; i++)
/*     */     {
/* 647 */       attribList.add(attributes[i].toAttribute());
/*     */     }ManagementException me;
/*     */     try {
/* 651 */       AttributeList result = this.server.setAttributes(name, attribList);
/* 652 */       Attribute[] values = new Attribute[result.size()];
/* 653 */       for (int i = 0; i < result.size(); i++)
/*     */       {
/* 655 */         values[i] = new Attribute((javax.management.Attribute)result.get(i));
/*     */       }
/* 657 */       return values;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 661 */       ManagementException me = new ManagementException();
/* 662 */       me.setMessage(10407, new Object[] { name });
/* 663 */       me.setRootCause(infe);
/* 664 */       throw me;
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 668 */       me = new ManagementException();
/* 669 */       me.setMessage(10425, new Object[] { name });
/* 670 */       me.setRootCause(re);
/* 671 */     }throw me;
/*     */   }
/*     */   public Object invoke(String objectName, String operationName, Object[] params, String[] signature) {
/* 686 */     javax.management.ObjectName name = validateObjectName(objectName);
/*     */     ManagementException me;
/*     */     try {
/* 689 */       return this.server.invoke(name, operationName, params, signature);
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 693 */       ManagementException me = new ManagementException();
/* 694 */       me.setMessage(10415, new Object[] { operationName, objectName });
/* 695 */       me.setRootCause(re);
/* 696 */       throw me;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 700 */       ManagementException me = new ManagementException();
/* 701 */       me.setMessage(10407, new Object[] { objectName });
/* 702 */       me.setRootCause(infe);
/* 703 */       throw me;
/*     */     }
/*     */     catch (MBeanException mbe)
/*     */     {
/* 707 */       me = new ManagementException();
/* 708 */       me.setMessage(10416, new Object[] { operationName, objectName });
/* 709 */       me.setRootCause(mbe);
/* 710 */     }throw me;
/*     */   }
/*     */ 
/*     */   public String getDefaultDomain()
/*     */   {
/* 721 */     return this.server.getDefaultDomain();
/*     */   }
/*     */   public MBeanInfo getMBeanInfo(String objectName) {
/* 733 */     javax.management.ObjectName name = validateObjectName(objectName);
/*     */     ManagementException me;
/*     */     try {
/* 736 */       return new MBeanInfo(this.server.getMBeanInfo(name));
/*     */     }
/*     */     catch (IntrospectionException ie)
/*     */     {
/* 740 */       ManagementException me = new ManagementException();
/* 741 */       me.setMessage(10406, new Object[] { objectName });
/* 742 */       me.setRootCause(ie);
/* 743 */       throw me;
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 747 */       ManagementException me = new ManagementException();
/* 748 */       me.setMessage(10407, new Object[] { objectName });
/* 749 */       me.setRootCause(infe);
/* 750 */       throw me;
/*     */     }
/*     */     catch (ReflectionException re)
/*     */     {
/* 754 */       me = new ManagementException();
/* 755 */       me.setMessage(10408, new Object[] { objectName });
/* 756 */       me.setRootCause(re);
/* 757 */     }throw me;
/*     */   }
/*     */   public boolean isInstanceOf(String objectName, String className) {
/* 771 */     javax.management.ObjectName name = validateObjectName(objectName);
/*     */     ManagementException me;
/*     */     try {
/* 774 */       return this.server.isInstanceOf(name, className);
/*     */     }
/*     */     catch (InstanceNotFoundException infe)
/*     */     {
/* 778 */       me = new ManagementException();
/* 779 */       me.setMessage(10407, new Object[] { objectName });
/* 780 */       me.setRootCause(infe);
/* 781 */     }throw me;
/*     */   }
/*     */ 
/*     */   public ObjectName[] getFlexMBeanObjectNames()
/*     */   {
/* 798 */     javax.management.ObjectName pattern = validateObjectName("flex.runtime*:*");
/* 799 */     Set result = this.server.queryNames(pattern, null);
/* 800 */     ObjectName[] names = new ObjectName[result.size()];
/* 801 */     int i = 0;
/* 802 */     for (Iterator iter = result.iterator(); iter.hasNext(); )
/*     */     {
/* 804 */       names[(i++)] = new ObjectName((javax.management.ObjectName)iter.next());
/*     */     }
/* 806 */     return names;
/*     */   }
/*     */ 
/*     */   public Integer getFlexMBeanCount()
/*     */   {
/* 816 */     return new Integer(getFlexMBeanObjectNames().length);
/*     */   }
/*     */ 
/*     */   public String[] getFlexDomains()
/*     */   {
/* 827 */     ObjectName[] names = getFlexMBeanObjectNames();
/* 828 */     Set domains = new TreeSet();
/*     */ 
/* 831 */     if (names.length > 0)
/*     */     {
/* 833 */       for (int i = 0; i < names.length; i++)
/*     */       {
/* 835 */         String name = names[i].toString();
/* 836 */         String domain = name.substring(0, name.indexOf(':'));
/* 837 */         if (domains.contains(domain))
/*     */           continue;
/* 839 */         domains.add(domain);
/*     */       }
/*     */     }
/*     */ 
/* 843 */     return (String[])(String[])domains.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   private javax.management.ObjectName validateObjectName(String objectName)
/*     */   {
/*     */     ManagementException me;
/*     */     try
/*     */     {
/* 862 */       return new javax.management.ObjectName(objectName);
/*     */     }
/*     */     catch (MalformedObjectNameException mone)
/*     */     {
/* 866 */       me = new ManagementException();
/* 867 */       me.setMessage(10400, new Object[] { objectName });
/* 868 */     }throw me;
/*     */   }
/*     */ 
/*     */   private javax.management.Attribute validateAttribute(Attribute attribute)
/*     */   {
/* 880 */     return attribute.toAttribute();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.MBeanServerGateway
 * JD-Core Version:    0.6.0
 */