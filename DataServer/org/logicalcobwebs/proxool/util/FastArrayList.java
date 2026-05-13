/*      */ package org.logicalcobwebs.proxool.util;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.ConcurrentModificationException;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ 
/*      */ public class FastArrayList extends ArrayList
/*      */ {
/*  176 */   private ArrayList list = null;
/*      */ 
/*  185 */   private boolean fast = false;
/*      */ 
/*      */   public FastArrayList()
/*      */   {
/*  137 */     this.list = new ArrayList();
/*      */   }
/*      */ 
/*      */   public FastArrayList(int capacity)
/*      */   {
/*  150 */     this.list = new ArrayList(capacity);
/*      */   }
/*      */ 
/*      */   public FastArrayList(Collection collection)
/*      */   {
/*  165 */     this.list = new ArrayList(collection);
/*      */   }
/*      */ 
/*      */   public boolean getFast()
/*      */   {
/*  194 */     return this.fast;
/*      */   }
/*      */ 
/*      */   public void setFast(boolean fast)
/*      */   {
/*  203 */     this.fast = fast;
/*      */   }
/*      */ 
/*      */   public boolean add(Object element)
/*      */   {
/*  217 */     if (this.fast) {
/*  218 */       synchronized (this) {
/*  219 */         ArrayList temp = (ArrayList)this.list.clone();
/*  220 */         boolean result = temp.add(element);
/*  221 */         this.list = temp;
/*  222 */         return result;
/*      */       }
/*      */     }
/*  225 */     synchronized (this.list) {
/*  226 */       return this.list.add(element);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void add(int index, Object element)
/*      */   {
/*  244 */     if (this.fast)
/*  245 */       synchronized (this) {
/*  246 */         ArrayList temp = (ArrayList)this.list.clone();
/*  247 */         temp.add(index, element);
/*  248 */         this.list = temp;
/*      */       }
/*      */     else
/*  251 */       synchronized (this.list) {
/*  252 */         this.list.add(index, element);
/*      */       }
/*      */   }
/*      */ 
/*      */   public boolean addAll(Collection collection)
/*      */   {
/*  268 */     if (this.fast) {
/*  269 */       synchronized (this) {
/*  270 */         ArrayList temp = (ArrayList)this.list.clone();
/*  271 */         boolean result = temp.addAll(collection);
/*  272 */         this.list = temp;
/*  273 */         return result;
/*      */       }
/*      */     }
/*  276 */     synchronized (this.list) {
/*  277 */       return this.list.addAll(collection);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean addAll(int index, Collection collection)
/*      */   {
/*  296 */     if (this.fast) {
/*  297 */       synchronized (this) {
/*  298 */         ArrayList temp = (ArrayList)this.list.clone();
/*  299 */         boolean result = temp.addAll(index, collection);
/*  300 */         this.list = temp;
/*  301 */         return result;
/*      */       }
/*      */     }
/*  304 */     synchronized (this.list) {
/*  305 */       return this.list.addAll(index, collection);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  321 */     if (this.fast)
/*  322 */       synchronized (this) {
/*  323 */         ArrayList temp = (ArrayList)this.list.clone();
/*  324 */         temp.clear();
/*  325 */         this.list = temp;
/*      */       }
/*      */     else
/*  328 */       synchronized (this.list) {
/*  329 */         this.list.clear();
/*      */       }
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/*  342 */     FastArrayList results = null;
/*  343 */     if (this.fast)
/*  344 */       results = new FastArrayList(this.list);
/*      */     else {
/*  346 */       synchronized (this.list) {
/*  347 */         results = new FastArrayList(this.list);
/*      */       }
/*      */     }
/*  350 */     results.setFast(getFast());
/*  351 */     return results;
/*      */   }
/*      */ 
/*      */   public boolean contains(Object element)
/*      */   {
/*  363 */     if (this.fast) {
/*  364 */       return this.list.contains(element);
/*      */     }
/*  366 */     synchronized (this.list) {
/*  367 */       return this.list.contains(element);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean containsAll(Collection collection)
/*      */   {
/*  382 */     if (this.fast) {
/*  383 */       return this.list.containsAll(collection);
/*      */     }
/*  385 */     synchronized (this.list) {
/*  386 */       return this.list.containsAll(collection);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ensureCapacity(int capacity)
/*      */   {
/*  402 */     if (this.fast)
/*  403 */       synchronized (this) {
/*  404 */         ArrayList temp = (ArrayList)this.list.clone();
/*  405 */         temp.ensureCapacity(capacity);
/*  406 */         this.list = temp;
/*      */       }
/*      */     else
/*  409 */       synchronized (this.list) {
/*  410 */         this.list.ensureCapacity(capacity);
/*      */       }
/*      */   }
/*      */ 
/*      */   public boolean equals(Object o)
/*      */   {
/*  428 */     if (o == this)
/*  429 */       return true;
/*  430 */     if (!(o instanceof List)) {
/*  431 */       return false;
/*      */     }
/*  433 */     List lo = (List)o;
/*      */ 
/*  436 */     if (this.fast) {
/*  437 */       ListIterator li1 = this.list.listIterator();
/*  438 */       ListIterator li2 = lo.listIterator();
/*  439 */       while ((li1.hasNext()) && (li2.hasNext())) {
/*  440 */         Object o1 = li1.next();
/*  441 */         Object o2 = li2.next();
/*  442 */         if (o1 == null ? o2 != null : !o1.equals(o2)) {
/*  443 */           return false;
/*      */         }
/*      */       }
/*  446 */       return (!li1.hasNext()) && (!li2.hasNext());
/*      */     }
/*  448 */     synchronized (this.list) {
/*  449 */       ListIterator li1 = this.list.listIterator();
/*  450 */       ListIterator li2 = lo.listIterator();
/*  451 */       while ((li1.hasNext()) && (li2.hasNext())) {
/*  452 */         Object o1 = li1.next();
/*  453 */         Object o2 = li2.next();
/*  454 */         if (o1 == null ? o2 != null : !o1.equals(o2)) {
/*  455 */           return false;
/*      */         }
/*      */       }
/*  458 */       return (!li1.hasNext()) && (!li2.hasNext());
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object get(int index)
/*      */   {
/*  474 */     if (this.fast) {
/*  475 */       return this.list.get(index);
/*      */     }
/*  477 */     synchronized (this.list) {
/*  478 */       return this.list.get(index);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  492 */     if (this.fast) {
/*  493 */       int hashCode = 1;
/*  494 */       Iterator i = this.list.iterator();
/*  495 */       while (i.hasNext()) {
/*  496 */         Object o = i.next();
/*  497 */         hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
/*      */       }
/*  499 */       return hashCode;
/*      */     }
/*  501 */     synchronized (this.list) {
/*  502 */       int hashCode = 1;
/*  503 */       Iterator i = this.list.iterator();
/*  504 */       while (i.hasNext()) {
/*  505 */         Object o = i.next();
/*  506 */         hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
/*      */       }
/*  508 */       return hashCode;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int indexOf(Object element)
/*      */   {
/*  524 */     if (this.fast) {
/*  525 */       return this.list.indexOf(element);
/*      */     }
/*  527 */     synchronized (this.list) {
/*  528 */       return this.list.indexOf(element);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  540 */     if (this.fast) {
/*  541 */       return this.list.isEmpty();
/*      */     }
/*  543 */     synchronized (this.list) {
/*  544 */       return this.list.isEmpty();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Iterator iterator()
/*      */   {
/*  561 */     if (this.fast) {
/*  562 */       return new ListIter(0);
/*      */     }
/*  564 */     return this.list.iterator();
/*      */   }
/*      */ 
/*      */   public int lastIndexOf(Object element)
/*      */   {
/*  578 */     if (this.fast) {
/*  579 */       return this.list.lastIndexOf(element);
/*      */     }
/*  581 */     synchronized (this.list) {
/*  582 */       return this.list.lastIndexOf(element);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ListIterator listIterator()
/*      */   {
/*  594 */     if (this.fast) {
/*  595 */       return new ListIter(0);
/*      */     }
/*  597 */     return this.list.listIterator();
/*      */   }
/*      */ 
/*      */   public ListIterator listIterator(int index)
/*      */   {
/*  612 */     if (this.fast) {
/*  613 */       return new ListIter(index);
/*      */     }
/*  615 */     return this.list.listIterator(index);
/*      */   }
/*      */ 
/*      */   public Object remove(int index)
/*      */   {
/*  630 */     if (this.fast) {
/*  631 */       synchronized (this) {
/*  632 */         ArrayList temp = (ArrayList)this.list.clone();
/*  633 */         Object result = temp.remove(index);
/*  634 */         this.list = temp;
/*  635 */         return result;
/*      */       }
/*      */     }
/*  638 */     synchronized (this.list) {
/*  639 */       return this.list.remove(index);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean remove(Object element)
/*      */   {
/*  654 */     if (this.fast) {
/*  655 */       synchronized (this) {
/*  656 */         ArrayList temp = (ArrayList)this.list.clone();
/*  657 */         boolean result = temp.remove(element);
/*  658 */         this.list = temp;
/*  659 */         return result;
/*      */       }
/*      */     }
/*  662 */     synchronized (this.list) {
/*  663 */       return this.list.remove(element);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean removeAll(Collection collection)
/*      */   {
/*  681 */     if (this.fast) {
/*  682 */       synchronized (this) {
/*  683 */         ArrayList temp = (ArrayList)this.list.clone();
/*  684 */         boolean result = temp.removeAll(collection);
/*  685 */         this.list = temp;
/*  686 */         return result;
/*      */       }
/*      */     }
/*  689 */     synchronized (this.list) {
/*  690 */       return this.list.removeAll(collection);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean retainAll(Collection collection)
/*      */   {
/*  708 */     if (this.fast) {
/*  709 */       synchronized (this) {
/*  710 */         ArrayList temp = (ArrayList)this.list.clone();
/*  711 */         boolean result = temp.retainAll(collection);
/*  712 */         this.list = temp;
/*  713 */         return result;
/*      */       }
/*      */     }
/*  716 */     synchronized (this.list) {
/*  717 */       return this.list.retainAll(collection);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object set(int index, Object element)
/*      */   {
/*  739 */     if (this.fast) {
/*  740 */       return this.list.set(index, element);
/*      */     }
/*  742 */     synchronized (this.list) {
/*  743 */       return this.list.set(index, element);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  755 */     if (this.fast) {
/*  756 */       return this.list.size();
/*      */     }
/*  758 */     synchronized (this.list) {
/*  759 */       return this.list.size();
/*      */     }
/*      */   }
/*      */ 
/*      */   public List subList(int fromIndex, int toIndex)
/*      */   {
/*  779 */     if (this.fast) {
/*  780 */       return new SubList(fromIndex, toIndex);
/*      */     }
/*  782 */     return this.list.subList(fromIndex, toIndex);
/*      */   }
/*      */ 
/*      */   public Object[] toArray()
/*      */   {
/*  793 */     if (this.fast) {
/*  794 */       return this.list.toArray();
/*      */     }
/*  796 */     synchronized (this.list) {
/*  797 */       return this.list.toArray();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object[] toArray(Object[] array)
/*      */   {
/*  818 */     if (this.fast) {
/*  819 */       return this.list.toArray(array);
/*      */     }
/*  821 */     synchronized (this.list) {
/*  822 */       return this.list.toArray(array);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  834 */     StringBuffer sb = new StringBuffer("FastArrayList[");
/*  835 */     sb.append(this.list.toString());
/*  836 */     sb.append("]");
/*  837 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   public void trimToSize()
/*      */   {
/*  849 */     if (this.fast)
/*  850 */       synchronized (this) {
/*  851 */         ArrayList temp = (ArrayList)this.list.clone();
/*  852 */         temp.trimToSize();
/*  853 */         this.list = temp;
/*      */       }
/*      */     else
/*  856 */       synchronized (this.list) {
/*  857 */         this.list.trimToSize();
/*      */       }
/*      */   }
/*      */ 
/*      */   private class ListIter
/*      */     implements ListIterator
/*      */   {
/*      */     private List expected;
/*      */     private ListIterator iter;
/* 1299 */     private int lastReturnedIndex = -1;
/*      */ 
/*      */     public ListIter(int i)
/*      */     {
/* 1303 */       this.expected = FastArrayList.this.list;
/* 1304 */       this.iter = get().listIterator(i);
/*      */     }
/*      */ 
/*      */     private void checkMod() {
/* 1308 */       if (FastArrayList.this.list != this.expected)
/* 1309 */         throw new ConcurrentModificationException();
/*      */     }
/*      */ 
/*      */     List get()
/*      */     {
/* 1314 */       return this.expected;
/*      */     }
/*      */ 
/*      */     public boolean hasNext() {
/* 1318 */       checkMod();
/* 1319 */       return this.iter.hasNext();
/*      */     }
/*      */ 
/*      */     public Object next() {
/* 1323 */       checkMod();
/* 1324 */       this.lastReturnedIndex = this.iter.nextIndex();
/* 1325 */       return this.iter.next();
/*      */     }
/*      */ 
/*      */     public boolean hasPrevious() {
/* 1329 */       checkMod();
/* 1330 */       return this.iter.hasPrevious();
/*      */     }
/*      */ 
/*      */     public Object previous() {
/* 1334 */       checkMod();
/* 1335 */       this.lastReturnedIndex = this.iter.previousIndex();
/* 1336 */       return this.iter.previous();
/*      */     }
/*      */ 
/*      */     public int previousIndex() {
/* 1340 */       checkMod();
/* 1341 */       return this.iter.previousIndex();
/*      */     }
/*      */ 
/*      */     public int nextIndex() {
/* 1345 */       checkMod();
/* 1346 */       return this.iter.nextIndex();
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 1350 */       checkMod();
/* 1351 */       if (this.lastReturnedIndex < 0) {
/* 1352 */         throw new IllegalStateException();
/*      */       }
/* 1354 */       get().remove(this.lastReturnedIndex);
/* 1355 */       this.expected = FastArrayList.this.list;
/* 1356 */       this.iter = get().listIterator(previousIndex());
/* 1357 */       this.lastReturnedIndex = -1;
/*      */     }
/*      */ 
/*      */     public void set(Object o) {
/* 1361 */       checkMod();
/* 1362 */       if (this.lastReturnedIndex < 0) {
/* 1363 */         throw new IllegalStateException();
/*      */       }
/* 1365 */       get().set(this.lastReturnedIndex, o);
/* 1366 */       this.expected = FastArrayList.this.list;
/* 1367 */       this.iter = get().listIterator(previousIndex() + 1);
/*      */     }
/*      */ 
/*      */     public void add(Object o) {
/* 1371 */       checkMod();
/* 1372 */       int i = nextIndex();
/* 1373 */       get().add(i, o);
/* 1374 */       this.iter = get().listIterator(i + 1);
/* 1375 */       this.lastReturnedIndex = 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SubList
/*      */     implements List
/*      */   {
/*      */     private int first;
/*      */     private int last;
/*      */     private List expected;
/*      */ 
/*      */     public SubList(int first, int last)
/*      */     {
/*  872 */       this.first = first;
/*  873 */       this.last = last;
/*  874 */       this.expected = FastArrayList.this.list;
/*      */     }
/*      */ 
/*      */     private List get(List l) {
/*  878 */       if (FastArrayList.this.list != this.expected) {
/*  879 */         throw new ConcurrentModificationException();
/*      */       }
/*  881 */       return l.subList(this.first, this.last);
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  885 */       if (FastArrayList.this.fast)
/*  886 */         synchronized (FastArrayList.this) {
/*  887 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/*  888 */           get(temp).clear();
/*  889 */           this.last = this.first;
/*  890 */           FastArrayList.access$002(FastArrayList.this, temp);
/*  891 */           this.expected = temp;
/*      */         }
/*      */       else
/*  894 */         synchronized (FastArrayList.this.list) {
/*  895 */           get(this.expected).clear();
/*      */         }
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/*  901 */       if (FastArrayList.this.fast) {
/*  902 */         synchronized (FastArrayList.this) {
/*  903 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/*  904 */           boolean r = get(temp).remove(o);
/*  905 */           if (r) {
/*  906 */             this.last -= 1;
/*      */           }
/*  908 */           FastArrayList.access$002(FastArrayList.this, temp);
/*  909 */           this.expected = temp;
/*  910 */           return r;
/*      */         }
/*      */       }
/*  913 */       synchronized (FastArrayList.this.list) {
/*  914 */         return get(this.expected).remove(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection o)
/*      */     {
/*  920 */       if (FastArrayList.this.fast) {
/*  921 */         synchronized (FastArrayList.this) {
/*  922 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/*  923 */           List sub = get(temp);
/*  924 */           boolean r = sub.removeAll(o);
/*  925 */           if (r) {
/*  926 */             this.last = (this.first + sub.size());
/*      */           }
/*  928 */           FastArrayList.access$002(FastArrayList.this, temp);
/*  929 */           this.expected = temp;
/*  930 */           return r;
/*      */         }
/*      */       }
/*  933 */       synchronized (FastArrayList.this.list) {
/*  934 */         return get(this.expected).removeAll(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean retainAll(Collection o)
/*      */     {
/*  940 */       if (FastArrayList.this.fast) {
/*  941 */         synchronized (FastArrayList.this) {
/*  942 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/*  943 */           List sub = get(temp);
/*  944 */           boolean r = sub.retainAll(o);
/*  945 */           if (r) {
/*  946 */             this.last = (this.first + sub.size());
/*      */           }
/*  948 */           FastArrayList.access$002(FastArrayList.this, temp);
/*  949 */           this.expected = temp;
/*  950 */           return r;
/*      */         }
/*      */       }
/*  953 */       synchronized (FastArrayList.this.list) {
/*  954 */         return get(this.expected).retainAll(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/*  960 */       if (FastArrayList.this.fast) {
/*  961 */         return get(this.expected).size();
/*      */       }
/*  963 */       synchronized (FastArrayList.this.list) {
/*  964 */         return get(this.expected).size();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/*  971 */       if (FastArrayList.this.fast) {
/*  972 */         return get(this.expected).isEmpty();
/*      */       }
/*  974 */       synchronized (FastArrayList.this.list) {
/*  975 */         return get(this.expected).isEmpty();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/*  981 */       if (FastArrayList.this.fast) {
/*  982 */         return get(this.expected).contains(o);
/*      */       }
/*  984 */       synchronized (FastArrayList.this.list) {
/*  985 */         return get(this.expected).contains(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean containsAll(Collection o)
/*      */     {
/*  991 */       if (FastArrayList.this.fast) {
/*  992 */         return get(this.expected).containsAll(o);
/*      */       }
/*  994 */       synchronized (FastArrayList.this.list) {
/*  995 */         return get(this.expected).containsAll(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object[] toArray(Object[] o)
/*      */     {
/* 1001 */       if (FastArrayList.this.fast) {
/* 1002 */         return get(this.expected).toArray(o);
/*      */       }
/* 1004 */       synchronized (FastArrayList.this.list) {
/* 1005 */         return get(this.expected).toArray(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object[] toArray()
/*      */     {
/* 1011 */       if (FastArrayList.this.fast) {
/* 1012 */         return get(this.expected).toArray();
/*      */       }
/* 1014 */       synchronized (FastArrayList.this.list) {
/* 1015 */         return get(this.expected).toArray();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean equals(Object o)
/*      */     {
/* 1022 */       if (o == this) {
/* 1023 */         return true;
/*      */       }
/* 1025 */       if (FastArrayList.this.fast) {
/* 1026 */         return get(this.expected).equals(o);
/*      */       }
/* 1028 */       synchronized (FastArrayList.this.list) {
/* 1029 */         return get(this.expected).equals(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 1035 */       if (FastArrayList.this.fast) {
/* 1036 */         return get(this.expected).hashCode();
/*      */       }
/* 1038 */       synchronized (FastArrayList.this.list) {
/* 1039 */         return get(this.expected).hashCode();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean add(Object o)
/*      */     {
/* 1045 */       if (FastArrayList.this.fast) {
/* 1046 */         synchronized (FastArrayList.this) {
/* 1047 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/* 1048 */           boolean r = get(temp).add(o);
/* 1049 */           if (r) {
/* 1050 */             this.last += 1;
/*      */           }
/* 1052 */           FastArrayList.access$002(FastArrayList.this, temp);
/* 1053 */           this.expected = temp;
/* 1054 */           return r;
/*      */         }
/*      */       }
/* 1057 */       synchronized (FastArrayList.this.list) {
/* 1058 */         return get(this.expected).add(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean addAll(Collection o)
/*      */     {
/* 1064 */       if (FastArrayList.this.fast) {
/* 1065 */         synchronized (FastArrayList.this) {
/* 1066 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/* 1067 */           boolean r = get(temp).addAll(o);
/* 1068 */           if (r) {
/* 1069 */             this.last += o.size();
/*      */           }
/* 1071 */           FastArrayList.access$002(FastArrayList.this, temp);
/* 1072 */           this.expected = temp;
/* 1073 */           return r;
/*      */         }
/*      */       }
/* 1076 */       synchronized (FastArrayList.this.list) {
/* 1077 */         return get(this.expected).addAll(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void add(int i, Object o)
/*      */     {
/* 1083 */       if (FastArrayList.this.fast)
/* 1084 */         synchronized (FastArrayList.this) {
/* 1085 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/* 1086 */           get(temp).add(i, o);
/* 1087 */           this.last += 1;
/* 1088 */           FastArrayList.access$002(FastArrayList.this, temp);
/* 1089 */           this.expected = temp;
/*      */         }
/*      */       else
/* 1092 */         synchronized (FastArrayList.this.list) {
/* 1093 */           get(this.expected).add(i, o);
/*      */         }
/*      */     }
/*      */ 
/*      */     public boolean addAll(int i, Collection o)
/*      */     {
/* 1099 */       if (FastArrayList.this.fast) {
/* 1100 */         synchronized (FastArrayList.this) {
/* 1101 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/* 1102 */           boolean r = get(temp).addAll(i, o);
/* 1103 */           FastArrayList.access$002(FastArrayList.this, temp);
/* 1104 */           if (r) {
/* 1105 */             this.last += o.size();
/*      */           }
/* 1107 */           this.expected = temp;
/* 1108 */           return r;
/*      */         }
/*      */       }
/* 1111 */       synchronized (FastArrayList.this.list) {
/* 1112 */         return get(this.expected).addAll(i, o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object remove(int i)
/*      */     {
/* 1118 */       if (FastArrayList.this.fast) {
/* 1119 */         synchronized (FastArrayList.this) {
/* 1120 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/* 1121 */           Object o = get(temp).remove(i);
/* 1122 */           this.last -= 1;
/* 1123 */           FastArrayList.access$002(FastArrayList.this, temp);
/* 1124 */           this.expected = temp;
/* 1125 */           return o;
/*      */         }
/*      */       }
/* 1128 */       synchronized (FastArrayList.this.list) {
/* 1129 */         return get(this.expected).remove(i);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Object set(int i, Object a)
/*      */     {
/* 1135 */       if (FastArrayList.this.fast) {
/* 1136 */         synchronized (FastArrayList.this) {
/* 1137 */           ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
/* 1138 */           Object o = get(temp).set(i, a);
/* 1139 */           FastArrayList.access$002(FastArrayList.this, temp);
/* 1140 */           this.expected = temp;
/* 1141 */           return o;
/*      */         }
/*      */       }
/* 1144 */       synchronized (FastArrayList.this.list) {
/* 1145 */         return get(this.expected).set(i, a);
/*      */       }
/*      */     }
/*      */ 
/*      */     public Iterator iterator()
/*      */     {
/* 1152 */       return new SubListIter(0);
/*      */     }
/*      */ 
/*      */     public ListIterator listIterator() {
/* 1156 */       return new SubListIter(0);
/*      */     }
/*      */ 
/*      */     public ListIterator listIterator(int i) {
/* 1160 */       return new SubListIter(i);
/*      */     }
/*      */ 
/*      */     public Object get(int i)
/*      */     {
/* 1165 */       if (FastArrayList.this.fast) {
/* 1166 */         return get(this.expected).get(i);
/*      */       }
/* 1168 */       synchronized (FastArrayList.this.list) {
/* 1169 */         return get(this.expected).get(i);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int indexOf(Object o)
/*      */     {
/* 1175 */       if (FastArrayList.this.fast) {
/* 1176 */         return get(this.expected).indexOf(o);
/*      */       }
/* 1178 */       synchronized (FastArrayList.this.list) {
/* 1179 */         return get(this.expected).indexOf(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int lastIndexOf(Object o)
/*      */     {
/* 1186 */       if (FastArrayList.this.fast) {
/* 1187 */         return get(this.expected).lastIndexOf(o);
/*      */       }
/* 1189 */       synchronized (FastArrayList.this.list) {
/* 1190 */         return get(this.expected).lastIndexOf(o);
/*      */       }
/*      */     }
/*      */ 
/*      */     public List subList(int f, int l)
/*      */     {
/* 1197 */       if (FastArrayList.this.list != this.expected) {
/* 1198 */         throw new ConcurrentModificationException();
/*      */       }
/* 1200 */       return new SubList(FastArrayList.this, this.first + f, f + l);
/*      */     }
/*      */ 
/*      */     private class SubListIter
/*      */       implements ListIterator
/*      */     {
/*      */       private List expected;
/*      */       private ListIterator iter;
/* 1208 */       private int lastReturnedIndex = -1;
/*      */ 
/*      */       public SubListIter(int i)
/*      */       {
/* 1212 */         this.expected = FastArrayList.this.list;
/* 1213 */         this.iter = FastArrayList.SubList.this.get(this.expected).listIterator(i);
/*      */       }
/*      */ 
/*      */       private void checkMod() {
/* 1217 */         if (FastArrayList.this.list != this.expected)
/* 1218 */           throw new ConcurrentModificationException();
/*      */       }
/*      */ 
/*      */       List get()
/*      */       {
/* 1223 */         return FastArrayList.SubList.this.get(this.expected);
/*      */       }
/*      */ 
/*      */       public boolean hasNext() {
/* 1227 */         checkMod();
/* 1228 */         return this.iter.hasNext();
/*      */       }
/*      */ 
/*      */       public Object next() {
/* 1232 */         checkMod();
/* 1233 */         this.lastReturnedIndex = this.iter.nextIndex();
/* 1234 */         return this.iter.next();
/*      */       }
/*      */ 
/*      */       public boolean hasPrevious() {
/* 1238 */         checkMod();
/* 1239 */         return this.iter.hasPrevious();
/*      */       }
/*      */ 
/*      */       public Object previous() {
/* 1243 */         checkMod();
/* 1244 */         this.lastReturnedIndex = this.iter.previousIndex();
/* 1245 */         return this.iter.previous();
/*      */       }
/*      */ 
/*      */       public int previousIndex() {
/* 1249 */         checkMod();
/* 1250 */         return this.iter.previousIndex();
/*      */       }
/*      */ 
/*      */       public int nextIndex() {
/* 1254 */         checkMod();
/* 1255 */         return this.iter.nextIndex();
/*      */       }
/*      */ 
/*      */       public void remove() {
/* 1259 */         checkMod();
/* 1260 */         if (this.lastReturnedIndex < 0) {
/* 1261 */           throw new IllegalStateException();
/*      */         }
/* 1263 */         get().remove(this.lastReturnedIndex);
/* 1264 */         FastArrayList.SubList.access$310(FastArrayList.SubList.this);
/* 1265 */         this.expected = FastArrayList.this.list;
/* 1266 */         this.iter = get().listIterator(previousIndex());
/* 1267 */         this.lastReturnedIndex = -1;
/*      */       }
/*      */ 
/*      */       public void set(Object o) {
/* 1271 */         checkMod();
/* 1272 */         if (this.lastReturnedIndex < 0) {
/* 1273 */           throw new IllegalStateException();
/*      */         }
/* 1275 */         get().set(this.lastReturnedIndex, o);
/* 1276 */         this.expected = FastArrayList.this.list;
/* 1277 */         this.iter = get().listIterator(previousIndex() + 1);
/*      */       }
/*      */ 
/*      */       public void add(Object o) {
/* 1281 */         checkMod();
/* 1282 */         int i = nextIndex();
/* 1283 */         get().add(i, o);
/* 1284 */         FastArrayList.SubList.access$308(FastArrayList.SubList.this);
/* 1285 */         this.iter = get().listIterator(i + 1);
/* 1286 */         this.lastReturnedIndex = 1;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.util.FastArrayList
 * JD-Core Version:    0.6.0
 */