<#import "libs/autoFragmentImport_lib.ftlh" as imported>
default content

<#macro Macro1>
    <@LibMacro1 />

    macro 1<#lt>
</#macro>
<#macro Fragment\-Init_Macro>
    init macro about to call auto-imported lib macro<#lt>
    <@imported.LibMacro2 />

    <#include "libs/autoFragmentImport_lib.ftlh">
    macro included in init macro about to be called in fragment macro<#lt>
</#macro>