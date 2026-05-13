/*     */ package org.apache.mina.filter.reqres;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.filter.util.WriteRequestFilter;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class RequestResponseFilter extends WriteRequestFilter
/*     */ {
/*  50 */   private final AttributeKey RESPONSE_INSPECTOR = new AttributeKey(getClass(), "responseInspector");
/*  51 */   private final AttributeKey REQUEST_STORE = new AttributeKey(getClass(), "requestStore");
/*  52 */   private final AttributeKey UNRESPONDED_REQUEST_STORE = new AttributeKey(getClass(), "unrespondedRequestStore");
/*     */   private final ResponseInspectorFactory responseInspectorFactory;
/*     */   private final ScheduledExecutorService timeoutScheduler;
/*  57 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */ 
/*     */   public RequestResponseFilter(ResponseInspector responseInspector, ScheduledExecutorService timeoutScheduler)
/*     */   {
/*  61 */     if (responseInspector == null) {
/*  62 */       throw new NullPointerException("responseInspector");
/*     */     }
/*  64 */     if (timeoutScheduler == null) {
/*  65 */       throw new NullPointerException("timeoutScheduler");
/*     */     }
/*  67 */     this.responseInspectorFactory = new ResponseInspectorFactory(responseInspector) {
/*     */       public ResponseInspector getResponseInspector() {
/*  69 */         return this.val$responseInspector;
/*     */       }
/*     */     };
/*  72 */     this.timeoutScheduler = timeoutScheduler;
/*     */   }
/*     */ 
/*     */   public RequestResponseFilter(ResponseInspectorFactory responseInspectorFactory, ScheduledExecutorService timeoutScheduler)
/*     */   {
/*  78 */     if (responseInspectorFactory == null) {
/*  79 */       throw new NullPointerException("responseInspectorFactory");
/*     */     }
/*  81 */     if (timeoutScheduler == null) {
/*  82 */       throw new NullPointerException("timeoutScheduler");
/*     */     }
/*  84 */     this.responseInspectorFactory = responseInspectorFactory;
/*  85 */     this.timeoutScheduler = timeoutScheduler;
/*     */   }
/*     */ 
/*     */   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/*  91 */     if (parent.contains(this)) {
/*  92 */       throw new IllegalArgumentException("You can't add the same filter instance more than once.  Create another instance and add it.");
/*     */     }
/*     */ 
/*  96 */     IoSession session = parent.getSession();
/*  97 */     session.setAttribute(this.RESPONSE_INSPECTOR, this.responseInspectorFactory.getResponseInspector());
/*     */ 
/*  99 */     session.setAttribute(this.REQUEST_STORE, createRequestStore(session));
/* 100 */     session.setAttribute(this.UNRESPONDED_REQUEST_STORE, createUnrespondedRequestStore(session));
/*     */   }
/*     */ 
/*     */   public void onPostRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/* 106 */     IoSession session = parent.getSession();
/*     */ 
/* 108 */     destroyUnrespondedRequestStore(getUnrespondedRequestStore(session));
/* 109 */     destroyRequestStore(getRequestStore(session));
/*     */ 
/* 111 */     session.removeAttribute(this.UNRESPONDED_REQUEST_STORE);
/* 112 */     session.removeAttribute(this.REQUEST_STORE);
/* 113 */     session.removeAttribute(this.RESPONSE_INSPECTOR);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 119 */     ResponseInspector responseInspector = (ResponseInspector)session.getAttribute(this.RESPONSE_INSPECTOR);
/*     */ 
/* 121 */     Object requestId = responseInspector.getRequestId(message);
/* 122 */     if (requestId == null)
/*     */     {
/* 124 */       nextFilter.messageReceived(session, message);
/* 125 */       return;
/*     */     }
/*     */ 
/* 129 */     ResponseType type = responseInspector.getResponseType(message);
/* 130 */     if (type == null) {
/* 131 */       nextFilter.exceptionCaught(session, new IllegalStateException(responseInspector.getClass().getName() + "#getResponseType() may not return null."));
/*     */     }
/*     */ 
/* 136 */     Map requestStore = getRequestStore(session);
/*     */     Request request;
/* 139 */     switch (2.$SwitchMap$org$apache$mina$filter$reqres$ResponseType[type.ordinal()]) {
/*     */     case 1:
/*     */     case 2:
/* 142 */       synchronized (requestStore) {
/* 143 */         request = (Request)requestStore.remove(requestId);
/*     */       }
/* 145 */       break;
/*     */     case 3:
/* 147 */       synchronized (requestStore) {
/* 148 */         request = (Request)requestStore.get(requestId);
/*     */       }
/* 150 */       break;
/*     */     default:
/* 152 */       throw new InternalError();
/*     */     }
/*     */ 
/* 155 */     if (request == null)
/*     */     {
/* 158 */       if (this.logger.isWarnEnabled()) {
/* 159 */         this.logger.warn("Unknown request ID '" + requestId + "' for the response message. Timed out already?: " + message);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 166 */       if (type != ResponseType.PARTIAL) {
/* 167 */         ScheduledFuture scheduledFuture = request.getTimeoutFuture();
/* 168 */         if (scheduledFuture != null) {
/* 169 */           scheduledFuture.cancel(false);
/* 170 */           Set unrespondedRequests = getUnrespondedRequestStore(session);
/* 171 */           synchronized (unrespondedRequests) {
/* 172 */             unrespondedRequests.remove(request);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 178 */       Response response = new Response(request, message, type);
/* 179 */       request.signal(response);
/* 180 */       nextFilter.messageReceived(session, response);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Object doFilterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 187 */     Object message = writeRequest.getMessage();
/* 188 */     if (!(message instanceof Request)) {
/* 189 */       return null;
/*     */     }
/*     */ 
/* 192 */     Request request = (Request)message;
/* 193 */     if (request.getTimeoutFuture() != null) {
/* 194 */       throw new IllegalArgumentException("Request can not be reused.");
/*     */     }
/*     */ 
/* 197 */     Map requestStore = getRequestStore(session);
/* 198 */     Object oldValue = null;
/* 199 */     Object requestId = request.getId();
/* 200 */     synchronized (requestStore) {
/* 201 */       oldValue = requestStore.get(requestId);
/* 202 */       if (oldValue == null) {
/* 203 */         requestStore.put(requestId, request);
/*     */       }
/*     */     }
/* 206 */     if (oldValue != null) {
/* 207 */       throw new IllegalStateException("Duplicate request ID: " + request.getId());
/*     */     }
/*     */ 
/* 212 */     TimeoutTask timeoutTask = new TimeoutTask(nextFilter, request, session, null);
/*     */ 
/* 214 */     ScheduledFuture timeoutFuture = this.timeoutScheduler.schedule(timeoutTask, request.getTimeoutMillis(), TimeUnit.MILLISECONDS);
/*     */ 
/* 217 */     request.setTimeoutTask(timeoutTask);
/* 218 */     request.setTimeoutFuture(timeoutFuture);
/*     */ 
/* 221 */     Set unrespondedRequests = getUnrespondedRequestStore(session);
/* 222 */     synchronized (unrespondedRequests) {
/* 223 */       unrespondedRequests.add(request);
/*     */     }
/*     */ 
/* 226 */     return request.getMessage();
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 234 */     Set unrespondedRequests = getUnrespondedRequestStore(session);
/*     */     List unrespondedRequestsCopy;
/* 236 */     synchronized (unrespondedRequests) {
/* 237 */       unrespondedRequestsCopy = new ArrayList(unrespondedRequests);
/*     */ 
/* 239 */       unrespondedRequests.clear();
/*     */     }
/*     */ 
/* 243 */     for (Request r : unrespondedRequestsCopy) {
/* 244 */       if (r.getTimeoutFuture().cancel(false)) {
/* 245 */         r.getTimeoutTask().run();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 250 */     Map requestStore = getRequestStore(session);
/* 251 */     synchronized (requestStore) {
/* 252 */       requestStore.clear();
/*     */     }
/*     */ 
/* 256 */     nextFilter.sessionClosed(session);
/*     */   }
/*     */ 
/*     */   private Map<Object, Request> getRequestStore(IoSession session)
/*     */   {
/* 261 */     return (Map)session.getAttribute(this.REQUEST_STORE);
/*     */   }
/*     */ 
/*     */   private Set<Request> getUnrespondedRequestStore(IoSession session)
/*     */   {
/* 266 */     return (Set)session.getAttribute(this.UNRESPONDED_REQUEST_STORE);
/*     */   }
/*     */ 
/*     */   protected Map<Object, Request> createRequestStore(IoSession session)
/*     */   {
/* 277 */     return new HashMap();
/*     */   }
/*     */ 
/*     */   protected Set<Request> createUnrespondedRequestStore(IoSession session)
/*     */   {
/* 294 */     return new LinkedHashSet();
/*     */   }
/*     */ 
/*     */   protected void destroyRequestStore(Map<Object, Request> requestStore)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void destroyUnrespondedRequestStore(Set<Request> unrespondedRequestStore)
/*     */   {
/*     */   }
/*     */ 
/*     */   private class TimeoutTask
/*     */     implements Runnable
/*     */   {
/*     */     private final IoFilter.NextFilter filter;
/*     */     private final Request request;
/*     */     private final IoSession session;
/*     */ 
/*     */     private TimeoutTask(IoFilter.NextFilter filter, Request request, IoSession session)
/*     */     {
/* 328 */       this.filter = filter;
/* 329 */       this.request = request;
/* 330 */       this.session = session;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 334 */       Set unrespondedRequests = RequestResponseFilter.this.getUnrespondedRequestStore(this.session);
/* 335 */       if (unrespondedRequests != null) {
/* 336 */         synchronized (unrespondedRequests) {
/* 337 */           unrespondedRequests.remove(this.request);
/*     */         }
/*     */       }
/*     */ 
/* 341 */       Map requestStore = RequestResponseFilter.this.getRequestStore(this.session);
/* 342 */       Object requestId = this.request.getId();
/*     */       boolean timedOut;
/* 344 */       synchronized (requestStore)
/*     */       {
/*     */         boolean timedOut;
/* 345 */         if (requestStore.get(requestId) == this.request) {
/* 346 */           requestStore.remove(requestId);
/* 347 */           timedOut = true;
/*     */         } else {
/* 349 */           timedOut = false;
/*     */         }
/*     */       }
/*     */ 
/* 353 */       if (timedOut)
/*     */       {
/* 355 */         RequestTimeoutException e = new RequestTimeoutException(this.request);
/* 356 */         this.request.signal(e);
/* 357 */         this.filter.exceptionCaught(this.session, e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.reqres.RequestResponseFilter
 * JD-Core Version:    0.6.0
 */