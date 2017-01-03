package study.heaven7.com.android_study.app_permission;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用户权限辅助
 * Created by heaven7 on 2016/7/5.
 */
public final class UserPermissionHelper {
     //游客，注册，认证, 飞刀
    public static final int USER_TYPE_VISITOR      = 0x0001;
    public static final int USER_TYPE_REGISTER     = 0x0002;
    public static final int USER_TYPE_LEGALIZE     = 0x0003;
    public static final int USER_TYPE_LEGALIZE_FLY = 0x0004;

    public static final int PERMISSION_READ                   = 0x00000001;
    //点赞权限：病例详情，问题详情
    public static final int PERMISSION_LAUD_CASE_DETAIL       = 0x00000002;
    public static final int PERMISSION_LAUD_PROBLEM_DETAIL    = 0x00000004;

    //订阅科室
    public static final int PERMISSION_SUBSCRIBE_DEPARTMENT   = 0x00000010;
    //创建: 圈子
    public static final int PERMISSION_CREATE_CIRCLE          = 0x00000100;

    //发布,评论，交互,打赏
    public static final int PERMISSION_PUBLISH                = 0x00010000;
    public static final int PERMISSION_COMMENT                = 0x00020000;
    public static final int PERMISSION_INTERACTIVE            = 0x00040000;
    public static final int PERMISSION_REWARD                 = 0x00080000;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({USER_TYPE_VISITOR, USER_TYPE_REGISTER, USER_TYPE_LEGALIZE ,USER_TYPE_LEGALIZE_FLY})
    public @interface UserType{
    }

    private final User mUser;

    public UserPermissionHelper(@UserType int userType, IUserPermissionApplier applier) {
        this.mUser = new User(userType, applier.mapUserPermissions(userType));
    }

    public User getUser(){
        return mUser;
    }

    public void checkUserPermission(int requestPermissions, IUserPermissionCallback callback){
        if(callback ==null ) throw new NullPointerException();
        callback.onRequestPermissionResult(requestPermissions, mUser.hasPermission(requestPermissions));
    }

    /**
     * the user permission applier
     */
    public interface IUserPermissionApplier{
        /**
         * map the permission by target userType
          * @param userType the user type
         * @return actual user permissions
         */
        int mapUserPermissions(@UserType int userType);
    }

    /**
     * the user permission callback
     */
    public interface IUserPermissionCallback{
        /**
         * called when you request user permission.
         * @param requestPermissions the permissions you request
         * @param granted  indicate the permission is granted or not.
         */
        void onRequestPermissionResult(int requestPermissions, boolean granted);
    }

    public static class User {
        private int mPermissions ;
        private int mUserType ;

        User(@UserType int userType, int mPermissions) {
            this.mUserType = userType;
            this.mPermissions = mPermissions;
        }

        public void addUserPermission(int permission){
            mPermissions |= permission;
        }
        public void deleteUserPermission(int permission){
            mPermissions &= ~permission;
        }
        public void setUserPermission(int permission){
            this.mPermissions = permission;
        }

        public boolean hasPermission(int permissions){
            return (mPermissions & permissions) != 0;
        }

        public int getUserPermissions(){
            return mPermissions;
        }
        public @UserType int getUserType(){
            return mUserType;
        }
    }

}
