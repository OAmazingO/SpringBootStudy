package com.yearn.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by zhoub on 2019/12/11.
 */
@SpringBootApplication
public class Application {

    /**
     * 一个项目中不应出现多个main函数，否在在打包的时候 spring-boot-maven-plugin 将找不到主函数（主动指定打包主函数入口除外…）
     * @param args
     */
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

}
