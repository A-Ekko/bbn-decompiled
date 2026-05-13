/*    */ package flex.management.runtime.messaging.services;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.management.runtime.messaging.DestinationControl;
/*    */ import flex.messaging.services.ServiceAdapter;
/*    */ import java.util.Date;
/*    */ 
/*    */ public abstract class ServiceAdapterControl extends BaseControl
/*    */   implements ServiceAdapterControlMBean
/*    */ {
/*    */   protected ServiceAdapter serviceAdapter;
/*    */ 
/*    */   public ServiceAdapterControl(ServiceAdapter serviceAdapter, BaseControl parent)
/*    */   {
/* 47 */     super(parent);
/* 48 */     this.serviceAdapter = serviceAdapter;
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 57 */     return this.serviceAdapter.getId();
/*    */   }
/*    */ 
/*    */   public Boolean isRunning()
/*    */   {
/* 66 */     return Boolean.valueOf(this.serviceAdapter.isStarted());
/*    */   }
/*    */ 
/*    */   public Date getStartTimestamp()
/*    */   {
/* 75 */     return this.startTimestamp;
/*    */   }
/*    */ 
/*    */   public void preDeregister()
/*    */     throws Exception
/*    */   {
/* 84 */     DestinationControl parent = (DestinationControl)getParentControl();
/* 85 */     parent.setAdapter(null);
/*    */ 
/* 87 */     super.preDeregister();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.ServiceAdapterControl
 * JD-Core Version:    0.6.0
 */