/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class Version
/*    */ {
/* 32 */   private static final Log LOG = LogFactory.getLog(Version.class);
/*    */   private static final String VERSION = "0.9.1";
/*    */   private static final String BUILD_DATE = "23-Aug-2008 11:10";
/*    */   private static final String CVS = "0.9.1+";
/*    */ 
/*    */   public static String getVersion()
/*    */   {
/* 45 */     StringBuffer version = new StringBuffer();
/*    */ 
/* 47 */     if ("0.9.1" != null) {
/* 48 */       version.append("0.9.1");
/*    */     }
/*    */     else
/*    */     {
/* 54 */       version.append("0.9.1+");
/*    */     }
/*    */ 
/* 57 */     if ("23-Aug-2008 11:10" != null) {
/* 58 */       version.append(" (");
/* 59 */       version.append("23-Aug-2008 11:10");
/* 60 */       version.append(")");
/*    */     }
/*    */ 
/* 63 */     return version.toString();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 71 */     LOG.info("Version " + getVersion());
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.Version
 * JD-Core Version:    0.6.0
 */