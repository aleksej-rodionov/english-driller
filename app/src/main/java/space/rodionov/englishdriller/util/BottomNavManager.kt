package space.rodionov.englishdriller.util

import android.view.MenuItem
import java.util.concurrent.CopyOnWriteArrayList

class BottomNavManager {

    lateinit var startItem: MenuItem
    private val navStack = CopyOnWriteArrayList<MenuItem>()

    fun start(item: MenuItem) {
        startItem = item
        navStack.clear()
        navStack.add(item)
    }

    fun changeNav(item: MenuItem) {
        if(item != startItem) {
            if (!navStack.contains(item)) navStack.add(item)
            else {
                navStack.remove(item)
                navStack.add(item)
            }
        } else {
            var isExist = false
            navStack.forEachIndexed { index, i ->
                if(index != 0 && i.itemId == item.itemId && navStack[index.minus(1)].itemId != item.itemId) {
                    navStack.removeAt(index)
                    navStack.add(item)
                    isExist = true
                    return@forEachIndexed
                }
            }
            if(!isExist) navStack.add(item)
        }
    }

    fun backItemId(): MenuItem? {
        return if(navStack.size > 1) {
            navStack.removeLast()
            val item = navStack.last()
            item
        } else null
    }
}