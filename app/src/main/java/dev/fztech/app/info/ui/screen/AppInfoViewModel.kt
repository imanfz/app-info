package dev.fztech.app.info.ui.screen

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.fztech.app.info.utils.Category
import kotlinx.coroutines.launch

class AppInfoViewModel: ViewModel() {

    private val _category: MutableState<Category>  = mutableStateOf(Category.ALL)
    val category: State<Category>
        get() = _category

    private val _query = MutableLiveData<String>()
    val query: LiveData<String>
        get() = _query

    private val _items = MutableLiveData<List<PackageInfo>>()
    private val _filteredItem = MutableLiveData<List<PackageInfo>>()
    val items: LiveData<List<PackageInfo>>
        get() = _filteredItem

    private lateinit var packageManager: PackageManager

    fun getSize(category: Category): Int {
        return _items.value.orEmpty().filter {
            when (category) {
                Category.SYSTEM -> it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0
                Category.USER -> it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) <= 0
                Category.ALL -> true
            }
        }.size
    }
    fun setQuery(value: String) {
        viewModelScope.launch {
            _query.value = value
            applyFilter()
        }
    }

    fun changeCategory(category: Category) {
        viewModelScope.launch {
            _category.value = category
            applyFilter()
        }
    }

    private fun applyFilter() {
        viewModelScope.launch {
            val results = _items.value.orEmpty().filter {
                when (category.value) {
                    Category.SYSTEM -> it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0
                    Category.USER -> it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) <= 0
                    else -> true
                }
            }.filter {
                try {
                    it.applicationInfo.packageName.contains(
                        query.value.orEmpty(),
                        true
                    ) or it.applicationInfo.loadLabel(packageManager).contains(
                        query.value.orEmpty(),
                        true
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            _filteredItem.value = results
        }
    }

    fun loadPackage(context: Context) {
        viewModelScope.launch {
            packageManager = context.packageManager
            try {
                val results = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                _items.value = results
                _filteredItem.value = _items.value
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to load package: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}