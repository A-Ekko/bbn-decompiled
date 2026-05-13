package flex.messaging.endpoints;

import flex.management.Manageable;
import flex.messaging.MessageBroker;
import flex.messaging.config.ConfigMap;
import flex.messaging.config.SecurityConstraint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface Endpoint extends Manageable
{
  public abstract void initialize(String paramString, ConfigMap paramConfigMap);

  public abstract void start();

  public abstract boolean isStarted();

  public abstract void stop();

  public abstract String getClientType();

  public abstract void setClientType(String paramString);

  public abstract ConfigMap describeEndpoint();

  public abstract String getId();

  public abstract void setId(String paramString);

  public abstract MessageBroker getMessageBroker();

  public abstract void setMessageBroker(MessageBroker paramMessageBroker);

  public abstract double getMessagingVersion();

  public abstract String getParsedUrl(String paramString);

  public abstract int getPort();

  public abstract boolean isSecure();

  public abstract SecurityConstraint getSecurityConstraint();

  public abstract void setSecurityConstraint(SecurityConstraint paramSecurityConstraint);

  public abstract void service(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse);

  public abstract String getUrl();

  public abstract void setUrl(String paramString);

  public abstract String getUrlForClient();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.Endpoint
 * JD-Core Version:    0.6.0
 */