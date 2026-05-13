/*     */ package flex.messaging.services.messaging.selector;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.messages.Message;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class JMSSelector
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Message.Selector";
/*     */   private static final int PARSE_FAILURE = 10600;
/*     */   private static final int BAD_TYPE_COMPARISON = 10601;
/*     */   private static final int PARSER_ERROR = 10602;
/*     */   static final boolean debug = false;
/*     */   SQLParser parser;
/*     */   String pattern;
/*     */   Message msg;
/*     */ 
/*     */   public JMSSelector(String pattern)
/*     */   {
/*  47 */     if (pattern == null) {
/*  48 */       pattern = "";
/*     */     }
/*     */ 
/*  52 */     this.msg = null;
/*  53 */     this.pattern = pattern;
/*  54 */     InputStream stream = new ByteArrayInputStream(pattern.getBytes());
/*     */ 
/*  56 */     this.parser = new SQLParser(this, stream);
/*     */   }
/*     */ 
/*     */   public String getPattern()
/*     */   {
/*  64 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   public void setPattern(String p)
/*     */   {
/*  73 */     this.pattern = p;
/*  74 */     InputStream stream = new ByteArrayInputStream(this.pattern.getBytes());
/*     */ 
/*  76 */     this.parser.ReInit(stream);
/*     */     try
/*     */     {
/*  79 */       match(this.msg);
/*     */     }
/*     */     catch (MessageException me)
/*     */     {
/*  83 */       throw me;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  87 */       throw new MessageException(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean match(Message msg)
/*     */   {
/* 100 */     boolean matched = false;
/* 101 */     if (this.pattern.equals(""))
/*     */     {
/* 103 */       matched = true;
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 109 */         matched = this.parser.match(msg);
/*     */       }
/*     */       catch (ParseException pex)
/*     */       {
/*     */         InputStream stream;
/* 113 */         JMSSelectorException jmse = new JMSSelectorException();
/* 114 */         jmse.setMessage(10600, new Object[] { this.pattern, pex.getMessage() });
/* 115 */         throw jmse;
/*     */       }
/*     */       catch (ClassCastException cce)
/*     */       {
/* 119 */         JMSSelectorException jmse = new JMSSelectorException();
/* 120 */         jmse.setMessage(10601, new Object[] { this.pattern });
/* 121 */         throw jmse;
/*     */       }
/*     */       catch (Error err)
/*     */       {
/* 125 */         JMSSelectorException jmse = new JMSSelectorException();
/* 126 */         jmse.setMessage(10602, new Object[] { this.pattern, err.getMessage() });
/* 127 */         throw jmse;
/*     */       }
/*     */       finally
/*     */       {
/* 131 */         InputStream stream = new ByteArrayInputStream(this.pattern.getBytes());
/* 132 */         this.parser.ReInit(stream);
/*     */       }
/*     */     }
/* 135 */     return matched;
/*     */   }
/*     */ 
/*     */   boolean matchPattern(String patternStr, String str, char escapeChar)
/*     */   {
/* 153 */     boolean matched = false;
/* 154 */     String escapeCharStr = String.valueOf(escapeChar);
/* 155 */     String wildCards = "_%";
/* 156 */     String delims = wildCards + escapeCharStr;
/* 157 */     boolean escaped = false;
/* 158 */     int index = 0;
/* 159 */     String tok = null;
/*     */     try
/*     */     {
/* 167 */       if (str != null) {
/* 168 */         StringTokenizer st = new StringTokenizer(patternStr, delims, true);
/*     */ 
/* 172 */         ArrayList tokens = new ArrayList();
/* 173 */         int k = 1;
/* 174 */         while (st.hasMoreTokens()) {
/* 175 */           tok = st.nextToken();
/*     */ 
/* 179 */           tokens.add(tok);
/*     */         }
/*     */ 
/* 182 */         matched = true;
/*     */ 
/* 185 */         int numTokens = tokens.size();
/* 186 */         for (int i = 0; i < numTokens; i++) {
/* 187 */           tok = (String)tokens.get(i);
/*     */ 
/* 190 */           if ((tok.equals(escapeCharStr)) && (!escaped))
/*     */           {
/* 192 */             escaped = true;
/*     */           }
/* 194 */           else if ((tok.equals("%")) && (!escaped)) {
/* 195 */             if (i == numTokens - 1)
/*     */             {
/* 199 */               index = str.length(); } else {
/* 200 */               if (i == numTokens - 1) {
/*     */                 continue;
/*     */               }
/* 203 */               int _cnt = 0;
/* 204 */               i++;
/* 205 */               for (; i < numTokens; i++) {
/* 206 */                 tok = (String)tokens.get(i);
/*     */ 
/* 208 */                 if ((tok.equals(escapeCharStr)) && (!escaped))
/*     */                 {
/* 210 */                   escaped = true;
/*     */                 } else {
/* 212 */                   if ((tok.equals("%")) && (!escaped)) {
/*     */                     continue;
/*     */                   }
/* 215 */                   if ((tok.equals("_")) && (!escaped)) {
/* 216 */                     _cnt++;
/*     */                   }
/*     */                   else
/*     */                   {
/* 220 */                     int oldIndex = index;
/*     */ 
/* 222 */                     if (i == numTokens - 1)
/*     */                     {
/* 230 */                       if (str.endsWith(tok))
/* 231 */                         index = str.length() - tok.length();
/*     */                       else {
/* 233 */                         matched = false;
/*     */                       }
/*     */ 
/*     */                     }
/*     */                     else
/*     */                     {
/* 239 */                       index = str.indexOf(tok, index);
/*     */                     }
/*     */ 
/* 242 */                     if (index < 0) {
/* 243 */                       matched = false;
/*     */                     }
/* 249 */                     else if (index - oldIndex >= _cnt) {
/* 250 */                       index += tok.length();
/*     */                     }
/*     */                     else
/*     */                     {
/* 256 */                       matched = false;
/*     */                     }
/*     */ 
/* 264 */                     escaped = false;
/* 265 */                     break;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/* 269 */           } else if ((tok.equals("_")) && (!escaped)) {
/* 270 */             index++;
/*     */           }
/*     */           else
/*     */           {
/* 276 */             int tokLen = tok.length();
/*     */ 
/* 281 */             if (index + tokLen <= str.length()) {
/* 282 */               String subStr = null;
/*     */               try
/*     */               {
/* 285 */                 subStr = str.substring(index, index + tokLen);
/*     */               } catch (StringIndexOutOfBoundsException e) {
/* 287 */                 matched = false;
/* 288 */                 break;
/*     */               }
/*     */ 
/* 291 */               if (!subStr.equalsIgnoreCase(tok)) {
/* 292 */                 matched = false;
/*     */ 
/* 297 */                 break;
/*     */               }
/* 299 */               index += tok.length();
/*     */             }
/*     */             else
/*     */             {
/* 305 */               matched = false;
/*     */ 
/* 310 */               break;
/*     */             }
/* 312 */             escaped = false;
/*     */           }
/*     */         }
/*     */       }
/* 316 */       if ((matched) && (index != str.length()))
/*     */       {
/* 320 */         matched = false;
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (StringIndexOutOfBoundsException e)
/*     */     {
/* 328 */       matched = false;
/*     */     }
/*     */ 
/* 338 */     return matched;
/*     */   }
/*     */ 
/*     */   String processStringLiteral(String strLiteral)
/*     */   {
/* 350 */     strLiteral = strLiteral.substring(1, strLiteral.length() - 1);
/*     */ 
/* 353 */     int index = strLiteral.indexOf("''");
/* 354 */     if (index > -1) {
/* 355 */       StringBuffer sb = new StringBuffer(strLiteral);
/* 356 */       while (index != -1) {
/* 357 */         sb.deleteCharAt(index);
/* 358 */         index = sb.toString().indexOf("''");
/*     */       }
/* 360 */       strLiteral = sb.toString();
/*     */     }
/* 362 */     return strLiteral;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.selector.JMSSelector
 * JD-Core Version:    0.6.0
 */