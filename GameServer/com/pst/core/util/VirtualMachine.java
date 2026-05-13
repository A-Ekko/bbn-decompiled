/*    */ package com.pst.core.util;
/*    */ 
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.lang.management.RuntimeMXBean;
/*    */ 
/*    */ public class VirtualMachine
/*    */ {
/*    */   public static int getPid()
/*    */   {
/*  8 */     RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
/*  9 */     String name = runtime.getName();
/*    */     try {
/* 11 */       return Integer.parseInt(name.substring(0, name.indexOf('@'))); } catch (Exception e) {
/*    */     }
/* 13 */     return -1;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.util.VirtualMachine
 * JD-Core Version:    0.6.0
 */