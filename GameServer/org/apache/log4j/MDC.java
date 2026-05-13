/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.ThreadLocalMap;
/*     */ 
/*     */ public class MDC
/*     */ {
/*  45 */   static final MDC mdc = new MDC();
/*     */   static final int HT_SIZE = 7;
/*     */   boolean java1;
/*     */   Object tlm;
/*     */ 
/*     */   private MDC()
/*     */   {
/*  55 */     this.java1 = Loader.isJava1();
/*  56 */     if (!this.java1)
/*  57 */       this.tlm = new ThreadLocalMap();
/*     */   }
/*     */ 
/*     */   public static void put(String key, Object o)
/*     */   {
/*  73 */     if (mdc != null)
/*  74 */       mdc.put0(key, o);
/*     */   }
/*     */ 
/*     */   public static Object get(String key)
/*     */   {
/*  86 */     if (mdc != null) {
/*  87 */       return mdc.get0(key);
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   public static void remove(String key)
/*     */   {
/* 100 */     if (mdc != null)
/* 101 */       mdc.remove0(key);
/*     */   }
/*     */ 
/*     */   public static Hashtable getContext()
/*     */   {
/* 111 */     if (mdc != null) {
/* 112 */       return mdc.getContext0();
/*     */     }
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   private void put0(String key, Object o)
/*     */   {
/* 121 */     if ((this.java1) || (this.tlm == null)) {
/* 122 */       return;
/*     */     }
/* 124 */     Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 125 */     if (ht == null) {
/* 126 */       ht = new Hashtable(7);
/* 127 */       ((ThreadLocalMap)this.tlm).set(ht);
/*     */     }
/* 129 */     ht.put(key, o);
/*     */   }
/*     */ 
/*     */   private Object get0(String key)
/*     */   {
/* 135 */     if ((this.java1) || (this.tlm == null)) {
/* 136 */       return null;
/*     */     }
/* 138 */     Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 139 */     if ((ht != null) && (key != null)) {
/* 140 */       return ht.get(key);
/*     */     }
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   private void remove0(String key)
/*     */   {
/* 149 */     if ((!this.java1) && (this.tlm != null)) {
/* 150 */       Hashtable ht = (Hashtable)((ThreadLocalMap)this.tlm).get();
/* 151 */       if (ht != null)
/* 152 */         ht.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Hashtable getContext0()
/*     */   {
/* 160 */     if ((this.java1) || (this.tlm == null)) {
/* 161 */       return null;
/*     */     }
/* 163 */     return (Hashtable)((ThreadLocalMap)this.tlm).get();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.MDC
 * JD-Core Version:    0.6.0
 */