/*    */ package org.logicalcobwebs.cglib.beans;
/*    */ 
/*    */ import java.util.AbstractSet;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class FixedKeySet extends AbstractSet
/*    */ {
/*    */   private Set set;
/*    */   private int size;
/*    */ 
/*    */   public FixedKeySet(String[] keys)
/*    */   {
/* 25 */     this.size = keys.length;
/* 26 */     this.set = Collections.unmodifiableSet(new HashSet(Arrays.asList(keys)));
/*    */   }
/*    */ 
/*    */   public Iterator iterator() {
/* 30 */     return this.set.iterator();
/*    */   }
/*    */ 
/*    */   public int size() {
/* 34 */     return this.size;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.beans.FixedKeySet
 * JD-Core Version:    0.6.0
 */