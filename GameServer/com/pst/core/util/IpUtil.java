/*    */ package com.pst.core.util;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.net.SocketAddress;
/*    */ 
/*    */ public class IpUtil
/*    */ {
/*    */   public long addressToLong(SocketAddress address)
/*    */   {
/*  8 */     String addressString = address.toString().replace("/", "").replace("\\", "");
/*  9 */     int index = addressString.indexOf(':');
/* 10 */     if (index > 0) {
/* 11 */       addressString = addressString.substring(0, index);
/*    */     }
/* 13 */     return ipToLong(addressString);
/*    */   }
/*    */ 
/*    */   public long ipToLong(String strIp)
/*    */   {
/* 18 */     long[] ip = new long[4];
/*    */ 
/* 20 */     int position1 = strIp.indexOf(".");
/* 21 */     int position2 = strIp.indexOf(".", position1 + 1);
/* 22 */     int position3 = strIp.indexOf(".", position2 + 1);
/*    */ 
/* 24 */     ip[0] = Long.parseLong(strIp.substring(0, position1));
/* 25 */     ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
/* 26 */     ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
/* 27 */     ip[3] = Long.parseLong(strIp.substring(position3 + 1));
/* 28 */     return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
/*    */   }
/*    */ 
/*    */   public String longToIP(long longIp)
/*    */   {
/* 33 */     StringBuffer sb = new StringBuffer("");
/*    */ 
/* 35 */     sb.append(String.valueOf(longIp >>> 24));
/* 36 */     sb.append(".");
/*    */ 
/* 38 */     sb.append(String.valueOf((longIp & 0xFFFFFF) >>> 16));
/* 39 */     sb.append(".");
/*    */ 
/* 41 */     sb.append(String.valueOf((longIp & 0xFFFF) >>> 8));
/* 42 */     sb.append(".");
/*    */ 
/* 44 */     sb.append(String.valueOf(longIp & 0xFF));
/* 45 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 49 */     System.out.print(new IpUtil().ipToLong("59.34.197.174"));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.util.IpUtil
 * JD-Core Version:    0.6.0
 */