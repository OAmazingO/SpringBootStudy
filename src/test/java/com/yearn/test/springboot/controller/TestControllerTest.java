package com.yearn.test.springboot.controller;

import com.yearn.springboot.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;

/**
 * Created by zhoub on 2019/12/11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestControllerTest {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws MalformedURLException {
        System.out.println("test start");
        this.base = new URL("http://localhost:"+port+"/test");
    }

    @Test
    public void test(){
        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
        Assert.assertEquals("test",response.getBody());
    }

    @Test
    public void test2(){
        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
        Assert.assertEquals("test",response.getBody());
    }
}
