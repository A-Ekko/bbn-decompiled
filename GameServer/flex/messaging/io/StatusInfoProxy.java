/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletException;
/*     */ 
/*     */ public class StatusInfoProxy extends AbstractProxy
/*     */ {
/*     */   static final long serialVersionUID = 8860353096401173320L;
/*     */   public static final String DESCRIPTION = "description";
/*     */   public static final String DETAILS = "details";
/*     */   public static final String CLASS = "type";
/*     */   public static final String CODE = "code";
/*     */   public static final String ROOTCAUSE = "rootcause";
/*  91 */   public static final List propertyNameCache = new ArrayList();
/*     */   protected boolean showStacktraces;
/*     */ 
/*     */   public StatusInfoProxy()
/*     */   {
/* 105 */     super(null);
/*     */   }
/*     */ 
/*     */   public StatusInfoProxy(Throwable defaultInstance)
/*     */   {
/* 110 */     super(defaultInstance);
/*     */   }
/*     */ 
/*     */   public void setShowStacktraces(boolean value)
/*     */   {
/* 115 */     this.showStacktraces = value;
/*     */   }
/*     */ 
/*     */   public String getAlias(Object instance)
/*     */   {
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   public List getPropertyNames(Object instance)
/*     */   {
/* 126 */     return propertyNameCache;
/*     */   }
/*     */ 
/*     */   public Class getType(Object instance, String propertyName)
/*     */   {
/* 131 */     Class type = null;
/*     */ 
/* 133 */     if ("code".equals(propertyName))
/*     */     {
/* 135 */       type = String.class;
/*     */     }
/* 137 */     else if ("type".equals(propertyName))
/*     */     {
/* 139 */       type = String.class;
/*     */     }
/* 141 */     else if ("description".equals(propertyName))
/*     */     {
/* 143 */       type = String.class;
/*     */     }
/* 145 */     else if ("details".equals(propertyName))
/*     */     {
/* 147 */       type = String.class;
/*     */     }
/* 149 */     else if ("rootcause".equals(propertyName))
/*     */     {
/* 151 */       type = Map.class;
/*     */     }
/*     */ 
/* 154 */     return type;
/*     */   }
/*     */ 
/*     */   public Object getValue(Object instance, String propertyName)
/*     */   {
/* 159 */     Object value = null;
/*     */ 
/* 161 */     if ("code".equals(propertyName))
/*     */     {
/* 163 */       value = getCode(instance);
/*     */     }
/* 165 */     else if ("type".equals(propertyName))
/*     */     {
/* 167 */       value = getType(instance);
/*     */     }
/* 169 */     else if ("description".equals(propertyName))
/*     */     {
/* 171 */       value = getDescription(instance);
/*     */     }
/* 173 */     else if ("details".equals(propertyName))
/*     */     {
/* 175 */       value = getDetails(instance);
/*     */     }
/* 177 */     else if ("rootcause".equals(propertyName))
/*     */     {
/* 179 */       value = getRootCause(instance);
/*     */     }
/*     */ 
/* 182 */     return value;
/*     */   }
/*     */ 
/*     */   public void setValue(Object instance, String propertyName, Object value)
/*     */   {
/*     */   }
/*     */ 
/*     */   private String getCode(Object ex)
/*     */   {
/* 192 */     String code = null;
/* 193 */     if ((ex instanceof MessageException))
/*     */     {
/* 195 */       code = ((MessageException)ex).getCode();
/*     */     }
/*     */ 
/* 198 */     if (code == null)
/*     */     {
/* 200 */       code = "Server.Processing";
/*     */     }
/*     */ 
/* 203 */     return code;
/*     */   }
/*     */ 
/*     */   private String getType(Object ex)
/*     */   {
/* 208 */     String type = "";
/* 209 */     if ((ex != null) && (this.showStacktraces))
/*     */     {
/* 211 */       type = ex.getClass().getName();
/*     */     }
/* 213 */     return type;
/*     */   }
/*     */ 
/*     */   private String getDescription(Object ex)
/*     */   {
/* 218 */     String desc = null;
/* 219 */     if ((ex instanceof Throwable))
/*     */     {
/* 221 */       desc = ((Throwable)ex).getMessage();
/*     */     }
/*     */ 
/* 224 */     return desc;
/*     */   }
/*     */ 
/*     */   private String getDetails(Object ex)
/*     */   {
/* 229 */     StringBuffer details = new StringBuffer();
/* 230 */     if ((ex instanceof MessageException))
/*     */     {
/* 232 */       MessageException e = (MessageException)ex;
/* 233 */       if (e.getDetails() != null) {
/* 234 */         details.append(e.getDetails());
/*     */       }
/*     */     }
/* 237 */     if ((this.showStacktraces) && ((ex instanceof Throwable))) {
/* 238 */       details.append(getTraceback((Throwable)ex));
/*     */     }
/* 240 */     return details.toString();
/*     */   }
/*     */ 
/*     */   private Map getRootCause(Object ex)
/*     */   {
/* 245 */     if (ex == null) {
/* 246 */       return null;
/*     */     }
/* 248 */     if ((ex instanceof ServletException))
/*     */     {
/* 250 */       ex = ((ServletException)ex).getRootCause();
/*     */     }
/*     */ 
/* 253 */     if ((ex instanceof Throwable)) {
/* 254 */       return getExceptionInfo((Throwable)ex);
/*     */     }
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */   private Map getExceptionInfo(Throwable t)
/*     */   {
/* 261 */     Map info = new HashMap();
/* 262 */     info.put("code", getCode(t));
/* 263 */     info.put("type", getType(t));
/* 264 */     info.put("description", getDescription(t));
/* 265 */     info.put("details", getDetails(t));
/* 266 */     info.put("rootcause", getRootCause(t));
/* 267 */     return info;
/*     */   }
/*     */ 
/*     */   private static String getTraceback(Throwable e)
/*     */   {
/* 272 */     String trace = "";
/*     */ 
/* 274 */     if (e != null)
/*     */     {
/* 276 */       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 277 */       PrintWriter pr = new PrintWriter(outputStream);
/* 278 */       pr.println();
/* 279 */       e.printStackTrace(pr);
/* 280 */       pr.flush();
/* 281 */       trace = outputStream.toString();
/*     */     }
/*     */ 
/* 284 */     return trace;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 289 */     StatusInfoProxy proxy = new StatusInfoProxy();
/* 290 */     proxy.setCloneFieldsFrom(this);
/* 291 */     return proxy;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  94 */     propertyNameCache.add("code");
/*  95 */     propertyNameCache.add("type");
/*  96 */     propertyNameCache.add("description");
/*  97 */     propertyNameCache.add("details");
/*  98 */     propertyNameCache.add("rootcause");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.StatusInfoProxy
 * JD-Core Version:    0.6.0
 */