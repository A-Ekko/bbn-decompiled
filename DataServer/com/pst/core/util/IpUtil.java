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
/*    */   public String addressToString(SocketAddress address) {
/* 17 */     String addressString = address.toString().replace("/", "").replace("\\", "");
/* 18 */     int index = addressString.indexOf(':');
/* 19 */     if (index > 0) {
/* 20 */       addressString = addressString.substring(0, index);
/*    */     }
/* 22 */     return addressString;
/*    */   }
/*    */ 
/*    */   public long ipToLong(String strIp)
/*    */   {
/* 27 */     long[] ip = new long[4];
/*    */ 
/* 29 */     int position1 = strIp.indexOf(".");
/* 30 */     int position2 = strIp.indexOf(".", position1 + 1);
/* 31 */     int position3 = strIp.indexOf(".", position2 + 1);
/*    */ 
/* 33 */     ip[0] = Long.parseLong(strIp.substring(0, position1));
/* 34 */     ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
/* 35 */     ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
/* 36 */     ip[3] = Long.parseLong(strIp.substring(position3 + 1));
/* 37 */     return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
/*    */   }
/*    */ 
/*    */   public String longToIP(long longIp)
/*    */   {
/* 42 */     StringBuffer sb = new StringBuffer("");
/*    */ 
/* 44 */     sb.append(String.valueOf(longIp >>> 24));
/* 45 */     sb.append(".");
/*    */ 
/* 47 */     sb.append(String.valueOf((longIp & 0xFFFFFF) >>> 16));
/* 48 */     sb.append(".");
/*    */ 
/* 50 */     sb.append(String.valueOf((longIp & 0xFFFF) >>> 8));
/* 51 */     sb.append(".");
/*    */ 
/* 53 */     sb.append(String.valueOf(longIp & 0xFF));
/* 54 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 58 */     System.out.print(new IpUtil().longToIP(977115606L));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.core.util.IpUtil
 * JD-Core Version:    0.6.0
 */