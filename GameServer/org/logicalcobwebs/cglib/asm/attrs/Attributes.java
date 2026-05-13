package org.logicalcobwebs.cglib.asm.attrs;

import org.logicalcobwebs.cglib.asm.Attribute;

public class Attributes
{
  public static Attribute[] getDefaultAttributes()
  {
    return new Attribute[] { new AnnotationDefaultAttribute(), new RuntimeInvisibleAnnotations(), new RuntimeInvisibleParameterAnnotations(), new RuntimeVisibleAnnotations(), new RuntimeVisibleParameterAnnotations(), new StackMapAttribute(), new SourceDebugExtensionAttribute(), new SignatureAttribute(), new EnclosingMethodAttribute(), new LocalVariableTypeTableAttribute() };
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.Attributes
 * JD-Core Version:    0.6.0
 */