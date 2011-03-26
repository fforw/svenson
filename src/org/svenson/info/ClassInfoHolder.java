package org.svenson.info;


public class ClassInfoHolder
{
    private Class<?> cls;

    private volatile JSONClassInfo classInfo;

    private ObjectSupport objectSupport;

    public ClassInfoHolder(ObjectSupport objectSupport, Class<?> cls)
    {
        this.objectSupport = objectSupport;
        
        this.cls = cls;
    }
    
    protected Class<?> getType()
    {
        return cls;
    }

    public JSONClassInfo getClassInfo()
    {
        if (classInfo == null)
        {
            synchronized(this)
            {
                if (classInfo == null)
                {
                    classInfo = objectSupport.createClassInfo(cls);
                }
            }
        }
        return classInfo;
    }

}
