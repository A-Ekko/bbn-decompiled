/*    */ package flex.messaging.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class FileUtils
/*    */ {
/*    */   public static final String UTF_8 = "UTF-8";
/*    */   public static final String UTF_16 = "UTF-16";
/*    */ 
/*    */   public static final String consumeBOM(InputStream in, String default_encoding)
/*    */     throws IOException
/*    */   {
/* 24 */     in.mark(3);
/*    */ 
/* 30 */     if ((in.read() == 239) && (in.read() == 187) && (in.read() == 191))
/*    */     {
/* 33 */       if (System.getProperty("flex.platform.CLR") != null)
/*    */       {
/* 35 */         return "UTF8";
/*    */       }
/*    */ 
/* 39 */       return "UTF-8";
/*    */     }
/*    */ 
/* 44 */     in.reset();
/* 45 */     int b0 = in.read();
/* 46 */     int b1 = in.read();
/* 47 */     if (((b0 == 255) && (b1 == 254)) || ((b0 == 254) && (b1 == 255)))
/*    */     {
/* 49 */       in.reset();
/*    */ 
/* 51 */       if (System.getProperty("flex.platform.CLR") != null)
/*    */       {
/* 53 */         return "UTF16";
/*    */       }
/*    */ 
/* 57 */       return "UTF-16";
/*    */     }
/*    */ 
/* 63 */     in.reset();
/* 64 */     if ((default_encoding != null) && (default_encoding.length() != 0))
/*    */     {
/* 66 */       return default_encoding;
/*    */     }
/*    */ 
/* 70 */     return System.getProperty("file.encoding");
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.FileUtils
 * JD-Core Version:    0.6.0
 */