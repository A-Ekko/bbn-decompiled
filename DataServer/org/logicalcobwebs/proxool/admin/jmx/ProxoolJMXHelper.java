/*     */ package org.logicalcobwebs.proxool.admin.jmx;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.MBeanServerFactory;
/*     */ import javax.management.MalformedObjectNameException;
/*     */ import javax.management.ObjectName;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.logicalcobwebs.proxool.ProxoolFacade;
/*     */ 
/*     */ public class ProxoolJMXHelper
/*     */ {
/*  32 */   private static final Log LOG = LogFactory.getLog(ProxoolJMXHelper.class);
/*     */ 
/*     */   public static void registerPool(String alias, Properties poolPropeties)
/*     */     throws ProxoolException
/*     */   {
/*  44 */     ConnectionPoolDefinitionIF connectionPoolDefinition = ProxoolFacade.getConnectionPoolDefinition(alias);
/*     */ 
/*  46 */     String[] agentIds = getAgentIds(poolPropeties);
/*  47 */     ArrayList servers = null;
/*  48 */     for (int i = 0; i < agentIds.length; i++) {
/*  49 */       servers = MBeanServerFactory.findMBeanServer(agentIds[i]);
/*  50 */       if ((servers == null) || (servers.size() < 1)) {
/*  51 */         LOG.error("Could not register pool " + connectionPoolDefinition.getAlias() + " for JMX instrumentation" + " because lookup of MBeanServer using agent id " + agentIds[i] + " failed.");
/*     */       }
/*     */       else {
/*  54 */         MBeanServer mBeanServer = (MBeanServer)servers.get(0);
/*  55 */         ConnectionPoolMBean poolMBean = new ConnectionPoolMBean(alias, poolPropeties);
/*     */         try
/*     */         {
/*  58 */           mBeanServer.registerMBean(poolMBean, getObjectName(connectionPoolDefinition.getAlias()));
/*  59 */           LOG.info("Registered JMX MBean for pool " + connectionPoolDefinition.getAlias() + " in agent " + agentIds[i]);
/*     */         } catch (Exception e) {
/*  61 */           LOG.error("Registration of JMX MBean for pool " + connectionPoolDefinition.getAlias() + "in agent " + agentIds[i] + " failed.", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void unregisterPool(String alias, Properties poolPropeties)
/*     */   {
/*  74 */     String[] agentIds = getAgentIds(poolPropeties);
/*  75 */     ArrayList servers = null;
/*  76 */     for (int i = 0; i < agentIds.length; i++) {
/*  77 */       servers = MBeanServerFactory.findMBeanServer(agentIds[i]);
/*  78 */       if ((servers == null) || (servers.size() < 1)) {
/*  79 */         LOG.error("Could not unregister MBean for pool " + alias + " because lookup of MBeanServer using agent id " + agentIds[i] + " failed.");
/*     */       }
/*     */       else {
/*  82 */         MBeanServer mBeanServer = (MBeanServer)servers.get(0);
/*     */         try {
/*  84 */           mBeanServer.unregisterMBean(getObjectName(alias));
/*  85 */           LOG.info("Unregistered JMX MBean for pool " + alias + " in agent " + agentIds[i]);
/*     */         } catch (Exception e) {
/*  87 */           LOG.error("Unregistration of JMX MBean for pool " + alias + "in agent " + agentIds[i] + " failed.", e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ObjectName getObjectName(String alias)
/*     */     throws MalformedObjectNameException
/*     */   {
/* 101 */     return new ObjectName("Proxool:type=Pool, name=" + alias);
/*     */   }
/*     */ 
/*     */   private static String[] getAgentIds(Properties poolPropeties) {
/* 105 */     String idString = poolPropeties.getProperty("proxool.jmx-agent-id");
/* 106 */     if ((idString == null) || (idString.trim().equals("")))
/*     */     {
/* 108 */       return new String[] { null };
/*     */     }
/* 110 */     StringTokenizer tokenizer = new StringTokenizer(idString, ",");
/* 111 */     List tokens = new ArrayList(3);
/* 112 */     while (tokenizer.hasMoreElements()) {
/* 113 */       tokens.add(tokenizer.nextToken().trim());
/*     */     }
/* 115 */     return (String[])(String[])tokens.toArray(new String[tokens.size()]);
/*     */   }
/*     */ 
/*     */   public static String getValidIdentifier(String propertyName)
/*     */   {
/* 129 */     if (propertyName.indexOf("-") == -1) {
/* 130 */       return propertyName;
/*     */     }
/* 132 */     StringBuffer buffer = new StringBuffer(propertyName);
/* 133 */     int index = -1;
/* 134 */     while ((index = buffer.toString().indexOf("-")) > -1) {
/* 135 */       buffer.deleteCharAt(index);
/* 136 */       buffer.setCharAt(index, Character.toUpperCase(buffer.charAt(index)));
/*     */     }
/* 138 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.jmx.ProxoolJMXHelper
 * JD-Core Version:    0.6.0
 */