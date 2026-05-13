/*     */ package flex.management;
/*     */ 
/*     */ import flex.messaging.FlexComponent;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import java.util.Date;
/*     */ 
/*     */ public abstract class ManageableComponent
/*     */   implements Manageable, FlexComponent
/*     */ {
/*     */   protected static final int PROPERTY_CHANGE_AFTER_STARTUP = 11115;
/*     */   protected static final int NULL_COMPONENT_PROPERTY = 11116;
/*     */   protected BaseControl control;
/*     */   protected String id;
/*     */   protected boolean managed;
/*     */   protected Manageable parent;
/*     */   protected volatile boolean started;
/*     */   protected boolean valid;
/*     */ 
/*     */   public ManageableComponent(boolean enableManagement)
/*     */   {
/*  73 */     setManaged(enableManagement);
/*     */   }
/*     */ 
/*     */   public BaseControl getControl()
/*     */   {
/*  96 */     return this.control;
/*     */   }
/*     */ 
/*     */   public void setControl(BaseControl control)
/*     */   {
/* 104 */     this.control = control;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 123 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/* 134 */     if (isStarted())
/*     */     {
/* 136 */       blockAssignmentWhileStarted("id");
/*     */     }
/* 138 */     if (id == null)
/*     */     {
/* 141 */       blockNullAssignment("id");
/*     */     }
/* 143 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public boolean isManaged()
/*     */   {
/* 160 */     return this.managed;
/*     */   }
/*     */ 
/*     */   public void setManaged(boolean enableManagement)
/*     */   {
/* 172 */     if ((isStarted()) && (this.control != null))
/*     */     {
/* 174 */       blockAssignmentWhileStarted("managed");
/*     */     }
/* 176 */     if (this.parent != null)
/*     */     {
/* 178 */       if ((enableManagement) && (!this.parent.isManaged()))
/*     */       {
/* 180 */         if (Log.isWarn())
/*     */         {
/* 182 */           Log.getLogger(getLogCategory()).warn("Component: '" + this.id + "' cannot be managed" + " since its parent is unmanaged.");
/*     */         }
/*     */ 
/* 185 */         return;
/*     */       }
/*     */     }
/* 188 */     this.managed = enableManagement;
/*     */   }
/*     */ 
/*     */   public Manageable getParent()
/*     */   {
/* 207 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public void setParent(Manageable parent)
/*     */   {
/* 218 */     if (isStarted())
/*     */     {
/* 220 */       blockAssignmentWhileStarted("parent");
/*     */     }
/* 222 */     if (parent == null)
/*     */     {
/* 225 */       blockNullAssignment("parent");
/*     */     }
/* 227 */     if ((!parent.isManaged()) && (isManaged()))
/*     */     {
/* 229 */       if (Log.isWarn())
/*     */       {
/* 231 */         Log.getLogger(getLogCategory()).warn("Component: '" + this.id + "' cannot be managed" + " since its parent is unmanaged.");
/*     */       }
/*     */ 
/* 234 */       setManaged(false);
/*     */     }
/* 236 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public boolean isStarted()
/*     */   {
/* 255 */     return this.started;
/*     */   }
/*     */ 
/*     */   protected void setStarted(boolean started)
/*     */   {
/* 265 */     if (this.started != started)
/*     */     {
/* 267 */       this.started = started;
/* 268 */       if ((started) && (this.control != null))
/*     */       {
/* 270 */         this.control.setStartTimestamp(new Date());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 291 */     return this.valid;
/*     */   }
/*     */ 
/*     */   protected void setValid(boolean valid)
/*     */   {
/* 301 */     this.valid = valid;
/*     */   }
/*     */ 
/*     */   protected abstract String getLogCategory();
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/* 333 */     setId(id);
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 343 */     validate();
/* 344 */     setStarted(true);
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 354 */     invalidate();
/* 355 */     setStarted(false);
/*     */   }
/*     */ 
/*     */   protected void blockAssignmentWhileStarted(String propertyName)
/*     */   {
/* 372 */     ConfigurationException ce = new ConfigurationException();
/* 373 */     ce.setMessage(11115, new Object[] { propertyName });
/* 374 */     throw ce;
/*     */   }
/*     */ 
/*     */   protected void blockNullAssignment(String propertyName)
/*     */   {
/* 385 */     ConfigurationException ce = new ConfigurationException();
/* 386 */     ce.setMessage(11116, new Object[] { propertyName });
/* 387 */     throw ce;
/*     */   }
/*     */ 
/*     */   protected void invalidate()
/*     */   {
/* 397 */     setValid(false);
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/* 410 */     if (getId() == null)
/*     */     {
/* 413 */       blockNullAssignment("id");
/*     */     }
/* 415 */     if (getParent() == null)
/*     */     {
/* 418 */       blockNullAssignment("parent");
/*     */     }
/* 420 */     setValid(true);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.ManageableComponent
 * JD-Core Version:    0.6.0
 */