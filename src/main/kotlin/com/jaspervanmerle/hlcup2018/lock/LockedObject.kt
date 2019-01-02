package com.jaspervanmerle.hlcup2018.lock

import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

class LockedObject<out L : Lock, out T>(val lock: L, val state: T) {
    inline fun <Y> runWithLock(action: T.() -> Y) = lock.withLock {
        state.action()
    }
}
