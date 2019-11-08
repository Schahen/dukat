@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external open class TestSet {
    open fun isElement(obj: Any?): Boolean
    open fun isObject(obj: Any?): Boolean
    open fun isArray(obj: Any?): Boolean
    open fun isArrayLike(collection: Any?): Boolean
    open fun keyInObj(key: Any?, obj: Any?): Boolean
    open fun negate(predicate: Any?): Boolean
}