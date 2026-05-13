/*     */ package flex.messaging.services;
/*     */ 
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import flex.messaging.security.LoginManager;
/*     */ import flex.messaging.security.SecurityException;
/*     */ import flex.messaging.util.Base64.Decoder;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class AuthenticationService extends AbstractService
/*     */ {
/*     */   private static final int INVALID_CREDENTIALS_ERROR = 10064;
/*  37 */   private final String id = "authentication-service";
/*     */ 
/*     */   public AuthenticationService()
/*     */   {
/*  41 */     this(false);
/*     */   }
/*     */ 
/*     */   public AuthenticationService(boolean enableManagement)
/*     */   {
/*  47 */     super(false);
/*  48 */     super.setId("authentication-service");
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/*     */   }
/*     */ 
/*     */   public ConfigMap describeService(Endpoint endpoint)
/*     */   {
/*  60 */     return null;
/*     */   }
/*     */ 
/*     */   public Object serviceMessage(Message message)
/*     */   {
/*  65 */     return null;
/*     */   }
/*     */ 
/*     */   public Object serviceCommand(CommandMessage msg)
/*     */   {
/*  70 */     LoginManager lm = getMessageBroker().getLoginManager();
/*  71 */     switch (msg.getOperation())
/*     */     {
/*     */     case 8:
/*  74 */       if (!(msg.getBody() instanceof String))
/*     */         break;
/*  76 */       String encoded = (String)msg.getBody();
/*  77 */       Object charsetHeader = msg.getHeader("DSCredentialsCharset");
/*  78 */       if ((charsetHeader instanceof String))
/*  79 */         decodeAndLoginWithCharset(encoded, lm, (String)charsetHeader);
/*     */       else
/*  81 */         decodeAndLoginWithCharset(encoded, lm, null);
/*  82 */       break;
/*     */     case 9:
/*  85 */       lm.logout();
/*  86 */       break;
/*     */     default:
/*  88 */       throw new MessageException("Service Does Not Support Command Type " + msg.getOperation());
/*     */     }
/*  90 */     return "success";
/*     */   }
/*     */ 
/*     */   public static void decodeAndLogin(String encoded, LoginManager lm)
/*     */   {
/*  95 */     decodeAndLoginWithCharset(encoded, lm, null);
/*     */   }
/*     */ 
/*     */   private static void decodeAndLoginWithCharset(String encoded, LoginManager lm, String charset)
/*     */   {
/* 100 */     String username = null;
/* 101 */     String password = null;
/* 102 */     Base64.Decoder decoder = new Base64.Decoder();
/* 103 */     decoder.decode(encoded);
/* 104 */     String decoded = "";
/*     */ 
/* 107 */     if (charset != null)
/*     */     {
/*     */       try
/*     */       {
/* 111 */         decoded = new String(decoder.drain(), charset);
/*     */       }
/*     */       catch (UnsupportedEncodingException ex)
/*     */       {
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 119 */       decoded = new String(decoder.drain());
/*     */     }
/*     */ 
/* 122 */     int colon = decoded.indexOf(":");
/* 123 */     if ((colon > 0) && (colon < decoded.length() - 1))
/*     */     {
/* 125 */       username = decoded.substring(0, colon);
/* 126 */       password = decoded.substring(colon + 1);
/*     */     }
/*     */ 
/* 129 */     if ((username != null) && (password != null))
/*     */     {
/* 131 */       lm.login(username, password);
/*     */     }
/*     */     else
/*     */     {
/* 135 */       SecurityException se = new SecurityException();
/* 136 */       se.setCode("Client.Authentication");
/* 137 */       se.setMessage(10064);
/* 138 */       throw se;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setupServiceControl(MessageBroker broker)
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.AuthenticationService
 * JD-Core Version:    0.6.0
 */