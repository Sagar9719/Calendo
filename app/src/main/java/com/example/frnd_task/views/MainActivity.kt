package com.example.frnd_task.views

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frnd_task.R
import com.example.frnd_task.data.ApiResponseState
import com.example.frnd_task.data.Task
import com.example.frnd_task.data.TaskDetail
import com.example.frnd_task.databinding.ActivityMainBinding
import com.example.frnd_task.viewmodel.CalendarViewModel
import com.example.frnd_task.views.adapter.CalendarViewAdapter
import com.example.frnd_task.views.adapter.TaskViewAdapter
import com.example.frnd_task.views.dialogs.AddTaskDialog
import com.example.frnd_task.views.dialogs.DeleteTaskDialog
import com.example.frnd_task.views.interfaces.CalendarApis
import com.example.frnd_task.views.interfaces.Spin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Spin, CalendarApis {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private val calendarViewModel: CalendarViewModel by viewModels()
    private var taskList: List<TaskDetail>? = null
    private var selectedYear: Int? = 1
    private var selectedMonth: Int? = 2
    private var selectedDay: Int? = 1
    private var userId: Int = 123456

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        callTaskApi()
        initObservers()
        initSpinListeners()
        setCalendarAdapter(selectedYear, selectedMonth)
        initClickListeners()
    }

    private fun initSpinListeners() {
        setSpinYearAndListener()
        setSpinMonthAndListener()
    }

    private fun initObservers() {
        initGetTaskObserver()
        initStoreTaskObserver()
        initDeleteTaskObserver()
    }

    override fun setSpinMonthAndListener() {
        binding?.apply {
            val spinMonth: Spinner = spinnerMonth
            val monthNames = listOf(
                JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
                JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
            )

            val adapter =
                ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, monthNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinMonth.adapter = adapter

            val predefinedMonth = JANUARY
            val predefinedPosition = monthNames.indexOf(predefinedMonth)
            if (predefinedPosition >= 0) {
                spinMonth.setSelection(predefinedPosition)
            }
            selectedMonth = predefinedPosition + 1

            spinMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedMonth = position + 1
                    updateCalendarForSelectedMonth()
                    filterTasksByDate(taskList, selectedYear, selectedMonth, selectedDay)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    override fun setSpinYearAndListener() {
        binding?.apply {
            val spinnerYear: Spinner = spinnerYear
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            val yearList = mutableListOf<String>()
            for (year in 2020..currentYear) {
                yearList.add(year.toString())
            }

            for (year in currentYear + 1..2025) {
                yearList.add(year.toString())
            }

            val adapter =
                ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, yearList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerYear.adapter = adapter

            val predefinedYear = TWENTY_FOUR
            val predefinedPosition = yearList.indexOf(predefinedYear)
            if (predefinedPosition >= 0) {
                spinnerYear.setSelection(predefinedPosition)
            }
            selectedYear = predefinedYear.toInt()
            spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedYear = parent.getItemAtPosition(position).toString().toInt()
                    updateCalendarForSelectedMonth()
                    filterTasksByDate(taskList, selectedYear, selectedMonth, selectedDay)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        }
    }

    private fun updateCalendarForSelectedMonth() {
        setCalendarAdapter(selectedYear, selectedMonth)
    }

    fun filterTasksByDate(
        taskList: List<TaskDetail>?,
        selectedYear: Int?,
        selectedMonth: Int?,
        selectedDay: Int?
    ) {
        val dateFormat = SimpleDateFormat("d / M / yyyy", Locale.getDefault())

        val selectedCalendar = Calendar.getInstance().apply {
            set(
                selectedYear ?: 2024,
                (selectedMonth ?: 1) - 1,
                selectedDay ?: 1
            )
        }

        val filteredList = taskList?.filter { task ->
            task.taskModel?.createdDate?.let { createdDateString ->
                try {
                    val taskDate = dateFormat.parse(createdDateString)
                    val taskCalendar = Calendar.getInstance().apply {
                        if (taskDate != null) {
                            time = taskDate
                        }
                    }

                    taskCalendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                            taskCalendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                            taskCalendar.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH)
                } catch (e: ParseException) {
                    null
                }
            } ?: false
        } ?: emptyList()

        setTaskAdapter(filteredList)
    }

    private fun initGetTaskObserver() {
        lifecycleScope.launch {
            calendarViewModel.getTaskSharedFlow.collectLatest {
                when (it.status) {
                    ApiResponseState.Status.SUCCESS -> {
                        it.data?.taskDetails?.let { dataList ->
                            taskList = dataList.toMutableList()
                            filterTasksByDate(dataList, selectedYear, selectedMonth, selectedDay)
                        }
                    }

                    ApiResponseState.Status.ERROR -> {
                        showToast(description = getString(R.string.something_went_wrong))
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun initDeleteTaskObserver() {
        lifecycleScope.launch {
            calendarViewModel.deleteSharedFlow.collectLatest {
                when (it.status) {
                    ApiResponseState.Status.SUCCESS -> {
                        showToast(description = getString(R.string.task_deleted_successfully))
                    }

                    ApiResponseState.Status.ERROR -> {
                        showToast(description = getString(R.string.something_went_wrong))
                    }

                    ApiResponseState.Status.LOADING -> {}
                }
            }
        }
    }

    private fun initStoreTaskObserver() {
        lifecycleScope.launch {
            calendarViewModel.storeTaskSharedFlow.collectLatest {
                when (it.status) {
                    ApiResponseState.Status.SUCCESS -> {
                        showToast(description = getString(R.string.task_added_successfully))
                    }

                    ApiResponseState.Status.ERROR -> {
                        showToast(description = getString(R.string.something_went_wrong))
                    }

                    ApiResponseState.Status.LOADING -> {}
                }
            }
        }
    }

    private fun initClickListeners() {
        binding?.ivAddTask?.setOnClickListener {
            val addTaskDialog = AddTaskDialog { title, description ->
                val createdAt = "$selectedDay / $selectedMonth / $selectedYear"
                addTaskToLocalList(title, description, createdAt)
                callStoreTaskApi(
                    userId = userId,
                    title = title,
                    description = description,
                    createdAt = createdAt
                )

                callTaskApi()
            }
            addTaskDialog.show(supportFragmentManager, ADD_TASK_DIALOG)
        }
    }

    private fun addTaskToLocalList(title: String, description: String, createdAt: String) {
        val newTask = TaskDetail(
            taskId = generateTemporaryTaskId(),
            taskModel = Task(
                title = title,
                description = description,
                createdDate = createdAt
            )
        )

        taskList = taskList?.toMutableList()?.apply { add(newTask) } ?: listOf(newTask)
        setTaskAdapter(taskList)
    }

    private fun generateTemporaryTaskId(): Int {
        return -(taskList?.size ?: 0) - 1
    }

    override fun callStoreTaskApi(
        userId: Int,
        title: String,
        description: String,
        createdAt: String
    ) {
        calendarViewModel.storeTask(
            userId = userId,
            title = title,
            description = description,
            createdAt = createdAt
        )
    }

    override fun deleteTaskApi(userId: Int, taskId: Int) {
        calendarViewModel.deleteTask(userId = userId, taskId = taskId)
        setTaskAdapter(taskList)
    }

    override fun callTaskApi() {
        calendarViewModel.getTask(userId = userId)
    }

    private fun setCalendarAdapter(year: Int?, month: Int?) {
        val recyclerView: RecyclerView? = binding?.rvCalendar
        recyclerView?.layoutManager = GridLayoutManager(this, 7)

        val daysOfWeek = listOf(SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY)
        val dates = generateDates(year ?: 1, month ?: 2)

        val calendarData = daysOfWeek + dates
        recyclerView?.adapter = CalendarViewAdapter(calendarData) { selectedDate, position ->
            lifecycleScope.launch(Dispatchers.Main) {
                selectedDay = selectedDate.toInt()
                notifyCalendarAdapter(position)
                withContext(Dispatchers.IO) {
                    filterTasksByDate(taskList, selectedYear, selectedMonth, selectedDate.toInt())
                }
            }
        }
    }

    private fun notifyCalendarAdapter(position: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding?.rvCalendar?.adapter?.notifyItemChanged(position)
        }
    }

    private fun generateDates(year: Int, month: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val padding = MutableList(firstDayOfWeek) { "" }
        val dates = (1..daysInMonth).map { it.toString() }

        return padding + dates
    }

    private fun setTaskAdapter(taskList: List<TaskDetail>?) {
        val recyclerView: RecyclerView? = binding?.rvTaskList
        val noTasksTextView: View? = binding?.tvNoTasks

        if (taskList.isNullOrEmpty()) {
            lifecycleScope.launch(Dispatchers.Main) {
                showToast(description = getString(R.string.no_task))
                noTasksTextView?.visibility = View.VISIBLE
                recyclerView?.visibility = View.GONE
            }
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                noTasksTextView?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                recyclerView?.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView?.adapter = TaskViewAdapter(taskList) { taskDetail ->
                    openDeleteTaskDialog(taskDetail)
                }
            }
        }
    }

    private fun openDeleteTaskDialog(taskDetail: TaskDetail) {
        val deleteTaskDialog = DeleteTaskDialog { isDelete ->
            if (isDelete) {
                taskDetail.taskId?.let { taskId ->
                    deleteTaskApi(
                        userId = userId,
                        taskId = taskId
                    )
                }

                deleteTaskFromLocalList(taskDetail)
                callTaskApi()
            }
        }
        deleteTaskDialog.show(supportFragmentManager, DELETE_TASK_DIALOG)
    }

    private fun deleteTaskFromLocalList(taskDetail: TaskDetail) {
        taskList = taskList?.toMutableList()?.apply { remove(taskDetail) }
        setTaskAdapter(taskList)
    }

    private fun showToast(description: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(this@MainActivity, description, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ADD_TASK_DIALOG = "AddTaskDialog"
        private const val DELETE_TASK_DIALOG = "DeleteTaskDialog"
        private const val JANUARY = "January"
        private const val FEBRUARY = "February"
        private const val MARCH = "March"
        private const val APRIL = "April"
        private const val MAY = "May"
        private const val JUNE = "June"
        private const val JULY = "July"
        private const val AUGUST = "August"
        private const val SEPTEMBER = "September"
        private const val OCTOBER = "October"
        private const val NOVEMBER = "November"
        private const val DECEMBER = "December"
        private const val TWENTY_FOUR = "2024"
        private const val SUNDAY = "Sun"
        private const val MONDAY = "Mon"
        private const val TUESDAY = "Tue"
        private const val WEDNESDAY = "Wed"
        private const val THURSDAY = "Thu"
        private const val FRIDAY = "Fri"
        private const val SATURDAY = "Sat"
    }
}