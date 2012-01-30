/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Adam\\workspace\\AudioEqualizer\\src\\com\\brotherstuck\\equalizer\\EqInterface.aidl
 */
package com.brotherstuck.equalizer;
public interface EqInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.brotherstuck.equalizer.EqInterface
{
private static final java.lang.String DESCRIPTOR = "com.brotherstuck.equalizer.EqInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.brotherstuck.equalizer.EqInterface interface,
 * generating a proxy if needed.
 */
public static com.brotherstuck.equalizer.EqInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.brotherstuck.equalizer.EqInterface))) {
return ((com.brotherstuck.equalizer.EqInterface)iin);
}
return new com.brotherstuck.equalizer.EqInterface.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getBandLevelLow:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBandLevelLow();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getBandLevelHigh:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBandLevelHigh();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getNumberOfBands:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getNumberOfBands();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCenterFreq:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getCenterFreq(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getBandLevel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getBandLevel(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setBandLevel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setBandLevel(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_isEqEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isEqEnabled();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setEqEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setEqEnabled(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isBassBoostEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isBassBoostEnabled();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setBassBoostEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setBassBoostEnabled(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getBassBoostStrength:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getBassBoostStrength();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setBassBoostStrength:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setBassBoostStrength(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isVirtualizerEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isVirtualizerEnabled();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setVirtualizerEnabled:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setVirtualizerEnabled(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getVirtualizerStrength:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getVirtualizerStrength();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setVirtualizerStrength:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setVirtualizerStrength(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_usePreset:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.usePreset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setProperties:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setProperties(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getProperties:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getProperties();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_isRunning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isRunning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.brotherstuck.equalizer.EqInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public int getBandLevelLow() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBandLevelLow, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getBandLevelHigh() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBandLevelHigh, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getNumberOfBands() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getNumberOfBands, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCenterFreq(int band) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(band);
mRemote.transact(Stub.TRANSACTION_getCenterFreq, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getBandLevel(int band) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(band);
mRemote.transact(Stub.TRANSACTION_getBandLevel, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setBandLevel(int band, int level) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(band);
_data.writeInt(level);
mRemote.transact(Stub.TRANSACTION_setBandLevel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isEqEnabled() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isEqEnabled, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setEqEnabled(boolean isEnabled) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnabled)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setEqEnabled, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isBassBoostEnabled() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isBassBoostEnabled, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setBassBoostEnabled(boolean isEnabled) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnabled)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setBassBoostEnabled, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getBassBoostStrength() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getBassBoostStrength, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setBassBoostStrength(int strength) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(strength);
mRemote.transact(Stub.TRANSACTION_setBassBoostStrength, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean isVirtualizerEnabled() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isVirtualizerEnabled, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setVirtualizerEnabled(boolean isEnabled) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isEnabled)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setVirtualizerEnabled, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getVirtualizerStrength() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getVirtualizerStrength, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setVirtualizerStrength(int strength) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(strength);
mRemote.transact(Stub.TRANSACTION_setVirtualizerStrength, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void usePreset(int preset) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(preset);
mRemote.transact(Stub.TRANSACTION_usePreset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setProperties(java.lang.String properties) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(properties);
mRemote.transact(Stub.TRANSACTION_setProperties, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getProperties() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getProperties, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isRunning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isRunning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getBandLevelLow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getBandLevelHigh = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getNumberOfBands = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getCenterFreq = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getBandLevel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setBandLevel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_isEqEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setEqEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_isBassBoostEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_setBassBoostEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getBassBoostStrength = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_setBassBoostStrength = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_isVirtualizerEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_setVirtualizerEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getVirtualizerStrength = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_setVirtualizerStrength = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_usePreset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_setProperties = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_getProperties = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_isRunning = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
}
public int getBandLevelLow() throws android.os.RemoteException;
public int getBandLevelHigh() throws android.os.RemoteException;
public int getNumberOfBands() throws android.os.RemoteException;
public int getCenterFreq(int band) throws android.os.RemoteException;
public int getBandLevel(int band) throws android.os.RemoteException;
public void setBandLevel(int band, int level) throws android.os.RemoteException;
public boolean isEqEnabled() throws android.os.RemoteException;
public void setEqEnabled(boolean isEnabled) throws android.os.RemoteException;
public boolean isBassBoostEnabled() throws android.os.RemoteException;
public void setBassBoostEnabled(boolean isEnabled) throws android.os.RemoteException;
public int getBassBoostStrength() throws android.os.RemoteException;
public void setBassBoostStrength(int strength) throws android.os.RemoteException;
public boolean isVirtualizerEnabled() throws android.os.RemoteException;
public void setVirtualizerEnabled(boolean isEnabled) throws android.os.RemoteException;
public int getVirtualizerStrength() throws android.os.RemoteException;
public void setVirtualizerStrength(int strength) throws android.os.RemoteException;
public void usePreset(int preset) throws android.os.RemoteException;
public void setProperties(java.lang.String properties) throws android.os.RemoteException;
public java.lang.String getProperties() throws android.os.RemoteException;
public boolean isRunning() throws android.os.RemoteException;
}
