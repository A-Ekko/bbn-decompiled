package org.logicalcobwebs.cglib.asm;

public abstract interface ClassVisitor
{
  public abstract void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String[] paramArrayOfString, String paramString3);

  public abstract void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt);

  public abstract void visitField(int paramInt, String paramString1, String paramString2, Object paramObject, Attribute paramAttribute);

  public abstract CodeVisitor visitMethod(int paramInt, String paramString1, String paramString2, String[] paramArrayOfString, Attribute paramAttribute);

  public abstract void visitAttribute(Attribute paramAttribute);

  public abstract void visitEnd();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.ClassVisitor
 * JD-Core Version:    0.6.0
 */