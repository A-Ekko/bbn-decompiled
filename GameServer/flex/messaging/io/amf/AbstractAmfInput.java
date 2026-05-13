/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.io.BeanProxy;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.util.XMLUtil;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public abstract class AbstractAmfInput extends AmfIO
/*     */   implements ActionMessageInput
/*     */ {
/*  38 */   protected BeanProxy beanProxy = new BeanProxy();
/*     */ 
/*  40 */   protected DataInputStream in = null;
/*     */ 
/*     */   public AbstractAmfInput(SerializationContext context)
/*     */   {
/*  47 */     super(context);
/*     */   }
/*     */ 
/*     */   public void setInputStream(InputStream in)
/*     */   {
/*  52 */     this.in = new DataInputStream(in);
/*     */   }
/*     */ 
/*     */   protected Object stringToDocument(String xml)
/*     */   {
/*  58 */     if ((xml != null) && (xml.indexOf('<') == -1)) {
/*  59 */       return xml;
/*     */     }
/*  61 */     return XMLUtil.stringToDocument(xml, !this.context.legacyXMLNamespaces);
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/*  70 */     return this.in.available();
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  75 */     this.in.close();
/*     */   }
/*     */ 
/*     */   public int read() throws IOException
/*     */   {
/*  80 */     return this.in.read();
/*     */   }
/*     */ 
/*     */   public int read(byte[] bytes) throws IOException
/*     */   {
/*  85 */     return this.in.read(bytes);
/*     */   }
/*     */ 
/*     */   public int read(byte[] bytes, int offset, int length) throws IOException
/*     */   {
/*  90 */     return this.in.read(bytes, offset, length);
/*     */   }
/*     */ 
/*     */   public long skip(long n) throws IOException
/*     */   {
/*  95 */     return this.in.skip(n);
/*     */   }
/*     */ 
/*     */   public int skipBytes(int n) throws IOException
/*     */   {
/* 100 */     return this.in.skipBytes(n);
/*     */   }
/*     */ 
/*     */   public boolean readBoolean()
/*     */     throws IOException
/*     */   {
/* 109 */     return this.in.readBoolean();
/*     */   }
/*     */ 
/*     */   public byte readByte() throws IOException
/*     */   {
/* 114 */     return this.in.readByte();
/*     */   }
/*     */ 
/*     */   public char readChar() throws IOException
/*     */   {
/* 119 */     return this.in.readChar();
/*     */   }
/*     */ 
/*     */   public double readDouble() throws IOException
/*     */   {
/* 124 */     return this.in.readDouble();
/*     */   }
/*     */ 
/*     */   public float readFloat() throws IOException
/*     */   {
/* 129 */     return this.in.readFloat();
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] bytes) throws IOException
/*     */   {
/* 134 */     this.in.readFully(bytes);
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] bytes, int offset, int length) throws IOException
/*     */   {
/* 139 */     this.in.readFully(bytes, offset, length);
/*     */   }
/*     */ 
/*     */   public int readInt() throws IOException
/*     */   {
/* 144 */     return this.in.readInt();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public String readLine()
/*     */     throws IOException
/*     */   {
/* 152 */     return this.in.readLine();
/*     */   }
/*     */ 
/*     */   public long readLong() throws IOException
/*     */   {
/* 157 */     return this.in.readLong();
/*     */   }
/*     */ 
/*     */   public short readShort() throws IOException
/*     */   {
/* 162 */     return this.in.readShort();
/*     */   }
/*     */ 
/*     */   public int readUnsignedByte() throws IOException
/*     */   {
/* 167 */     return this.in.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   public int readUnsignedShort() throws IOException
/*     */   {
/* 172 */     return this.in.readUnsignedShort();
/*     */   }
/*     */ 
/*     */   public String readUTF() throws IOException
/*     */   {
/* 177 */     return this.in.readUTF();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.AbstractAmfInput
 * JD-Core Version:    0.6.0
 */