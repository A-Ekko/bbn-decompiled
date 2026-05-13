/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.util.XMLUtil;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public abstract class AbstractAmfOutput extends AmfIO
/*     */   implements ActionMessageOutput
/*     */ {
/*     */   protected DataOutputStream out;
/*     */ 
/*     */   public AbstractAmfOutput(SerializationContext context)
/*     */   {
/*  42 */     super(context);
/*     */   }
/*     */ 
/*     */   public void setOutputStream(OutputStream out)
/*     */   {
/*  52 */     if ((out instanceof DataOutputStream))
/*     */     {
/*  54 */       this.out = ((DataOutputStream)out);
/*     */     }
/*     */     else
/*     */     {
/*  58 */       this.out = new DataOutputStream(out);
/*     */     }
/*  60 */     reset();
/*     */   }
/*     */ 
/*     */   protected String documentToString(Object value) throws IOException
/*     */   {
/*  65 */     return XMLUtil.documentToString((Document)value);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  74 */     this.out.close();
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException
/*     */   {
/*  79 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   public void write(int b) throws IOException
/*     */   {
/*  84 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */   public void write(byte[] bytes) throws IOException
/*     */   {
/*  89 */     this.out.write(bytes);
/*     */   }
/*     */ 
/*     */   public void write(byte[] bytes, int offset, int length) throws IOException
/*     */   {
/*  94 */     this.out.write(bytes, offset, length);
/*     */   }
/*     */ 
/*     */   public void writeBoolean(boolean v)
/*     */     throws IOException
/*     */   {
/* 104 */     this.out.writeBoolean(v);
/*     */   }
/*     */ 
/*     */   public void writeByte(int v) throws IOException
/*     */   {
/* 109 */     this.out.writeByte(v);
/*     */   }
/*     */ 
/*     */   public void writeBytes(String s) throws IOException
/*     */   {
/* 114 */     this.out.writeBytes(s);
/*     */   }
/*     */ 
/*     */   public void writeChar(int v) throws IOException
/*     */   {
/* 119 */     this.out.writeChar(v);
/*     */   }
/*     */ 
/*     */   public void writeChars(String s) throws IOException
/*     */   {
/* 124 */     this.out.writeChars(s);
/*     */   }
/*     */ 
/*     */   public void writeDouble(double v) throws IOException
/*     */   {
/* 129 */     this.out.writeDouble(v);
/*     */   }
/*     */ 
/*     */   public void writeFloat(float v) throws IOException
/*     */   {
/* 134 */     this.out.writeFloat(v);
/*     */   }
/*     */ 
/*     */   public void writeInt(int v) throws IOException
/*     */   {
/* 139 */     this.out.writeInt(v);
/*     */   }
/*     */ 
/*     */   public void writeLong(long v) throws IOException
/*     */   {
/* 144 */     this.out.writeLong(v);
/*     */   }
/*     */ 
/*     */   public void writeShort(int v) throws IOException
/*     */   {
/* 149 */     this.out.writeShort(v);
/*     */   }
/*     */ 
/*     */   public void writeUTF(String s) throws IOException
/*     */   {
/* 154 */     this.out.writeUTF(s);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.AbstractAmfOutput
 * JD-Core Version:    0.6.0
 */