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

    public ExpiryDayDecorator(Context context, List<Date> expiryDates) {
        // Convert your expiryDates into CalendarDay objects
        dates = new HashSet<>();
        for (Date date : expiryDates) {
            CalendarDay day = CalendarDay.from(date);
            dates.add(day);
        }
        // Prepare a highlight drawable
        highlightDrawable = ContextCompat.getDrawable(context, R.drawable.custom_highlight);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Only decorate if the day is in our set of "special" days
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Apply the custom highlight
        view.setSelectionDrawable(highlightDrawable);
        // e.g., view.addSpan(new ForegroundColorSpan(Color.RED)); // additional styling
    }
}
