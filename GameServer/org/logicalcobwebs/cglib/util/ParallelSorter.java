/*     */ package org.logicalcobwebs.cglib.util;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassesKey;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ 
/*     */ public abstract class ParallelSorter extends SorterTemplate
/*     */ {
/*     */   protected Object[] a;
/*     */   private Comparer comparer;
/*     */ 
/*     */   public abstract ParallelSorter newInstance(Object[] paramArrayOfObject);
/*     */ 
/*     */   public static ParallelSorter create(Object[] arrays)
/*     */   {
/*  66 */     Generator gen = new Generator();
/*  67 */     gen.setArrays(arrays);
/*  68 */     return gen.create();
/*     */   }
/*     */ 
/*     */   private int len() {
/*  72 */     return ((Object[])(Object[])this.a[0]).length;
/*     */   }
/*     */ 
/*     */   public void quickSort(int index)
/*     */   {
/*  80 */     quickSort(index, 0, len(), null);
/*     */   }
/*     */ 
/*     */   public void quickSort(int index, int lo, int hi)
/*     */   {
/*  90 */     quickSort(index, lo, hi, null);
/*     */   }
/*     */ 
/*     */   public void quickSort(int index, Comparator cmp)
/*     */   {
/*  99 */     quickSort(index, 0, len(), cmp);
/*     */   }
/*     */ 
/*     */   public void quickSort(int index, int lo, int hi, Comparator cmp)
/*     */   {
/* 110 */     chooseComparer(index, cmp);
/* 111 */     super.quickSort(lo, hi - 1);
/*     */   }
/*     */ 
/*     */   public void mergeSort(int index)
/*     */   {
/* 118 */     mergeSort(index, 0, len(), null);
/*     */   }
/*     */ 
/*     */   public void mergeSort(int index, int lo, int hi)
/*     */   {
/* 128 */     mergeSort(index, lo, hi, null);
/*     */   }
/*     */ 
/*     */   public void mergeSort(int index, Comparator cmp)
/*     */   {
/* 138 */     mergeSort(index, 0, len(), cmp);
/*     */   }
/*     */ 
/*     */   public void mergeSort(int index, int lo, int hi, Comparator cmp)
/*     */   {
/* 149 */     chooseComparer(index, cmp);
/* 150 */     super.mergeSort(lo, hi - 1);
/*     */   }
/*     */ 
/*     */   private void chooseComparer(int index, Comparator cmp) {
/* 154 */     Object array = this.a[index];
/* 155 */     Class type = array.getClass().getComponentType();
/* 156 */     if (type.equals(Integer.TYPE))
/* 157 */       this.comparer = new IntComparer((int[])(int[])array);
/* 158 */     else if (type.equals(Long.TYPE))
/* 159 */       this.comparer = new LongComparer((long[])(long[])array);
/* 160 */     else if (type.equals(Double.TYPE))
/* 161 */       this.comparer = new DoubleComparer((double[])(double[])array);
/* 162 */     else if (type.equals(Float.TYPE))
/* 163 */       this.comparer = new FloatComparer((float[])(float[])array);
/* 164 */     else if (type.equals(Short.TYPE))
/* 165 */       this.comparer = new ShortComparer((short[])(short[])array);
/* 166 */     else if (type.equals(Byte.TYPE))
/* 167 */       this.comparer = new ByteComparer((byte[])(byte[])array);
/* 168 */     else if (cmp != null)
/* 169 */       this.comparer = new ComparatorComparer((Object[])(Object[])array, cmp);
/*     */     else
/* 171 */       this.comparer = new ObjectComparer((Object[])(Object[])array);
/*     */   }
/*     */ 
/*     */   protected int compare(int i, int j)
/*     */   {
/* 176 */     return this.comparer.compare(i, j);
/*     */   }
/*     */ 
/*     */   public static class Generator extends AbstractClassGenerator {
/* 254 */     private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(ParallelSorter.class.getName());
/*     */     private Object[] arrays;
/*     */ 
/*     */     public Generator() {
/* 259 */       super();
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 263 */       return null;
/*     */     }
/*     */ 
/*     */     public void setArrays(Object[] arrays) {
/* 267 */       this.arrays = arrays;
/*     */     }
/*     */ 
/*     */     public ParallelSorter create() {
/* 271 */       return (ParallelSorter)super.create(ClassesKey.create(this.arrays));
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) throws Exception {
/* 275 */       if (this.arrays.length == 0) {
/* 276 */         throw new IllegalArgumentException("No arrays specified to sort");
/*     */       }
/* 278 */       for (int i = 0; i < this.arrays.length; i++) {
/* 279 */         if (!this.arrays[i].getClass().isArray()) {
/* 280 */           throw new IllegalArgumentException(this.arrays[i].getClass() + " is not an array");
/*     */         }
/*     */       }
/* 283 */       new ParallelSorterEmitter(v, getClassName(), this.arrays);
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 287 */       return ((ParallelSorter)ReflectUtils.newInstance(type)).newInstance(this.arrays);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 291 */       return ((ParallelSorter)instance).newInstance(this.arrays);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ByteComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private byte[] a;
/*     */ 
/*     */     public ByteComparer(byte[] a)
/*     */     {
/* 249 */       this.a = a; } 
/* 250 */     public int compare(int i, int j) { return this.a[i] - this.a[j];
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ShortComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private short[] a;
/*     */ 
/*     */     public ShortComparer(short[] a)
/*     */     {
/* 243 */       this.a = a; } 
/* 244 */     public int compare(int i, int j) { return this.a[i] - this.a[j];
/*     */     }
/*     */   }
/*     */ 
/*     */   static class DoubleComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private double[] a;
/*     */ 
/*     */     public DoubleComparer(double[] a)
/*     */     {
/* 233 */       this.a = a;
/*     */     }
/* 235 */     public int compare(int i, int j) { double vi = this.a[i];
/* 236 */       double vj = this.a[j];
/* 237 */       return vi > vj ? 1 : vi == vj ? 0 : -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class FloatComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private float[] a;
/*     */ 
/*     */     public FloatComparer(float[] a)
/*     */     {
/* 223 */       this.a = a;
/*     */     }
/* 225 */     public int compare(int i, int j) { float vi = this.a[i];
/* 226 */       float vj = this.a[j];
/* 227 */       return vi > vj ? 1 : vi == vj ? 0 : -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class LongComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private long[] a;
/*     */ 
/*     */     public LongComparer(long[] a)
/*     */     {
/* 213 */       this.a = a;
/*     */     }
/* 215 */     public int compare(int i, int j) { long vi = this.a[i];
/* 216 */       long vj = this.a[j];
/* 217 */       return vi > vj ? 1 : vi == vj ? 0 : -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class IntComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private int[] a;
/*     */ 
/*     */     public IntComparer(int[] a)
/*     */     {
/* 207 */       this.a = a; } 
/* 208 */     public int compare(int i, int j) { return this.a[i] - this.a[j];
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ObjectComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private Object[] a;
/*     */ 
/*     */     public ObjectComparer(Object[] a)
/*     */     {
/* 199 */       this.a = a;
/*     */     }
/* 201 */     public int compare(int i, int j) { return ((Comparable)this.a[i]).compareTo(this.a[j]);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ComparatorComparer
/*     */     implements ParallelSorter.Comparer
/*     */   {
/*     */     private Object[] a;
/*     */     private Comparator cmp;
/*     */ 
/*     */     public ComparatorComparer(Object[] a, Comparator cmp)
/*     */     {
/* 188 */       this.a = a;
/* 189 */       this.cmp = cmp;
/*     */     }
/*     */ 
/*     */     public int compare(int i, int j) {
/* 193 */       return this.cmp.compare(this.a[i], this.a[j]);
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface Comparer
/*     */   {
/*     */     public abstract int compare(int paramInt1, int paramInt2);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.util.ParallelSorter
 * JD-Core Version:    0.6.0
 */