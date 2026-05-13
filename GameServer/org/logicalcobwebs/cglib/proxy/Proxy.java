/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Method;
/*    */ import org.logicalcobwebs.cglib.core.CodeGenerationException;
/*    */ 
/*    */ public class Proxy
/*    */   implements Serializable
/*    */ {
/*    */   protected InvocationHandler h;
/* 41 */   private static final CallbackFilter BAD_OBJECT_METHOD_FILTER = new CallbackFilter() {
/*    */     public int accept(Method method) {
/* 43 */       if (method.getDeclaringClass().getName().equals("java.lang.Object")) {
/* 44 */         String name = method.getName();
/* 45 */         if ((!name.equals("hashCode")) && (!name.equals("equals")) && (!name.equals("toString")))
/*    */         {
/* 48 */           return 1;
/*    */         }
/*    */       }
/* 51 */       return 0;
/*    */     }
/* 41 */   };
/*    */ 
/*    */   protected Proxy(InvocationHandler h)
/*    */   {
/* 56 */     Enhancer.registerCallbacks(getClass(), new Callback[] { h, null });
/* 57 */     this.h = h;
/*    */   }
/*    */ 
/*    */   public static InvocationHandler getInvocationHandler(Object proxy)
/*    */   {
/* 68 */     if (!(proxy instanceof ProxyImpl)) {
/* 69 */       throw new IllegalArgumentException("Object is not a proxy");
/*    */     }
/* 71 */     return ((Proxy)proxy).h;
/*    */   }
/*    */ 
/*    */   public static Class getProxyClass(ClassLoader loader, Class[] interfaces) {
/* 75 */     Enhancer e = new Enhancer();
/* 76 */     e.setSuperclass(ProxyImpl.class);
/* 77 */     e.setInterfaces(interfaces);
/* 78 */     e.setCallbackTypes(new Class[] { InvocationHandler.class, NoOp.class });
/*    */ 
/* 82 */     e.setCallbackFilter(BAD_OBJECT_METHOD_FILTER);
/* 83 */     e.setUseFactory(false);
/* 84 */     return e.createClass();
/*    */   }
/*    */ 
/*    */   public static boolean isProxyClass(Class cl) {
/* 88 */     return cl.getSuperclass().equals(ProxyImpl.class);
/*    */   }
/*    */ 
/*    */   public static Object newProxyInstance(ClassLoader loader, Class[] interfaces, InvocationHandler h) {
/*    */     try {
/* 93 */       Class clazz = getProxyClass(loader, interfaces);
/* 94 */       return clazz.getConstructor(new Class[] { InvocationHandler.class }).newInstance(new Object[] { h });
/*    */     } catch (RuntimeException e) {
/* 96 */       throw e; } catch (Exception e) {
/*    */     }
/* 98 */     throw new CodeGenerationException(e);
/*    */   }
/*    */ 
/*    */   private static class ProxyImpl extends Proxy
/*    */   {
/*    */     protected ProxyImpl(InvocationHandler h)
/*    */     {
/* 63 */       super();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.Proxy
 * JD-Core Version:    0.6.0
 */