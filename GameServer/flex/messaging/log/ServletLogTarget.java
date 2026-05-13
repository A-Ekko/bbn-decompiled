/*    */ package flex.messaging.log;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import javax.servlet.ServletContext;
/*    */ 
/*    */ public class ServletLogTarget extends LineFormattedTarget
/*    */ {
/*    */   static ServletContext context;
/* 55 */   boolean warned = false;
/*    */ 
/*    */   public static void setServletContext(ServletContext ctx)
/*    */   {
/* 38 */     context = ctx;
/*    */   }
/*    */ 
/*    */   protected void internalLog(String message)
/*    */   {
/* 59 */     if (context == null)
/*    */     {
/* 61 */       if (!this.warned)
/*    */       {
/* 63 */         System.out.println("**** No servlet context set in ServletLogTarget - logging disabled.");
/* 64 */         this.warned = true;
/*    */       }
/*    */     }
/*    */     else
/* 68 */       context.log(message);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.ServletLogTarget
 * JD-Core Version:    0.6.0
 */