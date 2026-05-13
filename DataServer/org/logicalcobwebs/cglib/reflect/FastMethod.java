/*    */ package org.logicalcobwebs.cglib.reflect;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class FastMethod extends FastMember
/*    */ {
/*    */   FastMethod(FastClass fc, Method method)
/*    */   {
/* 24 */     super(fc, method, helper(fc, method));
/*    */   }
/*    */ 
/*    */   private static int helper(FastClass fc, Method method) {
/* 28 */     int index = fc.getIndex(method.getName(), method.getParameterTypes());
/* 29 */     if (index < 0) {
/* 30 */       Class[] types = method.getParameterTypes();
/* 31 */       System.err.println("hash=" + method.getName().hashCode() + " size=" + types.length);
/* 32 */       for (int i = 0; i < types.length; i++) {
/* 33 */         System.err.println("  types[" + i + "]=" + types[i].getName());
/*    */       }
/* 35 */       throw new IllegalArgumentException("Cannot find method " + method);
/*    */     }
/* 37 */     return index;
/*    */   }
/*    */ 
/*    */   public Class getReturnType() {
/* 41 */     return ((Method)this.member).getReturnType();
/*    */   }
/*    */ 
/*    */   public Class[] getParameterTypes() {
/* 45 */     return ((Method)this.member).getParameterTypes();
/*    */   }
/*    */ 
/*    */   public Class[] getExceptionTypes() {
/* 49 */     return ((Method)this.member).getExceptionTypes();
/*    */   }
/*    */ 
/*    */   public Object invoke(Object obj, Object[] args) throws InvocationTargetException {
/* 53 */     return this.fc.invoke(this.index, obj, args);
/*    */   }
/*    */ 
/*    */   public Method getJavaMethod() {
/* 57 */     return (Method)this.member;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.reflect.FastMethod
 * JD-Core Version:    0.6.0
 */