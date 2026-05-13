/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.ThreadLocalMap;
/*     */ 
/*     */ public class MDC
/*     */ {
/*  36 */   static final MDC mdc = new MDC();
/*     */   static final int HT_SIZE = 7;
/*     */   boolean java1;
/*     */   Object tlm;
/*     */ 
/*     */   private MDC()
/*     */   {
/*  46 */     this.java1 = Loader.isJava1();
/*  47 */     if (!this.java1)
/*  48 */       this.tlm = new ThreadLocalMap();
/*     */   }
/*     */ 
/*     */   public static void put(String key, Object o)
/*     */   {
/*  64 */     mdc.put0(key, o);
/*     */   }
/*     */ 
/*     */   public static Object get(String key)
/*     */   {
/*  75 */     return mdc.get0(key);
/*     */   }
/*     */ 
/*     */   public static void remove(String key)
/*     */   {
/*  86 */     mdc.remove0(key);
/*     */   }
/*     */ 
/*     */   public static Hashtable getContext()
/*     */   {
/*  95 */     return mdc.getContext0();
/*     */   }
/*     */ 
/*     */   private void put0(String key, Object o)
/*     */   {
/* 101 */     if (this.java1) {
/* 102 */       return;
/*     */     }
/* 104 */     Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 105 */     if (ht == null) {
/* 106 */       ht = new Hashtable(7);
/* 107 */       ((ThreadLocalMap)this.tlm).set(ht);
/*     */     }
/* 109 */     ht.put(key, o);
/*     */   }
/*     */ 
/*     */   private Object get0(String key)
/*     */   {
/* 115 */     if (this.java1) {
/* 116 */       return null;
/*     */     }
/* 118 */     Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 119 */     if ((ht != null) && (key != null)) {
/* 120 */       return ht.get(key);
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   private void remove0(String key)
/*     */   {
/* 129 */     if (!this.java1) {
/* 130 */       Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 131 */       if (ht != null)
/* 132 */         ht.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Hashtable getContext0()
/*     */   {
/* 140 */     if (this.java1) {
/* 141 */       return null;
/*     */     }
/* 143 */     return (Hashtable)((ThreadLocalMap)this.tlm).get();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.MDC
 * JD-Core Version:    0.6.0
 */