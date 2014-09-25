package ut.com.biercoff.customJiraEmailPlugin;

import org.junit.Test;
import com.biercoff.customJiraEmailPlugin.MyPluginComponent;
import com.biercoff.customJiraEmailPlugin.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}