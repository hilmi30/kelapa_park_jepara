package com.perusdajepara.kelapaparkjepara

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.perusdajepara.kelapaparkjepara.profile.EditProfileActivity
import io.paperdb.Paper
import java.util.*

class DatePickerCustom: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var year = Paper.book().read<Int>(EditProfileActivity().YEAR)
        var month = Paper.book().read<Int>(EditProfileActivity().MONTH)
        var day = Paper.book().read<Int>(EditProfileActivity().DAY)

        if(year == null && month == null && day == null){
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }

        return DatePickerDialog(activity, activity as DatePickerDialog.OnDateSetListener, year, month, day)
    }
}