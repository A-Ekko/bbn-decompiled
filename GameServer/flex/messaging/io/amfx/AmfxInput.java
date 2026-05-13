/*      */ package flex.messaging.io.amfx;
/*      */ 
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.io.AbstractProxy;
/*      */ import flex.messaging.io.ArrayCollection;
/*      */ import flex.messaging.io.BeanProxy;
/*      */ import flex.messaging.io.ClassAliasRegistry;
/*      */ import flex.messaging.io.PropertyProxy;
/*      */ import flex.messaging.io.PropertyProxyRegistry;
/*      */ import flex.messaging.io.SerializationContext;
/*      */ import flex.messaging.io.SerializationException;
/*      */ import flex.messaging.io.TypeMarshallingContext;
/*      */ import flex.messaging.io.amf.ASObject;
/*      */ import flex.messaging.io.amf.ActionMessage;
/*      */ import flex.messaging.io.amf.Amf3Input;
/*      */ import flex.messaging.io.amf.AmfTrace;
/*      */ import flex.messaging.io.amf.MessageBody;
/*      */ import flex.messaging.io.amf.MessageHeader;
/*      */ import flex.messaging.util.ClassUtil;
/*      */ import flex.messaging.util.Hex.Decoder;
/*      */ import flex.messaging.util.XMLUtil;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.Externalizable;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Array;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.EmptyStackException;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Stack;
/*      */ import org.xml.sax.Attributes;
/*      */ 
/*      */ public class AmfxInput
/*      */ {
/*      */   private SerializationContext context;
/*   76 */   private BeanProxy beanproxy = new BeanProxy();
/*      */   private final ArrayList objectTable;
/*      */   private final ArrayList stringTable;
/*      */   private final ArrayList traitsTable;
/*      */   private StringBuffer text;
/*      */   private ActionMessage message;
/*      */   private MessageHeader currentHeader;
/*      */   private MessageBody currentBody;
/*      */   private Stack objectStack;
/*      */   private Stack proxyStack;
/*      */   private Stack arrayPropertyStack;
/*      */   private Stack ecmaArrayIndexStack;
/*      */   private Stack strictArrayIndexStack;
/*      */   private Stack traitsStack;
/*      */   private boolean isStringReference;
/*      */   private boolean isTraitProperty;
/*      */   protected boolean isDebug;
/*      */   protected AmfTrace trace;
/*      */ 
/*      */   public AmfxInput(SerializationContext context)
/*      */   {
/*  104 */     this.context = context;
/*      */ 
/*  106 */     this.stringTable = new ArrayList(64);
/*  107 */     this.objectTable = new ArrayList(64);
/*  108 */     this.traitsTable = new ArrayList(10);
/*      */ 
/*  110 */     this.objectStack = new Stack();
/*  111 */     this.proxyStack = new Stack();
/*  112 */     this.arrayPropertyStack = new Stack();
/*  113 */     this.strictArrayIndexStack = new Stack();
/*  114 */     this.ecmaArrayIndexStack = new Stack();
/*  115 */     this.traitsStack = new Stack();
/*      */ 
/*  117 */     this.text = new StringBuffer(32);
/*      */   }
/*      */ 
/*      */   public void reset()
/*      */   {
/*  122 */     this.stringTable.clear();
/*  123 */     this.objectTable.clear();
/*  124 */     this.traitsTable.clear();
/*  125 */     this.objectStack.clear();
/*  126 */     this.proxyStack.clear();
/*  127 */     this.arrayPropertyStack.clear();
/*  128 */     this.traitsStack.clear();
/*  129 */     this.currentBody = null;
/*  130 */     this.currentHeader = null;
/*      */ 
/*  132 */     TypeMarshallingContext marshallingContext = TypeMarshallingContext.getTypeMarshallingContext();
/*  133 */     marshallingContext.reset();
/*      */   }
/*      */ 
/*      */   public void setDebugTrace(AmfTrace trace)
/*      */   {
/*  138 */     this.trace = trace;
/*  139 */     this.isDebug = (this.trace != null);
/*      */   }
/*      */ 
/*      */   public void setActionMessage(ActionMessage msg)
/*      */   {
/*  144 */     this.message = msg;
/*      */   }
/*      */ 
/*      */   public Object readObject() throws IOException
/*      */   {
/*  149 */     return null;
/*      */   }
/*      */ 
/*      */   public void text(String s)
/*      */   {
/*  157 */     this.text.append(s);
/*      */   }
/*      */ 
/*      */   public void start_amfx(Attributes attributes)
/*      */   {
/*  167 */     String ver = attributes.getValue("ver");
/*  168 */     int version = 3;
/*  169 */     if (ver != null)
/*      */     {
/*      */       try
/*      */       {
/*  173 */         version = Integer.parseInt(ver);
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/*  177 */         throw new MessageException("Unknown version: " + ver);
/*      */       }
/*      */     }
/*      */ 
/*  181 */     if (this.isDebug) {
/*  182 */       this.trace.version(version);
/*      */     }
/*  184 */     this.message.setVersion(version);
/*      */   }
/*      */ 
/*      */   public void end_amfx()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void start_header(Attributes attributes)
/*      */   {
/*  193 */     if ((this.currentHeader != null) || (this.currentBody != null)) {
/*  194 */       throw new MessageException("Unexpected header tag.");
/*      */     }
/*  196 */     this.currentHeader = new MessageHeader();
/*      */ 
/*  198 */     String name = attributes.getValue("name");
/*  199 */     this.currentHeader.setName(name);
/*      */ 
/*  201 */     String mu = attributes.getValue("mustUnderstand");
/*  202 */     boolean mustUnderstand = false;
/*  203 */     if (mu != null)
/*      */     {
/*  205 */       mustUnderstand = Boolean.valueOf(mu).booleanValue();
/*  206 */       this.currentHeader.setMustUnderstand(mustUnderstand);
/*      */     }
/*      */ 
/*  209 */     if (this.isDebug)
/*  210 */       this.trace.startHeader(name, mustUnderstand, this.message.getHeaderCount());
/*      */   }
/*      */ 
/*      */   public void end_header()
/*      */   {
/*  215 */     this.message.addHeader(this.currentHeader);
/*  216 */     this.currentHeader = null;
/*      */ 
/*  218 */     if (this.isDebug)
/*  219 */       this.trace.endHeader();
/*      */   }
/*      */ 
/*      */   public void start_body(Attributes attributes)
/*      */   {
/*  224 */     if ((this.currentBody != null) || (this.currentHeader != null)) {
/*  225 */       throw new MessageException("Unexpected body tag.");
/*      */     }
/*  227 */     this.currentBody = new MessageBody();
/*      */ 
/*  229 */     String targetURI = attributes.getValue("targetURI");
/*  230 */     this.currentBody.setTargetURI(targetURI);
/*      */ 
/*  232 */     String responseURI = attributes.getValue("responseURI");
/*  233 */     this.currentBody.setResponseURI(responseURI);
/*      */ 
/*  235 */     if (this.isDebug)
/*  236 */       this.trace.startMessage(targetURI, responseURI, this.message.getBodyCount());
/*      */   }
/*      */ 
/*      */   public void end_body()
/*      */   {
/*  241 */     this.message.addBody(this.currentBody);
/*  242 */     this.currentBody = null;
/*      */ 
/*  244 */     if (this.isDebug)
/*  245 */       this.trace.endMessage();
/*      */   }
/*      */ 
/*      */   public void start_array(Attributes attributes)
/*      */   {
/*  255 */     int length = 10;
/*  256 */     String len = attributes.getValue("length");
/*  257 */     if (len != null)
/*      */     {
/*      */       try
/*      */       {
/*  261 */         len = len.trim();
/*  262 */         length = Integer.parseInt(len);
/*  263 */         if (length < 0)
/*  264 */           throw new NumberFormatException();
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/*  268 */         throw new MessageException("Invalid array length: " + len);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  273 */     String ecma = attributes.getValue("ecma");
/*  274 */     boolean isECMA = "true".equalsIgnoreCase(ecma);
/*      */     Object array;
/*      */     Object array;
/*  278 */     if (isECMA)
/*      */     {
/*  280 */       array = new HashMap();
/*      */     }
/*      */     else
/*      */     {
/*      */       Object array;
/*  282 */       if (this.context.legacyCollection)
/*      */       {
/*  284 */         array = new ArrayList(length);
/*      */       }
/*      */       else
/*      */       {
/*  288 */         array = new Object[length];
/*      */       }
/*      */     }
/*  291 */     setValue(array);
/*      */ 
/*  293 */     this.ecmaArrayIndexStack.push(new int[] { 0 });
/*  294 */     this.strictArrayIndexStack.push(new int[] { 0 });
/*      */ 
/*  296 */     this.objectTable.add(array);
/*  297 */     this.objectStack.push(array);
/*  298 */     this.proxyStack.push(null);
/*      */ 
/*  300 */     if (isECMA)
/*      */     {
/*  302 */       if (this.isDebug) {
/*  303 */         this.trace.startECMAArray(this.objectTable.size() - 1);
/*      */       }
/*      */ 
/*      */     }
/*  307 */     else if (this.isDebug)
/*  308 */       this.trace.startAMFArray(this.objectTable.size() - 1);
/*      */   }
/*      */ 
/*      */   public void end_array()
/*      */   {
/*      */     try
/*      */     {
/*  316 */       this.objectStack.pop();
/*  317 */       this.proxyStack.pop();
/*  318 */       this.ecmaArrayIndexStack.pop();
/*  319 */       this.strictArrayIndexStack.pop();
/*      */     }
/*      */     catch (EmptyStackException ex)
/*      */     {
/*  323 */       throw new MessageException("Unexpected end of array");
/*      */     }
/*      */ 
/*  326 */     if (this.isDebug)
/*  327 */       this.trace.endAMFArray();
/*      */   }
/*      */ 
/*      */   public void start_bytearray(Attributes attributes)
/*      */   {
/*  334 */     this.text.delete(0, this.text.length());
/*      */   }
/*      */ 
/*      */   public void end_bytearray()
/*      */   {
/*  339 */     String bs = this.text.toString().trim();
/*      */ 
/*  341 */     Hex.Decoder decoder = new Hex.Decoder();
/*  342 */     decoder.decode(bs);
/*  343 */     byte[] value = decoder.drain();
/*      */ 
/*  345 */     setValue(value);
/*      */ 
/*  347 */     if (this.isDebug)
/*  348 */       this.trace.startByteArray(this.objectTable.size() - 1, bs.length());
/*      */   }
/*      */ 
/*      */   public void start_date(Attributes attributes)
/*      */   {
/*  353 */     this.text.delete(0, this.text.length());
/*      */   }
/*      */ 
/*      */   public void end_date()
/*      */   {
/*  358 */     String d = this.text.toString().trim();
/*      */     try
/*      */     {
/*  361 */       long l = Long.parseLong(d);
/*  362 */       Date date = new Date(l);
/*  363 */       setValue(date);
/*      */ 
/*  365 */       this.objectTable.add(date);
/*      */ 
/*  367 */       if (this.isDebug)
/*  368 */         this.trace.write(date);
/*      */     }
/*      */     catch (NumberFormatException ex)
/*      */     {
/*  372 */       throw new MessageException("Invalid date: " + d);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void start_double(Attributes attributes)
/*      */   {
/*  378 */     this.text.delete(0, this.text.length());
/*      */   }
/*      */ 
/*      */   public void end_double()
/*      */   {
/*  383 */     String ds = this.text.toString().trim();
/*      */     try
/*      */     {
/*  386 */       Double d = Double.valueOf(ds);
/*  387 */       setValue(d);
/*      */ 
/*  389 */       if (this.isDebug)
/*  390 */         this.trace.write(d.doubleValue());
/*      */     }
/*      */     catch (NumberFormatException ex)
/*      */     {
/*  394 */       throw new MessageException("Invalid double: " + ds);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void start_false(Attributes attributes)
/*      */   {
/*  400 */     setValue(Boolean.FALSE);
/*      */ 
/*  402 */     if (this.isDebug)
/*  403 */       this.trace.write(false);
/*      */   }
/*      */ 
/*      */   public void end_false()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void start_item(Attributes attributes)
/*      */   {
/*  412 */     String name = attributes.getValue("name");
/*  413 */     if (name != null)
/*      */     {
/*  415 */       name = name.trim();
/*  416 */       if (name.length() <= 0) {
/*  417 */         throw new MessageException("Array item names cannot be the empty string.");
/*      */       }
/*  419 */       char c = name.charAt(0);
/*  420 */       if ((!Character.isLetterOrDigit(c)) && (c != '_')) {
/*  421 */         throw new MessageException("Invalid item name: " + name + ". Array item names must start with a letter, a digit or the underscore '_' character.");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  426 */       throw new MessageException("Array item must have a name attribute.");
/*      */     }
/*      */ 
/*  430 */     Object o = this.objectStack.peek();
/*  431 */     if (!(o instanceof Map))
/*      */     {
/*  433 */       throw new MessageException("Unexpected array item name: " + name + ". Please set the ecma attribute to 'true'.");
/*      */     }
/*      */ 
/*  437 */     this.arrayPropertyStack.push(name);
/*      */   }
/*      */ 
/*      */   public void end_item()
/*      */   {
/*  442 */     this.arrayPropertyStack.pop();
/*      */   }
/*      */ 
/*      */   public void start_int(Attributes attributes)
/*      */   {
/*  447 */     this.text.delete(0, this.text.length());
/*      */   }
/*      */ 
/*      */   public void end_int()
/*      */   {
/*  452 */     String is = this.text.toString().trim();
/*      */     try
/*      */     {
/*  455 */       Integer i = Integer.valueOf(is);
/*  456 */       setValue(i);
/*      */ 
/*  458 */       if (this.isDebug)
/*  459 */         this.trace.write(i.intValue());
/*      */     }
/*      */     catch (NumberFormatException ex)
/*      */     {
/*  463 */       throw new MessageException("Invalid int: " + is);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void start_null(Attributes attributes)
/*      */   {
/*  469 */     setValue(null);
/*      */ 
/*  471 */     if (this.isDebug)
/*  472 */       this.trace.writeNull();
/*      */   }
/*      */ 
/*      */   public void end_null()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void start_object(Attributes attributes)
/*      */   {
/*  483 */     PropertyProxy proxy = null;
/*      */ 
/*  485 */     String type = attributes.getValue("type");
/*  486 */     if (type != null)
/*      */     {
/*  488 */       type = type.trim();
/*      */     }
/*      */     Object object;
/*  493 */     if ((type != null) && (type.length() > 0))
/*      */     {
/*  496 */       String aliasedClass = ClassAliasRegistry.getRegistry().getClassName(type);
/*  497 */       if (aliasedClass != null)
/*  498 */         type = aliasedClass;
/*      */       Object object;
/*  500 */       if ((type == null) || (type.length() == 0))
/*      */       {
/*  502 */         object = new ASObject();
/*      */       }
/*  504 */       else if (type.startsWith(">"))
/*      */       {
/*  506 */         Object object = new ASObject();
/*  507 */         ((ASObject)object).setType(type);
/*      */       }
/*      */       else
/*      */       {
/*      */         Object object;
/*  509 */         if ((this.context.instantiateTypes) || (type.startsWith("flex.")))
/*      */         {
/*  511 */           Class desiredClass = AbstractProxy.getClassFromClassName(type);
/*      */ 
/*  513 */           proxy = PropertyProxyRegistry.getRegistry().getProxyAndRegister(desiredClass);
/*      */           Object object;
/*  515 */           if (proxy == null)
/*  516 */             object = ClassUtil.createDefaultInstance(desiredClass, null);
/*      */           else {
/*  518 */             object = proxy.createInstance(type);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  523 */           Object object = new ASObject();
/*  524 */           ((ASObject)object).setType(type);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  530 */       object = new ASObject(type);
/*      */     }
/*      */ 
/*  533 */     if (proxy == null) {
/*  534 */       proxy = PropertyProxyRegistry.getProxyAndRegister(object);
/*      */     }
/*  536 */     this.objectStack.push(object);
/*  537 */     this.proxyStack.push(proxy);
/*  538 */     this.objectTable.add(object);
/*      */ 
/*  540 */     if (this.isDebug)
/*  541 */       this.trace.startAMFObject(type, this.objectTable.size() - 1);
/*      */   }
/*      */ 
/*      */   public void end_object()
/*      */   {
/*  549 */     if (!this.traitsStack.empty()) {
/*  550 */       this.traitsStack.pop();
/*      */     }
/*  552 */     if (!this.objectStack.empty())
/*      */     {
/*  554 */       Object obj = this.objectStack.pop();
/*  555 */       PropertyProxy proxy = (PropertyProxy)this.proxyStack.pop();
/*      */ 
/*  557 */       Object newObj = proxy == null ? obj : proxy.instanceComplete(obj);
/*  558 */       if (newObj != obj)
/*      */       {
/*  563 */         for (int i = 0; (i < this.objectTable.size()) && 
/*  564 */           (this.objectTable.get(i) != obj); i++);
/*  567 */         if (i != this.objectTable.size()) {
/*  568 */           this.objectTable.set(i, newObj);
/*      */         }
/*  570 */         obj = newObj;
/*      */       }
/*  572 */       setValue(obj);
/*      */     }
/*      */     else
/*      */     {
/*  576 */       throw new MessageException("Unexpected end of object.");
/*      */     }
/*      */ 
/*  579 */     if (this.isDebug)
/*  580 */       this.trace.endAMFObject();
/*      */   }
/*      */ 
/*      */   public void start_ref(Attributes attributes)
/*      */   {
/*  585 */     String id = attributes.getValue("id");
/*  586 */     if (id != null)
/*      */     {
/*      */       try
/*      */       {
/*  590 */         int i = Integer.parseInt(id);
/*  591 */         Object o = this.objectTable.get(i);
/*  592 */         setValue(o);
/*      */ 
/*  594 */         if (this.isDebug)
/*  595 */           this.trace.writeRef(i);
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/*  599 */         throw new MessageException("Invalid object reference: " + id);
/*      */       }
/*      */       catch (IndexOutOfBoundsException ex)
/*      */       {
/*  603 */         throw new MessageException("Unknown object reference: " + id);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  608 */       throw new MessageException("Unknown object reference: " + id);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void end_ref()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void start_string(Attributes attributes)
/*      */   {
/*  619 */     String id = attributes.getValue("id");
/*  620 */     if (id != null)
/*      */     {
/*  622 */       this.isStringReference = true;
/*      */       try
/*      */       {
/*  626 */         int i = Integer.parseInt(id);
/*  627 */         String s = (String)this.stringTable.get(i);
/*  628 */         if (this.isTraitProperty)
/*      */         {
/*  630 */           TraitsContext traitsContext = (TraitsContext)this.traitsStack.peek();
/*  631 */           traitsContext.add(s);
/*      */         }
/*      */         else
/*      */         {
/*  635 */           setValue(s);
/*      */         }
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/*  640 */         throw new MessageException("Invalid string reference: " + id);
/*      */       }
/*      */       catch (IndexOutOfBoundsException ex)
/*      */       {
/*  644 */         throw new MessageException("Unknown string reference: " + id);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  649 */       this.text.delete(0, this.text.length());
/*  650 */       this.isStringReference = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void end_string()
/*      */   {
/*  656 */     if (!this.isStringReference)
/*      */     {
/*  658 */       String s = this.text.toString();
/*      */ 
/*  662 */       if (s.length() > 0)
/*      */       {
/*  665 */         if (!this.isTraitProperty) {
/*  666 */           s = unescapeCloseCDATA(s);
/*      */         }
/*  668 */         this.stringTable.add(s);
/*      */       }
/*      */ 
/*  671 */       if (this.isTraitProperty)
/*      */       {
/*  673 */         TraitsContext traitsContext = (TraitsContext)this.traitsStack.peek();
/*  674 */         traitsContext.add(s);
/*      */       }
/*      */       else
/*      */       {
/*  678 */         setValue(s);
/*      */ 
/*  680 */         if (this.isDebug)
/*  681 */           this.trace.writeString(s);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void start_traits(Attributes attributes)
/*      */   {
/*  689 */     if (!this.objectStack.empty())
/*      */     {
/*  691 */       List traitsList = new ArrayList();
/*  692 */       TraitsContext traitsContext = new TraitsContext(traitsList, null);
/*  693 */       this.traitsStack.push(traitsContext);
/*      */ 
/*  695 */       String id = attributes.getValue("id");
/*  696 */       if (id != null)
/*      */       {
/*      */         try
/*      */         {
/*  700 */           int i = Integer.parseInt(id);
/*  701 */           List l = (List)this.traitsTable.get(i);
/*      */ 
/*  703 */           Iterator it = l.iterator();
/*  704 */           while (it.hasNext())
/*      */           {
/*  706 */             String prop = (String)it.next();
/*  707 */             traitsList.add(prop);
/*      */           }
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*  712 */           throw new MessageException("Invalid traits reference: " + id);
/*      */         }
/*      */         catch (IndexOutOfBoundsException ex)
/*      */         {
/*  716 */           throw new MessageException("Unknown traits reference: " + id);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  721 */         boolean externalizable = false;
/*      */ 
/*  723 */         String ext = attributes.getValue("externalizable");
/*  724 */         if (ext != null)
/*      */         {
/*  726 */           externalizable = "true".equals(ext.trim());
/*      */         }
/*      */ 
/*  729 */         Object obj = this.objectStack.peek();
/*  730 */         if ((externalizable) && (!(obj instanceof Externalizable)))
/*      */         {
/*  733 */           SerializationException ex = new SerializationException();
/*  734 */           ex.setMessage(10305, new Object[] { obj.getClass().getName() });
/*  735 */           throw ex;
/*      */         }
/*      */ 
/*  738 */         this.traitsTable.add(traitsList);
/*      */       }
/*      */ 
/*  741 */       this.isTraitProperty = true;
/*      */     }
/*      */     else
/*      */     {
/*  745 */       throw new MessageException("Unexpected traits");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void end_traits()
/*      */   {
/*  751 */     this.isTraitProperty = false;
/*      */   }
/*      */ 
/*      */   public void start_true(Attributes attributes)
/*      */   {
/*  757 */     setValue(Boolean.TRUE);
/*      */ 
/*  759 */     if (this.isDebug)
/*  760 */       this.trace.write(true);
/*      */   }
/*      */ 
/*      */   public void end_true()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void start_undefined(Attributes attributes)
/*      */   {
/*  769 */     setValue(null);
/*      */ 
/*  771 */     if (this.isDebug)
/*  772 */       this.trace.writeUndefined();
/*      */   }
/*      */ 
/*      */   public void end_undefined()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void start_xml(Attributes attributes)
/*      */   {
/*  781 */     this.text.delete(0, this.text.length());
/*      */   }
/*      */ 
/*      */   public void end_xml()
/*      */   {
/*  786 */     String xml = this.text.toString();
/*  787 */     xml = unescapeCloseCDATA(xml);
/*      */ 
/*  789 */     Object value = XMLUtil.stringToDocument(xml, !this.context.legacyXMLNamespaces);
/*  790 */     setValue(value);
/*      */   }
/*      */ 
/*      */   private String unescapeCloseCDATA(String s)
/*      */   {
/*  796 */     if ((s.length() > 5) && (s.indexOf("]]&gt;") != -1))
/*      */     {
/*  798 */       s = s.replaceAll("]]&gt;", "]]>");
/*      */     }
/*      */ 
/*  801 */     return s;
/*      */   }
/*      */ 
/*      */   private void setValue(Object value)
/*      */   {
/*  806 */     if (this.objectStack.empty())
/*      */     {
/*  809 */       if (this.currentHeader != null)
/*      */       {
/*  811 */         this.currentHeader.setData(value);
/*      */       }
/*  815 */       else if (this.currentBody != null)
/*      */       {
/*  817 */         this.currentBody.setData(value);
/*      */       }
/*      */       else
/*      */       {
/*  822 */         throw new MessageException("Unexpected value: " + value);
/*      */       }
/*      */ 
/*  825 */       return;
/*      */     }
/*      */ 
/*  830 */     Object obj = this.objectStack.peek();
/*      */ 
/*  833 */     if ((obj instanceof Externalizable))
/*      */     {
/*  835 */       if ((value != null) && (value.getClass().isArray()) && (Byte.TYPE.equals(value.getClass().getComponentType())))
/*      */       {
/*  837 */         Externalizable extern = (Externalizable)obj;
/*  838 */         Amf3Input objIn = new Amf3Input(this.context);
/*  839 */         byte[] ba = (byte[])(byte[])value;
/*  840 */         ByteArrayInputStream baIn = new ByteArrayInputStream(ba);
/*      */         try
/*      */         {
/*  844 */           objIn.setInputStream(baIn);
/*  845 */           extern.readExternal(objIn);
/*      */         }
/*      */         catch (ClassNotFoundException ex)
/*      */         {
/*  849 */           throw new MessageException("Error while reading Externalizable class " + extern.getClass().getName(), ex);
/*      */         }
/*      */         catch (IOException ex)
/*      */         {
/*  853 */           throw new MessageException("Error while reading Externalizable class " + extern.getClass().getName(), ex);
/*      */         }
/*      */         finally
/*      */         {
/*      */           try
/*      */           {
/*  859 */             objIn.close();
/*      */           }
/*      */           catch (IOException ex)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  868 */         throw new MessageException("Error while reading Externalizable class. Value must be a byte array.");
/*      */       }
/*      */ 
/*      */     }
/*  873 */     else if ((obj instanceof ASObject))
/*      */     {
/*  877 */       TraitsContext traitsContext = (TraitsContext)this.traitsStack.peek();
/*      */       String prop;
/*      */       try {
/*  880 */         prop = traitsContext.next();
/*      */       }
/*      */       catch (IndexOutOfBoundsException ex)
/*      */       {
/*  884 */         throw new MessageException("Object has no trait info for value: " + value);
/*      */       }
/*      */ 
/*  887 */       ASObject aso = (ASObject)obj;
/*  888 */       aso.put(prop, value);
/*      */ 
/*  890 */       if (this.isDebug) {
/*  891 */         this.trace.namedElement(prop);
/*      */       }
/*      */ 
/*      */     }
/*  895 */     else if (((obj instanceof ArrayList)) && (!(obj instanceof ArrayCollection)))
/*      */     {
/*  897 */       ArrayList list = (ArrayList)obj;
/*  898 */       list.add(value);
/*      */ 
/*  900 */       if (this.isDebug) {
/*  901 */         this.trace.arrayElement(list.size() - 1);
/*      */       }
/*      */ 
/*      */     }
/*  906 */     else if (obj.getClass().isArray())
/*      */     {
/*  908 */       if (!this.strictArrayIndexStack.empty())
/*      */       {
/*  910 */         int[] indexObj = (int[])(int[])this.strictArrayIndexStack.peek();
/*  911 */         int index = indexObj[0];
/*      */ 
/*  913 */         if (Array.getLength(obj) > index)
/*      */         {
/*  915 */           Array.set(obj, index, value);
/*      */         }
/*      */         else
/*      */         {
/*  919 */           throw new MessageException("Index out of bounds at: " + index + " cannot set array value: " + value + "");
/*      */         }
/*  921 */         indexObj[0] += 1;
/*      */       }
/*      */ 
/*      */     }
/*  926 */     else if ((obj instanceof Map))
/*      */     {
/*  928 */       Map map = (Map)obj;
/*      */ 
/*  931 */       if (!this.arrayPropertyStack.empty())
/*      */       {
/*  933 */         String prop = (String)this.arrayPropertyStack.peek();
/*  934 */         map.put(prop, value);
/*      */ 
/*  936 */         if (this.isDebug) {
/*  937 */           this.trace.namedElement(prop);
/*      */         }
/*  939 */         return;
/*      */       }
/*      */ 
/*  943 */       if (!this.ecmaArrayIndexStack.empty())
/*      */       {
/*  945 */         int[] index = (int[])(int[])this.ecmaArrayIndexStack.peek();
/*      */ 
/*  947 */         String prop = String.valueOf(index[0]);
/*  948 */         index[0] += 1;
/*      */ 
/*  950 */         map.put(prop, value);
/*      */ 
/*  952 */         if (this.isDebug) {
/*  953 */           this.trace.namedElement(prop);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     else {
/*  962 */       TraitsContext traitsContext = (TraitsContext)this.traitsStack.peek();
/*      */       String prop;
/*      */       try {
/*  965 */         prop = traitsContext.next();
/*      */       }
/*      */       catch (IndexOutOfBoundsException ex)
/*      */       {
/*  969 */         throw new MessageException("Object has no trait info for value: " + value, ex);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  975 */         PropertyProxy proxy = (PropertyProxy)this.proxyStack.peek();
/*  976 */         if (proxy == null)
/*  977 */           proxy = this.beanproxy;
/*  978 */         proxy.setValue(obj, prop, value);
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/*  982 */         throw new MessageException("Failed to set property '" + prop + "' with value: " + value, ex);
/*      */       }
/*      */ 
/*  986 */       if (this.isDebug)
/*  987 */         this.trace.namedElement(prop); 
/*      */     }
/*      */   }
/*      */ 
/*      */   private class TraitsContext {
/*      */     private List traits;
/*      */     private int counter;
/*      */     private final AmfxInput this$0;
/*      */ 
/*      */     private TraitsContext(List traits) {
/*  998 */       this.traits = traits;
/*      */     }
/*      */ 
/*      */     private void add(String trait)
/*      */     {
/* 1003 */       trait = trait.trim();
/*      */ 
/* 1005 */       if (trait.length() <= 0) {
/* 1006 */         throw new MessageException("Traits cannot be the empty string.");
/*      */       }
/* 1008 */       char c = trait.charAt(0);
/* 1009 */       if ((!Character.isLetterOrDigit(c)) && (c != '_')) {
/* 1010 */         throw new MessageException("Invalid trait name: " + trait + ". Object property names must start with a letter, a digit or the underscore '_' character.");
/*      */       }
/*      */ 
/* 1014 */       this.traits.add(trait);
/*      */     }
/*      */ 
/*      */     private String next()
/*      */     {
/* 1019 */       String trait = (String)this.traits.get(this.counter);
/* 1020 */       this.counter += 1;
/* 1021 */       return trait;
/*      */     }
/*      */ 
/*      */     TraitsContext(List x1, AmfxInput.1 x2)
/*      */     {
/*  991 */       this(x1);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amfx.AmfxInput
 * JD-Core Version:    0.6.0
 */