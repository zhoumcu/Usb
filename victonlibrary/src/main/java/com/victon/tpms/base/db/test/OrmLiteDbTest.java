package com.victon.tpms.base.db.test;

import android.test.AndroidTestCase;

import com.victon.tpms.base.db.dao.DeviceDao;
import com.victon.tpms.base.db.dao.UserDao;
import com.victon.tpms.base.db.entity.Device;
import com.victon.tpms.base.db.entity.User;

import java.util.List;

public class OrmLiteDbTest extends AndroidTestCase {
    public void testAddArticle() {
        User u = new User();
        u.setName("xxx");
        new UserDao(getContext()).add(u);
        Device article = new Device();
        article.setLeft_FD("02:12:45:33:45");
        article.setUser(u);
        new DeviceDao(getContext()).add(article);

    }

    public void testGetArticleById() {
        Device article = new DeviceDao(getContext()).get(1);
    }

    public void testGetArticleWithUser() {

        Device article = new DeviceDao(getContext()).getArticleWithUser(1);
//        L.e(article.getUser() + " , " + article.getTitle());
    }

    public void testListArticlesByUserId() {

        List<Device> articles = new DeviceDao(getContext()).listByUserId(1);
//        L.e(articles.toString());
    }

    public void testGetUserById() {
        User user = new UserDao(getContext()).get(1);
//        L.e(user.getName());
        if (user.getDevices() != null)
            for (Device article : user.getDevices()) {
//                L.e(article.toString());
            }
    }
}
