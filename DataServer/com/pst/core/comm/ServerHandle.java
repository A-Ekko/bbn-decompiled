/*    */ package com.pst.core.comm;
/*    */ 
/*    */ import com.pst.config.SystemConfig;
/*    */ import com.pst.core.data.DataQueue;
/*    */ import com.pst.core.protocol.Information;
/*    */ import com.pst.core.protocol.Protocol;
/*    */ import com.pst.core.protocol.ProtocolF002;
/*    */ import com.pst.core.util.IpUtil;
/*    */ import java.util.List;
/*    */ import java.util.Vector;
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.mina.core.service.IoHandlerAdapter;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class ServerHandle extends IoHandlerAdapter
/*    */ {
/* 17 */   private Logger logger = Logger.getLogger(ServerHandle.class);
/* 18 */   private ProtocolF002 protocol = new ProtocolF002();
/* 19 */   private IpUtil ipUtil = new IpUtil();
/*    */ 
/*    */   public ServerHandle() {
/* 22 */     this.logger.setLevel(Level.DEBUG);
/*    */   }
/*    */ 
/*    */   public void sessionCreated(IoSession session)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void sessionOpened(IoSession session)
/*    */   {
/* 31 */     String ip = this.ipUtil.addressToString(session.getRemoteAddress());
/* 32 */     if (!SystemConfig.concentipList.contains(ip)) {
/* 33 */       this.logger.info("非法IP链接" + ip);
/* 34 */       session.close(true);
/*    */     } else {
/* 36 */       this.logger.info("监听到有IP[" + ip + "]链接");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void sessionClosed(IoSession session)
/*    */   {
/* 42 */     this.logger.info("角色Session[" + session.getId() + "]下线");
/*    */   }
/*    */ 
/*    */   public void sessionIdle(IoSession session)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void messageReceived(IoSession session, Object data) {
/* 50 */     Information info = this.protocol.parseCmdBytes((byte[])data);
/* 51 */     if (("3000".equals(info.getType())) && (info.getRoleId() == SystemConfig.account) && (info.getPt() == SystemConfig.powertoken)) {
/* 52 */       String sql = null;
/*    */       try {
/* 54 */         sql = new String(info.getData(), Protocol.PROTOCAL_ENCODEDEFAULT);
/* 55 */         synchronized (DataQueue.sqlQueue) {
/* 56 */           DataQueue.sqlQueue.add(sql);
/*    */         }
/* 58 */         this.logger.info("收到数据:" + sql); } catch (Exception e) {
/* 59 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void messageSent(IoSession session, Object data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void exceptionCaught(IoSession session, Throwable throwables)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.core.comm.ServerHandle
 * JD-Core Version:    0.6.0
 */