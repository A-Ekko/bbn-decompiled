/*    */ package flex.messaging.config;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LoggingSettings extends PropertiesSettings
/*    */ {
/*    */   private final List targets;
/*    */ 
/*    */   public LoggingSettings()
/*    */   {
/* 36 */     this.targets = new ArrayList();
/*    */   }
/*    */ 
/*    */   public void addTarget(TargetSettings t)
/*    */   {
/* 41 */     this.targets.add(t);
/*    */   }
/*    */ 
/*    */   public List getTargets()
/*    */   {
/* 46 */     return this.targets;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.LoggingSettings
 * JD-Core Version:    0.6.0
 */