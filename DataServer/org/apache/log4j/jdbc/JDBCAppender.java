/*     */ package org.apache.log4j.jdbc;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.PatternLayout;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class JDBCAppender extends AppenderSkeleton
/*     */   implements Appender
/*     */ {
/*  70 */   protected String databaseURL = "jdbc:odbc:myDB";
/*     */ 
/*  75 */   protected String databaseUser = "me";
/*     */ 
/*  80 */   protected String databasePassword = "mypassword";
/*     */ 
/*  89 */   protected Connection connection = null;
/*     */ 
/* 100 */   protected String sqlStatement = "";
/*     */ 
/* 106 */   protected int bufferSize = 1;
/*     */   protected ArrayList buffer;
/*     */   protected ArrayList removes;
/*     */ 
/*     */   public JDBCAppender()
/*     */   {
/* 120 */     this.buffer = new ArrayList(this.bufferSize);
/* 121 */     this.removes = new ArrayList(this.bufferSize);
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 128 */     this.buffer.add(event);
/*     */ 
/* 130 */     if (this.buffer.size() >= this.bufferSize)
/* 131 */       flushBuffer();
/*     */   }
/*     */ 
/*     */   protected String getLogStatement(LoggingEvent event)
/*     */   {
/* 143 */     return getLayout().format(event);
/*     */   }
/*     */ 
/*     */   protected void execute(String sql)
/*     */     throws SQLException
/*     */   {
/* 156 */     Connection con = null;
/* 157 */     Statement stmt = null;
/*     */     try
/*     */     {
/* 160 */       con = getConnection();
/*     */ 
/* 162 */       stmt = con.createStatement();
/* 163 */       stmt.executeUpdate(sql);
/*     */     } catch (SQLException e) {
/* 165 */       if (stmt != null)
/* 166 */         stmt.close();
/* 167 */       throw e;
/*     */     }
/* 169 */     stmt.close();
/* 170 */     closeConnection(con);
/*     */   }
/*     */ 
/*     */   protected void closeConnection(Connection con)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 193 */     if (!DriverManager.getDrivers().hasMoreElements()) {
/* 194 */       setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
/*     */     }
/* 196 */     if (this.connection == null) {
/* 197 */       this.connection = DriverManager.getConnection(this.databaseURL, this.databaseUser, this.databasePassword);
/*     */     }
/*     */ 
/* 201 */     return this.connection;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 210 */     flushBuffer();
/*     */     try
/*     */     {
/* 213 */       if ((this.connection != null) && (!this.connection.isClosed()))
/* 214 */         this.connection.close();
/*     */     } catch (SQLException e) {
/* 216 */       this.errorHandler.error("Error closing connection", e, 0);
/*     */     }
/* 218 */     this.closed = true;
/*     */   }
/*     */ 
/*     */   public void flushBuffer()
/*     */   {
/* 230 */     this.removes.ensureCapacity(this.buffer.size());
/* 231 */     for (Iterator i = this.buffer.iterator(); i.hasNext(); ) {
/*     */       try {
/* 233 */         LoggingEvent logEvent = (LoggingEvent)i.next();
/* 234 */         String sql = getLogStatement(logEvent);
/* 235 */         execute(sql);
/* 236 */         this.removes.add(logEvent);
/*     */       }
/*     */       catch (SQLException e) {
/* 239 */         this.errorHandler.error("Failed to excute sql", e, 2);
/*     */       }
/*     */     }
/*     */ 
/* 243 */     this.buffer.removeAll(this.removes);
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 250 */     close();
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 258 */     return true;
/*     */   }
/*     */ 
/*     */   public void setSql(String s)
/*     */   {
/* 266 */     this.sqlStatement = s;
/* 267 */     if (getLayout() == null) {
/* 268 */       setLayout(new PatternLayout(s));
/*     */     }
/*     */     else
/* 271 */       ((PatternLayout)getLayout()).setConversionPattern(s);
/*     */   }
/*     */ 
/*     */   public String getSql()
/*     */   {
/* 280 */     return this.sqlStatement;
/*     */   }
/*     */ 
/*     */   public void setUser(String user)
/*     */   {
/* 285 */     this.databaseUser = user;
/*     */   }
/*     */ 
/*     */   public void setURL(String url)
/*     */   {
/* 290 */     this.databaseURL = url;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 295 */     this.databasePassword = password;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int newBufferSize)
/*     */   {
/* 300 */     this.bufferSize = newBufferSize;
/* 301 */     this.buffer.ensureCapacity(this.bufferSize);
/* 302 */     this.removes.ensureCapacity(this.bufferSize);
/*     */   }
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 307 */     return this.databaseUser;
/*     */   }
/*     */ 
/*     */   public String getURL()
/*     */   {
/* 312 */     return this.databaseURL;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 317 */     return this.databasePassword;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 322 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setDriver(String driverClass)
/*     */   {
/*     */     try
/*     */     {
/* 332 */       Class.forName(driverClass);
/*     */     } catch (Exception e) {
/* 334 */       this.errorHandler.error("Failed to load driver", e, 0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.jdbc.JDBCAppender
 * JD-Core Version:    0.6.0
 */