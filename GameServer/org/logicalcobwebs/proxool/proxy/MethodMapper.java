/*    */ package org.logicalcobwebs.proxool.proxy;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.logicalcobwebs.proxool.ProxoolException;
/*    */ 
/*    */ public class MethodMapper
/*    */ {
/*    */   private Class concreteClass;
/* 30 */   private Map cachedConcreteMethods = new HashMap();
/*    */ 
/*    */   public MethodMapper(Class concreteClass)
/*    */   {
/* 37 */     this.concreteClass = concreteClass;
/*    */   }
/*    */ 
/*    */   protected Method getConcreteMethod(Method injectableMethod)
/*    */     throws ProxoolException
/*    */   {
/* 51 */     Method concreteMethod = (Method)this.cachedConcreteMethods.get(injectableMethod);
/* 52 */     if (concreteMethod == null)
/*    */     {
/* 54 */       Method[] candidateMethods = this.concreteClass.getMethods();
/* 55 */       for (int i = 0; i < candidateMethods.length; i++) {
/* 56 */         Method candidateMethod = candidateMethods[i];
/*    */ 
/* 58 */         if ((!candidateMethod.getName().equals(injectableMethod.getName())) || (candidateMethod.getParameterTypes().length != injectableMethod.getParameterTypes().length) || (!candidateMethod.getReturnType().equals(injectableMethod.getReturnType())))
/*    */         {
/*    */           continue;
/*    */         }
/* 62 */         boolean matches = true;
/* 63 */         Class[] candidateTypes = candidateMethod.getParameterTypes();
/* 64 */         Class[] injectableTypes = injectableMethod.getParameterTypes();
/* 65 */         for (int j = 0; j < candidateTypes.length; j++) {
/* 66 */           if (!candidateTypes[j].equals(injectableTypes[j])) {
/* 67 */             matches = false;
/* 68 */             break;
/*    */           }
/*    */         }
/*    */ 
/* 72 */         if (matches) {
/* 73 */           concreteMethod = candidateMethod;
/* 74 */           break;
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/* 79 */       if (concreteMethod == null) {
/* 80 */         throw new ProxoolException("Couldn't match injectable method " + injectableMethod + " with any of those " + "found in " + this.concreteClass.getName());
/*    */       }
/*    */ 
/* 84 */       this.cachedConcreteMethods.put(injectableMethod, concreteMethod);
/*    */     }
/* 86 */     return concreteMethod;
/*    */   }
/*    */ 
/*    */   public void overrideConcreteMethod(Method injectableMethod, Method overridenMethod)
/*    */   {
/* 95 */     this.cachedConcreteMethods.put(injectableMethod, overridenMethod);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.proxy.MethodMapper
 * JD-Core Version:    0.6.0
 */