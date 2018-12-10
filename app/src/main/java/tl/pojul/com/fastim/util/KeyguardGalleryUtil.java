package tl.pojul.com.fastim.util;

import android.content.Context;
import android.widget.Spinner;

import com.pojul.fastIM.entity.User;

public class KeyguardGalleryUtil {

    /**
     * 锁屏图库合法性
     * 1：不可用; 2: 会员可用; 3: 可以体验; 其他: 体验过期时间
     * */
    public long validStatus(){
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            return 1;
        }
        String validNumberTime = user.getNumberValidTime();
        if(DateUtil.isDateOverdue(validNumberTime)){
            String experienceValidDate = SPUtil.getInstance().getExperienceValidTime();
            if(user.getCanExperience() == 1){
                return 3;
            }else{
                if(experienceValidDate == null){
                    return 1;
                }
                if(DateUtil.isDateOverdue(experienceValidDate)){
                    return 1;
                }else{
                    return DateUtil.convertTimeToLong(experienceValidDate);
                }
            }
        }else{
            return 2;
        }
    }

}
