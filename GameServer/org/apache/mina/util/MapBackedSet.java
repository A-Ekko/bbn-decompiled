/*    */ package org.apache.mina.util;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.AbstractSet;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class MapBackedSet<E> extends AbstractSet<E>
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -8347878570391674042L;
/*    */   protected final Map<E, Boolean> map;
/*    */ 
/*    */   public MapBackedSet(Map<E, Boolean> map)
/*    */   {
/* 42 */     this.map = map;
/*    */   }
/*    */ 
/*    */   public MapBackedSet(Map<E, Boolean> map, Collection<E> c) {
/* 46 */     this.map = map;
/* 47 */     addAll(c);
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 52 */     return this.map.size();
/*    */   }
/*    */ 
/*    */   public boolean contains(Object o)
/*    */   {
/* 57 */     return this.map.containsKey(o);
/*    */   }
/*    */ 
/*    */   public Iterator<E> iterator()
/*    */   {
/* 62 */     return this.map.keySet().iterator();
/*    */   }
/*    */ 
/*    */   public boolean add(E o)
/*    */   {
/* 67 */     return this.map.put(o, Boolean.TRUE) == null;
/*    */   }
/*    */ 
/*    */   public boolean remove(Object o)
/*    */   {
/* 72 */     return this.map.remove(o) != null;
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 77 */     this.map.clear();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.MapBackedSet
 * JD-Core Version:    0.6.0
 */