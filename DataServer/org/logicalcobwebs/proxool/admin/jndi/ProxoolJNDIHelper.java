/*    */ package org.logicalcobwebs.proxool.admin.jndi;
/*    */ 
/*    */ import java.util.Properties;
/*    */ import javax.naming.InitialContext;
/*    */ import javax.naming.NamingException;
/*    */ import javax.sql.DataSource;
/*    */ import org.logicalcobwebs.proxool.ProxoolDataSource;
/*    */ import org.logicalcobwebs.proxool.ProxoolException;
/*    */ 
/*    */ public class ProxoolJNDIHelper
/*    */ {
/*    */   public static void registerDatasource(String alias, Properties jndiProperties)
/*    */     throws ProxoolException
/*    */   {
/* 35 */     DataSource dataSource = new ProxoolDataSource(alias);
/* 36 */     String jndiName = jndiProperties.getProperty("jndi-name");
/* 37 */     jndiProperties.remove("jndi-name");
/*    */     try {
/* 39 */       InitialContext initalContext = new InitialContext(jndiProperties);
/* 40 */       initalContext.rebind(jndiName, dataSource);
/*    */     } catch (NamingException e) {
/* 42 */       throw new ProxoolException("JNDI binding of DataSource for alias " + alias + " failed.", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.jndi.ProxoolJNDIHelper
 * JD-Core Version:    0.6.0
 */