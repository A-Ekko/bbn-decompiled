/*     */ package flex.messaging.util;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.io.TypeMarshaller;
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MethodMatcher
/*     */ {
/*  41 */   private final Map methodCache = new HashMap();
/*     */   private static final int ARGUMENT_CONVERSION_ERROR = 10006;
/*     */   private static final int CANNOT_INVOKE_METHOD = 10007;
/*     */ 
/*     */   public Method getMethod(Class c, String methodName, List parameters)
/*     */   {
/*  61 */     Method method = null;
/*     */ 
/*  64 */     Match bestMatch = new Match(methodName);
/*     */ 
/*  67 */     Class[] suppliedParamTypes = paramTypes(parameters);
/*     */ 
/*  70 */     MethodKey methodKey = new MethodKey(c, methodName, suppliedParamTypes);
/*     */ 
/*  72 */     if (this.methodCache.containsKey(methodKey))
/*     */     {
/*  74 */       method = (Method)this.methodCache.get(methodKey);
/*     */ 
/*  76 */       String thisMethodName = method.getName();
/*  77 */       bestMatch.matchedMethodName = thisMethodName;
/*     */ 
/*  81 */       Class[] desiredParamTypes = method.getParameterTypes();
/*  82 */       bestMatch.methodParamTypes = desiredParamTypes;
/*  83 */       convertParams(parameters, desiredParamTypes, bestMatch);
/*     */     }
/*     */     else
/*     */     {
/*  88 */       Method[] methods = c.getMethods();
/*  89 */       for (int i = 0; i < methods.length; i++)
/*     */       {
/*  91 */         Method thisMethod = methods[i];
/*     */ 
/*  93 */         String thisMethodName = thisMethod.getName();
/*     */ 
/*  98 */         if (!thisMethodName.equalsIgnoreCase(methodName)) {
/*     */           continue;
/*     */         }
/* 101 */         Match currentMatch = new Match(methodName);
/* 102 */         currentMatch.matchedMethodName = thisMethodName;
/*     */ 
/* 106 */         if (bestMatch.matchedMethodName == null)
/*     */         {
/* 108 */           bestMatch = currentMatch;
/*     */         }
/*     */ 
/* 112 */         Class[] desiredParamTypes = thisMethod.getParameterTypes();
/* 113 */         currentMatch.methodParamTypes = desiredParamTypes;
/*     */ 
/* 115 */         if (desiredParamTypes.length != suppliedParamTypes.length)
/*     */           continue;
/* 117 */         currentMatch.matchedByNumberOfParams = true;
/*     */ 
/* 121 */         if ((!bestMatch.matchedByNumberOfParams) && (bestMatch.matchedParamCount == 0))
/*     */         {
/* 123 */           bestMatch = currentMatch;
/*     */         }
/*     */ 
/* 127 */         convertParams(parameters, desiredParamTypes, currentMatch);
/*     */ 
/* 131 */         if (currentMatch.matchedParamCount >= bestMatch.matchedParamCount)
/*     */         {
/* 133 */           bestMatch = currentMatch;
/*     */         }
/*     */ 
/* 137 */         if (currentMatch.matchedParamCount != desiredParamTypes.length)
/*     */           continue;
/* 139 */         method = thisMethod;
/* 140 */         bestMatch = currentMatch;
/* 141 */         synchronized (this.methodCache)
/*     */         {
/* 143 */           Method method2 = (Method)this.methodCache.get(methodKey);
/* 144 */           if (method2 == null)
/* 145 */             this.methodCache.put(methodKey, method);
/*     */           else
/* 147 */             method = method2;
/*     */         }
/* 149 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 156 */     if (method == null)
/*     */     {
/* 158 */       methodNotFound(methodName, suppliedParamTypes, bestMatch);
/*     */     }
/* 160 */     else if (bestMatch.paramTypeConversionFailure != null)
/*     */     {
/* 163 */       MessageException me = new MessageException();
/* 164 */       me.setMessage(10006);
/* 165 */       me.setCode("Server.Processing");
/* 166 */       me.setRootCause(bestMatch.paramTypeConversionFailure);
/* 167 */       throw me;
/*     */     }
/*     */ 
/* 170 */     return method;
/*     */   }
/*     */ 
/*     */   public static void convertParams(List parameters, Class[] desiredParamTypes, Match currentMatch)
/*     */   {
/* 185 */     int matchCount = 0;
/*     */ 
/* 187 */     currentMatch.matchedParamCount = 0;
/* 188 */     currentMatch.convertedSuppliedTypes = new Class[desiredParamTypes.length];
/*     */ 
/* 190 */     TypeMarshaller marshaller = TypeMarshallingContext.getTypeMarshaller();
/*     */ 
/* 192 */     for (int i = 0; i < desiredParamTypes.length; i++)
/*     */     {
/* 194 */       Object param = parameters.get(i);
/*     */ 
/* 197 */       if (param != null)
/*     */       {
/* 199 */         Object obj = null;
/*     */ 
/* 201 */         if (marshaller != null)
/*     */         {
/*     */           try
/*     */           {
/* 205 */             obj = marshaller.convert(param, desiredParamTypes[i]);
/*     */           }
/*     */           catch (MessageException ex)
/*     */           {
/* 209 */             currentMatch.paramTypeConversionFailure = ex;
/* 210 */             break;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 215 */           obj = param;
/*     */         }
/*     */ 
/* 218 */         currentMatch.convertedSuppliedTypes[i] = (obj != null ? obj.getClass() : null);
/*     */ 
/* 223 */         if ((!desiredParamTypes[i].isAssignableFrom(obj.getClass())) && ((desiredParamTypes[i] != Integer.TYPE) || (!Integer.class.isAssignableFrom(obj.getClass()))) && ((desiredParamTypes[i] != Double.TYPE) || (!Double.class.isAssignableFrom(obj.getClass()))) && ((desiredParamTypes[i] != Long.TYPE) || (!Long.class.isAssignableFrom(obj.getClass()))) && ((desiredParamTypes[i] != Boolean.TYPE) || (!Boolean.class.isAssignableFrom(obj.getClass()))) && ((desiredParamTypes[i] != Character.TYPE) || (!Character.class.isAssignableFrom(obj.getClass()))) && ((desiredParamTypes[i] != Float.TYPE) || (!Float.class.isAssignableFrom(obj.getClass()))) && ((desiredParamTypes[i] != Short.TYPE) || (!Short.class.isAssignableFrom(obj.getClass()))) && ((desiredParamTypes[i] != Byte.TYPE) || (!Byte.class.isAssignableFrom(obj.getClass()))))
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 233 */         parameters.set(i, obj);
/* 234 */         matchCount++;
/*     */       }
/*     */       else
/*     */       {
/* 243 */         matchCount++;
/*     */       }
/*     */     }
/*     */ 
/* 247 */     currentMatch.matchedParamCount = matchCount;
/*     */   }
/*     */ 
/*     */   public static Class[] paramTypes(List parameters)
/*     */   {
/* 261 */     Class[] paramTypes = new Class[parameters.size()];
/* 262 */     for (int i = 0; i < paramTypes.length; i++)
/*     */     {
/* 264 */       Object p = parameters.get(i);
/* 265 */       paramTypes[i] = (p == null ? Object.class : p.getClass());
/*     */     }
/* 267 */     return paramTypes;
/*     */   }
/*     */ 
/*     */   public static void methodNotFound(String methodName, Class[] suppliedParamTypes, Match bestMatch)
/*     */   {
/* 282 */     int errorCode = 10007;
/* 283 */     Object[] errorParams = { methodName };
/* 284 */     String errorDetailVariant = "0";
/*     */ 
/* 286 */     Object[] errorDetailParams = { methodName };
/*     */ 
/* 288 */     if (bestMatch.matchedMethodName != null)
/*     */     {
/* 291 */       errorCode = 10007;
/* 292 */       errorParams = new Object[] { bestMatch.matchedMethodName };
/*     */ 
/* 294 */       int suppliedParamCount = suppliedParamTypes.length;
/* 295 */       int expectedParamCount = bestMatch.methodParamTypes != null ? bestMatch.methodParamTypes.length : 0;
/*     */ 
/* 297 */       if (suppliedParamCount != expectedParamCount)
/*     */       {
/* 300 */         errorDetailVariant = "1";
/* 301 */         errorDetailParams = new Object[] { new Integer(suppliedParamCount), new Integer(expectedParamCount) };
/*     */       }
/*     */       else
/*     */       {
/* 306 */         String suppliedTypes = bestMatch.listTypes(suppliedParamTypes);
/* 307 */         String convertedTypes = bestMatch.listConvertedTypes();
/* 308 */         String expectedTypes = bestMatch.listExpectedTypes();
/*     */ 
/* 310 */         if (expectedTypes != null)
/*     */         {
/* 312 */           if (suppliedTypes != null)
/*     */           {
/* 314 */             if (convertedTypes != null)
/*     */             {
/* 319 */               errorDetailVariant = "2";
/* 320 */               errorDetailParams = new Object[] { expectedTypes, suppliedTypes, convertedTypes };
/*     */             }
/*     */             else
/*     */             {
/* 327 */               errorDetailVariant = "3";
/* 328 */               errorDetailParams = new Object[] { expectedTypes, suppliedTypes };
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 335 */             errorDetailVariant = "4";
/* 336 */             errorDetailParams = new Object[] { expectedTypes };
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 342 */           errorDetailVariant = "5";
/* 343 */           errorDetailParams = new Object[] { suppliedTypes };
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 348 */     MessageException ex = new MessageException();
/* 349 */     ex.setMessage(errorCode, errorParams);
/* 350 */     ex.setCode("Server.ResourceUnavailable");
/* 351 */     if (errorDetailVariant != null) {
/* 352 */       ex.setDetails(errorCode, errorDetailVariant, errorDetailParams);
/*     */     }
/* 354 */     if (bestMatch.paramTypeConversionFailure != null) {
/* 355 */       ex.setRootCause(bestMatch.paramTypeConversionFailure);
/*     */     }
/* 357 */     throw ex; } 
/*     */   public static class Match { final String methodName;
/*     */     String matchedMethodName;
/*     */     boolean matchedByNumberOfParams;
/*     */     int matchedParamCount;
/*     */     Class[] methodParamTypes;
/*     */     Class[] convertedSuppliedTypes;
/*     */     Exception paramTypeConversionFailure;
/*     */ 
/* 369 */     public Match(String name) { this.methodName = name;
/*     */     }
/*     */ 
/*     */     public boolean matchedExactlyByName()
/*     */     {
/* 374 */       if (this.matchedMethodName != null) {
/* 375 */         return this.matchedMethodName.equals(this.methodName);
/*     */       }
/* 377 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean matchedLooselyByName()
/*     */     {
/* 382 */       if (this.matchedMethodName != null) {
/* 383 */         return (!matchedExactlyByName()) && (this.matchedMethodName.equalsIgnoreCase(this.methodName));
/*     */       }
/* 385 */       return false;
/*     */     }
/*     */ 
/*     */     public String listExpectedTypes()
/*     */     {
/* 390 */       return listTypes(this.methodParamTypes);
/*     */     }
/*     */ 
/*     */     public String listConvertedTypes()
/*     */     {
/* 395 */       return listTypes(this.convertedSuppliedTypes);
/*     */     }
/*     */ 
/*     */     public String listTypes(Class[] types)
/*     */     {
/* 400 */       if ((types != null) && (types.length > 0))
/*     */       {
/* 402 */         StringBuffer sb = new StringBuffer();
/*     */ 
/* 404 */         for (int i = 0; i < types.length; i++)
/*     */         {
/* 406 */           if (i > 0) {
/* 407 */             sb.append(", ");
/*     */           }
/* 409 */           Class c = types[i];
/*     */ 
/* 411 */           if (c != null)
/*     */           {
/* 413 */             if (c.isArray())
/*     */             {
/* 415 */               c = c.getComponentType();
/* 416 */               sb.append(c.getName()).append("[]");
/*     */             }
/*     */             else
/*     */             {
/* 420 */               sb.append(c.getName());
/*     */             }
/*     */           }
/*     */           else {
/* 424 */             sb.append("null");
/*     */           }
/*     */         }
/* 427 */         return sb.toString();
/*     */       }
/*     */ 
/* 430 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.MethodMatcher
 * JD-Core Version:    0.6.0
 */