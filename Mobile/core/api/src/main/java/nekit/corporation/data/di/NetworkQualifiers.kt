package nekit.corporation.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class DefaultOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class DefaultRetrofit
