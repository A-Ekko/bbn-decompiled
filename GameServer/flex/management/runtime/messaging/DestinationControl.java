/*     */ package flex.management.runtime.messaging;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.messaging.services.ServiceControl;
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.services.ServiceAdapter;
/*     */ import java.util.Date;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public abstract class DestinationControl extends BaseControl
/*     */   implements DestinationControlMBean
/*     */ {
/*     */   protected Destination destination;
/*     */   private ObjectName adapter;
/*     */ 
/*     */   public DestinationControl(Destination destination, BaseControl parent)
/*     */   {
/*  50 */     super(parent);
/*  51 */     this.destination = destination;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  60 */     return this.destination.getId();
/*     */   }
/*     */ 
/*     */   public ObjectName getAdapter()
/*     */   {
/*  69 */     return this.adapter;
/*     */   }
/*     */ 
/*     */   public void setAdapter(ObjectName value)
/*     */   {
/*  79 */     this.adapter = value;
/*     */   }
/*     */ 
/*     */   public Boolean isRunning()
/*     */   {
/*  88 */     return Boolean.valueOf(this.destination.isStarted());
/*     */   }
/*     */ 
/*     */   public Date getStartTimestamp()
/*     */   {
/*  97 */     return this.startTimestamp;
/*     */   }
/*     */ 
/*     */   public void preDeregister()
/*     */     throws Exception
/*     */   {
/* 106 */     ServiceControl parent = (ServiceControl)getParentControl();
/* 107 */     parent.removeDestination(getObjectName());
/*     */ 
/* 110 */     ServiceAdapter child = this.destination.getAdapter();
/* 111 */     if (child.getControl() != null)
/*     */     {
/* 113 */       child.getControl().unregister();
/* 114 */       child.setControl(null);
/* 115 */       child.setManaged(false);
/*     */     }
/*     */ 
/* 118 */     super.preDeregister();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.DestinationControl
 * JD-Core Version:    0.6.0
 */