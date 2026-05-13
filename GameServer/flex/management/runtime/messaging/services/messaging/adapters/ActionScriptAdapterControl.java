/*    */ package flex.management.runtime.messaging.services.messaging.adapters;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.management.runtime.messaging.services.ServiceAdapterControl;
/*    */ import flex.messaging.services.messaging.adapters.ActionScriptAdapter;
/*    */ 
/*    */ public class ActionScriptAdapterControl extends ServiceAdapterControl
/*    */   implements ActionScriptAdapterControlMBean
/*    */ {
/*    */   private static final String TYPE = "ActionScriptAdapter";
/*    */ 
/*    */   public ActionScriptAdapterControl(ActionScriptAdapter serviceAdapter, BaseControl parent)
/*    */   {
/* 42 */     super(serviceAdapter, parent);
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 51 */     return "ActionScriptAdapter";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.messaging.adapters.ActionScriptAdapterControl
 * JD-Core Version:    0.6.0
 */