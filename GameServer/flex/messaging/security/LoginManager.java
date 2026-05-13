/*     */ package flex.messaging.security;
/*     */ 
/*     */ import flex.messaging.FlexComponent;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.FlexSession;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.config.SecurityConstraint;
/*     */ import java.security.Principal;
/*     */ import java.util.List;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class LoginManager
/*     */   implements FlexComponent
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Security";
/*     */   private static final int INVALID_LOGIN = 10050;
/*     */   private static final int LOGIN_REQ = 10051;
/*     */   private static final int NO_LOGIN_COMMAND = 10053;
/*     */   private static final int CANNOT_REAUTH = 10054;
/*     */   private static final int ACCESS_DENIED = 10055;
/*     */   private static final int LOGIN_REQ_FOR_AUTH = 10056;
/*     */   private static final int RTMP_NO_BASIC_SECURITY = 10057;
/*     */   private static final int PER_CLIENT_ANT_APPSERVER = 10065;
/*     */   private LoginCommand loginCommand;
/*     */   private boolean perClientAuthentication;
/*     */   private boolean started;
/*     */ 
/*     */   public LoginManager()
/*     */   {
/*  71 */     this.perClientAuthentication = false;
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap configMap)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/*  95 */     if ((this.perClientAuthentication) && ((this.loginCommand instanceof AppServerLoginCommand)))
/*     */     {
/*  98 */       ConfigurationException configException = new ConfigurationException();
/*  99 */       configException.setMessage(10065);
/* 100 */       throw configException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 110 */     if (!this.started)
/*     */     {
/* 112 */       validate();
/* 113 */       this.started = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 123 */     if (this.started)
/* 124 */       this.started = false;
/*     */   }
/*     */ 
/*     */   public boolean isPerClientAuthentication()
/*     */   {
/* 141 */     return this.perClientAuthentication;
/*     */   }
/*     */ 
/*     */   public void setPerClientAuthentication(boolean perClientAuthentication)
/*     */   {
/* 152 */     this.perClientAuthentication = perClientAuthentication;
/*     */   }
/*     */ 
/*     */   public boolean isStarted()
/*     */   {
/* 163 */     return this.started;
/*     */   }
/*     */ 
/*     */   public LoginCommand getLoginCommand()
/*     */   {
/* 173 */     return this.loginCommand;
/*     */   }
/*     */ 
/*     */   public void setLoginCommand(LoginCommand loginCommand)
/*     */   {
/* 183 */     this.loginCommand = loginCommand;
/*     */   }
/*     */ 
/*     */   public void login(String username, Object credentials)
/*     */   {
/* 194 */     if (getCurrentPrincipal() == null)
/*     */     {
/* 196 */       if (this.loginCommand != null)
/*     */       {
/* 198 */         if ((username != null) && (credentials != null))
/*     */         {
/* 200 */           Principal authenticated = this.loginCommand.doAuthentication(username, credentials);
/*     */ 
/* 202 */           if (authenticated == null)
/*     */           {
/* 205 */             SecurityException se = new SecurityException();
/* 206 */             se.setMessage(10050);
/* 207 */             se.setCode("Client.Authentication");
/* 208 */             throw se;
/*     */           }
/* 210 */           setCurrentPrincipal(authenticated);
/*     */         }
/*     */         else
/*     */         {
/* 215 */           SecurityException se = new SecurityException();
/* 216 */           se.setMessage(10051);
/* 217 */           se.setCode("Client.Authentication");
/* 218 */           throw se;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 224 */         SecurityException se = new SecurityException();
/* 225 */         se.setMessage(10053);
/* 226 */         se.setCode("Server.Authentication");
/* 227 */         throw se;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       String comparisonUsername;
/*     */       String comparisonUsername;
/* 237 */       if ((this.loginCommand instanceof LoginCommandExt))
/*     */       {
/* 239 */         comparisonUsername = ((LoginCommandExt)this.loginCommand).getPrincipalNameFromCredentials(username, credentials);
/*     */       }
/*     */       else
/*     */       {
/* 243 */         comparisonUsername = username;
/*     */       }
/*     */ 
/* 249 */       if ((comparisonUsername != null) && (!comparisonUsername.equals(getCurrentPrincipal().getName())))
/*     */       {
/* 252 */         SecurityException se = new SecurityException();
/* 253 */         se.setMessage(10054);
/* 254 */         se.setCode("Client.Authentication");
/* 255 */         throw se;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void logout()
/*     */   {
/* 265 */     if (this.loginCommand != null)
/*     */     {
/* 268 */       this.loginCommand.logout(getCurrentPrincipal());
/*     */ 
/* 270 */       if (FlexContext.isPerClientAuthentication())
/* 271 */         FlexContext.setUserPrincipal(null);
/*     */       else
/* 273 */         FlexContext.getFlexSession().invalidate();
/*     */     }
/*     */     else
/*     */     {
/* 277 */       FlexContext.getFlexSession().invalidate();
/*     */ 
/* 280 */       SecurityException se = new SecurityException();
/* 281 */       se.setMessage(10053);
/* 282 */       se.setCode("Server.Authorization");
/* 283 */       throw se;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkConstraint(SecurityConstraint constraint)
/*     */   {
/* 289 */     if (constraint != null)
/*     */     {
/* 291 */       Principal currentPrincipal = getCurrentPrincipal();
/*     */ 
/* 293 */       if (currentPrincipal != null)
/*     */       {
/* 295 */         List roles = constraint.getRoles();
/* 296 */         boolean authorized = (roles == null) || (checkRoles(currentPrincipal, roles));
/*     */ 
/* 298 */         if (!authorized)
/*     */         {
/* 301 */           SecurityException se = new SecurityException();
/* 302 */           se.setMessage(10055);
/* 303 */           se.setCode("Client.Authorization");
/* 304 */           throw se;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 309 */         if (!isCustomAuth(constraint))
/*     */         {
/* 312 */           if (FlexContext.getHttpResponse() == null)
/*     */           {
/* 315 */             SecurityException se = new SecurityException();
/* 316 */             se.setMessage(10057);
/* 317 */             se.setCode("Client.Authorization");
/* 318 */             throw se;
/*     */           }
/*     */ 
/* 321 */           FlexContext.getHttpResponse().setStatus(401);
/* 322 */           FlexContext.getHttpResponse().addHeader("WWW-Authenticate", "Basic realm=\"default\"");
/*     */         }
/*     */ 
/* 325 */         SecurityException se = new SecurityException();
/* 326 */         se.setMessage(10056);
/* 327 */         se.setCode("Client.Authentication");
/* 328 */         throw se;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean checkRoles(Principal principal, List roles)
/*     */   {
/* 335 */     if (this.loginCommand == null)
/* 336 */       return false;
/* 337 */     return this.loginCommand.doAuthorization(principal, roles);
/*     */   }
/*     */ 
/*     */   private Principal getCurrentPrincipal()
/*     */   {
/* 348 */     return FlexContext.getUserPrincipal();
/*     */   }
/*     */ 
/*     */   private void setCurrentPrincipal(Principal p)
/*     */   {
/* 353 */     FlexContext.setUserPrincipal(p);
/*     */   }
/*     */ 
/*     */   private boolean isCustomAuth(SecurityConstraint constraint)
/*     */   {
/* 358 */     return "Custom".equals(constraint.getMethod());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.security.LoginManager
 * JD-Core Version:    0.6.0
 */