/*     */ package org.apache.mina.filter.firewall;
/*     */ 
/*     */ import java.net.Inet4Address;
/*     */ import java.net.InetAddress;
/*     */ 
/*     */ public class Subnet
/*     */ {
/*     */   private static final int IP_MASK = -2147483648;
/*     */   private static final int BYTE_MASK = 255;
/*     */   private InetAddress subnet;
/*     */   private int subnetInt;
/*     */   private int subnetMask;
/*     */   private int suffix;
/*     */ 
/*     */   public Subnet(InetAddress subnet, int mask)
/*     */   {
/*  51 */     if (subnet == null) {
/*  52 */       throw new NullPointerException("Subnet address can not be null");
/*     */     }
/*  54 */     if (!(subnet instanceof Inet4Address)) {
/*  55 */       throw new IllegalArgumentException("Only IPv4 supported");
/*     */     }
/*     */ 
/*  58 */     if ((mask < 0) || (mask > 32)) {
/*  59 */       throw new IllegalArgumentException("Mask has to be an integer between 0 and 32");
/*     */     }
/*     */ 
/*  62 */     this.subnet = subnet;
/*  63 */     this.subnetInt = toInt(subnet);
/*  64 */     this.suffix = mask;
/*     */ 
/*  67 */     this.subnetMask = (-2147483648 >> mask - 1);
/*     */   }
/*     */ 
/*     */   private int toInt(InetAddress inetAddress)
/*     */   {
/*  74 */     byte[] address = inetAddress.getAddress();
/*  75 */     int result = 0;
/*  76 */     for (int i = 0; i < address.length; i++) {
/*  77 */       result <<= 8;
/*  78 */       result |= address[i] & 0xFF;
/*     */     }
/*  80 */     return result;
/*     */   }
/*     */ 
/*     */   private int toSubnet(InetAddress address)
/*     */   {
/*  90 */     return toInt(address) & this.subnetMask;
/*     */   }
/*     */ 
/*     */   public boolean inSubnet(InetAddress address)
/*     */   {
/*  99 */     return toSubnet(address) == this.subnetInt;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 107 */     return this.subnet.getHostAddress() + "/" + this.suffix;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 112 */     if (!(obj instanceof Subnet)) {
/* 113 */       return false;
/*     */     }
/*     */ 
/* 116 */     Subnet other = (Subnet)obj;
/*     */ 
/* 118 */     return (other.subnetInt == this.subnetInt) && (other.suffix == this.suffix);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.firewall.Subnet
 * JD-Core Version:    0.6.0
 */