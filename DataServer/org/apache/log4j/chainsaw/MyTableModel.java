/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ class MyTableModel extends AbstractTableModel
/*     */ {
/*  32 */   private static final Category LOG = Category.getInstance(MyTableModel.class);
/*     */ 
/*  36 */   private static final Comparator MY_COMP = new Comparator()
/*     */   {
/*     */     public int compare(Object aObj1, Object aObj2)
/*     */     {
/*  40 */       if ((aObj1 == null) && (aObj2 == null))
/*  41 */         return 0;
/*  42 */       if (aObj1 == null)
/*  43 */         return -1;
/*  44 */       if (aObj2 == null) {
/*  45 */         return 1;
/*     */       }
/*     */ 
/*  49 */       EventDetails le1 = (EventDetails)aObj1;
/*  50 */       EventDetails le2 = (EventDetails)aObj2;
/*     */ 
/*  52 */       if (le1.getTimeStamp() < le2.getTimeStamp()) {
/*  53 */         return 1;
/*     */       }
/*     */ 
/*  56 */       return -1;
/*     */     }
/*  36 */   };
/*     */ 
/* 103 */   private static final String[] COL_NAMES = { "Time", "Priority", "Trace", "Category", "NDC", "Message" };
/*     */ 
/* 107 */   private static final EventDetails[] EMPTY_LIST = new EventDetails[0];
/*     */ 
/* 110 */   private static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(3, 2);
/*     */ 
/* 114 */   private final Object mLock = new Object();
/*     */ 
/* 116 */   private final SortedSet mAllEvents = new TreeSet(MY_COMP);
/*     */ 
/* 118 */   private EventDetails[] mFilteredEvents = EMPTY_LIST;
/*     */ 
/* 120 */   private final List mPendingEvents = new ArrayList();
/*     */ 
/* 122 */   private boolean mPaused = false;
/*     */ 
/* 125 */   private String mThreadFilter = "";
/*     */ 
/* 127 */   private String mMessageFilter = "";
/*     */ 
/* 129 */   private String mNDCFilter = "";
/*     */ 
/* 131 */   private String mCategoryFilter = "";
/*     */ 
/* 133 */   private Priority mPriorityFilter = Priority.DEBUG;
/*     */ 
/*     */   MyTableModel()
/*     */   {
/* 141 */     Thread t = new Thread(new Processor(null));
/* 142 */     t.setDaemon(true);
/* 143 */     t.start();
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 153 */     synchronized (this.mLock) {
/* 154 */       return this.mFilteredEvents.length;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/* 161 */     return COL_NAMES.length;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int aCol)
/*     */   {
/* 167 */     return COL_NAMES[aCol];
/*     */   }
/*     */ 
/*     */   public Class getColumnClass(int aCol)
/*     */   {
/* 173 */     return Object.class;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int aRow, int aCol)
/*     */   {
/* 178 */     synchronized (this.mLock) {
/* 179 */       EventDetails event = this.mFilteredEvents[aRow];
/*     */       Object localObject2;
/* 181 */       if (aCol == 0)
/* 182 */         return DATE_FORMATTER.format(new Date(event.getTimeStamp()));
/* 183 */       if (aCol == 1)
/* 184 */         return event.getPriority();
/* 185 */       if (aCol == 2) {
/* 186 */         return event.getThrowableStrRep() == null ? Boolean.FALSE : Boolean.TRUE;
/*     */       }
/* 188 */       if (aCol == 3)
/* 189 */         return event.getCategoryName();
/* 190 */       if (aCol == 4) {
/* 191 */         return event.getNDC();
/*     */       }
/* 193 */       return event.getMessage();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setPriorityFilter(Priority aPriority)
/*     */   {
/* 208 */     synchronized (this.mLock) {
/* 209 */       this.mPriorityFilter = aPriority;
/* 210 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setThreadFilter(String aStr)
/*     */   {
/* 220 */     synchronized (this.mLock) {
/* 221 */       this.mThreadFilter = aStr.trim();
/* 222 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMessageFilter(String aStr)
/*     */   {
/* 232 */     synchronized (this.mLock) {
/* 233 */       this.mMessageFilter = aStr.trim();
/* 234 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setNDCFilter(String aStr)
/*     */   {
/* 244 */     synchronized (this.mLock) {
/* 245 */       this.mNDCFilter = aStr.trim();
/* 246 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setCategoryFilter(String aStr)
/*     */   {
/* 256 */     synchronized (this.mLock) {
/* 257 */       this.mCategoryFilter = aStr.trim();
/* 258 */       updateFilteredEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addEvent(EventDetails aEvent)
/*     */   {
/* 268 */     synchronized (this.mLock) {
/* 269 */       this.mPendingEvents.add(aEvent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 277 */     synchronized (this.mLock) {
/* 278 */       this.mAllEvents.clear();
/* 279 */       this.mFilteredEvents = new EventDetails[0];
/* 280 */       this.mPendingEvents.clear();
/* 281 */       fireTableDataChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void toggle()
/*     */   {
/* 287 */     synchronized (this.mLock) {
/* 288 */       this.mPaused = (!this.mPaused);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isPaused()
/*     */   {
/* 294 */     synchronized (this.mLock) {
/* 295 */       return this.mPaused;
/*     */     }
/*     */   }
/*     */ 
/*     */   public EventDetails getEventDetails(int aRow)
/*     */   {
/* 306 */     synchronized (this.mLock) {
/* 307 */       return this.mFilteredEvents[aRow];
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updateFilteredEvents(boolean aInsertedToFront)
/*     */   {
/* 322 */     long start = System.currentTimeMillis();
/* 323 */     List filtered = new ArrayList();
/* 324 */     int size = this.mAllEvents.size();
/* 325 */     Iterator it = this.mAllEvents.iterator();
/*     */ 
/* 327 */     while (it.hasNext()) {
/* 328 */       EventDetails event = (EventDetails)it.next();
/* 329 */       if (matchFilter(event)) {
/* 330 */         filtered.add(event);
/*     */       }
/*     */     }
/*     */ 
/* 334 */     EventDetails lastFirst = this.mFilteredEvents.length == 0 ? null : this.mFilteredEvents[0];
/*     */ 
/* 337 */     this.mFilteredEvents = ((EventDetails[])filtered.toArray(EMPTY_LIST));
/*     */ 
/* 339 */     if ((aInsertedToFront) && (lastFirst != null)) {
/* 340 */       int index = filtered.indexOf(lastFirst);
/* 341 */       if (index < 1) {
/* 342 */         LOG.warn("In strange state");
/* 343 */         fireTableDataChanged();
/*     */       } else {
/* 345 */         fireTableRowsInserted(0, index - 1);
/*     */       }
/*     */     } else {
/* 348 */       fireTableDataChanged();
/*     */     }
/*     */ 
/* 351 */     long end = System.currentTimeMillis();
/* 352 */     LOG.debug("Total time [ms]: " + (end - start) + " in update, size: " + size);
/*     */   }
/*     */ 
/*     */   private boolean matchFilter(EventDetails aEvent)
/*     */   {
/* 363 */     if ((aEvent.getPriority().isGreaterOrEqual(this.mPriorityFilter)) && (aEvent.getThreadName().indexOf(this.mThreadFilter) >= 0) && (aEvent.getCategoryName().indexOf(this.mCategoryFilter) >= 0) && ((this.mNDCFilter.length() == 0) || ((aEvent.getNDC() != null) && (aEvent.getNDC().indexOf(this.mNDCFilter) >= 0))))
/*     */     {
/* 370 */       String rm = aEvent.getMessage();
/* 371 */       if (rm == null)
/*     */       {
/* 373 */         return this.mMessageFilter.length() == 0;
/*     */       }
/* 375 */       return rm.indexOf(this.mMessageFilter) >= 0;
/*     */     }
/*     */ 
/* 379 */     return false;
/*     */   }
/*     */ 
/*     */   private class Processor
/*     */     implements Runnable
/*     */   {
/*     */     private final MyTableModel this$0;
/*     */ 
/*     */     private Processor()
/*     */     {
/*  64 */       this.this$0 = this$0;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       while (true) {
/*     */         try {
/*  71 */           Thread.sleep(1000L);
/*     */         }
/*     */         catch (java.lang.InterruptedException )
/*     */         {
/*     */         }
/*  76 */         synchronized (this.this$0.mLock) {
/*  77 */           if (this.this$0.mPaused)
/*     */           {
/*     */             continue;
/*     */           }
/*  81 */           boolean toHead = true;
/*  82 */           boolean needUpdate = false;
/*  83 */           Iterator it = this.this$0.mPendingEvents.iterator();
/*  84 */           while (it.hasNext()) {
/*  85 */             EventDetails event = (EventDetails)it.next();
/*  86 */             this.this$0.mAllEvents.add(event);
/*  87 */             toHead = (toHead) && (event == this.this$0.mAllEvents.first());
/*  88 */             needUpdate = (needUpdate) || (this.this$0.matchFilter(event));
/*     */           }
/*  90 */           this.this$0.mPendingEvents.clear();
/*     */ 
/*  92 */           if (needUpdate)
/*  93 */             this.this$0.updateFilteredEvents(toHead);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     Processor(MyTableModel.1 x1)
/*     */     {
/*  64 */       this();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.MyTableModel
 * JD-Core Version:    0.6.0
 */