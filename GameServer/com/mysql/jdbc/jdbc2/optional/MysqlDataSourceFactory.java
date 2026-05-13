/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ 
/*     */ public class MysqlDataSourceFactory
/*     */   implements ObjectFactory
/*     */ {
/*     */   protected static final String DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
/*     */   protected static final String POOL_DATA_SOURCE_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource";
/*     */ 
/*     */   public Object getObjectInstance(Object refObj, Name nm, Context ctx, Hashtable env)
/*     */     throws Exception
/*     */   {
/*  69 */     Reference ref = (Reference)refObj;
/*  70 */     String className = ref.getClassName();
/*     */ 
/*  72 */     if ((className != null) && ((className.equals("com.mysql.jdbc.jdbc2.optional.MysqlDataSource")) || (className.equals("com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource"))))
/*     */     {
/*  75 */       MysqlDataSource dataSource = null;
/*     */       try
/*     */       {
/*  78 */         dataSource = (MysqlDataSource)Class.forName(className).newInstance();
/*     */       }
/*     */       catch (Exception ex) {
/*  81 */         throw new RuntimeException("Unable to create DataSource of class '" + className + "', reason: " + ex.toString());
/*     */       }
/*     */ 
/*  85 */       int portNumber = 3306;
/*     */ 
/*  87 */       String portNumberAsString = (String)ref.get("port").getContent();
/*     */ 
/*  89 */       if (portNumberAsString != null) {
/*  90 */         portNumber = Integer.parseInt(portNumberAsString);
/*     */       }
/*     */ 
/*  93 */       dataSource.setPort(portNumber);
/*     */ 
/*  95 */       String user = (String)ref.get("user").getContent();
/*     */ 
/*  98 */       if (user != null) {
/*  99 */         dataSource.setUser(user);
/*     */       }
/*     */ 
/* 102 */       String password = (String)ref.get("password").getContent();
/*     */ 
/* 105 */       if (password != null) {
/* 106 */         dataSource.setPassword(password);
/*     */       }
/*     */ 
/* 109 */       String serverName = (String)ref.get("serverName").getContent();
/*     */ 
/* 111 */       if (serverName != null) {
/* 112 */         dataSource.setServerName(serverName);
/*     */       }
/*     */ 
/* 115 */       String databaseName = (String)ref.get("databaseName").getContent();
/*     */ 
/* 117 */       if (databaseName != null) {
/* 118 */         dataSource.setDatabaseName(databaseName);
/*     */       }
/*     */ 
/* 121 */       String explicitUrlAsString = (String)ref.get("explicitUrl").getContent();
/*     */ 
/* 124 */       if ((explicitUrlAsString != null) && 
/* 125 */         (Boolean.valueOf(explicitUrlAsString).booleanValue())) {
/* 126 */         dataSource.setUrl((String)ref.get("url").getContent());
/*     */       }
/*     */ 
/* 130 */       dataSource.setPropertiesViaRef(ref);
/*     */ 
/* 132 */       return dataSource;
/*     */     }
/*     */ 
/* 136 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory
 * JD-Core Version:    0.6.0
 */