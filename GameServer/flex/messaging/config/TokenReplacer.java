/*     */ package flex.messaging.config;
/*     */ 
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.w3c.dom.Text;
/*     */ 
/*     */ public class TokenReplacer
/*     */ {
/*     */   private final Map replacedTokens;
/*     */   private final Pattern pattern;
/*     */ 
/*     */   public TokenReplacer()
/*     */   {
/*  45 */     this.replacedTokens = new LinkedHashMap();
/*  46 */     String tokenRegEx = "\\{(.*?)\\}";
/*  47 */     this.pattern = Pattern.compile(tokenRegEx);
/*     */   }
/*     */ 
/*     */   public void replaceToken(Node node, String sourceFileName)
/*     */   {
/*  60 */     if ("ip-address-pattern".equals(node.getNodeName()))
/*  61 */       return;
/*     */     Node replacementNode;
/*  66 */     if (node.getNodeValue() == null)
/*     */     {
/*     */       Node replacementNode;
/*  68 */       if ((node.getChildNodes().getLength() == 1) && ((node.getFirstChild() instanceof Text)))
/*  69 */         replacementNode = node.getFirstChild();
/*     */       else
/*  71 */         return;
/*     */     }
/*     */     else
/*     */     {
/*  75 */       replacementNode = node;
/*     */     }
/*     */ 
/*  78 */     String nodeValue = replacementNode.getNodeValue();
/*  79 */     Matcher matcher = this.pattern.matcher(nodeValue);
/*  80 */     while (matcher.find())
/*     */     {
/*  82 */       String tokenWithCurlyBraces = matcher.group();
/*  83 */       String tokenWithoutCurlyBraces = matcher.group(1);
/*     */ 
/*  85 */       String propertyValue = System.getProperty(tokenWithoutCurlyBraces);
/*  86 */       if (propertyValue != null)
/*     */       {
/*  88 */         nodeValue = StringUtils.substitute(nodeValue, tokenWithCurlyBraces, propertyValue);
/*  89 */         this.replacedTokens.put(tokenWithCurlyBraces, propertyValue);
/*     */       }
/*  94 */       else if ((!"{context.root}".equals(tokenWithCurlyBraces)) && (!"{context-root}".equals(tokenWithCurlyBraces)) && (!"{server.name}".equals(tokenWithCurlyBraces)) && (!"{server.port}".equals(tokenWithCurlyBraces)))
/*     */       {
/* 100 */         ConfigurationException ex = new ConfigurationException();
/* 101 */         Object[] args = { tokenWithCurlyBraces, sourceFileName };
/* 102 */         ex.setMessage(11125, args);
/* 103 */         throw ex;
/*     */       }
/*     */     }
/* 106 */     replacementNode.setNodeValue(nodeValue);
/*     */   }
/*     */ 
/*     */   public void reportTokens()
/*     */   {
/*     */     Iterator iter;
/* 114 */     if (Log.isWarn())
/*     */     {
/* 116 */       for (iter = this.replacedTokens.entrySet().iterator(); iter.hasNext(); )
/*     */       {
/* 118 */         Map.Entry entry = (Map.Entry)iter.next();
/* 119 */         String tokenWithParanthesis = (String)entry.getKey();
/* 120 */         String propertyValue = (String)entry.getValue();
/*     */ 
/* 122 */         if (("{context.root}".equals(tokenWithParanthesis)) || ("{context-root}".equals(tokenWithParanthesis)))
/*     */         {
/* 125 */           if (Log.isWarn()) {
/* 126 */             Log.getLogger("Configuration").warn("Token '{0}' was replaced with '{1}'. Note that this will apply to all applications on the JVM", new Object[] { tokenWithParanthesis, propertyValue });
/*     */           }
/*     */         }
/* 129 */         else if (Log.isDebug())
/*     */         {
/* 131 */           Log.getLogger("Configuration").debug("Token '{0}' was replaced with '{1}'", new Object[] { tokenWithParanthesis, propertyValue });
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.TokenReplacer
 * JD-Core Version:    0.6.0
 */