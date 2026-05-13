/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Hierarchy;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.LogManager;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.RootLogger;
/*     */ 
/*     */ public class SocketServer
/*     */ {
/*  85 */   static String GENERIC = "generic";
/*  86 */   static String CONFIG_FILE_EXT = ".lcf";
/*     */ 
/*  88 */   static Logger cat = Logger.getLogger(SocketServer.class);
/*     */   static SocketServer server;
/*     */   static int port;
/*     */   Hashtable hierarchyMap;
/*     */   LoggerRepository genericHierarchy;
/*     */   File dir;
/*     */ 
/*     */   public static void main(String[] argv)
/*     */   {
/* 100 */     if (argv.length == 3)
/* 101 */       init(argv[0], argv[1], argv[2]);
/*     */     else
/* 103 */       usage("Wrong number of arguments.");
/*     */     try
/*     */     {
/* 106 */       cat.info("Listening on port " + port);
/* 107 */       ServerSocket serverSocket = new ServerSocket(port);
/*     */       while (true) {
/* 109 */         cat.info("Waiting to accept a new client.");
/* 110 */         Socket socket = serverSocket.accept();
/* 111 */         InetAddress inetAddress = socket.getInetAddress();
/* 112 */         cat.info("Connected to client at " + inetAddress);
/*     */ 
/* 114 */         LoggerRepository h = (LoggerRepository)server.hierarchyMap.get(inetAddress);
/* 115 */         if (h == null) {
/* 116 */           h = server.configureHierarchy(inetAddress);
/*     */         }
/*     */ 
/* 119 */         cat.info("Starting new socket node.");
/* 120 */         new Thread(new SocketNode(socket, h)).start();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 124 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   static void usage(String msg)
/*     */   {
/* 131 */     System.err.println(msg);
/* 132 */     System.err.println("Usage: java " + SocketServer.class.getName() + " port configFile directory");
/*     */ 
/* 134 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   static void init(String portStr, String configFile, String dirStr)
/*     */   {
/*     */     try {
/* 140 */       port = Integer.parseInt(portStr);
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 143 */       e.printStackTrace();
/* 144 */       usage("Could not interpret port number [" + portStr + "].");
/*     */     }
/*     */ 
/* 147 */     PropertyConfigurator.configure(configFile);
/*     */ 
/* 149 */     File dir = new File(dirStr);
/* 150 */     if (!dir.isDirectory()) {
/* 151 */       usage("[" + dirStr + "] is not a directory.");
/*     */     }
/* 153 */     server = new SocketServer(dir);
/*     */   }
/*     */ 
/*     */   public SocketServer(File directory)
/*     */   {
/* 159 */     this.dir = directory;
/* 160 */     this.hierarchyMap = new Hashtable(11);
/*     */   }
/*     */ 
/*     */   LoggerRepository configureHierarchy(InetAddress inetAddress)
/*     */   {
/* 166 */     cat.info("Locating configuration file for " + inetAddress);
/*     */ 
/* 169 */     String s = inetAddress.toString();
/* 170 */     int i = s.indexOf("/");
/* 171 */     if (i == -1) {
/* 172 */       cat.warn("Could not parse the inetAddress [" + inetAddress + "]. Using default hierarchy.");
/*     */ 
/* 174 */       return genericHierarchy();
/*     */     }
/* 176 */     String key = s.substring(0, i);
/*     */ 
/* 178 */     File configFile = new File(this.dir, key + CONFIG_FILE_EXT);
/* 179 */     if (configFile.exists()) {
/* 180 */       Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
/* 181 */       this.hierarchyMap.put(inetAddress, h);
/*     */ 
/* 183 */       new PropertyConfigurator().doConfigure(configFile.getAbsolutePath(), h);
/*     */ 
/* 185 */       return h;
/*     */     }
/* 187 */     cat.warn("Could not find config file [" + configFile + "].");
/* 188 */     return genericHierarchy();
/*     */   }
/*     */ 
/*     */   LoggerRepository genericHierarchy()
/*     */   {
/* 194 */     if (this.genericHierarchy == null) {
/* 195 */       File f = new File(this.dir, GENERIC + CONFIG_FILE_EXT);
/* 196 */       if (f.exists()) {
/* 197 */         this.genericHierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
/* 198 */         new PropertyConfigurator().doConfigure(f.getAbsolutePath(), this.genericHierarchy);
/*     */       } else {
/* 200 */         cat.warn("Could not find config file [" + f + "]. Will use the default hierarchy.");
/*     */ 
/* 202 */         this.genericHierarchy = LogManager.getLoggerRepository();
/*     */       }
/*     */     }
/* 205 */     return this.genericHierarchy;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.SocketServer
 * JD-Core Version:    0.6.0
 */