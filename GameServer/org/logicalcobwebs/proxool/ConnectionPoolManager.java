/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ class ConnectionPoolManager
/*     */ {
/*  24 */   private static final Object LOCK = new Object();
/*     */ 
/*  26 */   private Map connectionPoolMap = new HashMap();
/*     */ 
/*  28 */   private Set connectionPools = new HashSet();
/*     */ 
/*  30 */   private static ConnectionPoolManager connectionPoolManager = null;
/*     */ 
/*  32 */   private static final Log LOG = LogFactory.getLog(ProxoolFacade.class);
/*     */ 
/*     */   public static ConnectionPoolManager getInstance() {
/*  35 */     if (connectionPoolManager == null) {
/*  36 */       synchronized (LOCK) {
/*  37 */         if (connectionPoolManager == null) {
/*  38 */           connectionPoolManager = new ConnectionPoolManager();
/*     */         }
/*     */       }
/*     */     }
/*  42 */     return connectionPoolManager;
/*     */   }
/*     */ 
/*     */   protected ConnectionPool getConnectionPool(String alias)
/*     */     throws ProxoolException
/*     */   {
/*  55 */     ConnectionPool cp = (ConnectionPool)this.connectionPoolMap.get(alias);
/*  56 */     if (cp == null) {
/*  57 */       throw new ProxoolException(getKnownPools(alias));
/*     */     }
/*  59 */     return cp;
/*     */   }
/*     */ 
/*     */   protected String getKnownPools(String alias)
/*     */   {
/*  69 */     StringBuffer message = new StringBuffer("Couldn't find a pool called '" + alias + "'. Known pools are: ");
/*  70 */     Iterator i = this.connectionPoolMap.keySet().iterator();
/*  71 */     while (i.hasNext()) {
/*  72 */       message.append((String)i.next());
/*  73 */       message.append(i.hasNext() ? ", " : ".");
/*     */     }
/*  75 */     return message.toString();
/*     */   }
/*     */ 
/*     */   protected boolean isPoolExists(String alias)
/*     */   {
/*  84 */     return this.connectionPoolMap.containsKey(alias);
/*     */   }
/*     */ 
/*     */   protected ConnectionPool[] getConnectionPools()
/*     */   {
/*  89 */     return (ConnectionPool[])(ConnectionPool[])this.connectionPools.toArray(new ConnectionPool[this.connectionPools.size()]);
/*     */   }
/*     */ 
/*     */   protected ConnectionPool createConnectionPool(ConnectionPoolDefinition connectionPoolDefinition) throws ProxoolException {
/*  93 */     ConnectionPool connectionPool = new ConnectionPool(connectionPoolDefinition);
/*  94 */     this.connectionPools.add(connectionPool);
/*  95 */     this.connectionPoolMap.put(connectionPoolDefinition.getAlias(), connectionPool);
/*  96 */     return connectionPool;
/*     */   }
/*     */ 
/*     */   protected void removeConnectionPool(String name) {
/* 100 */     ConnectionPool cp = (ConnectionPool)this.connectionPoolMap.get(name);
/* 101 */     if (cp != null) {
/* 102 */       this.connectionPoolMap.remove(cp.getDefinition().getAlias());
/* 103 */       this.connectionPools.remove(cp);
/*     */     } else {
/* 105 */       LOG.info("Ignored attempt to remove either non-existent or already removed connection pool " + name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getConnectionPoolNames() {
/* 110 */     return (String[])(String[])this.connectionPoolMap.keySet().toArray(new String[this.connectionPoolMap.size()]);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionPoolManager
 * JD-Core Version:    0.6.0
 */