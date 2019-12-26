package com.example.administrator.bombtest.Model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

public class Student extends BmobUser {
    private BmobRelation join_group;

    public BmobRelation getJoin_group() {
        return join_group;
    }

    public void setJoin_group(BmobRelation join_group) {
        this.join_group = join_group;
    }


}
