package we_share_ad.server.util

import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.UnaryOperator
import kotlin.collections.ArrayList

open class SynchronizedList<E>: ArrayList<E>() {
    override fun add(element: E) = synchronized(this) { super.add(element) }

    override fun add(index: Int, element: E) = synchronized(this) { super.add(index, element) }

    override fun addAll(elements: Collection<E>) = synchronized(this) { super.addAll(elements) }

    override fun addAll(index: Int, elements: Collection<E>) = synchronized(this) { super.addAll(index, elements) }

    override fun clear() = synchronized(this) { super.clear() }

    override fun contains(element: E) = synchronized(this) { super.contains(element) }

    override fun ensureCapacity(minCapacity: Int) = synchronized(this) { super.ensureCapacity(minCapacity) }

    override fun forEach(action: Consumer<in E>?) = synchronized(this) { super.forEach(action) }

    override fun get(index: Int) = synchronized(this) { super.get(index) }

    override fun indexOf(element: E) = synchronized(this) { super.indexOf(element) }

    override fun isEmpty() = synchronized(this) { super.isEmpty() }

    override fun iterator() = synchronized(this) { super.iterator() }

    override fun lastIndexOf(element: E) = synchronized(this) { super.lastIndexOf(element) }

    override fun listIterator() = synchronized(this) { super.listIterator() }

    override fun removeAt(index: Int) = synchronized(this) { super.removeAt(index) }

    override fun remove(element: E) = synchronized(this) { super.remove(element) }

    override fun removeAll(elements: Collection<E>) = synchronized(this) { super.removeAll(elements) }

    override fun removeIf(filter: Predicate<in E>) = synchronized(this) { super.removeIf(filter) }

    override fun removeRange(fromIndex: Int, toIndex: Int) = synchronized(this) { super.removeRange(fromIndex, toIndex) }

    override fun replaceAll(operator: UnaryOperator<E>) = synchronized(this) { super.replaceAll(operator) }

    override fun retainAll(elements: Collection<E>) = synchronized(this) { super.retainAll(elements) }

    override fun set(index: Int, element: E) = synchronized(this) { super.set(index, element) }

    override val size: Int
        get() = synchronized(this) { super.size }

    override fun sort(c: Comparator<in E>?) = synchronized(this) { sortWith(c!!) }

    override fun spliterator() = synchronized(this) { super.spliterator() }

    override fun subList(fromIndex: Int, toIndex: Int) = synchronized(this) { super.subList(fromIndex, toIndex) }

    override fun toArray(): Array<Any> = synchronized(this) { super.toArray() }

    override fun <T : Any?> toArray(a: Array<T>): Array<T> = synchronized(this) { super.toArray(a) }

    override fun trimToSize() = synchronized(this) { super.trimToSize() }
}