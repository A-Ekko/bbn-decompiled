/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ASObject extends HashMap
/*     */ {
/*     */   static final long serialVersionUID = 1613529666682805692L;
/*  40 */   private boolean inHashCode = false;
/*  41 */   private boolean inToString = false;
/*     */ 
/*  46 */   String namedType = null;
/*     */ 
/*     */   public ASObject()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ASObject(String name)
/*     */   {
/*  62 */     this.namedType = name;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  70 */     return this.namedType;
/*     */   }
/*     */ 
/*     */   public void setType(String type)
/*     */   {
/*  80 */     this.namedType = type;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  85 */     int h = 0;
/*  86 */     if (!this.inHashCode)
/*     */     {
/*  88 */       this.inHashCode = true;
/*     */       try
/*     */       {
/*  91 */         Iterator i = entrySet().iterator();
/*  92 */         while (i.hasNext())
/*     */         {
/*  94 */           h += i.next().hashCode();
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/*  99 */         this.inHashCode = false;
/*     */       }
/*     */     }
/* 102 */     return h;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 107 */     String className = getClass().getName();
/* 108 */     int dotIndex = className.lastIndexOf('.');
/*     */ 
/* 110 */     StringBuffer buffer = new StringBuffer();
/* 111 */     buffer.append(className.substring(dotIndex + 1));
/* 112 */     buffer.append("(").append(System.identityHashCode(this)).append(')');
/* 113 */     buffer.append('{');
/* 114 */     if (!this.inToString)
/*     */     {
/* 116 */       this.inToString = true;
/*     */       try
/*     */       {
/* 119 */         boolean pairEmitted = false;
/*     */ 
/* 121 */         Iterator i = entrySet().iterator();
/* 122 */         while (i.hasNext())
/*     */         {
/* 124 */           if (pairEmitted)
/*     */           {
/* 126 */             buffer.append(", ");
/*     */           }
/* 128 */           Map.Entry e = (Map.Entry)(Map.Entry)i.next();
/* 129 */           buffer.append(e.getKey()).append('=').append(e.getValue());
/* 130 */           pairEmitted = true;
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 135 */         this.inToString = false;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 140 */       buffer.append("...");
/*     */     }
/* 142 */     buffer.append('}');
/* 143 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.ASObject
 * JD-Core Version:    0.6.0
 */