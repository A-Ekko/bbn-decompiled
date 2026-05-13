/*     */ package flex.messaging.util;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.Executors;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
/*     */ import flex.messaging.FlexComponent;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RedeployManager
/*     */   implements FlexComponent
/*     */ {
/*     */   private boolean enabled;
/*     */   private long watchInterval;
/*     */   private List watches;
/*     */   private List touches;
/*     */   private ScheduledExecutorService redeployService;
/*     */   private boolean started;
/*     */ 
/*     */   public RedeployManager()
/*     */   {
/*  57 */     this(null);
/*     */   }
/*     */ 
/*     */   public RedeployManager(ThreadFactory tf)
/*     */   {
/*  68 */     if (tf == null) {
/*  69 */       tf = new MonitorThreadFactory();
/*     */     }
/*  71 */     this.enabled = false;
/*  72 */     this.touches = new ArrayList();
/*  73 */     this.watchInterval = 20L;
/*  74 */     this.watches = new ArrayList();
/*     */ 
/*  76 */     this.redeployService = Executors.newSingleThreadScheduledExecutor(tf);
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 101 */     if ((!this.started) && (this.enabled))
/*     */     {
/* 103 */       this.redeployService.schedule(new RedeployTask(), 20000L, TimeUnit.MILLISECONDS);
/* 104 */       this.started = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 113 */     if ((this.started) && (this.enabled))
/*     */     {
/* 115 */       this.redeployService.shutdown();
/* 116 */       this.started = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 133 */     return this.enabled;
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 143 */     this.enabled = enabled;
/*     */   }
/*     */ 
/*     */   public boolean isStarted()
/*     */   {
/* 154 */     return this.started;
/*     */   }
/*     */ 
/*     */   public long getWatchInterval()
/*     */   {
/* 164 */     return this.watchInterval;
/*     */   }
/*     */ 
/*     */   public void setWatchInterval(long watchInterval)
/*     */   {
/* 174 */     this.watchInterval = watchInterval;
/*     */   }
/*     */ 
/*     */   public List getWatchFiles()
/*     */   {
/* 184 */     return this.watches;
/*     */   }
/*     */ 
/*     */   public void addWatchFile(String watch)
/*     */   {
/* 195 */     this.watches.add(watch);
/*     */   }
/*     */ 
/*     */   public void setWatchFiles(List watches)
/*     */   {
/* 206 */     this.watches = watches;
/*     */   }
/*     */ 
/*     */   public List getTouchFiles()
/*     */   {
/* 216 */     return this.touches;
/*     */   }
/*     */ 
/*     */   public void addTouchFile(String touch)
/*     */   {
/* 227 */     this.touches.add(touch);
/*     */   }
/*     */ 
/*     */   public void setTouchFiles(List touches)
/*     */   {
/* 238 */     this.touches = touches;
/*     */   }
/*     */ 
/*     */   public void forceRedeploy()
/*     */   {
/* 246 */     Iterator iter = this.touches.iterator();
/* 247 */     while (iter.hasNext())
/*     */     {
/* 249 */       String filename = (String)iter.next();
/* 250 */       File file = new File(filename);
/* 251 */       if ((file.exists()) && ((file.isFile()) || (file.isDirectory())))
/*     */       {
/* 253 */         file.setLastModified(System.currentTimeMillis());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class RedeployTask
/*     */     implements Runnable
/*     */   {
/*     */     RedeployTask()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 279 */       boolean redeploy = false;
/*     */ 
/* 282 */       Iterator iter = RedeployManager.this.watches.iterator();
/* 283 */       while ((iter.hasNext()) && (!redeploy))
/*     */       {
/* 285 */         WatchedObject watched = (WatchedObject)iter.next();
/* 286 */         if (!watched.isUptodate()) {
/* 287 */           redeploy = true;
/*     */         }
/*     */       }
/* 290 */       if (redeploy)
/* 291 */         RedeployManager.this.forceRedeploy();
/*     */       else
/* 293 */         RedeployManager.this.redeployService.schedule(new RedeployTask(RedeployManager.this), RedeployManager.this.watchInterval * 1000L, TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */   class MonitorThreadFactory
/*     */     implements ThreadFactory
/*     */   {
/*     */     MonitorThreadFactory()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Thread newThread(Runnable r)
/*     */     {
/* 268 */       Thread t = new Thread(r);
/* 269 */       t.setDaemon(true);
/* 270 */       t.setName("RedeployManager");
/* 271 */       return t;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.RedeployManager
 * JD-Core Version:    0.6.0
 */