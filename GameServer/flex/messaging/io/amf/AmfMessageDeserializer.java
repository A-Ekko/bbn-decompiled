/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.io.MessageDeserializer;
/*     */ import flex.messaging.io.RecoverableSerializationException;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class AmfMessageDeserializer
/*     */   implements MessageDeserializer
/*     */ {
/*     */   private static final int UNSUPPORTED_AMF_VERSION = 10310;
/*     */   protected ActionMessageInput amfIn;
/*     */   protected AmfTrace debugTrace;
/*     */   protected boolean isDebug;
/*     */ 
/*     */   public void initialize(SerializationContext context, InputStream in, AmfTrace trace)
/*     */   {
/*  68 */     this.amfIn = new Amf0Input(context);
/*  69 */     this.amfIn.setInputStream(in);
/*     */ 
/*  71 */     this.debugTrace = trace;
/*  72 */     this.isDebug = (this.debugTrace != null);
/*  73 */     this.amfIn.setDebugTrace(this.debugTrace);
/*     */   }
/*     */ 
/*     */   public void readMessage(ActionMessage m, ActionContext context)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/*  84 */     if (this.isDebug) {
/*  85 */       this.debugTrace.startRequest("Deserializing AMF/HTTP request");
/*     */     }
/*     */ 
/*  88 */     int version = this.amfIn.readUnsignedShort();
/*     */ 
/*  90 */     if ((version != 0) && (version != 3))
/*     */     {
/*  93 */       MessageException ex = new MessageException();
/*  94 */       ex.setMessage(10310, new Object[] { new Integer(version) });
/*  95 */       ex.setCode("VersionMismatch");
/*  96 */       throw ex;
/*     */     }
/*     */ 
/*  99 */     m.setVersion(version);
/* 100 */     context.setVersion(version);
/*     */ 
/* 102 */     if (this.isDebug) {
/* 103 */       this.debugTrace.version(version);
/*     */     }
/*     */ 
/* 106 */     int headerCount = this.amfIn.readUnsignedShort();
/* 107 */     for (int i = 0; i < headerCount; i++)
/*     */     {
/* 109 */       MessageHeader header = new MessageHeader();
/* 110 */       m.addHeader(header);
/* 111 */       readHeader(header, i);
/*     */     }
/*     */ 
/* 115 */     int bodyCount = this.amfIn.readUnsignedShort();
/* 116 */     for (int i = 0; i < bodyCount; i++)
/*     */     {
/* 118 */       MessageBody body = new MessageBody();
/* 119 */       m.addBody(body);
/* 120 */       readBody(body, i);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readHeader(MessageHeader header, int index)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 138 */     String name = this.amfIn.readUTF();
/* 139 */     header.setName(name);
/* 140 */     boolean mustUnderstand = this.amfIn.readBoolean();
/* 141 */     header.setMustUnderstand(mustUnderstand);
/*     */ 
/* 143 */     this.amfIn.readInt();
/*     */ 
/* 145 */     this.amfIn.reset();
/* 146 */     Object data = null;
/*     */ 
/* 148 */     if (this.isDebug) {
/* 149 */       this.debugTrace.startHeader(name, mustUnderstand, index);
/*     */     }
/*     */     try
/*     */     {
/* 153 */       data = readObject();
/*     */     }
/*     */     catch (RecoverableSerializationException ex)
/*     */     {
/* 157 */       ex.setCode("Client.Header.Encoding");
/* 158 */       data = ex;
/*     */     }
/*     */     catch (MessageException ex)
/*     */     {
/* 162 */       ex.setCode("Client.Header.Encoding");
/* 163 */       throw ex;
/*     */     }
/*     */ 
/* 166 */     header.setData(data);
/*     */ 
/* 168 */     if (this.isDebug)
/* 169 */       this.debugTrace.endHeader();
/*     */   }
/*     */ 
/*     */   public void readBody(MessageBody body, int index)
/*     */     throws ClassNotFoundException, IOException
/*     */   {
/* 181 */     String targetURI = this.amfIn.readUTF();
/* 182 */     body.setTargetURI(targetURI);
/* 183 */     String responseURI = this.amfIn.readUTF();
/* 184 */     body.setResponseURI(responseURI);
/*     */ 
/* 186 */     this.amfIn.readInt();
/*     */ 
/* 188 */     this.amfIn.reset();
/* 189 */     Object data = null;
/*     */ 
/* 191 */     if (this.isDebug) {
/* 192 */       this.debugTrace.startMessage(targetURI, responseURI, index);
/*     */     }
/*     */     try
/*     */     {
/* 196 */       data = readObject();
/*     */     }
/*     */     catch (RecoverableSerializationException ex)
/*     */     {
/* 200 */       ex.setCode("Client.Message.Encoding");
/* 201 */       data = ex;
/*     */     }
/*     */     catch (MessageException ex)
/*     */     {
/* 205 */       ex.setCode("Client.Message.Encoding");
/* 206 */       throw ex;
/*     */     }
/*     */ 
/* 209 */     body.setData(data);
/*     */ 
/* 211 */     if (this.isDebug)
/* 212 */       this.debugTrace.endMessage();
/*     */   }
/*     */ 
/*     */   public Object readObject() throws ClassNotFoundException, IOException
/*     */   {
/* 217 */     return this.amfIn.readObject();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.AmfMessageDeserializer
 * JD-Core Version:    0.6.0
 */