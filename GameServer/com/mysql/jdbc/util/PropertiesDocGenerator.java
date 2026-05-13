/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionProperties;
/*    */ import java.io.PrintStream;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class PropertiesDocGenerator extends ConnectionProperties
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws SQLException
/*    */   {
/* 38 */     System.out.println(new PropertiesDocGenerator().exposeAsXml());
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.util.PropertiesDocGenerator
 * JD-Core Version:    0.6.0
 */