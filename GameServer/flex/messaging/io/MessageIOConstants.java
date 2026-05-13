/*    */ package flex.messaging.io;
/*    */ 
/*    */ public abstract interface MessageIOConstants
/*    */ {
/*    */   public static final int AMF0 = 0;
/*    */   public static final int AMF3 = 3;
/* 25 */   public static final Double AMF3_INFO_PROPERTY = new Double(3.0D);
/*    */   public static final String CONTENT_TYPE_XML = "text/xml; charset=utf-8";
/*    */   public static final String AMF_CONTENT_TYPE = "application/x-amf";
/*    */   public static final String XML_CONTENT_TYPE = "application/xml";
/*    */   public static final String RESULT_METHOD = "/onResult";
/*    */   public static final String STATUS_METHOD = "/onStatus";
/*    */   public static final int STATUS_OK = 0;
/*    */   public static final int STATUS_ERR = 1;
/*    */   public static final int STATUS_NOTAMF = 2;
/*    */   public static final String SECURITY_HEADER_NAME = "Credentials";
/*    */   public static final String SECURITY_PRINCIPAL = "userid";
/*    */   public static final String SECURITY_CREDENTIALS = "password";
/*    */   public static final String URL_APPEND_HEADER = "AppendToGatewayUrl";
/*    */   public static final String SERVICE_TYPE_HEADER = "ServiceType";
/*    */   public static final String REMOTE_CLASS_FIELD = "_remoteClass";
/*    */   public static final String SUPPORT_REMOTE_CLASS = "SupportRemoteClass";
/*    */   public static final String SUPPORT_DATES_BY_REFERENCE = "SupportDatesByReference";
/*    */   public static final String METHOD_POST = "POST";
/*    */   public static final String HEADER_SOAP_ACTION = "SOAPAction";
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.MessageIOConstants
 * JD-Core Version:    0.6.0
 */