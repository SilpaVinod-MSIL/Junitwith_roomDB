package com.example.junitwith_roomdb.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.junitwith_roomdb.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@ExperimentalCoroutinesApi
//To tell the JUnit to instrument the test(to run on the emulator)
@RunWith(AndroidJUnit4::class)
/*
Unit test -@SmallTest
integrated test-@MediumTest
UI test-@LargeTest
*/
@SmallTest
class ShoppingDaoTest {
    @get:Rule
    var instantTaskExecuteRule=InstantTaskExecutorRule()
    private lateinit var dataBase:ShoppingItemDatabase
    private lateinit var dao:ShoppingDao


    @Before
    fun setUp(){
        //inMemoryDatabaseBuilder()-It is a DB. But hold data in RAM.Used for storing data during automation testing
        //allowMainThreadQueries: To run the testing on the main thread
       dataBase= Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),ShoppingItemDatabase::class.java).allowMainThreadQueries().build()
        dao=dataBase.shoppingDao()
    }
    @Test
    fun insertShoppingItem()= runBlockingTest {
        val shoppingItem=ShoppingItem("name",3,7f,"url",1)
        dao.insertShoppingItem(shoppingItem)
        val observeShoppingItem=dao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(observeShoppingItem).contains(shoppingItem)

    }

    @Test
    fun deleteShoppingItem()= runBlockingTest {
        val shoppingItem=ShoppingItem("name",4,9f,"url",1)
        dao.insertShoppingItem(shoppingItem)
        dao.deleteShoppingItem(shoppingItem)
        val observeShoppingItem=dao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(observeShoppingItem).doesNotContain(shoppingItem)
    }

    @Test
    fun observeTotalPrice()= runBlocking{
        val shoppingItem1=ShoppingItem("name",4,9f,"url",1)
        val shoppingItem2=ShoppingItem("name",1,5f,"url",2)
        val shoppingItem3=ShoppingItem("name",0,9f,"url",3)
        dao.insertShoppingItem(shoppingItem1)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)
        val totalPrice=dao.observeTotalPrice().getOrAwaitValue()
        assertThat(totalPrice).isEqualTo((4*9f)+(1*5f))
    }

    @After
    fun setDown(){
        dataBase.close()
    }
}