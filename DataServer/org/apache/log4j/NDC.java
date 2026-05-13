/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Stack;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public class NDC
/*     */ {
/* 105 */   static Hashtable ht = new Hashtable();
/*     */ 
/* 107 */   static int pushCounter = 0;
/*     */   static final int REAP_THRESHOLD = 5;
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 133 */     Stack stack = (Stack)ht.get(Thread.currentThread());
/* 134 */     if (stack != null)
/* 135 */       stack.setSize(0);
/*     */   }
/*     */ 
/*     */   public static Stack cloneStack()
/*     */   {
/* 156 */     Object o = ht.get(Thread.currentThread());
/* 157 */     if (o == null) {
/* 158 */       return null;
/*     */     }
/* 160 */     Stack stack = (Stack)o;
/* 161 */     return (Stack)stack.clone();
/*     */   }
/*     */ 
/*     */   public static void inherit(Stack stack)
/*     */   {
/* 189 */     if (stack != null)
/* 190 */       ht.put(Thread.currentThread(), stack);
/*     */   }
/*     */ 
/*     */   public static String get()
/*     */   {
/* 201 */     Stack s = (Stack)ht.get(Thread.currentThread());
/* 202 */     if ((s != null) && (!s.isEmpty())) {
/* 203 */       return ((DiagnosticContext)s.peek()).fullMessage;
/*     */     }
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */   public static int getDepth()
/*     */   {
/* 217 */     Stack stack = (Stack)ht.get(Thread.currentThread());
/* 218 */     if (stack == null) {
/* 219 */       return 0;
/*     */     }
/* 221 */     return stack.size();
/*     */   }
/*     */ 
/*     */   private static void lazyRemove()
/*     */   {
/*     */     Vector v;
/* 233 */     synchronized (ht)
/*     */     {
/* 235 */       if (++pushCounter <= 5) {
/* 236 */         return;
/*     */       }
/* 238 */       pushCounter = 0;
/*     */ 
/* 241 */       int misses = 0;
/* 242 */       v = new Vector();
/* 243 */       Enumeration enum = ht.keys();
/*     */       do
/*     */       {
/* 249 */         Thread t = (Thread)enum.nextElement();
/* 250 */         if (t.isAlive()) {
/* 251 */           misses++;
/*     */         } else {
/* 253 */           misses = 0;
/* 254 */           v.addElement(t);
/*     */         }
/* 248 */         if (!enum.hasMoreElements()) break; 
/* 248 */       }while (misses <= 4);
/*     */     }
/*     */ 
/* 259 */     int size = v.size();
/* 260 */     for (int i = 0; i < size; i++) {
/* 261 */       Thread t = (Thread)v.elementAt(i);
/* 262 */       LogLog.debug("Lazy NDC removal for thread [" + t.getName() + "] (" + ht.size() + ").");
/*     */ 
/* 264 */       ht.remove(t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String pop()
/*     */   {
/* 281 */     Thread key = Thread.currentThread();
/* 282 */     Stack stack = (Stack)ht.get(key);
/* 283 */     if ((stack != null) && (!stack.isEmpty())) {
/* 284 */       return ((DiagnosticContext)stack.pop()).message;
/*     */     }
/* 286 */     return "";
/*     */   }
/*     */ 
/*     */   public static String peek()
/*     */   {
/* 302 */     Thread key = Thread.currentThread();
/* 303 */     Stack stack = (Stack)ht.get(key);
/* 304 */     if ((stack != null) && (!stack.isEmpty())) {
/* 305 */       return ((DiagnosticContext)stack.peek()).message;
/*     */     }
/* 307 */     return "";
/*     */   }
/*     */ 
/*     */   public static void push(String message)
/*     */   {
/* 320 */     Thread key = Thread.currentThread();
/* 321 */     Stack stack = (Stack)ht.get(key);
/*     */ 
/* 323 */     if (stack == null) {
/* 324 */       DiagnosticContext dc = new DiagnosticContext(message, null);
/* 325 */       stack = new Stack();
/* 326 */       ht.put(key, stack);
/* 327 */       stack.push(dc);
/* 328 */     } else if (stack.isEmpty()) {
/* 329 */       DiagnosticContext dc = new DiagnosticContext(message, null);
/* 330 */       stack.push(dc);
/*     */     } else {
/* 332 */       DiagnosticContext parent = (DiagnosticContext)stack.peek();
/* 333 */       stack.push(new DiagnosticContext(message, parent));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void remove()
/*     */   {
/* 359 */     ht.remove(Thread.currentThread());
/*     */ 
/* 362 */     lazyRemove();
/*     */   }
/*     */ 
/*     */   public static void setMaxDepth(int maxDepth)
/*     */   {
/* 395 */     Stack stack = (Stack)ht.get(Thread.currentThread());
/* 396 */     if ((stack != null) && (maxDepth < stack.size()))
/* 397 */       stack.setSize(maxDepth);
/*     */   }
/*     */ 
/*     */   private static class DiagnosticContext {
/*     */     String fullMessage;
/*     */     String message;
/*     */ 
/*     */     DiagnosticContext(String message, DiagnosticContext parent) {
/* 407 */       this.message = message;
/* 408 */       if (parent != null)
/* 409 */         this.fullMessage = (parent.fullMessage + ' ' + message);
/*     */       else
/* 411 */         this.fullMessage = message;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.NDC
 * JD-Core Version:    0.6.0
 */