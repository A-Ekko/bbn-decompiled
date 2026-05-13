/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TreeMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ abstract class AbstractProxyStatement
/*     */ {
/*  29 */   private static final Log LOG = LogFactory.getLog(ProxyStatement.class);
/*     */ 
/*  31 */   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy.HH:mm:ss");
/*     */   private Statement statement;
/*     */   private ConnectionPool connectionPool;
/*     */   private ProxyConnectionIF proxyConnection;
/*     */   private Map parameters;
/*     */   private String sqlStatement;
/*  43 */   private StringBuffer sqlLog = new StringBuffer();
/*     */ 
/*     */   public AbstractProxyStatement(Statement statement, ConnectionPool connectionPool, ProxyConnectionIF proxyConnection, String sqlStatement)
/*     */   {
/*  53 */     this.statement = statement;
/*  54 */     this.connectionPool = connectionPool;
/*  55 */     this.proxyConnection = proxyConnection;
/*  56 */     this.sqlStatement = sqlStatement;
/*     */   }
/*     */ 
/*     */   protected boolean testException(Throwable t)
/*     */   {
/*  65 */     if (FatalSqlExceptionHelper.testException(this.connectionPool.getDefinition(), t))
/*     */     {
/*     */       try
/*     */       {
/*  69 */         this.statement.close();
/*  70 */         this.connectionPool.throwConnection(this.proxyConnection, 8, "Fatal SQL Exception has been detected");
/*     */ 
/*  73 */         HouseKeeperController.sweepNow(this.connectionPool.getDefinition().getAlias());
/*     */ 
/*  75 */         LOG.warn("Connection has been thrown away because fatal exception was detected", t);
/*     */       } catch (SQLException e2) {
/*  77 */         LOG.error("Problem trying to throw away suspect connection", e2);
/*     */       }
/*  79 */       return true;
/*     */     }
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */   public Statement getDelegateStatement()
/*     */   {
/*  90 */     return this.statement;
/*     */   }
/*     */ 
/*     */   protected ConnectionPool getConnectionPool()
/*     */   {
/*  98 */     return this.connectionPool;
/*     */   }
/*     */ 
/*     */   protected Statement getStatement()
/*     */   {
/* 106 */     return this.statement;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 115 */     this.statement.close();
/* 116 */     this.proxyConnection.registerClosedStatement(this.statement);
/*     */   }
/*     */ 
/*     */   protected Connection getConnection() {
/* 120 */     return ProxyFactory.getWrappedConnection((ProxyConnection)this.proxyConnection);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 128 */     return this.statement.hashCode() == obj.hashCode();
/*     */   }
/*     */ 
/*     */   protected void putParameter(int index, Object value)
/*     */   {
/* 139 */     if (this.parameters == null) {
/* 140 */       this.parameters = new TreeMap(new Comparator() {
/*     */         public int compare(Object o1, Object o2) {
/* 142 */           int c = 0;
/*     */ 
/* 144 */           if (((o1 instanceof Integer)) && ((o2 instanceof Integer))) {
/* 145 */             c = ((Integer)o1).compareTo((Integer)o2);
/*     */           }
/*     */ 
/* 148 */           return c;
/*     */         }
/*     */       });
/*     */     }
/* 153 */     Object key = new Integer(index);
/* 154 */     if (value == null) {
/* 155 */       this.parameters.put(key, "NULL");
/* 156 */     } else if ((value instanceof String)) {
/* 157 */       this.parameters.put(key, "'" + value + "'");
/* 158 */     } else if ((value instanceof Number)) {
/* 159 */       this.parameters.put(key, value);
/* 160 */     } else if ((value instanceof Boolean)) {
/* 161 */       this.parameters.put(key, ((Boolean)value).toString());
/* 162 */     } else if ((value instanceof Date)) {
/* 163 */       this.parameters.put(key, "'" + getDateAsString((Date)value) + "'");
/*     */     } else {
/* 165 */       String className = value.getClass().getName();
/* 166 */       StringTokenizer st = new StringTokenizer(className, ".");
/* 167 */       while (st.hasMoreTokens()) {
/* 168 */         className = st.nextToken();
/*     */       }
/* 170 */       this.parameters.put(key, className);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void trace(long startTime, Exception exception)
/*     */     throws SQLException
/*     */   {
/* 182 */     if (isTrace())
/*     */     {
/* 184 */       if ((this.connectionPool.getLog().isDebugEnabled()) && (this.connectionPool.getDefinition().isTrace())) {
/* 185 */         this.connectionPool.getLog().debug(this.sqlLog.toString() + " (" + (System.currentTimeMillis() - startTime) + " milliseconds" + (exception != null ? ", threw a " + exception.getClass().getName() + ": " + exception.getMessage() + ")" : ")"));
/*     */       }
/*     */ 
/* 189 */       this.connectionPool.onExecute(this.sqlLog.toString(), System.currentTimeMillis() - startTime, exception);
/*     */     }
/*     */ 
/* 193 */     if (this.parameters != null) {
/* 194 */       this.parameters.clear();
/*     */     }
/* 196 */     this.sqlStatement = null;
/* 197 */     this.sqlLog.setLength(0);
/*     */   }
/*     */ 
/*     */   protected void startExecute()
/*     */   {
/* 202 */     if (isTrace())
/* 203 */       ((ProxyConnection)this.proxyConnection).addSqlCall(this.sqlLog.toString());
/*     */   }
/*     */ 
/*     */   protected void appendToSqlLog()
/*     */   {
/* 213 */     if ((this.sqlStatement != null) && (this.sqlStatement.length() > 0) && (isTrace())) {
/* 214 */       int parameterIndex = 0;
/* 215 */       StringTokenizer st = new StringTokenizer(this.sqlStatement, "?");
/* 216 */       while (st.hasMoreTokens()) {
/* 217 */         if (parameterIndex > 0) {
/* 218 */           if (this.parameters != null) {
/* 219 */             Object value = this.parameters.get(new Integer(parameterIndex));
/* 220 */             if (value != null)
/* 221 */               this.sqlLog.append(value);
/*     */             else
/* 223 */               this.sqlLog.append("?");
/*     */           }
/*     */           else {
/* 226 */             this.sqlLog.append("?");
/*     */           }
/*     */         }
/* 229 */         parameterIndex++;
/* 230 */         this.sqlLog.append(st.nextToken());
/*     */       }
/* 232 */       if ((this.sqlStatement.endsWith("?")) && 
/* 233 */         (parameterIndex > 0)) {
/* 234 */         if (this.parameters != null) {
/* 235 */           Object value = this.parameters.get(new Integer(parameterIndex));
/* 236 */           if (value != null)
/* 237 */             this.sqlLog.append(value);
/*     */           else
/* 239 */             this.sqlLog.append("?");
/*     */         }
/*     */         else {
/* 242 */           this.sqlLog.append("?");
/*     */         }
/*     */       }
/*     */ 
/* 246 */       if ((this.sqlStatement != null) && (!this.sqlStatement.trim().endsWith(";"))) {
/* 247 */         this.sqlLog.append("; ");
/*     */       }
/*     */     }
/* 250 */     if (this.parameters != null)
/* 251 */       this.parameters.clear();
/*     */   }
/*     */ 
/*     */   protected boolean isTrace()
/*     */   {
/* 256 */     return (getConnectionPool().isConnectionListenedTo()) || (getConnectionPool().getDefinition().isTrace());
/*     */   }
/*     */ 
/*     */   protected void setSqlStatementIfNull(String sqlStatement)
/*     */   {
/* 264 */     if (this.sqlStatement == null)
/* 265 */       this.sqlStatement = sqlStatement;
/*     */   }
/*     */ 
/*     */   protected static String getDateAsString(Date date)
/*     */   {
/* 270 */     return DATE_FORMAT.format(date);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.AbstractProxyStatement
 * JD-Core Version:    0.6.0
 */