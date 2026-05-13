/*    */ package org.apache.log4j;
/*    */ 
/*    */ public class BasicConfigurator
/*    */ {
/*    */   public static void configure()
/*    */   {
/* 36 */     Logger root = Logger.getRootLogger();
/* 37 */     root.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
/*    */   }
/*    */ 
/*    */   public static void configure(Appender appender)
/*    */   {
/* 48 */     Logger root = Logger.getRootLogger();
/* 49 */     root.addAppender(appender);
/*    */   }
/*    */ 
/*    */   public static void resetConfiguration()
/*    */   {
/* 61 */     LogManager.resetConfiguration();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.BasicConfigurator
 * JD-Core Version:    0.6.0
 */