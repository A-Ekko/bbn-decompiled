/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import flex.messaging.io.amf.ASObject;
/*     */ import flex.messaging.io.amf.translator.TranslationException;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DecoderFactory
/*     */ {
/*  39 */   private static final NativeDecoder nativeDecoder = new NativeDecoder();
/*     */ 
/*  42 */   private static final NullDecoder nullDecoder = new NullDecoder();
/*     */ 
/*  45 */   private static final NumberDecoder numberDecoder = new NumberDecoder();
/*  46 */   private static final StringDecoder stringDecoder = new StringDecoder();
/*  47 */   private static final BooleanDecoder booleanDecoder = new BooleanDecoder();
/*  48 */   private static final CharacterDecoder characterDecoder = new CharacterDecoder();
/*     */ 
/*  52 */   private static final DateDecoder dateDecoder = new DateDecoder();
/*  53 */   private static final CalendarDecoder calendarDecoder = new CalendarDecoder();
/*     */ 
/*  56 */   private static final ArrayDecoder arrayDecoder = new ArrayDecoder();
/*  57 */   private static final MapDecoder mapDecoder = new MapDecoder();
/*  58 */   private static final CollectionDecoder collectionDecoder = new CollectionDecoder();
/*  59 */   private static final TypedObjectDecoder typedObjectDecoder = new TypedObjectDecoder();
/*     */ 
/*  63 */   private static final ArrayDecoder deepArrayDecoder = new ReferenceAwareArrayDecoder();
/*  64 */   private static final MapDecoder deepMapDecoder = new ReferenceAwareMapDecoder();
/*  65 */   private static final CollectionDecoder deepCollectionDecoder = new ReferenceAwareCollectionDecoder();
/*  66 */   private static final TypedObjectDecoder deepTypedObjectDecoder = new ReferenceAwareTypedObjectDecoder();
/*     */ 
/*     */   public static ActionScriptDecoder getDecoderForShell(Class desiredClass)
/*     */   {
/*  81 */     if (desiredClass == null) {
/*  82 */       return nullDecoder;
/*     */     }
/*  84 */     if (Collection.class.isAssignableFrom(desiredClass)) {
/*  85 */       return collectionDecoder;
/*     */     }
/*  87 */     if (Map.class.isAssignableFrom(desiredClass)) {
/*  88 */       return mapDecoder;
/*     */     }
/*  90 */     if (desiredClass.isArray()) {
/*  91 */       return arrayDecoder;
/*     */     }
/*  93 */     return nativeDecoder;
/*     */   }
/*     */ 
/*     */   public static ActionScriptDecoder getDecoder(Object encodedObject, Class desiredClass)
/*     */   {
/* 115 */     if (encodedObject != null)
/*     */     {
/* 118 */       if (desiredClass.isAssignableFrom(encodedObject.getClass())) {
/* 119 */         return nativeDecoder;
/*     */       }
/* 121 */       if (String.class.equals(desiredClass)) {
/* 122 */         return stringDecoder;
/*     */       }
/*     */ 
/* 126 */       if (isNumber(desiredClass)) {
/* 127 */         return numberDecoder;
/*     */       }
/* 129 */       if (isBoolean(desiredClass)) {
/* 130 */         return booleanDecoder;
/*     */       }
/* 132 */       if (Collection.class.isAssignableFrom(desiredClass)) {
/* 133 */         return collectionDecoder;
/*     */       }
/* 135 */       if (Map.class.isAssignableFrom(desiredClass)) {
/* 136 */         return mapDecoder;
/*     */       }
/* 138 */       if (desiredClass.isArray()) {
/* 139 */         return arrayDecoder;
/*     */       }
/*     */ 
/* 145 */       if (isTypedObject(encodedObject)) {
/* 146 */         return typedObjectDecoder;
/*     */       }
/* 148 */       if (Date.class.isAssignableFrom(desiredClass)) {
/* 149 */         return dateDecoder;
/*     */       }
/* 151 */       if (Calendar.class.isAssignableFrom(desiredClass)) {
/* 152 */         return calendarDecoder;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 158 */     if (isNumber(desiredClass)) {
/* 159 */       return numberDecoder;
/*     */     }
/* 161 */     if (isBoolean(desiredClass)) {
/* 162 */       return booleanDecoder;
/*     */     }
/* 164 */     if (isCharacter(desiredClass)) {
/* 165 */       return characterDecoder;
/*     */     }
/* 167 */     if (encodedObject == null) {
/* 168 */       return nullDecoder;
/*     */     }
/* 170 */     invalidType(encodedObject, desiredClass);
/*     */ 
/* 173 */     return nativeDecoder;
/*     */   }
/*     */ 
/*     */   public static ActionScriptDecoder getReferenceAwareDecoder(Object encodedObject, Class desiredClass)
/*     */   {
/* 187 */     if (encodedObject != null)
/*     */     {
/* 189 */       if (String.class.equals(desiredClass)) {
/* 190 */         return stringDecoder;
/*     */       }
/*     */ 
/* 194 */       if (isNumber(desiredClass)) {
/* 195 */         return numberDecoder;
/*     */       }
/* 197 */       if (isBoolean(desiredClass)) {
/* 198 */         return booleanDecoder;
/*     */       }
/* 200 */       if (Collection.class.isAssignableFrom(desiredClass)) {
/* 201 */         return deepCollectionDecoder;
/*     */       }
/* 203 */       if (Map.class.isAssignableFrom(desiredClass)) {
/* 204 */         return deepMapDecoder;
/*     */       }
/* 206 */       if (desiredClass.isArray()) {
/* 207 */         return deepArrayDecoder;
/*     */       }
/*     */ 
/* 213 */       if (isTypedObject(encodedObject)) {
/* 214 */         return deepTypedObjectDecoder;
/*     */       }
/* 216 */       if (Date.class.isAssignableFrom(desiredClass)) {
/* 217 */         return dateDecoder;
/*     */       }
/* 219 */       if (Calendar.class.isAssignableFrom(desiredClass)) {
/* 220 */         return calendarDecoder;
/*     */       }
/* 222 */       if (isCharacter(desiredClass)) {
/* 223 */         return characterDecoder;
/*     */       }
/*     */ 
/* 228 */       if (desiredClass.isAssignableFrom(encodedObject.getClass())) {
/* 229 */         return nativeDecoder;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 235 */     if (isNumber(desiredClass)) {
/* 236 */       return numberDecoder;
/*     */     }
/* 238 */     if (isBoolean(desiredClass)) {
/* 239 */       return booleanDecoder;
/*     */     }
/* 241 */     if (isCharacter(desiredClass)) {
/* 242 */       return characterDecoder;
/*     */     }
/* 244 */     if (encodedObject == null) {
/* 245 */       return nullDecoder;
/*     */     }
/* 247 */     invalidType(encodedObject, desiredClass);
/*     */ 
/* 250 */     return nativeDecoder;
/*     */   }
/*     */ 
/*     */   public static boolean isNumber(Class desiredClass)
/*     */   {
/* 255 */     boolean isNum = false;
/*     */ 
/* 257 */     if (desiredClass.isPrimitive())
/*     */     {
/* 259 */       if ((desiredClass.equals(Integer.TYPE)) || (desiredClass.equals(Double.TYPE)) || (desiredClass.equals(Long.TYPE)) || (desiredClass.equals(Float.TYPE)) || (desiredClass.equals(Short.TYPE)) || (desiredClass.equals(Byte.TYPE)))
/*     */       {
/* 266 */         isNum = true;
/*     */       }
/*     */     }
/* 269 */     else if (Number.class.isAssignableFrom(desiredClass))
/*     */     {
/* 271 */       isNum = true;
/*     */     }
/*     */ 
/* 274 */     return isNum;
/*     */   }
/*     */ 
/*     */   public static boolean isCharacter(Class desiredClass)
/*     */   {
/* 279 */     boolean isChar = false;
/*     */ 
/* 281 */     if ((desiredClass.isPrimitive()) && (desiredClass.equals(Character.TYPE)))
/*     */     {
/* 283 */       isChar = true;
/*     */     }
/* 285 */     else if (desiredClass.equals(Character.class))
/*     */     {
/* 287 */       isChar = true;
/*     */     }
/*     */ 
/* 290 */     return isChar;
/*     */   }
/*     */ 
/*     */   public static boolean isBoolean(Class desiredClass)
/*     */   {
/* 295 */     boolean isBool = false;
/*     */ 
/* 297 */     if ((desiredClass.isPrimitive()) && (desiredClass.equals(Boolean.TYPE)))
/*     */     {
/* 299 */       isBool = true;
/*     */     }
/* 301 */     else if (desiredClass.equals(Boolean.class))
/*     */     {
/* 303 */       isBool = true;
/*     */     }
/*     */ 
/* 306 */     return isBool;
/*     */   }
/*     */ 
/*     */   public static boolean isCharArray(Class desiredClass)
/*     */   {
/* 311 */     boolean isCharArray = false;
/*     */ 
/* 313 */     if (desiredClass.isArray())
/*     */     {
/* 315 */       Class type = desiredClass.getComponentType();
/* 316 */       if ((type != null) && (type.equals(Character.TYPE)))
/*     */       {
/* 318 */         isCharArray = true;
/*     */       }
/*     */     }
/*     */ 
/* 322 */     return isCharArray;
/*     */   }
/*     */ 
/*     */   public static boolean isTypedObject(Object encodedObject)
/*     */   {
/* 327 */     boolean typed = false;
/*     */ 
/* 329 */     if ((encodedObject instanceof ASObject))
/*     */     {
/* 331 */       typed = TypeMarshallingContext.getType((ASObject)encodedObject) != null;
/*     */     }
/*     */ 
/* 334 */     return typed;
/*     */   }
/*     */ 
/*     */   public static void invalidType(Object object, Class desiredClass)
/*     */   {
/* 339 */     String inputType = null;
/*     */ 
/* 341 */     if (object != null)
/*     */     {
/* 343 */       inputType = object.getClass().getName();
/*     */     }
/*     */ 
/* 346 */     StringBuffer message = new StringBuffer("Cannot convert ");
/* 347 */     if (inputType != null)
/*     */     {
/* 349 */       message.append("type ").append(inputType).append(" ");
/*     */     }
/*     */ 
/* 352 */     if ((object != null) && (((object instanceof String)) || ((object instanceof Number)) || ((object instanceof Boolean)) || ((object instanceof Date))))
/*     */     {
/* 357 */       message.append("with value '").append(object.toString()).append("' ");
/*     */     }
/* 359 */     else if ((object instanceof ASObject))
/*     */     {
/* 361 */       ASObject aso = (ASObject)object;
/* 362 */       message.append("with remote type specified as '").append(aso.getType()).append("' ");
/*     */     }
/*     */ 
/* 365 */     message.append("to an instance of ").append(desiredClass.toString());
/*     */ 
/* 367 */     TranslationException ex = new TranslationException(message.toString());
/* 368 */     ex.setCode("Client.Message.Deserialize.InvalidType");
/* 369 */     throw ex;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.DecoderFactory
 * JD-Core Version:    0.6.0
 */