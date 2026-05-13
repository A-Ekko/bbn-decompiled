/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class ExceptionUtil
/*     */ {
/*  37 */   public static String[] unwrapMethods = { "getRootCause", "getTargetException", "getTargetError", "getException", "getCausedByException", "getLinkedException" };
/*     */ 
/*     */   public static Throwable wrappedException(Throwable t)
/*     */   {
/*  44 */     if ((t instanceof InvocationTargetException))
/*     */     {
/*  46 */       return ((InvocationTargetException)t).getTargetException();
/*     */     }
/*     */ 
/*  49 */     return getRootCauseWithReflection(t);
/*     */   }
/*     */ 
/*     */   public static Throwable baseException(Throwable t)
/*     */   {
/*  56 */     Throwable wrapped = wrappedException(t);
/*  57 */     if (wrapped != null) {
/*  58 */       return baseException(wrapped);
/*     */     }
/*  60 */     return t;
/*     */   }
/*     */ 
/*     */   public static String toString(Throwable t)
/*     */   {
/*  67 */     StringWriter strWrt = new StringWriter();
/*  68 */     t.printStackTrace(new PrintWriter(strWrt));
/*     */ 
/*  70 */     return strWrt.toString();
/*     */   }
/*     */ 
/*     */   public static String getStackTraceUpTo(Throwable t, String prefix)
/*     */   {
/*  79 */     StringTokenizer tokens = new StringTokenizer(toString(t), "\n\r");
/*     */ 
/*  81 */     StringBuffer trace = new StringBuffer();
/*     */ 
/*  83 */     boolean done = false;
/*     */ 
/*  85 */     String lookingFor = "at " + prefix;
/*  86 */     while ((!done) && (tokens.hasMoreElements()))
/*     */     {
/*  88 */       String token = tokens.nextToken();
/*  89 */       if (token.indexOf(lookingFor) == -1)
/*  90 */         trace.append(token);
/*     */       else
/*  92 */         done = true;
/*  93 */       trace.append("\n");
/*     */     }
/*     */ 
/*  96 */     return trace.toString();
/*     */   }
/*     */ 
/*     */   public static String getStackTraceLines(Throwable t, int numLines)
/*     */   {
/* 105 */     StringTokenizer tokens = new StringTokenizer(toString(t), "\n\r");
/*     */ 
/* 107 */     StringBuffer trace = new StringBuffer();
/*     */ 
/* 109 */     for (int i = 0; i < numLines; i++)
/*     */     {
/* 111 */       String token = tokens.nextToken();
/* 112 */       trace.append(token);
/* 113 */       trace.append("\n");
/*     */     }
/*     */ 
/* 116 */     return trace.toString();
/*     */   }
/*     */ 
/*     */   public static String getCallAt(Throwable t, int nth)
/*     */   {
/* 124 */     StringTokenizer tokens = new StringTokenizer(toString(t), "\n\r");
/*     */     try
/*     */     {
/* 127 */       for (int i = 0; i <= nth; i++) {
/* 128 */         tokens.nextToken();
/*     */       }
/*     */ 
/* 131 */       String token = tokens.nextToken();
/* 132 */       int index1 = token.indexOf(' ');
/* 133 */       int index2 = token.indexOf('(');
/* 134 */       StringBuffer call = new StringBuffer();
/* 135 */       call.append(token.substring(index1 < 0 ? 0 : index1 + 1, index2 < 0 ? call.length() : index2));
/*     */ 
/* 137 */       int index3 = token.indexOf(':', index2 < 0 ? 0 : index2);
/* 138 */       if (index3 >= 0) {
/* 139 */         int index4 = token.indexOf(')', index3);
/* 140 */         call.append(token.substring(index3, index4 < 0 ? token.length() : index4));
/*     */       }
/* 142 */       return call.toString();
/*     */     }
/*     */     catch (NoSuchElementException e) {
/*     */     }
/* 146 */     return "unknown";
/*     */   }
/*     */ 
/*     */   public static String exceptionToString(Throwable t)
/*     */   {
/* 158 */     StringWriter sw = new StringWriter();
/* 159 */     PrintWriter out = new PrintWriter(sw);
/*     */ 
/* 162 */     printExceptionStack(t, out, 0);
/* 163 */     return sw.toString();
/*     */   }
/*     */ 
/*     */   protected static void printExceptionStack(Throwable th, PrintWriter out, int depth)
/*     */   {
/* 171 */     boolean printStackDepth = depth > 0;
/*     */ 
/* 173 */     Throwable wrappedException = wrappedException(th);
/* 174 */     if (wrappedException != null)
/*     */     {
/* 176 */       printStackDepth = true;
/* 177 */       printExceptionStack(wrappedException, out, depth + 1);
/*     */     }
/*     */ 
/* 180 */     if (printStackDepth) {
/* 181 */       out.write("[" + depth + "]");
/*     */     }
/*     */ 
/* 184 */     th.printStackTrace(out);
/*     */   }
/*     */ 
/*     */   private static Throwable getRootCauseWithReflection(Throwable t)
/*     */   {
/* 189 */     for (int i = 0; i < unwrapMethods.length; i++)
/*     */     {
/* 191 */       Method m = null;
/*     */       try
/*     */       {
/* 195 */         m = t.getClass().getMethod(unwrapMethods[i], null);
/* 196 */         return (Throwable)m.invoke(t, null);
/*     */       }
/*     */       catch (Exception nsme)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 204 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.ExceptionUtil
 * JD-Core Version:    0.6.0
 */