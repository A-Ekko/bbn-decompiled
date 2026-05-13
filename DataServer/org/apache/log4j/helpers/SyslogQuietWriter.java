/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.io.Writer;
/*    */ import org.apache.log4j.spi.ErrorHandler;
/*    */ 
/*    */ public class SyslogQuietWriter extends QuietWriter
/*    */ {
/*    */   int syslogFacility;
/*    */   int level;
/*    */ 
/*    */   public SyslogQuietWriter(Writer writer, int syslogFacility, ErrorHandler eh)
/*    */   {
/* 29 */     super(writer, eh);
/* 30 */     this.syslogFacility = syslogFacility;
/*    */   }
/*    */ 
/*    */   public void setLevel(int level)
/*    */   {
/* 35 */     this.level = level;
/*    */   }
/*    */ 
/*    */   public void setSyslogFacility(int syslogFacility)
/*    */   {
/* 40 */     this.syslogFacility = syslogFacility;
/*    */   }
/*    */ 
/*    */   public void write(String string)
/*    */   {
/* 45 */     super.write("<" + (this.syslogFacility | this.level) + ">" + string);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.SyslogQuietWriter
 * JD-Core Version:    0.6.0
 */