/*    */ package org.apache.log4j;
/*    */ 
/*    */ import java.util.Vector;
/*    */ 
/*    */ class ProvisionNode extends Vector
/*    */ {
/*    */   private static final long serialVersionUID = -4479121426311014469L;
/*    */ 
/*    */   ProvisionNode(Logger logger)
/*    */   {
/* 27 */     addElement(logger);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.ProvisionNode
 * JD-Core Version:    0.6.0
 */