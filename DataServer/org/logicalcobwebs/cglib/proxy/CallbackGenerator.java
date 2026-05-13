package org.logicalcobwebs.cglib.proxy;

import java.util.List;
import org.logicalcobwebs.cglib.core.ClassEmitter;
import org.logicalcobwebs.cglib.core.CodeEmitter;
import org.logicalcobwebs.cglib.core.MethodInfo;
import org.logicalcobwebs.cglib.core.Signature;

abstract interface CallbackGenerator
{
  public abstract void generate(ClassEmitter paramClassEmitter, Context paramContext, List paramList)
    throws Exception;

  public abstract void generateStatic(CodeEmitter paramCodeEmitter, Context paramContext, List paramList)
    throws Exception;

  public static abstract interface Context
  {
    public abstract CodeEmitter beginMethod(ClassEmitter paramClassEmitter, MethodInfo paramMethodInfo);

    public abstract int getOriginalModifiers(MethodInfo paramMethodInfo);

    public abstract int getIndex(MethodInfo paramMethodInfo);

    public abstract void emitCallback(CodeEmitter paramCodeEmitter, int paramInt);

    public abstract Signature getImplSignature(MethodInfo paramMethodInfo);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.CallbackGenerator
 * JD-Core Version:    0.6.0
 */