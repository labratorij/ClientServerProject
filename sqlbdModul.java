package com.company;

import java.sql.*;
import java.util.ArrayList;


public class sqlbdModul {
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private static final String url = "jdbc:mysql://localhost:3306/chatproject?useSSL=false";
    private static final String user = "admin";
    private static final String password = "admin";
    //конструктор класса
    public sqlbdModul(){
        try {
            connection = DriverManager.getConnection(url,user,password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //деструктор класса закрывает все переменные и соединения
    protected void finalize(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try{statement.close();}catch (SQLException e){}
        try{resultSet.close();}catch (SQLException e){}

    }
    //авторизация
    public String authorization(String login, String pass) throws Exception{
        String sqlAuthorization1 = "select* from chatproject.person where name = " +"\'"+ login + "\'";
        resultSet = null;
        try {


            resultSet = statement.executeQuery(sqlAuthorization1);
            if(resultSet.next())
                if(resultSet.getString("name").equals(login)){
                    if(resultSet.getString("password").equals(pass)){
                        return login;
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    //регистрация нового пользователя
    public void addPerson(String login,String password){
        resultSet = null;
        try {
            resultSet = statement.executeQuery("select* from chatproject.person");
            while (resultSet.next()){
                if(login.equals(resultSet.getString("name"))){
                    System.out.println("login err");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String insert = " insert into person (idPerson,name,password)"
                + " values (2,"+"\'"+login+"\',\'"+password+"\')";
        try {
            statement.executeUpdate(insert);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //добавления в список нового сообщения
    public void addMassageBD(String login, String text){
        String sql = "insert into messege (namePerson,text)values(\'"+login+"\',\'"+text+"\')";
        try{
            statement.executeUpdate(sql);
            if(getMess()) {
                delete();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    //Получение всех прошлых сообщений
    public ArrayList<String> getAllMessege(){
        ArrayList<String> allMessage = new ArrayList<>();
        resultSet = null;
        try{
            resultSet = statement.executeQuery("select* from messege");
            while(resultSet.next()){
                System.out.println(resultSet.getString("idMessege"));
                allMessage.add(resultSet.getString("namePerson")+"-> " + resultSet.getString("text"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allMessage;

    }

    //удаление самого старого сообщения
    private void delete(){
        String sql = "delete from messege limit 1";
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //получение количества записей
    private boolean getMess(){
        resultSet = null;
        int size ;
        try {
            resultSet = statement.executeQuery("select* from messege");
            resultSet.last();
            size = resultSet.getRow();
            if(size<=3)
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
