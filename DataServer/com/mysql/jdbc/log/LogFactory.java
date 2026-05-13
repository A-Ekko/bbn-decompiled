/*    */ package com.mysql.jdbc.log;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class LogFactory
/*    */ {
/*    */   public static Log getLogger(String className, String instanceName)
/*    */     throws SQLException
/*    */   {
/* 57 */     if (className == null) {
/* 58 */       throw new SQLException("Logger class can not be NULL", "S1009");
/*    */     }
/*    */ 
/* 62 */     if (instanceName == null) {
/* 63 */       throw new SQLException("Logger instance name can not be NULL", "S1009");
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 68 */       Class loggerClass = Class.forName(className);
/* 69 */       Constructor constructor = loggerClass.getConstructor(new Class[] { String.class });
/*    */ 
/* 72 */       return (Log)constructor.newInstance(new Object[] { instanceName });
/*    */     } catch (ClassNotFoundException cnfe) {
/* 74 */       throw new SQLException("Unable to load class for logger '" + className + "'", "S1009");
/*    */     }
/*    */     catch (NoSuchMethodException nsme) {
/* 77 */       throw new SQLException("Logger class does not have a single-arg constructor that takes an instance name", "S1009");
/*    */     }
/*    */     catch (InstantiationException inse)
/*    */     {
/* 81 */       throw new SQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009");
/*    */     }
/*    */     catch (InvocationTargetException ite)
/*    */     {
/* 85 */       throw new SQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009");
/*    */     }
/*    */     catch (IllegalAccessException iae)
/*    */     {
/* 89 */       throw new SQLException("Unable to instantiate logger class '" + className + "', constructor not public", "S1009");
/*    */     }
/*    */     catch (ClassCastException cce) {
/*    */     }
/* 93 */     throw new SQLException("Logger class '" + className + "' does not implement the '" + Log.class.getName() + "' interface", "S1009");
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.log.LogFactory
 * JD-Core Version:    0.6.0
 */