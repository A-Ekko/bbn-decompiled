/*     */ package flex.messaging.messages;
/*     */ 
/*     */ import flex.messaging.util.StringUtils;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class HTTPMessage extends RPCMessage
/*     */ {
/*     */   private static final long serialVersionUID = 5954910346466323369L;
/*     */   protected String contentType;
/*     */   protected String method;
/*     */   protected String url;
/*     */   protected Map httpHeaders;
/*     */   protected boolean recordHeaders;
/*     */ 
/*     */   public String getContentType()
/*     */   {
/*  57 */     return this.contentType;
/*     */   }
/*     */ 
/*     */   public void setContentType(String type)
/*     */   {
/*  62 */     this.contentType = type;
/*     */   }
/*     */ 
/*     */   public String getMethod()
/*     */   {
/*  67 */     return this.method;
/*     */   }
/*     */ 
/*     */   public void setMethod(String m)
/*     */   {
/*  72 */     if (m != null)
/*     */     {
/*  74 */       this.method = m.trim().toUpperCase();
/*     */     }
/*     */     else
/*     */     {
/*  78 */       this.method = m;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map getHttpHeaders()
/*     */   {
/*  84 */     return this.httpHeaders;
/*     */   }
/*     */ 
/*     */   public void setHttpHeaders(Map h)
/*     */   {
/*  89 */     this.httpHeaders = h;
/*     */   }
/*     */ 
/*     */   public void setUrl(String s)
/*     */   {
/*  94 */     this.url = s;
/*     */   }
/*     */ 
/*     */   public String getUrl()
/*     */   {
/*  99 */     return this.url;
/*     */   }
/*     */ 
/*     */   public boolean getRecordHeaders()
/*     */   {
/* 104 */     return this.recordHeaders;
/*     */   }
/*     */ 
/*     */   public void setRecordHeaders(boolean recordHeaders)
/*     */   {
/* 109 */     this.recordHeaders = recordHeaders;
/*     */   }
/*     */ 
/*     */   protected String toStringFields(int indentLevel)
/*     */   {
/* 114 */     String sep = getFieldSeparator(indentLevel);
/* 115 */     String s = sep + "method = " + getMethod() + sep + "url = " + getUrl() + sep + "headers = " + getHeaders();
/*     */ 
/* 118 */     return s += super.toStringFields(indentLevel);
/*     */   }
/*     */ 
/*     */   protected String internalBodyToString(Object body, int indentLevel)
/*     */   {
/* 123 */     return (body instanceof String) ? StringUtils.prettifyString((String)body) : super.internalBodyToString(body, indentLevel);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.HTTPMessage
 * JD-Core Version:    0.6.0
 */