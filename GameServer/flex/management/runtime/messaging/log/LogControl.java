/*     */ package flex.management.runtime.messaging.log;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.messaging.log.AbstractTarget;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Target;
/*     */ import java.util.List;
/*     */ 
/*     */ public class LogControl extends BaseControl
/*     */   implements LogControlMBean
/*     */ {
/*     */   private static final String TYPE = "Log";
/*     */   private LogManager logManager;
/*     */ 
/*     */   public LogControl(BaseControl parent, LogManager manager)
/*     */   {
/*  46 */     super(parent);
/*  47 */     this.logManager = manager;
/*  48 */     register();
/*     */   }
/*     */ 
/*     */   public void changeTargetLevel(String searchId, String level)
/*     */   {
/*  59 */     Target selectedTarget = Log.getTarget(searchId);
/*  60 */     if (selectedTarget != null)
/*     */     {
/*  62 */       selectedTarget.setLevel(new Short(level).shortValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  71 */     return this.logManager.getId();
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  79 */     return "Log";
/*     */   }
/*     */ 
/*     */   public String[] getLoggers()
/*     */   {
/*  87 */     return this.logManager.getLoggers();
/*     */   }
/*     */ 
/*     */   public String[] getTargets()
/*     */   {
/*  95 */     return this.logManager.getTargetIds();
/*     */   }
/*     */ 
/*     */   public void addFilterForTarget(String targetId, String filter)
/*     */   {
/* 103 */     AbstractTarget target = (AbstractTarget)this.logManager.getTarget(targetId);
/*     */ 
/* 105 */     if (target != null)
/*     */     {
/* 107 */       if (this.logManager.checkFilter(filter))
/* 108 */         target.addFilter(filter);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getTargetFilters(String targetId)
/*     */   {
/* 118 */     return this.logManager.getTargetFilters(targetId);
/*     */   }
/*     */ 
/*     */   public void removeFilterForTarget(String targetId, String filter)
/*     */   {
/* 126 */     AbstractTarget target = (AbstractTarget)this.logManager.getTarget(targetId);
/*     */ 
/* 128 */     if (target != null)
/*     */     {
/* 130 */       if (target.containsFilter(filter))
/* 131 */         target.removeFilter(filter);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getCategories()
/*     */   {
/* 140 */     return (String[])(String[])this.logManager.getCategories().toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public Integer getTargetLevel(String searchId)
/*     */   {
/* 146 */     AbstractTarget target = (AbstractTarget)this.logManager.getTarget(searchId);
/*     */ 
/* 148 */     if (target != null)
/*     */     {
/* 150 */       return new Integer(target.getLevel());
/*     */     }
/* 152 */     return new Integer(-1);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.log.LogControl
 * JD-Core Version:    0.6.0
 */