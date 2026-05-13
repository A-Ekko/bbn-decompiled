package flex.messaging.messages;

import java.io.Serializable;
import java.util.Map;

public abstract interface Message extends Serializable, Cloneable
{
  public static final String FLEX_CLIENT_ID_HEADER = "DSId";
  public static final String DESTINATION_CLIENT_ID_HEADER = "DSDstClientId";
  public static final String ENDPOINT_HEADER = "DSEndpoint";
  public static final String VALIDATE_ENDPOINT_HEADER = "DSValidateEndpoint";
  public static final String REMOTE_CREDENTIALS_HEADER = "DSRemoteCredentials";
  public static final String REMOTE_CREDENTIALS_CHARSET_HEADER = "DSRemoteCredentialsCharset";
  public static final String SYNC_HEADER = "sync";

  public abstract Object getClientId();

  public abstract void setClientId(Object paramObject);

  public abstract String getDestination();

  public abstract void setDestination(String paramString);

  public abstract String getMessageId();

  public abstract void setMessageId(String paramString);

  public abstract long getTimestamp();

  public abstract void setTimestamp(long paramLong);

  public abstract long getTimeToLive();

  public abstract void setTimeToLive(long paramLong);

  public abstract Object getBody();

  public abstract void setBody(Object paramObject);

  public abstract Map getHeaders();

  public abstract void setHeaders(Map paramMap);

  public abstract Object getHeader(String paramString);

  public abstract void setHeader(String paramString, Object paramObject);

  public abstract boolean headerExists(String paramString);

  public abstract Object clone();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.messages.Message
 * JD-Core Version:    0.6.0
 */