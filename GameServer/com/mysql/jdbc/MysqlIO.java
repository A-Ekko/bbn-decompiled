/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.util.ReadAheadInputStream;
/*      */ import com.mysql.jdbc.util.ResultSetUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.math.BigInteger;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.SocketChannel;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.Deflater;
/*      */ 
/*      */ class MysqlIO
/*      */ {
/*      */   protected static final int NULL_LENGTH = -1;
/*      */   protected static final int COMP_HEADER_LENGTH = 3;
/*      */   protected static final int MIN_COMPRESS_LEN = 50;
/*      */   protected static final int HEADER_LENGTH = 4;
/*   79 */   private static int maxBufferSize = 65535;
/*      */   private static final int CLIENT_COMPRESS = 32;
/*      */   protected static final int CLIENT_CONNECT_WITH_DB = 8;
/*      */   private static final int CLIENT_FOUND_ROWS = 2;
/*      */   private static final int CLIENT_LOCAL_FILES = 128;
/*      */   private static final int CLIENT_LONG_FLAG = 4;
/*      */   private static final int CLIENT_LONG_PASSWORD = 1;
/*      */   private static final int CLIENT_PROTOCOL_41 = 512;
/*      */   private static final int CLIENT_INTERACTIVE = 1024;
/*      */   protected static final int CLIENT_SSL = 2048;
/*      */   private static final int CLIENT_TRANSACTIONS = 8192;
/*      */   protected static final int CLIENT_RESERVED = 16384;
/*      */   protected static final int CLIENT_SECURE_CONNECTION = 32768;
/*      */   private static final int CLIENT_MULTI_QUERIES = 65536;
/*      */   private static final int CLIENT_MULTI_RESULTS = 131072;
/*      */   private static final int SERVER_STATUS_IN_TRANS = 1;
/*      */   private static final int SERVER_STATUS_AUTOCOMMIT = 2;
/*      */   private static final int SERVER_MORE_RESULTS_EXISTS = 8;
/*      */   private static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
/*      */   private static final int SERVER_QUERY_NO_INDEX_USED = 32;
/*      */   private static final String FALSE_SCRAMBLE = "xxxxxxxx";
/*      */   protected static final int MAX_QUERY_SIZE_TO_LOG = 1024;
/*      */   protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
/*  113 */   private static String jvmPlatformCharset = null;
/*      */ 
/*  118 */   private boolean binaryResultsAreUnpacked = true;
/*      */   protected static final String ZERO_DATE_VALUE_MARKER = "0000-00-00";
/*      */   protected static final String ZERO_DATETIME_VALUE_MARKER = "0000-00-00 00:00:00";
/*      */   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
/*  151 */   private boolean packetSequenceReset = false;
/*      */   protected int serverCharsetIndex;
/*  159 */   private Buffer reusablePacket = null;
/*  160 */   private Buffer sendPacket = null;
/*  161 */   private Buffer sharedSendPacket = null;
/*      */ 
/*  164 */   protected BufferedOutputStream mysqlOutput = null;
/*      */   protected Connection connection;
/*  166 */   private Deflater deflater = null;
/*  167 */   protected InputStream mysqlInput = null;
/*  168 */   private LinkedList packetDebugRingBuffer = null;
/*  169 */   private RowData streamingData = null;
/*      */ 
/*  172 */   protected Socket mysqlConnection = null;
/*      */   private SocketChannel socketChannel;
/*  174 */   private SocketFactory socketFactory = null;
/*      */   private SoftReference loadFileBufRef;
/*      */   private SoftReference splitBufRef;
/*  190 */   protected String host = null;
/*      */   protected String seed;
/*  192 */   private String serverVersion = null;
/*  193 */   private String socketFactoryClassName = null;
/*  194 */   private byte[] packetHeaderBuf = new byte[4];
/*  195 */   private boolean colDecimalNeedsBump = false;
/*  196 */   private boolean hadWarnings = false;
/*  197 */   private boolean has41NewNewProt = false;
/*      */ 
/*  200 */   private boolean hasLongColumnInfo = false;
/*  201 */   private boolean isInteractiveClient = false;
/*  202 */   private boolean logSlowQueries = false;
/*      */ 
/*  208 */   private boolean platformDbCharsetMatches = true;
/*  209 */   private boolean profileSql = false;
/*  210 */   private boolean queryBadIndexUsed = false;
/*  211 */   private boolean queryNoIndexUsed = false;
/*      */ 
/*  214 */   private boolean use41Extensions = false;
/*  215 */   private boolean useCompression = false;
/*  216 */   protected boolean useNewIo = false;
/*  217 */   private boolean useNewLargePackets = false;
/*  218 */   private boolean useNewUpdateCounts = false;
/*  219 */   private byte packetSequence = 0;
/*  220 */   private byte readPacketSequence = -1;
/*  221 */   private boolean checkPacketSequence = false;
/*  222 */   byte protocolVersion = 0;
/*  223 */   private int maxAllowedPacket = 1048576;
/*  224 */   protected int maxThreeBytes = 16581375;
/*  225 */   protected int port = 3306;
/*      */   protected int serverCapabilities;
/*  227 */   private int serverMajorVersion = 0;
/*  228 */   private int serverMinorVersion = 0;
/*  229 */   private int serverStatus = 0;
/*  230 */   private int serverSubMinorVersion = 0;
/*  231 */   private int warningCount = 0;
/*  232 */   protected long clientParam = 0L;
/*  233 */   protected long lastPacketSentTimeMs = 0L;
/*  234 */   private boolean traceProtocol = false;
/*  235 */   private boolean enablePacketDebug = false;
/*      */   private ByteBuffer channelClearBuf;
/*      */   private Calendar sessionCalendar;
/*      */   private boolean useConnectWithDb;
/*      */   private boolean needToGrabQueryFromPacket;
/*      */   private boolean autoGenerateTestcaseScript;
/*      */ 
/*      */   public MysqlIO(String host, int port, Properties props, String socketFactoryClassName, Connection conn, int socketTimeout)
/*      */     throws IOException, SQLException
/*      */   {
/*  259 */     this.connection = conn;
/*      */ 
/*  261 */     if (this.connection.getEnablePacketDebug()) {
/*  262 */       this.packetDebugRingBuffer = new LinkedList();
/*      */     }
/*      */ 
/*  265 */     this.logSlowQueries = this.connection.getLogSlowQueries();
/*      */ 
/*  267 */     this.useNewIo = this.connection.getUseNewIo();
/*      */ 
/*  269 */     if (this.useNewIo) {
/*  270 */       this.reusablePacket = Buffer.allocateDirect(this.connection.getNetBufferLength(), true);
/*      */     }
/*      */     else {
/*  273 */       this.reusablePacket = Buffer.allocateNew(this.connection.getNetBufferLength(), false);
/*      */     }
/*      */ 
/*  277 */     this.port = port;
/*  278 */     this.host = host;
/*      */ 
/*  280 */     if (!this.useNewIo) {
/*  281 */       this.socketFactoryClassName = socketFactoryClassName;
/*  282 */       this.socketFactory = createSocketFactory();
/*      */ 
/*  284 */       this.mysqlConnection = this.socketFactory.connect(this.host, this.port, props);
/*      */ 
/*  287 */       if (socketTimeout != 0) {
/*      */         try {
/*  289 */           this.mysqlConnection.setSoTimeout(socketTimeout);
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/*      */       }
/*      */ 
/*  296 */       this.mysqlConnection = this.socketFactory.beforeHandshake();
/*      */ 
/*  298 */       if (this.connection.getUseReadAheadInput()) {
/*  299 */         this.mysqlInput = new ReadAheadInputStream(this.mysqlConnection.getInputStream(), 16384, this.connection.getTraceProtocol(), this.connection.getLog());
/*      */       }
/*  302 */       else if (this.connection.useUnbufferedInput())
/*  303 */         this.mysqlInput = this.mysqlConnection.getInputStream();
/*      */       else {
/*  305 */         this.mysqlInput = new BufferedInputStream(this.mysqlConnection.getInputStream(), 16384);
/*      */       }
/*      */ 
/*  309 */       this.mysqlOutput = new BufferedOutputStream(this.mysqlConnection.getOutputStream(), 16384);
/*      */     }
/*      */     else {
/*  312 */       this.socketChannel = SocketChannel.open();
/*  313 */       this.socketChannel.configureBlocking(true);
/*  314 */       this.socketChannel.connect(new InetSocketAddress(host, port));
/*  315 */       this.channelClearBuf = ByteBuffer.allocate(4096);
/*      */ 
/*  317 */       this.mysqlInput = this.socketChannel.socket().getInputStream();
/*      */     }
/*      */ 
/*  320 */     this.isInteractiveClient = this.connection.getInteractiveClient();
/*  321 */     this.profileSql = this.connection.getProfileSql();
/*  322 */     this.sessionCalendar = Calendar.getInstance();
/*  323 */     this.autoGenerateTestcaseScript = this.connection.getAutoGenerateTestcaseScript();
/*      */ 
/*  325 */     this.needToGrabQueryFromPacket = ((this.profileSql) || (this.logSlowQueries) || (this.autoGenerateTestcaseScript));
/*      */   }
/*      */ 
/*      */   public boolean hasLongColumnInfo()
/*      */   {
/*  336 */     return this.hasLongColumnInfo;
/*      */   }
/*      */ 
/*      */   protected boolean isDataAvailable() throws SQLException {
/*      */     try {
/*  341 */       if (!this.useNewIo) {
/*  342 */         return this.mysqlInput.available() > 0;
/*      */       }
/*      */ 
/*  345 */       return false; } catch (IOException ioEx) {
/*      */     }
/*  347 */     throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */   }
/*      */ 
/*      */   protected long getLastPacketSentTimeMs()
/*      */   {
/*  358 */     return this.lastPacketSentTimeMs;
/*      */   }
/*      */ 
/*      */   protected ResultSet getResultSet(Statement callingStatement, long columnCount, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean isBinaryEncoded, boolean unpackFieldInfo)
/*      */     throws SQLException
/*      */   {
/*  388 */     Field[] fields = null;
/*      */ 
/*  390 */     if (unpackFieldInfo) {
/*  391 */       fields = new Field[(int)columnCount];
/*      */     }
/*      */ 
/*  395 */     for (int i = 0; i < columnCount; i++) {
/*  396 */       Buffer fieldPacket = null;
/*      */ 
/*  398 */       if (this.useNewIo)
/*      */       {
/*  404 */         Buffer packet = reuseAndReadPacket(this.reusablePacket);
/*      */ 
/*  406 */         if (unpackFieldInfo)
/*  407 */           fieldPacket = new ByteArrayBuffer(packet.getByteBuffer());
/*      */       }
/*      */       else {
/*  410 */         fieldPacket = readPacket();
/*      */       }
/*      */ 
/*  413 */       if (unpackFieldInfo) {
/*  414 */         fields[i] = unpackField(fieldPacket, false);
/*      */       }
/*      */     }
/*      */ 
/*  418 */     Buffer packet = reuseAndReadPacket(this.reusablePacket);
/*      */ 
/*  420 */     RowData rowData = null;
/*      */ 
/*  422 */     if (!streamResults) {
/*  423 */       rowData = readSingleRowSet(columnCount, maxRows, resultSetConcurrency, isBinaryEncoded, fields);
/*      */     }
/*      */     else {
/*  426 */       rowData = new RowDataDynamic(this, (int)columnCount, fields, isBinaryEncoded);
/*      */ 
/*  428 */       this.streamingData = rowData;
/*      */     }
/*      */ 
/*  431 */     ResultSet rs = buildResultSetWithRows(callingStatement, catalog, fields, rowData, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */ 
/*  434 */     return rs;
/*      */   }
/*      */ 
/*      */   protected final void forceClose()
/*      */   {
/*      */     try
/*      */     {
/*  442 */       if (this.mysqlInput != null) {
/*  443 */         this.mysqlInput.close();
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*  448 */       this.mysqlInput = null;
/*      */     }
/*      */     try
/*      */     {
/*  452 */       if (this.mysqlOutput != null) {
/*  453 */         this.mysqlOutput.close();
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*  458 */       this.mysqlOutput = null;
/*      */     }
/*      */     try
/*      */     {
/*  462 */       if (this.mysqlConnection != null) {
/*  463 */         this.mysqlConnection.close();
/*      */       }
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*  468 */       this.mysqlConnection = null; }  } 
/*      */   // ERROR //
/*      */   protected final Buffer readPacket() throws SQLException { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 30	com/mysql/jdbc/MysqlIO:useNewIo	Z
/*      */     //   4: ifne +362 -> 366
/*      */     //   7: aload_0
/*      */     //   8: aload_0
/*      */     //   9: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   12: aload_0
/*      */     //   13: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   16: iconst_0
/*      */     //   17: iconst_4
/*      */     //   18: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   21: istore_1
/*      */     //   22: iload_1
/*      */     //   23: iconst_4
/*      */     //   24: if_icmpge +20 -> 44
/*      */     //   27: aload_0
/*      */     //   28: invokevirtual 112	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   31: new 94	java/io/IOException
/*      */     //   34: dup
/*      */     //   35: ldc 113
/*      */     //   37: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   40: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   43: athrow
/*      */     //   44: aload_0
/*      */     //   45: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   48: iconst_0
/*      */     //   49: baload
/*      */     //   50: sipush 255
/*      */     //   53: iand
/*      */     //   54: aload_0
/*      */     //   55: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   58: iconst_1
/*      */     //   59: baload
/*      */     //   60: sipush 255
/*      */     //   63: iand
/*      */     //   64: bipush 8
/*      */     //   66: ishl
/*      */     //   67: iadd
/*      */     //   68: aload_0
/*      */     //   69: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   72: iconst_2
/*      */     //   73: baload
/*      */     //   74: sipush 255
/*      */     //   77: iand
/*      */     //   78: bipush 16
/*      */     //   80: ishl
/*      */     //   81: iadd
/*      */     //   82: istore_2
/*      */     //   83: aload_0
/*      */     //   84: getfield 49	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   87: ifeq +66 -> 153
/*      */     //   90: new 116	java/lang/StringBuffer
/*      */     //   93: dup
/*      */     //   94: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   97: astore_3
/*      */     //   98: aload_3
/*      */     //   99: ldc 118
/*      */     //   101: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   104: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   107: pop
/*      */     //   108: aload_3
/*      */     //   109: iload_2
/*      */     //   110: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   113: pop
/*      */     //   114: aload_3
/*      */     //   115: ldc 121
/*      */     //   117: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   120: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   123: pop
/*      */     //   124: aload_3
/*      */     //   125: aload_0
/*      */     //   126: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   129: iconst_4
/*      */     //   130: invokestatic 122	com/mysql/jdbc/StringUtils:dumpAsHex	([BI)Ljava/lang/String;
/*      */     //   133: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   136: pop
/*      */     //   137: aload_0
/*      */     //   138: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   141: invokevirtual 69	com/mysql/jdbc/Connection:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   144: aload_3
/*      */     //   145: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   148: invokeinterface 124 2 0
/*      */     //   153: aload_0
/*      */     //   154: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   157: iconst_3
/*      */     //   158: baload
/*      */     //   159: istore_3
/*      */     //   160: aload_0
/*      */     //   161: getfield 3	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   164: ifne +25 -> 189
/*      */     //   167: aload_0
/*      */     //   168: getfield 50	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   171: ifeq +23 -> 194
/*      */     //   174: aload_0
/*      */     //   175: getfield 35	com/mysql/jdbc/MysqlIO:checkPacketSequence	Z
/*      */     //   178: ifeq +16 -> 194
/*      */     //   181: aload_0
/*      */     //   182: iload_3
/*      */     //   183: invokespecial 125	com/mysql/jdbc/MysqlIO:checkPacketSequencing	(B)V
/*      */     //   186: goto +8 -> 194
/*      */     //   189: aload_0
/*      */     //   190: iconst_0
/*      */     //   191: putfield 3	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   194: aload_0
/*      */     //   195: iload_3
/*      */     //   196: putfield 34	com/mysql/jdbc/MysqlIO:readPacketSequence	B
/*      */     //   199: iload_2
/*      */     //   200: iconst_1
/*      */     //   201: iadd
/*      */     //   202: newarray byte
/*      */     //   204: astore 4
/*      */     //   206: aload_0
/*      */     //   207: aload_0
/*      */     //   208: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   211: aload 4
/*      */     //   213: iconst_0
/*      */     //   214: iload_2
/*      */     //   215: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   218: istore 5
/*      */     //   220: iload 5
/*      */     //   222: iload_2
/*      */     //   223: if_icmpeq +40 -> 263
/*      */     //   226: new 94	java/io/IOException
/*      */     //   229: dup
/*      */     //   230: new 116	java/lang/StringBuffer
/*      */     //   233: dup
/*      */     //   234: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   237: ldc 126
/*      */     //   239: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   242: iload_2
/*      */     //   243: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   246: ldc 127
/*      */     //   248: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   251: iload 5
/*      */     //   253: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   256: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   259: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   262: athrow
/*      */     //   263: aload 4
/*      */     //   265: iload_2
/*      */     //   266: iconst_0
/*      */     //   267: bastore
/*      */     //   268: aload 4
/*      */     //   270: aload_0
/*      */     //   271: getfield 30	com/mysql/jdbc/MysqlIO:useNewIo	Z
/*      */     //   274: invokestatic 128	com/mysql/jdbc/Buffer:allocateNew	([BZ)Lcom/mysql/jdbc/Buffer;
/*      */     //   277: astore 6
/*      */     //   279: aload 6
/*      */     //   281: iload_2
/*      */     //   282: iconst_1
/*      */     //   283: iadd
/*      */     //   284: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   287: aload_0
/*      */     //   288: getfield 49	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   291: ifeq +52 -> 343
/*      */     //   294: new 116	java/lang/StringBuffer
/*      */     //   297: dup
/*      */     //   298: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   301: astore 7
/*      */     //   303: aload 7
/*      */     //   305: ldc 130
/*      */     //   307: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   310: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   313: pop
/*      */     //   314: aload 7
/*      */     //   316: aload 6
/*      */     //   318: iload_2
/*      */     //   319: invokestatic 131	com/mysql/jdbc/MysqlIO:getPacketDumpToLog	(Lcom/mysql/jdbc/Buffer;I)Ljava/lang/String;
/*      */     //   322: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   325: pop
/*      */     //   326: aload_0
/*      */     //   327: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   330: invokevirtual 69	com/mysql/jdbc/Connection:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   333: aload 7
/*      */     //   335: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   338: invokeinterface 124 2 0
/*      */     //   343: aload_0
/*      */     //   344: getfield 50	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   347: ifeq +16 -> 363
/*      */     //   350: aload_0
/*      */     //   351: iconst_0
/*      */     //   352: iconst_0
/*      */     //   353: iconst_0
/*      */     //   354: aload_0
/*      */     //   355: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   358: aload 6
/*      */     //   360: invokespecial 132	com/mysql/jdbc/MysqlIO:enqueuePacketForDebugging	(ZZI[BLcom/mysql/jdbc/Buffer;)V
/*      */     //   363: aload 6
/*      */     //   365: areturn
/*      */     //   366: aload_0
/*      */     //   367: invokespecial 133	com/mysql/jdbc/MysqlIO:readViaChannel	()Lcom/mysql/jdbc/Buffer;
/*      */     //   370: areturn
/*      */     //   371: astore_1
/*      */     //   372: new 95	com/mysql/jdbc/CommunicationsException
/*      */     //   375: dup
/*      */     //   376: aload_0
/*      */     //   377: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   380: aload_0
/*      */     //   381: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   384: aload_1
/*      */     //   385: invokespecial 96	com/mysql/jdbc/CommunicationsException:<init>	(Lcom/mysql/jdbc/Connection;JLjava/lang/Exception;)V
/*      */     //   388: athrow
/*      */     //   389: astore_1
/*      */     //   390: aload_0
/*      */     //   391: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   394: iconst_0
/*      */     //   395: iconst_0
/*      */     //   396: iconst_1
/*      */     //   397: aload_1
/*      */     //   398: invokevirtual 135	com/mysql/jdbc/Connection:realClose	(ZZZLjava/lang/Throwable;)V
/*      */     //   401: aload_1
/*      */     //   402: athrow
/*      */     //   403: astore 8
/*      */     //   405: aload_1
/*      */     //   406: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	365	371	java/io/IOException
/*      */     //   366	370	371	java/io/IOException
/*      */     //   0	365	389	java/lang/OutOfMemoryError
/*      */     //   366	370	389	java/lang/OutOfMemoryError
/*      */     //   390	401	403	finally
/*      */     //   403	405	403	finally } 
/*  578 */   protected final Field unpackField(Buffer packet, boolean extractDefaultValues) throws SQLException { if (this.use41Extensions)
/*      */     {
/*      */       int catalogNameLength;
/*  581 */       if (this.has41NewNewProt)
/*      */       {
/*  583 */         int catalogNameStart = packet.getPosition() + 1;
/*  584 */         catalogNameLength = packet.fastSkipLenString();
/*      */       }
/*      */ 
/*  587 */       int databaseNameStart = packet.getPosition() + 1;
/*  588 */       int databaseNameLength = packet.fastSkipLenString();
/*      */ 
/*  590 */       int tableNameStart = packet.getPosition() + 1;
/*  591 */       int tableNameLength = packet.fastSkipLenString();
/*      */ 
/*  594 */       int originalTableNameStart = packet.getPosition() + 1;
/*  595 */       int originalTableNameLength = packet.fastSkipLenString();
/*      */ 
/*  598 */       int nameStart = packet.getPosition() + 1;
/*  599 */       int nameLength = packet.fastSkipLenString();
/*      */ 
/*  602 */       int originalColumnNameStart = packet.getPosition() + 1;
/*  603 */       int originalColumnNameLength = packet.fastSkipLenString();
/*      */ 
/*  605 */       packet.readByte();
/*      */ 
/*  607 */       short charSetNumber = (short)packet.readInt();
/*      */ 
/*  609 */       long colLength = 0L;
/*      */ 
/*  611 */       if (this.has41NewNewProt)
/*  612 */         colLength = packet.readLong();
/*      */       else {
/*  614 */         colLength = packet.readLongInt();
/*      */       }
/*      */ 
/*  617 */       int colType = packet.readByte() & 0xFF;
/*      */ 
/*  619 */       short colFlag = 0;
/*      */ 
/*  621 */       if (this.hasLongColumnInfo)
/*  622 */         colFlag = (short)packet.readInt();
/*      */       else {
/*  624 */         colFlag = (short)(packet.readByte() & 0xFF);
/*      */       }
/*      */ 
/*  627 */       int colDecimals = packet.readByte() & 0xFF;
/*      */ 
/*  629 */       int defaultValueStart = -1;
/*  630 */       int defaultValueLength = -1;
/*      */ 
/*  632 */       if (extractDefaultValues) {
/*  633 */         defaultValueStart = packet.getPosition() + 1;
/*  634 */         defaultValueLength = packet.fastSkipLenString();
/*      */       }
/*      */ 
/*  637 */       Field field = new Field(this.connection, packet.getByteBuffer(), databaseNameStart, databaseNameLength, tableNameStart, tableNameLength, originalTableNameStart, originalTableNameLength, nameStart, nameLength, originalColumnNameStart, originalColumnNameLength, colLength, colType, colFlag, colDecimals, defaultValueStart, defaultValueLength, charSetNumber);
/*      */ 
/*  645 */       return field;
/*      */     }
/*      */ 
/*  648 */     int tableNameStart = packet.getPosition() + 1;
/*  649 */     int tableNameLength = packet.fastSkipLenString();
/*  650 */     int nameStart = packet.getPosition() + 1;
/*  651 */     int nameLength = packet.fastSkipLenString();
/*  652 */     int colLength = packet.readnBytes();
/*  653 */     int colType = packet.readnBytes();
/*  654 */     packet.readByte();
/*      */ 
/*  656 */     short colFlag = 0;
/*      */ 
/*  658 */     if (this.hasLongColumnInfo)
/*  659 */       colFlag = (short)packet.readInt();
/*      */     else {
/*  661 */       colFlag = (short)(packet.readByte() & 0xFF);
/*      */     }
/*      */ 
/*  664 */     int colDecimals = packet.readByte() & 0xFF;
/*      */ 
/*  666 */     if (this.colDecimalNeedsBump) {
/*  667 */       colDecimals++;
/*      */     }
/*      */ 
/*  670 */     Field field = new Field(this.connection, packet.getByteBuffer(), nameStart, nameLength, tableNameStart, tableNameLength, colLength, colType, colFlag, colDecimals);
/*      */ 
/*  674 */     return field; }
/*      */ 
/*      */   protected boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag)
/*      */   {
/*  678 */     if ((this.use41Extensions) && (this.connection.getElideSetAutoCommits())) {
/*  679 */       boolean autoCommitModeOnServer = (this.serverStatus & 0x2) != 0;
/*      */ 
/*  682 */       if (!autoCommitFlag)
/*      */       {
/*  686 */         boolean inTransactionOnServer = (this.serverStatus & 0x1) != 0;
/*      */ 
/*  689 */         return !inTransactionOnServer;
/*      */       }
/*      */ 
/*  692 */       return !autoCommitModeOnServer;
/*      */     }
/*      */ 
/*  695 */     return true;
/*      */   }
/*      */ 
/*      */   protected void changeUser(String userName, String password, String database)
/*      */     throws SQLException
/*      */   {
/*  709 */     this.packetSequence = -1;
/*      */ 
/*  711 */     int passwordLength = 16;
/*  712 */     int userLength = 0;
/*      */ 
/*  714 */     if (userName != null) {
/*  715 */       userLength = userName.length();
/*      */     }
/*      */ 
/*  718 */     int packLength = userLength + passwordLength + 7 + 4;
/*      */ 
/*  720 */     if ((this.serverCapabilities & 0x8000) != 0) {
/*  721 */       Buffer changeUserPacket = Buffer.allocateNew(packLength + 1, this.useNewIo);
/*      */ 
/*  723 */       changeUserPacket.writeByte(17);
/*      */ 
/*  725 */       if (versionMeetsMinimum(4, 1, 1)) {
/*  726 */         secureAuth411(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */       else {
/*  729 */         secureAuth(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  734 */       Buffer packet = Buffer.allocateNew(packLength, this.useNewIo);
/*  735 */       packet.writeByte(17);
/*      */ 
/*  738 */       packet.writeString(userName);
/*      */ 
/*  740 */       if (this.protocolVersion > 9)
/*  741 */         packet.writeString(Util.newCrypt(password, this.seed));
/*      */       else {
/*  743 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       }
/*      */ 
/*  746 */       boolean localUseConnectWithDb = (this.useConnectWithDb) && (database != null) && (database.length() > 0);
/*      */ 
/*  749 */       if (localUseConnectWithDb) {
/*  750 */         packet.writeString(database);
/*      */       }
/*      */ 
/*  753 */       send(packet);
/*  754 */       checkErrorPacket();
/*      */ 
/*  756 */       if (!localUseConnectWithDb)
/*  757 */         changeDatabaseTo(database);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Buffer checkErrorPacket()
/*      */     throws SQLException
/*      */   {
/*  771 */     return checkErrorPacket(-1);
/*      */   }
/*      */ 
/*      */   protected void checkForCharsetMismatch()
/*      */   {
/*  778 */     if ((this.connection.getUseUnicode()) && (this.connection.getEncoding() != null))
/*      */     {
/*  780 */       String encodingToCheck = jvmPlatformCharset;
/*      */ 
/*  782 */       if (encodingToCheck == null) {
/*  783 */         encodingToCheck = System.getProperty("file.encoding");
/*      */       }
/*      */ 
/*  786 */       if (encodingToCheck == null)
/*  787 */         this.platformDbCharsetMatches = false;
/*      */       else
/*  789 */         this.platformDbCharsetMatches = encodingToCheck.equals(this.connection.getEncoding());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void clearInputStream() throws SQLException
/*      */   {
/*  795 */     if (!this.useNewIo) {
/*      */       try {
/*  797 */         int len = this.mysqlInput.available();
/*      */ 
/*  799 */         while (len > 0) {
/*  800 */           this.mysqlInput.skip(len);
/*  801 */           len = this.mysqlInput.available();
/*      */         }
/*      */       } catch (IOException ioEx) {
/*  804 */         throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/*  811 */         this.socketChannel.configureBlocking(false);
/*      */ 
/*  813 */         int len = 0;
/*      */         while (true)
/*      */         {
/*  816 */           len = this.socketChannel.read(this.channelClearBuf);
/*      */ 
/*  818 */           if ((len == 0) || (len == -1))
/*      */           {
/*      */             break;
/*      */           }
/*  822 */           this.channelClearBuf.clear();
/*      */         }
/*      */       } catch (IOException ioEx) {
/*  825 */         throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */       }
/*      */       finally {
/*      */         try {
/*  829 */           this.socketChannel.configureBlocking(true);
/*      */         } catch (IOException ioEx) {
/*  831 */           throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void resetReadPacketSequence()
/*      */   {
/*  845 */     this.readPacketSequence = 0;
/*      */   }
/*      */ 
/*      */   protected void dumpPacketRingBuffer() throws SQLException {
/*  849 */     if ((this.packetDebugRingBuffer != null) && (this.connection.getEnablePacketDebug()))
/*      */     {
/*  851 */       StringBuffer dumpBuffer = new StringBuffer();
/*      */ 
/*  853 */       dumpBuffer.append("Last " + this.packetDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
/*      */ 
/*  855 */       dumpBuffer.append("\n");
/*      */ 
/*  857 */       Iterator ringBufIter = this.packetDebugRingBuffer.iterator();
/*  858 */       while (ringBufIter.hasNext()) {
/*  859 */         dumpBuffer.append((StringBuffer)ringBufIter.next());
/*  860 */         dumpBuffer.append("\n");
/*      */       }
/*      */ 
/*  863 */       this.connection.getLog().logTrace(dumpBuffer.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void explainSlowQuery(byte[] querySQL, String truncatedQuery)
/*      */     throws SQLException
/*      */   {
/*  877 */     if (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, "SELECT"))
/*      */     {
/*  879 */       PreparedStatement stmt = null;
/*  880 */       java.sql.ResultSet rs = null;
/*      */       try
/*      */       {
/*  883 */         stmt = this.connection.clientPrepareStatement("EXPLAIN ?");
/*  884 */         stmt.setBytesNoEscapeNoQuotes(1, querySQL);
/*  885 */         rs = stmt.executeQuery();
/*      */ 
/*  887 */         StringBuffer explainResults = new StringBuffer(Messages.getString("MysqlIO.8") + truncatedQuery + Messages.getString("MysqlIO.9"));
/*      */ 
/*  891 */         ResultSetUtil.appendResultSetSlashGStyle(explainResults, rs);
/*      */ 
/*  893 */         this.connection.getLog().logWarn(explainResults.toString());
/*      */       } catch (SQLException sqlEx) {
/*      */       } finally {
/*  896 */         if (rs != null) {
/*  897 */           rs.close();
/*      */         }
/*      */ 
/*  900 */         if (stmt != null)
/*  901 */           stmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static int getMaxBuf()
/*      */   {
/*  909 */     return maxBufferSize;
/*      */   }
/*      */ 
/*      */   final int getServerMajorVersion()
/*      */   {
/*  918 */     return this.serverMajorVersion;
/*      */   }
/*      */ 
/*      */   final int getServerMinorVersion()
/*      */   {
/*  927 */     return this.serverMinorVersion;
/*      */   }
/*      */ 
/*      */   final int getServerSubMinorVersion()
/*      */   {
/*  936 */     return this.serverSubMinorVersion;
/*      */   }
/*      */ 
/*      */   String getServerVersion()
/*      */   {
/*  945 */     return this.serverVersion;
/*      */   }
/*      */ 
/*      */   void doHandshake(String user, String password, String database)
/*      */     throws SQLException
/*      */   {
/*  962 */     this.checkPacketSequence = false;
/*  963 */     this.readPacketSequence = 0;
/*      */ 
/*  965 */     Buffer buf = readPacket();
/*      */ 
/*  968 */     this.protocolVersion = buf.readByte();
/*      */ 
/*  970 */     if (this.protocolVersion == -1) {
/*      */       try {
/*  972 */         this.mysqlConnection.close();
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */       }
/*  977 */       int errno = 2000;
/*      */ 
/*  979 */       errno = buf.readInt();
/*      */ 
/*  981 */       String serverErrorMessage = buf.readString();
/*      */ 
/*  983 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.10"));
/*      */ 
/*  985 */       errorBuf.append(serverErrorMessage);
/*  986 */       errorBuf.append("\"");
/*      */ 
/*  988 */       String xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */ 
/*  991 */       throw new SQLException(SQLError.get(xOpen) + ", " + errorBuf.toString(), xOpen, errno);
/*      */     }
/*      */ 
/*  995 */     this.serverVersion = buf.readString();
/*      */ 
/*  998 */     int point = this.serverVersion.indexOf(".");
/*      */ 
/* 1000 */     if (point != -1) {
/*      */       try {
/* 1002 */         int n = Integer.parseInt(this.serverVersion.substring(0, point));
/* 1003 */         this.serverMajorVersion = n;
/*      */       }
/*      */       catch (NumberFormatException NFE1)
/*      */       {
/*      */       }
/* 1008 */       String remaining = this.serverVersion.substring(point + 1, this.serverVersion.length());
/*      */ 
/* 1010 */       point = remaining.indexOf(".");
/*      */ 
/* 1012 */       if (point != -1) {
/*      */         try {
/* 1014 */           int n = Integer.parseInt(remaining.substring(0, point));
/* 1015 */           this.serverMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/*      */         }
/* 1020 */         remaining = remaining.substring(point + 1, remaining.length());
/*      */ 
/* 1022 */         int pos = 0;
/*      */ 
/* 1024 */         while ((pos < remaining.length()) && 
/* 1025 */           (remaining.charAt(pos) >= '0') && (remaining.charAt(pos) <= '9'))
/*      */         {
/* 1030 */           pos++;
/*      */         }
/*      */         try
/*      */         {
/* 1034 */           int n = Integer.parseInt(remaining.substring(0, pos));
/* 1035 */           this.serverSubMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/* 1042 */     if (versionMeetsMinimum(4, 0, 8)) {
/* 1043 */       this.maxThreeBytes = 16777215;
/* 1044 */       this.useNewLargePackets = true;
/*      */     } else {
/* 1046 */       this.maxThreeBytes = 16581375;
/* 1047 */       this.useNewLargePackets = false;
/*      */     }
/*      */ 
/* 1050 */     this.colDecimalNeedsBump = versionMeetsMinimum(3, 23, 0);
/* 1051 */     this.colDecimalNeedsBump = (!versionMeetsMinimum(3, 23, 15));
/* 1052 */     this.useNewUpdateCounts = versionMeetsMinimum(3, 22, 5);
/*      */ 
/* 1054 */     buf.readLong();
/* 1055 */     this.seed = buf.readString();
/*      */ 
/* 1057 */     this.serverCapabilities = 0;
/*      */ 
/* 1059 */     if (buf.getPosition() < buf.getBufLength()) {
/* 1060 */       this.serverCapabilities = buf.readInt();
/*      */     }
/*      */ 
/* 1063 */     if (versionMeetsMinimum(4, 1, 1)) {
/* 1064 */       int position = buf.getPosition();
/*      */ 
/* 1067 */       this.serverCharsetIndex = (buf.readByte() & 0xFF);
/* 1068 */       this.serverStatus = buf.readInt();
/* 1069 */       buf.setPosition(position + 16);
/*      */ 
/* 1071 */       String seedPart2 = buf.readString();
/* 1072 */       StringBuffer newSeed = new StringBuffer(20);
/* 1073 */       newSeed.append(this.seed);
/* 1074 */       newSeed.append(seedPart2);
/* 1075 */       this.seed = newSeed.toString();
/*      */     }
/*      */ 
/* 1078 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()))
/*      */     {
/* 1080 */       this.clientParam |= 32L;
/*      */     }
/*      */ 
/* 1083 */     this.useConnectWithDb = ((database != null) && (database.length() > 0) && (!this.connection.getCreateDatabaseIfNotExist()));
/*      */ 
/* 1087 */     if (this.useConnectWithDb) {
/* 1088 */       this.clientParam |= 8L;
/*      */     }
/*      */ 
/* 1091 */     if (((this.serverCapabilities & 0x800) == 0) && (this.connection.getUseSSL()))
/*      */     {
/* 1093 */       if (this.connection.getRequireSSL()) {
/* 1094 */         this.connection.close();
/* 1095 */         forceClose();
/* 1096 */         throw new SQLException(Messages.getString("MysqlIO.15"), "08001");
/*      */       }
/*      */ 
/* 1100 */       this.connection.setUseSSL(false);
/*      */     }
/*      */ 
/* 1103 */     if ((this.serverCapabilities & 0x4) != 0)
/*      */     {
/* 1105 */       this.clientParam |= 4L;
/* 1106 */       this.hasLongColumnInfo = true;
/*      */     }
/*      */ 
/* 1110 */     this.clientParam |= 2L;
/*      */ 
/* 1112 */     if (this.connection.getAllowLoadLocalInfile()) {
/* 1113 */       this.clientParam |= 128L;
/*      */     }
/*      */ 
/* 1116 */     if (this.isInteractiveClient) {
/* 1117 */       this.clientParam |= 1024L;
/*      */     }
/*      */ 
/* 1121 */     if (this.protocolVersion > 9)
/* 1122 */       this.clientParam |= 1L;
/*      */     else {
/* 1124 */       this.clientParam &= -2L;
/*      */     }
/*      */ 
/* 1130 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 1131 */       if (versionMeetsMinimum(4, 1, 1)) {
/* 1132 */         this.clientParam |= 512L;
/* 1133 */         this.has41NewNewProt = true;
/*      */ 
/* 1136 */         this.clientParam |= 8192L;
/*      */ 
/* 1139 */         this.clientParam |= 131072L;
/*      */ 
/* 1144 */         if (this.connection.getAllowMultiQueries())
/* 1145 */           this.clientParam |= 65536L;
/*      */       }
/*      */       else {
/* 1148 */         this.clientParam |= 16384L;
/* 1149 */         this.has41NewNewProt = false;
/*      */       }
/*      */ 
/* 1152 */       this.use41Extensions = true;
/*      */     }
/*      */ 
/* 1155 */     int passwordLength = 16;
/* 1156 */     int userLength = 0;
/* 1157 */     int databaseLength = 0;
/*      */ 
/* 1159 */     if (user != null) {
/* 1160 */       userLength = user.length();
/*      */     }
/*      */ 
/* 1163 */     if (database != null) {
/* 1164 */       databaseLength = database.length();
/*      */     }
/*      */ 
/* 1167 */     int packLength = userLength + passwordLength + databaseLength + 7 + 4;
/*      */ 
/* 1169 */     Buffer packet = null;
/*      */ 
/* 1171 */     if (!this.connection.getUseSSL()) {
/* 1172 */       if ((this.serverCapabilities & 0x8000) != 0) {
/* 1173 */         this.clientParam |= 32768L;
/*      */ 
/* 1175 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 1176 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         }
/*      */         else
/* 1179 */           secureAuth(null, packLength, user, password, database, true);
/*      */       }
/*      */       else
/*      */       {
/* 1183 */         packet = Buffer.allocateNew(packLength, this.useNewIo);
/*      */ 
/* 1185 */         if ((this.clientParam & 0x4000) != 0L) {
/* 1186 */           if (versionMeetsMinimum(4, 1, 1)) {
/* 1187 */             packet.writeLong(this.clientParam);
/* 1188 */             packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 1193 */             packet.writeByte(8);
/*      */ 
/* 1196 */             packet.writeBytesNoNull(new byte[23]);
/*      */           } else {
/* 1198 */             packet.writeLong(this.clientParam);
/* 1199 */             packet.writeLong(this.maxThreeBytes);
/*      */           }
/*      */         } else {
/* 1202 */           packet.writeInt((int)this.clientParam);
/* 1203 */           packet.writeLongInt(this.maxThreeBytes);
/*      */         }
/*      */ 
/* 1207 */         packet.writeString(user);
/*      */ 
/* 1209 */         if (this.protocolVersion > 9)
/* 1210 */           packet.writeString(Util.newCrypt(password, this.seed));
/*      */         else {
/* 1212 */           packet.writeString(Util.oldCrypt(password, this.seed));
/*      */         }
/*      */ 
/* 1215 */         if (this.useConnectWithDb) {
/* 1216 */           packet.writeString(database);
/*      */         }
/*      */ 
/* 1219 */         send(packet);
/*      */       }
/*      */     }
/* 1222 */     else negotiateSSLConnection(user, password, database, packLength);
/*      */ 
/* 1228 */     if (!versionMeetsMinimum(4, 1, 1)) {
/* 1229 */       checkErrorPacket();
/*      */     }
/*      */ 
/* 1235 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()))
/*      */     {
/* 1239 */       this.deflater = new Deflater();
/* 1240 */       this.useCompression = true;
/* 1241 */       this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
/*      */     }
/*      */ 
/* 1245 */     if (!this.useConnectWithDb)
/* 1246 */       changeDatabaseTo(database);
/*      */   }
/*      */ 
/*      */   private void changeDatabaseTo(String database) throws SQLException, CommunicationsException
/*      */   {
/* 1251 */     if ((database == null) || (database.length() == 0)) {
/* 1252 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1256 */       sendCommand(2, database, null, false, null);
/*      */     } catch (Exception ex) {
/* 1258 */       if (this.connection.getCreateDatabaseIfNotExist()) {
/* 1259 */         sendCommand(3, "CREATE DATABASE IF NOT EXISTS " + database, null, false, null);
/*      */ 
/* 1262 */         sendCommand(2, database, null, false, null);
/*      */       } else {
/* 1264 */         throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ex);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   final Object[] nextRow(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 1289 */     Buffer rowPacket = checkErrorPacket();
/*      */ 
/* 1291 */     if (!isBinaryEncoded)
/*      */     {
/* 1296 */       rowPacket.setPosition(rowPacket.getPosition() - 1);
/*      */ 
/* 1298 */       if (!rowPacket.isLastDataPacket()) {
/* 1299 */         byte[][] rowData = new byte[columnCount][];
/*      */ 
/* 1301 */         int offset = 0;
/*      */ 
/* 1303 */         for (int i = 0; i < columnCount; i++) {
/* 1304 */           rowData[i] = rowPacket.readLenByteArray(offset);
/*      */         }
/*      */ 
/* 1307 */         return rowData;
/*      */       }
/*      */ 
/* 1310 */       readServerStatusForResultSets(rowPacket);
/*      */ 
/* 1312 */       return null;
/*      */     }
/*      */ 
/* 1319 */     if (!rowPacket.isLastDataPacket()) {
/* 1320 */       return unpackBinaryResultSetRow(fields, rowPacket, resultSetConcurrency);
/*      */     }
/*      */ 
/* 1324 */     readServerStatusForResultSets(rowPacket);
/*      */ 
/* 1326 */     return null;
/*      */   }
/*      */ 
/*      */   final void quit()
/*      */     throws SQLException
/*      */   {
/* 1335 */     Buffer packet = Buffer.allocateNew(6, this.useNewIo);
/* 1336 */     this.packetSequence = -1;
/* 1337 */     packet.writeByte(1);
/* 1338 */     send(packet);
/* 1339 */     forceClose();
/*      */   }
/*      */ 
/*      */   Buffer getSharedSendPacket()
/*      */   {
/* 1349 */     if (this.sharedSendPacket == null) {
/* 1350 */       if (this.useNewIo) {
/* 1351 */         this.sharedSendPacket = Buffer.allocateDirect(this.connection.getNetBufferLength(), true);
/*      */       }
/*      */       else {
/* 1354 */         this.sharedSendPacket = Buffer.allocateNew(this.connection.getNetBufferLength(), false);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1359 */     return this.sharedSendPacket;
/*      */   }
/*      */ 
/*      */   void closeStreamer(RowData streamer) throws SQLException {
/* 1363 */     if (this.streamingData == null) {
/* 1364 */       throw new SQLException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"));
/*      */     }
/*      */ 
/* 1368 */     if (streamer != this.streamingData) {
/* 1369 */       throw new SQLException(Messages.getString("MysqlIO.19") + streamer + Messages.getString("MysqlIO.20") + Messages.getString("MysqlIO.21") + Messages.getString("MysqlIO.22"));
/*      */     }
/*      */ 
/* 1375 */     this.streamingData = null;
/*      */   }
/*      */ 
/*      */   ResultSet readAllResults(Statement callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, boolean unpackFieldInfo)
/*      */     throws SQLException
/*      */   {
/* 1383 */     resultPacket.setPosition(resultPacket.getPosition() - 1);
/*      */ 
/* 1385 */     ResultSet topLevelResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, isBinaryEncoded, preSentColumnCount, unpackFieldInfo);
/*      */ 
/* 1390 */     ResultSet currentResultSet = topLevelResultSet;
/*      */ 
/* 1392 */     boolean checkForMoreResults = (this.clientParam & 0x20000) != 0L;
/*      */ 
/* 1395 */     boolean serverHasMoreResults = (this.serverStatus & 0x8) != 0;
/*      */ 
/* 1401 */     if ((serverHasMoreResults) && (streamResults)) {
/* 1402 */       clearInputStream();
/*      */ 
/* 1404 */       throw new SQLException(Messages.getString("MysqlIO.23"), "S1C00");
/*      */     }
/*      */ 
/* 1408 */     boolean moreRowSetsExist = checkForMoreResults & serverHasMoreResults;
/*      */ 
/* 1410 */     while (moreRowSetsExist) {
/* 1411 */       Buffer fieldPacket = readPacket();
/*      */ 
/* 1413 */       if ((fieldPacket.readByte(0) == 0) && (fieldPacket.readByte(1) == 0) && (fieldPacket.readByte(2) == 0))
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/* 1419 */       ResultSet newResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, fieldPacket, isBinaryEncoded, preSentColumnCount, unpackFieldInfo);
/*      */ 
/* 1424 */       currentResultSet.setNextResultSet(newResultSet);
/*      */ 
/* 1426 */       currentResultSet = newResultSet;
/*      */ 
/* 1428 */       moreRowSetsExist = (this.serverStatus & 0x8) != 0;
/*      */     }
/*      */ 
/* 1431 */     if (!streamResults) {
/* 1432 */       clearInputStream();
/*      */     }
/*      */ 
/* 1435 */     reclaimLargeReusablePacket();
/*      */ 
/* 1437 */     return topLevelResultSet;
/*      */   }
/*      */ 
/*      */   void resetMaxBuf()
/*      */   {
/* 1444 */     this.maxAllowedPacket = this.connection.getMaxAllowedPacket();
/*      */   }
/*      */ 
/*      */   final Buffer sendCommand(int command, String extraData, Buffer queryPacket, boolean skipCheck, String extraDataCharEncoding)
/*      */     throws SQLException
/*      */   {
/* 1475 */     this.enablePacketDebug = this.connection.getEnablePacketDebug();
/* 1476 */     this.traceProtocol = this.connection.getTraceProtocol();
/* 1477 */     this.readPacketSequence = 0;
/*      */     try
/*      */     {
/* 1481 */       checkForOutstandingStreamingData();
/*      */ 
/* 1486 */       this.serverStatus = 0;
/* 1487 */       this.hadWarnings = false;
/* 1488 */       this.warningCount = 0;
/*      */ 
/* 1490 */       this.queryNoIndexUsed = false;
/* 1491 */       this.queryBadIndexUsed = false;
/*      */ 
/* 1497 */       if (this.useCompression) {
/* 1498 */         int bytesLeft = this.mysqlInput.available();
/*      */ 
/* 1500 */         if (bytesLeft > 0) {
/* 1501 */           this.mysqlInput.skip(bytesLeft);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 1506 */         clearInputStream();
/*      */ 
/* 1515 */         if (queryPacket == null) {
/* 1516 */           int packLength = 8 + (extraData != null ? extraData.length() : 0) + 2;
/*      */ 
/* 1519 */           if (this.sendPacket == null) {
/* 1520 */             this.sendPacket = Buffer.allocateNew(packLength, this.useNewIo);
/*      */           }
/*      */ 
/* 1524 */           this.packetSequence = -1;
/* 1525 */           this.readPacketSequence = 0;
/* 1526 */           this.checkPacketSequence = true;
/* 1527 */           this.sendPacket.clear();
/*      */ 
/* 1529 */           this.sendPacket.writeByte((byte)command);
/*      */ 
/* 1531 */           if ((command == 2) || (command == 5) || (command == 6) || (command == 3) || (command == 22))
/*      */           {
/* 1536 */             if (extraDataCharEncoding == null)
/* 1537 */               this.sendPacket.writeStringNoNull(extraData);
/*      */             else {
/* 1539 */               this.sendPacket.writeStringNoNull(extraData, extraDataCharEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */             }
/*      */ 
/*      */           }
/* 1544 */           else if (command == 12) {
/* 1545 */             long id = new Long(extraData).longValue();
/* 1546 */             this.sendPacket.writeLong(id);
/*      */           }
/*      */ 
/* 1549 */           send(this.sendPacket);
/*      */         } else {
/* 1551 */           this.packetSequence = -1;
/* 1552 */           send(queryPacket);
/*      */         }
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1556 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 1558 */         throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ex);
/*      */       }
/*      */ 
/* 1562 */       Buffer returnPacket = null;
/*      */ 
/* 1564 */       if (!skipCheck) {
/* 1565 */         if ((command == 23) || (command == 26))
/*      */         {
/* 1567 */           this.readPacketSequence = 0;
/* 1568 */           this.packetSequenceReset = true;
/*      */         }
/*      */ 
/* 1571 */         returnPacket = checkErrorPacket(command);
/*      */       }
/*      */ 
/* 1574 */       return returnPacket; } catch (IOException ioEx) {
/*      */     }
/* 1576 */     throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */   }
/*      */ 
/*      */   final ResultSet sqlQueryDirect(Statement callingStatement, String query, String characterEncoding, Buffer queryPacket, int maxRows, Connection conn, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean unpackFieldInfo)
/*      */     throws Exception
/*      */   {
/* 1605 */     long queryStartTime = 0L;
/* 1606 */     long queryEndTime = 0L;
/*      */ 
/* 1608 */     if (query != null)
/*      */     {
/* 1614 */       int packLength = 5 + query.length() * 2 + 2;
/*      */ 
/* 1616 */       if (this.sendPacket == null) {
/* 1617 */         if (this.useNewIo) {
/* 1618 */           this.sendPacket = Buffer.allocateDirect(packLength, this.useNewIo);
/*      */         }
/*      */         else
/* 1621 */           this.sendPacket = Buffer.allocateNew(packLength, false);
/*      */       }
/*      */       else {
/* 1624 */         this.sendPacket.clear();
/*      */       }
/*      */ 
/* 1627 */       this.sendPacket.writeByte(3);
/*      */ 
/* 1629 */       if (characterEncoding != null) {
/* 1630 */         if (this.platformDbCharsetMatches) {
/* 1631 */           this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */         }
/* 1635 */         else if (StringUtils.startsWithIgnoreCaseAndWs(query, "LOAD DATA"))
/* 1636 */           this.sendPacket.writeBytesNoNull(query.getBytes());
/*      */         else {
/* 1638 */           this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1645 */         this.sendPacket.writeStringNoNull(query);
/*      */       }
/*      */ 
/* 1648 */       queryPacket = this.sendPacket;
/*      */     }
/*      */ 
/* 1651 */     byte[] queryBuf = null;
/* 1652 */     int oldPacketPosition = 0;
/*      */ 
/* 1656 */     if (this.needToGrabQueryFromPacket) {
/* 1657 */       queryBuf = queryPacket.getByteBuffer();
/*      */ 
/* 1660 */       oldPacketPosition = queryPacket.getPosition();
/*      */ 
/* 1662 */       queryStartTime = System.currentTimeMillis();
/*      */     }
/*      */ 
/* 1666 */     Buffer resultPacket = sendCommand(3, null, queryPacket, false, null);
/*      */ 
/* 1669 */     long fetchBeginTime = 0L;
/* 1670 */     long fetchEndTime = 0L;
/*      */ 
/* 1672 */     String profileQueryToLog = null;
/*      */ 
/* 1674 */     boolean queryWasSlow = false;
/*      */ 
/* 1676 */     if ((this.profileSql) || (this.logSlowQueries)) {
/* 1677 */       queryEndTime = System.currentTimeMillis();
/*      */ 
/* 1679 */       boolean shouldExtractQuery = false;
/*      */ 
/* 1681 */       if (this.profileSql) {
/* 1682 */         shouldExtractQuery = true;
/* 1683 */       } else if ((this.logSlowQueries) && (queryEndTime - queryStartTime > this.connection.getSlowQueryThresholdMillis()))
/*      */       {
/* 1685 */         shouldExtractQuery = true;
/* 1686 */         queryWasSlow = true;
/*      */       }
/*      */ 
/* 1689 */       if (shouldExtractQuery)
/*      */       {
/* 1691 */         boolean truncated = false;
/*      */ 
/* 1693 */         int extractPosition = oldPacketPosition;
/*      */ 
/* 1695 */         if (oldPacketPosition > 1024) {
/* 1696 */           extractPosition = 1024;
/* 1697 */           truncated = true;
/*      */         }
/*      */ 
/* 1700 */         profileQueryToLog = new String(queryBuf, 5, extractPosition - 5);
/*      */ 
/* 1703 */         if (truncated) {
/* 1704 */           profileQueryToLog = profileQueryToLog + Messages.getString("MysqlIO.25");
/*      */         }
/*      */       }
/*      */ 
/* 1708 */       fetchBeginTime = queryEndTime;
/*      */     }
/*      */ 
/* 1711 */     if (this.autoGenerateTestcaseScript) {
/* 1712 */       String testcaseQuery = null;
/*      */ 
/* 1714 */       if (query != null)
/* 1715 */         testcaseQuery = query;
/*      */       else {
/* 1717 */         testcaseQuery = new String(queryBuf, 5, oldPacketPosition - 5);
/*      */       }
/*      */ 
/* 1721 */       StringBuffer debugBuf = new StringBuffer(testcaseQuery.length() + 32);
/* 1722 */       this.connection.generateConnectionCommentBlock(debugBuf);
/* 1723 */       debugBuf.append(testcaseQuery);
/* 1724 */       debugBuf.append(';');
/* 1725 */       this.connection.dumpTestcaseQuery(debugBuf.toString());
/*      */     }
/*      */ 
/* 1728 */     ResultSet rs = readAllResults(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, false, -1L, unpackFieldInfo);
/*      */ 
/* 1732 */     if (queryWasSlow) {
/* 1733 */       StringBuffer mesgBuf = new StringBuffer(48 + profileQueryToLog.length());
/*      */ 
/* 1735 */       mesgBuf.append(Messages.getString("MysqlIO.26"));
/* 1736 */       mesgBuf.append(this.connection.getSlowQueryThresholdMillis());
/* 1737 */       mesgBuf.append(Messages.getString("MysqlIO.27"));
/* 1738 */       mesgBuf.append(profileQueryToLog);
/*      */ 
/* 1740 */       this.connection.getLog().logWarn(mesgBuf.toString());
/*      */ 
/* 1742 */       if (this.connection.getExplainSlowQueries()) {
/* 1743 */         if (oldPacketPosition < 1048576) {
/* 1744 */           explainSlowQuery(queryPacket.getBytes(5, oldPacketPosition - 5), profileQueryToLog);
/*      */         }
/*      */         else {
/* 1747 */           this.connection.getLog().logWarn(Messages.getString("MysqlIO.28") + 1048576 + Messages.getString("MysqlIO.29"));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1755 */     if (this.profileSql) {
/* 1756 */       fetchEndTime = System.currentTimeMillis();
/*      */ 
/* 1758 */       ProfileEventSink eventSink = ProfileEventSink.getInstance(this.connection);
/*      */ 
/* 1760 */       eventSink.consumeEvent(new ProfilerEvent(3, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, rs.resultId, System.currentTimeMillis(), (int)(queryEndTime - queryStartTime), null, new Throwable(), profileQueryToLog));
/*      */ 
/* 1767 */       eventSink.consumeEvent(new ProfilerEvent(5, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, rs.resultId, System.currentTimeMillis(), (int)(fetchEndTime - fetchBeginTime), null, new Throwable(), null));
/*      */ 
/* 1774 */       if (this.queryBadIndexUsed) {
/* 1775 */         eventSink.consumeEvent(new ProfilerEvent(0, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, rs.resultId, System.currentTimeMillis(), (int)(queryEndTime - queryStartTime), null, new Throwable(), Messages.getString("MysqlIO.33") + profileQueryToLog));
/*      */       }
/*      */ 
/* 1787 */       if (this.queryNoIndexUsed) {
/* 1788 */         eventSink.consumeEvent(new ProfilerEvent(0, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, rs.resultId, System.currentTimeMillis(), (int)(queryEndTime - queryStartTime), null, new Throwable(), Messages.getString("MysqlIO.35") + profileQueryToLog));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1801 */     if (this.hadWarnings) {
/* 1802 */       scanForAndThrowDataTruncation();
/*      */     }
/*      */ 
/* 1805 */     return rs;
/*      */   }
/*      */ 
/*      */   String getHost()
/*      */   {
/* 1814 */     return this.host;
/*      */   }
/*      */ 
/*      */   boolean isVersion(int major, int minor, int subminor)
/*      */   {
/* 1829 */     return (major == getServerMajorVersion()) && (minor == getServerMinorVersion()) && (subminor == getServerSubMinorVersion());
/*      */   }
/*      */ 
/*      */   boolean versionMeetsMinimum(int major, int minor, int subminor)
/*      */   {
/* 1845 */     if (getServerMajorVersion() >= major) {
/* 1846 */       if (getServerMajorVersion() == major) {
/* 1847 */         if (getServerMinorVersion() >= minor) {
/* 1848 */           if (getServerMinorVersion() == minor) {
/* 1849 */             return getServerSubMinorVersion() >= subminor;
/*      */           }
/*      */ 
/* 1853 */           return true;
/*      */         }
/*      */ 
/* 1857 */         return false;
/*      */       }
/*      */ 
/* 1861 */       return true;
/*      */     }
/*      */ 
/* 1864 */     return false;
/*      */   }
/*      */ 
/*      */   private static final String getPacketDumpToLog(Buffer packetToDump, int packetLength)
/*      */   {
/* 1878 */     if (packetLength < 1024) {
/* 1879 */       return packetToDump.dump(packetLength);
/*      */     }
/*      */ 
/* 1882 */     StringBuffer packetDumpBuf = new StringBuffer(4096);
/* 1883 */     packetDumpBuf.append(packetToDump.dump(1024));
/* 1884 */     packetDumpBuf.append(Messages.getString("MysqlIO.36"));
/* 1885 */     packetDumpBuf.append(1024);
/* 1886 */     packetDumpBuf.append(Messages.getString("MysqlIO.37"));
/*      */ 
/* 1888 */     return packetDumpBuf.toString();
/*      */   }
/*      */ 
/*      */   private final int readFully(InputStream in, byte[] b, int off, int len) throws IOException
/*      */   {
/* 1893 */     if (len < 0) {
/* 1894 */       throw new IndexOutOfBoundsException();
/*      */     }
/*      */ 
/* 1897 */     int n = 0;
/*      */ 
/* 1899 */     while (n < len) {
/* 1900 */       int count = in.read(b, off + n, len - n);
/*      */ 
/* 1902 */       if (count < 0) {
/* 1903 */         throw new EOFException();
/*      */       }
/*      */ 
/* 1906 */       n += count;
/*      */     }
/*      */ 
/* 1909 */     return n;
/*      */   }
/*      */ 
/*      */   private final ResultSet readResultsForQueryOrUpdate(Statement callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, boolean unpackFieldInfo)
/*      */     throws SQLException
/*      */   {
/* 1937 */     long columnCount = resultPacket.readFieldLength();
/*      */ 
/* 1939 */     if (columnCount == 0L)
/* 1940 */       return buildResultSetWithUpdates(callingStatement, resultPacket);
/* 1941 */     if (columnCount == -1L) {
/* 1942 */       String charEncoding = null;
/*      */ 
/* 1944 */       if (this.connection.getUseUnicode()) {
/* 1945 */         charEncoding = this.connection.getEncoding();
/*      */       }
/*      */ 
/* 1948 */       String fileName = null;
/*      */ 
/* 1950 */       if (this.platformDbCharsetMatches) {
/* 1951 */         fileName = charEncoding != null ? resultPacket.readString(charEncoding) : resultPacket.readString();
/*      */       }
/*      */       else
/*      */       {
/* 1955 */         fileName = resultPacket.readString();
/*      */       }
/*      */ 
/* 1958 */       return sendFileToServer(callingStatement, fileName);
/*      */     }
/* 1960 */     ResultSet results = getResultSet(callingStatement, columnCount, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, isBinaryEncoded, unpackFieldInfo);
/*      */ 
/* 1964 */     return results;
/*      */   }
/*      */ 
/*      */   private int alignPacketSize(int a, int l)
/*      */   {
/* 1969 */     return a + l - 1 & (l - 1 ^ 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */   private ResultSet buildResultSetWithRows(Statement callingStatement, String catalog, Field[] fields, RowData rows, int resultSetType, int resultSetConcurrency, boolean isBinaryEncoded)
/*      */     throws SQLException
/*      */   {
/* 1977 */     ResultSet rs = null;
/*      */ 
/* 1979 */     switch (resultSetConcurrency) {
/*      */     case 1007:
/* 1981 */       rs = new ResultSet(catalog, fields, rows, this.connection, callingStatement);
/*      */ 
/* 1984 */       if (!isBinaryEncoded) break;
/* 1985 */       rs.setBinaryEncoded(); break;
/*      */     case 1008:
/* 1991 */       rs = new UpdatableResultSet(catalog, fields, rows, this.connection, callingStatement);
/*      */ 
/* 1994 */       break;
/*      */     default:
/* 1997 */       return new ResultSet(catalog, fields, rows, this.connection, callingStatement);
/*      */     }
/*      */ 
/* 2001 */     rs.setResultSetType(resultSetType);
/* 2002 */     rs.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 2004 */     return rs;
/*      */   }
/*      */ 
/*      */   private ResultSet buildResultSetWithUpdates(Statement callingStatement, Buffer resultPacket)
/*      */     throws SQLException
/*      */   {
/* 2010 */     long updateCount = -1L;
/* 2011 */     long updateID = -1L;
/* 2012 */     String info = null;
/*      */     try
/*      */     {
/* 2015 */       if (this.useNewUpdateCounts) {
/* 2016 */         updateCount = resultPacket.newReadLength();
/* 2017 */         updateID = resultPacket.newReadLength();
/*      */       } else {
/* 2019 */         updateCount = resultPacket.readLength();
/* 2020 */         updateID = resultPacket.readLength();
/*      */       }
/*      */ 
/* 2023 */       if (this.use41Extensions) {
/* 2024 */         this.serverStatus = resultPacket.readInt();
/*      */ 
/* 2026 */         this.warningCount = resultPacket.readInt();
/*      */ 
/* 2028 */         if (this.warningCount > 0) {
/* 2029 */           this.hadWarnings = true;
/*      */         }
/*      */ 
/* 2032 */         resultPacket.readByte();
/*      */ 
/* 2034 */         if (this.profileSql) {
/* 2035 */           this.queryNoIndexUsed = ((this.serverStatus & 0x10) != 0);
/*      */ 
/* 2037 */           this.queryBadIndexUsed = ((this.serverStatus & 0x20) != 0);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2042 */       if (this.connection.isReadInfoMsgEnabled())
/* 2043 */         info = resultPacket.readString();
/*      */     }
/*      */     catch (Exception ex) {
/* 2046 */       throw new SQLException(SQLError.get("S1000") + ": " + ex.getClass().getName(), "S1000", -1);
/*      */     }
/*      */ 
/* 2051 */     ResultSet updateRs = new ResultSet(updateCount, updateID, this.connection, callingStatement);
/*      */ 
/* 2054 */     if (info != null) {
/* 2055 */       updateRs.setServerInfo(info);
/*      */     }
/*      */ 
/* 2058 */     return updateRs;
/*      */   }
/*      */ 
/*      */   private void checkForOutstandingStreamingData() throws SQLException {
/* 2062 */     if (this.streamingData != null) {
/* 2063 */       if (!this.connection.getClobberStreamingResults()) {
/* 2064 */         throw new SQLException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"));
/*      */       }
/*      */ 
/* 2072 */       this.streamingData.getOwner().realClose(false);
/*      */ 
/* 2075 */       clearInputStream();
/*      */     }
/*      */   }
/*      */ 
/*      */   private Buffer compressPacket(Buffer packet, int offset, int packetLen, int headerLength) throws SQLException
/*      */   {
/* 2081 */     packet.writeLongInt(packetLen - headerLength);
/* 2082 */     packet.writeByte(0);
/*      */ 
/* 2084 */     int lengthToWrite = 0;
/* 2085 */     int compressedLength = 0;
/* 2086 */     byte[] bytesToCompress = packet.getByteBuffer();
/* 2087 */     byte[] compressedBytes = null;
/* 2088 */     int offsetWrite = 0;
/*      */ 
/* 2090 */     if (packetLen < 50) {
/* 2091 */       lengthToWrite = packetLen;
/* 2092 */       compressedBytes = packet.getByteBuffer();
/* 2093 */       compressedLength = 0;
/* 2094 */       offsetWrite = offset;
/*      */     } else {
/* 2096 */       compressedBytes = new byte[bytesToCompress.length * 2];
/*      */ 
/* 2098 */       this.deflater.reset();
/* 2099 */       this.deflater.setInput(bytesToCompress, offset, packetLen);
/* 2100 */       this.deflater.finish();
/*      */ 
/* 2102 */       int compLen = this.deflater.deflate(compressedBytes);
/*      */ 
/* 2104 */       if (compLen > packetLen) {
/* 2105 */         lengthToWrite = packetLen;
/* 2106 */         compressedBytes = packet.getByteBuffer();
/* 2107 */         compressedLength = 0;
/* 2108 */         offsetWrite = offset;
/*      */       } else {
/* 2110 */         lengthToWrite = compLen;
/* 2111 */         headerLength += 3;
/* 2112 */         compressedLength = packetLen;
/*      */       }
/*      */     }
/*      */ 
/* 2116 */     Buffer compressedPacket = Buffer.allocateNew(packetLen + headerLength, this.useNewIo);
/*      */ 
/* 2119 */     compressedPacket.setPosition(0);
/* 2120 */     compressedPacket.writeLongInt(lengthToWrite);
/* 2121 */     compressedPacket.writeByte(this.packetSequence);
/* 2122 */     compressedPacket.writeLongInt(compressedLength);
/* 2123 */     compressedPacket.writeBytesNoNull(compressedBytes, offsetWrite, lengthToWrite);
/*      */ 
/* 2126 */     return compressedPacket;
/*      */   }
/*      */ 
/*      */   private final void readServerStatusForResultSets(Buffer rowPacket) throws SQLException
/*      */   {
/* 2131 */     if (this.use41Extensions) {
/* 2132 */       rowPacket.readByte();
/*      */ 
/* 2134 */       this.warningCount = rowPacket.readInt();
/*      */ 
/* 2136 */       if (this.warningCount > 0) {
/* 2137 */         this.hadWarnings = true;
/*      */       }
/*      */ 
/* 2140 */       this.serverStatus = rowPacket.readInt();
/*      */ 
/* 2142 */       if (this.profileSql) {
/* 2143 */         this.queryNoIndexUsed = ((this.serverStatus & 0x10) != 0);
/*      */ 
/* 2145 */         this.queryBadIndexUsed = ((this.serverStatus & 0x20) != 0);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private SocketFactory createSocketFactory() throws SQLException
/*      */   {
/*      */     try {
/* 2153 */       if (this.socketFactoryClassName == null) {
/* 2154 */         throw new SQLException(Messages.getString("MysqlIO.75"), "08001");
/*      */       }
/*      */ 
/* 2158 */       return (SocketFactory)Class.forName(this.socketFactoryClassName).newInstance();
/*      */     }
/*      */     catch (Exception ex) {
/* 2161 */       if (this.connection.getParanoid()) tmpTernaryOp = ""; 
/* 2161 */     }throw new SQLException(Messages.getString("MysqlIO.76") + this.socketFactoryClassName + Messages.getString("MysqlIO.77") + ex.toString() + Util.stackTraceToString(ex), "08001");
/*      */   }
/*      */ 
/*      */   private void enqueuePacketForDebugging(boolean isPacketBeingSent, boolean isPacketReused, int sendLength, byte[] header, Buffer packet)
/*      */     throws SQLException
/*      */   {
/* 2173 */     if (this.packetDebugRingBuffer.size() + 1 > this.connection.getPacketDebugBufferSize()) {
/* 2174 */       this.packetDebugRingBuffer.removeFirst();
/*      */     }
/*      */ 
/* 2177 */     StringBuffer packetDump = null;
/*      */ 
/* 2179 */     if (!isPacketBeingSent) {
/* 2180 */       int bytesToDump = Math.min(1024, packet.getBufLength());
/*      */ 
/* 2183 */       Buffer packetToDump = Buffer.allocateNew(4 + bytesToDump, false);
/*      */ 
/* 2185 */       packetToDump.setPosition(0);
/* 2186 */       packetToDump.writeBytesNoNull(header);
/* 2187 */       packetToDump.writeBytesNoNull(packet.getBytes(0, bytesToDump));
/*      */ 
/* 2189 */       String packetPayload = packetToDump.dump(bytesToDump);
/*      */ 
/* 2191 */       packetDump = new StringBuffer(96 + packetPayload.length());
/*      */ 
/* 2193 */       packetDump.append("Server ");
/*      */ 
/* 2195 */       if (isPacketReused)
/* 2196 */         packetDump.append("(re-used)");
/*      */       else {
/* 2198 */         packetDump.append("(new)");
/*      */       }
/*      */ 
/* 2201 */       packetDump.append(" ");
/* 2202 */       packetDump.append(packet.toSuperString());
/* 2203 */       packetDump.append(" --------------------> Client\n");
/* 2204 */       packetDump.append("\nPacket payload:\n\n");
/* 2205 */       packetDump.append(packetPayload);
/*      */ 
/* 2207 */       if (bytesToDump == 1024) {
/* 2208 */         packetDump.append("\nNote: Packet of " + packet.getBufLength() + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 2213 */       int bytesToDump = Math.min(1024, sendLength);
/*      */ 
/* 2215 */       String packetPayload = packet.dump(bytesToDump);
/*      */ 
/* 2217 */       packetDump = new StringBuffer(68 + packetPayload.length());
/*      */ 
/* 2219 */       packetDump.append("Client ");
/* 2220 */       packetDump.append(packet.toSuperString());
/* 2221 */       packetDump.append("--------------------> Server\n");
/* 2222 */       packetDump.append("\nPacket payload:\n\n");
/* 2223 */       packetDump.append(packetPayload);
/*      */ 
/* 2225 */       if (bytesToDump == 1024) {
/* 2226 */         packetDump.append("\nNote: Packet of " + sendLength + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2232 */     this.packetDebugRingBuffer.addLast(packetDump);
/*      */   }
/*      */ 
/*      */   private void readChannelFully(ByteBuffer buf, int length) throws IOException
/*      */   {
/* 2237 */     int n = 0;
/*      */ 
/* 2239 */     while (n < length) {
/* 2240 */       int count = this.socketChannel.read(buf);
/*      */ 
/* 2242 */       if (count < 0) {
/* 2243 */         throw new EOFException();
/*      */       }
/*      */ 
/* 2246 */       n += count;
/*      */ 
/* 2248 */       buf.position(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   private RowData readSingleRowSet(long columnCount, int maxRows, int resultSetConcurrency, boolean isBinaryEncoded, Field[] fields)
/*      */     throws SQLException
/*      */   {
/* 2256 */     ArrayList rows = new ArrayList();
/*      */ 
/* 2259 */     Object rowBytes = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency);
/*      */ 
/* 2262 */     int rowCount = 0;
/*      */ 
/* 2264 */     if (rowBytes != null) {
/* 2265 */       rows.add(rowBytes);
/* 2266 */       rowCount = 1;
/*      */     }
/*      */ 
/* 2269 */     while (rowBytes != null) {
/* 2270 */       rowBytes = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency);
/*      */ 
/* 2273 */       if ((rowBytes == null) || (
/* 2274 */         (maxRows != -1) && (rowCount >= maxRows))) continue;
/* 2275 */       rows.add(rowBytes);
/* 2276 */       rowCount++;
/*      */     }
/*      */ 
/* 2281 */     RowData rowData = new RowDataStatic(rows);
/*      */ 
/* 2283 */     return rowData;
/*      */   }
/*      */ 
/*      */   private Buffer readViaChannel() throws IOException, SQLException {
/* 2287 */     Buffer packet = Buffer.allocateNew(16384, true);
/* 2288 */     packet.setPosition(0);
/*      */ 
/* 2290 */     packet.setBufLength(4);
/*      */ 
/* 2292 */     ByteBuffer lenBuf = packet.getNioBuffer();
/*      */ 
/* 2294 */     readChannelFully(lenBuf, 4);
/*      */ 
/* 2296 */     byte b1 = lenBuf.get(0);
/* 2297 */     byte b2 = lenBuf.get(1);
/* 2298 */     byte b3 = lenBuf.get(2);
/*      */ 
/* 2300 */     int packetLength = (b1 & 0xFF) + ((b2 & 0xFF) << 8) + ((b3 & 0xFF) << 16);
/*      */ 
/* 2304 */     if (packetLength == -65793) {
/* 2305 */       forceClose();
/* 2306 */       throw new IOException(Messages.getString("MysqlIO.79"));
/*      */     }
/*      */ 
/* 2309 */     packet.ensureCapacity(packetLength + 1);
/* 2310 */     packet.setBufLength(packetLength);
/* 2311 */     packet.setPosition(0);
/* 2312 */     this.socketChannel.read(packet.getNioBuffer());
/*      */ 
/* 2314 */     packet.setBufLength(packetLength + 1);
/* 2315 */     packet.setPosition(packetLength);
/* 2316 */     packet.writeByte(0);
/* 2317 */     packet.setPosition(0);
/*      */ 
/* 2319 */     return packet;
/*      */   }
/*      */ 
/*      */   private void reclaimLargeReusablePacket()
/*      */   {
/* 2326 */     if ((this.reusablePacket != null) && (this.reusablePacket.getCapacity() > 1048576))
/*      */     {
/* 2328 */       this.reusablePacket = Buffer.allocateNew(this.connection.getNetBufferLength(), this.useNewIo); }  } 
/*      */   // ERROR //
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse) throws SQLException { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 30	com/mysql/jdbc/MysqlIO:useNewIo	Z
/*      */     //   4: ifne +1018 -> 1022
/*      */     //   7: aload_1
/*      */     //   8: iconst_0
/*      */     //   9: invokevirtual 407	com/mysql/jdbc/Buffer:setWasMultiPacket	(Z)V
/*      */     //   12: aload_0
/*      */     //   13: aload_0
/*      */     //   14: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   17: aload_0
/*      */     //   18: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   21: iconst_0
/*      */     //   22: iconst_4
/*      */     //   23: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   26: istore_2
/*      */     //   27: iload_2
/*      */     //   28: iconst_4
/*      */     //   29: if_icmpge +21 -> 50
/*      */     //   32: aload_0
/*      */     //   33: invokevirtual 112	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   36: new 94	java/io/IOException
/*      */     //   39: dup
/*      */     //   40: ldc_w 408
/*      */     //   43: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   46: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   49: athrow
/*      */     //   50: aload_0
/*      */     //   51: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   54: iconst_0
/*      */     //   55: baload
/*      */     //   56: sipush 255
/*      */     //   59: iand
/*      */     //   60: aload_0
/*      */     //   61: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   64: iconst_1
/*      */     //   65: baload
/*      */     //   66: sipush 255
/*      */     //   69: iand
/*      */     //   70: bipush 8
/*      */     //   72: ishl
/*      */     //   73: iadd
/*      */     //   74: aload_0
/*      */     //   75: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   78: iconst_2
/*      */     //   79: baload
/*      */     //   80: sipush 255
/*      */     //   83: iand
/*      */     //   84: bipush 16
/*      */     //   86: ishl
/*      */     //   87: iadd
/*      */     //   88: istore_3
/*      */     //   89: aload_0
/*      */     //   90: getfield 49	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   93: ifeq +74 -> 167
/*      */     //   96: new 116	java/lang/StringBuffer
/*      */     //   99: dup
/*      */     //   100: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   103: astore 4
/*      */     //   105: aload 4
/*      */     //   107: ldc_w 409
/*      */     //   110: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   113: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   116: pop
/*      */     //   117: aload 4
/*      */     //   119: iload_3
/*      */     //   120: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   123: pop
/*      */     //   124: aload 4
/*      */     //   126: ldc_w 410
/*      */     //   129: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   132: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   135: pop
/*      */     //   136: aload 4
/*      */     //   138: aload_0
/*      */     //   139: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   142: iconst_4
/*      */     //   143: invokestatic 122	com/mysql/jdbc/StringUtils:dumpAsHex	([BI)Ljava/lang/String;
/*      */     //   146: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   149: pop
/*      */     //   150: aload_0
/*      */     //   151: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   154: invokevirtual 69	com/mysql/jdbc/Connection:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   157: aload 4
/*      */     //   159: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   162: invokeinterface 124 2 0
/*      */     //   167: aload_0
/*      */     //   168: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   171: iconst_3
/*      */     //   172: baload
/*      */     //   173: istore 4
/*      */     //   175: aload_0
/*      */     //   176: getfield 3	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   179: ifne +26 -> 205
/*      */     //   182: aload_0
/*      */     //   183: getfield 50	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   186: ifeq +24 -> 210
/*      */     //   189: aload_0
/*      */     //   190: getfield 35	com/mysql/jdbc/MysqlIO:checkPacketSequence	Z
/*      */     //   193: ifeq +17 -> 210
/*      */     //   196: aload_0
/*      */     //   197: iload 4
/*      */     //   199: invokespecial 125	com/mysql/jdbc/MysqlIO:checkPacketSequencing	(B)V
/*      */     //   202: goto +8 -> 210
/*      */     //   205: aload_0
/*      */     //   206: iconst_0
/*      */     //   207: putfield 3	com/mysql/jdbc/MysqlIO:packetSequenceReset	Z
/*      */     //   210: aload_0
/*      */     //   211: iload 4
/*      */     //   213: putfield 34	com/mysql/jdbc/MysqlIO:readPacketSequence	B
/*      */     //   216: aload_1
/*      */     //   217: iconst_0
/*      */     //   218: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   221: aload_1
/*      */     //   222: invokevirtual 100	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   225: arraylength
/*      */     //   226: iload_3
/*      */     //   227: if_icmpgt +12 -> 239
/*      */     //   230: aload_1
/*      */     //   231: iload_3
/*      */     //   232: iconst_1
/*      */     //   233: iadd
/*      */     //   234: newarray byte
/*      */     //   236: invokevirtual 411	com/mysql/jdbc/Buffer:setByteBuffer	([B)V
/*      */     //   239: aload_1
/*      */     //   240: iload_3
/*      */     //   241: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   244: aload_0
/*      */     //   245: aload_0
/*      */     //   246: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   249: aload_1
/*      */     //   250: invokevirtual 100	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   253: iconst_0
/*      */     //   254: iload_3
/*      */     //   255: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   258: istore 5
/*      */     //   260: iload 5
/*      */     //   262: iload_3
/*      */     //   263: if_icmpeq +40 -> 303
/*      */     //   266: new 94	java/io/IOException
/*      */     //   269: dup
/*      */     //   270: new 116	java/lang/StringBuffer
/*      */     //   273: dup
/*      */     //   274: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   277: ldc 126
/*      */     //   279: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   282: iload_3
/*      */     //   283: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   286: ldc 127
/*      */     //   288: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   291: iload 5
/*      */     //   293: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   296: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   299: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   302: athrow
/*      */     //   303: aload_0
/*      */     //   304: getfield 49	com/mysql/jdbc/MysqlIO:traceProtocol	Z
/*      */     //   307: ifeq +52 -> 359
/*      */     //   310: new 116	java/lang/StringBuffer
/*      */     //   313: dup
/*      */     //   314: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   317: astore 6
/*      */     //   319: aload 6
/*      */     //   321: ldc_w 412
/*      */     //   324: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   327: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   330: pop
/*      */     //   331: aload 6
/*      */     //   333: aload_1
/*      */     //   334: iload_3
/*      */     //   335: invokestatic 131	com/mysql/jdbc/MysqlIO:getPacketDumpToLog	(Lcom/mysql/jdbc/Buffer;I)Ljava/lang/String;
/*      */     //   338: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   341: pop
/*      */     //   342: aload_0
/*      */     //   343: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   346: invokevirtual 69	com/mysql/jdbc/Connection:getLog	()Lcom/mysql/jdbc/log/Log;
/*      */     //   349: aload 6
/*      */     //   351: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   354: invokeinterface 124 2 0
/*      */     //   359: aload_0
/*      */     //   360: getfield 50	com/mysql/jdbc/MysqlIO:enablePacketDebug	Z
/*      */     //   363: ifeq +15 -> 378
/*      */     //   366: aload_0
/*      */     //   367: iconst_0
/*      */     //   368: iconst_1
/*      */     //   369: iconst_0
/*      */     //   370: aload_0
/*      */     //   371: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   374: aload_1
/*      */     //   375: invokespecial 132	com/mysql/jdbc/MysqlIO:enqueuePacketForDebugging	(ZZI[BLcom/mysql/jdbc/Buffer;)V
/*      */     //   378: iconst_0
/*      */     //   379: istore 6
/*      */     //   381: iload_3
/*      */     //   382: aload_0
/*      */     //   383: getfield 40	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   386: if_icmpne +566 -> 952
/*      */     //   389: aload_1
/*      */     //   390: aload_0
/*      */     //   391: getfield 40	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   394: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   397: iload_3
/*      */     //   398: istore 7
/*      */     //   400: iconst_1
/*      */     //   401: istore 6
/*      */     //   403: aload_0
/*      */     //   404: aload_0
/*      */     //   405: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   408: aload_0
/*      */     //   409: iconst_4
/*      */     //   410: newarray byte
/*      */     //   412: dup_x1
/*      */     //   413: putfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   416: iconst_0
/*      */     //   417: iconst_4
/*      */     //   418: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   421: istore_2
/*      */     //   422: iload_2
/*      */     //   423: iconst_4
/*      */     //   424: if_icmpge +21 -> 445
/*      */     //   427: aload_0
/*      */     //   428: invokevirtual 112	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   431: new 94	java/io/IOException
/*      */     //   434: dup
/*      */     //   435: ldc_w 413
/*      */     //   438: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   441: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   444: athrow
/*      */     //   445: aload_0
/*      */     //   446: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   449: iconst_0
/*      */     //   450: baload
/*      */     //   451: sipush 255
/*      */     //   454: iand
/*      */     //   455: aload_0
/*      */     //   456: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   459: iconst_1
/*      */     //   460: baload
/*      */     //   461: sipush 255
/*      */     //   464: iand
/*      */     //   465: bipush 8
/*      */     //   467: ishl
/*      */     //   468: iadd
/*      */     //   469: aload_0
/*      */     //   470: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   473: iconst_2
/*      */     //   474: baload
/*      */     //   475: sipush 255
/*      */     //   478: iand
/*      */     //   479: bipush 16
/*      */     //   481: ishl
/*      */     //   482: iadd
/*      */     //   483: istore_3
/*      */     //   484: iload_3
/*      */     //   485: aload_0
/*      */     //   486: getfield 30	com/mysql/jdbc/MysqlIO:useNewIo	Z
/*      */     //   489: invokestatic 59	com/mysql/jdbc/Buffer:allocateNew	(IZ)Lcom/mysql/jdbc/Buffer;
/*      */     //   492: astore 8
/*      */     //   494: iconst_1
/*      */     //   495: istore 9
/*      */     //   497: iload 9
/*      */     //   499: ifne +87 -> 586
/*      */     //   502: aload_0
/*      */     //   503: aload_0
/*      */     //   504: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   507: aload_0
/*      */     //   508: iconst_4
/*      */     //   509: newarray byte
/*      */     //   511: dup_x1
/*      */     //   512: putfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   515: iconst_0
/*      */     //   516: iconst_4
/*      */     //   517: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   520: istore_2
/*      */     //   521: iload_2
/*      */     //   522: iconst_4
/*      */     //   523: if_icmpge +21 -> 544
/*      */     //   526: aload_0
/*      */     //   527: invokevirtual 112	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   530: new 94	java/io/IOException
/*      */     //   533: dup
/*      */     //   534: ldc_w 414
/*      */     //   537: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   540: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   543: athrow
/*      */     //   544: aload_0
/*      */     //   545: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   548: iconst_0
/*      */     //   549: baload
/*      */     //   550: sipush 255
/*      */     //   553: iand
/*      */     //   554: aload_0
/*      */     //   555: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   558: iconst_1
/*      */     //   559: baload
/*      */     //   560: sipush 255
/*      */     //   563: iand
/*      */     //   564: bipush 8
/*      */     //   566: ishl
/*      */     //   567: iadd
/*      */     //   568: aload_0
/*      */     //   569: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   572: iconst_2
/*      */     //   573: baload
/*      */     //   574: sipush 255
/*      */     //   577: iand
/*      */     //   578: bipush 16
/*      */     //   580: ishl
/*      */     //   581: iadd
/*      */     //   582: istore_3
/*      */     //   583: goto +6 -> 589
/*      */     //   586: iconst_0
/*      */     //   587: istore 9
/*      */     //   589: aload_0
/*      */     //   590: getfield 31	com/mysql/jdbc/MysqlIO:useNewLargePackets	Z
/*      */     //   593: ifne +15 -> 608
/*      */     //   596: iload_3
/*      */     //   597: iconst_1
/*      */     //   598: if_icmpne +10 -> 608
/*      */     //   601: aload_0
/*      */     //   602: invokevirtual 275	com/mysql/jdbc/MysqlIO:clearInputStream	()V
/*      */     //   605: goto +337 -> 942
/*      */     //   608: iload_3
/*      */     //   609: aload_0
/*      */     //   610: getfield 40	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   613: if_icmpge +166 -> 779
/*      */     //   616: aload_0
/*      */     //   617: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   620: iconst_3
/*      */     //   621: baload
/*      */     //   622: istore 10
/*      */     //   624: iload 10
/*      */     //   626: iload 4
/*      */     //   628: iconst_1
/*      */     //   629: iadd
/*      */     //   630: if_icmpeq +17 -> 647
/*      */     //   633: new 94	java/io/IOException
/*      */     //   636: dup
/*      */     //   637: ldc_w 415
/*      */     //   640: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   643: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   646: athrow
/*      */     //   647: iload 10
/*      */     //   649: istore 4
/*      */     //   651: aload 8
/*      */     //   653: iconst_0
/*      */     //   654: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   657: aload 8
/*      */     //   659: iload_3
/*      */     //   660: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   663: aload 8
/*      */     //   665: invokevirtual 100	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   668: astore 11
/*      */     //   670: iload_3
/*      */     //   671: istore 12
/*      */     //   673: aload_0
/*      */     //   674: aload_0
/*      */     //   675: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   678: aload 11
/*      */     //   680: iconst_0
/*      */     //   681: iload_3
/*      */     //   682: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   685: istore 13
/*      */     //   687: iload 13
/*      */     //   689: iload 12
/*      */     //   691: if_icmpeq +69 -> 760
/*      */     //   694: new 95	com/mysql/jdbc/CommunicationsException
/*      */     //   697: dup
/*      */     //   698: aload_0
/*      */     //   699: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   702: aload_0
/*      */     //   703: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   706: new 190	java/sql/SQLException
/*      */     //   709: dup
/*      */     //   710: new 116	java/lang/StringBuffer
/*      */     //   713: dup
/*      */     //   714: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   717: ldc_w 416
/*      */     //   720: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   723: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   726: iload 12
/*      */     //   728: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   731: ldc_w 417
/*      */     //   734: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   737: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   740: iload 13
/*      */     //   742: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   745: ldc 202
/*      */     //   747: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   750: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   753: invokespecial 269	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*      */     //   756: invokespecial 96	com/mysql/jdbc/CommunicationsException:<init>	(Lcom/mysql/jdbc/Connection;JLjava/lang/Exception;)V
/*      */     //   759: athrow
/*      */     //   760: aload_1
/*      */     //   761: aload 11
/*      */     //   763: iconst_0
/*      */     //   764: iload 12
/*      */     //   766: invokevirtual 367	com/mysql/jdbc/Buffer:writeBytesNoNull	([BII)V
/*      */     //   769: iload 7
/*      */     //   771: iload 12
/*      */     //   773: iadd
/*      */     //   774: istore 7
/*      */     //   776: goto +166 -> 942
/*      */     //   779: aload_0
/*      */     //   780: getfield 17	com/mysql/jdbc/MysqlIO:packetHeaderBuf	[B
/*      */     //   783: iconst_3
/*      */     //   784: baload
/*      */     //   785: istore 10
/*      */     //   787: iload 10
/*      */     //   789: iload 4
/*      */     //   791: iconst_1
/*      */     //   792: iadd
/*      */     //   793: if_icmpeq +17 -> 810
/*      */     //   796: new 94	java/io/IOException
/*      */     //   799: dup
/*      */     //   800: ldc_w 418
/*      */     //   803: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   806: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   809: athrow
/*      */     //   810: iload 10
/*      */     //   812: istore 4
/*      */     //   814: aload 8
/*      */     //   816: iconst_0
/*      */     //   817: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   820: aload 8
/*      */     //   822: iload_3
/*      */     //   823: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   826: aload 8
/*      */     //   828: invokevirtual 100	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   831: astore 11
/*      */     //   833: iload_3
/*      */     //   834: istore 12
/*      */     //   836: aload_0
/*      */     //   837: aload_0
/*      */     //   838: getfield 9	com/mysql/jdbc/MysqlIO:mysqlInput	Ljava/io/InputStream;
/*      */     //   841: aload 11
/*      */     //   843: iconst_0
/*      */     //   844: iload_3
/*      */     //   845: invokespecial 111	com/mysql/jdbc/MysqlIO:readFully	(Ljava/io/InputStream;[BII)I
/*      */     //   848: istore 13
/*      */     //   850: iload 13
/*      */     //   852: iload 12
/*      */     //   854: if_icmpeq +69 -> 923
/*      */     //   857: new 95	com/mysql/jdbc/CommunicationsException
/*      */     //   860: dup
/*      */     //   861: aload_0
/*      */     //   862: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   865: aload_0
/*      */     //   866: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   869: new 190	java/sql/SQLException
/*      */     //   872: dup
/*      */     //   873: new 116	java/lang/StringBuffer
/*      */     //   876: dup
/*      */     //   877: invokespecial 117	java/lang/StringBuffer:<init>	()V
/*      */     //   880: ldc_w 419
/*      */     //   883: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   886: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   889: iload 12
/*      */     //   891: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   894: ldc_w 420
/*      */     //   897: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   900: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   903: iload 13
/*      */     //   905: invokevirtual 120	java/lang/StringBuffer:append	(I)Ljava/lang/StringBuffer;
/*      */     //   908: ldc 202
/*      */     //   910: invokevirtual 119	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   913: invokevirtual 123	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   916: invokespecial 269	java/sql/SQLException:<init>	(Ljava/lang/String;)V
/*      */     //   919: invokespecial 96	com/mysql/jdbc/CommunicationsException:<init>	(Lcom/mysql/jdbc/Connection;JLjava/lang/Exception;)V
/*      */     //   922: athrow
/*      */     //   923: aload_1
/*      */     //   924: aload 11
/*      */     //   926: iconst_0
/*      */     //   927: iload 12
/*      */     //   929: invokevirtual 367	com/mysql/jdbc/Buffer:writeBytesNoNull	([BII)V
/*      */     //   932: iload 7
/*      */     //   934: iload 12
/*      */     //   936: iadd
/*      */     //   937: istore 7
/*      */     //   939: goto -442 -> 497
/*      */     //   942: aload_1
/*      */     //   943: iconst_0
/*      */     //   944: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   947: aload_1
/*      */     //   948: iconst_1
/*      */     //   949: invokevirtual 407	com/mysql/jdbc/Buffer:setWasMultiPacket	(Z)V
/*      */     //   952: iload 6
/*      */     //   954: ifne +10 -> 964
/*      */     //   957: aload_1
/*      */     //   958: invokevirtual 100	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   961: iload_3
/*      */     //   962: iconst_0
/*      */     //   963: bastore
/*      */     //   964: aload_1
/*      */     //   965: areturn
/*      */     //   966: astore_2
/*      */     //   967: new 95	com/mysql/jdbc/CommunicationsException
/*      */     //   970: dup
/*      */     //   971: aload_0
/*      */     //   972: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   975: aload_0
/*      */     //   976: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   979: aload_2
/*      */     //   980: invokespecial 96	com/mysql/jdbc/CommunicationsException:<init>	(Lcom/mysql/jdbc/Connection;JLjava/lang/Exception;)V
/*      */     //   983: athrow
/*      */     //   984: astore_2
/*      */     //   985: aload_0
/*      */     //   986: invokevirtual 275	com/mysql/jdbc/MysqlIO:clearInputStream	()V
/*      */     //   989: jsr +14 -> 1003
/*      */     //   992: goto +30 -> 1022
/*      */     //   995: astore 14
/*      */     //   997: jsr +6 -> 1003
/*      */     //   1000: aload 14
/*      */     //   1002: athrow
/*      */     //   1003: astore 15
/*      */     //   1005: aload_0
/*      */     //   1006: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   1009: iconst_0
/*      */     //   1010: iconst_0
/*      */     //   1011: iconst_1
/*      */     //   1012: aload_2
/*      */     //   1013: invokevirtual 135	com/mysql/jdbc/Connection:realClose	(ZZZLjava/lang/Throwable;)V
/*      */     //   1016: aload_2
/*      */     //   1017: athrow
/*      */     //   1018: astore 16
/*      */     //   1020: aload_2
/*      */     //   1021: athrow
/*      */     //   1022: aload_0
/*      */     //   1023: aload_1
/*      */     //   1024: invokespecial 421	com/mysql/jdbc/MysqlIO:reuseAndReadViaChannel	(Lcom/mysql/jdbc/Buffer;)Lcom/mysql/jdbc/Buffer;
/*      */     //   1027: areturn
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	965	966	java/io/IOException
/*      */     //   7	965	984	java/lang/OutOfMemoryError
/*      */     //   985	992	995	finally
/*      */     //   995	1000	995	finally
/*      */     //   1005	1016	1018	finally
/*      */     //   1018	1020	1018	finally } 
/* 2586 */   private void checkPacketSequencing(byte multiPacketSeq) throws CommunicationsException { if ((multiPacketSeq == -128) && (this.readPacketSequence != 127)) {
/* 2587 */       throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, new IOException("Packets out of order, expected packet # -128, but received packet # " + multiPacketSeq));
/*      */     }
/*      */ 
/* 2593 */     if ((this.readPacketSequence == -1) && (multiPacketSeq != 0)) {
/* 2594 */       throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, new IOException("Packets out of order, expected packet # -1, but received packet # " + multiPacketSeq));
/*      */     }
/*      */ 
/* 2600 */     if ((multiPacketSeq != -128) && (this.readPacketSequence != -1) && (multiPacketSeq != this.readPacketSequence + 1))
/*      */     {
/* 2602 */       throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, new IOException("Packets out of order, expected packet # " + (this.readPacketSequence + 1) + ", but received packet # " + multiPacketSeq));
/*      */     }
/*      */   }
/*      */ 
/*      */   final void send(Buffer packet)
/*      */     throws SQLException
/*      */   {
/* 2618 */     int l = packet.getPosition();
/* 2619 */     send(packet, l);
/*      */ 
/* 2624 */     if (packet == this.sharedSendPacket)
/* 2625 */       reclaimLargeSharedSendPacket();
/*      */   }
/*      */ 
/*      */   private final void send(Buffer packet, int packetLen) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2632 */       if (packetLen > this.maxAllowedPacket) {
/* 2633 */         throw new PacketTooBigException(packetLen, this.maxAllowedPacket);
/*      */       }
/*      */ 
/* 2636 */       if (this.connection.getMaintainTimeStats()) {
/* 2637 */         this.lastPacketSentTimeMs = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 2640 */       if ((this.serverMajorVersion >= 4) && (packetLen >= this.maxThreeBytes))
/*      */       {
/* 2642 */         if (!this.useNewIo)
/* 2643 */           sendSplitPackets(packet);
/*      */         else
/* 2645 */           sendSplitPacketsViaChannel(packet);
/*      */       }
/*      */       else {
/* 2648 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 2650 */         Buffer packetToSend = packet;
/*      */ 
/* 2652 */         packetToSend.setPosition(0);
/*      */ 
/* 2654 */         if (this.useCompression) {
/* 2655 */           int originalPacketLen = packetLen;
/*      */ 
/* 2657 */           packetToSend = compressPacket(packet, 0, packetLen, 4);
/*      */ 
/* 2659 */           packetLen = packetToSend.getPosition();
/*      */ 
/* 2661 */           if (this.traceProtocol) {
/* 2662 */             StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 2664 */             traceMessageBuf.append(Messages.getString("MysqlIO.57"));
/* 2665 */             traceMessageBuf.append(getPacketDumpToLog(packetToSend, packetLen));
/*      */ 
/* 2667 */             traceMessageBuf.append(Messages.getString("MysqlIO.58"));
/* 2668 */             traceMessageBuf.append(getPacketDumpToLog(packet, originalPacketLen));
/*      */ 
/* 2671 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           }
/*      */         } else {
/* 2674 */           packetToSend.writeLongInt(packetLen - 4);
/* 2675 */           packetToSend.writeByte(this.packetSequence);
/*      */ 
/* 2677 */           if (this.traceProtocol) {
/* 2678 */             StringBuffer traceMessageBuf = new StringBuffer();
/*      */ 
/* 2680 */             traceMessageBuf.append(Messages.getString("MysqlIO.59"));
/* 2681 */             traceMessageBuf.append(packetToSend.dump(packetLen));
/*      */ 
/* 2683 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           }
/*      */         }
/*      */ 
/* 2687 */         if (!this.useNewIo) {
/* 2688 */           this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */ 
/* 2690 */           this.mysqlOutput.flush();
/*      */         } else {
/* 2692 */           sendViaChannel(packetToSend, packetLen);
/*      */         }
/*      */       }
/*      */ 
/* 2696 */       if (this.enablePacketDebug) {
/* 2697 */         enqueuePacketForDebugging(true, false, packetLen + 5, this.packetHeaderBuf, packet);
/*      */       }
/*      */ 
/* 2704 */       if (packet == this.sharedSendPacket)
/* 2705 */         reclaimLargeSharedSendPacket();
/*      */     }
/*      */     catch (IOException ioEx) {
/* 2708 */       throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   private final ResultSet sendFileToServer(Statement callingStatement, String fileName)
/*      */     throws SQLException
/*      */   {
/* 2726 */     Buffer filePacket = this.loadFileBufRef == null ? null : (Buffer)this.loadFileBufRef.get();
/*      */ 
/* 2729 */     int bigPacketLength = Math.min(this.connection.getMaxAllowedPacket() - 12, alignPacketSize(this.connection.getMaxAllowedPacket() - 16, 4096) - 12);
/*      */ 
/* 2734 */     int oneMeg = 1048576;
/*      */ 
/* 2736 */     int smallerPacketSizeAligned = Math.min(oneMeg - 12, alignPacketSize(oneMeg - 16, 4096) - 12);
/*      */ 
/* 2739 */     int packetLength = Math.min(smallerPacketSizeAligned, bigPacketLength);
/*      */ 
/* 2741 */     if (filePacket == null) {
/*      */       try {
/* 2743 */         filePacket = Buffer.allocateNew(packetLength + 4, this.useNewIo);
/*      */ 
/* 2745 */         this.loadFileBufRef = new SoftReference(filePacket);
/*      */       } catch (OutOfMemoryError oom) {
/* 2747 */         throw new SQLException("Could not allocate packet of " + packetLength + " bytes required for LOAD DATA LOCAL INFILE operation." + " Try increasing max heap allocation for JVM or decreasing server variable " + "'max_allowed_packet'", "S1001");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2755 */     filePacket.clear();
/* 2756 */     send(filePacket, 0);
/*      */ 
/* 2758 */     byte[] fileBuf = new byte[packetLength];
/*      */ 
/* 2760 */     BufferedInputStream fileIn = null;
/*      */     try
/*      */     {
/* 2763 */       if (!this.connection.getAllowUrlInLocalInfile()) {
/* 2764 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       }
/* 2767 */       else if (fileName.indexOf(":") != -1) {
/*      */         try {
/* 2769 */           URL urlFromFileName = new URL(fileName);
/* 2770 */           fileIn = new BufferedInputStream(urlFromFileName.openStream());
/*      */         }
/*      */         catch (MalformedURLException badUrlEx) {
/* 2773 */           fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */         }
/*      */       }
/*      */       else {
/* 2777 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       }
/*      */ 
/* 2782 */       int bytesRead = 0;
/*      */ 
/* 2784 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 2785 */         filePacket.clear();
/* 2786 */         filePacket.writeBytesNoNull(fileBuf, 0, bytesRead);
/* 2787 */         send(filePacket);
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 2790 */       StringBuffer messageBuf = new StringBuffer(Messages.getString("MysqlIO.60"));
/*      */ 
/* 2793 */       if (!this.connection.getParanoid()) {
/* 2794 */         messageBuf.append("'");
/*      */ 
/* 2796 */         if (fileName != null) {
/* 2797 */           messageBuf.append(fileName);
/*      */         }
/*      */ 
/* 2800 */         messageBuf.append("'");
/*      */       }
/*      */ 
/* 2803 */       messageBuf.append(Messages.getString("MysqlIO.63"));
/*      */ 
/* 2805 */       if (!this.connection.getParanoid()) {
/* 2806 */         messageBuf.append(Messages.getString("MysqlIO.64"));
/* 2807 */         messageBuf.append(Util.stackTraceToString(ioEx));
/*      */       }
/*      */ 
/* 2810 */       throw new SQLException(messageBuf.toString(), "S1009");
/*      */     }
/*      */     finally {
/* 2813 */       if (fileIn != null) {
/*      */         try {
/* 2815 */           fileIn.close();
/*      */         } catch (Exception ex) {
/* 2817 */           throw new SQLException(Messages.getString("MysqlIO.65"), "S1000");
/*      */         }
/*      */ 
/* 2821 */         fileIn = null;
/*      */       }
/*      */       else {
/* 2824 */         filePacket.clear();
/* 2825 */         send(filePacket);
/* 2826 */         checkErrorPacket();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2831 */     filePacket.clear();
/* 2832 */     send(filePacket);
/*      */ 
/* 2834 */     Buffer resultPacket = checkErrorPacket();
/*      */ 
/* 2836 */     return buildResultSetWithUpdates(callingStatement, resultPacket);
/*      */   }
/*      */ 
/*      */   private Buffer checkErrorPacket(int command)
/*      */     throws SQLException
/*      */   {
/* 2851 */     int statusCode = 0;
/* 2852 */     Buffer resultPacket = null;
/* 2853 */     this.serverStatus = 0;
/*      */     try
/*      */     {
/* 2860 */       resultPacket = reuseAndReadPacket(this.reusablePacket);
/* 2861 */       statusCode = resultPacket.readByte();
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 2864 */       throw sqlEx;
/*      */     } catch (Exception fallThru) {
/* 2866 */       throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, fallThru);
/*      */     }
/*      */ 
/* 2871 */     if (statusCode == -1)
/*      */     {
/* 2873 */       int errno = 2000;
/*      */ 
/* 2875 */       if (this.protocolVersion > 9) {
/* 2876 */         errno = resultPacket.readInt();
/*      */ 
/* 2878 */         String xOpen = null;
/*      */ 
/* 2880 */         String serverErrorMessage = resultPacket.readString();
/*      */ 
/* 2882 */         if (serverErrorMessage.startsWith("#"))
/*      */         {
/* 2885 */           if (serverErrorMessage.length() > 6) {
/* 2886 */             xOpen = serverErrorMessage.substring(1, 6);
/* 2887 */             serverErrorMessage = serverErrorMessage.substring(6);
/*      */ 
/* 2889 */             if (xOpen.equals("HY000"))
/* 2890 */               xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           }
/*      */           else
/*      */           {
/* 2894 */             xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           }
/*      */         }
/*      */         else {
/* 2898 */           xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */         }
/*      */ 
/* 2902 */         clearInputStream();
/*      */ 
/* 2904 */         StringBuffer errorBuf = new StringBuffer();
/*      */ 
/* 2906 */         String xOpenErrorMessage = SQLError.get(xOpen);
/*      */ 
/* 2908 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 2909 */           (xOpenErrorMessage != null)) {
/* 2910 */           errorBuf.append(xOpenErrorMessage);
/* 2911 */           errorBuf.append(Messages.getString("MysqlIO.68"));
/*      */         }
/*      */ 
/* 2915 */         errorBuf.append(serverErrorMessage);
/*      */ 
/* 2917 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 2918 */           (xOpenErrorMessage != null)) {
/* 2919 */           errorBuf.append("\"");
/*      */         }
/*      */ 
/* 2923 */         if ((xOpen != null) && (xOpen.startsWith("22"))) {
/* 2924 */           throw new MysqlDataTruncation(errorBuf.toString(), 0, true, false, 0, 0);
/*      */         }
/* 2926 */         throw new SQLException(errorBuf.toString(), xOpen, errno);
/*      */       }
/*      */ 
/* 2930 */       String serverErrorMessage = resultPacket.readString();
/* 2931 */       clearInputStream();
/*      */ 
/* 2933 */       if (serverErrorMessage.indexOf(Messages.getString("MysqlIO.70")) != -1) {
/* 2934 */         throw new SQLException(SQLError.get("S0022") + ", " + serverErrorMessage, "S0022", -1);
/*      */       }
/*      */ 
/* 2941 */       StringBuffer errorBuf = new StringBuffer(Messages.getString("MysqlIO.72"));
/*      */ 
/* 2943 */       errorBuf.append(serverErrorMessage);
/* 2944 */       errorBuf.append("\"");
/*      */ 
/* 2946 */       throw new SQLException(SQLError.get("S1000") + ", " + errorBuf.toString(), "S1000", -1);
/*      */     }
/*      */ 
/* 2951 */     return resultPacket;
/*      */   }
/*      */ 
/*      */   private final void sendSplitPackets(Buffer packet)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2974 */       Buffer headerPacket = this.splitBufRef == null ? null : (Buffer)this.splitBufRef.get();
/*      */ 
/* 2982 */       if (headerPacket == null) {
/* 2983 */         headerPacket = Buffer.allocateNew(this.maxThreeBytes + 4, this.useNewIo);
/*      */ 
/* 2985 */         this.splitBufRef = new SoftReference(headerPacket);
/*      */       }
/*      */ 
/* 2988 */       int len = packet.getPosition();
/* 2989 */       int splitSize = this.maxThreeBytes;
/* 2990 */       int originalPacketPos = 4;
/* 2991 */       byte[] origPacketBytes = packet.getByteBuffer();
/* 2992 */       byte[] headerPacketBytes = headerPacket.getByteBuffer();
/*      */ 
/* 2994 */       while (len >= this.maxThreeBytes) {
/* 2995 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 2997 */         headerPacket.setPosition(0);
/* 2998 */         headerPacket.writeLongInt(splitSize);
/*      */ 
/* 3000 */         headerPacket.writeByte(this.packetSequence);
/* 3001 */         System.arraycopy(origPacketBytes, originalPacketPos, headerPacketBytes, 4, splitSize);
/*      */ 
/* 3004 */         int packetLen = splitSize + 4;
/*      */ 
/* 3010 */         if (!this.useCompression) {
/* 3011 */           this.mysqlOutput.write(headerPacketBytes, 0, splitSize + 4);
/*      */ 
/* 3013 */           this.mysqlOutput.flush();
/*      */         }
/*      */         else
/*      */         {
/* 3017 */           headerPacket.setPosition(0);
/* 3018 */           Buffer packetToSend = compressPacket(headerPacket, 4, splitSize, 4);
/*      */ 
/* 3020 */           packetLen = packetToSend.getPosition();
/*      */ 
/* 3022 */           this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */ 
/* 3024 */           this.mysqlOutput.flush();
/*      */         }
/*      */ 
/* 3027 */         originalPacketPos += splitSize;
/* 3028 */         len -= splitSize;
/*      */       }
/*      */ 
/* 3034 */       headerPacket.clear();
/* 3035 */       headerPacket.setPosition(0);
/* 3036 */       headerPacket.writeLongInt(len - 4);
/* 3037 */       this.packetSequence = (byte)(this.packetSequence + 1);
/* 3038 */       headerPacket.writeByte(this.packetSequence);
/*      */ 
/* 3040 */       if (len != 0) {
/* 3041 */         System.arraycopy(origPacketBytes, originalPacketPos, headerPacketBytes, 4, len - 4);
/*      */       }
/*      */ 
/* 3045 */       int packetLen = len - 4;
/*      */ 
/* 3051 */       if (!this.useCompression) {
/* 3052 */         this.mysqlOutput.write(headerPacket.getByteBuffer(), 0, len);
/* 3053 */         this.mysqlOutput.flush();
/*      */       }
/*      */       else
/*      */       {
/* 3057 */         headerPacket.setPosition(0);
/* 3058 */         Buffer packetToSend = compressPacket(headerPacket, 4, packetLen, 4);
/*      */ 
/* 3060 */         packetLen = packetToSend.getPosition();
/*      */ 
/* 3062 */         this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/*      */ 
/* 3064 */         this.mysqlOutput.flush();
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3067 */       throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void sendSplitPacketsViaChannel(Buffer packet)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3092 */       Buffer headerPacket = this.splitBufRef == null ? null : (Buffer)this.splitBufRef.get();
/*      */ 
/* 3100 */       if (headerPacket == null) {
/* 3101 */         headerPacket = Buffer.allocateNew(this.maxThreeBytes + 4, this.useNewIo);
/*      */ 
/* 3103 */         this.splitBufRef = new SoftReference(headerPacket);
/*      */       }
/*      */ 
/* 3106 */       int len = packet.getPosition();
/* 3107 */       int splitSize = this.maxThreeBytes;
/* 3108 */       int originalPacketPos = 4;
/* 3109 */       byte[] origPacketBytes = packet.getByteBuffer();
/*      */ 
/* 3111 */       while (len >= this.maxThreeBytes) {
/* 3112 */         this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3114 */         headerPacket.setPosition(0);
/* 3115 */         headerPacket.writeLongInt(splitSize);
/*      */ 
/* 3117 */         headerPacket.writeByte(this.packetSequence);
/*      */ 
/* 3119 */         headerPacket.setPosition(4);
/* 3120 */         headerPacket.writeBytesNoNull(origPacketBytes, originalPacketPos, splitSize);
/*      */ 
/* 3123 */         int packetLen = splitSize + 4;
/*      */ 
/* 3129 */         if (!this.useCompression) {
/* 3130 */           headerPacket.getNioBuffer().limit(splitSize + 4);
/*      */ 
/* 3132 */           headerPacket.setPosition(0);
/*      */ 
/* 3134 */           this.socketChannel.write(headerPacket.getNioBuffer());
/*      */         }
/*      */         else
/*      */         {
/* 3138 */           headerPacket.setPosition(0);
/* 3139 */           Buffer packetToSend = compressPacket(headerPacket, 4, splitSize, 4);
/*      */ 
/* 3141 */           packetLen = packetToSend.getPosition();
/*      */ 
/* 3143 */           packetToSend.setPosition(0);
/* 3144 */           packetToSend.getNioBuffer().limit(packetLen);
/* 3145 */           this.socketChannel.write(packetToSend.getNioBuffer());
/*      */         }
/*      */ 
/* 3148 */         originalPacketPos += splitSize;
/* 3149 */         len -= splitSize;
/*      */       }
/*      */ 
/* 3155 */       headerPacket.clear();
/* 3156 */       headerPacket.setPosition(0);
/* 3157 */       headerPacket.writeLongInt(len - 4);
/* 3158 */       this.packetSequence = (byte)(this.packetSequence + 1);
/* 3159 */       headerPacket.writeByte(this.packetSequence);
/*      */ 
/* 3161 */       if (len != 0) {
/* 3162 */         headerPacket.setPosition(4);
/* 3163 */         headerPacket.writeBytesNoNull(origPacketBytes, originalPacketPos, len - 4);
/*      */       }
/*      */ 
/* 3167 */       int packetLen = len - 4;
/*      */ 
/* 3173 */       if (!this.useCompression) {
/* 3174 */         headerPacket.getNioBuffer().limit(len);
/* 3175 */         headerPacket.setPosition(0);
/*      */ 
/* 3177 */         this.socketChannel.write(headerPacket.getNioBuffer());
/*      */       }
/*      */       else
/*      */       {
/* 3181 */         headerPacket.setPosition(0);
/* 3182 */         Buffer packetToSend = compressPacket(headerPacket, 4, packetLen, 4);
/*      */ 
/* 3184 */         packetLen = packetToSend.getPosition();
/*      */ 
/* 3186 */         packetToSend.setPosition(0);
/* 3187 */         packetToSend.getNioBuffer().limit(packetLen);
/*      */ 
/* 3189 */         this.socketChannel.write(packetToSend.getNioBuffer());
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3192 */       throw new CommunicationsException(this.connection, this.lastPacketSentTimeMs, ioEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reclaimLargeSharedSendPacket()
/*      */   {
/* 3198 */     if ((this.sharedSendPacket != null) && (this.sharedSendPacket.getCapacity() > 1048576))
/*      */     {
/* 3200 */       this.sharedSendPacket = Buffer.allocateNew(this.connection.getNetBufferLength(), this.useNewIo); }  } 
/*      */   // ERROR //
/*      */   private Buffer reuseAndReadViaChannel(Buffer reuse) throws SQLException { // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: iconst_0
/*      */     //   2: invokevirtual 407	com/mysql/jdbc/Buffer:setWasMultiPacket	(Z)V
/*      */     //   5: aload_1
/*      */     //   6: iconst_0
/*      */     //   7: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   10: aload_1
/*      */     //   11: iconst_4
/*      */     //   12: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   15: aload_1
/*      */     //   16: invokevirtual 400	com/mysql/jdbc/Buffer:getNioBuffer	()Ljava/nio/ByteBuffer;
/*      */     //   19: astore_2
/*      */     //   20: aload_0
/*      */     //   21: aload_2
/*      */     //   22: iconst_4
/*      */     //   23: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   26: aload_2
/*      */     //   27: iconst_0
/*      */     //   28: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   31: istore_3
/*      */     //   32: aload_2
/*      */     //   33: iconst_1
/*      */     //   34: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   37: istore 4
/*      */     //   39: aload_2
/*      */     //   40: iconst_2
/*      */     //   41: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   44: istore 5
/*      */     //   46: iload_3
/*      */     //   47: sipush 255
/*      */     //   50: iand
/*      */     //   51: iload 4
/*      */     //   53: sipush 255
/*      */     //   56: iand
/*      */     //   57: bipush 8
/*      */     //   59: ishl
/*      */     //   60: iadd
/*      */     //   61: iload 5
/*      */     //   63: sipush 255
/*      */     //   66: iand
/*      */     //   67: bipush 16
/*      */     //   69: ishl
/*      */     //   70: iadd
/*      */     //   71: istore 6
/*      */     //   73: iload 6
/*      */     //   75: ldc_w 403
/*      */     //   78: if_icmpne +21 -> 99
/*      */     //   81: aload_0
/*      */     //   82: invokevirtual 112	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   85: new 94	java/io/IOException
/*      */     //   88: dup
/*      */     //   89: ldc_w 484
/*      */     //   92: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   95: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   98: athrow
/*      */     //   99: aload_2
/*      */     //   100: iconst_3
/*      */     //   101: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   104: istore 7
/*      */     //   106: aload_1
/*      */     //   107: iconst_0
/*      */     //   108: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   111: aload_1
/*      */     //   112: iload 6
/*      */     //   114: iconst_1
/*      */     //   115: iadd
/*      */     //   116: invokevirtual 405	com/mysql/jdbc/Buffer:ensureCapacity	(I)V
/*      */     //   119: aload_1
/*      */     //   120: iload 6
/*      */     //   122: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   125: aload_0
/*      */     //   126: aload_1
/*      */     //   127: invokevirtual 400	com/mysql/jdbc/Buffer:getNioBuffer	()Ljava/nio/ByteBuffer;
/*      */     //   130: iload 6
/*      */     //   132: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   135: aload_1
/*      */     //   136: iconst_0
/*      */     //   137: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   140: iconst_0
/*      */     //   141: istore 8
/*      */     //   143: iload 6
/*      */     //   145: aload_0
/*      */     //   146: getfield 40	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   149: if_icmpne +462 -> 611
/*      */     //   152: aload_1
/*      */     //   153: aload_0
/*      */     //   154: getfield 40	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   157: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   160: iload 6
/*      */     //   162: istore 9
/*      */     //   164: iconst_1
/*      */     //   165: istore 8
/*      */     //   167: iconst_4
/*      */     //   168: iconst_1
/*      */     //   169: invokestatic 59	com/mysql/jdbc/Buffer:allocateNew	(IZ)Lcom/mysql/jdbc/Buffer;
/*      */     //   172: astore 10
/*      */     //   174: aload 10
/*      */     //   176: invokevirtual 400	com/mysql/jdbc/Buffer:getNioBuffer	()Ljava/nio/ByteBuffer;
/*      */     //   179: astore_2
/*      */     //   180: aload_2
/*      */     //   181: iconst_0
/*      */     //   182: invokevirtual 393	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*      */     //   185: pop
/*      */     //   186: aload_0
/*      */     //   187: aload_2
/*      */     //   188: iconst_4
/*      */     //   189: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   192: aload_2
/*      */     //   193: iconst_0
/*      */     //   194: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   197: istore_3
/*      */     //   198: aload_2
/*      */     //   199: iconst_1
/*      */     //   200: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   203: istore 4
/*      */     //   205: aload_2
/*      */     //   206: iconst_2
/*      */     //   207: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   210: istore 5
/*      */     //   212: iload_3
/*      */     //   213: sipush 255
/*      */     //   216: iand
/*      */     //   217: iload 4
/*      */     //   219: sipush 255
/*      */     //   222: iand
/*      */     //   223: bipush 8
/*      */     //   225: ishl
/*      */     //   226: iadd
/*      */     //   227: iload 5
/*      */     //   229: sipush 255
/*      */     //   232: iand
/*      */     //   233: bipush 16
/*      */     //   235: ishl
/*      */     //   236: iadd
/*      */     //   237: istore 6
/*      */     //   239: iload 6
/*      */     //   241: ldc_w 403
/*      */     //   244: if_icmpne +21 -> 265
/*      */     //   247: aload_0
/*      */     //   248: invokevirtual 112	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   251: new 94	java/io/IOException
/*      */     //   254: dup
/*      */     //   255: ldc_w 485
/*      */     //   258: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   261: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   264: athrow
/*      */     //   265: iload 6
/*      */     //   267: aload_0
/*      */     //   268: getfield 30	com/mysql/jdbc/MysqlIO:useNewIo	Z
/*      */     //   271: invokestatic 59	com/mysql/jdbc/Buffer:allocateNew	(IZ)Lcom/mysql/jdbc/Buffer;
/*      */     //   274: astore 11
/*      */     //   276: iconst_1
/*      */     //   277: istore 12
/*      */     //   279: iload 12
/*      */     //   281: ifne +88 -> 369
/*      */     //   284: aload_2
/*      */     //   285: iconst_0
/*      */     //   286: invokevirtual 393	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*      */     //   289: pop
/*      */     //   290: aload_0
/*      */     //   291: aload_2
/*      */     //   292: iconst_4
/*      */     //   293: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   296: aload_2
/*      */     //   297: iconst_0
/*      */     //   298: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   301: istore_3
/*      */     //   302: aload_2
/*      */     //   303: iconst_1
/*      */     //   304: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   307: istore 4
/*      */     //   309: aload_2
/*      */     //   310: iconst_2
/*      */     //   311: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   314: istore 5
/*      */     //   316: iload_3
/*      */     //   317: sipush 255
/*      */     //   320: iand
/*      */     //   321: iload 4
/*      */     //   323: sipush 255
/*      */     //   326: iand
/*      */     //   327: bipush 8
/*      */     //   329: ishl
/*      */     //   330: iadd
/*      */     //   331: iload 5
/*      */     //   333: sipush 255
/*      */     //   336: iand
/*      */     //   337: bipush 16
/*      */     //   339: ishl
/*      */     //   340: iadd
/*      */     //   341: istore 6
/*      */     //   343: iload 6
/*      */     //   345: ldc_w 403
/*      */     //   348: if_icmpne +24 -> 372
/*      */     //   351: aload_0
/*      */     //   352: invokevirtual 112	com/mysql/jdbc/MysqlIO:forceClose	()V
/*      */     //   355: new 94	java/io/IOException
/*      */     //   358: dup
/*      */     //   359: ldc_w 486
/*      */     //   362: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   365: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   368: athrow
/*      */     //   369: iconst_0
/*      */     //   370: istore 12
/*      */     //   372: aload_0
/*      */     //   373: getfield 31	com/mysql/jdbc/MysqlIO:useNewLargePackets	Z
/*      */     //   376: ifne +16 -> 392
/*      */     //   379: iload 6
/*      */     //   381: iconst_1
/*      */     //   382: if_icmpne +10 -> 392
/*      */     //   385: aload_0
/*      */     //   386: invokevirtual 275	com/mysql/jdbc/MysqlIO:clearInputStream	()V
/*      */     //   389: goto +212 -> 601
/*      */     //   392: iload 6
/*      */     //   394: aload_0
/*      */     //   395: getfield 40	com/mysql/jdbc/MysqlIO:maxThreeBytes	I
/*      */     //   398: if_icmpge +103 -> 501
/*      */     //   401: aload_2
/*      */     //   402: iconst_0
/*      */     //   403: invokevirtual 393	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*      */     //   406: pop
/*      */     //   407: aload_0
/*      */     //   408: aload_2
/*      */     //   409: iconst_1
/*      */     //   410: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   413: aload_2
/*      */     //   414: iconst_0
/*      */     //   415: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   418: istore 13
/*      */     //   420: iload 13
/*      */     //   422: iload 7
/*      */     //   424: iconst_1
/*      */     //   425: iadd
/*      */     //   426: if_icmpeq +17 -> 443
/*      */     //   429: new 94	java/io/IOException
/*      */     //   432: dup
/*      */     //   433: ldc_w 487
/*      */     //   436: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   439: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   442: athrow
/*      */     //   443: iload 13
/*      */     //   445: istore 7
/*      */     //   447: aload 11
/*      */     //   449: iconst_0
/*      */     //   450: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   453: aload 11
/*      */     //   455: iload 6
/*      */     //   457: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   460: iload 6
/*      */     //   462: istore 14
/*      */     //   464: aload_0
/*      */     //   465: aload 11
/*      */     //   467: invokevirtual 400	com/mysql/jdbc/Buffer:getNioBuffer	()Ljava/nio/ByteBuffer;
/*      */     //   470: iload 6
/*      */     //   472: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   475: aload 11
/*      */     //   477: invokevirtual 100	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   480: astore 15
/*      */     //   482: aload_1
/*      */     //   483: aload 15
/*      */     //   485: iconst_0
/*      */     //   486: iload 14
/*      */     //   488: invokevirtual 367	com/mysql/jdbc/Buffer:writeBytesNoNull	([BII)V
/*      */     //   491: iload 9
/*      */     //   493: iload 14
/*      */     //   495: iadd
/*      */     //   496: istore 9
/*      */     //   498: goto +103 -> 601
/*      */     //   501: aload_2
/*      */     //   502: iconst_0
/*      */     //   503: invokevirtual 393	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
/*      */     //   506: pop
/*      */     //   507: aload_0
/*      */     //   508: aload_2
/*      */     //   509: iconst_1
/*      */     //   510: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   513: aload_2
/*      */     //   514: iconst_0
/*      */     //   515: invokevirtual 402	java/nio/ByteBuffer:get	(I)B
/*      */     //   518: istore 13
/*      */     //   520: iload 13
/*      */     //   522: iload 7
/*      */     //   524: iconst_1
/*      */     //   525: iadd
/*      */     //   526: if_icmpeq +17 -> 543
/*      */     //   529: new 94	java/io/IOException
/*      */     //   532: dup
/*      */     //   533: ldc_w 488
/*      */     //   536: invokestatic 114	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   539: invokespecial 115	java/io/IOException:<init>	(Ljava/lang/String;)V
/*      */     //   542: athrow
/*      */     //   543: iload 13
/*      */     //   545: istore 7
/*      */     //   547: aload 11
/*      */     //   549: iconst_0
/*      */     //   550: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   553: aload 11
/*      */     //   555: iload 6
/*      */     //   557: invokevirtual 129	com/mysql/jdbc/Buffer:setBufLength	(I)V
/*      */     //   560: iload 6
/*      */     //   562: istore 14
/*      */     //   564: aload_0
/*      */     //   565: aload 11
/*      */     //   567: invokevirtual 400	com/mysql/jdbc/Buffer:getNioBuffer	()Ljava/nio/ByteBuffer;
/*      */     //   570: iload 6
/*      */     //   572: invokespecial 401	com/mysql/jdbc/MysqlIO:readChannelFully	(Ljava/nio/ByteBuffer;I)V
/*      */     //   575: aload 11
/*      */     //   577: invokevirtual 100	com/mysql/jdbc/Buffer:getByteBuffer	()[B
/*      */     //   580: astore 15
/*      */     //   582: aload_1
/*      */     //   583: aload 15
/*      */     //   585: iconst_0
/*      */     //   586: iload 14
/*      */     //   588: invokevirtual 367	com/mysql/jdbc/Buffer:writeBytesNoNull	([BII)V
/*      */     //   591: iload 9
/*      */     //   593: iload 14
/*      */     //   595: iadd
/*      */     //   596: istore 9
/*      */     //   598: goto -319 -> 279
/*      */     //   601: aload_1
/*      */     //   602: iconst_0
/*      */     //   603: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   606: aload_1
/*      */     //   607: iconst_1
/*      */     //   608: invokevirtual 407	com/mysql/jdbc/Buffer:setWasMultiPacket	(Z)V
/*      */     //   611: iload 8
/*      */     //   613: ifne +14 -> 627
/*      */     //   616: aload_1
/*      */     //   617: iload 6
/*      */     //   619: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   622: aload_1
/*      */     //   623: iconst_0
/*      */     //   624: invokevirtual 149	com/mysql/jdbc/Buffer:writeByte	(B)V
/*      */     //   627: aload_1
/*      */     //   628: iconst_0
/*      */     //   629: invokevirtual 211	com/mysql/jdbc/Buffer:setPosition	(I)V
/*      */     //   632: aload_1
/*      */     //   633: areturn
/*      */     //   634: astore_2
/*      */     //   635: new 95	com/mysql/jdbc/CommunicationsException
/*      */     //   638: dup
/*      */     //   639: aload_0
/*      */     //   640: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   643: aload_0
/*      */     //   644: getfield 48	com/mysql/jdbc/MysqlIO:lastPacketSentTimeMs	J
/*      */     //   647: aload_2
/*      */     //   648: invokespecial 96	com/mysql/jdbc/CommunicationsException:<init>	(Lcom/mysql/jdbc/Connection;JLjava/lang/Exception;)V
/*      */     //   651: athrow
/*      */     //   652: astore_2
/*      */     //   653: aload_0
/*      */     //   654: invokevirtual 275	com/mysql/jdbc/MysqlIO:clearInputStream	()V
/*      */     //   657: jsr +14 -> 671
/*      */     //   660: goto +30 -> 690
/*      */     //   663: astore 16
/*      */     //   665: jsr +6 -> 671
/*      */     //   668: aload 16
/*      */     //   670: athrow
/*      */     //   671: astore 17
/*      */     //   673: aload_0
/*      */     //   674: getfield 51	com/mysql/jdbc/MysqlIO:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   677: iconst_0
/*      */     //   678: iconst_0
/*      */     //   679: iconst_1
/*      */     //   680: aload_2
/*      */     //   681: invokevirtual 135	com/mysql/jdbc/Connection:realClose	(ZZZLjava/lang/Throwable;)V
/*      */     //   684: aload_2
/*      */     //   685: athrow
/*      */     //   686: astore 18
/*      */     //   688: aload_2
/*      */     //   689: athrow
/*      */     //   690: goto +0 -> 690
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	633	634	java/io/IOException
/*      */     //   0	633	652	java/lang/OutOfMemoryError
/*      */     //   653	660	663	finally
/*      */     //   663	668	663	finally
/*      */     //   673	684	686	finally
/*      */     //   686	688	686	finally } 
/* 3406 */   private void scanForAndThrowDataTruncation() throws SQLException { if ((this.streamingData == null) && (versionMeetsMinimum(4, 1, 0)) && (this.connection.getJdbcCompliantTruncation()))
/*      */     {
/* 3408 */       SQLError.convertShowWarningsToSQLWarnings(this.connection, this.warningCount, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void secureAuth(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 3429 */     if (packet == null) {
/* 3430 */       packet = Buffer.allocateNew(packLength, this.useNewIo);
/*      */     }
/*      */ 
/* 3433 */     if (writeClientParams) {
/* 3434 */       if (this.use41Extensions) {
/* 3435 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 3436 */           packet.writeLong(this.clientParam);
/* 3437 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 3442 */           packet.writeByte(8);
/*      */ 
/* 3445 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 3447 */           packet.writeLong(this.clientParam);
/* 3448 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 3451 */         packet.writeInt((int)this.clientParam);
/* 3452 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3457 */     packet.writeString(user);
/*      */ 
/* 3459 */     if (password.length() != 0)
/*      */     {
/* 3461 */       packet.writeString("xxxxxxxx");
/*      */     }
/*      */     else {
/* 3464 */       packet.writeString("");
/*      */     }
/*      */ 
/* 3467 */     if (this.useConnectWithDb) {
/* 3468 */       packet.writeString(database);
/*      */     }
/*      */ 
/* 3471 */     send(packet);
/*      */ 
/* 3476 */     if (password.length() > 0) {
/* 3477 */       Buffer b = readPacket();
/*      */ 
/* 3479 */       b.setPosition(0);
/*      */ 
/* 3481 */       byte[] replyAsBytes = b.getByteBuffer();
/*      */ 
/* 3483 */       if ((replyAsBytes.length == 25) && (replyAsBytes[0] != 0))
/*      */       {
/* 3485 */         if (replyAsBytes[0] != 42) {
/*      */           try
/*      */           {
/* 3488 */             byte[] buff = Security.passwordHashStage1(password);
/*      */ 
/* 3491 */             byte[] passwordHash = new byte[buff.length];
/* 3492 */             System.arraycopy(buff, 0, passwordHash, 0, buff.length);
/*      */ 
/* 3495 */             passwordHash = Security.passwordHashStage2(passwordHash, replyAsBytes);
/*      */ 
/* 3498 */             byte[] packetDataAfterSalt = new byte[replyAsBytes.length - 5];
/*      */ 
/* 3501 */             System.arraycopy(replyAsBytes, 4, packetDataAfterSalt, 0, replyAsBytes.length - 5);
/*      */ 
/* 3504 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/* 3507 */             Security.passwordCrypt(packetDataAfterSalt, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/* 3511 */             Security.passwordCrypt(mysqlScrambleBuff, buff, buff, 20);
/*      */ 
/* 3513 */             Buffer packet2 = Buffer.allocateNew(25, this.useNewIo);
/* 3514 */             packet2.writeBytesNoNull(buff);
/*      */ 
/* 3516 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3518 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 3520 */             throw new SQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000");
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*      */           try
/*      */           {
/* 3527 */             byte[] passwordHash = Security.createKeyFromOldPassword(password);
/*      */ 
/* 3530 */             byte[] netReadPos4 = new byte[replyAsBytes.length - 5];
/*      */ 
/* 3532 */             System.arraycopy(replyAsBytes, 4, netReadPos4, 0, replyAsBytes.length - 5);
/*      */ 
/* 3535 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */ 
/* 3538 */             Security.passwordCrypt(netReadPos4, mysqlScrambleBuff, passwordHash, 20);
/*      */ 
/* 3542 */             String scrambledPassword = Util.scramble(new String(mysqlScrambleBuff), password);
/*      */ 
/* 3545 */             Buffer packet2 = Buffer.allocateNew(packLength, this.useNewIo);
/*      */ 
/* 3547 */             packet2.writeString(scrambledPassword);
/* 3548 */             this.packetSequence = (byte)(this.packetSequence + 1);
/*      */ 
/* 3550 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 3552 */             throw new SQLException(Messages.getString("MysqlIO.93") + Messages.getString("MysqlIO.94"), "S1000");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void secureAuth411(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 3594 */     if (packet == null) {
/* 3595 */       packet = Buffer.allocateNew(packLength, this.useNewIo);
/*      */     }
/*      */ 
/* 3598 */     if (writeClientParams) {
/* 3599 */       if (this.use41Extensions) {
/* 3600 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 3601 */           packet.writeLong(this.clientParam);
/* 3602 */           packet.writeLong(this.maxThreeBytes);
/*      */ 
/* 3607 */           packet.writeByte(8);
/*      */ 
/* 3610 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 3612 */           packet.writeLong(this.clientParam);
/* 3613 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 3616 */         packet.writeInt((int)this.clientParam);
/* 3617 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3622 */     packet.writeString(user);
/*      */ 
/* 3624 */     if (password.length() != 0) {
/* 3625 */       packet.writeByte(20);
/*      */       try
/*      */       {
/* 3628 */         packet.writeBytesNoNull(Security.scramble411(password, this.seed));
/*      */       } catch (NoSuchAlgorithmException nse) {
/* 3630 */         throw new SQLException(Messages.getString("MysqlIO.95") + Messages.getString("MysqlIO.96"), "S1000");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 3636 */       packet.writeByte(0);
/*      */     }
/*      */ 
/* 3639 */     if (this.useConnectWithDb) {
/* 3640 */       packet.writeString(database);
/*      */     }
/*      */ 
/* 3643 */     send(packet);
/*      */ 
/* 3645 */     byte savePacketSequence = this.packetSequence++;
/*      */ 
/* 3647 */     Buffer reply = checkErrorPacket();
/*      */ 
/* 3649 */     if (reply.isLastDataPacket())
/*      */     {
/* 3654 */       savePacketSequence = (byte)(savePacketSequence + 1); this.packetSequence = savePacketSequence;
/* 3655 */       packet.clear();
/*      */ 
/* 3657 */       String seed323 = this.seed.substring(0, 8);
/* 3658 */       packet.writeString(Util.newCrypt(password, seed323));
/* 3659 */       send(packet);
/*      */ 
/* 3662 */       checkErrorPacket();
/*      */     }
/*      */   }
/*      */ 
/*      */   private final Object[] unpackBinaryResultSetRow(Field[] fields, Buffer binaryData, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 3679 */     int numFields = fields.length;
/*      */ 
/* 3681 */     Object[] unpackedRowData = new Object[numFields];
/*      */ 
/* 3688 */     int nullCount = (numFields + 9) / 8;
/*      */ 
/* 3690 */     byte[] nullBitMask = new byte[nullCount];
/*      */ 
/* 3692 */     for (int i = 0; i < nullCount; i++) {
/* 3693 */       nullBitMask[i] = binaryData.readByte();
/*      */     }
/*      */ 
/* 3696 */     int nullMaskPos = 0;
/* 3697 */     int bit = 4;
/*      */ 
/* 3704 */     for (int i = 0; i < numFields; i++) {
/* 3705 */       if ((nullBitMask[nullMaskPos] & bit) != 0) {
/* 3706 */         unpackedRowData[i] = null;
/*      */       }
/* 3708 */       else if (resultSetConcurrency != 1008) {
/* 3709 */         extractNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       }
/*      */       else {
/* 3712 */         unpackNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       }
/*      */ 
/* 3717 */       if ((bit <<= 1 & 0xFF) == 0) {
/* 3718 */         bit = 1;
/*      */ 
/* 3720 */         nullMaskPos++;
/*      */       }
/*      */     }
/*      */ 
/* 3724 */     return unpackedRowData;
/*      */   }
/*      */ 
/*      */   private final void extractNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, Object[] unpackedRowData)
/*      */     throws SQLException
/*      */   {
/* 3730 */     Field curField = fields[columnIndex];
/*      */ 
/* 3732 */     switch (curField.getMysqlType()) {
/*      */     case 6:
/* 3734 */       break;
/*      */     case 1:
/* 3738 */       unpackedRowData[columnIndex] = { binaryData.readByte() };
/* 3739 */       break;
/*      */     case 2:
/*      */     case 13:
/* 3744 */       unpackedRowData[columnIndex] = binaryData.getBytes(2);
/* 3745 */       break;
/*      */     case 3:
/*      */     case 9:
/* 3749 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 3750 */       break;
/*      */     case 8:
/* 3753 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 3754 */       break;
/*      */     case 4:
/* 3757 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 3758 */       break;
/*      */     case 5:
/* 3761 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 3762 */       break;
/*      */     case 11:
/* 3765 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 3767 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/* 3769 */       break;
/*      */     case 10:
/* 3772 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 3774 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */ 
/* 3776 */       break;
/*      */     case 7:
/*      */     case 12:
/* 3779 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 3781 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/* 3782 */       break;
/*      */     case 0:
/*      */     case 15:
/*      */     case 246:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/*      */     case 253:
/*      */     case 254:
/*      */     case 255:
/* 3793 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 3795 */       break;
/*      */     case 16:
/* 3797 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 3799 */       break;
/*      */     default:
/* 3801 */       throw new SQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000");
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void unpackNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, Object[] unpackedRowData)
/*      */     throws SQLException
/*      */   {
/* 3813 */     Field curField = fields[columnIndex];
/*      */ 
/* 3815 */     switch (curField.getMysqlType()) {
/*      */     case 6:
/* 3817 */       break;
/*      */     case 1:
/* 3821 */       byte tinyVal = binaryData.readByte();
/*      */ 
/* 3823 */       if (!curField.isUnsigned()) {
/* 3824 */         unpackedRowData[columnIndex] = String.valueOf(tinyVal).getBytes();
/*      */       }
/*      */       else {
/* 3827 */         short unsignedTinyVal = (short)(tinyVal & 0xFF);
/*      */ 
/* 3829 */         unpackedRowData[columnIndex] = String.valueOf(unsignedTinyVal).getBytes();
/*      */       }
/*      */ 
/* 3833 */       break;
/*      */     case 2:
/*      */     case 13:
/* 3838 */       short shortVal = (short)binaryData.readInt();
/*      */ 
/* 3840 */       if (!curField.isUnsigned()) {
/* 3841 */         unpackedRowData[columnIndex] = String.valueOf(shortVal).getBytes();
/*      */       }
/*      */       else {
/* 3844 */         int unsignedShortVal = shortVal & 0xFFFF;
/*      */ 
/* 3846 */         unpackedRowData[columnIndex] = String.valueOf(unsignedShortVal).getBytes();
/*      */       }
/*      */ 
/* 3850 */       break;
/*      */     case 3:
/*      */     case 9:
/* 3855 */       int intVal = (int)binaryData.readLong();
/*      */ 
/* 3857 */       if (!curField.isUnsigned()) {
/* 3858 */         unpackedRowData[columnIndex] = String.valueOf(intVal).getBytes();
/*      */       }
/*      */       else {
/* 3861 */         long longVal = intVal & 0xFFFFFFFF;
/*      */ 
/* 3863 */         unpackedRowData[columnIndex] = String.valueOf(longVal).getBytes();
/*      */       }
/*      */ 
/* 3867 */       break;
/*      */     case 8:
/* 3871 */       long longVal = binaryData.readLongLong();
/*      */ 
/* 3873 */       if (!curField.isUnsigned()) {
/* 3874 */         unpackedRowData[columnIndex] = String.valueOf(longVal).getBytes();
/*      */       }
/*      */       else {
/* 3877 */         BigInteger asBigInteger = ResultSet.convertLongToUlong(longVal);
/*      */ 
/* 3879 */         unpackedRowData[columnIndex] = asBigInteger.toString().getBytes();
/*      */       }
/*      */ 
/* 3883 */       break;
/*      */     case 4:
/* 3887 */       float floatVal = Float.intBitsToFloat(binaryData.readIntAsLong());
/*      */ 
/* 3889 */       unpackedRowData[columnIndex] = String.valueOf(floatVal).getBytes();
/*      */ 
/* 3891 */       break;
/*      */     case 5:
/* 3895 */       double doubleVal = Double.longBitsToDouble(binaryData.readLongLong());
/*      */ 
/* 3897 */       unpackedRowData[columnIndex] = String.valueOf(doubleVal).getBytes();
/*      */ 
/* 3899 */       break;
/*      */     case 11:
/* 3903 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 3905 */       int hour = 0;
/* 3906 */       int minute = 0;
/* 3907 */       int seconds = 0;
/*      */ 
/* 3909 */       if (length != 0) {
/* 3910 */         binaryData.readByte();
/* 3911 */         binaryData.readLong();
/* 3912 */         hour = binaryData.readByte();
/* 3913 */         minute = binaryData.readByte();
/* 3914 */         seconds = binaryData.readByte();
/*      */ 
/* 3916 */         if (length > 8) {
/* 3917 */           binaryData.readLong();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 3922 */       byte[] timeAsBytes = new byte[8];
/*      */ 
/* 3924 */       timeAsBytes[0] = (byte)Character.forDigit(hour / 10, 10);
/* 3925 */       timeAsBytes[1] = (byte)Character.forDigit(hour % 10, 10);
/*      */ 
/* 3927 */       timeAsBytes[2] = 58;
/*      */ 
/* 3929 */       timeAsBytes[3] = (byte)Character.forDigit(minute / 10, 10);
/*      */ 
/* 3931 */       timeAsBytes[4] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/* 3934 */       timeAsBytes[5] = 58;
/*      */ 
/* 3936 */       timeAsBytes[6] = (byte)Character.forDigit(seconds / 10, 10);
/*      */ 
/* 3938 */       timeAsBytes[7] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/* 3941 */       unpackedRowData[columnIndex] = timeAsBytes;
/*      */ 
/* 3944 */       break;
/*      */     case 10:
/* 3947 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 3949 */       int year = 0;
/* 3950 */       int month = 0;
/* 3951 */       int day = 0;
/*      */ 
/* 3953 */       int hour = 0;
/* 3954 */       int minute = 0;
/* 3955 */       int seconds = 0;
/*      */ 
/* 3957 */       if (length != 0) {
/* 3958 */         year = binaryData.readInt();
/* 3959 */         month = binaryData.readByte();
/* 3960 */         day = binaryData.readByte();
/*      */       }
/*      */ 
/* 3963 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 3964 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 3966 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 3969 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 3971 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */           }
/*      */ 
/* 3975 */           year = 1;
/* 3976 */           month = 1;
/* 3977 */           day = 1;
/*      */         }
/*      */       }
/*      */       else {
/* 3981 */         byte[] dateAsBytes = new byte[10];
/*      */ 
/* 3983 */         dateAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/* 3986 */         int after1000 = year % 1000;
/*      */ 
/* 3988 */         dateAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/* 3991 */         int after100 = after1000 % 100;
/*      */ 
/* 3993 */         dateAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */ 
/* 3995 */         dateAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/* 3998 */         dateAsBytes[4] = 45;
/*      */ 
/* 4000 */         dateAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */ 
/* 4002 */         dateAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/* 4005 */         dateAsBytes[7] = 45;
/*      */ 
/* 4007 */         dateAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/* 4008 */         dateAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */ 
/* 4010 */         unpackedRowData[columnIndex] = dateAsBytes;
/*      */       }
/*      */ 
/* 4013 */       break;
/*      */     case 7:
/*      */     case 12:
/* 4017 */       int length = (int)binaryData.readFieldLength();
/*      */ 
/* 4019 */       int year = 0;
/* 4020 */       int month = 0;
/* 4021 */       int day = 0;
/*      */ 
/* 4023 */       int hour = 0;
/* 4024 */       int minute = 0;
/* 4025 */       int seconds = 0;
/*      */ 
/* 4027 */       int nanos = 0;
/*      */ 
/* 4029 */       if (length != 0) {
/* 4030 */         year = binaryData.readInt();
/* 4031 */         month = binaryData.readByte();
/* 4032 */         day = binaryData.readByte();
/*      */ 
/* 4034 */         if (length > 4) {
/* 4035 */           hour = binaryData.readByte();
/* 4036 */           minute = binaryData.readByte();
/* 4037 */           seconds = binaryData.readByte();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4045 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 4046 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 4048 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 4051 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */           {
/* 4053 */             throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */           }
/*      */ 
/* 4057 */           year = 1;
/* 4058 */           month = 1;
/* 4059 */           day = 1;
/*      */         }
/*      */       }
/*      */       else {
/* 4063 */         int stringLength = 19;
/*      */ 
/* 4065 */         byte[] nanosAsBytes = Integer.toString(nanos).getBytes();
/*      */ 
/* 4067 */         stringLength += 1 + nanosAsBytes.length;
/*      */ 
/* 4069 */         byte[] datetimeAsBytes = new byte[stringLength];
/*      */ 
/* 4071 */         datetimeAsBytes[0] = (byte)Character.forDigit(year / 1000, 10);
/*      */ 
/* 4074 */         int after1000 = year % 1000;
/*      */ 
/* 4076 */         datetimeAsBytes[1] = (byte)Character.forDigit(after1000 / 100, 10);
/*      */ 
/* 4079 */         int after100 = after1000 % 100;
/*      */ 
/* 4081 */         datetimeAsBytes[2] = (byte)Character.forDigit(after100 / 10, 10);
/*      */ 
/* 4083 */         datetimeAsBytes[3] = (byte)Character.forDigit(after100 % 10, 10);
/*      */ 
/* 4086 */         datetimeAsBytes[4] = 45;
/*      */ 
/* 4088 */         datetimeAsBytes[5] = (byte)Character.forDigit(month / 10, 10);
/*      */ 
/* 4090 */         datetimeAsBytes[6] = (byte)Character.forDigit(month % 10, 10);
/*      */ 
/* 4093 */         datetimeAsBytes[7] = 45;
/*      */ 
/* 4095 */         datetimeAsBytes[8] = (byte)Character.forDigit(day / 10, 10);
/*      */ 
/* 4097 */         datetimeAsBytes[9] = (byte)Character.forDigit(day % 10, 10);
/*      */ 
/* 4100 */         datetimeAsBytes[10] = 32;
/*      */ 
/* 4102 */         datetimeAsBytes[11] = (byte)Character.forDigit(hour / 10, 10);
/*      */ 
/* 4104 */         datetimeAsBytes[12] = (byte)Character.forDigit(hour % 10, 10);
/*      */ 
/* 4107 */         datetimeAsBytes[13] = 58;
/*      */ 
/* 4109 */         datetimeAsBytes[14] = (byte)Character.forDigit(minute / 10, 10);
/*      */ 
/* 4111 */         datetimeAsBytes[15] = (byte)Character.forDigit(minute % 10, 10);
/*      */ 
/* 4114 */         datetimeAsBytes[16] = 58;
/*      */ 
/* 4116 */         datetimeAsBytes[17] = (byte)Character.forDigit(seconds / 10, 10);
/*      */ 
/* 4118 */         datetimeAsBytes[18] = (byte)Character.forDigit(seconds % 10, 10);
/*      */ 
/* 4121 */         datetimeAsBytes[19] = 46;
/*      */ 
/* 4123 */         int nanosOffset = 20;
/*      */ 
/* 4125 */         for (int j = 0; j < nanosAsBytes.length; j++) {
/* 4126 */           datetimeAsBytes[(nanosOffset + j)] = nanosAsBytes[j];
/*      */         }
/*      */ 
/* 4129 */         unpackedRowData[columnIndex] = datetimeAsBytes;
/*      */       }
/*      */ 
/* 4132 */       break;
/*      */     case 0:
/*      */     case 15:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/*      */     case 253:
/*      */     case 254:
/*      */     case 255:
/* 4143 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */ 
/* 4145 */       break;
/*      */     default:
/* 4148 */       throw new SQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void sendViaChannel(Buffer packet, int packetLength)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 4160 */       int oldLength = packet.getBufLength();
/*      */ 
/* 4162 */       packet.getNioBuffer().limit(packetLength);
/* 4163 */       packet.setPosition(0);
/*      */ 
/* 4165 */       this.socketChannel.write(packet.getNioBuffer());
/*      */ 
/* 4167 */       packet.setBufLength(oldLength);
/*      */     } catch (IOException ioEx) {
/* 4169 */       StringBuffer message = new StringBuffer(SQLError.get("08S01"));
/*      */ 
/* 4171 */       message.append(": ");
/* 4172 */       message.append(ioEx.getClass().getName());
/* 4173 */       message.append(Messages.getString("MysqlIO.102"));
/* 4174 */       message.append(ioEx.getMessage());
/*      */ 
/* 4176 */       if (!this.connection.getParanoid()) {
/* 4177 */         message.append(Util.stackTraceToString(ioEx));
/*      */       }
/*      */ 
/* 4180 */       throw new SQLException(message.toString(), "08S01", 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 4190 */     if (this.connection.getDynamicCalendars()) {
/* 4191 */       return Calendar.getInstance();
/*      */     }
/* 4193 */     return this.sessionCalendar;
/*      */   }
/*      */ 
/*      */   private void negotiateSSLConnection(String user, String password, String database, int packLength)
/*      */     throws SQLException, CommunicationsException
/*      */   {
/* 4211 */     if (!ExportControlled.enabled()) {
/* 4212 */       throw new ConnectionFeatureNotAvailableException(this.connection, this.lastPacketSentTimeMs, null);
/*      */     }
/*      */ 
/* 4216 */     boolean doSecureAuth = false;
/*      */ 
/* 4218 */     if ((this.serverCapabilities & 0x8000) != 0) {
/* 4219 */       this.clientParam |= 32768L;
/* 4220 */       doSecureAuth = true;
/*      */     }
/*      */ 
/* 4223 */     this.clientParam |= 2048L;
/*      */ 
/* 4225 */     Buffer packet = Buffer.allocateNew(packLength, this.useNewIo);
/*      */ 
/* 4227 */     if ((this.clientParam & 0x4000) != 0L)
/* 4228 */       packet.writeLong(this.clientParam);
/*      */     else {
/* 4230 */       packet.writeInt((int)this.clientParam);
/*      */     }
/*      */ 
/* 4233 */     send(packet);
/*      */ 
/* 4235 */     ExportControlled.transformSocketToSSLSocket(this);
/*      */ 
/* 4237 */     packet.clear();
/*      */ 
/* 4239 */     if (doSecureAuth) {
/* 4240 */       if (versionMeetsMinimum(4, 1, 1))
/* 4241 */         secureAuth411(null, packLength, user, password, database, true);
/*      */       else
/* 4243 */         secureAuth411(null, packLength, user, password, database, true);
/*      */     }
/*      */     else {
/* 4246 */       if ((this.clientParam & 0x4000) != 0L) {
/* 4247 */         packet.writeLong(this.clientParam);
/* 4248 */         packet.writeLong(this.maxThreeBytes);
/*      */       } else {
/* 4250 */         packet.writeInt((int)this.clientParam);
/* 4251 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */ 
/* 4255 */       packet.writeString(user);
/*      */ 
/* 4257 */       if (this.protocolVersion > 9)
/* 4258 */         packet.writeString(Util.newCrypt(password, this.seed));
/*      */       else {
/* 4260 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       }
/*      */ 
/* 4263 */       if (((this.serverCapabilities & 0x8) != 0) && (database != null) && (database.length() > 0))
/*      */       {
/* 4265 */         packet.writeString(database);
/*      */       }
/*      */ 
/* 4268 */       send(packet);
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  128 */     OutputStreamWriter outWriter = null;
/*      */     try
/*      */     {
/*  136 */       outWriter = new OutputStreamWriter(new ByteArrayOutputStream());
/*  137 */       jvmPlatformCharset = outWriter.getEncoding();
/*      */     } finally {
/*      */       try {
/*  140 */         if (outWriter != null)
/*  141 */           outWriter.close();
/*      */       }
/*      */       catch (IOException ioEx)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.MysqlIO
 * JD-Core Version:    0.6.0
 */