/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.Marker;
/*     */ 
/*     */ public abstract class MarkerIgnoringBase extends NamedLoggerBase
/*     */   implements Logger
/*     */ {
/*     */   public boolean isTraceEnabled(Marker marker)
/*     */   {
/*  42 */     return isTraceEnabled();
/*     */   }
/*     */ 
/*     */   public void trace(Marker marker, String msg) {
/*  46 */     trace(msg);
/*     */   }
/*     */ 
/*     */   public void trace(Marker marker, String format, Object arg) {
/*  50 */     trace(format, arg);
/*     */   }
/*     */ 
/*     */   public void trace(Marker marker, String format, Object arg1, Object arg2) {
/*  54 */     trace(format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void trace(Marker marker, String format, Object[] argArray) {
/*  58 */     trace(format, argArray);
/*     */   }
/*     */ 
/*     */   public void trace(Marker marker, String msg, Throwable t) {
/*  62 */     trace(msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled(Marker marker) {
/*  66 */     return isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public void debug(Marker marker, String msg) {
/*  70 */     debug(msg);
/*     */   }
/*     */ 
/*     */   public void debug(Marker marker, String format, Object arg) {
/*  74 */     debug(format, arg);
/*     */   }
/*     */ 
/*     */   public void debug(Marker marker, String format, Object arg1, Object arg2) {
/*  78 */     debug(format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void debug(Marker marker, String format, Object[] argArray) {
/*  82 */     debug(format, argArray);
/*     */   }
/*     */ 
/*     */   public void debug(Marker marker, String msg, Throwable t) {
/*  86 */     debug(msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled(Marker marker) {
/*  90 */     return isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public void info(Marker marker, String msg) {
/*  94 */     info(msg);
/*     */   }
/*     */ 
/*     */   public void info(Marker marker, String format, Object arg) {
/*  98 */     info(format, arg);
/*     */   }
/*     */ 
/*     */   public void info(Marker marker, String format, Object arg1, Object arg2) {
/* 102 */     info(format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void info(Marker marker, String format, Object[] argArray) {
/* 106 */     info(format, argArray);
/*     */   }
/*     */ 
/*     */   public void info(Marker marker, String msg, Throwable t) {
/* 110 */     info(msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled(Marker marker) {
/* 114 */     return isWarnEnabled();
/*     */   }
/*     */ 
/*     */   public void warn(Marker marker, String msg) {
/* 118 */     warn(msg);
/*     */   }
/*     */ 
/*     */   public void warn(Marker marker, String format, Object arg) {
/* 122 */     warn(format, arg);
/*     */   }
/*     */ 
/*     */   public void warn(Marker marker, String format, Object arg1, Object arg2) {
/* 126 */     warn(format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void warn(Marker marker, String format, Object[] argArray) {
/* 130 */     warn(format, argArray);
/*     */   }
/*     */ 
/*     */   public void warn(Marker marker, String msg, Throwable t) {
/* 134 */     warn(msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled(Marker marker)
/*     */   {
/* 139 */     return isErrorEnabled();
/*     */   }
/*     */ 
/*     */   public void error(Marker marker, String msg) {
/* 143 */     error(msg);
/*     */   }
/*     */ 
/*     */   public void error(Marker marker, String format, Object arg) {
/* 147 */     error(format, arg);
/*     */   }
/*     */ 
/*     */   public void error(Marker marker, String format, Object arg1, Object arg2) {
/* 151 */     error(format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void error(Marker marker, String format, Object[] argArray) {
/* 155 */     error(format, argArray);
/*     */   }
/*     */ 
/*     */   public void error(Marker marker, String msg, Throwable t) {
/* 159 */     error(msg, t);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 163 */     return getClass().getName() + "(" + getName() + ")";
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.helpers.MarkerIgnoringBase
 * JD-Core Version:    0.6.0
 */