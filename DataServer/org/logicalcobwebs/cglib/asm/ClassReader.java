package org.logicalcobwebs.cglib.asm;

import java.io.IOException;
import java.io.InputStream;

public class ClassReader
{
  public final byte[] b;
  private int[] items;
  private String[] strings;
  private int maxStringLength;
  private int header;

  public ClassReader(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public ClassReader(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.b = paramArrayOfByte;
    this.items = new int[readUnsignedShort(paramInt1 + 8)];
    this.strings = new String[this.items.length];
    int i = 0;
    int j = paramInt1 + 10;
    for (int k = 1; k < this.items.length; k++)
    {
      this.items[k] = (j + 1);
      int m = paramArrayOfByte[j];
      int n;
      switch (m)
      {
      case 3:
      case 4:
      case 9:
      case 10:
      case 11:
      case 12:
        n = 5;
        break;
      case 5:
      case 6:
        n = 9;
        k++;
        break;
      case 1:
        n = 3 + readUnsignedShort(j + 1);
        i = n > i ? n : i;
        break;
      case 2:
      case 7:
      case 8:
      default:
        n = 3;
      }
      j += n;
    }
    this.maxStringLength = i;
    this.header = j;
  }

  public ClassReader(InputStream paramInputStream)
    throws IOException
  {
    this(readClass(paramInputStream));
  }

  public ClassReader(String paramString)
    throws IOException
  {
    this(ClassLoader.getSystemResourceAsStream(paramString.replace('.', '/') + ".class"));
  }

  private static byte[] readClass(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream == null)
      throw new IOException("Class not found");
    Object localObject = new byte[paramInputStream.available()];
    int i = 0;
    while (true)
    {
      int j = paramInputStream.read(localObject, i, localObject.length - i);
      byte[] arrayOfByte;
      if (j == -1)
      {
        if (i < localObject.length)
        {
          arrayOfByte = new byte[i];
          System.arraycopy(localObject, 0, arrayOfByte, 0, i);
          localObject = arrayOfByte;
        }
        return localObject;
      }
      i += j;
      if (i == localObject.length)
      {
        arrayOfByte = new byte[localObject.length + 1000];
        System.arraycopy(localObject, 0, arrayOfByte, 0, i);
        localObject = arrayOfByte;
      }
    }
  }

  public void accept(ClassVisitor paramClassVisitor, boolean paramBoolean)
  {
    accept(paramClassVisitor, new Attribute[0], paramBoolean);
  }

