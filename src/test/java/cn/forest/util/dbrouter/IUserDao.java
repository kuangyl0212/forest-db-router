package cn.forest.util.dbrouter;

import cn.forest.util.dbrouter.annotation.DBRouter;

/**
 * @author Forest
 * @date 2023/3/14 17:24
 */
public class IUserDao {
    @DBRouter(key = "uid")
    Object findByUserId(Integer uid) {
        return null;
    }
}
