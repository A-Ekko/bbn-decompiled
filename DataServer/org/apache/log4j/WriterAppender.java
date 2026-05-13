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
/*  45 */   protected boolean immediateFlush = true;
/*     */   protected String encoding;
/*     */   protected QuietWriter qw;
/*     */ 
/*     */   public WriterAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WriterAppender(Layout layout, OutputStream os)
/*     */   {
/*  73 */     this(layout, new OutputStreamWriter(os));
/*     */   }
/*     */ 
/*     */   public WriterAppender(Layout layout, Writer writer)
/*     */   {
/*  84 */     this.layout = layout;
/*  85 */     setWriter(writer);
/*     */   }
/*     */ 
/*     */   public void setImmediateFlush(boolean value)
/*     */   {
/* 104 */     this.immediateFlush = value;
/*     */   }
/*     */ 
/*     */   public boolean getImmediateFlush()
/*     */   {
/* 112 */     return this.immediateFlush;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 147 */     if (!checkEntryConditions()) {
/* 148 */       return;
/*     */     }
/* 150 */     subAppend(event);
/*     */   }
/*     */ 
/*     */   protected boolean checkEntryConditions()
/*     */   {
/* 161 */     if (this.closed) {
/* 162 */       LogLog.warn("Not allowed to write to a closed appender.");
/* 163 */       return false;
/*     */     }
/*     */ 
/* 166 */     if (this.qw == null) {
/* 167 */       this.errorHandler.error("No output stream or file set for the appender named [" + this.name + "].");
/*     */ 
/* 169 */       return false;
/*     */     }
/*     */ 
/* 172 */     if (this.layout == null) {
/* 173 */       this.errorHandler.error("No layout set for the appender named [" + this.name + "].");
/* 174 */       return false;
/*     */     }
/* 176 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 191 */     if (this.closed)
/* 192 */       return;
/* 193 */     this.closed = true;
/* 194 */     writeFooter();
/* 195 */     reset();
/*     */   }
/*     */ 
/*     */   protected void closeWriter()
/*     */   {
/* 202 */     if (this.qw != null)
/*     */       try {
/* 204 */         this.qw.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 208 */         LogLog.error("Could not close " + this.qw, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected OutputStreamWriter createWriter(OutputStream os)
/*     */   {
/* 221 */     OutputStreamWriter retval = null;
/*     */ 
/* 223 */     String enc = getEncoding();
/* 224 */     if (enc != null) {
/*     */       try {
/* 226 */         retval = new OutputStreamWriter(os, enc);
/*     */       } catch (IOException e) {
/* 228 */         LogLog.warn("Error initializing output writer.");
/* 229 */         LogLog.warn("Unsupported encoding?");
/*     */       }
/*     */     }
/* 232 */     if (retval == null) {
/* 233 */       retval = new OutputStreamWriter(os);
/*     */     }
/* 235 */     return retval;
/*     */   }
/*     */ 
/*     */   public String getEncoding() {
/* 239 */     return this.encoding;
/*     */   }
/*     */ 
/*     */   public void setEncoding(String value) {
/* 243 */     this.encoding = value;
/*     */   }
/*     */ 
/*     */   public synchronized void setErrorHandler(ErrorHandler eh)
/*     */   {
/* 253 */     if (eh == null) {
/* 254 */       LogLog.warn("You have tried to set a null error-handler.");
/*     */     } else {
/* 256 */       this.errorHandler = eh;
/* 257 */       if (this.qw != null)
/* 258 */         this.qw.setErrorHandler(eh);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setWriter(Writer writer)
/*     */   {
/* 276 */     reset();
/* 277 */     this.qw = new QuietWriter(writer, this.errorHandler);
/*     */ 
/* 279 */     writeHeader();
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 292 */     this.qw.write(this.layout.format(event));
/*     */ 
/* 294 */     if (this.layout.ignoresThrowable()) {
/* 295 */       String[] s = event.getThrowableStrRep();
/* 296 */       if (s != null) {
/* 297 */         int len = s.length;
/* 298 */         for (int i = 0; i < len; i++) {
/* 299 */           this.qw.write(s[i]);
/* 300 */           this.qw.write(Layout.LINE_SEP);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 305 */     if (this.immediateFlush)
/* 306 */       this.qw.flush();
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 318 */     return true;
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 328 */     closeWriter();
/* 329 */     this.qw = null;
/*     */   }
/*     */ 
/*     */   protected void writeFooter()
/*     */   {
/* 339 */     if (this.layout != null) {
/* 340 */       String f = this.layout.getFooter();
/* 341 */       if ((f != null) && (this.qw != null)) {
/* 342 */         this.qw.write(f);
/* 343 */         this.qw.flush();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeHeader()
/*     */   {
/* 353 */     if (this.layout != null) {
/* 354 */       String h = this.layout.getHeader();
/* 355 */       if ((h != null) && (this.qw != null))
/* 356 */         this.qw.write(h);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.WriterAppender
 * JD-Core Version:    0.6.0
 */