/*     */ package flex.management.runtime.messaging.services;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.messaging.MessageBrokerControl;
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.services.Service;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public abstract class ServiceControl extends BaseControl
/*     */   implements ServiceControlMBean
/*     */ {
/*     */   protected Service service;
/*     */   private List destinations;
/*     */ 
/*     */   public ServiceControl(Service service, BaseControl parent)
/*     */   {
/*  53 */     super(parent);
/*  54 */     this.service = service;
/*  55 */     this.destinations = new ArrayList();
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  64 */     return this.service.getId();
/*     */   }
/*     */ 
/*     */   public Boolean isRunning()
/*     */   {
/*  73 */     return Boolean.valueOf(this.service.isStarted());
/*     */   }
/*     */ 
/*     */   public void addDestination(ObjectName value)
/*     */   {
/*  84 */     this.destinations.add(value);
/*     */   }
/*     */ 
/*     */   public void removeDestination(ObjectName value)
/*     */   {
/*  94 */     this.destinations.remove(value);
/*     */   }
/*     */ 
/*     */   public ObjectName[] getDestinations()
/*     */   {
/* 103 */     int size = this.destinations.size();
/* 104 */     ObjectName[] destinationNames = new ObjectName[size];
/* 105 */     for (int i = 0; i < size; i++)
/*     */     {
/* 107 */       destinationNames[i] = ((ObjectName)this.destinations.get(i));
/*     */     }
/* 109 */     return destinationNames;
/*     */   }
/*     */ 
/*     */   public Date getStartTimestamp()
/*     */   {
/* 119 */     return this.startTimestamp;
/*     */   }
/*     */ 
/*     */   public void preDeregister()
/*     */     throws Exception
/*     */   {
/* 129 */     MessageBrokerControl parent = (MessageBrokerControl)getParentControl();
/* 130 */     parent.removeService(getObjectName());
/*     */ 
/* 133 */     for (Iterator iter = this.service.getDestinations().values().iterator(); iter.hasNext(); ) {
/* 134 */       Destination child = (Destination)iter.next();
/* 135 */       if (child.getControl() != null)
/*     */       {
/* 137 */         child.getControl().unregister();
/* 138 */         child.setControl(null);
/* 139 */         child.setManaged(false);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 144 */     super.preDeregister();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.ServiceControl
 * JD-Core Version:    0.6.0
 */