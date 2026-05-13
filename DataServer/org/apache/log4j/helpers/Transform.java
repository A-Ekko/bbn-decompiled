/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ public class Transform
/*    */ {
/*    */   private static final String CDATA_START = "<![CDATA[";
/*    */   private static final String CDATA_END = "]]>";
/*    */   private static final String CDATA_PSEUDO_END = "]]&gt;";
/*    */   private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
/* 25 */   private static final int CDATA_END_LEN = "]]>".length();
/*    */ 
/*    */   public static String escapeTags(String input)
/*    */   {
/* 40 */     if ((input == null) || (input.length() == 0)) {
/* 41 */       return input;
/*    */     }
/*    */ 
/* 47 */     StringBuffer buf = new StringBuffer(input.length() + 6);
/* 48 */     char ch = ' ';
/*    */ 
/* 50 */     int len = input.length();
/* 51 */     for (int i = 0; i < len; i++) {
/* 52 */       ch = input.charAt(i);
/* 53 */       if (ch == '<')
/* 54 */         buf.append("&lt;");
/* 55 */       else if (ch == '>')
/* 56 */         buf.append("&gt;");
/*    */       else {
/* 58 */         buf.append(ch);
/*    */       }
/*    */     }
/* 61 */     return buf.toString();
/*    */   }
/*    */ 
/*    */   public static void appendEscapingCDATA(StringBuffer buf, String str)
/*    */   {
/* 74 */     int end = str.indexOf("]]>");
/*    */ 
/* 76 */     if (end < 0) {
/* 77 */       buf.append(str);
/* 78 */       return;
/*    */     }
/*    */ 
/* 81 */     int start = 0;
/* 82 */     while (end > -1) {
/* 83 */       buf.append(str.substring(start, end));
/* 84 */       buf.append("]]>]]&gt;<![CDATA[");
/* 85 */       start = end + CDATA_END_LEN;
/* 86 */       if (start < str.length())
/* 87 */         end = str.indexOf("]]>", start);
/*    */       else {
/* 89 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 93 */     buf.append(str.substring(start));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.Transform
 * JD-Core Version:    0.6.0
 */