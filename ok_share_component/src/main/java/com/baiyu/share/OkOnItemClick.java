package com.baiyu.share;

import android.view.View;

interface OkOnItemClick<T> {

    void itemClick(View v, T item);
}
