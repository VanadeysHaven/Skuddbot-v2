package me.VanadeysHaven.Skuddbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * [describe]
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.0
 * @since 2.0
 */
public class TestThings {

    private enum TestEnum {
        ONE, TWO, THREE
    }

//    @Test
    public void testThings(){
        var testEnum = TestEnum.valueOf("FOUR");
        System.out.println(testEnum);
    }

}
