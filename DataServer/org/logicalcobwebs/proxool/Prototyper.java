/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class Prototyper
/*     */ {
/*     */   private ConnectionPool connectionPool;
/*  25 */   private Log log = LogFactory.getLog(Prototyper.class);
/*     */   private long connectionCount;
/*  29 */   private final Object lock = new Integer(1);
/*     */ 
/*  31 */   private boolean sweepNeeded = true;
/*     */ 
/*  34 */   private long nextConnectionId = 1L;
/*     */   private boolean cancel;
/*     */   private int connectionsBeingMade;
/*  53 */   private ConnectionBuilderIF connectionBuilder = new DefaultConnectionBuilder();
/*     */ 
/*     */   public Prototyper(ConnectionPool connectionPool) {
/*  56 */     this.connectionPool = connectionPool;
/*  57 */     this.log = connectionPool.getLog();
/*     */   }
/*     */ 
/*     */   protected boolean isSweepNeeded() {
/*  61 */     return this.sweepNeeded;
/*     */   }
/*     */ 
/*     */   protected void triggerSweep() {
/*  65 */     this.sweepNeeded = true;
/*     */   }
/*     */ 
/*     */   protected boolean sweep()
/*     */   {
/*  74 */     boolean somethingDone = false;
/*     */     try
/*     */     {
/*  77 */       while ((!this.cancel) && (this.connectionPool.isConnectionPoolUp()))
/*     */       {
/*  83 */         String reason = null;
/*  84 */         if (this.connectionCount >= getDefinition().getMaximumConnectionCount()) {
/*     */           break;
/*     */         }
/*  87 */         if (this.connectionCount < getDefinition().getMinimumConnectionCount()) {
/*  88 */           reason = "to achieve minimum of " + getDefinition().getMinimumConnectionCount(); } else {
/*  89 */           if (this.connectionPool.getAvailableConnectionCount() >= getDefinition().getPrototypeCount()) break;
/*  90 */           reason = "to keep " + getDefinition().getPrototypeCount() + " available";
/*     */         }
/*     */ 
/*  96 */         ProxyConnectionIF freshlyBuiltProxyConnection = null;
/*     */         try
/*     */         {
/*  99 */           if (!this.connectionPool.isConnectionPoolUp()) {
/*     */             break;
/*     */           }
/* 102 */           freshlyBuiltProxyConnection = buildConnection(1, reason);
/* 103 */           somethingDone = true;
/*     */         } catch (Throwable e) {
/* 105 */           this.log.error("Prototype", e);
/*     */ 
/* 110 */           break;
/*     */         }
/*     */ 
/* 114 */         if (freshlyBuiltProxyConnection != null);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 120 */       this.log.error("Unexpected error", t);
/*     */     }
/*     */ 
/* 123 */     return somethingDone;
/*     */   }
/*     */ 
/*     */   protected ProxyConnection buildConnection(int status, String creator)
/*     */     throws SQLException, ProxoolException
/*     */   {
/* 136 */     long id = 0L;
/* 137 */     synchronized (this.lock)
/*     */     {
/* 140 */       if (this.connectionCount >= getDefinition().getMaximumConnectionCount()) {
/* 141 */         throw new ProxoolException("ConnectionCount is " + this.connectionCount + ". Maximum connection count of " + getDefinition().getMaximumConnectionCount() + " cannot be exceeded.");
/*     */       }
/*     */ 
/* 145 */       checkSimultaneousBuildThrottle();
/*     */ 
/* 147 */       this.connectionsBeingMade += 1;
/* 148 */       this.connectionCount += 1L;
/* 149 */       id = this.nextConnectionId++;
/*     */     }
/*     */ 
/* 153 */     ProxyConnection proxyConnection = null;
/* 154 */     Connection realConnection = null;
/*     */     try
/*     */     {
/* 158 */       ConnectionPoolDefinition definition = this.connectionPool.getDefinition();
/* 159 */       realConnection = this.connectionBuilder.buildConnection(definition);
/*     */ 
/* 167 */       String url = definition.getUrl();
/* 168 */       proxyConnection = new ProxyConnection(realConnection, id, url, this.connectionPool, definition, status);
/*     */       try
/*     */       {
/* 171 */         this.connectionPool.onBirth(realConnection);
/*     */       } catch (Exception e) {
/* 173 */         this.log.error("Problem during onBirth (ignored)", e);
/*     */       }
/*     */ 
/* 182 */       boolean added = this.connectionPool.addProxyConnection(proxyConnection);
/* 183 */       if (this.log.isDebugEnabled()) {
/* 184 */         StringBuffer out = new StringBuffer(this.connectionPool.displayStatistics());
/* 185 */         out.append(" - Connection #");
/* 186 */         out.append(proxyConnection.getId());
/* 187 */         if (getDefinition().isVerbose()) {
/* 188 */           out.append(" (");
/* 189 */           out.append(Integer.toHexString(proxyConnection.hashCode()));
/* 190 */           out.append(")");
/*     */         }
/* 192 */         out.append(" created ");
/* 193 */         out.append(creator);
/* 194 */         out.append(" = ");
/* 195 */         out.append(ConnectionPool.getStatusDescription(proxyConnection.getStatus()));
/* 196 */         if (getDefinition().isVerbose()) {
/* 197 */           out.append(" -> ");
/* 198 */           out.append(getDefinition().getUrl());
/* 199 */           out.append(" (");
/* 200 */           out.append(Integer.toHexString(proxyConnection.getConnection().hashCode()));
/* 201 */           out.append(") by thread ");
/* 202 */           out.append(Thread.currentThread().getName());
/*     */         }
/* 204 */         this.log.debug(out);
/* 205 */         if (!added) {
/* 206 */           out = new StringBuffer(this.connectionPool.displayStatistics());
/* 207 */           out.append(" - Connection #");
/* 208 */           out.append(proxyConnection.getId());
/* 209 */           out.append(" has been discarded immediately because the definition it was built with is out of date");
/* 210 */           this.log.debug(out);
/*     */         }
/*     */       }
/* 213 */       if (!added) {
/* 214 */         proxyConnection.reallyClose();
/*     */       }
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 219 */       throw e;
/*     */     } catch (RuntimeException e) {
/* 221 */       if (this.log.isDebugEnabled()) {
/* 222 */         this.log.debug("Prototyping problem", e);
/*     */       }
/* 224 */       throw e;
/*     */     } catch (Throwable t) {
/* 226 */       if (this.log.isDebugEnabled()) {
/* 227 */         this.log.debug("Prototyping problem", t);
/*     */       }
/* 229 */       throw new ProxoolException("Unexpected prototyping problem", t);
/*     */     } finally {
/* 231 */       synchronized (this.lock) {
/* 232 */         if (proxyConnection == null)
/*     */         {
/* 235 */           this.connectionCount -= 1L;
/*     */         }
/* 237 */         this.connectionsBeingMade -= 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 242 */     return proxyConnection;
/*     */   }
/*     */ 
/*     */   protected void connectionRemoved()
/*     */   {
/* 250 */     this.connectionCount -= 1L;
/*     */   }
/*     */ 
/*     */   protected void checkSimultaneousBuildThrottle()
/*     */     throws SQLException
/*     */   {
/* 259 */     if (this.connectionsBeingMade > getDefinition().getSimultaneousBuildThrottle())
/* 260 */       throw new SQLException("We are already in the process of making " + this.connectionsBeingMade + " connections and the number of simultaneous builds has been throttled to " + getDefinition().getSimultaneousBuildThrottle());
/*     */   }
/*     */ 
/*     */   public long getConnectionCount()
/*     */   {
/* 272 */     return this.connectionCount;
/*     */   }
/*     */ 
/*     */   private ConnectionPoolDefinitionIF getDefinition()
/*     */   {
/* 280 */     return this.connectionPool.getDefinition();
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 287 */     this.cancel = true;
/*     */   }
/*     */ 
/*     */   public String getAlias()
/*     */   {
/* 295 */     return getDefinition().getAlias();
/*     */   }
/*     */ 
/*     */   public void quickRefuse()
/*     */     throws SQLException
/*     */   {
/* 308 */     if ((this.connectionCount >= getDefinition().getMaximumConnectionCount()) && (this.connectionPool.getAvailableConnectionCount() < 1))
/* 309 */       throw new SQLException("Couldn't get connection because we are at maximum connection count (" + this.connectionCount + "/" + getDefinition().getMaximumConnectionCount() + ") and there are none available");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.Prototyper
 * JD-Core Version:    0.6.0
 */