/*      */ package flex.messaging.io.amf;
/*      */ 
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.io.ArrayCollection;
/*      */ import flex.messaging.io.PagedRowSet;
/*      */ import flex.messaging.io.PropertyProxy;
/*      */ import flex.messaging.io.PropertyProxyRegistry;
/*      */ import flex.messaging.io.SerializationContext;
/*      */ import flex.messaging.io.SerializationDescriptor;
/*      */ import flex.messaging.io.StatusInfoProxy;
/*      */ import flex.messaging.util.Trace;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.Externalizable;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.sql.RowSet;
/*      */ import org.w3c.dom.Document;
/*      */ 
/*      */ public class Amf3Output extends AbstractAmfOutput
/*      */   implements Amf3Types
/*      */ {
/*      */   protected IdentityHashMap objectTable;
/*      */   protected HashMap traitsTable;
/*      */   protected HashMap stringTable;
/*      */ 
/*      */   public Amf3Output(SerializationContext context)
/*      */   {
/*   77 */     super(context);
/*      */ 
/*   79 */     this.objectTable = new IdentityHashMap(64);
/*   80 */     this.traitsTable = new HashMap(10);
/*   81 */     this.stringTable = new HashMap(64);
/*      */ 
/*   83 */     context.supportDatesByReference = true;
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*   88 */     super.reset();
/*   89 */     this.objectTable.clear();
/*   90 */     this.traitsTable.clear();
/*   91 */     this.stringTable.clear();
/*      */   }
/*      */ 
/*      */   public void writeObject(Object o)
/*      */     throws IOException
/*      */   {
/*  103 */     if (o == null)
/*      */     {
/*  105 */       writeAMFNull();
/*  106 */       return;
/*      */     }
/*      */ 
/*  109 */     if (((o instanceof String)) || ((o instanceof Character)))
/*      */     {
/*  111 */       String s = o.toString();
/*  112 */       writeAMFString(s);
/*      */     }
/*  114 */     else if ((o instanceof Number))
/*      */     {
/*  116 */       if (((o instanceof Integer)) || ((o instanceof Short)) || ((o instanceof Byte)))
/*      */       {
/*  118 */         int i = ((Number)o).intValue();
/*  119 */         writeAMFInt(i);
/*      */       }
/*  121 */       else if ((!this.context.legacyBigNumbers) && (((o instanceof BigInteger)) || ((o instanceof BigDecimal))))
/*      */       {
/*  127 */         writeAMFString(((Number)o).toString());
/*      */       }
/*      */       else
/*      */       {
/*  131 */         double d = ((Number)o).doubleValue();
/*  132 */         writeAMFDouble(d);
/*      */       }
/*      */     }
/*  135 */     else if ((o instanceof Boolean))
/*      */     {
/*  137 */       writeAMFBoolean(((Boolean)o).booleanValue());
/*      */     }
/*  140 */     else if ((o instanceof Date))
/*      */     {
/*  142 */       writeAMFDate((Date)o);
/*      */     }
/*  144 */     else if ((o instanceof Calendar))
/*      */     {
/*  146 */       writeAMFDate(((Calendar)o).getTime());
/*      */     }
/*  148 */     else if ((o instanceof Document))
/*      */     {
/*  150 */       if (this.context.legacyXMLDocument)
/*  151 */         this.out.write(7);
/*      */       else {
/*  153 */         this.out.write(11);
/*      */       }
/*  155 */       String xml = documentToString(o);
/*  156 */       if (this.isDebug) {
/*  157 */         this.trace.write(xml);
/*      */       }
/*  159 */       writeAMFUTF(xml);
/*      */     }
/*      */     else
/*      */     {
/*  164 */       Class cls = o.getClass();
/*      */ 
/*  166 */       if ((this.context.legacyMap) && ((o instanceof Map)) && (!(o instanceof ASObject)))
/*      */       {
/*  168 */         writeMapAsECMAArray((Map)o);
/*      */       }
/*  170 */       else if ((o instanceof Collection))
/*      */       {
/*  172 */         if (this.context.legacyCollection)
/*  173 */           writeCollection((Collection)o, null);
/*      */         else
/*  175 */           writeArrayCollection((Collection)o, null);
/*      */       }
/*  177 */       else if (cls.isArray())
/*      */       {
/*  179 */         writeAMFArray(o, cls.getComponentType());
/*      */       }
/*      */       else
/*      */       {
/*  184 */         if ((o instanceof RowSet))
/*      */         {
/*  186 */           o = new PagedRowSet((RowSet)o, 2147483647, false);
/*      */         }
/*  188 */         else if ((this.context.legacyThrowable) && ((o instanceof Throwable)))
/*      */         {
/*  190 */           o = new StatusInfoProxy((Throwable)o);
/*      */         }
/*      */ 
/*  193 */         writeCustomObject(o);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeObjectTraits(TraitsInfo ti) throws IOException
/*      */   {
/*  200 */     String className = ti.getClassName();
/*      */ 
/*  202 */     if (this.isDebug)
/*      */     {
/*  204 */       if (ti.isExternalizable())
/*  205 */         this.trace.startExternalizableObject(className, this.objectTable.size() - 1);
/*      */       else {
/*  207 */         this.trace.startAMFObject(className, this.objectTable.size() - 1);
/*      */       }
/*      */     }
/*  210 */     if (!byReference(ti))
/*      */     {
/*  212 */       int count = 0;
/*  213 */       List propertyNames = null;
/*  214 */       boolean externalizable = ti.isExternalizable();
/*      */ 
/*  216 */       if (!externalizable)
/*      */       {
/*  218 */         propertyNames = ti.getProperties();
/*  219 */         if (propertyNames != null) {
/*  220 */           count = propertyNames.size();
/*      */         }
/*      */       }
/*  223 */       boolean dynamic = ti.isDynamic();
/*      */ 
/*  225 */       writeUInt29(0x3 | (externalizable ? 4 : 0) | (dynamic ? 8 : 0) | count << 4);
/*  226 */       writeStringWithoutType(className);
/*      */ 
/*  228 */       if ((!externalizable) && (propertyNames != null))
/*      */       {
/*  230 */         for (int i = 0; i < count; i++)
/*      */         {
/*  232 */           String propName = ti.getProperty(i);
/*  233 */           writeStringWithoutType(propName);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeObjectProperty(String name, Object value) throws IOException
/*      */   {
/*  241 */     if (this.isDebug)
/*  242 */       this.trace.namedElement(name);
/*  243 */     writeObject(value);
/*      */   }
/*      */ 
/*      */   public void writeObjectEnd()
/*      */     throws IOException
/*      */   {
/*  250 */     if (this.isDebug)
/*  251 */       this.trace.endAMFObject();
/*      */   }
/*      */ 
/*      */   protected void writeAMFBoolean(boolean b)
/*      */     throws IOException
/*      */   {
/*  263 */     if (this.isDebug) {
/*  264 */       this.trace.write(b);
/*      */     }
/*  266 */     if (b)
/*  267 */       this.out.write(3);
/*      */     else
/*  269 */       this.out.write(2);
/*      */   }
/*      */ 
/*      */   protected void writeAMFDate(Date d)
/*      */     throws IOException
/*      */   {
/*  277 */     this.out.write(8);
/*      */ 
/*  279 */     if (!byReference(d))
/*      */     {
/*  281 */       if (this.isDebug) {
/*  282 */         this.trace.write(d);
/*      */       }
/*      */ 
/*  285 */       writeUInt29(1);
/*      */ 
/*  288 */       this.out.writeDouble(d.getTime());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeAMFDouble(double d)
/*      */     throws IOException
/*      */   {
/*  297 */     if (this.isDebug) {
/*  298 */       this.trace.write(d);
/*      */     }
/*  300 */     this.out.write(5);
/*  301 */     this.out.writeDouble(d);
/*      */   }
/*      */ 
/*      */   protected void writeAMFInt(int i)
/*      */     throws IOException
/*      */   {
/*  309 */     if ((i >= -268435456) && (i <= 268435455))
/*      */     {
/*  311 */       if (this.isDebug) {
/*  312 */         this.trace.write(i);
/*      */       }
/*      */ 
/*  318 */       i &= 536870911;
/*  319 */       this.out.write(4);
/*  320 */       writeUInt29(i);
/*      */     }
/*      */     else
/*      */     {
/*  325 */       writeAMFDouble(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeMapAsECMAArray(Map map)
/*      */     throws IOException
/*      */   {
/*  334 */     this.out.write(9);
/*      */ 
/*  336 */     if (!byReference(map))
/*      */     {
/*  338 */       if (this.isDebug) {
/*  339 */         this.trace.startECMAArray(this.objectTable.size() - 1);
/*      */       }
/*  341 */       writeUInt29(1);
/*      */ 
/*  343 */       Iterator it = map.keySet().iterator();
/*  344 */       while (it.hasNext())
/*      */       {
/*  346 */         Object key = it.next();
/*  347 */         if (key != null)
/*      */         {
/*  349 */           String propName = key.toString();
/*  350 */           writeStringWithoutType(propName);
/*      */ 
/*  352 */           if (this.isDebug) {
/*  353 */             this.trace.namedElement(propName);
/*      */           }
/*  355 */           writeObject(map.get(key));
/*      */         }
/*      */       }
/*      */ 
/*  359 */       writeStringWithoutType("");
/*      */ 
/*  361 */       if (this.isDebug)
/*  362 */         this.trace.endAMFArray();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeAMFNull()
/*      */     throws IOException
/*      */   {
/*  371 */     if (this.isDebug) {
/*  372 */       this.trace.writeNull();
/*      */     }
/*  374 */     this.out.write(1);
/*      */   }
/*      */ 
/*      */   protected void writeAMFString(String s)
/*      */     throws IOException
/*      */   {
/*  382 */     this.out.write(6);
/*  383 */     writeStringWithoutType(s);
/*      */ 
/*  385 */     if (this.isDebug)
/*      */     {
/*  387 */       this.trace.writeString(s);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeStringWithoutType(String s)
/*      */     throws IOException
/*      */   {
/*  396 */     if (s.length() == 0)
/*      */     {
/*  401 */       writeUInt29(1);
/*  402 */       return;
/*      */     }
/*      */ 
/*  405 */     if (!byReference(s))
/*      */     {
/*  407 */       writeAMFUTF(s);
/*  408 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeAMFArray(Object o, Class componentType)
/*      */     throws IOException
/*      */   {
/*  417 */     if (componentType.isPrimitive())
/*      */     {
/*  419 */       writePrimitiveArray(o);
/*      */     }
/*  421 */     else if (componentType.equals(Byte.class))
/*      */     {
/*  423 */       writeAMFByteArray((Byte[])(Byte[])o);
/*      */     }
/*  425 */     else if (componentType.equals(Character.class))
/*      */     {
/*  427 */       writeCharArrayAsString((Character[])(Character[])o);
/*      */     }
/*      */     else
/*      */     {
/*  431 */       writeObjectArray((Object[])(Object[])o, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeArrayCollection(Collection col, SerializationDescriptor desc)
/*      */     throws IOException
/*      */   {
/*  440 */     this.out.write(10);
/*      */ 
/*  442 */     if (!byReference(col))
/*      */     {
/*      */       ArrayCollection ac;
/*      */       ArrayCollection ac;
/*  446 */       if ((col instanceof ArrayCollection))
/*      */       {
/*  448 */         ac = (ArrayCollection)col;
/*      */       }
/*      */       else
/*      */       {
/*  455 */         ac = new ArrayCollection(col);
/*  456 */         if (desc != null) {
/*  457 */           ac.setDescriptor(desc);
/*      */         }
/*      */       }
/*      */ 
/*  461 */       PropertyProxy proxy = PropertyProxyRegistry.getProxy(ac);
/*  462 */       writePropertyProxy(proxy, ac);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeCustomObject(Object o)
/*      */     throws IOException
/*      */   {
/*  471 */     PropertyProxy proxy = null;
/*      */ 
/*  473 */     if ((o instanceof PropertyProxy))
/*      */     {
/*  475 */       proxy = (PropertyProxy)o;
/*  476 */       o = proxy.getDefaultInstance();
/*      */ 
/*  479 */       if (o == null)
/*      */       {
/*  481 */         writeAMFNull();
/*  482 */         return;
/*      */       }
/*      */ 
/*  489 */       if ((o instanceof Collection))
/*      */       {
/*  491 */         if (this.context.legacyCollection)
/*  492 */           writeCollection((Collection)o, proxy.getDescriptor());
/*      */         else
/*  494 */           writeArrayCollection((Collection)o, proxy.getDescriptor());
/*  495 */         return;
/*      */       }
/*  497 */       if (o.getClass().isArray())
/*      */       {
/*  499 */         writeObjectArray((Object[])(Object[])o, proxy.getDescriptor());
/*  500 */         return;
/*      */       }
/*  502 */       if ((this.context.legacyMap) && ((o instanceof Map)) && (!(o instanceof ASObject)))
/*      */       {
/*  504 */         writeMapAsECMAArray((Map)o);
/*  505 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  509 */     this.out.write(10);
/*      */ 
/*  511 */     if (!byReference(o))
/*      */     {
/*  513 */       if (proxy == null)
/*      */       {
/*  515 */         proxy = PropertyProxyRegistry.getProxyAndRegister(o);
/*      */       }
/*      */ 
/*  518 */       writePropertyProxy(proxy, o);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writePropertyProxy(PropertyProxy proxy, Object instance)
/*      */     throws IOException
/*      */   {
/*  530 */     Object newInst = proxy.getInstanceToSerialize(instance);
/*  531 */     if (newInst != instance)
/*      */     {
/*  536 */       if (newInst == null) {
/*  537 */         throw new MessageException("PropertyProxy.getInstanceToSerialize class: " + proxy.getClass() + " returned null for instance class: " + instance.getClass().getName());
/*      */       }
/*      */ 
/*  540 */       proxy = PropertyProxyRegistry.getProxyAndRegister(newInst);
/*  541 */       instance = newInst;
/*      */     }
/*      */ 
/*  544 */     List propertyNames = null;
/*  545 */     boolean externalizable = proxy.isExternalizable(instance);
/*      */ 
/*  547 */     if (!externalizable) {
/*  548 */       propertyNames = proxy.getPropertyNames(instance);
/*      */     }
/*  550 */     TraitsInfo ti = new TraitsInfo(proxy.getAlias(instance), proxy.isDynamic(), externalizable, propertyNames);
/*  551 */     writeObjectTraits(ti);
/*      */ 
/*  553 */     if (externalizable)
/*      */     {
/*  556 */       ((Externalizable)instance).writeExternal(this);
/*      */     }
/*  558 */     else if (propertyNames != null)
/*      */     {
/*  560 */       Iterator it = propertyNames.iterator();
/*  561 */       while (it.hasNext())
/*      */       {
/*  563 */         String propName = (String)it.next();
/*  564 */         Object value = null;
/*  565 */         value = proxy.getValue(instance, propName);
/*  566 */         writeObjectProperty(propName, value);
/*      */       }
/*      */     }
/*      */ 
/*  570 */     writeObjectEnd();
/*      */   }
/*      */ 
/*      */   protected void writePrimitiveArray(Object obj)
/*      */     throws IOException
/*      */   {
/*  585 */     Class aType = obj.getClass().getComponentType();
/*      */ 
/*  587 */     if (aType.equals(Character.TYPE))
/*      */     {
/*  590 */       char[] c = (char[])(char[])obj;
/*  591 */       writeCharArrayAsString(c);
/*      */     }
/*  593 */     else if (aType.equals(Byte.TYPE))
/*      */     {
/*  595 */       writeAMFByteArray((byte[])(byte[])obj);
/*      */     }
/*      */     else
/*      */     {
/*  599 */       this.out.write(9);
/*      */ 
/*  601 */       if (!byReference(obj))
/*      */       {
/*  603 */         if (aType.equals(Boolean.TYPE))
/*      */         {
/*  605 */           boolean[] b = (boolean[])(boolean[])obj;
/*      */ 
/*  608 */           writeUInt29(b.length << 1 | 0x1);
/*      */ 
/*  611 */           writeStringWithoutType("");
/*      */ 
/*  613 */           if (this.isDebug)
/*      */           {
/*  615 */             this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */ 
/*  617 */             for (int i = 0; i < b.length; i++)
/*      */             {
/*  619 */               this.trace.arrayElement(i);
/*  620 */               writeAMFBoolean(b[i]);
/*      */             }
/*      */ 
/*  623 */             this.trace.endAMFArray();
/*      */           }
/*      */           else
/*      */           {
/*  627 */             for (int i = 0; i < b.length; i++)
/*      */             {
/*  629 */               writeAMFBoolean(b[i]);
/*      */             }
/*      */           }
/*      */         }
/*  633 */         else if ((aType.equals(Integer.TYPE)) || (aType.equals(Short.TYPE)))
/*      */         {
/*  637 */           int length = Array.getLength(obj);
/*      */ 
/*  640 */           writeUInt29(length << 1 | 0x1);
/*      */ 
/*  642 */           writeStringWithoutType("");
/*      */ 
/*  644 */           if (this.isDebug)
/*      */           {
/*  646 */             this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */ 
/*  648 */             for (int i = 0; i < length; i++)
/*      */             {
/*  650 */               this.trace.arrayElement(i);
/*  651 */               int v = Array.getInt(obj, i);
/*  652 */               writeAMFInt(v);
/*      */             }
/*      */ 
/*  655 */             this.trace.endAMFArray();
/*      */           }
/*      */           else
/*      */           {
/*  659 */             for (int i = 0; i < length; i++)
/*      */             {
/*  661 */               int v = Array.getInt(obj, i);
/*  662 */               writeAMFInt(v);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  670 */           int length = Array.getLength(obj);
/*      */ 
/*  673 */           writeUInt29(length << 1 | 0x1);
/*      */ 
/*  675 */           writeStringWithoutType("");
/*      */ 
/*  677 */           if (this.isDebug)
/*      */           {
/*  679 */             this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */ 
/*  681 */             for (int i = 0; i < length; i++)
/*      */             {
/*  683 */               this.trace.arrayElement(i);
/*  684 */               double v = Array.getDouble(obj, i);
/*  685 */               writeAMFDouble(v);
/*      */             }
/*      */ 
/*  688 */             this.trace.endAMFArray();
/*      */           }
/*      */           else
/*      */           {
/*  692 */             for (int i = 0; i < length; i++)
/*      */             {
/*  694 */               double v = Array.getDouble(obj, i);
/*  695 */               writeAMFDouble(v);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeAMFByteArray(byte[] ba)
/*      */     throws IOException
/*      */   {
/*  708 */     this.out.write(12);
/*      */ 
/*  710 */     if (!byReference(ba))
/*      */     {
/*  712 */       int length = ba.length;
/*      */ 
/*  715 */       writeUInt29(length << 1 | 0x1);
/*      */ 
/*  717 */       if (this.isDebug)
/*      */       {
/*  719 */         this.trace.startByteArray(this.objectTable.size() - 1, length);
/*      */       }
/*      */ 
/*  722 */       this.out.write(ba, 0, length);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeAMFByteArray(Byte[] ba)
/*      */     throws IOException
/*      */   {
/*  731 */     this.out.write(12);
/*      */ 
/*  733 */     if (!byReference(ba))
/*      */     {
/*  735 */       int length = ba.length;
/*      */ 
/*  738 */       writeUInt29(length << 1 | 0x1);
/*      */ 
/*  740 */       if (this.isDebug)
/*      */       {
/*  742 */         this.trace.startByteArray(this.objectTable.size() - 1, length);
/*      */       }
/*      */ 
/*  745 */       for (int i = 0; i < ba.length; i++)
/*      */       {
/*  747 */         Byte b = ba[i];
/*  748 */         if (b == null)
/*  749 */           this.out.write(0);
/*      */         else
/*  751 */           this.out.write(b.byteValue());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeCharArrayAsString(Character[] ca)
/*      */     throws IOException
/*      */   {
/*  761 */     int length = ca.length;
/*  762 */     char[] chars = new char[length];
/*      */ 
/*  764 */     for (int i = 0; i < length; i++)
/*      */     {
/*  766 */       Character c = ca[i];
/*  767 */       if (c == null)
/*  768 */         chars[i] = '\000';
/*      */       else
/*  770 */         chars[i] = ca[i].charValue();
/*      */     }
/*  772 */     writeCharArrayAsString(chars);
/*      */   }
/*      */ 
/*      */   protected void writeCharArrayAsString(char[] ca)
/*      */     throws IOException
/*      */   {
/*  780 */     String str = new String(ca);
/*  781 */     writeAMFString(str);
/*      */   }
/*      */ 
/*      */   protected void writeObjectArray(Object[] values, SerializationDescriptor descriptor)
/*      */     throws IOException
/*      */   {
/*  789 */     this.out.write(9);
/*      */ 
/*  791 */     if (!byReference(values))
/*      */     {
/*  793 */       if (this.isDebug) {
/*  794 */         this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */       }
/*  796 */       writeUInt29(values.length << 1 | 0x1);
/*      */ 
/*  799 */       writeStringWithoutType("");
/*      */ 
/*  801 */       for (int i = 0; i < values.length; i++)
/*      */       {
/*  803 */         if (this.isDebug) {
/*  804 */           this.trace.arrayElement(i);
/*      */         }
/*  806 */         Object item = values[i];
/*  807 */         if ((item != null) && (descriptor != null) && (!(item instanceof String)) && (!(item instanceof Number)) && (!(item instanceof Boolean)) && (!(item instanceof Character)))
/*      */         {
/*  811 */           PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
/*  812 */           proxy = (PropertyProxy)proxy.clone();
/*  813 */           proxy.setDescriptor(descriptor);
/*  814 */           proxy.setDefaultInstance(item);
/*  815 */           item = proxy;
/*      */         }
/*  817 */         writeObject(item);
/*      */       }
/*      */ 
/*  820 */       if (this.isDebug)
/*  821 */         this.trace.endAMFArray();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeCollection(Collection c, SerializationDescriptor descriptor)
/*      */     throws IOException
/*      */   {
/*  830 */     this.out.write(9);
/*      */ 
/*  835 */     if (!byReference(c))
/*      */     {
/*  837 */       if (this.isDebug) {
/*  838 */         this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */       }
/*  840 */       writeUInt29(c.size() << 1 | 0x1);
/*      */ 
/*  843 */       writeStringWithoutType("");
/*      */ 
/*  845 */       Iterator it = c.iterator();
/*  846 */       int i = 0;
/*  847 */       while (it.hasNext())
/*      */       {
/*  849 */         if (this.isDebug) {
/*  850 */           this.trace.arrayElement(i);
/*      */         }
/*  852 */         Object item = it.next();
/*      */ 
/*  854 */         if ((item != null) && (descriptor != null) && (!(item instanceof String)) && (!(item instanceof Number)) && (!(item instanceof Boolean)) && (!(item instanceof Character)))
/*      */         {
/*  858 */           PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
/*  859 */           proxy = (PropertyProxy)proxy.clone();
/*  860 */           proxy.setDescriptor(descriptor);
/*  861 */           proxy.setDefaultInstance(item);
/*  862 */           item = proxy;
/*      */         }
/*  864 */         writeObject(item);
/*      */ 
/*  866 */         i++;
/*      */       }
/*      */ 
/*  869 */       if (this.isDebug)
/*  870 */         this.trace.endAMFArray();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeUInt29(int ref)
/*      */     throws IOException
/*      */   {
/*  889 */     if (ref < 128)
/*      */     {
/*  892 */       this.out.writeByte(ref);
/*      */     }
/*  894 */     else if (ref < 16384)
/*      */     {
/*  897 */       this.out.writeByte(ref >> 7 & 0x7F | 0x80);
/*  898 */       this.out.writeByte(ref & 0x7F);
/*      */     }
/*  901 */     else if (ref < 2097152)
/*      */     {
/*  904 */       this.out.writeByte(ref >> 14 & 0x7F | 0x80);
/*  905 */       this.out.writeByte(ref >> 7 & 0x7F | 0x80);
/*  906 */       this.out.writeByte(ref & 0x7F);
/*      */     }
/*  909 */     else if (ref < 1073741824)
/*      */     {
/*  912 */       this.out.writeByte(ref >> 22 & 0x7F | 0x80);
/*  913 */       this.out.writeByte(ref >> 15 & 0x7F | 0x80);
/*  914 */       this.out.writeByte(ref >> 8 & 0x7F | 0x80);
/*  915 */       this.out.writeByte(ref & 0xFF);
/*      */     }
/*      */     else
/*      */     {
/*  921 */       throw new MessageException("Integer out of range: " + ref);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeAMFUTF(String s)
/*      */     throws IOException
/*      */   {
/*  930 */     int strlen = s.length();
/*  931 */     int utflen = 0;
/*  932 */     int count = 0;
/*      */ 
/*  934 */     char[] charr = getTempCharArray(strlen);
/*  935 */     s.getChars(0, strlen, charr, 0);
/*      */ 
/*  937 */     for (int i = 0; i < strlen; i++)
/*      */     {
/*  939 */       int c = charr[i];
/*  940 */       if (c <= 127)
/*      */       {
/*  942 */         utflen++;
/*      */       }
/*  944 */       else if (c > 2047)
/*      */       {
/*  946 */         utflen += 3;
/*      */       }
/*      */       else
/*      */       {
/*  950 */         utflen += 2;
/*      */       }
/*      */     }
/*      */ 
/*  954 */     writeUInt29(utflen << 1 | 0x1);
/*      */ 
/*  956 */     byte[] bytearr = getTempByteArray(utflen);
/*      */ 
/*  958 */     for (int i = 0; i < strlen; i++)
/*      */     {
/*  960 */       int c = charr[i];
/*  961 */       if (c <= 127)
/*      */       {
/*  963 */         bytearr[(count++)] = (byte)c;
/*      */       }
/*  965 */       else if (c > 2047)
/*      */       {
/*  967 */         bytearr[(count++)] = (byte)(0xE0 | c >> 12 & 0xF);
/*  968 */         bytearr[(count++)] = (byte)(0x80 | c >> 6 & 0x3F);
/*  969 */         bytearr[(count++)] = (byte)(0x80 | c >> 0 & 0x3F);
/*      */       }
/*      */       else
/*      */       {
/*  973 */         bytearr[(count++)] = (byte)(0xC0 | c >> 6 & 0x1F);
/*  974 */         bytearr[(count++)] = (byte)(0x80 | c >> 0 & 0x3F);
/*      */       }
/*      */     }
/*  977 */     this.out.write(bytearr, 0, utflen);
/*      */   }
/*      */ 
/*      */   protected boolean byReference(Object o)
/*      */     throws IOException
/*      */   {
/*  991 */     Object ref = this.objectTable.get(o);
/*      */ 
/*  993 */     if (ref != null)
/*      */     {
/*      */       try
/*      */       {
/*  997 */         int refNum = ((Integer)ref).intValue();
/*      */ 
/*  999 */         if (this.isDebug) {
/* 1000 */           this.trace.writeRef(refNum);
/*      */         }
/* 1002 */         writeUInt29(refNum << 1);
/*      */       }
/*      */       catch (ClassCastException e)
/*      */       {
/* 1006 */         throw new IOException("Object reference is not an Integer");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1011 */       this.objectTable.put(o, new Integer(this.objectTable.size()));
/*      */     }
/*      */ 
/* 1014 */     return ref != null;
/*      */   }
/*      */ 
/*      */   public void addObjectReference(Object o)
/*      */     throws IOException
/*      */   {
/* 1022 */     byReference(o);
/*      */   }
/*      */ 
/*      */   protected boolean byReference(String s)
/*      */     throws IOException
/*      */   {
/* 1030 */     Object ref = this.stringTable.get(s);
/*      */ 
/* 1032 */     if (ref != null)
/*      */     {
/*      */       try
/*      */       {
/* 1036 */         int refNum = ((Integer)ref).intValue();
/*      */ 
/* 1038 */         writeUInt29(refNum << 1);
/*      */ 
/* 1040 */         if ((Trace.amf) && (this.isDebug))
/*      */         {
/* 1042 */           this.trace.writeStringRef(refNum);
/*      */         }
/*      */       }
/*      */       catch (ClassCastException e)
/*      */       {
/* 1047 */         throw new IOException("String reference is not an Integer");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1052 */       this.stringTable.put(s, new Integer(this.stringTable.size()));
/*      */     }
/*      */ 
/* 1055 */     return ref != null;
/*      */   }
/*      */ 
/*      */   protected boolean byReference(TraitsInfo ti)
/*      */     throws IOException
/*      */   {
/* 1063 */     Object ref = this.traitsTable.get(ti);
/*      */ 
/* 1065 */     if (ref != null)
/*      */     {
/*      */       try
/*      */       {
/* 1069 */         int refNum = ((Integer)ref).intValue();
/*      */ 
/* 1071 */         writeUInt29(refNum << 2 | 0x1);
/*      */ 
/* 1073 */         if ((Trace.amf) && (this.isDebug))
/*      */         {
/* 1075 */           this.trace.writeTraitsInfoRef(refNum);
/*      */         }
/*      */       }
/*      */       catch (ClassCastException e)
/*      */       {
/* 1080 */         throw new IOException("TraitsInfo reference is not an Integer");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1085 */       this.traitsTable.put(ti, new Integer(this.traitsTable.size()));
/*      */     }
/*      */ 
/* 1088 */     return ref != null;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.Amf3Output
 * JD-Core Version:    0.6.0
 */