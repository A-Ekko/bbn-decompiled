/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TraitsInfo
/*     */ {
/*     */   private final String className;
/*     */   private final boolean dynamic;
/*     */   private final boolean externalizable;
/*     */   private List properties;
/*     */ 
/*     */   public TraitsInfo(String className)
/*     */   {
/*  42 */     this(className, false, false, 10);
/*     */   }
/*     */ 
/*     */   public TraitsInfo(String className, int initialCount)
/*     */   {
/*  47 */     this(className, false, false, initialCount);
/*     */   }
/*     */ 
/*     */   public TraitsInfo(String className, boolean dynamic, boolean externalizable, int initialCount)
/*     */   {
/*  52 */     this(className, dynamic, externalizable, new ArrayList(initialCount));
/*     */   }
/*     */ 
/*     */   public TraitsInfo(String className, boolean dynamic, boolean externalizable, List properties)
/*     */   {
/*  57 */     if (className == null) {
/*  58 */       className = "";
/*     */     }
/*  60 */     this.className = className;
/*     */ 
/*  62 */     if (properties == null) {
/*  63 */       properties = new ArrayList();
/*     */     }
/*  65 */     this.properties = properties;
/*  66 */     this.dynamic = dynamic;
/*  67 */     this.externalizable = externalizable;
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/*  72 */     return this.dynamic;
/*     */   }
/*     */ 
/*     */   public boolean isExternalizable()
/*     */   {
/*  77 */     return this.externalizable;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/*  82 */     return this.properties.size();
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/*  87 */     return this.className;
/*     */   }
/*     */ 
/*     */   public void addProperty(String name)
/*     */   {
/*  92 */     this.properties.add(name);
/*     */   }
/*     */ 
/*     */   public void addAllProperties(Collection props)
/*     */   {
/*  97 */     this.properties.addAll(props);
/*     */   }
/*     */ 
/*     */   public String getProperty(int i)
/*     */   {
/* 102 */     return (String)this.properties.get(i);
/*     */   }
/*     */ 
/*     */   public List getProperties()
/*     */   {
/* 107 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 112 */     if (obj == this)
/*     */     {
/* 114 */       return true;
/*     */     }
/*     */ 
/* 117 */     if ((obj instanceof TraitsInfo))
/*     */     {
/* 119 */       TraitsInfo other = (TraitsInfo)obj;
/*     */ 
/* 121 */       if (!this.className.equals(other.className))
/*     */       {
/* 123 */         return false;
/*     */       }
/*     */ 
/* 126 */       if (this.dynamic != other.dynamic)
/*     */       {
/* 128 */         return false;
/*     */       }
/*     */ 
/* 131 */       List thisProperties = this.properties;
/* 132 */       List otherProperties = other.properties;
/*     */ 
/* 134 */       if (thisProperties != otherProperties)
/*     */       {
/* 136 */         int thisCount = thisProperties.size();
/*     */ 
/* 138 */         if (thisCount != otherProperties.size())
/*     */         {
/* 140 */           return false;
/*     */         }
/*     */ 
/* 143 */         for (int i = 0; i < thisCount; i++)
/*     */         {
/* 145 */           Object thisProp = thisProperties.get(i);
/* 146 */           Object otherProp = otherProperties.get(i);
/* 147 */           if ((thisProp == null) || (otherProp == null))
/*     */             continue;
/* 149 */           if (!thisProp.equals(otherProp))
/*     */           {
/* 151 */             return false;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 157 */       return true;
/*     */     }
/*     */ 
/* 160 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 170 */     int c = this.className.hashCode();
/* 171 */     c = this.dynamic ? c << 2 : c << 1;
/* 172 */     c |= this.properties.size() << 24;
/* 173 */     return c;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.TraitsInfo
 * JD-Core Version:    0.6.0
 */