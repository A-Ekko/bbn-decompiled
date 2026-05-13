/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class LRUCache extends LinkedHashMap
/*    */ {
/*    */   protected int maxElements;
/*    */ 
/*    */   public LRUCache(int maxSize)
/*    */   {
/* 38 */     super(maxSize);
/* 39 */     this.maxElements = maxSize;
/*    */   }
/*    */ 
/*    */   protected boolean removeEldestEntry(Map.Entry eldest)
/*    */   {
/* 48 */     return size() > this.maxElements;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.util.LRUCache
 * JD-Core Version:    0.6.0
 */