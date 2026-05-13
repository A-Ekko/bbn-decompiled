/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.io.AbstractProxy;
/*     */ import flex.messaging.io.ClassAliasRegistry;
/*     */ import flex.messaging.io.PropertyProxy;
/*     */ import flex.messaging.io.PropertyProxyRegistry;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.UnknownTypeException;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UTFDataFormatException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Amf0Input extends AbstractAmfInput
/*     */   implements AmfTypes
/*     */ {
/*     */   protected ActionMessageInput avmPlusInput;
/*     */   protected List objectsTable;
/*     */ 
/*     */   public Amf0Input(SerializationContext context)
/*     */   {
/*  57 */     super(context);
/*     */ 
/*  59 */     this.objectsTable = new ArrayList(64);
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  71 */     super.reset();
/*     */ 
/*  73 */     this.objectsTable.clear();
/*     */ 
/*  75 */     if (this.avmPlusInput != null)
/*  76 */       this.avmPlusInput.reset();
/*     */   }
/*     */ 
/*     */   public Object readObject()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/*  90 */     int type = this.in.readByte();
/*     */ 
/*  92 */     Object value = readObjectValue(type);
/*  93 */     return value;
/*     */   }
/*     */ 
/*     */   protected Object readObjectValue(int type) throws ClassNotFoundException, IOException
/*     */   {
/*  98 */     Object value = null;
/*  99 */     switch (type)
/*     */     {
/*     */     case 0:
/* 102 */       double d = readDouble();
/*     */ 
/* 104 */       if (this.isDebug) {
/* 105 */         this.trace.write(d);
/*     */       }
/* 107 */       value = new Double(d);
/*     */ 
/* 109 */       break;
/*     */     case 1:
/* 112 */       value = Boolean.valueOf(readBoolean());
/*     */ 
/* 114 */       if (!this.isDebug) break;
/* 115 */       this.trace.write(value); break;
/*     */     case 2:
/* 120 */       value = readString();
/* 121 */       break;
/*     */     case 17:
/* 125 */       if (this.avmPlusInput == null)
/*     */       {
/* 127 */         this.avmPlusInput = new Amf3Input(this.context);
/* 128 */         this.avmPlusInput.setDebugTrace(this.trace);
/* 129 */         this.avmPlusInput.setInputStream(this.in);
/*     */       }
/*     */ 
/* 132 */       value = this.avmPlusInput.readObject();
/* 133 */       break;
/*     */     case 10:
/* 136 */       value = readArrayValue();
/* 137 */       break;
/*     */     case 16:
/* 140 */       String typeName = this.in.readUTF();
/* 141 */       value = readObjectValue(typeName);
/* 142 */       break;
/*     */     case 12:
/* 145 */       value = readLongUTF();
/*     */ 
/* 147 */       if (!this.isDebug) break;
/* 148 */       this.trace.writeString((String)value); break;
/*     */     case 3:
/* 152 */       value = readObjectValue(null);
/* 153 */       break;
/*     */     case 15:
/* 156 */       value = readXml();
/* 157 */       break;
/*     */     case 5:
/* 160 */       if (!this.isDebug) break;
/* 161 */       this.trace.writeNull(); break;
/*     */     case 11:
/* 165 */       value = readDate();
/*     */ 
/* 167 */       break;
/*     */     case 8:
/* 170 */       value = readECMAArrayValue();
/* 171 */       break;
/*     */     case 7:
/* 174 */       int refNum = this.in.readUnsignedShort();
/*     */ 
/* 176 */       if (this.isDebug) {
/* 177 */         this.trace.writeRef(refNum);
/*     */       }
/* 179 */       value = this.objectsTable.get(refNum);
/* 180 */       break;
/*     */     case 6:
/* 183 */       if (!this.isDebug) break;
/* 184 */       this.trace.writeUndefined(); break;
/*     */     case 13:
/* 189 */       if (this.isDebug) {
/* 190 */         this.trace.write("UNSUPPORTED");
/*     */       }
/*     */ 
/* 193 */       UnknownTypeException ex = new UnknownTypeException();
/* 194 */       ex.setMessage(10302);
/* 195 */       throw ex;
/*     */     case 9:
/* 199 */       if (this.isDebug) {
/* 200 */         this.trace.write("UNEXPECTED OBJECT END");
/*     */       }
/*     */ 
/* 203 */       UnknownTypeException ex1 = new UnknownTypeException();
/* 204 */       ex1.setMessage(10303);
/* 205 */       throw ex1;
/*     */     case 14:
/* 209 */       if (this.isDebug) {
/* 210 */         this.trace.write("UNEXPECTED RECORDSET");
/*     */       }
/*     */ 
/* 213 */       UnknownTypeException ex2 = new UnknownTypeException();
/* 214 */       ex2.setMessage(10304);
/* 215 */       throw ex2;
/*     */     case 4:
/*     */     default:
/* 219 */       if (this.isDebug) {
/* 220 */         this.trace.write("UNKNOWN TYPE");
/*     */       }
/* 222 */       UnknownTypeException ex3 = new UnknownTypeException();
/* 223 */       ex3.setMessage(10301, new Object[] { new Integer(type) });
/* 224 */       throw ex3;
/*     */     }
/* 226 */     return value;
/*     */   }
/*     */ 
/*     */   protected Date readDate() throws IOException
/*     */   {
/* 231 */     long time = ()this.in.readDouble();
/*     */ 
/* 239 */     this.in.readShort();
/*     */ 
/* 241 */     Date d = new Date(time);
/*     */ 
/* 243 */     if (this.isDebug) {
/* 244 */       this.trace.write(d.toString());
/*     */     }
/* 246 */     return d;
/*     */   }
/*     */ 
/*     */   protected Map readECMAArrayValue()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 254 */     int size = this.in.readInt();
/*     */     HashMap h;
/*     */     HashMap h;
/* 256 */     if (size == 0)
/* 257 */       h = new HashMap();
/*     */     else {
/* 259 */       h = new HashMap(size);
/*     */     }
/* 261 */     rememberObject(h);
/*     */ 
/* 263 */     if (this.isDebug) {
/* 264 */       this.trace.startECMAArray(this.objectsTable.size() - 1);
/*     */     }
/* 266 */     String name = this.in.readUTF();
/* 267 */     int type = this.in.readByte();
/* 268 */     while (type != 9)
/*     */     {
/* 270 */       if (type != 9)
/*     */       {
/* 272 */         if (this.isDebug) {
/* 273 */           this.trace.namedElement(name);
/*     */         }
/*     */ 
/* 276 */         Object value = readObjectValue(type);
/* 277 */         if (!name.equals("length")) {
/* 278 */           h.put(name, value);
/*     */         }
/*     */       }
/* 281 */       name = this.in.readUTF();
/* 282 */       type = this.in.readByte();
/*     */     }
/*     */ 
/* 285 */     if (this.isDebug) {
/* 286 */       this.trace.endAMFArray();
/*     */     }
/* 288 */     return h;
/*     */   }
/*     */ 
/*     */   protected String readString() throws IOException
/*     */   {
/* 293 */     String s = readUTF();
/*     */ 
/* 295 */     if (this.isDebug) {
/* 296 */       this.trace.writeString(s);
/*     */     }
/* 298 */     return s;
/*     */   }
/*     */ 
/*     */   protected Object readArrayValue()
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 307 */     int size = this.in.readInt();
/* 308 */     ArrayList l = new ArrayList(size);
/* 309 */     rememberObject(l);
/*     */ 
/* 311 */     if (this.isDebug) {
/* 312 */       this.trace.startAMFArray(this.objectsTable.size() - 1);
/*     */     }
/* 314 */     for (int i = 0; i < size; i++)
/*     */     {
/* 317 */       int type = this.in.readByte();
/*     */ 
/* 319 */       if (this.isDebug) {
/* 320 */         this.trace.arrayElement(i);
/*     */       }
/*     */ 
/* 323 */       l.add(readObjectValue(type));
/*     */     }
/*     */ 
/* 326 */     if (this.isDebug) {
/* 327 */       this.trace.endAMFArray();
/*     */     }
/* 329 */     if (this.context.legacyCollection) {
/* 330 */       return l;
/*     */     }
/* 332 */     return l.toArray();
/*     */   }
/*     */ 
/*     */   protected Object readObjectValue(String className)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 343 */     PropertyProxy proxy = null;
/*     */ 
/* 347 */     String aliasedClass = ClassAliasRegistry.getRegistry().getClassName(className);
/* 348 */     if (aliasedClass != null)
/* 349 */       className = aliasedClass;
/*     */     Object object;
/*     */     Object object;
/* 351 */     if ((className == null) || (className.length() == 0))
/*     */     {
/* 353 */       object = new ASObject();
/*     */     }
/* 355 */     else if (className.startsWith(">"))
/*     */     {
/* 357 */       Object object = new ASObject();
/* 358 */       ((ASObject)object).setType(className);
/*     */     }
/*     */     else
/*     */     {
/*     */       Object object;
/* 360 */       if ((this.context.instantiateTypes) || (className.startsWith("flex.")))
/*     */       {
/* 362 */         Class desiredClass = AbstractProxy.getClassFromClassName(className);
/*     */ 
/* 364 */         proxy = PropertyProxyRegistry.getRegistry().getProxyAndRegister(desiredClass);
/*     */         Object object;
/* 366 */         if (proxy == null)
/* 367 */           object = ClassUtil.createDefaultInstance(desiredClass, null);
/*     */         else {
/* 369 */           object = proxy.createInstance(className);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 374 */         object = new ASObject();
/* 375 */         ((ASObject)object).setType(className);
/*     */       }
/*     */     }
/* 378 */     if (proxy == null) {
/* 379 */       proxy = PropertyProxyRegistry.getProxyAndRegister(object);
/*     */     }
/* 381 */     int objectId = rememberObject(object);
/*     */ 
/* 383 */     if (this.isDebug) {
/* 384 */       this.trace.startAMFObject(className, this.objectsTable.size() - 1);
/*     */     }
/* 386 */     String propertyName = this.in.readUTF();
/* 387 */     int type = this.in.readByte();
/* 388 */     while (type != 9)
/*     */     {
/* 390 */       if (this.isDebug) {
/* 391 */         this.trace.namedElement(propertyName);
/*     */       }
/* 393 */       Object value = readObjectValue(type);
/* 394 */       proxy.setValue(object, propertyName, value);
/* 395 */       propertyName = this.in.readUTF();
/* 396 */       type = this.in.readByte();
/*     */     }
/*     */ 
/* 399 */     if (this.isDebug) {
/* 400 */       this.trace.endAMFObject();
/*     */     }
/*     */ 
/* 406 */     Object newObj = proxy.instanceComplete(object);
/*     */ 
/* 412 */     if (newObj != object)
/*     */     {
/* 414 */       this.objectsTable.set(objectId, newObj);
/* 415 */       object = newObj;
/*     */     }
/*     */ 
/* 418 */     return object;
/*     */   }
/*     */ 
/*     */   protected String readLongUTF()
/*     */     throws IOException
/*     */   {
/* 432 */     int utflen = this.in.readInt();
/*     */ 
/* 434 */     char[] charr = getTempCharArray(utflen);
/* 435 */     byte[] bytearr = getTempByteArray(utflen);
/* 436 */     int count = 0;
/* 437 */     int chCount = 0;
/*     */ 
/* 439 */     this.in.readFully(bytearr, 0, utflen);
/*     */ 
/* 441 */     while (count < utflen)
/*     */     {
/* 443 */       int c = bytearr[count] & 0xFF;
/*     */       int char2;
/* 444 */       switch (c >> 4)
/*     */       {
/*     */       case 0:
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 455 */         count++;
/* 456 */         charr[chCount] = (char)c;
/* 457 */         break;
/*     */       case 12:
/*     */       case 13:
/* 461 */         count += 2;
/* 462 */         if (count > utflen)
/* 463 */           throw new UTFDataFormatException();
/* 464 */         char2 = bytearr[(count - 1)];
/* 465 */         if ((char2 & 0xC0) != 128)
/* 466 */           throw new UTFDataFormatException();
/* 467 */         charr[chCount] = (char)((c & 0x1F) << 6 | char2 & 0x3F);
/* 468 */         break;
/*     */       case 14:
/* 471 */         count += 3;
/* 472 */         if (count > utflen)
/* 473 */           throw new UTFDataFormatException();
/* 474 */         char2 = bytearr[(count - 2)];
/* 475 */         int char3 = bytearr[(count - 1)];
/* 476 */         if (((char2 & 0xC0) != 128) || ((char3 & 0xC0) != 128))
/* 477 */           throw new UTFDataFormatException();
/* 478 */         charr[chCount] = (char)((c & 0xF) << 12 | (char2 & 0x3F) << 6 | (char3 & 0x3F) << 0);
/*     */ 
/* 482 */         break;
/*     */       case 8:
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       default:
/* 485 */         throw new UTFDataFormatException();
/*     */       }
/* 487 */       chCount++;
/*     */     }
/*     */ 
/* 490 */     return new String(charr, 0, chCount);
/*     */   }
/*     */ 
/*     */   protected Object readXml() throws IOException
/*     */   {
/* 495 */     String xml = readLongUTF();
/*     */ 
/* 497 */     if (this.isDebug) {
/* 498 */       this.trace.write(xml);
/*     */     }
/* 500 */     return stringToDocument(xml);
/*     */   }
/*     */ 
/*     */   protected int rememberObject(Object obj)
/*     */   {
/* 509 */     int id = this.objectsTable.size();
/* 510 */     this.objectsTable.add(obj);
/* 511 */     return id;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.Amf0Input
 * JD-Core Version:    0.6.0
 */