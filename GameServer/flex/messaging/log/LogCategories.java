package flex.messaging.log;

public abstract interface LogCategories
{
  public static final String CLIENT_FLEXCLIENT = "Client.FlexClient";
  public static final String CLIENT_MESSAGECLIENT = "Client.MessageClient";
  public static final String CONFIGURATION = "Configuration";
  public static final String ENDPOINT_GENERAL = "Endpoint.General";
  public static final String ENDPOINT_AMF = "Endpoint.AMF";
  public static final String ENDPOINT_NIO_AMF = "Endpoint.NIOAMF";
  public static final String ENDPOINT_FLEXSESSION = "Endpoint.FlexSession";
  public static final String ENDPOINT_HTTP = "Endpoint.HTTP";
  public static final String ENDPOINT_NIO_HTTP = "Endpoint.NIOHTTP";
  public static final String ENDPOINT_RTMP = "Endpoint.RTMP";
  public static final String ENDPOINT_STREAMING_AMF = "Endpoint.StreamingAMF";
  public static final String ENDPOINT_STREAMING_NIO_AMF = "Endpoint.StreamingNIOAMF";
  public static final String ENDPOINT_STREAMING_HTTP = "Endpoint.StreamingHTTP";
  public static final String ENDPOINT_STREAMING_NIO_HTTP = "Endpoint.StreamingNIOHTTP";
  public static final String ENDPOINT_TYPE = "Endpoint.Type";
  public static final String EXECUTOR = "Executor";
  public static final String MANAGEMENT_GENERAL = "Management.General";
  public static final String MANAGEMENT_MBEANSERVER = "Management.MBeanServer";
  public static final String MESSAGE_GENERAL = "Message.General";
  public static final String MESSAGE_COMMAND = "Message.Command";
  public static final String MESSAGE_DATA = "Message.Data";
  public static final String MESSAGE_REMOTING = "Message.Remoting";
  public static final String MESSAGE_RPC = "Message.RPC";
  public static final String MESSAGE_SELECTOR = "Message.Selector";
  public static final String MESSAGE_TIMING = "Message.Timing";
  public static final String PROTOCOL_HTTP = "Protocol.HTTP";
  public static final String PROTOCOL_RTMP = "Protocol.RTMP";
  public static final String PROTOCOL_RTMPT = "Protocol.RTMPT";
  public static final String RESOURCE = "Resource";
  public static final String SERVICE_GENERAL = "Service.General";
  public static final String SERVICE_CLUSTER = "Service.Cluster";
  public static final String SERVICE_COLLABORATION = "Service.Collaboration";
  public static final String SERVICE_DATA = "Service.Data";
  public static final String SERVICE_DATA_GENERAL = "Service.Data.General";
  public static final String SERVICE_DATA_HIBERNATE = "Service.Data.Hibernate";
  public static final String SERVICE_DATA_SQL = "Service.Data.SQL";
  public static final String SERVICE_DATA_TRANSACTION = "Service.Data.Transaction";
  public static final String SERVICE_HTTP = "Service.HTTP";
  public static final String SERVICE_MESSAGE = "Service.Message";
  public static final String SERVICE_MESSAGE_JMS = "Service.Message.JMS";
  public static final String SERVICE_REMOTING = "Service.Remoting";
  public static final String SECURITY = "Security";
  public static final String SOCKET_SERVER_GENERAL = "SocketServer.General";
  public static final String SOCKET_SERVER_BYTE_BUFFER_MANAGEMENT = "SocketServer.ByteBufferManagement";
  public static final String SSL = "SSL";
  public static final String STARTUP_MESSAGEBROKER = "Startup.MessageBroker";
  public static final String STARTUP_SERVICE = "Startup.Service";
  public static final String STARTUP_DESTINATION = "Startup.Destination";
  public static final String TIMEOUT = "Timeout";
  public static final String WSRP_GENERAL = "WSRP.General";
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.LogCategories
 * JD-Core Version:    0.6.0
 */