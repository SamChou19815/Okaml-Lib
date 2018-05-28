package com.developersam.fp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Test

/**
 * [FpListTest] contains a collection of tests for map.
 */
class FpMapTest {

    /**
     * [emptyMap] is the commonly used empty map.
     */
    private val emptyMap: FpMap<String, Int> = FpMap.empty()
    /**
     * [singletonMap] is the commonly used map that contains "hi" -> 0.
     */
    private val singletonMap: FpMap<String, Int> = FpMap.singleton(key = "hi", value = 0)
    /**
     * [simplePair] is the commonly used simple pair that is "hi" -> 0.
     */
    private val simplePair: Pair<String, Int> = "hi" to 0

    /**
     * [always] function.
     */
    private val always: (String, Int) -> Boolean = { _, _ -> true }
    /**
     * [never] function.
     */
    private val never: (String, Int) -> Boolean = { _, _ -> false }
    /**
     * [eq] function tests whether it's equal to [simplePair].
     */
    private val eq: (String, Int) -> Boolean = { k, v -> k == "hi" && v == 0 }
    /**
     * [nEq] function tests whether it's not equal to [simplePair].
     */
    private val nEq: (String, Int) -> Boolean = { k, v -> k != "hi" || v != 0 }

    /**
     * Test contains function.
     */
    @Test
    fun containsTest() {
        // empty list
        assertEquals(false, "hi" in emptyMap)
        assertEquals(false, "hello" in emptyMap)
        // singleton list
        assertEquals(true, "hi" in singletonMap)
        assertEquals(false, "hello" in singletonMap)
    }

    /**
     * Test put and remove function.
     */
    @Test
    fun putRemoveTest() {
        val algorithmsPair = listOf("A" to 0, "L" to 1, "G" to 2, "O" to 3,
                "R" to 4, "I" to 5, "T" to 6, "H" to 7, "M" to 8, "S" to 9)
        val l = algorithmsPair.size
        val c: Comparator<Pair<String, Int>> = Comparator { p1, p2 -> p1.first.compareTo(p2.first) }
        var map = emptyMap
        for (i in 0 until l) {
            val pair = algorithmsPair[i]
            val newMap = map.put(pair.first, pair.second)
            val l1 = newMap.bindings.toArrayList().sortedWith(c)
            val l2 = algorithmsPair.subList(fromIndex = 0, toIndex = i + 1).sortedWith(c)
            assertEquals(l1, l2)
            map = newMap
        }
        for (i in 0 until l) {
            val pair = algorithmsPair[i]
            val newMap = map.remove(pair.first)
            val l1 = newMap.bindings.toArrayList().sortedWith(c)
            val l2 = algorithmsPair.subList(fromIndex = i + 1, toIndex = l).sortedWith(c)
            assertEquals(l1, l2)
            map = newMap
        }
        assertEquals(emptyMap, map)
    }

    /**
     * Test for each function.
     */
    @Test
    fun forEachTest() {
        assertEquals(Unit, emptyMap.forEach { _, _ -> Unit })
        assertEquals(Unit, singletonMap.forEach { _, _ -> Unit })
    }

    /**
     * Test exists function.
     */
    @Test
    fun existsTest() {
        // empty map
        assertEquals(false, emptyMap.exists(f = always))
        assertEquals(false, emptyMap.exists(f = eq))
        assertEquals(false, emptyMap.exists(f = nEq))
        // singleton map
        assertEquals(true, singletonMap.exists(f = always))
        assertEquals(true, singletonMap.exists(f = eq))
        assertEquals(false, singletonMap.exists(f = nEq))
    }

    /**
     * Test for all function.
     */
    @Test
    fun forAllTest() {
        // empty map
        assertEquals(true, emptyMap.forAll(f = never))
        assertEquals(true, emptyMap.forAll(f = eq))
        assertEquals(true, emptyMap.forAll(f = nEq))
        // singleton map
        assertEquals(false, singletonMap.forAll(f = never))
        assertEquals(true, singletonMap.forAll(f = eq))
        assertEquals(false, singletonMap.forAll(f = nEq))
    }

    /**
     * Test filter function.
     */
    @Test
    fun filterTest() {
        // empty map
        assertEquals(emptyMap, emptyMap.filter(f = always))
        assertEquals(emptyMap, emptyMap.filter(f = never))
        assertEquals(emptyMap, emptyMap.filter(f = eq))
        assertEquals(emptyMap, emptyMap.filter(f = nEq))
        // singleton map
        assertEquals(singletonMap, singletonMap.filter(f = always))
        assertEquals(emptyMap, singletonMap.filter(f = never))
        assertEquals(singletonMap, singletonMap.filter(f = eq))
        assertEquals(emptyMap, singletonMap.filter(f = nEq))
    }

    /**
     * Test partition function.
     */
    @Test
    fun partitionTest() {
        // empty map
        assertEquals(emptyMap to emptyMap, emptyMap.partition(f = always))
        assertEquals(emptyMap to emptyMap, emptyMap.partition(f = never))
        assertEquals(emptyMap to emptyMap, emptyMap.partition(f = eq))
        assertEquals(emptyMap to emptyMap, emptyMap.partition(f = nEq))
        // singleton map
        assertEquals(singletonMap to emptyMap, singletonMap.partition(f = always))
        assertEquals(emptyMap to singletonMap, singletonMap.partition(f = never))
        assertEquals(singletonMap to emptyMap, singletonMap.partition(f = eq))
        assertEquals(emptyMap to singletonMap, singletonMap.partition(f = nEq))
    }

    /**
     * Test peek function.
     */
    @Test
    fun peekTest() {
        assertNull(emptyMap.peek())
        assertNotNull(singletonMap.peek())
        assertNotNull(FpMap.create(simplePair, simplePair, "d" to 4).peek())
    }

    /**
     * Test map function.
     */
    @Test
    fun mapTest() {
        // empty map
        assertEquals(emptyMap, emptyMap.mapByKey { simplePair.first })
        assertEquals(emptyMap, emptyMap.mapByValue { simplePair.second })
        assertEquals(emptyMap, emptyMap.mapByKeyValuePair { _, _ -> simplePair })
        // singleton map
        assertEquals(singletonMap, singletonMap.mapByKey { simplePair.first })
        assertEquals(singletonMap, singletonMap.mapByValue { simplePair.second })
        assertEquals(singletonMap, singletonMap.mapByKeyValuePair { _, _ -> simplePair })
        assertEquals(FpMap.create("ddd" to 0), singletonMap.mapByKey { "ddd" })
        assertEquals(FpMap.create("hi" to 1), singletonMap.mapByValue { 1 })
        assertEquals(FpMap.create("d" to 1), singletonMap.mapByKeyValuePair { _, _ -> "d" to 1 })
    }

}
