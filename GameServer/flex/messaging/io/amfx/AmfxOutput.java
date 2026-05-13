/*      */ package flex.messaging.io.amfx;
/*      */ 
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.io.ArrayCollection;
/*      */ import flex.messaging.io.PagedRowSet;
/*      */ import flex.messaging.io.PropertyProxy;
/*      */ import flex.messaging.io.PropertyProxyRegistry;
/*      */ import flex.messaging.io.SerializationContext;
/*      */ import flex.messaging.io.SerializationDescriptor;
/*      */ import flex.messaging.io.StatusInfoProxy;
/*      */ import flex.messaging.io.amf.ASObject;
/*      */ import flex.messaging.io.amf.AbstractAmfOutput;
/*      */ import flex.messaging.io.amf.Amf3Output;
/*      */ import flex.messaging.io.amf.AmfTrace;
/*      */ import flex.messaging.io.amf.TraitsInfo;
/*      */ import flex.messaging.util.Hex.Encoder;
/*      */ import flex.messaging.util.Trace;
/*      */ import java.io.ByteArrayOutputStream;
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
/*      */ public class AmfxOutput extends AbstractAmfOutput
/*      */   implements AmfxTypes
/*      */ {
/*      */   protected IdentityHashMap objectTable;
/*      */   protected HashMap traitsTable;
/*      */   protected HashMap stringTable;
/*      */ 
/*      */   public AmfxOutput(SerializationContext context)
/*      */   {
/*   78 */     super(context);
/*      */ 
/*   80 */     this.objectTable = new IdentityHashMap(64);
/*   81 */     this.traitsTable = new HashMap(10);
/*   82 */     this.stringTable = new HashMap(64);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*   87 */     super.reset();
/*   88 */     this.objectTable.clear();
/*   89 */     this.traitsTable.clear();
/*   90 */     this.stringTable.clear();
/*      */   }
/*      */ 
/*      */   protected Amf3Output createAMF3Output()
/*      */   {
/*  100 */     return new Amf3Output(this.context);
/*      */   }
/*      */ 
/*      */   public void writeObject(Object o)
/*      */     throws IOException
/*      */   {
/*  109 */     if (o == null)
/*      */     {
/*  111 */       writeAMFNull();
/*  112 */       return;
/*      */     }
/*      */ 
/*  115 */     if (((o instanceof String)) || ((o instanceof Character)))
/*      */     {
/*  117 */       String s = o.toString();
/*  118 */       writeString(s);
/*      */     }
/*  120 */     else if ((o instanceof Number))
/*      */     {
/*  122 */       if (((o instanceof Integer)) || ((o instanceof Short)) || ((o instanceof Byte)))
/*      */       {
/*  124 */         int i = ((Number)o).intValue();
/*  125 */         writeAMFInt(i);
/*      */       }
/*  127 */       else if ((!this.context.legacyBigNumbers) && (((o instanceof BigInteger)) || ((o instanceof BigDecimal))))
/*      */       {
/*  133 */         writeString(((Number)o).toString());
/*      */       }
/*      */       else
/*      */       {
/*  137 */         double d = ((Number)o).doubleValue();
/*  138 */         writeAMFDouble(d);
/*      */       }
/*      */     }
/*  141 */     else if ((o instanceof Boolean))
/*      */     {
/*  143 */       writeAMFBoolean(((Boolean)o).booleanValue());
/*      */     }
/*  146 */     else if ((o instanceof Date))
/*      */     {
/*  148 */       writeDate((Date)o);
/*      */     }
/*  150 */     else if ((o instanceof Calendar))
/*      */     {
/*  152 */       writeDate(((Calendar)o).getTime());
/*      */     }
/*  154 */     else if ((o instanceof Document))
/*      */     {
/*  156 */       String xml = documentToString(o);
/*      */ 
/*  158 */       int len = xml.length() + 15;
/*      */ 
/*  160 */       StringBuffer sb = new StringBuffer(len);
/*  161 */       sb.append("<xml>");
/*  162 */       writeEscapedString(sb, xml);
/*  163 */       sb.append("</xml>");
/*      */ 
/*  165 */       writeUTF(sb);
/*      */ 
/*  167 */       if (this.isDebug) {
/*  168 */         this.trace.writeString(xml);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  173 */       Class cls = o.getClass();
/*      */ 
/*  175 */       if (((o instanceof Map)) && (this.context.legacyMap) && (!(o instanceof ASObject)))
/*      */       {
/*  177 */         writeMapAsECMAArray((Map)o);
/*      */       }
/*  179 */       else if ((o instanceof Collection))
/*      */       {
/*  181 */         if (this.context.legacyCollection)
/*  182 */           writeCollection((Collection)o, null);
/*      */         else
/*  184 */           writeArrayCollection((Collection)o, null);
/*      */       }
/*  186 */       else if (cls.isArray())
/*      */       {
/*  188 */         writeAMFArray(o, cls.getComponentType());
/*      */       }
/*      */       else
/*      */       {
/*  193 */         if ((o instanceof RowSet))
/*      */         {
/*  195 */           o = new PagedRowSet((RowSet)o, 2147483647, false);
/*      */         }
/*  197 */         else if (((o instanceof Throwable)) && (this.context.legacyThrowable))
/*      */         {
/*  199 */           o = new StatusInfoProxy((Throwable)o);
/*      */         }
/*      */ 
/*  202 */         writeCustomObject(o);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeObjectTraits(TraitsInfo ti) throws IOException
/*      */   {
/*  209 */     String className = ti.getClassName();
/*      */ 
/*  211 */     if ((className == null) || (className.length() == 0))
/*      */     {
/*  213 */       writeUTF("<object>");
/*      */     }
/*      */     else
/*      */     {
/*  217 */       int len = 127;
/*  218 */       StringBuffer sb = new StringBuffer(len);
/*  219 */       sb.append("<").append("object").append(" type=\"");
/*  220 */       sb.append(className);
/*  221 */       sb.append("\">");
/*      */ 
/*  223 */       writeUTF(sb);
/*      */     }
/*      */ 
/*  226 */     if (this.isDebug) {
/*  227 */       this.trace.startAMFObject(className, this.objectTable.size() - 1);
/*      */     }
/*      */ 
/*  231 */     if ((ti.length() == 0) && (className == null))
/*      */     {
/*  233 */       writeUTF("<traits/>");
/*      */     }
/*  235 */     else if (!byReference(ti))
/*      */     {
/*  239 */       if (ti.isExternalizable())
/*      */       {
/*  241 */         writeUTF("<traits externalizable=\"true\" />");
/*      */       }
/*      */       else
/*      */       {
/*  245 */         int count = ti.getProperties().size();
/*      */ 
/*  247 */         if (count <= 0)
/*      */         {
/*  249 */           writeUTF("<traits/>");
/*      */         }
/*      */         else
/*      */         {
/*  253 */           writeUTF("<traits>");
/*      */ 
/*  255 */           for (int i = 0; i < count; i++)
/*      */           {
/*  257 */             String propName = ti.getProperty(i);
/*  258 */             writeString(propName, true);
/*      */           }
/*      */ 
/*  261 */           writeUTF("</traits>");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void writeObjectProperty(String name, Object value)
/*      */     throws IOException
/*      */   {
/*  270 */     if (this.isDebug) {
/*  271 */       this.trace.namedElement(name);
/*      */     }
/*  273 */     writeObject(value);
/*      */   }
/*      */ 
/*      */   public void writeObjectEnd() throws IOException
/*      */   {
/*  278 */     writeUTF("</object>");
/*      */ 
/*  280 */     if (this.isDebug)
/*  281 */       this.trace.endAMFObject();
/*      */   }
/*      */ 
/*      */   public void writeUTF(String s)
/*      */     throws IOException
/*      */   {
/*  290 */     byte[] bytes = s.getBytes("UTF-8");
/*  291 */     this.out.write(bytes);
/*      */   }
/*      */ 
/*      */   protected void writeAMFBoolean(boolean b)
/*      */     throws IOException
/*      */   {
/*  303 */     if (b)
/*  304 */       writeUTF("<true/>");
/*      */     else {
/*  306 */       writeUTF("<false/>");
/*      */     }
/*  308 */     if (this.isDebug)
/*  309 */       this.trace.write(b);
/*      */   }
/*      */ 
/*      */   protected void writeAMFDouble(double d)
/*      */     throws IOException
/*      */   {
/*  317 */     int buflen = 40;
/*  318 */     StringBuffer sb = new StringBuffer(buflen);
/*  319 */     sb.append("<double>");
/*  320 */     sb.append(d);
/*  321 */     sb.append("</double>");
/*      */ 
/*  323 */     writeUTF(sb);
/*      */ 
/*  325 */     if (this.isDebug)
/*  326 */       this.trace.write(d);
/*      */   }
/*      */ 
/*      */   protected void writeAMFInt(int i)
/*      */     throws IOException
/*      */   {
/*  334 */     int buflen = 25;
/*  335 */     StringBuffer sb = new StringBuffer(buflen);
/*  336 */     sb.append("<int>");
/*  337 */     sb.append(i);
/*  338 */     sb.append("</int>");
/*      */ 
/*  340 */     writeUTF(sb);
/*      */ 
/*  342 */     if (this.isDebug)
/*  343 */       this.trace.write(i);
/*      */   }
/*      */ 
/*      */   protected void writeByteArray(byte[] ba)
/*      */     throws IOException
/*      */   {
/*  351 */     int length = ba.length * 2;
/*      */ 
/*  353 */     int len = 23 + length;
/*  354 */     StringBuffer sb = new StringBuffer(len);
/*  355 */     sb.append("<bytearray>");
/*  356 */     writeUTF(sb);
/*      */ 
/*  358 */     Hex.Encoder encoder = new Hex.Encoder(ba.length * 2);
/*  359 */     encoder.encode(ba);
/*  360 */     String encoded = encoder.drain();
/*  361 */     writeUTF(encoded);
/*      */ 
/*  363 */     writeUTF("</bytearray>");
/*      */ 
/*  365 */     if (this.isDebug)
/*  366 */       this.trace.startByteArray(this.objectTable.size() - 1, ba.length);
/*      */   }
/*      */ 
/*      */   protected void writeByteArray(Byte[] ba)
/*      */     throws IOException
/*      */   {
/*  374 */     int length = ba.length;
/*  375 */     byte[] bytes = new byte[length];
/*      */ 
/*  377 */     for (int i = 0; i < length; i++)
/*      */     {
/*  379 */       Byte b = ba[i];
/*  380 */       if (b == null)
/*  381 */         bytes[i] = 0;
/*      */       else {
/*  383 */         bytes[i] = ba[i].byteValue();
/*      */       }
/*      */     }
/*  386 */     writeByteArray(bytes);
/*      */   }
/*      */ 
/*      */   public void writeUTF(StringBuffer sb)
/*      */     throws IOException
/*      */   {
/*  394 */     byte[] bytes = sb.toString().getBytes("UTF-8");
/*  395 */     this.out.write(bytes);
/*      */   }
/*      */ 
/*      */   protected void writeDate(Date d)
/*      */     throws IOException
/*      */   {
/*  403 */     if (!byReference(d))
/*      */     {
/*  405 */       int buflen = 30;
/*  406 */       long time = d.getTime();
/*  407 */       StringBuffer sb = new StringBuffer(buflen);
/*  408 */       sb.append("<date>");
/*  409 */       sb.append(time);
/*  410 */       sb.append("</date>");
/*      */ 
/*  412 */       writeUTF(sb);
/*      */ 
/*  414 */       if (this.isDebug)
/*  415 */         this.trace.write(d);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeMapAsECMAArray(Map map)
/*      */     throws IOException
/*      */   {
/*  424 */     int len = 20;
/*      */ 
/*  426 */     StringBuffer sb = new StringBuffer(len);
/*  427 */     sb.append("<").append("array").append(" ecma=\"true\">");
/*  428 */     writeUTF(sb);
/*      */ 
/*  430 */     if (this.isDebug) {
/*  431 */       this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */     }
/*  433 */     Iterator it = map.keySet().iterator();
/*  434 */     while (it.hasNext())
/*      */     {
/*  436 */       Object key = it.next();
/*  437 */       if (key != null)
/*      */       {
/*  439 */         String propName = key.toString();
/*  440 */         sb = new StringBuffer();
/*      */ 
/*  443 */         sb.append("<").append("item").append(" name=\"").append(propName).append("\">");
/*  444 */         writeUTF(sb);
/*      */ 
/*  446 */         if (this.isDebug) {
/*  447 */           this.trace.namedElement(propName);
/*      */         }
/*  449 */         writeObject(map.get(key));
/*      */ 
/*  451 */         writeUTF("</item>");
/*      */       }
/*      */     }
/*      */ 
/*  455 */     writeUTF("</array>");
/*      */ 
/*  457 */     if (this.isDebug)
/*  458 */       this.trace.endAMFArray();
/*      */   }
/*      */ 
/*      */   protected void writeAMFNull()
/*      */     throws IOException
/*      */   {
/*  466 */     writeUTF("<null/>");
/*      */ 
/*  468 */     if (this.isDebug)
/*  469 */       this.trace.writeNull();
/*      */   }
/*      */ 
/*      */   protected void writeString(String s)
/*      */     throws IOException
/*      */   {
/*  477 */     writeString(s, false);
/*      */ 
/*  479 */     if (this.isDebug)
/*  480 */       this.trace.writeString(s);
/*      */   }
/*      */ 
/*      */   protected void writeAMFArray(Object o, Class componentType)
/*      */     throws IOException
/*      */   {
/*  492 */     if (componentType.isPrimitive())
/*      */     {
/*  494 */       writePrimitiveArray(o);
/*      */     }
/*  496 */     else if (componentType.equals(Byte.class))
/*      */     {
/*  498 */       writeByteArray((Byte[])(Byte[])o);
/*      */     }
/*  500 */     else if (componentType.equals(Character.class))
/*      */     {
/*  502 */       writeCharArrayAsString((Character[])(Character[])o);
/*      */     }
/*      */     else
/*      */     {
/*  506 */       writeObjectArray((Object[])(Object[])o, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeArrayCollection(Collection col, SerializationDescriptor desc)
/*      */     throws IOException
/*      */   {
/*  515 */     if (!byReference(col))
/*      */     {
/*      */       ArrayCollection ac;
/*      */       ArrayCollection ac;
/*  519 */       if ((col instanceof ArrayCollection))
/*      */       {
/*  521 */         ac = (ArrayCollection)col;
/*      */       }
/*      */       else
/*      */       {
/*  528 */         ac = new ArrayCollection(col);
/*  529 */         if (desc != null) {
/*  530 */           ac.setDescriptor(desc);
/*      */         }
/*      */       }
/*      */ 
/*  534 */       PropertyProxy proxy = PropertyProxyRegistry.getProxy(ac);
/*  535 */       writePropertyProxy(proxy, ac);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeCustomObject(Object o)
/*      */     throws IOException
/*      */   {
/*  544 */     PropertyProxy proxy = null;
/*      */ 
/*  546 */     if ((o instanceof PropertyProxy))
/*      */     {
/*  548 */       proxy = (PropertyProxy)o;
/*  549 */       o = proxy.getDefaultInstance();
/*      */ 
/*  552 */       if (o == null)
/*      */       {
/*  554 */         writeAMFNull();
/*  555 */         return;
/*      */       }
/*      */ 
/*  560 */       if ((o instanceof Collection))
/*      */       {
/*  562 */         if (this.context.legacyCollection)
/*  563 */           writeCollection((Collection)o, proxy.getDescriptor());
/*      */         else
/*  565 */           writeArrayCollection((Collection)o, proxy.getDescriptor());
/*  566 */         return;
/*      */       }
/*  568 */       if (o.getClass().isArray())
/*      */       {
/*  570 */         writeObjectArray((Object[])(Object[])o, proxy.getDescriptor());
/*  571 */         return;
/*      */       }
/*  573 */       if ((this.context.legacyMap) && ((o instanceof Map)) && (!(o instanceof ASObject)))
/*      */       {
/*  575 */         writeMapAsECMAArray((Map)o);
/*  576 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  580 */     if (!byReference(o))
/*      */     {
/*  582 */       if (proxy == null)
/*      */       {
/*  584 */         proxy = PropertyProxyRegistry.getProxyAndRegister(o);
/*      */       }
/*      */ 
/*  587 */       writePropertyProxy(proxy, o);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writePropertyProxy(PropertyProxy pp, Object instance)
/*      */     throws IOException
/*      */   {
/*  599 */     Object newInst = pp.getInstanceToSerialize(instance);
/*  600 */     if (newInst != instance)
/*      */     {
/*  605 */       if (newInst == null) {
/*  606 */         throw new MessageException("PropertyProxy.getInstanceToSerialize class: " + pp.getClass() + " returned null for instance class: " + instance.getClass().getName());
/*      */       }
/*      */ 
/*  609 */       pp = PropertyProxyRegistry.getProxyAndRegister(newInst);
/*  610 */       instance = newInst;
/*      */     }
/*      */ 
/*  613 */     List propertyNames = null;
/*  614 */     boolean externalizable = pp.isExternalizable(instance);
/*      */ 
/*  616 */     if (!externalizable) {
/*  617 */       propertyNames = pp.getPropertyNames(instance);
/*      */     }
/*  619 */     TraitsInfo ti = new TraitsInfo(pp.getAlias(instance), pp.isDynamic(), externalizable, propertyNames);
/*  620 */     writeObjectTraits(ti);
/*      */ 
/*  622 */     if (externalizable)
/*      */     {
/*  624 */       ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*  625 */       Amf3Output objOut = createAMF3Output();
/*  626 */       objOut.setOutputStream(bout);
/*      */ 
/*  628 */       ((Externalizable)instance).writeExternal(objOut);
/*  629 */       writeByteArray(bout.toByteArray());
/*      */     }
/*  631 */     else if (propertyNames != null)
/*      */     {
/*  633 */       Iterator it = propertyNames.iterator();
/*  634 */       while (it.hasNext())
/*      */       {
/*  636 */         String propName = (String)it.next();
/*  637 */         Object value = pp.getValue(instance, propName);
/*  638 */         writeObjectProperty(propName, value);
/*      */       }
/*      */     }
/*      */ 
/*  642 */     writeObjectEnd();
/*      */   }
/*      */ 
/*      */   protected void writeString(String s, boolean isTrait)
/*      */     throws IOException
/*      */   {
/*  650 */     if (s.length() == 0)
/*      */     {
/*  652 */       writeUTF("<string/>");
/*      */     }
/*  654 */     else if (!byReference(s))
/*      */     {
/*  656 */       int len = s.length() + 35;
/*      */ 
/*  658 */       StringBuffer sb = new StringBuffer(len);
/*  659 */       sb.append("<string>");
/*      */ 
/*  662 */       if (!isTrait)
/*  663 */         writeEscapedString(sb, s);
/*      */       else {
/*  665 */         sb.append(s);
/*      */       }
/*  667 */       sb.append("</string>");
/*      */ 
/*  669 */       writeUTF(sb);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeEscapedString(StringBuffer sb, String s)
/*      */   {
/*  692 */     StringBuffer temp = new StringBuffer(s.length());
/*      */ 
/*  694 */     char[] chars = s.toCharArray();
/*  695 */     for (int i = 0; i < chars.length; i++)
/*      */     {
/*  697 */       char c = chars[i];
/*  698 */       if (c >= ' ')
/*      */       {
/*  700 */         if (c == '&')
/*      */         {
/*  702 */           temp.append("&amp;");
/*      */         }
/*  704 */         else if (c == '<')
/*      */         {
/*  706 */           temp.append("&lt;");
/*      */         }
/*  708 */         else if ((c > 55295) && ((c < 57344) || (c > 65533)))
/*      */         {
/*  710 */           temp.append("&#x").append(Integer.toHexString(c)).append(";");
/*      */         }
/*      */         else
/*      */         {
/*  714 */           temp.append(c);
/*      */         }
/*      */       }
/*  717 */       else if ((c == '\t') || (c == '\n') || (c == '\r'))
/*      */       {
/*  719 */         temp.append(c);
/*      */       }
/*      */       else
/*      */       {
/*  723 */         temp.append("&#x").append(Integer.toHexString(c)).append(";");
/*      */       }
/*      */     }
/*      */ 
/*  727 */     sb.append(temp);
/*      */   }
/*      */ 
/*      */   protected void writeCharArrayAsString(Character[] ca)
/*      */     throws IOException
/*      */   {
/*  735 */     int length = ca.length;
/*  736 */     char[] chars = new char[length];
/*      */ 
/*  738 */     for (int i = 0; i < length; i++)
/*      */     {
/*  740 */       Character c = ca[i];
/*  741 */       if (c == null)
/*  742 */         chars[i] = '\000';
/*      */       else
/*  744 */         chars[i] = ca[i].charValue();
/*      */     }
/*  746 */     writeCharArrayAsString(chars);
/*      */   }
/*      */ 
/*      */   protected void writeCharArrayAsString(char[] ca)
/*      */     throws IOException
/*      */   {
/*  754 */     String str = new String(ca);
/*  755 */     writeString(str);
/*      */   }
/*      */ 
/*      */   protected void writeCollection(Collection c, SerializationDescriptor descriptor)
/*      */     throws IOException
/*      */   {
/*  763 */     if (!byReference(c))
/*      */     {
/*  765 */       writeObjectArrayDirectly(c.toArray(), descriptor);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeObjectArray(Object[] values, SerializationDescriptor descriptor)
/*      */     throws IOException
/*      */   {
/*  774 */     if (!byReference(values))
/*      */     {
/*  776 */       writeObjectArrayDirectly(values, descriptor);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writeObjectArrayDirectly(Object[] values, SerializationDescriptor descriptor)
/*      */     throws IOException
/*      */   {
/*  785 */     int len = 25;
/*      */ 
/*  787 */     StringBuffer sb = new StringBuffer(len);
/*  788 */     sb.append("<").append("array").append(" length=\"");
/*  789 */     sb.append(values.length);
/*  790 */     sb.append("\">");
/*  791 */     writeUTF(sb);
/*      */ 
/*  793 */     if (this.isDebug) {
/*  794 */       this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */     }
/*  796 */     for (int i = 0; i < values.length; i++)
/*      */     {
/*  798 */       if (this.isDebug) {
/*  799 */         this.trace.arrayElement(i);
/*      */       }
/*  801 */       writeObject(values[i]);
/*      */     }
/*      */ 
/*  804 */     writeUTF("</array>");
/*      */ 
/*  806 */     if (this.isDebug)
/*  807 */       this.trace.endAMFArray();
/*      */   }
/*      */ 
/*      */   protected void writePrimitiveArray(Object obj)
/*      */     throws IOException
/*      */   {
/*  822 */     Class aType = obj.getClass().getComponentType();
/*      */ 
/*  824 */     if (aType.equals(Character.TYPE))
/*      */     {
/*  827 */       char[] c = (char[])(char[])obj;
/*  828 */       writeCharArrayAsString(c);
/*      */     }
/*  830 */     else if (aType.equals(Byte.TYPE))
/*      */     {
/*  832 */       writeByteArray((byte[])(byte[])obj);
/*      */     }
/*  834 */     else if (!byReference(obj))
/*      */     {
/*  836 */       int length = Array.getLength(obj);
/*      */ 
/*  838 */       int buflen = 25;
/*  839 */       StringBuffer sb = new StringBuffer(buflen);
/*  840 */       sb.append("<").append("array").append(" length=\"");
/*  841 */       sb.append(length);
/*  842 */       sb.append("\">");
/*  843 */       writeUTF(sb);
/*      */ 
/*  845 */       if (this.isDebug) {
/*  846 */         this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */       }
/*  848 */       if (aType.equals(Boolean.TYPE))
/*      */       {
/*  850 */         boolean[] b = (boolean[])(boolean[])obj;
/*      */ 
/*  852 */         for (int i = 0; i < b.length; i++)
/*      */         {
/*  854 */           if (this.isDebug) {
/*  855 */             this.trace.arrayElement(i);
/*      */           }
/*  857 */           writeAMFBoolean(b[i]);
/*      */         }
/*      */       }
/*  860 */       else if ((aType.equals(Integer.TYPE)) || (aType.equals(Short.TYPE)))
/*      */       {
/*  864 */         for (int i = 0; i < length; i++)
/*      */         {
/*  866 */           if (this.isDebug) {
/*  867 */             this.trace.arrayElement(i);
/*      */           }
/*  869 */           int v = Array.getInt(obj, i);
/*  870 */           writeAMFInt(v);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  877 */         for (int i = 0; i < length; i++)
/*      */         {
/*  879 */           if (this.isDebug) {
/*  880 */             this.trace.arrayElement(i);
/*      */           }
/*  882 */           double v = Array.getDouble(obj, i);
/*  883 */           writeAMFDouble(v);
/*      */         }
/*      */       }
/*      */ 
/*  887 */       writeUTF("</array>");
/*      */ 
/*  889 */       if (this.isDebug)
/*  890 */         this.trace.endAMFArray();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean byReference(Object o)
/*      */     throws IOException
/*      */   {
/*  906 */     Object ref = this.objectTable.get(o);
/*      */ 
/*  908 */     if (ref != null)
/*      */     {
/*      */       try
/*      */       {
/*  912 */         int refNum = ((Integer)ref).intValue();
/*      */ 
/*  914 */         int len = 20;
/*      */ 
/*  916 */         StringBuffer sb = new StringBuffer(len);
/*  917 */         sb.append("<").append("ref").append(" id=\"");
/*  918 */         sb.append(refNum);
/*  919 */         sb.append("\"/>");
/*      */ 
/*  921 */         writeUTF(sb);
/*      */ 
/*  923 */         if (this.isDebug)
/*  924 */           this.trace.writeRef(refNum);
/*      */       }
/*      */       catch (ClassCastException e)
/*      */       {
/*  928 */         throw new IOException("Object reference is not an Integer");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  933 */       this.objectTable.put(o, new Integer(this.objectTable.size()));
/*      */     }
/*      */ 
/*  936 */     return ref != null;
/*      */   }
/*      */ 
/*      */   protected boolean byReference(String s)
/*      */     throws IOException
/*      */   {
/*  944 */     Object ref = this.stringTable.get(s);
/*      */ 
/*  946 */     if (ref != null)
/*      */     {
/*      */       try
/*      */       {
/*  950 */         int refNum = ((Integer)ref).intValue();
/*      */ 
/*  952 */         int len = 20;
/*      */ 
/*  954 */         StringBuffer sb = new StringBuffer(len);
/*  955 */         sb.append("<").append("string").append(" id=\"");
/*  956 */         sb.append(refNum);
/*  957 */         sb.append("\"/>");
/*      */ 
/*  959 */         writeUTF(sb);
/*      */ 
/*  961 */         if ((Trace.amf) && (this.isDebug))
/*  962 */           this.trace.writeStringRef(refNum);
/*      */       }
/*      */       catch (ClassCastException e)
/*      */       {
/*  966 */         throw new IOException("String reference is not an Integer");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  971 */       this.stringTable.put(s, new Integer(this.stringTable.size()));
/*      */     }
/*      */ 
/*  974 */     return ref != null;
/*      */   }
/*      */ 
/*      */   protected boolean byReference(TraitsInfo ti)
/*      */     throws IOException
/*      */   {
/*  984 */     if ((ti.length() == 0) && (ti.getClassName() == null)) {
/*  985 */       return false;
/*      */     }
/*  987 */     Object ref = this.traitsTable.get(ti);
/*      */ 
/*  989 */     if (ref != null)
/*      */     {
/*      */       try
/*      */       {
/*  993 */         int refNum = ((Integer)ref).intValue();
/*      */ 
/*  995 */         int len = 20;
/*      */ 
/*  997 */         StringBuffer sb = new StringBuffer(len);
/*  998 */         sb.append("<").append("traits").append(" id=\"");
/*  999 */         sb.append(refNum);
/* 1000 */         sb.append("\"/>");
/*      */ 
/* 1002 */         writeUTF(sb);
/*      */ 
/* 1004 */         if ((Trace.amf) && (this.isDebug))
/* 1005 */           this.trace.writeTraitsInfoRef(refNum);
/*      */       }
/*      */       catch (ClassCastException e)
/*      */       {
/* 1009 */         throw new IOException("Traits reference is not an Integer");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1014 */       this.traitsTable.put(ti, new Integer(this.traitsTable.size()));
/*      */     }
/*      */ 
/* 1017 */     return ref != null;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amfx.AmfxOutput
 * JD-Core Version:    0.6.0
 */