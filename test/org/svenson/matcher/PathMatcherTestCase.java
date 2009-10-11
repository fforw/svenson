package org.svenson.matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;


public class PathMatcherTestCase
{
    @Test
    public void testAndMatcher()
    {
        assertThat( new AndMatcher(ALWAYS).matches("", Object.class), is(true));
        assertThat( new AndMatcher(ALWAYS, ALWAYS).matches("", Object.class), is(true));
        assertThat( new AndMatcher(ALWAYS, ALWAYS, ALWAYS).matches("", Object.class), is(true));
        assertThat( new AndMatcher(NEVER).matches("", Object.class), is(false));
        assertThat( new AndMatcher(ALWAYS,NEVER).matches("", Object.class), is(false));
    }
    
    @Test
    public void testOrMatcher()
    {
        assertThat( new OrMatcher(ALWAYS).matches("", Object.class), is(true));
        assertThat( new OrMatcher(ALWAYS, NEVER).matches("", Object.class), is(true));
        assertThat( new OrMatcher(NEVER, ALWAYS, NEVER).matches("", Object.class), is(true));
        
        assertThat( new OrMatcher(NEVER).matches("", Object.class), is(false));
    }
    
    @Test
    public void testNotMatcher()
    {
        assertThat( new NotMatcher(NEVER).matches("", Object.class), is(true));
        assertThat( new NotMatcher(ALWAYS).matches("", Object.class), is(false));
    }

    @Test
    public void testEquals()
    {
        assertThat( new EqualsPathMatcher(".aaa").matches(".aaa", Object.class), is(true));
        assertThat( new EqualsPathMatcher(".bbb").matches(".aaa", Object.class), is(false));
        assertThat( new EqualsPathMatcher(".bbb[]").matches(".bbb[]", Object.class), is(true));
    }

    @Test
    public void testPrefix()
    {
        assertThat( new PrefixPathMatcher(".aaa[]").matches(".aaa", Object.class), is(true));
        assertThat( new PrefixPathMatcher(".bbb[]").matches(".aaa", Object.class), is(false));
    }
    
    @Test
    public void testSuffix()
    {
        assertThat( new SuffixPathMatcher(".aaa[].test").matches(".test", Object.class), is(true));
        assertThat( new SuffixPathMatcher(".bbb[]").matches(".test", Object.class), is(false));
    }

    @Test
    public void testRegEx()
    {
        RegExPathMatcher regExPathMatcher = new RegExPathMatcher("\\.aaa.*");
        System.out.println(regExPathMatcher);
        assertThat( regExPathMatcher.matches(".aaa[]", Object.class), is(true));
        assertThat( regExPathMatcher.matches(".aaa[].test", Object.class), is(true));
    }
    
    @Test
    public void testSubtypeMatcher()
    {
        SubtypeMatcher matcher = new SubtypeMatcher(Number.class);
        
        assertThat(matcher.matches("", null), is(false));
        assertThat(matcher.matches("", Object.class), is(false));
        assertThat(matcher.matches("", Integer.class), is(true));
        assertThat(matcher.matches("", Double.class), is(true));
    }

    final static PathMatcher ALWAYS = new PathMatcher()
    {
        public boolean matches(String parsePath, Class typeHint)
        {
            return true;
        }
    };
    final static PathMatcher NEVER = new PathMatcher()
    {
        public boolean matches(String parsePath, Class typeHint)
        {
            return false;
        }
    };
}
