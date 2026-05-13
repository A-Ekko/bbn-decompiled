/*    */ package org.apache.mina.util;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ 
/*    */ public class ConcurrentHashSet<E> extends MapBackedSet<E>
/*    */ {
/*    */   private static final long serialVersionUID = 8518578988740277828L;
/*    */ 
/*    */   public ConcurrentHashSet()
/*    */   {
/* 38 */     super(new ConcurrentHashMap());
/*    */   }
/*    */ 
/*    */   public ConcurrentHashSet(Collection<E> c) {
/* 42 */     super(new ConcurrentHashMap(), c);
/*    */   }
/*    */ 
/*    */   public boolean add(E o)
/*    */   {
/* 47 */     Boolean answer = (Boolean)((ConcurrentMap)this.map).putIfAbsent(o, Boolean.TRUE);
/* 48 */     return answer == null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.ConcurrentHashSet
 * JD-Core Version:    0.6.0
 */