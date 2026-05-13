/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.io.ArrayCollection;
/*     */ import flex.messaging.io.PagedRowSet;
/*     */ import flex.messaging.io.PropertyProxy;
/*     */ import flex.messaging.io.PropertyProxyRegistry;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.SerializationDescriptor;
/*     */ import flex.messaging.io.StatusInfoProxy;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import javax.sql.RowSet;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public class Amf0Output extends AbstractAmfOutput
/*     */   implements AmfTypes
/*     */ {
/*  53 */   public static final byte[] OBJECT_END_MARKER = { 0, 0, 9 };
/*     */   protected IdentityHashMap serializedObjects;
/*  66 */   protected int serializedObjectCount = 0;
/*     */   protected boolean avmPlus;
/*     */   protected Amf3Output avmPlusOutput;
/*     */ 
/*     */   public Amf0Output(SerializationContext context)
/*     */   {
/*  84 */     super(context);
/*  85 */     context.supportDatesByReference = false;
/*     */ 
/*  87 */     this.serializedObjects = new IdentityHashMap(64);
/*     */   }
/*     */ 
/*     */   public void setAvmPlus(boolean a)
/*     */   {
/*  98 */     this.avmPlus = a;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 107 */     super.reset();
/*     */ 
/* 109 */     this.serializedObjects.clear();
/* 110 */     this.serializedObjectCount = 0;
/*     */ 
/* 112 */     if (this.avmPlusOutput != null)
/* 113 */       this.avmPlusOutput.reset();
/*     */   }
/*     */ 
/*     */   protected void createAMF3Output()
/*     */   {
/* 123 */     this.avmPlusOutput = new Amf3Output(this.context);
/* 124 */     this.avmPlusOutput.setOutputStream(this.out);
/* 125 */     this.avmPlusOutput.setDebugTrace(this.trace);
/*     */   }
/*     */ 
/*     */   public void writeObject(Object o)
/*     */     throws IOException
/*     */   {
/* 137 */     if (o == null)
/*     */     {
/* 139 */       writeAMFNull();
/* 140 */       return;
/*     */     }
/*     */ 
/* 143 */     if ((o instanceof String))
/*     */     {
/* 145 */       writeAMFString((String)o);
/*     */     }
/* 147 */     else if ((o instanceof Number))
/*     */     {
/* 149 */       if ((!this.context.legacyBigNumbers) && (((o instanceof BigInteger)) || ((o instanceof BigDecimal))))
/*     */       {
/* 155 */         writeAMFString(((Number)o).toString());
/*     */       }
/*     */       else
/*     */       {
/* 159 */         writeAMFDouble(((Number)o).doubleValue());
/*     */       }
/*     */     }
/* 162 */     else if ((o instanceof Boolean))
/*     */     {
/* 164 */       writeAMFBoolean(((Boolean)o).booleanValue());
/*     */     }
/* 166 */     else if ((o instanceof Character))
/*     */     {
/* 168 */       String s = o.toString();
/* 169 */       writeAMFString(s);
/*     */     }
/* 171 */     else if ((o instanceof Date))
/*     */     {
/* 174 */       writeAMFDate((Date)o);
/*     */     }
/* 176 */     else if ((o instanceof Calendar))
/*     */     {
/* 178 */       writeAMFDate(((Calendar)o).getTime());
/*     */     }
/* 185 */     else if (this.avmPlus)
/*     */     {
/* 187 */       if (this.avmPlusOutput == null)
/*     */       {
/* 189 */         createAMF3Output();
/*     */       }
/*     */ 
/* 192 */       this.out.writeByte(17);
/* 193 */       this.avmPlusOutput.writeObject(o);
/*     */     }
/*     */     else
/*     */     {
/* 197 */       Class cls = o.getClass();
/*     */ 
/* 199 */       if (cls.isArray())
/*     */       {
/* 201 */         writeAMFArray(o, cls.getComponentType());
/*     */       }
/* 203 */       else if (((o instanceof Map)) && (this.context.legacyMap) && (!(o instanceof ASObject)))
/*     */       {
/* 205 */         writeMapAsECMAArray((Map)o);
/*     */       }
/* 207 */       else if ((o instanceof Collection))
/*     */       {
/* 209 */         if (this.context.legacyCollection)
/* 210 */           writeCollection((Collection)o, null);
/*     */         else
/* 212 */           writeArrayCollection((Collection)o, null);
/*     */       }
/* 214 */       else if ((o instanceof Document))
/*     */       {
/* 216 */         this.out.write(15);
/* 217 */         String xml = documentToString(o);
/* 218 */         if (this.isDebug) {
/* 219 */           this.trace.write(xml);
/*     */         }
/* 221 */         writeUTF(xml, true, false);
/*     */       }
/*     */       else
/*     */       {
/* 226 */         if ((o instanceof RowSet))
/*     */         {
/* 228 */           o = new PagedRowSet((RowSet)o, 2147483647, false);
/*     */         }
/* 230 */         else if (((o instanceof Throwable)) && (this.context.legacyThrowable))
/*     */         {
/* 232 */           o = new StatusInfoProxy((Throwable)o);
/*     */         }
/*     */ 
/* 235 */         writeCustomObject(o);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeObjectTraits(TraitsInfo traits)
/*     */     throws IOException
/*     */   {
/* 246 */     String className = null;
/* 247 */     if (traits != null) {
/* 248 */       className = traits.getClassName();
/*     */     }
/* 250 */     if (this.isDebug) {
/* 251 */       this.trace.startAMFObject(className, this.serializedObjectCount - 1);
/*     */     }
/* 253 */     if ((className == null) || (className.length() == 0))
/*     */     {
/* 255 */       this.out.write(3);
/*     */     }
/*     */     else
/*     */     {
/* 259 */       this.out.write(16);
/* 260 */       this.out.writeUTF(className);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeObjectProperty(String name, Object value)
/*     */     throws IOException
/*     */   {
/* 269 */     if (this.isDebug) {
/* 270 */       this.trace.namedElement(name);
/*     */     }
/* 272 */     this.out.writeUTF(name);
/* 273 */     writeObject(value);
/*     */   }
/*     */ 
/*     */   public void writeObjectEnd()
/*     */     throws IOException
/*     */   {
/* 281 */     this.out.write(OBJECT_END_MARKER, 0, OBJECT_END_MARKER.length);
/*     */ 
/* 283 */     if (this.isDebug)
/* 284 */       this.trace.endAMFObject();
/*     */   }
/*     */ 
/*     */   protected void writeAMFBoolean(boolean b)
/*     */     throws IOException
/*     */   {
/* 297 */     if (this.isDebug) {
/* 298 */       this.trace.write(b);
/*     */     }
/* 300 */     this.out.write(1);
/*     */ 
/* 302 */     this.out.writeBoolean(b);
/*     */   }
/*     */ 
/*     */   protected void writeAMFDouble(double d)
/*     */     throws IOException
/*     */   {
/* 310 */     if (this.isDebug) {
/* 311 */       this.trace.write(d);
/*     */     }
/* 313 */     this.out.write(0);
/*     */ 
/* 315 */     this.out.writeDouble(d);
/*     */   }
/*     */ 
/*     */   protected void writeAMFDate(Date d)
/*     */     throws IOException
/*     */   {
/* 323 */     if (this.isDebug) {
/* 324 */       this.trace.write(d);
/*     */     }
/* 326 */     this.out.write(11);
/*     */ 
/* 328 */     this.out.writeDouble(d.getTime());
/* 329 */     int nCurrentTimezoneOffset = TimeZone.getDefault().getRawOffset();
/* 330 */     this.out.writeShort(nCurrentTimezoneOffset / 60000);
/*     */   }
/*     */ 
/*     */   protected void writeAMFArray(Object o, Class componentType)
/*     */     throws IOException
/*     */   {
/* 338 */     if (componentType.isPrimitive())
/*     */     {
/* 340 */       writePrimitiveArray(o);
/*     */     }
/* 342 */     else if (componentType.equals(Character.class))
/*     */     {
/* 344 */       writeCharArrayAsString((Character[])(Character[])o);
/*     */     }
/*     */     else
/*     */     {
/* 348 */       writeObjectArray((Object[])(Object[])o, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeArrayCollection(Collection col, SerializationDescriptor desc)
/*     */     throws IOException
/*     */   {
/* 357 */     if (!serializeAsReference(col))
/*     */     {
/*     */       ArrayCollection ac;
/*     */       ArrayCollection ac;
/* 361 */       if ((col instanceof ArrayCollection))
/*     */       {
/* 363 */         ac = (ArrayCollection)col;
/*     */       }
/*     */       else
/*     */       {
/* 370 */         ac = new ArrayCollection(col);
/* 371 */         if (desc != null) {
/* 372 */           ac.setDescriptor(desc);
/*     */         }
/*     */       }
/*     */ 
/* 376 */       PropertyProxy proxy = PropertyProxyRegistry.getProxy(ac);
/* 377 */       writePropertyProxy(proxy, ac);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeCustomObject(Object o)
/*     */     throws IOException
/*     */   {
/* 386 */     PropertyProxy proxy = null;
/*     */ 
/* 388 */     if ((o instanceof PropertyProxy))
/*     */     {
/* 390 */       proxy = (PropertyProxy)o;
/* 391 */       o = proxy.getDefaultInstance();
/*     */ 
/* 394 */       if (o == null)
/*     */       {
/* 396 */         writeAMFNull();
/* 397 */         return;
/*     */       }
/*     */ 
/* 402 */       if ((o instanceof Collection))
/*     */       {
/* 404 */         if (this.context.legacyCollection)
/* 405 */           writeCollection((Collection)o, proxy.getDescriptor());
/*     */         else
/* 407 */           writeArrayCollection((Collection)o, proxy.getDescriptor());
/* 408 */         return;
/*     */       }
/* 410 */       if (o.getClass().isArray())
/*     */       {
/* 412 */         writeObjectArray((Object[])(Object[])o, proxy.getDescriptor());
/* 413 */         return;
/*     */       }
/* 415 */       if ((this.context.legacyMap) && ((o instanceof Map)) && (!(o instanceof ASObject)))
/*     */       {
/* 417 */         writeMapAsECMAArray((Map)o);
/* 418 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 422 */     if (!serializeAsReference(o))
/*     */     {
/* 424 */       if (proxy == null)
/*     */       {
/* 426 */         proxy = PropertyProxyRegistry.getProxyAndRegister(o);
/*     */       }
/*     */ 
/* 429 */       writePropertyProxy(proxy, o);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writePropertyProxy(PropertyProxy pp, Object instance)
/*     */     throws IOException
/*     */   {
/* 441 */     Object newInst = pp.getInstanceToSerialize(instance);
/* 442 */     if (newInst != instance)
/*     */     {
/* 447 */       if (newInst == null) {
/* 448 */         throw new MessageException("PropertyProxy.getInstanceToSerialize class: " + pp.getClass() + " returned null for instance class: " + instance.getClass().getName());
/*     */       }
/*     */ 
/* 451 */       pp = PropertyProxyRegistry.getProxyAndRegister(newInst);
/* 452 */       instance = newInst;
/*     */     }
/*     */ 
/* 456 */     boolean externalizable = false;
/* 457 */     List propertyNames = pp.getPropertyNames(instance);
/*     */ 
/* 459 */     TraitsInfo ti = new TraitsInfo(pp.getAlias(instance), pp.isDynamic(), externalizable, propertyNames);
/* 460 */     writeObjectTraits(ti);
/*     */ 
/* 462 */     if (propertyNames != null)
/*     */     {
/* 464 */       Iterator it = propertyNames.iterator();
/* 465 */       while (it.hasNext())
/*     */       {
/* 467 */         String propName = (String)it.next();
/* 468 */         Object value = pp.getValue(instance, propName);
/* 469 */         writeObjectProperty(propName, value);
/*     */       }
/*     */     }
/*     */ 
/* 473 */     writeObjectEnd();
/*     */   }
/*     */ 
/*     */   protected void writeMapAsECMAArray(Map m)
/*     */     throws IOException
/*     */   {
/* 481 */     if (!serializeAsReference(m))
/*     */     {
/* 483 */       if (this.isDebug) {
/* 484 */         this.trace.startECMAArray(this.serializedObjectCount - 1);
/*     */       }
/* 486 */       this.out.write(8);
/* 487 */       this.out.writeInt(0);
/*     */ 
/* 489 */       Iterator it = m.keySet().iterator();
/* 490 */       while (it.hasNext())
/*     */       {
/* 492 */         Object key = it.next();
/* 493 */         Object value = m.get(key);
/* 494 */         writeObjectProperty(key.toString(), value);
/*     */       }
/*     */ 
/* 497 */       writeObjectEnd();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeAMFNull()
/*     */     throws IOException
/*     */   {
/* 506 */     if (this.isDebug) {
/* 507 */       this.trace.writeNull();
/*     */     }
/* 509 */     this.out.write(5);
/*     */   }
/*     */ 
/*     */   protected void writeAMFString(String str)
/*     */     throws IOException
/*     */   {
/* 517 */     if (this.isDebug) {
/* 518 */       this.trace.writeString(str);
/*     */     }
/* 520 */     writeUTF(str, false, true);
/*     */   }
/*     */ 
/*     */   protected void writeObjectArray(Object[] values, SerializationDescriptor descriptor)
/*     */     throws IOException
/*     */   {
/* 528 */     if (!serializeAsReference(values))
/*     */     {
/* 530 */       if (this.isDebug) {
/* 531 */         this.trace.startAMFArray(this.serializedObjectCount - 1);
/*     */       }
/* 533 */       this.out.write(10);
/* 534 */       this.out.writeInt(values.length);
/* 535 */       for (int i = 0; i < values.length; i++)
/*     */       {
/* 537 */         if (this.isDebug) {
/* 538 */           this.trace.arrayElement(i);
/*     */         }
/* 540 */         Object item = values[i];
/* 541 */         if ((item != null) && (descriptor != null) && (!(item instanceof String)) && (!(item instanceof Number)) && (!(item instanceof Boolean)) && (!(item instanceof Character)))
/*     */         {
/* 546 */           PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
/* 547 */           proxy = (PropertyProxy)proxy.clone();
/* 548 */           proxy.setDescriptor(descriptor);
/* 549 */           item = proxy;
/*     */         }
/* 551 */         writeObject(item);
/*     */       }
/*     */ 
/* 554 */       if (this.isDebug)
/* 555 */         this.trace.endAMFArray();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeCollection(Collection c, SerializationDescriptor descriptor)
/*     */     throws IOException
/*     */   {
/* 568 */     if (!serializeAsReference(c))
/*     */     {
/* 570 */       if (this.isDebug) {
/* 571 */         this.trace.startAMFArray(this.serializedObjectCount - 1);
/*     */       }
/* 573 */       this.out.write(10);
/* 574 */       this.out.writeInt(c.size());
/* 575 */       Iterator it = c.iterator();
/* 576 */       int i = 0;
/* 577 */       while (it.hasNext())
/*     */       {
/* 579 */         if (this.isDebug) {
/* 580 */           this.trace.arrayElement(i);
/*     */         }
/* 582 */         Object item = it.next();
/* 583 */         if ((item != null) && (descriptor != null) && (!(item instanceof String)) && (!(item instanceof Number)) && (!(item instanceof Boolean)) && (!(item instanceof Character)))
/*     */         {
/* 587 */           PropertyProxy proxy = PropertyProxyRegistry.getProxy(item);
/* 588 */           proxy = (PropertyProxy)proxy.clone();
/* 589 */           proxy.setDescriptor(descriptor);
/* 590 */           item = proxy;
/*     */         }
/* 592 */         writeObject(item);
/*     */       }
/*     */ 
/* 595 */       i++;
/*     */ 
/* 597 */       if (this.isDebug)
/* 598 */         this.trace.endAMFArray();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writePrimitiveArray(Object obj)
/*     */     throws IOException
/*     */   {
/* 613 */     Class aType = obj.getClass().getComponentType();
/*     */ 
/* 616 */     if (aType.equals(Character.TYPE))
/*     */     {
/* 618 */       char[] c = (char[])(char[])obj;
/* 619 */       writeCharArrayAsString(c);
/*     */     }
/* 621 */     else if (!serializeAsReference(obj))
/*     */     {
/* 623 */       if (aType.equals(Boolean.TYPE))
/*     */       {
/* 625 */         this.out.write(10);
/*     */ 
/* 627 */         boolean[] b = (boolean[])(boolean[])obj;
/* 628 */         this.out.writeInt(b.length);
/*     */ 
/* 630 */         if (this.isDebug)
/*     */         {
/* 632 */           this.trace.startAMFArray(this.serializedObjectCount - 1);
/*     */ 
/* 634 */           for (int i = 0; i < b.length; i++)
/*     */           {
/* 636 */             this.trace.arrayElement(i);
/* 637 */             writeAMFBoolean(b[i]);
/*     */           }
/*     */ 
/* 640 */           this.trace.endAMFArray();
/*     */         }
/*     */         else
/*     */         {
/* 644 */           for (int i = 0; i < b.length; i++)
/*     */           {
/* 646 */             writeAMFBoolean(b[i]);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 654 */         this.out.write(10);
/*     */ 
/* 656 */         int length = Array.getLength(obj);
/* 657 */         this.out.writeInt(length);
/*     */ 
/* 659 */         if (this.isDebug)
/*     */         {
/* 661 */           this.trace.startAMFArray(this.serializedObjectCount - 1);
/*     */ 
/* 663 */           for (int i = 0; i < length; i++)
/*     */           {
/* 665 */             this.trace.arrayElement(i);
/* 666 */             double v = Array.getDouble(obj, i);
/* 667 */             writeAMFDouble(v);
/*     */           }
/*     */ 
/* 670 */           this.trace.endAMFArray();
/*     */         }
/*     */         else
/*     */         {
/* 674 */           for (int i = 0; i < length; i++)
/*     */           {
/* 676 */             double v = Array.getDouble(obj, i);
/* 677 */             writeAMFDouble(v);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeCharArrayAsString(Character[] ca)
/*     */     throws IOException
/*     */   {
/* 689 */     int length = ca.length;
/* 690 */     char[] chars = new char[length];
/*     */ 
/* 692 */     for (int i = 0; i < length; i++)
/*     */     {
/* 694 */       Character c = ca[i];
/* 695 */       if (c == null)
/* 696 */         chars[i] = '\000';
/*     */       else
/* 698 */         chars[i] = ca[i].charValue();
/*     */     }
/* 700 */     writeCharArrayAsString(chars);
/*     */   }
/*     */ 
/*     */   protected void writeCharArrayAsString(char[] ca)
/*     */     throws IOException
/*     */   {
/* 708 */     writeAMFString(new String(ca));
/*     */   }
/*     */ 
/*     */   protected void writeUTF(String str, boolean forceLong, boolean writeType)
/*     */     throws IOException
/*     */   {
/* 716 */     int strlen = str.length();
/* 717 */     int utflen = 0;
/* 718 */     int count = 0;
/*     */ 
/* 720 */     char[] charr = getTempCharArray(strlen);
/* 721 */     str.getChars(0, strlen, charr, 0);
/*     */ 
/* 723 */     for (int i = 0; i < strlen; i++)
/*     */     {
/* 725 */       int c = charr[i];
/* 726 */       if (c <= 127)
/*     */       {
/* 728 */         utflen++;
/*     */       }
/* 730 */       else if (c > 2047)
/*     */       {
/* 732 */         utflen += 3;
/*     */       }
/*     */       else
/*     */       {
/* 736 */         utflen += 2;
/*     */       }
/*     */     }
/*     */     int type;
/*     */     int type;
/* 741 */     if (forceLong)
/*     */     {
/* 743 */       type = 12;
/*     */     }
/*     */     else
/*     */     {
/*     */       int type;
/* 747 */       if (utflen <= 65535)
/* 748 */         type = 2;
/*     */       else
/* 750 */         type = 12;
/*     */     }
/*     */     byte[] bytearr;
/* 754 */     if (writeType)
/*     */     {
/* 756 */       byte[] bytearr = getTempByteArray(utflen + (type == 2 ? 3 : 5));
/* 757 */       bytearr[(count++)] = (byte)type;
/*     */     }
/*     */     else {
/* 760 */       bytearr = getTempByteArray(utflen + (type == 2 ? 2 : 4));
/*     */     }
/* 762 */     if (type == 12)
/*     */     {
/* 764 */       bytearr[(count++)] = (byte)(utflen >>> 24 & 0xFF);
/* 765 */       bytearr[(count++)] = (byte)(utflen >>> 16 & 0xFF);
/*     */     }
/* 767 */     bytearr[(count++)] = (byte)(utflen >>> 8 & 0xFF);
/* 768 */     bytearr[(count++)] = (byte)(utflen >>> 0 & 0xFF);
/* 769 */     for (int i = 0; i < strlen; i++)
/*     */     {
/* 771 */       int c = charr[i];
/* 772 */       if (c <= 127)
/*     */       {
/* 774 */         bytearr[(count++)] = (byte)c;
/*     */       }
/* 776 */       else if (c > 2047)
/*     */       {
/* 778 */         bytearr[(count++)] = (byte)(0xE0 | c >> 12 & 0xF);
/* 779 */         bytearr[(count++)] = (byte)(0x80 | c >> 6 & 0x3F);
/* 780 */         bytearr[(count++)] = (byte)(0x80 | c >> 0 & 0x3F);
/*     */       }
/*     */       else
/*     */       {
/* 784 */         bytearr[(count++)] = (byte)(0xC0 | c >> 6 & 0x1F);
/* 785 */         bytearr[(count++)] = (byte)(0x80 | c >> 0 & 0x3F);
/*     */       }
/*     */     }
/* 788 */     this.out.write(bytearr, 0, count);
/*     */   }
/*     */ 
/*     */   protected void rememberObjectReference(Object obj)
/*     */   {
/* 799 */     this.serializedObjects.put(obj, new Integer(this.serializedObjectCount++));
/*     */   }
/*     */ 
/*     */   protected boolean serializeAsReference(Object obj)
/*     */     throws IOException
/*     */   {
/* 813 */     Object ref = this.serializedObjects.get(obj);
/* 814 */     if (ref != null)
/*     */     {
/*     */       try
/*     */       {
/* 818 */         int refNum = ((Integer)ref).intValue();
/* 819 */         this.out.write(7);
/* 820 */         this.out.writeShort(refNum);
/*     */ 
/* 822 */         if (this.isDebug)
/* 823 */           this.trace.writeRef(refNum);
/*     */       }
/*     */       catch (ClassCastException e)
/*     */       {
/* 827 */         throw new IOException("Object reference value is not an Integer");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 832 */       rememberObjectReference(obj);
/*     */     }
/* 834 */     return ref != null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.Amf0Output
 * JD-Core Version:    0.6.0
 */