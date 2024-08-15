<#import "libs/autoFragmentImport_lib.ftlh" as lib>
first line
<@lib.LibMacro2 />
last line

<#macro LocalMacro1>
    local macro 1 first line<#lt>
    <@lib.LibMacro2 />
    local macro 1 last line<#lt>
</#macro>