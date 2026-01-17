# Add project specific ProGuard rules here.

# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.hamhub.app.**$$serializer { *; }
-keepclassmembers class com.hamhub.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.hamhub.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Room entities
-keep class com.hamhub.app.data.local.entity.** { *; }

# Keep Retrofit interfaces
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
