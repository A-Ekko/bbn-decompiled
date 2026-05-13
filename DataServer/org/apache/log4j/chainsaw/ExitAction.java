/*    */ package org.apache.log4j.chainsaw;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ import org.apache.log4j.Category;
/*    */ 
/*    */ class ExitAction extends AbstractAction
/*    */ {
/* 23 */   private static final Category LOG = Category.getInstance(ExitAction.class);
/*    */ 
/* 25 */   public static final ExitAction INSTANCE = new ExitAction();
/*    */ 
/*    */   public void actionPerformed(ActionEvent aIgnore)
/*    */   {
/* 35 */     LOG.info("shutting down");
/* 36 */     System.exit(0);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.ExitAction
 * JD-Core Version:    0.6.0
 */