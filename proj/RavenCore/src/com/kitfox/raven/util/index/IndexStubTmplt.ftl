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

import com.kitfox.puffin.util.index.IndexStub;
import com.kitfox.puffin.util.service.ServiceAnno;

@ServiceAnno(service=IndexStub.class)
public class ${className}Stub extends IndexStub<${indexQname}, ${classQname}>
{
    public ${className}Stub()
    {
        super(${indexQname}.class, ${classQname}.class);
    }
}