  public void accept(ClassVisitor paramClassVisitor, Attribute[] paramArrayOfAttribute, boolean paramBoolean)
  {
    byte[] arrayOfByte = this.b;
    char[] arrayOfChar = new char[this.maxStringLength];
    int m = this.header;
    int i2 = readInt(4);
    int i3 = readUnsignedShort(m);
    String str1 = readClass(m + 2, arrayOfChar);
    int n = this.items[readUnsignedShort(m + 4)];
    String str2 = n == 0 ? null : readUTF8(n, arrayOfChar);
    String[] arrayOfString = new String[readUnsignedShort(m + 6)];
    String str3 = null;
    Object localObject2 = null;
    int i1 = 0;
    m += 8;
    for (int i = 0; i < arrayOfString.length; i++)
    {
      arrayOfString[i] = readClass(m, arrayOfChar);
      m += 2;
    }
    n = m;
    i = readUnsignedShort(n);
    n += 2;
    int j;
    while (i > 0)
    {
      j = readUnsignedShort(n + 6);
      n += 8;
      while (j > 0)
      {
        n += 6 + readInt(n + 2);
        j--;
      }
      i--;
    }
    i = readUnsignedShort(n);
    n += 2;
    while (i > 0)
    {
      j = readUnsignedShort(n + 6);
      n += 8;
      while (j > 0)
      {
        n += 6 + readInt(n + 2);
        j--;
      }
      i--;
    }
    i = readUnsignedShort(n);
    n += 2;
    while (i > 0)
    {
      localObject3 = readUTF8(n, arrayOfChar);
      if (((String)localObject3).equals("SourceFile"))
      {
        str3 = readUTF8(n + 6, arrayOfChar);
      }
      else if (((String)localObject3).equals("Deprecated"))
      {
        i3 |= 131072;
      }
      else if (((String)localObject3).equals("Synthetic"))
      {
        i3 |= 4096;
      }
      else if (((String)localObject3).equals("InnerClasses"))
      {
        i1 = n + 6;
      }
      else
      {
        localObject1 = readAttribute(paramArrayOfAttribute, (String)localObject3, n + 6, readInt(n + 2), arrayOfChar, -1, null);
        if (localObject1 != null)
        {
          ((Attribute)localObject1).next = localObject2;
          localObject2 = localObject1;
        }
      }
      n += 6 + readInt(n + 2);
      i--;
    }
    paramClassVisitor.visit(i2, i3, str1, str2, arrayOfString, str3);
    if (i1 != 0)
    {
      i = readUnsignedShort(i1);
      i1 += 2;
      while (i > 0)
      {
        paramClassVisitor.visitInnerClass(readUnsignedShort(i1) == 0 ? null : readClass(i1, arrayOfChar), readUnsignedShort(i1 + 2) == 0 ? null : readClass(i1 + 2, arrayOfChar), readUnsignedShort(i1 + 4) == 0 ? null : readUTF8(i1 + 4, arrayOfChar), readUnsignedShort(i1 + 6));
        i1 += 8;
        i--;
      }
    }
    i = readUnsignedShort(m);
    m += 2;
    Object localObject4;
    Object localObject5;
    Object localObject7;
    while (i > 0)
    {
      i3 = readUnsignedShort(m);
      localObject3 = readUTF8(m + 2, arrayOfChar);
      localObject4 = readUTF8(m + 4, arrayOfChar);
      localObject5 = null;
      int i4 = 0;
      j = readUnsignedShort(m + 6);
      m += 8;
      while (j > 0)
      {
        localObject7 = readUTF8(m, arrayOfChar);
        if (((String)localObject7).equals("ConstantValue"))
        {
          i4 = readUnsignedShort(m + 6);
        }
        else if (((String)localObject7).equals("Synthetic"))
        {
          i3 |= 4096;
        }
        else if (((String)localObject7).equals("Deprecated"))
        {
          i3 |= 131072;
        }
        else
        {
          localObject1 = readAttribute(paramArrayOfAttribute, (String)localObject7, m + 6, readInt(m + 2), arrayOfChar, -1, null);
          if (localObject1 != null)
          {
            ((Attribute)localObject1).next = localObject5;
            localObject5 = localObject1;
          }
        }
        m += 6 + readInt(m + 2);
        j--;
      }
      localObject7 = i4 == 0 ? null : readConst(i4, arrayOfChar);
      paramClassVisitor.visitField(i3, (String)localObject3, (String)localObject4, localObject7, localObject5);
      i--;
    }
    i = readUnsignedShort(m);
    m += 2;
    while (i > 0)
    {
      i3 = readUnsignedShort(m);
      localObject3 = readUTF8(m + 2, arrayOfChar);
      localObject4 = readUTF8(m + 4, arrayOfChar);
      localObject5 = null;
      Object localObject6 = null;
      n = 0;
      i1 = 0;
      j = readUnsignedShort(m + 6);
      m += 8;
      while (j > 0)
      {
        localObject7 = readUTF8(m, arrayOfChar);
        m += 2;
        int i5 = readInt(m);
        m += 4;
        if (((String)localObject7).equals("Code"))
        {
          n = m;
        }
        else if (((String)localObject7).equals("Exceptions"))
        {
          i1 = m;
        }
        else if (((String)localObject7).equals("Synthetic"))
        {
          i3 |= 4096;
        }
        else if (((String)localObject7).equals("Deprecated"))
        {
          i3 |= 131072;
        }
        else
        {
          localObject1 = readAttribute(paramArrayOfAttribute, (String)localObject7, m, i5, arrayOfChar, -1, null);
          if (localObject1 != null)
          {
            ((Attribute)localObject1).next = localObject5;
            localObject5 = localObject1;
          }
        }
        m += i5;
        j--;
      }
      if (i1 == 0)
      {
        localObject7 = null;
      }
      else
      {
        localObject7 = new String[readUnsignedShort(i1)];
        i1 += 2;
        for (j = 0; j < localObject7.length; j++)
        {
          localObject7[j] = readClass(i1, arrayOfChar);
          i1 += 2;
        }
      }
      CodeVisitor localCodeVisitor = paramClassVisitor.visitMethod(i3, (String)localObject3, (String)localObject4, localObject7, localObject5);
      if ((localCodeVisitor != null) && (n != 0))
      {
        int i6 = readUnsignedShort(n);
        int i7 = readUnsignedShort(n + 2);
        int i8 = readInt(n + 4);
        n += 8;
        int i9 = n;
        int i10 = n + i8;
        Label[] arrayOfLabel1 = new Label[i8 + 1];
        int i11;
        while (n < i10)
        {
          int i12 = arrayOfByte[n] & 0xFF;
          switch (ClassWriter.TYPE[i12])
          {
          case 0:
          case 4:
            n++;
            break;
          case 8:
            i11 = n - i9 + readShort(n + 1);
            if (arrayOfLabel1[i11] == null)
              arrayOfLabel1[i11] = new Label();
            n += 3;
            break;
          case 9:
            i11 = n - i9 + readInt(n + 1);
            if (arrayOfLabel1[i11] == null)
              arrayOfLabel1[i11] = new Label();
            n += 5;
            break;
          case 16:
            i12 = arrayOfByte[(n + 1)] & 0xFF;
            if (i12 == 132)
              n += 6;
            else
              n += 4;
            break;
          case 13:
            i1 = n - i9;
            n = n + 4 - (i1 & 0x3);
            i11 = i1 + readInt(n);
            n += 4;
            if (arrayOfLabel1[i11] == null)
              arrayOfLabel1[i11] = new Label();
            j = readInt(n);
            n += 4;
            j = readInt(n) - j + 1;
            n += 4;
          case 14:
          case 1:
          case 3:
          case 10:
          case 2:
          case 5:
          case 6:
          case 11:
          case 12:
          case 7:
          case 15:
          default:
            while (j > 0)
            {
              i11 = i1 + readInt(n);
              n += 4;
              if (arrayOfLabel1[i11] == null)
                arrayOfLabel1[i11] = new Label();
              j--;
              continue;
              i1 = n - i9;
              n = n + 4 - (i1 & 0x3);
              i11 = i1 + readInt(n);
              n += 4;
              if (arrayOfLabel1[i11] == null)
                arrayOfLabel1[i11] = new Label();
              j = readInt(n);
              n += 4;
              while (j > 0)
              {
                n += 4;
                i11 = i1 + readInt(n);
                n += 4;
                if (arrayOfLabel1[i11] == null)
                  arrayOfLabel1[i11] = new Label();
                j--;
                continue;
                n += 2;
                break;
                n += 3;
                break;
                n += 5;
                break;
                n += 4;
              }
            }
          }
        }
        j = readUnsignedShort(n);
        n += 2;
        while (j > 0)
        {
          i11 = readUnsignedShort(n);
          if (arrayOfLabel1[i11] == null)
            arrayOfLabel1[i11] = new Label();
          i11 = readUnsignedShort(n + 2);
          if (arrayOfLabel1[i11] == null)
            arrayOfLabel1[i11] = new Label();
          i11 = readUnsignedShort(n + 4);
          if (arrayOfLabel1[i11] == null)
            arrayOfLabel1[i11] = new Label();
          n += 8;
          j--;
        }
        j = readUnsignedShort(n);
        n += 2;
        int k;
        while (j > 0)
        {
          localObject8 = readUTF8(n, arrayOfChar);
          if (((String)localObject8).equals("LocalVariableTable"))
          {
            if (!paramBoolean)
            {
              k = readUnsignedShort(n + 6);
              i1 = n + 8;
              while (k > 0)
              {
                i11 = readUnsignedShort(i1);
                if (arrayOfLabel1[i11] == null)
                  arrayOfLabel1[i11] = new Label();
                i11 += readUnsignedShort(i1 + 2);
                if (arrayOfLabel1[i11] == null)
                  arrayOfLabel1[i11] = new Label();
                i1 += 10;
                k--;
              }
            }
          }
          else if (((String)localObject8).equals("LineNumberTable"))
          {
            if (!paramBoolean)
            {
              k = readUnsignedShort(n + 6);
              i1 = n + 8;
              while (k > 0)
              {
                i11 = readUnsignedShort(i1);
                if (arrayOfLabel1[i11] == null)
                  arrayOfLabel1[i11] = new Label();
                arrayOfLabel1[i11].line = readUnsignedShort(i1 + 2);
                i1 += 4;
                k--;
              }
            }
          }
          else
            for (k = 0; k < paramArrayOfAttribute.length; k++)
            {
              if (!paramArrayOfAttribute[k].type.equals(localObject8))
                continue;
              localObject1 = paramArrayOfAttribute[k].read(this, n + 6, readInt(n + 2), arrayOfChar, i9 - 8, arrayOfLabel1);
              if (localObject1 == null)
                continue;
              ((Attribute)localObject1).next = localObject6;
              localObject6 = localObject1;
            }
          n += 6 + readInt(n + 2);
          j--;
        }
        n = i9;
        while (n < i10)
        {
          i1 = n - i9;
          localObject8 = arrayOfLabel1[i1];
          if (localObject8 != null)
          {
            localCodeVisitor.visitLabel((Label)localObject8);
            if ((!paramBoolean) && (((Label)localObject8).line > 0))
              localCodeVisitor.visitLineNumber(((Label)localObject8).line, (Label)localObject8);
          }
          int i13 = arrayOfByte[n] & 0xFF;
          switch (ClassWriter.TYPE[i13])
          {
          case 0:
            localCodeVisitor.visitInsn(i13);
            n++;
            break;
          case 4:
            if (i13 > 54)
            {
              i13 -= 59;
              localCodeVisitor.visitVarInsn(54 + (i13 >> 2), i13 & 0x3);
            }
            else
            {
              i13 -= 26;
              localCodeVisitor.visitVarInsn(21 + (i13 >> 2), i13 & 0x3);
            }
            n++;
            break;
          case 8:
            localCodeVisitor.visitJumpInsn(i13, arrayOfLabel1[(i1 + readShort(n + 1))]);
            n += 3;
            break;
          case 9:
            localCodeVisitor.visitJumpInsn(i13, arrayOfLabel1[(i1 + readInt(n + 1))]);
            n += 5;
            break;
          case 16:
            i13 = arrayOfByte[(n + 1)] & 0xFF;
            if (i13 == 132)
            {
              localCodeVisitor.visitIincInsn(readUnsignedShort(n + 2), readShort(n + 4));
              n += 6;
            }
            else
            {
              localCodeVisitor.visitVarInsn(i13, readUnsignedShort(n + 2));
              n += 4;
            }
            break;
          case 13:
            n = n + 4 - (i1 & 0x3);
            i11 = i1 + readInt(n);
            n += 4;
            int i14 = readInt(n);
            n += 4;
            int i15 = readInt(n);
            n += 4;
            Label[] arrayOfLabel2 = new Label[i15 - i14 + 1];
            for (j = 0; j < arrayOfLabel2.length; j++)
            {
              arrayOfLabel2[j] = arrayOfLabel1[(i1 + readInt(n))];
              n += 4;
            }
            localCodeVisitor.visitTableSwitchInsn(i14, i15, arrayOfLabel1[i11], arrayOfLabel2);
            break;
          case 14:
            n = n + 4 - (i1 & 0x3);
            i11 = i1 + readInt(n);
            n += 4;
            j = readInt(n);
            n += 4;
            int[] arrayOfInt = new int[j];
            Label[] arrayOfLabel3 = new Label[j];
            for (j = 0; j < arrayOfInt.length; j++)
            {
              arrayOfInt[j] = readInt(n);
              n += 4;
              arrayOfLabel3[j] = arrayOfLabel1[(i1 + readInt(n))];
              n += 4;
            }
            localCodeVisitor.visitLookupSwitchInsn(arrayOfLabel1[i11], arrayOfInt, arrayOfLabel3);
            break;
          case 3:
            localCodeVisitor.visitVarInsn(i13, arrayOfByte[(n + 1)] & 0xFF);
            n += 2;
            break;
          case 1:
            localCodeVisitor.visitIntInsn(i13, arrayOfByte[(n + 1)]);
            n += 2;
            break;
          case 2:
            localCodeVisitor.visitIntInsn(i13, readShort(n + 1));
            n += 3;
            break;
          case 10:
            localCodeVisitor.visitLdcInsn(readConst(arrayOfByte[(n + 1)] & 0xFF, arrayOfChar));
            n += 2;
            break;
          case 11:
            localCodeVisitor.visitLdcInsn(readConst(readUnsignedShort(n + 1), arrayOfChar));
            n += 3;
            break;
          case 6:
          case 7:
            int i17 = this.items[readUnsignedShort(n + 1)];
            String str4 = readClass(i17, arrayOfChar);
            i17 = this.items[readUnsignedShort(i17 + 2)];
            String str5 = readUTF8(i17, arrayOfChar);
            String str6 = readUTF8(i17 + 2, arrayOfChar);
            if (i13 < 182)
              localCodeVisitor.visitFieldInsn(i13, str4, str5, str6);
            else
              localCodeVisitor.visitMethodInsn(i13, str4, str5, str6);
            if (i13 == 185)
              n += 5;
            else
              n += 3;
            break;
          case 5:
            localCodeVisitor.visitTypeInsn(i13, readClass(n + 1, arrayOfChar));
            n += 3;
            break;
          case 12:
            localCodeVisitor.visitIincInsn(arrayOfByte[(n + 1)] & 0xFF, arrayOfByte[(n + 2)]);
            n += 3;
            break;
          case 15:
          default:
            localCodeVisitor.visitMultiANewArrayInsn(readClass(n + 1, arrayOfChar), arrayOfByte[(n + 3)] & 0xFF);
            n += 4;
          }
        }
        Object localObject8 = arrayOfLabel1[(i10 - i9)];
        if (localObject8 != null)
          localCodeVisitor.visitLabel((Label)localObject8);
        j = readUnsignedShort(n);
        n += 2;
        Object localObject9;
        Label localLabel1;
        Label localLabel2;
        while (j > 0)
        {
          localObject9 = arrayOfLabel1[readUnsignedShort(n)];
          localLabel1 = arrayOfLabel1[readUnsignedShort(n + 2)];
          localLabel2 = arrayOfLabel1[readUnsignedShort(n + 4)];
          int i16 = readUnsignedShort(n + 6);
          if (i16 == 0)
            localCodeVisitor.visitTryCatchBlock((Label)localObject9, localLabel1, localLabel2, null);
          else
            localCodeVisitor.visitTryCatchBlock((Label)localObject9, localLabel1, localLabel2, readUTF8(this.items[i16], arrayOfChar));
          n += 8;
          j--;
        }
        j = readUnsignedShort(n);
        n += 2;
        if (!paramBoolean)
          while (j > 0)
          {
            localObject9 = readUTF8(n, arrayOfChar);
            if (((String)localObject9).equals("LocalVariableTable"))
            {
              k = readUnsignedShort(n + 6);
              i1 = n + 8;
              while (k > 0)
              {
                i11 = readUnsignedShort(i1);
                localLabel1 = arrayOfLabel1[i11];
                i11 += readUnsignedShort(i1 + 2);
                localLabel2 = arrayOfLabel1[i11];
                localCodeVisitor.visitLocalVariable(readUTF8(i1 + 4, arrayOfChar), readUTF8(i1 + 6, arrayOfChar), localLabel1, localLabel2, readUnsignedShort(i1 + 8));
                i1 += 10;
                k--;
              }
            }
            n += 6 + readInt(n + 2);
            j--;
          }
        while (localObject6 != null)
        {
          localObject1 = localObject6.next;
          localObject6.next = null;
          localCodeVisitor.visitAttribute(localObject6);
          localObject6 = localObject1;
        }
        localCodeVisitor.visitMaxs(i6, i7);
      }
      i--;
    }
    Object localObject3 = null;
    for (Object localObject1 = localObject2; localObject1 != null; localObject1 = localObject4)
    {
      localObject4 = ((Attribute)localObject1).next;
      ((Attribute)localObject1).next = ((Attribute)localObject3);
      localObject3 = localObject1;
    }
    while (localObject3 != null)
    {
      localObject1 = ((Attribute)localObject3).next;
      ((Attribute)localObject3).next = null;
      paramClassVisitor.visitAttribute((Attribute)localObject3);
      localObject3 = localObject1;
    }
    paramClassVisitor.visitEnd();
  }

