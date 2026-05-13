/*    */ package org.logicalcobwebs.cglib.util;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.Constants;
/*    */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*    */ import org.logicalcobwebs.cglib.core.Local;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ 
/*    */ class ParallelSorterEmitter extends ClassEmitter
/*    */ {
/* 25 */   private static final Type PARALLEL_SORTER = TypeUtils.parseType("org.logicalcobwebs.cglib.util.ParallelSorter");
/*    */ 
/* 27 */   private static final Signature CSTRUCT_OBJECT_ARRAY = TypeUtils.parseConstructor("Object[]");
/*    */ 
/* 29 */   private static final Signature NEW_INSTANCE = new Signature("newInstance", PARALLEL_SORTER, new Type[] { Constants.TYPE_OBJECT_ARRAY });
/*    */ 
/* 31 */   private static final Signature SWAP = TypeUtils.parseSignature("void swap(int, int)");
/*    */ 
/*    */   public ParallelSorterEmitter(ClassVisitor v, String className, Object[] arrays)
/*    */   {
/* 35 */     super(v);
/* 36 */     begin_class(46, 1, className, PARALLEL_SORTER, null, "<generated>");
/* 37 */     EmitUtils.null_constructor(this);
/* 38 */     EmitUtils.factory_method(this, NEW_INSTANCE);
/* 39 */     generateConstructor(arrays);
/* 40 */     generateSwap(arrays);
/* 41 */     end_class();
/*    */   }
/*    */ 
/*    */   private String getFieldName(int index) {
/* 45 */     return "FIELD_" + index;
/*    */   }
/*    */ 
/*    */   private void generateConstructor(Object[] arrays) {
/* 49 */     CodeEmitter e = begin_method(1, CSTRUCT_OBJECT_ARRAY, null, null);
/* 50 */     e.load_this();
/* 51 */     e.super_invoke_constructor();
/* 52 */     e.load_this();
/* 53 */     e.load_arg(0);
/* 54 */     e.super_putfield("a", Constants.TYPE_OBJECT_ARRAY);
/* 55 */     for (int i = 0; i < arrays.length; i++) {
/* 56 */       Type type = Type.getType(arrays[i].getClass());
/* 57 */       declare_field(2, getFieldName(i), type, null, null);
/* 58 */       e.load_this();
/* 59 */       e.load_arg(0);
/* 60 */       e.push(i);
/* 61 */       e.aaload();
/* 62 */       e.checkcast(type);
/* 63 */       e.putfield(getFieldName(i));
/*    */     }
/* 65 */     e.return_value();
/* 66 */     e.end_method();
/*    */   }
/*    */ 
/*    */   private void generateSwap(Object[] arrays) {
/* 70 */     CodeEmitter e = begin_method(1, SWAP, null, null);
/* 71 */     for (int i = 0; i < arrays.length; i++) {
/* 72 */       Type type = Type.getType(arrays[i].getClass());
/* 73 */       Type component = TypeUtils.getComponentType(type);
/* 74 */       Local T = e.make_local(type);
/*    */ 
/* 76 */       e.load_this();
/* 77 */       e.getfield(getFieldName(i));
/* 78 */       e.store_local(T);
/*    */ 
/* 80 */       e.load_local(T);
/* 81 */       e.load_arg(0);
/*    */ 
/* 83 */       e.load_local(T);
/* 84 */       e.load_arg(1);
/* 85 */       e.array_load(component);
/*    */ 
/* 87 */       e.load_local(T);
/* 88 */       e.load_arg(1);
/*    */ 
/* 90 */       e.load_local(T);
/* 91 */       e.load_arg(0);
/* 92 */       e.array_load(component);
/*    */ 
/* 94 */       e.array_store(component);
/* 95 */       e.array_store(component);
/*    */     }
/* 97 */     e.return_value();
/* 98 */     e.end_method();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.util.ParallelSorterEmitter
 * JD-Core Version:    0.6.0
 */