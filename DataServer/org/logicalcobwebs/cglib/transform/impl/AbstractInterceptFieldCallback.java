/*    */ package org.logicalcobwebs.cglib.transform.impl;
/*    */ 
/*    */ public class AbstractInterceptFieldCallback
/*    */   implements InterceptFieldCallback
/*    */ {
/*    */   public int writeInt(Object obj, String name, int oldValue, int newValue)
/*    */   {
/* 23 */     return newValue; } 
/* 24 */   public char writeChar(Object obj, String name, char oldValue, char newValue) { return newValue; } 
/* 25 */   public byte writeByte(Object obj, String name, byte oldValue, byte newValue) { return newValue; } 
/* 26 */   public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) { return newValue; } 
/* 27 */   public short writeShort(Object obj, String name, short oldValue, short newValue) { return newValue; } 
/* 28 */   public float writeFloat(Object obj, String name, float oldValue, float newValue) { return newValue; } 
/* 29 */   public double writeDouble(Object obj, String name, double oldValue, double newValue) { return newValue; } 
/* 30 */   public long writeLong(Object obj, String name, long oldValue, long newValue) { return newValue; } 
/* 31 */   public Object writeObject(Object obj, String name, Object oldValue, Object newValue) { return newValue; } 
/*    */   public int readInt(Object obj, String name, int oldValue) {
/* 33 */     return oldValue; } 
/* 34 */   public char readChar(Object obj, String name, char oldValue) { return oldValue; } 
/* 35 */   public byte readByte(Object obj, String name, byte oldValue) { return oldValue; } 
/* 36 */   public boolean readBoolean(Object obj, String name, boolean oldValue) { return oldValue; } 
/* 37 */   public short readShort(Object obj, String name, short oldValue) { return oldValue; } 
/* 38 */   public float readFloat(Object obj, String name, float oldValue) { return oldValue; } 
/* 39 */   public double readDouble(Object obj, String name, double oldValue) { return oldValue; } 
/* 40 */   public long readLong(Object obj, String name, long oldValue) { return oldValue; } 
/* 41 */   public Object readObject(Object obj, String name, Object oldValue) { return oldValue;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.AbstractInterceptFieldCallback
 * JD-Core Version:    0.6.0
 */