  public int getItem(int paramInt)
  {
    return this.items[paramInt];
  }

  public int readByte(int paramInt)
  {
    return this.b[paramInt] & 0xFF;
  }

  public int readUnsignedShort(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF;
  }

  public short readShort(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (short)((arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF);
  }

  public int readInt(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 24 | (arrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (arrayOfByte[(paramInt + 2)] & 0xFF) << 8 | arrayOfByte[(paramInt + 3)] & 0xFF;
  }

  public long readLong(int paramInt)
  {
    long l1 = readInt(paramInt);
    long l2 = readInt(paramInt + 4) & 0xFFFFFFFF;
    return l1 << 32 | l2;
  }

  public String readUTF8(int paramInt, char[] paramArrayOfChar)
  {
    int i = readUnsignedShort(paramInt);
    String str = this.strings[i];
    if (str != null)
      return str;
    paramInt = this.items[i];
    int j = readUnsignedShort(paramInt);
    paramInt += 2;
    int k = paramInt + j;
    byte[] arrayOfByte = this.b;
    int m = 0;
    while (paramInt < k)
    {
      int n = arrayOfByte[(paramInt++)] & 0xFF;
      int i1;
      switch (n >> 4)
      {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        paramArrayOfChar[(m++)] = (char)n;
        break;
      case 12:
      case 13:
        i1 = arrayOfByte[(paramInt++)];
        paramArrayOfChar[(m++)] = (char)((n & 0x1F) << 6 | i1 & 0x3F);
        break;
      case 8:
      case 9:
      case 10:
      case 11:
      default:
        i1 = arrayOfByte[(paramInt++)];
        int i2 = arrayOfByte[(paramInt++)];
        paramArrayOfChar[(m++)] = (char)((n & 0xF) << 12 | (i1 & 0x3F) << 6 | i2 & 0x3F);
      }
    }
    str = new String(paramArrayOfChar, 0, m);
    this.strings[i] = str;
    return str;
  }

  public String readClass(int paramInt, char[] paramArrayOfChar)
  {
    return readUTF8(this.items[readUnsignedShort(paramInt)], paramArrayOfChar);
  }

  public Object readConst(int paramInt, char[] paramArrayOfChar)
  {
    int i = this.items[paramInt];
    switch (this.b[(i - 1)])
    {
    case 3:
      return new Integer(readInt(i));
    case 4:
      return new Float(Float.intBitsToFloat(readInt(i)));
    case 5:
      return new Long(readLong(i));
    case 6:
      return new Double(Double.longBitsToDouble(readLong(i)));
    case 7:
      String str = readUTF8(i, paramArrayOfChar);
      return Type.getType("L" + str + ";");
    }
    return readUTF8(i, paramArrayOfChar);
  }

  protected Attribute readAttribute(Attribute[] paramArrayOfAttribute, String paramString, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    for (int i = 0; i < paramArrayOfAttribute.length; i++)
      if (paramArrayOfAttribute[i].type.equals(paramString))
        return paramArrayOfAttribute[i].read(this, paramInt1, paramInt2, paramArrayOfChar, paramInt3, paramArrayOfLabel);
    return new Attribute(paramString);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.ClassReader
 * JD-Core Version:    0.6.0
 */