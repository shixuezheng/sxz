package cn.itcast.dao.impl;

import cn.itcast.dao.UserDao;
import cn.itcast.domain.User;
import cn.itcast.util.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDaoImpl implements UserDao {

    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    @Override
    public List<User> findAll() {
        //使用JDBC操作数据库...
        //1.定义sql
        String sql = "select * from user";
        List<User> users = template.query(sql, new BeanPropertyRowMapper<User>(User.class));

        return users;
    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        try {
            String sql = "select * from user where username = ? and password = ?";
            User user = template.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), username, password);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void add(User user) {
        //1.定义sql
        String sql = "insert into user values(null,?,?,?,?,?,?,null,null)";
        //2.执行sql
        template.update(sql, user.getName(), user.getGender(), user.getAge(), user.getAddress(), user.getQq(), user.getEmail());
    }

    @Override
    public void delete(int id) {
        //1.定义sql
        String sql = "delete from user where id = ?";
        //2.执行sql
        template.update(sql, id);
    }

    @Override
    public User findById(int id) {
        String sql = "select * from user where id = ?";
        return template.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), id);
    }

    @Override
    public void update(User user) {
        String sql = "update user set name = ?,gender = ? ,age = ? , address = ? , qq = ?, email = ? where id = ?";
        template.update(sql, user.getName(), user.getGender(), user.getAge(), user.getAddress(), user.getQq(), user.getEmail(), user.getId());
    }


    //带条件查询返回条数
    @Override
    public int findTotalCount(Map<String, String[]> condition) {
        StringBuilder s  = new StringBuilder();
        String sql = "select count(*) from user where 1=1 ";
        s.append(sql);
        //"%"+value+"%"

        //创建保存参数的集合
        List<Object> param = new ArrayList<Object>();

        for (String key : condition.keySet()) {
            String value = condition.get(key)[0];
            if("currentPage".equals(value)||"rows".equals(value)){
                continue;
            }
            if (value!=null || "".equals(value)){
                s.append(" and "+key+" like ?");

                param.add("%"+value+"%");
            }
        }

       return   template.queryForObject(s.toString(),Integer.class,param.toArray());
    }


    //返回每页的数据的list集合
    @Override
    public List<User> findByPage(int start, int rows, Map<String, String[]> condition) {
        StringBuilder s  = new StringBuilder();
        String sql = "select * from user where 1=1 ";
        s.append(sql);

        //创建保存参数的集合
        List<Object> param = new ArrayList<Object>();

        for (String key : condition.keySet()) {
            String value = condition.get(key)[0];
            if("currentPage".equals(value)||"rows".equals(value)){
                continue;
            }
            if (value!=null || "".equals(value)){
                s.append(" and "+key+" like ? ");

                param.add("%"+value+"%");
            }
        }
        //添加分页查询条件
        s.append(" limit ?,?");
        param.add(start);
        param.add(rows);
        return  template.query(s.toString(),new BeanPropertyRowMapper<User>(User.class),param.toArray());
    }
}
