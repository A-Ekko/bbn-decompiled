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
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.RootCategory;
/*     */ 
/*     */ public class SocketServer
/*     */ {
/*  75 */   static String GENERIC = "generic";
/*  76 */   static String CONFIG_FILE_EXT = ".lcf";
/*     */ 
/*  78 */   static Category cat = Category.getInstance(SocketServer.class);
/*     */   static SocketServer server;
/*     */   static int port;
/*     */   Hashtable hierarchyMap;
/*     */   LoggerRepository genericHierarchy;
/*     */   File dir;
/*     */ 
/*     */   public static void main(String[] argv)
/*     */   {
/*  90 */     if (argv.length == 3)
/*  91 */       init(argv[0], argv[1], argv[2]);
/*     */     else
/*  93 */       usage("Wrong number of arguments.");
/*     */     try
/*     */     {
/*  96 */       cat.info("Listening on port " + port);
/*  97 */       ServerSocket serverSocket = new ServerSocket(port);
/*     */       while (true) {
/*  99 */         cat.info("Waiting to accept a new client.");
/* 100 */         Socket socket = serverSocket.accept();
/* 101 */         InetAddress inetAddress = socket.getInetAddress();
/* 102 */         cat.info("Connected to client at " + inetAddress);
/*     */ 
/* 104 */         LoggerRepository h = (LoggerRepository)server.hierarchyMap.get(inetAddress);
/* 105 */         if (h == null) {
/* 106 */           h = server.configureHierarchy(inetAddress);
/*     */         }
/*     */ 
/* 109 */         cat.info("Starting new socket node.");
/* 110 */         new Thread(new SocketNode(socket, h)).start();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 114 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   static void usage(String msg)
/*     */   {
/* 121 */     System.err.println(msg);
/* 122 */     System.err.println("Usage: java " + SocketServer.class.getName() + " port configFile directory");
/*     */ 
/* 124 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   static void init(String portStr, String configFile, String dirStr)
/*     */   {
/*     */     try {
/* 130 */       port = Integer.parseInt(portStr);
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 133 */       e.printStackTrace();
/* 134 */       usage("Could not interpret port number [" + portStr + "].");
/*     */     }
/*     */ 
/* 137 */     PropertyConfigurator.configure(configFile);
/*     */ 
/* 139 */     File dir = new File(dirStr);
/* 140 */     if (!dir.isDirectory()) {
/* 141 */       usage("[" + dirStr + "] is not a directory.");
/*     */     }
/* 143 */     server = new SocketServer(dir);
/*     */   }
/*     */ 
/*     */   public SocketServer(File directory)
/*     */   {
/* 149 */     this.dir = directory;
/* 150 */     this.hierarchyMap = new Hashtable(11);
/*     */   }
/*     */ 
/*     */   LoggerRepository configureHierarchy(InetAddress inetAddress)
/*     */   {
/* 156 */     cat.info("Locating configuration file for " + inetAddress);
/*     */ 
/* 159 */     String s = inetAddress.toString();
/* 160 */     int i = s.indexOf("/");
/* 161 */     if (i == -1) {
/* 162 */       cat.warn("Could not parse the inetAddress [" + inetAddress + "]. Using default hierarchy.");
/*     */ 
/* 164 */       return genericHierarchy();
/*     */     }
/* 166 */     String key = s.substring(0, i);
/*     */ 
/* 168 */     File configFile = new File(this.dir, key + CONFIG_FILE_EXT);
/* 169 */     if (configFile.exists()) {
/* 170 */       Hierarchy h = new Hierarchy(new RootCategory((Level)Priority.DEBUG));
/* 171 */       this.hierarchyMap.put(inetAddress, h);
/*     */ 
/* 173 */       new PropertyConfigurator().doConfigure(configFile.getAbsolutePath(), h);
/*     */ 
/* 175 */       return h;
/*     */     }
/* 177 */     cat.warn("Could not find config file [" + configFile + "].");
/* 178 */     return genericHierarchy();
/*     */   }
/*     */ 
/*     */   LoggerRepository genericHierarchy()
/*     */   {
/* 184 */     if (this.genericHierarchy == null) {
/* 185 */       File f = new File(this.dir, GENERIC + CONFIG_FILE_EXT);
/* 186 */       if (f.exists()) {
/* 187 */         this.genericHierarchy = new Hierarchy(new RootCategory((Level)Priority.DEBUG));
/* 188 */         new PropertyConfigurator().doConfigure(f.getAbsolutePath(), this.genericHierarchy);
/*     */       } else {
/* 190 */         cat.warn("Could not find config file [" + f + "]. Will use the default hierarchy.");
/*     */ 
/* 192 */         this.genericHierarchy = LogManager.getLoggerRepository();
/*     */       }
/*     */     }
/* 195 */     return this.genericHierarchy;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.SocketServer
 * JD-Core Version:    0.6.0
 */