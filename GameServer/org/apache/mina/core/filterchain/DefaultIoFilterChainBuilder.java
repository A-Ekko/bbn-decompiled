/*     */ package org.apache.mina.core.filterchain;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DefaultIoFilterChainBuilder
/*     */   implements IoFilterChainBuilder
/*     */ {
/*  65 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */   private final List<IoFilterChain.Entry> entries;
/*     */ 
/*     */   public DefaultIoFilterChainBuilder()
/*     */   {
/*  72 */     this.entries = new CopyOnWriteArrayList();
/*     */   }
/*     */ 
/*     */   public DefaultIoFilterChainBuilder(DefaultIoFilterChainBuilder filterChain)
/*     */   {
/*  79 */     if (filterChain == null) {
/*  80 */       throw new NullPointerException("filterChain");
/*     */     }
/*  82 */     this.entries = new CopyOnWriteArrayList(filterChain.entries);
/*     */   }
/*     */ 
/*     */   public IoFilterChain.Entry getEntry(String name)
/*     */   {
/*  89 */     for (IoFilterChain.Entry e : this.entries) {
/*  90 */       if (e.getName().equals(name)) {
/*  91 */         return e;
/*     */       }
/*     */     }
/*     */ 
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   public IoFilterChain.Entry getEntry(IoFilter filter)
/*     */   {
/* 102 */     for (IoFilterChain.Entry e : this.entries) {
/* 103 */       if (e.getFilter() == filter) {
/* 104 */         return e;
/*     */       }
/*     */     }
/*     */ 
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   public IoFilterChain.Entry getEntry(Class<? extends IoFilter> filterType)
/*     */   {
/* 115 */     for (IoFilterChain.Entry e : this.entries) {
/* 116 */       if (filterType.isAssignableFrom(e.getFilter().getClass())) {
/* 117 */         return e;
/*     */       }
/*     */     }
/*     */ 
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   public IoFilter get(String name)
/*     */   {
/* 128 */     IoFilterChain.Entry e = getEntry(name);
/* 129 */     if (e == null) {
/* 130 */       return null;
/*     */     }
/*     */ 
/* 133 */     return e.getFilter();
/*     */   }
/*     */ 
/*     */   public IoFilter get(Class<? extends IoFilter> filterType)
/*     */   {
/* 140 */     IoFilterChain.Entry e = getEntry(filterType);
/* 141 */     if (e == null) {
/* 142 */       return null;
/*     */     }
/*     */ 
/* 145 */     return e.getFilter();
/*     */   }
/*     */ 
/*     */   public List<IoFilterChain.Entry> getAll()
/*     */   {
/* 152 */     return new ArrayList(this.entries);
/*     */   }
/*     */ 
/*     */   public List<IoFilterChain.Entry> getAllReversed()
/*     */   {
/* 159 */     List result = getAll();
/* 160 */     Collections.reverse(result);
/* 161 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean contains(String name)
/*     */   {
/* 168 */     return getEntry(name) != null;
/*     */   }
/*     */ 
/*     */   public boolean contains(IoFilter filter)
/*     */   {
/* 175 */     return getEntry(filter) != null;
/*     */   }
/*     */ 
/*     */   public boolean contains(Class<? extends IoFilter> filterType)
/*     */   {
/* 182 */     return getEntry(filterType) != null;
/*     */   }
/*     */ 
/*     */   public synchronized void addFirst(String name, IoFilter filter)
/*     */   {
/* 189 */     register(0, new EntryImpl(name, filter, null));
/*     */   }
/*     */ 
/*     */   public synchronized void addLast(String name, IoFilter filter)
/*     */   {
/* 196 */     register(this.entries.size(), new EntryImpl(name, filter, null));
/*     */   }
/*     */ 
/*     */   public synchronized void addBefore(String baseName, String name, IoFilter filter)
/*     */   {
/* 204 */     checkBaseName(baseName);
/*     */ 
/* 206 */     for (ListIterator i = this.entries.listIterator(); i.hasNext(); ) {
/* 207 */       IoFilterChain.Entry base = (IoFilterChain.Entry)i.next();
/* 208 */       if (base.getName().equals(baseName)) {
/* 209 */         register(i.previousIndex(), new EntryImpl(name, filter, null));
/* 210 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void addAfter(String baseName, String name, IoFilter filter)
/*     */   {
/* 220 */     checkBaseName(baseName);
/*     */ 
/* 222 */     for (ListIterator i = this.entries.listIterator(); i.hasNext(); ) {
/* 223 */       IoFilterChain.Entry base = (IoFilterChain.Entry)i.next();
/* 224 */       if (base.getName().equals(baseName)) {
/* 225 */         register(i.nextIndex(), new EntryImpl(name, filter, null));
/* 226 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter remove(String name)
/*     */   {
/* 235 */     if (name == null) {
/* 236 */       throw new NullPointerException("name");
/*     */     }
/*     */ 
/* 239 */     for (ListIterator i = this.entries.listIterator(); i.hasNext(); ) {
/* 240 */       IoFilterChain.Entry e = (IoFilterChain.Entry)i.next();
/* 241 */       if (e.getName().equals(name)) {
/* 242 */         this.entries.remove(i.previousIndex());
/* 243 */         return e.getFilter();
/*     */       }
/*     */     }
/*     */ 
/* 247 */     throw new IllegalArgumentException("Unknown filter name: " + name);
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter remove(IoFilter filter)
/*     */   {
/* 254 */     if (filter == null) {
/* 255 */       throw new NullPointerException("filter");
/*     */     }
/*     */ 
/* 258 */     for (ListIterator i = this.entries.listIterator(); i.hasNext(); ) {
/* 259 */       IoFilterChain.Entry e = (IoFilterChain.Entry)i.next();
/* 260 */       if (e.getFilter() == filter) {
/* 261 */         this.entries.remove(i.previousIndex());
/* 262 */         return e.getFilter();
/*     */       }
/*     */     }
/*     */ 
/* 266 */     throw new IllegalArgumentException("Filter not found: " + filter.getClass().getName());
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter remove(Class<? extends IoFilter> filterType)
/*     */   {
/* 273 */     if (filterType == null) {
/* 274 */       throw new NullPointerException("filterType");
/*     */     }
/*     */ 
/* 277 */     for (ListIterator i = this.entries.listIterator(); i.hasNext(); ) {
/* 278 */       IoFilterChain.Entry e = (IoFilterChain.Entry)i.next();
/* 279 */       if (filterType.isAssignableFrom(e.getFilter().getClass())) {
/* 280 */         this.entries.remove(i.previousIndex());
/* 281 */         return e.getFilter();
/*     */       }
/*     */     }
/*     */ 
/* 285 */     throw new IllegalArgumentException("Filter not found: " + filterType.getName());
/*     */   }
/*     */ 
/*     */   public synchronized IoFilter replace(String name, IoFilter newFilter) {
/* 289 */     checkBaseName(name);
/* 290 */     EntryImpl e = (EntryImpl)get(name);
/* 291 */     IoFilter oldFilter = e.getFilter();
/* 292 */     e.setFilter(newFilter);
/* 293 */     return oldFilter;
/*     */   }
/*     */ 
/*     */   public synchronized void replace(IoFilter oldFilter, IoFilter newFilter) {
/* 297 */     for (IoFilterChain.Entry e : this.entries) {
/* 298 */       if (e.getFilter() == oldFilter) {
/* 299 */         ((EntryImpl)e).setFilter(newFilter);
/* 300 */         return;
/*     */       }
/*     */     }
/* 303 */     throw new IllegalArgumentException("Filter not found: " + oldFilter.getClass().getName());
/*     */   }
/*     */ 
/*     */   public synchronized void replace(Class<? extends IoFilter> oldFilterType, IoFilter newFilter)
/*     */   {
/* 309 */     for (IoFilterChain.Entry e : this.entries) {
/* 310 */       if (oldFilterType.isAssignableFrom(e.getFilter().getClass())) {
/* 311 */         ((EntryImpl)e).setFilter(newFilter);
/* 312 */         return;
/*     */       }
/*     */     }
/* 315 */     throw new IllegalArgumentException("Filter not found: " + oldFilterType.getName());
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 323 */     this.entries.clear();
/*     */   }
/*     */ 
/*     */   public void setFilters(Map<String, ? extends IoFilter> filters)
/*     */   {
/* 334 */     if (filters == null) {
/* 335 */       throw new NullPointerException("filters");
/*     */     }
/*     */ 
/* 338 */     if (!isOrderedMap(filters)) {
/* 339 */       throw new IllegalArgumentException("filters is not an ordered map. Please try " + LinkedHashMap.class.getName() + ".");
/*     */     }
/*     */ 
/* 344 */     filters = new LinkedHashMap(filters);
/* 345 */     for (Map.Entry e : filters.entrySet()) {
/* 346 */       if (e.getKey() == null) {
/* 347 */         throw new NullPointerException("filters contains a null key.");
/*     */       }
/* 349 */       if (e.getValue() == null) {
/* 350 */         throw new NullPointerException("filters contains a null value.");
/*     */       }
/*     */     }
/*     */ 
/* 354 */     synchronized (this) {
/* 355 */       clear();
/* 356 */       for (Map.Entry e : filters.entrySet())
/* 357 */         addLast((String)e.getKey(), (IoFilter)e.getValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isOrderedMap(Map map)
/*     */   {
/* 364 */     Class mapType = map.getClass();
/* 365 */     if (LinkedHashMap.class.isAssignableFrom(mapType)) {
/* 366 */       if (this.logger.isDebugEnabled()) {
/* 367 */         this.logger.debug(mapType.getSimpleName() + " is an ordered map.");
/*     */       }
/* 369 */       return true;
/*     */     }
/*     */ 
/* 372 */     if (this.logger.isDebugEnabled()) {
/* 373 */       this.logger.debug(mapType.getName() + " is not a " + LinkedHashMap.class.getSimpleName());
/*     */     }
/*     */ 
/* 377 */     Class type = mapType;
/* 378 */     while (type != null) {
/* 379 */       for (Class i : type.getInterfaces()) {
/* 380 */         if (i.getName().endsWith("OrderedMap")) {
/* 381 */           if (this.logger.isDebugEnabled()) {
/* 382 */             this.logger.debug(mapType.getSimpleName() + " is an ordered map (guessed from that it " + " implements OrderedMap interface.)");
/*     */           }
/*     */ 
/* 387 */           return true;
/*     */         }
/*     */       }
/* 390 */       type = type.getSuperclass();
/*     */     }
/*     */ 
/* 393 */     if (this.logger.isDebugEnabled()) {
/* 394 */       this.logger.debug(mapType.getName() + " doesn't implement OrderedMap interface.");
/*     */     }
/*     */ 
/* 401 */     this.logger.debug("Last resort; trying to create a new map instance with a default constructor and test if insertion order is maintained.");
/*     */     Map newMap;
/*     */     try
/*     */     {
/* 408 */       newMap = (Map)mapType.newInstance();
/*     */     } catch (Exception e) {
/* 410 */       if (this.logger.isDebugEnabled()) {
/* 411 */         this.logger.debug("Failed to create a new map instance of '" + mapType.getName() + "'.", e);
/*     */       }
/*     */ 
/* 415 */       return false;
/*     */     }
/*     */ 
/* 418 */     Random rand = new Random();
/* 419 */     List expectedNames = new ArrayList();
/* 420 */     IoFilter dummyFilter = new IoFilterAdapter();
/*     */     Iterator it;
/*     */     Iterator i$;
/* 421 */     for (int i = 0; i < 65536; i++) {
/*     */       String filterName;
/*     */       do filterName = String.valueOf(rand.nextInt());
/* 425 */       while (newMap.containsKey(filterName));
/*     */ 
/* 427 */       newMap.put(filterName, dummyFilter);
/* 428 */       expectedNames.add(filterName);
/*     */ 
/* 430 */       it = expectedNames.iterator();
/* 431 */       for (i$ = newMap.keySet().iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 432 */         if (!((String)it.next()).equals(key)) {
/* 433 */           if (this.logger.isDebugEnabled()) {
/* 434 */             this.logger.debug("The specified map didn't pass the insertion order test after " + (i + 1) + " tries.");
/*     */           }
/*     */ 
/* 438 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 443 */     if (this.logger.isDebugEnabled()) {
/* 444 */       this.logger.debug("The specified map passed the insertion order test.");
/*     */     }
/*     */ 
/* 447 */     return true;
/*     */   }
/*     */ 
/*     */   public void buildFilterChain(IoFilterChain chain) throws Exception {
/* 451 */     for (IoFilterChain.Entry e : this.entries)
/* 452 */       chain.addLast(e.getName(), e.getFilter());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 458 */     StringBuilder buf = new StringBuilder();
/* 459 */     buf.append("{ ");
/*     */ 
/* 461 */     boolean empty = true;
/*     */ 
/* 463 */     for (IoFilterChain.Entry e : this.entries) {
/* 464 */       if (!empty)
/* 465 */         buf.append(", ");
/*     */       else {
/* 467 */         empty = false;
/*     */       }
/*     */ 
/* 470 */       buf.append('(');
/* 471 */       buf.append(e.getName());
/* 472 */       buf.append(':');
/* 473 */       buf.append(e.getFilter());
/* 474 */       buf.append(')');
/*     */     }
/*     */ 
/* 477 */     if (empty) {
/* 478 */       buf.append("empty");
/*     */     }
/*     */ 
/* 481 */     buf.append(" }");
/*     */ 
/* 483 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private void checkBaseName(String baseName) {
/* 487 */     if (baseName == null) {
/* 488 */       throw new NullPointerException("baseName");
/*     */     }
/*     */ 
/* 491 */     if (!contains(baseName))
/* 492 */       throw new IllegalArgumentException("Unknown filter name: " + baseName);
/*     */   }
/*     */ 
/*     */   private void register(int index, IoFilterChain.Entry e)
/*     */   {
/* 498 */     if (contains(e.getName())) {
/* 499 */       throw new IllegalArgumentException("Other filter is using the same name: " + e.getName());
/*     */     }
/*     */ 
/* 503 */     this.entries.add(index, e);
/*     */   }
/*     */   private class EntryImpl implements IoFilterChain.Entry {
/*     */     private final String name;
/*     */     private volatile IoFilter filter;
/*     */ 
/* 511 */     private EntryImpl(String name, IoFilter filter) { if (name == null) {
/* 512 */         throw new NullPointerException("name");
/*     */       }
/* 514 */       if (filter == null) {
/* 515 */         throw new NullPointerException("filter");
/*     */       }
/*     */ 
/* 518 */       this.name = name;
/* 519 */       this.filter = filter; }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 523 */       return this.name;
/*     */     }
/*     */ 
/*     */     public IoFilter getFilter() {
/* 527 */       return this.filter;
/*     */     }
/*     */ 
/*     */     private void setFilter(IoFilter filter) {
/* 531 */       this.filter = filter;
/*     */     }
/*     */ 
/*     */     public IoFilter.NextFilter getNextFilter() {
/* 535 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 540 */       return "(" + getName() + ':' + this.filter + ')';
/*     */     }
/*     */ 
/*     */     public void addAfter(String name, IoFilter filter) {
/* 544 */       DefaultIoFilterChainBuilder.this.addAfter(getName(), name, filter);
/*     */     }
/*     */ 
/*     */     public void addBefore(String name, IoFilter filter) {
/* 548 */       DefaultIoFilterChainBuilder.this.addBefore(getName(), name, filter);
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 552 */       DefaultIoFilterChainBuilder.this.remove(getName());
/*     */     }
/*     */ 
/*     */     public void replace(IoFilter newFilter) {
/* 556 */       DefaultIoFilterChainBuilder.this.replace(getName(), newFilter);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder
 * JD-Core Version:    0.6.0
 */