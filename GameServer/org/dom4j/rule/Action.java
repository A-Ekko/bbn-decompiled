package org.dom4j.rule;

import org.dom4j.Node;

public abstract interface Action
{
  public abstract void run(Node paramNode)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.dom4j.rule.Action
 * JD-Core Version:    0.6.0
 */