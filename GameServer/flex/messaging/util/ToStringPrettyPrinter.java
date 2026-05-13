/*     */ package flex.messaging.util;
/*     */ 
/*     */ import flex.messaging.io.PropertyProxy;
/*     */ import flex.messaging.io.PropertyProxyRegistry;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.w3c.dom.Document;
/*     */ 
/*     */ public class ToStringPrettyPrinter extends BasicPrettyPrinter
/*     */ {
/*     */   private IdentityHashMap knownObjects;
/*     */   private int knownObjectsCount;
/*     */ 
/*     */   public String prettify(Object o)
/*     */   {
/*     */     try
/*     */     {
/*  59 */       this.knownObjects = new IdentityHashMap();
/*  60 */       this.knownObjectsCount = 0;
/*  61 */       String str = super.prettify(o);
/*     */       return str;
/*     */     }
/*     */     finally
/*     */     {
/*  65 */       this.knownObjects = null;
/*  66 */       this.knownObjectsCount = 0; } throw localObject;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  72 */     return new ToStringPrettyPrinter();
/*     */   }
/*     */ 
/*     */   protected void prettifyComplexType(Object o)
/*     */   {
/*  78 */     if (!isKnownObject(o))
/*     */     {
/*  80 */       StringBuffer header = new StringBuffer();
/*     */ 
/*  82 */       Class c = o.getClass();
/*     */ 
/*  84 */       if (hasCustomToStringMethod(c))
/*     */       {
/*  86 */         this.trace.write(String.valueOf(o));
/*     */       }
/*  88 */       else if ((o instanceof Collection))
/*     */       {
/*  90 */         Collection col = (Collection)o;
/*  91 */         header.append(c.getName()).append(" (Collection size:").append(col.size()).append(")");
/*  92 */         this.trace.startArray(header.toString());
/*     */ 
/*  94 */         Iterator it = col.iterator();
/*  95 */         int i = 0;
/*  96 */         while (it.hasNext())
/*     */         {
/*  98 */           this.trace.arrayElement(i);
/*  99 */           internalPrettify(it.next());
/* 100 */           this.trace.newLine();
/* 101 */           i++;
/*     */         }
/*     */ 
/* 104 */         this.trace.endArray();
/*     */       }
/* 106 */       else if (c.isArray())
/*     */       {
/* 108 */         Class componentType = c.getComponentType();
/* 109 */         int count = Array.getLength(o);
/*     */ 
/* 111 */         header.append(componentType.getName()).append("[] (Array length:").append(count).append(")");
/* 112 */         this.trace.startArray(header.toString());
/*     */ 
/* 114 */         for (int i = 0; i < count; i++)
/*     */         {
/* 116 */           this.trace.arrayElement(i);
/* 117 */           internalPrettify(Array.get(o, i));
/* 118 */           this.trace.newLine();
/*     */         }
/*     */ 
/* 121 */         this.trace.endArray();
/*     */       }
/* 123 */       else if ((o instanceof Document))
/*     */       {
/*     */         try
/*     */         {
/* 127 */           String xml = XMLUtil.documentToString((Document)o);
/* 128 */           this.trace.write(xml);
/*     */         }
/*     */         catch (IOException ex)
/*     */         {
/* 132 */           this.trace.write("(Document not printable)");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 137 */         PropertyProxy proxy = PropertyProxyRegistry.getProxy(o);
/*     */ 
/* 139 */         if ((o instanceof PrettyPrintable))
/*     */         {
/* 141 */           PrettyPrintable pp = (PrettyPrintable)o;
/* 142 */           header.append(pp.toStringHeader());
/*     */         }
/*     */         else
/*     */         {
/* 146 */           header.append(c.getName());
/* 147 */           if ((o instanceof Map))
/*     */           {
/* 149 */             header.append(" (Map size:").append(((Map)o).size()).append(")");
/*     */           }
/*     */         }
/*     */ 
/* 153 */         this.trace.startObject(header.toString());
/*     */ 
/* 155 */         List propertyNames = proxy.getPropertyNames();
/* 156 */         if (propertyNames != null)
/*     */         {
/* 158 */           Iterator it = propertyNames.iterator();
/* 159 */           while (it.hasNext())
/*     */           {
/* 161 */             String propName = (String)it.next();
/* 162 */             this.trace.namedElement(propName);
/*     */ 
/* 164 */             Object value = null;
/* 165 */             if (this.trace.nextElementExclude)
/*     */             {
/* 167 */               this.trace.nextElementExclude = false;
/* 168 */               value = "** [Value Suppressed] **";
/*     */             }
/*     */             else
/*     */             {
/* 172 */               if ((o instanceof PrettyPrintable))
/*     */               {
/* 174 */                 String customToString = ((PrettyPrintable)o).toStringCustomProperty(propName);
/* 175 */                 if (customToString != null)
/*     */                 {
/* 177 */                   value = customToString;
/*     */                 }
/*     */ 
/*     */               }
/*     */ 
/* 182 */               if (value == null)
/*     */               {
/* 184 */                 value = proxy.getValue(propName);
/*     */               }
/*     */             }
/*     */ 
/* 188 */             internalPrettify(value);
/* 189 */             this.trace.newLine();
/*     */           }
/*     */         }
/*     */ 
/* 193 */         this.trace.endObject();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isKnownObject(Object o)
/*     */   {
/* 200 */     Object ref = this.knownObjects.get(o);
/* 201 */     if (ref != null)
/*     */     {
/*     */       try
/*     */       {
/* 205 */         int refNum = ((Integer)ref).intValue();
/* 206 */         this.trace.writeRef(refNum);
/*     */       }
/*     */       catch (ClassCastException e)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 215 */       rememberObject(o);
/*     */     }
/* 217 */     return ref != null;
/*     */   }
/*     */ 
/*     */   private void rememberObject(Object o)
/*     */   {
/* 222 */     this.knownObjects.put(o, new Integer(this.knownObjectsCount++));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.ToStringPrettyPrinter
 * JD-Core Version:    0.6.0
 */