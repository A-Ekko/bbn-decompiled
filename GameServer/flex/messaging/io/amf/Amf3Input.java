/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.io.AbstractProxy;
/*     */ import flex.messaging.io.ClassAliasRegistry;
/*     */ import flex.messaging.io.PropertyProxy;
/*     */ import flex.messaging.io.PropertyProxyRegistry;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.SerializationException;
/*     */ import flex.messaging.io.UnknownTypeException;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import flex.messaging.util.Trace;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.UTFDataFormatException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Amf3Input extends AbstractAmfInput
/*     */   implements Amf3Types
/*     */ {
/*     */   protected List objectTable;
/*     */   protected List stringTable;
/*     */   protected List traitsTable;
/*     */ 
/*     */   public Amf3Input(SerializationContext context)
/*     */   {
/*  70 */     super(context);
/*     */ 
/*  72 */     this.stringTable = new ArrayList(64);
/*  73 */     this.objectTable = new ArrayList(64);
/*  74 */     this.traitsTable = new ArrayList(10);
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  83 */     super.reset();
/*  84 */     this.stringTable.clear();
/*  85 */     this.objectTable.clear();
/*  86 */     this.traitsTable.clear();
/*     */   }
/*     */ 
/*     */   public Object saveObjectTable()
/*     */   {
/*  91 */     Object table = this.objectTable;
/*  92 */     this.objectTable = new ArrayList(64);
/*  93 */     return table;
/*     */   }
/*     */ 
/*     */   public void restoreObjectTable(Object table)
/*     */   {
/*  98 */     this.objectTable = ((ArrayList)table);
/*     */   }
/*     */ 
/*     */   public Object saveTraitsTable()
/*     */   {
/* 103 */     Object table = this.traitsTable;
/* 104 */     this.traitsTable = new ArrayList(10);
/* 105 */     return table;
/*     */   }
/*     */ 
/*     */   public void restoreTraitsTable(Object table)
/*     */   {
/* 110 */     this.traitsTable = ((ArrayList)table);
/*     */   }
/*     */ 
/*     */   public Object saveStringTable()
/*     */   {
/* 115 */     Object table = this.stringTable;
/* 116 */     this.stringTable = new ArrayList(64);
/* 117 */     return table;
/*     */   }
/*     */ 
/*     */   public void restoreStringTable(Object table)
/*     */   {
/* 122 */     this.stringTable = ((ArrayList)table);
/*     */   }
/*     */ 
/*     */   public Object readObject()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 131 */     int type = this.in.readByte();
/* 132 */     Object value = readObjectValue(type);
/* 133 */     return value;
/*     */   }
/*     */ 
/*     */   protected Object readObjectValue(int type)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 141 */     Object value = null;
/*     */ 
/* 143 */     switch (type)
/*     */     {
/*     */     case 6:
/* 146 */       value = readString();
/*     */ 
/* 148 */       if (!this.isDebug) break;
/* 149 */       this.trace.writeString((String)value); break;
/*     */     case 10:
/* 153 */       value = readScriptObject();
/* 154 */       break;
/*     */     case 9:
/* 157 */       value = readArray();
/* 158 */       break;
/*     */     case 2:
/* 161 */       value = Boolean.FALSE;
/*     */ 
/* 163 */       if (!this.isDebug) break;
/* 164 */       this.trace.write(value); break;
/*     */     case 3:
/* 168 */       value = Boolean.TRUE;
/*     */ 
/* 170 */       if (!this.isDebug) break;
/* 171 */       this.trace.write(value); break;
/*     */     case 4:
/* 175 */       int i = readUInt29();
/*     */ 
/* 177 */       i = i << 3 >> 3;
/* 178 */       value = new Integer(i);
/*     */ 
/* 180 */       if (!this.isDebug) break;
/* 181 */       this.trace.write(value); break;
/*     */     case 5:
/* 185 */       value = new Double(this.in.readDouble());
/*     */ 
/* 187 */       if (!this.isDebug) break;
/* 188 */       this.trace.write(value); break;
/*     */     case 0:
/* 192 */       if (!this.isDebug) break;
/* 193 */       this.trace.writeUndefined(); break;
/*     */     case 1:
/* 198 */       if (!this.isDebug) break;
/* 199 */       this.trace.writeNull(); break;
/*     */     case 7:
/*     */     case 11:
/* 204 */       value = readXml();
/* 205 */       break;
/*     */     case 8:
/* 208 */       value = readDate();
/*     */ 
/* 210 */       if (!this.isDebug) break;
/* 211 */       this.trace.write(value.toString()); break;
/*     */     case 12:
/* 215 */       value = readByteArray();
/* 216 */       break;
/*     */     default:
/* 219 */       UnknownTypeException ex = new UnknownTypeException();
/* 220 */       ex.setMessage(10301, new Object[] { new Integer(type) });
/* 221 */       throw ex;
/*     */     }
/*     */ 
/* 224 */     return value;
/*     */   }
/*     */ 
/*     */   protected String readString()
/*     */     throws IOException
/*     */   {
/* 232 */     int ref = readUInt29();
/*     */ 
/* 234 */     if ((ref & 0x1) == 0)
/*     */     {
/* 237 */       return getStringReference(ref >> 1);
/*     */     }
/*     */ 
/* 242 */     int len = ref >> 1;
/*     */ 
/* 246 */     if (0 == len)
/*     */     {
/* 248 */       return "";
/*     */     }
/*     */ 
/* 251 */     String str = readUTF(len);
/*     */ 
/* 254 */     this.stringTable.add(str);
/*     */ 
/* 256 */     return str;
/*     */   }
/*     */ 
/*     */   protected Date readDate()
/*     */     throws IOException
/*     */   {
/* 265 */     int ref = readUInt29();
/*     */ 
/* 267 */     if ((ref & 0x1) == 0)
/*     */     {
/* 270 */       return (Date)getObjectReference(ref >> 1);
/*     */     }
/*     */ 
/* 274 */     long time = ()this.in.readDouble();
/*     */ 
/* 276 */     Date d = new Date(time);
/*     */ 
/* 279 */     this.objectTable.add(d);
/*     */ 
/* 281 */     if (this.isDebug) {
/* 282 */       this.trace.write(d);
/*     */     }
/* 284 */     return d;
/*     */   }
/*     */ 
/*     */   protected Object readArray()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 293 */     int ref = readUInt29();
/*     */ 
/* 295 */     if ((ref & 0x1) == 0)
/*     */     {
/* 298 */       return getObjectReference(ref >> 1);
/*     */     }
/*     */ 
/* 302 */     int len = ref >> 1;
/* 303 */     Object array = null;
/*     */ 
/* 308 */     Map map = null;
/*     */     while (true)
/*     */     {
/* 311 */       String name = readString();
/* 312 */       if ((name == null) || (name.length() == 0))
/*     */         break;
/* 314 */       if (map == null)
/*     */       {
/* 316 */         map = new HashMap();
/* 317 */         array = map;
/*     */ 
/* 320 */         this.objectTable.add(array);
/*     */ 
/* 322 */         if (this.isDebug) {
/* 323 */           this.trace.startECMAArray(this.objectTable.size() - 1);
/*     */         }
/*     */       }
/* 326 */       Object value = readObject();
/* 327 */       map.put(name, value);
/*     */     }
/*     */ 
/* 332 */     if (map == null)
/*     */     {
/* 335 */       if (this.context.legacyCollection)
/*     */       {
/* 337 */         List list = new ArrayList(len);
/* 338 */         array = list;
/*     */ 
/* 341 */         this.objectTable.add(array);
/*     */ 
/* 343 */         if (this.isDebug) {
/* 344 */           this.trace.startAMFArray(this.objectTable.size() - 1);
/*     */         }
/* 346 */         for (int i = 0; i < len; i++)
/*     */         {
/* 348 */           if (this.isDebug) {
/* 349 */             this.trace.arrayElement(i);
/*     */           }
/* 351 */           Object item = readObject();
/* 352 */           list.add(i, item);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 358 */         array = new Object[len];
/*     */ 
/* 361 */         this.objectTable.add(array);
/*     */ 
/* 363 */         if (this.isDebug) {
/* 364 */           this.trace.startAMFArray(this.objectTable.size() - 1);
/*     */         }
/* 366 */         for (int i = 0; i < len; i++)
/*     */         {
/* 368 */           if (this.isDebug) {
/* 369 */             this.trace.arrayElement(i);
/*     */           }
/* 371 */           Object item = readObject();
/* 372 */           Array.set(array, i, item);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 378 */       for (int i = 0; i < len; i++)
/*     */       {
/* 380 */         if (this.isDebug) {
/* 381 */           this.trace.arrayElement(i);
/*     */         }
/* 383 */         Object item = readObject();
/* 384 */         map.put(Integer.toString(i), item);
/*     */       }
/*     */     }
/*     */ 
/* 388 */     if (this.isDebug) {
/* 389 */       this.trace.endAMFArray();
/*     */     }
/* 391 */     return array;
/*     */   }
/*     */ 
/*     */   protected Object readScriptObject()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 400 */     int ref = readUInt29();
/*     */ 
/* 402 */     if ((ref & 0x1) == 0)
/*     */     {
/* 404 */       return getObjectReference(ref >> 1);
/*     */     }
/*     */ 
/* 408 */     TraitsInfo ti = readTraits(ref);
/* 409 */     String className = ti.getClassName();
/* 410 */     boolean externalizable = ti.isExternalizable();
/*     */ 
/* 412 */     PropertyProxy proxy = null;
/*     */ 
/* 415 */     String aliasedClass = ClassAliasRegistry.getRegistry().getClassName(className);
/* 416 */     if (aliasedClass != null)
/* 417 */       className = aliasedClass;
/*     */     Object object;
/*     */     Object object;
/* 419 */     if ((className == null) || (className.length() == 0))
/*     */     {
/* 421 */       object = new ASObject();
/*     */     }
/* 423 */     else if (className.startsWith(">"))
/*     */     {
/* 425 */       Object object = new ASObject();
/* 426 */       ((ASObject)object).setType(className);
/*     */     }
/*     */     else
/*     */     {
/*     */       Object object;
/* 428 */       if ((this.context.instantiateTypes) || (className.startsWith("flex.")))
/*     */       {
/* 430 */         Class desiredClass = AbstractProxy.getClassFromClassName(className);
/*     */ 
/* 432 */         proxy = PropertyProxyRegistry.getRegistry().getProxyAndRegister(desiredClass);
/*     */         Object object;
/* 434 */         if (proxy == null)
/* 435 */           object = ClassUtil.createDefaultInstance(desiredClass, null);
/*     */         else {
/* 437 */           object = proxy.createInstance(className);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 442 */         object = new ASObject();
/* 443 */         ((ASObject)object).setType(className);
/*     */       }
/*     */     }
/* 446 */     if (proxy == null) {
/* 447 */       proxy = PropertyProxyRegistry.getProxyAndRegister(object);
/*     */     }
/*     */ 
/* 450 */     int objectId = this.objectTable.size();
/* 451 */     this.objectTable.add(object);
/*     */ 
/* 453 */     if (externalizable)
/*     */     {
/* 455 */       readExternalizable(className, object);
/*     */     }
/*     */     else
/*     */     {
/* 459 */       if (this.isDebug)
/*     */       {
/* 461 */         this.trace.startAMFObject(className, this.objectTable.size() - 1);
/*     */       }
/*     */ 
/* 464 */       int len = ti.getProperties().size();
/*     */ 
/* 466 */       for (int i = 0; i < len; i++)
/*     */       {
/* 468 */         String propName = ti.getProperty(i);
/*     */ 
/* 470 */         if (this.isDebug) {
/* 471 */           this.trace.namedElement(propName);
/*     */         }
/* 473 */         Object value = readObject();
/* 474 */         proxy.setValue(object, propName, value);
/*     */       }
/*     */ 
/* 477 */       if (ti.isDynamic())
/*     */       {
/*     */         while (true)
/*     */         {
/* 481 */           String name = readString();
/* 482 */           if ((name == null) || (name.length() == 0))
/*     */             break;
/* 484 */           if (this.isDebug) {
/* 485 */             this.trace.namedElement(name);
/*     */           }
/* 487 */           Object value = readObject();
/* 488 */           proxy.setValue(object, name, value);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 493 */     if (this.isDebug) {
/* 494 */       this.trace.endAMFObject();
/*     */     }
/*     */ 
/* 500 */     Object newObj = proxy.instanceComplete(object);
/*     */ 
/* 506 */     if (newObj != object)
/*     */     {
/* 508 */       this.objectTable.set(objectId, newObj);
/* 509 */       object = newObj;
/*     */     }
/*     */ 
/* 512 */     return object;
/*     */   }
/*     */ 
/*     */   protected void readExternalizable(String className, Object object)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 521 */     if ((object instanceof Externalizable))
/*     */     {
/* 523 */       if (this.isDebug)
/*     */       {
/* 525 */         this.trace.startExternalizableObject(className, this.objectTable.size() - 1);
/*     */       }
/*     */ 
/* 528 */       ((Externalizable)object).readExternal(this);
/*     */     }
/*     */     else
/*     */     {
/* 533 */       SerializationException ex = new SerializationException();
/* 534 */       ex.setMessage(10305, new Object[] { object.getClass().getName() });
/* 535 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected byte[] readByteArray()
/*     */     throws IOException
/*     */   {
/* 544 */     int ref = readUInt29();
/*     */ 
/* 546 */     if ((ref & 0x1) == 0)
/*     */     {
/* 548 */       return (byte[])(byte[])getObjectReference(ref >> 1);
/*     */     }
/*     */ 
/* 552 */     int len = ref >> 1;
/*     */ 
/* 554 */     byte[] ba = new byte[len];
/*     */ 
/* 557 */     this.objectTable.add(ba);
/*     */ 
/* 559 */     this.in.readFully(ba, 0, len);
/*     */ 
/* 561 */     if (this.isDebug) {
/* 562 */       this.trace.startByteArray(this.objectTable.size() - 1, len);
/*     */     }
/* 564 */     return ba;
/*     */   }
/*     */ 
/*     */   protected TraitsInfo readTraits(int ref)
/*     */     throws IOException
/*     */   {
/* 573 */     if ((ref & 0x3) == 1)
/*     */     {
/* 576 */       return getTraitReference(ref >> 2);
/*     */     }
/*     */ 
/* 580 */     boolean externalizable = (ref & 0x4) == 4;
/* 581 */     boolean dynamic = (ref & 0x8) == 8;
/* 582 */     int count = ref >> 4;
/* 583 */     String className = readString();
/*     */ 
/* 585 */     TraitsInfo ti = new TraitsInfo(className, dynamic, externalizable, count);
/*     */ 
/* 588 */     this.traitsTable.add(ti);
/*     */ 
/* 590 */     for (int i = 0; i < count; i++)
/*     */     {
/* 592 */       String propName = readString();
/* 593 */       ti.addProperty(propName);
/*     */     }
/*     */ 
/* 596 */     return ti;
/*     */   }
/*     */ 
/*     */   protected String readUTF(int utflen)
/*     */     throws IOException
/*     */   {
/* 605 */     char[] charr = getTempCharArray(utflen);
/* 606 */     byte[] bytearr = getTempByteArray(utflen);
/*     */ 
/* 608 */     int count = 0;
/* 609 */     int chCount = 0;
/*     */ 
/* 611 */     this.in.readFully(bytearr, 0, utflen);
/*     */ 
/* 613 */     while (count < utflen)
/*     */     {
/* 615 */       int c = bytearr[count] & 0xFF;
/*     */       int char2;
/* 616 */       switch (c >> 4)
/*     */       {
/*     */       case 0:
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 627 */         count++;
/* 628 */         charr[chCount] = (char)c;
/* 629 */         break;
/*     */       case 12:
/*     */       case 13:
/* 633 */         count += 2;
/* 634 */         if (count > utflen)
/* 635 */           throw new UTFDataFormatException();
/* 636 */         char2 = bytearr[(count - 1)];
/* 637 */         if ((char2 & 0xC0) != 128)
/* 638 */           throw new UTFDataFormatException();
/* 639 */         charr[chCount] = (char)((c & 0x1F) << 6 | char2 & 0x3F);
/* 640 */         break;
/*     */       case 14:
/* 643 */         count += 3;
/* 644 */         if (count > utflen)
/* 645 */           throw new UTFDataFormatException();
/* 646 */         char2 = bytearr[(count - 2)];
/* 647 */         int char3 = bytearr[(count - 1)];
/* 648 */         if (((char2 & 0xC0) != 128) || ((char3 & 0xC0) != 128))
/* 649 */           throw new UTFDataFormatException();
/* 650 */         charr[chCount] = (char)((c & 0xF) << 12 | (char2 & 0x3F) << 6 | (char3 & 0x3F) << 0);
/*     */ 
/* 654 */         break;
/*     */       case 8:
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       default:
/* 657 */         throw new UTFDataFormatException();
/*     */       }
/* 659 */       chCount++;
/*     */     }
/*     */ 
/* 662 */     return new String(charr, 0, chCount);
/*     */   }
/*     */ 
/*     */   protected int readUInt29()
/*     */     throws IOException
/*     */   {
/* 686 */     int b = this.in.readByte() & 0xFF;
/*     */ 
/* 688 */     if (b < 128)
/*     */     {
/* 690 */       return b;
/*     */     }
/*     */ 
/* 693 */     int value = (b & 0x7F) << 7;
/* 694 */     b = this.in.readByte() & 0xFF;
/*     */ 
/* 696 */     if (b < 128)
/*     */     {
/* 698 */       return value | b;
/*     */     }
/*     */ 
/* 701 */     value = (value | b & 0x7F) << 7;
/* 702 */     b = this.in.readByte() & 0xFF;
/*     */ 
/* 704 */     if (b < 128)
/*     */     {
/* 706 */       return value | b;
/*     */     }
/*     */ 
/* 709 */     value = (value | b & 0x7F) << 8;
/* 710 */     b = this.in.readByte() & 0xFF;
/*     */ 
/* 712 */     return value | b;
/*     */   }
/*     */ 
/*     */   protected Object readXml()
/*     */     throws IOException
/*     */   {
/* 720 */     String xml = null;
/*     */ 
/* 722 */     int ref = readUInt29();
/*     */ 
/* 724 */     if ((ref & 0x1) == 0)
/*     */     {
/* 727 */       xml = (String)getObjectReference(ref >> 1);
/*     */     }
/*     */     else
/*     */     {
/* 732 */       int len = ref >> 1;
/*     */ 
/* 736 */       if (0 == len)
/* 737 */         xml = "";
/*     */       else {
/* 739 */         xml = readUTF(len);
/*     */       }
/*     */ 
/* 742 */       this.objectTable.add(xml);
/*     */ 
/* 744 */       if (this.isDebug) {
/* 745 */         this.trace.write(xml);
/*     */       }
/*     */     }
/* 748 */     return stringToDocument(xml);
/*     */   }
/*     */ 
/*     */   protected Object getObjectReference(int ref)
/*     */   {
/* 756 */     if (this.isDebug)
/*     */     {
/* 758 */       this.trace.writeRef(ref);
/*     */     }
/*     */ 
/* 761 */     return this.objectTable.get(ref);
/*     */   }
/*     */ 
/*     */   protected String getStringReference(int ref)
/*     */   {
/* 769 */     String str = (String)this.stringTable.get(ref);
/*     */ 
/* 771 */     if ((Trace.amf) && (this.isDebug))
/*     */     {
/* 773 */       this.trace.writeStringRef(ref);
/*     */     }
/*     */ 
/* 776 */     return str;
/*     */   }
/*     */ 
/*     */   protected TraitsInfo getTraitReference(int ref)
/*     */   {
/* 784 */     if ((Trace.amf) && (this.isDebug))
/*     */     {
/* 786 */       this.trace.writeTraitsInfoRef(ref);
/*     */     }
/*     */ 
/* 789 */     return (TraitsInfo)this.traitsTable.get(ref);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.Amf3Input
 * JD-Core Version:    0.6.0
 */