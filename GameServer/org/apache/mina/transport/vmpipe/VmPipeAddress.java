/*    */ package org.apache.mina.transport.vmpipe;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ 
/*    */ public class VmPipeAddress extends SocketAddress
/*    */   implements Comparable<VmPipeAddress>
/*    */ {
/*    */   private static final long serialVersionUID = 3257844376976830515L;
/*    */   private final int port;
/*    */ 
/*    */   public VmPipeAddress(int port)
/*    */   {
/* 39 */     this.port = port;
/*    */   }
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 46 */     return this.port;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 51 */     return this.port;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 56 */     if (o == null) {
/* 57 */       return false;
/*    */     }
/* 59 */     if (this == o) {
/* 60 */       return true;
/*    */     }
/* 62 */     if ((o instanceof VmPipeAddress)) {
/* 63 */       VmPipeAddress that = (VmPipeAddress)o;
/* 64 */       return this.port == that.port;
/*    */     }
/*    */ 
/* 67 */     return false;
/*    */   }
/*    */ 
/*    */   public int compareTo(VmPipeAddress o) {
/* 71 */     return this.port - o.port;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 76 */     if (this.port >= 0) {
/* 77 */       return "vm:server:" + this.port;
/*    */     }
/* 79 */     return "vm:client:" + -this.port;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.vmpipe.VmPipeAddress
 * JD-Core Version:    0.6.0
 */