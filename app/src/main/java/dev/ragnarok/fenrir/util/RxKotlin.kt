package dev.ragnarok.fenrir.util

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource

object RxKotlin {
    inline fun <T, U, R> zip(
        s1: SingleSource<T>,
        s2: SingleSource<U>,
        crossinline zipper: (T, U) -> R
    ): Single<R> = Single.zip(s1, s2, { t, u -> zipper.invoke(t, u) })
}