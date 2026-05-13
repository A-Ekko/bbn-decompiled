/*      */ package org.logicalcobwebs.cglib.proxy;
/*      */ 
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*      */ import org.logicalcobwebs.cglib.asm.Label;
/*      */ import org.logicalcobwebs.cglib.asm.Type;
/*      */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*      */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*      */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*      */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*      */ import org.logicalcobwebs.cglib.core.CodeGenerationException;
/*      */ import org.logicalcobwebs.cglib.core.CollectionUtils;
/*      */ import org.logicalcobwebs.cglib.core.Constants;
/*      */ import org.logicalcobwebs.cglib.core.DuplicatesPredicate;
/*      */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*      */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*      */ import org.logicalcobwebs.cglib.core.Local;
/*      */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*      */ import org.logicalcobwebs.cglib.core.MethodInfoTransformer;
/*      */ import org.logicalcobwebs.cglib.core.MethodWrapper;
/*      */ import org.logicalcobwebs.cglib.core.ObjectSwitchCallback;
/*      */ import org.logicalcobwebs.cglib.core.ProcessSwitchCallback;
/*      */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*      */ import org.logicalcobwebs.cglib.core.RejectModifierPredicate;
/*      */ import org.logicalcobwebs.cglib.core.Signature;
/*      */ import org.logicalcobwebs.cglib.core.Transformer;
/*      */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*      */ import org.logicalcobwebs.cglib.core.VisibilityPredicate;
/*      */ 
/*      */ public class Enhancer extends AbstractClassGenerator
/*      */ {
/*   62 */   private static final CallbackFilter ALL_ZERO = new CallbackFilter() {
/*      */     public int accept(Method method) {
/*   64 */       return 0;
/*      */     }
/*   62 */   };
/*      */ 
/*   68 */   private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(Enhancer.class.getName());
/*   69 */   private static final EnhancerKey KEY_FACTORY = (EnhancerKey)KeyFactory.create(EnhancerKey.class);
/*      */   private static final String BOUND_FIELD = "CGLIB$BOUND";
/*      */   private static final String THREAD_CALLBACKS_FIELD = "CGLIB$THREAD_CALLBACKS";
/*      */   private static final String STATIC_CALLBACKS_FIELD = "CGLIB$STATIC_CALLBACKS";
/*      */   private static final String SET_THREAD_CALLBACKS_NAME = "CGLIB$SET_THREAD_CALLBACKS";
/*      */   private static final String SET_STATIC_CALLBACKS_NAME = "CGLIB$SET_STATIC_CALLBACKS";
/*      */   private static final String CONSTRUCTED_FIELD = "CGLIB$CONSTRUCTED";
/*   79 */   private static final Type FACTORY = TypeUtils.parseType("org.logicalcobwebs.cglib.proxy.Factory");
/*      */ 
/*   81 */   private static final Type ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
/*      */ 
/*   83 */   private static final Type ILLEGAL_ARGUMENT_EXCEPTION = TypeUtils.parseType("IllegalArgumentException");
/*      */ 
/*   85 */   private static final Type THREAD_LOCAL = TypeUtils.parseType("ThreadLocal");
/*      */ 
/*   87 */   private static final Type CALLBACK = TypeUtils.parseType("org.logicalcobwebs.cglib.proxy.Callback");
/*      */ 
/*   89 */   private static final Type CALLBACK_ARRAY = Type.getType(new Callback[0].getClass());
/*      */ 
/*   91 */   private static final Signature CSTRUCT_NULL = TypeUtils.parseConstructor("");
/*      */ 
/*   93 */   private static final Signature SET_THREAD_CALLBACKS = new Signature("CGLIB$SET_THREAD_CALLBACKS", Type.VOID_TYPE, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*   95 */   private static final Signature SET_STATIC_CALLBACKS = new Signature("CGLIB$SET_STATIC_CALLBACKS", Type.VOID_TYPE, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*   97 */   private static final Signature NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*   99 */   private static final Signature MULTIARG_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { Constants.TYPE_CLASS_ARRAY, Constants.TYPE_OBJECT_ARRAY, CALLBACK_ARRAY });
/*      */ 
/*  105 */   private static final Signature SINGLE_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { CALLBACK });
/*      */ 
/*  107 */   private static final Signature SET_CALLBACK = new Signature("setCallback", Type.VOID_TYPE, new Type[] { Type.INT_TYPE, CALLBACK });
/*      */ 
/*  109 */   private static final Signature GET_CALLBACK = new Signature("getCallback", CALLBACK, new Type[] { Type.INT_TYPE });
/*      */ 
/*  111 */   private static final Signature SET_CALLBACKS = new Signature("setCallbacks", Type.VOID_TYPE, new Type[] { CALLBACK_ARRAY });
/*      */ 
/*  113 */   private static final Signature GET_CALLBACKS = new Signature("getCallbacks", CALLBACK_ARRAY, new Type[0]);
/*      */ 
/*  115 */   private static final Signature THREAD_LOCAL_GET = TypeUtils.parseSignature("Object get()");
/*      */ 
/*  117 */   private static final Signature THREAD_LOCAL_SET = TypeUtils.parseSignature("void set(Object)");
/*      */ 
/*  119 */   private static final Signature BIND_CALLBACKS = TypeUtils.parseSignature("void CGLIB$BIND_CALLBACKS(Object)");
/*      */   private Class[] interfaces;
/*      */   private CallbackFilter filter;
/*      */   private Callback[] callbacks;
/*      */   private Type[] callbackTypes;
/*      */   private boolean classOnly;
/*      */   private Class superclass;
/*      */   private Class[] argumentTypes;
/*      */   private Object[] arguments;
/*  141 */   private boolean useFactory = true;
/*      */   private Long serialVersionUID;
/*  143 */   private boolean interceptDuringConstruction = true;
/*      */ 
/*      */   public Enhancer()
/*      */   {
/*  153 */     super(SOURCE);
/*      */   }
/*      */ 
/*      */   public void setSuperclass(Class superclass)
/*      */   {
/*  166 */     if ((superclass != null) && (superclass.isInterface()))
/*  167 */       setInterfaces(new Class[] { superclass });
/*  168 */     else if ((superclass != null) && (superclass.equals(Object.class)))
/*      */     {
/*  170 */       this.superclass = null;
/*      */     }
/*  172 */     else this.superclass = superclass;
/*      */   }
/*      */ 
/*      */   public void setInterfaces(Class[] interfaces)
/*      */   {
/*  183 */     this.interfaces = interfaces;
/*      */   }
/*      */ 
/*      */   public void setCallbackFilter(CallbackFilter filter)
/*      */   {
/*  195 */     this.filter = filter;
/*      */   }
/*      */ 
/*      */   public void setCallback(Callback callback)
/*      */   {
/*  206 */     setCallbacks(new Callback[] { callback });
/*      */   }
/*      */ 
/*      */   public void setCallbacks(Callback[] callbacks)
/*      */   {
/*  219 */     if ((callbacks != null) && (callbacks.length == 0)) {
/*  220 */       throw new IllegalArgumentException("Array cannot be empty");
/*      */     }
/*  222 */     this.callbacks = callbacks;
/*      */   }
/*      */ 
/*      */   public void setUseFactory(boolean useFactory)
/*      */   {
/*  235 */     this.useFactory = useFactory;
/*      */   }
/*      */ 
/*      */   public void setInterceptDuringConstruction(boolean interceptDuringConstruction)
/*      */   {
/*  245 */     this.interceptDuringConstruction = interceptDuringConstruction;
/*      */   }
/*      */ 
/*      */   public void setCallbackType(Class callbackType)
/*      */   {
/*  257 */     setCallbackTypes(new Class[] { callbackType });
/*      */   }
/*      */ 
/*      */   public void setCallbackTypes(Class[] callbackTypes)
/*      */   {
/*  270 */     if ((callbackTypes != null) && (callbackTypes.length == 0)) {
/*  271 */       throw new IllegalArgumentException("Array cannot be empty");
/*      */     }
/*  273 */     this.callbackTypes = CallbackInfo.determineTypes(callbackTypes);
/*      */   }
/*      */ 
/*      */   public Object create()
/*      */   {
/*  283 */     this.classOnly = false;
/*  284 */     this.argumentTypes = null;
/*  285 */     return createHelper();
/*      */   }
/*      */ 
/*      */   public Object create(Class[] argumentTypes, Object[] arguments)
/*      */   {
/*  298 */     this.classOnly = false;
/*  299 */     if ((argumentTypes == null) || (arguments == null) || (argumentTypes.length != arguments.length)) {
/*  300 */       throw new IllegalArgumentException("Arguments must be non-null and of equal length");
/*      */     }
/*  302 */     this.argumentTypes = argumentTypes;
/*  303 */     this.arguments = arguments;
/*  304 */     return createHelper();
/*      */   }
/*      */ 
/*      */   public Class createClass()
/*      */   {
/*  316 */     this.classOnly = true;
/*  317 */     return (Class)createHelper();
/*      */   }
/*      */ 
/*      */   public void setSerialVersionUID(Long sUID)
/*      */   {
/*  325 */     this.serialVersionUID = sUID;
/*      */   }
/*      */ 
/*      */   private void validate() {
/*  329 */     if ((this.classOnly ^ this.callbacks == null)) {
/*  330 */       if (this.classOnly) {
/*  331 */         throw new IllegalStateException("createClass does not accept callbacks");
/*      */       }
/*  333 */       throw new IllegalStateException("Callbacks are required");
/*      */     }
/*      */ 
/*  336 */     if ((this.classOnly) && (this.callbackTypes == null)) {
/*  337 */       throw new IllegalStateException("Callback types are required");
/*      */     }
/*  339 */     if ((this.callbacks != null) && (this.callbackTypes != null)) {
/*  340 */       if (this.callbacks.length != this.callbackTypes.length) {
/*  341 */         throw new IllegalStateException("Lengths of callback and callback types array must be the same");
/*      */       }
/*  343 */       Type[] check = CallbackInfo.determineTypes(this.callbacks);
/*  344 */       for (int i = 0; i < check.length; i++) {
/*  345 */         if (!check[i].equals(this.callbackTypes[i]))
/*  346 */           throw new IllegalStateException("Callback " + check[i] + " is not assignable to " + this.callbackTypes[i]);
/*      */       }
/*      */     }
/*  349 */     else if (this.callbacks != null) {
/*  350 */       this.callbackTypes = CallbackInfo.determineTypes(this.callbacks);
/*      */     }
/*  352 */     if (this.filter == null) {
/*  353 */       if (this.callbackTypes.length > 1) {
/*  354 */         throw new IllegalStateException("Multiple callback types possible but no filter specified");
/*      */       }
/*  356 */       this.filter = ALL_ZERO;
/*      */     }
/*  358 */     if (this.interfaces != null)
/*  359 */       for (int i = 0; i < this.interfaces.length; i++) {
/*  360 */         if (this.interfaces[i] == null) {
/*  361 */           throw new IllegalStateException("Interfaces cannot be null");
/*      */         }
/*  363 */         if (!this.interfaces[i].isInterface())
/*  364 */           throw new IllegalStateException(this.interfaces[i] + " is not an interface");
/*      */       }
/*      */   }
/*      */ 
/*      */   private Object createHelper()
/*      */   {
/*  371 */     validate();
/*  372 */     if (this.superclass != null)
/*  373 */       setNamePrefix(this.superclass.getName());
/*  374 */     else if (this.interfaces != null) {
/*  375 */       setNamePrefix(this.interfaces[ReflectUtils.findPackageProtected(this.interfaces)].getName());
/*      */     }
/*  377 */     return super.create(KEY_FACTORY.newInstance(this.superclass != null ? this.superclass.getName() : null, ReflectUtils.getNames(this.interfaces), this.filter, this.callbackTypes, this.useFactory, this.interceptDuringConstruction, this.serialVersionUID));
/*      */   }
/*      */ 
/*      */   protected ClassLoader getDefaultClassLoader()
/*      */   {
/*  387 */     if (this.superclass != null)
/*  388 */       return this.superclass.getClassLoader();
/*  389 */     if (this.interfaces != null) {
/*  390 */       return this.interfaces[0].getClassLoader();
/*      */     }
/*  392 */     return null;
/*      */   }
/*      */ 
/*      */   private Signature rename(Signature sig, int index)
/*      */   {
/*  397 */     return new Signature("CGLIB$" + sig.getName() + "$" + index, sig.getDescriptor());
/*      */   }
/*      */ 
/*      */   public static void getMethods(Class superclass, Class[] interfaces, List methods)
/*      */   {
/*  416 */     getMethods(superclass, interfaces, methods, null, null);
/*      */   }
/*      */ 
/*      */   private static void getMethods(Class superclass, Class[] interfaces, List methods, List interfaceMethods, Set forcePublic)
/*      */   {
/*  421 */     ReflectUtils.addAllMethods(superclass, methods);
/*  422 */     List target = interfaceMethods != null ? interfaceMethods : methods;
/*  423 */     if (interfaces != null) {
/*  424 */       for (int i = 0; i < interfaces.length; i++) {
/*  425 */         if (interfaces[i] != Factory.class) {
/*  426 */           ReflectUtils.addAllMethods(interfaces[i], target);
/*      */         }
/*      */       }
/*      */     }
/*  430 */     if (interfaceMethods != null) {
/*  431 */       if (forcePublic != null) {
/*  432 */         forcePublic.addAll(MethodWrapper.createSet(interfaceMethods));
/*      */       }
/*  434 */       methods.addAll(interfaceMethods);
/*      */     }
/*  436 */     CollectionUtils.filter(methods, new RejectModifierPredicate(8));
/*  437 */     CollectionUtils.filter(methods, new VisibilityPredicate(superclass, true));
/*  438 */     CollectionUtils.filter(methods, new DuplicatesPredicate());
/*  439 */     CollectionUtils.filter(methods, new RejectModifierPredicate(16));
/*      */   }
/*      */ 
/*      */   public void generateClass(ClassVisitor v) throws Exception {
/*  443 */     Class sc = this.superclass == null ? Object.class : this.superclass;
/*      */ 
/*  445 */     if (TypeUtils.isFinal(sc.getModifiers()))
/*  446 */       throw new IllegalArgumentException("Cannot subclass final class " + sc);
/*  447 */     List constructors = new ArrayList(Arrays.asList(sc.getDeclaredConstructors()));
/*  448 */     filterConstructors(sc, constructors);
/*      */ 
/*  453 */     List actualMethods = new ArrayList();
/*  454 */     List interfaceMethods = new ArrayList();
/*  455 */     Set forcePublic = new HashSet();
/*  456 */     getMethods(sc, this.interfaces, actualMethods, interfaceMethods, forcePublic);
/*      */ 
/*  458 */     List methods = CollectionUtils.transform(actualMethods, new Transformer(forcePublic) {
/*      */       public Object transform(Object value) {
/*  460 */         Method method = (Method)value;
/*  461 */         int modifiers = 0x10 | method.getModifiers() & 0xFFFFFBFF & 0xFFFFFEFF & 0xFFFFFFDF;
/*      */ 
/*  466 */         if (this.val$forcePublic.contains(MethodWrapper.create(method))) {
/*  467 */           modifiers = modifiers & 0xFFFFFFFB | 0x1;
/*      */         }
/*  469 */         return ReflectUtils.getMethodInfo(method, modifiers);
/*      */       }
/*      */     });
/*  473 */     ClassEmitter e = new ClassEmitter(v);
/*  474 */     e.begin_class(46, 1, getClassName(), Type.getType(sc), this.useFactory ? TypeUtils.add(TypeUtils.getTypes(this.interfaces), FACTORY) : TypeUtils.getTypes(this.interfaces), "<generated>");
/*      */ 
/*  482 */     List constructorInfo = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());
/*      */ 
/*  484 */     e.declare_field(2, "CGLIB$BOUND", Type.BOOLEAN_TYPE, null, null);
/*  485 */     if (!this.interceptDuringConstruction) {
/*  486 */       e.declare_field(2, "CGLIB$CONSTRUCTED", Type.BOOLEAN_TYPE, null, null);
/*      */     }
/*  488 */     e.declare_field(26, "CGLIB$THREAD_CALLBACKS", THREAD_LOCAL, null, null);
/*  489 */     e.declare_field(26, "CGLIB$STATIC_CALLBACKS", CALLBACK_ARRAY, null, null);
/*  490 */     if (this.serialVersionUID != null) {
/*  491 */       e.declare_field(26, "serialVersionUID", Type.LONG_TYPE, this.serialVersionUID, null);
/*      */     }
/*      */ 
/*  494 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  495 */       e.declare_field(2, getCallbackField(i), this.callbackTypes[i], null, null);
/*      */     }
/*      */ 
/*  498 */     emitMethods(e, methods, actualMethods);
/*  499 */     emitConstructors(e, constructorInfo);
/*  500 */     emitSetThreadCallbacks(e);
/*  501 */     emitSetStaticCallbacks(e);
/*  502 */     emitBindCallbacks(e);
/*      */ 
/*  504 */     if (this.useFactory) {
/*  505 */       int[] keys = getCallbackKeys();
/*  506 */       emitNewInstanceCallbacks(e);
/*  507 */       emitNewInstanceCallback(e);
/*  508 */       emitNewInstanceMultiarg(e, constructorInfo);
/*  509 */       emitGetCallback(e, keys);
/*  510 */       emitSetCallback(e, keys);
/*  511 */       emitGetCallbacks(e);
/*  512 */       emitSetCallbacks(e);
/*      */     }
/*      */ 
/*  515 */     e.end_class();
/*      */   }
/*      */ 
/*      */   protected void filterConstructors(Class sc, List constructors)
/*      */   {
/*  529 */     CollectionUtils.filter(constructors, new VisibilityPredicate(sc, true));
/*  530 */     if (constructors.size() == 0)
/*  531 */       throw new IllegalArgumentException("No visible constructors in " + sc);
/*      */   }
/*      */ 
/*      */   protected Object firstInstance(Class type) throws Exception {
/*  535 */     if (this.classOnly) {
/*  536 */       return type;
/*      */     }
/*  538 */     return createUsingReflection(type);
/*      */   }
/*      */ 
/*      */   protected Object nextInstance(Object instance)
/*      */   {
/*  543 */     Class protoclass = (instance instanceof Class) ? (Class)instance : instance.getClass();
/*  544 */     if (this.classOnly)
/*  545 */       return protoclass;
/*  546 */     if ((instance instanceof Factory)) {
/*  547 */       if (this.argumentTypes != null) {
/*  548 */         return ((Factory)instance).newInstance(this.argumentTypes, this.arguments, this.callbacks);
/*      */       }
/*  550 */       return ((Factory)instance).newInstance(this.callbacks);
/*      */     }
/*      */ 
/*  553 */     return createUsingReflection(protoclass);
/*      */   }
/*      */ 
/*      */   public static void registerCallbacks(Class generatedClass, Callback[] callbacks)
/*      */   {
/*  578 */     setThreadCallbacks(generatedClass, callbacks);
/*      */   }
/*      */ 
/*      */   public static void registerStaticCallbacks(Class generatedClass, Callback[] callbacks)
/*      */   {
/*  591 */     setCallbacksHelper(generatedClass, callbacks, "CGLIB$SET_STATIC_CALLBACKS");
/*      */   }
/*      */ 
/*      */   public static boolean isEnhanced(Class type)
/*      */   {
/*      */     try
/*      */     {
/*  601 */       getCallbacksSetter(type, "CGLIB$SET_THREAD_CALLBACKS");
/*  602 */       return true; } catch (NoSuchMethodException e) {
/*      */     }
/*  604 */     return false;
/*      */   }
/*      */ 
/*      */   private static void setThreadCallbacks(Class type, Callback[] callbacks)
/*      */   {
/*  609 */     setCallbacksHelper(type, callbacks, "CGLIB$SET_THREAD_CALLBACKS");
/*      */   }
/*      */ 
/*      */   private static void setCallbacksHelper(Class type, Callback[] callbacks, String methodName)
/*      */   {
/*      */     try {
/*  615 */       Method setter = getCallbacksSetter(type, methodName);
/*  616 */       setter.invoke(null, new Object[] { callbacks });
/*      */     } catch (NoSuchMethodException e) {
/*  618 */       throw new IllegalArgumentException(type + " is not an enhanced class");
/*      */     } catch (IllegalAccessException e) {
/*  620 */       throw new CodeGenerationException(e);
/*      */     } catch (InvocationTargetException e) {
/*  622 */       throw new CodeGenerationException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static Method getCallbacksSetter(Class type, String methodName) throws NoSuchMethodException {
/*  627 */     return type.getDeclaredMethod(methodName, new Class[] { new Callback[0].getClass() });
/*      */   }
/*      */ 
/*      */   private Object createUsingReflection(Class type) {
/*  631 */     setThreadCallbacks(type, this.callbacks);
/*      */     try
/*      */     {
/*  634 */       if (this.argumentTypes != null)
/*      */       {
/*  636 */         localObject1 = ReflectUtils.newInstance(type, this.argumentTypes, this.arguments);
/*      */         return localObject1;
/*      */       }
/*  640 */       Object localObject1 = ReflectUtils.newInstance(type);
/*      */       return localObject1;
/*      */     }
/*      */     finally
/*      */     {
/*  645 */       setThreadCallbacks(type, null);
/*  646 */     }throw localObject2;
/*      */   }
/*      */ 
/*      */   public static Object create(Class type, Callback callback)
/*      */   {
/*  657 */     Enhancer e = new Enhancer();
/*  658 */     e.setSuperclass(type);
/*  659 */     e.setCallback(callback);
/*  660 */     return e.create();
/*      */   }
/*      */ 
/*      */   public static Object create(Class superclass, Class[] interfaces, Callback callback)
/*      */   {
/*  672 */     Enhancer e = new Enhancer();
/*  673 */     e.setSuperclass(superclass);
/*  674 */     e.setInterfaces(interfaces);
/*  675 */     e.setCallback(callback);
/*  676 */     return e.create();
/*      */   }
/*      */ 
/*      */   public static Object create(Class superclass, Class[] interfaces, CallbackFilter filter, Callback[] callbacks)
/*      */   {
/*  689 */     Enhancer e = new Enhancer();
/*  690 */     e.setSuperclass(superclass);
/*  691 */     e.setInterfaces(interfaces);
/*  692 */     e.setCallbackFilter(filter);
/*  693 */     e.setCallbacks(callbacks);
/*  694 */     return e.create();
/*      */   }
/*      */ 
/*      */   private void emitConstructors(ClassEmitter ce, List constructors) {
/*  698 */     boolean seenNull = false;
/*  699 */     for (Iterator it = constructors.iterator(); it.hasNext(); ) {
/*  700 */       MethodInfo constructor = (MethodInfo)it.next();
/*  701 */       CodeEmitter e = EmitUtils.begin_method(ce, constructor, 1);
/*  702 */       e.load_this();
/*  703 */       e.dup();
/*  704 */       e.load_args();
/*  705 */       Signature sig = constructor.getSignature();
/*  706 */       seenNull = (seenNull) || (sig.getDescriptor().equals("()V"));
/*  707 */       e.super_invoke_constructor(sig);
/*  708 */       e.invoke_static_this(BIND_CALLBACKS);
/*  709 */       if (!this.interceptDuringConstruction) {
/*  710 */         e.load_this();
/*  711 */         e.push(1);
/*  712 */         e.putfield("CGLIB$CONSTRUCTED");
/*      */       }
/*  714 */       e.return_value();
/*  715 */       e.end_method();
/*      */     }
/*  717 */     if ((!this.classOnly) && (!seenNull) && (this.arguments == null))
/*  718 */       throw new IllegalArgumentException("Superclass has no null constructors but no arguments were given");
/*      */   }
/*      */ 
/*      */   private int[] getCallbackKeys() {
/*  722 */     int[] keys = new int[this.callbackTypes.length];
/*  723 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  724 */       keys[i] = i;
/*      */     }
/*  726 */     return keys;
/*      */   }
/*      */ 
/*      */   private void emitGetCallback(ClassEmitter ce, int[] keys) {
/*  730 */     CodeEmitter e = ce.begin_method(1, GET_CALLBACK, null, null);
/*  731 */     e.load_this();
/*  732 */     e.invoke_static_this(BIND_CALLBACKS);
/*  733 */     e.load_this();
/*  734 */     e.load_arg(0);
/*  735 */     e.process_switch(keys, new ProcessSwitchCallback(e) {
/*      */       public void processCase(int key, Label end) {
/*  737 */         this.val$e.getfield(Enhancer.access$000(key));
/*  738 */         this.val$e.goTo(end);
/*      */       }
/*      */       public void processDefault() {
/*  741 */         this.val$e.pop();
/*  742 */         this.val$e.aconst_null();
/*      */       }
/*      */     });
/*  745 */     e.return_value();
/*  746 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetCallback(ClassEmitter ce, int[] keys) {
/*  750 */     CodeEmitter e = ce.begin_method(1, SET_CALLBACK, null, null);
/*  751 */     e.load_this();
/*  752 */     e.load_arg(1);
/*  753 */     e.load_arg(0);
/*  754 */     e.process_switch(keys, new ProcessSwitchCallback(e) {
/*      */       public void processCase(int key, Label end) {
/*  756 */         this.val$e.checkcast(Enhancer.this.callbackTypes[key]);
/*  757 */         this.val$e.putfield(Enhancer.access$000(key));
/*  758 */         this.val$e.goTo(end);
/*      */       }
/*      */ 
/*      */       public void processDefault() {
/*  762 */         this.val$e.pop2();
/*      */       }
/*      */     });
/*  765 */     e.return_value();
/*  766 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetCallbacks(ClassEmitter ce) {
/*  770 */     CodeEmitter e = ce.begin_method(1, SET_CALLBACKS, null, null);
/*  771 */     e.load_this();
/*  772 */     e.load_arg(0);
/*  773 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  774 */       e.dup2();
/*  775 */       e.aaload(i);
/*  776 */       e.checkcast(this.callbackTypes[i]);
/*  777 */       e.putfield(getCallbackField(i));
/*      */     }
/*  779 */     e.return_value();
/*  780 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitGetCallbacks(ClassEmitter ce) {
/*  784 */     CodeEmitter e = ce.begin_method(1, GET_CALLBACKS, null, null);
/*  785 */     e.load_this();
/*  786 */     e.invoke_static_this(BIND_CALLBACKS);
/*  787 */     e.load_this();
/*  788 */     e.push(this.callbackTypes.length);
/*  789 */     e.newarray(CALLBACK);
/*  790 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  791 */       e.dup();
/*  792 */       e.push(i);
/*  793 */       e.load_this();
/*  794 */       e.getfield(getCallbackField(i));
/*  795 */       e.aastore();
/*      */     }
/*  797 */     e.return_value();
/*  798 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitNewInstanceCallbacks(ClassEmitter ce) {
/*  802 */     CodeEmitter e = ce.begin_method(1, NEW_INSTANCE, null, null);
/*  803 */     e.load_arg(0);
/*  804 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  805 */     emitCommonNewInstance(e);
/*      */   }
/*      */ 
/*      */   private void emitCommonNewInstance(CodeEmitter e) {
/*  809 */     e.new_instance_this();
/*  810 */     e.dup();
/*  811 */     e.invoke_constructor_this();
/*  812 */     e.aconst_null();
/*  813 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  814 */     e.return_value();
/*  815 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitNewInstanceCallback(ClassEmitter ce) {
/*  819 */     CodeEmitter e = ce.begin_method(1, SINGLE_NEW_INSTANCE, null, null);
/*  820 */     switch (this.callbackTypes.length)
/*      */     {
/*      */     case 0:
/*  823 */       break;
/*      */     case 1:
/*  826 */       e.push(1);
/*  827 */       e.newarray(CALLBACK);
/*  828 */       e.dup();
/*  829 */       e.push(0);
/*  830 */       e.load_arg(0);
/*  831 */       e.aastore();
/*  832 */       e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  833 */       break;
/*      */     default:
/*  835 */       e.throw_exception(ILLEGAL_STATE_EXCEPTION, "More than one callback object required");
/*      */     }
/*  837 */     emitCommonNewInstance(e);
/*      */   }
/*      */ 
/*      */   private void emitNewInstanceMultiarg(ClassEmitter ce, List constructors) {
/*  841 */     CodeEmitter e = ce.begin_method(1, MULTIARG_NEW_INSTANCE, null, null);
/*  842 */     e.load_arg(2);
/*  843 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  844 */     e.new_instance_this();
/*  845 */     e.dup();
/*  846 */     e.load_arg(0);
/*  847 */     EmitUtils.constructor_switch(e, constructors, new ObjectSwitchCallback(e) {
/*      */       public void processCase(Object key, Label end) {
/*  849 */         MethodInfo constructor = (MethodInfo)key;
/*  850 */         Type[] types = constructor.getSignature().getArgumentTypes();
/*  851 */         for (int i = 0; i < types.length; i++) {
/*  852 */           this.val$e.load_arg(1);
/*  853 */           this.val$e.push(i);
/*  854 */           this.val$e.aaload();
/*  855 */           this.val$e.unbox(types[i]);
/*      */         }
/*  857 */         this.val$e.invoke_constructor_this(constructor.getSignature());
/*  858 */         this.val$e.goTo(end);
/*      */       }
/*      */       public void processDefault() {
/*  861 */         this.val$e.throw_exception(Enhancer.ILLEGAL_ARGUMENT_EXCEPTION, "Constructor not found");
/*      */       }
/*      */     });
/*  864 */     e.aconst_null();
/*  865 */     e.invoke_static_this(SET_THREAD_CALLBACKS);
/*  866 */     e.return_value();
/*  867 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitMethods(ClassEmitter ce, List methods, List actualMethods) {
/*  871 */     CallbackGenerator[] generators = CallbackInfo.getGenerators(this.callbackTypes);
/*      */ 
/*  873 */     Map groups = new HashMap();
/*  874 */     Map indexes = new HashMap();
/*  875 */     Map originalModifiers = new HashMap();
/*  876 */     Map positions = CollectionUtils.getIndexMap(methods);
/*      */ 
/*  878 */     Iterator it1 = methods.iterator();
/*  879 */     Iterator it2 = actualMethods != null ? actualMethods.iterator() : null;
/*      */ 
/*  881 */     while (it1.hasNext()) {
/*  882 */       MethodInfo method = (MethodInfo)it1.next();
/*  883 */       Method actualMethod = it2 != null ? (Method)it2.next() : null;
/*  884 */       int index = this.filter.accept(actualMethod);
/*  885 */       if (index >= this.callbackTypes.length) {
/*  886 */         throw new IllegalArgumentException("Callback filter returned an index that is too large: " + index);
/*      */       }
/*  888 */       originalModifiers.put(method, new Integer(actualMethod != null ? actualMethod.getModifiers() : method.getModifiers()));
/*  889 */       indexes.put(method, new Integer(index));
/*  890 */       List group = (List)groups.get(generators[index]);
/*  891 */       if (group == null) {
/*  892 */         groups.put(generators[index], group = new ArrayList(methods.size()));
/*      */       }
/*  894 */       group.add(method);
/*      */     }
/*      */ 
/*  897 */     Set seenGen = new HashSet();
/*  898 */     CodeEmitter se = ce.getStaticHook();
/*  899 */     se.new_instance(THREAD_LOCAL);
/*  900 */     se.dup();
/*  901 */     se.invoke_constructor(THREAD_LOCAL, CSTRUCT_NULL);
/*  902 */     se.putfield("CGLIB$THREAD_CALLBACKS");
/*      */ 
/*  904 */     Object[] state = new Object[1];
/*  905 */     CallbackGenerator.Context context = new CallbackGenerator.Context(originalModifiers, indexes, positions) {
/*      */       public int getOriginalModifiers(MethodInfo method) {
/*  907 */         return ((Integer)this.val$originalModifiers.get(method)).intValue();
/*      */       }
/*      */       public int getIndex(MethodInfo method) {
/*  910 */         return ((Integer)this.val$indexes.get(method)).intValue();
/*      */       }
/*      */       public void emitCallback(CodeEmitter e, int index) {
/*  913 */         Enhancer.this.emitCurrentCallback(e, index);
/*      */       }
/*      */       public Signature getImplSignature(MethodInfo method) {
/*  916 */         return Enhancer.this.rename(method.getSignature(), ((Integer)this.val$positions.get(method)).intValue());
/*      */       }
/*      */       public CodeEmitter beginMethod(ClassEmitter ce, MethodInfo method) {
/*  919 */         CodeEmitter e = EmitUtils.begin_method(ce, method);
/*  920 */         if ((!Enhancer.this.interceptDuringConstruction) && (!TypeUtils.isAbstract(method.getModifiers())))
/*      */         {
/*  922 */           Label constructed = e.make_label();
/*  923 */           e.load_this();
/*  924 */           e.getfield("CGLIB$CONSTRUCTED");
/*  925 */           e.if_jump(154, constructed);
/*  926 */           e.load_this();
/*  927 */           e.load_args();
/*  928 */           e.super_invoke();
/*  929 */           e.return_value();
/*  930 */           e.mark(constructed);
/*      */         }
/*  932 */         return e;
/*      */       }
/*      */     };
/*  935 */     for (int i = 0; i < this.callbackTypes.length; i++) {
/*  936 */       CallbackGenerator gen = generators[i];
/*  937 */       if (!seenGen.contains(gen)) {
/*  938 */         seenGen.add(gen);
/*  939 */         List fmethods = (List)groups.get(gen);
/*  940 */         if (fmethods == null) continue;
/*      */         try {
/*  942 */           gen.generate(ce, context, fmethods);
/*  943 */           gen.generateStatic(se, context, fmethods);
/*      */         } catch (RuntimeException x) {
/*  945 */           throw x;
/*      */         } catch (Exception x) {
/*  947 */           throw new CodeGenerationException(x);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  952 */     se.return_value();
/*  953 */     se.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetThreadCallbacks(ClassEmitter ce) {
/*  957 */     CodeEmitter e = ce.begin_method(9, SET_THREAD_CALLBACKS, null, null);
/*      */ 
/*  961 */     e.getfield("CGLIB$THREAD_CALLBACKS");
/*  962 */     e.load_arg(0);
/*  963 */     e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_SET);
/*  964 */     e.return_value();
/*  965 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitSetStaticCallbacks(ClassEmitter ce) {
/*  969 */     CodeEmitter e = ce.begin_method(9, SET_STATIC_CALLBACKS, null, null);
/*      */ 
/*  973 */     e.load_arg(0);
/*  974 */     e.putfield("CGLIB$STATIC_CALLBACKS");
/*  975 */     e.return_value();
/*  976 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private void emitCurrentCallback(CodeEmitter e, int index) {
/*  980 */     e.load_this();
/*  981 */     e.getfield(getCallbackField(index));
/*  982 */     e.dup();
/*  983 */     Label end = e.make_label();
/*  984 */     e.ifnonnull(end);
/*  985 */     e.pop();
/*  986 */     e.load_this();
/*  987 */     e.invoke_static_this(BIND_CALLBACKS);
/*  988 */     e.load_this();
/*  989 */     e.getfield(getCallbackField(index));
/*  990 */     e.mark(end);
/*      */   }
/*      */ 
/*      */   private void emitBindCallbacks(ClassEmitter ce) {
/*  994 */     CodeEmitter e = ce.begin_method(26, BIND_CALLBACKS, null, null);
/*      */ 
/*  998 */     Local me = e.make_local();
/*  999 */     e.load_arg(0);
/* 1000 */     e.checkcast_this();
/* 1001 */     e.store_local(me);
/*      */ 
/* 1003 */     Label end = e.make_label();
/* 1004 */     e.load_local(me);
/* 1005 */     e.getfield("CGLIB$BOUND");
/* 1006 */     e.if_jump(154, end);
/* 1007 */     e.load_local(me);
/* 1008 */     e.push(1);
/* 1009 */     e.putfield("CGLIB$BOUND");
/*      */ 
/* 1011 */     e.getfield("CGLIB$THREAD_CALLBACKS");
/* 1012 */     e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_GET);
/* 1013 */     e.dup();
/* 1014 */     Label found_callback = e.make_label();
/* 1015 */     e.ifnonnull(found_callback);
/* 1016 */     e.pop();
/*      */ 
/* 1018 */     e.getfield("CGLIB$STATIC_CALLBACKS");
/* 1019 */     e.dup();
/* 1020 */     e.ifnonnull(found_callback);
/* 1021 */     e.pop();
/* 1022 */     e.goTo(end);
/*      */ 
/* 1024 */     e.mark(found_callback);
/* 1025 */     e.checkcast(CALLBACK_ARRAY);
/* 1026 */     e.load_local(me);
/* 1027 */     e.swap();
/* 1028 */     for (int i = this.callbackTypes.length - 1; i >= 0; i--) {
/* 1029 */       if (i != 0) {
/* 1030 */         e.dup2();
/*      */       }
/* 1032 */       e.aaload(i);
/* 1033 */       e.checkcast(this.callbackTypes[i]);
/* 1034 */       e.putfield(getCallbackField(i));
/*      */     }
/*      */ 
/* 1037 */     e.mark(end);
/* 1038 */     e.return_value();
/* 1039 */     e.end_method();
/*      */   }
/*      */ 
/*      */   private static String getCallbackField(int index) {
/* 1043 */     return "CGLIB$CALLBACK_" + index;
/*      */   }
/*      */ 
/*      */   public static abstract interface EnhancerKey
/*      */   {
/*      */     public abstract Object newInstance(String paramString, String[] paramArrayOfString, CallbackFilter paramCallbackFilter, Type[] paramArrayOfType, boolean paramBoolean1, boolean paramBoolean2, Long paramLong);
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.Enhancer
 * JD-Core Version:    0.6.0
 */