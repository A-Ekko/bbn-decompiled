/*     */ package org.apache.mina.core.write;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.mina.util.MapBackedSet;
/*     */ 
/*     */ public class WriteException extends IOException
/*     */ {
/*     */   private static final long serialVersionUID = -4174407422754524197L;
/*     */   private final List<WriteRequest> requests;
/*     */ 
/*     */   public WriteException(WriteRequest request)
/*     */   {
/*  49 */     this.requests = asRequestList(request);
/*     */   }
/*     */ 
/*     */   public WriteException(WriteRequest request, String s)
/*     */   {
/*  56 */     super(s);
/*  57 */     this.requests = asRequestList(request);
/*     */   }
/*     */ 
/*     */   public WriteException(WriteRequest request, String message, Throwable cause)
/*     */   {
/*  64 */     super(message);
/*  65 */     initCause(cause);
/*  66 */     this.requests = asRequestList(request);
/*     */   }
/*     */ 
/*     */   public WriteException(WriteRequest request, Throwable cause)
/*     */   {
/*  73 */     initCause(cause);
/*  74 */     this.requests = asRequestList(request);
/*     */   }
/*     */ 
/*     */   public WriteException(Collection<WriteRequest> requests)
/*     */   {
/*  82 */     this.requests = asRequestList(requests);
/*     */   }
/*     */ 
/*     */   public WriteException(Collection<WriteRequest> requests, String s)
/*     */   {
/*  89 */     super(s);
/*  90 */     this.requests = asRequestList(requests);
/*     */   }
/*     */ 
/*     */   public WriteException(Collection<WriteRequest> requests, String message, Throwable cause)
/*     */   {
/*  97 */     super(message);
/*  98 */     initCause(cause);
/*  99 */     this.requests = asRequestList(requests);
/*     */   }
/*     */ 
/*     */   public WriteException(Collection<WriteRequest> requests, Throwable cause)
/*     */   {
/* 106 */     initCause(cause);
/* 107 */     this.requests = asRequestList(requests);
/*     */   }
/*     */ 
/*     */   public List<WriteRequest> getRequests()
/*     */   {
/* 114 */     return this.requests;
/*     */   }
/*     */ 
/*     */   public WriteRequest getRequest()
/*     */   {
/* 121 */     return (WriteRequest)this.requests.get(0);
/*     */   }
/*     */ 
/*     */   private static List<WriteRequest> asRequestList(Collection<WriteRequest> requests) {
/* 125 */     if (requests == null) {
/* 126 */       throw new NullPointerException("requests");
/*     */     }
/* 128 */     if (requests.isEmpty()) {
/* 129 */       throw new IllegalArgumentException("requests is empty.");
/*     */     }
/*     */ 
/* 133 */     Set newRequests = new MapBackedSet(new LinkedHashMap());
/* 134 */     for (WriteRequest r : requests) {
/* 135 */       newRequests.add(r.getOriginalRequest());
/*     */     }
/*     */ 
/* 138 */     return Collections.unmodifiableList(new ArrayList(newRequests));
/*     */   }
/*     */ 
/*     */   private static List<WriteRequest> asRequestList(WriteRequest request) {
/* 142 */     if (request == null) {
/* 143 */       throw new NullPointerException("request");
/*     */     }
/*     */ 
/* 146 */     List requests = new ArrayList(1);
/* 147 */     requests.add(request.getOriginalRequest());
/* 148 */     return Collections.unmodifiableList(requests);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.write.WriteException
 * JD-Core Version:    0.6.0
 */