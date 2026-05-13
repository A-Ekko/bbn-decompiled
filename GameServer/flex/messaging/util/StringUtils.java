/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.TransformerFactoryConfigurationError;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ 
/*     */ public class StringUtils
/*     */ {
/*  37 */   public static final String NEWLINE = System.getProperty("line.separator");
/*     */ 
/*     */   public static String substitute(String str, String from, String to)
/*     */   {
/*  41 */     if ((from == null) || (from.equals("")) || (to == null)) {
/*  42 */       return str;
/*     */     }
/*  44 */     int index = str.indexOf(from);
/*     */ 
/*  46 */     if (index == -1) {
/*  47 */       return str;
/*     */     }
/*  49 */     StringBuffer buf = new StringBuffer(str.length());
/*  50 */     int lastIndex = 0;
/*     */ 
/*  52 */     while (index != -1)
/*     */     {
/*  54 */       buf.append(str.substring(lastIndex, index));
/*  55 */       buf.append(to);
/*  56 */       lastIndex = index + from.length();
/*  57 */       index = str.indexOf(from, lastIndex);
/*     */     }
/*     */ 
/*  61 */     buf.append(str.substring(lastIndex));
/*     */ 
/*  63 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public static boolean findMatchWithWildcard(char[] src, char[] pat)
/*     */   {
/*  68 */     if ((src == null) || (pat == null)) {
/*  69 */       return false;
/*     */     }
/*     */ 
/*  72 */     if (pat.length == 0) {
/*  73 */       return false;
/*     */     }
/*  75 */     if (src.length == 0) {
/*  76 */       return (pat.length == 0) || ((pat.length == 1) && ((pat[0] == '*') || (pat[0] == '?')));
/*     */     }
/*  78 */     boolean star = false;
/*     */ 
/*  80 */     int srcLen = src.length;
/*  81 */     int patLen = pat.length;
/*  82 */     int srcIdx = 0;
/*  83 */     int patIdx = 0;
/*     */ 
/*  85 */     for (; srcIdx < srcLen; srcIdx++)
/*     */     {
/*  87 */       if (patIdx == patLen)
/*     */       {
/*  89 */         if (patLen < srcLen - srcIdx)
/*  90 */           patIdx = 0;
/*     */         else {
/*  92 */           return false;
/*     */         }
/*     */       }
/*  95 */       char s = src[srcIdx];
/*  96 */       char m = pat[patIdx];
/*     */ 
/*  98 */       switch (m)
/*     */       {
/*     */       case '*':
/* 102 */         if (patIdx == pat.length - 1)
/* 103 */           return true;
/* 104 */         star = true;
/* 105 */         patIdx++;
/* 106 */         break;
/*     */       case '?':
/* 109 */         patIdx++;
/* 110 */         break;
/*     */       default:
/* 113 */         if (s != m)
/*     */         {
/* 115 */           if (star)
/*     */             continue;
/* 117 */           if (patLen < srcLen - srcIdx)
/* 118 */             patIdx = 0;
/*     */           else {
/* 120 */             return false;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 125 */           star = false;
/* 126 */           patIdx++;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 132 */     if (patIdx < patLen)
/*     */     {
/* 135 */       for (; patIdx < patLen; patLen++)
/*     */       {
/* 137 */         if (pat[patIdx] != '*')
/*     */         {
/* 139 */           return false;
/*     */         }
/*     */       }
/* 142 */       return true;
/*     */     }
/*     */ 
/* 146 */     return !star;
/*     */   }
/*     */ 
/*     */   public static String prettifyXML(String xml)
/*     */   {
/* 155 */     String result = xml;
/*     */     try
/*     */     {
/* 158 */       StringReader reader = new StringReader(xml);
/* 159 */       StringWriter writer = new StringWriter();
/* 160 */       Transformer transformer = TransformerFactory.newInstance().newTransformer();
/*     */ 
/* 162 */       transformer.setOutputProperty("method", "xml");
/* 163 */       transformer.setOutputProperty("indent", "yes");
/* 164 */       transformer.transform(new StreamSource(reader), new StreamResult(writer));
/*     */ 
/* 166 */       writer.close();
/*     */ 
/* 168 */       result = writer.toString();
/*     */     }
/*     */     catch (TransformerFactoryConfigurationError error)
/*     */     {
/*     */     }
/*     */     catch (TransformerException error)
/*     */     {
/*     */     }
/*     */     catch (IOException error)
/*     */     {
/*     */     }
/*     */ 
/* 182 */     return result;
/*     */   }
/*     */ 
/*     */   public static String prettifyString(String string)
/*     */   {
/* 191 */     String result = string;
/* 192 */     if (string.startsWith("<?xml"))
/*     */     {
/* 194 */       result = prettifyXML(string);
/*     */     }
/* 196 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(String string)
/*     */   {
/* 204 */     return (string == null) || (string.length() == 0);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.StringUtils
 * JD-Core Version:    0.6.0
 */