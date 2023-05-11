package com.ityanlan.reggie.dto;

import com.ityanlan.reggie.entity.Setmeal;
import com.ityanlan.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
