package com.forreview.ui.menu.reminders

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forreview.R
import com.forreview.base.BaseFragment
import com.forreview.datamanager.MyAlarmManager
import com.forreview.helper.PrefsHelper
import com.forreview.model.Reminder
import kotlinx.android.synthetic.main.fragment_reminders.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class RemindersFragment : BaseFragment() {

    private val viewModel by viewModel<RemindersViewModel>()

    private val myAlarmManager by inject<MyAlarmManager>()

    private val prefsHelper by inject<PrefsHelper>()

    private lateinit var reminder: Reminder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reminder = prefsHelper.getMeditationReminder()

        initDaysTextView()

        initTimeTextView()

        initReminderSwitch()

        timeTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dialog = TimePickerDialog(context!!, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH,1)
                    reminder.timeMillis = calendar.timeInMillis
                } else {
                    reminder.timeMillis = calendar.timeInMillis
                }
                prefsHelper.setMeditationReminder(reminder)
                initTimeTextView()
                myAlarmManager.setMeditationReminder(reminder)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(context))

            dialog.show()
        }

        daysTextView.setOnClickListener {

            val checkedItem = if (reminder.days == Reminder.Days.DAILY) {
                0
            } else {
                1
            }

            val builder = AlertDialog.Builder(context)
                .setSingleChoiceItems(R.array.fragment_reminder_days_array, checkedItem) { _, which ->
                    when (which) {
                        0 -> {
                            reminder.days = Reminder.Days.DAILY
                        }
                        1 -> {
                            reminder.days = Reminder.Days.WEEKDAYS
                        }
                    }
                    initDaysTextView()
                    prefsHelper.setMeditationReminder(reminder)
                    myAlarmManager.setMeditationReminder(reminder)
                }

            val dialog = builder.create()

            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            reminder.isSet = isChecked
            prefsHelper.setMeditationReminder(reminder)
            initReminderSwitch()

            myAlarmManager.setMeditationReminder(reminder)
        }
    }

    private fun initTimeTextView() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = reminder.timeMillis
        val date = Date(reminder.timeMillis)
        val dateFormat = if (DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("H:mm", Locale.US)
        } else {
            SimpleDateFormat("h:mm a", Locale.US)
        }
        timeTextView.text = dateFormat.format(date)
    }

    private fun initDaysTextView() {
        when (reminder.days) {
            Reminder.Days.WEEKDAYS -> daysTextView.text = getString(R.string.fragment_reminder_weekdays)
            Reminder.Days.DAILY -> daysTextView.text = getString(R.string.fragment_reminder_daily)
        }
    }

    private fun initReminderSwitch() {
        switchAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            setContainerVisibility(isChecked)
        }
        switchAlarm.isChecked = reminder.isSet
        setContainerVisibility(reminder.isSet)
    }

    private fun setContainerVisibility(isChecked: Boolean) {
        if (isChecked) {
            container.visibility = View.VISIBLE
        } else {
            container.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val TAG = "RemindersFragment"
    }
}
