/*    */ package flex.messaging.util;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class MethodKey
/*    */ {
/*    */   private Class enclosingClass;
/*    */   private String methodName;
/*    */   private Class[] parameterTypes;
/*    */ 
/*    */   public MethodKey(Class enclosingClass, String methodName, Class[] parameterTypes)
/*    */   {
/* 31 */     this.enclosingClass = enclosingClass;
/* 32 */     this.methodName = methodName;
/* 33 */     this.parameterTypes = parameterTypes;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object object)
/*    */   {
/*    */     boolean result;
/*    */     boolean result;
/* 39 */     if (this == object)
/*    */     {
/* 41 */       result = true;
/*    */     }
/*    */     else
/*    */     {
/*    */       boolean result;
/* 43 */       if ((object instanceof MethodKey))
/*    */       {
/* 45 */         MethodKey other = (MethodKey)object;
/* 46 */         result = (other.methodName.equals(this.methodName)) && (other.parameterTypes.length == this.parameterTypes.length) && (other.enclosingClass == this.enclosingClass) && (Arrays.equals(other.parameterTypes, this.parameterTypes));
/*    */       }
/*    */       else
/*    */       {
/* 54 */         result = false;
/*    */       }
/*    */     }
/* 56 */     return result;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 63 */     return this.enclosingClass.hashCode() * 10003 + this.methodName.hashCode();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.MethodKey
 * JD-Core Version:    0.6.0
 */