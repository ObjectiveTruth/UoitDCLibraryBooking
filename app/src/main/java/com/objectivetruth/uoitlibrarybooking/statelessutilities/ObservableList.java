package com.objectivetruth.uoitlibrarybooking.statelessutilities;

import rx.Observable;
import rx.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;

/**
 * Useful utility class for emiting programmatically added Observables
 * Hard to describe but read PublishSubject for more info
 * http://reactivex.io/documentation/subject.html
 * @param <T>
 */
public class ObservableList<T> {

    protected final List<T> list;
    protected final PublishSubject<T> onAdd;

    public ObservableList() {
        this.list = new ArrayList<T>();
        this.onAdd = PublishSubject.create();
    }
    public void add(T value) {
        list.add(value);
        onAdd.onNext(value);
    }
    public Observable<T> getObservable() {
        return onAdd;
    }
}
