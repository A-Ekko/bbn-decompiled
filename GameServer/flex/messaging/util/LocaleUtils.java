/*    */ package flex.messaging.util;
/*    */ 
/*    */ import java.util.Locale;
/*    */ 
/*    */ public class LocaleUtils
/*    */ {
/*    */   public static Locale buildLocale(String locale)
/*    */   {
/* 34 */     if (locale == null)
/*    */     {
/* 36 */       return Locale.getDefault();
/*    */     }
/*    */ 
/* 40 */     int index = locale.indexOf('_');
/* 41 */     if (index == -1)
/*    */     {
/* 43 */       return new Locale(locale);
/*    */     }
/* 45 */     String language = locale.substring(0, index);
/* 46 */     String rest = locale.substring(index + 1);
/* 47 */     index = rest.indexOf('_');
/* 48 */     if (index == -1)
/*    */     {
/* 50 */       return new Locale(language, rest);
/*    */     }
/* 52 */     String country = rest.substring(0, index);
/* 53 */     rest = rest.substring(index + 1);
/* 54 */     return new Locale(language, country, rest);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.LocaleUtils
 * JD-Core Version:    0.6.0
 */