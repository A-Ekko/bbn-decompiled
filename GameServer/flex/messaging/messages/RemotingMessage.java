/*     */ package flex.messaging.messages;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RemotingMessage extends RPCMessage
/*     */ {
/*     */   private static final long serialVersionUID = 1491092800943415719L;
/*     */   private String source;
/*     */   private String operation;
/*     */   private Object[] parameters;
/*     */   private transient List parameterList;
/*     */ 
/*     */   public String getSource()
/*     */   {
/*  55 */     return this.source;
/*     */   }
/*     */ 
/*     */   public void setSource(String s)
/*     */   {
/*  60 */     this.source = s;
/*     */   }
/*     */ 
/*     */   public Object getBody()
/*     */   {
/*  65 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public void setBody(Object bodyValue)
/*     */   {
/*  70 */     if ((bodyValue instanceof List))
/*     */     {
/*  76 */       if (this.parameterList != null)
/*     */       {
/*  78 */         this.parameterList.addAll((List)bodyValue);
/*     */       }
/*     */       else
/*     */       {
/*  82 */         this.parameterList = ((List)bodyValue);
/*     */       }
/*     */     }
/*  85 */     else if (!bodyValue.getClass().isArray())
/*     */     {
/*  87 */       this.parameters = new Object[] { bodyValue };
/*     */     }
/*     */     else
/*     */     {
/*  91 */       this.parameters = ((Object[])(Object[])bodyValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getOperation()
/*     */   {
/*  97 */     return this.operation;
/*     */   }
/*     */ 
/*     */   public void setOperation(String operation)
/*     */   {
/* 102 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   public List getParameters()
/*     */   {
/* 107 */     if ((this.parameters == null) && (this.parameterList != null))
/*     */     {
/* 109 */       this.parameters = this.parameterList.toArray();
/*     */ 
/* 111 */       this.parameterList = null;
/*     */     }
/* 113 */     return this.parameters == null ? null : Arrays.asList(this.parameters);
/*     */   }
/*     */ 
/*     */   public void setParameters(List params)
/*     */   {
/* 118 */     this.parameters = params.toArray();
/*     */   }
/*     */ 
/*     */   protected String toStringFields(int indentLevel)
/*     */   {
/* 123 */     String s = getOperation();
/* 124 */     String sp = super.toStringFields(indentLevel);
/* 125 */     String sep = getFieldSeparator(indentLevel);
/* 126 */     return sep + "operation = " + s + sp;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.RemotingMessage
 * JD-Core Version:    0.6.0
 */