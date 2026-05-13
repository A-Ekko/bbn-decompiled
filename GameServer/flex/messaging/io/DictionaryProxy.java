/*     */ package flex.messaging.io;
/*     */ 
/*     */ import java.util.Dictionary;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ 
/*     */ public class DictionaryProxy extends BeanProxy
/*     */ {
/*     */   static final long serialVersionUID = 1501461889185692712L;
/*     */ 
/*     */   public DictionaryProxy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DictionaryProxy(Dictionary defaultInstance)
/*     */   {
/*  44 */     super(defaultInstance);
/*     */   }
/*     */ 
/*     */   public List getPropertyNames(Object instance)
/*     */   {
/*  49 */     if (instance == null) {
/*  50 */       return null;
/*     */     }
/*  52 */     List propertyNames = null;
/*  53 */     List excludes = null;
/*     */ 
/*  55 */     if (this.descriptor != null)
/*     */     {
/*  57 */       excludes = this.descriptor.getExcludesForInstance(instance);
/*  58 */       if (excludes == null) {
/*  59 */         excludes = this.descriptor.getExcludes();
/*     */       }
/*     */     }
/*     */ 
/*  63 */     if ((instance instanceof Dictionary))
/*     */     {
/*  65 */       Dictionary dictionary = (Dictionary)instance;
/*     */ 
/*  67 */       propertyNames = new ArrayList(dictionary.size());
/*     */ 
/*  69 */       Enumeration keys = dictionary.keys();
/*  70 */       while (keys.hasMoreElements())
/*     */       {
/*  72 */         Object key = keys.nextElement();
/*  73 */         if (key != null)
/*     */         {
/*  75 */           if ((excludes != null) && (excludes.contains(key))) {
/*     */             continue;
/*     */           }
/*  78 */           propertyNames.add(key.toString());
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  84 */     List beanProperties = super.getPropertyNames();
/*  85 */     if (propertyNames == null)
/*     */     {
/*  87 */       propertyNames = beanProperties;
/*     */     }
/*     */     else
/*     */     {
/*  91 */       propertyNames.addAll(beanProperties);
/*     */     }
/*     */ 
/*  94 */     return propertyNames;
/*     */   }
/*     */ 
/*     */   public Object getValue(Object instance, String propertyName)
/*     */   {
/*  99 */     if ((instance == null) || (propertyName == null)) {
/* 100 */       return null;
/*     */     }
/*     */ 
/* 103 */     Object value = super.getValue(instance, propertyName);
/*     */ 
/* 106 */     if ((value == null) && ((instance instanceof Dictionary)))
/*     */     {
/* 108 */       Dictionary dictionary = (Dictionary)instance;
/* 109 */       value = dictionary.get(propertyName);
/*     */     }
/*     */ 
/* 112 */     return value;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 117 */     DictionaryProxy proxy = new DictionaryProxy();
/* 118 */     proxy.setCloneFieldsFrom(this);
/* 119 */     return proxy;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.DictionaryProxy
 * JD-Core Version:    0.6.0
 */