# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ================================================================================================
# Reader App ProGuard Rules
# ================================================================================================

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signature for reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ================================================================================================
# Kotlin
# ================================================================================================
-dontwarn kotlin.**
-dontwarn kotlinx.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ================================================================================================
# Reader App - Data Models
# ================================================================================================
# Keep all data models used with Gson/Retrofit
-keep class com.example.reader.data.source.remote.api.dto.** { *; }
-keep class com.example.reader.data.model.** { *; }
-keep class com.example.reader.domain.model.** { *; }

# Keep data class fields and constructors
-keepclassmembers class com.example.reader.domain.model.** {
    <fields>;
    <init>(...);
}
-keepclassmembers class com.example.reader.data.model.** {
    <fields>;
    <init>(...);
}
-keepclassmembers class com.example.reader.data.source.remote.api.dto.** {
    <fields>;
    <init>(...);
}

# Keep Realm models
-keep class com.example.reader.data.realm.** { *; }
-keep class com.example.reader.data.source.local.realm.entities.** { *; }

# ================================================================================================
# Retrofit & OkHttp
# ================================================================================================
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ================================================================================================
# Gson (used by Retrofit)
# ================================================================================================
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# ================================================================================================
# Domain Models & DTOs
# ================================================================================================
# Keep all domain models (they are data classes used throughout the app)
-keep class com.example.reader.domain.model.** { *; }

# Keep all DTOs (used by Retrofit/Gson for JSON serialization)
-keep class com.example.reader.data.source.remote.api.dto.** { *; }

# Keep all Realm entities
-keep class com.example.reader.data.source.local.realm.entities.** { *; }

# ================================================================================================
# Realm Kotlin
# ================================================================================================
-keep class io.realm.kotlin.** { *; }
-keep class io.realm.kotlin.types.** { *; }
-dontwarn io.realm.kotlin.**

# Keep Realm model classes
-keep @io.realm.kotlin.types.annotations.PersistedName class * { *; }
-keep class * implements io.realm.kotlin.types.RealmObject { *; }
-keep class * implements io.realm.kotlin.types.EmbeddedRealmObject { *; }

# Keep Realm internal classes
-keep class io.realm.kotlin.internal.** { *; }
-keep class io.realm.kotlin.mongodb.** { *; }

# ================================================================================================
# Firebase
# ================================================================================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep Firebase Auth classes
-keep class com.google.firebase.auth.** { *; }

# Keep Firestore classes
-keep class com.google.firebase.firestore.** { *; }

# Keep Firebase model classes (if using Firestore with custom objects)
-keepclassmembers class com.example.reader.data.realm.** {
    *;
}

# ================================================================================================
# Hilt / Dagger
# ================================================================================================
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.inject.**
-dontwarn javax.annotation.**

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Hilt modules
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.components.SingletonComponent class * { *; }

# Keep injected constructors
-keepclasseswithmembernames class * {
    @javax.inject.Inject <init>(...);
}

# Keep injected fields
-keepclasseswithmembernames class * {
    @javax.inject.Inject <fields>;
}

# Keep injected methods
-keepclasseswithmembernames class * {
    @javax.inject.Inject <methods>;
}

# ================================================================================================
# Jetpack Compose
# ================================================================================================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# Keep ViewModels with Hilt
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# ================================================================================================
# Coil (Image Loading)
# ================================================================================================
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ================================================================================================
# AndroidX
# ================================================================================================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Keep Navigation component classes
-keep class androidx.navigation.** { *; }

# Keep Lifecycle classes
-keep class androidx.lifecycle.** { *; }

# ================================================================================================
# Kotlin Serialization (if used)
# ================================================================================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.example.reader.**$$serializer { *; }
-keepclassmembers class com.example.reader.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.reader.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ================================================================================================
# Coroutines
# ================================================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# ================================================================================================
# Enum Classes
# ================================================================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ================================================================================================
# Parcelable
# ================================================================================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ================================================================================================
# Serializable
# ================================================================================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ================================================================================================
# Remove Logging (for release builds)
# ================================================================================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ================================================================================================
# General Android
# ================================================================================================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# ================================================================================================
# R8 Optimizations
# ================================================================================================
# Enable aggressive optimizations
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization options
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification
-repackageclasses ''
