/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.io.MessageSerializer;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class AmfMessageSerializer
/*     */   implements MessageSerializer
/*     */ {
/*     */   public static final int UNKNOWN_CONTENT_LENGTH = -1;
/*     */   protected Amf0Output amfOut;
/*     */   protected boolean isDebug;
/*     */   protected AmfTrace debugTrace;
/*     */   protected int version;
/*     */ 
/*     */   public void setVersion(int value)
/*     */   {
/*  69 */     this.version = value;
/*     */   }
/*     */ 
/*     */   public void initialize(SerializationContext context, OutputStream out, AmfTrace trace)
/*     */   {
/*  86 */     this.amfOut = new Amf0Output(context);
/*  87 */     this.amfOut.setOutputStream(out);
/*  88 */     this.amfOut.setAvmPlus(this.version > 0);
/*     */ 
/*  90 */     this.debugTrace = trace;
/*  91 */     this.isDebug = (trace != null);
/*  92 */     this.amfOut.setDebugTrace(this.debugTrace);
/*     */   }
/*     */ 
/*     */   public void writeMessage(ActionMessage m)
/*     */     throws IOException
/*     */   {
/* 103 */     if (this.isDebug) {
/* 104 */       this.debugTrace.startResponse("Serializing AMF/HTTP response");
/*     */     }
/* 106 */     int version = m.getVersion();
/*     */ 
/* 108 */     this.amfOut.setAvmPlus(version > 0);
/*     */ 
/* 111 */     this.amfOut.writeShort(version);
/*     */ 
/* 113 */     if (this.isDebug) {
/* 114 */       this.debugTrace.version(version);
/*     */     }
/*     */ 
/* 117 */     int headerCount = m.getHeaderCount();
/* 118 */     this.amfOut.writeShort(headerCount);
/* 119 */     for (int i = 0; i < headerCount; i++)
/*     */     {
/* 121 */       MessageHeader header = m.getHeader(i);
/*     */ 
/* 123 */       if (this.isDebug) {
/* 124 */         this.debugTrace.startHeader(header.getName(), header.getMustUnderstand(), i);
/*     */       }
/* 126 */       writeHeader(header);
/*     */ 
/* 128 */       if (this.isDebug) {
/* 129 */         this.debugTrace.endHeader();
/*     */       }
/*     */     }
/*     */ 
/* 133 */     int bodyCount = m.getBodyCount();
/* 134 */     this.amfOut.writeShort(bodyCount);
/* 135 */     for (int i = 0; i < bodyCount; i++)
/*     */     {
/* 137 */       MessageBody body = m.getBody(i);
/*     */ 
/* 139 */       if (this.isDebug) {
/* 140 */         this.debugTrace.startMessage(body.getTargetURI(), body.getResponseURI(), i);
/*     */       }
/* 142 */       writeBody(body);
/*     */ 
/* 144 */       if (this.isDebug)
/* 145 */         this.debugTrace.endMessage();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeHeader(MessageHeader h)
/*     */     throws IOException
/*     */   {
/* 157 */     this.amfOut.writeUTF(h.getName());
/* 158 */     this.amfOut.writeBoolean(h.getMustUnderstand());
/* 159 */     this.amfOut.writeInt(-1);
/* 160 */     this.amfOut.reset();
/* 161 */     writeObject(h.getData());
/*     */   }
/*     */ 
/*     */   public void writeBody(MessageBody b)
/*     */     throws IOException
/*     */   {
/* 172 */     if (b.getTargetURI() == null)
/* 173 */       this.amfOut.writeUTF("null");
/*     */     else {
/* 175 */       this.amfOut.writeUTF(b.getTargetURI());
/*     */     }
/* 177 */     if (b.getResponseURI() == null)
/* 178 */       this.amfOut.writeUTF("null");
/*     */     else {
/* 180 */       this.amfOut.writeUTF(b.getResponseURI());
/*     */     }
/* 182 */     this.amfOut.writeInt(-1);
/* 183 */     this.amfOut.reset();
/*     */ 
/* 185 */     Object data = b.getData();
/* 186 */     writeObject(data);
/*     */   }
/*     */ 
/*     */   public void writeObject(Object value)
/*     */     throws IOException
/*     */   {
/* 196 */     this.amfOut.writeObject(value);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.AmfMessageSerializer
 * JD-Core Version:    0.6.0
 */