package org.logicalcobwebs.cglib.asm.attrs;

import java.util.ArrayList;
import java.util.List;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Type;

public class Annotation
{
  public String type;
  public List elementValues = new ArrayList();

  public Annotation()
  {
  }

  public Annotation(String paramString)
  {
    this.type = paramString;
  }

  public void add(String paramString, Object paramObject)
  {
    this.elementValues.add(new Object[] { paramString, paramObject });
  }

  public int read(ClassReader paramClassReader, int paramInt, char[] paramArrayOfChar)
  {
    this.type = paramClassReader.readUTF8(paramInt, paramArrayOfChar);
    int i = paramClassReader.readUnsignedShort(paramInt + 2);
    paramInt += 4;
    int[] arrayOfInt = { paramInt };
    for (int j = 0; j < i; j++)
    {
      String str = paramClassReader.readUTF8(arrayOfInt[0], paramArrayOfChar);
      arrayOfInt[0] += 2;
      this.elementValues.add(new Object[] { str, readValue(paramClassReader, arrayOfInt, paramArrayOfChar) });
    }
    return arrayOfInt[0];
  }

  public void write(ByteVector paramByteVector, ClassWriter paramClassWriter)
  {
    paramByteVector.putShort(paramClassWriter.newUTF8(this.type));
    paramByteVector.putShort(this.elementValues.size());
    for (int i = 0; i < this.elementValues.size(); i++)
    {
      Object[] arrayOfObject = (Object[])(Object[])this.elementValues.get(i);
      paramByteVector.putShort(paramClassWriter.newUTF8((String)arrayOfObject[0]));
      writeValue(paramByteVector, arrayOfObject[1], paramClassWriter);
    }
  }

  public static int readAnnotations(List paramList, ClassReader paramClassReader, int paramInt, char[] paramArrayOfChar)
  {
    int i = paramClassReader.readUnsignedShort(paramInt);
    paramInt += 2;
    for (int j = 0; j < i; j++)
    {
      Annotation localAnnotation = new Annotation();
      paramInt = localAnnotation.read(paramClassReader, paramInt, paramArrayOfChar);
      paramList.add(localAnnotation);
    }
    return paramInt;
  }

  public static void readParameterAnnotations(List paramList, ClassReader paramClassReader, int paramInt, char[] paramArrayOfChar)
  {
    int i = paramClassReader.b[(paramInt++)] & 0xFF;
    for (int j = 0; j < i; j++)
    {
      ArrayList localArrayList = new ArrayList();
      paramInt = readAnnotations(localArrayList, paramClassReader, paramInt, paramArrayOfChar);
      paramList.add(localArrayList);
    }
  }

  public static ByteVector writeAnnotations(ByteVector paramByteVector, List paramList, ClassWriter paramClassWriter)
  {
    paramByteVector.putShort(paramList.size());
    for (int i = 0; i < paramList.size(); i++)
      ((Annotation)paramList.get(i)).write(paramByteVector, paramClassWriter);
    return paramByteVector;
  }

  public static ByteVector writeParametersAnnotations(ByteVector paramByteVector, List paramList, ClassWriter paramClassWriter)
  {
    paramByteVector.putByte(paramList.size());
    for (int i = 0; i < paramList.size(); i++)
      writeAnnotations(paramByteVector, (List)paramList.get(i), paramClassWriter);
    return paramByteVector;
  }

