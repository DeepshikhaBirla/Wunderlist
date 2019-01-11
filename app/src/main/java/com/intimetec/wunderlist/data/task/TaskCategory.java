package com.intimetec.wunderlist.data.task;


public enum TaskCategory {
    Default(0), Personal(1), Shopping(2), WishList(3), Work(4), AllList(5), IsFinished(6);

    private int position;

    TaskCategory(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
