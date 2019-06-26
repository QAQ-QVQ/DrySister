package com.yu.drysister.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.yu.drysister.Bean.Sister;

public class DiffCallBack extends DiffUtil.Callback {
    private Sister oldSister;
    private Sister newSister;
    public DiffCallBack(Sister oldSister, Sister newSister) {
        this.oldSister = oldSister;
        this.newSister = newSister;
    }

    @Override
    public int getOldListSize() {
        return oldSister.getResults().size();
    }

    @Override
    public int getNewListSize() {
        return newSister.getResults().size();
    }
//判断两个是否存在
    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldSister.getResults().get(i).get_id().equals(newSister.getResults().get(i1).get_id());
    }
//如果已存在判断是否相同
    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return oldSister.getResults().get(i).getUrl().equals(newSister.getResults().get(i1).getUrl());
    }
}
