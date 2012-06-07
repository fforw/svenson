package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.svenson.test.RenamedHintBase;
import org.svenson.test.RenamedHintChild;

public class RenamedHintTestCase
{
    @Test
    public void test()
    {
        final String testValue = "joerugoeiguzoe";
        RenamedHintBase renamedHintBase = new RenamedHintBase();

        RenamedHintChild renamedHintChild = new RenamedHintChild();
        renamedHintChild.setName(testValue);

        List<RenamedHintChild> renamedHintChildList = new ArrayList<RenamedHintChild>();
        renamedHintChildList.add(renamedHintChild);

        renamedHintBase.setRenamedHintChildList(renamedHintChildList);

        String json = JSON.defaultJSON().forValue(renamedHintBase);
        System.out.println(json);

        RenamedHintBase result = JSONParser.defaultJSONParser().parse(RenamedHintBase.class, json);

        assertThat(result, is(notNullValue()));
        assertThat(result.getRenamedHintChildList().size(), is(1));

        RenamedHintChild child = result.getRenamedHintChildList().get(0);
        assertThat(child.getName(), is(testValue));
        
    }
}
