package demo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试、实例代码
 */
public class User implements Serializable{

    private Long userId;
    private String name;
    private String sex;
    private Integer age;
    private String address;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User(Long userId, String name, String sex, Integer age, String address) {
        this.userId = userId;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.address = address;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }

    public static List<User> getUsers(){
        User user = null;
        List<User> users = new ArrayList<User>();
        for(int i=0;i<10;i++){
            user = new User();
            user.setUserId(100L + i);
            user.setAddress("广东-广州");
            user.setAge(10+i);
            user.setName("大军"+i);
            user.setSex("男");
            users.add(user);
        }
        return users;
    }
}
