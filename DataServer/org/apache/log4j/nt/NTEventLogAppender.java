/*     */ package org.apache.log4j.nt;
/*     */ 
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.TTCCLayout;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class NTEventLogAppender extends AppenderSkeleton
/*     */ {
/*  31 */   private int _handle = 0;
/*     */ 
/*  33 */   private String source = null;
/*  34 */   private String server = null;
/*     */ 
/*  36 */   private static final int FATAL = Level.FATAL.toInt();
/*  37 */   private static final int ERROR = Level.ERROR.toInt();
/*  38 */   private static final int WARN = Level.WARN.toInt();
/*  39 */   private static final int INFO = Level.INFO.toInt();
/*  40 */   private static final int DEBUG = Level.DEBUG.toInt();
/*     */ 
/*     */   public NTEventLogAppender() {
/*  43 */     this(null, null, null);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String source) {
/*  47 */     this(null, source, null);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String server, String source) {
/*  51 */     this(server, source, null);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(Layout layout) {
/*  55 */     this(null, null, layout);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String source, Layout layout) {
/*  59 */     this(null, source, layout);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String server, String source, Layout layout) {
/*  63 */     if (source == null) {
/*  64 */       source = "Log4j";
/*     */     }
/*  66 */     if (layout == null)
/*  67 */       this.layout = new TTCCLayout();
/*     */     else {
/*  69 */       this.layout = layout;
/*     */     }
/*     */     try
/*     */     {
/*  73 */       this._handle = registerEventSource(server, source);
/*     */     } catch (Exception e) {
/*  75 */       e.printStackTrace();
/*  76 */       this._handle = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*  87 */     if (this.source != null)
/*     */       try {
/*  89 */         this._handle = registerEventSource(this.server, this.source);
/*     */       } catch (Exception e) {
/*  91 */         LogLog.error("Could not register event source.", e);
/*  92 */         this._handle = 0;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 100 */     StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 102 */     sbuf.append(this.layout.format(event));
/* 103 */     if (this.layout.ignoresThrowable()) {
/* 104 */       String[] s = event.getThrowableStrRep();
/* 105 */       if (s != null) {
/* 106 */         int len = s.length;
/* 107 */         for (int i = 0; i < len; i++) {
/* 108 */           sbuf.append(s[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 113 */     int nt_category = event.getLevel().toInt();
/*     */ 
/* 119 */     reportEvent(this._handle, sbuf.toString(), nt_category);
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 125 */     deregisterEventSource(this._handle);
/* 126 */     this._handle = 0;
/*     */   }
/*     */ 
/*     */   public void setSource(String source)
/*     */   {
/* 135 */     this.source = source.trim();
/*     */   }
/*     */ 
/*     */   public String getSource()
/*     */   {
/* 140 */     return this.source;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 148 */     return true; } 
/*     */   private native int registerEventSource(String paramString1, String paramString2);
/*     */ 
/*     */   private native void reportEvent(int paramInt1, String paramString, int paramInt2);
/*     */ 
/*     */   private native void deregisterEventSource(int paramInt);
/*     */ 
/* 156 */   static { System.loadLibrary("NTEventLogAppender");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.nt.NTEventLogAppender
 * JD-Core Version:    0.6.0
 */