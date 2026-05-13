/*     */ package org.logicalcobwebs.proxool.admin;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.concurrent.Sync;
/*     */ import org.logicalcobwebs.concurrent.WriterPreferenceReadWriteLock;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ 
/*     */ class StatsRoller
/*     */ {
/*  28 */   private static final Log LOG = LogFactory.getLog(StatsRoller.class);
/*     */ 
/*  30 */   private WriterPreferenceReadWriteLock readWriteLock = new WriterPreferenceReadWriteLock();
/*     */   private Statistics completeStatistics;
/*     */   private Statistics currentStatistics;
/*     */   private Calendar nextRollDate;
/*     */   private int period;
/*     */   private int units;
/*  42 */   private boolean running = true;
/*     */   private CompositeStatisticsListener compositeStatisticsListener;
/*     */   private String alias;
/*     */ 
/*     */   public StatsRoller(String alias, CompositeStatisticsListener compositeStatisticsListener, String token)
/*     */     throws ProxoolException
/*     */   {
/*  49 */     this.alias = alias;
/*  50 */     this.compositeStatisticsListener = compositeStatisticsListener;
/*     */ 
/*  52 */     this.nextRollDate = Calendar.getInstance();
/*  53 */     if (token.endsWith("s")) {
/*  54 */       this.units = 13;
/*  55 */       this.nextRollDate.clear(13);
/*  56 */       this.nextRollDate.clear(14);
/*  57 */     } else if (token.endsWith("m")) {
/*  58 */       this.units = 12;
/*  59 */       this.nextRollDate.clear(12);
/*  60 */       this.nextRollDate.clear(13);
/*  61 */       this.nextRollDate.clear(14);
/*  62 */     } else if (token.endsWith("h")) {
/*  63 */       this.nextRollDate.clear(11);
/*  64 */       this.nextRollDate.clear(12);
/*  65 */       this.nextRollDate.clear(13);
/*  66 */       this.nextRollDate.clear(14);
/*  67 */       this.units = 11;
/*  68 */     } else if (token.endsWith("d")) {
/*  69 */       this.units = 5;
/*  70 */       this.nextRollDate.clear(11);
/*  71 */       this.nextRollDate.clear(12);
/*  72 */       this.nextRollDate.clear(13);
/*  73 */       this.nextRollDate.clear(14);
/*     */     } else {
/*  75 */       throw new ProxoolException("Unrecognised suffix in statistics: " + token);
/*     */     }
/*     */ 
/*  78 */     this.period = Integer.parseInt(token.substring(0, token.length() - 1));
/*     */ 
/*  81 */     Calendar now = Calendar.getInstance();
/*  82 */     while (this.nextRollDate.before(now)) {
/*  83 */       this.nextRollDate.add(this.units, this.period);
/*     */     }
/*     */ 
/*  86 */     LOG.debug("Collecting first statistics for '" + token + "' at " + this.nextRollDate.getTime());
/*  87 */     this.currentStatistics = new Statistics(now.getTime());
/*     */ 
/*  90 */     Thread t = new Thread()
/*     */     {
/*     */       public void run() {
/*  93 */         while (StatsRoller.this.running) {
/*     */           try {
/*  95 */             Thread.sleep(5000L);
/*     */           } catch (InterruptedException e) {
/*  97 */             StatsRoller.LOG.debug("Interruption", e);
/*     */           }
/*  99 */           StatsRoller.this.roll();
/*     */         }
/*     */       }
/*     */     };
/* 104 */     t.setDaemon(true);
/* 105 */     t.start();
/*     */   }
/*     */ 
/*     */   protected void cancel()
/*     */   {
/* 112 */     this.running = false;
/*     */   }
/*     */ 
/*     */   private void roll() {
/* 116 */     if (!isCurrent())
/*     */       try {
/* 118 */         this.readWriteLock.writeLock().acquire();
/* 119 */         if (!isCurrent()) {
/* 120 */           this.currentStatistics.setStopDate(this.nextRollDate.getTime());
/* 121 */           this.completeStatistics = this.currentStatistics;
/* 122 */           this.currentStatistics = new Statistics(this.nextRollDate.getTime());
/* 123 */           this.nextRollDate.add(this.units, this.period);
/* 124 */           this.compositeStatisticsListener.statistics(this.alias, this.completeStatistics);
/*     */         }
/*     */       } catch (Throwable e) {
/* 127 */         LOG.error("Unable to roll statistics log", e);
/*     */       } finally {
/* 129 */         this.readWriteLock.writeLock().release();
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean isCurrent()
/*     */   {
/* 135 */     return System.currentTimeMillis() < this.nextRollDate.getTime().getTime();
/*     */   }
/*     */ 
/*     */   public void connectionReturned(long activeTime)
/*     */   {
/* 142 */     roll();
/*     */     try {
/* 144 */       this.readWriteLock.readLock().acquire();
/* 145 */       this.currentStatistics.connectionReturned(activeTime);
/*     */     } catch (InterruptedException e) {
/* 147 */       LOG.error("Unable to log connectionReturned", e);
/*     */     } finally {
/* 149 */       this.readWriteLock.readLock().release();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connectionRefused()
/*     */   {
/* 157 */     roll();
/*     */     try {
/* 159 */       this.readWriteLock.readLock().acquire();
/* 160 */       this.currentStatistics.connectionRefused();
/*     */     } catch (InterruptedException e) {
/* 162 */       LOG.error("Unable to log connectionRefused", e);
/*     */     } finally {
/* 164 */       this.readWriteLock.readLock().release();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Statistics getCompleteStatistics()
/*     */   {
/*     */     try
/*     */     {
/* 174 */       this.readWriteLock.readLock().acquire();
/* 175 */       Statistics localStatistics = this.completeStatistics;
/*     */       return localStatistics;
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 177 */       LOG.error("Couldn't read statistics", e);
/* 178 */       Object localObject1 = null;
/*     */       return localObject1; } finally { this.readWriteLock.readLock().release(); } throw localObject2;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.StatsRoller
 * JD-Core Version:    0.6.0
 */