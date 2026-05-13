/*     */ package org.apache.log4j.nt;
/*     */ 
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.TTCCLayout;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class NTEventLogAppender extends AppenderSkeleton
/*     */ {
/*  40 */   private int _handle = 0;
/*     */ 
/*  42 */   private String source = null;
/*  43 */   private String server = null;
/*     */ 
/*     */   public NTEventLogAppender()
/*     */   {
/*  47 */     this(null, null, null);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String source) {
/*  51 */     this(null, source, null);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String server, String source) {
/*  55 */     this(server, source, null);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(Layout layout) {
/*  59 */     this(null, null, layout);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String source, Layout layout) {
/*  63 */     this(null, source, layout);
/*     */   }
/*     */ 
/*     */   public NTEventLogAppender(String server, String source, Layout layout) {
/*  67 */     if (source == null) {
/*  68 */       source = "Log4j";
/*     */     }
/*  70 */     if (layout == null)
/*  71 */       this.layout = new TTCCLayout();
/*     */     else {
/*  73 */       this.layout = layout;
/*     */     }
/*     */     try
/*     */     {
/*  77 */       this._handle = registerEventSource(server, source);
/*     */     } catch (Exception e) {
/*  79 */       e.printStackTrace();
/*  80 */       this._handle = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*  91 */     if (this.source != null)
/*     */       try {
/*  93 */         this._handle = registerEventSource(this.server, this.source);
/*     */       } catch (Exception e) {
/*  95 */         LogLog.error("Could not register event source.", e);
/*  96 */         this._handle = 0;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 104 */     StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 106 */     sbuf.append(this.layout.format(event));
/* 107 */     if (this.layout.ignoresThrowable()) {
/* 108 */       String[] s = event.getThrowableStrRep();
/* 109 */       if (s != null) {
/* 110 */         int len = s.length;
/* 111 */         for (int i = 0; i < len; i++) {
/* 112 */           sbuf.append(s[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 117 */     int nt_category = event.getLevel().toInt();
/*     */ 
/* 123 */     reportEvent(this._handle, sbuf.toString(), nt_category);
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 129 */     deregisterEventSource(this._handle);
/* 130 */     this._handle = 0;
/*     */   }
/*     */ 
/*     */   public void setSource(String source)
/*     */   {
/* 139 */     this.source = source.trim();
/*     */   }
/*     */ 
/*     */   public String getSource()
/*     */   {
/* 144 */     return this.source;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 152 */     return true; } 
/*     */   private native int registerEventSource(String paramString1, String paramString2);
/*     */ 
/*     */   private native void reportEvent(int paramInt1, String paramString, int paramInt2);
/*     */ 
/*     */   private native void deregisterEventSource(int paramInt);
/*     */ 
/* 160 */   static { System.loadLibrary("NTEventLogAppender");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.nt.NTEventLogAppender
 * JD-Core Version:    0.6.0
 */