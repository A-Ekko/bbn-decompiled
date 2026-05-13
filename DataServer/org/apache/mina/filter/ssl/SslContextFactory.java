/*     */ package org.apache.mina.filter.ssl;
/*     */ 
/*     */ import java.security.KeyStore;
/*     */ import java.security.SecureRandom;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.ManagerFactoryParameters;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSessionContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ 
/*     */ public class SslContextFactory
/*     */ {
/*  57 */   private String provider = null;
/*  58 */   private String protocol = "TLS";
/*  59 */   private SecureRandom secureRandom = null;
/*  60 */   private KeyStore keyManagerFactoryKeyStore = null;
/*  61 */   private char[] keyManagerFactoryKeyStorePassword = null;
/*  62 */   private KeyManagerFactory keyManagerFactory = null;
/*  63 */   private String keyManagerFactoryAlgorithm = null;
/*  64 */   private String keyManagerFactoryProvider = null;
/*  65 */   private boolean keyManagerFactoryAlgorithmUseDefault = true;
/*  66 */   private KeyStore trustManagerFactoryKeyStore = null;
/*  67 */   private TrustManagerFactory trustManagerFactory = null;
/*  68 */   private String trustManagerFactoryAlgorithm = null;
/*  69 */   private String trustManagerFactoryProvider = null;
/*  70 */   private boolean trustManagerFactoryAlgorithmUseDefault = true;
/*  71 */   private ManagerFactoryParameters trustManagerFactoryParameters = null;
/*  72 */   private int clientSessionCacheSize = -1;
/*  73 */   private int clientSessionTimeout = -1;
/*  74 */   private int serverSessionCacheSize = -1;
/*  75 */   private int serverSessionTimeout = -1;
/*     */ 
/*     */   public SSLContext newInstance() throws Exception {
/*  78 */     KeyManagerFactory kmf = this.keyManagerFactory;
/*  79 */     TrustManagerFactory tmf = this.trustManagerFactory;
/*     */ 
/*  81 */     if (kmf == null) {
/*  82 */       String algorithm = this.keyManagerFactoryAlgorithm;
/*  83 */       if ((algorithm == null) && (this.keyManagerFactoryAlgorithmUseDefault)) {
/*  84 */         algorithm = KeyManagerFactory.getDefaultAlgorithm();
/*     */       }
/*  86 */       if (algorithm != null) {
/*  87 */         if (this.keyManagerFactoryProvider == null)
/*  88 */           kmf = KeyManagerFactory.getInstance(algorithm);
/*     */         else {
/*  90 */           kmf = KeyManagerFactory.getInstance(algorithm, this.keyManagerFactoryProvider);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  96 */     if (tmf == null) {
/*  97 */       String algorithm = this.trustManagerFactoryAlgorithm;
/*  98 */       if ((algorithm == null) && (this.trustManagerFactoryAlgorithmUseDefault)) {
/*  99 */         algorithm = TrustManagerFactory.getDefaultAlgorithm();
/*     */       }
/* 101 */       if (algorithm != null) {
/* 102 */         if (this.trustManagerFactoryProvider == null)
/* 103 */           tmf = TrustManagerFactory.getInstance(algorithm);
/*     */         else {
/* 105 */           tmf = TrustManagerFactory.getInstance(algorithm, this.trustManagerFactoryProvider);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 111 */     KeyManager[] keyManagers = null;
/* 112 */     if (kmf != null) {
/* 113 */       kmf.init(this.keyManagerFactoryKeyStore, this.keyManagerFactoryKeyStorePassword);
/*     */ 
/* 115 */       keyManagers = kmf.getKeyManagers();
/*     */     }
/* 117 */     TrustManager[] trustManagers = null;
/* 118 */     if (tmf != null) {
/* 119 */       if (this.trustManagerFactoryParameters != null)
/* 120 */         tmf.init(this.trustManagerFactoryParameters);
/*     */       else {
/* 122 */         tmf.init(this.trustManagerFactoryKeyStore);
/*     */       }
/* 124 */       trustManagers = tmf.getTrustManagers();
/*     */     }
/*     */ 
/* 127 */     SSLContext context = null;
/* 128 */     if (this.provider == null)
/* 129 */       context = SSLContext.getInstance(this.protocol);
/*     */     else {
/* 131 */       context = SSLContext.getInstance(this.protocol, this.provider);
/*     */     }
/*     */ 
/* 134 */     context.init(keyManagers, trustManagers, this.secureRandom);
/*     */ 
/* 136 */     if (this.clientSessionCacheSize >= 0) {
/* 137 */       context.getClientSessionContext().setSessionCacheSize(this.clientSessionCacheSize);
/*     */     }
/*     */ 
/* 141 */     if (this.clientSessionTimeout >= 0) {
/* 142 */       context.getClientSessionContext().setSessionTimeout(this.clientSessionTimeout);
/*     */     }
/*     */ 
/* 146 */     if (this.serverSessionCacheSize >= 0) {
/* 147 */       context.getServerSessionContext().setSessionCacheSize(this.serverSessionCacheSize);
/*     */     }
/*     */ 
/* 151 */     if (this.serverSessionTimeout >= 0) {
/* 152 */       context.getServerSessionContext().setSessionTimeout(this.serverSessionTimeout);
/*     */     }
/*     */ 
/* 156 */     return context;
/*     */   }
/*     */ 
/*     */   public void setProvider(String provider)
/*     */   {
/* 166 */     this.provider = provider;
/*     */   }
/*     */ 
/*     */   public void setProtocol(String protocol)
/*     */   {
/* 176 */     if (protocol == null) {
/* 177 */       throw new NullPointerException("protocol");
/*     */     }
/* 179 */     this.protocol = protocol;
/*     */   }
/*     */ 
/*     */   public void setKeyManagerFactoryAlgorithmUseDefault(boolean useDefault)
/*     */   {
/* 194 */     this.keyManagerFactoryAlgorithmUseDefault = useDefault;
/*     */   }
/*     */ 
/*     */   public void setTrustManagerFactoryAlgorithmUseDefault(boolean useDefault)
/*     */   {
/* 208 */     this.trustManagerFactoryAlgorithmUseDefault = useDefault;
/*     */   }
/*     */ 
/*     */   public void setKeyManagerFactory(KeyManagerFactory factory)
/*     */   {
/* 219 */     this.keyManagerFactory = factory;
/*     */   }
/*     */ 
/*     */   public void setKeyManagerFactoryAlgorithm(String algorithm)
/*     */   {
/* 241 */     this.keyManagerFactoryAlgorithm = algorithm;
/*     */   }
/*     */ 
/*     */   public void setKeyManagerFactoryProvider(String provider)
/*     */   {
/* 262 */     this.keyManagerFactoryProvider = provider;
/*     */   }
/*     */ 
/*     */   public void setKeyManagerFactoryKeyStore(KeyStore keyStore)
/*     */   {
/* 273 */     this.keyManagerFactoryKeyStore = keyStore;
/*     */   }
/*     */ 
/*     */   public void setKeyManagerFactoryKeyStorePassword(String password)
/*     */   {
/* 284 */     if (password != null)
/* 285 */       this.keyManagerFactoryKeyStorePassword = password.toCharArray();
/*     */     else
/* 287 */       this.keyManagerFactoryKeyStorePassword = null;
/*     */   }
/*     */ 
/*     */   public void setTrustManagerFactory(TrustManagerFactory factory)
/*     */   {
/* 300 */     this.trustManagerFactory = factory;
/*     */   }
/*     */ 
/*     */   public void setTrustManagerFactoryAlgorithm(String algorithm)
/*     */   {
/* 322 */     this.trustManagerFactoryAlgorithm = algorithm;
/*     */   }
/*     */ 
/*     */   public void setTrustManagerFactoryKeyStore(KeyStore keyStore)
/*     */   {
/* 337 */     this.trustManagerFactoryKeyStore = keyStore;
/*     */   }
/*     */ 
/*     */   public void setTrustManagerFactoryParameters(ManagerFactoryParameters parameters)
/*     */   {
/* 349 */     this.trustManagerFactoryParameters = parameters;
/*     */   }
/*     */ 
/*     */   public void setTrustManagerFactoryProvider(String provider)
/*     */   {
/* 370 */     this.trustManagerFactoryProvider = provider;
/*     */   }
/*     */ 
/*     */   public void setSecureRandom(SecureRandom secureRandom)
/*     */   {
/* 382 */     this.secureRandom = secureRandom;
/*     */   }
/*     */ 
/*     */   public void setClientSessionCacheSize(int size)
/*     */   {
/* 392 */     this.clientSessionCacheSize = size;
/*     */   }
/*     */ 
/*     */   public void setClientSessionTimeout(int seconds)
/*     */   {
/* 402 */     this.clientSessionTimeout = seconds;
/*     */   }
/*     */ 
/*     */   public void setServerSessionCacheSize(int serverSessionCacheSize)
/*     */   {
/* 412 */     this.serverSessionCacheSize = serverSessionCacheSize;
/*     */   }
/*     */ 
/*     */   public void setServerSessionTimeout(int serverSessionTimeout)
/*     */   {
/* 422 */     this.serverSessionTimeout = serverSessionTimeout;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.ssl.SslContextFactory
 * JD-Core Version:    0.6.0
 */