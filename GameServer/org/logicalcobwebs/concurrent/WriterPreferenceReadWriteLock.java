/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ public class WriterPreferenceReadWriteLock
/*     */   implements ReadWriteLock
/*     */ {
/*     */   protected long activeReaders_;
/*     */   protected Thread activeWriter_;
/*     */   protected long waitingReaders_;
/*     */   protected long waitingWriters_;
/*     */   protected final ReaderLock readerLock_;
/*     */   protected final WriterLock writerLock_;
/*     */ 
/*     */   public WriterPreferenceReadWriteLock()
/*     */   {
/*  37 */     this.activeReaders_ = 0L;
/*  38 */     this.activeWriter_ = null;
/*  39 */     this.waitingReaders_ = 0L;
/*  40 */     this.waitingWriters_ = 0L;
/*     */ 
/*  43 */     this.readerLock_ = new ReaderLock();
/*  44 */     this.writerLock_ = new WriterLock();
/*     */   }
/*     */   public Sync writeLock() {
/*  47 */     return this.writerLock_;
/*     */   }
/*     */ 
/*     */   public Sync readLock() {
/*  51 */     return this.readerLock_;
/*     */   }
/*     */ 
/*     */   protected synchronized void cancelledWaitingReader()
/*     */   {
/*  62 */     this.waitingReaders_ -= 1L;
/*     */   }
/*     */ 
/*     */   protected synchronized void cancelledWaitingWriter() {
/*  66 */     this.waitingWriters_ -= 1L;
/*     */   }
/*     */ 
/*     */   protected boolean allowReader()
/*     */   {
/*  72 */     return (this.activeWriter_ == null) && (this.waitingWriters_ == 0L);
/*     */   }
/*     */ 
/*     */   protected synchronized boolean startRead()
/*     */   {
/*  77 */     boolean allowRead = allowReader();
/*  78 */     if (allowRead) this.activeReaders_ += 1L;
/*  79 */     return allowRead;
/*     */   }
/*     */ 
/*     */   protected synchronized boolean startWrite()
/*     */   {
/*  87 */     boolean allowWrite = (this.activeWriter_ == null) && (this.activeReaders_ == 0L);
/*  88 */     if (allowWrite) this.activeWriter_ = Thread.currentThread();
/*  89 */     return allowWrite;
/*     */   }
/*     */ 
/*     */   protected synchronized boolean startReadFromNewReader()
/*     */   {
/* 101 */     boolean pass = startRead();
/* 102 */     if (!pass) this.waitingReaders_ += 1L;
/* 103 */     return pass;
/*     */   }
/*     */ 
/*     */   protected synchronized boolean startWriteFromNewWriter() {
/* 107 */     boolean pass = startWrite();
/* 108 */     if (!pass) this.waitingWriters_ += 1L;
/* 109 */     return pass;
/*     */   }
/*     */ 
/*     */   protected synchronized boolean startReadFromWaitingReader() {
/* 113 */     boolean pass = startRead();
/* 114 */     if (pass) this.waitingReaders_ -= 1L;
/* 115 */     return pass;
/*     */   }
/*     */ 
/*     */   protected synchronized boolean startWriteFromWaitingWriter() {
/* 119 */     boolean pass = startWrite();
/* 120 */     if (pass) this.waitingWriters_ -= 1L;
/* 121 */     return pass;
/*     */   }
/*     */ 
/*     */   protected synchronized Signaller endRead()
/*     */   {
/* 129 */     if ((--this.activeReaders_ == 0L) && (this.waitingWriters_ > 0L)) {
/* 130 */       return this.writerLock_;
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   protected synchronized Signaller endWrite()
/*     */   {
/* 141 */     this.activeWriter_ = null;
/* 142 */     if ((this.waitingReaders_ > 0L) && (allowReader()))
/* 143 */       return this.readerLock_;
/* 144 */     if (this.waitingWriters_ > 0L) {
/* 145 */       return this.writerLock_;
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   protected class WriterLock extends WriterPreferenceReadWriteLock.Signaller
/*     */     implements Sync
/*     */   {
/*     */     protected WriterLock()
/*     */     {
/* 245 */       super();
/*     */     }
/*     */     public void acquire() throws InterruptedException {
/* 248 */       if (Thread.interrupted()) throw new InterruptedException();
/* 249 */       InterruptedException ie = null;
/* 250 */       synchronized (this) {
/* 251 */         if (!WriterPreferenceReadWriteLock.this.startWriteFromNewWriter()) {
/*     */           try {
/*     */             while (true) {
/* 254 */               wait();
/* 255 */               if (WriterPreferenceReadWriteLock.this.startWriteFromWaitingWriter())
/* 256 */                 return; 
/*     */             }
/*     */           } catch (InterruptedException ex) {
/* 258 */             WriterPreferenceReadWriteLock.this.cancelledWaitingWriter();
/* 259 */             notify();
/* 260 */             ie = ex;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 266 */       if (ie != null)
/*     */       {
/* 270 */         WriterPreferenceReadWriteLock.this.readerLock_.signalWaiters();
/* 271 */         throw ie;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void release() {
/* 276 */       WriterPreferenceReadWriteLock.Signaller s = WriterPreferenceReadWriteLock.this.endWrite();
/* 277 */       if (s != null) s.signalWaiters(); 
/*     */     }
/*     */ 
/*     */     synchronized void signalWaiters()
/*     */     {
/* 281 */       notify();
/*     */     }
/*     */ 
/*     */     public boolean attempt(long msecs) throws InterruptedException {
/* 285 */       if (Thread.interrupted()) throw new InterruptedException();
/* 286 */       InterruptedException ie = null;
/* 287 */       synchronized (this) {
/* 288 */         if (msecs <= 0L)
/* 289 */           return WriterPreferenceReadWriteLock.this.startWrite();
/* 290 */         if (WriterPreferenceReadWriteLock.this.startWriteFromNewWriter()) {
/* 291 */           return true;
/*     */         }
/* 293 */         long waitTime = msecs;
/* 294 */         long start = System.currentTimeMillis();
/*     */         while (true) {
/*     */           try {
/* 297 */             wait(waitTime);
/*     */           } catch (InterruptedException ex) {
/* 299 */             WriterPreferenceReadWriteLock.this.cancelledWaitingWriter();
/* 300 */             notify();
/* 301 */             ie = ex;
/* 302 */             break;
/*     */           }
/* 304 */           if (WriterPreferenceReadWriteLock.this.startWriteFromWaitingWriter()) {
/* 305 */             return true;
/*     */           }
/* 307 */           waitTime = msecs - (System.currentTimeMillis() - start);
/* 308 */           if (waitTime <= 0L) {
/* 309 */             WriterPreferenceReadWriteLock.this.cancelledWaitingWriter();
/* 310 */             notify();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 318 */       WriterPreferenceReadWriteLock.this.readerLock_.signalWaiters();
/* 319 */       if (ie != null) {
/* 320 */         throw ie;
/*     */       }
/* 322 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class ReaderLock extends WriterPreferenceReadWriteLock.Signaller
/*     */     implements Sync
/*     */   {
/*     */     protected ReaderLock()
/*     */     {
/* 164 */       super();
/*     */     }
/*     */     public void acquire() throws InterruptedException {
/* 167 */       if (Thread.interrupted()) throw new InterruptedException();
/* 168 */       InterruptedException ie = null;
/* 169 */       synchronized (this) {
/* 170 */         if (!WriterPreferenceReadWriteLock.this.startReadFromNewReader()) {
/*     */           try {
/*     */             while (true) {
/* 173 */               wait();
/* 174 */               if (WriterPreferenceReadWriteLock.this.startReadFromWaitingReader())
/* 175 */                 return; 
/*     */             }
/*     */           } catch (InterruptedException ex) {
/* 177 */             WriterPreferenceReadWriteLock.this.cancelledWaitingReader();
/* 178 */             ie = ex;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 184 */       if (ie != null)
/*     */       {
/* 188 */         WriterPreferenceReadWriteLock.this.writerLock_.signalWaiters();
/* 189 */         throw ie;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void release()
/*     */     {
/* 195 */       WriterPreferenceReadWriteLock.Signaller s = WriterPreferenceReadWriteLock.this.endRead();
/* 196 */       if (s != null) s.signalWaiters();
/*     */     }
/*     */ 
/*     */     synchronized void signalWaiters()
/*     */     {
/* 201 */       notifyAll();
/*     */     }
/*     */ 
/*     */     public boolean attempt(long msecs) throws InterruptedException {
/* 205 */       if (Thread.interrupted()) throw new InterruptedException();
/* 206 */       InterruptedException ie = null;
/* 207 */       synchronized (this) {
/* 208 */         if (msecs <= 0L)
/* 209 */           return WriterPreferenceReadWriteLock.this.startRead();
/* 210 */         if (WriterPreferenceReadWriteLock.this.startReadFromNewReader()) {
/* 211 */           return true;
/*     */         }
/* 213 */         long waitTime = msecs;
/* 214 */         long start = System.currentTimeMillis();
/*     */         while (true) {
/*     */           try {
/* 217 */             wait(waitTime);
/*     */           } catch (InterruptedException ex) {
/* 219 */             WriterPreferenceReadWriteLock.this.cancelledWaitingReader();
/* 220 */             ie = ex;
/* 221 */             break;
/*     */           }
/* 223 */           if (WriterPreferenceReadWriteLock.this.startReadFromWaitingReader()) {
/* 224 */             return true;
/*     */           }
/* 226 */           waitTime = msecs - (System.currentTimeMillis() - start);
/* 227 */           if (waitTime <= 0L) {
/* 228 */             WriterPreferenceReadWriteLock.this.cancelledWaitingReader();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 236 */       WriterPreferenceReadWriteLock.this.writerLock_.signalWaiters();
/* 237 */       if (ie != null) {
/* 238 */         throw ie;
/*     */       }
/* 240 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract class Signaller
/*     */   {
/*     */     protected Signaller()
/*     */     {
/*     */     }
/*     */ 
/*     */     abstract void signalWaiters();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.WriterPreferenceReadWriteLock
 * JD-Core Version:    0.6.0
 */