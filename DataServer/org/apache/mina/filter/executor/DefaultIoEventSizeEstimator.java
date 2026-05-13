/*     */ package org.apache.mina.filter.executor;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.session.IoEvent;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class DefaultIoEventSizeEstimator
/*     */   implements IoEventSizeEstimator
/*     */ {
/*  49 */   private final ConcurrentMap<Class<?>, Integer> class2size = new ConcurrentHashMap();
/*     */ 
/*     */   public DefaultIoEventSizeEstimator()
/*     */   {
/*  56 */     this.class2size.put(Boolean.TYPE, Integer.valueOf(4));
/*  57 */     this.class2size.put(Byte.TYPE, Integer.valueOf(1));
/*  58 */     this.class2size.put(Character.TYPE, Integer.valueOf(2));
/*  59 */     this.class2size.put(Integer.TYPE, Integer.valueOf(4));
/*  60 */     this.class2size.put(Short.TYPE, Integer.valueOf(2));
/*  61 */     this.class2size.put(Long.TYPE, Integer.valueOf(8));
/*  62 */     this.class2size.put(Float.TYPE, Integer.valueOf(4));
/*  63 */     this.class2size.put(Double.TYPE, Integer.valueOf(8));
/*  64 */     this.class2size.put(Void.TYPE, Integer.valueOf(0));
/*     */   }
/*     */ 
/*     */   public int estimateSize(IoEvent event)
/*     */   {
/*  71 */     return estimateSize(event) + estimateSize(event.getParameter());
/*     */   }
/*     */ 
/*     */   public int estimateSize(Object message)
/*     */   {
/*  80 */     if (message == null) {
/*  81 */       return 8;
/*     */     }
/*     */ 
/*  84 */     int answer = 8 + estimateSize(message.getClass(), null);
/*     */     Iterator i$;
/*  86 */     if ((message instanceof IoBuffer))
/*  87 */       answer += ((IoBuffer)message).remaining();
/*  88 */     else if ((message instanceof WriteRequest))
/*  89 */       answer += estimateSize(((WriteRequest)message).getMessage());
/*  90 */     else if ((message instanceof CharSequence))
/*  91 */       answer += (((CharSequence)message).length() << 1);
/*  92 */     else if ((message instanceof Iterable)) {
/*  93 */       for (i$ = ((Iterable)message).iterator(); i$.hasNext(); ) { Object m = i$.next();
/*  94 */         answer += estimateSize(m);
/*     */       }
/*     */     }
/*     */ 
/*  98 */     return align(answer);
/*     */   }
/*     */ 
/*     */   private int estimateSize(Class<?> clazz, Set<Class<?>> visitedClasses) {
/* 102 */     Integer objectSize = (Integer)this.class2size.get(clazz);
/* 103 */     if (objectSize != null) {
/* 104 */       return objectSize.intValue();
/*     */     }
/*     */ 
/* 107 */     if (visitedClasses != null) {
/* 108 */       if (visitedClasses.contains(clazz))
/* 109 */         return 0;
/*     */     }
/*     */     else {
/* 112 */       visitedClasses = new HashSet();
/*     */     }
/*     */ 
/* 115 */     visitedClasses.add(clazz);
/*     */ 
/* 117 */     int answer = 8;
/* 118 */     for (Class c = clazz; c != null; c = c.getSuperclass()) {
/* 119 */       Field[] fields = c.getDeclaredFields();
/* 120 */       for (Field f : fields) {
/* 121 */         if ((f.getModifiers() & 0x8) != 0)
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 126 */         answer += estimateSize(f.getType(), visitedClasses);
/*     */       }
/*     */     }
/*     */ 
/* 130 */     visitedClasses.remove(clazz);
/*     */ 
/* 133 */     answer = align(answer);
/*     */ 
/* 136 */     this.class2size.putIfAbsent(clazz, Integer.valueOf(answer));
/* 137 */     return answer;
/*     */   }
/*     */ 
/*     */   private static int align(int size) {
/* 141 */     if (size % 8 != 0) {
/* 142 */       size /= 8;
/* 143 */       size++;
/* 144 */       size *= 8;
/*     */     }
/* 146 */     return size;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.executor.DefaultIoEventSizeEstimator
 * JD-Core Version:    0.6.0
 */