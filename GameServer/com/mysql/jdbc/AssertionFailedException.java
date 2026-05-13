/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ public class AssertionFailedException extends RuntimeException
/*    */ {
/*    */   public static void shouldNotHappen(Exception ex)
/*    */     throws AssertionFailedException
/*    */   {
/* 49 */     throw new AssertionFailedException(ex);
/*    */   }
/*    */ 
/*    */   public AssertionFailedException(Exception ex)
/*    */   {
/* 63 */     super(Messages.getString("AssertionFailedException.0") + ex.toString() + Messages.getString("AssertionFailedException.1"));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.AssertionFailedException
 * JD-Core Version:    0.6.0
 */