  public static String stringAnnotations(List paramList)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramList.size() > 0)
      for (int i = 0; i < paramList.size(); i++)
        localStringBuffer.append('\n').append(paramList.get(i));
    else
      localStringBuffer.append("<none>");
    return localStringBuffer.toString();
  }

  public static String stringParameterAnnotations(List paramList)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    String str = "";
    for (int i = 0; i < paramList.size(); i++)
    {
      localStringBuffer.append(str).append(stringAnnotations((List)paramList.get(i)));
      str = ", ";
    }
    return localStringBuffer.toString();
  }

  protected static Object readValue(ClassReader paramClassReader, int[] paramArrayOfInt, char[] paramArrayOfChar)
  {
    Object localObject1 = null;
    int tmp5_4 = 0;
    int[] tmp5_3 = paramArrayOfInt;
    int tmp7_6 = tmp5_3[tmp5_4];
    tmp5_3[tmp5_4] = (tmp7_6 + 1);
    int i = paramClassReader.readByte(tmp7_6);
    switch (i)
    {
    case 68:
    case 70:
    case 73:
    case 74:
      localObject1 = paramClassReader.readConst(paramClassReader.readUnsignedShort(paramArrayOfInt[0]), paramArrayOfChar);
      paramArrayOfInt[0] += 2;
      break;
    case 66:
      localObject1 = new Byte((byte)paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0]))));
      paramArrayOfInt[0] += 2;
      break;
    case 67:
      localObject1 = new Character((char)paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0]))));
      paramArrayOfInt[0] += 2;
      break;
    case 83:
      localObject1 = new Short((short)paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0]))));
      paramArrayOfInt[0] += 2;
      break;
    case 90:
      localObject1 = paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0]))) == 0 ? Boolean.FALSE : Boolean.TRUE;
      paramArrayOfInt[0] += 2;
      break;
    case 115:
      localObject1 = paramClassReader.readUTF8(paramArrayOfInt[0], paramArrayOfChar);
      paramArrayOfInt[0] += 2;
      break;
    case 101:
      localObject1 = new EnumConstValue(paramClassReader.readUTF8(paramArrayOfInt[0], paramArrayOfChar), paramClassReader.readUTF8(paramArrayOfInt[0] + 2, paramArrayOfChar));
      paramArrayOfInt[0] += 4;
      break;
    case 99:
      localObject1 = Type.getType(paramClassReader.readUTF8(paramArrayOfInt[0], paramArrayOfChar));
      paramArrayOfInt[0] += 2;
      break;
    case 64:
      localObject1 = new Annotation();
      paramArrayOfInt[0] = ((Annotation)localObject1).read(paramClassReader, paramArrayOfInt[0], paramArrayOfChar);
      break;
    case 91:
      int j = paramClassReader.readUnsignedShort(paramArrayOfInt[0]);
      paramArrayOfInt[0] += 2;
      int k = paramClassReader.readByte(paramArrayOfInt[0]);
      Object localObject2;
      int m;
      switch (k)
      {
      case 73:
        localObject2 = new int[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0])));
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 74:
        localObject2 = new long[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = paramClassReader.readLong(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0])));
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 68:
        localObject2 = new double[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = Double.longBitsToDouble(paramClassReader.readLong(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0]))));
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 70:
        localObject2 = new float[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = Float.intBitsToFloat(paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0]))));
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 66:
        localObject2 = new byte[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = (byte)paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0])));
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 67:
        localObject2 = new char[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = (char)paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0])));
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 83:
        localObject2 = new short[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = (short)paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0])));
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 90:
        localObject2 = new boolean[j];
        for (m = 0; m < j; m++)
        {
          paramArrayOfInt[0] += 1;
          localObject2[m] = (paramClassReader.readInt(paramClassReader.getItem(paramClassReader.readUnsignedShort(paramArrayOfInt[0]))) != 0 ? 1 : 0);
          paramArrayOfInt[0] += 2;
        }
        localObject1 = localObject2;
        break;
      case 69:
      case 71:
      case 72:
      case 75:
      case 76:
      case 77:
      case 78:
      case 79:
      case 80:
      case 81:
      case 82:
      case 84:
      case 85:
      case 86:
      case 87:
      case 88:
      case 89:
      default:
        localObject2 = new Object[j];
        localObject1 = localObject2;
        for (m = 0; m < j; m++)
          localObject2[m] = readValue(paramClassReader, paramArrayOfInt, paramArrayOfChar);
      }
    case 65:
    case 69:
    case 71:
    case 72:
    case 75:
    case 76:
    case 77:
    case 78:
    case 79:
    case 80:
    case 81:
    case 82:
    case 84:
    case 85:
    case 86:
    case 87:
    case 88:
    case 89:
    case 92:
    case 93:
    case 94:
    case 95:
    case 96:
    case 97:
    case 98:
    case 100:
    case 102:
    case 103:
    case 104:
    case 105:
    case 106:
    case 107:
    case 108:
    case 109:
    case 110:
    case 111:
    case 112:
    case 113:
    case 114:
    }
    return localObject1;
  }

  protected static ByteVector writeValue(ByteVector paramByteVector, Object paramObject, ClassWriter paramClassWriter)
  {
    if ((paramObject instanceof String))
    {
      paramByteVector.putByte(115);
      paramByteVector.putShort(paramClassWriter.newUTF8((String)paramObject));
    }
    else if ((paramObject instanceof EnumConstValue))
    {
      paramByteVector.putByte(101);
      paramByteVector.putShort(paramClassWriter.newUTF8(((EnumConstValue)paramObject).typeName));
      paramByteVector.putShort(paramClassWriter.newUTF8(((EnumConstValue)paramObject).constName));
    }
    else if ((paramObject instanceof Type))
    {
      paramByteVector.putByte(99);
      paramByteVector.putShort(paramClassWriter.newUTF8(((Type)paramObject).getDescriptor()));
    }
    else if ((paramObject instanceof Annotation))
    {
      paramByteVector.putByte(64);
      ((Annotation)paramObject).write(paramByteVector, paramClassWriter);
    }
    else
    {
      Object localObject;
      int j;
      if ((paramObject instanceof Object[]))
      {
        paramByteVector.putByte(91);
        localObject = (Object[])(Object[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
          writeValue(paramByteVector, localObject[j], paramClassWriter);
      }
      else if ((paramObject instanceof byte[]))
      {
        paramByteVector.putByte(91);
        localObject = (byte[])(byte[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(66);
          paramByteVector.putShort(paramClassWriter.newConstInt(localObject[j]));
        }
      }
      else if ((paramObject instanceof short[]))
      {
        paramByteVector.putByte(91);
        localObject = (short[])(short[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(83);
          paramByteVector.putShort(paramClassWriter.newConstInt(localObject[j]));
        }
      }
      else if ((paramObject instanceof int[]))
      {
        paramByteVector.putByte(91);
        localObject = (int[])(int[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(73);
          paramByteVector.putShort(paramClassWriter.newConstInt(localObject[j]));
        }
      }
      else if ((paramObject instanceof char[]))
      {
        paramByteVector.putByte(91);
        localObject = (char[])(char[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(67);
          paramByteVector.putShort(paramClassWriter.newConstInt(localObject[j]));
        }
      }
      else if ((paramObject instanceof boolean[]))
      {
        paramByteVector.putByte(91);
        localObject = (boolean[])(boolean[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(90);
          paramByteVector.putShort(paramClassWriter.newConstInt(localObject[j] != 0 ? 1 : 0));
        }
      }
      else if ((paramObject instanceof long[]))
      {
        paramByteVector.putByte(91);
        localObject = (long[])(long[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(74);
          paramByteVector.putShort(paramClassWriter.newConstLong(localObject[j]));
        }
      }
      else if ((paramObject instanceof float[]))
      {
        paramByteVector.putByte(91);
        localObject = (float[])(float[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(70);
          paramByteVector.putShort(paramClassWriter.newConstFloat(localObject[j]));
        }
      }
      else if ((paramObject instanceof double[]))
      {
        paramByteVector.putByte(91);
        localObject = (double[])(double[])paramObject;
        paramByteVector.putShort(localObject.length);
        for (j = 0; j < localObject.length; j++)
        {
          paramByteVector.putByte(68);
          paramByteVector.putShort(paramClassWriter.newConstDouble(localObject[j]));
        }
      }
      else
      {
        int i = -1;
        if ((paramObject instanceof Integer))
          i = 73;
        else if ((paramObject instanceof Byte))
          i = 66;
        else if ((paramObject instanceof Character))
          i = 67;
        else if ((paramObject instanceof Double))
          i = 68;
        else if ((paramObject instanceof Float))
          i = 70;
        else if ((paramObject instanceof Long))
          i = 74;
        else if ((paramObject instanceof Short))
          i = 83;
        else if ((paramObject instanceof Boolean))
          i = 90;
        paramByteVector.putByte(i);
        paramByteVector.putShort(paramClassWriter.newConst(paramObject));
      }
    }
    return (ByteVector)paramByteVector;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("@").append(this.type);
    if (this.elementValues.size() > 0)
    {
      localStringBuffer.append(" ( ");
      String str1 = "";
      for (int i = 0; i < this.elementValues.size(); i++)
      {
        Object[] arrayOfObject1 = (Object[])(Object[])this.elementValues.get(i);
        if ((this.elementValues.size() != 1) && (!"value".equals(this.elementValues.get(0))))
          localStringBuffer.append(str1).append(arrayOfObject1[0]).append(" = ");
        if ((arrayOfObject1[1] instanceof Object[]))
        {
          Object[] arrayOfObject2 = (Object[])(Object[])arrayOfObject1[1];
          localStringBuffer.append("{");
          String str2 = "";
          for (int j = 0; j < arrayOfObject2.length; j++)
          {
            localStringBuffer.append(str2).append(arrayOfObject2[j]);
            str2 = ", ";
          }
          localStringBuffer.append("}");
        }
        else
        {
          localStringBuffer.append(arrayOfObject1[1]);
        }
        str1 = ", ";
      }
      localStringBuffer.append(" )");
    }
    return localStringBuffer.toString();
  }

  public static class EnumConstValue
  {
    public String typeName;
    public String constName;

    public EnumConstValue(String paramString1, String paramString2)
    {
      this.typeName = paramString1;
      this.constName = paramString2;
    }

    public String toString()
    {
      return this.typeName + ":" + this.constName;
    }
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.Annotation
 * JD-Core Version:    0.6.0
 */