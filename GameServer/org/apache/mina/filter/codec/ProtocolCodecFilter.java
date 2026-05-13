/*     */ package org.apache.mina.filter.codec;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Queue;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.file.FileRegion;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.future.DefaultWriteFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.DefaultWriteRequest;
/*     */ import org.apache.mina.core.write.NothingWrittenException;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestWrapper;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ProtocolCodecFilter extends IoFilterAdapter
/*     */ {
/*  52 */   private static final Class<?>[] EMPTY_PARAMS = new Class[0];
/*  53 */   private static final IoBuffer EMPTY_BUFFER = IoBuffer.wrap(new byte[0]);
/*     */ 
/*  55 */   private final AttributeKey ENCODER = new AttributeKey(getClass(), "encoder");
/*  56 */   private final AttributeKey DECODER = new AttributeKey(getClass(), "decoder");
/*  57 */   private final AttributeKey DECODER_OUT = new AttributeKey(getClass(), "decoderOut");
/*  58 */   private final AttributeKey ENCODER_OUT = new AttributeKey(getClass(), "encoderOut");
/*     */   private final ProtocolCodecFactory factory;
/*  63 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */ 
/*     */   public ProtocolCodecFilter(ProtocolCodecFactory factory)
/*     */   {
/*  73 */     if (factory == null) {
/*  74 */       throw new NullPointerException("factory");
/*     */     }
/*  76 */     this.factory = factory;
/*     */   }
/*     */ 
/*     */   public ProtocolCodecFilter(ProtocolEncoder encoder, ProtocolDecoder decoder)
/*     */   {
/*  90 */     if (encoder == null) {
/*  91 */       throw new NullPointerException("encoder");
/*     */     }
/*  93 */     if (decoder == null) {
/*  94 */       throw new NullPointerException("decoder");
/*     */     }
/*     */ 
/*  98 */     this.factory = new ProtocolCodecFactory(encoder, decoder) {
/*     */       public ProtocolEncoder getEncoder(IoSession session) {
/* 100 */         return this.val$encoder;
/*     */       }
/*     */ 
/*     */       public ProtocolDecoder getDecoder(IoSession session) {
/* 104 */         return this.val$decoder;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ProtocolCodecFilter(Class<? extends ProtocolEncoder> encoderClass, Class<? extends ProtocolDecoder> decoderClass)
/*     */   {
/* 121 */     if (encoderClass == null) {
/* 122 */       throw new NullPointerException("encoderClass");
/*     */     }
/* 124 */     if (decoderClass == null) {
/* 125 */       throw new NullPointerException("decoderClass");
/*     */     }
/* 127 */     if (!ProtocolEncoder.class.isAssignableFrom(encoderClass)) {
/* 128 */       throw new IllegalArgumentException("encoderClass: " + encoderClass.getName());
/*     */     }
/*     */ 
/* 131 */     if (!ProtocolDecoder.class.isAssignableFrom(decoderClass)) {
/* 132 */       throw new IllegalArgumentException("decoderClass: " + decoderClass.getName());
/*     */     }
/*     */     try
/*     */     {
/* 136 */       encoderClass.getConstructor(EMPTY_PARAMS);
/*     */     } catch (NoSuchMethodException e) {
/* 138 */       throw new IllegalArgumentException("encoderClass doesn't have a public default constructor.");
/*     */     }
/*     */     try
/*     */     {
/* 142 */       decoderClass.getConstructor(EMPTY_PARAMS);
/*     */     } catch (NoSuchMethodException e) {
/* 144 */       throw new IllegalArgumentException("decoderClass doesn't have a public default constructor.");
/*     */     }
/*     */ 
/* 150 */     this.factory = new ProtocolCodecFactory(encoderClass, decoderClass) {
/*     */       public ProtocolEncoder getEncoder(IoSession session) throws Exception {
/* 152 */         return (ProtocolEncoder)this.val$encoderClass.newInstance();
/*     */       }
/*     */ 
/*     */       public ProtocolDecoder getDecoder(IoSession session) throws Exception {
/* 156 */         return (ProtocolDecoder)this.val$decoderClass.newInstance();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ProtocolEncoder getEncoder(IoSession session)
/*     */   {
/* 169 */     return (ProtocolEncoder)session.getAttribute(this.ENCODER);
/*     */   }
/*     */ 
/*     */   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/* 175 */     if (parent.contains(this)) {
/* 176 */       throw new IllegalArgumentException("You can't add the same filter instance more than once.  Create another instance and add it.");
/*     */     }
/*     */ 
/* 181 */     initCodec(parent.getSession());
/*     */   }
/*     */ 
/*     */   public void onPostRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/* 188 */     disposeCodec(parent.getSession());
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 206 */     if (!(message instanceof IoBuffer)) {
/* 207 */       nextFilter.messageReceived(session, message);
/* 208 */       return;
/*     */     }
/*     */ 
/* 211 */     IoBuffer in = (IoBuffer)message;
/* 212 */     ProtocolDecoder decoder = getDecoder(session);
/* 213 */     ProtocolDecoderOutput decoderOut = getDecoderOut(session, nextFilter);
/*     */ 
/* 219 */     while (in.hasRemaining()) {
/* 220 */       int oldPos = in.position();
/*     */       try {
/* 222 */         synchronized (decoderOut)
/*     */         {
/* 224 */           decoder.decode(session, in, decoderOut);
/*     */         }
/*     */ 
/* 228 */         decoderOut.flush(nextFilter, session);
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */         ProtocolDecoderException pde;
/*     */         ProtocolDecoderException pde;
/* 231 */         if ((t instanceof ProtocolDecoderException))
/* 232 */           pde = (ProtocolDecoderException)t;
/*     */         else {
/* 234 */           pde = new ProtocolDecoderException(t);
/*     */         }
/*     */ 
/* 237 */         if (pde.getHexdump() == null)
/*     */         {
/* 239 */           int curPos = in.position();
/* 240 */           in.position(oldPos);
/* 241 */           pde.setHexdump(in.getHexDump());
/* 242 */           in.position(curPos);
/*     */         }
/*     */ 
/* 246 */         decoderOut.flush(nextFilter, session);
/* 247 */         nextFilter.exceptionCaught(session, pde);
/*     */ 
/* 253 */         if (!(t instanceof RecoverableProtocolDecoderException)) return;  } if (in.position() == oldPos)
/*     */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 264 */     if ((writeRequest instanceof EncodedWriteRequest)) {
/* 265 */       return;
/*     */     }
/*     */ 
/* 268 */     if (!(writeRequest instanceof MessageWriteRequest)) {
/* 269 */       nextFilter.messageSent(session, writeRequest);
/* 270 */       return;
/*     */     }
/*     */ 
/* 273 */     MessageWriteRequest wrappedRequest = (MessageWriteRequest)writeRequest;
/* 274 */     nextFilter.messageSent(session, wrappedRequest.getParentRequest());
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 280 */     Object message = writeRequest.getMessage();
/*     */ 
/* 284 */     if (((message instanceof IoBuffer)) || ((message instanceof FileRegion))) {
/* 285 */       nextFilter.filterWrite(session, writeRequest);
/* 286 */       return;
/*     */     }
/*     */ 
/* 290 */     ProtocolEncoder encoder = getEncoder(session);
/*     */ 
/* 292 */     ProtocolEncoderOutput encoderOut = getEncoderOut(session, nextFilter, writeRequest);
/*     */     try
/*     */     {
/* 297 */       encoder.encode(session, message, encoderOut);
/*     */ 
/* 300 */       ((ProtocolEncoderOutputImpl)encoderOut).flushWithoutFuture();
/*     */ 
/* 303 */       nextFilter.filterWrite(session, new MessageWriteRequest(writeRequest, null));
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */       ProtocolEncoderException pee;
/*     */       ProtocolEncoderException pee;
/* 309 */       if ((t instanceof ProtocolEncoderException))
/* 310 */         pee = (ProtocolEncoderException)t;
/*     */       else {
/* 312 */         pee = new ProtocolEncoderException(t);
/*     */       }
/*     */ 
/* 315 */       throw pee;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 324 */     ProtocolDecoder decoder = getDecoder(session);
/* 325 */     ProtocolDecoderOutput decoderOut = getDecoderOut(session, nextFilter);
/*     */     try
/*     */     {
/* 328 */       decoder.finishDecode(session, decoderOut);
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */       ProtocolDecoderException pde;
/*     */       ProtocolDecoderException pde;
/* 331 */       if ((t instanceof ProtocolDecoderException))
/* 332 */         pde = (ProtocolDecoderException)t;
/*     */       else {
/* 334 */         pde = new ProtocolDecoderException(t);
/*     */       }
/* 336 */       throw pde;
/*     */     }
/*     */     finally {
/* 339 */       disposeCodec(session);
/* 340 */       decoderOut.flush(nextFilter, session);
/*     */     }
/*     */ 
/* 344 */     nextFilter.sessionClosed(session);
/*     */   }
/*     */ 
/*     */   private void initCodec(IoSession session)
/*     */     throws Exception
/*     */   {
/* 446 */     ProtocolDecoder decoder = this.factory.getDecoder(session);
/* 447 */     session.setAttribute(this.DECODER, decoder);
/*     */ 
/* 450 */     ProtocolEncoder encoder = this.factory.getEncoder(session);
/* 451 */     session.setAttribute(this.ENCODER, encoder);
/*     */   }
/*     */ 
/*     */   private void disposeCodec(IoSession session)
/*     */   {
/* 461 */     disposeEncoder(session);
/* 462 */     disposeDecoder(session);
/*     */ 
/* 465 */     disposeDecoderOut(session);
/*     */   }
/*     */ 
/*     */   private void disposeEncoder(IoSession session)
/*     */   {
/* 474 */     ProtocolEncoder encoder = (ProtocolEncoder)session.removeAttribute(this.ENCODER);
/*     */ 
/* 476 */     if (encoder == null) {
/* 477 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 481 */       encoder.dispose(session);
/*     */     } catch (Throwable t) {
/* 483 */       this.logger.warn("Failed to dispose: " + encoder.getClass().getName() + " (" + encoder + ')');
/*     */     }
/*     */   }
/*     */ 
/*     */   private ProtocolDecoder getDecoder(IoSession session)
/*     */   {
/* 495 */     return (ProtocolDecoder)session.getAttribute(this.DECODER);
/*     */   }
/*     */ 
/*     */   private void disposeDecoder(IoSession session)
/*     */   {
/* 504 */     ProtocolDecoder decoder = (ProtocolDecoder)session.removeAttribute(this.DECODER);
/*     */ 
/* 506 */     if (decoder == null) {
/* 507 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 511 */       decoder.dispose(session);
/*     */     } catch (Throwable t) {
/* 513 */       this.logger.warn("Falied to dispose: " + decoder.getClass().getName() + " (" + decoder + ')');
/*     */     }
/*     */   }
/*     */ 
/*     */   private ProtocolDecoderOutput getDecoderOut(IoSession session, IoFilter.NextFilter nextFilter)
/*     */   {
/* 524 */     ProtocolDecoderOutput out = (ProtocolDecoderOutput)session.getAttribute(this.DECODER_OUT);
/*     */ 
/* 526 */     if (out == null)
/*     */     {
/* 528 */       out = new ProtocolDecoderOutputImpl();
/* 529 */       session.setAttribute(this.DECODER_OUT, out);
/*     */     }
/*     */ 
/* 532 */     return out;
/*     */   }
/*     */ 
/*     */   private ProtocolEncoderOutput getEncoderOut(IoSession session, IoFilter.NextFilter nextFilter, WriteRequest writeRequest)
/*     */   {
/* 537 */     ProtocolEncoderOutput out = (ProtocolEncoderOutput)session.getAttribute(this.ENCODER_OUT);
/*     */ 
/* 539 */     if (out == null)
/*     */     {
/* 541 */       out = new ProtocolEncoderOutputImpl(session, nextFilter, writeRequest);
/* 542 */       session.setAttribute(this.ENCODER_OUT, out);
/*     */     }
/*     */ 
/* 545 */     return out;
/*     */   }
/*     */ 
/*     */   private void disposeDecoderOut(IoSession session)
/*     */   {
/* 552 */     session.removeAttribute(this.DECODER_OUT);
/*     */   }
/*     */ 
/*     */   private static class ProtocolEncoderOutputImpl extends AbstractProtocolEncoderOutput
/*     */   {
/*     */     private final IoSession session;
/*     */     private final IoFilter.NextFilter nextFilter;
/*     */     private final WriteRequest writeRequest;
/*     */ 
/*     */     public ProtocolEncoderOutputImpl(IoSession session, IoFilter.NextFilter nextFilter, WriteRequest writeRequest)
/*     */     {
/* 388 */       this.session = session;
/* 389 */       this.nextFilter = nextFilter;
/* 390 */       this.writeRequest = writeRequest;
/*     */     }
/*     */ 
/*     */     public WriteFuture flush() {
/* 394 */       Queue bufferQueue = getMessageQueue();
/* 395 */       WriteFuture future = null;
/*     */       while (true) {
/* 397 */         Object encodedMessage = bufferQueue.poll();
/* 398 */         if (encodedMessage == null)
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 403 */         if ((!(encodedMessage instanceof IoBuffer)) || (((IoBuffer)encodedMessage).hasRemaining()))
/*     */         {
/* 405 */           future = new DefaultWriteFuture(this.session);
/* 406 */           this.nextFilter.filterWrite(this.session, new ProtocolCodecFilter.EncodedWriteRequest(encodedMessage, future, this.writeRequest.getDestination(), null));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 411 */       if (future == null) {
/* 412 */         future = DefaultWriteFuture.newNotWrittenFuture(this.session, new NothingWrittenException(this.writeRequest));
/*     */       }
/*     */ 
/* 416 */       return future;
/*     */     }
/*     */ 
/*     */     public void flushWithoutFuture() {
/* 420 */       Queue bufferQueue = getMessageQueue();
/*     */       while (true) {
/* 422 */         Object encodedMessage = bufferQueue.poll();
/* 423 */         if (encodedMessage == null)
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 428 */         if ((!(encodedMessage instanceof IoBuffer)) || (((IoBuffer)encodedMessage).hasRemaining()))
/*     */         {
/* 430 */           SocketAddress destination = this.writeRequest.getDestination();
/* 431 */           WriteRequest writeRequest = new ProtocolCodecFilter.EncodedWriteRequest(encodedMessage, null, destination, null);
/*     */ 
/* 433 */           this.nextFilter.filterWrite(this.session, writeRequest);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ProtocolDecoderOutputImpl extends AbstractProtocolDecoderOutput
/*     */   {
/*     */     public void flush(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     {
/* 371 */       Queue messageQueue = getMessageQueue();
/* 372 */       while (!messageQueue.isEmpty())
/* 373 */         nextFilter.messageReceived(session, messageQueue.poll());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MessageWriteRequest extends WriteRequestWrapper
/*     */   {
/*     */     private MessageWriteRequest(WriteRequest writeRequest)
/*     */     {
/* 356 */       super();
/*     */     }
/*     */ 
/*     */     public Object getMessage()
/*     */     {
/* 361 */       return ProtocolCodecFilter.EMPTY_BUFFER;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class EncodedWriteRequest extends DefaultWriteRequest
/*     */   {
/*     */     private EncodedWriteRequest(Object encodedMessage, WriteFuture future, SocketAddress destination)
/*     */     {
/* 350 */       super(future, destination);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolCodecFilter
 * JD-Core Version:    0.6.0
 */