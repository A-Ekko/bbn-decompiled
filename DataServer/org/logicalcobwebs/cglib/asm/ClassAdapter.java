package org.logicalcobwebs.cglib.asm;

public class ClassAdapter
  implements ClassVisitor
{
  protected ClassVisitor cv;

  public ClassAdapter(ClassVisitor paramClassVisitor)
  {
    this.cv = paramClassVisitor;
  }

  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String[] paramArrayOfString, String paramString3)
  {
    this.cv.visit(paramInt1, paramInt2, paramString1, paramString2, paramArrayOfString, paramString3);
  }

  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    this.cv.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
  }

  public void visitField(int paramInt, String paramString1, String paramString2, Object paramObject, Attribute paramAttribute)
  {
    this.cv.visitField(paramInt, paramString1, paramString2, paramObject, paramAttribute);
  }

  public CodeVisitor visitMethod(int paramInt, String paramString1, String paramString2, String[] paramArrayOfString, Attribute paramAttribute)
  {
    return new CodeAdapter(this.cv.visitMethod(paramInt, paramString1, paramString2, paramArrayOfString, paramAttribute));
  }

  public void visitAttribute(Attribute paramAttribute)
  {
    this.cv.visitAttribute(paramAttribute);
  }

  public void visitEnd()
  {
    this.cv.visitEnd();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.ClassAdapter
 * JD-Core Version:    0.6.0
 */