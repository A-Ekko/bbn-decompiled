/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.messaging.config.SystemSettings;
/*     */ import flex.messaging.messages.ErrorMessage;
/*     */ import flex.messaging.util.ResourceLoader;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MessageException extends LocalizedException
/*     */ {
/*     */   static final long serialVersionUID = 3310842114461162689L;
/*     */   protected String code;
/*     */   protected Map extendedData;
/*     */   protected ErrorMessage errorMessage;
/*     */ 
/*     */   public MessageException()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MessageException(ResourceLoader loader)
/*     */   {
/*  48 */     super(loader);
/*     */   }
/*     */ 
/*     */   public MessageException(String message)
/*     */   {
/*  58 */     setMessage(message);
/*     */   }
/*     */ 
/*     */   public MessageException(String message, Throwable t)
/*     */   {
/*  69 */     setMessage(message);
/*  70 */     setRootCause(t);
/*     */   }
/*     */ 
/*     */   public MessageException(Throwable t)
/*     */   {
/*  80 */     String rootMessage = t.getMessage();
/*  81 */     if (rootMessage == null)
/*  82 */       rootMessage = t.toString();
/*  83 */     setMessage(rootMessage);
/*  84 */     setRootCause(t);
/*     */   }
/*     */ 
/*     */   public String getCode()
/*     */   {
/*  94 */     return this.code;
/*     */   }
/*     */ 
/*     */   public void setCode(String code)
/*     */   {
/* 104 */     this.code = code;
/*     */   }
/*     */ 
/*     */   public Map getExtendedData()
/*     */   {
/* 114 */     return this.extendedData;
/*     */   }
/*     */ 
/*     */   public void setExtendedData(Map extendedData)
/*     */   {
/* 124 */     this.extendedData = extendedData;
/*     */   }
/*     */ 
/*     */   public ErrorMessage getErrorMessage()
/*     */   {
/* 134 */     if (this.errorMessage == null)
/*     */     {
/* 136 */       this.errorMessage = createErrorMessage();
/*     */     }
/* 138 */     return this.errorMessage;
/*     */   }
/*     */ 
/*     */   public void setErrorMessage(ErrorMessage errorMessage)
/*     */   {
/* 148 */     this.errorMessage = errorMessage;
/*     */   }
/*     */ 
/*     */   public Object getRootCauseErrorMessage()
/*     */   {
/* 155 */     if (this.rootCause != null)
/*     */     {
/* 157 */       if ((this.rootCause instanceof MessageException))
/*     */       {
/* 159 */         return ((MessageException)this.rootCause).createErrorMessage();
/*     */       }
/*     */ 
/* 163 */       return this.rootCause;
/*     */     }
/*     */ 
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   public ErrorMessage createErrorMessage()
/*     */   {
/* 177 */     ErrorMessage msg = new ErrorMessage();
/* 178 */     if (this.code == null)
/*     */     {
/* 180 */       msg.faultCode = "Server.Processing";
/*     */     }
/*     */     else
/*     */     {
/* 184 */       msg.faultCode = this.code;
/*     */     }
/* 186 */     msg.faultString = this.message;
/* 187 */     msg.faultDetail = this.details;
/* 188 */     msg.rootCause = getRootCauseErrorMessage();
/* 189 */     if (this.extendedData != null)
/*     */     {
/* 191 */       msg.extendedData = this.extendedData;
/*     */     }
/* 193 */     return msg;
/*     */   }
/*     */ 
/*     */   protected ResourceLoader getResourceLoader()
/*     */   {
/* 198 */     if (this.resourceLoader == null) {
/* 199 */       this.resourceLoader = MessageBroker.getSystemSettings().getResourceLoader();
/*     */     }
/* 201 */     return this.resourceLoader;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.MessageException
 * JD-Core Version:    0.6.0
 */