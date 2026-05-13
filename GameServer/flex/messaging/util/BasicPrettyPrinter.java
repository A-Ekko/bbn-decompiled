/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class BasicPrettyPrinter
/*     */   implements PrettyPrinter, Cloneable
/*     */ {
/*     */   protected ObjectTrace trace;
/*     */ 
/*     */   public String prettify(Object o)
/*     */   {
/*     */     try
/*     */     {
/*  54 */       this.trace = new ObjectTrace();
/*  55 */       internalPrettify(o);
/*  56 */       String str1 = this.trace.toString();
/*     */       return str1;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  60 */       String str2 = this.trace.toString();
/*     */       return str2; } finally { this.trace = null; } throw localObject;
/*     */   }
/*     */ 
/*     */   protected void internalPrettify(Object o)
/*     */   {
/*  70 */     if (o == null)
/*     */     {
/*  72 */       this.trace.writeNull();
/*     */     }
/*  74 */     else if ((o instanceof String))
/*     */     {
/*  76 */       String string = (String)o;
/*  77 */       if (string.startsWith("<?xml"))
/*     */       {
/*  79 */         this.trace.write(StringUtils.prettifyXML(string));
/*     */       }
/*     */       else
/*     */       {
/*  83 */         this.trace.write(string);
/*     */       }
/*     */     }
/*  86 */     else if (((o instanceof Number)) || ((o instanceof Boolean)) || ((o instanceof Date)) || ((o instanceof Calendar)) || ((o instanceof Character)))
/*     */     {
/*  89 */       this.trace.write(o);
/*     */     }
/*     */     else
/*     */     {
/*  93 */       prettifyComplexType(o);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void prettifyComplexType(Object o)
/*     */   {
/*  99 */     StringBuffer header = new StringBuffer();
/*     */ 
/* 101 */     if ((o instanceof PrettyPrintable))
/*     */     {
/* 103 */       PrettyPrintable pp = (PrettyPrintable)o;
/* 104 */       header.append(pp.toStringHeader());
/*     */     }
/*     */ 
/* 107 */     Class c = o.getClass();
/* 108 */     String className = c.getName();
/*     */ 
/* 110 */     if ((o instanceof Collection))
/*     */     {
/* 112 */       header.append(className).append(" (Collection size:").append(((Collection)o).size()).append(")");
/*     */     }
/* 114 */     else if ((o instanceof Map))
/*     */     {
/* 116 */       header.append(className).append(" (Map size:").append(((Map)o).size()).append(")");
/*     */     }
/* 118 */     else if ((c.isArray()) && (c.getComponentType() != null))
/*     */     {
/* 120 */       Class componentType = c.getComponentType();
/* 121 */       className = componentType.getName();
/* 122 */       header.append(className).append("[] (Array length:").append(Array.getLength(o)).append(")");
/*     */     }
/*     */     else
/*     */     {
/* 126 */       header.append(className);
/*     */     }
/*     */ 
/* 129 */     this.trace.startObject(header.toString());
/* 130 */     this.trace.endObject();
/*     */   }
/*     */ 
/*     */   protected boolean hasCustomToStringMethod(Class c)
/*     */   {
/*     */     try
/*     */     {
/* 145 */       Method toStringMethod = c.getMethod("toString", null);
/* 146 */       Class declaringClass = toStringMethod.getDeclaringClass();
/* 147 */       if ((declaringClass != Object.class) && (!declaringClass.getName().startsWith("java.util")))
/*     */       {
/* 150 */         return true;
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/*     */ 
/* 157 */     return false;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 162 */     return new BasicPrettyPrinter();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.BasicPrettyPrinter
 * JD-Core Version:    0.6.0
 */