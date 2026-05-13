/*    */ package org.apache.mina.filter.ssl;
/*    */ 
/*    */ import java.security.InvalidAlgorithmParameterException;
/*    */ import java.security.KeyStore;
/*    */ import java.security.KeyStoreException;
/*    */ import java.security.Provider;
/*    */ import java.security.cert.CertificateException;
/*    */ import java.security.cert.X509Certificate;
/*    */ import javax.net.ssl.ManagerFactoryParameters;
/*    */ import javax.net.ssl.TrustManager;
/*    */ import javax.net.ssl.TrustManagerFactory;
/*    */ import javax.net.ssl.TrustManagerFactorySpi;
/*    */ import javax.net.ssl.X509TrustManager;
/*    */ 
/*    */ public class BogusTrustManagerFactory extends TrustManagerFactory
/*    */ {
/* 51 */   private static final X509TrustManager X509 = new X509TrustManager()
/*    */   {
/*    */     public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
/*    */     {
/*    */     }
/*    */ 
/*    */     public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
/*    */     }
/*    */ 
/*    */     public X509Certificate[] getAcceptedIssuers() {
/* 61 */       return new X509Certificate[0];
/*    */     }
/* 51 */   };
/*    */ 
/* 65 */   private static final TrustManager[] X509_MANAGERS = { X509 };
/*    */ 
/*    */   public BogusTrustManagerFactory()
/*    */   {
/* 45 */     super(new BogusTrustManagerFactorySpi(null), new Provider(1.0D, "")
/*    */     {
/*    */       private static final long serialVersionUID = -4024169055312053827L;
/*    */     }
/*    */     , "MinaBogus");
/*    */   }
/*    */ 
/*    */   private static class BogusTrustManagerFactorySpi extends TrustManagerFactorySpi
/*    */   {
/*    */     protected TrustManager[] engineGetTrustManagers()
/*    */     {
/* 72 */       return BogusTrustManagerFactory.X509_MANAGERS;
/*    */     }
/*    */ 
/*    */     protected void engineInit(KeyStore keystore)
/*    */       throws KeyStoreException
/*    */     {
/*    */     }
/*    */ 
/*    */     protected void engineInit(ManagerFactoryParameters managerFactoryParameters)
/*    */       throws InvalidAlgorithmParameterException
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.ssl.BogusTrustManagerFactory
 * JD-Core Version:    0.6.0
 */