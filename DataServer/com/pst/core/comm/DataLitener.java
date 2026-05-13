/*    */ package com.pst.core.comm;
/*    */ 
/*    */ import com.pst.config.SystemConfig;
/*    */ import com.pst.core.protocol.F002ProtocolCodecFactory;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Executors;
/*    */ import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
/*    */ import org.apache.mina.filter.codec.ProtocolCodecFilter;
/*    */ import org.apache.mina.filter.executor.ExecutorFilter;
/*    */ import org.apache.mina.transport.socket.SocketAcceptor;
/*    */ import org.apache.mina.transport.socket.SocketSessionConfig;
/*    */ import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
/*    */ 
/*    */ public class DataLitener
/*    */ {
/*    */   public void litener()
/*    */   {
/* 18 */     SocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
/*    */ 
/* 21 */     acceptor.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(new F002ProtocolCodecFactory()));
/*    */ 
/* 23 */     ExecutorService exethreadPool = Executors.newCachedThreadPool();
/* 24 */     acceptor.getFilterChain().addLast("executor", new ExecutorFilter(exethreadPool));
/*    */ 
/* 27 */     acceptor.setDefaultLocalAddress(new InetSocketAddress(SystemConfig.port));
/* 28 */     acceptor.getSessionConfig().setReceiveBufferSize(2048);
/* 29 */     acceptor.getSessionConfig().setReadBufferSize(2048);
/* 30 */     acceptor.getSessionConfig().setWriteTimeout(10);
/*    */ 
/* 32 */     acceptor.getSessionConfig().setReuseAddress(true);
/*    */ 
/* 35 */     acceptor.setHandler(new ServerHandle());
/*    */     try
/*    */     {
/* 38 */       acceptor.bind();
/* 39 */       SystemConfig.acceptor = acceptor;
/*    */     } catch (Exception e) {
/* 41 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.core.comm.DataLitener
 * JD-Core Version:    0.6.0
 */