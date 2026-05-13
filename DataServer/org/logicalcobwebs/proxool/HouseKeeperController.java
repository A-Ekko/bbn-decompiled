/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.util.FastArrayList;
/*     */ 
/*     */ public class HouseKeeperController
/*     */ {
/*  26 */   private static final Log LOG = LogFactory.getLog(HouseKeeperController.class);
/*     */ 
/*  28 */   private static Map houseKeepers = new HashMap();
/*     */ 
/*  30 */   private static List houseKeeperList = new FastArrayList();
/*     */ 
/*  32 */   private static int houseKeeperIndex = 0;
/*     */ 
/*  34 */   private static List houseKeeperThreads = new FastArrayList();
/*     */ 
/*  36 */   private static final Object LOCK = new Integer(1);
/*     */ 
/*     */   private static HouseKeeper getHouseKeeper(String alias) throws ProxoolException {
/*  39 */     HouseKeeper houseKeeper = (HouseKeeper)houseKeepers.get(alias);
/*  40 */     if (houseKeeper == null) {
/*  41 */       throw new ProxoolException("Tried to use an unregistered house keeper '" + alias + "'");
/*     */     }
/*  43 */     return houseKeeper;
/*     */   }
/*     */ 
/*     */   protected static HouseKeeper getHouseKeeperToRun()
/*     */   {
/*  51 */     HouseKeeper houseKeeper = null;
/*  52 */     synchronized (LOCK) {
/*  53 */       for (int i = 0; i < houseKeeperList.size(); i++) {
/*  54 */         HouseKeeper hk = null;
/*     */         try {
/*  56 */           hk = (HouseKeeper)houseKeeperList.get(houseKeeperIndex);
/*  57 */           if (hk.isSweepDue()) {
/*  58 */             houseKeeper = hk;
/*  59 */             break;
/*     */           }
/*  61 */           houseKeeperIndex += 1;
/*     */         } catch (IndexOutOfBoundsException e) {
/*  63 */           houseKeeperIndex = 0;
/*     */         }
/*     */       }
/*     */     }
/*  67 */     return houseKeeper;
/*     */   }
/*     */ 
/*     */   protected static void sweepNow(String alias) {
/*     */     try {
/*  72 */       getHouseKeeper(alias).sweep();
/*     */     } catch (ProxoolException e) {
/*  74 */       LOG.error("Couldn't run house keeper for " + alias, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static void register(ConnectionPool connectionPool)
/*     */   {
/*  83 */     String alias = connectionPool.getDefinition().getAlias();
/*  84 */     LOG.debug("Registering '" + alias + "' house keeper");
/*  85 */     HouseKeeper houseKeeper = new HouseKeeper(connectionPool);
/*  86 */     synchronized (LOCK) {
/*  87 */       houseKeepers.put(alias, houseKeeper);
/*  88 */       houseKeeperList.add(houseKeeper);
/*     */ 
/*  90 */       if (houseKeeperThreads.size() == 0) {
/*  91 */         HouseKeeperThread hkt = new HouseKeeperThread("HouseKeeper");
/*  92 */         LOG.debug("Starting a house keeper thread");
/*  93 */         hkt.start();
/*  94 */         houseKeeperThreads.add(hkt);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static void shutdown()
/*     */   {
/* 103 */     synchronized (LOCK) {
/* 104 */       Iterator i = houseKeeperThreads.iterator();
/* 105 */       while (i.hasNext()) {
/* 106 */         HouseKeeperThread hkt = (HouseKeeperThread)i.next();
/* 107 */         LOG.info("Stopping " + hkt.getName() + " thread");
/* 108 */         hkt.cancel();
/*     */       }
/* 110 */       houseKeeperThreads.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static void cancel(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 122 */     HouseKeeper hk = getHouseKeeper(alias);
/* 123 */     houseKeepers.remove(alias);
/* 124 */     houseKeeperList.remove(hk);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.HouseKeeperController
 * JD-Core Version:    0.6.0
 */