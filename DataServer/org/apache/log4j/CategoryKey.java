/*    */ package org.apache.log4j;
/*    */ 
/*    */ class CategoryKey
/*    */ {
/*    */   String name;
/*    */   int hashCache;
/*    */ 
/*    */   CategoryKey(String name)
/*    */   {
/* 20 */     this.name = name.intern();
/* 21 */     this.hashCache = name.hashCode();
/*    */   }
/*    */ 
/*    */   public final int hashCode()
/*    */   {
/* 27 */     return this.hashCache;
/*    */   }
/*    */ 
/*    */   public final boolean equals(Object rArg)
/*    */   {
/* 33 */     if (this == rArg) {
/* 34 */       return true;
/*    */     }
/* 36 */     if ((rArg != null) && (CategoryKey.class == rArg.getClass())) {
/* 37 */       return this.name == ((CategoryKey)rArg).name;
/*    */     }
/* 39 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.CategoryKey
 * JD-Core Version:    0.6.0
 */