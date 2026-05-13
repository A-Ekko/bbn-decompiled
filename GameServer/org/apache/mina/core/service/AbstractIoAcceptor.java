/*     */ package org.apache.mina.core.service;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ 
/*     */ public abstract class AbstractIoAcceptor extends AbstractIoService
/*     */   implements IoAcceptor
/*     */ {
/*  48 */   private final List<SocketAddress> defaultLocalAddresses = new ArrayList();
/*     */ 
/*  50 */   private final List<SocketAddress> unmodifiableDefaultLocalAddresses = Collections.unmodifiableList(this.defaultLocalAddresses);
/*     */ 
/*  52 */   private final Set<SocketAddress> boundAddresses = new HashSet();
/*     */ 
/*  55 */   private boolean disconnectOnUnbind = true;
/*     */ 
/*  62 */   protected final Object bindLock = new Object();
/*     */ 
/*     */   protected AbstractIoAcceptor(IoSessionConfig sessionConfig, Executor executor)
/*     */   {
/*  79 */     super(sessionConfig, executor);
/*  80 */     this.defaultLocalAddresses.add(null);
/*     */   }
/*     */ 
/*     */   public SocketAddress getLocalAddress()
/*     */   {
/*  87 */     Set localAddresses = getLocalAddresses();
/*  88 */     if (localAddresses.isEmpty()) {
/*  89 */       return null;
/*     */     }
/*  91 */     return (SocketAddress)localAddresses.iterator().next();
/*     */   }
/*     */ 
/*     */   public final Set<SocketAddress> getLocalAddresses()
/*     */   {
/*  99 */     Set localAddresses = new HashSet();
/* 100 */     synchronized (this.bindLock) {
/* 101 */       localAddresses.addAll(this.boundAddresses);
/*     */     }
/* 103 */     return localAddresses;
/*     */   }
/*     */ 
/*     */   public SocketAddress getDefaultLocalAddress()
/*     */   {
/* 110 */     if (this.defaultLocalAddresses.isEmpty()) {
/* 111 */       return null;
/*     */     }
/* 113 */     return (SocketAddress)this.defaultLocalAddresses.iterator().next();
/*     */   }
/*     */ 
/*     */   public final void setDefaultLocalAddress(SocketAddress localAddress)
/*     */   {
/* 120 */     setDefaultLocalAddresses(localAddress, new SocketAddress[0]);
/*     */   }
/*     */ 
/*     */   public final List<SocketAddress> getDefaultLocalAddresses()
/*     */   {
/* 127 */     return this.unmodifiableDefaultLocalAddresses;
/*     */   }
/*     */ 
/*     */   public final void setDefaultLocalAddresses(List<? extends SocketAddress> localAddresses)
/*     */   {
/* 135 */     if (localAddresses == null) {
/* 136 */       throw new NullPointerException("localAddresses");
/*     */     }
/* 138 */     setDefaultLocalAddresses(localAddresses);
/*     */   }
/*     */ 
/*     */   public final void setDefaultLocalAddresses(Iterable<? extends SocketAddress> localAddresses)
/*     */   {
/* 145 */     if (localAddresses == null) {
/* 146 */       throw new NullPointerException("localAddresses");
/*     */     }
/*     */ 
/* 149 */     synchronized (this.bindLock) {
/* 150 */       if (!this.boundAddresses.isEmpty()) {
/* 151 */         throw new IllegalStateException("localAddress can't be set while the acceptor is bound.");
/*     */       }
/*     */ 
/* 155 */       Collection newLocalAddresses = new ArrayList();
/*     */ 
/* 157 */       for (SocketAddress a : localAddresses) {
/* 158 */         checkAddressType(a);
/* 159 */         newLocalAddresses.add(a);
/*     */       }
/*     */ 
/* 162 */       if (newLocalAddresses.isEmpty()) {
/* 163 */         throw new IllegalArgumentException("empty localAddresses");
/*     */       }
/*     */ 
/* 166 */       this.defaultLocalAddresses.clear();
/* 167 */       this.defaultLocalAddresses.addAll(newLocalAddresses);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void setDefaultLocalAddresses(SocketAddress firstLocalAddress, SocketAddress[] otherLocalAddresses)
/*     */   {
/* 176 */     if (otherLocalAddresses == null) {
/* 177 */       otherLocalAddresses = new SocketAddress[0];
/*     */     }
/*     */ 
/* 180 */     Collection newLocalAddresses = new ArrayList(otherLocalAddresses.length + 1);
/*     */ 
/* 183 */     newLocalAddresses.add(firstLocalAddress);
/* 184 */     for (SocketAddress a : otherLocalAddresses) {
/* 185 */       newLocalAddresses.add(a);
/*     */     }
/*     */ 
/* 188 */     setDefaultLocalAddresses(newLocalAddresses);
/*     */   }
/*     */ 
/*     */   public final boolean isCloseOnDeactivation()
/*     */   {
/* 195 */     return this.disconnectOnUnbind;
/*     */   }
/*     */ 
/*     */   public final void setCloseOnDeactivation(boolean disconnectClientsOnUnbind)
/*     */   {
/* 202 */     this.disconnectOnUnbind = disconnectClientsOnUnbind;
/*     */   }
/*     */ 
/*     */   public final void bind()
/*     */     throws IOException
/*     */   {
/* 209 */     bind(getDefaultLocalAddresses());
/*     */   }
/*     */ 
/*     */   public final void bind(SocketAddress[] addresses)
/*     */     throws IOException
/*     */   {
/* 216 */     if ((addresses == null) || (addresses.length == 0)) {
/* 217 */       bind(getDefaultLocalAddresses());
/*     */     }
/*     */ 
/* 220 */     if (addresses.length == 1) {
/* 221 */       List localAddresses = new ArrayList(addresses.length);
/* 222 */       for (SocketAddress address : addresses) {
/* 223 */         localAddresses.add(address);
/*     */       }
/*     */ 
/* 226 */       bind(localAddresses);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void bind(Iterable<? extends SocketAddress> localAddresses)
/*     */     throws IOException
/*     */   {
/* 234 */     if (isDisposing()) {
/* 235 */       throw new IllegalStateException("Already disposed.");
/*     */     }
/*     */ 
/* 238 */     if (localAddresses == null) {
/* 239 */       throw new NullPointerException("localAddresses");
/*     */     }
/*     */ 
/* 242 */     List localAddressesCopy = new ArrayList();
/*     */ 
/* 244 */     for (SocketAddress a : localAddresses) {
/* 245 */       checkAddressType(a);
/* 246 */       localAddressesCopy.add(a);
/*     */     }
/*     */ 
/* 249 */     if (localAddressesCopy.isEmpty()) {
/* 250 */       throw new IllegalArgumentException("localAddresses is empty.");
/*     */     }
/*     */ 
/* 253 */     boolean activate = false;
/* 254 */     synchronized (this.bindLock) {
/* 255 */       if (this.boundAddresses.isEmpty()) {
/* 256 */         activate = true;
/*     */       }
/*     */ 
/* 259 */       if (getHandler() == null) {
/* 260 */         throw new IllegalStateException("handler is not set.");
/*     */       }
/*     */       try
/*     */       {
/* 264 */         this.boundAddresses.addAll(bindInternal(localAddressesCopy));
/*     */       } catch (IOException e) {
/* 266 */         throw e;
/*     */       } catch (RuntimeException e) {
/* 268 */         throw e;
/*     */       } catch (Throwable e) {
/* 270 */         throw new RuntimeIoException("Failed to bind to: " + getLocalAddresses(), e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 275 */     if (activate)
/* 276 */       getListeners().fireServiceActivated();
/*     */   }
/*     */ 
/*     */   public final void unbind()
/*     */   {
/* 284 */     unbind(getLocalAddresses());
/*     */   }
/*     */ 
/*     */   public final void unbind(SocketAddress localAddress)
/*     */   {
/* 291 */     if (localAddress == null) {
/* 292 */       throw new NullPointerException("localAddress");
/*     */     }
/*     */ 
/* 295 */     List localAddresses = new ArrayList(1);
/* 296 */     localAddresses.add(localAddress);
/* 297 */     unbind(localAddresses);
/*     */   }
/*     */ 
/*     */   public final void unbind(SocketAddress firstLocalAddress, SocketAddress[] otherLocalAddresses)
/*     */   {
/* 305 */     if (firstLocalAddress == null) {
/* 306 */       throw new NullPointerException("firstLocalAddress");
/*     */     }
/* 308 */     if (otherLocalAddresses == null) {
/* 309 */       throw new NullPointerException("otherLocalAddresses");
/*     */     }
/*     */ 
/* 312 */     List localAddresses = new ArrayList();
/* 313 */     localAddresses.add(firstLocalAddress);
/* 314 */     Collections.addAll(localAddresses, otherLocalAddresses);
/* 315 */     unbind(localAddresses);
/*     */   }
/*     */ 
/*     */   public final void unbind(Iterable<? extends SocketAddress> localAddresses)
/*     */   {
/* 322 */     if (localAddresses == null) {
/* 323 */       throw new NullPointerException("localAddresses");
/*     */     }
/*     */ 
/* 326 */     boolean deactivate = false;
/* 327 */     synchronized (this.bindLock) {
/* 328 */       if (this.boundAddresses.isEmpty()) {
/* 329 */         return;
/*     */       }
/*     */ 
/* 332 */       List localAddressesCopy = new ArrayList();
/* 333 */       int specifiedAddressCount = 0;
/* 334 */       for (SocketAddress a : localAddresses) {
/* 335 */         specifiedAddressCount++;
/* 336 */         if ((a != null) && (this.boundAddresses.contains(a))) {
/* 337 */           localAddressesCopy.add(a);
/*     */         }
/*     */       }
/* 340 */       if (specifiedAddressCount == 0) {
/* 341 */         throw new IllegalArgumentException("localAddresses is empty.");
/*     */       }
/*     */ 
/* 344 */       if (!localAddressesCopy.isEmpty()) {
/*     */         try {
/* 346 */           unbind0(localAddressesCopy);
/*     */         } catch (RuntimeException e) {
/* 348 */           throw e;
/*     */         } catch (Throwable e) {
/* 350 */           throw new RuntimeIoException("Failed to unbind from: " + getLocalAddresses(), e);
/*     */         }
/*     */ 
/* 354 */         this.boundAddresses.removeAll(localAddressesCopy);
/* 355 */         if (this.boundAddresses.isEmpty()) {
/* 356 */           deactivate = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 361 */     if (deactivate)
/* 362 */       getListeners().fireServiceDeactivated();
/*     */   }
/*     */ 
/*     */   protected abstract Set<SocketAddress> bindInternal(List<? extends SocketAddress> paramList)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void unbind0(List<? extends SocketAddress> paramList)
/*     */     throws Exception;
/*     */ 
/*     */   public String toString()
/*     */   {
/* 381 */     TransportMetadata m = getTransportMetadata();
/* 382 */     return '(' + m.getProviderName() + ' ' + m.getName() + " acceptor: " + (isActive() ? "localAddress(es): " + getLocalAddresses() + ", managedSessionCount: " + getManagedSessionCount() : "not bound") + ')';
/*     */   }
/*     */ 
/*     */   private void checkAddressType(SocketAddress a)
/*     */   {
/* 390 */     if ((a != null) && (!getTransportMetadata().getAddressType().isAssignableFrom(a.getClass())))
/*     */     {
/* 393 */       throw new IllegalArgumentException("localAddress type: " + a.getClass().getSimpleName() + " (expected: " + getTransportMetadata().getAddressType().getSimpleName() + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class AcceptorOperationFuture extends AbstractIoService.ServiceOperationFuture
/*     */   {
/*     */     private final List<SocketAddress> localAddresses;
/*     */ 
/*     */     public AcceptorOperationFuture(List<? extends SocketAddress> localAddresses) {
/* 403 */       this.localAddresses = new ArrayList(localAddresses);
/*     */     }
/*     */ 
/*     */     public final List<SocketAddress> getLocalAddresses() {
/* 407 */       return Collections.unmodifiableList(this.localAddresses);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 414 */       StringBuilder sb = new StringBuilder();
/*     */ 
/* 416 */       sb.append("Acceptor operation : ");
/*     */       boolean isFirst;
/* 418 */       if (this.localAddresses != null) {
/* 419 */         isFirst = true;
/*     */ 
/* 421 */         for (SocketAddress address : this.localAddresses) {
/* 422 */           if (isFirst)
/* 423 */             isFirst = false;
/*     */           else {
/* 425 */             sb.append(", ");
/*     */           }
/*     */ 
/* 428 */           sb.append(address);
/*     */         }
/*     */       }
/* 431 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.AbstractIoAcceptor
 * JD-Core Version:    0.6.0
 */