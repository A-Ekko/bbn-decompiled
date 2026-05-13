/*     */ package flex.messaging.services.messaging.selector;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ 
/*     */ public class NumericValue
/*     */ {
/*  24 */   Number value = null;
/*  25 */   String image = null;
/*  26 */   int imageType = 0;
/*     */   private static final int ByteValue = 0;
/*     */   private static final int ShortValue = 1;
/*     */   private static final int IntValue = 2;
/*     */   private static final int FloatValue = 3;
/*     */   static final int LongValue = 4;
/*     */   static final int DoubleValue = 5;
/*  36 */   private static String[] indexToTypeMap = { "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Float", "java.lang.Long", "java.lang.Double" };
/*     */ 
/*  45 */   private static final int[][] returnTypes = { { 0, 1, 2, 3, 4, 5 }, { 1, 1, 2, 3, 4, 5 }, { 2, 2, 2, 3, 4, 5 }, { 3, 3, 3, 3, 3, 5 }, { 4, 4, 4, 3, 4, 5 }, { 5, 5, 5, 5, 5, 5 } };
/*     */ 
/*     */   public NumericValue(Object obj)
/*     */     throws ClassCastException
/*     */   {
/*  57 */     if ((this.value != null) && (!(obj instanceof Number))) {
/*  58 */       throw new ClassCastException();
/*     */     }
/*  60 */     if ((obj instanceof NumericValue)) {
/*  61 */       NumericValue n = (NumericValue)obj;
/*  62 */       this.value = n.value;
/*  63 */       this.image = n.image;
/*  64 */       this.imageType = n.imageType;
/*     */     } else {
/*  66 */       this.value = ((Number)obj);
/*     */     }
/*     */   }
/*     */ 
/*     */   public NumericValue(String image, int imageType) {
/*  71 */     this.image = image;
/*  72 */     this.imageType = imageType;
/*  73 */     this.value = null;
/*     */   }
/*     */ 
/*     */   public Number getValue() {
/*  77 */     if ((this.value == null) && (this.image != null)) {
/*  78 */       switch (this.imageType) {
/*     */       case 5:
/*  80 */         this.value = new Double(this.image);
/*  81 */         break;
/*     */       case 4:
/*  83 */         this.value = Long.decode(this.image);
/*  84 */         break;
/*     */       }
/*     */ 
/*  87 */       this.image = null;
/*  88 */       this.imageType = -1;
/*     */     }
/*     */ 
/*  91 */     return this.value;
/*     */   }
/*     */ 
/*     */   private int getIndexForType(Number obj)
/*     */   {
/*  96 */     int index = -1;
/*  97 */     String typeName = obj.getClass().getName();
/*     */ 
/*  99 */     if (typeName.equals("java.lang.Byte")) {
/* 100 */       index = 0;
/*     */     }
/* 102 */     else if (typeName.equals("java.lang.Short")) {
/* 103 */       index = 1;
/*     */     }
/* 105 */     else if (typeName.equals("java.lang.Integer")) {
/* 106 */       index = 2;
/*     */     }
/* 108 */     else if (typeName.equals("java.lang.Float")) {
/* 109 */       index = 3;
/*     */     }
/* 111 */     else if (typeName.equals("java.lang.Long")) {
/* 112 */       index = 4;
/*     */     }
/* 114 */     else if (typeName.equals("java.lang.Double")) {
/* 115 */       index = 5;
/*     */     }
/*     */ 
/* 118 */     return index;
/*     */   }
/*     */ 
/*     */   int getUnifiedTypeIndex(Number val1, Number val2) {
/* 122 */     int index = -1;
/*     */ 
/* 124 */     if ((val1 != null) && (val2 != null)) {
/* 125 */       int index1 = getIndexForType(val1);
/* 126 */       int index2 = getIndexForType(val2);
/*     */ 
/* 129 */       index = returnTypes[index1][index2];
/*     */     }
/* 131 */     return index;
/*     */   }
/*     */ 
/*     */   private Number convertNumber(Number val, int typeIndex) {
/* 135 */     Number newVal = null;
/*     */ 
/* 137 */     String newClassName = indexToTypeMap[typeIndex];
/*     */     try
/*     */     {
/* 140 */       Class newClass = Class.forName(newClassName);
/* 141 */       Class[] paramTypes = { Class.forName("java.lang.String") };
/*     */ 
/* 145 */       Constructor constr = newClass.getDeclaredConstructor(paramTypes);
/*     */ 
/* 147 */       Object[] args = { val.toString() };
/*     */ 
/* 150 */       newVal = (Number)constr.newInstance(args);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 154 */       throw new RuntimeException(e);
/*     */     }
/* 156 */     return newVal;
/*     */   }
/*     */ 
/*     */   public Number add(NumericValue num) {
/* 160 */     Number result = null;
/*     */ 
/* 162 */     if ((getValue() != null) && (num != null)) {
/* 163 */       Number value2 = num.getValue();
/* 164 */       int typeIndex = getUnifiedTypeIndex(this.value, value2);
/*     */ 
/* 167 */       Number val1 = convertNumber(this.value, typeIndex);
/* 168 */       Number val2 = convertNumber(value2, typeIndex);
/*     */ 
/* 172 */       switch (typeIndex) {
/*     */       case 0:
/* 174 */         result = new Byte((byte)(val1.byteValue() + val2.byteValue()));
/* 175 */         break;
/*     */       case 1:
/* 177 */         result = new Short((short)(val1.shortValue() + val2.shortValue()));
/* 178 */         break;
/*     */       case 2:
/* 180 */         result = new Integer(val1.intValue() + val2.intValue());
/* 181 */         break;
/*     */       case 3:
/* 183 */         result = new Float(val1.floatValue() + val2.floatValue());
/* 184 */         break;
/*     */       case 4:
/* 186 */         result = new Long(val1.longValue() + val2.longValue());
/* 187 */         break;
/*     */       case 5:
/* 189 */         result = new Double(val1.doubleValue() + val2.doubleValue());
/* 190 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 197 */     return result;
/*     */   }
/*     */ 
/*     */   public Number subtract(NumericValue num) {
/* 201 */     Number result = null;
/*     */ 
/* 203 */     if ((getValue() != null) && (num != null)) {
/* 204 */       Number value2 = num.getValue();
/* 205 */       int typeIndex = getUnifiedTypeIndex(this.value, value2);
/*     */ 
/* 208 */       Number val1 = convertNumber(this.value, typeIndex);
/* 209 */       Number val2 = convertNumber(value2, typeIndex);
/*     */ 
/* 213 */       switch (typeIndex) {
/*     */       case 0:
/* 215 */         result = new Byte((byte)(val1.byteValue() - val2.byteValue()));
/* 216 */         break;
/*     */       case 1:
/* 218 */         result = new Short((short)(val1.shortValue() - val2.shortValue()));
/* 219 */         break;
/*     */       case 2:
/* 221 */         result = new Integer(val1.intValue() - val2.intValue());
/* 222 */         break;
/*     */       case 3:
/* 224 */         result = new Float(val1.floatValue() - val2.floatValue());
/* 225 */         break;
/*     */       case 4:
/* 227 */         result = new Long(val1.longValue() - val2.longValue());
/* 228 */         break;
/*     */       case 5:
/* 230 */         result = new Double(val1.doubleValue() - val2.doubleValue());
/* 231 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 237 */     return result;
/*     */   }
/*     */ 
/*     */   public Number multiply(NumericValue num) {
/* 241 */     Number result = null;
/*     */ 
/* 243 */     if ((getValue() != null) && (num != null)) {
/* 244 */       Number value2 = num.getValue();
/* 245 */       int typeIndex = getUnifiedTypeIndex(this.value, value2);
/*     */ 
/* 248 */       Number val1 = convertNumber(this.value, typeIndex);
/* 249 */       Number val2 = convertNumber(value2, typeIndex);
/*     */ 
/* 253 */       switch (typeIndex) {
/*     */       case 0:
/* 255 */         result = new Byte((byte)(val1.byteValue() * val2.byteValue()));
/* 256 */         break;
/*     */       case 1:
/* 258 */         result = new Short((short)(val1.shortValue() * val2.shortValue()));
/* 259 */         break;
/*     */       case 2:
/* 261 */         result = new Integer(val1.intValue() * val2.intValue());
/* 262 */         break;
/*     */       case 3:
/* 264 */         result = new Float(val1.floatValue() * val2.floatValue());
/* 265 */         break;
/*     */       case 4:
/* 267 */         result = new Long(val1.longValue() * val2.longValue());
/* 268 */         break;
/*     */       case 5:
/* 270 */         result = new Double(val1.doubleValue() * val2.doubleValue());
/* 271 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 278 */     return result;
/*     */   }
/*     */ 
/*     */   public Number divide(NumericValue num) {
/* 282 */     Number result = null;
/*     */ 
/* 284 */     if ((getValue() != null) && (num != null)) {
/* 285 */       Number value2 = num.getValue();
/* 286 */       int typeIndex = getUnifiedTypeIndex(this.value, value2);
/*     */ 
/* 289 */       Number val1 = convertNumber(this.value, typeIndex);
/* 290 */       Number val2 = convertNumber(value2, typeIndex);
/*     */ 
/* 294 */       switch (typeIndex) {
/*     */       case 0:
/* 296 */         result = new Byte((byte)(val1.byteValue() / val2.byteValue()));
/* 297 */         break;
/*     */       case 1:
/* 299 */         result = new Short((short)(val1.shortValue() / val2.shortValue()));
/* 300 */         break;
/*     */       case 2:
/* 302 */         result = new Integer(val1.intValue() / val2.intValue());
/* 303 */         break;
/*     */       case 3:
/* 305 */         result = new Float(val1.floatValue() / val2.floatValue());
/* 306 */         break;
/*     */       case 4:
/* 308 */         result = new Long(val1.longValue() / val2.longValue());
/* 309 */         break;
/*     */       case 5:
/* 311 */         result = new Double(val1.doubleValue() / val2.doubleValue());
/* 312 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 318 */     return result;
/*     */   }
/*     */ 
/*     */   public Number negate() {
/* 322 */     Number result = null;
/*     */ 
/* 324 */     if (this.image != null) {
/* 325 */       this.image = ("-" + this.image);
/* 326 */       return getValue();
/*     */     }
/*     */ 
/* 329 */     if (getValue() != null) {
/* 330 */       int typeIndex = getIndexForType(this.value);
/*     */ 
/* 335 */       switch (typeIndex) {
/*     */       case 0:
/* 337 */         result = new Byte((byte)(-this.value.byteValue()));
/* 338 */         break;
/*     */       case 1:
/* 340 */         result = new Short((short)(-this.value.shortValue()));
/* 341 */         break;
/*     */       case 2:
/* 343 */         result = new Integer(-this.value.intValue());
/* 344 */         break;
/*     */       case 3:
/* 346 */         result = new Float(-this.value.floatValue());
/* 347 */         break;
/*     */       case 4:
/* 349 */         result = new Long(-this.value.longValue());
/* 350 */         break;
/*     */       case 5:
/* 352 */         result = new Double(-this.value.doubleValue());
/* 353 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 359 */     return result;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 363 */     String str = "null";
/*     */ 
/* 365 */     if (getValue() != null) {
/* 366 */       str = this.value.toString();
/*     */     }
/* 368 */     return str;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 372 */     NumericValue num1 = new NumericValue(new Float(3.65D));
/* 373 */     NumericValue num2 = new NumericValue(new Long(100L));
/* 374 */     System.err.println(num1 + "+" + num2 + " = " + num1.add(num2));
/* 375 */     System.err.println(num2 + "+" + num1 + " = " + num2.add(num1));
/*     */ 
/* 377 */     System.err.println(num1 + "-" + num2 + " = " + num1.subtract(num2));
/* 378 */     System.err.println(num2 + "-" + num1 + " = " + num2.subtract(num1));
/*     */ 
/* 380 */     System.err.println(num1 + "*" + num2 + " = " + num1.multiply(num2));
/* 381 */     System.err.println(num2 + "*" + num1 + " = " + num2.multiply(num1));
/*     */ 
/* 383 */     System.err.println(num1 + "/" + num2 + " = " + num1.divide(num2));
/* 384 */     System.err.println(num2 + "/" + num1 + " = " + num2.divide(num1));
/*     */ 
/* 386 */     System.err.println("-" + num1 + " = " + num1.negate());
/* 387 */     System.err.println("-" + num2 + " = " + num2.negate());
/*     */ 
/* 390 */     System.exit(0);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.selector.NumericValue
 * JD-Core Version:    0.6.0
 */