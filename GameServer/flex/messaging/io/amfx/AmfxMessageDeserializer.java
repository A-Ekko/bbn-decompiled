/*     */ package flex.messaging.io.amfx;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.io.MessageDeserializer;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.amf.ActionContext;
/*     */ import flex.messaging.io.amf.ActionMessage;
/*     */ import flex.messaging.io.amf.AmfTrace;
/*     */ import flex.messaging.io.amf.MessageBody;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.Locator;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class AmfxMessageDeserializer extends DefaultHandler
/*     */   implements MessageDeserializer
/*     */ {
/*     */   protected InputStream in;
/*     */   protected Locator locator;
/*     */   protected AmfxInput amfxIn;
/*     */   protected AmfTrace debugTrace;
/*     */   protected boolean isDebug;
/* 259 */   private static Class[] attribArr = { Attributes.class };
/*     */ 
/*     */   public void initialize(SerializationContext context, InputStream in, AmfTrace trace)
/*     */   {
/*  71 */     this.amfxIn = new AmfxInput(context);
/*  72 */     this.in = in;
/*     */ 
/*  74 */     this.debugTrace = trace;
/*  75 */     this.isDebug = (this.debugTrace != null);
/*     */ 
/*  77 */     if (this.debugTrace != null)
/*  78 */       this.amfxIn.setDebugTrace(this.debugTrace);
/*     */   }
/*     */ 
/*     */   public void setSerializationContext(SerializationContext context)
/*     */   {
/*  83 */     this.amfxIn = new AmfxInput(context);
/*     */   }
/*     */ 
/*     */   public void readMessage(ActionMessage m, ActionContext context) throws IOException
/*     */   {
/*  88 */     if (this.isDebug) {
/*  89 */       this.debugTrace.startRequest("Deserializing AMFX/HTTP request");
/*     */     }
/*  91 */     this.amfxIn.reset();
/*  92 */     this.amfxIn.setDebugTrace(this.debugTrace);
/*  93 */     this.amfxIn.setActionMessage(m);
/*     */ 
/*  95 */     parse(m);
/*     */ 
/*  97 */     context.setVersion(m.getVersion());
/*     */   }
/*     */ 
/*     */   public Object readObject() throws ClassNotFoundException, IOException
/*     */   {
/* 102 */     return this.amfxIn.readObject();
/*     */   }
/*     */ 
/*     */   protected void parse(ActionMessage m)
/*     */   {
/*     */     try
/*     */     {
/* 109 */       SAXParserFactory factory = SAXParserFactory.newInstance();
/* 110 */       factory.setValidating(false);
/* 111 */       factory.setNamespaceAware(true);
/* 112 */       SAXParser parser = factory.newSAXParser();
/* 113 */       parser.parse(this.in, this);
/*     */     }
/*     */     catch (MessageException ex)
/*     */     {
/* 117 */       clientMessageEncodingException(m, ex);
/*     */     }
/*     */     catch (SAXParseException e)
/*     */     {
/* 121 */       if (e.getException() != null)
/*     */       {
/* 123 */         clientMessageEncodingException(m, e.getException());
/*     */       }
/*     */       else
/*     */       {
/* 127 */         clientMessageEncodingException(m, e);
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 132 */       clientMessageEncodingException(m, ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void clientMessageEncodingException(ActionMessage m, Throwable t)
/*     */   {
/*     */     MessageException me;
/*     */     MessageException me;
/* 139 */     if ((t instanceof MessageException))
/*     */     {
/* 141 */       me = (MessageException)t;
/*     */     }
/*     */     else
/*     */     {
/* 145 */       me = new MessageException("Error occurred parsing AMFX: " + t.getMessage());
/*     */     }
/*     */ 
/* 148 */     me.setCode("Client.Message.Encoding");
/* 149 */     MessageBody body = new MessageBody();
/* 150 */     body.setData(me);
/* 151 */     m.addBody(body);
/*     */   }
/*     */ 
/*     */   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 158 */       String methodName = "start_" + localName;
/* 159 */       Method method = this.amfxIn.getClass().getMethod(methodName, attribArr);
/* 160 */       method.invoke(this.amfxIn, new Object[] { attributes });
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 164 */       fatalError(new SAXParseException("Unknown type: " + qName, this.locator));
/*     */     }
/*     */     catch (IllegalAccessException e)
/*     */     {
/* 168 */       fatalError(new SAXParseException(e.getMessage(), this.locator, e));
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 172 */       Throwable t = e.getTargetException();
/* 173 */       if ((t instanceof SAXException))
/*     */       {
/* 175 */         throw ((SAXException)t);
/*     */       }
/* 177 */       if ((t instanceof Exception))
/*     */       {
/* 179 */         fatalError(new SAXParseException(t.getMessage(), this.locator, (Exception)t));
/*     */       }
/*     */       else
/*     */       {
/* 183 */         fatalError(new SAXParseException(e.getMessage(), this.locator, e));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endElement(String uri, String localName, String qName) throws SAXException
/*     */   {
/*     */     try
/*     */     {
/* 192 */       String methodName = "end_" + localName;
/* 193 */       Method method = this.amfxIn.getClass().getMethod(methodName, new Class[0]);
/* 194 */       method.invoke(this.amfxIn, new Object[0]);
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 198 */       fatalError(new SAXParseException("Unfinished type: " + qName, this.locator));
/*     */     }
/*     */     catch (IllegalAccessException e)
/*     */     {
/* 202 */       fatalError(new SAXParseException(e.getMessage(), this.locator, e));
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 206 */       Throwable t = e.getTargetException();
/* 207 */       if ((t instanceof SAXException))
/*     */       {
/* 209 */         throw ((SAXException)t);
/*     */       }
/* 211 */       if ((t instanceof Error))
/*     */       {
/* 213 */         throw ((Error)t);
/*     */       }
/*     */ 
/* 217 */       fatalError(new SAXParseException(t.getMessage(), this.locator));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void characters(char[] ch, int start, int length)
/*     */     throws SAXException
/*     */   {
/* 224 */     String chars = new String(ch, start, length);
/* 225 */     if (chars.length() > 0)
/*     */     {
/* 227 */       this.amfxIn.text(chars);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDocumentLocator(Locator l)
/*     */   {
/* 233 */     this.locator = l;
/*     */   }
/*     */ 
/*     */   public void error(SAXParseException exception)
/*     */     throws SAXException
/*     */   {
/* 239 */     throw new MessageException(exception.getMessage());
/*     */   }
/*     */ 
/*     */   public void fatalError(SAXParseException exception) throws SAXException
/*     */   {
/* 244 */     if ((exception.getException() != null) && ((exception.getException() instanceof MessageException)))
/*     */     {
/* 246 */       throw ((MessageException)exception.getException());
/*     */     }
/*     */ 
/* 250 */     throw new MessageException(exception.getMessage());
/*     */   }
/*     */ 
/*     */   public void warning(SAXParseException exception)
/*     */     throws SAXException
/*     */   {
/* 256 */     throw new MessageException(exception.getMessage());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amfx.AmfxMessageDeserializer
 * JD-Core Version:    0.6.0
 */