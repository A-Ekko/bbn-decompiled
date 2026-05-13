/*     */ package flex.messaging.services.messaging.selector;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public class PropertyValueComparator
/*     */   implements Comparator
/*     */ {
/*  24 */   static PropertyValueComparator instance = null;
/*     */   public static final int UNKNOWN = -100;
/*     */ 
/*     */   public static PropertyValueComparator getInstance()
/*     */   {
/*  28 */     if (instance == null) {
/*  29 */       instance = new PropertyValueComparator();
/*     */     }
/*  31 */     return instance;
/*     */   }
/*     */ 
/*     */   public int compare(Object o1, Object o2) throws ClassCastException {
/*  35 */     int result = 0;
/*     */ 
/*  37 */     if ((o1 instanceof NumericValue)) {
/*  38 */       o1 = ((NumericValue)o1).getValue();
/*     */     }
/*  40 */     if ((o2 instanceof NumericValue))
/*  41 */       o2 = ((NumericValue)o2).getValue();
/*     */     try
/*     */     {
/*  44 */       if ((o1 == null) || (o2 == null)) {
/*  45 */         result = -100;
/*     */       }
/*  47 */       else if ((o1 instanceof Boolean)) {
/*  48 */         if ((o2 instanceof Boolean)) {
/*  49 */           if (!o1.equals(o2))
/*  50 */             result = -1;
/*     */           else
/*  52 */             result = 0;
/*     */         }
/*     */         else
/*  55 */           throw new ClassCastException();
/*     */       }
/*  57 */       else if ((o1 instanceof Byte)) {
/*  58 */         if ((o2 instanceof Byte))
/*  59 */           result = ((Byte)o1).compareTo((Byte)o2);
/*  60 */         else if ((o2 instanceof Short))
/*  61 */           result = new Short(((Byte)o1).shortValue()).compareTo((Short)o2);
/*  62 */         else if ((o2 instanceof Integer))
/*  63 */           result = new Integer(((Byte)o1).intValue()).compareTo((Integer)o2);
/*  64 */         else if ((o2 instanceof Long))
/*  65 */           result = new Long(((Byte)o1).longValue()).compareTo((Long)o2);
/*  66 */         else if ((o2 instanceof Float))
/*  67 */           result = new Float(((Byte)o1).floatValue()).compareTo((Float)o2);
/*  68 */         else if ((o2 instanceof Double))
/*  69 */           result = new Double(((Byte)o1).doubleValue()).compareTo((Double)o2);
/*     */         else
/*  71 */           throw new ClassCastException();
/*     */       }
/*  73 */       else if ((o1 instanceof Short)) {
/*  74 */         if ((o2 instanceof Byte))
/*  75 */           result = ((Short)o1).compareTo(new Short(((Byte)o2).shortValue()));
/*  76 */         else if ((o2 instanceof Short))
/*  77 */           result = ((Short)o1).compareTo((Short)o2);
/*  78 */         else if ((o2 instanceof Integer))
/*  79 */           result = new Integer(((Short)o1).intValue()).compareTo((Integer)o2);
/*  80 */         else if ((o2 instanceof Long))
/*  81 */           result = new Long(((Short)o1).longValue()).compareTo((Long)o2);
/*  82 */         else if ((o2 instanceof Float))
/*  83 */           result = new Float(((Short)o1).floatValue()).compareTo((Float)o2);
/*  84 */         else if ((o2 instanceof Double))
/*  85 */           result = new Double(((Short)o1).doubleValue()).compareTo((Double)o2);
/*     */         else
/*  87 */           throw new ClassCastException();
/*     */       }
/*  89 */       else if ((o1 instanceof Integer)) {
/*  90 */         if ((o2 instanceof Byte))
/*  91 */           result = ((Integer)o1).compareTo(new Integer(((Byte)o2).intValue()));
/*  92 */         else if ((o2 instanceof Short))
/*  93 */           result = ((Integer)o1).compareTo(new Integer(((Short)o2).intValue()));
/*  94 */         else if ((o2 instanceof Integer))
/*  95 */           result = ((Integer)o1).compareTo((Integer)o2);
/*  96 */         else if ((o2 instanceof Long))
/*  97 */           result = new Long(((Integer)o1).longValue()).compareTo((Long)o2);
/*  98 */         else if ((o2 instanceof Float))
/*  99 */           result = new Float(((Integer)o1).floatValue()).compareTo((Float)o2);
/* 100 */         else if ((o2 instanceof Double))
/* 101 */           result = new Double(((Integer)o1).doubleValue()).compareTo((Double)o2);
/*     */         else
/* 103 */           throw new ClassCastException();
/*     */       }
/* 105 */       else if ((o1 instanceof Long)) {
/* 106 */         if ((o2 instanceof Byte))
/* 107 */           result = ((Long)o1).compareTo(new Long(((Byte)o2).longValue()));
/* 108 */         else if ((o2 instanceof Short))
/* 109 */           result = ((Long)o1).compareTo(new Long(((Short)o2).longValue()));
/* 110 */         else if ((o2 instanceof Integer))
/* 111 */           result = ((Long)o1).compareTo(new Long(((Integer)o2).longValue()));
/* 112 */         else if ((o2 instanceof Long))
/* 113 */           result = ((Long)o1).compareTo((Long)o2);
/* 114 */         else if ((o2 instanceof Float))
/* 115 */           result = new Float(((Long)o1).floatValue()).compareTo((Float)o2);
/* 116 */         else if ((o2 instanceof Double))
/* 117 */           result = new Double(((Long)o1).doubleValue()).compareTo((Double)o2);
/*     */         else
/* 119 */           throw new ClassCastException();
/*     */       }
/* 121 */       else if ((o1 instanceof Float)) {
/* 122 */         if ((o2 instanceof Byte))
/* 123 */           result = ((Float)o1).compareTo(new Float(((Byte)o2).floatValue()));
/* 124 */         else if ((o2 instanceof Short))
/* 125 */           result = ((Float)o1).compareTo(new Float(((Short)o2).floatValue()));
/* 126 */         else if ((o2 instanceof Integer))
/* 127 */           result = ((Float)o1).compareTo(new Float(((Integer)o2).floatValue()));
/* 128 */         else if ((o2 instanceof Long))
/* 129 */           result = ((Float)o1).compareTo(new Float(((Long)o2).floatValue()));
/* 130 */         else if ((o2 instanceof Float))
/* 131 */           result = ((Float)o1).compareTo((Float)o2);
/* 132 */         else if ((o2 instanceof Double))
/* 133 */           result = new Double(((Float)o1).doubleValue()).compareTo((Double)o2);
/*     */         else
/* 135 */           throw new ClassCastException();
/*     */       }
/* 137 */       else if ((o1 instanceof Double)) {
/* 138 */         if ((o2 instanceof Byte))
/* 139 */           result = ((Double)o1).compareTo(new Double(((Byte)o2).doubleValue()));
/* 140 */         else if ((o2 instanceof Short))
/* 141 */           result = ((Double)o1).compareTo(new Double(((Short)o2).doubleValue()));
/* 142 */         else if ((o2 instanceof Integer))
/* 143 */           result = ((Double)o1).compareTo(new Double(((Integer)o2).doubleValue()));
/* 144 */         else if ((o2 instanceof Long))
/* 145 */           result = ((Double)o1).compareTo(new Double(((Long)o2).doubleValue()));
/* 146 */         else if ((o2 instanceof Float))
/* 147 */           result = ((Double)o1).compareTo(new Double(((Float)o2).doubleValue()));
/* 148 */         else if ((o2 instanceof Double))
/* 149 */           result = ((Double)o1).compareTo((Double)o2);
/*     */         else
/* 151 */           throw new ClassCastException();
/*     */       }
/* 153 */       else if ((o1 instanceof String))
/* 154 */         result = ((String)o1).compareTo(o2.toString());
/*     */       else
/* 156 */         throw new ClassCastException();
/*     */     }
/*     */     catch (Exception e) {
/* 159 */       throw new ClassCastException();
/*     */     }
/* 161 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 166 */     return (obj instanceof PropertyValueComparator);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.selector.PropertyValueComparator
 * JD-Core Version:    0.6.0
 */