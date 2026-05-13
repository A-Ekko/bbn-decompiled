/*     */ package org.logicalcobwebs.proxool.util;
/*     */ 
/*     */ public abstract class AbstractListenerContainer
/*     */   implements ListenerContainerIF
/*     */ {
/*  36 */   private Object[] listeners = EMPTY_LISTENERS;
/*  37 */   private static final Object[] EMPTY_LISTENERS = new Object[0];
/*     */ 
/*     */   public synchronized void addListener(Object listener)
/*     */   {
/*  45 */     if (listener == null) {
/*  46 */       throw new NullPointerException("Unexpected NULL listener argument received");
/*     */     }
/*     */ 
/*  49 */     Object[] newListeners = new Object[this.listeners.length + 1];
/*     */ 
/*  52 */     System.arraycopy(this.listeners, 0, newListeners, 0, this.listeners.length);
/*     */ 
/*  55 */     newListeners[this.listeners.length] = listener;
/*     */ 
/*  58 */     this.listeners = newListeners;
/*     */   }
/*     */ 
/*     */   public synchronized boolean removeListener(Object listener)
/*     */   {
/*  67 */     if (listener == null) {
/*  68 */       throw new NullPointerException("Unexpected NULL listener argument received");
/*     */     }
/*     */ 
/*  71 */     int index = -1;
/*  72 */     for (int i = 0; i < this.listeners.length; i++) {
/*  73 */       if (this.listeners[i] == listener) {
/*  74 */         index = i;
/*  75 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  80 */     if (index == -1) {
/*  81 */       return false;
/*     */     }
/*     */ 
/*  84 */     Object[] newListeners = new Object[this.listeners.length - 1];
/*     */ 
/*  87 */     if (index > 0) {
/*  88 */       System.arraycopy(this.listeners, 0, newListeners, 0, index);
/*     */     }
/*  90 */     if (index < this.listeners.length - 1) {
/*  91 */       System.arraycopy(this.listeners, index + 1, newListeners, index, this.listeners.length - index - 1);
/*     */     }
/*     */ 
/*  94 */     this.listeners = newListeners;
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   protected Object[] getListeners()
/*     */   {
/* 105 */     return this.listeners;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 113 */     return this.listeners.length == 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.util.AbstractListenerContainer
 * JD-Core Version:    0.6.0
 */