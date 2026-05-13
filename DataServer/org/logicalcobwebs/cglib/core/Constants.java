/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ 
/*    */ public abstract interface Constants extends org.logicalcobwebs.cglib.asm.Constants
/*    */ {
/* 25 */   public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
/* 26 */   public static final Type[] TYPES_EMPTY = new Type[0];
/*    */ 
/* 28 */   public static final Signature SIG_STATIC = TypeUtils.parseSignature("void <clinit>()");
/*    */ 
/* 31 */   public static final Type TYPE_OBJECT_ARRAY = TypeUtils.parseType("Object[]");
/* 32 */   public static final Type TYPE_CLASS_ARRAY = TypeUtils.parseType("Class[]");
/* 33 */   public static final Type TYPE_STRING_ARRAY = TypeUtils.parseType("String[]");
/*    */ 
/* 35 */   public static final Type TYPE_OBJECT = TypeUtils.parseType("Object");
/* 36 */   public static final Type TYPE_CLASS = TypeUtils.parseType("Class");
/* 37 */   public static final Type TYPE_CLASS_LOADER = TypeUtils.parseType("ClassLoader");
/* 38 */   public static final Type TYPE_CHARACTER = TypeUtils.parseType("Character");
/* 39 */   public static final Type TYPE_BOOLEAN = TypeUtils.parseType("Boolean");
/* 40 */   public static final Type TYPE_DOUBLE = TypeUtils.parseType("Double");
/* 41 */   public static final Type TYPE_FLOAT = TypeUtils.parseType("Float");
/* 42 */   public static final Type TYPE_LONG = TypeUtils.parseType("Long");
/* 43 */   public static final Type TYPE_INTEGER = TypeUtils.parseType("Integer");
/* 44 */   public static final Type TYPE_SHORT = TypeUtils.parseType("Short");
/* 45 */   public static final Type TYPE_BYTE = TypeUtils.parseType("Byte");
/* 46 */   public static final Type TYPE_NUMBER = TypeUtils.parseType("Number");
/* 47 */   public static final Type TYPE_STRING = TypeUtils.parseType("String");
/* 48 */   public static final Type TYPE_THROWABLE = TypeUtils.parseType("Throwable");
/* 49 */   public static final Type TYPE_BIG_INTEGER = TypeUtils.parseType("java.math.BigInteger");
/* 50 */   public static final Type TYPE_BIG_DECIMAL = TypeUtils.parseType("java.math.BigDecimal");
/* 51 */   public static final Type TYPE_STRING_BUFFER = TypeUtils.parseType("StringBuffer");
/* 52 */   public static final Type TYPE_RUNTIME_EXCEPTION = TypeUtils.parseType("RuntimeException");
/* 53 */   public static final Type TYPE_ERROR = TypeUtils.parseType("Error");
/* 54 */   public static final Type TYPE_SYSTEM = TypeUtils.parseType("System");
/* 55 */   public static final Type TYPE_SIGNATURE = TypeUtils.parseType("org.logicalcobwebs.cglib.core.Signature");
/*    */   public static final String CONSTRUCTOR_NAME = "<init>";
/*    */   public static final String STATIC_NAME = "<clinit>";
/*    */   public static final String SOURCE_FILE = "<generated>";
/*    */   public static final String SUID_FIELD_NAME = "serialVersionUID";
/*    */   public static final int PRIVATE_FINAL_STATIC = 26;
/*    */   public static final int SWITCH_STYLE_TRIE = 0;
/*    */   public static final int SWITCH_STYLE_HASH = 1;
/*    */   public static final int SWITCH_STYLE_HASHONLY = 2;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.Constants
 * JD-Core Version:    0.6.0
 */