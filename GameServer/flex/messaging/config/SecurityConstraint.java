/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SecurityConstraint
/*     */ {
/*     */   public static final String BASIC_AUTH_METHOD = "Basic";
/*     */   public static final String CUSTOM_AUTH_METHOD = "Custom";
/*     */   private final String id;
/*     */   private String method;
/*     */   private List roles;
/*     */ 
/*     */   public SecurityConstraint()
/*     */   {
/*  41 */     this(null);
/*     */   }
/*     */ 
/*     */   public SecurityConstraint(String id)
/*     */   {
/*  51 */     this.id = id;
/*  52 */     this.method = "Custom";
/*     */   }
/*     */ 
/*     */   public List getRoles()
/*     */   {
/*  62 */     return this.roles;
/*     */   }
/*     */ 
/*     */   public void addRole(String role)
/*     */   {
/*  72 */     if (role != null)
/*     */     {
/*  74 */       if (this.roles == null) {
/*  75 */         this.roles = new Vector();
/*     */       }
/*  77 */       this.roles.add(role);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  88 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getMethod()
/*     */   {
/*  98 */     return this.method;
/*     */   }
/*     */ 
/*     */   public void setMethod(String method)
/*     */   {
/* 109 */     if (method != null)
/*     */     {
/* 111 */       if ("Custom".equalsIgnoreCase(method))
/*     */       {
/* 113 */         this.method = "Custom";
/*     */       }
/* 115 */       else if ("Basic".equalsIgnoreCase(method))
/*     */       {
/* 117 */         this.method = "Basic";
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.SecurityConstraint
 * JD-Core Version:    0.6.0
 */