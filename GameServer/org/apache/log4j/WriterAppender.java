/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.FilterWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.QuietWriter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class WriterAppender extends AppenderSkeleton
/*     */ {
/*  55 */   protected boolean immediateFlush = true;
/*     */   protected String encoding;
/*     */   protected QuietWriter qw;
/*     */ 
/*     */   public WriterAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WriterAppender(Layout layout, OutputStream os)
/*     */   {
/*  83 */     this(layout, new OutputStreamWriter(os));
/*     */   }
/*     */ 
/*     */   public WriterAppender(Layout layout, Writer writer)
/*     */   {
/*  94 */     this.layout = layout;
/*  95 */     setWriter(writer);
/*     */   }
/*     */ 
/*     */   public void setImmediateFlush(boolean value)
/*     */   {
/* 114 */     this.immediateFlush = value;
/*     */   }
/*     */ 
/*     */   public boolean getImmediateFlush()
/*     */   {
/* 122 */     return this.immediateFlush;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 157 */     if (!checkEntryConditions()) {
/* 158 */       return;
/*     */     }
/* 160 */     subAppend(event);
/*     */   }
/*     */ 
/*     */   protected boolean checkEntryConditions()
/*     */   {
/* 171 */     if (this.closed) {
/* 172 */       LogLog.warn("Not allowed to write to a closed appender.");
/* 173 */       return false;
/*     */     }
/*     */ 
/* 176 */     if (this.qw == null) {
/* 177 */       this.errorHandler.error("No output stream or file set for the appender named [" + this.name + "].");
/*     */ 
/* 179 */       return false;
/*     */     }
/*     */ 
/* 182 */     if (this.layout == null) {
/* 183 */       this.errorHandler.error("No layout set for the appender named [" + this.name + "].");
/* 184 */       return false;
/*     */     }
/* 186 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 201 */     if (this.closed)
/* 202 */       return;
/* 203 */     this.closed = true;
/* 204 */     writeFooter();
/* 205 */     reset();
/*     */   }
/*     */ 
/*     */   protected void closeWriter()
/*     */   {
/* 212 */     if (this.qw != null)
/*     */       try {
/* 214 */         this.qw.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 218 */         LogLog.error("Could not close " + this.qw, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected OutputStreamWriter createWriter(OutputStream os)
/*     */   {
/* 231 */     OutputStreamWriter retval = null;
/*     */ 
/* 233 */     String enc = getEncoding();
/* 234 */     if (enc != null) {
/*     */       try {
/* 236 */         retval = new OutputStreamWriter(os, enc);
/*     */       } catch (IOException e) {
/* 238 */         LogLog.warn("Error initializing output writer.");
/* 239 */         LogLog.warn("Unsupported encoding?");
/*     */       }
/*     */     }
/* 242 */     if (retval == null) {
/* 243 */       retval = new OutputStreamWriter(os);
/*     */     }
/* 245 */     return retval;
/*     */   }
/*     */ 
/*     */   public String getEncoding() {
/* 249 */     return this.encoding;
/*     */   }
/*     */ 
/*     */   public void setEncoding(String value) {
/* 253 */     this.encoding = value;
/*     */   }
/*     */ 
/*     */   public synchronized void setErrorHandler(ErrorHandler eh)
/*     */   {
/* 263 */     if (eh == null) {
/* 264 */       LogLog.warn("You have tried to set a null error-handler.");
/*     */     } else {
/* 266 */       this.errorHandler = eh;
/* 267 */       if (this.qw != null)
/* 268 */         this.qw.setErrorHandler(eh);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setWriter(Writer writer)
/*     */   {
/* 286 */     reset();
/* 287 */     this.qw = new QuietWriter(writer, this.errorHandler);
/*     */ 
/* 289 */     writeHeader();
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 302 */     this.qw.write(this.layout.format(event));
/*     */ 
/* 304 */     if (this.layout.ignoresThrowable()) {
/* 305 */       String[] s = event.getThrowableStrRep();
/* 306 */       if (s != null) {
/* 307 */         int len = s.length;
/* 308 */         for (int i = 0; i < len; i++) {
/* 309 */           this.qw.write(s[i]);
/* 310 */           this.qw.write(Layout.LINE_SEP);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 315 */     if (this.immediateFlush)
/* 316 */       this.qw.flush();
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 328 */     return true;
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 338 */     closeWriter();
/* 339 */     this.qw = null;
/*     */   }
/*     */ 
/*     */   protected void writeFooter()
/*     */   {
/* 349 */     if (this.layout != null) {
/* 350 */       String f = this.layout.getFooter();
/* 351 */       if ((f != null) && (this.qw != null)) {
/* 352 */         this.qw.write(f);
/* 353 */         this.qw.flush();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeHeader()
/*     */   {
/* 363 */     if (this.layout != null) {
/* 364 */       String h = this.layout.getHeader();
/* 365 */       if ((h != null) && (this.qw != null))
/* 366 */         this.qw.write(h);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.WriterAppender
 * JD-Core Version:    0.6.0
 */