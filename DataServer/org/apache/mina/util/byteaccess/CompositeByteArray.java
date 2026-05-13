/*      */ package org.apache.mina.util.byteaccess;
/*      */ 
/*      */ import java.nio.ByteOrder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import org.apache.mina.core.buffer.IoBuffer;
/*      */ 
/*      */ public final class CompositeByteArray extends AbstractByteArray
/*      */ {
/*   81 */   private final ByteArrayList bas = new ByteArrayList();
/*      */   private ByteOrder order;
/*      */   private final ByteArrayFactory byteArrayFactory;
/*      */ 
/*      */   public CompositeByteArray()
/*      */   {
/*   98 */     this(null);
/*      */   }
/*      */ 
/*      */   public CompositeByteArray(ByteArrayFactory byteArrayFactory)
/*      */   {
/*  110 */     this.byteArrayFactory = byteArrayFactory;
/*      */   }
/*      */ 
/*      */   public ByteArray getFirst()
/*      */   {
/*  121 */     if (this.bas.isEmpty())
/*      */     {
/*  123 */       return null;
/*      */     }
/*      */ 
/*  127 */     return this.bas.getFirst().getByteArray();
/*      */   }
/*      */ 
/*      */   public void addFirst(ByteArray ba)
/*      */   {
/*  140 */     addHook(ba);
/*  141 */     this.bas.addFirst(ba);
/*      */   }
/*      */ 
/*      */   public ByteArray removeFirst()
/*      */   {
/*  152 */     ByteArrayList.Node node = this.bas.removeFirst();
/*  153 */     return node == null ? null : node.getByteArray();
/*      */   }
/*      */ 
/*      */   public ByteArray removeTo(int index)
/*      */   {
/*  166 */     if ((index < first()) || (index > last()))
/*      */     {
/*  168 */       throw new IndexOutOfBoundsException();
/*      */     }
/*      */ 
/*  177 */     CompositeByteArray prefix = new CompositeByteArray(this.byteArrayFactory);
/*  178 */     int remaining = index - first();
/*  179 */     while (remaining > 0)
/*      */     {
/*  181 */       ByteArray component = removeFirst();
/*  182 */       if (component.last() <= remaining)
/*      */       {
/*  185 */         prefix.addLast(component);
/*  186 */         remaining -= component.last();
/*      */       }
/*      */       else
/*      */       {
/*  194 */         IoBuffer bb = component.getSingleIoBuffer();
/*      */ 
/*  196 */         int originalLimit = bb.limit();
/*      */ 
/*  198 */         bb.position(0);
/*      */ 
/*  200 */         bb.limit(remaining);
/*      */ 
/*  202 */         IoBuffer bb1 = bb.slice();
/*      */ 
/*  204 */         bb.position(remaining);
/*      */ 
/*  206 */         bb.limit(originalLimit);
/*      */ 
/*  208 */         IoBuffer bb2 = bb.slice();
/*      */ 
/*  210 */         ByteArray ba1 = new BufferByteArray(bb1)
/*      */         {
/*      */           public void free()
/*      */           {
/*      */           }
/*      */         };
/*  219 */         prefix.addLast(ba1);
/*  220 */         remaining -= ba1.last();
/*      */ 
/*  223 */         ByteArray componentFinal = component;
/*  224 */         ByteArray ba2 = new BufferByteArray(bb2, componentFinal)
/*      */         {
/*      */           public void free()
/*      */           {
/*  229 */             this.val$componentFinal.free();
/*      */           }
/*      */         };
/*  233 */         addFirst(ba2);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  238 */     return prefix;
/*      */   }
/*      */ 
/*      */   public void addLast(ByteArray ba)
/*      */   {
/*  249 */     addHook(ba);
/*  250 */     this.bas.addLast(ba);
/*      */   }
/*      */ 
/*      */   public ByteArray removeLast()
/*      */   {
/*  261 */     ByteArrayList.Node node = this.bas.removeLast();
/*  262 */     return node == null ? null : node.getByteArray();
/*      */   }
/*      */ 
/*      */   public void free()
/*      */   {
/*  271 */     while (!this.bas.isEmpty())
/*      */     {
/*  273 */       ByteArrayList.Node node = this.bas.getLast();
/*  274 */       node.getByteArray().free();
/*  275 */       this.bas.removeLast();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkBounds(int index, int accessSize)
/*      */   {
/*  282 */     int lower = index;
/*  283 */     int upper = index + accessSize;
/*  284 */     if (lower < first())
/*      */     {
/*  286 */       throw new IndexOutOfBoundsException("Index " + lower + " less than start " + first() + ".");
/*      */     }
/*  288 */     if (upper > last())
/*      */     {
/*  290 */       throw new IndexOutOfBoundsException("Index " + upper + " greater than length " + last() + ".");
/*      */     }
/*      */   }
/*      */ 
/*      */   public Iterable<IoBuffer> getIoBuffers()
/*      */   {
/*  300 */     if (this.bas.isEmpty())
/*      */     {
/*  302 */       return Collections.emptyList();
/*      */     }
/*  304 */     Collection result = new ArrayList();
/*  305 */     ByteArrayList.Node node = this.bas.getFirst();
/*  306 */     for (IoBuffer bb : node.getByteArray().getIoBuffers())
/*      */     {
/*  308 */       result.add(bb);
/*      */     }
/*  310 */     while (node.hasNextNode())
/*      */     {
/*  312 */       node = node.getNextNode();
/*  313 */       for (IoBuffer bb : node.getByteArray().getIoBuffers())
/*      */       {
/*  315 */         result.add(bb);
/*      */       }
/*      */     }
/*  318 */     return result;
/*      */   }
/*      */ 
/*      */   public IoBuffer getSingleIoBuffer()
/*      */   {
/*  327 */     if (this.byteArrayFactory == null)
/*      */     {
/*  329 */       throw new IllegalStateException("Can't get single buffer from CompositeByteArray unless it has a ByteArrayFactory.");
/*      */     }
/*      */ 
/*  332 */     if (this.bas.isEmpty())
/*      */     {
/*  334 */       ByteArray ba = this.byteArrayFactory.create(1);
/*  335 */       return ba.getSingleIoBuffer();
/*      */     }
/*  337 */     int actualLength = last() - first();
/*      */ 
/*  339 */     ByteArrayList.Node node = this.bas.getFirst();
/*  340 */     ByteArray ba = node.getByteArray();
/*  341 */     if (ba.last() == actualLength)
/*      */     {
/*  343 */       return ba.getSingleIoBuffer();
/*      */     }
/*      */ 
/*  347 */     ByteArray target = this.byteArrayFactory.create(actualLength);
/*  348 */     IoBuffer bb = target.getSingleIoBuffer();
/*  349 */     ByteArray.Cursor cursor = cursor();
/*  350 */     cursor.put(bb);
/*  351 */     while (!this.bas.isEmpty())
/*      */     {
/*  353 */       ByteArrayList.Node node = this.bas.getLast();
/*  354 */       ByteArray component = node.getByteArray();
/*  355 */       this.bas.removeLast();
/*  356 */       component.free();
/*      */     }
/*  358 */     this.bas.addLast(target);
/*  359 */     return bb;
/*      */   }
/*      */ 
/*      */   public ByteArray.Cursor cursor()
/*      */   {
/*  368 */     return new CursorImpl();
/*      */   }
/*      */ 
/*      */   public ByteArray.Cursor cursor(int index)
/*      */   {
/*  377 */     return new CursorImpl(index);
/*      */   }
/*      */ 
/*      */   public ByteArray.Cursor cursor(CursorListener listener)
/*      */   {
/*  390 */     return new CursorImpl(listener);
/*      */   }
/*      */ 
/*      */   public ByteArray.Cursor cursor(int index, CursorListener listener)
/*      */   {
/*  404 */     return new CursorImpl(index, listener);
/*      */   }
/*      */ 
/*      */   public ByteArray slice(int index, int length)
/*      */   {
/*  413 */     return cursor(index).slice(length);
/*      */   }
/*      */ 
/*      */   public byte get(int index)
/*      */   {
/*  422 */     return cursor(index).get();
/*      */   }
/*      */ 
/*      */   public void put(int index, byte b)
/*      */   {
/*  431 */     cursor(index).put(b);
/*      */   }
/*      */ 
/*      */   public void get(int index, IoBuffer bb)
/*      */   {
/*  440 */     cursor(index).get(bb);
/*      */   }
/*      */ 
/*      */   public void put(int index, IoBuffer bb)
/*      */   {
/*  449 */     cursor(index).put(bb);
/*      */   }
/*      */ 
/*      */   public int first()
/*      */   {
/*  458 */     return this.bas.firstByte();
/*      */   }
/*      */ 
/*      */   public int last()
/*      */   {
/*  467 */     return this.bas.lastByte();
/*      */   }
/*      */ 
/*      */   private void addHook(ByteArray ba)
/*      */   {
/*  482 */     if (ba.first() != 0)
/*      */     {
/*  484 */       throw new IllegalArgumentException("Cannot add byte array that doesn't start from 0: " + ba.first());
/*      */     }
/*      */ 
/*  487 */     if (this.order == null)
/*      */     {
/*  489 */       this.order = ba.order();
/*      */     }
/*  491 */     else if (!this.order.equals(ba.order()))
/*      */     {
/*  493 */       throw new IllegalArgumentException("Cannot add byte array with different byte order: " + ba.order());
/*      */     }
/*      */   }
/*      */ 
/*      */   public ByteOrder order()
/*      */   {
/*  503 */     if (this.order == null)
/*      */     {
/*  505 */       throw new IllegalStateException("Byte order not yet set.");
/*      */     }
/*  507 */     return this.order;
/*      */   }
/*      */ 
/*      */   public void order(ByteOrder order)
/*      */   {
/*  516 */     if ((order == null) || (!order.equals(this.order)))
/*      */     {
/*  518 */       this.order = order;
/*  519 */       if (!this.bas.isEmpty())
/*      */       {
/*  521 */         for (ByteArrayList.Node node = this.bas.getFirst(); node.hasNextNode(); node = node.getNextNode())
/*      */         {
/*  523 */           node.getByteArray().order(order);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public short getShort(int index)
/*      */   {
/*  535 */     return cursor(index).getShort();
/*      */   }
/*      */ 
/*      */   public void putShort(int index, short s)
/*      */   {
/*  544 */     cursor(index).putShort(s);
/*      */   }
/*      */ 
/*      */   public int getInt(int index)
/*      */   {
/*  553 */     return cursor(index).getInt();
/*      */   }
/*      */ 
/*      */   public void putInt(int index, int i)
/*      */   {
/*  562 */     cursor(index).putInt(i);
/*      */   }
/*      */ 
/*      */   public long getLong(int index)
/*      */   {
/*  571 */     return cursor(index).getLong();
/*      */   }
/*      */ 
/*      */   public void putLong(int index, long l)
/*      */   {
/*  580 */     cursor(index).putLong(l);
/*      */   }
/*      */ 
/*      */   public float getFloat(int index)
/*      */   {
/*  589 */     return cursor(index).getFloat();
/*      */   }
/*      */ 
/*      */   public void putFloat(int index, float f)
/*      */   {
/*  598 */     cursor(index).putFloat(f);
/*      */   }
/*      */ 
/*      */   public double getDouble(int index)
/*      */   {
/*  607 */     return cursor(index).getDouble();
/*      */   }
/*      */ 
/*      */   public void putDouble(int index, double d)
/*      */   {
/*  616 */     cursor(index).putDouble(d);
/*      */   }
/*      */ 
/*      */   public char getChar(int index)
/*      */   {
/*  625 */     return cursor(index).getChar();
/*      */   }
/*      */ 
/*      */   public void putChar(int index, char c)
/*      */   {
/*  634 */     cursor(index).putChar(c);
/*      */   }
/*      */ 
/*      */   private class CursorImpl
/*      */     implements ByteArray.Cursor
/*      */   {
/*      */     private int index;
/*      */     private final CompositeByteArray.CursorListener listener;
/*      */     private ByteArrayList.Node componentNode;
/*      */     private int componentIndex;
/*      */     private ByteArray.Cursor componentCursor;
/*      */ 
/*      */     public CursorImpl()
/*      */     {
/*  655 */       this(0, null);
/*      */     }
/*      */ 
/*      */     public CursorImpl(int index)
/*      */     {
/*  661 */       this(index, null);
/*      */     }
/*      */ 
/*      */     public CursorImpl(CompositeByteArray.CursorListener listener)
/*      */     {
/*  667 */       this(0, listener);
/*      */     }
/*      */ 
/*      */     public CursorImpl(int index, CompositeByteArray.CursorListener listener)
/*      */     {
/*  673 */       this.index = index;
/*  674 */       this.listener = listener;
/*      */     }
/*      */ 
/*      */     public int getIndex()
/*      */     {
/*  683 */       return this.index;
/*      */     }
/*      */ 
/*      */     public void setIndex(int index)
/*      */     {
/*  692 */       CompositeByteArray.this.checkBounds(index, 0);
/*  693 */       this.index = index;
/*      */     }
/*      */ 
/*      */     public void skip(int length)
/*      */     {
/*  702 */       setIndex(this.index + length);
/*      */     }
/*      */ 
/*      */     public ByteArray slice(int length)
/*      */     {
/*  711 */       CompositeByteArray slice = new CompositeByteArray(CompositeByteArray.this.byteArrayFactory);
/*  712 */       int remaining = length;
/*  713 */       while (remaining > 0)
/*      */       {
/*  715 */         prepareForAccess(remaining);
/*  716 */         int componentSliceSize = Math.min(remaining, this.componentCursor.getRemaining());
/*  717 */         ByteArray componentSlice = this.componentCursor.slice(componentSliceSize);
/*  718 */         slice.addLast(componentSlice);
/*  719 */         this.index += componentSliceSize;
/*  720 */         remaining -= componentSliceSize;
/*      */       }
/*  722 */       return slice;
/*      */     }
/*      */ 
/*      */     public ByteOrder order()
/*      */     {
/*  731 */       return CompositeByteArray.this.order();
/*      */     }
/*      */ 
/*      */     private void prepareForAccess(int accessSize)
/*      */     {
/*  739 */       if ((this.componentNode != null) && (this.componentNode.isRemoved()))
/*      */       {
/*  741 */         this.componentNode = null;
/*  742 */         this.componentCursor = null;
/*      */       }
/*      */ 
/*  746 */       CompositeByteArray.this.checkBounds(this.index, accessSize);
/*      */ 
/*  750 */       ByteArrayList.Node oldComponentNode = this.componentNode;
/*      */ 
/*  753 */       if (this.componentNode == null)
/*      */       {
/*  755 */         int basMidpoint = (CompositeByteArray.this.last() - CompositeByteArray.this.first()) / 2 + CompositeByteArray.this.first();
/*  756 */         if (this.index <= basMidpoint)
/*      */         {
/*  759 */           this.componentNode = CompositeByteArray.this.bas.getFirst();
/*  760 */           this.componentIndex = CompositeByteArray.this.first();
/*  761 */           if (this.listener != null)
/*      */           {
/*  763 */             this.listener.enteredFirstComponent(this.componentIndex, this.componentNode.getByteArray());
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  769 */           this.componentNode = CompositeByteArray.this.bas.getLast();
/*  770 */           this.componentIndex = (CompositeByteArray.this.last() - this.componentNode.getByteArray().last());
/*  771 */           if (this.listener != null)
/*      */           {
/*  773 */             this.listener.enteredLastComponent(this.componentIndex, this.componentNode.getByteArray());
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  779 */       while (this.index < this.componentIndex)
/*      */       {
/*  781 */         this.componentNode = this.componentNode.getPreviousNode();
/*  782 */         this.componentIndex -= this.componentNode.getByteArray().last();
/*  783 */         if (this.listener == null)
/*      */           continue;
/*  785 */         this.listener.enteredPreviousComponent(this.componentIndex, this.componentNode.getByteArray());
/*      */       }
/*      */ 
/*  790 */       while (this.index >= this.componentIndex + this.componentNode.getByteArray().length())
/*      */       {
/*  792 */         this.componentIndex += this.componentNode.getByteArray().last();
/*  793 */         this.componentNode = this.componentNode.getNextNode();
/*  794 */         if (this.listener == null)
/*      */           continue;
/*  796 */         this.listener.enteredNextComponent(this.componentIndex, this.componentNode.getByteArray());
/*      */       }
/*      */ 
/*  801 */       int internalComponentIndex = this.index - this.componentIndex;
/*  802 */       if (this.componentNode == oldComponentNode)
/*      */       {
/*  805 */         this.componentCursor.setIndex(internalComponentIndex);
/*      */       }
/*      */       else
/*      */       {
/*  810 */         this.componentCursor = this.componentNode.getByteArray().cursor(internalComponentIndex);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getRemaining()
/*      */     {
/*  820 */       return CompositeByteArray.this.last() - this.index + 1;
/*      */     }
/*      */ 
/*      */     public boolean hasRemaining()
/*      */     {
/*  829 */       return getRemaining() > 0;
/*      */     }
/*      */ 
/*      */     public byte get()
/*      */     {
/*  838 */       prepareForAccess(1);
/*  839 */       byte b = this.componentCursor.get();
/*  840 */       this.index += 1;
/*  841 */       return b;
/*      */     }
/*      */ 
/*      */     public void put(byte b)
/*      */     {
/*  850 */       prepareForAccess(1);
/*  851 */       this.componentCursor.put(b);
/*  852 */       this.index += 1;
/*      */     }
/*      */ 
/*      */     public void get(IoBuffer bb)
/*      */     {
/*  861 */       while (bb.hasRemaining())
/*      */       {
/*  863 */         int remainingBefore = bb.remaining();
/*  864 */         prepareForAccess(remainingBefore);
/*  865 */         this.componentCursor.get(bb);
/*  866 */         int remainingAfter = bb.remaining();
/*      */ 
/*  868 */         int chunkSize = remainingBefore - remainingAfter;
/*  869 */         this.index += chunkSize;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void put(IoBuffer bb)
/*      */     {
/*  879 */       while (bb.hasRemaining())
/*      */       {
/*  881 */         int remainingBefore = bb.remaining();
/*  882 */         prepareForAccess(remainingBefore);
/*  883 */         this.componentCursor.put(bb);
/*  884 */         int remainingAfter = bb.remaining();
/*      */ 
/*  886 */         int chunkSize = remainingBefore - remainingAfter;
/*  887 */         this.index += chunkSize;
/*      */       }
/*      */     }
/*      */ 
/*      */     public short getShort()
/*      */     {
/*  897 */       prepareForAccess(2);
/*  898 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/*  900 */         short s = this.componentCursor.getShort();
/*  901 */         this.index += 2;
/*  902 */         return s;
/*      */       }
/*      */ 
/*  906 */       byte b0 = get();
/*  907 */       byte b1 = get();
/*  908 */       if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */       {
/*  910 */         return (short)(b0 << 8 | b1 << 0);
/*      */       }
/*      */ 
/*  914 */       return (short)(b1 << 8 | b0 << 0);
/*      */     }
/*      */ 
/*      */     public void putShort(short s)
/*      */     {
/*  925 */       prepareForAccess(2);
/*  926 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/*  928 */         this.componentCursor.putShort(s);
/*  929 */         this.index += 2;
/*      */       }
/*      */       else
/*      */       {
/*      */         byte b1;
/*      */         byte b0;
/*      */         byte b1;
/*  935 */         if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */         {
/*  937 */           byte b0 = (byte)(s >> 8 & 0xFF);
/*  938 */           b1 = (byte)(s >> 0 & 0xFF);
/*      */         }
/*      */         else
/*      */         {
/*  942 */           b0 = (byte)(s >> 0 & 0xFF);
/*  943 */           b1 = (byte)(s >> 8 & 0xFF);
/*      */         }
/*  945 */         put(b0);
/*  946 */         put(b1);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int getInt()
/*      */     {
/*  956 */       prepareForAccess(4);
/*  957 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/*  959 */         int i = this.componentCursor.getInt();
/*  960 */         this.index += 4;
/*  961 */         return i;
/*      */       }
/*      */ 
/*  965 */       byte b0 = get();
/*  966 */       byte b1 = get();
/*  967 */       byte b2 = get();
/*  968 */       byte b3 = get();
/*  969 */       if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */       {
/*  971 */         return b0 << 24 | b1 << 16 | b2 << 8 | b3 << 0;
/*      */       }
/*      */ 
/*  975 */       return b3 << 24 | b2 << 16 | b1 << 8 | b0 << 0;
/*      */     }
/*      */ 
/*      */     public void putInt(int i)
/*      */     {
/*  986 */       prepareForAccess(4);
/*  987 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/*  989 */         this.componentCursor.putInt(i);
/*  990 */         this.index += 4;
/*      */       }
/*      */       else
/*      */       {
/*      */         byte b3;
/*      */         byte b0;
/*      */         byte b1;
/*      */         byte b2;
/*      */         byte b3;
/*  998 */         if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */         {
/* 1000 */           byte b0 = (byte)(i >> 24 & 0xFF);
/* 1001 */           byte b1 = (byte)(i >> 16 & 0xFF);
/* 1002 */           byte b2 = (byte)(i >> 8 & 0xFF);
/* 1003 */           b3 = (byte)(i >> 0 & 0xFF);
/*      */         }
/*      */         else
/*      */         {
/* 1007 */           b0 = (byte)(i >> 0 & 0xFF);
/* 1008 */           b1 = (byte)(i >> 8 & 0xFF);
/* 1009 */           b2 = (byte)(i >> 16 & 0xFF);
/* 1010 */           b3 = (byte)(i >> 24 & 0xFF);
/*      */         }
/* 1012 */         put(b0);
/* 1013 */         put(b1);
/* 1014 */         put(b2);
/* 1015 */         put(b3);
/*      */       }
/*      */     }
/*      */ 
/*      */     public long getLong()
/*      */     {
/* 1025 */       prepareForAccess(8);
/* 1026 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1028 */         long l = this.componentCursor.getLong();
/* 1029 */         this.index += 8;
/* 1030 */         return l;
/*      */       }
/*      */ 
/* 1034 */       byte b0 = get();
/* 1035 */       byte b1 = get();
/* 1036 */       byte b2 = get();
/* 1037 */       byte b3 = get();
/* 1038 */       byte b4 = get();
/* 1039 */       byte b5 = get();
/* 1040 */       byte b6 = get();
/* 1041 */       byte b7 = get();
/* 1042 */       if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */       {
/* 1044 */         return (b0 & 0xFF) << 56 | (b1 & 0xFF) << 48 | (b2 & 0xFF) << 40 | (b3 & 0xFF) << 32 | (b4 & 0xFF) << 24 | (b5 & 0xFF) << 16 | (b6 & 0xFF) << 8 | (b7 & 0xFF) << 0;
/*      */       }
/*      */ 
/* 1050 */       return (b7 & 0xFF) << 56 | (b6 & 0xFF) << 48 | (b5 & 0xFF) << 40 | (b4 & 0xFF) << 32 | (b3 & 0xFF) << 24 | (b2 & 0xFF) << 16 | (b1 & 0xFF) << 8 | (b0 & 0xFF) << 0;
/*      */     }
/*      */ 
/*      */     public void putLong(long l)
/*      */     {
/* 1064 */       prepareForAccess(8);
/* 1065 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1067 */         this.componentCursor.putLong(l);
/* 1068 */         this.index += 8;
/*      */       }
/*      */       else
/*      */       {
/*      */         byte b7;
/*      */         byte b0;
/*      */         byte b1;
/*      */         byte b2;
/*      */         byte b3;
/*      */         byte b4;
/*      */         byte b5;
/*      */         byte b6;
/*      */         byte b7;
/* 1080 */         if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */         {
/* 1082 */           byte b0 = (byte)(int)(l >> 56 & 0xFF);
/* 1083 */           byte b1 = (byte)(int)(l >> 48 & 0xFF);
/* 1084 */           byte b2 = (byte)(int)(l >> 40 & 0xFF);
/* 1085 */           byte b3 = (byte)(int)(l >> 32 & 0xFF);
/* 1086 */           byte b4 = (byte)(int)(l >> 24 & 0xFF);
/* 1087 */           byte b5 = (byte)(int)(l >> 16 & 0xFF);
/* 1088 */           byte b6 = (byte)(int)(l >> 8 & 0xFF);
/* 1089 */           b7 = (byte)(int)(l >> 0 & 0xFF);
/*      */         }
/*      */         else
/*      */         {
/* 1093 */           b0 = (byte)(int)(l >> 0 & 0xFF);
/* 1094 */           b1 = (byte)(int)(l >> 8 & 0xFF);
/* 1095 */           b2 = (byte)(int)(l >> 16 & 0xFF);
/* 1096 */           b3 = (byte)(int)(l >> 24 & 0xFF);
/* 1097 */           b4 = (byte)(int)(l >> 32 & 0xFF);
/* 1098 */           b5 = (byte)(int)(l >> 40 & 0xFF);
/* 1099 */           b6 = (byte)(int)(l >> 48 & 0xFF);
/* 1100 */           b7 = (byte)(int)(l >> 56 & 0xFF);
/*      */         }
/* 1102 */         put(b0);
/* 1103 */         put(b1);
/* 1104 */         put(b2);
/* 1105 */         put(b3);
/* 1106 */         put(b4);
/* 1107 */         put(b5);
/* 1108 */         put(b6);
/* 1109 */         put(b7);
/*      */       }
/*      */     }
/*      */ 
/*      */     public float getFloat()
/*      */     {
/* 1119 */       prepareForAccess(4);
/* 1120 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1122 */         float f = this.componentCursor.getFloat();
/* 1123 */         this.index += 4;
/* 1124 */         return f;
/*      */       }
/*      */ 
/* 1128 */       int i = getInt();
/* 1129 */       return Float.intBitsToFloat(i);
/*      */     }
/*      */ 
/*      */     public void putFloat(float f)
/*      */     {
/* 1139 */       prepareForAccess(4);
/* 1140 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1142 */         this.componentCursor.putFloat(f);
/* 1143 */         this.index += 4;
/*      */       }
/*      */       else
/*      */       {
/* 1147 */         int i = Float.floatToIntBits(f);
/* 1148 */         putInt(i);
/*      */       }
/*      */     }
/*      */ 
/*      */     public double getDouble()
/*      */     {
/* 1158 */       prepareForAccess(8);
/* 1159 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1161 */         double d = this.componentCursor.getDouble();
/* 1162 */         this.index += 8;
/* 1163 */         return d;
/*      */       }
/*      */ 
/* 1167 */       long l = getLong();
/* 1168 */       return Double.longBitsToDouble(l);
/*      */     }
/*      */ 
/*      */     public void putDouble(double d)
/*      */     {
/* 1178 */       prepareForAccess(8);
/* 1179 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1181 */         this.componentCursor.putDouble(d);
/* 1182 */         this.index += 8;
/*      */       }
/*      */       else
/*      */       {
/* 1186 */         long l = Double.doubleToLongBits(d);
/* 1187 */         putLong(l);
/*      */       }
/*      */     }
/*      */ 
/*      */     public char getChar()
/*      */     {
/* 1197 */       prepareForAccess(2);
/* 1198 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1200 */         char c = this.componentCursor.getChar();
/* 1201 */         this.index += 2;
/* 1202 */         return c;
/*      */       }
/*      */ 
/* 1206 */       byte b0 = get();
/* 1207 */       byte b1 = get();
/* 1208 */       if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */       {
/* 1210 */         return (char)(b0 << 8 | b1 << 0);
/*      */       }
/*      */ 
/* 1214 */       return (char)(b1 << 8 | b0 << 0);
/*      */     }
/*      */ 
/*      */     public void putChar(char c)
/*      */     {
/* 1225 */       prepareForAccess(2);
/* 1226 */       if (this.componentCursor.getRemaining() >= 4)
/*      */       {
/* 1228 */         this.componentCursor.putChar(c);
/* 1229 */         this.index += 2;
/*      */       }
/*      */       else
/*      */       {
/*      */         byte b1;
/*      */         byte b0;
/*      */         byte b1;
/* 1235 */         if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN))
/*      */         {
/* 1237 */           byte b0 = (byte)(c >> '\b' & 0xFF);
/* 1238 */           b1 = (byte)(c >> '\000' & 0xFF);
/*      */         }
/*      */         else
/*      */         {
/* 1242 */           b0 = (byte)(c >> '\000' & 0xFF);
/* 1243 */           b1 = (byte)(c >> '\b' & 0xFF);
/*      */         }
/* 1245 */         put(b0);
/* 1246 */         put(b1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface CursorListener
/*      */   {
/*      */     public abstract void enteredFirstComponent(int paramInt, ByteArray paramByteArray);
/*      */ 
/*      */     public abstract void enteredNextComponent(int paramInt, ByteArray paramByteArray);
/*      */ 
/*      */     public abstract void enteredPreviousComponent(int paramInt, ByteArray paramByteArray);
/*      */ 
/*      */     public abstract void enteredLastComponent(int paramInt, ByteArray paramByteArray);
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.CompositeByteArray
 * JD-Core Version:    0.6.0
 */