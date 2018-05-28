package com.developersam.fp

import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * [FpListTest] contains a collection of tests for list.
 */
class FpListTest {

    /**
     * [emptyList] is the commonly used empty list.
     */
    private val emptyList: FpList<String> = FpList.empty()
    /**
     * [singletonList] is the commonly used singleton test that contains only "hi".
     */
    private val singletonList: FpList<String> = FpList.singleton(data = "hi")
    /**
     * [complexList] is the commonly used list that contains more than 1 values.
     */
    private val complexList: FpList<String> = "hello" cons ("world" cons emptyList)

    @Test
    fun createTest() {
        assertEquals(emptyList, FpList.create<String>())
        assertEquals(singletonList, FpList.create("hi"))
        assertEquals(complexList, FpList.create("hello", "world"))
    }

    /**
     * [ignore] simply ignores the value passed in.
     */
    @Suppress(names = ["UNUSED_PARAMETER"])
    private fun <T> ignore(v: T): Unit = Unit

    /**
     * Test head function for [emptyList].
     */
    @Test(expected = NotFoundError::class)
    fun emptyListHeadTest(): Unit = ignore(v = emptyList.head)

    /**
     * Test tail function for [emptyList].
     */
    @Test(expected = NotFoundError::class)
    fun emptyListTailTest(): Unit = ignore(v = emptyList.tail)

    /**
     * Test head and tail function for non-empty lists.
     */
    @Test
    fun nonEmptyListHeadTailTest() {
        assertEquals("hi", singletonList.head)
        assertEquals(FpList.Nil, singletonList.tail)
        assertEquals("hello", complexList.head)
        assertEquals(FpList.singleton("world"), complexList.tail)
    }

    /**
     * Test size function.
     */
    @Test
    fun sizeTest() {
        assertEquals(0, emptyList.size)
        assertEquals(1, singletonList.size)
        assertEquals(2, complexList.size)
    }

    /**
     * Test reverse function.
     */
    @Test
    fun reverseTest() {
        assertEquals(emptyList, emptyList.reverse)
        assertEquals(emptyList, singletonList.reverse)
        assertEquals(FpList.create("world", "hello"), complexList.reverse)
    }

    /**
     * Test contains function.
     */
    @Test
    fun containsTest() {
        // empty list
        assertEquals(false, "hi" in emptyList)
        assertEquals(false, "hello" in emptyList)
        assertEquals(false, "world" in emptyList)
        // singleton list
        assertEquals(true, "hi" in singletonList)
        assertEquals(false, "hello" in singletonList)
        assertEquals(false, "world" in singletonList)
        // complex list
        assertEquals(false, "hi" in complexList)
        assertEquals(true, "hello" in complexList)
        assertEquals(true, "world" in complexList)
    }

    /**
     * Test append function.
     */
    @Test
    fun appendTest() {
        assertEquals(singletonList, emptyList.append(singletonList))
        assertEquals(singletonList, singletonList.append(emptyList))
        assertEquals(complexList, emptyList.append(complexList))
        assertEquals(complexList, complexList.append(emptyList))
        assertEquals(FpList.create("hi", "hello", "world"), singletonList.append(complexList))
    }

    /**
     * Test higher order functions in for all lists.
     */
    @Test
    fun higherOrderFunctionsTest() {
        // empty list
        assertEquals(false, emptyList.exists { true })
        assertEquals(true, emptyList.forAll { false })
        assertEquals(Unit, emptyList.forEach { })
        assertEquals("", emptyList.reduceFromLeft(acc = "", f = String::plus))
        assertEquals("", emptyList.reduceFromRight(init = "", f = String::plus))
        assertEquals(emptyList, emptyList.map { "Bad!" })
        // singleton list
        assertEquals(false, singletonList.exists { false })
        assertEquals(true, singletonList.exists { it == "hi" })
        assertEquals(false, singletonList.forAll { false })
        assertEquals(true, singletonList.forAll { it == "hi" })
        assertEquals(Unit, singletonList.forEach { })
        assertEquals("hi", singletonList.reduceFromLeft(acc = "", f = String::plus))
        assertEquals("hi", singletonList.reduceFromRight(init = "", f = String::plus))
        assertEquals(FpList.singleton("bad"), singletonList.map { "bad" })
        // complex list
        assertEquals(false, complexList.exists { false })
        assertEquals(false, complexList.exists { it == "hi" })
        assertEquals(true, complexList.exists { it == "hello" })
        assertEquals(true, complexList.exists { it == "world" })
        assertEquals(false, complexList.forAll { false })
        assertEquals(false, complexList.forAll { it == "hi" })
        assertEquals(true, complexList.forAll(String::isNotEmpty))
        assertEquals(Unit, complexList.forEach { })
        assertEquals("helloworld", complexList.reduceFromLeft(acc = "", f = String::plus))
        assertEquals("helloworld", complexList.reduceFromRight(init = "", f = String::plus))
        assertEquals(FpList.create("HELLO", "WORLD"), complexList.map(f = String::toUpperCase))
    }

}
