package de.hpi.dbs2.exercise2

open class IndexEntry<K : Comparable<K>, V : Any>(
    val key: K,
    val value: V
) {
    override fun toString(): String = "[$key]->$value"
}
