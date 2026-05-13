/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class ConnectionResetter
/*     */ {
/*     */   private Log log;
/*     */   private boolean initialised;
/*  44 */   private Map accessorMutatorMap = new HashMap();
/*     */ 
/*  50 */   private Map defaultValues = new HashMap();
/*     */   protected static final String MUTATOR_PREFIX = "set";
/*     */   private String driverName;
/*     */   protected static boolean triggerResetException;
/*     */ 
/*     */   protected ConnectionResetter(Log log, String driverName)
/*     */   {
/*  69 */     this.log = log;
/*  70 */     this.driverName = driverName;
/*     */ 
/*  73 */     addReset("getCatalog", "setCatalog");
/*  74 */     addReset("isReadOnly", "setReadOnly");
/*  75 */     addReset("getTransactionIsolation", "setTransactionIsolation");
/*  76 */     addReset("getTypeMap", "setTypeMap");
/*  77 */     addReset("getHoldability", "setHoldability");
/*     */   }
/*     */ 
/*     */   private void addReset(String accessorName, String mutatorName)
/*     */   {
/*     */     try
/*     */     {
/*  90 */       Method accessor = null;
/*  91 */       Method mutator = null;
/*     */ 
/*  93 */       Method[] methods = Connection.class.getMethods();
/*  94 */       for (int i = 0; i < methods.length; i++) {
/*  95 */         Method method = methods[i];
/*  96 */         if (method.getName().equals(accessorName)) {
/*  97 */           if (accessor == null) {
/*  98 */             accessor = method;
/*     */           } else {
/* 100 */             this.log.info("Skipping ambiguous reset method " + accessorName);
/* 101 */             return;
/*     */           }
/*     */         }
/* 104 */         if (method.getName().equals(mutatorName)) {
/* 105 */           if (mutator == null) {
/* 106 */             mutator = method;
/*     */           } else {
/* 108 */             this.log.info("Skipping ambiguous reset method " + mutatorName);
/* 109 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 114 */       if (accessor == null) {
/* 115 */         this.log.debug("Ignoring attempt to map reset method " + accessorName + " (probably because it isn't implemented in this JDK)");
/* 116 */       } else if (mutator == null) {
/* 117 */         this.log.debug("Ignoring attempt to map reset method " + mutatorName + " (probably because it isn't implemented in this JDK)");
/* 118 */       } else if (this.accessorMutatorMap.containsKey(accessor)) {
/* 119 */         this.log.warn("Ignoring attempt to map duplicate reset method " + accessorName);
/* 120 */       } else if (this.accessorMutatorMap.containsValue(mutator)) {
/* 121 */         this.log.warn("Ignoring attempt to map duplicate reset method " + mutatorName);
/*     */       }
/*     */       else {
/* 124 */         if (mutatorName.indexOf("set") != 0) {
/* 125 */           this.log.warn("Resetter mutator " + mutatorName + " does not start with " + "set" + " as expected. Proxool maynot recognise that a reset is necessary.");
/*     */         }
/*     */ 
/* 129 */         if (accessor.getParameterTypes().length > 0)
/* 130 */           this.log.info("Ignoring attempt to map accessor method " + accessorName + ". It must have no arguments.");
/* 131 */         else if (mutator.getParameterTypes().length != 1) {
/* 132 */           this.log.info("Ignoring attempt to map mutator method " + mutatorName + ". It must have exactly one argument, not " + mutator.getParameterTypes().length);
/*     */         }
/*     */         else
/* 135 */           this.accessorMutatorMap.put(accessor, mutator);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 139 */       this.log.error("Problem mapping " + accessorName + " and " + mutatorName, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void initialise(Connection connection)
/*     */   {
/* 150 */     if (!this.initialised)
/* 151 */       synchronized (this) {
/* 152 */         if (!this.initialised)
/*     */         {
/* 154 */           Set accessorsToRemove = new HashSet();
/* 155 */           Iterator i = this.accessorMutatorMap.keySet().iterator();
/* 156 */           while (i.hasNext()) {
/* 157 */             Method accessor = (Method)i.next();
/* 158 */             Method mutator = (Method)this.accessorMutatorMap.get(accessor);
/* 159 */             Object value = null;
/*     */             try {
/* 161 */               value = accessor.invoke(connection, null);
/*     */ 
/* 164 */               if (value != null) {
/* 165 */                 this.defaultValues.put(mutator, value);
/*     */               }
/* 167 */               if (this.log.isDebugEnabled())
/* 168 */                 this.log.debug("Remembering default value: " + accessor.getName() + "() = " + value);
/*     */             }
/*     */             catch (Throwable t)
/*     */             {
/* 172 */               this.log.debug(this.driverName + " does not support " + accessor.getName() + ". Proxool doesn't mind.");
/*     */ 
/* 174 */               accessorsToRemove.add(accessor);
/*     */             }
/*     */ 
/*     */             try
/*     */             {
/* 180 */               Object[] args = { value };
/* 181 */               mutator.invoke(connection, args);
/*     */             } catch (Throwable t) {
/* 183 */               this.log.debug(this.driverName + " does not support " + mutator.getName() + ". Proxool doesn't mind.");
/*     */ 
/* 185 */               accessorsToRemove.add(accessor);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 191 */           Iterator j = accessorsToRemove.iterator();
/* 192 */           while (j.hasNext()) {
/* 193 */             Method accessor = (Method)j.next();
/* 194 */             Method mutator = (Method)this.accessorMutatorMap.get(accessor);
/* 195 */             this.accessorMutatorMap.remove(accessor);
/* 196 */             this.defaultValues.remove(mutator);
/*     */           }
/*     */ 
/* 199 */           this.initialised = true;
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   protected boolean reset(Connection connection, String id)
/*     */   {
/* 213 */     boolean errorsEncountered = false;
/*     */     try
/*     */     {
/* 216 */       connection.clearWarnings();
/*     */     } catch (SQLException e) {
/* 218 */       errorsEncountered = true;
/* 219 */       this.log.warn(id + " - Problem calling connection.clearWarnings()", e);
/*     */     }
/*     */ 
/* 223 */     boolean autoCommit = true;
/*     */     try {
/* 225 */       autoCommit = connection.getAutoCommit();
/*     */     } catch (SQLException e) {
/* 227 */       errorsEncountered = true;
/* 228 */       this.log.warn(id + " - Problem calling connection.getAutoCommit()", e);
/*     */     }
/*     */ 
/* 244 */     if (!autoCommit) {
/*     */       try {
/* 246 */         connection.rollback();
/*     */       } catch (SQLException e) {
/* 248 */         this.log.error("Unexpected exception whilst calling rollback during connection reset", e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 258 */     Iterator i = this.accessorMutatorMap.keySet().iterator();
/* 259 */     while (i.hasNext()) {
/* 260 */       Method accessor = (Method)i.next();
/* 261 */       Method mutator = (Method)this.accessorMutatorMap.get(accessor);
/* 262 */       Object[] args = { this.defaultValues.get(mutator) };
/*     */       try {
/* 264 */         Object currentValue = accessor.invoke(connection, null);
/* 265 */         if ((currentValue != null) || (args[0] != null))
/*     */         {
/* 267 */           if (!currentValue.equals(args[0]))
/*     */           {
/* 270 */             mutator.invoke(connection, args);
/* 271 */             if (this.log.isDebugEnabled())
/* 272 */               this.log.debug(id + " - Reset: " + mutator.getName() + "(" + args[0] + ") from " + currentValue);
/*     */           }
/*     */         }
/*     */       } catch (Throwable t) {
/* 276 */         errorsEncountered = true;
/* 277 */         if (this.log.isDebugEnabled()) {
/* 278 */           this.log.debug(id + " - Problem resetting: " + mutator.getName() + "(" + args[0] + ").", t);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 284 */     if (!autoCommit)
/*     */     {
/*     */       try
/*     */       {
/* 288 */         connection.setAutoCommit(true);
/* 289 */         this.log.debug(id + " - autoCommit reset back to true");
/*     */       } catch (Throwable t) {
/* 291 */         errorsEncountered = true;
/* 292 */         this.log.warn(id + " - Problem calling connection.commit() or connection.setAutoCommit(true)", t);
/*     */       }
/*     */     }
/*     */ 
/* 296 */     if (isTriggerResetException()) {
/* 297 */       this.log.warn("Triggering pretend exception during reset");
/* 298 */       errorsEncountered = true;
/*     */     }
/*     */ 
/* 301 */     if (errorsEncountered)
/*     */     {
/* 303 */       this.log.warn(id + " - There were some problems resetting the connection (see debug output for details). It will not be used again " + "(just in case). The thread that is responsible is named '" + Thread.currentThread().getName() + "'");
/*     */ 
/* 305 */       if (!autoCommit) {
/* 306 */         this.log.warn(id + " - The connection was closed with autoCommit=false. That is fine, but it might indicate that " + "the problems that happened whilst trying to reset it were because a transaction is still in progress.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 311 */     return !errorsEncountered;
/*     */   }
/*     */ 
/*     */   private static boolean isTriggerResetException() {
/* 315 */     return triggerResetException;
/*     */   }
/*     */ 
/*     */   protected static void setTriggerResetException(boolean triggerResetException)
/*     */   {
/* 324 */     triggerResetException = triggerResetException;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionResetter
 * JD-Core Version:    0.6.0
 */