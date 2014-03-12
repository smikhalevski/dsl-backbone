/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package org.ehony.dsl;

import org.ehony.dsl.api.ContainerTag;
import org.junit.*;

import static org.junit.Assert.*;

public class TagChildrenTest
{

    private A a;
    private TagChildren<A, B> children;

    class A extends ContainerBaseTag<A, ContainerTag> {}

    class B extends BaseTag<B, A>
    {

        private int i;

        B(int i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return String.valueOf(i);
        }
    }

    @Before
    public void setUp() {
        a = new A();
        children = new TagChildren<A, B>(a);
        children.add(new B(0));
        children.add(new B(1));
    }

    @Test
    public void testNoop() {
        B b = new B(2);
        children.add(b);
        // [0, 1, 2]
        
        children.set(2, b);
        assertEquals("[0, 1, 2]", children.toString());
    }
    @Test
    public void testReplace() {
        children.add(new B(2));
        // [0, 1, 2]
        
        children.set(1, new B(3));
        assertEquals("[0, 3, 2]", children.toString());
    }

    @Test
    public void testRearrangeBeginning() {
        B b = new B(3);
        children.add(new B(2));
        children.add(b);
        // [0, 1, 2, 3]
        
        children.set(1, b);
        assertEquals("[0, 3, 2]", children.toString());
    }

    @Test
    public void testRearrangeTail() {
        B b = new B(2);
        children.add(b);
        children.add(new B(3));
        // [0, 1, 2, 3]
        
        children.add(4, b);
        assertEquals("[0, 1, 3, 2]", children.toString());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInsertOutOfBounds() {
        children.add(3, new B(2));
    }
    @Test
    public void testInsertCenter() {
        children.add(1, new B(2));
        assertEquals("[0, 2, 1]", children.toString());
    }
    
    @Test
    public void testInsertBeginning() {
        children.add(0, new B(2));
        assertEquals("[2, 0, 1]", children.toString());
    }
    
    @Test
    public void testInsertTail() {
        children.add(2, new B(2));
        assertEquals("[0, 1, 2]", children.toString());
    }
}
