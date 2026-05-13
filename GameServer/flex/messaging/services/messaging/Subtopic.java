/*     */ package flex.messaging.services.messaging;
/*     */ 
/*     */ import flex.messaging.services.ServiceException;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class Subtopic
/*     */ {
/*     */   public static final String SUBTOPIC_WILDCARD = "*";
/*     */   private String subtopic;
/*     */   private String separator;
/*     */   private boolean hierarchical;
/*     */   private boolean hasSubtopicWildcard;
/*     */ 
/*     */   public Subtopic(String subtopic, String separator)
/*     */   {
/*  49 */     this.subtopic = subtopic;
/*  50 */     this.separator = separator;
/*     */ 
/*  53 */     if (subtopic.length() == 0)
/*     */     {
/*  55 */       ServiceException se = new ServiceException();
/*  56 */       se.setMessage(10554, new Object[] { subtopic });
/*  57 */       throw se;
/*     */     }
/*     */ 
/*  60 */     if ((separator != null) && (subtopic.indexOf(separator) != -1))
/*     */     {
/*  62 */       this.hierarchical = true;
/*     */ 
/*  67 */       if ((subtopic.startsWith(separator)) || (subtopic.endsWith(separator)) || (subtopic.indexOf(separator + separator) != -1))
/*     */       {
/*  71 */         ServiceException se = new ServiceException();
/*  72 */         se.setMessage(10554, new Object[] { subtopic });
/*  73 */         throw se;
/*     */       }
/*     */ 
/*  82 */       StringTokenizer tokenizer = new StringTokenizer(subtopic, separator);
/*  83 */       while (tokenizer.hasMoreTokens())
/*     */       {
/*  85 */         String token = tokenizer.nextToken();
/*  86 */         if (token.indexOf("*") != -1)
/*     */         {
/*  88 */           if (!token.equals("*"))
/*     */           {
/*  90 */             ServiceException se = new ServiceException();
/*  91 */             se.setMessage(10554, new Object[] { subtopic });
/*  92 */             throw se;
/*     */           }
/*     */ 
/*  96 */           this.hasSubtopicWildcard = true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/* 103 */     else if (subtopic.indexOf("*") != -1)
/*     */     {
/* 105 */       if (!subtopic.equals("*"))
/*     */       {
/* 107 */         ServiceException se = new ServiceException();
/* 108 */         se.setMessage(10554, new Object[] { subtopic });
/* 109 */         throw se;
/*     */       }
/*     */ 
/* 113 */       this.hasSubtopicWildcard = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean containsSubtopicWildcard()
/*     */   {
/* 169 */     return this.hasSubtopicWildcard;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 175 */     if ((!(other instanceof Subtopic)) || (other == null))
/* 176 */       return false;
/* 177 */     Subtopic otherSubtopic = (Subtopic)other;
/*     */ 
/* 181 */     return (this.subtopic.equals(otherSubtopic.subtopic)) && ((this.separator.equals(otherSubtopic.separator)) || ((this.separator == null) && (otherSubtopic.separator == null)));
/*     */   }
/*     */ 
/*     */   public String getSeparator()
/*     */   {
/* 197 */     return this.separator;
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/* 207 */     return this.subtopic;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 213 */     return this.subtopic.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean isHierarchical()
/*     */   {
/* 223 */     return this.hierarchical;
/*     */   }
/*     */ 
/*     */   public boolean matches(Subtopic other)
/*     */   {
/* 240 */     if ((!this.hasSubtopicWildcard) && (!other.hasSubtopicWildcard))
/*     */     {
/* 242 */       return this.subtopic.equals(other.subtopic);
/*     */     }
/*     */ 
/* 248 */     if ((this.hierarchical) && (other.hierarchical) && (!this.separator.equals(other.separator))) {
/* 249 */       return false;
/*     */     }
/* 251 */     StringTokenizer t1 = new StringTokenizer(this.subtopic, this.separator);
/* 252 */     StringTokenizer t2 = new StringTokenizer(other.subtopic, other.separator);
/* 253 */     int n = t1.countTokens();
/* 254 */     int difference = n - t2.countTokens();
/*     */ 
/* 256 */     String tok1 = null;
/* 257 */     String tok2 = null;
/*     */ 
/* 259 */     while (n-- > 0)
/*     */     {
/* 261 */       tok1 = t1.nextToken();
/*     */       boolean matchToken;
/*     */       boolean matchToken;
/* 262 */       if (tok1.equals("*"))
/* 263 */         matchToken = false;
/*     */       else {
/* 265 */         matchToken = true;
/*     */       }
/* 267 */       if (!t2.hasMoreTokens())
/*     */         break;
/* 269 */       tok2 = t2.nextToken();
/* 270 */       if ((!tok2.equals("*")) && 
/* 278 */         (matchToken) && (!tok1.equals(tok2))) {
/* 279 */         return false;
/*     */       }
/*     */     }
/* 282 */     if (difference == 0)
/* 283 */       return true;
/* 284 */     if ((difference < 0) && (tok1.equals("*"))) {
/* 285 */       return true;
/*     */     }
/* 287 */     return (difference > 0) && (tok2.equals("*"));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 296 */     return this.subtopic;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.Subtopic
 * JD-Core Version:    0.6.0
 */