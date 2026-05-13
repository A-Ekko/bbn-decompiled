/*     */ package org.apache.mina.filter.firewall;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class BlacklistFilter extends IoFilterAdapter
/*     */ {
/*  45 */   private final List<Subnet> blacklist = new CopyOnWriteArrayList();
/*     */ 
/*  47 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */ 
/*     */   public void setBlacklist(InetAddress[] addresses)
/*     */   {
/*  56 */     if (addresses == null) {
/*  57 */       throw new NullPointerException("addresses");
/*     */     }
/*  59 */     this.blacklist.clear();
/*  60 */     for (int i = 0; i < addresses.length; i++) {
/*  61 */       InetAddress addr = addresses[i];
/*  62 */       block(addr);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSubnetBlacklist(Subnet[] subnets)
/*     */   {
/*  74 */     if (subnets == null) {
/*  75 */       throw new NullPointerException("Subnets must not be null");
/*     */     }
/*  77 */     this.blacklist.clear();
/*  78 */     for (Subnet subnet : subnets)
/*  79 */       block(subnet);
/*     */   }
/*     */ 
/*     */   public void setBlacklist(Iterable<InetAddress> addresses)
/*     */   {
/*  94 */     if (addresses == null) {
/*  95 */       throw new NullPointerException("addresses");
/*     */     }
/*     */ 
/*  98 */     this.blacklist.clear();
/*     */ 
/* 100 */     for (InetAddress address : addresses)
/* 101 */       block(address);
/*     */   }
/*     */ 
/*     */   public void setSubnetBlacklist(Iterable<Subnet> subnets)
/*     */   {
/* 113 */     if (subnets == null) {
/* 114 */       throw new NullPointerException("Subnets must not be null");
/*     */     }
/* 116 */     this.blacklist.clear();
/* 117 */     for (Subnet subnet : subnets)
/* 118 */       block(subnet);
/*     */   }
/*     */ 
/*     */   public void block(InetAddress address)
/*     */   {
/* 126 */     if (address == null) {
/* 127 */       throw new NullPointerException("Adress to block can not be null");
/*     */     }
/*     */ 
/* 130 */     block(new Subnet(address, 32));
/*     */   }
/*     */ 
/*     */   public void block(Subnet subnet)
/*     */   {
/* 137 */     if (subnet == null) {
/* 138 */       throw new NullPointerException("Subnet can not be null");
/*     */     }
/*     */ 
/* 141 */     this.blacklist.add(subnet);
/*     */   }
/*     */ 
/*     */   public void unblock(InetAddress address)
/*     */   {
/* 148 */     if (address == null) {
/* 149 */       throw new NullPointerException("Adress to unblock can not be null");
/*     */     }
/*     */ 
/* 152 */     unblock(new Subnet(address, 32));
/*     */   }
/*     */ 
/*     */   public void unblock(Subnet subnet)
/*     */   {
/* 159 */     if (subnet == null) {
/* 160 */       throw new NullPointerException("Subnet can not be null");
/*     */     }
/* 162 */     this.blacklist.remove(subnet);
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */   {
/* 167 */     if (!isBlocked(session))
/*     */     {
/* 169 */       nextFilter.sessionCreated(session);
/*     */     }
/* 171 */     else blockSession(session);
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 178 */     if (!isBlocked(session))
/*     */     {
/* 180 */       nextFilter.sessionOpened(session);
/*     */     }
/* 182 */     else blockSession(session);
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 189 */     if (!isBlocked(session))
/*     */     {
/* 191 */       nextFilter.sessionClosed(session);
/*     */     }
/* 193 */     else blockSession(session);
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */     throws Exception
/*     */   {
/* 200 */     if (!isBlocked(session))
/*     */     {
/* 202 */       nextFilter.sessionIdle(session, status);
/*     */     }
/* 204 */     else blockSession(session);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */   {
/* 211 */     if (!isBlocked(session))
/*     */     {
/* 213 */       nextFilter.messageReceived(session, message);
/*     */     }
/* 215 */     else blockSession(session);
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 222 */     if (!isBlocked(session))
/*     */     {
/* 224 */       nextFilter.messageSent(session, writeRequest);
/*     */     }
/* 226 */     else blockSession(session);
/*     */   }
/*     */ 
/*     */   private void blockSession(IoSession session)
/*     */   {
/* 231 */     this.logger.warn("Remote address in the blacklist; closing.");
/* 232 */     session.close(true);
/*     */   }
/*     */ 
/*     */   private boolean isBlocked(IoSession session) {
/* 236 */     SocketAddress remoteAddress = session.getRemoteAddress();
/*     */     InetAddress address;
/* 237 */     if ((remoteAddress instanceof InetSocketAddress)) {
/* 238 */       address = ((InetSocketAddress)remoteAddress).getAddress();
/*     */ 
/* 241 */       for (Subnet subnet : this.blacklist) {
/* 242 */         if (subnet.inSubnet(address)) {
/* 243 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 248 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.firewall.BlacklistFilter
 * JD-Core Version:    0.6.0
 */