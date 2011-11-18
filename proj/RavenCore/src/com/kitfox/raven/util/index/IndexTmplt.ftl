<#--
Source code generation
 -->
<#setting number_format="computer">

/**
 * Automatically generated ResourceComponent service
 * <#if date??>${date?date}</#if>
 * <#if author??>@author ${author}</#if>
 */

package ${package};

import com.kitfox.puffin.util.index.Index;

public class ${className}Index extends Index<${classQname}>
{
    private static ${className}Index instance = new ${className}Index();

    private ${className}Index()
    {
        super(new int[]{
            <#list tuples as tuple>
                ${tuple.id},
            </#list>},
            new Class[]{
            <#list tuples as tuple>
                ${tuple.indexClass}.class,
            </#list>
            });
    }

    public static ${className}Index inst()
    {
        return instance;
    }

    @Override
    protected ${classQname} createInstance(int id)
    {
        switch (id)
        {
<#list tuples as tuple>
            case ${tuple.id}:
                return new ${tuple.indexClass}(${tuple.id});
</#list>
        }

        throw new RuntimeException();
    }
}

