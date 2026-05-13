/*    */ package flex.messaging.security;
/*    */ 
/*    */ import flex.messaging.FlexContext;
/*    */ import java.security.Principal;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.servlet.ServletConfig;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ public abstract class AppServerLoginCommand
/*    */   implements LoginCommand
/*    */ {
/*    */   public void start(ServletConfig config)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void stop()
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean doAuthorization(Principal principal, List roles)
/*    */     throws SecurityException
/*    */   {
/* 50 */     HttpServletRequest request = FlexContext.getHttpRequest();
/* 51 */     return request != null ? doAuthorization(principal, roles, request) : false;
/*    */   }
/*    */ 
/*    */   protected boolean doAuthorization(Principal principal, List roles, HttpServletRequest request)
/*    */     throws SecurityException
/*    */   {
/* 60 */     boolean authorized = false;
/*    */ 
/* 62 */     for (int i = 0; i < roles.size(); i++)
/*    */     {
/* 64 */       String role = (String)roles.get(i);
/* 65 */       if (!request.isUserInRole(role))
/*    */         continue;
/* 67 */       authorized = true;
/* 68 */       break;
/*    */     }
/*    */ 
/* 72 */     return authorized;
/*    */   }
/*    */ 
/*    */   protected String extractPassword(Object credentials)
/*    */   {
/* 77 */     String password = null;
/* 78 */     if ((credentials instanceof String))
/*    */     {
/* 80 */       password = (String)credentials;
/*    */     }
/* 82 */     else if ((credentials instanceof Map))
/*    */     {
/* 84 */       password = (String)((Map)credentials).get("password");
/*    */     }
/* 86 */     return password;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.security.AppServerLoginCommand
 * JD-Core Version:    0.6.0
 */