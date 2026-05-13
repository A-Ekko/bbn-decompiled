/*     */ package flex.messaging.io.amfx;
/*     */ 
/*     */ import flex.messaging.io.MessageSerializer;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.amf.ActionMessage;
/*     */ import flex.messaging.io.amf.AmfTrace;
/*     */ import flex.messaging.io.amf.MessageBody;
/*     */ import flex.messaging.io.amf.MessageHeader;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class AmfxMessageSerializer
/*     */   implements MessageSerializer, AmfxTypes
/*     */ {
/*     */   protected AmfxOutput amfxOut;
/*     */   protected int version;
/*     */   protected boolean isDebug;
/*     */   protected AmfTrace debugTrace;
/*     */   public static final String XML_DIRECTIVE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
/*     */ 
/*     */   public void setVersion(int value)
/*     */   {
/*  56 */     this.version = value;
/*     */   }
/*     */ 
/*     */   public void initialize(SerializationContext context, OutputStream out, AmfTrace trace)
/*     */   {
/*  70 */     this.amfxOut = new AmfxOutput(context);
/*  71 */     this.amfxOut.setOutputStream(out);
/*  72 */     this.debugTrace = trace;
/*  73 */     this.isDebug = (this.debugTrace != null);
/*  74 */     this.amfxOut.setDebugTrace(trace);
/*     */   }
/*     */ 
/*     */   public void writeMessage(ActionMessage m)
/*     */     throws IOException
/*     */   {
/*  80 */     if (this.isDebug) {
/*  81 */       this.debugTrace.startResponse("Serializing AMFX/HTTP response");
/*     */     }
/*  83 */     this.amfxOut.writeUTF("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
/*     */ 
/*  85 */     int version = m.getVersion();
/*  86 */     writeOpenAMFX(version);
/*     */ 
/*  88 */     if (this.isDebug) {
/*  89 */       this.debugTrace.version(version);
/*     */     }
/*     */ 
/*  92 */     int headerCount = m.getHeaderCount();
/*  93 */     for (int i = 0; i < headerCount; i++)
/*     */     {
/*  95 */       MessageHeader header = m.getHeader(i);
/*     */ 
/*  97 */       if (this.isDebug) {
/*  98 */         this.debugTrace.startHeader(header.getName(), header.getMustUnderstand(), i);
/*     */       }
/* 100 */       writeHeader(header);
/*     */ 
/* 102 */       if (this.isDebug) {
/* 103 */         this.debugTrace.endHeader();
/*     */       }
/*     */     }
/*     */ 
/* 107 */     int bodyCount = m.getBodyCount();
/* 108 */     for (int i = 0; i < bodyCount; i++)
/*     */     {
/* 110 */       MessageBody body = m.getBody(i);
/*     */ 
/* 112 */       if (this.isDebug) {
/* 113 */         this.debugTrace.startMessage(body.getTargetURI(), body.getResponseURI(), i);
/*     */       }
/* 115 */       writeBody(body);
/*     */ 
/* 117 */       if (this.isDebug) {
/* 118 */         this.debugTrace.endMessage();
/*     */       }
/*     */     }
/* 121 */     writeCloseAMFX();
/*     */ 
/* 123 */     if (this.isDebug)
/* 124 */       this.debugTrace.endMessage();
/*     */   }
/*     */ 
/*     */   protected void writeOpenAMFX(int version) throws IOException
/*     */   {
/* 129 */     int buflen = 14;
/* 130 */     StringBuffer sb = new StringBuffer(buflen);
/* 131 */     sb.append("<").append("amfx").append(" ver=\"");
/* 132 */     sb.append(version);
/* 133 */     sb.append("\">");
/*     */ 
/* 135 */     this.amfxOut.writeUTF(sb);
/*     */   }
/*     */ 
/*     */   protected void writeCloseAMFX() throws IOException
/*     */   {
/* 140 */     this.amfxOut.writeUTF("</amfx>");
/*     */   }
/*     */ 
/*     */   protected void writeHeader(MessageHeader h) throws IOException
/*     */   {
/* 145 */     int buflen = 127;
/* 146 */     StringBuffer sb = new StringBuffer(buflen);
/* 147 */     sb.append("<").append("header").append(" name=\"");
/* 148 */     sb.append(h.getName());
/* 149 */     sb.append("\"");
/*     */ 
/* 151 */     if (h.getMustUnderstand())
/*     */     {
/* 153 */       sb.append(" mustUnderstand=\"");
/* 154 */       sb.append(h.getMustUnderstand());
/* 155 */       sb.append("\"");
/*     */     }
/*     */ 
/* 158 */     sb.append(">");
/*     */ 
/* 160 */     this.amfxOut.writeUTF(sb);
/*     */ 
/* 162 */     writeObject(h.getData());
/*     */ 
/* 164 */     this.amfxOut.writeUTF("</header>");
/*     */   }
/*     */ 
/*     */   protected void writeBody(MessageBody b) throws IOException
/*     */   {
/* 169 */     if ((b.getTargetURI() == null) && (b.getResponseURI() == null))
/*     */     {
/* 171 */       this.amfxOut.writeUTF("<body>");
/*     */     }
/*     */     else
/*     */     {
/* 175 */       int buflen = 127;
/* 176 */       StringBuffer sb = new StringBuffer(buflen);
/* 177 */       sb.append("<").append("body");
/*     */ 
/* 179 */       if (b.getTargetURI() != null) {
/* 180 */         sb.append(" targetURI=\"").append(b.getTargetURI()).append("\"");
/*     */       }
/* 182 */       if (b.getResponseURI() != null) {
/* 183 */         sb.append(" responseURI=\"").append(b.getResponseURI()).append("\"");
/*     */       }
/* 185 */       sb.append(">");
/* 186 */       this.amfxOut.writeUTF(sb);
/*     */     }
/*     */ 
/* 189 */     Object data = b.getData();
/* 190 */     writeObject(data);
/*     */ 
/* 192 */     this.amfxOut.writeUTF("</body>");
/*     */   }
/*     */ 
/*     */   public void writeObject(Object value) throws IOException
/*     */   {
/* 197 */     this.amfxOut.writeObject(value);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amfx.AmfxMessageSerializer
 * JD-Core Version:    0.6.0
 */