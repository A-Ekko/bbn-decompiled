package org.logicalcobwebs.cglib.asm;

public class CodeAdapter
  implements CodeVisitor
{
  protected CodeVisitor cv;

  public CodeAdapter(CodeVisitor paramCodeVisitor)
  {
    this.cv = paramCodeVisitor;
  }

  public void visitInsn(int paramInt)
  {
    this.cv.visitInsn(paramInt);
  }

  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    this.cv.visitIntInsn(paramInt1, paramInt2);
  }

  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    this.cv.visitVarInsn(paramInt1, paramInt2);
  }

  public void visitTypeInsn(int paramInt, String paramString)
  {
    this.cv.visitTypeInsn(paramInt, paramString);
  }

  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.cv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
  }

  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.cv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
  }

  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    this.cv.visitJumpInsn(paramInt, paramLabel);
  }

  public void visitLabel(Label paramLabel)
  {
    this.cv.visitLabel(paramLabel);
  }

  public void visitLdcInsn(Object paramObject)
  {
    this.cv.visitLdcInsn(paramObject);
  }

  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    this.cv.visitIincInsn(paramInt1, paramInt2);
  }

  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label[] paramArrayOfLabel)
  {
    this.cv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramArrayOfLabel);
  }

  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    this.cv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
  }

  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    this.cv.visitMultiANewArrayInsn(paramString, paramInt);
  }

  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    this.cv.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
  }

  public void visitMaxs(int paramInt1, int paramInt2)
  {
    this.cv.visitMaxs(paramInt1, paramInt2);
  }

  public void visitLocalVariable(String paramString1, String paramString2, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    this.cv.visitLocalVariable(paramString1, paramString2, paramLabel1, paramLabel2, paramInt);
  }

  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    this.cv.visitLineNumber(paramInt, paramLabel);
  }

  public void visitAttribute(Attribute paramAttribute)
  {
    this.cv.visitAttribute(paramAttribute);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.CodeAdapter
 * JD-Core Version:    0.6.0
 */