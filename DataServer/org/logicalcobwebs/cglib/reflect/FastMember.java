/*    */ package org.logicalcobwebs.cglib.reflect;
/*    */ 
/*    */ import java.lang.reflect.Member;
/*    */ 
/*    */ public abstract class FastMember
/*    */ {
/*    */   protected FastClass fc;
/*    */   protected Member member;
/*    */   protected int index;
/*    */ 
/*    */   protected FastMember(FastClass fc, Member member, int index)
/*    */   {
/* 27 */     this.fc = fc;
/* 28 */     this.member = member;
/* 29 */     this.index = index; } 
/*    */   public abstract Class[] getParameterTypes();
/*    */ 
/*    */   public abstract Class[] getExceptionTypes();
/*    */ 
/* 36 */   public int getIndex() { return this.index; }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 40 */     return this.member.getName();
/*    */   }
/*    */ 
/*    */   public Class getDeclaringClass() {
/* 44 */     return this.fc.getJavaClass();
/*    */   }
/*    */ 
/*    */   public int getModifiers() {
/* 48 */     return this.member.getModifiers();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 52 */     return this.member.toString();
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 56 */     return this.member.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o) {
/* 60 */     if ((o == null) || (!(o instanceof FastMember))) {
/* 61 */       return false;
/*    */     }
/* 63 */     return this.member.equals(((FastMember)o).member);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.reflect.FastMember
 * JD-Core Version:    0.6.0
 */