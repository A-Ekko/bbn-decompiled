/*     */ package flex.management.jmx;
/*     */ 
/*     */ public class MBeanInfo
/*     */ {
/*     */   public String className;
/*     */   public String description;
/*     */   public MBeanAttributeInfo[] attributes;
/*     */   public MBeanConstructorInfo[] constructors;
/*     */   public MBeanOperationInfo[] operations;
/*     */ 
/*     */   public MBeanInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MBeanInfo(javax.management.MBeanInfo mbeanInfo)
/*     */   {
/*  68 */     this.className = mbeanInfo.getClassName();
/*  69 */     this.description = mbeanInfo.getDescription();
/*  70 */     this.attributes = convertAttributes(mbeanInfo.getAttributes());
/*  71 */     this.constructors = convertConstructors(mbeanInfo.getConstructors());
/*  72 */     this.operations = convertOperations(mbeanInfo.getOperations());
/*     */   }
/*     */ 
/*     */   public javax.management.MBeanInfo toMBeanInfo()
/*     */   {
/*  83 */     return new javax.management.MBeanInfo(this.className, this.description, convertAttributes(this.attributes), convertConstructors(this.constructors), convertOperations(this.operations), null);
/*     */   }
/*     */ 
/*     */   private MBeanAttributeInfo[] convertAttributes(javax.management.MBeanAttributeInfo[] source)
/*     */   {
/*  99 */     MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[source.length];
/* 100 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 102 */       attributes[i] = new MBeanAttributeInfo(source[i]);
/*     */     }
/* 104 */     return attributes;
/*     */   }
/*     */ 
/*     */   private javax.management.MBeanAttributeInfo[] convertAttributes(MBeanAttributeInfo[] source)
/*     */   {
/* 115 */     javax.management.MBeanAttributeInfo[] attributes = new javax.management.MBeanAttributeInfo[source.length];
/* 116 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 118 */       attributes[i] = source[i].toMBeanAttributeInfo();
/*     */     }
/* 120 */     return attributes;
/*     */   }
/*     */ 
/*     */   private MBeanConstructorInfo[] convertConstructors(javax.management.MBeanConstructorInfo[] source)
/*     */   {
/* 132 */     MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[source.length];
/* 133 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 135 */       constructors[i] = new MBeanConstructorInfo(source[i]);
/*     */     }
/* 137 */     return constructors;
/*     */   }
/*     */ 
/*     */   private javax.management.MBeanConstructorInfo[] convertConstructors(MBeanConstructorInfo[] source)
/*     */   {
/* 148 */     javax.management.MBeanConstructorInfo[] constructors = new javax.management.MBeanConstructorInfo[source.length];
/* 149 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 151 */       constructors[i] = source[i].toMBeanConstructorInfo();
/*     */     }
/* 153 */     return constructors;
/*     */   }
/*     */ 
/*     */   private MBeanOperationInfo[] convertOperations(javax.management.MBeanOperationInfo[] source)
/*     */   {
/* 164 */     MBeanOperationInfo[] operations = new MBeanOperationInfo[source.length];
/* 165 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 167 */       operations[i] = new MBeanOperationInfo(source[i]);
/*     */     }
/* 169 */     return operations;
/*     */   }
/*     */ 
/*     */   private javax.management.MBeanOperationInfo[] convertOperations(MBeanOperationInfo[] source)
/*     */   {
/* 180 */     javax.management.MBeanOperationInfo[] operations = new javax.management.MBeanOperationInfo[source.length];
/* 181 */     for (int i = 0; i < source.length; i++)
/*     */     {
/* 183 */       operations[i] = source[i].toMBeanOperationInfo();
/*     */     }
/* 185 */     return operations;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.jmx.MBeanInfo
 * JD-Core Version:    0.6.0
 */