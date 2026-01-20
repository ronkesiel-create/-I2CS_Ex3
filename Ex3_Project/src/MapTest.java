

import org.junit.jupiter.api.Test;

import static jdk.internal.org.jline.utils.Colors.h;
import static org.junit.jupiter.api.Assertions.*;

class MapTest {
    @Test
    void TestInitConstruction() {

        Map Testpoint = new Map(3, 6, 2);


    }


    @Test
    void TestInitConstructionException() {
        //Arrange
        Map Testpoint = new Map(2, 5, 1);
        //Act + Tests
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {

        });
        assertEquals("In the method init: the variable can't get null", exception.getMessage());

    }

    @Test
    void TestEquals1() {
        Map map = new Map(1);
        Object object = new Object();
        assertNotEquals(map, object);
        ;
    }

    @Test
    void TestEquals2() {
        Map map = new Map(1, 2, 1);
        Object object = new Map(1, 3, 1);
        assertNotEquals(map, object);
        ;
    }

    @Test
    void TestEquals3() {
        Map map = new Map(1, 2, 1);
        Object object = new Map(1, 2, 2);
        assertNotEquals(map, object);
        ;
    }

    @Test
    void TestEqualsTrue() {
        Map map = new Map(1, 2, 1);
        Object object = new Map(1, 2, 1);
        assertEquals(map, object);
        ;
    }
    @Test
    void TestAllDistance1() {
        int[][] fill = {{3,4,3,2,1,2,3,6,5},{3,1,1,2,1,3,2,2,2},{3,2,2,5,4,1,1,1,1},
                {4,1,5,5,5,4,2,5,6},{4,1,4,2,4,2,4,6,6}};
        Map map = new Map(fill);
        Map2D distance = map.allDistance(new Index2D(3,4),1);
        map.printMap();
        System.out.println();
        ((Map)distance).printMap();
    }
    @Test
    void TestAllDistance2() {
        int[][] fill = {{3,4,3,2,1,2,3,6,5},{3,1,1,2,1,3,2,2,2},{3,2,2,5,4,1,1,1,1},
                {4,1,5,5,5,4,2,5,6},{4,1,4,2,4,2,4,6,6}};
        Map map = new Map(fill);
        Map2D distance = map.allDistance(new Index2D(0,0),2);
        map.printMap();
        System.out.println();
        ((Map)distance).printMap();
    }
    @Test
    void TestAllDistanceWithCyclic() {
        int[][] fill = {{3,4,3,2,1,2,3,6,5},{3,1,1,2,1,3,2,2,2},{3,2,2,5,4,1,1,1,1},
                {4,1,5,5,5,4,2,5,6},{4,1,4,2,4,2,4,6,6}};
        Map map = new Map(fill);
        Map2D distance = map.allDistance(new Index2D(0,0),2);
        map.printMap();
        System.out.println();
        ((Map)distance).printMap();
    }
}