package org.logicalcobwebs.cglib.asm;

public class ClassWriter
  implements ClassVisitor
{
  static final int CLASS = 7;
  static final int FIELD = 9;
  static final int METH = 10;
  static final int IMETH = 11;
  static final int STR = 8;
  static final int INT = 3;
  static final int FLOAT = 4;
  static final int LONG = 5;
  static final int DOUBLE = 6;
  static final int NAME_TYPE = 12;
  static final int UTF8 = 1;
  private int version;
  private short index = 1;
  private ByteVector pool = new ByteVector();
  private Item[] items = new Item[64];
  private int threshold = (int)(0.75D * this.items.length);
  private int access;
  private int name;
  private int superName;
  private int interfaceCount;
  private int[] interfaces;
  private int sourceFile;
  private int fieldCount;
  private ByteVector fields;
  private boolean computeMaxs;
  boolean checkAttributes;
  CodeWriter firstMethod;
  CodeWriter lastMethod;
  private int innerClassesCount;
  private ByteVector innerClasses;
  private Attribute attrs;
  Item key = new Item();
  Item key2 = new Item();
  Item key3 = new Item();
  static final int NOARG_INSN = 0;
  static final int SBYTE_INSN = 1;
  static final int SHORT_INSN = 2;
  static final int VAR_INSN = 3;
  static final int IMPLVAR_INSN = 4;
  static final int TYPE_INSN = 5;
  static final int FIELDORMETH_INSN = 6;
  static final int ITFMETH_INSN = 7;
  static final int LABEL_INSN = 8;
  static final int LABELW_INSN = 9;
  static final int LDC_INSN = 10;
  static final int LDCW_INSN = 11;
  static final int IINC_INSN = 12;
  static final int TABL_INSN = 13;
  static final int LOOK_INSN = 14;
  static final int MANA_INSN = 15;
  static final int WIDE_INSN = 16;
  static byte[] TYPE;

  public ClassWriter(boolean paramBoolean)
  {
    this(paramBoolean, false);
  }

  public ClassWriter(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.computeMaxs = paramBoolean1;
    this.checkAttributes = (!paramBoolean2);
  }

  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String[] paramArrayOfString, String paramString3)
  {
    this.version = paramInt1;
    this.access = paramInt2;
    this.name = newClass(paramString1);
    this.superName = (paramString2 == null ? 0 : newClass(paramString2));
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      this.interfaceCount = paramArrayOfString.length;
      this.interfaces = new int[this.interfaceCount];
      for (int i = 0; i < this.interfaceCount; i++)
        this.interfaces[i] = newClass(paramArrayOfString[i]);
    }
    if (paramString3 != null)
    {
      newUTF8("SourceFile");
      this.sourceFile = newUTF8(paramString3);
    }
    if ((paramInt2 & 0x20000) != 0)
      newUTF8("Deprecated");
    if ((paramInt2 & 0x1000) != 0)
      newUTF8("Synthetic");
  }

  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if (this.innerClasses == null)
    {
      newUTF8("InnerClasses");
      this.innerClasses = new ByteVector();
    }
    this.innerClassesCount += 1;
    this.innerClasses.putShort(paramString1 == null ? 0 : newClass(paramString1));
    this.innerClasses.putShort(paramString2 == null ? 0 : newClass(paramString2));
    this.innerClasses.putShort(paramString3 == null ? 0 : newUTF8(paramString3));
    this.innerClasses.putShort(paramInt);
  }

  public void visitField(int paramInt, String paramString1, String paramString2, Object paramObject, Attribute paramAttribute)
  {
    this.fieldCount += 1;
    if (this.fields == null)
      this.fields = new ByteVector();
    this.fields.putShort(paramInt).putShort(newUTF8(paramString1)).putShort(newUTF8(paramString2));
    int i = 0;
    if (paramObject != null)
      i++;
    if ((paramInt & 0x1000) != 0)
      i++;
    if ((paramInt & 0x20000) != 0)
      i++;
    if (paramAttribute != null)
      i += paramAttribute.getCount();
    this.fields.putShort(i);
    if (paramObject != null)
    {
      this.fields.putShort(newUTF8("ConstantValue"));
      this.fields.putInt(2).putShort(newConstItem(paramObject).index);
    }
    if ((paramInt & 0x1000) != 0)
      this.fields.putShort(newUTF8("Synthetic")).putInt(0);
    if ((paramInt & 0x20000) != 0)
      this.fields.putShort(newUTF8("Deprecated")).putInt(0);
    if (paramAttribute != null)
      paramAttribute.put(this, null, 0, -1, -1, this.fields);
  }

  public CodeVisitor visitMethod(int paramInt, String paramString1, String paramString2, String[] paramArrayOfString, Attribute paramAttribute)
  {
    CodeWriter localCodeWriter = new CodeWriter(this, this.computeMaxs);
    localCodeWriter.init(paramInt, paramString1, paramString2, paramArrayOfString, paramAttribute);
    return localCodeWriter;
  }

  public void visitAttribute(Attribute paramAttribute)
  {
    paramAttribute.next = this.attrs;
    this.attrs = paramAttribute;
  }

  public void visitEnd()
  {
  }

  public byte[] toByteArray()
  {
    int i = 24 + 2 * this.interfaceCount;
    if (this.fields != null)
      i += this.fields.length;
    int j = 0;
    for (CodeWriter localCodeWriter = this.firstMethod; localCodeWriter != null; localCodeWriter = localCodeWriter.next)
    {
      j++;
      i += localCodeWriter.getSize();
    }
    int k = 0;
    if (this.sourceFile != 0)
    {
      k++;
      i += 8;
    }
    if ((this.access & 0x20000) != 0)
    {
      k++;
      i += 6;
    }
    if ((this.access & 0x1000) != 0)
    {
      k++;
      i += 6;
    }
    if (this.innerClasses != null)
    {
      k++;
      i += 8 + this.innerClasses.length;
    }
    if (this.attrs != null)
    {
      k += this.attrs.getCount();
      i += this.attrs.getSize(this, null, 0, -1, -1);
    }
    i += this.pool.length;
    ByteVector localByteVector = new ByteVector(i);
    localByteVector.putInt(-889275714).putInt(this.version);
    localByteVector.putShort(this.index).putByteArray(this.pool.data, 0, this.pool.length);
    localByteVector.putShort(this.access).putShort(this.name).putShort(this.superName);
    localByteVector.putShort(this.interfaceCount);
    for (int m = 0; m < this.interfaceCount; m++)
      localByteVector.putShort(this.interfaces[m]);
    localByteVector.putShort(this.fieldCount);
    if (this.fields != null)
      localByteVector.putByteArray(this.fields.data, 0, this.fields.length);
    localByteVector.putShort(j);
    for (localCodeWriter = this.firstMethod; localCodeWriter != null; localCodeWriter = localCodeWriter.next)
      localCodeWriter.put(localByteVector);
    localByteVector.putShort(k);
    if (this.sourceFile != 0)
      localByteVector.putShort(newUTF8("SourceFile")).putInt(2).putShort(this.sourceFile);
    if ((this.access & 0x20000) != 0)
      localByteVector.putShort(newUTF8("Deprecated")).putInt(0);
    if ((this.access & 0x1000) != 0)
      localByteVector.putShort(newUTF8("Synthetic")).putInt(0);
    if (this.innerClasses != null)
    {
      localByteVector.putShort(newUTF8("InnerClasses"));
      localByteVector.putInt(this.innerClasses.length + 2).putShort(this.innerClassesCount);
      localByteVector.putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
    }
    if (this.attrs != null)
      this.attrs.put(this, null, 0, -1, -1, localByteVector);
    return localByteVector.data;
  }

  Item newConstItem(Object paramObject)
  {
    int i;
    if ((paramObject instanceof Integer))
    {
      i = ((Integer)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Byte))
    {
      i = ((Byte)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Character))
    {
      i = ((Character)paramObject).charValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Short))
    {
      i = ((Short)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Boolean))
    {
      i = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      return newInteger(i);
    }
    if ((paramObject instanceof Float))
    {
      float f = ((Float)paramObject).floatValue();
      return newFloat(f);
    }
    if ((paramObject instanceof Long))
    {
      long l = ((Long)paramObject).longValue();
      return newLong(l);
    }
    if ((paramObject instanceof Double))
    {
      double d = ((Double)paramObject).doubleValue();
      return newDouble(d);
    }
    if ((paramObject instanceof String))
      return newString((String)paramObject);
    if ((paramObject instanceof Type))
    {
      Type localType = (Type)paramObject;
      return newClassItem(localType.getSort() == 10 ? localType.getInternalName() : localType.getDescriptor());
    }
    throw new IllegalArgumentException("value " + paramObject);
  }

  public int newConst(Object paramObject)
  {
    return newConstItem(paramObject).index;
  }

  public int newConstInt(int paramInt)
  {
    return newInteger(paramInt).index;
  }

  public int newConstLong(long paramLong)
  {
    return newLong(paramLong).index;
  }

  public int newConstFloat(float paramFloat)
  {
    return newFloat(paramFloat).index;
  }

  public int newConstDouble(double paramDouble)
  {
    return newDouble(paramDouble).index;
  }

  public int newUTF8(String paramString)
  {
    this.key.set(1, paramString, null, null);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(1).putUTF8(paramString);
      localItem = new Item(this.index++, this.key);
      put(localItem);
    }
    return localItem.index;
  }

  public int newClass(String paramString)
  {
    return newClassItem(paramString).index;
  }

  private Item newClassItem(String paramString)
  {
    this.key2.set(7, paramString, null, null);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      this.pool.put12(7, newUTF8(paramString));
      localItem = new Item(this.index++, this.key2);
      put(localItem);
    }
    return localItem;
  }

  public int newField(String paramString1, String paramString2, String paramString3)
  {
    this.key3.set(9, paramString1, paramString2, paramString3);
    Item localItem = get(this.key3);
    if (localItem == null)
    {
      put122(9, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new Item(this.index++, this.key3);
      put(localItem);
    }
    return localItem.index;
  }

  Item newMethodItem(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    this.key3.set(paramBoolean ? 11 : 10, paramString1, paramString2, paramString3);
    Item localItem = get(this.key3);
    if (localItem == null)
    {
      put122(paramBoolean ? 11 : 10, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new Item(this.index++, this.key3);
      put(localItem);
    }
    return localItem;
  }

  public int newMethod(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    return newMethodItem(paramString1, paramString2, paramString3, paramBoolean).index;
  }

  private Item newInteger(int paramInt)
  {
    this.key.set(paramInt);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(3).putInt(paramInt);
      localItem = new Item(this.index++, this.key);
      put(localItem);
    }
    return localItem;
  }

  private Item newFloat(float paramFloat)
  {
    this.key.set(paramFloat);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(4).putInt(Float.floatToIntBits(paramFloat));
      localItem = new Item(this.index++, this.key);
      put(localItem);
    }
    return localItem;
  }

  private Item newLong(long paramLong)
  {
    this.key.set(paramLong);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(5).putLong(paramLong);
      localItem = new Item(this.index, this.key);
      put(localItem);
      this.index = (short)(this.index + 2);
    }
    return localItem;
  }

  private Item newDouble(double paramDouble)
  {
    this.key.set(paramDouble);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(6).putLong(Double.doubleToLongBits(paramDouble));
      localItem = new Item(this.index, this.key);
      put(localItem);
      this.index = (short)(this.index + 2);
    }
    return localItem;
  }

  private Item newString(String paramString)
  {
    this.key2.set(8, paramString, null, null);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      this.pool.put12(8, newUTF8(paramString));
      localItem = new Item(this.index++, this.key2);
      put(localItem);
    }
    return localItem;
  }

  public int newNameType(String paramString1, String paramString2)
  {
    this.key2.set(12, paramString1, paramString2, null);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      put122(12, newUTF8(paramString1), newUTF8(paramString2));
      localItem = new Item(this.index++, this.key2);
      put(localItem);
    }
    return localItem.index;
  }

  private Item get(Item paramItem)
  {
    int i = paramItem.hashCode;
    for (Item localItem = this.items[(i % this.items.length)]; localItem != null; localItem = localItem.next)
      if ((localItem.hashCode == i) && (paramItem.isEqualTo(localItem)))
        return localItem;
    return null;
  }

  private void put(Item paramItem)
  {
    if (this.index > this.threshold)
    {
      Item[] arrayOfItem = new Item[this.items.length * 2 + 1];
      for (int j = this.items.length - 1; j >= 0; j--)
      {
        Item localItem;
        for (Object localObject = this.items[j]; localObject != null; localObject = localItem)
        {
          int k = ((Item)localObject).hashCode % arrayOfItem.length;
          localItem = ((Item)localObject).next;
          ((Item)localObject).next = arrayOfItem[k];
          arrayOfItem[k] = localObject;
        }
      }
      this.items = arrayOfItem;
      this.threshold = (int)(this.items.length * 0.75D);
    }
    int i = paramItem.hashCode % this.items.length;
    paramItem.next = this.items[i];
    this.items[i] = paramItem;
  }

  private void put122(int paramInt1, int paramInt2, int paramInt3)
  {
    this.pool.put12(paramInt1, paramInt2).putShort(paramInt3);
  }

  static
  {
    byte[] arrayOfByte = new byte['Ü'];
    String str = "AAAAAAAAAAAAAAAABCKLLDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAAAAAAAAAAAAAAAAAAIIIIIIIIIIIIIIIIDNOAAAAAAGGGGGGGHAFBFAAFFAAQPIIJJIIIIIIIIIIIIIIIIII";
    for (int i = 0; i < arrayOfByte.length; i++)
      arrayOfByte[i] = (byte)(str.charAt(i) - 'A');
    TYPE = arrayOfByte;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.ClassWriter
 * JD-Core Version:    0.6.0
 */