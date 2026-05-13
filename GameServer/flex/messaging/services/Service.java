package flex.messaging.services;

import flex.management.Manageable;
import flex.messaging.Destination;
import flex.messaging.FlexComponent;
import flex.messaging.MessageBroker;
import flex.messaging.config.ConfigMap;
import flex.messaging.endpoints.Endpoint;
import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import java.util.List;
import java.util.Map;

public abstract interface Service extends Manageable, FlexComponent
{
  public abstract Map getRegisteredAdapters();

  public abstract String registerAdapter(String paramString1, String paramString2);

  public abstract String unregisterAdapter(String paramString);

  public abstract String getDefaultAdapter();

  public abstract void setDefaultAdapter(String paramString);

  public abstract List getDefaultChannels();

  public abstract void addDefaultChannel(String paramString);

  public abstract void setDefaultChannels(List paramList);

  public abstract boolean removeDefaultChannel(String paramString);

  public abstract Destination getDestination(Message paramMessage);

  public abstract Destination getDestination(String paramString);

  public abstract Map getDestinations();

  public abstract Destination createDestination(String paramString);

  public abstract void addDestination(Destination paramDestination);

  public abstract Destination removeDestination(String paramString);

  public abstract String getId();

  public abstract void setId(String paramString);

  public abstract MessageBroker getMessageBroker();

  public abstract void setMessageBroker(MessageBroker paramMessageBroker);

  public abstract ConfigMap describeService(Endpoint paramEndpoint);

  public abstract Object serviceMessage(Message paramMessage);

  public abstract Object serviceCommand(CommandMessage paramCommandMessage);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.Service
 * JD-Core Version:    0.6.0
 */