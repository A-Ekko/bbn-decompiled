/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.spi.AppenderAttachable;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class AppenderAttachableImpl
/*     */   implements AppenderAttachable
/*     */ {
/*     */   protected Vector appenderList;
/*     */ 
/*     */   public void addAppender(Appender newAppender)
/*     */   {
/*  36 */     if (newAppender == null) {
/*  37 */       return;
/*     */     }
/*  39 */     if (this.appenderList == null) {
/*  40 */       this.appenderList = new Vector(1);
/*     */     }
/*  42 */     if (!this.appenderList.contains(newAppender))
/*  43 */       this.appenderList.addElement(newAppender);
/*     */   }
/*     */ 
/*     */   public int appendLoopOnAppenders(LoggingEvent event)
/*     */   {
/*  50 */     int size = 0;
/*     */ 
/*  53 */     if (this.appenderList != null) {
/*  54 */       size = this.appenderList.size();
/*  55 */       for (int i = 0; i < size; i++) {
/*  56 */         Appender appender = (Appender)this.appenderList.elementAt(i);
/*  57 */         appender.doAppend(event);
/*     */       }
/*     */     }
/*  60 */     return size;
/*     */   }
/*     */ 
/*     */   public Enumeration getAllAppenders()
/*     */   {
/*  72 */     if (this.appenderList == null) {
/*  73 */       return null;
/*     */     }
/*  75 */     return this.appenderList.elements();
/*     */   }
/*     */ 
/*     */   public Appender getAppender(String name)
/*     */   {
/*  87 */     if ((this.appenderList == null) || (name == null)) {
/*  88 */       return null;
/*     */     }
/*  90 */     int size = this.appenderList.size();
/*     */ 
/*  92 */     for (int i = 0; i < size; i++) {
/*  93 */       Appender appender = (Appender)this.appenderList.elementAt(i);
/*  94 */       if (name.equals(appender.getName()))
/*  95 */         return appender;
/*     */     }
/*  97 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isAttached(Appender appender)
/*     */   {
/* 108 */     if ((this.appenderList == null) || (appender == null)) {
/* 109 */       return false;
/*     */     }
/* 111 */     int size = this.appenderList.size();
/*     */ 
/* 113 */     for (int i = 0; i < size; i++) {
/* 114 */       Appender a = (Appender)this.appenderList.elementAt(i);
/* 115 */       if (a == appender)
/* 116 */         return true;
/*     */     }
/* 118 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAllAppenders()
/*     */   {
/* 128 */     if (this.appenderList != null) {
/* 129 */       int len = this.appenderList.size();
/* 130 */       for (int i = 0; i < len; i++) {
/* 131 */         Appender a = (Appender)this.appenderList.elementAt(i);
/* 132 */         a.close();
/*     */       }
/* 134 */       this.appenderList.removeAllElements();
/* 135 */       this.appenderList = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAppender(Appender appender)
/*     */   {
/* 145 */     if ((appender == null) || (this.appenderList == null))
/* 146 */       return;
/* 147 */     this.appenderList.removeElement(appender);
/*     */   }
/*     */ 
/*     */   public void removeAppender(String name)
/*     */   {
/* 157 */     if ((name == null) || (this.appenderList == null)) return;
/* 158 */     int size = this.appenderList.size();
/* 159 */     for (int i = 0; i < size; i++)
/* 160 */       if (name.equals(((Appender)this.appenderList.elementAt(i)).getName())) {
/* 161 */         this.appenderList.removeElementAt(i);
/* 162 */         break;
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.AppenderAttachableImpl
 * JD-Core Version:    0.6.0
 */