/*     */ package flex.management.runtime.messaging.log;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.Manageable;
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.LogCategories;
/*     */ import flex.messaging.log.Target;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class LogManager extends ManageableComponent
/*     */ {
/*     */   private static final String LOG_CATEGORY = "Configuration";
/*     */   private static final int NULL_LOG_REF_EXCEPTION = 10031;
/*     */   private Log log;
/*  26 */   private String ID = "log";
/*     */   private CategoryManager categoryManager;
/*     */   private LogControl controller;
/*     */   private boolean isSetup;
/*     */ 
/*     */   public LogManager()
/*     */   {
/*  37 */     this(true);
/*     */   }
/*     */ 
/*     */   public LogManager(boolean enableManagement)
/*     */   {
/*  42 */     super(enableManagement);
/*  43 */     setId(this.ID);
/*     */ 
/*  45 */     this.categoryManager = new CategoryManager();
/*     */   }
/*     */ 
/*     */   public void setupLogControl()
/*     */   {
/*  52 */     if (!this.isSetup)
/*     */     {
/*  54 */       this.controller = new LogControl(getParent().getControl(), this);
/*  55 */       setControl(this.controller);
/*  56 */       this.controller.register();
/*  57 */       this.isSetup = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/*  63 */     if (!isStarted())
/*     */     {
/*  65 */       return;
/*     */     }
/*     */ 
/*  69 */     super.stop();
/*     */ 
/*  72 */     if (isManaged())
/*     */     {
/*  74 */       if (getControl() != null)
/*     */       {
/*  76 */         getControl().unregister();
/*  77 */         setControl(null);
/*     */       }
/*  79 */       setManaged(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLog(Log logInstance)
/*     */   {
/*  85 */     this.log = logInstance;
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/*  93 */     return "Configuration";
/*     */   }
/*     */ 
/*     */   public String[] getLoggers()
/*     */   {
/* 102 */     return this.log.getLoggers();
/*     */   }
/*     */ 
/*     */   public String[] getTargetIds()
/*     */   {
/* 111 */     return (String[])(String[])Log.getTargetMap().keySet().toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public Target getTarget(String targetId)
/*     */   {
/* 122 */     return (Target)Log.getTargetMap().get(targetId);
/*     */   }
/*     */ 
/*     */   public String[] getTargetFilters(String targetId)
/*     */   {
/* 133 */     Target target = getTarget(targetId);
/*     */ 
/* 135 */     if (target == null) {
/* 136 */       return new String[0];
/*     */     }
/* 138 */     List filterObjects = target.getFilters();
/* 139 */     String[] filters = new String[filterObjects.size()];
/* 140 */     for (int i = 0; i < filterObjects.size(); i++)
/*     */     {
/* 142 */       filters[i] = ((String)filterObjects.get(i));
/*     */     }
/*     */ 
/* 145 */     return filters;
/*     */   }
/*     */ 
/*     */   public boolean checkFilter(String filter)
/*     */   {
/* 154 */     return this.categoryManager.checkFilter(filter);
/*     */   }
/*     */ 
/*     */   public List getCategories()
/*     */   {
/* 162 */     return this.categoryManager.getCategories();
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/* 167 */     if (isValid()) {
/* 168 */       return;
/*     */     }
/* 170 */     super.validate();
/*     */ 
/* 172 */     if (this.log == null)
/*     */     {
/* 174 */       invalidate();
/* 175 */       ConfigurationException ex = new ConfigurationException();
/* 176 */       ex.setMessage(10031, new Object[0]);
/* 177 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CategoryManager
/*     */     implements LogCategories
/*     */   {
/*     */     private List categories;
/*     */ 
/*     */     public CategoryManager()
/*     */     {
/* 200 */       this.categories = new ArrayList();
/*     */ 
/* 202 */       Field[] categoryFields = getClass().getFields();
/* 203 */       for (int i = 0; i < categoryFields.length; i++)
/*     */       {
/*     */         try
/*     */         {
/* 207 */           this.categories.add((String)categoryFields[i].get(this));
/*     */         }
/*     */         catch (IllegalAccessException iae)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean checkFilter(String filter)
/*     */     {
/* 224 */       for (int i = 0; i < this.categories.size(); i++)
/*     */       {
/* 226 */         if (Log.checkFilterToCategory(filter, (String)this.categories.get(i)))
/*     */         {
/* 228 */           return true;
/*     */         }
/*     */       }
/* 231 */       return false;
/*     */     }
/*     */ 
/*     */     public List getCategories()
/*     */     {
/* 239 */       return Collections.unmodifiableList(new ArrayList(this.categories));
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.log.LogManager
 * JD-Core Version:    0.6.0
 */