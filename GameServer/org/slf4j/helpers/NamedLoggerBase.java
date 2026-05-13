/*    */ package org.slf4j.helpers;
/*    */ 
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ abstract class NamedLoggerBase
/*    */   implements Logger, Serializable
/*    */ {
/*    */   protected String name;
/*    */ 
/*    */   public String getName()
/*    */   {
/* 21 */     return this.name;
/*    */   }
/*    */ 
/*    */   protected Object readResolve()
/*    */     throws ObjectStreamException
/*    */   {
/* 41 */     return LoggerFactory.getLogger(getName());
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.slf4j.helpers.NamedLoggerBase
 * JD-Core Version:    0.6.0
 */