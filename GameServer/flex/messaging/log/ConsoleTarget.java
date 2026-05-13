/*    */ package flex.messaging.log;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class ConsoleTarget extends LineFormattedTarget
/*    */ {
/*    */   protected void internalLog(String message)
/*    */   {
/* 39 */     System.out.println(message);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.ConsoleTarget
 * JD-Core Version:    0.6.0
 */