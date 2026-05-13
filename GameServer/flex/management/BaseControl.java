/*     */ package flex.management;
/*     */ 
/*     */ import flex.management.runtime.AdminConsoleDisplayRegistrar;
/*     */ import flex.messaging.FlexContext;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import javax.management.InstanceAlreadyExistsException;
/*     */ import javax.management.InstanceNotFoundException;
/*     */ import javax.management.MBeanRegistration;
/*     */ import javax.management.MBeanRegistrationException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.MalformedObjectNameException;
/*     */ import javax.management.NotCompliantMBeanException;
/*     */ import javax.management.ObjectInstance;
/*     */ import javax.management.ObjectName;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ 
/*     */ public abstract class BaseControl
/*     */   implements BaseControlMBean, MBeanRegistration
/*     */ {
/*     */   public static final String DOMAIN_PREFIX = "flex.runtime";
/*     */   private static final int MALFORMED_OBJECTNAME = 10400;
/*     */   private static final int UNREG_EXCEPTION = 10401;
/*     */   private static final int UNREG_NOTFOUND = 10402;
/*     */   private static final int REG_EXCEPTION = 10403;
/*     */   private static final int REG_ALREADYEXISTS = 10404;
/*     */   private static final int REG_NOTCOMPLIANT = 10405;
/*     */   private static final int DISABLE_MANAGEMENT = 10426;
/*     */   protected Date startTimestamp;
/*     */   private BaseControl parent;
/*     */   private ObjectName objectName;
/*     */   private ObjectName registeredObjectName;
/*     */   private MBeanServer server;
/*  72 */   private boolean registered = false;
/*     */   private AdminConsoleDisplayRegistrar registrar;
/*     */ 
/*     */   public abstract String getId();
/*     */ 
/*     */   public abstract String getType();
/*     */ 
/*     */   public final ObjectName getParent()
/*     */   {
/*  88 */     return this.parent != null ? this.parent.getObjectName() : null;
/*     */   }
/*     */ 
/*     */   public String getApplicationId()
/*     */   {
/* 100 */     String id = null;
/*     */ 
/* 103 */     ServletConfig config = FlexContext.getServletConfig();
/* 104 */     if (config != null)
/*     */     {
/* 106 */       id = config.getServletContext().getServletContextName();
/*     */     }
/* 108 */     return id != null ? id : "";
/*     */   }
/*     */ 
/*     */   protected void setRegistrar(AdminConsoleDisplayRegistrar registrar)
/*     */   {
/* 116 */     this.registrar = registrar;
/*     */   }
/*     */ 
/*     */   public AdminConsoleDisplayRegistrar getRegistrar()
/*     */   {
/* 124 */     if ((this.parent == null) && (this.registrar == null))
/*     */     {
/* 126 */       return new AdminConsoleDisplayRegistrar(null);
/*     */     }
/*     */ 
/* 129 */     return this.registrar != null ? this.registrar : this.parent.getRegistrar();
/*     */   }
/*     */ 
/*     */   public BaseControl(BaseControl parent)
/*     */   {
/* 141 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public final BaseControl getParentControl()
/*     */   {
/* 151 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public final MBeanServer getMBeanServer()
/*     */   {
/* 164 */     return this.server;
/*     */   }
/*     */ 
/*     */   public final void register()
/*     */   {
/* 177 */     if (!this.registered)
/*     */     {
/* 179 */       MBeanServer server = MBeanServerLocatorFactory.getMBeanServerLocator().getMBeanServer();
/* 180 */       ObjectName name = getObjectName();
/*     */       try
/*     */       {
/* 183 */         if (server.isRegistered(name))
/*     */         {
/* 185 */           server.unregisterMBean(name);
/*     */         }
/*     */ 
/* 188 */         this.registeredObjectName = server.registerMBean(this, name).getObjectName();
/* 189 */         this.registered = true;
/* 190 */         onRegistrationComplete();
/*     */       }
/*     */       catch (ManagementException me)
/*     */       {
/* 195 */         throw me;
/*     */       }
/*     */       catch (MBeanRegistrationException mre)
/*     */       {
/* 200 */         ManagementException me = new ManagementException();
/* 201 */         me.setMessage(10403, new Object[] { name.toString() });
/* 202 */         me.setRootCause(mre);
/* 203 */         throw me;
/*     */       }
/*     */       catch (InstanceAlreadyExistsException iaee)
/*     */       {
/* 211 */         if (!server.isRegistered(name))
/*     */         {
/* 213 */           ManagementException me = new ManagementException();
/* 214 */           me.setMessage(10426, new Object[] { name.toString() });
/* 215 */           throw me;
/*     */         }
/*     */ 
/* 220 */         ManagementException me = new ManagementException();
/* 221 */         me.setMessage(10404, new Object[] { name.toString() });
/* 222 */         throw me;
/*     */       }
/*     */       catch (NotCompliantMBeanException ncme)
/*     */       {
/* 228 */         ManagementException me = new ManagementException();
/* 229 */         me.setMessage(10405, new Object[] { name.toString() });
/* 230 */         throw me;
/*     */       }
/*     */       catch (InstanceNotFoundException infe)
/*     */       {
/* 235 */         ManagementException me = new ManagementException();
/* 236 */         me.setMessage(10402, new Object[] { name.toString() });
/* 237 */         throw me;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onRegistrationComplete()
/*     */   {
/*     */   }
/*     */ 
/*     */   public final void unregister()
/*     */   {
/* 259 */     if (this.registered)
/*     */     {
/*     */       try
/*     */       {
/* 268 */         if (this.server.isRegistered(this.registeredObjectName))
/*     */         {
/* 270 */           this.server.unregisterMBean(this.registeredObjectName);
/*     */         }
/*     */ 
/* 273 */         this.registeredObjectName = null;
/* 274 */         this.registered = false;
/*     */       }
/*     */       catch (ManagementException me)
/*     */       {
/* 278 */         throw me;
/*     */       }
/*     */       catch (MBeanRegistrationException mre)
/*     */       {
/* 283 */         ManagementException me = new ManagementException();
/* 284 */         me.setMessage(10401, new Object[] { this.registeredObjectName.toString() });
/* 285 */         if (me.getMessage().indexOf(Integer.toString(10401)) != -1)
/*     */         {
/* 287 */           me.setMessage("The MBean named, '" + this.registeredObjectName.toString() + "', could not be unregistered because its preDeregister() method threw an exception.");
/*     */         }
/* 289 */         me.setRootCause(mre);
/* 290 */         throw me;
/*     */       }
/*     */       catch (InstanceNotFoundException infe)
/*     */       {
/* 295 */         ManagementException me = new ManagementException();
/* 296 */         me.setMessage(10402, new Object[] { this.registeredObjectName.toString() });
/* 297 */         if (me.getMessage().indexOf(Integer.toString(10402)) != -1)
/*     */         {
/* 299 */           me.setMessage("The MBean named, '" + this.registeredObjectName.toString() + "', could not be unregistered because it is not currently registered.");
/*     */         }
/* 301 */         throw me;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public final ObjectName getObjectName()
/*     */   {
/* 354 */     if (this.registered) {
/* 355 */       return this.registeredObjectName;
/*     */     }
/* 357 */     if (this.objectName == null)
/*     */     {
/* 359 */       StringBuffer buffer = new StringBuffer();
/* 360 */       buffer.append("flex.runtime");
/* 361 */       String appId = getApplicationId();
/* 362 */       if ((appId != null) && (appId.length() > 0))
/*     */       {
/* 364 */         buffer.append('.');
/* 365 */         buffer.append(appId);
/*     */       }
/* 367 */       buffer.append(":type=");
/*     */ 
/* 369 */       List types = new ArrayList();
/* 370 */       List ids = new ArrayList();
/* 371 */       types.add(getType());
/* 372 */       ids.add(getId());
/* 373 */       BaseControl ancestor = this.parent;
/* 374 */       while (ancestor != null)
/*     */       {
/* 376 */         types.add(ancestor.getType());
/* 377 */         ids.add(ancestor.getId());
/* 378 */         ancestor = ancestor.getParentControl();
/*     */       }
/* 380 */       for (int i = types.size() - 1; i >= 0; i--)
/*     */       {
/* 382 */         buffer.append((String)types.get(i));
/* 383 */         if (i <= 0)
/*     */           continue;
/* 385 */         buffer.append('.');
/*     */       }
/*     */ 
/* 388 */       buffer.append(',');
/*     */ 
/* 390 */       for (int i = ids.size() - 1; i >= 1; i--)
/*     */       {
/* 392 */         buffer.append((String)types.get(i));
/* 393 */         buffer.append('=');
/* 394 */         buffer.append((String)ids.get(i));
/* 395 */         buffer.append(',');
/*     */       }
/* 397 */       buffer.append("id=");
/* 398 */       buffer.append(getId());
/* 399 */       String name = buffer.toString();
/*     */       try
/*     */       {
/* 404 */         this.objectName = new ObjectName(name);
/*     */       }
/*     */       catch (MalformedObjectNameException mone)
/*     */       {
/* 409 */         ManagementException me = new ManagementException();
/* 410 */         me.setMessage(10400, new Object[] { name });
/* 411 */         throw me;
/*     */       }
/*     */     }
/* 414 */     return this.objectName;
/*     */   }
/*     */ 
/*     */   public ObjectName preRegister(MBeanServer server, ObjectName name)
/*     */     throws Exception
/*     */   {
/* 432 */     this.server = server;
/* 433 */     return name == null ? getObjectName() : name;
/*     */   }
/*     */ 
/*     */   public void postRegister(Boolean registrationDone)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void preDeregister()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void postDeregister()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setStartTimestamp(Date value)
/*     */   {
/* 479 */     this.startTimestamp = value;
/*     */   }
/*     */ 
/*     */   protected double differenceInMinutes(long startTime, long endTime)
/*     */   {
/* 493 */     double minutes = (endTime - startTime) / 60000.0D;
/* 494 */     if (minutes > 1.0D)
/*     */     {
/* 496 */       return minutes;
/*     */     }
/*     */ 
/* 500 */     return 1.0D;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.BaseControl
 * JD-Core Version:    0.6.0
 */