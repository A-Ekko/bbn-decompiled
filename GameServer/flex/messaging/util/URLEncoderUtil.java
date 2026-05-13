/*    */ package flex.messaging.util;
/*    */ 
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.net.URLEncoder;
/*    */ 
/*    */ public class URLEncoderUtil
/*    */ {
/*    */   public static String encode(String s, String encoding)
/*    */     throws UnsupportedEncodingException
/*    */   {
/* 29 */     return URLEncoder.encode(s, encoding);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.URLEncoderUtil
 * JD-Core Version:    0.6.0
 */