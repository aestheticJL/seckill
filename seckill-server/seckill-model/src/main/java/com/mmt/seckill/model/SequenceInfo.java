package com.mmt.seckill.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

@TableName("sequence_info")
public class SequenceInfo extends Model<SequenceInfo> {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer current_value;
    private Integer step;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(Integer current_value) {
        this.current_value = current_value;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

}
