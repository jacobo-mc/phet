/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_lwjgl_opengl_AWTSurfaceLock */

#ifndef _Included_org_lwjgl_opengl_AWTSurfaceLock
#define _Included_org_lwjgl_opengl_AWTSurfaceLock
#ifdef __cplusplus
extern "C" {
#endif
#undef org_lwjgl_opengl_AWTSurfaceLock_WAIT_DELAY_MILLIS
#define org_lwjgl_opengl_AWTSurfaceLock_WAIT_DELAY_MILLIS 100L
/*
 * Class:     org_lwjgl_opengl_AWTSurfaceLock
 * Method:    createHandle
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_org_lwjgl_opengl_AWTSurfaceLock_createHandle
  (JNIEnv *, jclass);

/*
 * Class:     org_lwjgl_opengl_AWTSurfaceLock
 * Method:    lockAndInitHandle
 * Signature: (Ljava/nio/ByteBuffer;Ljava/awt/Canvas;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_AWTSurfaceLock_lockAndInitHandle
  (JNIEnv *, jclass, jobject, jobject);

/*
 * Class:     org_lwjgl_opengl_AWTSurfaceLock
 * Method:    nUnlock
 * Signature: (Ljava/nio/ByteBuffer;)V
 */
JNIEXPORT void JNICALL Java_org_lwjgl_opengl_AWTSurfaceLock_nUnlock
  (JNIEnv *, jclass, jobject);

#ifdef __cplusplus
}
#endif
#endif
