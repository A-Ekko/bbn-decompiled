/*     */ package flex.messaging.services;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.management.runtime.messaging.DestinationControl;
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ 
/*     */ public abstract class ServiceAdapter extends ManageableComponent
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Service.General";
/*     */ 
/*     */   public ServiceAdapter()
/*     */   {
/*  49 */     this(false);
/*     */   }
/*     */ 
/*     */   public ServiceAdapter(boolean enableManagement)
/*     */   {
/*  60 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/*  76 */     if (isValid()) {
/*  77 */       return;
/*     */     }
/*  79 */     super.validate();
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/*  89 */     if (isStarted())
/*     */     {
/*  91 */       return;
/*     */     }
/*     */ 
/*  95 */     Destination destination = getDestination();
/*  96 */     if (!destination.isStarted())
/*     */     {
/*  98 */       if (Log.isWarn())
/*     */       {
/* 100 */         Log.getLogger(getLogCategory()).warn("Adapter with id '{0}' cannot be started when its Destination with id '{1}' is not started.", new Object[] { getId(), destination.getId() });
/*     */       }
/*     */ 
/* 104 */       return;
/*     */     }
/*     */ 
/* 108 */     if ((isManaged()) && (destination.isManaged()))
/*     */     {
/* 110 */       setupAdapterControl(destination);
/* 111 */       DestinationControl controller = (DestinationControl)destination.getControl();
/* 112 */       if (getControl() != null) {
/* 113 */         controller.setAdapter(getControl().getObjectName());
/*     */       }
/*     */     }
/*     */ 
/* 117 */     super.start();
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 127 */     if (!isStarted())
/*     */     {
/* 129 */       return;
/*     */     }
/*     */ 
/* 132 */     super.stop();
/*     */ 
/* 135 */     if ((isManaged()) && (getDestination().isManaged()))
/*     */     {
/* 137 */       if (getControl() != null)
/*     */       {
/* 139 */         getControl().unregister();
/* 140 */         setControl(null);
/*     */       }
/* 142 */       setManaged(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Destination getDestination()
/*     */   {
/* 160 */     return (Destination)getParent();
/*     */   }
/*     */ 
/*     */   public void setDestination(Destination destination)
/*     */   {
/* 172 */     Destination oldDestination = getDestination();
/*     */ 
/* 174 */     setParent(destination);
/*     */ 
/* 176 */     if (oldDestination != null) {
/* 177 */       oldDestination.setAdapter(null);
/*     */     }
/*     */ 
/* 180 */     if (destination.getAdapter() != this)
/*     */     {
/* 182 */       destination.setAdapter(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract Object invoke(Message paramMessage);
/*     */ 
/*     */   public Object manage(CommandMessage commandMessage)
/*     */   {
/* 241 */     return null;
/*     */   }
/*     */ 
/*     */   public Object getAdapterState()
/*     */   {
/* 252 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void setAdapterState(Object adapterState)
/*     */   {
/* 263 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean handlesSubscriptions()
/*     */   {
/* 273 */     return false;
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 290 */     return "Service.General";
/*     */   }
/*     */ 
/*     */   protected void setupAdapterControl(Destination destination)
/*     */   {
/* 301 */     setManaged(false);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.ServiceAdapter
 * JD-Core Version:    0.6.0
 */