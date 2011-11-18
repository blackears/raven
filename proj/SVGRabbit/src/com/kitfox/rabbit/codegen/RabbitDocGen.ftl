<#--
Source code generation
 -->
<#setting number_format="computer">
/**
 * Automatically generated ResourceComponent service
 * <#if date??>${date?date}</#if>
 * <#if author??>@author ${author}</#if>
 */


package ${packageName};

import com.kitfox.rabbit.nodes.*;
import com.kitfox.rabbit.style.StyleKey;

<#macro fields node>
    <#list node.children as child>
        <@fields child/>
    </#list>
    <#if node.name??>
    private ${node.nodeClass} ${node.name};
    </#if>
</#macro>

<#macro buildMethods node>
    <#list node.children as child>
        <@buildMethods child/>
    </#list>
    <#if node.name??>

    private void build_${node.name}()
    {
        <#list node.children as child>
        <#if child.name??>
        build_${child.name}();
        </#if>
        </#list>

        ${node.name} = new ${node.nodeClass}();

        <#list node.properties as prop>
        ${node.name}.set${prop.name?cap_first}(${prop.initValue});
        </#list>

        <#list node.styles as style>
        ${node.name}.getStyle().put(StyleKey.${style.key}, ${style.initValue});
        </#list>

        <#list node.children as child>
        <#if child.name??>
        ${node.name}.addChild(${child.name});
        <#elseif child.stringValue??>
        ${node.name}.addText("${child.stringValue?j_string}");
        </#if>
        </#list>
    }
    </#if>
</#macro>


public class ${className} extends RaSvg
{
    public static final int DOC_ID = ${docId};

<@fields root/>

    public ${className}()
    {
        build_${root.name}();
    }

<@buildMethods root/>

    public RaElement lookupElement(int id)
    {
        switch (id)
        {
        <#list elementRefs?keys as key>
            case ${key}:
                return ${elementRefs[key]};
        </#list>
        }
        return null;
    }
}

