package com.quizmaster;

import com.quizmaster.entities.User;
import com.quizmaster.repositories.UserMongoRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class UserUnitTest {

    @Autowired
    private UserMongoRepository userMongoRepository;

    @Test
    public void testFetchData(){
        System.out.println(userMongoRepository.findByName("Anna"));
    }

//    @Before
//    public void setUp() throws Exception {
//        User user1= new User("Alice");
//        User user2= new User("Bob");
//
//        assertNull(user1.getId());
//        assertNull(user2.getId());//null before save
//        this.userMongoRepository.save(user1);
//        this.userMongoRepository.save(user2);
//        assertNotNull(user1.getId());
//        assertNotNull(user2.getId());
//    }
//
//    @Test
//    public void testFetchData(){
//        /*Test data retrieval*/
//        User userA = userMongoRepository.findByName("Bob");
//        assertNotNull(userA);
//        assertEquals("Alice", userA.getName());
//        List<User> users = userMongoRepository.findAll();
//        int count = 0;
//        for(User p : users){
//            count++;
//        }
//        assertEquals(count, 2);
//    }

}
