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
/*  85 */   protected String databaseURL = "jdbc:odbc:myDB";
/*     */ 
/*  90 */   protected String databaseUser = "me";
/*     */ 
/*  95 */   protected String databasePassword = "mypassword";
/*     */ 
/* 104 */   protected Connection connection = null;
/*     */ 
/* 115 */   protected String sqlStatement = "";
/*     */ 
/* 121 */   protected int bufferSize = 1;
/*     */   protected ArrayList buffer;
/*     */   protected ArrayList removes;
/*     */ 
/*     */   public JDBCAppender()
/*     */   {
/* 135 */     this.buffer = new ArrayList(this.bufferSize);
/* 136 */     this.removes = new ArrayList(this.bufferSize);
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 143 */     this.buffer.add(event);
/*     */ 
/* 145 */     if (this.buffer.size() >= this.bufferSize)
/* 146 */       flushBuffer();
/*     */   }
/*     */ 
/*     */   protected String getLogStatement(LoggingEvent event)
/*     */   {
/* 158 */     return getLayout().format(event);
/*     */   }
/*     */ 
/*     */   protected void execute(String sql)
/*     */     throws SQLException
/*     */   {
/* 171 */     Connection con = null;
/* 172 */     Statement stmt = null;
/*     */     try
/*     */     {
/* 175 */       con = getConnection();
/*     */ 
/* 177 */       stmt = con.createStatement();
/* 178 */       stmt.executeUpdate(sql);
/*     */     } catch (SQLException e) {
/* 180 */       if (stmt != null)
/* 181 */         stmt.close();
/* 182 */       throw e;
/*     */     }
/* 184 */     stmt.close();
/* 185 */     closeConnection(con);
/*     */   }
/*     */ 
/*     */   protected void closeConnection(Connection con)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 208 */     if (!DriverManager.getDrivers().hasMoreElements()) {
/* 209 */       setDriver("sun.jdbc.odbc.JdbcOdbcDriver");
/*     */     }
/* 211 */     if (this.connection == null) {
/* 212 */       this.connection = DriverManager.getConnection(this.databaseURL, this.databaseUser, this.databasePassword);
/*     */     }
/*     */ 
/* 216 */     return this.connection;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 225 */     flushBuffer();
/*     */     try
/*     */     {
/* 228 */       if ((this.connection != null) && (!this.connection.isClosed()))
/* 229 */         this.connection.close();
/*     */     } catch (SQLException e) {
/* 231 */       this.errorHandler.error("Error closing connection", e, 0);
/*     */     }
/* 233 */     this.closed = true;
/*     */   }
/*     */ 
/*     */   public void flushBuffer()
/*     */   {
/* 245 */     this.removes.ensureCapacity(this.buffer.size());
/* 246 */     for (Iterator i = this.buffer.iterator(); i.hasNext(); ) {
/*     */       try {
/* 248 */         LoggingEvent logEvent = (LoggingEvent)i.next();
/* 249 */         String sql = getLogStatement(logEvent);
/* 250 */         execute(sql);
/* 251 */         this.removes.add(logEvent);
/*     */       }
/*     */       catch (SQLException e) {
/* 254 */         this.errorHandler.error("Failed to excute sql", e, 2);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 260 */     this.buffer.removeAll(this.removes);
/*     */ 
/* 263 */     this.removes.clear();
/*     */   }
/*     */ 
/*     */   public void finalize()
/*     */   {
/* 269 */     close();
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 277 */     return true;
/*     */   }
/*     */ 
/*     */   public void setSql(String s)
/*     */   {
/* 285 */     this.sqlStatement = s;
/* 286 */     if (getLayout() == null) {
/* 287 */       setLayout(new PatternLayout(s));
/*     */     }
/*     */     else
/* 290 */       ((PatternLayout)getLayout()).setConversionPattern(s);
/*     */   }
/*     */ 
/*     */   public String getSql()
/*     */   {
/* 299 */     return this.sqlStatement;
/*     */   }
/*     */ 
/*     */   public void setUser(String user)
/*     */   {
/* 304 */     this.databaseUser = user;
/*     */   }
/*     */ 
/*     */   public void setURL(String url)
/*     */   {
/* 309 */     this.databaseURL = url;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 314 */     this.databasePassword = password;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int newBufferSize)
/*     */   {
/* 319 */     this.bufferSize = newBufferSize;
/* 320 */     this.buffer.ensureCapacity(this.bufferSize);
/* 321 */     this.removes.ensureCapacity(this.bufferSize);
/*     */   }
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 326 */     return this.databaseUser;
/*     */   }
/*     */ 
/*     */   public String getURL()
/*     */   {
/* 331 */     return this.databaseURL;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 336 */     return this.databasePassword;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 341 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setDriver(String driverClass)
/*     */   {
/*     */     try
/*     */     {
/* 351 */       Class.forName(driverClass);
/*     */     } catch (Exception e) {
/* 353 */       this.errorHandler.error("Failed to load driver", e, 0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.jdbc.JDBCAppender
 * JD-Core Version:    0.6.0
 */