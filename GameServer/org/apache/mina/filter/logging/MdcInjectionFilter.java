/*     */ package org.apache.mina.filter.logging;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Arrays;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.mina.core.filterchain.IoFilterEvent;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.util.CommonEventFilter;
/*     */ import org.slf4j.MDC;
/*     */ 
/*     */ public class MdcInjectionFilter extends CommonEventFilter
/*     */ {
/*  81 */   private static final AttributeKey CONTEXT_KEY = new AttributeKey(MdcInjectionFilter.class, "context");
/*     */ 
/*  84 */   private ThreadLocal<Integer> callDepth = new ThreadLocal()
/*     */   {
/*     */     protected Integer initialValue() {
/*  87 */       return Integer.valueOf(0);
/*     */     }
/*  84 */   };
/*     */   private EnumSet<MdcKey> mdcKeys;
/*     */ 
/*     */   public MdcInjectionFilter(EnumSet<MdcKey> keys)
/*     */   {
/* 101 */     this.mdcKeys = keys.clone();
/*     */   }
/*     */ 
/*     */   public MdcInjectionFilter(MdcKey[] keys)
/*     */   {
/* 112 */     Set keySet = new HashSet(Arrays.asList(keys));
/* 113 */     this.mdcKeys = EnumSet.copyOf(keySet);
/*     */   }
/*     */ 
/*     */   public MdcInjectionFilter() {
/* 117 */     this.mdcKeys = EnumSet.allOf(MdcKey.class);
/*     */   }
/*     */ 
/*     */   protected void filter(IoFilterEvent event)
/*     */     throws Exception
/*     */   {
/* 124 */     int currentCallDepth = ((Integer)this.callDepth.get()).intValue();
/* 125 */     this.callDepth.set(Integer.valueOf(currentCallDepth + 1));
/* 126 */     Map context = getAndFillContext(event.getSession());
/*     */ 
/* 128 */     if (currentCallDepth == 0)
/*     */     {
/* 130 */       for (Map.Entry e : context.entrySet()) {
/* 131 */         MDC.put((String)e.getKey(), (String)e.getValue());
/*     */       }
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 137 */       event.fire();
/*     */     }
/*     */     finally
/*     */     {
/*     */       Iterator i$;
/*     */       String key;
/* 139 */       if (currentCallDepth == 0)
/*     */       {
/* 141 */         for (String key : context.keySet()) {
/* 142 */           MDC.remove(key);
/*     */         }
/* 144 */         this.callDepth.remove();
/*     */       } else {
/* 146 */         this.callDepth.set(Integer.valueOf(currentCallDepth));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Map<String, String> getAndFillContext(IoSession session) {
/* 152 */     Map context = getContext(session);
/* 153 */     if (context.isEmpty()) {
/* 154 */       fillContext(session, context);
/*     */     }
/* 156 */     return context;
/*     */   }
/*     */ 
/*     */   private static Map<String, String> getContext(IoSession session)
/*     */   {
/* 161 */     Map context = (Map)session.getAttribute(CONTEXT_KEY);
/* 162 */     if (context == null) {
/* 163 */       context = new ConcurrentHashMap();
/* 164 */       session.setAttribute(CONTEXT_KEY, context);
/*     */     }
/* 166 */     return context;
/*     */   }
/*     */ 
/*     */   protected void fillContext(IoSession session, Map<String, String> context)
/*     */   {
/* 176 */     if (this.mdcKeys.contains(MdcKey.handlerClass)) {
/* 177 */       context.put(MdcKey.handlerClass.name(), session.getHandler().getClass().getName());
/*     */     }
/*     */ 
/* 180 */     if (this.mdcKeys.contains(MdcKey.remoteAddress)) {
/* 181 */       context.put(MdcKey.remoteAddress.name(), session.getRemoteAddress().toString());
/*     */     }
/*     */ 
/* 184 */     if (this.mdcKeys.contains(MdcKey.localAddress)) {
/* 185 */       context.put(MdcKey.localAddress.name(), session.getLocalAddress().toString());
/*     */     }
/*     */ 
/* 188 */     if (session.getTransportMetadata().getAddressType() == InetSocketAddress.class) {
/* 189 */       InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
/*     */ 
/* 191 */       InetSocketAddress localAddress = (InetSocketAddress)session.getLocalAddress();
/*     */ 
/* 193 */       if (this.mdcKeys.contains(MdcKey.remoteIp)) {
/* 194 */         context.put(MdcKey.remoteIp.name(), remoteAddress.getAddress().getHostAddress());
/*     */       }
/*     */ 
/* 197 */       if (this.mdcKeys.contains(MdcKey.remotePort)) {
/* 198 */         context.put(MdcKey.remotePort.name(), String.valueOf(remoteAddress.getPort()));
/*     */       }
/*     */ 
/* 201 */       if (this.mdcKeys.contains(MdcKey.localIp)) {
/* 202 */         context.put(MdcKey.localIp.name(), localAddress.getAddress().getHostAddress());
/*     */       }
/*     */ 
/* 205 */       if (this.mdcKeys.contains(MdcKey.localPort))
/* 206 */         context.put(MdcKey.localPort.name(), String.valueOf(localAddress.getPort()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getProperty(IoSession session, String key)
/*     */   {
/* 213 */     if (key == null) {
/* 214 */       throw new NullPointerException("key should not be null");
/*     */     }
/*     */ 
/* 217 */     Map context = getContext(session);
/* 218 */     String answer = (String)context.get(key);
/* 219 */     if (answer != null) {
/* 220 */       return answer;
/*     */     }
/*     */ 
/* 223 */     return MDC.get(key);
/*     */   }
/*     */ 
/*     */   public static void setProperty(IoSession session, String key, String value)
/*     */   {
/* 235 */     if (key == null) {
/* 236 */       throw new NullPointerException("key should not be null");
/*     */     }
/* 238 */     if (value == null) {
/* 239 */       removeProperty(session, key);
/*     */     }
/* 241 */     Map context = getContext(session);
/* 242 */     context.put(key, value);
/* 243 */     MDC.put(key, value);
/*     */   }
/*     */ 
/*     */   public static void removeProperty(IoSession session, String key) {
/* 247 */     if (key == null) {
/* 248 */       throw new NullPointerException("key should not be null");
/*     */     }
/* 250 */     Map context = getContext(session);
/* 251 */     context.remove(key);
/* 252 */     MDC.remove(key);
/*     */   }
/*     */ 
/*     */   public static enum MdcKey
/*     */   {
/*  77 */     handlerClass, remoteAddress, localAddress, remoteIp, remotePort, localIp, localPort;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.logging.MdcInjectionFilter
 * JD-Core Version:    0.6.0
 */