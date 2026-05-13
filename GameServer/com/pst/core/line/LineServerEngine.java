/*    */ package com.pst.core.line;
/*    */ 
/*    */ import com.pst.core.config.SystemConfig;
/*    */ import com.pst.core.protocol.F002ProtocolCodecFactory;
/*    */ import com.pst.core.shutdown.ShutdownHook;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.util.Properties;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Executors;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.PropertyConfigurator;
/*    */ import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
/*    */ import org.apache.mina.filter.codec.ProtocolCodecFilter;
/*    */ import org.apache.mina.filter.executor.ExecutorFilter;
/*    */ import org.apache.mina.transport.socket.SocketAcceptor;
/*    */ import org.apache.mina.transport.socket.SocketSessionConfig;
/*    */ import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
/*    */ 
/*    */ public class LineServerEngine
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 22 */     if ((args != null) && (args.length > 0) && ("stop".equalsIgnoreCase(args[0].trim())))
/*    */     {
/* 24 */       SystemConfig.systemStop();
/* 25 */       return;
/*    */     }
/*    */ 
/* 28 */     PropertyConfigurator.configure(System.getProperties().getProperty("user.dir") + "/resource/log4j.properties");
/* 29 */     Logger logger = Logger.getLogger(LineServerEngine.class);
/* 30 */     boolean flag = new InitLine().init();
/* 31 */     if (flag)
/*    */     {
/* 35 */       SocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
/*    */ 
/* 38 */       acceptor.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(new F002ProtocolCodecFactory()));
/*    */ 
/* 40 */       ExecutorService exethreadPool = Executors.newCachedThreadPool();
/* 41 */       acceptor.getFilterChain().addLast("executor", new ExecutorFilter(exethreadPool));
/*    */ 
/* 44 */       acceptor.setDefaultLocalAddress(new InetSocketAddress(SystemConfig.port));
/* 45 */       acceptor.getSessionConfig().setReceiveBufferSize(2048);
/* 46 */       acceptor.getSessionConfig().setReadBufferSize(2048);
/* 47 */       acceptor.getSessionConfig().setWriteTimeout(10);
/*    */ 
/* 49 */       acceptor.getSessionConfig().setReuseAddress(true);
/*    */ 
/* 52 */       acceptor.setHandler(new ServerHandle());
/* 53 */       Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));
/*    */       try
/*    */       {
/* 56 */         acceptor.bind();
/* 57 */         SystemConfig.acceptor = acceptor;
/* 58 */         logger.info("游戏线服务启动>>");
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 62 */         acceptor.unbind();
/* 63 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.line.LineServerEngine
 * JD-Core Version:    0.6.0
 */