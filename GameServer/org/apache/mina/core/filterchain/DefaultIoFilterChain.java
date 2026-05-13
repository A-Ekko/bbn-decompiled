/*     */ package org.apache.mina.core.filterchain;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.future.CloseFuture;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.service.IoHandler;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionAttributeMap;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestQueue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DefaultIoFilterChain
/*     */   implements IoFilterChain
/*     */ {
/*  55 */   public static final AttributeKey SESSION_CREATED_FUTURE = new AttributeKey(DefaultIoFilterChain.class, "connectFuture");
/*     */   private final AbstractIoSession session;
/*  61 */   private final Map<String, IoFilterChain.Entry> name2entry = new HashMap();
/*     */   private final EntryImpl head;
/*     */   private final EntryImpl tail;
/*  70 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */ 
/*     */   public DefaultIoFilterChain(AbstractIoSession session)
/*     */   {
/*  80 */     if (session == null) {
/*  81 */       throw new NullPointerException("session");
/*     */     }
/*     */ 
/*  84 */     this.session = session;
/*  85 */     this.head = new EntryImpl(null, null, "head", new HeadFilter(null), null);
/*  86 */     this.tail = new EntryImpl(this.head, null, "tail", new TailFilter(null), null);
/*  87 */     EntryImpl.access$302(this.head, this.tail);
/*     */   }
/*     */ 
/*     */   public IoSession getSession() {
/*  91 */     return this.session;
/*     */   }
/*     */ 
/*     */   public IoFilterChain.Entry getEntry(String name) {
/*  95 */     IoFilterChain.Entry e = (IoFilterChain.Entry)this.name2entry.get(name);
/*  96 */     if (e == null) {
/*  97 */       return null;
/*     */     }
/*  99 */     return e;
/*     */   }
/*     */ 
/*     */   public IoFilterChain.Entry getEntry(IoFilter filter) {
/* 103 */     EntryImpl e = this.head.nextEntry;
/* 104 */     while (e != this.tail) {
/* 105 */       if (e.getFilter() == filter) {
/* 106 */         return e;
/*     */       }
/* 108 */       e = e.nextEntry;
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */ 
/*     */   public IoFilterChain.Entry getEntry(Class<? extends IoFilter> filterType) {
/* 114 */     EntryImpl e = this.head.nextEntry;
/* 115 */     while (e != this.tail) {
/* 116 */       if (filterType.isAssignableFrom(e.getFilter().getClass())) {
/* 117 */         return e;
/*     */       }
/* 119 */       e = e.nextEntry;
/*     */     }
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   public IoFilter get(String name) {
/* 125 */     IoFilterChain.Entry e = getEntry(name);
/* 126 */     if (e == null) {
/* 127 */       return null;
/*     */     }
/*     */ 
/* 130 */     return e.getFilter();
/*     */   }
/*     */ 
/*     */   public IoFilter get(Class<? extends IoFilter> filterType) {
/* 134 */     IoFilterChain.Entry e = getEntry(filterType);
/* 135 */     if (e == null) {
/* 136 */       return null;
/*     */     }
/*     */ 
/* 139 */     return e.getFilter();
/*     */   }
/*     */ 
/*     */   public IoFilter.NextFilter getNextFilter(String name) {
/* 143 */     IoFilterChain.Entry e = getEntry(name);
/* 144 */     if (e == null) {
/* 145 */       return null;
/*     */     }
/*     */ 
/* 148 */     return e.getNextFilter();
/*     */   }
/*     */ 
/*     */   public IoFilter.NextFilter getNextFilter(IoFilter filter) {
/* 152 */     IoFilterChain.Entry e = getEntry(filter);
/* 153 */     if (e == null) {
/* 154 */       return null;
/*     */     }
/*     */ 
/* 157 */     return e.getNextFilter();
/*     */   }
/*     */ 
/*     */   public IoFilter.NextFilter getNextFilter(Class<? extends IoFilter> filterType) {
/* 161 */     IoFilterChain.Entry e = getEntry(filterType);
/* 162 */     if (e == null) {
/* 163 */       return null;
/*     */     }
/*     */ 
/* 166 */     return e.getNextFilter();
/*     */   }
/*     */ 
/*     */   public synchronized void addFirst(String name, IoFilter filter) {
/* 170 */     checkAddable(name);
/* 171 */     register(this.head, name, filter);
/*     */   }
/*     */ 
/*     */   public synchronized void addLast(String name, IoFilter filter) {
/* 175 */     checkAddable(name);
/* 176 */     register(this.tail.prevEntry, name, filter);
/*     */   }
/*     */ 
/*     */   public synchronized void addBefore(String baseName, String name, IoFilter filter)
/*     */   {
/* 181 */     EntryImpl baseEntry = checkOldName(baseName);
/* 182 */     checkAddable(name);
/* 183 */     register(baseEntry.prevEntry, name, filter);
/*     */   }
/*     */ 
/*     */   public synchronized void addAfter(String baseName, String name, IoFilter filter)
/*     */   {
/* 188 */     EntryImpl baseEntry = checkOldName(baseName);
/* 189 */     checkAddable(name);
/* 190 */     register(baseEntry, name, filter);
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter remove(String name) {
/* 194 */     EntryImpl entry = checkOldName(name);
/* 195 */     deregister(entry);
/* 196 */     return entry.getFilter();
/*     */   }
/*     */ 
/*     */   public synchronized void remove(IoFilter filter) {
/* 200 */     EntryImpl e = this.head.nextEntry;
/* 201 */     while (e != this.tail) {
/* 202 */       if (e.getFilter() == filter) {
/* 203 */         deregister(e);
/* 204 */         return;
/*     */       }
/* 206 */       e = e.nextEntry;
/*     */     }
/* 208 */     throw new IllegalArgumentException("Filter not found: " + filter.getClass().getName());
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter remove(Class<? extends IoFilter> filterType)
/*     */   {
/* 213 */     EntryImpl e = this.head.nextEntry;
/* 214 */     while (e != this.tail) {
/* 215 */       if (filterType.isAssignableFrom(e.getFilter().getClass())) {
/* 216 */         IoFilter oldFilter = e.getFilter();
/* 217 */         deregister(e);
/* 218 */         return oldFilter;
/*     */       }
/* 220 */       e = e.nextEntry;
/*     */     }
/* 222 */     throw new IllegalArgumentException("Filter not found: " + filterType.getName());
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter replace(String name, IoFilter newFilter)
/*     */   {
/* 227 */     EntryImpl entry = checkOldName(name);
/* 228 */     IoFilter oldFilter = entry.getFilter();
/* 229 */     entry.setFilter(newFilter);
/* 230 */     return oldFilter;
/*     */   }
/*     */ 
/*     */   public synchronized void replace(IoFilter oldFilter, IoFilter newFilter) {
/* 234 */     EntryImpl e = this.head.nextEntry;
/* 235 */     while (e != this.tail) {
/* 236 */       if (e.getFilter() == oldFilter) {
/* 237 */         e.setFilter(newFilter);
/* 238 */         return;
/*     */       }
/* 240 */       e = e.nextEntry;
/*     */     }
/* 242 */     throw new IllegalArgumentException("Filter not found: " + oldFilter.getClass().getName());
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter replace(Class<? extends IoFilter> oldFilterType, IoFilter newFilter)
/*     */   {
/* 248 */     EntryImpl e = this.head.nextEntry;
/* 249 */     while (e != this.tail) {
/* 250 */       if (oldFilterType.isAssignableFrom(e.getFilter().getClass())) {
/* 251 */         IoFilter oldFilter = e.getFilter();
/* 252 */         e.setFilter(newFilter);
/* 253 */         return oldFilter;
/*     */       }
/* 255 */       e = e.nextEntry;
/*     */     }
/* 257 */     throw new IllegalArgumentException("Filter not found: " + oldFilterType.getName());
/*     */   }
/*     */ 
/*     */   public synchronized void clear() throws Exception
/*     */   {
/* 262 */     Iterator it = new ArrayList(this.name2entry.keySet()).iterator();
/*     */ 
/* 264 */     while (it.hasNext()) {
/* 265 */       String name = (String)it.next();
/* 266 */       if (contains(name))
/* 267 */         remove(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void register(EntryImpl prevEntry, String name, IoFilter filter)
/*     */   {
/* 273 */     EntryImpl newEntry = new EntryImpl(prevEntry, prevEntry.nextEntry, name, filter, null);
/*     */     try
/*     */     {
/* 277 */       filter.onPreAdd(this, name, newEntry.getNextFilter());
/*     */     } catch (Exception e) {
/* 279 */       throw new IoFilterLifeCycleException("onPreAdd(): " + name + ':' + filter + " in " + getSession(), e);
/*     */     }
/*     */ 
/* 283 */     EntryImpl.access$402(prevEntry.nextEntry, newEntry);
/* 284 */     EntryImpl.access$302(prevEntry, newEntry);
/* 285 */     this.name2entry.put(name, newEntry);
/*     */     try
/*     */     {
/* 288 */       filter.onPostAdd(this, name, newEntry.getNextFilter());
/*     */     } catch (Exception e) {
/* 290 */       deregister0(newEntry);
/* 291 */       throw new IoFilterLifeCycleException("onPostAdd(): " + name + ':' + filter + " in " + getSession(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void deregister(EntryImpl entry)
/*     */   {
/* 297 */     IoFilter filter = entry.getFilter();
/*     */     try
/*     */     {
/* 300 */       filter.onPreRemove(this, entry.getName(), entry.getNextFilter());
/*     */     } catch (Exception e) {
/* 302 */       throw new IoFilterLifeCycleException("onPreRemove(): " + entry.getName() + ':' + filter + " in " + getSession(), e);
/*     */     }
/*     */ 
/* 306 */     deregister0(entry);
/*     */     try
/*     */     {
/* 309 */       filter.onPostRemove(this, entry.getName(), entry.getNextFilter());
/*     */     } catch (Exception e) {
/* 311 */       throw new IoFilterLifeCycleException("onPostRemove(): " + entry.getName() + ':' + filter + " in " + getSession(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void deregister0(EntryImpl entry)
/*     */   {
/* 317 */     EntryImpl prevEntry = entry.prevEntry;
/* 318 */     EntryImpl nextEntry = entry.nextEntry;
/* 319 */     EntryImpl.access$302(prevEntry, nextEntry);
/* 320 */     EntryImpl.access$402(nextEntry, prevEntry);
/*     */ 
/* 322 */     this.name2entry.remove(entry.name);
/*     */   }
/*     */ 
/*     */   private EntryImpl checkOldName(String baseName)
/*     */   {
/* 331 */     EntryImpl e = (EntryImpl)this.name2entry.get(baseName);
/* 332 */     if (e == null) {
/* 333 */       throw new IllegalArgumentException("Filter not found:" + baseName);
/*     */     }
/* 335 */     return e;
/*     */   }
/*     */ 
/*     */   private void checkAddable(String name)
/*     */   {
/* 342 */     if (this.name2entry.containsKey(name))
/* 343 */       throw new IllegalArgumentException("Other filter is using the same name '" + name + "'");
/*     */   }
/*     */ 
/*     */   public void fireSessionCreated()
/*     */   {
/* 349 */     IoFilterChain.Entry head = this.head;
/* 350 */     callNextSessionCreated(head, this.session);
/*     */   }
/*     */ 
/*     */   private void callNextSessionCreated(IoFilterChain.Entry entry, IoSession session) {
/*     */     try {
/* 355 */       IoFilter filter = entry.getFilter();
/* 356 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 357 */       filter.sessionCreated(nextFilter, session);
/*     */     } catch (Throwable e) {
/* 359 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireSessionOpened() {
/* 364 */     IoFilterChain.Entry head = this.head;
/* 365 */     callNextSessionOpened(head, this.session);
/*     */   }
/*     */ 
/*     */   private void callNextSessionOpened(IoFilterChain.Entry entry, IoSession session) {
/*     */     try {
/* 370 */       IoFilter filter = entry.getFilter();
/* 371 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 372 */       filter.sessionOpened(nextFilter, session);
/*     */     } catch (Throwable e) {
/* 374 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireSessionClosed()
/*     */   {
/*     */     try {
/* 381 */       this.session.getCloseFuture().setClosed();
/*     */     } catch (Throwable t) {
/* 383 */       fireExceptionCaught(t);
/*     */     }
/*     */ 
/* 387 */     IoFilterChain.Entry head = this.head;
/* 388 */     callNextSessionClosed(head, this.session);
/*     */   }
/*     */ 
/*     */   private void callNextSessionClosed(IoFilterChain.Entry entry, IoSession session) {
/*     */     try {
/* 393 */       IoFilter filter = entry.getFilter();
/* 394 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 395 */       filter.sessionClosed(nextFilter, session);
/*     */     } catch (Throwable e) {
/* 397 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireSessionIdle(IdleStatus status) {
/* 402 */     this.session.increaseIdleCount(status, System.currentTimeMillis());
/* 403 */     IoFilterChain.Entry head = this.head;
/* 404 */     callNextSessionIdle(head, this.session, status);
/*     */   }
/*     */ 
/*     */   private void callNextSessionIdle(IoFilterChain.Entry entry, IoSession session, IdleStatus status)
/*     */   {
/*     */     try {
/* 410 */       IoFilter filter = entry.getFilter();
/* 411 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 412 */       filter.sessionIdle(nextFilter, session, status);
/*     */     }
/*     */     catch (Throwable e) {
/* 415 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireMessageReceived(Object message) {
/* 420 */     if ((message instanceof IoBuffer)) {
/* 421 */       this.session.increaseReadBytes(((IoBuffer)message).remaining(), System.currentTimeMillis());
/*     */     }
/*     */ 
/* 425 */     IoFilterChain.Entry head = this.head;
/* 426 */     callNextMessageReceived(head, this.session, message);
/*     */   }
/*     */ 
/*     */   private void callNextMessageReceived(IoFilterChain.Entry entry, IoSession session, Object message)
/*     */   {
/*     */     try {
/* 432 */       IoFilter filter = entry.getFilter();
/* 433 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 434 */       filter.messageReceived(nextFilter, session, message);
/*     */     }
/*     */     catch (Throwable e) {
/* 437 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireMessageSent(WriteRequest request) {
/* 442 */     this.session.increaseWrittenMessages(request, System.currentTimeMillis());
/*     */     try
/*     */     {
/* 445 */       request.getFuture().setWritten();
/*     */     } catch (Throwable t) {
/* 447 */       fireExceptionCaught(t);
/*     */     }
/*     */ 
/* 450 */     IoFilterChain.Entry head = this.head;
/* 451 */     callNextMessageSent(head, this.session, request);
/*     */   }
/*     */ 
/*     */   private void callNextMessageSent(IoFilterChain.Entry entry, IoSession session, WriteRequest writeRequest)
/*     */   {
/*     */     try {
/* 457 */       IoFilter filter = entry.getFilter();
/* 458 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 459 */       filter.messageSent(nextFilter, session, writeRequest);
/*     */     }
/*     */     catch (Throwable e) {
/* 462 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireExceptionCaught(Throwable cause) {
/* 467 */     IoFilterChain.Entry head = this.head;
/* 468 */     callNextExceptionCaught(head, this.session, cause);
/*     */   }
/*     */ 
/*     */   private void callNextExceptionCaught(IoFilterChain.Entry entry, IoSession session, Throwable cause)
/*     */   {
/* 474 */     ConnectFuture future = (ConnectFuture)session.removeAttribute(SESSION_CREATED_FUTURE);
/*     */ 
/* 476 */     if (future == null) {
/*     */       try {
/* 478 */         IoFilter filter = entry.getFilter();
/* 479 */         IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 480 */         filter.exceptionCaught(nextFilter, session, cause);
/*     */       }
/*     */       catch (Throwable e) {
/* 483 */         this.logger.warn("Unexpected exception from exceptionCaught handler.", e);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 491 */       session.close(true);
/* 492 */       future.setException(cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireFilterWrite(WriteRequest writeRequest) {
/* 497 */     IoFilterChain.Entry tail = this.tail;
/* 498 */     callPreviousFilterWrite(tail, this.session, writeRequest);
/*     */   }
/*     */ 
/*     */   private void callPreviousFilterWrite(IoFilterChain.Entry entry, IoSession session, WriteRequest writeRequest)
/*     */   {
/*     */     try {
/* 504 */       IoFilter filter = entry.getFilter();
/* 505 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 506 */       filter.filterWrite(nextFilter, session, writeRequest);
/*     */     } catch (Throwable e) {
/* 508 */       writeRequest.getFuture().setException(e);
/* 509 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireFilterClose() {
/* 514 */     IoFilterChain.Entry tail = this.tail;
/* 515 */     callPreviousFilterClose(tail, this.session);
/*     */   }
/*     */ 
/*     */   private void callPreviousFilterClose(IoFilterChain.Entry entry, IoSession session) {
/*     */     try {
/* 520 */       IoFilter filter = entry.getFilter();
/* 521 */       IoFilter.NextFilter nextFilter = entry.getNextFilter();
/* 522 */       filter.filterClose(nextFilter, session);
/*     */     } catch (Throwable e) {
/* 524 */       fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<IoFilterChain.Entry> getAll() {
/* 529 */     List list = new ArrayList();
/* 530 */     EntryImpl e = this.head.nextEntry;
/* 531 */     while (e != this.tail) {
/* 532 */       list.add(e);
/* 533 */       e = e.nextEntry;
/*     */     }
/*     */ 
/* 536 */     return list;
/*     */   }
/*     */ 
/*     */   public List<IoFilterChain.Entry> getAllReversed() {
/* 540 */     List list = new ArrayList();
/* 541 */     EntryImpl e = this.tail.prevEntry;
/* 542 */     while (e != this.head) {
/* 543 */       list.add(e);
/* 544 */       e = e.prevEntry;
/*     */     }
/* 546 */     return list;
/*     */   }
/*     */ 
/*     */   public boolean contains(String name) {
/* 550 */     return getEntry(name) != null;
/*     */   }
/*     */ 
/*     */   public boolean contains(IoFilter filter) {
/* 554 */     return getEntry(filter) != null;
/*     */   }
/*     */ 
/*     */   public boolean contains(Class<? extends IoFilter> filterType) {
/* 558 */     return getEntry(filterType) != null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 563 */     StringBuilder buf = new StringBuilder();
/* 564 */     buf.append("{ ");
/*     */ 
/* 566 */     boolean empty = true;
/*     */ 
/* 568 */     EntryImpl e = this.head.nextEntry;
/* 569 */     while (e != this.tail) {
/* 570 */       if (!empty)
/* 571 */         buf.append(", ");
/*     */       else {
/* 573 */         empty = false;
/*     */       }
/*     */ 
/* 576 */       buf.append('(');
/* 577 */       buf.append(e.getName());
/* 578 */       buf.append(':');
/* 579 */       buf.append(e.getFilter());
/* 580 */       buf.append(')');
/*     */ 
/* 582 */       e = e.nextEntry;
/*     */     }
/*     */ 
/* 585 */     if (empty) {
/* 586 */       buf.append("empty");
/*     */     }
/*     */ 
/* 589 */     buf.append(" }");
/*     */ 
/* 591 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/*     */     try {
/* 597 */       clear();
/*     */     } finally {
/* 599 */       super.finalize();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryImpl
/*     */     implements IoFilterChain.Entry
/*     */   {
/*     */     private EntryImpl prevEntry;
/*     */     private EntryImpl nextEntry;
/*     */     private final String name;
/*     */     private IoFilter filter;
/*     */     private final IoFilter.NextFilter nextFilter;
/*     */ 
/*     */     private EntryImpl(EntryImpl prevEntry, EntryImpl nextEntry, String name, IoFilter filter)
/*     */     {
/* 763 */       if (filter == null) {
/* 764 */         throw new NullPointerException("filter");
/*     */       }
/* 766 */       if (name == null) {
/* 767 */         throw new NullPointerException("name");
/*     */       }
/*     */ 
/* 770 */       this.prevEntry = prevEntry;
/* 771 */       this.nextEntry = nextEntry;
/* 772 */       this.name = name;
/* 773 */       this.filter = filter;
/* 774 */       this.nextFilter = new IoFilter.NextFilter(DefaultIoFilterChain.this) {
/*     */         public void sessionCreated(IoSession session) {
/* 776 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.nextEntry;
/* 777 */           DefaultIoFilterChain.this.callNextSessionCreated(nextEntry, session);
/*     */         }
/*     */ 
/*     */         public void sessionOpened(IoSession session) {
/* 781 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.nextEntry;
/* 782 */           DefaultIoFilterChain.this.callNextSessionOpened(nextEntry, session);
/*     */         }
/*     */ 
/*     */         public void sessionClosed(IoSession session) {
/* 786 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.nextEntry;
/* 787 */           DefaultIoFilterChain.this.callNextSessionClosed(nextEntry, session);
/*     */         }
/*     */ 
/*     */         public void sessionIdle(IoSession session, IdleStatus status) {
/* 791 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.nextEntry;
/* 792 */           DefaultIoFilterChain.this.callNextSessionIdle(nextEntry, session, status);
/*     */         }
/*     */ 
/*     */         public void exceptionCaught(IoSession session, Throwable cause) {
/* 796 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.nextEntry;
/* 797 */           DefaultIoFilterChain.this.callNextExceptionCaught(nextEntry, session, cause);
/*     */         }
/*     */ 
/*     */         public void messageReceived(IoSession session, Object message) {
/* 801 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.nextEntry;
/* 802 */           DefaultIoFilterChain.this.callNextMessageReceived(nextEntry, session, message);
/*     */         }
/*     */ 
/*     */         public void messageSent(IoSession session, WriteRequest writeRequest)
/*     */         {
/* 807 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.nextEntry;
/* 808 */           DefaultIoFilterChain.this.callNextMessageSent(nextEntry, session, writeRequest);
/*     */         }
/*     */ 
/*     */         public void filterWrite(IoSession session, WriteRequest writeRequest)
/*     */         {
/* 813 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.prevEntry;
/* 814 */           DefaultIoFilterChain.this.callPreviousFilterWrite(nextEntry, session, writeRequest);
/*     */         }
/*     */ 
/*     */         public void filterClose(IoSession session) {
/* 818 */           IoFilterChain.Entry nextEntry = DefaultIoFilterChain.EntryImpl.this.prevEntry;
/* 819 */           DefaultIoFilterChain.this.callPreviousFilterClose(nextEntry, session);
/*     */         }
/*     */ 
/*     */         public String toString() {
/* 823 */           return DefaultIoFilterChain.EntryImpl.access$300(DefaultIoFilterChain.EntryImpl.this).name;
/*     */         } } ;
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 829 */       return this.name;
/*     */     }
/*     */ 
/*     */     public IoFilter getFilter() {
/* 833 */       return this.filter;
/*     */     }
/*     */ 
/*     */     private void setFilter(IoFilter filter) {
/* 837 */       if (filter == null) {
/* 838 */         throw new NullPointerException("filter");
/*     */       }
/*     */ 
/* 841 */       this.filter = filter;
/*     */     }
/*     */ 
/*     */     public IoFilter.NextFilter getNextFilter() {
/* 845 */       return this.nextFilter;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 850 */       StringBuilder sb = new StringBuilder();
/*     */ 
/* 853 */       sb.append("('").append(getName()).append('\'');
/*     */ 
/* 856 */       sb.append(", prev: '");
/*     */ 
/* 858 */       if (this.prevEntry != null) {
/* 859 */         sb.append(this.prevEntry.name);
/* 860 */         sb.append(':');
/* 861 */         sb.append(this.prevEntry.getFilter().getClass().getSimpleName());
/*     */       } else {
/* 863 */         sb.append("null");
/*     */       }
/*     */ 
/* 867 */       sb.append("', next: '");
/*     */ 
/* 869 */       if (this.nextEntry != null) {
/* 870 */         sb.append(this.nextEntry.name);
/* 871 */         sb.append(':');
/* 872 */         sb.append(this.nextEntry.getFilter().getClass().getSimpleName());
/*     */       } else {
/* 874 */         sb.append("null");
/*     */       }
/*     */ 
/* 877 */       sb.append("')");
/* 878 */       return sb.toString();
/*     */     }
/*     */ 
/*     */     public void addAfter(String name, IoFilter filter) {
/* 882 */       DefaultIoFilterChain.this.addAfter(getName(), name, filter);
/*     */     }
/*     */ 
/*     */     public void addBefore(String name, IoFilter filter) {
/* 886 */       DefaultIoFilterChain.this.addBefore(getName(), name, filter);
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 890 */       DefaultIoFilterChain.this.remove(getName());
/*     */     }
/*     */ 
/*     */     public void replace(IoFilter newFilter) {
/* 894 */       DefaultIoFilterChain.this.replace(getName(), newFilter);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class TailFilter extends IoFilterAdapter
/*     */   {
/*     */     public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */       throws Exception
/*     */     {
/*     */       try
/*     */       {
/* 649 */         session.getHandler().sessionCreated(session);
/*     */       }
/*     */       finally
/*     */       {
/*     */         ConnectFuture future;
/* 652 */         ConnectFuture future = (ConnectFuture)session.removeAttribute(DefaultIoFilterChain.SESSION_CREATED_FUTURE);
/*     */ 
/* 654 */         if (future != null)
/* 655 */           future.setSession(session);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
/*     */       throws Exception
/*     */     {
/* 663 */       session.getHandler().sessionOpened(session);
/*     */     }
/*     */ 
/*     */     public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */       throws Exception
/*     */     {
/* 669 */       AbstractIoSession s = (AbstractIoSession)session;
/*     */       try {
/* 671 */         s.getHandler().sessionClosed(session);
/*     */       } finally {
/*     */         try {
/* 674 */           s.getWriteRequestQueue().dispose(session);
/*     */         } finally {
/*     */           try {
/* 677 */             s.getAttributeMap().dispose(session);
/*     */           }
/*     */           finally {
/*     */             try {
/* 681 */               session.getFilterChain().clear();
/*     */             } finally {
/* 683 */               if (s.getConfig().isUseReadOperation())
/* 684 */                 s.offerClosedReadFuture();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */       throws Exception
/*     */     {
/* 695 */       session.getHandler().sessionIdle(session, status);
/*     */     }
/*     */ 
/*     */     public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */       throws Exception
/*     */     {
/* 701 */       AbstractIoSession s = (AbstractIoSession)session;
/*     */       try {
/* 703 */         s.getHandler().exceptionCaught(s, cause);
/*     */       } finally {
/* 705 */         if (s.getConfig().isUseReadOperation())
/* 706 */           s.offerFailedReadFuture(cause);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */       throws Exception
/*     */     {
/* 714 */       AbstractIoSession s = (AbstractIoSession)session;
/* 715 */       if (!(message instanceof IoBuffer))
/* 716 */         s.increaseReadMessages(System.currentTimeMillis());
/* 717 */       else if (!((IoBuffer)message).hasRemaining()) {
/* 718 */         s.increaseReadMessages(System.currentTimeMillis());
/*     */       }
/*     */       try
/*     */       {
/* 722 */         session.getHandler().messageReceived(s, message);
/*     */       } finally {
/* 724 */         if (s.getConfig().isUseReadOperation())
/* 725 */           s.offerReadFuture(message);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */       throws Exception
/*     */     {
/* 733 */       session.getHandler().messageSent(session, writeRequest.getMessage());
/*     */     }
/*     */ 
/*     */     public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */       throws Exception
/*     */     {
/* 740 */       nextFilter.filterWrite(session, writeRequest);
/*     */     }
/*     */ 
/*     */     public void filterClose(IoFilter.NextFilter nextFilter, IoSession session)
/*     */       throws Exception
/*     */     {
/* 746 */       nextFilter.filterClose(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class HeadFilter extends IoFilterAdapter
/*     */   {
/*     */     private HeadFilter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */       throws Exception
/*     */     {
/* 609 */       AbstractIoSession s = (AbstractIoSession)session;
/*     */ 
/* 612 */       if ((writeRequest.getMessage() instanceof IoBuffer)) {
/* 613 */         IoBuffer buffer = (IoBuffer)writeRequest.getMessage();
/*     */ 
/* 617 */         buffer.mark();
/* 618 */         int remaining = buffer.remaining();
/* 619 */         if (remaining == 0)
/*     */         {
/* 622 */           s.increaseScheduledWriteMessages();
/*     */         }
/* 624 */         else s.increaseScheduledWriteBytes(remaining); 
/*     */       }
/*     */       else
/*     */       {
/* 627 */         s.increaseScheduledWriteMessages();
/*     */       }
/*     */ 
/* 630 */       s.getWriteRequestQueue().offer(s, writeRequest);
/* 631 */       if (!s.isWriteSuspended())
/* 632 */         s.getProcessor().flush(s);
/*     */     }
/*     */ 
/*     */     public void filterClose(IoFilter.NextFilter nextFilter, IoSession session)
/*     */       throws Exception
/*     */     {
/* 640 */       ((AbstractIoSession)session).getProcessor().remove((AbstractIoSession)session);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.filterchain.DefaultIoFilterChain
 * JD-Core Version:    0.6.0
 */