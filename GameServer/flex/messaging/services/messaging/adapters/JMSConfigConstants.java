package flex.messaging.services.messaging.adapters;

public abstract interface JMSConfigConstants
{
  public static final String ASYNC = "async";
  public static final String ACKNOWLEDGE_MODE = "acknowledge-mode";
  public static final String AUTO_ACKNOWLEDGE = "auto_acknowledge";
  public static final String CLIENT_ACKNOWLEDGE = "client_acknowledge";
  public static final String CONNECTION_FACTORY = "connection-factory";
  public static final String CONNECTION_CREDENTIALS = "connection-credentials";
  public static final String DELIVERY_SETTINGS = "delivery-settings";
  public static final String DEFAULT_DELIVERY_MODE = "default_delivery_mode";
  public static final String DEFAULT_PRIORITY = "default-priority";
  public static final String DELIVERY_MODE = "delivery-mode";
  public static final String DESTINATION_JNDI_NAME = "destination-jndi-name";
  public static final String DESTINATION_NAME = "destination-name";
  public static final String DESTINATION_TYPE = "destination-type";
  public static final String DUPS_OK_ACKNOWLEDGE = "dups_ok_acknowledge";
  public static final String INITIAL_CONTEXT_ENVIRONMENT = "initial-context-environment";
  public static final String JMS = "jms";
  public static final String MAX_PRODUCERS = "max-producers";
  public static final String MESSAGE_TYPE = "message-type";
  public static final String MESSAGE_PRIORITY = "message-priority";
  public static final String MODE = "mode";
  public static final String NAME = "name";
  public static final String NON_PERSISTENT = "non_persistent";
  public static final String OBJECT_MESSAGE = "javax.jms.ObjectMessage";
  public static final String PASSWORD = "password";
  public static final String PERSISTENT = "persistent";
  public static final String PRESERVE_JMS_HEADERS = "preserve-jms-headers";
  public static final String PROPERTY = "property";
  public static final String SYNC = "sync";
  public static final String SYNC_RECEIVE_INTERVAL_MILLIS = "sync-receive-interval-millis";
  public static final String SYNC_RECEIVE_WAIT_MILLIS = "sync-receive-wait-millis";
  public static final String QUEUE = "queue";
  public static final String TEXT_MESSAGE = "javax.jms.TextMessage";
  public static final String TOPIC = "topic";
  public static final String TRANSACTION_MODE = "transacted-sessions";
  public static final String USERNAME = "username";
  public static final String VALUE = "value";
  public static final String TIME_TO_LIVE = "timeToLive";
  public static final String JMS_CORRELATION_ID = "JMSCorrelationID";
  public static final String JMS_DELIVERY_MODE = "JMSDeliveryMode";
  public static final String JMS_DESTINATION = "JMSDestination";
  public static final String JMS_EXPIRATION = "JMSExpiration";
  public static final String JMS_PRIORITY = "JMSPriority";
  public static final String JMS_REDELIVERED = "JMSRedelivered";
  public static final String JMS_REPLY_TO = "JMSReplyTo";
  public static final String JMS_TYPE = "JMSType";
  public static final String defaultAcknowledgeMode = "auto_acknowledge";
  public static final String defaultDestinationType = "topic";
  public static final boolean defaultPreserveJMSHeaders = true;
  public static final long defaultSyncReceiveIntervalMillis = 100L;
  public static final long defaultSyncReceiveWaitMillis = 0L;
  public static final int defaultMaxProducers = 1;
  public static final String defaultMode = "sync";
  public static final int MISSING_NAME_OR_VALUE = 10800;
  public static final int INVALID_CONTEXT_NAME = 10801;
  public static final int INACCESIBLE_CONTEXT_NAME = 10802;
  public static final int MISSING_PROPERTY_SUBELEMENT = 10803;
  public static final int MISSING_CONNECTION_FACTORY = 10804;
  public static final int INVALID_DESTINATION_TYPE = 10805;
  public static final int MISSING_DESTINATION_JNDI_NAME = 10807;
  public static final int INVALID_ACKNOWLEDGE_MODE = 10808;
  public static final int INVALID_DELIVERY_MODE = 10809;
  public static final int NONSERIALIZABLE_MESSAGE_BODY = 10810;
  public static final int INVALID_JMS_MESSAGE_TYPE = 10811;
  public static final int NON_QUEUE_DESTINATION = 10813;
  public static final int NON_QUEUE_FACTORY = 10814;
  public static final int NON_TOPIC_DESTINATION = 10815;
  public static final int NON_TOPIC_FACTORY = 10816;
  public static final int INVALID_DELIVERY_MODE_VALUE = 10817;
  public static final int ASYNC_MESSAGE_DELIVERY_NOT_SUPPORTED = 10818;
  public static final int DURABLE_SUBSCRIBER_NOT_SUPPORTED = 10819;
  public static final int CLIENT_UNSUBSCRIBE_DUE_TO_MESSAGE_DELIVERY_ERROR = 10820;
  public static final int CLIENT_UNSUBSCRIBE_DUE_TO_CONSUMER_REMOVAL = 10821;
  public static final int CLIENT_UNSUBSCRIBE_DUE_TO_CONSUMER_STOP = 10822;
  public static final int MISSING_DURABLE_SUBSCRIPTION_NAME = 10823;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSConfigConstants
 * JD-Core Version:    0.6.0
 */