/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ 
/*    */ public class Signature
/*    */ {
/*    */   private String name;
/*    */   private String desc;
/*    */ 
/*    */   public Signature(String name, String desc)
/*    */   {
/* 30 */     if (name.indexOf('(') >= 0) {
/* 31 */       throw new IllegalArgumentException("Name '" + name + "' is invalid");
/*    */     }
/* 33 */     this.name = name;
/* 34 */     this.desc = desc;
/*    */   }
/*    */ 
/*    */   public Signature(String name, Type returnType, Type[] argumentTypes) {
/* 38 */     this(name, Type.getMethodDescriptor(returnType, argumentTypes));
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 42 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getDescriptor() {
/* 46 */     return this.desc;
/*    */   }
/*    */ 
/*    */   public Type getReturnType() {
/* 50 */     return Type.getReturnType(this.desc);
/*    */   }
/*    */ 
/*    */   public Type[] getArgumentTypes() {
/* 54 */     return Type.getArgumentTypes(this.desc);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 58 */     return this.name + this.desc;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o) {
/* 62 */     if (o == null)
/* 63 */       return false;
/* 64 */     if (!(o instanceof Signature))
/* 65 */       return false;
/* 66 */     Signature other = (Signature)o;
/* 67 */     return (this.name.equals(other.name)) && (this.desc.equals(other.desc));
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 71 */     return this.name.hashCode() ^ this.desc.hashCode();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.Signature
 * JD-Core Version:    0.6.0
 */