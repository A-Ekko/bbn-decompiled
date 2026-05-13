/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class ProxoolDriver
/*     */   implements Driver
/*     */ {
/*  28 */   private static final Log LOG = LogFactory.getLog(ProxoolDriver.class);
/*     */   private static final ResourceBundle ATTRIBUTE_DESCRIPTIONS_RESOURCE;
/*     */ 
/*     */   private static ResourceBundle createAttributeDescriptionsResource()
/*     */   {
/*     */     try
/*     */     {
/*  42 */       return ResourceBundle.getBundle("org.logicalcobwebs.proxool.resources.attributeDescriptions");
/*     */     } catch (Exception e) {
/*  44 */       LOG.error("Could not find resource org.logicalcobwebs.proxool.resources.attributeDescriptions", e);
/*     */     }
/*  46 */     return null;
/*     */   }
/*     */ 
/*     */   public Connection connect(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/*  68 */     if (!url.startsWith("proxool")) {
/*  69 */       return null;
/*     */     }
/*     */ 
/*  72 */     ConnectionPool cp = null;
/*     */     try {
/*  74 */       String alias = ProxoolFacade.getAlias(url);
/*     */ 
/*  76 */       if (!ConnectionPoolManager.getInstance().isPoolExists(alias)) {
/*  77 */         ProxoolFacade.registerConnectionPool(url, info, false);
/*  78 */         cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*  79 */       } else if ((info != null) && (info.size() > 0))
/*     */       {
/*  81 */         cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*  82 */         ConnectionPoolDefinition cpd = cp.getDefinition();
/*  83 */         if (!cpd.isEqual(url, info))
/*  84 */           cpd.redefine(url, info);
/*     */       }
/*     */       else {
/*  87 */         cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*     */       }
/*  89 */       return cp.getConnection();
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/*     */       try
/*     */       {
/*  96 */         String alias = ProxoolFacade.getAlias(url);
/*  97 */         cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*  98 */         if (FatalSqlExceptionHelper.testException(cp.getDefinition(), e)) {
/*  99 */           FatalSqlExceptionHelper.throwFatalSQLException(cp.getDefinition().getFatalSqlExceptionWrapper(), e);
/*     */         }
/*     */ 
/* 102 */         throw e;
/*     */       } catch (ProxoolException e1) {
/* 104 */         LOG.error("Problem", e);
/* 105 */         throw new SQLException(e.toString());
/*     */       }
/*     */     } catch (ProxoolException e) {
/* 108 */       LOG.error("Problem", e);
/* 109 */     }throw new SQLException(e.toString());
/*     */   }
/*     */ 
/*     */   public boolean acceptsURL(String url)
/*     */     throws SQLException
/*     */   {
/* 118 */     return url.startsWith("proxool");
/*     */   }
/*     */ 
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 127 */     DriverPropertyInfo[] dpi = new DriverPropertyInfo[18];
/* 128 */     ConnectionPool cp = null;
/*     */     try {
/* 130 */       cp = ConnectionPoolManager.getInstance().getConnectionPool(url);
/*     */     } catch (ProxoolException e) {
/* 132 */       throw new SQLException(e.toString());
/*     */     }
/*     */ 
/* 135 */     ConnectionPoolDefinitionIF cpd = cp.getDefinition();
/*     */ 
/* 137 */     dpi[0] = buildDriverPropertyInfo("proxool.driver", String.valueOf(cpd.getDriver()));
/*     */ 
/* 140 */     dpi[1] = buildDriverPropertyInfo("proxool.url", String.valueOf(cpd.getUrl()));
/*     */ 
/* 143 */     dpi[2] = buildDriverPropertyInfo("proxool.minimum-connection-count", String.valueOf(cpd.getMinimumConnectionCount()));
/*     */ 
/* 146 */     dpi[3] = buildDriverPropertyInfo("proxool.maximum-connection-count", String.valueOf(cpd.getMaximumConnectionCount()));
/*     */ 
/* 149 */     dpi[4] = buildDriverPropertyInfo("proxool.maximum-connection-lifetime", String.valueOf(cpd.getMaximumConnectionLifetime()));
/*     */ 
/* 152 */     dpi[5] = buildDriverPropertyInfo("proxool.maximum-new-connections", String.valueOf(cpd.getMaximumNewConnections()));
/*     */ 
/* 155 */     dpi[6] = buildDriverPropertyInfo("proxool.prototype-count", String.valueOf(cpd.getPrototypeCount()));
/*     */ 
/* 158 */     dpi[7] = buildDriverPropertyInfo("proxool.house-keeping-sleep-time", String.valueOf(cpd.getHouseKeepingSleepTime()));
/*     */ 
/* 161 */     dpi[8] = buildDriverPropertyInfo("proxool.house-keeping-test-sql", cpd.getHouseKeepingTestSql());
/*     */ 
/* 164 */     dpi[9] = buildDriverPropertyInfo("proxool.recently-started-threshold", String.valueOf(cpd.getRecentlyStartedThreshold()));
/*     */ 
/* 167 */     dpi[10] = buildDriverPropertyInfo("proxool.overload-without-refusal-lifetime", String.valueOf(cpd.getOverloadWithoutRefusalLifetime()));
/*     */ 
/* 170 */     dpi[11] = buildDriverPropertyInfo("proxool.maximum-active-time", String.valueOf(cpd.getMaximumActiveTime()));
/*     */ 
/* 173 */     dpi[12] = buildDriverPropertyInfo("proxool.verbose", String.valueOf(cpd.isVerbose()));
/*     */ 
/* 176 */     dpi[13] = buildDriverPropertyInfo("proxool.trace", String.valueOf(cpd.isTrace()));
/*     */ 
/* 179 */     dpi[14] = buildDriverPropertyInfo("proxool.fatal-sql-exception", String.valueOf(cpd.getFatalSqlExceptions()));
/*     */ 
/* 182 */     dpi[15] = buildDriverPropertyInfo("proxool.fatal-sql-exception", String.valueOf(cpd.getFatalSqlExceptions()));
/*     */ 
/* 185 */     dpi[16] = buildDriverPropertyInfo("proxool.statistics", String.valueOf(cpd.getStatistics()));
/*     */ 
/* 188 */     dpi[17] = buildDriverPropertyInfo("proxool.statistics-log-level", String.valueOf(cpd.getStatisticsLogLevel()));
/*     */ 
/* 191 */     return dpi;
/*     */   }
/*     */ 
/*     */   private DriverPropertyInfo buildDriverPropertyInfo(String propertyName, String value) {
/* 195 */     DriverPropertyInfo dpi = new DriverPropertyInfo(propertyName, ATTRIBUTE_DESCRIPTIONS_RESOURCE.getString(propertyName));
/*     */ 
/* 197 */     if (value != null) {
/* 198 */       dpi.value = value;
/*     */     }
/* 200 */     return dpi;
/*     */   }
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 207 */     return 1;
/*     */   }
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 214 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean jdbcCompliant()
/*     */   {
/* 221 */     return true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  32 */       DriverManager.registerDriver(new ProxoolDriver());
/*     */     } catch (SQLException e) {
/*  34 */       System.out.println(e.toString());
/*     */     }
/*     */ 
/*  38 */     ATTRIBUTE_DESCRIPTIONS_RESOURCE = createAttributeDescriptionsResource();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxoolDriver
 * JD-Core Version:    0.6.0
 */