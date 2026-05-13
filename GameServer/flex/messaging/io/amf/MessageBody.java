/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class MessageBody
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 3874002169129668459L;
/*  49 */   private String targetURI = "";
/*     */ 
/*  65 */   private String responseURI = "";
/*     */   protected Object data;
/*     */ 
/*     */   public MessageBody()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MessageBody(String targetURI, String responseURI, Object data)
/*     */   {
/*  93 */     setTargetURI(targetURI);
/*  94 */     setResponseURI(responseURI);
/*  95 */     this.data = data;
/*     */   }
/*     */ 
/*     */   public String getTargetURI()
/*     */   {
/* 101 */     return this.targetURI;
/*     */   }
/*     */ 
/*     */   public void setTargetURI(String uri)
/*     */   {
/* 106 */     if (uri == null) {
/* 107 */       uri = "";
/*     */     }
/* 109 */     this.targetURI = uri;
/*     */   }
/*     */ 
/*     */   public void setReplyMethod(String methodName)
/*     */   {
/* 114 */     if ((this.targetURI.endsWith("/onStatus")) || (this.targetURI.endsWith("/onResult")))
/*     */     {
/* 116 */       this.targetURI = this.targetURI.substring(0, this.targetURI.lastIndexOf("/"));
/*     */     }
/* 118 */     this.targetURI += methodName;
/*     */   }
/*     */ 
/*     */   public String getReplyMethod()
/*     */   {
/* 123 */     return this.targetURI.substring(this.targetURI.lastIndexOf("/") + 1, this.targetURI.length());
/*     */   }
/*     */ 
/*     */   public String getResponseURI()
/*     */   {
/* 129 */     return this.responseURI;
/*     */   }
/*     */ 
/*     */   public void setResponseURI(String uri)
/*     */   {
/* 134 */     if (uri == null) {
/* 135 */       uri = "";
/*     */     }
/* 137 */     this.responseURI = uri;
/*     */   }
/*     */ 
/*     */   public Object getData()
/*     */   {
/* 142 */     return this.data;
/*     */   }
/*     */ 
/*     */   public void setData(Object data)
/*     */   {
/* 147 */     this.data = data;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.MessageBody
 * JD-Core Version:    0.6.0
 */