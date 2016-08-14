package com.objectivetruth.uoitlibrarybooking.data.models.calendarmodel;

public class ScrollAtTopOfGridEvent {
    private boolean isScrollAtTop = true;

    public ScrollAtTopOfGridEvent(boolean isScrollAtTop) {
        this.isScrollAtTop = isScrollAtTop;
    }

    public boolean isScrollAtTop() {
        return isScrollAtTop;
    }
}
