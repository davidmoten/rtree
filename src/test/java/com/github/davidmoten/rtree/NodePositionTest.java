package com.github.davidmoten.rtree;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

public class NodePositionTest {

    @Test
    public void test() {
        Node node = Mockito.mock(Node.class);
        assertTrue(new NodePosition(node, 1).toString().startsWith("NodePosition ["));
    }
    
}
