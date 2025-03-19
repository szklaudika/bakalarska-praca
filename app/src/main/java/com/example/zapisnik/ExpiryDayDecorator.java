package com.example.zapisnik;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ExpiryDayDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    private final Drawable highlightDrawable;

    public ExpiryDayDecorator(Context context, List<Date> expiryDates, int drawableResId) {
        dates = new HashSet<>();
        for (Date date : expiryDates) {
            dates.add(CalendarDay.from(date));
        }
        highlightDrawable = ContextCompat.getDrawable(context, drawableResId);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(highlightDrawable);
    }
}

