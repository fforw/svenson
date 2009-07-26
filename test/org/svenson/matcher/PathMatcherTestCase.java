package org.svenson.matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;


public class PathMatcherTestCase
{
    @Test
    public void testAndMatcher()
    {
        assertThat( new AndMatcher(ALWAYS).matches(""), is(true));
        assertThat( new AndMatcher(ALWAYS, ALWAYS).matches(""), is(true));
        assertThat( new AndMatcher(ALWAYS, ALWAYS, ALWAYS).matches(""), is(true));
        assertThat( new AndMatcher(NEVER).matches(""), is(false));
        assertThat( new AndMatcher(ALWAYS,NEVER).matches(""), is(false));
    }
    
    @Test
    public void testOrMatcher()
    {
        assertThat( new OrMatcher(ALWAYS).matches(""), is(true));
        assertThat( new OrMatcher(ALWAYS, NEVER).matches(""), is(true));
        assertThat( new OrMatcher(NEVER, ALWAYS, NEVER).matches(""), is(true));
        
        assertThat( new OrMatcher(NEVER).matches(""), is(false));
    }
    
    @Test
    public void testNotMatcher()
    {
        assertThat( new NotMatcher(NEVER).matches(""), is(true));
        assertThat( new NotMatcher(ALWAYS).matches(""), is(false));
    }

    @Test
    public void testEquals()
    {
        assertThat( new EqualsPathMatcher(".aaa").matches(".aaa"), is(true));
        assertThat( new EqualsPathMatcher(".bbb").matches(".aaa"), is(false));
        assertThat( new EqualsPathMatcher(".bbb[]").matches(".bbb[]"), is(true));
    }

    @Test
    public void testPrefix()
    {
        assertThat( new PrefixPathMatcher(".aaa[]").matches(".aaa"), is(true));
        assertThat( new PrefixPathMatcher(".bbb[]").matches(".aaa"), is(false));
    }
    
    @Test
    public void testSuffix()
    {
        assertThat( new SuffixPathMatcher(".aaa[].test").matches(".test"), is(true));
        assertThat( new SuffixPathMatcher(".bbb[]").matches(".test"), is(false));
    }

    @Test
    public void testRegEx()
    {
        RegExPathMatcher regExPathMatcher = new RegExPathMatcher("\\.aaa.*");
        System.out.println(regExPathMatcher);
        assertThat( regExPathMatcher.matches(".aaa[]"), is(true));
        assertThat( regExPathMatcher.matches(".aaa[].test"), is(true));
    }

    final static PathMatcher ALWAYS = new PathMatcher()
    {
        public boolean matches(String parsePath)
        {
            return true;
        }
    };
    final static PathMatcher NEVER = new PathMatcher()
    {
        public boolean matches(String parsePath)
        {
            return false;
        }
    };
}
