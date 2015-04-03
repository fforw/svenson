package org.svenson;

import java.util.Locale;

import org.junit.Test;
import org.svenson.converter.TypeConverter;

public class LocaleArrayTestCase
{
    @Test
    public void testConverting()
    {
        Bean bean = new Bean();
        bean.setLocales(Locale.getAvailableLocales());
        
        JSON gen = new JSON();
        gen.registerTypeConversion(Locale.class, new TypeConverter()
        {
            
            public Object toJSON(Object in)
            {
                Locale locale = (Locale) in;
                
                return locale.getLanguage() + "_" +locale.getCountry() + "_";
            }
            
            
            public Object fromJSON(Object in)
            {
                return null;
            }
        });
        
        String json = gen.forValue(bean);
        
        System.out.println(json);
    }
    
    
    public static class Bean
    {
        private Locale[] locales;

        public Locale[] getLocales()
        {
            return locales;
        }

        public void setLocales(Locale[] locales)
        {
            this.locales = locales;
        }
        
    }
}
