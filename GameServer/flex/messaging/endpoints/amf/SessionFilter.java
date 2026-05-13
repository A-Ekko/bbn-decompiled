/*    */ package flex.messaging.endpoints.amf;
/*    */ 
/*    */ import flex.messaging.FlexContext;
/*    */ import flex.messaging.io.amf.ActionContext;
/*    */ import flex.messaging.io.amf.ActionMessage;
/*    */ import flex.messaging.io.amf.MessageHeader;
/*    */ import java.io.IOException;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ 
/*    */ public class SessionFilter extends AMFFilter
/*    */ {
/*    */   public void invoke(ActionContext context)
/*    */     throws IOException
/*    */   {
/* 49 */     this.next.invoke(context);
/*    */     try
/*    */     {
/* 53 */       HttpServletRequest request = FlexContext.getHttpRequest();
/* 54 */       HttpServletResponse response = FlexContext.getHttpResponse();
/*    */ 
/* 56 */       StringBuffer reqURL = request.getRequestURL();
/*    */ 
/* 58 */       if (reqURL != null)
/*    */       {
/* 60 */         if (request.getQueryString() != null) {
/* 61 */           reqURL.append("?").append(request.getQueryString());
/*    */         }
/* 63 */         String oldFullURL = reqURL.toString().trim();
/* 64 */         String encFullURL = response.encodeURL(oldFullURL).trim();
/*    */ 
/* 66 */         String sessionSuffix = null;
/*    */ 
/* 69 */         int pos = encFullURL.toLowerCase().indexOf(";jsessionid");
/* 70 */         if (pos > 0)
/*    */         {
/* 72 */           StringBuffer sb = new StringBuffer();
/* 73 */           sb.append(encFullURL.substring(pos));
/* 74 */           sessionSuffix = sb.toString();
/*    */         }
/*    */ 
/* 77 */         if ((sessionSuffix != null) && (oldFullURL.indexOf(sessionSuffix) < 0))
/*    */         {
/* 79 */           context.getResponseMessage().addHeader(new MessageHeader("AppendToGatewayUrl", false, sessionSuffix));
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.amf.SessionFilter
 * JD-Core Version:    0.6.0
 */