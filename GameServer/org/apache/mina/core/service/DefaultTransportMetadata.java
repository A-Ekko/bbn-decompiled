/*     */ package org.apache.mina.core.service;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.util.IdentityHashSet;
/*     */ 
/*     */ public class DefaultTransportMetadata
/*     */   implements TransportMetadata
/*     */ {
/*     */   private final String providerName;
/*     */   private final String name;
/*     */   private final boolean connectionless;
/*     */   private final boolean fragmentation;
/*     */   private final Class<? extends SocketAddress> addressType;
/*     */   private final Class<? extends IoSessionConfig> sessionConfigType;
/*     */   private final Set<Class<? extends Object>> envelopeTypes;
/*     */ 
/*     */   public DefaultTransportMetadata(String providerName, String name, boolean connectionless, boolean fragmentation, Class<? extends SocketAddress> addressType, Class<? extends IoSessionConfig> sessionConfigType, Class<?>[] envelopeTypes)
/*     */   {
/*  55 */     if (providerName == null) {
/*  56 */       throw new NullPointerException("providerName");
/*     */     }
/*  58 */     if (name == null) {
/*  59 */       throw new NullPointerException("name");
/*     */     }
/*     */ 
/*  62 */     providerName = providerName.trim().toLowerCase();
/*  63 */     if (providerName.length() == 0) {
/*  64 */       throw new IllegalArgumentException("providerName is empty.");
/*     */     }
/*  66 */     name = name.trim().toLowerCase();
/*  67 */     if (name.length() == 0) {
/*  68 */       throw new IllegalArgumentException("name is empty.");
/*     */     }
/*     */ 
/*  71 */     if (addressType == null) {
/*  72 */       throw new NullPointerException("addressType");
/*     */     }
/*     */ 
/*  75 */     if (envelopeTypes == null) {
/*  76 */       throw new NullPointerException("envelopeTypes");
/*     */     }
/*     */ 
/*  79 */     if (envelopeTypes.length == 0) {
/*  80 */       throw new NullPointerException("envelopeTypes is empty.");
/*     */     }
/*     */ 
/*  83 */     if (sessionConfigType == null) {
/*  84 */       throw new NullPointerException("sessionConfigType");
/*     */     }
/*     */ 
/*  87 */     this.providerName = providerName;
/*  88 */     this.name = name;
/*  89 */     this.connectionless = connectionless;
/*  90 */     this.fragmentation = fragmentation;
/*  91 */     this.addressType = addressType;
/*  92 */     this.sessionConfigType = sessionConfigType;
/*     */ 
/*  94 */     Set newEnvelopeTypes = new IdentityHashSet();
/*     */ 
/*  96 */     for (Class c : envelopeTypes) {
/*  97 */       newEnvelopeTypes.add(c);
/*     */     }
/*  99 */     this.envelopeTypes = Collections.unmodifiableSet(newEnvelopeTypes);
/*     */   }
/*     */ 
/*     */   public Class<? extends SocketAddress> getAddressType() {
/* 103 */     return this.addressType;
/*     */   }
/*     */ 
/*     */   public Set<Class<? extends Object>> getEnvelopeTypes() {
/* 107 */     return this.envelopeTypes;
/*     */   }
/*     */ 
/*     */   public Class<? extends IoSessionConfig> getSessionConfigType() {
/* 111 */     return this.sessionConfigType;
/*     */   }
/*     */ 
/*     */   public String getProviderName() {
/* 115 */     return this.providerName;
/*     */   }
/*     */ 
/*     */   public String getName() {
/* 119 */     return this.name;
/*     */   }
/*     */ 
/*     */   public boolean isConnectionless() {
/* 123 */     return this.connectionless;
/*     */   }
/*     */ 
/*     */   public boolean hasFragmentation() {
/* 127 */     return this.fragmentation;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 132 */     return this.name;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.DefaultTransportMetadata
 * JD-Core Version:    0.6.0
 */