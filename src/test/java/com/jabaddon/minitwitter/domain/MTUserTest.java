package com.jabaddon.minitwitter.domain;

import flexjson.JSONDeserializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.staticmock.MockStaticEntityMethods;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
@MockStaticEntityMethods
public class MTUserTest {

    @Test
    public void testJsonToTweet() {
        String json = "{text: 'Hello'}";
        Tweet t = new Tweet();
        new JSONDeserializer().deserializeInto(json, t);
        assertThat(t.getText(), is("Hello"));
    }
}
